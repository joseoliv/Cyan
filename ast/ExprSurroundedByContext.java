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
 * {@literal @}pushCompilationContext(atiDsa_id_2, "number(bin, Bin, BIN)", main, Program, 6) i + j {@literal @}popCompilationContext(atiDsa_id_2)<br><br>
 *
 * The metaobject annotations that precede the expression should be a regular metaobject annotation, optionally markDeletedCode, and necessarily
 * pushCompilationContext. The metaobject annotation that follows the expression should be popCompilationContext.
 * </code>
   @author José
 */

public class ExprSurroundedByContext extends Expr {

	public ExprSurroundedByContext(AnnotationAt regularMetaobjectAnnotation, AnnotationAt markDeletedCodeMetaobjectAnnotation,
			AnnotationAt pushCompilationContextMetaobjectAnnotation,
			Expr expr, AnnotationAt popCompilationContextMetaobjectAnnotation) {
		super();
		this.regularMetaobjectAnnotation = regularMetaobjectAnnotation;
		this.markDeletedCodeMetaobjectAnnotation = markDeletedCodeMetaobjectAnnotation;
		this.pushCompilationContextMetaobjectAnnotation = pushCompilationContextMetaobjectAnnotation;
		this.expr = expr;
		this.popCompilationContextMetaobjectAnnotation = popCompilationContextMetaobjectAnnotation;
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
		if ( regularMetaobjectAnnotation != null ) { return regularMetaobjectAnnotation.getFirstSymbol(); }
		else if ( this.markDeletedCodeMetaobjectAnnotation != null ) { return this.markDeletedCodeMetaobjectAnnotation.getFirstSymbol(); }
		else {
			return pushCompilationContextMetaobjectAnnotation.getFirstSymbol();
		}
	}



	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
		pw.print(" ( ");
		if ( regularMetaobjectAnnotation != null ) {
			regularMetaobjectAnnotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		}
		pw.print(" ");
		if ( this.markDeletedCodeMetaobjectAnnotation != null ) {
			this.markDeletedCodeMetaobjectAnnotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			pw.print(" ");
		}
		pushCompilationContextMetaobjectAnnotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.print(" ");
		expr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.print(" ");
		popCompilationContextMetaobjectAnnotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.print(" ) ");
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		return expr.genJavaExpr(pw, env);
	}

	@Override
	public void calcInternalTypes(Env env) {

		if ( regularMetaobjectAnnotation != null ) {
			regularMetaobjectAnnotation.calcInternalTypes(env);
		}
		if ( this.markDeletedCodeMetaobjectAnnotation != null ) {
			this.markDeletedCodeMetaobjectAnnotation.calcInternalTypes(env);
		}
		pushCompilationContextMetaobjectAnnotation.calcInternalTypes(env);
		expr.calcInternalTypes(env);
		type = expr.getType(env);
		popCompilationContextMetaobjectAnnotation.calcInternalTypes(env);
		List<Object> javaParamList = popCompilationContextMetaobjectAnnotation.getJavaParameterList();
		if ( javaParamList.size() != 3 ) {
			env.error(this.popCompilationContextMetaobjectAnnotation.getFirstSymbol(), "It was expected that '" + MetaHelper.popCompilationContextName +
					"' had three parameters, the last two with the type of the expression");
		}
		else {
			String packageName = MetaHelper.removeQuotes((String ) popCompilationContextMetaobjectAnnotation.getJavaParameterList().get(1));
			String prototypeName = MetaHelper.removeQuotes((String ) popCompilationContextMetaobjectAnnotation.getJavaParameterList().get(2));
			ProgramUnit pu = env.searchPackagePrototype(
					packageName,
					prototypeName);
			Type ty = pu;
			if ( pu == null ) {
				ty = env.searchPackageJavaClass(packageName, prototypeName);
			}
			if ( ty == null ) {
				env.error(this.popCompilationContextMetaobjectAnnotation.getFirstSymbol(), "Metaobject'" + MetaHelper.popCompilationContextName +
						"' says that the metaobject '" +
						MetaHelper.removeQuotes( (String  ) this.pushCompilationContextMetaobjectAnnotation.getJavaParameterList().get(1))
						+ "' should produce an expression of type '" +
						packageName + "." + prototypeName + "' but this type does not exist"
						);
			}
			else {
				if ( !(ty.isSupertypeOf(type, env)) ) {
					env.error(this.popCompilationContextMetaobjectAnnotation.getFirstSymbol(), "Metaobject '" + MetaHelper.popCompilationContextName +
							"' says that the metaobject '" +
							MetaHelper.removeQuotes( (String  ) this.pushCompilationContextMetaobjectAnnotation.getJavaParameterList().get(1))
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

		if ( regularMetaobjectAnnotation != null ) { return regularMetaobjectAnnotation.getFirstSymbol(); }
		else if ( this.markDeletedCodeMetaobjectAnnotation != null ) { return this.markDeletedCodeMetaobjectAnnotation.getFirstSymbol(); }
		else {
			return pushCompilationContextMetaobjectAnnotation.getFirstSymbol();
		}

	}


	public Expr getExpr() {
		return expr;
	}



	private AnnotationAt regularMetaobjectAnnotation, markDeletedCodeMetaobjectAnnotation, pushCompilationContextMetaobjectAnnotation, popCompilationContextMetaobjectAnnotation;
	private Expr expr;
}
