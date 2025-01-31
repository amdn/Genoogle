/*
 * Genoogle: Similar DNA Sequences Searching Engine and Tools. (http://genoogle.pih.bio.br)
 * Copyright (C) 2008,2009,2010,2011,2012  Felipe Fernandes Albrecht (felipe.albrecht@gmail.com)
 *
 * For further information check the LICENSE file.
 */

package bio.pih.genoogle.search;

import java.util.List;

import bio.pih.genoogle.index.SubSequenceIndexInfo;
import bio.pih.genoogle.util.CircularArrayList;
import bio.pih.genoogle.util.Poll;

import com.google.common.collect.Lists;

/**
 * Stores the data retrieved from the Inverted Index.
 * 
 * @author albrecht
 */
public class IndexRetrievedData {

	private final List<RetrievedArea>[] retrievedAreasArray;
	private final CircularArrayList[] openedAreasArray;
	private final Poll poll = new Poll();
	private final int minLength;
	private final int subSequenceLength;
	private final int maxSubSequenceDistance;
	public int hits;

	/**
	 * Constructor.
	 * 
	 * @param size
	 *            Quantity of sequences stored in the data bank.
	 * @param sp
	 *            Search parameters.
	 * @param subSequenceLength
	 *            Sub sequences length.
	 * @param searcher
	 *            Index searcher that is used.
	 */
	public IndexRetrievedData(int size, SearchParams sp, int subSequenceLength, IndexSearcher searcher) {
		this(size, sp, subSequenceLength, searcher, null);
	}

	@SuppressWarnings("unchecked")
	public IndexRetrievedData(int size, SearchParams sp, int subSequenceLength, IndexSearcher searche, List<RetrievedArea>[] retrievedData) {

		this.minLength = sp.getMinHspLength();
		this.subSequenceLength = subSequenceLength;
		this.maxSubSequenceDistance = sp.getMaxSubSequencesDistance();

		if (retrievedData == null) {
			this.retrievedAreasArray = new List[size];
		} else {
			this.retrievedAreasArray = retrievedData;
		}

		openedAreasArray = new CircularArrayList[size];
	}

	/**
	 * Insert a found subSequences and check if it will be merged or added as a
	 * new area.
	 * 
	 * @param queryPos
	 *            sub-sequence position in the query.
	 * @param subSequenceInfoIntRepresention
	 *            representation of the sub sequence by
	 *            {@link SubSequenceIndexInfo}.
	 */
	final void addSubSequenceInfoIntRepresention(int queryPos, long subSequenceInfoIntRepresention) {
		int sequencePos = SubSequenceIndexInfo.getStart(subSequenceInfoIntRepresention);
		int sequenceId = SubSequenceIndexInfo.getSequenceId(subSequenceInfoIntRepresention);

		mergeOrRemoveOrNew(queryPos, sequencePos, sequenceId);
	}

	/**
	 * Merge the subsequence or create a new retrieved area.
	 * 
	 * @param queryPos
	 *            Position in the query.
	 * @param sequencePos
	 *            Position in the data bank sequence.
	 * @param sequenceId
	 *            Data bank sequence id.
	 */
	private final void mergeOrRemoveOrNew(int queryPos, int sequencePos, int sequenceId) {

		CircularArrayList openedList = openedAreasArray[sequenceId];

		if (openedList == null) {
			openedList = poll.pop();
		}

		if (openedList.size() == 0) {
			openedAreasArray[sequenceId] = openedList;
			openedList.add(queryPos, sequencePos, subSequenceLength);

		} else {
			boolean merged = false;
			int totalRemove = 0;
			int pos = 0;
			for (RetrievedArea openedArea = openedList.get(pos); (openedArea = openedList.get(pos)) != null; pos++) {
				// Try merge with previous area.
				if (openedArea.testAndSet(queryPos, sequencePos, maxSubSequenceDistance, subSequenceLength)) {
					merged = true;
					openedList.rePos(openedArea, pos);
					// Check if the area end is away from the actual sequence
					// position.
				} else if (queryPos - openedArea.getQueryAreaEnd() > maxSubSequenceDistance) {
					// Count areas to remove.
					totalRemove++;
					if (openedArea.length() >= minLength) {
						if (retrievedAreasArray[sequenceId] == null) {
							retrievedAreasArray[sequenceId] = Lists.newArrayList();
						}
						hits++;
						retrievedAreasArray[sequenceId].add(openedArea.copy());
					}
				}
			}

			if (totalRemove != 0) {
				openedList.removeElements(totalRemove);
			}

			if (!merged) {
				openedList.add(queryPos, sequencePos, subSequenceLength);
			} else {
				if (openedList.size() == 0) {
					poll.push(openedList);
					openedAreasArray[sequenceId] = null;
				}
			}
		}
	}

	/**
	 * Finish the index searching process. It will close all retrieved areas and
	 * it will check if the areas has the minumun length.
	 * 
	 * @return all {@link RetrievedArea} that has at least the minimum length.
	 */
	public List<RetrievedArea>[] finish() {
		for (int sequenceId = 0; sequenceId < openedAreasArray.length; sequenceId++) {
			CircularArrayList openedList = openedAreasArray[sequenceId];
			if (openedList != null) {
				int pos = 0;
				for (RetrievedArea openedArea = openedList.get(pos); (openedArea = openedList.get(pos)) != null; pos++) {
					if (openedArea.length() >= minLength) {
						if (retrievedAreasArray[sequenceId] == null) {
							retrievedAreasArray[sequenceId] = Lists.newArrayList();
						}
						retrievedAreasArray[sequenceId].add(openedArea);
					}
				}
			}
		}
		
		return retrievedAreasArray;
	}

	/**
	 * Get the retrieved areas.
	 * 
	 * @return all {@link RetrievedArea} that has at least the minimum length.
	 */
	public List<RetrievedArea>[] getRetrievedAreasArray() {
		return retrievedAreasArray;
	}
}
