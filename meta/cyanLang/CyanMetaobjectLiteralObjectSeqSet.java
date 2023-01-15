package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.CyanMetaobjectLiteralObjectSeq;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IParseWithCyanCompiler_parsing;
import meta.Token;
import meta.WrExpr;
import meta.WrSymbol;

/**
 * A literal Set. The set elements are delimited by <code>[*</code> and <code>*]</code> as in<br>
 * <code>
 *     let set = [* 5, 3, 4, 8 *];
 * </code>
   @author jose
 */
public class CyanMetaobjectLiteralObjectSeqSet extends CyanMetaobjectLiteralObjectSeq implements IParseWithCyanCompiler_parsing {

	public CyanMetaobjectLiteralObjectSeqSet() {
		super("[*");
	}

	@Override
	public void parsing_parse(ICompiler_parsing compiler_parsing) {
		/**
		 * elements of the set
		 */
		// // List<WrExpr> exprList = new ArrayList<>();
		exprList = new ArrayList<>();

		while ( compiler_parsing.symbolCanStartExpr(compiler_parsing.getSymbol()) ) {
			exprList.add(compiler_parsing.expr());
			if ( compiler_parsing.getSymbol().token == Token.COMMA) {
				compiler_parsing.next();
			}
			else {
				// not ',', the list should have ended
				break;
			}
		}
		if ( exprList.size() == 0 ) {
			compiler_parsing.error(compiler_parsing.getSymbol(), "A literal set should have at least one element");
			return ;
		}
		final WrSymbol sy = compiler_parsing.getSymbol();
/*		if ( sy.token == Token.EOLO ) {
			// // this.getAnnotation().setInfo_parsing(exprList);
		}
		else {
			compiler_parsing.error(compiler_parsing.getSymbol(), "Syntax error in literal set");
		}
*/
		if ( sy.token != Token.EOLO ) {
			compiler_parsing.error(compiler_parsing.getSymbol(), "Syntax error in literal set");
		}

		return ;
	}

	/*
	 *
	 *
	 */


	/**
	 * produces something like</p>
	 * <code> </p>
	 * ({ var1 = Set{@literal <}Int> new; </p>
	 *    var1 add: 1; </p>
	 *    var1 add: 2; </p>
	 *    var1 add: 3; </p>
	 *  } eval)</p>
	 * </code></p>
	 * for <code>"[* 1, 2, 3 *]"</code>
	   @param compiler_semAn
	   @return
	 */
	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		// // List<WrExpr> exprList;
		// // exprList = (List<WrExpr> ) getAnnotation().getInfo_parsing();
		final StringBuffer s = new StringBuffer();
		s.append(" ( { var ");
		final String varName = compiler_semAn.getEnv().getNewUniqueVariableName();
		s.append( varName + " = " + this.getPackageOfType() + "." + getPrototypeOfType() + " new;\n");
		for ( int i = 0; i < exprList.size(); ++i) {
			s.append(varName + " add: " + exprList.get(i).asString() + ";\n");
		}
		s.append("^" + varName + " } eval)");

		return s;
	}

	@Override
	public String getPackageOfType() {
		return "cyan.lang";
	}

	@Override
	public String getPrototypeOfType() {

		final String ret = "Set<" + exprList.get(0).getType().getFullName() + ">";
		return ret;
	}


	private List<WrExpr> exprList;

}
