/*
 * Genoogle: Similar DNA Sequences Searching Engine and Tools. (http://genoogle.pih.bio.br)
 * Copyright (C) 2008,2009  Felipe Fernandes Albrecht (felipe.albrecht@gmail.com)
 *
 * For further information check the LICENSE file.
 */

package bio.pih.genoogle.encoder;

import bio.pih.genoogle.seq.SymbolList;
import bio.pih.genoogle.util.SymbolListWindowIterator;
import bio.pih.genoogle.util.SymbolListWindowIteratorFactory;

/**
 * Class that apply a mask to the given sub-sequences.
 * 
 * @author albrecht
 */
public final class MaskEncoder {

	private final boolean[] mask;
	private final int patternLength;
	private final int resultLength;
	private final SequenceEncoder encoder;

	/**
	 * @param mask Mask where "1" means that the base should be preserved and "0" that should be removed. 
	 * @param subSequenceLength The subsequence length, the value should be the total of "1"s at the mask. 
	 */
	public MaskEncoder(final String mask, final SequenceEncoder encoder) {
		this.patternLength = mask.length();
		this.mask = new boolean[patternLength];
		this.encoder = encoder;
		
		int length = 0;
		for (int i = 0; i < this.patternLength; i++) {
			if (mask.charAt(i) == '1') {
				this.mask[i] = true;
				length++;
			}
		}
		if (length != encoder.getSubSequenceLength()) {
			throw new RuntimeException("The subSequenceLength (" + encoder.getSubSequenceLength()
					+ ") and the count of the usable values of the mask (" + length + ") should be the same.");
		}
		this.resultLength = length;
	}

	public int getPatternLength() {
		return patternLength;
	}

	/**
	 * Apply the mask in a informed {@link SymbolList} and return the encoded masked sequence. 
	 * @param symbolList sequence
	 * @return encoded masked {@link SymbolList}.
	 */
	public int applyMask(SymbolList symbolList) {
		int encoded = 0;
		int offset = 0;
		int length = symbolList.getLength();

		for (int i = 1; i <= length; i++) {
			if (this.mask[i - 1]) {
				encoded |= (encoder.getBitsFromChar(symbolList.symbolAt(i)) << ((resultLength - (i - offset)) << 1));
			} else {
				offset++;
			}
		}

		return encoded;
	}

	/**
	 * Apply the mask in a informed {@link String} sub-sequence and return the encoded masked sequence. 
	 * @param subSequence 
	 * @return encoded masked sequence.
	 */
	public int applyMask(String subSequence) {
		int encoded = 0;
		int offset = 0;
		int length = subSequence.length();

		for (int i = 0; i < length; i++) {
			if (this.mask[i]) {
				encoded |= (encoder.getBitsFromChar(subSequence.charAt(i)) << ((resultLength - (i - offset + 1)) << 1));
			} else {
				offset++;
			}
		}

		return encoded;
	}

	/**
	 * Apply the mask on a portion of the given sub-sequence
	 * @param begin  of the sub-sequence
	 * @param end  of the sub-sequence.
	 * @param subSequence  where the mask will be applied.
	 * @return encoded version of the masked sub-sequence.
	 */
	public int applyMask(int begin, int end, String subSequence) {
		int encoded = 0;
		int offset = 0;

		for (int i = begin; i < end; i++) {
			int pos = i - begin;
			if (this.mask[pos]) {
				encoded |= (encoder.getBitsFromChar(subSequence.charAt(i)) << ((resultLength - (pos - offset + 1)) << 1));
			} else {
				offset++;
			}
		}

		return encoded;
	}
	
	/**
	 * Apply mask in a whole {@link SymbolList} sequence. 
	 * @param sequence  where the mask will be applied.
	 * @return encoded version of the masked sequence.
	 */
	public int[] applySequenceMask(SymbolList sequence) {
		assert (sequence.getAlphabet().equals(encoder.getAlphabet()));
		int size = sequence.getLength() / this.patternLength;

		size++; // extra space for information on the length.
		int sequenceEncoded[] = new int[size];
		sequenceEncoded[SequenceEncoder.getPositionLength()] = sequence.getLength();

		int pos = SequenceEncoder.getPositionBeginBitsVector();
		SymbolListWindowIterator symbolListWindowIterator = SymbolListWindowIteratorFactory.getNotOverlappedFactory().newSymbolListWindowIterator(
				sequence, this.patternLength);
		while (symbolListWindowIterator.hasNext()) {
			SymbolList next = symbolListWindowIterator.next();
			sequenceEncoded[pos] = applyMask(next);
			pos++;
		}

		return sequenceEncoded;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Pattern: ");
		sb.append("\"");
		for (int i = 0; i < patternLength; i++) {
			if (this.mask[i]) {
				sb.append('X');
			} else {
				sb.append(' ');
			}
		}
		sb.append("\" ");
		sb.append(patternLength);
		sb.append(" ");
		sb.append(resultLength);
		return sb.toString();
	}
}
