package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afti;
import meta.ICompiler_afti;
import meta.ICompiler_dpa;
import meta.IParseWithCyanCompiler_dpa;
import meta.ISlotInterface;
import meta.Token;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrEvalEnv;
import meta.WrStatement;
import meta.WrSymbol;

/**
 * Demonstration metaobject. It takes Cyan statements as DSL code and interprets them
 * during phase afti (3). No code is inserted in the prototype. If this is needed,
 * use metaobject insertCode instead.
   @author jose
 */
public class CyanMetaobjectRun extends CyanMetaobjectAtAnnot
implements IAction_afti, IParseWithCyanCompiler_dpa {

	public CyanMetaobjectRun() {
		super("run", AnnotationArgumentsKind.ZeroParameters);
	}

	@Override
	public void dpa_parse(ICompiler_dpa compiler_dpa) {
		compiler_dpa.next();
		statList = new ArrayList<>();
		while ( compiler_dpa.getSymbol().token != Token.EOLO ) {
			WrStatement lastStat = compiler_dpa.statement();
			statList.add(lastStat);
			compiler_dpa.removeLastExprStat();
			if ( lastStat.demandSemicolon() ) {
				if ( compiler_dpa.getSymbol().token == Token.SEMICOLON ) {
					compiler_dpa.next();
				}
				else {
					WrSymbol sym = compiler_dpa.getSymbol();
					if ( sym.token == Token.EOF || sym.token == Token.EOLO ) {
						sym = lastStat.getFirstSymbol();
					}
					throw new CompileErrorException("Error in line " + sym.getLineNumber()
							+ "(" + sym.getColumnNumber() + "): ';' expected");
				}
			}

		}

	}

	@Override
	public Tuple2<StringBuffer, String> afti_codeToAdd(
			ICompiler_afti compiler_afti, List<Tuple2<WrAnnotation, List<ISlotInterface>>> infoList)  {
		if ( statList.size() == 0 ) { return null; }
		final WrEnv env = compiler_afti.getEnv();
		final WrStatement fs = statList.get(0);
		final Object selfObject = new Object() { };

		WrEvalEnv ee;

		ee = new WrEvalEnv( env, selfObject, fs.getFirstSymbol());
		for ( final WrStatement is : statList ) {
			try {
				is.eval(ee);
			}
			catch ( meta.InterpretationErrorException e ) {
				WrSymbol sym = e.getSymbol();
				this.addError(sym, e.getMessage());
			}
			catch ( final Throwable e ) {
				WrSymbol sym = is.getFirstSymbol();
				this.addError("Error in statement of line " + sym.getLineNumber() + "(" + sym.getColumnNumber() + "): " +
						   "Exception '" + e.getClass().getCanonicalName() + "' was thrown and not caught. Its message is '"
						   + e.getMessage() + "'.");
			}
		}
		return null;
	}

	@Override
	public boolean shouldTakeText() { return true; }

	private List<WrStatement> statList;

}
