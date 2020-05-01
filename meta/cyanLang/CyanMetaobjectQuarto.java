package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.IVariableDecInterface;
import meta.WrASTNode;
import meta.WrASTVisitor;
import meta.WrCaseRecord;
import meta.WrCastRecord;
import meta.WrCompilationUnit;
import meta.WrContextParameter;
import meta.WrAnnotation;
import meta.WrAnnotationLiteralObject;
import meta.WrAnnotationMacroCall;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprAnyLiteral;
import meta.WrExprAnyLiteralIdent;
import meta.WrExprBooleanAnd;
import meta.WrExprBooleanOr;
import meta.WrExprFunction;
import meta.WrExprFunctionRegular;
import meta.WrExprFunctionWithKeywords;
import meta.WrExprGenericPrototypeInstantiation;
import meta.WrExprIdentStar;
import meta.WrExprIndexed;
import meta.WrExprJavaArrayType;
import meta.WrExprLiteral;
import meta.WrExprLiteralArray;
import meta.WrExprLiteralBoolean;
import meta.WrExprLiteralChar;
import meta.WrExprLiteralMap;
import meta.WrExprLiteralNil;
import meta.WrExprLiteralNumber;
import meta.WrExprLiteralString;
import meta.WrExprLiteralTuple;
import meta.WrExprMessageSend;
import meta.WrExprMessageSendUnaryChainToExpr;
import meta.WrExprMessageSendUnaryChainToSuper;
import meta.WrExprMessageSendWithKeywordsToExpr;
import meta.WrExprMessageSendWithKeywordsToSuper;
import meta.WrExprObjectCreation;
import meta.WrExprSelf;
import meta.WrExprSelfPeriodIdent;
import meta.WrExprSurroundedByContext;
import meta.WrExprTypeof;
import meta.WrExprUnary;
import meta.WrExprWithParenthesis;
import meta.WrFieldDec;
import meta.WrGenericParameter;
import meta.WrJVMPackage;
import meta.WrMessageBinaryOperator;
import meta.WrMessageKeywordWithRealParameters;
import meta.WrMessageWithKeywords;
import meta.WrMethodDec;
import meta.WrMethodKeywordWithParameters;
import meta.WrMethodSignature;
import meta.WrMethodSignatureOperator;
import meta.WrMethodSignatureUnary;
import meta.WrMethodSignatureWithKeywords;
import meta.WrParameterDec;
import meta.WrPrototype;
import meta.WrSlotDec;
import meta.WrStatement;
import meta.WrStatementAssignmentList;
import meta.WrStatementBreak;
import meta.WrStatementCast;
import meta.WrStatementFor;
import meta.WrStatementIf;
import meta.WrStatementList;
import meta.WrStatementLocalVariableDec;
import meta.WrStatementLocalVariableDecList;
import meta.WrStatementAnnotation;
import meta.WrStatementMinusMinusIdent;
import meta.WrStatementNull;
import meta.WrStatementPlusPlusIdent;
import meta.WrStatementRepeat;
import meta.WrStatementReturn;
import meta.WrStatementReturnFunction;
import meta.WrStatementType;
import meta.WrStatementWhile;

public class CyanMetaobjectQuarto extends CyanMetaobjectAtAnnot
implements ICheckDeclaration_afterSemAn {


	public CyanMetaobjectQuarto() {
		super("quarto", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.PROTOTYPE_DEC
		});

	}

	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {
		IDeclaration dec = this.getAttachedDeclaration();
		WrPrototype pu = (WrPrototype ) dec;
		final CyanMetaobjectAtAnnot metaobject = this;
		WrASTVisitorAll visitor = new WrASTVisitorAll();
		pu.accept( visitor, compiler.getEnv() );
		System.out.println("End of AFTER_SEM_AN quarto");
	}

}



class WrASTVisitorAll extends WrASTVisitor {

	public int countIf = 0;
	public int countWhile = 0;
	public int countRepeat = 0;
	private int countFor = 0;

	public WrASTVisitorAll() { }

	@Override
	public void visit(WrJVMPackage node, WrEnv env) {
		System.out.println("WrJVMPackage " + node.getPackageName() + " ");
	}
	@Override
	public void visit(WrCompilationUnit node, WrEnv env) {
		System.out.println("WrCompilationUnit " + node.getFilename() + " ");
	}
	@Override
	public void visit(WrPrototype node, WrEnv env) {	}
	@Override
	public void visit(WrMethodDec node, WrEnv env) { }
	@Override
	public void visit(WrMethodSignature node, WrEnv env) {
		if ( node instanceof WrMethodSignatureWithKeywords ) {
			this.visit( (WrMethodSignatureWithKeywords ) node, env );
		}
		else if ( node instanceof WrMethodSignatureOperator ) {
			this.visit( (WrMethodSignatureOperator ) node, env );
		}
		else if ( node instanceof WrMethodSignatureUnary ) {
			this.visit( (WrMethodSignatureUnary ) node, env );
		}
		else {
			super.signalError(node);
		}
	}
	@Override
	public void visit(WrMethodSignatureWithKeywords node, WrEnv env) { }
	@Override
	public void visit(WrMethodSignatureOperator node, WrEnv env) { }
	@Override
	public void visit(WrMethodSignatureUnary node, WrEnv env) { }


	// public void preVisit(WrProgram node, WrEnv env) { }
	// public void preVisit(WrCyanPackage node, WrEnv env) { }
	@Override
	public void preVisit(WrCompilationUnit node, WrEnv env) { }
	@Override
	public void preVisit(WrPrototype node, WrEnv env) { }

	@Override
	public void preVisit(WrMethodDec node, WrEnv env) { }
	@Override
	public void preVisit(WrMethodSignature node, WrEnv env) {
		if ( node instanceof WrMethodSignatureWithKeywords ) {
			this.preVisit( (WrMethodSignatureWithKeywords ) node, env );
		}
		else if ( node instanceof WrMethodSignatureOperator ) {
			this.preVisit( (WrMethodSignatureOperator ) node, env );
		}
		else if ( node instanceof WrMethodSignatureUnary ) {
			this.preVisit( (WrMethodSignatureUnary ) node, env );
		}
		else {
			signalError(node);
		}
	}

	@Override
	public void preVisit(WrMethodSignatureWithKeywords node, WrEnv env) { }
	@Override
	public void preVisit(WrMethodSignatureOperator node, WrEnv env) { }
	@Override
	public void preVisit(WrMethodSignatureUnary node, WrEnv env) { }
	@Override
	public void preVisit(WrExprFunctionRegular node, WrEnv env) { }
	@Override
	public void preVisit(WrExprFunctionWithKeywords node, WrEnv env) { }


	@Override
	public void visit(WrSlotDec node, WrEnv env) {
		if ( node instanceof WrFieldDec ) {
			if ( node instanceof WrContextParameter ) {
				this.visit( (WrContextParameter ) node, env );
			}
			else {
				this.visit( (WrFieldDec ) node, env );
			}
		}
		else if ( node instanceof WrMethodDec ) {
			this.visit( (WrMethodDec ) node, env );
		}
		else {
			signalError(node);
		}
	}

	@Override
	public void visit(WrContextParameter node, WrEnv env) { }
	@Override
	public void visit(WrFieldDec node, WrEnv env) { }
	@Override
	public void visit(WrAnnotation node, WrEnv env) {
		if ( node instanceof WrAnnotationLiteralObject ) {
			this.visit( (WrAnnotationLiteralObject ) node, env);
		}
		else if ( node instanceof WrAnnotationMacroCall ) {
			this.visit( (WrAnnotationMacroCall )  node, env );
		}
		else if ( node instanceof WrAnnotationAt ) {
			this.visit( (WrAnnotationAt ) node, env );
		}
		else {
			signalError(node);
		}
	}
	@Override
	public void visit(WrAnnotationLiteralObject node, WrEnv env) { }
	@Override
	public void visit(WrAnnotationMacroCall node, WrEnv env) { }
	@Override
	public void visit(WrAnnotationAt node, WrEnv env) { }


	@Override
	public void visit(WrExpr node, WrEnv env) {

		if ( node instanceof WrAnnotationLiteralObject ) {
			this.visit( (WrAnnotationLiteralObject ) node, env);
		}
		else if ( node instanceof WrAnnotationMacroCall ) {
			this.visit( (WrAnnotationMacroCall )  node, env );
		}
		else if ( node instanceof WrAnnotationAt ) {
			this.visit( (WrAnnotationAt ) node, env );
		}
		else if ( node instanceof WrExprAnyLiteral ) {
			this.visit( (WrExprAnyLiteral ) node, env);
		}
		else if ( node instanceof WrExprBooleanAnd ) {
			this.visit( (WrExprBooleanAnd )  node, env );
		}
		else if ( node instanceof WrExprBooleanOr ) {
			this.visit( (WrExprBooleanOr ) node, env );
		}
		else if ( node instanceof WrExprFunctionRegular ) {
			this.visit( (WrExprFunctionRegular ) node, env);
		}
		else if ( node instanceof WrExprFunctionWithKeywords ) {
			this.visit( (WrExprFunctionWithKeywords )  node, env );
		}
		else if ( node instanceof WrExprGenericPrototypeInstantiation ) {
			this.visit( (WrExprGenericPrototypeInstantiation ) node, env );
		}
		else if ( node instanceof WrExprIdentStar ) {
			this.visit( (WrExprIdentStar ) node, env);
		}
		else if ( node instanceof WrExprIndexed ) {
			this.visit( (WrExprIndexed )  node, env );
		}
		else if ( node instanceof WrExprMessageSendUnaryChainToExpr ) {
			this.visit( (WrExprMessageSendUnaryChainToExpr ) node, env );
		}
		else if ( node instanceof WrExprMessageSendUnaryChainToSuper ) {
			this.visit( (WrExprMessageSendUnaryChainToSuper ) node, env );
		}
		else if ( node instanceof WrExprMessageSendWithKeywordsToExpr ) {
			this.visit( (WrExprMessageSendWithKeywordsToExpr ) node, env );
		}
		else if ( node instanceof WrExprMessageSendWithKeywordsToSuper ) {
			this.visit( (WrExprMessageSendWithKeywordsToSuper ) node, env );
		}
		else if ( node instanceof WrExprObjectCreation ) {
			this.visit( (WrExprObjectCreation ) node, env );
		}
		else if ( node instanceof WrExprSelf ) {
			this.visit( (WrExprSelf ) node, env);
		}
		else if ( node instanceof WrExprSurroundedByContext ) {
			this.visit( node, env );
		}
		else if ( node instanceof WrExprTypeof ) {
			this.visit( (WrExprTypeof ) node, env );
		}
		else if ( node instanceof WrExprUnary ) {
			this.visit( (WrExprUnary ) node, env);
		}
		else if ( node instanceof WrExprWithParenthesis ) {
			this.visit( (WrExprWithParenthesis )  node, env );
		}
		else if ( node instanceof WrExprJavaArrayType ) {
			this.visit( (WrExprJavaArrayType ) node, env );
		}
		else {
			signalError(node);
		}
	}

	@Override
	public void visit(WrExprAnyLiteral node, WrEnv env) {
		if ( node instanceof WrExprAnyLiteralIdent ) {
			this.visit( (WrExprAnyLiteralIdent ) node, env);
		}
		else if ( node instanceof WrExprLiteral ) {
			this.visit( (WrExprLiteral )  node, env );
		}
		else if ( node instanceof WrExprLiteralArray ) {
			this.visit( (WrExprLiteralArray ) node, env );
		}
		else if ( node instanceof WrExprLiteralTuple ) {
			this.visit( (WrExprLiteralTuple ) node, env );
		}
		else if ( node instanceof WrExprLiteralMap ) {
			this.visit( (WrExprLiteralMap ) node, env );
		}
		else {
			signalError(node);
		}

	}
	@Override
	public void visit(WrExprAnyLiteralIdent node, WrEnv env) { }
	@Override
	public void visit(WrExprLiteral node, WrEnv env) {
		if ( node instanceof WrExprLiteralBoolean ) {
			this.visit( (WrExprLiteralBoolean ) node, env);
		}
		else if ( node instanceof WrExprLiteralChar ) {
			this.visit( (WrExprLiteralChar )  node, env );
		}
		else if ( node instanceof WrExprLiteralNil ) {
			this.visit( (WrExprLiteralNil ) node, env );
		}
		else if ( node instanceof WrExprLiteralNumber ) {
			this.visit( (WrExprLiteralNumber ) node, env );
		}
		else if ( node instanceof WrExprLiteralString ) {
			this.visit( (WrExprLiteralString ) node, env );
		}
		else {
			signalError(node);
		}

	}

	@Override
	public void visit(WrExprBooleanAnd node, WrEnv env) { }
	@Override
	public void visit(WrExprBooleanOr node, WrEnv env) { }
	@Override
	public void visit(WrExprLiteralBoolean node, WrEnv env) { }
	@Override
	public void visit(WrExprLiteralChar node, WrEnv env) { }
	@Override
	public void visit(WrExprLiteralNil node, WrEnv env) { }
	@Override
	public void visit(WrExprLiteralNumber node, WrEnv env) { }
	@Override
	public void visit(WrExprLiteralString node, WrEnv env) { }
	@Override
	public void visit(WrExprLiteralArray node, WrEnv env) { }
	@Override
	public void visit(WrExprLiteralTuple node, WrEnv env) { }
	@Override
	public void visit(WrExprLiteralMap node, WrEnv env) { }


	@Override
	public void visit(WrExprFunction node, WrEnv env) {
		if ( node instanceof WrExprFunctionRegular ) {
			this.visit( (WrExprFunctionRegular ) node, env );
		}
		else if ( node instanceof WrExprFunctionWithKeywords ) {
			this.visit( (WrExprFunctionWithKeywords ) node, env );
		}
		else {
			signalError(node);
		}
	}
	@Override
	public void visit(WrExprFunctionRegular node, WrEnv env) { }
	@Override
	public void visit(WrExprFunctionWithKeywords node, WrEnv env) { }

	@Override
	public void visit(WrExprGenericPrototypeInstantiation node, WrEnv env) { }
	@Override
	public void visit(WrExprIdentStar node, WrEnv env) {
		System.out.println("WrExprIdentStar " + node.asString());
	}
	@Override
	public void visit(WrExprIndexed node, WrEnv env) {
		System.out.println("WrExprIndexed " + node.asString());
	}

	@Override
	public void visit(WrExprMessageSend node, WrEnv env) {
		if ( node instanceof WrExprMessageSendUnaryChainToExpr ) {
			this.visit( (WrExprMessageSendUnaryChainToExpr ) node, env );
		}
		else if ( node instanceof WrExprMessageSendUnaryChainToSuper ) {
			this.visit( (WrExprMessageSendUnaryChainToSuper ) node, env );
		}
		else if ( node instanceof WrExprMessageSendWithKeywordsToExpr ) {
			this.visit( (WrExprMessageSendWithKeywordsToExpr ) node, env );
		}
		else if ( node instanceof WrExprMessageSendWithKeywordsToSuper ) {
			this.visit( (WrExprMessageSendWithKeywordsToSuper ) node, env );
		}
		else {
			signalError(node);
		}

	}
	@Override
	public void visit(WrExprMessageSendUnaryChainToExpr node, WrEnv env) {
		System.out.println("WrExprMessageSendUnaryChainToExpr " + node.asString() + " ");
	}
	@Override
	public void visit(WrExprMessageSendUnaryChainToSuper node, WrEnv env) { }
	@Override
	public void visit(WrExprMessageSendWithKeywordsToExpr node, WrEnv env) { }
	@Override
	public void visit(WrExprMessageSendWithKeywordsToSuper node, WrEnv env) { }

	@Override
	public void visit(WrExprObjectCreation node, WrEnv env) { }
	@Override
	public void visit(WrExprSelf node, WrEnv env) { }
	@Override
	public void visit(WrExprSelfPeriodIdent node, WrEnv env) { }
	// public void visit(WrExprSurroundedByContext node, WrEnv env) { }
	@Override
	public void visit(WrExprTypeof node, WrEnv env) { }
	@Override
	public void visit(WrExprUnary node, WrEnv env) { }
	@Override
	public void visit(WrExprWithParenthesis node, WrEnv env) { }

	@Override
	public void visit(WrExprJavaArrayType node, WrEnv env) { }

	@Override
	public void visit(WrGenericParameter node, WrEnv env) { }

	@Override
	public void visit(WrMessageBinaryOperator node, WrEnv env) { }
	@Override
	public void visit(WrMessageWithKeywords node, WrEnv env) {
		if ( node instanceof WrMessageBinaryOperator ) {
			this.visit( (WrMessageBinaryOperator ) node, env );
		}
	}

	@Override
	public void visit(WrMethodKeywordWithParameters node, WrEnv env) { }


	@Override
	public void visit(IVariableDecInterface node, WrEnv env) {
		if ( node instanceof WrFieldDec ) {
			if ( node instanceof WrContextParameter ) {
				this.visit( (WrContextParameter ) node, env );
			}
			else {
				this.visit( (WrFieldDec ) node, env );
			}
		}
		else if ( node instanceof WrParameterDec ) {
			this.visit( (WrParameterDec ) node, env );
		}
		else if ( node instanceof WrStatementLocalVariableDec ) {
			this.visit( (WrStatementLocalVariableDec ) node, env );
		}
		else {
			signalError( (WrASTNode ) node);
		}
	}
	@Override
	public void visit(WrParameterDec node, WrEnv env) { }

	@Override
	public void visit(WrStatement node, WrEnv env) {
		if ( node instanceof WrExpr ) {
			this.visit( (WrExpr ) node, env );
		}
		else if ( node instanceof WrStatementAssignmentList ) {
			this.visit( (WrStatementAssignmentList ) node, env );
		}
		else if ( node instanceof WrStatementBreak ) {
			this.visit( (WrStatementBreak ) node, env );
		}
		else if ( node instanceof WrStatementAnnotation ) {
			this.visit( (WrStatementAnnotation ) node, env );
		}
		else if ( node instanceof WrStatementFor ) {
			this.visit( (WrStatementFor ) node, env );
		}
		else if ( node instanceof WrStatementType ) {
			this.visit( (WrStatementType) node, env );
		}
		else if ( node instanceof WrStatementIf ) {
			this.visit( (WrStatementIf ) node, env );
		}
		else if ( node instanceof WrStatementLocalVariableDec ) {
			this.visit( (WrStatementLocalVariableDec ) node, env );
		}
		else if ( node instanceof WrStatementLocalVariableDecList ) {
			this.visit( (WrStatementLocalVariableDecList ) node, env );
		}
		else if ( node instanceof WrStatementMinusMinusIdent ) {
			this.visit( (WrStatementMinusMinusIdent ) node, env );
		}
		else if ( node instanceof WrStatementNull ) {
			this.visit( (WrStatementNull ) node, env );
		}
		else if ( node instanceof WrStatementPlusPlusIdent ) {
			this.visit( (WrStatementPlusPlusIdent ) node, env );
		}
		else if ( node instanceof WrStatementReturn ) {
			this.visit( (WrStatementReturn ) node, env );
		}
		else if ( node instanceof WrStatementReturnFunction ) {
			this.visit( (WrStatementReturnFunction ) node, env );
		}
		else if ( node instanceof WrStatementWhile ) {
			this.visit( (WrStatementWhile ) node, env );
		}
		else if ( node instanceof WrStatementRepeat ) {
			this.visit( (WrStatementRepeat ) node, env );
		}
		else if ( node instanceof WrStatementCast ) {
			this.visit( (WrStatementCast ) node, env );
		}
		else {
			signalError(node);
		}

	}


	@Override
	public void visit(WrStatementList node, WrEnv env) { }
	@Override
	public void visit(WrStatementAssignmentList node, WrEnv env) { }
	@Override
	public void visit(WrStatementBreak node, WrEnv env) { }
	@Override
	public void visit(WrStatementAnnotation node, WrEnv env) { }

	@Override
	public void visit(WrStatementFor node, WrEnv env) {
		System.out.println("WrStatementFor " + node.asString());
		++this.countFor ;
	}
	@Override
	public void visit(WrStatementIf node, WrEnv env) {
		System.out.println("WrStatementIf " + node.asString());
		++this.countIf;
	}
    @Override
	public void visit(WrStatementLocalVariableDec node, WrEnv env) {
    	System.out.println("WrStatementLocalVariableDec " + node.asString());
    }
    @Override
	public void visit(WrStatementLocalVariableDecList node, WrEnv env) { }
	@Override
	public void visit(WrStatementMinusMinusIdent node, WrEnv env) { }
	@Override
	public void visit(WrStatementNull node, WrEnv env) { }
	@Override
	public void visit(WrStatementPlusPlusIdent node, WrEnv env) { }

	@Override
	public void visit(WrStatementReturn node, WrEnv env) { }
	@Override
	public void visit(WrStatementReturnFunction node, WrEnv env) { }
	@Override
	public void visit(WrStatementWhile node, WrEnv env) {
		System.out.println("WrStatementWhile " + node.asString());
		++this.countWhile;
	}
	@Override
	public void visit(WrStatementRepeat node, WrEnv env) {
		System.out.println("WrStatementRepeat " + node.asString());
		++this.countRepeat;
	}
	@Override
	public void visit(WrStatementType node, WrEnv env) { }
	@Override
	public void visit(WrStatementCast node, WrEnv env) { }


	@Override
	public void visit(WrMessageKeywordWithRealParameters node, WrEnv env) { }


	// public void visit(WrCompilationUnitSuper node, WrEnv env) { }

	// public void visit(WrType node, WrEnv env) { }
	@Override
	public void visit(WrCastRecord node, WrEnv env) {	}

	@Override
	public void visit(WrCaseRecord node, WrEnv env) { }



}
