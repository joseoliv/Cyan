package ast;

@SuppressWarnings("unused")
public abstract class ASTVisitor {

	private void signalError(ASTNode node) {
		String msg = "Class '" + node.getClass().getName() + "' is not recognized by class " + this.getClass().getName();
		System.out.println(msg);
		throw new error.CompileErrorException(msg);
	}

	public void visit(Program node) { }
	public void visit(CyanPackage node) { }
	public void visit(JVMPackage node) { }
	public void visit(CompilationUnit node) { }
	public void visit(Prototype node) {
		if ( node instanceof InterfaceDec ) {
			this.visit((InterfaceDec ) node);
		}
		else if ( node instanceof ObjectDec ) {
			this.visit((ObjectDec ) node);
		}
		else {
			signalError(node);
		}
	}
	public void visit(InterfaceDec node) { }
	public void visit(ObjectDec node) { }
	public void visit(MethodDec node) { }
	public void visit(MethodSignature node) {
		if ( node instanceof MethodSignatureWithKeywords ) {
			this.visit( (MethodSignatureWithKeywords ) node );
		}
		else if ( node instanceof MethodSignatureOperator ) {
			this.visit( (MethodSignatureOperator ) node );
		}
		else if ( node instanceof MethodSignatureUnary ) {
			this.visit( (MethodSignatureUnary ) node );
		}
		else {
			signalError(node);
		}
	}
	public void visit(MethodSignatureWithKeywords node) { }
	public void visit(MethodSignatureOperator node) { }
	public void visit(MethodSignatureUnary node) { }


	public void preVisit(Program node) { }
	public void preVisit(CyanPackage node) { }
	public void preVisit(JVMPackage node) { }
	public void preVisit(CompilationUnit node) { }
	public void preVisit(CompilationUnitSuper compilationUnitSuper) { }
	public void preVisit(Prototype node) {
		if ( node instanceof InterfaceDec ) {
			this.preVisit((InterfaceDec ) node);
		}
		else if ( node instanceof ObjectDec ) {
			this.preVisit((ObjectDec ) node);
		}
		else {
			signalError(node);
		}
}

	public void preVisit(InterfaceDec node) { }
	public void preVisit(ObjectDec node) { }
	public void preVisit(MethodDec node) { }
	public void preVisit(MethodSignature node) {
		if ( node instanceof MethodSignatureWithKeywords ) {
			this.preVisit( (MethodSignatureWithKeywords ) node );
		}
		else if ( node instanceof MethodSignatureOperator ) {
			this.preVisit( (MethodSignatureOperator ) node );
		}
		else if ( node instanceof MethodSignatureUnary ) {
			this.preVisit( (MethodSignatureUnary ) node );
		}
		else {
			signalError(node);
		}
	}

	public void preVisit(MethodSignatureWithKeywords node) { }
	public void preVisit(MethodSignatureOperator node) { }
	public void preVisit(MethodSignatureUnary node) { }


	public void visit(SlotDec node) {
		if ( node instanceof FieldDec ) {
			if ( node instanceof ContextParameter ) {
				this.visit( (ContextParameter ) node );
			}
			else {
				this.visit( (FieldDec ) node );
			}
		}
		else if ( node instanceof MethodDec ) {
			this.visit( (MethodDec ) node );
		}
		else {
			signalError(node);
		}
	}

	public void visit(ContextParameter node) { }
	public void visit(FieldDec node) { }
	public void visit(Annotation node) {
		if ( node instanceof AnnotationLiteralObject ) {
			this.visit( (AnnotationLiteralObject ) node);
		}
		else if ( node instanceof AnnotationMacroCall ) {
			this.visit( (AnnotationMacroCall )  node );
		}
		else if ( node instanceof AnnotationAt ) {
			this.visit( (AnnotationAt ) node );
		}
		else {
			signalError(node);
		}
	}
	public void visit(AnnotationLiteralObject node) { }
	public void visit(AnnotationMacroCall node) { }
	public void visit(AnnotationAt node) { }


	public void visit(Expr node) {

		if ( node instanceof AnnotationLiteralObject ) {
			this.visit( (AnnotationLiteralObject ) node);
		}
		else if ( node instanceof AnnotationMacroCall ) {
			this.visit( (AnnotationMacroCall )  node );
		}
		else if ( node instanceof AnnotationAt ) {
			this.visit( (AnnotationAt ) node );
		}
		else if ( node instanceof ExprAnyLiteral ) {
			this.visit( (ExprAnyLiteral ) node);
		}
		else if ( node instanceof ExprBooleanAnd ) {
			this.visit( (ExprBooleanAnd )  node );
		}
		else if ( node instanceof ExprBooleanOr ) {
			this.visit( (ExprBooleanOr ) node );
		}
		else if ( node instanceof ExprFunctionRegular ) {
			this.visit( (ExprFunctionRegular ) node);
		}
		else if ( node instanceof ExprFunctionWithKeywords ) {
			this.visit( (ExprFunctionWithKeywords )  node );
		}
		else if ( node instanceof  ExprGenericPrototypeInstantiation ) {
			this.visit( (ExprGenericPrototypeInstantiation ) node );
		}
		else if ( node instanceof ExprIdentStar ) {
			this.visit( (ExprIdentStar ) node);
		}
		else if ( node instanceof ExprIndexed ) {
			this.visit( (ExprIndexed )  node );
		}
		else if ( node instanceof ExprMessageSendUnaryChainToExpr ) {
			this.visit( (ExprMessageSendUnaryChainToExpr ) node );
		}
		else if ( node instanceof ExprMessageSendUnaryChainToSuper ) {
			this.visit( (ExprMessageSendUnaryChainToSuper ) node );
		}
		else if ( node instanceof ExprMessageSendWithKeywordsToExpr ) {
			this.visit( (ExprMessageSendWithKeywordsToExpr ) node );
		}
		else if ( node instanceof ExprMessageSendWithKeywordsToSuper ) {
			this.visit( (ExprMessageSendWithKeywordsToSuper ) node );
		}
		else if ( node instanceof ExprObjectCreation ) {
			this.visit( (ExprObjectCreation ) node );
		}
		else if ( node instanceof ExprSelf ) {
			this.visit( (ExprSelf ) node);
		}
		else if ( node instanceof ExprSurroundedByContext ) {
			this.visit( (ExprSurroundedByContext )  node );
		}
		else if ( node instanceof ExprTypeof ) {
			this.visit( (ExprTypeof ) node );
		}
		else if ( node instanceof ExprUnary ) {
			this.visit( (ExprUnary ) node);
		}
		else if ( node instanceof ExprWithParenthesis ) {
			this.visit( (ExprWithParenthesis )  node );
		}
		else if ( node instanceof ExprJavaArrayType ) {
			this.visit( (ExprJavaArrayType ) node );
		}
		else if ( node instanceof ExprTypeUnion ) {
			this.visit( (ExprTypeUnion ) node );
		}
		else {
			signalError(node);
		}
	}

	public void visit(ExprAnyLiteral node) {
		if ( node instanceof ExprAnyLiteralIdent ) {
			this.visit( (ExprAnyLiteralIdent ) node);
		}
		else if ( node instanceof ExprLiteral ) {
			this.visit( (ExprLiteral )  node );
		}
		else if ( node instanceof ExprLiteralArray ) {
			this.visit( (ExprLiteralArray ) node );
		}
		else if ( node instanceof ExprLiteralTuple ) {
			this.visit( (ExprLiteralTuple ) node );
		}
		else if ( node instanceof ExprLiteralMap ) {
			this.visit( (ExprLiteralMap ) node );
		}
		else {
			signalError(node);
		}

	}
	public void visit(ExprAnyLiteralIdent node) { }
	public void visit(ExprLiteral node) {
		if ( node instanceof ExprLiteralBoolean ) {
			this.visit( (ExprLiteralBoolean ) node);
		}
		else if ( node instanceof ExprLiteralChar ) {
			this.visit( (ExprLiteralChar )  node );
		}
		else if ( node instanceof ExprLiteralNil ) {
			this.visit( (ExprLiteralNil ) node );
		}
		else if ( node instanceof ExprLiteralNumber ) {
			this.visit( (ExprLiteralNumber ) node );
		}
		else if ( node instanceof ExprLiteralString ) {
			this.visit( (ExprLiteralString ) node );
		}
		else {
			signalError(node);
		}

	}

	public void visit(ExprBooleanAnd node) { }
	public void visit(ExprBooleanOr node) { }
	public void visit(ExprLiteralBoolean node) { }
	public void visit(ExprLiteralChar node) { }
	public void visit(ExprLiteralNil node) { }
	public void visit(ExprLiteralNumber node) { }
	public void visit(ExprLiteralString node) { }
	public void visit(ExprLiteralArray node) { }
	public void visit(ExprLiteralTuple node) { }
	public void visit(ExprLiteralMap node) { }


	public void visit(ExprFunction node) {
		if ( node instanceof ExprFunctionRegular ) {
			this.visit( (ExprFunctionRegular ) node );
		}
		else if ( node instanceof ExprFunctionWithKeywords ) {
			this.visit( (ExprFunctionWithKeywords ) node );
		}
		else {
			signalError(node);
		}
	}
	public void visit(ExprFunctionRegular node) { }
	public void visit(ExprFunctionWithKeywords node) { }

	public void visit(ExprGenericPrototypeInstantiation node) { }
	public void visit(ExprIdentStar node) { }
	public void visit(ExprIndexed node) { }

	public void visit(ExprMessageSend node) {
		if ( node instanceof ExprMessageSendUnaryChainToExpr ) {
			this.visit( (ExprMessageSendUnaryChainToExpr ) node );
		}
		else if ( node instanceof ExprMessageSendUnaryChainToSuper ) {
			this.visit( (ExprMessageSendUnaryChainToSuper ) node );
		}
		else if ( node instanceof ExprMessageSendWithKeywordsToExpr ) {
			this.visit( (ExprMessageSendWithKeywordsToExpr ) node );
		}
		else if ( node instanceof ExprMessageSendWithKeywordsToSuper ) {
			this.visit( (ExprMessageSendWithKeywordsToSuper ) node );
		}
		else {
			signalError(node);
		}

	}
	public void visit(ExprMessageSendUnaryChainToExpr node) { }
	public void visit(ExprMessageSendUnaryChainToSuper node) { }
	public void visit(ExprMessageSendWithKeywordsToExpr node) { }
	public void visit(ExprMessageSendWithKeywordsToSuper node) { }

	public void visit(ExprObjectCreation node) { }
	public void visit(ExprSelf node) { }
	public void visit(ExprSelfPeriodIdent node) { }
	public void visit(ExprSurroundedByContext node) { }
	public void visit(ExprTypeof node) { }
	public void visit(ExprUnary node) { }
	public void visit(ExprWithParenthesis node) {
		this.visit(node.getExpr());
	}

	public void visit(ExprTypeUnion node) { }

	public void visit(ExprJavaArrayType node) {	}
	public void visit(GenericParameter node) { }

	public void visit(MessageBinaryOperator node) { }
	public void visit(MessageWithKeywords node) {
		if ( node instanceof MessageBinaryOperator ) {
			this.visit( (MessageBinaryOperator ) node );
		}
	}

	public void visit(MethodKeywordWithParameters node) { }


	public void visit(VariableDecInterface node) {
		if ( node instanceof FieldDec ) {
			if ( node instanceof ContextParameter ) {
				this.visit( (ContextParameter ) node );
			}
			else {
				this.visit( (FieldDec ) node );
			}
		}
		else if ( node instanceof ParameterDec ) {
			this.visit( (ParameterDec ) node );
		}
		else if ( node instanceof StatementLocalVariableDec ) {
			this.visit( (StatementLocalVariableDec ) node );
		}
		else {
			signalError(node);
		}
	}
	public void visit(ParameterDec node) { }

	public void visit(Statement node) {
		if ( node instanceof Expr ) {
			this.visit( (Expr ) node );
		}
		else if ( node instanceof StatementAssignmentList ) {
			this.visit( (StatementAssignmentList ) node );
		}
		else if ( node instanceof StatementBreak ) {
			this.visit( (StatementBreak ) node );
		}
		else if ( node instanceof StatementAnnotation ) {
			this.visit( (StatementAnnotation ) node );
		}
		else if ( node instanceof StatementFor ) {
			this.visit( (StatementFor ) node );
		}
		else if ( node instanceof StatementType ) {
			this.visit( (StatementType) node );
		}
		else if ( node instanceof StatementIf ) {
			this.visit( (StatementIf ) node );
		}
		else if ( node instanceof StatementLocalVariableDec ) {
			this.visit( (StatementLocalVariableDec ) node );
		}
		else if ( node instanceof StatementLocalVariableDecList ) {
			this.visit( (StatementLocalVariableDecList ) node );
		}
		else if ( node instanceof StatementMinusMinusIdent ) {
			this.visit( (StatementMinusMinusIdent ) node );
		}
		else if ( node instanceof StatementNull ) {
			this.visit( (StatementNull ) node );
		}
		else if ( node instanceof StatementPlusPlusIdent ) {
			this.visit( (StatementPlusPlusIdent ) node );
		}
		else if ( node instanceof StatementReturn ) {
			this.visit( (StatementReturn ) node );
		}
		else if ( node instanceof StatementReturnFunction ) {
			this.visit( (StatementReturnFunction ) node );
		}
		else if ( node instanceof StatementWhile ) {
			this.visit( (StatementWhile ) node );
		}
		else if ( node instanceof StatementRepeat ) {
			this.visit( (StatementRepeat ) node );
		}
		else if ( node instanceof StatementCast ) {
			this.visit( (StatementCast ) node );
		}
		else if ( node instanceof StatementTry ) {
			this.visit( (StatementTry ) node );
		}
		else if ( node instanceof StatementThrow ) {
			this.visit( (StatementThrow ) node );
		}
		else {
			signalError(node);
		}

	}


	public void visit(StatementList node) { }
	public void visit(StatementAssignmentList node) { }
	public void visit(StatementBreak node) { }
	public void visit(StatementAnnotation node) { }

	public void visit(StatementFor node) { }
	public void visit(StatementIf node) { }
    public void visit(StatementLocalVariableDec node) { }
    public void visit(StatementLocalVariableDecList node) { }
	public void visit(StatementMinusMinusIdent node) { }
	public void visit(StatementNull node) { }
	public void visit(StatementPlusPlusIdent node) { }

	public void visit(StatementReturn node) { }
	public void visit(StatementReturnFunction node) { }
	public void visit(StatementWhile node) { }
	public void visit(StatementRepeat node) { }
	public void visit(StatementType node) { }
	public void visit(CaseRecord node) { }
	public void visit(StatementCast node) { }
	public void visit(StatementTry node) { }
	public void visit(StatementThrow node) { }


	public void visit(MessageKeywordWithRealParameters node) { }


	public void visit(CompilationUnitSuper compilationUnitSuper) { }

	public void visit(Type node) { }

	public void visit(CastRecord castRecord) {	}


}
