package meta;

import ast.CompilationUnit;
import ast.CompilationUnitSuper;
import ast.Annotation;
import ast.AnnotationAt;
import ast.CyanPackage;
import ast.EvalEnv;
import ast.Expr;
import ast.ExprAnyLiteral;
import ast.FieldDec;
import ast.MessageWithKeywords;
import ast.MethodDec;
import ast.MethodKeywordWithParameters;
import ast.MethodSignature;
import ast.MethodSignatureOperator;
import ast.MethodSignatureUnary;
import ast.ParameterDec;
import ast.Program;
import ast.Prototype;
import ast.Statement;
import ast.StatementLocalVariableDec;
import ast.Type;
import ast.TypeDynamic;
import ast.TypeJavaClass;
import ast.TypeJavaRefArray;
import ast.TypeWithAnnotations;
import lexer.Symbol;
import saci.Env;

public class GetHiddenItem {

	static public CompilationUnit getHiddenCompilationUnit(WrCompilationUnit item) {
		if ( item == null ) {
			return null;
		}
		return (CompilationUnit ) item.hidden;
	}

	static public CompilationUnitSuper getHiddenCompilationUnitSuper(WrCompilationUnitSuper item) {
		if ( item == null ) {
			return null;
		}
		return item.hidden;
	}


    /*
    static public Ww getHiddenWw(IWw item) {
        return item.hidden;
    }
    */

    static public Prototype getHiddenPrototype(WrPrototype item) {
		if ( item == null ) {
			return null;
		}
        return (Prototype ) item.hidden;
    }

    static public Program getHiddenProgram(WrProgram item) {
		if ( item == null ) {
			return null;
		}
        return item.hidden;
    }

    static public ParameterDec getHiddenParameterDec(WrParameterDec item) {
		if ( item == null ) {
			return null;
		}
        return item.hidden;
    }

    static public MethodSignatureUnary getHiddenMethodSignatureUnary(WrMethodSignatureUnary item) {
		if ( item == null ) {
			return null;
		}
        return item.hidden;
    }

    static public MethodSignatureOperator getHiddenMethodSignatureOperator(WrMethodSignatureOperator item) {
		if ( item == null ) {
			return null;
		}
        return item.hidden;
    }

	public static MethodSignature getHiddenMethodSignature(
			WrMethodSignature ims) {
		if ( ims instanceof WrMethodSignatureOperator ) {
			return ((WrMethodSignatureOperator ) ims).hidden;
		}
		else if ( ims instanceof WrMethodSignatureUnary ) {
			return ((WrMethodSignatureUnary ) ims).hidden;
		}
		else if ( ims instanceof WrMethodSignatureWithKeywords ) {
			return ((WrMethodSignatureWithKeywords ) ims).hidden;
		}
		else {
			return null;
		}

	}


    static public MethodDec getHiddenMethodDec(WrMethodDec item) {
		if ( item == null ) {
			return null;
		}
        return item.hidden;
    }

    static public MethodKeywordWithParameters getHiddenMessageKeywordWithParameters(WrMethodKeywordWithParameters item) {
		if ( item == null ) {
			return null;
		}
        return item.hidden;
    }

    static public CyanPackage getHiddenCyanPackage(WrCyanPackage item) {
		if ( item == null ) {
			return null;
		}
        return item.hidden;
    }

    static public FieldDec getHiddenFieldDec(WrFieldDec item) {
		if ( item == null ) {
			return null;
		}
        return item.hidden;
    }

    static public StatementLocalVariableDec getHiddenStatementLocalVariableDec(WrStatementLocalVariableDec item) {
		if ( item == null ) {
			return null;
		}
        return (StatementLocalVariableDec ) item.hidden;
    }

    static public TypeWithAnnotations getHiddenTypeWithAnnotations(WrTypeWithAnnotations item) {
		if ( item == null ) {
			return null;
		}
        return (TypeWithAnnotations ) item.hidden;
    }


    static public TypeDynamic getHiddenTypeDynamic(WrTypeDynamic item) {
		if ( item == null ) {
			return null;
		}
        return (TypeDynamic ) item.hidden;
    }

    static public TypeJavaClass getHiddenTypeJavaClass(WrTypeJavaClass item) {
		if ( item == null ) {
			return null;
		}
        return (TypeJavaClass ) item.hidden;
    }

    static public TypeJavaRefArray getHiddenTypeJavaRefArray(WrTypeJavaRefArray item) {
		if ( item == null ) {
			return null;
		}
        return (TypeJavaRefArray ) item.hidden;
    }

    static public Expr getHiddenExpr(WrExpr item) {
		if ( item == null ) {
			return null;
		}
        return (Expr ) item.hidden;
    }

    static public MessageWithKeywords getHiddenMessageWithKeywords(WrMessageWithKeywords item) {
		if ( item == null ) {
			return null;
		}
    	return (MessageWithKeywords ) item.hidden;
    }

    static public ExprAnyLiteral getHiddenExprAnyLiteral(WrExprAnyLiteral item) {
		if ( item == null ) {
			return null;
		}
    	return (ExprAnyLiteral ) item.hidden;
    }

    static public Env getHiddenEnv(WrEnv item) {
		if ( item == null ) {
			return null;
		}
    	return item.hidden;
    }


    static public EvalEnv getHiddenEvalEnv(WrEvalEnv item) {
		if ( item == null ) {
			return null;
		}
    	return item.hidden;
    }


    static public Type getHiddenType(WrType item) {
    	if ( item instanceof WrPrototype ) {
    		return GetHiddenItem.getHiddenPrototype( (WrPrototype ) item);
    	}
    	else if ( item instanceof WrTypeDynamic ) {
    		return GetHiddenItem.getHiddenTypeDynamic( (WrTypeDynamic) item );
    	}
    	else if ( item instanceof WrTypeJavaClass ) {
    		return GetHiddenItem.getHiddenTypeJavaClass( (WrTypeJavaClass) item );
    	}
    	else if ( item instanceof WrTypeJavaRefArray ) {
    		return GetHiddenItem.getHiddenTypeJavaRefArray( (WrTypeJavaRefArray ) item );
    	}
    	else if ( item instanceof WrTypeWithAnnotations ) {
    		return GetHiddenItem.getHiddenTypeWithAnnotations( (WrTypeWithAnnotations ) item );
    	}
    	else {
    		return null;
    	}
    }


	public static Statement getHiddenStatement(WrStatement item) {
		if ( item == null ) {
			return null;
		}
		return item.hidden;
	}

	public static Symbol getHiddenSymbol(WrSymbol item) {
		if ( item == null ) {
			return null;
		}
		return item.hidden;
	}


	public static Annotation getHiddenCyanAnnotation(
			WrAnnotation item) {
		if ( item == null ) {
			return null;
		}
		return (Annotation ) item.hidden;
	}

	public static AnnotationAt getHiddenCyanMetaobjectWithAtAnnotation(
			WrAnnotationAt item) {
		if ( item == null ) {
			return null;
		}
		return (AnnotationAt ) item.hidden;
	}
}
