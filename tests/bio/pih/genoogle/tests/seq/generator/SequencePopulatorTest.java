/*
 * Genoogle: Similar DNA Sequences Searching Engine and Tools. (http://genoogle.pih.bio.br)
 * Copyright (C) 2008,2009  Felipe Fernandes Albrecht (felipe.albrecht@gmail.com)
 *
 * For further information check the LICENSE file.
 */

package bio.pih.genoogle.tests.seq.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.Test;

import bio.pih.genoogle.seq.Alphabet;
import bio.pih.genoogle.seq.DNAAlphabet;
import bio.pih.genoogle.seq.IllegalSymbolException;
import bio.pih.genoogle.seq.Sequence;
import bio.pih.genoogle.seq.generator.DNASequencesPopulator;
import bio.pih.genoogle.seq.generator.RandomSequenceGenerator;

/**
 * @author albrecht
 *
 */
public class SequencePopulatorTest extends TestCase {

	
	private static final String sequencePopulationTestFile = "data" + File.separator + "populator" + File.separator + "sequencePopulatorTest.seqs";
	
	@Override
	@AfterClass
	public void tearDown() {
		try {
			removeIfExistFile(sequencePopulationTestFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void removeIfExistFile(String filePath) throws IOException {
		File serializableSequencePopulationFile = new File(filePath);
		if (serializableSequencePopulationFile.exists()) {
			if (serializableSequencePopulationFile.delete() == false) {
				throw new IOException("Was not possible to delete " + serializableSequencePopulationFile);
			}
		}		
	}
	
	/**
	 * Test if the length and {@link Alphabet} of sequence generated are correct.  
	 */
	public void testSequenceGenerator() throws IllegalSymbolException {
		int length = 1;
		RandomSequenceGenerator randomSequenceGenerator = new RandomSequenceGenerator(DNAAlphabet.SINGLETON, length);
		Sequence sequence = randomSequenceGenerator.generateSequence();
		assertEquals(1, sequence.getLength());
		assertEquals(DNAAlphabet.SINGLETON, sequence.getAlphabet());
		
		length = 10;
		randomSequenceGenerator = new RandomSequenceGenerator(DNAAlphabet.SINGLETON, length);
		sequence = randomSequenceGenerator.generateSequence();
		assertEquals(length, sequence.getLength());
		assertEquals(DNAAlphabet.SINGLETON, sequence.getAlphabet());
		
		length = 100;
		randomSequenceGenerator = new RandomSequenceGenerator(DNAAlphabet.SINGLETON, length);
		sequence = randomSequenceGenerator.generateSequence();
		assertEquals(length, sequence.getLength());
		assertEquals(DNAAlphabet.SINGLETON, sequence.getAlphabet());
		
		length = 250;
		randomSequenceGenerator = new RandomSequenceGenerator(DNAAlphabet.SINGLETON, length);
		sequence = randomSequenceGenerator.generateSequence();
		assertEquals(length, sequence.getLength());
		assertEquals(DNAAlphabet.SINGLETON, sequence.getAlphabet());
		
		length = 1000;
		randomSequenceGenerator = new RandomSequenceGenerator(DNAAlphabet.SINGLETON, length);
		sequence = randomSequenceGenerator.generateSequence();
		assertEquals(length, sequence.getLength());
		assertEquals(DNAAlphabet.SINGLETON, sequence.getAlphabet());
		
		length = 10000;
		randomSequenceGenerator = new RandomSequenceGenerator(DNAAlphabet.SINGLETON, length);
		sequence = randomSequenceGenerator.generateSequence();
		assertEquals(length, sequence.getLength());
		assertEquals(DNAAlphabet.SINGLETON, sequence.getAlphabet());		
	}
	
	/**
	 * Test if the length of the generated sequences are correct
	 */
	public void testDNASequencesPopulator() throws IllegalSymbolException {
		int from = 0;
		int to = 3;
		List<Sequence> sequences = DNASequencesPopulator.populateSequences(100, from, to);
		for(Sequence sequence: sequences) {
			assertTrue(sequence.getLength() >= from);
			assertTrue(sequence.getLength() <= to);
		}
		
		from = 0;
		to = 100;
		sequences = DNASequencesPopulator.populateSequences(100, from, to);
		for(Sequence sequence: sequences) {
			assertTrue(sequence.getLength() >= from);
			assertTrue(sequence.getLength() <= to);
		}
		
		from = 99;
		to = 100;
		sequences = DNASequencesPopulator.populateSequences(100, from, to);
		for(Sequence sequence: sequences) {
			assertTrue(sequence.getLength() >= from);
			assertTrue(sequence.getLength() <= to);
		}
		
		from = 0;
		to = 1000;
		sequences = DNASequencesPopulator.populateSequences(100, from, to);
		for(Sequence sequence: sequences) {
			assertTrue(sequence.getLength() >= from);
			assertTrue(sequence.getLength() <= to);
		}
		
		from = 999;
		to = 1000;
		sequences = DNASequencesPopulator.populateSequences(100, from, to);
		for(Sequence sequence: sequences) {
			assertTrue(sequence.getLength() >= from);
			assertTrue(sequence.getLength() <= to);
		}
		
		from = 1;
		to = 2;
		sequences = DNASequencesPopulator.populateSequences(100, from, to);
		for(Sequence sequence: sequences) {
			assertTrue(sequence.getLength() >= from);
			assertTrue(sequence.getLength() <= to);
		}	
	}
	
	/**
	 * Test if the save and load sequence population from a file is working
	 */
	public void testCreateSaveAndLoadSequencePopulation() throws IllegalSymbolException, FileNotFoundException, IOException, ClassNotFoundException {
		List<Sequence> sequences = new LinkedList<Sequence>();
		
		String stringSequence = "CATGACTGGCATCAGTGCATGCATGCAGTCAGTATATATGACGC";
		Sequence ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 1");
		sequences.add(ss);
		
		stringSequence = "ACATGCTCGATGTGTGTGTATCAGTACTGACCTAGCATGACTCAGTACACATGACGTCATCATGTAGCGTCTAGACTGACTACGTACGACTGCATACGACTATCAGACTGACTACGCATGACGTACGTGTACGTACTGATGACGTACTATCGTAGCATGACTACGTACGACTGAC";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 1");
		sequences.add(ss);
		
		stringSequence = "ATGCTAGCATTCAGTACGTACGCATGATGCTAGATCGCATGACTAGCACGTACTGCATCGTGTGTGTCATGTGACTGAC";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 2");
		sequences.add(ss);
		
		stringSequence = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 3");
		sequences.add(ss);
		
		stringSequence = "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 4");
		sequences.add(ss);
		
		stringSequence = "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 5");
		sequences.add(ss);
		
		stringSequence = "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 6");
		sequences.add(ss);
		
		stringSequence = "ACTGGTCAACTGGTCAACTGGTCAACTGGTCAACTGGTCAACTGGTCAACTGGTCAACTGGTCA";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 7");
		sequences.add(ss);
		
		stringSequence = "ATCTGAGTCATGCGATCAGTGTTGGTCATGTCAGGTCAGTACTACGTAGCATGCATGCATACGATCGACTATATTGCATGAC";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 8");
		sequences.add(ss);
		
		stringSequence = "AAAAAAACAAAAAAAGAAAAAAATTTTTTTGCATCAGATTTTTTTTCAGTACTGCATGACTACTGTGAC";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 9");
		sequences.add(ss);
		
		stringSequence = "TGCAGTACGTACGTGTTGAGTGCTATGCATGTTTAGGCGCGGCGCTAGCATGCATCAGACGCATACGTGTACGTACGTACTGATTCAGACTGAC";
		ss = new Sequence(DNAAlphabet.SINGLETON, stringSequence, "Sequence 10");
		sequences.add(ss);
				
		removeIfExistFile(sequencePopulationTestFile);
		DNASequencesPopulator.writePopulation(sequences, sequencePopulationTestFile);
		
		List<Sequence> readSequences = DNASequencesPopulator.readPopulation(sequencePopulationTestFile);
		Iterator<Sequence> iterator = readSequences.iterator();
		assertEquals(iterator.next().seqString().toUpperCase(), "CATGACTGGCATCAGTGCATGCATGCAGTCAGTATATATGACGC");
		assertEquals(iterator.next().seqString().toUpperCase(), "ACATGCTCGATGTGTGTGTATCAGTACTGACCTAGCATGACTCAGTACACATGACGTCATCATGTAGCGTCTAGACTGACTACGTACGACTGCATACGACTATCAGACTGACTACGCATGACGTACGTGTACGTACTGATGACGTACTATCGTAGCATGACTACGTACGACTGAC");
		assertEquals(iterator.next().seqString().toUpperCase(), "ATGCTAGCATTCAGTACGTACGCATGATGCTAGATCGCATGACTAGCACGTACTGCATCGTGTGTGTCATGTGACTGAC");
		assertEquals(iterator.next().seqString().toUpperCase(), "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		assertEquals(iterator.next().seqString().toUpperCase(), "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
		assertEquals(iterator.next().seqString().toUpperCase(), "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
		assertEquals(iterator.next().seqString().toUpperCase(), "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
		assertEquals(iterator.next().seqString().toUpperCase(), "ACTGGTCAACTGGTCAACTGGTCAACTGGTCAACTGGTCAACTGGTCAACTGGTCAACTGGTCA");
		assertEquals(iterator.next().seqString().toUpperCase(), "ATCTGAGTCATGCGATCAGTGTTGGTCATGTCAGGTCAGTACTACGTAGCATGCATGCATACGATCGACTATATTGCATGAC");
		assertEquals(iterator.next().seqString().toUpperCase(), "AAAAAAACAAAAAAAGAAAAAAATTTTTTTGCATCAGATTTTTTTTCAGTACTGCATGACTACTGTGAC");
		assertEquals(iterator.next().seqString().toUpperCase(), "TGCAGTACGTACGTGTTGAGTGCTATGCATGTTTAGGCGCGGCGCTAGCATGCATCAGACGCATACGTGTACGTACGTACTGATTCAGACTGAC");
		assertFalse(iterator.hasNext());
		removeIfExistFile(sequencePopulationTestFile);
	}
		
	/**
	 * What is enough? May be 1k sequences is enough ? May be one million is huge, but 1k is enough for this test, I think...
	 */
	@Test
	public void testCreateSaveAndLoadRandomSequencePopulation() throws FileNotFoundException, IOException, ClassNotFoundException, IllegalSymbolException {
		int stepSize = 10;
		int maxSequences = 1000;
		int lengthFrom = 20;
		int lengthTo = 700;
		
		List<List<Sequence>> populations = new LinkedList<List<Sequence>>();
		for (int sequenceQuantity = stepSize; sequenceQuantity <= maxSequences; sequenceQuantity *= stepSize) {
			System.out.println("Creating population: " + sequenceQuantity + " sequences");
			String populationPath = sequencePopulationTestFile + "_" + sequenceQuantity;
			List<Sequence> populateSequences = DNASequencesPopulator.populateSequences(sequenceQuantity, lengthFrom, lengthTo);
			populations.add(populateSequences);
			removeIfExistFile(populationPath);
			DNASequencesPopulator.writePopulation(populateSequences, populationPath);
		}
		
		int listCreatedPos = 0;
		for (int sequenceQuantity = stepSize; sequenceQuantity <= maxSequences; sequenceQuantity *= stepSize) {
			String populationPath = sequencePopulationTestFile + "_" + sequenceQuantity;
			System.out.println("Testing population: " + sequenceQuantity + " sequences");
			Iterator<Sequence> storedPopulationIterator = DNASequencesPopulator.readPopulation(populationPath).iterator();
			Iterator<Sequence> createdPopulationIterator = populations.get(listCreatedPos).iterator();
			while (storedPopulationIterator.hasNext() && createdPopulationIterator.hasNext()) {
				Sequence nextStored = storedPopulationIterator.next();
				Sequence nextCreated = createdPopulationIterator.next();
				checkSequenceEquals(nextStored, nextCreated);
			}
			assertFalse(storedPopulationIterator.hasNext());
			assertFalse(createdPopulationIterator.hasNext());
			removeIfExistFile(populationPath);
			listCreatedPos++;
		}
	}
	
	private static void checkSequenceEquals(Sequence seq1, Sequence seq2) {
		assertEquals(seq1.getAlphabet().getClass(), seq2.getAlphabet().getClass());
		assertEquals(seq1.getName(), seq2.getName());
		assertEquals(seq1.getLength(), seq2.getLength());
		assertEquals(seq1.seqString(), seq2.seqString());
	}
}
