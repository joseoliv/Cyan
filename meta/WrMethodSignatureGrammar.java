/**
 *
 */
package meta;

import java.util.List;
import ast.ASTNode;
import meta.cyanLang.MessageKeywordGrammar;

/**
   Represents a method signature of a grammar method such as
          fun (add: Int)+  Array<Int> v [ ... ]
          fun (format: String  println: (String)+)) t [ ... ]


 * @author José
 *
 */
public class WrMethodSignatureGrammar extends WrMethodSignature  {

	public WrMethodSignatureGrammar(MessageKeywordGrammar keywordGrammar) {
		this.keywordGrammar = keywordGrammar;
		this.paramType = null;
		this.cyanName = null;
	}



	public void setkeywordGrammar(MessageKeywordGrammar keywordGrammar) {
		this.keywordGrammar = keywordGrammar;
	}

	public MessageKeywordGrammar getkeywordGrammar() {
		return keywordGrammar;
	}


	/*
	@Override
	public String getSingleParameterType() {
		return keywordGrammar.getStringType();
	}


	@Override
	public String getPrototypeNameForMethod() {
		String s = getNameWithoutParamNumber();
		String protoName = "";
		int i = 0;
		int size = s.length();
		while ( i < size ) {
			char ch = s.charAt(i);
			if ( ch == ' ' )
				protoName += "_";
			else {
				if ( Character.isAlphabetic(ch) || Character.isDigit(ch) )
					protoName += ch;
				else
					protoName += meta.MetaHelper.alphaName("" + ch) + "_";
			}
			++i;
		}
		return protoName;
	}


	@Override
	public String getSuperprototypeNameForMethod() {
		String s = "UFunction<" + this.getSingleParameterType() + ", ";
		String ret;
		if ( this.getReturnTypeExpr() == null )
			ret = "Nil";
		else
			ret = this.getReturnTypeExpr().ifPrototypeReturnsItsName();
		return s + ret + ">";
	}



	@Override
	public String getNameWithoutParamNumber() {
		return PWCounter.toStringBuffer(this).toString();
	}

	//
	// * this method should not be used for this class
	//
	@Override
	public MethodComplexName getNameParametersTypes() {
		return null;
	}


	@Override
	public void genCyanEvalMethodSignature(StringBuffer s) {
		s.append("eval: " + keywordGrammar.getStringType() );
		if ( parameterDec.getName() != null )
			s.append(" " + parameterDec.getName());
		if ( this.getReturnTypeExpr() != null ) {
			s.append(" -> " + this.getReturnTypeExpr().ifPrototypeReturnsItsName() );
		}
	}

*/
	@Override
	public WrSymbol getFirstSymbol() {
		return this.keywordGrammar.getFirstSymbol();
	}

	@Override
	public void calcInterfaceTypes(WrEnv env) {
		//# super.calcInterfaceTypes(env);
		keywordGrammar.calcInterfaceTypes(env);
		if ( paramType != null )
			paramType.calcInternalTypes(env);
	}



	public WrParameterDec getParameterDec() {
		return parameterDec;
	}

	public void setParameterDec(WrParameterDec parameterDec) {
		this.parameterDec = parameterDec;
	}


	public String getCyanName() {
		return cyanName;
	}

	public void setCyanName(String cyanName) {
		this.cyanName = cyanName;
	}




	/**
	 * the composite keyword. In
	 *    (case: char do: Function)+
	 * keywordGrammar references an object of MessageKeywordGrammarList representing
	 *     case: char do: Function
	 *
	 */
	private MessageKeywordGrammar  keywordGrammar;

	/**
	 * the parameter type of the grammar method. There is only one. It is
	 *      Array<Tuple<String, int>>
	 * in
	 *     public fun (key: String value: int)+  Array<Tuple<String, Int>> t { ... }
	 */
	private final WrExpr paramType;

	/**
	 * the sole parameter of this grammar method
	 */
	private WrParameterDec parameterDec;

	/**
	 * the name of this grammar method
	 */
	private String cyanName;

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
	}



	@Override
	public String getName() {
		return null;
	}



	@Override
	public AttachedDeclarationKind getKind(WrEnv env) {
		return null;
	}



	@Override
	public void addDocumentText(String doc, String docKind, WrEnv env) {
	}



	@Override
	public void addDocumentExample(String example, String exampleKind, WrEnv env) {
	}



	@Override
	public List<Tuple2<String, String>> getDocumentTextList(WrEnv env) {
		return null;
	}



	@Override
	public List<Tuple2<String, String>> getDocumentExampleList(WrEnv env) {
		return null;
	}



	@Override
	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature, WrEnv env) {
	}



	@Override
	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList(WrEnv env) {
		return null;
	}



	@Override
	public List<WrExprAnyLiteral> searchFeature(String name, WrEnv env) {
		return null;
	}



	@Override
	public WrType getReturnType(WrEnv env) {
		return null;
	}



	@Override
	public String getFullName(WrEnv env) {
		return null;
	}


	@Override
	public String getFullName() {
		return null;
	}



	@Override
	public String getFullNameWithReturnType(WrEnv env) {
		return null;
	}



	@Override
	public String getFullNameWithReturnType() {
		return null;
	}


	@Override
	public String getNameWithDeclaredTypes() {
		return null;
	}


	@Override
	public String asString() {
		return null;
	}



	@Override
	public WrExpr getReturnTypeExpr() {
		return null;
	}



	@Override
	public void setReturnTypeExpr(WrExpr returnType) {
	}



	@Override
	public WrMethodDec getMethod() {
		return null;
	}


	@Override
	public String getFunctionNameWithSelf(String fullName) {
		return null;
	}

	@Override
	ASTNode getHidden() {
		return null;
	}



	@Override
	public List<WrParameterDec> getParameterList() {
		return null;
	}



}
