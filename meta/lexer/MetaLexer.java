package meta.lexer;

public class MetaLexer {

	/**
	 * gets a left symbol sequence of a literal object or metaobjec as input and returns
	 * the right symbol sequence:
	 *            (#  returns  #)
	 *            <(*   returns *)>
	 *
	 * @param leftCharSeq should be a sequence of char´s terminated with '\0'
	 * @param size
	 * @return
	 */
	static public String rightSymbolSeqFromLeftSymbolSeq(String leftCharSeq) {
	
		int realSize = leftCharSeq.length();
		if ( leftCharSeq.charAt(realSize - 1) == '\0' ) --realSize;
		String rightCharSeq = "";
		int j;
		char ch;
		for (j = 0; j < realSize; j++) {
			ch = leftCharSeq.charAt(realSize - j - 1);
			if (ch == ')' || ch == ']' || ch == '}' || ch == '>') {
				return null;
			} else if (ch == '(')
				ch = ')';
			else if (ch == '[')
				ch = ']';
			else if (ch == '{')
				ch = '}';
			else if (ch == '<')
				ch = '>';
			rightCharSeq = rightCharSeq + ch;
		}
		return rightCharSeq;
	}

	/**
	 * a fault escape Java string. Some day I will correct it
	   @param st
	   @return
	 */
	static public String escapeJavaString(String st) {
	
	    StringBuilder sb = new StringBuilder(st.length());
	
	    for (int i = 0; i < st.length(); i++) {
	        char ch = st.charAt(i);
	        switch ( ch ) {
	
	        case '\\':
	        	if ( i < st.length() - 1 && st.charAt(i+1) == '$' ) {
	        		sb.append("$");
	        		++i;
	        	}
	        	else {
	    	        sb.append("\\\\");
	        	}
	            break;
	        case '\b':
		        sb.append("\\b");
	            break;
	        case '\f':
		        sb.append("\\f");
	            break;
	        case '\n':
		        sb.append("\\n");
	            break;
	        case '\r':
		        sb.append("\\r");
	            break;
	        case '\t':
		        sb.append("\\t");
	            break;
	        case '\"':
		        sb.append("\\\"");
	            break;
	        case '\'':
		        sb.append("\\\'");
	            break;
	        default:
		        sb.append(ch);
	        }
	    }
	    return sb.toString();
	}

	/**
	 * Unescapes a string that contains standard Java escape sequences.
	 * <ul>
	 * <li><strong>\b \f \n \r \t \" \'</strong> :
	 * BS, FF, NL, CR, TAB, double and single quote.</li>
	 * <li><strong>\X \XX \XXX</strong> : Octal character
	 * specification (0 - 377, 0x00 - 0xFF).</li>
	 * <li><strong>\ uXXXX</strong> : Hexadecimal based Unicode character.</li>
	 * </ul>
	 * Taken from Udo Java blog, https://udojava.com/2013/09/28/unescape-a-string-that-contains-standard-java-escape-sequences/
	 *
	 * @param st
	 *            A string optionally containing standard java escape sequences.
	 * @return The translated string.
	 */
	static public String unescapeJavaString(String st) {
	
	    StringBuilder sb = new StringBuilder(st.length());
	
	    for (int i = 0; i < st.length(); i++) {
	        char ch = st.charAt(i);
	        if (ch == '\\') {
	            char nextChar = (i == st.length() - 1) ? '\\' : st
	                    .charAt(i + 1);
	            // Octal escape?
	            if (nextChar >= '0' && nextChar <= '7') {
	                String code = "" + nextChar;
	                i++;
	                if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                        && st.charAt(i + 1) <= '7') {
	                    code += st.charAt(i + 1);
	                    i++;
	                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                            && st.charAt(i + 1) <= '7') {
	                        code += st.charAt(i + 1);
	                        i++;
	                    }
	                }
	                sb.append((char) Integer.parseInt(code, 8));
	                continue;
	            }
	            switch (nextChar) {
	            case '\\':
	                ch = '\\';
	                break;
	            case 'b':
	                ch = '\b';
	                break;
	            case 'f':
	                ch = '\f';
	                break;
	            case 'n':
	                ch = '\n';
	                break;
	            case 'r':
	                ch = '\r';
	                break;
	            case 't':
	                ch = '\t';
	                break;
	            case '\"':
	                ch = '\"';
	                break;
	            case '\'':
	                ch = '\'';
	                break;
	            // Hex Unicode: u????
	            case 'u':
	                if (i >= st.length() - 5) {
	                    ch = 'u';
	                    break;
	                }
	                int code = Integer.parseInt(
	                        "" + st.charAt(i + 2) + st.charAt(i + 3)
	                                + st.charAt(i + 4) + st.charAt(i + 5), 16);
	                sb.append(Character.toChars(code));
	                i += 5;
	                continue;
	            }
	            i++;
	        }
	        sb.append(ch);
	    }
	    return sb.toString();
	}

	static public boolean isIdentifier(String s) {
	
		if ( s.length() == 0 )
			return false;
		char ch = s.charAt(0);
		if ( Character.isLetter(ch) || ch == '_' ) {
			for (int i = 0; i < s.length(); ++i) {
				ch = s.charAt(i);
				if ( ! Character.isLetterOrDigit(ch) && ch != '_' )
					return false;
			}
		}
		else {
			return false;
		}
		return true;
	}

}
