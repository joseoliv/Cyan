package meta.cyanLang;

import java.util.List;
import meta.WrMessageKeywordWithRealParameters;

/**
 * this class is used for parsing a message send, that is, a sequence of keywords with their real parameters.
   @author jose
 */

public class MessageKeywordLexer {

	public MessageKeywordLexer(List<WrMessageKeywordWithRealParameters> selList) {
		this.selList = selList;
		index = 0;
		if ( selList.size() > 0 ) {
			current = selList.get(0);
		}
		else
			current = null;
	}

	public WrMessageKeywordWithRealParameters current() {
		return current;
	}

	public WrMessageKeywordWithRealParameters next() {
		if ( index < selList.size() - 1 ) {
			++index;
			current = selList.get(index);
		}
		else {
			index = selList.size();
			current = null;
		}
		return current;
	}


	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		if ( index < selList.size() ) {
			this.index = index;
			current = selList.get(index);
		}
		else {
			this.index = selList.size();
			current = null;
		}
	}



	private List<WrMessageKeywordWithRealParameters> selList;
	private WrMessageKeywordWithRealParameters current;
	private int index;
}
