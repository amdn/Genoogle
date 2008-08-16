package bio.pih.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.biojava.bio.BioException;
import org.biojava.bio.alignment.SubstitutionMatrix;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojava.bio.symbol.IllegalSymbolException;

import bio.pih.alignment.GenoogleNeedlemanWunsch;
import bio.pih.alignment.GenoogleSequenceAlignment;
import bio.pih.encoder.DNASequenceEncoderToShort;
import bio.pih.seq.LightweightSymbolList;

/**
 * Given two sub-sequences, return the *global* alignment between they.
 * 
 * @author albrecht
 * 
 * 
 * <p>
 * How to use:
 * 
 * Create a instance, check if exists, if not createData(), else loadData().
 * 
 * <p>
 * FILES FORMAT:<br>
 * Index File:<br>
 * <code>
 * ([encodedSequence:short][quantity:short][data offset:long]){4^8 times}
 * </code> <br>
 * Data file:<br>
 * <code>
 * ([encodedSequence:short][quantity:short]([encodedSequence,score:int]){quantity times}){4^8 times}
 * </code> <br>
 * The encodedSequence and quantity appears also in data file for sanity checks.<br>
 * <br>
 * 
 * TODO: Remove the default values from hardcoded class and put in a configuration file.
 */
public class SimilarSubSequencesIndex {

	private final GenoogleNeedlemanWunsch aligner;
	private final DNASequenceEncoderToShort encoder;

	private static final int defaultThreshold = 3;
	private static final int defaultMatch = -1;
	private static final int defaultDismatch = 1;
	private static final int defaultGapOpen = 0;
	private static final int defaultGapExtend = 1;
	private static final int defaultSubSequenceLength = 8;

	private final int subSequenceLength;
	private final int threshold;
	private final int match;
	private final int dismatch;
	private final int gapOpen;
	private final int gapExtend;
	private final int maxEncodedSequenceValue;

	private File indexFile = null;
	private File dataFile = null;
	private FileChannel dataFileChannel = null;

	private long[] dataOffsetIndex;
	private int[] dataQuantityIndex;
	
	private boolean isLoad = false;

	private static SimilarSubSequencesIndex defaultInstance = null;

	static Logger logger = Logger.getLogger("bio.pih.index.SubSequencesComparer");

	/**
	 * @return the default instance of {@link SimilarSubSequencesIndex}
	 * @throws ValueOutOfBoundsException
	 */
	public static SimilarSubSequencesIndex getDefaultInstance() {
		if (defaultInstance == null) {
			try {
				defaultInstance = new SimilarSubSequencesIndex(DNATools.getDNA(), defaultSubSequenceLength, defaultMatch, defaultDismatch, defaultGapOpen, defaultGapExtend, defaultThreshold);
			} catch (ValueOutOfBoundsException e) {
				logger.fatal("Fatar error in loading the default SubSequenceComparer. Probably related with subSequenceLength", e);
			}
		}
		return defaultInstance;
	}

	/**
	 * Auxiliar main class for generate default data.
	 * 
	 * @param args
	 * @throws IllegalSymbolException
	 * @throws IOException
	 * @throws BioException
	 * @throws ValueOutOfBoundsException
	 */
	public static void main(String[] args) throws IllegalSymbolException, IOException, BioException {
		getDefaultInstance().generateData(true);
	}

	/**
	 * @param alphabet
	 * @param subSequenceLength
	 * @param match
	 * @param dismatch
	 * @param gapOpen
	 * @param gapExtend
	 * @param threshold
	 * @throws ValueOutOfBoundsException
	 */
	public SimilarSubSequencesIndex(FiniteAlphabet alphabet, int subSequenceLength, int match, int dismatch, int gapOpen, int gapExtend, int threshold) throws ValueOutOfBoundsException {
		this.subSequenceLength = subSequenceLength;
		this.match = match;
		this.dismatch = dismatch;
		this.gapOpen = gapOpen;
		this.gapExtend = gapExtend;
		this.threshold = threshold;

		SubstitutionMatrix substitutionMatrix = new SubstitutionMatrix(alphabet, match * -1, dismatch * -1); // values
		this.aligner = new GenoogleNeedlemanWunsch(match, dismatch, gapOpen, gapOpen, gapExtend, substitutionMatrix);
		this.encoder = new DNASequenceEncoderToShort(subSequenceLength);
		this.maxEncodedSequenceValue = (int) Math.pow(alphabet.size(), subSequenceLength) - 1;
	}

	/**
	 * @return the maximum value for a sequence that is possible for this actual alphabet and sequence length
	 */
	public int getMaxEncodedSequenceValue() {
		return this.maxEncodedSequenceValue;
	}

	private String indexFileName = null;

	/**
	 * @return name of the file containing the index of the data of this {@link SimilarSubSequencesIndex} score scheme.
	 */
	public String getIndexFileName() {
		if (this.indexFileName == null) {
			StringBuilder sb = new StringBuilder("subSequencesComparer");
			sb.append("_");
			sb.append(subSequenceLength);
			sb.append("_");
			sb.append(match);
			sb.append("_");
			sb.append(dismatch);
			sb.append("_");
			sb.append(gapOpen);
			sb.append("_");
			sb.append(gapExtend);
			sb.append("_");
			sb.append(threshold);
			sb.append(".idx");
			this.indexFileName = sb.toString();
		}
		return this.indexFileName;
	}

	private String dataFileName = null;

	/**
	 * @return name of the file containing the the data of this {@link SimilarSubSequencesIndex} score scheme.
	 */
	public String getDataFileName() {
		if (this.dataFileName == null) {
			StringBuilder sb = new StringBuilder("subSequencesComparer");
			sb.append("_");
			sb.append(subSequenceLength);
			sb.append("_");
			sb.append(match);
			sb.append("_");
			sb.append(dismatch);
			sb.append("_");
			sb.append(gapOpen);
			sb.append("_");
			sb.append(gapExtend);
			sb.append("_");
			sb.append(threshold);
			sb.append(".bin");
			this.dataFileName = sb.toString();
		}
		return this.dataFileName;
	}

	private File getIndexFile() {
		if (indexFile == null) {
			indexFile = new File(getIndexFileName());
		}
		return indexFile;
	}

	/**
	 * TODO: check if the file is *really* a index file
	 * 
	 * @return if a file having the appropriate name for index exists.
	 */
	public boolean hasIndexFile() {
		return getIndexFile().isFile() && getIndexFile().exists();
	}

	private File getDataFile() {
		if (dataFile == null) {
			dataFile = new File(getDataFileName());
		}
		return dataFile;
	}

	/**
	 * TODO: check if the file is *really* a data file
	 * 
	 * @return if a file having the appropriate name for data exists.
	 */
	public boolean hasDataFile() {
		return getDataFile().isFile() && getDataFile().exists();
	}

	private FileChannel getDataFileChannel() throws FileNotFoundException {
		if (dataFileChannel == null) {
			dataFileChannel = new FileInputStream(getDataFile()).getChannel();
		}
		return dataFileChannel;
	}

	/**
	 * Same <code>load(false)</code>
	 * 
	 * @throws IOException
	 * @throws InvalidHeaderData
	 */
	public void load() throws IOException, InvalidHeaderData {
		load(false);
	}

	/**
	 * Load the data (index and the data itself) for utilize it.
	 * 
	 * @param check
	 *            <code>true</code> if the consistency must be checked
	 * @throws IOException
	 * @throws InvalidHeaderData
	 */
	public void load(boolean check) throws IOException, InvalidHeaderData {
		logger.info("Loading " + this.toString() + " data");
		if (this.isLoad) {
			logger.info(this.toString() + " is already loaded.");
			return;
		}
		
		long begin = System.currentTimeMillis();
		MappedByteBuffer mappedIndexFile = new FileInputStream(getIndexFile()).getChannel().map(MapMode.READ_ONLY, 0, getIndexFile().length());

		int encodedSequenceAndQuantity;
		int sequence;
		int quantity;
		long offset;

		this.dataQuantityIndex = new int[maxEncodedSequenceValue + 1];
		this.dataOffsetIndex = new long[maxEncodedSequenceValue + 1];

		for (int i = 0; i <= maxEncodedSequenceValue; i++) {
			encodedSequenceAndQuantity = mappedIndexFile.getInt();
			sequence = (encodedSequenceAndQuantity >> 16) & 0xFFFF;
			quantity = encodedSequenceAndQuantity & 0xFFFF;
			offset = mappedIndexFile.getLong();

			// check the data consistency ?
			if (check) {
				getSimilarSequences(sequence, quantity, offset);
			}

			if ((sequence) != i) {
				throw new InvalidHeaderData("Sequence (" + sequence + ") count do not match sequence position (" + i + ").");
			}
			this.dataQuantityIndex[sequence] = quantity;
			this.dataOffsetIndex[sequence] = offset;
		}
		this.isLoad = true;
		logger.info("SubSequencesComparer data loaded in " + (System.currentTimeMillis() - begin) + "ms");
	}

	/**
	 * Same <code>getSimilarSequences(encodedSubSequence & 0xFFFF);</code>
	 * 
	 * @param encodedSubSequence
	 * @return a array containing all sub-sequences that are similar with the given encodedSubSequence.  
	 * @throws IOException
	 * @throws InvalidHeaderData
	 */
	public int[] getSimilarSequences(short encodedSubSequence) throws IOException, InvalidHeaderData {
		return getSimilarSequences(encodedSubSequence & 0xFFFF);

	}

	/**
	 * @param encodedSubSequence
	 *            an int that where is read <b>only</b> its first 16bits. If the code has a short, use: <br>
	 *            <code>getSimilarSequences(shortVariable & 0xFFFF)</code>
	 * @return the similar subsequences that are equal or higher than threshold.
	 * @throws IOException
	 * @throws InvalidHeaderData
	 */
	private int[] getSimilarSequences(int encodedSubSequence) throws IOException, InvalidHeaderData {
		long offset = dataOffsetIndex[encodedSubSequence];
		int quantity = dataQuantityIndex[encodedSubSequence];

		return getSimilarSequences(encodedSubSequence, quantity, offset);
	}

	private int[] getSimilarSequences(int encodedSequence, int quantity, long offset) throws IOException, InvalidHeaderData {
		int resultsInByte = quantity * 4;
		MappedByteBuffer map = getDataFileChannel().map(MapMode.READ_ONLY, offset, 2 + 2 + resultsInByte);
		IntBuffer buffer = map.asIntBuffer();
		int header = buffer.get();
		if (((header >> 16) & 0xFFFF) != (encodedSequence & 0xFFFF)) {
			throw new InvalidHeaderData("the value " + (header >> 16) + " is different from " + encodedSequence);
		}

		if ((header & 0xFFFF) != quantity) {
			throw new InvalidHeaderData("the value " + (header & 0xFFFF) + " is different from " + encodedSequence);
		}

		int similarSequences[] = new int[quantity];
		buffer.get(similarSequences, 0, quantity);
		return similarSequences;
	}

	/**
	 * Get the score from the int representation of similar sub sequence.
	 * @param alignmentIntRepresentation
	 * @return the score from short (16 bits) representation
	 */
	public static int getScore(int alignmentIntRepresentation) {
		return ComparationResult.getScoreFromRepresentation(alignmentIntRepresentation);
	}

	/**
	 * Get the sequence from the int representation of similar sub sequence.
	 * @param alignmentIntRepresentation
	 * @return the sequence from short (16 bits) representation
	 */
	public static int getSequence(int alignmentIntRepresentation) {
		return ComparationResult.getSequenceFromRepresentation(alignmentIntRepresentation);
	}

	/**
	 * Same as generateData(false)
	 * 
	 * @throws IOException
	 * @throws IllegalSymbolException
	 * @throws BioException
	 */
	public void generateData() throws IOException, IllegalSymbolException, BioException {
		generateData(false);
	}
	
	/**
	 * Delete the data. Use with caution!
	 * @return <code>true</code> if the data was deleted sucesfull.
	 */
	public boolean deleteData() {
		boolean r = true;
		if (getDataFile().delete() == false) {
			r = false;
		}
		if (getIndexFile().delete() == false) {
			r = false;
		}
		return r;
	}

	/**
	 * Calculate and store data for the actual score schema.
	 * 
	 * @param verbose
	 * @throws IOException
	 * @throws BioException
	 * @throws IllegalSymbolException
	 * @throws Exception
	 */
	public void generateData(boolean verbose) throws IOException, IllegalSymbolException, BioException {
		int score;
		long initialTime = System.currentTimeMillis();
		ComparationResult ar;
		LinkedList<ComparationResult> results;

		if (getIndexFile().createNewFile() == false) {
			throw new IOException("File " + getIndexFile() + " alread exists. Delete it before if you *really* want to generate a new data set");
		}
		FileChannel indexFileChannel = new FileOutputStream(getIndexFile()).getChannel();

		if (getDataFile().createNewFile() == false) {
			throw new IOException("File " + getDataFile() + " alread exists. Delete it before if you *really* want to generate a new data set");
		}

		FileChannel dataFileChannel = new FileOutputStream(getDataFile()).getChannel();

		System.out.println("Gerating data for  " + this.toString());
		for (int encodedSequence1 = 0; encodedSequence1 <= maxEncodedSequenceValue; encodedSequence1++) {
			results = new LinkedList<ComparationResult>();
			long time = System.currentTimeMillis();
			for (int encodedSequence2 = 0; encodedSequence2 <= maxEncodedSequenceValue; encodedSequence2++) {
				score = (int) compareCompactedSequences((short) encodedSequence1, (short) encodedSequence2);
				ar = new ComparationResult((short) score, (short) encodedSequence2);
				if (score >= this.threshold) {
					results.add(ar);
				}
			}
			Collections.sort(results, ComparationResult.getScoreComparator());

			if (verbose) {
				System.out.println(getSequenceFromShort((short) (encodedSequence1 & 0xFFFF)).getString() + "\t" + (System.currentTimeMillis() - time) + "\t" + results.size() + "\t" + encodedSequence1 + "\t" + Integer.toHexString(encodedSequence1) + "\t" + Integer.toBinaryString(encodedSequence1));
			}

			// 2 for sequence + 2 for the quantity + 64 for offset
			ByteBuffer buffer = ByteBuffer.allocate(68);
			buffer.putShort((short) (encodedSequence1 & 0xFFFF));
			buffer.putShort((short) results.size());
			buffer.putLong(dataFileChannel.position());
			buffer.flip();
			indexFileChannel.write(buffer);

			// 2 for sequence itself and 2 for the quantity (both for sanity
			// check) and 4 bytes for each result
			int size = results.size() * 4 + 2 + 2;
			buffer = ByteBuffer.allocate(size);
			buffer.putShort((short) (encodedSequence1 & 0xFFFF));
			buffer.putShort((short) results.size());
			for (ComparationResult cr : results) {
				buffer.putInt(cr.getIntRepresentation());
			}
			buffer.flip();
			dataFileChannel.write(buffer);
		}

		indexFileChannel.close();
		dataFileChannel.close();

		if (verbose) {
			System.out.println("-----------------------------");
			System.out.println(" tempo total: " + (System.currentTimeMillis() - initialTime));
			System.out.println("");
		}
	}

	private HashMap<Short, LightweightSymbolList> encodedToSymbolList = new HashMap<Short, LightweightSymbolList>();

	private LightweightSymbolList getSequenceFromShort(short encodedSymbolList) throws IllegalSymbolException, BioException {
		LightweightSymbolList symbolList = encodedToSymbolList.get(encodedSymbolList);
		if (symbolList == null) {
			symbolList = (LightweightSymbolList) encoder.decodeShortToSymbolList(encodedSymbolList);
			encodedToSymbolList.put(encodedSymbolList, symbolList);
		}
		return symbolList;
	}

	/**
	 * Compare and align two encoded sub-sequences
	 * 
	 * @param encodedSequence1
	 * @param encodedSequence2
	 * @return the alignment score
	 * @throws IllegalSymbolException
	 * @throws BioException
	 */
	public double compareCompactedSequences(short encodedSequence1, short encodedSequence2) throws IllegalSymbolException, BioException {
		LightweightSymbolList symbolList1 = getSequenceFromShort(encodedSequence1);
		LightweightSymbolList symbolList2 = getSequenceFromShort(encodedSequence2);
		return aligner.fastPairwiseAlignment(symbolList1, symbolList2) * -1;
	}

	/**
	 * @return the default threshold
	 */
	public static int getDefaultTreadshould() {
		return defaultThreshold;
	}

	/**
	 * @return the default dismatch cost
	 */
	public static int getDefaultDismatch() {
		return defaultDismatch;
	}

	/**
	 * @return the default gap extend cost
	 */
	public static int getDefaultGapExtend() {
		return defaultGapExtend;
	}

	/**
	 * @return the default gap open cost
	 */
	public static int getDefaultGapOpen() {
		return defaultGapOpen;
	}

	/**
	 * @return the default match value
	 */
	public static int getDefaultMatch() {
		return defaultMatch;
	}

	/**
	 * @return the default sub-sequence length
	 */
	public static int getDefaultSubSequenceLength() {
		return defaultSubSequenceLength;
	}

	/**
	 * @return the aligner used
	 */
	public GenoogleSequenceAlignment getAligner() {
		return aligner;
	}

	/**
	 * @return the encoder used
	 */
	public DNASequenceEncoderToShort getEncoder() {
		return encoder;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SubSequenceComparer ");
		sb.append(match);
		sb.append('/');
		sb.append(dismatch);
		sb.append('/');
		sb.append(gapOpen);
		sb.append('/');
		sb.append(gapExtend);
		sb.append('/');
		sb.append(threshold);
		return sb.toString();
	}

	/**
	 * Stores an sequences and its scores. Used to represent these informations into a int
	 * 
	 */
	public static class ComparationResult {
		short score;
		short sequence;

		/**
		 * @param score
		 * @param sequence
		 */
		public ComparationResult(short score, short sequence) {
			this.score = score;
			this.sequence = sequence;
		}

		/**
		 * @return the score
		 */
		public short getScore() {
			return score;
		}

		/**
		 * @return the sequence
		 */
		public short getSequence() {
			return sequence;
		}

		/**
		 * @return a int containing the sequence and the score.
		 */
		public int getIntRepresentation() {
			return ((sequence << 16) | (score & 0xFFFF));
		}

		/**
		 * @param alignmentIntRepresentation
		 * @return the score
		 */
		public static int getScoreFromRepresentation(int alignmentIntRepresentation) {
			return alignmentIntRepresentation;
		}

		/**
		 * @param alignmentIntRepresentation
		 * @return the sequence
		 */
		public static int getSequenceFromRepresentation(int alignmentIntRepresentation) {
			return (alignmentIntRepresentation >> 16) & 0xFFFF;
		}

		@Override
		public String toString() {
			return "[" + score + "/" + (sequence & 0xFFFF) + "]";
		}

		static Comparator<ComparationResult> scoreComparator = null;

		/**
		 * @return a comparator to compare two {@link ComparationResult} based in their score.
		 */
		public static Comparator<ComparationResult> getScoreComparator() {
			if (scoreComparator == null) {
				scoreComparator = new Comparator<ComparationResult>() {
					public int compare(ComparationResult o1, ComparationResult o2) {
						return o2.getScore() - o1.getScore();
					}
				};
			}
			return scoreComparator;
		}
	}

}
