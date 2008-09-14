package bio.pih.search;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import bio.pih.io.SequenceDataBank;
import bio.pih.search.results.SearchResults;

import com.google.common.collect.Maps;

/**
 * Manage Searchers, check its status and stores and returns its results.
 * 
 * @author albrecht
 */
public class SearchManager {

	static Logger logger = Logger.getLogger(SearchManager.class.getName());
	
	Map<String, SequenceDataBank> databanks;

	private Map<Long, AbstractSearcher> waitingQueue;
	private Map<Long, AbstractSearcher> runningQueue;
	private Map<Long, AbstractSearcher> finishedQueue;
	private final int maxSimulaneousSearchs;

	/**
	 * @param maxSimulaneousSearchs
	 * 
	 */
	public SearchManager(int maxSimulaneousSearchs) {
		this.maxSimulaneousSearchs = maxSimulaneousSearchs;
		databanks = Maps.newHashMap();
		waitingQueue = Maps.newLinkedHashMap();
		waitingQueue = Collections.synchronizedMap(waitingQueue);
		runningQueue = Maps.newHashMap();
		runningQueue = Collections.synchronizedMap(runningQueue);
		finishedQueue = Maps.newHashMap();
		finishedQueue = Collections.synchronizedMap(finishedQueue);
	}

	/**
	 * @param databank
	 */
	public void addDatabank(SequenceDataBank databank) {
		databanks.put(databank.getName(), databank);
	}

	/**
	 * @param sp
	 * @return unique identifier of the solicited search.
	 * @throws UnknowDataBankException
	 */
	public long doSearch(SearchParams sp) throws UnknowDataBankException {
		logger.info("doSearch on " + sp);
		SequenceDataBank databank = databanks.get(sp.getDatabank());
		if (databank == null) {
			throw new UnknowDataBankException(this, sp.getDatabank());
		}

		long id = getNextSearchId();

		AbstractSearcher searcher = SearcherFactory.getSearcher(id, sp, databank, this, null);

		if (runningQueue.size() < maxSimulaneousSearchs) {
			logger.info("Has space in the queue! Lets do the search");
			runningQueue.put(id, searcher);
			searcher.doSearch();
		} else {
			waitingQueue.put(id, searcher);
			logger.info("No space in the queue, lets wait together with the others " + waitingQueue.size() + " tasks.");			
		}

		return id;
	}

	public synchronized void setFinished(long finishedId) {
		AbstractSearcher finishedSearcher = runningQueue.remove(finishedId);
		finishedQueue.put(finishedId, finishedSearcher);

		Iterator<Entry<Long, AbstractSearcher>> iterator = waitingQueue.entrySet().iterator();
		if (iterator.hasNext()) {
			System.out.println("Removing one search in the waiting queue, has "
					+ waitingQueue.size() + " searches waiting..");
			Entry<Long, AbstractSearcher> next = iterator.next();
			AbstractSearcher nextSearcher = next.getValue();
			Long id = next.getKey();
			runningQueue.put(id, nextSearcher);
			waitingQueue.remove(id);
			nextSearcher.doSearch();
		}
	}

	public synchronized boolean hasPeding() {
		return ((waitingQueue.size() > 0) || (runningQueue.size() > 0));
	}

	/*
	 * @param code
	 * 
	 * @return <code>true</code> if the search is completed.
	 */
	public boolean checkSearch(long code) {
            return finishedQueue.containsKey(code);
	}

	/**
	 * @param code
	 * @return {@link SearchResults} of the related search.
	 */
	public SearchResults getResult(long code) {
		AbstractSearcher searcher = finishedQueue.get(code);
		if (searcher == null) {
			return null;
		}
		return searcher.getStatus().getResults();
	}

	/**
	 * @return {@link Collection} of all {@link SequenceDataBank} that this {@link SearchResults} is
	 *         managing.
	 */
	public Collection<SequenceDataBank> getDatabanks() {
		return databanks.values();
	}

	private long searchId = 0;

	private synchronized long getNextSearchId() {
		long id = searchId;
		searchId++;
		return id;
	}

}
