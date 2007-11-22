package bio.pih.indexer;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.biojava.bio.symbol.Alphabet;
import org.biojava.bio.symbol.SymbolList;

public class SimpleSubSequencesIndex implements SubSequecesIndex, Serializable {
	
	private static final long serialVersionUID = 7353450597201554133L;
	
	Alphabet alphabet;
	int subSequenceLength;
	Hashtable<SymbolList, List<SubSequenceInfo>> index;
	int total; // just statistical information
	int subSymbolTotal; // just statistical information 
	
	public SimpleSubSequencesIndex(Alphabet alphabet, int subSequenceLength) {
		this.alphabet = alphabet;
		this.subSequenceLength = subSequenceLength;
		this.total = 0;
		this.subSymbolTotal = 0;
		this.index = new Hashtable<SymbolList, List<SubSequenceInfo>>();
	}
			
	public void addSubSequence(SymbolList subSymbolList, SubSequenceInfo info) {
		List<SubSequenceInfo> infos = index.get(subSymbolList);
		if (infos == null) {
			infos = new LinkedList<SubSequenceInfo>();
			index.put(subSymbolList, infos);
			this.subSymbolTotal++;
		}
		total++;
		infos.add(info);
	}

	public List<SubSequenceInfo> retrievePosition(SymbolList subSymbolList) {
		return index.get(subSymbolList);
	}
	
	public int getTotal() {
		return total;
	}
	
	public int getSubSymbolTotal() {
		return subSymbolTotal;
	}

}
