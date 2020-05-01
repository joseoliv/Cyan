package meta.util;

import java.util.ArrayList;
import java.util.List;
import meta.CyanMetaobjectLiteralObjectSeq;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IParseWithCyanCompiler_parsing;
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
       implements IParseWithCyanCompiler_parsing {

	public CyanMetaobjectLiteralObjectSeqGraph() {
		super("[%");
	}

	@Override
	public void parsing_parse(ICompiler_parsing compiler_parsing) {


		/**
		 * each edge of the graph is represented by two numbers in exprList
		 */
		exprList = new ArrayList<>();


		while ( compiler_parsing.symbolCanStartExpr(compiler_parsing.getSymbol()) ) {
			exprList.add(compiler_parsing.expr());
			if ( compiler_parsing.getSymbol().token != Token.COLON ) {
				compiler_parsing.error(compiler_parsing.getSymbol(), "':' was expected");
				return ;
			}
			else {
				compiler_parsing.next();
				exprList.add(compiler_parsing.expr());
				if ( compiler_parsing.getSymbol().token == Token.COMMA) {
					compiler_parsing.next();
				}
				else {
					// not ',', the list of pair should have ended
					break;
				}

			}
		}
		if ( compiler_parsing.getSymbol().token != Token.EOLO ) {
			compiler_parsing.error(compiler_parsing.getSymbol(), "Syntax error in literal graph");
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
	   @param compiler_semAn
	   @return
	 */
	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		// // List<Expr> exprList;
		// // exprList = (List<Expr> ) getAnnotation().getInfo_parsing();

		StringBuffer s = new StringBuffer();
		s.append(" ({ var cyan.util.Graph ");
		String varName = compiler_semAn.getEnv().getNewUniqueVariableName();
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
