/**
   Represents a method signature of a unary method such as
          fun getName -> String
          fun set

   The method name cannot be an operator. For operators, use class
   MethodSignatureOperator instead.

 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.GetHiddenItem;
import meta.MessageKeywordComplexName;
import meta.MetaHelper;
import meta.MethodComplexName;
import meta.WrMethodDec;
import meta.WrMethodSignatureUnary;
import saci.CyanEnv;
import saci.Env;

/**
 * @author José
 *
 */
public class MethodSignatureUnary extends MethodSignature {

	public MethodSignatureUnary(Symbol methodSymbol, MethodDec method) {
		super(method);
		this.methodSymbol = methodSymbol;
	}

	public MethodSignatureUnary(Symbol symbol, WrMethodDec currentMethod) {
		this(symbol, GetHiddenItem.getHiddenMethodDec(currentMethod));
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.preVisit(this);

		visitor.visit(this);
	}

	public String getMethodName() {
		return methodSymbol.getSymbolString();
	}

	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);
		super.calcInterfaceTypes(env);
	}


	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			pw.print(cyanEnv.formalGenericParamToRealParam(methodSymbol.getSymbolString()));
		}
		else {
			String unaryMethod = methodSymbol.getSymbolString();
//			if ( cyanEnv.getGenInterfacesForCompiledCode() &&
//					unaryMethod.equals("init") ) {
//				pw.print("new");
//			}
//			else {
				pw.print(unaryMethod);
//			}
		}


		super.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}

	@Override
	public void genJava(PWInterface pw, Env env, boolean isMultiMethod) {
		super.genJava(pw, env, isMultiMethod);
		pw.print(MetaHelper.getJavaNameOfkeyword(methodSymbol.getSymbolString()));
		pw.print("()");
	}

	@Override
	public void genJava(PWInterface pw, Env env) {
		genJava(pw, env, false);
	}

	@Override
	public void genJavaAsConstructor(PWInterface pw, Env env, String javaNameDeclaringObject) {
		pw.printIdent( javaNameDeclaringObject + "()");
	}


	@Override
	public String getSingleParameterType() {
		return MetaHelper.getJavaName("Nil");
	}

	@Override
	public String getFullName(Env env) {
		return fullName = getNameWithoutParamNumber();
	}

	@Override
	public String getFullName() {
		return fullName = getNameWithoutParamNumber();
	}



	@Override
	public String getNameWithoutParamNumber() {
		return methodSymbol.getSymbolString();
	}


	@Override
	public MethodComplexName getNameParametersTypes() {
		MethodComplexName mc = new MethodComplexName();

		MessageKeywordComplexName sc = new MessageKeywordComplexName();
		mc.messageKeywordArray = new ArrayList<>();
		mc.messageKeywordArray.add(sc);
		sc.keyword = this.methodSymbol.getSymbolString();
		sc.paramList = null;
		if ( this.getReturnTypeExpr() == null ) {
			mc.returnType = "Nil";
		}
		else {
			mc.returnType = this.getReturnTypeExpr().asString();
		}
		return mc;
	}




	@Override
	public String getPrototypeNameForMethod() {
		return getNameWithoutParamNumber() + "_un";
	}

	@Override
	public String getSuperprototypeNameForMethod() {
		String s = "UFunction";
		if ( this.getReturnTypeExpr() == null )
			s += "<Nil>";
		else
			s += "<" + this.getReturnTypeExpr().ifPrototypeReturnsItsName() + ">";
		return s;
	}


	@Override
	public void genCyanEvalMethodSignature(StringBuffer s) {
		String ret;
		if ( this.getReturnTypeExpr() == null )
			ret = "Nil";
		else
			ret = this.getReturnTypeExpr().ifPrototypeReturnsItsName();
		s.append("eval -> " + ret);
	}



	@Override
	public Symbol getFirstSymbol() {
		return methodSymbol;
	}


	@Override
	public List<ParameterDec> getParameterList() {
		List<ParameterDec> paramList = new ArrayList<>();
		return paramList;
	}


	@Override
	public String getFunctionName() {
		String s = "Function<";
		if ( this.getReturnTypeExpr() != null && this.getReturnTypeExpr().getType() != null ) {
			s += this.getReturnTypeExpr().getType().getFullName() + ">";
		}
		else {
			s += "Nil>";
		}
		return s;
	}


	@Override
	public String getFunctionNameWithSelf(String fullName2) {
		String s = "Function<" + fullName2 + ", ";

		if ( this.getReturnTypeExpr() != null && this.getReturnTypeExpr().getType() != null ) {
			s += this.getReturnTypeExpr().getType().getFullName() + ">";
		}
		else {
			s += "Nil>";
		}
		return s;
	}

	@Override
	public String getSignatureWithoutReturnType() {
		return methodSymbol.getSymbolString();
	}




	@Override
	public String getNameWithDeclaredTypes() {
		if ( this.returnTypeExpr != null ) {
			return this.methodSymbol.getSymbolString() + " -> " + this.returnTypeExpr.asString();
		}
		else {
			return this.methodSymbol.getSymbolString();
		}
	}

	@Override
	public String getNameWithParamAndTypes() {
		return this.getNameWithDeclaredTypes();
	}



	@Override
	public WrMethodSignatureUnary getI() {
		if ( iMethodSignatureUnary == null ) {
			iMethodSignatureUnary = new WrMethodSignatureUnary(this);
		}
		return iMethodSignatureUnary;
	}

	private WrMethodSignatureUnary iMethodSignatureUnary;
	/**
	 * name of the unary method, such as getName, set, ++, or !
	 */
	private Symbol methodSymbol;


	@Override
	public String getJavaName() {
		return MetaHelper.getJavaName(methodSymbol.getSymbolString());
	}



}
