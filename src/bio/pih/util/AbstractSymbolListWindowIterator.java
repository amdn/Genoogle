package bio.pih.util;

import org.biojava.bio.symbol.SymbolList;

/**
 * @author albrecht
 *
 */
public abstract class AbstractSymbolListWindowIterator implements SymbolListWindowIterator {

	protected int windowSize;
	protected int actualPos;
	protected SymbolList sequence;

	
	/**
	 * @param sequence
	 * @param windowLength
	 * @throws IndexOutOfBoundsException
	 */
	public AbstractSymbolListWindowIterator(SymbolList sequence, int windowLength) throws IndexOutOfBoundsException {
		if (windowLength < 1) {
			throw new IndexOutOfBoundsException("The windowSize must has the size at least one");
		}
		this.sequence = sequence;
		this.windowSize = windowLength;
		this.actualPos = 0;
	}

	public SymbolList getSymbolList() {
		return sequence;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int size) {
		this.windowSize = size;

	}

	public int getActualPos() {
		return actualPos;
	}

	public boolean hasNext() {
		if (actualPos + windowSize <= sequence.length()) {
			return true;
		}
		return false;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}