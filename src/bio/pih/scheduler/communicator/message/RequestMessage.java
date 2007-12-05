package bio.pih.scheduler.communicator.message;

/**
 * A message that is send by the Server to the Worker for him to execute a query. 
 * @author albrecht
 * @date 02/12/2007
 */
public class RequestMessage extends Message {
	private static final long serialVersionUID = 7129366659656051697L;

	private String query;
	private String database;
	
	/**
	 * A message represeting 
	 * @param database - the database where the search will be performed
	 * @param query - the query
	 * TODO: parameters like blodsum matrix or seed size.
	 */
	public RequestMessage(String database, String query) {
		this.database = database;
		this.query = query;
	}
	
	@Override
	public MessageKind getKind() {
		return MessageKind.REQUEST;
	}
	
	/**
	 * Set the <code>query</code>, that is, the sequence that will be searched againds the <code>database</code>. 
	 * @param query
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	
	/**
	 * Get the <code>query</code>, that is, the sequence that will be searched againds the <code>database</code>.
	 * @return the <code>query</code> 
	 */
	public String getQuery() {
		return query;
	}
	
	/**
	 * Set the database where will be performed the search.
	 * @param database
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
	
	/**
	 * Get the database where will be performed the search.
	 * @return
	 */
	public String getDatabase() {
		return database;
	}
}
