package meta;

import java.util.List;


public class MethodComplexName {
	public MethodComplexName() { }
	public String getName() {
		if ( this.methodName == null ) {
			this.methodName = "";
			int size = messageKeywordArray.size();
			if ( size == 1 ) {
				String s = this.messageKeywordArray.get(0).keyword;
				char ch = s.charAt(0);
				if ( !Character.isAlphabetic(ch) && ch != '_' ) {
					methodName = s;
					MessageKeywordComplexName scn = this.messageKeywordArray.get(0);
					if ( scn.paramList != null && scn.paramList.size() != 0 ) {
						methodName += "1"; //     "+1"
					}
					return methodName;
				}
				else if ( ! s.endsWith(":") ) {
					// unary method
					methodName = s;
					return methodName;
				}
			}
			for ( MessageKeywordComplexName cn : this.messageKeywordArray ) {
				if ( cn.paramList == null ) {
					methodName += cn.keyword + "0";
				}
				else {
					methodName += cn.keyword + cn.paramList.size();
				}
				if ( --size > 0 ) {
					methodName += " ";
				}
			}
		}
		return this.methodName;
	}
	public List<MessageKeywordComplexName> messageKeywordArray;
	public String returnType;
	public String asString() {
		String r = "";
		for ( MessageKeywordComplexName sel : this.messageKeywordArray ) {
			r += sel.asString();
		}
		return r + " -> " + returnType;
	}

	public String methodName = null;
}
