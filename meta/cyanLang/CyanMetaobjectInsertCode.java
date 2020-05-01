package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.IAction_semAn;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IParseWithCyanCompiler_parsing;
import meta.ISlotSignature;
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
    implements IAction_afterResTypes, IAction_semAn, IParseWithCyanCompiler_parsing {

	public CyanMetaobjectInsertCode() {
		super("insertCode", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind [] { AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.METHOD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC,
						AttachedDeclarationKind.NONE_DEC});
	}


	@Override
	public boolean shouldTakeText() { return true; }

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
					this.addError(sym, "Error in line " + sym.getLineNumber()
							+ "(" + sym.getColumnNumber() + "): ';' expected");
				}
			}

		}



	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler,
			List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {

		if ( statList.size() == 0 ) { return null; }
		if ( this.getAnnotation().getInsideMethod() ) {
			return null;
		}
		Tuple2<StringBuffer, String> t = evalCode( compiler.getEnv(), compiler, infoList );
		if ( t != null ) {
			if ( t.f1 == null || t.f2 == null ) {
				this.addError("Metaobject of annotation '" + this.getName() + "' was not able to evaluate the code");
				return null;
			}
		}
		return t;
	}


	/**
	   @param compiler_afterResTypes
	   @return
	 */
	private Tuple2<StringBuffer, String> evalCode(WrEnv env, ICompiler_afterResTypes compiler,
			List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList ) {

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
		if ( compiler != null ) {
			ee.addVariable("compiler", compiler);
		}
		if ( infoList != null ) {
			ee.addVariable("infoList", infoList);
		}

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
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		if ( statList.size() == 0 ) { return null; }

		if ( this.getAnnotation().getInsideMethod() ) {
			Tuple2<StringBuffer, String> t = evalCode(compiler_semAn.getEnv(), null, null );
			if ( t != null ) {
				return t.f1;
			}
		}
		return null;
	}

	private List<WrStatement> statList;


	//private StringBuffer codeToAdd;
}
