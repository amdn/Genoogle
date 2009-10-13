package bio.pih.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.biojava.bio.BioException;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojava.bio.symbol.IllegalSymbolException;

import bio.pih.encoder.DNASequenceEncoderToInteger;
import bio.pih.index.IndexConstructionException;
import bio.pih.index.InvalidHeaderData;
import bio.pih.index.ValueOutOfBoundsException;

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
	 * @return the file name and directory where is this SequenceDataBank.
	 */
	File getFilePath();
	
	
	/**
	 * @return the file name and directory where is this SequenceDataBank considering its parent.
	 */
	File getFullPath();
	
	/**
	 * @param directory
	 *            where is this SequenceBank.
	 */
	void setPath(File directory);

	/**
	 * @return the number of sequences stored in this SequenceDataBank
	 */
	int getNumberOfSequences();
	
	/**
	 * @return the total number of sequences stored in this SequenceDataBank and all siblings.
	 * <b>To calculate statistics, this value should be used</b>.
	 */	
	long getTotalNumberOfSequences();
	
	/**
	 * @return the number of bases stored in this SequenceDataBank.
	 */	
	long getDataBaseSize();
	
	/**
	 * @return the number of bases stored in this SequenceDataBank and all siblings.
	 * <b>To calculate statistics, this value should be used</b>. 
	 */	
	long getTotalDataBaseSize();
	
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
	 * @throws IndexConstructionException 
	 */
	public void addFastaFile(File fastaFile) throws FileNotFoundException, NoSuchElementException, BioException, IOException, IndexConstructionException;

	/**
	 * Load this sequence bank
	 * 
	 * @throws IOException
	 * @throws ValueOutOfBoundsException  
	 * @throws InvalidHeaderData 
	 * @throws BioException 
	 * @throws IllegalSymbolException 
	 */
	void load() throws IOException, ValueOutOfBoundsException, InvalidHeaderData, IllegalSymbolException, BioException;	
	
	/**
	 * Encode the sequences into a computer legible mode 
	 * @throws IOException 
	 * @throws BioException 
	 * @throws NoSuchElementException 
	 * @throws ValueOutOfBoundsException 
	 * @throws InvalidHeaderData 
	 * @throws IndexConstructionException 
	 */
	void encodeSequences() throws IOException, NoSuchElementException, BioException, ValueOutOfBoundsException, InvalidHeaderData, IndexConstructionException;
	
	/**
	 * @return the parent of this {@link SequenceDataBank} or <code>null</code> if it do not have parent 
	 */
	public SequenceDataBank getParent();
	
	/**
	 * @return <code>true</code> if the data bank files and its data are okay. This method do <b>not</b> check file consistency. 
	 */
	boolean check();
	
	/**
	 * @return {@link DNASequenceEncoderToInteger} witch is responsible to encode the sequences in this data bank.
	 */
	public DNASequenceEncoderToInteger getEncoder(); 
	
	/**
	 * @return length of the sub sequences stored in this data bank.
	 */
	public int getSubSequenceLength();

	
	/**
	 * Delete all file informations of this data bank.
	 */
	void delete();
}
