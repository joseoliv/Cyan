package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.CyanMetaobjectLiteralObjectSeq;
import meta.ICompiler_dpa;
import meta.ICompiler_dsa;
import meta.IParseWithCyanCompiler_dpa;
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
public class CyanMetaobjectLiteralObjectSeqSet extends CyanMetaobjectLiteralObjectSeq implements IParseWithCyanCompiler_dpa {

	public CyanMetaobjectLiteralObjectSeqSet() {
		super("[*");
	}

	@Override
	public void dpa_parse(ICompiler_dpa compiler_dpa) {
		/**
		 * elements of the set
		 */
		// // List<WrExpr> exprList = new ArrayList<>();
		exprList = new ArrayList<>();

		while ( compiler_dpa.symbolCanStartExpr(compiler_dpa.getSymbol()) ) {
			exprList.add(compiler_dpa.expr());
			if ( compiler_dpa.getSymbol().token == Token.COMMA) {
				compiler_dpa.next();
			}
			else {
				// not ',', the list should have ended
				break;
			}
		}
		if ( exprList.size() == 0 ) {
			compiler_dpa.error(compiler_dpa.getSymbol(), "A literal set should have at least one element");
			return ;
		}
		final WrSymbol sy = compiler_dpa.getSymbol();
/*		if ( sy.token == Token.EOLO ) {
			// // this.getMetaobjectAnnotation().setInfo_dpa(exprList);
		}
		else {
			compiler_dpa.error(compiler_dpa.getSymbol(), "Syntax error in literal set");
		}
*/
		if ( sy.token != Token.EOLO ) {
			compiler_dpa.error(compiler_dpa.getSymbol(), "Syntax error in literal set");
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
	   @param compiler_dsa
	   @return
	 */
	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {
		// // List<WrExpr> exprList;
		// // exprList = (List<WrExpr> ) getMetaobjectAnnotation().getInfo_dpa();
		final StringBuffer s = new StringBuffer();
		s.append(" ( { var ");
		final String varName = compiler_dsa.getEnv().getNewUniqueVariableName();
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
