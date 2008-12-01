package bio.pih.util;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author Felipe Albrecht
 * 
 * KISS !
 * 
 * TODO: junits!
 */
public class LongArray {

	private static long[] EMPTY_ARRAY = new long[0];
	private List<long[]> blockArrays;
	private long[] finalArray;
	private long[] actualBlock;
	private int actualBockPos;
	private int blockSize;

	private static final int DEFAULT_INITIAL_SIZE = 25;
	private static final int BLOCKS_THRESHOULD = 100;

	/**
	 * Default constructor that uses default size for the blocks.
	 */
	public LongArray() {
		this(DEFAULT_INITIAL_SIZE);
	}

	/**
	 * Constructor that gives the block size.
	 * 
	 * @param blockSize
	 */
	public LongArray(int blockSize) {
		this.finalArray = null;
		this.actualBlock = null;
		this.blockArrays = null;
		this.actualBockPos = 0;
		this.blockSize = blockSize;
	}

	/**
	 * Add a new value into the array
	 * 
	 * @param value
	 */
	public void add(long value) {
		ensureCapacity();
		actualBlock[actualBockPos++] = value;
	}

	private void ensureCapacity() {
		if (actualBlock == null) {
			actualBlock = new long[blockSize];
			actualBockPos = 0;
		} else if (actualBockPos == blockSize) {
			getBlocksArray().add(actualBlock);
			if ((getBlocksArray().size() & BLOCKS_THRESHOULD) == BLOCKS_THRESHOULD) {
				getArray();
			}
			actualBlock = new long[blockSize];
			actualBockPos = 0;
		}
	}

	private List<long[]> getBlocksArray() {
		if (blockArrays == null) {
			blockArrays = Lists.newArrayList();
		}
		return blockArrays;
	}

	/**
	 * Get the data as a long array. It returns the internal array reference, do not a copy, for performance reasons.
	 * It is also used to compress the buckets into a unique array.
	 * @return the array, without empty fields.
	 */
	public long[] getArray() {
		// Special case: no data was added from begin or after the last getArray()
		if ((actualBockPos == 0) && (blockArrays == null || getBlocksArray().size() == 0)) {
			if (finalArray == null) {
				return EMPTY_ARRAY;
			}
			return finalArray;
		}

		int size = 0;
		if (finalArray != null) {
			size += finalArray.length;
		}
		if (blockArrays != null) {
			size += (getBlocksArray().size() * blockSize);
		}
		size += actualBockPos;

		long[] o = new long[size];

		int pos = 0;

		if (finalArray != null) {
			System.arraycopy(finalArray, 0, o, 0, finalArray.length);
			pos = finalArray.length;
		}

		if (blockArrays != null) {
			for (long[] block : getBlocksArray()) {
				System.arraycopy(block, 0, o, pos, blockSize);
				pos += blockSize;
			}
		}

		System.arraycopy(actualBlock, 0, o, pos, actualBockPos);

		actualBockPos = 0;
		actualBlock = null;
		blockArrays = null;
		finalArray = o;

		return finalArray;
	}

	/**
	 * Sort the array
	 */
	public void sort() {
		getArray();
		if (finalArray != null) {
			Arrays.sort(finalArray);
		}
	}

	@Override
	public String toString() {
		getArray();
		return Arrays.toString(finalArray);
	}

	/**
	 * @return the length of the data
	 */
	public int length() {
		return getArray().length;
	}

	/**
	 * Saving space. Do not create a new LongArray neither a new actualBlock. 
	 */
	public void reset() {
		actualBockPos = 0;
		blockArrays = null;
		finalArray = null;
	}

	/**
	 * test
	 * 
	 * @param args
	 */
	public static void mainX(String[] args) {
		LongArray longArray = new LongArray(3);
		longArray.add(1);
		longArray.add(2);
		longArray.add(3);
		for (long i : longArray.getArray())
			System.out.print(i + " ");
		System.out.println();
		longArray.add(2);
		longArray.add(5);
		longArray.add(3);
		for (long i : longArray.getArray())
			System.out.print(i + " ");
		System.out.println();
		longArray.add(4);
		for (long i : longArray.getArray())
			System.out.print(i + " ");
		System.out.println();
		longArray.add(30);
		longArray.add(40);
		longArray.add(50);
		longArray.add(60);
		longArray.add(70);
		longArray.add(80);
		for (long i : longArray.getArray())
			System.out.print(i + " ");
		System.out.println();
		longArray.add(15);
		longArray.add(21);
		for (long i : longArray.getArray())
			System.out.print(i + " ");
		System.out.println();
		longArray.add(6);
		longArray.add(6);
		longArray.add(6);
		longArray.add(6);
		longArray.add(7);
		longArray.add(9);
		longArray.add(1);
		longArray.add(2);
		longArray.add(3);
		longArray.add(2);
		longArray.add(5);
		longArray.add(3);
		longArray.add(4);
		for (long i : longArray.getArray())
			System.out.print(i + " ");
		System.out.println();
		longArray.add(15);
		longArray.add(6);
		longArray.add(6);
		longArray.add(6);
		longArray.add(6);
		longArray.add(7);
		longArray.add(9);
		for (long i : longArray.getArray())
			System.out.print(i + " ");
		System.out.println();
		longArray.add(6);
		longArray.add(6);
		longArray.add(6);
		longArray.add(6);
		longArray.add(7);
		longArray.add(9);
		longArray.add(7);
		longArray.add(6);
		longArray.add(11);
		for (long i : longArray.getArray())
			System.out.print(i + " ");
		System.out.println();
	}

}
