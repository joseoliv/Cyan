package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import meta.CyanMetaobjectMacro;
import meta.IActionNewPrototypes_afterResTypes;
import meta.IActionNewPrototypes_parsing;
import meta.ICommunicateInPrototype_afterResTypes_semAn_afterSemAn;
import meta.ICompilerAction_parsing;
import meta.ICompilerMacro_parsing;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_semAn;
import meta.Token;
import meta.Tuple2;
import meta.Tuple4;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrStatement;
import meta.WrSymbol;

/**
 * a macro for testing. Ignore it.
 *
 * enquanto expr faca
 *    inicio
 *    statements
 *    fim
   @author jose
 */
public class CyanMetaobjectMacroTestMacro extends CyanMetaobjectMacro
       implements IActionNewPrototypes_afterResTypes, IActionNewPrototypes_parsing, /* IActionNewPrototypes_semAn */
       // ICheckPrototype_afterResTypes3, ICheckPrototype_afterSemAn,
       ICommunicateInPrototype_afterResTypes_semAn_afterSemAn {

	public CyanMetaobjectMacroTestMacro() {
		super(new String[] { "enquanto" }, new String[] { "enquanto", "faca", "inicio", "fim" });
	}



	@Override
	public void parsing_parseMacro(ICompilerMacro_parsing compiler_parsing) {

		offsetStartLine = compiler_parsing.getSymbol().getColumnNumber();

		compiler_parsing.next();
		expr = compiler_parsing.expr();

		if ( compiler_parsing.getSymbol().token != Token.MACRO_KEYWORD || ! compiler_parsing.getSymbol().getSymbolString().equals("faca") ) {
			compiler_parsing.error(compiler_parsing.getSymbol(), "'faca' expected");
			return ;
		}
		else
			compiler_parsing.next();
		if ( compiler_parsing.getSymbol().token != Token.MACRO_KEYWORD || ! compiler_parsing.getSymbol().getSymbolString().equals("inicio") ) {
			compiler_parsing.error(compiler_parsing.getSymbol(), "'inicio' expected");
			return ;
		}
		else
			compiler_parsing.next();
		statList = new ArrayList<>();

		while (  compiler_parsing.getSymbol().token != Token.MACRO_KEYWORD || ! compiler_parsing.getSymbol().getSymbolString().equals("fim")  ) {
			WrStatement stat = compiler_parsing.statement();
			statList.add(stat);
			if ( stat.demandSemicolon() ) {
				if ( compiler_parsing.getSymbol().token == Token.SEMICOLON ) {
					compiler_parsing.next();
				}
				else {
					WrSymbol sym = compiler_parsing.getSymbol();
					if ( sym.token == Token.EOF || sym.token == Token.EOLO ) {
						sym = stat.getFirstSymbol();
					}

					compiler_parsing.error(sym, "';' expected");
					return ;
				}
			}


		}
		compiler_parsing.next();


		if ( compiler_parsing.getThereWasErrors() )
			return ;

		// // ((AnnotationMacroCall ) this.getAnnotation()).setInfo_parsing( new Tuple2<Expr, Integer>(expr, offsetStartLine) );
		return ;
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		// // Tuple2<Expr, Integer> info = (Tuple2<Expr, Integer> ) ((AnnotationMacroCall ) this.getAnnotation()).getInfo_parsing();
		// // Expr expr = info.f1;
		WrEnv env = compiler_semAn.getEnv();
		if ( env.isThereWasError() )
			return null;

		// // int offsetStartLine = info.f2;
		StringBuffer s = new StringBuffer();
		if ( offsetStartLine > CyanMetaobjectMacro.sizeWhiteSpace )
			offsetStartLine = CyanMetaobjectMacro.sizeWhiteSpace;
		String identSpace = CyanMetaobjectMacro.whiteSpace.substring(0, offsetStartLine);
		String strExpr = expr.asString();
		s.append(identSpace + "while " + strExpr + " { \r\n");
		for ( WrStatement stat : statList ) {
			s.append(identSpace + "    " + stat.asString() + ";\r\n");
		}
		s.append(identSpace + "}\r\n");
		return s;
	}


	@Override
	public List<Tuple2<String, StringBuffer>> parsing_NewPrototypeList(ICompilerAction_parsing compiler) {
		//System.out.println("parsing_NewPrototypeList(ICompilerAction_parsing compiler)");
		return null;
	}


	@Override
	public List<Tuple2<String, StringBuffer>> afterResTypes_NewPrototypeList(ICompiler_afterResTypes compiler_afterResTypes) {
		//System.out.println("afterResTypes_NewPrototypeList(ICompiler_afterResTypes compiler_afterResTypes) ");
		return null;
	}


	@Override
	public Object afterResTypes_semAn_afterSemAn_shareInfoPrototype(WrEnv env) {
		//System.out.println("afterResTypes_semAn_afterSemAn_shareInfoPrototype()");
		return null;
	}

	@Override
	public void afterResTypes_semAn_afterSemAn_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet, WrEnv env) {
		//System.out.println("afterResTypes_semAn_afterSemAn_receiveInfoPrototype");
	}

	private WrExpr expr;
	private List<WrStatement> statList;
	private int offsetStartLine;
}
