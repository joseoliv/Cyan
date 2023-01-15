package meta;

import java.util.List;

/**
 * Represents a Cyan macro, which is also a metaobject
   @author José
 */
public class CyanMetaobjectMacro extends CyanMetaobject
                implements IParseMacro_parsing, IAction_semAn {

	public CyanMetaobjectMacro(String []startKeywords, String []macroKeywords) {
		this.startKeywords = startKeywords;
		this.macroKeywords = macroKeywords;
	}


	public CyanMetaobjectMacro myClone() {
		try {
			return (CyanMetaobjectMacro ) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}


	public static String whiteSpace = "                                                                                                                                                                                                                                                                           ";
	public static int sizeWhiteSpace = whiteSpace.length() - 1;
	/**
	 * return a list of keywords that may start the macro. For example, there could be an 'assert' macro:
	 *
	 *     assert n >= 0;
	 *
	 * The metaobject that represents macro 'assert' would return  new String[] { "assert" } in this method
	 */
	public final String []getStartKeywords() {
		return this.startKeywords;
	}


	/**
	 * return a list of keywords that this macro uses. For example, consider the macro <code>'enquanto'</code>
	 * that plays the role of a 'while' statement: </p>
	 * <code>
	 *          enquanto s < p faca { </p>
	 *              ++s;  </p>
	 *          }  </p>
	 *</code> </p>
	 *
	 * Method <code>'getKeywords'</code> would return </p>
	 * <code>    'new String[] { "enquanto", "faca" }'</code>
	 * </p> for this macro.
	 * Without this information the compiler would consider <code>'p faca'</code> as a unary message send.
	 * This is because probably the macro would call method <code>'expr()'</code> of the compiler to parse the expression
	 * after <code>'enquanto'</code>. This method does not know the macro. It cannot deduce that <code>'faca'</code> is
	 * part of the macro. But with method <code>'getKeywords'</code> the macro would register with the
	 * lexer that both <code>'enquanto'</code> and <code>'faca'</code> are now temporary keywords. They are keywords inside
	 * this macro call. Now method <code>expr()</code> would stop its analysis when it finds <code>'faca'</code>.
	 *
	 * As another example, suppose there is a macro <code>'each'</code>
	 * for playing the same rule as the 'for' of Cyan:</p>
	 * <code>
	 *          var intArray = [ 0, 1, 2 ]; </p>
	 *              // print the array elements </p>
	 *          each s inside intArray { </p>
	 *              s print </p>
	 *          } </p>
	 *</code> </p>
	 * Here <code>'s'</code> is a variable declared by the macro and <code>'each'</code> and <code>'inside'</code> are keywords the macro uses.
	 * Method {@link #getMacroKeywords()} would return </p>
	 * <code>'new String[] { "each", "inside" }'</code>.
	 *
	 *
	   @return
	 */
	public final String []getMacroKeywords() {
		return this.macroKeywords;
	}


	@Override
	public boolean isExpression() {
		return false;
	}

	@Override
	public String getName() {
		String name = "macro(";
		for ( String kw : getMacroKeywords() )
			name += kw;
		name += ")";
		return name;
	}

	@Override
	public String getPackageOfType() { return "cyan.lang"; }
	/**
	 * If the metaobject annotation has type <code>packageName.prototypeName</code>, this method returns
	 * <code>prototypeName</code>.  See {@link CyanMetaobjectLiteralObject#getPackageOfType()}
	   @return
	 */

	@Override
	public String getPrototypeOfType() { return "Nil"; }

	/**
	 *
	 * This method should be called by a IDE plugin to show the text associated to the metaobject annotation
	 * <code>annotation</code> in several colors (text highlighting). Each element of the tuple is composed of<br>
	 * <code>
	 *      (color, offset, size)
	 * </code><br>
	 * <code>color</code> is the color number, <code>offset</code> is the number of characters
	 * from the beginning of the text, that starts at offset 0, <code>size</code> is the
	 * number of characters that should have color <code>color</code>.
	 *
	 *  <code>annotation</code> is redundant nowadays because this class already has a field
	 *  with the same contents.
	   @return
	 */
	@SuppressWarnings("unused")
	public List<Tuple4<Integer, Integer, Integer, Integer>> getColorTokenList(
			WrAnnotationMacroCall macroCall) {
		return null;
	}


	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		return null;
	}


	@Override
	public void parsing_parseMacro(ICompilerMacro_parsing compiler_parsing) {
	}



	@Override
	public WrAnnotationMacroCall getAnnotation() {
		return (WrAnnotationMacroCall ) annotation;
	}

	protected String []startKeywords;
	protected String []macroKeywords;
}
