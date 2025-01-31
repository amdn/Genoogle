/*
 * Genoogle: Similar DNA Sequences Searching Engine and Tools. (http://genoogle.pih.bio.br)
 * Copyright (C) 2008,2009  Felipe Fernandes Albrecht (felipe.albrecht@gmail.com)
 *
 * For further information check the LICENSE file.
 */

package bio.pih.genoogle.tests.encoder;

import junit.framework.TestCase;

import org.junit.Test;

import bio.pih.genoogle.encoder.SequenceEncoder;
import bio.pih.genoogle.encoder.SequenceEncoderFactory;
import bio.pih.genoogle.index.ValueOutOfBoundsException;
import bio.pih.genoogle.seq.DNAAlphabet;
import bio.pih.genoogle.seq.IllegalSymbolException;
import bio.pih.genoogle.seq.LightweightSymbolList;
import bio.pih.genoogle.seq.RNAAlphabet;
import bio.pih.genoogle.seq.SymbolList;

/**
 * Test the encoding and decoding from {@link DNASequenceCompressorToInteger}
 * 
 * @author albrecht
 */
public class SequenceEncoderToIntegerTest extends TestCase {

	@Test
	public void testEncodeDNASubSymbolList() throws ValueOutOfBoundsException, IllegalSymbolException {
		SequenceEncoder encoder = SequenceEncoderFactory.getEncoder(DNAAlphabet.SINGLETON, 8);
		
		String stringSequence = "TCGGACTG"; // 1101101000011110
		SymbolList symbolList = LightweightSymbolList.createDNA(stringSequence);
		assertEquals(Integer.parseInt("1101101000011110", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "AACAACAA"; // 0000010000010000
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		assertEquals(Integer.parseInt("0000010000010000", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "CCCCCCCC"; // 0101010101010101
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		assertEquals(Integer.parseInt("0101010101010101", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "TTTTTTTT"; // 1111111111111111
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		assertEquals(Integer.parseInt("1111111111111111", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "ACTGGTCA"; // 0001111010110100
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		assertEquals(Integer.parseInt("0001111010110100", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "ATTTTTTT"; // 001111111111111
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		assertEquals(Integer.parseInt("0011111111111111", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "TCTAGCCA"; // 1101110010010100
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		assertEquals(Integer.parseInt("1101110010010100", 2), encoder.encodeSubSequenceToInteger(symbolList));
	}

	@Test
	public void testDecodeToStringSDNASubSequence() throws ValueOutOfBoundsException {
		SequenceEncoder encoder = SequenceEncoderFactory.getEncoder(DNAAlphabet.SINGLETON, 8);

		// String stringSequence = "TCGGACTG"; // 1101101000011110
		String stringSequence = encoder.decodeIntegerToString(Integer.parseInt("1101101000011110", 2));
		assertEquals("TCGGACTG", stringSequence);

		// String stringSequence = "AACAACAA"; // 0000010000010000
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("0000010000010000", 2));
		assertEquals("AACAACAA", stringSequence);

		// stringSequence = "CCCCCCCC"; // 0101010101010101
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("0101010101010101", 2));
		assertEquals("CCCCCCCC", stringSequence);

		// stringSequence = "TTTTTTTT"; // 1111111111111111
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("1111111111111111", 2));
		assertEquals("TTTTTTTT", stringSequence);

		// stringSequence = "ACTGGTCA"; // 0001111010110100
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("0001111010110100", 2));
		assertEquals("ACTGGTCA", stringSequence);

		// stringSequence = "ATTTTTTT"; // 0011111111111111
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("0011111111111111", 2));
		assertEquals("ATTTTTTT", stringSequence);

		// stringSequence = "TCTAGCCA"; // 1101110010010100
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("1101110010010100", 2));
		assertEquals("TCTAGCCA", stringSequence);
	}


	@Test
	public void testEncodedAndDecodeToIntegerDNASubSequence() throws ValueOutOfBoundsException, IllegalSymbolException {
		SequenceEncoder encoder = SequenceEncoderFactory.getEncoder(DNAAlphabet.SINGLETON, 8);

		String stringSequence = "TCGGACTG"; // 1101101000011110
		SymbolList symbolList = LightweightSymbolList.createDNA(stringSequence);
		int encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "AACAACAA"; // 0000010000010000
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "CCCCCCCC"; // 0101010101010101
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "TTTTTTTT"; // 1111111111111111
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "ACTGGTCA"; // 0001111010110100
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "ATTTTTTT"; // 0011111111111111
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "TCTAGCCA"; // 1101110010010100
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));
		
		stringSequence = "TCTAGCAA"; // 1101110010010000
		symbolList = LightweightSymbolList.createDNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));
	}

	/**
	 * Test the sequence encoding of {@link DNASequenceCompressorToInteger}
	 */
	@Test
	public void testDecodeToIntegerDNASequence() throws ValueOutOfBoundsException, IllegalSymbolException {
		SequenceEncoder encoder = SequenceEncoderFactory.getEncoder(DNAAlphabet.SINGLETON, 8);

		SymbolList createDNA = LightweightSymbolList.createDNA("TCTAGCCAATTTTTTTACTGGTCATTTTTTTTCCCCCCCCAACAACAATCGGACTG");		                                                        
		int[] encodeSequenceToInteger = encoder.encodeSymbolListToIntegerArray(createDNA);
		assertEquals(Integer.parseInt("1101110010010100", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()]);
		assertEquals(Integer.parseInt("0011111111111111", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+1]);
		assertEquals(Integer.parseInt("0001111010110100", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+2]);
		assertEquals(Integer.parseInt("1111111111111111", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+3]);
		assertEquals(Integer.parseInt("0101010101010101", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+4]);
		assertEquals(Integer.parseInt("0000010000010000", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+5]);
		assertEquals(Integer.parseInt("1101101000011110", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+6]);
		assertEquals( createDNA.getLength(), encodeSequenceToInteger[SequenceEncoder.getPositionLength()]);
		assertEquals( createDNA.seqString(), encoder.decodeIntegerArrayToString(encodeSequenceToInteger));
		

		createDNA = LightweightSymbolList.createDNA("TCTAGC");
		encodeSequenceToInteger = encoder.encodeSymbolListToIntegerArray(createDNA);
		assertEquals(Integer.parseInt("1101110010010000", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()]);
		assertEquals(createDNA.getLength(), encodeSequenceToInteger[SequenceEncoder.getPositionLength()]);
		assertEquals(createDNA.seqString(), encoder.decodeIntegerArrayToString(encodeSequenceToInteger));

		createDNA = LightweightSymbolList.createDNA("TTTTACTGGTC");
		encodeSequenceToInteger = encoder.encodeSymbolListToIntegerArray(createDNA);
		assertEquals(Integer.parseInt("1111111100011110", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()]);
		assertEquals(Integer.parseInt("1011010000000000", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+1]);
		assertEquals(createDNA.getLength(), encodeSequenceToInteger[SequenceEncoder.getPositionLength()]);
		assertEquals(createDNA.seqString(), encoder.decodeIntegerArrayToString(encodeSequenceToInteger));

		createDNA = LightweightSymbolList.createDNA("AAACACTA" + // 0000000100011100
				"GCTACGTC" + // 1001110001101101
				"GAATAGCA" + // 1000001100100100
				"ACTGAGAT" + // 0001111000100011
				"GCATGAGC" + // 1001001110001001
				"ACAACTG"); //  0001000001111000
		encodeSequenceToInteger = encoder.encodeSymbolListToIntegerArray(createDNA);
		assertEquals(Integer.parseInt("0000000100011100", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()]);
		assertEquals(Integer.parseInt("1001110001101101", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+1]);
		assertEquals(Integer.parseInt("1000001100100100", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+2]);
		assertEquals(Integer.parseInt("0001111000100011", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+3]);
		assertEquals(Integer.parseInt("1001001110001001", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+4]);
		assertEquals(Integer.parseInt("0001000001111000", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+5]);
		assertEquals(createDNA.getLength(), encodeSequenceToInteger[SequenceEncoder.getPositionLength()]);
		assertEquals(createDNA.seqString(), encoder.decodeIntegerArrayToString(encodeSequenceToInteger));
	}
	
	@Test
	public void testEncodeRNASubSymbolList() throws ValueOutOfBoundsException, IllegalSymbolException {
		SequenceEncoder encoder = SequenceEncoderFactory.getEncoder(RNAAlphabet.SINGLETON, 8);
		
		String stringSequence = "UCGGACUG"; // 1101101000011110
		SymbolList symbolList = LightweightSymbolList.createRNA(stringSequence);
		assertEquals(Integer.parseInt("1101101000011110", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "AACAACAA"; // 0000010000010000
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		assertEquals(Integer.parseInt("0000010000010000", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "CCCCCCCC"; // 0101010101010101
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		assertEquals(Integer.parseInt("0101010101010101", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "UUUUUUUU"; // 1111111111111111
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		assertEquals(Integer.parseInt("1111111111111111", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "ACUGGUCA"; // 0001111010110100
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		assertEquals(Integer.parseInt("0001111010110100", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "AUUUUUUU"; // 001111111111111
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		assertEquals(Integer.parseInt("0011111111111111", 2), encoder.encodeSubSequenceToInteger(symbolList));

		stringSequence = "UCUAGCCA"; // 1101110010010100
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		assertEquals(Integer.parseInt("1101110010010100", 2), encoder.encodeSubSequenceToInteger(symbolList));
	}

	@Test
	public void testDecodeToStringRNASubSequence() throws ValueOutOfBoundsException {
		SequenceEncoder encoder = SequenceEncoderFactory.getEncoder(RNAAlphabet.SINGLETON, 8);

		// String stringSequence = "TCGGACTG"; // 1101101000011110
		String stringSequence = encoder.decodeIntegerToString(Integer.parseInt("1101101000011110", 2));
		assertEquals("UCGGACUG", stringSequence);

		// String stringSequence = "AACAACAA"; // 0000010000010000
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("0000010000010000", 2));
		assertEquals("AACAACAA", stringSequence);

		// stringSequence = "CCCCCCCC"; // 0101010101010101
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("0101010101010101", 2));
		assertEquals("CCCCCCCC", stringSequence);

		// stringSequence = "TTTTTTTT"; // 1111111111111111
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("1111111111111111", 2));
		assertEquals("UUUUUUUU", stringSequence);

		// stringSequence = "ACTGGTCA"; // 0001111010110100
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("0001111010110100", 2));
		assertEquals("ACUGGUCA", stringSequence);

		// stringSequence = "ATTTTTTT"; // 0011111111111111
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("0011111111111111", 2));
		assertEquals("AUUUUUUU", stringSequence);

		// stringSequence = "TCTAGCCA"; // 1101110010010100
		stringSequence = encoder.decodeIntegerToString(Integer.parseInt("1101110010010100", 2));
		assertEquals("UCUAGCCA", stringSequence);
	}


	@Test
	public void testEncodedAndDecodeToIntegerRNASubSequence() throws ValueOutOfBoundsException, IllegalSymbolException {
		SequenceEncoder encoder = SequenceEncoderFactory.getEncoder(RNAAlphabet.SINGLETON, 8);

		String stringSequence = "UCGGACUG"; // 1101101000011110
		SymbolList symbolList = LightweightSymbolList.createRNA(stringSequence);
		int encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "AACAACAA"; // 0000010000010000
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "CCCCCCCC"; // 0101010101010101
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "UUUUUUUU"; // 1111111111111111
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "ACUGGUCA"; // 0001111010110100
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "AUUUUUUU"; // 0011111111111111
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));

		stringSequence = "UCUAGCCA"; // 1101110010010100
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));
		
		stringSequence = "UCUAGCAA"; // 1101110010010000
		symbolList = LightweightSymbolList.createRNA(stringSequence);
		encoded = encoder.encodeSubSequenceToInteger(symbolList);
		assertEquals(symbolList.seqString(), encoder.decodeIntegerToString(encoded));
	}

	@Test
	public void testDecodeToIntegerRNASequence() throws ValueOutOfBoundsException, IllegalSymbolException {
		SequenceEncoder encoder = SequenceEncoderFactory.getEncoder(RNAAlphabet.SINGLETON, 8);

		SymbolList createRNA = LightweightSymbolList.createRNA("UCUAGCCAAUUUUUUUACUGGUCAUUUUUUUUCCCCCCCCAACAACAAUCGGACUG");		                                                        
		int[] encodeSequenceToInteger = encoder.encodeSymbolListToIntegerArray(createRNA);
		assertEquals(Integer.parseInt("1101110010010100", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()]);
		assertEquals(Integer.parseInt("0011111111111111", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+1]);
		assertEquals(Integer.parseInt("0001111010110100", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+2]);
		assertEquals(Integer.parseInt("1111111111111111", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+3]);
		assertEquals(Integer.parseInt("0101010101010101", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+4]);
		assertEquals(Integer.parseInt("0000010000010000", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+5]);
		assertEquals(Integer.parseInt("1101101000011110", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+6]);
		assertEquals( createRNA.getLength(), encodeSequenceToInteger[SequenceEncoder.getPositionLength()]);
		assertEquals( createRNA.seqString(), encoder.decodeIntegerArrayToString(encodeSequenceToInteger));
		

		createRNA = LightweightSymbolList.createRNA("UCUAGC");
		encodeSequenceToInteger = encoder.encodeSymbolListToIntegerArray(createRNA);
		assertEquals(Integer.parseInt("1101110010010000", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()]);
		assertEquals(createRNA.getLength(), encodeSequenceToInteger[SequenceEncoder.getPositionLength()]);
		assertEquals(createRNA.seqString(), encoder.decodeIntegerArrayToString(encodeSequenceToInteger));

		createRNA = LightweightSymbolList.createRNA("UUUUACUGGUC");
		encodeSequenceToInteger = encoder.encodeSymbolListToIntegerArray(createRNA);
		assertEquals(Integer.parseInt("1111111100011110", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()]);
		assertEquals(Integer.parseInt("1011010000000000", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+1]);
		assertEquals(createRNA.getLength(), encodeSequenceToInteger[SequenceEncoder.getPositionLength()]);
		assertEquals(createRNA.seqString(), encoder.decodeIntegerArrayToString(encodeSequenceToInteger));

		createRNA = LightweightSymbolList.createRNA("AAACACUA" + // 0000000100011100
				"GCUACGUC" + // 1001110001101101
				"GAAUAGCA" + // 1000001100100100
				"ACUGAGAU" + // 0001111000100011
				"GCAUGAGC" + // 1001001110001001
				"ACAACUG"); //  0001000001111000
		encodeSequenceToInteger = encoder.encodeSymbolListToIntegerArray(createRNA);
		assertEquals(Integer.parseInt("0000000100011100", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()]);
		assertEquals(Integer.parseInt("1001110001101101", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+1]);
		assertEquals(Integer.parseInt("1000001100100100", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+2]);
		assertEquals(Integer.parseInt("0001111000100011", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+3]);
		assertEquals(Integer.parseInt("1001001110001001", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+4]);
		assertEquals(Integer.parseInt("0001000001111000", 2), encodeSequenceToInteger[SequenceEncoder.getPositionBeginBitsVector()+5]);
		assertEquals(createRNA.getLength(), encodeSequenceToInteger[SequenceEncoder.getPositionLength()]);
		assertEquals(createRNA.seqString(), encoder.decodeIntegerArrayToString(encodeSequenceToInteger));
	}
}
