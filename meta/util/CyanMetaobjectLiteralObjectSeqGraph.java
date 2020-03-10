package meta.util;

import java.util.ArrayList;
import java.util.List;
import meta.CyanMetaobjectLiteralObjectSeq;
import meta.ICompiler_dpa;
import meta.ICompiler_dsa;
import meta.IParseWithCyanCompiler_dpa;
import meta.Token;
import meta.WrExpr;

/**
 * a graph between <code>[%</code> and <code>%]</code>. An example is
 * </p>
 * <code>
 * var g = [% 1:2, 2:3, 3:1 %];</p>
 * </code> </p>
 * The numbers should be Int literals
   @author José
 */
public class CyanMetaobjectLiteralObjectSeqGraph extends CyanMetaobjectLiteralObjectSeq
       implements IParseWithCyanCompiler_dpa {

	public CyanMetaobjectLiteralObjectSeqGraph() {
		super("[%");
	}

	@Override
	public void dpa_parse(ICompiler_dpa compiler_dpa) {


		/**
		 * each edge of the graph is represented by two numbers in exprList
		 */
		exprList = new ArrayList<>();


		while ( compiler_dpa.symbolCanStartExpr(compiler_dpa.getSymbol()) ) {
			exprList.add(compiler_dpa.expr());
			if ( compiler_dpa.getSymbol().token != Token.COLON ) {
				compiler_dpa.error(compiler_dpa.getSymbol(), "':' was expected");
				return ;
			}
			else {
				compiler_dpa.next();
				exprList.add(compiler_dpa.expr());
				if ( compiler_dpa.getSymbol().token == Token.COMMA) {
					compiler_dpa.next();
				}
				else {
					// not ',', the list of pair should have ended
					break;
				}

			}
		}
		if ( compiler_dpa.getSymbol().token != Token.EOLO ) {
			compiler_dpa.error(compiler_dpa.getSymbol(), "Syntax error in literal graph");
		}
	}

	/**
	 * produces something like</p>
	 * <code> </p>
	 * ({ var1 = Graph new; </p>
	 *    var1 add: 1, 2; </p>
	 *    var1 add: 2, 3; </p>
	 *  } eval)</p>
	 * </code></p>
	 * for <code>"[% 1:2, 2:3 %]"</code>
	   @param compiler_dsa
	   @return
	 */
	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		// // List<Expr> exprList;
		// // exprList = (List<Expr> ) getMetaobjectAnnotation().getInfo_dpa();

		StringBuffer s = new StringBuffer();
		s.append(" ({ var cyan.util.Graph ");
		String varName = compiler_dsa.getEnv().getNewUniqueVariableName();
		s.append( varName + " = Graph new;\n");
		for ( int i = 0; i < exprList.size(); i+=2) {
			s.append(varName + " addNumberEdge:" + exprList.get(i).asString() + ", " + exprList.get(i+1).asString() + ";\n");
		}
		s.append("^" + varName + " } eval)");
		return s;
	}


	@Override
	public String getPackageOfType() {
		return "cyan.util";
	}

	@Override
	public String getPrototypeOfType() {
		return "Graph";
	}


	/**
	 * each edge of the graph is represented by two numbers in exprList
	 */
	private List<WrExpr> exprList;
}
