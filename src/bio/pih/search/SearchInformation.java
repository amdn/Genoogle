package bio.pih.search;

/**
 * Class that hold informations about one search process.
 * @author albrecht
 */
public class SearchInformation {
	
	/**
	 * The step where the search is. 
	 * @author albrecht
	 *
	 */
	public enum SearchStep {
		/**
		 * Not initialized yet
		 */
		NOT_INITIALIZED,
		/**
		 * Searching the seeds
		 */
		SEEDS,
		
		/**
		 * Doing the alignments
		 */
		ALIGNMENT,
		
		/**
		 * Selecting the best alignments 
		 */
		SELECTING,
		
		/**
		 * The search finalized
		 */
		FINISHED,
		
			
		/**
		 * The search was canceled and stopped
		 */
		CANCELED,
		
		
		/**
		 * 
		 */
		ERASED;
	}
	
	String db;
	String query;
	SearchStep actualStep;
	long code;
		
	/**
	 * @param db
	 * @param query
	 * @param code 
	 */
	public SearchInformation(String db, String query, long code) {
		this.db = db;
		this.query = query;
		this.code = code;
		this.actualStep = SearchStep.NOT_INITIALIZED;
	}
	
	/**
	 * @param db
	 */
	public void setDb(String db) {
		this.db = db;
	}
	
	
	/**
	 * @return db
	 */
	public String getDb() {
		return db;
	}
	
	/**
	 * @param query
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	
	/**
	 * @return query
	 */
	public String getQuery() {
		return query;
	}
	
	/**
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}
	
	/**
	 * @return
	 */
	public long getCode() {
		return code;
	}
	
	/**
	 * @return the actual search step.
	 */
	public SearchStep getActualStep() {
		return actualStep;
	}
	
	/**
	 * @param step
	 */
	public void setActualStep(SearchStep step) {
		this.actualStep = step;		
	}
	
	@Override
	public String toString() {
		return "Query '"+ query + "' against " + db + " ("+actualStep+")";
	}
}
