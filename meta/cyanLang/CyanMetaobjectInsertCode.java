package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afti;
import meta.IAction_dsa;
import meta.ICompiler_afti;
import meta.ICompiler_dpa;
import meta.ICompiler_dsa;
import meta.IParseWithCyanCompiler_dpa;
import meta.ISlotInterface;
import meta.InterpretationErrorException;
import meta.Token;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrEvalEnv;
import meta.WrStatement;
import meta.WrSymbol;

/**
 *
 * <code>
package main

object Program

    {@literal @}insertCode{*
        var Int n = 0;
        for elem in [ "red", "green", "blue" ] {
            var String s = "    func " ++ elem ++ " -> Int; ";
            insert: s, "    func " ++ elem ++ " -> Int = " ++ n ++ ";" ++ '\n';
            n = n + 1
        }
    *}


    func insertCodeTest {
        assert red == 0 && green == 1 && blue == 2;

        var Int fat12;
        {@literal @}insertCode{*
            var p = 2;
            for n in 3..12 {
                p = p*n
            }
            insert: "    fat12 = " ++ p ++ ";" ++ '\n';
        *}
        "The factorial of 12 is $fat12" println;

        {@literal @}insertCode{*
              // generate test cases
            for aa in 1..10 {
                let s = "    if Person(" ++ '\"'
                   ++ "name" ++ aa ++ '\"' ++ ", " ++ aa
                   ++ ") getName != "
                   ++ '\"' ++ "name" ++ aa ++ '\"' ++ " { "
                   ++ '\"' ++ "error in method getName of Person"
                   ++ '\"' ++ " println; }" ++ '\n';
                insert: s;
            }
        *}

    }

end
 *
 *
 * </code>
   @author jose
 */
public class CyanMetaobjectInsertCode extends CyanMetaobjectAtAnnot
    implements IAction_afti, IAction_dsa, IParseWithCyanCompiler_dpa {

	public CyanMetaobjectInsertCode() {
		super("insertCode", AnnotationArgumentsKind.ZeroParameters );
	}


	@Override
	public boolean shouldTakeText() { return true; }

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
			ICompiler_afti compiler_afti, List<Tuple2<WrAnnotation, List<ISlotInterface>>> infoList) {

		if ( statList.size() == 0 ) { return null; }
		if ( this.getMetaobjectAnnotation().getInsideMethod() ) {
			return null;
		}
		Tuple2<StringBuffer, String> t = evalCode( compiler_afti.getEnv());
		if ( t != null ) {
			if ( t.f1 == null || t.f2 == null ) {
				this.addError("Metaobject of annotation '" + this.getName() + "' was not able to evaluate the code");
				return null;
			}
		}
		return t;
	}


	/**
	   @param compiler_afti
	   @return
	 */
	private Tuple2<StringBuffer, String> evalCode(WrEnv env) {

		if ( statList.size() == 0 ) { return null; }

		WrStatement fs = statList.get(0);
		StringBuffer codeToAdd = new StringBuffer();
		StringBuffer strSlotList = new StringBuffer();
		Object selfObject = new Object() {
			@SuppressWarnings("unused")
			public void insert(String code) {
				codeToAdd.append(code);
			}
			@SuppressWarnings("unused")
			public void insert(String strSlot, String code) {
				strSlotList.append(strSlot);
				codeToAdd.append(code);
			}
			@SuppressWarnings("unused")
			public void insertCode(String code) {
				codeToAdd.append(code);
			}
			@SuppressWarnings("unused")
			public void insertCode(String strSlot, String code) {
				strSlotList.append(strSlot);
				codeToAdd.append(code);
			}
		};
		WrEvalEnv ee;

		ee = new WrEvalEnv( env, selfObject, fs.getFirstSymbol());
		ee.addVariable("metaobject", this);
		ee.addVariable("env", env);

		for ( WrStatement is : statList ) {
			try {
				is.eval(ee);
			}
			catch ( InterpretationErrorException e ) {
				this.addError(is.getFirstSymbol(), e.getMessage());
				return null;
			}
			catch ( Throwable e ) {
				addError(is.getFirstSymbol(), "Exception '" + e.getClass().getCanonicalName() + "' was thrown and not caught");
				return null;
			}
		}

		return new Tuple2<StringBuffer, String>(codeToAdd, strSlotList.toString());
	}


	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		if ( statList.size() == 0 ) { return null; }

		if ( this.getMetaobjectAnnotation().getInsideMethod() ) {
			Tuple2<StringBuffer, String> t = evalCode(compiler_dsa.getEnv());
			if ( t != null ) {
				return t.f1;
			}
		}
		return null;
	}

	private List<WrStatement> statList;


	//private StringBuffer codeToAdd;
}
