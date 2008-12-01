package bio.pih.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojavax.bio.seq.RichSequence;

import bio.pih.encoder.DNASequenceEncoderToInteger;
import bio.pih.encoder.SequenceEncoder;
import bio.pih.index.ValueOutOfBoundsException;
import bio.pih.io.proto.Io.StoredDatabank;
import bio.pih.io.proto.Io.StoredSequence;
import bio.pih.io.proto.Io.StoredSequenceInfo;
import bio.pih.io.proto.Io.StoredDatabank.SequenceType;
import bio.pih.io.proto.Io.StoredSequence.Builder;
import bio.pih.seq.op.LightweightIOTools;
import bio.pih.seq.op.LightweightStreamReader;

import com.google.protobuf.ByteString;

/**
 * An abstract class for Sequence Banks that uses DNA sequences
 * 
 * @author albrecht
 * 
 */
public abstract class DNASequenceDataBank implements SequenceDataBank {

	private final FiniteAlphabet alphabet = DNATools.getDNA();
	private final String name;
	private final File file;
	private final DatabankCollection<? extends DNASequenceDataBank> parent;
	private final DNASequenceEncoderToInteger encoder;
	private final int subSequenceLength;
	private File fullPath = null;

	private volatile int nextSequenceId;
	private int numberOfSequences;
	private long dataBankSize;
	protected StoredDatabank storedDatabank;

	private File dataBankFile = null;
	private File storedDataBankInfoFile = null;

	Logger logger = Logger.getLogger("bio.pih.io.DNASequenceDataBank");

	/**
	 * Default constructor for all DNASequenceDataBank.
	 * 
	 * @param name
	 *            the name of the data bank.
	 * @param path
	 *            the path where will be stored.
	 * @param parent
	 * @param readOnly
	 *            if the data will be read only, no new sequences added.
	 * @param subSequenceLength
	 * @param minEvalueDropOut
	 * @throws IOException
	 */
	public DNASequenceDataBank(String name, File path,
			DatabankCollection<? extends DNASequenceDataBank> parent, int subSequenceLength) {
		this.name = name;
		this.file = path;
		this.parent = parent;
		this.nextSequenceId = 0;
		this.numberOfSequences = 0;
		this.dataBankSize = 0;
		this.subSequenceLength = subSequenceLength;
		this.encoder = DNASequenceEncoderToInteger.getEncoder(subSequenceLength);
		this.storedDatabank = null;
	}

	@Override
	public synchronized void load() throws IOException, ValueOutOfBoundsException {
		logger.info("Loading databank from " + getDataBankFile());

		long begin = System.currentTimeMillis();
		if (!getDataBankFile().exists() || !getStoredDataBankInfoFile().exists()) {
			logger.fatal("Databank " + this.getName() + " is not encoded. Please encode it.");
			throw new IOException("Databank " + this.getName()
					+ " is not encoded. Please encode it.\n Check " + this.getFullPath());
		}

		loadInformations();

		FileChannel dataBankFileChannel = new FileInputStream(getDataBankFile()).getChannel();
		this.storedDatabank = StoredDatabank.parseFrom(new FileInputStream(
				getStoredDataBankInfoFile()));

		System.out.println("Databank with : " + storedDatabank.getQtdSequences() + " sequences.");
		System.out.println("Databank with : " + storedDatabank.getQtdBases() + " bases.");
		System.out.println("Databank with : " + storedDatabank.getQtdBases() / 11
				+ " sub-sequences bases aprox.");

		beginSequencesProcessing();
		for (int i = 0; i < storedDatabank.getSequencesInfoCount(); i++) {
			StoredSequence storedSequence = getSequenceFromId(i);
			numberOfSequences++;
			final int[] encodedSequence = Utils.getEncodedSequenceAsArray(storedSequence);
			dataBankSize += SequenceEncoder.getSequenceLength(encodedSequence);

			doSequenceProcessing(storedSequence.getId(), encodedSequence);
			if (i % 1000 == 0) {
				System.out.println(i + "/" + storedDatabank.getSequencesInfoCount());
			}
		}
		dataBankFileChannel.close();
		finishSequencesProcessing();
		logger.info("Databank loaded in " + (System.currentTimeMillis() - begin) + "ms with "
				+ numberOfSequences + " sequences.");
	}

	/**
	 * Load informations previously the data bank is loaded.
	 * 
	 * @throws IOException
	 */
	abstract void loadInformations() throws IOException;

	/**
	 * Inform that the sequences processing is beginning.
	 * 
	 * @throws IOException
	 * @throws ValueOutOfBoundsException
	 */
	abstract void beginSequencesProcessing() throws IOException, ValueOutOfBoundsException;

	/**
	 * Process a {@link SequenceInformation}
	 * 
	 * @param sequenceInformation
	 */
	abstract void doSequenceProcessing(int sequenceId, int[] encodedSequence);

	/**
	 * Finish the sequences processing. <br>
	 * After this point no more sequences can be added to the data bank.
	 * 
	 * @throws IOException
	 */
	abstract void finishSequencesProcessing() throws IOException;

	/**
	 * @param sequenceId
	 * @return {@link StoredSequence} of the given sequenceId.
	 * @throws IOException
	 */
	public synchronized StoredSequence getSequenceFromId(int sequenceId) throws IOException {
		MappedByteBuffer mappedIndexFile = getMappedIndexFile();
		StoredSequenceInfo storedSequenceInfo = storedDatabank.getSequencesInfo(sequenceId);

		byte[] data = new byte[storedSequenceInfo.getLength()];
		mappedIndexFile.position(storedSequenceInfo.getOffset());
		mappedIndexFile.get(data);

		return StoredSequence.parseFrom(data);
	}

	WeakReference<MappedByteBuffer> mappedIndexFile = new WeakReference<MappedByteBuffer>(null);

	private MappedByteBuffer getMappedIndexFile() throws IOException {
		if (mappedIndexFile.get() == null) {
			FileChannel channel = new FileInputStream(getDataBankFile()).getChannel();
			mappedIndexFile = new WeakReference<MappedByteBuffer>(channel.map(MapMode.READ_ONLY, 0,
					getDataBankFile().length()));
		}
		return mappedIndexFile.get();
	}

	/**
	 * @throws IOException
	 * @throws BioException
	 * @throws NoSuchElementException
	 * @throws ValueOutOfBoundsException
	 */
	public void encodeSequences() throws IOException, NoSuchElementException, BioException,
			ValueOutOfBoundsException {
		if (getDataBankFile().exists()) {
			throw new IOException("File " + getDataBankFile()
					+ " already exists. Please remove it before creating another file.");
		}
		beginSequencesProcessing();
		addFastaFile(getFullPath());
		finishSequencesProcessing();
	}

	public void addFastaFile(File fastaFile) throws NoSuchElementException, BioException,
			IOException {
		logger.info("Adding a FASTA file from " + fastaFile);
		long begin = System.currentTimeMillis();
		FileChannel dataBankFileChannel = new FileOutputStream(getDataBankFile(), true)
				.getChannel();
		FileChannel storedSequenceInfoChannel = new FileOutputStream(getStoredDataBankInfoFile(),
				true).getChannel();
		bio.pih.io.proto.Io.StoredDatabank.Builder storedDatabankBuilder = StoredDatabank
				.newBuilder();

		BufferedReader is = new BufferedReader(new FileReader(fastaFile));
		LightweightStreamReader readFastaDNA = LightweightIOTools.readFastaDNA(is, null);

		while (readFastaDNA.hasNext()) {
			RichSequence s = readFastaDNA.nextRichSequence();
			StoredSequenceInfo addSequence = addSequence(s, dataBankFileChannel);
			storedDatabankBuilder.addSequencesInfo(addSequence);
		}

		storedDatabankBuilder.setType(SequenceType.DNA);
		storedDatabankBuilder.setQtdSequences(numberOfSequences);
		storedDatabankBuilder.setQtdBases(dataBankSize);

		storedDatabank = storedDatabankBuilder.build();
		storedSequenceInfoChannel.write(ByteBuffer.wrap(storedDatabank.toByteArray()));

		storedSequenceInfoChannel.close();
		dataBankFileChannel.close();
		logger.info("FASTA file added in " + (System.currentTimeMillis() - begin) + "ms");
	}

	synchronized StoredSequenceInfo addSequence(RichSequence s, FileChannel dataBankFileChannel)
			throws BioException, IOException {
		if (!s.getAlphabet().equals(this.alphabet)) {
			throw new BioException("Invalid alphabet for sequence " + s.getName());
		}

		if (s.length() < 8) {
			System.out.println(s.getName() + "is too short (" + s.length()
					+ ") and will not be stored in this data bank");
			return null;
		}

		long offset = dataBankFileChannel.position();

		final byte[] ret = intArrayToByteArray(s);

		int id = getNextSequenceId();
		Builder builder = StoredSequence.newBuilder()
				.setId(id).setGi(s.getIdentifier())
				.setName(s.getName())
				.setAccession(s.getAccession())
				.setVersion(s.getVersion())
				.setDescription(s.getDescription())
				.setEncodedSequence(ByteString.copyFrom(ret));

		StoredSequence storedSequence = builder.build();

		byte[] byteArray = storedSequence.toByteArray();
		dataBankFileChannel.write(ByteBuffer.wrap(byteArray));

		numberOfSequences++;

		if (offset > Integer.MAX_VALUE) {
			throw new IOException("PUTA QUE PARIU!, o offset eh maior que o valor maximo");
		}

		return StoredSequenceInfo.newBuilder().setId(id).setOffset((int) offset).setLength(
				byteArray.length).build();
	}

	private byte[] intArrayToByteArray(RichSequence s) {
		int[] encoded = encoder.encodeSymbolListToIntegerArray(s);

		ByteBuffer byteBuf = ByteBuffer.allocate(encoded.length * 4);
		for (int i = 0; i < encoded.length; i++) {
			byteBuf.putInt(encoded[i]);
		}

		return byteBuf.array();
	}

	protected static void checkFile(File file, boolean readOnly) throws IOException {
		if (file.exists()) {
			if (!file.canRead()) {
				throw new IOException("File " + file.getCanonicalPath()
						+ " exists but is not readable");
			}
			if (!readOnly & !file.canWrite()) {
				throw new IOException("File " + file.getCanonicalPath()
						+ " exists but is not writable");
			}
		} else if (readOnly) {
			throw new IOException("File " + file.getCanonicalPath()
					+ " does not exist and can not be marked as read-only");
		}
	}

	protected int getNextSequenceId() {
		int id = nextSequenceId;
		nextSequenceId++;
		return id;
	}

	public int getNumberOfSequences() {
		return numberOfSequences;
	}

	public FiniteAlphabet getAlphabet() {
		return alphabet;
	}

	public void setName(String name) {
		throw new IllegalStateException("The name is imutable for a DataBank");
	}

	public String getName() {
		return name;
	}

	public void setPath(File directory) {
		throw new UnsupportedOperationException("The path is imutable for a DataBank");
	}

	public File getFilePath() {
		return file;
	}

	@Override
	public File getFullPath() {
		if (fullPath == null) {
			if (getParent() == null) {
				fullPath = getFilePath();
			} else {
				fullPath = new File(getParent().getFullPath(), this.getFilePath().getPath());
			}
		}
		return fullPath;
	}

	protected synchronized File getDataBankFile() {
		if (dataBankFile == null) {
			dataBankFile = new File(getFullPath() + ".dsdb");
		}
		return dataBankFile;
	}

	protected synchronized File getStoredDataBankInfoFile() {
		if (storedDataBankInfoFile == null) {
			storedDataBankInfoFile = new File(getFullPath() + ".ssdb");
		}
		return storedDataBankInfoFile;
	}

	@Override
	public SequenceDataBank getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return this.name + "@" + this.getFullPath();
	}

	public boolean check() {
		if (getDataBankFile().exists()) {
			return true;
		}
		return false;
	}

	@Override
	public DNASequenceEncoderToInteger getEncoder() {
		return encoder;
	}

	@Override
	public long getDataBaseSize() {
		return dataBankSize;
	}

	@Override
	public long getTotalDataBaseSize() {
		if (parent == null) {
			return getDataBaseSize();
		}
		return parent.getTotalDataBaseSize();
	}

	@Override
	public long getTotalNumberOfSequences() {
		if (parent == null) {
			return getNumberOfSequences();
		}
		return parent.getTotalNumberOfSequences();
	}

	@Override
	public int getSubSequenceLength() {
		return subSequenceLength;
	}
}
