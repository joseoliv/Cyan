package meta;

import java.util.List;

/**
 * represents a user-defined literal object. There are several kinds of literal objects
 * defined by users in Cyan: those delimited by a sequence of symbols such as
 *       [* (1, 2), (2, 3), (3, 1) *]
 *
 * literal object numbers such as 1011bin, literal strings such as r"a+", and
 * those that start by '@' followed by an identifier immediately followed by a sequence of symbols:
 *       @graph[* (1, 2), (2, 3), (3, 1) *]
 *
 * All but the last type are represented by subclasses of this class
 *
   @author José
 */

public class CyanMetaobjectLiteralObject extends CyanMetaobject
                      implements  IAction_semAn {


	public CyanMetaobjectLiteralObject() {
		super();
	}

	@Override
	public String getName() { return null; }

	/**
	 *
	 * This method should be called by a IDE plugin to show the text associated to the metaobject annotation
	 * <code>annotation</code> in several colors (text highlighting).

	 *
	 * Each tuple (color number, line number, column number, size). <br>
	 * The characters starting at line number, column number till column number
	 * + size - 1 should be highlighted in color "color number".
	 *  <code>annotation</code> is redundant nowadays because this class already has a field
	 *  with the same contents.
	 * @param metaobjectLiteralObjectAnnotation
	   @return
	 */
	public List<Tuple4<Integer, Integer, Integer, Integer>>
	getColorTokenList(WrAnnotationLiteralObject metaobjectLiteralObjectAnnotation) {
		return null;
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


	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		return null;
	}

	public String getUsefulString() {
		return ((meta.WrAnnotationLiteralObject ) this.annotation).getUsefulString();

	}

}
