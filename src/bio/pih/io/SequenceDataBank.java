package bio.pih.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.biojava.bio.BioException;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;
import org.biojavax.bio.seq.RichSequence;

/**
 * This interface specifies the ways to access a sequence bank.
 * <p>
 * SequenceBank is how the Genoogle stores its sequences data. The methods are divided into 3 classes: general information like name and path, add a sequence file, a single sequence or a collection of them and sync these data and for last, and some way the most important, do searchs.
 * 
 * @author albrecht
 * 
 */
public interface SequenceDataBank {

	/**
	 * The name is related with the files names too.
	 * 
	 * @return the name of this sequence bank.
	 */
	String getName();

	/**
	 * @param name
	 *            set the name of this sequence bank.
	 */
	void setName(String name);

	/**
	 * @return the directory where is this SequenceBank.
	 */
	File getPath();

	/**
	 * @param directory
	 *            where is this SequenceBank.
	 */
	void setPath(File directory);

	/**
	 * @return the total quantity of sequences stored in this SequenceDataBank
	 */
	int getTotalSequences();

	/**
	 * @param extensions
	 *            for the files of this SequenceDataBank
	 */
	void setExtensions(String[] extensions);

	/**
	 * Set the {@link FiniteAlphabet} of the sequences of this sequence bank.
	 * 
	 * @param alphabet
	 */
	void setAlphabet(FiniteAlphabet alphabet);

	/**
	 * @return the {@link FiniteAlphabet} of the sequences of this sequence bank.
	 */
	FiniteAlphabet getAlphabet();

	/**
	 * Add a fasta formated sequence collection into the SequenceBank.
	 * 
	 * @param fastaFile
	 * @throws FileNotFoundException
	 * @throws NoSuchElementException
	 * @throws BioException
	 * @throws IOException
	 */
	public void addFastaFile(File fastaFile) throws FileNotFoundException, NoSuchElementException, BioException, IOException;

	/**
	 * Load this sequence bank
	 * 
	 * @throws IOException
	 * @throws IllegalSymbolException
	 */
	void loadInformations() throws IOException, IllegalSymbolException;

	/**
	 * Add a new sequence into the sequence bank
	 * 
	 * @param s
	 * @param sequence
	 * @return the added sequence id
	 * @throws BioException
	 * @throws IOException
	 */
	public int addSequence(RichSequence s) throws BioException, IOException;

	/**
	 * Get a {@link SymbolList} sequence from an internal id
	 * 
	 * @param sequenceId
	 * @return the symbol list of the given id
	 * @throws IOException
	 * @throws IllegalSymbolException
	 */
	SequenceInformation getSequenceInformationFromId(int sequenceId) throws IOException, IllegalSymbolException;
}
