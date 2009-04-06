package bio.pih.encoder;

import org.apache.log4j.Logger;
import org.biojava.bio.BioException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

import bio.pih.index.ValueOutOfBoundsException;
import bio.pih.seq.LightweightSymbolList;
import bio.pih.util.SymbolListWindowIterator;
import bio.pih.util.SymbolListWindowIteratorFactory;

/**
 * @author albrecht
 */
public class DNASequenceEncoderToInteger extends DNASequenceEncoder {

	static Logger logger = Logger.getLogger("bio.pih.encoder.DNASequenceEncoderToInteger");

	private static DNASequenceEncoderToInteger[] encoders = new DNASequenceEncoderToInteger[Integer.SIZE/2];
	
	/**
	 * @param subSequenceLength length of the subSequences.
	 * @return singleton of the {@link DNASequenceEncoderToInteger}
	 */
	public static DNASequenceEncoderToInteger getEncoder(int subSequenceLength) {
		if (encoders[subSequenceLength] == null) {
			try {
				encoders[subSequenceLength] = new DNASequenceEncoderToInteger(subSequenceLength);
			} catch (ValueOutOfBoundsException e) {
				logger
						.fatal("Problem creating the default instance for DNASequenceEncoderToInteger. Please check the stackstrace above.");
				logger.fatal(e);
				return null;
			}
		}
		return encoders[subSequenceLength];
	}

	/**
	 * @param subSequenceLength
	 * @throws ValueOutOfBoundsException
	 */
	protected DNASequenceEncoderToInteger(int subSequenceLength) throws ValueOutOfBoundsException {
		super(subSequenceLength);
	}

	/**
	 * Encode a subsequence of the encoder length to its int representation
	 * 
	 * @param subSymbolList
	 * @return an int containing the representation of the subsequence
	 */
	public int encodeSubSequenceToInteger(SymbolList subSymbolList) {
		if (subSymbolList.length() > subSequenceLength) {
			throw new ValueOutOfBoundsException(subSymbolList + " is bigger than subSequenceLength("+subSequenceLength+")");
		}

		int encoded = 0;

		for (int i = 1; i <= subSymbolList.length(); i++) {
			encoded |= (getBitsFromSymbol(subSymbolList.symbolAt(i)) << ((subSequenceLength - i) * bitsByAlphabetSize));
		}

		return encoded;
	}
	
	public int encodeSubSequenceToInteger(String subSequence) {
		if (subSequence.length() > subSequenceLength) {
			throw new ValueOutOfBoundsException(subSequence + " is bigger than subSequenceLength("+subSequenceLength+")");
		}

		int encoded = 0;

		for (int i = 0; i < subSequence.length(); i++) {
			encoded |= (getBitsFromChar(subSequence.charAt(i)) << ((subSequenceLength - (i + 1)) * bitsByAlphabetSize));
		}

		return encoded;
	}


	/**
	 * Decode an int vector to its sequence string representation
	 * 
	 * @param encoded
	 * @return the sequence string
	 */
	public String decodeIntegerToString(int encoded) {
		return decodeIntegerToString(encoded, subSequenceLength);
	}

	/**
	 * @param encoded
	 * @return {@link LightweightSymbolList} of the given encoded sub-sequence.
	 * @throws IllegalSymbolException
	 * @throws BioException
	 */
	public SymbolList decodeIntegerToSymbolList(int encoded) throws IllegalSymbolException,
			BioException {
		String sequenceString = decodeIntegerToString(encoded, subSequenceLength);
		return LightweightSymbolList.constructLightweightSymbolList(alphabet, sequenceString);
	}
	
	private String decodeIntegerToString(int encoded, int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int value = encoded & 3;
			sb.append(getSymbolFromBits(value));
			encoded >>= bitsByAlphabetSize;
		}
		sb.reverse();
		return sb.toString();
	}
	

	private String decodeIntegerToString(int encoded, int begin, int end) {
		StringBuilder sb = new StringBuilder((end - begin) + 1);
		for (int pos = begin; pos <= end; pos++) {
			int posInInt = subSequenceLength - pos;
			int shift = posInInt * bitsByAlphabetSize;
			int value = encoded >> (shift - bitsByAlphabetSize);
			sb.append(getSymbolFromBits(value & bitsMask));
		}
		return sb.toString();
	}

	/**
	 * Encode a {@link SymbolList} of length 1 to (2^16)-1 to an array of int.
	 * 
	 * @param sequence
	 * @return an array of int as bit vector
	 */
	public int[] encodeSymbolListToIntegerArray(SymbolList sequence) {
		assert (sequence.getAlphabet().equals(alphabet));
		int size = sequence.length() / subSequenceLength;
		int extra = sequence.length() % subSequenceLength;
		if (extra != 0) { // extra space for incomplete sub-sequence
			size++;
		}
		size++; // extra space for information on the length.
		int sequenceEncoded[] = new int[size];
		sequenceEncoded[getPositionLength()] = sequence.length();

		if (sequence.length() < subSequenceLength) {
			sequenceEncoded[getPositionBeginBitsVector()] = encodeSubSequenceToInteger(sequence
					.subList(1, sequence.length()));
		} else {
			int pos = getPositionBeginBitsVector();
			SymbolListWindowIterator symbolListWindowIterator = SymbolListWindowIteratorFactory
					.getNotOverlappedFactory().newSymbolListWindowIterator(sequence,
							this.subSequenceLength);
			while (symbolListWindowIterator.hasNext()) {
				SymbolList next = symbolListWindowIterator.next();
				sequenceEncoded[pos] = encodeSubSequenceToInteger(next);
				pos++;
			}
			if (pos < size) {
				int from = sequence.length() - extra + 1;
				sequenceEncoded[pos] = encodeSubSequenceToInteger(sequence.subList(from, sequence
						.length()));
			}
		}

		return sequenceEncoded;
	}
	
	/**
	 * @param encodedSequence
	 * @return the {@link SymbolList} that is stored in encodedSequence
	 * @throws IllegalSymbolException
	 * @throws BioException
	 */
	public SymbolList decodeIntegerArrayToSymbolList(int[] encodedSequence)
			throws IllegalSymbolException, BioException {
		String sequenceString = decodeIntegerArrayToString(encodedSequence);
		return LightweightSymbolList.constructLightweightSymbolList(alphabet, sequenceString);
	}

	/**
	 * @param encodedSequence
	 * @param begin 
	 * @param end 
	 * @return the sequence in {@link String} form that is stored in encodedSequence
	 * @throws IllegalSymbolException
	 * @throws BioException
	 */
	public String decodeIntegerArrayToString(int[] encodedSequence, int begin, int end) {

		if ((end - begin) + 1 < subSequenceLength) {
			return decoteIntegerArrayToStringShortenOneSubSequence(encodedSequence, begin, end);
		}
		StringBuilder sequence = new StringBuilder();

		int arrayPos = (begin / subSequenceLength) + 1;
		int posInInt = begin % subSequenceLength;

		if (posInInt != 0) {
			sequence.append(decodeIntegerToString(encodedSequence[arrayPos], posInInt,
					subSequenceLength-1));
			arrayPos++;
		}

		int arrayPosLast = end / subSequenceLength;
		for (; arrayPos <= arrayPosLast; arrayPos++) {
			sequence.append(decodeIntegerToString(encodedSequence[arrayPos], subSequenceLength));
		}

		int posInIntLast = end % subSequenceLength;
		sequence.append(decodeIntegerToString(encodedSequence[arrayPos], 0, posInIntLast));

		return sequence.toString();
	}

	private String decoteIntegerArrayToStringShortenOneSubSequence(int[] encodedSequence,
			int begin, int end) {

		int arrayPosBegin = (begin / subSequenceLength) + 1;
		int arrayPosEnd = (end / subSequenceLength) + 1;
		int firstInt = encodedSequence[arrayPosBegin];

		if (arrayPosBegin == arrayPosEnd) {
			return decodeIntegerToString(firstInt, begin, end);
		}

		StringBuilder sequence = new StringBuilder();
		int beginPos = begin % subSequenceLength;
		sequence.append(decodeIntegerToString(firstInt, beginPos, subSequenceLength - 1));
		int endPos = end % subSequenceLength;
		sequence.append(decodeIntegerToString(encodedSequence[arrayPosEnd], 0, endPos));
		return sequence.toString();
	}

	/**
	 * @param encodedSequence
	 * @return the Sequence in String form encoded in encodedSequence.
	 */
	public String decodeIntegerArrayToString(int[] encodedSequence) {
		StringBuilder sequence = new StringBuilder(encodedSequence[getPositionLength()]);
		int extra = encodedSequence[getPositionLength()] % subSequenceLength;

		if (extra == 0) {
			for (int i = getPositionBeginBitsVector(); i < encodedSequence.length; i++) {
				sequence.append(decodeIntegerToString(encodedSequence[i], subSequenceLength));
			}
			return sequence.toString();

		}
		int i;
		for (i = getPositionBeginBitsVector(); i < encodedSequence.length - 1; i++) {
			sequence.append(decodeIntegerToString(encodedSequence[i], subSequenceLength));
		}
		sequence.append(decodeIntegerToString(encodedSequence[i], extra));
		return sequence.toString();
	}
	
	//TODO: 1o. aplico a mask e depois faco o shift right, nao seria melhor fazer inverso?
	public static int getValueAtPos(int[] encodedSequence, int pos, int subSequenceLength) {
		int posInArray = (pos / subSequenceLength) + 1;
		int posInInt = (subSequenceLength) - (pos % subSequenceLength) ;
		int vectorValue = encodedSequence[posInArray]; 		
		int shift = posInInt * 2;
		int value = vectorValue >> (shift - 2);
		return value & 3;
	}
}