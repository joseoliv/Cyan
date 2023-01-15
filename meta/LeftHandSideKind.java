package meta;

/**  Enumerate that has a value for each kind of 'left-hand' side, each thing that may receive an expression.
 * The left may be<br>
 *  <ul>
 *  <li> a method signature when an expression is associated to a method, that does not have a body as in<br>
 *  <code> func zero -> Int = 5 - 5;<code><br>
 *   The enum value that represents this is MethodSignatureEqualTo_LHS
 *  </li>
 *  <li> a variable when an expressions is assigned to a variable in its declaration. The enum value that represents this is StatementLocalVariableDec_LHS </li>
 *  <li> a variable when an expression is assigned to a variable.
 *  This occurs in type-case statements and for statements. The enum value that represents this is LeftSideAssignmetn_LHS </li>
 *  <li> a method signature when an expression is returned by a method in a 'return' statement. The enum value that represents this is
 *  MethodSignatureReturn_LHS </li>
 *  <li> no left when an expression is in a literal array, tuple, or map. The enum value that represents this is LiteralObject_LHS</li>
 *  <li> a parameter when an expression is passed as a parameter in a message send. The receiver may be an expression or super.
 *  The enum value that represents this is ParameterDec_LHS.</li>
 *  <li> a parameter when an expression is passed as a parameter in an object creation as in String("ok") or Person(name, age).
 *  The enum value that represents this is ParameterDec_LHS. </li>
 *  </ul>
 *<br>
 *See enum {@link saci#LeftHandSideKind}
 *Each kind of
 *See the examples below.
 *<code><br>
     func m = expr;                MethodSignatureEqualTo_LHS<br>
     var k = expr;                 StatementLocalVariableDec_LHS<br>
     var Int k = expr;             StatementLocalVariableDec_LHS<br>
     k = expr;                     LeftSideAssignment_LHS<br>
     type expr case Int n { }      StatementLocalVariableDec_LHS<br>
     for n in array { } 		   StatementLocalVariableDec_LHS<br>
     return expr;                  MethodSignatureReturn_LHS<br>
     [ expr, expr ]                LiteralObject_LHS<br>
     [. expr, expr .]              LiteralObject_LHS<br>
     [ expr -> expr ]              LiteralObject_LHS<br>
     receiver key: expr;           ParameterDec_LHS<br>
     super key: expr;              ParameterDec_LHS<br>
     Proto(expr)                   ParameterDec_LHS<br>
  </code>
   @author jose

 */

public enum LeftHandSideKind {

	MethodSignatureEqualTo_LHS(ast.MethodSignature.class),
	MethodSignatureReturn_LHS(ast.MethodSignature.class),
	FunctionReturn_LHS(ast.ExprFunction.class),
	StatementLocalVariableDec_LHS(ast.StatementLocalVariableDec.class),
	LeftSideAssignment_LHS(ast.Expr.class),
    ParameterDec_LHS(ast.ParameterDec.class);

	LeftHandSideKind(Class<?> astNodeClass) { this.astNodeClass = astNodeClass; }
	public Class<?> getAstNodeClass() {
		return astNodeClass;
	}

	private Class<?> astNodeClass;
}
