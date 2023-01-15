/**
   Represents the method signature of a non-grammar method that is not a
   unary method. That is, a regular method such as
          fun width: (:w int)  height: (:h int) -> Rectangle [ ... ]
   or
          fun at: (:index int) put:  (:value String) [ ... ]

 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.MessageKeywordComplexName;
import meta.MetaHelper;
import meta.MethodComplexName;
import meta.ParameterComplexName;
import meta.WrMethodSignatureWithKeywords;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
/**
 * @author José
 *
 */
public class MethodSignatureWithKeywords extends MethodSignature {


	public MethodSignatureWithKeywords(
			   List<MethodKeywordWithParameters> keywordArray,
			   boolean indexingMethod, MethodDec method) {
		super(method);
		this.keywordArray = keywordArray;
		this.indexingMethod = indexingMethod;
	}

	/*
	public MethodSignatureWithKeywords(
			List<MethodKeywordWithParameters> keywordList,
			boolean indexingMethod2, WrMethodDec currentMethod) {
		this(keywordList, indexingMethod2, GetHiddenItem.getHiddenMethodDec(currentMethod));
	}
	*/

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.preVisit(this);

		for ( MethodKeywordWithParameters sel : keywordArray ) {
			sel.accept(visitor);
		}
		visitor.visit(this);
	}


	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( printInMoreThanOneLine ) {
			// super.genCyan(pw, printInMoreThanOneLine, cyanEnv);
			int size = keywordArray.size();
			if ( indexingMethod )
				pw.print(" [] ");
			for ( MethodKeywordWithParameters s : keywordArray ) {
				s.genCyan(pw, PWCounter.printInMoreThanOneLine(s), cyanEnv, genFunctions );
				if ( --size > 0 ) { pw.println(""); pw.printIdent("    "); }
			}
		}
		else {
			// super.genCyan(pw, false, cyanEnv);
			if ( indexingMethod )
				pw.print(" [] ");
			for ( MethodKeywordWithParameters s : keywordArray )
				s.genCyan(pw, false, cyanEnv, genFunctions);
		}
		super.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}

	/**
	 * 	In the general case:
	 *         keywordName + ("_p" + typeName)* +
	 *         ("_s" + keywordName + ("_p" + typeName)* )+
	 *
	 */

	@Override
	public String getJavaName() {
		if ( javaName == null ) {
			/*
			int size = 0;
			javaName = "";
			for ( MethodKeywordWithParameters keyword : keywordArray ) {
				if ( size > 0 )
					javaName = javaName + "_s_";
				javaName = javaName + keyword.getJavaName();
				size++;
			}
			*/
			javaName = NameServer.getJavaNameOfMethod(keywordArray);

		}
		return javaName;
	}



	@Override
	public void genJava(PWInterface pw, Env env, boolean isMultiMethod) {

		super.genJava(pw, env, isMultiMethod);
		pw.print(" ");
		String javaName1 = getJavaName();

		if ( isMultiMethod ) {
			/*
			 * a method that is part of a multi-method. The method name is changed to use the parameter types.
			 * The methods are transformed into private methods whose names end with "__"
			 */
			StringBuffer methodJavaName = new StringBuffer(javaName1);
			int size = keywordArray.size();
			for ( MethodKeywordWithParameters s : keywordArray ) {
				List<ParameterDec> parameterList = s.getParameterList();
				int size2 = parameterList.size();
				for ( ParameterDec paramDec : parameterList ) {
					methodJavaName.append(paramDec.getType().getJavaName().replace('.', '_'));
					if ( --size2 > 0 )
						methodJavaName.append("_");
				}
				if ( --size > 0 )
					methodJavaName.append("_s_");
			}
			this.javaNameMultiMethod = methodJavaName.toString();
			pw.print(this.javaNameMultiMethod);
		}
		else {
			pw.print(javaName1);
		}
		pw.print("( ");

		List<ParameterDec> paramDecList = new ArrayList<>();
		for ( MethodKeywordWithParameters keyword : keywordArray )  {
			for ( ParameterDec paramDec : keyword.getParameterList()) {
				paramDecList.add(paramDec);
			}
		}
		int size2 = paramDecList.size();
		for ( ParameterDec p : paramDecList ) {
			p.genJava(pw, env);
			if ( --size2 > 0 )
				pw.print(", ");
		}
		/*
		int size = keywordArray.size();

		int i = 0;
		boolean atLeastOneParameter = false;
		for ( MethodKeywordWithParameters s : keywordArray ) {
			s.genJava(pw, env);
			if ( s.getParameterList().size() > 0 )
				atLeastOneParameter = true;
			if ( --size > 0 && atLeastOneParameter  && keywordArray.get(i+1).getParameterList().size() > 0 )
				pw.print(", ");
			++i;
		}

		*/

		pw.print(" ) ");
	}

	@Override
	public void genJavaAsConstructor(PWInterface pw, Env env, String javaNameDeclaringObject) {
		pw.printIdent( javaNameDeclaringObject + "(");
		int size = keywordArray.size();
		for ( MethodKeywordWithParameters s : keywordArray) {
			List<ParameterDec> parameterList = s.getParameterList();
			int size2 = parameterList.size();
			for ( ParameterDec paramDec : parameterList ) {
				paramDec.genJava(pw, env);
				if ( --size2 > 0 )
					pw.print(", ");
			}
			if ( --size > 0 )
				pw.print(", ");
		}

		pw.print(") ");

	}

	/**
	 * generate the signature of the method that will replace all multi-methods in
	 * a prototype
	   @param pw
	   @param env
	 */
	public void genJavaOverloadMethod(PWInterface pw, Env env) {

		super.genJava(pw, env, false);
		pw.print(" ");
		pw.print(getJavaName() + "( ");

		/*
		int size = keywordArray.size();
		for ( MethodKeywordWithParameters s : keywordArray ) {
			List<ParameterDec> parameterList = s.getParameterList();
			int size2 = parameterList.size();
			for ( ParameterDec paramDec : parameterList ) {
				paramDec.genJavaForMultiMethod(pw);
				if ( --size2 > 0 )
					pw.print(", ");
			}
			if ( --size > 0 )
				pw.print(", ");
		}
		*/

		List<ParameterDec> paramDecList = new ArrayList<>();
		for ( MethodKeywordWithParameters keyword : keywordArray )  {
			for ( ParameterDec paramDec : keyword.getParameterList()) {
				paramDecList.add(paramDec);
			}
		}
		int size2 = paramDecList.size();
		for ( ParameterDec p : paramDecList ) {
			p.genJavaForMultiMethod(pw);
			if ( --size2 > 0 )
				pw.print(", ");
		}



		pw.print(" ) ");
	}

	@Override
	public String getFullName(Env env) {
		if ( fullName == null ) {
			String name = "";
			int size = keywordArray.size();
			for ( MethodKeywordWithParameters s : keywordArray ) {
				name = name + s.getFullName(env);
				if ( --size > 0 )
					name = name + " ";
			}
			fullName = name;
		}
		return fullName;
	}

	@Override
	public String getFullName() {
		if ( fullName == null ) {
			String name = "";
			int size = keywordArray.size();
			for ( MethodKeywordWithParameters s : keywordArray ) {
				name = name + s.getFullName();
				if ( --size > 0 )
					name = name + " ";
			}
			fullName = name;
		}
		return fullName;
	}


	@Override
	public String getSignatureWithoutReturnType() {
		String name = "";
		int size = keywordArray.size();
		for ( MethodKeywordWithParameters s : keywordArray ) {
			name = name + s.getName() + " ";
			int size2 = s.getParameterList().size();
			for ( ParameterDec param : s.getParameterList() ) {
				Expr typeInDec = param.getTypeInDec();
				if ( typeInDec == null ) {
					name += Type.Dyn.getName();
				}
				else {
					name += param.getTypeInDec().asString();
				}
				if ( --size2 > 0 ) {
					name += ", ";
				}
			}

			if ( --size > 0 )
				name = name + " ";
		}
		return name;
	}



	/**
	 * return the names of all keywords concatenated
	 */
	@Override
	public String getNameWithoutParamNumber() {
		String name = "";
		for ( MethodKeywordWithParameters s : keywordArray )
			name = name + s.getName();
		return name;
	}

	/**
	 * return the names of all keywords plus its number of parameters concatenated.
	 * That is, the return for method<br>
	 * <code>with: Int n, Char ch plus: Float f</code><br>
	 * would be <code>with:2 plus:1</code>
	 */
	@Override
	public String getName() {
		String name = "";
		int size = keywordArray.size();
		for ( MethodKeywordWithParameters s : keywordArray ) {
			name = name + s.getName() + s.getParameterList().size();
			if ( --size > 0 )
				name += " ";
		}
		return name;
	}

	@Override
	public String getNameWithDeclaredTypes() {

		String name = "";
		int size = keywordArray.size();
		for ( MethodKeywordWithParameters s : keywordArray ) {
			int sizep = s.getParameterList().size();
			name += s.getkeyword().getSymbolString();
			if ( sizep > 0 || size > 0 ) {
				name += " ";
			}
			if ( s.getParameterList() != null ) {
				for (ParameterDec p : s.getParameterList() ) {
					if ( p.getTypeInDec() != null ) {
						name += p.getTypeInDec().asString();
					}
					if ( --sizep > 0 ) {
						name += ", ";
					}
				}
			}
			if ( --size > 0 )
				name += " ";
		}

		if ( this.getReturnTypeExpr() != null ) {
			name += " -> " + this.getReturnTypeExpr().asString();
		}
		return name;
	}

	@Override
	public String getNameWithParamAndTypes() {


		String name = "";
		int size = keywordArray.size();
		for ( MethodKeywordWithParameters s : keywordArray ) {
			int sizep = s.getParameterList().size();
			name += s.getkeyword().getSymbolString();
			if ( sizep > 0 || size > 0 ) {
				name += " ";
			}
			if ( s.getParameterList() != null ) {
				for (ParameterDec p : s.getParameterList() ) {
					if ( p.getTypeInDec() != null ) {
						name += p.getTypeInDec().asString();
					}
					name += " " + p.getName();
					if ( --sizep > 0 ) {
						name += ", ";
					}
				}
			}
			if ( --size > 0 )
				name += " ";
		}

		if ( this.getReturnTypeExpr() != null ) {
			name += " -> " + this.getReturnTypeExpr().asString();
		}
		return name;
	}



	@Override
	public MethodComplexName getNameParametersTypes() {
		MethodComplexName mc = new MethodComplexName();

		mc.messageKeywordArray = new ArrayList<>();

		for( MethodKeywordWithParameters selec : this.keywordArray ) {
			MessageKeywordComplexName sc = new MessageKeywordComplexName();
			sc.keyword = selec.getName();
			if ( selec.getParameterList() != null ) {
				sc.paramList = new ArrayList<>();
				for ( ParameterDec param : selec.getParameterList() ) {
					ParameterComplexName pc = new ParameterComplexName();
					pc.name = param.getName();
					pc.type = param.getTypeInDec() == null ? null : param.getTypeInDec().asString();
					sc.paramList.add(pc);
				}
			}
			mc.messageKeywordArray.add(sc);
		}
		if ( this.getReturnTypeExpr() != null ) {
			mc.returnType = this.getReturnTypeExpr().asString();
		}
		return mc;
	}



	@Override
	public String getPrototypeNameForMethod() {
		String name = "";
		for ( MethodKeywordWithParameters s : keywordArray ) {
			String p = s.getName();
			if ( p.endsWith(":") )
				p = p.substring(0, p.length() - 1);
			int sizep = s.getParameterList().size();
			for ( ParameterDec param : s.getParameterList() ) {
				String fullName1;
				if ( param.getType() != null )
					fullName1 = MetaHelper.getJavaName(param.getType().getFullName());
				else if ( param.getTypeInDec() != null )
					// fullName = NameServer.getJavaNameGenericPrototype(param.getTypeInDec().asString());
					fullName1 = MetaHelper.getJavaName(param.getTypeInDec().asStringToCreateJavaName());
				else
					fullName1 = MetaHelper.dynName;

				p = p + fullName1;
				if ( --sizep > 0 )
					p = p + "_p_";
			}
			name = name + p + "_dot_";
		}
		return name;
	}

	@Override
	public String getSuperprototypeNameForMethod() {
		String s = "UFunction";
		int size = keywordArray.size();
		for ( MethodKeywordWithParameters keyword : keywordArray ) {
			s += "<";
			int sizeParamList = keyword.getParameterList().size();
			for ( ParameterDec parameter : keyword.getParameterList() ) {
				String paramType;
				if ( parameter.getTypeInDec() == null )
					paramType = MetaHelper.dynName;
				else
					paramType = parameter.getTypeInDec().ifPrototypeReturnsItsName();
				s += paramType;
				if ( --sizeParamList > 0 )
					s += ", ";
			}
			if ( --size > 0 )
				s += ">";
		}
		String ret;
		if ( this.getReturnTypeExpr() == null )
			ret = "Nil";
		else
			ret = this.getReturnTypeExpr().ifPrototypeReturnsItsName();
		// the return type is added to the last set of '< >' as in
		//    UFunction<Int, String><Char, Int><Boolean, Int, ReturnType>
		size = keywordArray.size();
		if ( size > 0 && keywordArray.get(size - 1).getParameterList().size() > 0 )
			s = s + ", ";
		return s + ret + ">";
	}

	@Override
	public void genCyanEvalMethodSignature(StringBuffer s) {
		for ( MethodKeywordWithParameters keyword : keywordArray ) {
			s.append("eval: ");
			int size = keyword.getParameterList().size();
			for ( ParameterDec parameter : keyword.getParameterList() ) {

				s.append( parameter.getTypeInDec().ifPrototypeReturnsItsName() );
				if ( parameter.getName() != null )
					s.append(" " + parameter.getName());
				if ( --size > 0 )
					s.append(", ");
			}
			s.append(" ");
		}
		if ( this.getReturnTypeExpr() != null ) {
			s.append("-> " + this.getReturnTypeExpr().ifPrototypeReturnsItsName() );

		}
	}



	public void setkeywordArray(List<MethodKeywordWithParameters> keywordArray) {
		this.keywordArray = keywordArray;
	}

	public List<MethodKeywordWithParameters> getKeywordArray() {
		return keywordArray;
	}

	@Override
	public String getSingleParameterType() {
		String s = "";
		int numParam = 0;
		for ( MethodKeywordWithParameters p : keywordArray ) {
			int numParamkeyword = p.getParameterList().size();
			if ( numParamkeyword > 0 ) {
				numParam += numParamkeyword;
				s = p.getParameterList().get(0).getTypeInDec().getJavaName();
			}
		}
		if ( numParam == 0 ) {
			return "Nil";
		}
		else if ( numParam == 1 ) {
			return s;
		}
		else {
			s = "UTuple<";
			for ( MethodKeywordWithParameters keywordWithParameters : keywordArray ) {
				for ( ParameterDec paramDec : keywordWithParameters.getParameterList() ) {
					s = s + paramDec.getTypeInDec().getJavaName();
					if ( --numParam > 0 )
						s = s + ", ";
				}
			}
			return s + ">";
		}
	}


	@Override
	public void check(Env env) {
		super.check(env);
		for ( MethodKeywordWithParameters keyword : keywordArray ) {
			for ( ParameterDec parameterDec : keyword.getParameterList() ) {
				if ( env.searchLocalVariableParameter(parameterDec.getName()) != null )
					env.error(parameterDec.getFirstSymbol(), "Parameter " +
							parameterDec.getName() + " is being redeclared", true, true);
				env.pushVariableDec(parameterDec);
			}
		}
	}


	@Override
	public Symbol getFirstSymbol() {
		return keywordArray.get(0).getkeyword();
	}



	@Override
	public void calcInterfaceTypes(Env env) {

		if ( this.hasCalculatedInterfaceTypes )
			return ;

		super.calcInterfaceTypes(env);
		for ( MethodKeywordWithParameters keyword : keywordArray )
			keyword.calcInternalTypes(env);
		hasCalculatedInterfaceTypes = true;

	}

	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);

		for ( MethodKeywordWithParameters keyword : keywordArray )
			for ( ParameterDec parameterDec : keyword.getParameterList() )
				env.pushVariableDec(parameterDec);
	}

	@Override
	public boolean isIndexingMethod() {
		return indexingMethod;
	}


	@Override
	public List<ParameterDec> getParameterList() {
		List<ParameterDec> paramList = new ArrayList<>();
		for ( MethodKeywordWithParameters s : keywordArray ) {
			for ( ParameterDec p : s.getParameterList() ) {
				paramList.add(p);
			}
		}
		return paramList;
	}


	@Override
	public String getFunctionName() {
		String s = "Function";
		int size = this.keywordArray.size();
		for ( MethodKeywordWithParameters sel : this.keywordArray ) {
			s += "<";
			int sizeP = sel.getParameterList().size();
			if ( sizeP > 0 ) {
				for ( ParameterDec param : sel.getParameterList() ) {
					s += param.getType().getFullName();
					if ( --sizeP > 0 ) {
						s += ", ";
					}
				}
			}
			else {
				s += "none";
			}
			if ( --size > 0 ) {
				s += ">";
			}
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
	public String getFunctionNameWithSelf(String fullNameReceiver) {

		String s = "Function";
		String r = "Function<" + fullNameReceiver + ">";
		int size = this.keywordArray.size();
		int count = 0;

		List<List<String>> typeListList = new ArrayList<>();
		List<String> typeList = new ArrayList<>();
		typeList.add(fullNameReceiver);
		typeListList.add(typeList);
		for ( MethodKeywordWithParameters sel : this.keywordArray ) {
			typeList = new ArrayList<>();
			typeListList.add(typeList);
			int sizeP = sel.getParameterList().size();
			r += "<";
			if ( sizeP > 0 ) {

				for ( ParameterDec param : sel.getParameterList() ) {
					typeList.add( param.getType().getFullName() );
					r += param.getType().getFullName();
					if ( --sizeP > 0 )
						r += ", ";
				}
			}
			else  { // && count < size - 1) || (this.keywordArray.size() == 1) ) {
				typeList.add(MetaHelper.noneArgumentNameForFunctions);
				r += MetaHelper.noneArgumentNameForFunctions;
			}

			if ( count == size - 1 ) {
				// last, insert the return type
				if ( this.getReturnTypeExpr() != null && this.getReturnTypeExpr().getType() != null ) {
					typeList.add( this.getReturnTypeExpr().getType().getFullName() );
					r += ", " + this.getReturnTypeExpr().getType().getFullName();
				}
				else {
					typeList.add( "Nil" );
					r += "Nil";
				}

			}
			++count;
			r += ">";
		}

		for ( List<String> strList : typeListList ) {
			int sizeSL = strList.size();
			s += "<";
			for ( String str : strList ) {
				s += str;
				if ( --sizeSL > 0 ) {
					s += ", ";
				}
			}
			s += ">";
		}
		assert r.equals(s);
		return s;

	}



	@Override
	public WrMethodSignatureWithKeywords getI() {
		if ( iMethodSignatureWithKeywords == null ) {
			iMethodSignatureWithKeywords = new WrMethodSignatureWithKeywords(this);
		}
		return iMethodSignatureWithKeywords;
	}

	private WrMethodSignatureWithKeywords iMethodSignatureWithKeywords = null;

	private List<MethodKeywordWithParameters>  keywordArray;

	/**
	 * true if there is a "[]" before the keywords of this method. After
	 * "[]" there should appear only keyword "at:" or "at: ... put: ..."
	 */
	private boolean indexingMethod;


	private String javaNameMultiMethod;


	public String getJavaNameOverloadMethod() {
		return javaNameMultiMethod;
	}




}
