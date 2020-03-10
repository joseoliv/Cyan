package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import meta.CyanMetaobjectMacro;
import meta.IActionNewPrototypes_afti;
import meta.IActionNewPrototypes_dpa;
import meta.ICommunicateInPrototype_afti_dsa_afsa;
import meta.ICompilerAction_dpa;
import meta.ICompilerMacro_dpa;
import meta.ICompiler_afti;
import meta.ICompiler_dsa;
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
       implements IActionNewPrototypes_afti, IActionNewPrototypes_dpa, /* IActionNewPrototypes_dsa */
       // ICheckProgramUnit_afti3, ICheckProgramUnit_afsa,
       ICommunicateInPrototype_afti_dsa_afsa {

	public CyanMetaobjectMacroTestMacro() {
		super(new String[] { "enquanto" }, new String[] { "enquanto", "faca", "inicio", "fim" });
	}



	@Override
	public void dpa_parseMacro(ICompilerMacro_dpa compiler_dpa) {

		offsetStartLine = compiler_dpa.getSymbol().getColumnNumber();

		compiler_dpa.next();
		expr = compiler_dpa.expr();

		if ( compiler_dpa.getSymbol().token != Token.MACRO_KEYWORD || ! compiler_dpa.getSymbol().getSymbolString().equals("faca") ) {
			compiler_dpa.error(compiler_dpa.getSymbol(), "'faca' expected");
			return ;
		}
		else
			compiler_dpa.next();
		if ( compiler_dpa.getSymbol().token != Token.MACRO_KEYWORD || ! compiler_dpa.getSymbol().getSymbolString().equals("inicio") ) {
			compiler_dpa.error(compiler_dpa.getSymbol(), "'inicio' expected");
			return ;
		}
		else
			compiler_dpa.next();
		statList = new ArrayList<>();

		while (  compiler_dpa.getSymbol().token != Token.MACRO_KEYWORD || ! compiler_dpa.getSymbol().getSymbolString().equals("fim")  ) {
			WrStatement stat = compiler_dpa.statement();
			statList.add(stat);
			if ( stat.demandSemicolon() ) {
				if ( compiler_dpa.getSymbol().token == Token.SEMICOLON ) {
					compiler_dpa.next();
				}
				else {
					WrSymbol sym = compiler_dpa.getSymbol();
					if ( sym.token == Token.EOF || sym.token == Token.EOLO ) {
						sym = stat.getFirstSymbol();
					}

					compiler_dpa.error(sym, "';' expected");
					return ;
				}
			}


		}
		compiler_dpa.next();


		if ( compiler_dpa.getThereWasErrors() )
			return ;

		// // ((AnnotationMacroCall ) this.getMetaobjectAnnotation()).setInfo_dpa( new Tuple2<Expr, Integer>(expr, offsetStartLine) );
		return ;
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		// // Tuple2<Expr, Integer> info = (Tuple2<Expr, Integer> ) ((AnnotationMacroCall ) this.getMetaobjectAnnotation()).getInfo_dpa();
		// // Expr expr = info.f1;
		WrEnv env = compiler_dsa.getEnv();
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
	public List<Tuple2<String, StringBuffer>> dpa_NewPrototypeList(ICompilerAction_dpa compiler) {
		//System.out.println("dpa_NewPrototypeList(ICompilerAction_dpa compiler)");
		return null;
	}


	@Override
	public List<Tuple2<String, StringBuffer>> afti_NewPrototypeList(ICompiler_afti compiler_afti) {
		//System.out.println("afti_NewPrototypeList(ICompiler_afti compiler_afti) ");
		return null;
	}


	@Override
	public Object afti_dsa_afsa_shareInfoPrototype(WrEnv env) {
		//System.out.println("afti_dsa_afsa_shareInfoPrototype()");
		return null;
	}

	@Override
	public void afti_dsa_afsa_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet, WrEnv env) {
		//System.out.println("afti_dsa_afsa_receiveInfoPrototype");
	}

	private WrExpr expr;
	private List<WrStatement> statList;
	private int offsetStartLine;
}
