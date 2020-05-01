package meta;

@SuppressWarnings("unused")
public class WrASTVisitor {

	public WrASTVisitor() { }

	protected void signalError(WrASTNode node) {
		String msg = "Class '" + node.getClass().getName() + "' is not recognized by class " + this.getClass().getName();
		System.out.println(msg);
		throw new error.CompileErrorException(msg);
	}

	// public void visit(WrProgram node, WrEnv env) { }
	// public void visit(WrCyanPackage node, WrEnv env) { }
	public void visit(WrJVMPackage node, WrEnv env) { }
	public void visit(WrCompilationUnit node, WrEnv env) { }
	public void visit(WrPrototype node, WrEnv env) {	}
	public void visit(WrMethodDec node, WrEnv env) { }
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
			signalError(node);
		}
	}
	public void visit(WrMethodSignatureWithKeywords node, WrEnv env) { }
	public void visit(WrMethodSignatureOperator node, WrEnv env) { }
	public void visit(WrMethodSignatureUnary node, WrEnv env) { }


	// public void preVisit(WrProgram node, WrEnv env) { }
	// public void preVisit(WrCyanPackage node, WrEnv env) { }
	public void preVisit(WrCompilationUnit node, WrEnv env) { }
	public void preVisit(WrPrototype node, WrEnv env) { }

	public void preVisit(WrMethodDec node, WrEnv env) { }
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

	public void preVisit(WrMethodSignatureWithKeywords node, WrEnv env) { }
	public void preVisit(WrMethodSignatureOperator node, WrEnv env) { }
	public void preVisit(WrMethodSignatureUnary node, WrEnv env) { }
	public void preVisit(WrExprFunctionRegular node, WrEnv env) { }
	public void preVisit(WrExprFunctionWithKeywords node, WrEnv env) { }


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

	public void visit(WrContextParameter node, WrEnv env) { }
	public void visit(WrFieldDec node, WrEnv env) { }
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
	public void visit(WrAnnotationLiteralObject node, WrEnv env) { }
	public void visit(WrAnnotationMacroCall node, WrEnv env) { }
	public void visit(WrAnnotationAt node, WrEnv env) { }


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
			this.visit( (WrExprSurroundedByContext ) node, env );
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
	public void visit(WrExprAnyLiteralIdent node, WrEnv env) { }
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

	public void visit(WrExprBooleanAnd node, WrEnv env) { }
	public void visit(WrExprBooleanOr node, WrEnv env) { }
	public void visit(WrExprLiteralBoolean node, WrEnv env) { }
	public void visit(WrExprLiteralChar node, WrEnv env) { }
	public void visit(WrExprLiteralNil node, WrEnv env) { }
	public void visit(WrExprLiteralNumber node, WrEnv env) { }
	public void visit(WrExprLiteralString node, WrEnv env) { }
	public void visit(WrExprLiteralArray node, WrEnv env) { }
	public void visit(WrExprLiteralTuple node, WrEnv env) { }
	public void visit(WrExprLiteralMap node, WrEnv env) { }


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
	public void visit(WrExprFunctionRegular node, WrEnv env) { }
	public void visit(WrExprFunctionWithKeywords node, WrEnv env) { }

	public void visit(WrExprGenericPrototypeInstantiation node, WrEnv env) { }
	public void visit(WrExprIdentStar node, WrEnv env) { }
	public void visit(WrExprIndexed node, WrEnv env) { }

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
	public void visit(WrExprMessageSendUnaryChainToExpr node, WrEnv env) { }
	public void visit(WrExprMessageSendUnaryChainToSuper node, WrEnv env) { }
	public void visit(WrExprMessageSendWithKeywordsToExpr node, WrEnv env) { }
	public void visit(WrExprMessageSendWithKeywordsToSuper node, WrEnv env) { }

	public void visit(WrExprObjectCreation node, WrEnv env) { }
	public void visit(WrExprSelf node, WrEnv env) { }
	public void visit(WrExprSelfPeriodIdent node, WrEnv env) { }
	public void visit(WrExprSurroundedByContext node, WrEnv env) {
		visit(node.getExpr(), env);
	}
	public void visit(WrExprTypeof node, WrEnv env) { }
	public void visit(WrExprUnary node, WrEnv env) { }
	public void visit(WrExprWithParenthesis node, WrEnv env) { }

	public void visit(WrExprJavaArrayType node, WrEnv env) { }

	public void visit(WrGenericParameter node, WrEnv env) { }

	public void visit(WrMessageBinaryOperator node, WrEnv env) { }
	public void visit(WrMessageWithKeywords node, WrEnv env) {
		if ( node instanceof WrMessageBinaryOperator ) {
			this.visit( (WrMessageBinaryOperator ) node, env );
		}
	}

	public void visit(WrMethodKeywordWithParameters node, WrEnv env) { }


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
	public void visit(WrParameterDec node, WrEnv env) { }

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


	public void visit(WrStatementList node, WrEnv env) { }
	public void visit(WrStatementAssignmentList node, WrEnv env) { }
	public void visit(WrStatementBreak node, WrEnv env) { }
	public void visit(WrStatementAnnotation node, WrEnv env) { }

	public void visit(WrStatementFor node, WrEnv env) { }
	public void visit(WrStatementIf node, WrEnv env) { }
    public void visit(WrStatementLocalVariableDec node, WrEnv env) { }
    public void visit(WrStatementLocalVariableDecList node, WrEnv env) { }
	public void visit(WrStatementMinusMinusIdent node, WrEnv env) { }
	public void visit(WrStatementNull node, WrEnv env) { }
	public void visit(WrStatementPlusPlusIdent node, WrEnv env) { }

	public void visit(WrStatementReturn node, WrEnv env) { }
	public void visit(WrStatementReturnFunction node, WrEnv env) { }
	public void visit(WrStatementWhile node, WrEnv env) { }
	public void visit(WrStatementRepeat node, WrEnv env) { }
	public void visit(WrStatementType node, WrEnv env) { }
	public void visit(WrStatementCast node, WrEnv env) { }


	public void visit(WrMessageKeywordWithRealParameters node, WrEnv env) { }


	// public void visit(WrCompilationUnitSuper node, WrEnv env) { }

	// public void visit(WrType node, WrEnv env) { }
	public void visit(WrCastRecord node, WrEnv env) {	}

	public void visit(WrCaseRecord node, WrEnv env) { }



}
