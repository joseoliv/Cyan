/**
 * Represents a method signature whose name is an operator such as
 *      fun ++  [ ... ]
 *      fun + (:other int) [ ... ]
 *      fun + (:other int) -> int [ ... ]
 *      fun ! -> int [ ... ]
 *
 *  the method may be unary such as ! and ++ in the above examples.

 */
package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.GetHiddenItem;
import meta.MessageKeywordComplexName;
import meta.MetaHelper;
import meta.MethodComplexName;
import meta.ParameterComplexName;
import meta.WrMethodDec;
import meta.WrMethodSignatureOperator;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 * @author José
 *
 */
public class MethodSignatureOperator extends MethodSignature {

	public MethodSignatureOperator(Symbol symbolOperator, MethodDec method) {
		super(method);
		this.symbolOperator = symbolOperator;
		parameter = null;
	}

	public MethodSignatureOperator(Symbol symbol, WrMethodDec currentMethod) {
		this(symbol, GetHiddenItem.getHiddenMethodDec(currentMethod));
	}


	@Override
	public void accept(ASTVisitor visitor) {
		visitor.preVisit(this);

		visitor.visit(this);
	}


	public void setOptionalParameter(ParameterDec optionalParameter) {
		this.parameter = optionalParameter;
	}

	public ParameterDec getOptionalParameter() {
		return parameter;
	}

	public void setSymbolOperator(Symbol symbolOperator) {
		this.symbolOperator = symbolOperator;
	}

	public Symbol getSymbolOperator() {
		return symbolOperator;
	}


	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {


		pw.print(symbolOperator.getSymbolString() + " ");
		if ( parameter != null )
			parameter.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		super.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}



	@Override
	public void genJava(PWInterface pw, Env env, boolean isMultiMethod) {
		super.genJava(pw, env, isMultiMethod);
		javaNameMultiMethod = MetaHelper.alphaName(symbolOperator.getSymbolString());
		if ( parameter != null ) {
			if ( isMultiMethod ) {
				this.javaNameMultiMethod = javaNameMultiMethod + "_" + parameter.getType().getJavaName();
			}
		}
		pw.print(" " + javaNameMultiMethod + "(");

		if ( parameter != null ) {
			env.pushVariableDec(parameter);


			/*
			 * 			if ( this.getMethod() != null && this.getMethod().getMultiMethod() ) {
				pw.print("Object");
			}
			else {
				pw.print(parameter.getType().getJavaName());
			}
			 */
			pw.print(parameter.getType().getJavaName());

			pw.print(" " + MetaHelper.getJavaName(parameter.getName()));
		}
		pw.print(")");

	}

	/**
	 * generate the signature of the method that will replace all multi-methods in
	 * a prototype
	   @param pw
	   @param env
	 */
	public void genJavaOverloadMethod(PWInterface pw, Env env) {
		super.genJava(pw, env, false);

		pw.print(" " + MetaHelper.alphaName(symbolOperator.getSymbolString()) + "(");
		if ( parameter != null ) {
			env.pushVariableDec(parameter);

			pw.print("Object " + MetaHelper.getJavaName(parameter.getName()));
		}
		pw.print(")");

	}


	@Override
	public String getSingleParameterType() {
		if ( parameter != null )
			return parameter.getTypeInDec().getFirstSymbol().getSymbolString();
		else
			return "Nil";
	}

	@Override
	public String getFullName(Env env) {
		if ( fullName == null ) {
			if ( parameter != null ) {
				fullName = getNameWithoutParamNumber() + " " + parameter.getType(env).getFullName(env);
			}
			else
				fullName = getNameWithoutParamNumber();
		}
		return fullName;
	}

	@Override
	public String getFullName() {
		if ( fullName == null ) {
			if ( parameter != null ) {
				fullName = getNameWithoutParamNumber() + " " + parameter.getType().getFullName();
			}
			else
				fullName = getNameWithoutParamNumber();
		}
		return fullName;
	}



	@Override
	public String getName() {
		String ret = symbolOperator.getSymbolString();
		if ( parameter != null )
			ret = ret + "1";
		return ret;
	}

	@Override
	public String getNameWithoutParamNumber() {
		return symbolOperator.getSymbolString();
	}

	@Override
	public MethodComplexName getNameParametersTypes() {
		ParameterComplexName pc = new ParameterComplexName();
		if ( parameter != null ) {
			pc.name = parameter.getName();
			if ( parameter.getTypeInDec() != null ) {
				pc.type = parameter.getTypeInDec().asString();
			}
		}
		MethodComplexName mc = new MethodComplexName();

		MessageKeywordComplexName sc = new MessageKeywordComplexName();
		mc.messageKeywordArray = new ArrayList<>();
		mc.messageKeywordArray.add(sc);
		sc.keyword = this.symbolOperator.getSymbolString();
		sc.paramList = new ArrayList<>();
		sc.paramList.add(pc);

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
		String typeName;

		if ( parameter == null ) {
			typeName = "_un";
		}
		else if ( parameter.getType() != null ) {
			typeName = MetaHelper.getJavaName(parameter.getType().getFullName());
		}
		else if ( parameter.getTypeInDec() != null ) {
			typeName = MetaHelper.getJavaName(parameter.getTypeInDec().asString());
		}
		else
			typeName = MetaHelper.dynName;
		return MetaHelper.alphaName(symbolOperator.token.toString()) + typeName;
	}


	@Override
	public String getSuperprototypeNameForMethod() {
		String s = "Function<";
		String ret;
		if ( this.getReturnTypeExpr() == null )
			ret = "Nil";
		else
			ret = this.getReturnTypeExpr().ifPrototypeReturnsItsName();

		if ( parameter != null ) {
			String paramType;
			if ( parameter.getTypeInDec() == null )
				paramType = MetaHelper.dynName;
			else
				paramType = parameter.getTypeInDec().ifPrototypeReturnsItsName();
			s += paramType + ", ";
		}
		s += ret + ">";
		return s;
	}


	@Override
	public void genCyanEvalMethodSignature(StringBuffer s) {
		String ret = "Nil";
		if ( this.getReturnTypeExpr() != null )
			ret = this.getReturnTypeExpr().ifPrototypeReturnsItsName();
		if ( parameter == null ) {
			// unary method
			s.append("eval -> " + ret);
		}
		else {
			   // binary method
			s.append( "eval: " + parameter.getTypeInDec().ifPrototypeReturnsItsName() +
					(parameter.getName() == null ? " " : (" " + parameter.getName())) +
					" -> " +
		              ret);
		}
	}



	@Override
	public void check(Env env) {
		super.check(env);
		if ( env.searchLocalVariableParameter(parameter.getName()) != null )
			env.error(parameter.getFirstSymbol(), "Parameter " +
					parameter.getName() + " is being redeclared", true, true);
		env.pushVariableDec(parameter);
	}

	@Override
	public Symbol getFirstSymbol() {
		return symbolOperator;
	}

	@Override
	public void calcInterfaceTypes(Env env) {

		if ( this.hasCalculatedInterfaceTypes )
			return ;

		super.calcInterfaceTypes(env);
		if ( parameter != null )
			parameter.calcInternalTypes(env);
		hasCalculatedInterfaceTypes = true;
	}

	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);
		if ( parameter != null )
			env.pushVariableDec(parameter);
	}


	@Override
	public List<ParameterDec> getParameterList() {
		List<ParameterDec> paramList = new ArrayList<>();
		if ( parameter != null )
			paramList.add(parameter);
		return paramList;
	}

	@Override
	public String getFunctionName() {
		String s = "Function<";
		if ( parameter != null ) {
			s += parameter.getType().getFullName() + ", ";
		}
		if ( this.getReturnTypeExpr() != null && this.getReturnTypeExpr().getType() != null ) {
			s += this.getReturnTypeExpr().getType().getFullName() + ">";
		}
		else {
			s += "Nil>";
		}
		return s;
	}


	@Override
	public String getFunctionNameWithSelf(String receiverType) {
		String s = "Function<" + receiverType;
		if ( parameter != null ) {
			s += "><";
			s += parameter.getType().getFullName();
		}
		s += ", ";
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
		String s = this.symbolOperator.getSymbolString();
		if ( parameter != null ) {
			Expr typeInDec = parameter.getTypeInDec();
			if ( typeInDec != null ) {
				s += parameter.getTypeInDec().asString();
			}
			else {
				s += "Dyn";
			}
		}
		return s;
	}

	@Override
	public String getNameWithDeclaredTypes() {
		String s = this.symbolOperator.getSymbolString();
		if ( this.parameter != null && this.parameter.getTypeInDec() != null ) {
			s += " " + this.parameter.getTypeInDec().asString();
		}
		if ( this.getReturnTypeExpr() != null ) {
			s += " -> " + this.getReturnTypeExpr().asString();
		}
		return s;
	}

	@Override
	public String getNameWithParamAndTypes() {
		String s = this.symbolOperator.getSymbolString();
		if ( this.parameter != null ) {
			if ( this.parameter.getTypeInDec() != null ) {
				s += " " + this.parameter.getTypeInDec().asString();
			}
			s += " " + parameter.getName();
		}
		if ( this.getReturnTypeExpr() != null ) {
			s += " -> " + this.getReturnTypeExpr().asString();
		}
		return s;
	}



	@Override
	public WrMethodSignatureOperator getI() {
		if ( iMethodSignatureOperator == null ) {
			iMethodSignatureOperator = new WrMethodSignatureOperator(this);
		}
		return iMethodSignatureOperator;
	}

	private WrMethodSignatureOperator iMethodSignatureOperator = null;

	/**
	 * parameter represents the optional parameter of the method.
	 * Unary methods do not take parameters such as ++ and ! in the examples
	 * above. Method + in the examples take one parameter that should
	 * be represented by this field. Note that
	 * a method whose name is an operator takes at most one parameter.
	 * That is, it is illegal to declare something like
	 *    fun + (:other int, :another int) [ ... ]
	 */
	private ParameterDec parameter;

	/**
	 * In
	 *     public fun + (:other int) -> int [ ... ]
	 *  symbolOperator is +
	 */
	private Symbol symbolOperator;

	@Override
	public String getJavaName() {
		return NameServer.getJavaMethodNameOfMessageSend(symbolOperator.getSymbolString());
	}


	public String getJavaNameOverloadMethod() {
		return javaNameMultiMethod;
	}

	private String javaNameMultiMethod;


}
