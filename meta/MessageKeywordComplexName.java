package meta;

import java.util.List;

public class MessageKeywordComplexName {
	public String keyword;
	public List<ParameterComplexName> paramList;
	public String asString() {
		String r = keyword;
		if ( paramList != null ) {
			for ( ParameterComplexName p : paramList ) {
				if ( p.type != null ) {
					r += " " + p.type;
				}
				if ( p.name != null ) {
					r += " " + p.name;
				}
			}
		}
		return r;
	}
	public String getName() {

		if ( paramList != null && paramList.size() > 0 ) {
			return keyword + paramList.size();
		}
		else {
			return keyword;
		}
	}

}
