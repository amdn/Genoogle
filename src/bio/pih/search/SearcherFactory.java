package bio.pih.search;

import bio.pih.io.DatabankCollection;
import bio.pih.io.IndexedDNASequenceDataBank;
import bio.pih.io.SequenceDataBank;

/**
 * Construct a Searcher appropriate to the kind of {@link SequenceDataBank}.
 * 
 * @author albrecht
 */
public class SearcherFactory {

	/**
	 * @param id 
	 * @param sp
	 * @param databank
	 * @param sm 
	 * @param parent
	 * @return {@link Searcher} related with the data bank given.
	 */
	static public AbstractSearcher getSearcher(long id, SearchParams sp, SequenceDataBank databank, SearchManager sm, AbstractSearcher parent) {
		if (databank instanceof IndexedDNASequenceDataBank) {
			return new DNASearcher(id, sp, databank, sm, parent);
		}
		
		if (databank instanceof DatabankCollection) {
			return new CollectionSearcher(id, sp, databank, sm, parent); 
		}
		
		throw new UnsupportedOperationException("Factory for " + databank.getClass().getName() + " not implemented yet. :-( ");		
	}
}
