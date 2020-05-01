package ast;

import java.util.List;
import lexer.Symbol;
import meta.MetaHelper;
import meta.WrExprSurroundedByContext;
import saci.CyanEnv;
import saci.Env;

/**
 * represents an expression surrounded by metaobject annotations
   such as <code>i+j</code> in
 * <code> <br>
 * {@literal @}markDeletedCode(1)
 * {@literal @}pushCompilationContext(atisemAn_id_2, "number(bin, Bin, BIN)", main, Program, 6) i + j {@literal @}popCompilationContext(atisemAn_id_2)<br><br>
 *
 * The metaobject annotations that precede the expression should be a regular metaobject annotation, optionally markDeletedCode, and necessarily
 * pushCompilationContext. The metaobject annotation that follows the expression should be popCompilationContext.
 * </code>
   @author José
 */

public class ExprSurroundedByContext extends Expr {

	public ExprSurroundedByContext(AnnotationAt regularAnnotation, AnnotationAt markDeletedCodeAnnotation,
			AnnotationAt pushCompilationContextAnnotation,
			Expr expr, AnnotationAt popCompilationContextAnnotation, MethodDec method) {
		super(method);
		this.regularAnnotation = regularAnnotation;
		this.markDeletedCodeAnnotation = markDeletedCodeAnnotation;
		this.pushCompilationContextAnnotation = pushCompilationContextAnnotation;
		this.expr = expr;
		this.popCompilationContextAnnotation = popCompilationContextAnnotation;
	}

	@Override
	public WrExprSurroundedByContext getI() {
		return new WrExprSurroundedByContext(this);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		expr.accept(visitor);
		visitor.visit(this);
	}


	@Override
	public boolean mayBeStatement() {
		return expr.mayBeStatement();
	}

	@Override
	public Symbol getFirstSymbolMayBeAnnotation() {
		if ( regularAnnotation != null ) { return regularAnnotation.getFirstSymbol(); }
		else if ( this.markDeletedCodeAnnotation != null ) { return this.markDeletedCodeAnnotation.getFirstSymbol(); }
		else {
			return pushCompilationContextAnnotation.getFirstSymbol();
		}
	}



	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
		pw.print(" ( ");
		if ( regularAnnotation != null ) {
			regularAnnotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		}
		pw.print(" ");
		if ( this.markDeletedCodeAnnotation != null ) {
			this.markDeletedCodeAnnotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			pw.print(" ");
		}
		pushCompilationContextAnnotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.print(" ");
		expr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.print(" ");
		popCompilationContextAnnotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.print(" ) ");
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		return expr.genJavaExpr(pw, env);
	}

	@Override
	public void calcInternalTypes(Env env) {

		if ( regularAnnotation != null ) {
			regularAnnotation.calcInternalTypes(env);
		}
		if ( this.markDeletedCodeAnnotation != null ) {
			this.markDeletedCodeAnnotation.calcInternalTypes(env);
		}
		pushCompilationContextAnnotation.calcInternalTypes(env);
		expr.calcInternalTypes(env);
		type = expr.getType(env);
		popCompilationContextAnnotation.calcInternalTypes(env);
		List<Object> javaParamList = popCompilationContextAnnotation.getJavaParameterList();
		if ( javaParamList.size() != 3 ) {
			env.error(this.popCompilationContextAnnotation.getFirstSymbol(), "It was expected that '" + MetaHelper.popCompilationContextName +
					"' had three parameters, the last two with the type of the expression");
		}
		else {
			String packageName = MetaHelper.removeQuotes((String ) popCompilationContextAnnotation.getJavaParameterList().get(1));
			String prototypeName = MetaHelper.removeQuotes((String ) popCompilationContextAnnotation.getJavaParameterList().get(2));
			Prototype pu = env.searchPackagePrototype(
					packageName,
					prototypeName);
			Type ty = pu;
			if ( pu == null ) {
				ty = env.searchPackageJavaClass(packageName, prototypeName);
			}
			if ( ty == null ) {
				env.error(this.popCompilationContextAnnotation.getFirstSymbol(), "Metaobject'" + MetaHelper.popCompilationContextName +
						"' says that the metaobject '" +
						MetaHelper.removeQuotes( (String  ) this.pushCompilationContextAnnotation.getJavaParameterList().get(1))
						+ "' should produce an expression of type '" +
						packageName + "." + prototypeName + "' but this type does not exist"
						);
			}
			else {
				if ( !(ty.isSupertypeOf(type, env)) ) {
					env.error(this.popCompilationContextAnnotation.getFirstSymbol(), "Metaobject '" + MetaHelper.popCompilationContextName +
							"' says that the metaobject '" +
							MetaHelper.removeQuotes( (String  ) this.pushCompilationContextAnnotation.getJavaParameterList().get(1))
							+ "' should produce an expression of type '" +
							packageName + "." + prototypeName + "'. But the expression produced has type '" +
							type.getFullName() + "' which is not subtype of that type"
							);
				}
			}
		}


		super.calcInternalTypes(env);
	}

	@Override
	public Symbol getFirstSymbol() {
		// return expr.getFirstSymbol();

		if ( regularAnnotation != null ) { return regularAnnotation.getFirstSymbol(); }
		else if ( this.markDeletedCodeAnnotation != null ) { return this.markDeletedCodeAnnotation.getFirstSymbol(); }
		else {
			return pushCompilationContextAnnotation.getFirstSymbol();
		}

	}


	public Expr getExpr() {
		return expr;
	}



	private AnnotationAt regularAnnotation, markDeletedCodeAnnotation, pushCompilationContextAnnotation, popCompilationContextAnnotation;
	private Expr expr;
}
