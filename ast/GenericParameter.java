/**
 *
 */
package ast;

import meta.MetaHelper;
import meta.WrGenericParameter;
import saci.CyanEnv;
import saci.Env;
import saci.TupleTwo;

/** Represents a generic parameter such as T in
 *  object Stack<T>
 *     ...
 *  end
 *
 *  There are three ways of declaring a generic parameter:
 *  1. as the above.  T  should be replaced by real parameters;
 *
 *  2. by a lower-case symbol as in
 *      object Proto<main>
 *         ...
 *      end
 *  3. as a real prototype as in
 *       object Stack<Int>
 *          ...
 *       end
 *     This declares a Stack specific for type Int. In this case, method
 *     isRealPrototype returns true. The prototype may be preceded by a package
 *
 * @author José
 *
 */
public class GenericParameter implements ASTNode {


	public enum GenericParameterKind {
		   //  Stack<main.Person> or Stack<Tuple<main.Person>>
		PrototypeWithPackage,
		   // Stack<Int>  or Stack<cyan.lang.Int> or Stack<cyan.lang.Tuple<Int>>
        PrototypeCyanLang,
           // Inter<add>
        LowerCaseSymbol,
           // Stack<T>
        FormalParameter
	}

	public GenericParameter(Expr parameter, GenericParameterKind kind) {
		this.parameter = parameter;
		this.kind = kind;
		plus = false;
		fullName = null;
	}

	@Override
	public WrGenericParameter getI() {
		if ( iGenericParamete == null ) {
			iGenericParamete = new WrGenericParameter(this);
		}
		return iGenericParamete;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		parameter.accept(visitor);
		visitor.visit(this);
	}



	/** this method should only be called if the generic parameter is a real parameter
	 * as "Int" in "Stack<Int>" or "Tuple<Int, String>" in "Stack<Tuple<Int, String>>".
	 * This method returns the name of the source file in which the generic parameter is.
	 * It this object represents "Int", it returns "Int". If it represents "Tuple<Int, String>",
	 * it returns "Tuple(Int,String)".
	 *
	 * All strings of the form "cyan.lang." are removed from the name.
	 * @return
	 */
	public String getNameSourceFile() {
		String s = getName();


		String sourceFileName = "";
		int i = 0;
		while ( i < s.length() ) {
			char ch = s.charAt(i);
			if ( ch == '<' )
				sourceFileName = sourceFileName + '(';
			else if ( ch == '>' )
				sourceFileName = sourceFileName + ')';
			else if ( ! Character.isWhitespace(ch) )
				sourceFileName = sourceFileName + ch;
			++i;
		}
		return sourceFileName.replace(MetaHelper.cyanLanguagePackageName + ".", "");


	}

	/**
	 * In Stack<T>, returns "T". In Stack<Int>, returns "Int". In Stack< Tuple<Int, String>>,
	 * returns "Tuple<Int,String>", no spaces after ",".
	 * @return
	 */
	public String getName() {
		/*
		if ( isRealPrototype ) {
			String name = realPrototype.ifPrototypeReturnsItsName();
			if ( name == null )
				throw new RuntimeException("Internal error in GenericParameter::getName: real prototype is not a type");
			return name;
		}
		else */
		String s = parameter.asString();
		String ret = "";
		int size = s.length();
		for (int i = 0; i < size; ++i) {
			char ch = s.charAt(i);
			if ( ! Character.isWhitespace(ch) )
				ret += ch;
		}
		return ret;
	}



	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		pw.print(" ");
		parameter.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);

/*		if ( isRealPrototype ) {
			realPrototype.genCyan(pw, cyanEnv);
		}
		else {
			if ( parameterType != null )
				parameterType.genCyan(pw, false, cyanEnv);
			pw.print(" ");
			parameter.genCyan(pw, cyanEnv);
		} */
	}

	public boolean isRealPrototype() {
		return kind == GenericParameterKind.LowerCaseSymbol ||
			   kind == GenericParameterKind.PrototypeCyanLang ||
			   kind == GenericParameterKind.PrototypeWithPackage;
	}


	public void calcInternalTypes(Env env) {

		if ( kind == GenericParameterKind.PrototypeCyanLang ||
				kind == GenericParameterKind.PrototypeWithPackage )
			parameter.calcInternalTypes(env);

	}

	public GenericParameterKind getKind() {
		return kind;
	}


	public boolean getPlus() {
		return plus;
	}

	public void setPlus(boolean plus) {
		this.plus = plus;
	}


	public Expr getParameter() {
		return parameter;
	}

	public String getFullName(Env env) {

		if ( fullName == null ) {
			if ( kind == GenericParameterKind.LowerCaseSymbol ||  kind == GenericParameterKind.FormalParameter ) {
				if ( parameter instanceof ExprIdentStar ) {
					fullName = ((ExprIdentStar) parameter).getName();
				}
				else {
					env.error(parameter.getFirstSymbol(), "Internal error: parameter starts with a lower-case or parameter is a formal one. But it is not instance of ExprIdentStar");
					return null;
				}
			}
			else {
				TupleTwo<String, Type> t = parameter.ifPrototypeReturnsNameWithPackageAndType(env);
				if ( t == null || t.f2 == null ) {
					Type pu = null;
					if ( kind == GenericParameterKind.PrototypeWithPackage ) {
						// this should always be true
						String str = parameter.asString();
						pu = saci.Compiler.singleTypeFromString(str, parameter.getFirstSymbol(), "Prototype '" +
								str + "' was not found", env.getCurrentCompilationUnit(), env.getCurrentPrototype(), env);
					}
					if ( pu == null ) {
						t = parameter.ifPrototypeReturnsNameWithPackageAndType(env);
						env.error( parameter.getFirstSymbol(), "Prototype '" + parameter.asString() + "' was not found" );
						return null;
					}
					else {
						fullName = pu.getFullName();
					}
				}
				else {
					fullName = t.f1;
				}
			}
		}
		return fullName;

		/*
		Type t = parameter.getType(env);
		if ( ! (t.getInsideType() instanceof Prototype) ) {
			env.error(parameter.getFirstSymbol(), "Type of the parameter should be a Cyan prototype");
		}
		Prototype paramType = (Prototype ) t.getInsideType();
		return paramType.getFullName();
		*/
	}



	/**
	 * the symbol of the parameter such as T in the example above.
	 */
	private Expr parameter;



	private GenericParameterKind kind;
	/**
	 * true if this generic parameter is followed by + in the declaration of the generic prototype
	 * as in
	 *    object Union<T+> ... end
	 */
	private boolean plus;

	/**
	 * the full type name of the parameter. It may be something like<br>
	 * {@code Function<main.Person, Int}
	 */
	private String fullName;

	private WrGenericParameter iGenericParamete = null;

}
