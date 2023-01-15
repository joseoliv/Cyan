package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_parsing;
import meta.IParseWithCyanCompiler_parsing;
import meta.ISlotSignature;
import meta.Token;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrEvalEnv;
import meta.WrStatement;
import meta.WrSymbol;

/**
 * Demonstration metaobject. It takes Cyan statements as DSL code and interprets them
 * during phase AFTER_RES_TYPES (3). No code is inserted in the prototype. If this is needed,
 * use metaobject insertCode instead.
   @author jose
 */
public class CyanMetaobjectRun extends CyanMetaobjectAtAnnot
implements IAction_afterResTypes, IParseWithCyanCompiler_parsing {

	public CyanMetaobjectRun() {
		super("run", AnnotationArgumentsKind.ZeroParameters);
	}

	@Override
	public void parsing_parse(ICompiler_parsing compiler_parsing) {
		compiler_parsing.next();
		statList = new ArrayList<>();
		while ( compiler_parsing.getSymbol().token != Token.EOLO ) {
			WrStatement lastStat = compiler_parsing.statement();
			statList.add(lastStat);
			compiler_parsing.removeLastExprStat();
			if ( lastStat.demandSemicolon() ) {
				if ( compiler_parsing.getSymbol().token == Token.SEMICOLON ) {
					compiler_parsing.next();
				}
				else {
					WrSymbol sym = compiler_parsing.getSymbol();
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
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler_afterResTypes, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList)  {
		if ( statList.size() == 0 ) { return null; }
		final WrEnv env = compiler_afterResTypes.getEnv();
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
