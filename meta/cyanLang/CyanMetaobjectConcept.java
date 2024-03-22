package meta.cyanLang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/**
 * See the documentation for this metaobject in the text on the
 * Cyan Metaobject Protocol
 * <code>
    @concept{*
        T belongs [ "Int", "Short", "Byte", "Char", "Long" ],
        U implements Savable,
        R sub-prototype Person
    *}
    object Proto<T, U, R>
        ...
    end
    </code>

 *
 *
 *
 */
import java.util.List;
import java.util.Set;
import error.CompileErrorException;
import lexer.Lexer;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.Function0;
import meta.IActionPrototypeLater_parsing;
import meta.ICheckPrototype_bsa;
import meta.ICompilerPrototypeLater_parsing;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.IListAfter_afterResTypes;
import meta.IParseWithCyanCompiler_parsing;
import meta.MetaHelper;
import meta.ParsingPhase;
import meta.Token;
import meta.Tuple2;
import meta.Tuple5;
import meta.VariableKind;
import meta.WrAnnotationAt;
import meta.WrCompilationUnit;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprIdentStar;
import meta.WrExprTypeof;
import meta.WrGenericParameter;
import meta.WrMethodDec;
import meta.WrMethodKeywordWithParameters;
import meta.WrMethodSignature;
import meta.WrMethodSignatureOperator;
import meta.WrMethodSignatureUnary;
import meta.WrMethodSignatureWithKeywords;
import meta.WrParameterDec;
import meta.WrPrototype;
import meta.WrSymbol;
import meta.WrSymbolIdent;
import meta.WrSymbolKeyword;
import meta.WrType;
import meta.lexer.MetaLexer;
import metaRealClasses.CompilerGenericPrototype_parsing;
import metaRealClasses.Compiler_parsing;
import saci.Compiler;
import saci.CompilerManager;

class ProtoInfo {
	public ProtoInfo(String name) {
		this.name = name;
	}
	public String name;
	public boolean isPrototype = false;
	public boolean isInterface = false;
	public boolean isSymbol = false;
	public List<String> superprototypeList = null;
	public List<String> subprototypeList = null;
	public List<String> interfaceImplementList = null;
	public List<WrMethodSignature> methodSignatureList = null;
	   // methods that should be preceded by 'override'
	public List<WrMethodSignature> overrideMethodSignatureList = null;
	public List<String> inList = null;
	public List<String> axiomList = null;
	   /*
	    * if isList is not null, the generic parameter appears in the the left-hand side of an equation such as T in<br>
	    * <code> T is A_Prototype</code><br>
	    * In this case, no prototype in the test cases should be created for T: it already exists.
	    */
	public List<WrExpr> isList = null;
	/**
	 * all method signatures, including those of 'would-to-be' super-prototypes and
	 * super-interfaces. That is, if we have formal parameters A, B, and C and restrictions<br>
	 * <code>
	 *     A extends Matriz<br>
	 *     B extends A, <br>
	 *     A implements I1, <br>
	 *     C extends B, <br>
	 *     B implements I2, <br>
	 *     C implements I3<br>
	 *
	 * </code> <br>
	 *
	 * Then field allMethodSignatures of C would contain all method signatures of Matriz, A, B, I1, B, I2, and I3
	 */
	public Set<WrMethodSignature> allMethodSignatures;
	/**
	 * all method signatures as strings
	 */
	public Set<String> allFullNameMethodSignatures;

}

abstract class Node {
	/**
	 * typeOrSymbol may be a type or just a symbol
	 */
	public WrExpr typeOrSymbol;
	/**
	 * true if typeOrSymbol is a symbol
	 */
	public boolean isSymbol = false;
	/*
	 * true if the expression is preceded by '!' as in<br>
	 * <code>
	 *     ! T subtype Company
	 * </code>
	 */
	public boolean precededByNot = false;
	public String errorMessage;
	/**
	 * true if the first element in a node is an identifier such as T in <br>
	 * <code>
	 * T implements ICompany<br>
	 * </code>
	 */
	public boolean isIdentifier;
	/**
	 * if isIdentifier is true, the name of the identifier
	 */
	public String name;
	/**
	 * return false if there was an error
	   @param annotation
	   @param compiler
	   @return
	 */
	abstract public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler);

	public Tuple2<StringBuffer, Integer> axiomTestCode_semAn(int numberNextMethod) {
		return null;
	}

	public List<Function0> createFunctionList(Function0 f) {
		final List<Function0> functionList = new ArrayList<>();
		functionList.add(f);
		return functionList;
	}

	/**
	 * collect data on generic parameters during semantic analysis. Then semantic information is
	 * available. This data is collected only on instantiated generic prototypes.
	 *
	   @param map
	   @return
	 */

	abstract public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map);

	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		if ( ! gpSet.contains(this.name) ) {
			if ( gpSet.size() == 0 || !(this.typeOrSymbol instanceof WrExprTypeof) ) {
				this.typeOrSymbol.calcInternalTypes(env);
			}
		}
	}
}

class IsNode extends Node {

	public IsNode() {  }

	public WrExpr isTypeExpr;
	@Override
	public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler) {

		final WrType t = typeOrSymbol.getType();
		final WrType isType = isTypeExpr.getType();
		if ( precededByNot ) {
			if ( t == isType ) {
				return createFunctionList(
						() -> { compiler.errorAtGenericPrototypeInstantiation(
								//"'" + t.getFullName() + "' is equal to "
								//+ isType.getFullName() + ". The concept associated to this generic prototype expected them to be equal."

									"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
									"' expected that '" + t.getFullName() + "' were NOT equal to '"
									+ isType.getFullName() + "'"); } );
			}
		}
		else {
			if ( t != isType ) {
				return createFunctionList(
						() -> { compiler.errorAtGenericPrototypeInstantiation(
								"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
								"' expected that '" + t.getFullName() + "' were equal to '"
								+ isType.getFullName() + "'"); } );
			}
		}
		return null;
	}

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);
		if ( pi != null ) {
			if ( pi.isList == null ) {
				pi.isList = new ArrayList<>();
			}
			pi.isList.add(isTypeExpr);
			/*
			String s = ((Expr ) isTypeExpr).asString();
			if ( map.containsKey(s) ) {
				pi.isList.add( s );
			}
			else {
				pi.isList.add( ((Expr ) isTypeExpr).getType().getFullName() );
			}
			*/
		}
		return null;
	}

	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		super.calcInternalTypes(env, gpSet);
		if ( gpSet.size() == 0 || !(this.isTypeExpr instanceof WrExprTypeof) ) {
			isTypeExpr.calcInternalTypes(env);
		}
		/*
		if ( ! gpSet.contains(isTypeExpr.asString()) ) {
		}
		*/
	}
}




class IsInterfaceNode extends Node {

	@Override
	public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler) {

		final WrType t = typeOrSymbol.getType();
		if ( precededByNot ) {
			if ( t instanceof WrPrototype && ((WrPrototype ) t).isInterface() ) {
				return createFunctionList(
						() -> { compiler.errorAtGenericPrototypeInstantiation(

								"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
								"' expected that '" +
								"'" + t.getFullName() + "' were NOT an interface. "
								); } );
			}
		}
		else {
			if ( !(t instanceof WrPrototype && ((WrPrototype ) t).isInterface() ) ) {
				return createFunctionList(
						() -> { compiler.errorAtGenericPrototypeInstantiation(
								"A concept associated to generic prototype '" +
						compiler.getEnv().getCurrentPrototype().getFullName() +
								"' expected that '" +
								"'" + t.getFullName() + "' were an interface. "
								); } );

			}
		}
		return null;
	}

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);
		if ( pi != null ) {
			pi.isInterface = true;
		}
		return null;
	}

	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		super.calcInternalTypes(env, gpSet);
	}
}


class IsNoninterfacePrototypeNode extends Node {

	@Override
	public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler) {

		final WrType t = typeOrSymbol.getType();
		if ( precededByNot ) {
			if ( t instanceof WrPrototype && ! ((WrPrototype ) t).isInterface() ) {
				return createFunctionList(
						() -> { compiler.errorAtGenericPrototypeInstantiation(
								"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
								"' expected that '" +
								"'" + t.getFullName() + "' were NOT a prototype (it may be an interface)"); } );
			}
		}
		else {
			if ( !t.isObjectDec() ) {
				return createFunctionList(
						() -> { compiler.errorAtGenericPrototypeInstantiation(
								"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
								"' expected that '" +
								"'" + t.getFullName() + "' were a prototype (it may not be an interface)"); } );
			}
		}
		return null;
	}

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);
		if ( pi != null ) {
			pi.isPrototype = true;
		}
		return null;
	}

	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		super.calcInternalTypes(env, gpSet);
	}
}

class IsSymbolNode extends Node {

	@Override
	public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler) {

		final String typeName = typeOrSymbol.ifPrototypeReturnsItsName();
		isSymbol = Lexer.isSymbol(typeName);
		if ( precededByNot ) {
			if ( isSymbol ) {
				return createFunctionList(
						() -> { compiler.errorAtGenericPrototypeInstantiation(
								"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
								"' expected that '" + typeName + "' were NOT a symbol"); } );
			}
		}
		else {
			if ( ! isSymbol ) {
				return createFunctionList(
						() -> { compiler.errorAtGenericPrototypeInstantiation(
								"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
								"' expected that '" + typeName + "' were a symbol"); } );
			}
		}
		return null;
	}

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);
		if ( pi != null ) {
			pi.isSymbol = true;
		}
		return null;
	}

	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
	}
}

class AxiomParameter {
	public String name;
	public String strType;
}


class AxiomNode extends Node {
	public char []funcText;
	public List<AxiomParameter> paramList;
	public String methodName;
	// public MethodSignature methodSignature;

	@Override
	public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler) {
		return null;
	}

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {

		/*ProtoInfo pi = map.get(name);
		if ( pi != null ) {
			if ( pi.axiomList == null ) {
				pi.axiomList = new ArrayList<>();
			}
			pi.axiomList.add( new String(funcText) );
		}
		*/
		return null;
	}

	@Override
	public Tuple2<StringBuffer, Integer>  axiomTestCode_semAn(int numberNextMethod) {
		final StringBuffer s = new StringBuffer();

		s.append("\n");
		s.append("    func " + methodName.substring(0, methodName.length()-1) + "_" + numberNextMethod + ": ");
		int size = this.paramList.size();
		for ( final AxiomParameter p : this.paramList ) {
			s.append(p.strType + " " + p.name);
			if ( --size > 0 ) {
				s.append(", ");
			}
		}
		s.append(" -> String|Nil { \n");
		s.append(this.funcText);
		s.append("\n");
		s.append("    }\n\n");

		return new Tuple2<StringBuffer, Integer>(s, numberNextMethod + 1);
	}


	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
	}

}

class TypeInNode extends Node {
	//public List<String> typeList;
	/*
	 * typeList may be a list of types or simple a list of symbols.
	 */
	public List<WrExpr> typeList;

	@Override
	public List<Function0>  check(WrAnnotationAt annotation, ICompiler_semAn compiler) {

		final WrType t = typeOrSymbol.getType();
		if ( !(t.getInsideType() instanceof WrPrototype) ) {
			return createFunctionList(
					() -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							"'" + t.getFullName() + "' were a prototype"); } );
		}
		boolean found = false;
		String nameLeft = t.getFullName();
		if ( nameLeft.startsWith(MetaHelper.cyanLanguagePackageName) ) {
			nameLeft = nameLeft.substring(MetaHelper.cyanLanguagePackageDirectory.length() + 1);
		}
		for ( final WrExpr exprType : typeList ) {
			if ( exprType.getType()  == t  ) {
				found = true;
				break;
			}
		}
		if ( !precededByNot && ! found || precededByNot && found ) {
			int size = typeList.size();
			String strList = "";
			for ( final WrExpr exprType : typeList )  {
				strList += exprType.getType().getFullName();
				if ( --size > 0 ) {
					strList += ", ";
				}
			}
			if ( errorMessage != null ) {
				return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
			}
			else {
				if ( !precededByNot ) {
					final String message = 	"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" + nameLeft + "' were one element of the following list: " + strList;
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(message); } );
				}
				else {
					final String message = 	"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" + nameLeft + "' were NOT one element of the following list: " + strList;
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(message); } );
				}
			}
		}
		return null;
	}

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);

		if ( pi != null ) {
			if ( pi.inList == null ) {
				pi.inList = new ArrayList<>();
			}
			for ( final WrExpr exprType : typeList ) {
				final String s = exprType.asString();
				if ( map.containsKey(s) ) {
					pi.inList.add( s );
				}
				else {
					pi.inList.add( exprType.getType().getFullName() );
				}
			}
		}
		return null;
	}

	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		super.calcInternalTypes(env, gpSet);
		for ( final WrExpr exprType : typeList ) {
			if ( gpSet.size() == 0 || !(exprType instanceof WrExprTypeof) ) {
				exprType.calcInternalTypes(env);
			}
		}
	}


}


class SymbolInNode extends Node {
	//public List<String> typeList;
	/*
	 * typeList may be a list of types or simple a list of symbols.
	 */
	public List<WrExprIdentStar> symbolList;

	@Override
	public List<Function0>  check(WrAnnotationAt annotation, ICompiler_semAn compiler) {

		final WrExprIdentStar left = (WrExprIdentStar ) this.typeOrSymbol;
		final String leftString = left.getName();
		boolean found = false;
		for ( final WrExprIdentStar id : symbolList ) {
			if ( id.getName().equals(leftString) ) {
				found = true;
				break;
			}
		}
		if ( ! found && ! precededByNot || found && precededByNot ) {
			int size = symbolList.size();
			String strList = "";
			for ( final WrExprIdentStar id : symbolList )  {
				strList += id.getName();
				if ( --size > 0 ) {
					strList += ", ";
				}
			}
			if ( errorMessage != null ) {
				return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
			}
			else {
				if ( ! precededByNot ) {
					final String message = "A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" + leftString +
							"' were one of the following symbols: " + strList;
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(message); } );
				}
				else {
					final String message = "A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" + leftString +
							"' were NOT one of the following symbols: " + strList;
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(message); } );
				}
			}
		}
		return null;
	}

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);

		if ( pi != null ) {
			if ( pi.inList == null ) {
				pi.inList = new ArrayList<>();
			}
			for ( final WrExprIdentStar id : symbolList ) {
				pi.inList.add( id.asString() );
			}
		}

		return null;
	}

	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
	}


}


class ImplementsNode extends Node {
	public WrExpr exprInterface;

	@Override
	public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler) {
		final WrType t = typeOrSymbol.getType();
		if ( !t.isObjectDec() ) {
			return createFunctionList(
					() -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							"'" + t.getFullName() + "' were a prototype"); } );
		}
		final WrType interType = exprInterface.getType();
		if ( !interType.isInterface() ) {
			return createFunctionList(
					() -> { compiler.errorAtGenericPrototypeInstantiation("'" + interType.getFullName() + "' is not an interface"); } );
		}
		if ( precededByNot ) {
			if ( interType.isSupertypeOf(t, compiler.getEnv()) ) {
				if ( errorMessage != null ) {
					return createFunctionList(
							() -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
				}
				else
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +

							"'" + t.getFullName() + "' did NOT implement interface '" + interType.getFullName() + "'"); } );
			}
		}
		else {
			if ( ! interType.isSupertypeOf(t, compiler.getEnv()) ) {
				if ( errorMessage != null ) {
					return createFunctionList(
							() -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
				}
				else
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +

							"'" + t.getFullName() + "' implemented interface '" + interType.getFullName() + "'"); } );
			}
		}
		return null;
	}


	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);

		if ( pi != null ) {
			if ( pi.interfaceImplementList == null ) {
				pi.interfaceImplementList = new ArrayList<>();
			}
			final String s = exprInterface.asString();
			if ( map.containsKey(s) ) {
				final ProtoInfo other = map.get(s);
				pi.interfaceImplementList.add(s);
				/*
				 * then s should be marked as an interface
				 */
				other.isInterface = true;
			}
			else {
				pi.interfaceImplementList.add(exprInterface.getType().getFullName());
			}
		}

		return null;
	}


	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		super.calcInternalTypes(env, gpSet);
		if ( gpSet.size() == 0 || !(exprInterface instanceof WrExprTypeof) ) {
			this.exprInterface.calcInternalTypes(env);
		}
	}


}

class NewTypeNode extends Node {

	public NewTypeNode() { }
	public WrExpr aNewTypeExpr;

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		return null;
	}

	@Override
	public List<Function0> check(WrAnnotationAt annotation,
			ICompiler_semAn compiler) {
		compiler.addNewTypeDef(typeOrSymbol.asString(), this.aNewTypeExpr.getType());
		//System.out.println(this.aNewTypeExpr.asString());
		return null;
	}


	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {

		final String typeName = typeOrSymbol.asString();
		final WrPrototype pu = env.getCurrentCompilationUnit().getPublicPrototype();
		if ( pu.getGenericParameterListList(env)  != null ) {
			for ( final List<WrGenericParameter> list : pu.getGenericParameterListList(env) ) {
				for ( final WrGenericParameter gpParam : list ) {
					if ( gpParam.getName().equals(typeName) ) {
						env.error(typeOrSymbol.getFirstSymbol(),
								"Type '" + typeName + "' is equal to one of the generic prototype parameters");
					}
				}
			}
		}
		/*
		super.calcInternalTypes(env, gpSet);
		if ( gpSet.size() == 0 || !(aNewTypeExpr instanceof ast.ExprTypeof) ) {
			((Expr ) this.aNewTypeExpr).calcInternalTypes(env);
		}
		*/
	}


}

class SubprototypeNode extends Node {
	public WrExpr exprSuperprototype;
	@Override
	public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler) {
		final WrType t = typeOrSymbol.getType();
		   // dangerous
		if ( !(t instanceof WrPrototype)  ) {
			return createFunctionList(
					() -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							t.getFullName() + "' were a prototype"
							); } );
		}
		final WrType supertype = exprSuperprototype.getType();
		if ( !(supertype.getInsideType() instanceof WrPrototype) ) {
			return createFunctionList(
					() -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							supertype.getFullName() + "' were a prototype"); } );
		}

		final WrPrototype subType = (WrPrototype ) t;
		subType.calcInterfaceSuperTypes(compiler.getEnv());

		if ( precededByNot ) {
			if ( supertype.isSupertypeOf(subType, compiler.getEnv()) ) {
				if ( errorMessage != null ) {
					return createFunctionList(
							() -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
				}
				else
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							"'" + t.getFullName() + "' were NOT a subtype of '"+ supertype.getFullName() + "'"); } );
			}
		}
		else {
			if ( ! supertype.isSupertypeOf(subType, compiler.getEnv()) ) {
				if ( errorMessage != null ) {
					return createFunctionList(
							() -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
				}
				else
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							"'" + t.getFullName() + "' were a subtype of '"+ supertype.getFullName() + "'"); } );
			}
		}
		return null;
	}


	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);

		if ( pi != null ) {
			if ( pi.superprototypeList == null ) {
				pi.superprototypeList  = new ArrayList<>();
			}
			final String s = exprSuperprototype.asString();
			if ( map.containsKey(s) ) {
				final ProtoInfo other = map.get(s);
				other.isPrototype = true;
				pi.superprototypeList.add(s);
			}
			else {
				pi.superprototypeList.add(exprSuperprototype.getType().getFullName());
			}
		}

		return null;
	}


	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		super.calcInternalTypes(env, gpSet);
		if ( gpSet.size() == 0 || !(exprSuperprototype instanceof WrExprTypeof) ) {
			this.exprSuperprototype.calcInternalTypes(env);
		}
	}

}

class SuperprototypeNode extends Node {
	public WrExpr exprSubtype;
	@Override
	public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler) {
		final WrType t = typeOrSymbol.getType();
		   // dangerous
		if ( !(t instanceof WrPrototype) ) {
			return createFunctionList(
					() -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							"'" + t.getFullName() + "' were a prototype"
							); } );
		}
		final WrType subType = exprSubtype.getType();
		if ( !(subType.getInsideType() instanceof WrPrototype) ) {
			return createFunctionList(
					() -> { compiler.errorAtGenericPrototypeInstantiation("'" + subType.getFullName() + "' is not a prototype"); } );
		}

		final WrPrototype superType = (WrPrototype ) t;
		superType.calcInterfaceSuperTypes(compiler.getEnv());

		if ( precededByNot ) {
			if ( superType.isSupertypeOf(subType, compiler.getEnv()) ) {
				if ( errorMessage != null ) {
					return createFunctionList(
							() -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
				}
				else
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							"'" + t.getFullName() + "' were NOT a supertype of '"+ subType.getFullName() + "'"); } );
			}
		}
		else {
			if ( ! superType.isSupertypeOf(subType, compiler.getEnv()) ) {
				if ( errorMessage != null ) {
					return createFunctionList(
							() -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
				}
				else {
					return createFunctionList( () -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							"'" + t.getFullName() + "' were a supertype of '"+ subType.getFullName() + "'"); } );
				}
			}
		}
		return null;
	}


	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);

		if ( pi != null ) {
			if ( pi.subprototypeList == null ) {
				pi.subprototypeList  = new ArrayList<>();
			}
			final String s = exprSubtype.asString();
			if ( map.containsKey(s) ) {
				final ProtoInfo other = map.get(s);
				other.isPrototype = true;
				pi.subprototypeList.add(s);
			}
			else {
				pi.subprototypeList.add(exprSubtype.getType().getFullName());
			}
		}

		return null;
	}

	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		super.calcInternalTypes(env, gpSet);
		if ( gpSet.size() == 0 || !(exprSubtype instanceof WrExprTypeof) ) {
			this.exprSubtype.calcInternalTypes(env);
		}
	}

}


class ConceptFileNode extends Node {

	public String conceptFilename;
	public List<String> paramNameList;
	public List<Node> conceptFileNodeList;
	public String conceptPackageName;
	public WrSymbol firstSymbol;

	@Override
	public List<Function0> check(WrAnnotationAt annotation, ICompiler_semAn compiler) {
		List<Function0> functionList = null;
		if ( conceptFileNodeList != null ) {
			for ( final Node node : conceptFileNodeList ) {
				final List<Function0> nodeFunctionList = node.check( annotation, compiler);
				if ( nodeFunctionList != null ) {
					if ( functionList == null ) {
						functionList = new ArrayList<>();
					}
					functionList.addAll(nodeFunctionList);
				}
			}
		}
		return functionList;
	}

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {

		List<String>  errorMessageList = null;

		if ( conceptFileNodeList != null ) {
			for ( final Node node : conceptFileNodeList ) {
				final List<String>  emList = node.collectGPData_semAn(map);
				if ( emList != null ) {
					if ( errorMessageList == null ) {
						errorMessageList = new ArrayList<>();
					}
					errorMessageList.addAll(emList);
				}
			}
		}
		return errorMessageList;
	}

	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		for ( final Node node : conceptFileNodeList ) {
			node.calcInternalTypes(env, gpSet);
		}
	}

	@Override
	public Tuple2<StringBuffer, Integer> axiomTestCode_semAn(int numberNextMethod) {
		StringBuffer s = null;
		for ( final Node node : conceptFileNodeList ) {
			final Tuple2<StringBuffer, Integer> t = node.axiomTestCode_semAn(numberNextMethod);
			if ( t != null ) {
				final StringBuffer other = t.f1;
				numberNextMethod = t.f2;
				if ( other != null ) {
					if ( s == null ) {
						s = new StringBuffer();
					}
					s.append(other);
				}
			}
		}
		return new Tuple2<StringBuffer, Integer>(s, numberNextMethod);
	}

}


class TypeHasNode extends Node {

	public List<WrMethodSignature> methodSignatureList;
	public List<String> errorMessageList;

	/**
	 * check if typeOrSymbol has all the method signatures of methodSignatureList
	 */
	@Override
	public List<Function0>  check(WrAnnotationAt annotation, ICompiler_semAn compiler) {

		List<Function0> functionList = null;


		WrType t = typeOrSymbol.getType();
		String typeName = t.getName();
		if ( CyanMetaobjectConcept.isCurrentPrototype(typeName) ) {
			t = compiler.getEnv().getCurrentPrototype();
		}
		if ( !(t.getInsideType() instanceof WrPrototype) ) {
			WrType tFinal = t;
			return createFunctionList(
					() -> { compiler.errorAtGenericPrototypeInstantiation(
							"A concept associated to generic prototype '" + compiler.getEnv().getCurrentPrototype().getFullName() +
							"' expected that '" +
							tFinal.getFullName() + "' were a prototype"
							); } );
		}
		final WrPrototype pu = (WrPrototype ) t.getInsideType();

		final WrEnv env = compiler.getEnv();
		int n = 0;
		for ( final WrMethodSignature ms : this.methodSignatureList ) {
			ms.calcInterfaceTypes(env);
			env.removeAllLocalVariableDec();
			final String methodName = ms.getName();
			boolean found = false;
			List<WrMethodSignature> msList = pu.searchMethodPublicSuperPublicProto(methodName, env);

			if ( methodName.equals("init") || methodName.equals("init:") ) {
				/*
				 * search is only valid if the method was found in the prototype, not in super-prototypes.
				 */
				// MethodSignature msList
				List<WrMethodSignature> msList2 = null;
				if ( msList != null && msList.size() > 0 ) {
					msList2 = new ArrayList<>();
					for ( final WrMethodSignature ms2 : msList ) {
						final Object declaringObj =  msList.get(0).getMethod().getDeclaringObject();
						if ( declaringObj != null && declaringObj == pu ) {
							msList2.add(ms2);
						}
					}
				}
				if ( msList2 != null && msList2.size() > 0 ) {
					if (  msList2.size() > 0 ) {
						if ( ms instanceof WrMethodSignatureUnary )
							found = true;
						else {
							found = env.searchMethodSignature(ms, msList2) != null;
						}
					}
				}
				else {
					/*
					 * if the prototype does not declare any 'init' or 'init:' method,
					 * the compiler will declare an 'init' method
					 */
					if ( methodName.equals("init") ) {
						msList = pu.searchMethodPublicPackageSuperPublicPackage("init:", env);
						msList2 = null;
						if ( msList != null && msList.size() > 0 ) {
							msList2 = new ArrayList<>();
							for ( final WrMethodSignature ms2 : msList ) {
								final Object declaringObj =  msList.get(0).getMethod().getDeclaringObject();
								if ( declaringObj != null && declaringObj == pu ) {
									msList2.add(ms2);
								}
							}
						}
						if ( msList2 == null || msList2.size() == 0 ) {
							/*
							 * no method 'init:' or 'init' was found. The compiler will create an 'init' method
							 */
							found = true;
						}
					}
				}
			}
			else {
				if ( msList != null && msList.size() > 0 ) {
//					if ( ms instanceof WrMethodSignatureUnary )
//						found = true;
//					else {
						found = env.searchMethodSignature(ms, msList) != null;
					//}
				}
			}


			if ( precededByNot ) {
				if ( found ) {
					if ( errorMessageList.get(n) != null ) {
						final String message = errorMessageList.get(n);
						if ( functionList == null ) { functionList = new ArrayList<>(); }
						functionList.add( () -> { compiler.errorAtGenericPrototypeInstantiation(message); } );
					}
					else {
						if ( errorMessage != null ) {
							if ( functionList == null ) { functionList = new ArrayList<>(); }
							functionList.add( () -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
						}
						else
							if ( functionList == null ) { functionList = new ArrayList<>(); }
							functionList.add( () -> { compiler.errorAtGenericPrototypeInstantiation(
									"A concept associated to generic prototype '" + env.getCurrentPrototype().getFullName() +
									"' expected that method '" + ms.getFullNameWithReturnType(env) + "' were in prototype '"
									+ pu.getFullName() + "'"); } );
					}
				}
			}
			else {
				if ( !found ) {
					if ( errorMessageList.get(n) != null ) {
						final String message = errorMessageList.get(n);
						if ( functionList == null ) { functionList = new ArrayList<>(); }
						functionList.add( () -> { compiler.errorAtGenericPrototypeInstantiation(message); } );
					}
					else {
						found = env.searchMethodSignature(ms, msList) != null;
						if ( errorMessage != null ) {
							if ( functionList == null ) { functionList = new ArrayList<>(); }
							functionList.add( () -> { compiler.errorAtGenericPrototypeInstantiation(errorMessage); } );
						}
						else {
							if ( functionList == null ) { functionList = new ArrayList<>(); }
							functionList.add( () -> { compiler.errorAtGenericPrototypeInstantiation(
									"A concept associated to generic prototype '" + env.getCurrentPrototype().getFullName() +
									"' expected that method '" + ms.getFullNameWithReturnType(env) + "' were in prototype '"
									+ pu.getFullName() + "'"); } );
						}
					}
				}
			}
			++n;

		}
		return functionList;
	}

	@Override
	public List<String> collectGPData_semAn(HashMap<String, ProtoInfo> map) {
		final ProtoInfo pi = map.get(name);
		List<String> emList = null;

		if ( pi != null ) {
			if ( pi.methodSignatureList == null ) {
				pi.methodSignatureList = new ArrayList<>();
			}
			for ( final WrMethodSignature ms : this.methodSignatureList ) {
				final String sigName = ms.getName();
				boolean error = false;
				for ( final WrMethodSignature ms2 : pi.methodSignatureList ) {
					if ( sigName.equals(ms2.getName()) ) {
						if ( emList == null ) {
							emList = new ArrayList<String>();
						}
						error = true;
						emList.add("Duplicated method signature: '" + sigName + "'. Note that currently this metaobject does not support"
								+ " method overloading");
					}
				}
				if ( ! error ) {
					pi.methodSignatureList.add(ms);
				}
			}
		}
		return emList;
	}

	@Override
	public void calcInternalTypes(WrEnv env, HashSet<String> gpSet) {
		super.calcInternalTypes(env, gpSet);
		for ( final WrMethodSignature ms : this.methodSignatureList ) {
			final String strMS = ms.asString();
			  /*
			   * using string strMS here is a very primitive way of discovering if the method signature
			   * uses 'typeof'. This hopefully will be changed some day
			   */
			if ( ! CyanMetaobjectConcept.hasTypeof(strMS) ) {
				ms.calcInterfaceTypes(env);
				env.atEndMethodDec();
			}
		}
	}

}

/**
 * concepts, which are restrictions on the prototype to which the annotation is attached. See the Cyan manual for
 * further explanations.
 *
   @author jose
 */

public class CyanMetaobjectConcept extends CyanMetaobjectAtAnnot
       implements IParseWithCyanCompiler_parsing,
       			  ICheckPrototype_bsa,
       			  IActionPrototypeLater_parsing,
       			  IListAfter_afterResTypes {

	public CyanMetaobjectConcept() {
		super("concept", AnnotationArgumentsKind.ZeroOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC });
	}


	@Override
	public void check() {
		final List<Object> paramList = this.getAnnotation().getJavaParameterList();
		if ( paramList != null && paramList.size() != 0 ) {
			if ( paramList.size() != 1 || !(paramList.get(0) instanceof String) ) {
				this.addError("This metaobject can take one parameter, which should be \"test\", with or without the quotes");
			}
			else {
				final String p = MetaHelper.removeQuotes((String ) paramList.get(0));
				if ( !p.equals("test") ) {
					this.addError("This metaobject can take one parameter, which should be \"test\", with or without the quotes");
				}
			}
		}
	}

	@Override
	public boolean shouldTakeText() { return true; }


	public void loadConceptFile(ICompiler_parsing comp, ICompiler_parsing originalCompiler_parsing, ConceptFileNode conceptNode, String conceptPackageName) {


		final Tuple5<FileError, char[], String, String, WrCyanPackage> t = originalCompiler_parsing.readTextFileFromPackage(
				conceptNode.conceptFilename, "concept", conceptNode.conceptPackageName, DirectoryKindPPP.DATA,
				conceptNode.paramNameList.size(), conceptNode.paramNameList);

		if ( t != null && t.f1 == FileError.package_not_found ) {
			comp.error( conceptNode.firstSymbol,  "Cannot find package '" + conceptNode.conceptPackageName + "'");
			return ;
		}
		if ( t == null  || t.f1 != FileError.ok_e ) {
			comp.error( conceptNode.firstSymbol,  "Cannot read concept from file '" + conceptNode.conceptFilename + ".concept" +
		        "' from package '" + conceptNode.conceptPackageName + "'. This file should be in directory '" + DirectoryKindPPP.DATA +
		        "' of the package directory"
		         );
			return ;
		}

		final char []text = t.f2;
		final ICompiler_parsing icomp = CompilerManager.getCompilerToInternalDSL(text,
				t.f3, t.f4, t.f5);

		conceptNode.conceptFileNodeList = null;
		try {
			conceptNode.conceptFileNodeList = this.parseConceptSourceCode(icomp, originalCompiler_parsing, conceptPackageName);
			((Compiler_parsing ) comp).getExprStatList().addAll( ((Compiler_parsing ) icomp).getExprStatList() );
		}
		catch ( final CompileErrorException e ) {
			/*
			 * convert exception from icomp to exception to comp

			CompilationUnitSuper cus = ((Compiler_parsing ) icomp).getCompiler().getCompilationUnitSuper();
			List<UnitError> errorList = cus.getErrorList();
			if ( errorList.size() > 0 ) {
				UnitError ue = errorList.get(errorList.size()-1);
				comp.error(ue.getSymbol(),  ue.getMessage());
			}
			else {
				comp.error(conceptNode.firstSymbol, "Internal error in metaobject '" + this.getName() + "'");
			}
			*/
		}
		/*
		 * all expressions found by icomp should be added to comp. Then the Cyan compiler can type them correctly.
		 */

	}

	/**
	 * This metaobject is used in a generic prototype P. If P is a non-instantiated generic prototype (file name, for
	 * example, P(1).cyan), returns false. If P is an instantiated generic prototype (file name, for example,
	 * P(Int).cyan), returns true;
	   @param compiler_parsing
	   @return
	 */
	private static boolean isInstantiatedGenericPrototype(ICompilerPrototypeLater_parsing compiler_parsing) {
		final String filename = ((CompilerGenericPrototype_parsing) compiler_parsing).getCompilationUnit().getFilename();
		final int index = filename.indexOf('(');
		return ! ( index >= 0 && index < filename.length() - 1 && Character.isDigit(filename.charAt(index+1)) );

	}

	@Override
	public void parsing_parse(ICompiler_parsing compiler_parsing) {

		compiler_parsing.next();

		WrCyanPackage wrCyanPackage = compiler_parsing.getCompilationUnit().getCyanPackage();
		nodeList = parseConceptSourceCode(compiler_parsing, compiler_parsing,
				wrCyanPackage == null ? "" : wrCyanPackage.getPackageName());


		// // this.metaobjectAnnotation.setInfo_parsing(nodeList);


		return ;

	}

	public static final String conceptFileExtension = "concept";
	public static final String dotConceptFileExtension = ".concept";

	public List<Node> parseConceptSourceCode(ICompiler_parsing compiler_parsing,
			ICompiler_parsing originalCompiler_parsing, String currentPackageName) {
		final List<Node> nodeListAux = new ArrayList<>();

		WrSymbol sym;
		while ( compiler_parsing.startType(compiler_parsing.getSymbol().token)
				|| compiler_parsing.getSymbol().token == Token.NOT ) {
			final boolean precededByNot = compiler_parsing.getSymbol().token == Token.NOT;
			if ( precededByNot ) {
				compiler_parsing.next();
			}

			final WrExpr typeOrSymbol = compiler_parsing.type();

			String gpName = typeOrSymbol.asString();
//			if ( gpName.equals("THISPROTOTYPE999") ) {
//				gpName = compiler_parsing.getCurrentPrototypeName();
//				WrExprIdentStar wri = new WrExprIdentStar(gpName);
//				System.out.println("CurrentProto name is " + gpName);
//			}
			final boolean isIdentifier = MetaLexer.isIdentifier(gpName);


			sym = compiler_parsing.getSymbol();
			final String symStr = compiler_parsing.getSymbol().getSymbolString();
			if ( symStr.equals("interface") ) {
				final IsInterfaceNode node = new IsInterfaceNode();
				node.typeOrSymbol = typeOrSymbol;
				nodeListAux.add(node);
				compiler_parsing.next();
			}
			else if ( symStr.equals("noninterface") ) {
				final IsNoninterfacePrototypeNode node = new IsNoninterfacePrototypeNode();
				node.typeOrSymbol = typeOrSymbol;
				nodeListAux.add(node);
				compiler_parsing.next();
			}
//			else if ( sym.getSymbolString().equals("newType")) {
//				   // Q newType  typeof(T get: 0)
//				compiler_parsing.next();
//				compiler_parsing.removeLastExprStat();
//				final WrExpr aNewTypeExpr = compiler_parsing.type();
//				final NewTypeNode node = new NewTypeNode();
//				nodeListAux.add(node);
//				node.typeOrSymbol = typeOrSymbol;
//				node.aNewTypeExpr = aNewTypeExpr;
//			}
			else if ( symStr.equals("identifier") ) {
				final IsSymbolNode node = new IsSymbolNode();
				node.typeOrSymbol = typeOrSymbol;
				nodeListAux.add(node);
				compiler_parsing.removeLastExprStat();
				compiler_parsing.next();
			}
			else if ( sym.getSymbolString().equals("is") ) {
				compiler_parsing.next();
				final WrExpr isTypeExpr = compiler_parsing.type();
				final IsNode node = new IsNode();
				nodeListAux.add(node);
				node.typeOrSymbol = typeOrSymbol;
				node.isTypeExpr = isTypeExpr;

			}
			else if ( typeOrSymbol.asString().equals("axiom") ) {
				if ( precededByNot ) {
					compiler_parsing.error(typeOrSymbol.getFirstSymbol(), "An axiom cannot be preceded by '!'");
				}
				compiler_parsing.removeLastExprStat();


				/*
                   axiom equalityTest: T a, T b, T c {%
                           if a == a && a != a && ! (a == a) || !(b == b) || !(c == c) || (a == b && a != b) ||
                              !(a == b && b == c && a != c) {
                               ^"method '==' or '!=' of T do not satisfy the axioms for equality and non-equality";
                           }
                       	if ( a == b && (a != b) ) || (a != a) || (b != b) || (a == b && b != a) ||
                       	   (b != a && b == a) {
                       	    ^" T do not obey the rules for equality"
                       	}

                           ^Nil
                   %}

				 *
				 */
				if ( compiler_parsing.getSymbol().token != Token.IDENTCOLON ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "A keyword name such as 'equalityTest:' was expected");
				}
				final AxiomNode axiom = new AxiomNode();
				axiom.methodName = compiler_parsing.getSymbol().getSymbolString();
				compiler_parsing.next();
				if ( compiler_parsing.getSymbol().token != Token.IDENT &&
						! compiler_parsing.isBasicType(compiler_parsing.getSymbol().token)
						) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "An identifier or type was expected. It should be a formal parameter to this generic prototype");
				}
				axiom.paramList = new ArrayList<>();
				while ( compiler_parsing.getSymbol().token == Token.IDENT ||
						compiler_parsing.isBasicType(compiler_parsing.getSymbol().token)
						) {
					final AxiomParameter p = new AxiomParameter();
					final WrExpr ident = compiler_parsing.parseIdent();
					p.strType = ident.asString();
					if ( compiler_parsing.getSymbol().token != Token.IDENT &&
							! compiler_parsing.isBasicType(compiler_parsing.getSymbol().token)
							) {
						compiler_parsing.error(compiler_parsing.getSymbol(), "An identifier was expected. It should be a parameter to this axiom");
					}
					p.name = compiler_parsing.getSymbol().getSymbolString();
					axiom.paramList.add(p);
					compiler_parsing.next();
					if ( compiler_parsing.getSymbol().token == Token.COMMA ) {
						compiler_parsing.next();
						if ( compiler_parsing.getSymbol().token != Token.IDENT &&
								! compiler_parsing.isBasicType(compiler_parsing.getSymbol().token)
								) {
							compiler_parsing.error(compiler_parsing.getSymbol(),
									"An identifier was expected. It should be a formal parameter to this generic prototype");
						}
					}
					else {
						break;
					}
				}

				if ( compiler_parsing.getSymbol().token != Token.LEFTCHAR_SEQUENCE ) {
					compiler_parsing.error(typeOrSymbol.getFirstSymbol(), "A left char sequence such as '{+' or '{%' was expected");
				}
				final WrSymbol leftSymbol = compiler_parsing.getSymbol();
				final String leftSeq = compiler_parsing.getSymbol().getSymbolString();
				compiler_parsing.next();
				final String rightSeq = MetaLexer.rightSymbolSeqFromLeftSymbolSeq(leftSeq);
				compiler_parsing.pushRightSymbolSeq(rightSeq);

				while ( compiler_parsing.getSymbol().token != Token.EOLO && compiler_parsing.getSymbol().token != Token.RIGHTCHAR_SEQUENCE ) {
					compiler_parsing.next();
				}
				if ( ! compiler_parsing.getSymbol().getSymbolString().equals(rightSeq) ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "The right char sequence '" + rightSeq + "' was expected");
				}
				axiom.funcText = compiler_parsing.getText( leftSymbol.getOffset() + leftSeq.length(), compiler_parsing.getSymbol().getOffset());
				compiler_parsing.next();
				nodeListAux.add(axiom);
				/*

				// ******
				if ( ! compiler_parsing.getSymbol().getSymbolString().equals("func") ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "'func' was expected");
				}

				axiom = new AxiomNode();

				compiler_parsing.next();
				axiom.methodSignature = this.methodSignature(compiler_parsing);
				if ( compiler_parsing.getSymbol().token != Token.LEFTCB ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "'{' was expected");
				}

				while ( compiler_parsing.getSymbol().token != Token.EOLO && compiler_parsing.getSymbol().token != Token.RIGHTCHAR_SEQUENCE ) {
					compiler_parsing.next();
				}
				if ( ! compiler_parsing.getSymbol().getSymbolString().equals(rightSeq) ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "The right char sequence '" + rightSeq + "' was expected");
				}
				axiom.funcText = compiler_parsing.getText( leftSymbol.getOffset() + leftSeq.length(), compiler_parsing.getSymbol().getOffset());
				compiler_parsing.next();
				nodeList.add(axiom);
				*/
			}
			else if ( sym.token == Token.LEFTPAR ) {
				// found a link to a file with further demands --- a concept file

				if ( precededByNot ) {
					compiler_parsing.error(typeOrSymbol.getFirstSymbol(), "A concept file name cannot be preceded by '!'");
				}

				compiler_parsing.removeLastExprStat();


				final ConceptFileNode conceptNode = new ConceptFileNode();

				conceptNode.firstSymbol = typeOrSymbol.getFirstSymbol();
				final String fn = typeOrSymbol.asString();
				final int indexdot = fn.lastIndexOf('.');
				if ( indexdot < 0 ) {
					// no '.' in the file name, use package of the metaobject
					conceptNode.conceptFilename = fn;
					if ( currentPackageName.length() == 0 ) {
						// if the annotation is used inside a project file,
						// then this option should not be used
						compiler_parsing.error(compiler_parsing.getSymbol(),
								"The annotation is inside a project (.pyan) file. Therefore, this option cannot be used");

					}
					conceptNode.conceptPackageName = currentPackageName; // compiler_parsing.getCompilationUnit().getCyanPackage().getPackageName();
				}
				else {
					conceptNode.conceptFilename = fn.substring(indexdot + 1);
					conceptNode.conceptPackageName = fn.substring(0, indexdot);
					if ( indexdot + 1 > fn.length() ) {
						compiler_parsing.error(typeOrSymbol.getFirstSymbol(), "A concept file name was expected");
						return null;
					}
				}


				compiler_parsing.next();
				final List<String> paramNameList = parseParamNameList2(compiler_parsing,
						typeOrSymbol);
				if ( compiler_parsing.getSymbol().token != Token.RIGHTPAR ) {
					compiler_parsing.error(typeOrSymbol.getFirstSymbol(), "')' expected");
					return null;
				}
				compiler_parsing.next();
				conceptNode.paramNameList = paramNameList;
				loadConceptFile(compiler_parsing, originalCompiler_parsing, conceptNode, conceptNode.conceptPackageName);
				/* if ( isInstantiatedGenericPrototype(compiler_parsing) ) {
				} */
				nodeListAux.add(conceptNode);
			}
			else if ( sym.token == Token.IMPLEMENTS ) {
				compiler_parsing.next();
				final WrExpr exprInterface = compiler_parsing.type();
				final ImplementsNode implNode = new ImplementsNode();
				implNode.typeOrSymbol = typeOrSymbol;
				implNode.exprInterface = exprInterface;
				nodeListAux.add(implNode);
			}
			else if ( sym.token == Token.IN ) {
				final String typeName = typeOrSymbol.ifPrototypeReturnsItsName();
				boolean isSymbol = false;
				isSymbol = Lexer.isSymbol(typeName) && !typeName.startsWith(Token.TYPEOF.toString() + "(") ;
				if ( isSymbol ) {
					compiler_parsing.removeLastExprStat();
					compiler_parsing.next();
					final List<WrExprIdentStar> symbolList = new ArrayList<>();

					if ( compiler_parsing.getSymbol().token != Token.LEFTSB ) {
						compiler_parsing.error(compiler_parsing.getSymbol(), "'[' was expected");
						return null;
					}
					compiler_parsing.next();

					if ( compiler_parsing.getSymbol().token != Token.IDENT ) {
						compiler_parsing.error(compiler_parsing.getSymbol(), "An identifier was expected");
						return null;
					}

					while ( compiler_parsing.getSymbol().token == Token.IDENT) {

						sym = compiler_parsing.getSymbol();
						final WrExprIdentStar id = compiler_parsing.parseSingleIdent();
						if ( id == null ) {
							compiler_parsing.error(sym, "An identifier was expected after ','");
							return null;
						}
						symbolList.add(id);

						if ( compiler_parsing.getSymbol().token == Token.COMMA ) {
							compiler_parsing.next();
							if ( compiler_parsing.getSymbol().token != Token.IDENT ) {
								compiler_parsing.error(compiler_parsing.getSymbol(), "An identifier was expected after ','");
								return null;
							}
						}
						else {
							if ( compiler_parsing.getSymbol().token != Token.RIGHTSB ) {
								compiler_parsing.error(compiler_parsing.getSymbol(), "']' was expected. Found '" + compiler_parsing.getSymbol().getSymbolString() + "'");
								return null;
							}
							else {
								compiler_parsing.next();
							}
							break;
						}
					}

					final SymbolInNode inNode = new SymbolInNode();
					inNode.typeOrSymbol = typeOrSymbol;
					inNode.symbolList = symbolList;
					inNode.isSymbol = true;
					nodeListAux.add(inNode);
				}
				else {
					compiler_parsing.next();
					final List<WrExpr> typeList = new ArrayList<>();

					if ( compiler_parsing.getSymbol().token != Token.LEFTSB ) {
						compiler_parsing.error(compiler_parsing.getSymbol(), "'[' was expected");
						return null;
					}
					compiler_parsing.next();

					if ( ! compiler_parsing.startType(compiler_parsing.getSymbol().token) ) {
						compiler_parsing.error(compiler_parsing.getSymbol(), "A type was expected");
						return null;
					}

					while ( compiler_parsing.startType(compiler_parsing.getSymbol().token) ) {

						final WrExpr exprType = compiler_parsing.type();
						typeList.add(exprType);

						if ( compiler_parsing.getSymbol().token == Token.COMMA ) {
							compiler_parsing.next();
							if ( ! compiler_parsing.startType(compiler_parsing.getSymbol().token)  ) {
								compiler_parsing.error(compiler_parsing.getSymbol(), "A type was expected after ','");
								return null;
							}
						}
						else {
							if ( compiler_parsing.getSymbol().token != Token.RIGHTSB ) {
								compiler_parsing.error(compiler_parsing.getSymbol(), "']' was expected. Found '" + compiler_parsing.getSymbol().getSymbolString() + "'");
								return null;
							}
							else {
								compiler_parsing.next();
							}
							break;
						}
					}
					final TypeInNode inNode = new TypeInNode();
					inNode.typeOrSymbol = typeOrSymbol;
					inNode.typeList = typeList;
					inNode.isSymbol = false;
					nodeListAux.add(inNode);

				}
			}
			else if ( sym.getSymbolString().equals("subprototype") ) {
				compiler_parsing.next();
				final WrExpr subtype = compiler_parsing.type();
				final SubprototypeNode subtypeNode = new SubprototypeNode();
				subtypeNode.typeOrSymbol = typeOrSymbol;
				subtypeNode.exprSuperprototype = subtype;
				nodeListAux.add(subtypeNode);
			}
			else if ( sym.getSymbolString().equals("superprototype") ) {
				compiler_parsing.next();
				final WrExpr supertype = compiler_parsing.type();
				final SuperprototypeNode supertypeNode = new SuperprototypeNode();
				supertypeNode.typeOrSymbol = typeOrSymbol;
				supertypeNode.exprSubtype = supertype;
				nodeListAux.add(supertypeNode);
			}
			else if ( sym.getSymbolString().equals("has") ) {

				compiler_parsing.next();

				if ( compiler_parsing.getSymbol().token != Token.LEFTSB ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "'[' was expected");
					return null;
				}
				compiler_parsing.next();

				final List<WrMethodSignature> methodSignatureList = new ArrayList<>();
				final List<String> errorMessageList = new ArrayList<>();
				while ( compiler_parsing.getSymbol().getSymbolString().equals("func") ) {

					compiler_parsing.next();
					final WrMethodSignature ms = this.methodSignature(compiler_parsing);

					methodSignatureList.add(ms);

					if ( compiler_parsing.getSymbol().token == Token.COMMA ) {
						compiler_parsing.next();
					}
					if ( compiler_parsing.getSymbol().token == Token.LITERALSTRING ) {
						errorMessageList.add(MetaHelper.removeQuotes(compiler_parsing.getSymbol().getSymbolString()) );
						compiler_parsing.next();
						if ( compiler_parsing.getSymbol().token == Token.COMMA ) {
							compiler_parsing.next();
						}
					}
					else {
						errorMessageList.add(null);
					}

				}
				if ( compiler_parsing.getSymbol().token != Token.RIGHTSB ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "']' was expected. Found '" + compiler_parsing.getSymbol().getSymbolString() + "'");
					return null;
				}
				else {
					compiler_parsing.next();
				}
				final TypeHasNode inNode = new TypeHasNode();
				inNode.typeOrSymbol = typeOrSymbol;
				inNode.methodSignatureList = methodSignatureList;
				inNode.errorMessageList = errorMessageList;
				nodeListAux.add(inNode);
			}
			else {
				compiler_parsing.error(compiler_parsing.getSymbol(), "A concept restriction was expected. Found '" +
			             compiler_parsing.getSymbol().getSymbolString() + "'");
			}
			nodeListAux.get(nodeListAux.size()-1).precededByNot = precededByNot;
			nodeListAux.get(nodeListAux.size()-1).isIdentifier = isIdentifier;
			nodeListAux.get(nodeListAux.size()-1).name = gpName;
			if ( compiler_parsing.getSymbol().token != Token.COMMA ) {
				if ( compiler_parsing.getSymbol().token != Token.EOLO ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "',' expected");
				}
				else
					break;
			}
			compiler_parsing.next();

			if ( compiler_parsing.getSymbol().token == Token.LITERALSTRING ) {
				nodeListAux.get(nodeListAux.size()-1).errorMessage = MetaHelper.removeQuotes(compiler_parsing.getSymbol().getSymbolString());
				compiler_parsing.next();
				if ( compiler_parsing.getSymbol().token != Token.COMMA ) {
					if ( compiler_parsing.getSymbol().token != Token.EOLO ) {
						compiler_parsing.error(compiler_parsing.getSymbol(), "',' expected");
					}
					else
						break;
				}
				compiler_parsing.next();
			}
			if ( !(compiler_parsing.startType(compiler_parsing.getSymbol().token) ||
					compiler_parsing.getSymbol().token == Token.NOT) ) {
				compiler_parsing.error(compiler_parsing.getSymbol(), "after a ',' it was expected a type or the end of the source code");
			}
		}
		if ( compiler_parsing.getSymbol().token != Token.EOLO ) {
			compiler_parsing.error(compiler_parsing.getSymbol(), "Unexpected symbol: '" + compiler_parsing.getSymbol().getSymbolString() + "'");
		}
		return nodeListAux;
	}


	/**
	   @param compiler_parsing
	   @param typeOrSymbol
	   @return
	 */
	private static List<String> parseParamNameList(ICompiler_parsing compiler_parsing,
			final WrExpr typeOrSymbol) {
		final List<String> paramNameList = new ArrayList<>();
		while ( compiler_parsing.getSymbol().token != Token.RIGHTPAR ) {
			String id = compiler_parsing.getSymbol().getSymbolString();
			compiler_parsing.next();
			while ( compiler_parsing.getSymbol().token == Token.PERIOD ) {
				compiler_parsing.next();
				id += ".";
				if ( compiler_parsing.getSymbol().token != Token.IDENT ) {
					compiler_parsing.error(typeOrSymbol.getFirstSymbol(), "An identifier was expected after '.'");
					return null;
				}
				id += compiler_parsing.getSymbol().getSymbolString();
				compiler_parsing.next();
			}
			paramNameList.add( id );
			if ( compiler_parsing.getSymbol().token == Token.COMMA ) {
				compiler_parsing.next();
			}
			else {
				break;
			}
		}
		return paramNameList;
	}


	/**
	 * S ::= ( NS )
	 * NS ::= N { , N }
	 * N ::= I { . I } [ < NS > ]
	 *
	   @param compiler_parsing
	   @param typeOrSymbol
	   @return
	 */
	private static List<String> parseParamNameList2(ICompiler_parsing compiler_parsing,
			final WrExpr typeOrSymbol) {
		final List<String> paramNameList = new ArrayList<>();

		while ( compiler_parsing.getSymbol().token != Token.RIGHTPAR ) {
			String id = parseParamName(compiler_parsing, typeOrSymbol);
			if ( id == null ) { return null; }
			paramNameList.add( id );
			if ( compiler_parsing.getSymbol().token == Token.COMMA ) {
				compiler_parsing.next();
			}
			else {
				break;
			}
		}
		return paramNameList;
	}

	/**
	 * N ::= I { . I } [ < NS > ]

	   @param compiler_parsing
	   @param typeOrSymbol
	   @return
	 */
	private static String parseParamName(ICompiler_parsing compiler_parsing,
			WrExpr typeOrSymbol) {
		String id = compiler_parsing.getSymbol().getSymbolString();
		compiler_parsing.next();
		while ( compiler_parsing.getSymbol().token == Token.PERIOD ) {
			compiler_parsing.next();
			id += ".";
			if ( compiler_parsing.getSymbol().token != Token.IDENT ) {
				compiler_parsing.error(typeOrSymbol.getFirstSymbol(),
						"An identifier was expected after '.'");
				return null;
			}
			id += compiler_parsing.getSymbol().getSymbolString();
			compiler_parsing.next();
		}
		if ( compiler_parsing.getSymbol().token == Token.LT_NOT_PREC_SPACE ) {
			compiler_parsing.next();
			List<String> ls = parseParamNameList2(compiler_parsing, typeOrSymbol);
			int size = ls.size();
			id += "<";
			for ( String s : ls ) {
				id += s;
				if ( --size > 0 ) { id += ", "; }
			}
			id += ">";
			if ( compiler_parsing.getSymbol().token != Token.GT ) {
				compiler_parsing.error(typeOrSymbol.getFirstSymbol(),
						"'>' was expected");
				return null;
			}
			compiler_parsing.next();
		}
		return id;
	}


	@Override
	public void bsa_checkPrototype(ICompiler_semAn compiler){
		// // List<Node> nodeList = (List<Node> ) this.metaobjectAnnotation.getInfo_parsing();

		for ( final Node node : nodeList ) {
			node.calcInternalTypes(compiler.getEnv(), new HashSet<String>() );
		}


		List<Function0> functionList;
		for ( final Node node : nodeList ) {
			functionList = node.check( (WrAnnotationAt ) this.annotation, compiler);
			if ( functionList != null ) {
				for ( final Function0 f : functionList ) {
					f.eval();
				}
			}
		}
	}



	private void createTestCasesForGenericParameters(List<Node> nodeList1, ICompiler_afterResTypes compiler, String dirName) {

		final HashMap<String, ProtoInfo> map = new HashMap<>();

		final IDeclaration dec = this.getAnnotation().getDeclaration();
		final WrPrototype pu = (WrPrototype ) dec;
		final List<List<WrGenericParameter>> gpListList = pu.getGenericParameterListList(compiler.getEnv());
		//Hashtable<String, String> mapFormalToRealParameter = new Hashtable<>();
		for ( final List<WrGenericParameter> gpList : gpListList ) {
			for ( final WrGenericParameter gp : gpList ) {
				map.put(gp.getName(), new ProtoInfo(gp.getName()));

			}
		}

		/*
		 * 	public static char []replaceOnly(char []text,  Hashtable<String, String> formalRealTable, String currentPrototypeName,
			ReplacementPolicyInGenericInstantiation replacementPolicy) {

		 */

		final List<String> errorMessageList = collectGenericParameterData(map,  nodeList1);
		if ( errorMessageList.size() > 0 ) {
			for ( final String errorMessage : errorMessageList ) {
				compiler.error(this.annotation.getFirstSymbol(), errorMessage);
			}
		}
		else {
			final String testPackageName = compiler.getPackageNameTest() + "." + dirName;
			final List<ProtoInfo> piList = this.topologicalSortingProtoInfoList(map, compiler.getEnv());
 			for ( final ProtoInfo info : piList )  {
				// ProtoInfo info = map.get(param);
				if ( info.isSymbol )
					continue;
				if ( ! checkPrototypeInfo(info, compiler, map) )
					continue;
				final StringBuffer code = createTestCase(compiler, info, map, testPackageName);
				if ( code != null ) {
					compiler.writeTestFileTo(code, info.name + "." + MetaHelper.cyanSourceFileExtension, dirName);
				}
			}
 			/*
 			StringBuffer allAxiomMethods = null;
 			for ( Node node : nodeList ) {
 				StringBuffer other = node.axiomTestCode_semAn();
 				if ( other != null ) {
 					if ( allAxiomMethods == null ) {
 						allAxiomMethods = new StringBuffer();
 						allAxiomMethods.append("package " + testPackageName + "\n\n");
 						allAxiomMethods.append("object AxiomTest\n");
 					}
 					allAxiomMethods.append(other);
 				}
 			}
 			if ( allAxiomMethods != null && allAxiomMethods.length() > 1 ) {
				allAxiomMethods.append("\nend\n");
 				compiler.writeTestFileTo(allAxiomMethods, "AxiomTest." + meta.MetaHelper.cyanSourceFileExtension, dirName);
 			}

 			*/
		}

	}

	private boolean checkPrototypeInfo(ProtoInfo info, ICompiler_afterResTypes compiler,
			HashMap<String, ProtoInfo> map) {
		/*
    	public String name;
    	public boolean isPrototype = false;
    	public boolean isInterface = false;
    	public boolean isSymbol = false;
    	public List<String> superprototypeList = null;
    	public List<String> subprototypeList = null;
    	public List<String> interfaceImplementList = null;
    	public List<MethodSignature> methodSignatureList = null;
    	public List<String> inList = null;
    	public List<String> axiomList = null;
	 *
	 */
		final WrEnv env = compiler.getEnv();
		int isNumber = info.isPrototype ? 1 : 0;
		isNumber += info.isInterface ? 1 : 0;
		isNumber += info.isSymbol ? 1 : 0;
		if ( isNumber > 1 ) {
			compiler.error(this.annotation.getFirstSymbol(),
					"'" + info.name + "' is considered by this concept two incompatible things in the following list: prototype, interface, and symbol");
		}
		info.allMethodSignatures = new HashSet<>();

		if ( info.isPrototype && info.superprototypeList != null && info.superprototypeList.size() > 1 ) {
			// interfaces can have more than one super-prototype
			final String supername = info.superprototypeList.get(0);
			for (int i = 1; i < info.superprototypeList.size(); ++i) {
				if ( ! supername.equals(info.superprototypeList.get(i)) ) {
					compiler.error(this.annotation.getFirstSymbol(), "This concept "
							+ "demands that the generic parameter '" + info.name + "' have two superprototypes: '"
							+ supername + "' and '" + info.superprototypeList.get(i) + "'");
					break;
				}
			}
		}
		info.overrideMethodSignatureList = new ArrayList<>();
		if ( info.superprototypeList != null ) {
			for ( final String supertypeName : info.superprototypeList ) {  // just one for ObjectDec. It may be several for InterfaceDe
				final ProtoInfo supertypeInfo = map.get(supertypeName);
				if ( supertypeInfo != null ) {

					/* supertypeName is a generic parameter
					 * due to the topological ordering, supertypeName has already been processed by this method
					 */
					if ( info.methodSignatureList != null ) {
						final List<WrMethodSignature> toBeRemovedList = new ArrayList<>();
						for ( final WrMethodSignature ms : info.methodSignatureList ) {
							final String fullName_ms = ms.getFullName(env);
							if ( supertypeInfo.allFullNameMethodSignatures.contains(fullName_ms) ) {
								info.overrideMethodSignatureList.add(ms);
								toBeRemovedList.add(ms);
							}
						}
						for ( final WrMethodSignature ms : toBeRemovedList ) {
							info.methodSignatureList.remove(ms);
						}

					}

					info.allMethodSignatures.addAll(supertypeInfo.allMethodSignatures);
				}
				else {

					/*
					 * supertypeName is not a generic parameter
					 */
					WrPrototype whoIsAsking = compiler.getCompilationUnit().getPublicPrototype();

					final WrType pu = env.searchPackagePrototype(supertypeName, this.annotation.getFirstSymbol());
					if ( pu == null ) {
						compiler.error(this.annotation.getFirstSymbol(), "'" + supertypeName + "', used in the concept of this "
								+ "generic prototype, was not found");
						return false;
					}
					/*
					if ( !(pu instanceof ObjectDec) ) {
						compiler.error(this.metaobjectAnnotation.getFirstSymbol(), "'" + supertypeName + "', used in the concept of this "
								+ "generic prototype, was expected to be a prototype (not an interface)");
					}
					*/
					if ( pu instanceof WrPrototype && ! ((WrPrototype ) pu).isInterface() ) {
						final WrPrototype supertype = (WrPrototype ) pu;
						if ( supertype.getIsAbstract(env) ) {
							/**
							 * collect the signatures of all abstract methods
							 */
							WrPrototype current = supertype;
							while ( current != null ) {
								if ( current.getIsAbstract(env) ) {
									for ( final WrMethodDec method : current.getMethodDecList(env) ) {
										if ( method.isAbstract() ) {
											info.overrideMethodSignatureList.add(method.getMethodSignature());
										}
									}
								}
								current = current.getSuperobject(env);
							}
						}
						for ( final WrMethodDec method : supertype.getMethodDecList(env) ) {
							info.allMethodSignatures.add(method.getMethodSignature());
						}

						/* get all method signatures */
						for ( final WrPrototype superPU : supertype.getAllSuperPrototypes(env) ) {
							/*
							 * init and init: methods do not need to be preceded by 'override'
							 */
							for ( final WrMethodDec method : superPU.getMethodDecList(env) ) {
								info.allMethodSignatures.add(method.getMethodSignature());
							}
						}
					}
					else if ( pu instanceof WrPrototype && ((WrPrototype ) pu).isInterface() ) {
						// an interface may have several super-prototypes
						final WrPrototype superInter = (WrPrototype ) pu;
						for ( final WrMethodSignature ms : superInter.getMethodSignatureList(env) ) {
							info.allMethodSignatures.add(ms);
						}
						for ( final WrPrototype superPU : superInter.getAllSuperPrototypes(env) ) {
							for ( final WrMethodSignature ms : superPU.getMethodSignatureList(env) ) {
								info.allMethodSignatures.add(ms);
							}
						}
					}
				}
			}
		}
		if ( info.interfaceImplementList != null && info.interfaceImplementList.size() > 1 ) {
			// delete interfaces that appear two times
			final HashSet<String> interfaceSet = new HashSet<>();
			for ( final String intername : info.interfaceImplementList ) {
				interfaceSet.add(intername);
			}
			if ( interfaceSet.size() != info.interfaceImplementList.size() ) {
				info.interfaceImplementList.clear();
				info.interfaceImplementList.addAll(interfaceSet);
			}
		}

		/*
		collect all super interfaces first because interfaces may repeat:
			 A implements I1
			 A implements I2

	    and I2 implements I1

	    // In overrideMethodSignatureList, put

	    */
		if ( info.interfaceImplementList != null ) {
			for ( final String interfaceName : info.interfaceImplementList ) {
				final ProtoInfo interInfo = map.get(interfaceName);
				if ( interInfo != null ) {
					/*
					 * interfaceName is a generic parameter already processed by this method
					 */
					info.allMethodSignatures.addAll(interInfo.allMethodSignatures);
					info.overrideMethodSignatureList.addAll(interInfo.allMethodSignatures);
				}
				else {
					WrPrototype whoIsAsking = compiler.getCompilationUnit().getPublicPrototype();

					final WrType pu = env.searchPackagePrototype(interfaceName, this.annotation.getFirstSymbol());
					if ( pu == null ) {
						compiler.error(this.annotation.getFirstSymbol(), "'" + interfaceName + "', used in the concept of this "
								+ "generic prototype, was not found");
						return false;
					}
					if ( !(pu instanceof WrPrototype) || !((WrPrototype ) pu).isInterface() ) {
						compiler.error(this.annotation.getFirstSymbol(), "'" + interfaceName + "', used in the concept of this "
								+ "generic prototype, was expected to be an interface");
					}
					final WrPrototype inter = (WrPrototype ) pu;
					/*
					 * collect the signatures of the interface
					 */
					for ( final WrMethodSignature ms : inter.getMethodSignatureList(env) ) {
						info.overrideMethodSignatureList.add(ms);
						info.allMethodSignatures.add(ms);
					}
					/*
					 * collect the signatures of the super-interfaces
					 */
					for ( final WrPrototype superPU : inter.getAllSuperPrototypes(env) ) {
						// InterfaceDec superInter = (InterfaceDec ) superPU;
						for ( final WrMethodSignature ms : superPU.getMethodSignatureList(env) ) {
							info.overrideMethodSignatureList.add(ms);
							info.allMethodSignatures.add(ms);
						}

					}
				}
			}
		}

		final HashSet<String> overrideMethodSignatureSet = new HashSet<>();
		if ( info.overrideMethodSignatureList.size() > 0 ) {
			// delete signatures that appear two times
			final List<WrMethodSignature> msList = new ArrayList<>();
			for ( final WrMethodSignature ms : info.overrideMethodSignatureList ) {
				final String name = ms.getFullName(env);
				if ( overrideMethodSignatureSet.add(name) ) {
					msList.add(ms);
				}
			}
			info.overrideMethodSignatureList = msList;
		}
		info.allFullNameMethodSignatures = new HashSet<>();
		for ( final WrMethodSignature ms : info.allMethodSignatures ) {
			info.allFullNameMethodSignatures.add(ms.getFullName(env));
		}

		if ( info.methodSignatureList != null && info.methodSignatureList.size() > 0 ) {
			/*
			 * this list has signatures specified through 'has' clauses such as
			 *    A has [ func run, func search: String -> String ]
			 */
			// delete signatures that appear two times
			// HashSet<String> msNameSet = new HashSet<>();
			final List<WrMethodSignature> msList = new ArrayList<>();

			/*
			 * put in msList all method signatures of the 'has' list that do not appear in
			 * super-prototypes and implemented interfaces. Put in overrideMethodSignatureList
			 * all method signatures of the 'has' list that appear in super-prototypes
			 * and implemented interfaces.
			 */
			for ( final WrMethodSignature ms : info.methodSignatureList ) {
				final String fullName = ms.getFullName(env);
				if ( ! info.allFullNameMethodSignatures.contains(fullName) ) {
					msList.add(ms);
					info.allMethodSignatures.add(ms);
					info.allFullNameMethodSignatures.add(fullName);
				}
				else {
					info.overrideMethodSignatureList.add(ms);
				}
			}

			/*
			for ( String supertypeName : info.superprototypeList ) {

				ProtoInfo superInfo = map.get(supertypeName);
				if ( superInfo != null ) {
					// supertypeName is a generic parameter
					Prototype superPU = env.searchPackagePrototype(supertypeName, this.metaobjectAnnotation.getFirstSymbol());
					if ( superPU instanceof ObjectDec ) {
						ObjectDec superObj = (ObjectDec ) superPU;


						 // list of real interfaces implemented by 'info'

						List<InterfaceDec> implInterfaceList = new ArrayList<>();
						for ( String interfaceName : info.interfaceImplementList ) {
							if ( ! map.containsKey(interfaceName) ) {
								Prototype pu = env.searchPackagePrototype(interfaceName, this.metaobjectAnnotation.getFirstSymbol());
								InterfaceDec inter = (InterfaceDec ) pu;
								implInterfaceList.add(inter);
							}
						}
						for ( MethodSignature ms : info.methodSignatureList ) {
							// for each signature specified using a 'has' clause
							String fullName = ms.getFullName(env);
							if ( msNameSet.add(fullName) && ! overrideMethodSignatureSet.contains(fullName) ) {
								String name = ms.getName();
								 // is the signature in the super prototype?
								List<MethodSignature> dec_ms_List = superObj.searchMethodPublicSuperPublicProtoAndInterfaces(name, env);
								boolean found = dec_ms_List != null && dec_ms_List.size() > 0;
								if ( ! found ) {
									// is the signature in the super interfaces?
									for ( InterfaceDec inter : implInterfaceList ) {
										dec_ms_List = inter.searchMethodPublicSuperPublic(name, env);
										if ( dec_ms_List != null && dec_ms_List.size() > 0 ) {
											found = true;
											break;
										}
									}
								}
								if ( ! found ) {
									msList.add(ms);
								}
								else {
									// add ms to overrideMethodSignatureList because it appear both in a 'has' restriction and
									// in a super-prototype
									info.overrideMethodSignatureList.add(ms);
								}
							}
						}

					}
					else if ( superPU instanceof InterfaceDec ) {

					}
				}
				else {
					// supertypeName is a real prototype

				}

			}
			*/


			info.methodSignatureList = msList;
		}



		if ( info.isList != null && info.isList.size() > 0 ) {
			for ( final WrExpr e : info.isList ) {
				if ( !(e instanceof WrExprTypeof) ) {
					compiler.error(this.annotation.getFirstSymbol(), "Generic prototype '" + info.name
							+ "' parameter is in the left-hand side of a 'is' predicate and the right-hand side is not 'typeof(aType)'."
							+ " This is illegal");
				}
			}
		}
		return true;
	}

	public StringBuffer createTestCase(ICompiler_afterResTypes compiler, ProtoInfo p, HashMap<String, ProtoInfo> map, String packageName1) {

		if ( p.isSymbol )
			return null;

		final String gpName = p.name;

		final StringBuffer s = new StringBuffer();
		s.append("package " + packageName1 + "\n\n");

		/*
    	public String name;
    	public boolean isPrototype = false;
    	public boolean isInterface = false;
    	public boolean isSymbol = false;
    	public List<String> superprototypeList = null;
    	public List<String> subprototypeList = null;
    	public List<String> interfaceImplementList = null;
    	public List<MethodSignature> methodSignatureList = null;
    	public List<String> inList = null;
    	public List<String> axiomList = null;
	 *
	 */
		if ( p.isInterface ) {
			s.append("interface ");
		}
		else
		    s.append("object ");
		s.append(gpName + " ");
		/*
		if ( gpName.equals("A") ) {
			for ( MethodSignature ms : p.overrideMethodSignatureList ) {
				//System.out.println("override " + ms.getName());
			}
			for ( MethodSignature ms : p.methodSignatureList ) {
				//System.out.println(ms.getName());
			}

		}
		*/
		if ( p.superprototypeList != null ) {
			s.append("extends ");
			int size = p.superprototypeList.size();
			  // interfaces can have more than one super-prototypes
			for ( final String superProto : p.superprototypeList ) {
				s.append(superProto);
				if ( --size > 0 )
					s.append(", ");
			}
		}
		s.append(" ");
		if ( p.interfaceImplementList != null ) {
			s.append("implements ");
			int size = p.interfaceImplementList.size();
			for ( final String inter : p.interfaceImplementList ) {
				s.append(inter);
				if ( --size > 0 )
					s.append(", ");
			}
		}
		s.append("\n");
		if ( p.methodSignatureList != null || p.overrideMethodSignatureList.size() > 0 ) {
			final List<WrMethodSignature> allMethodSignatureList = new ArrayList<>();
			allMethodSignatureList.addAll(p.overrideMethodSignatureList);
			int numMethodWithOverride = p.overrideMethodSignatureList.size();
			if ( p.methodSignatureList != null ) {
				allMethodSignatureList.addAll(p.methodSignatureList);
			}

			for ( final WrMethodSignature ms : allMethodSignatureList ) {
				final String strMS = ms.asString();
				  /*
				   * using string strMS here is a very primitive way of discovering if the method signature
				   * uses 'typeof'. This hopefully will be changed some day
				   */
				if ( hasTypeof(strMS) ) {
					s.append("    @error(\"Method '" + MetaLexer.escapeJavaString(strMS) +  " has 'typeof' in it. I cannot produce a method signature for it\")\n");
					continue;
				}
				if ( --numMethodWithOverride >= 0 ) {
					s.append("    override\n");
				}
				s.append("    func");
				if ( ms instanceof WrMethodSignatureWithKeywords ) {
					final WrMethodSignatureWithKeywords mss = (WrMethodSignatureWithKeywords ) ms;
					for ( final WrMethodKeywordWithParameters sel : mss.getKeywordArray() ) {
						s.append(" " + sel.getName() + " ");
						int size = sel.getParameterList().size();
						for ( final WrParameterDec param : sel.getParameterList() ) {
							String typeStr;
							if ( param.getTypeInDec() != null ) {
								typeStr = param.getTypeInDec().asString();
								if ( ! map.containsKey(typeStr) ) {
									// a type, not a generic parameter
									typeStr = param.getType().getFullName();
									if ( typeStr.startsWith(MetaHelper.cyanLanguagePackageNameDot) ) {
										typeStr = typeStr.substring(MetaHelper.cyanLanguagePackageNameDot.length());
									}
								}
							}
							else {
								typeStr = "Dyn";
							}
							/*
							String paramStr = "";
							if ( param.getName() != null )
								paramStr = " " + param.getName();
							s.append(typeStr + paramStr); */
							s.append(typeStr);
							if ( --size > 0 )
								s.append(", ");
						}

					}

				}
				else if ( ms instanceof WrMethodSignatureUnary ) {
					s.append( " " + ms.getName());
				}
				else if ( ms instanceof WrMethodSignatureOperator ) {
					final WrMethodSignatureOperator msop = (WrMethodSignatureOperator ) ms;
					s.append(" " + msop.getSymbolOperator().getSymbolString() );
					if ( msop.getOptionalParameter() != null ) {

						final WrParameterDec param = msop.getOptionalParameter();
						String typeStr;
						if ( param.getTypeInDec() != null ) {
							typeStr = param.getTypeInDec().asString();
							if ( ! map.containsKey(typeStr) ) {
								// a type, not a generic parameter
								typeStr = param.getType().getFullName();
							}
						}
						else {
							typeStr = "Dyn";
						}
						String paramStr = "";
						if ( param.getName() != null )
							paramStr = " " + param.getName();
						s.append(" " + typeStr + paramStr);
					}
				}
				else {
					compiler.error(this.annotation.getFirstSymbol(), "Internal error at metaobject class '" + this.getClass().getName() + "'");
				}
				if ( ms.getReturnTypeExpr() != null ) {
					String retTypeStr = ms.getReturnTypeExpr().asString();
					if ( ! map.containsKey(retTypeStr) ) {
						retTypeStr = ms.getReturnTypeExpr().getType().getFullName();

						if ( retTypeStr.startsWith(MetaHelper.cyanLanguagePackageNameDot) ) {
							retTypeStr = retTypeStr.substring(MetaHelper.cyanLanguagePackageNameDot.length());
						}

					}
					s.append(" -> " + retTypeStr
							+ " = " + retTypeStr + ";\n" );
				}
				else {
					s.append(" {  } \n");
				}

			}
		}
		s.append("\nend\n");

		return s;
	}




	static boolean hasTypeof(String strMS) {
		int i = strMS.indexOf("typeof");
		if ( i < 0 ) {
			return false;
		}
		else {
			while ( i >= 0 ) {
				if ( i > 0 && Character.isAlphabetic(strMS.charAt(i-1)) ||
						i + 6 < strMS.length() && Character.isAlphabetic(strMS.charAt(i+6)) ) {
					i = strMS.indexOf("typeof", i + 6);
				}
				else {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * for each identifier T that appears in the first position of a concept, such as T in<br>
	 * <code>
	 * T has [ func get -> Int ]<br>
	 * T implements ICompany <br>
	 * </code><br>
	 * add information about it in the data structure ProtoInfo
	 */
	private static List<String> collectGenericParameterData( HashMap<String, ProtoInfo> map,  List<Node> nodeList) {
		final List<String> errorMessageList = new ArrayList<>();
		if ( nodeList != null ) {
			for ( final Node node : nodeList ) {
				if ( node.isIdentifier ) {
					final List<String> emList = node.collectGPData_semAn(map);
					if ( emList != null ) {
						errorMessageList.addAll(emList);
					}
				}
			}

		}
		return errorMessageList;
	}

	private WrMethodSignature methodSignature(ICompiler_parsing compiler_parsing) {

		WrMethodSignature methodSignature = null;
		List<WrMethodKeywordWithParameters> keywordList;
		boolean indexingMethod = false;
		WrExpr returnType = null;


		if ( compiler_parsing.getSymbol().token == Token.LEFTRIGHTSB ) {
			indexingMethod = true;
			compiler_parsing.next();
		}
		final WrSymbol identSymbol = compiler_parsing.getSymbol();

		final boolean isInit = false;
		final boolean isInitShared = false;
		String name = "";

		switch ( compiler_parsing.getSymbol().token ) {
		case IDENT:
			name = compiler_parsing.getSymbol().getSymbolString();
			methodSignature = new WrMethodSignatureUnary(compiler_parsing.getSymbol(), compiler_parsing.getCurrentMethod() );

			if ( indexingMethod )
				compiler_parsing.error(compiler_parsing.getSymbol(), "unary methods cannot be indexing methods (declared with '[]')");

			if ( name.equals("new") ) {
				compiler_parsing.error(compiler_parsing.getSymbol(), "'new' cannot be user-declared. Use 'init' instead");
			}

			compiler_parsing.next();
			break;

		case IDENTCOLON:
			keywordList = new ArrayList<WrMethodKeywordWithParameters>();
			while ( compiler_parsing.getSymbol().token == Token.IDENTCOLON ) {
				final WrMethodKeywordWithParameters keywordWithParameters = new WrMethodKeywordWithParameters(compiler_parsing.getSymbol());
				// if ( name.length() > 0 )
				name = name +  compiler_parsing.getSymbol().getSymbolString();
				compiler_parsing.next();

				if ( compiler_parsing.getSymbol().token == Token.LEFTPAR || compiler_parsing.startType(compiler_parsing.getSymbol().token) ) {
					parameterDecList(compiler_parsing,
							keywordWithParameters.getParameterList());
				}
				keywordList.add(keywordWithParameters);

			}

			methodSignature = new WrMethodSignatureWithKeywords(keywordList, compiler_parsing.getCurrentMethod(), indexingMethod);
			if ( keywordList.size() == 1 ) {
				if ( keywordList.get(0).getName().equals("new:") ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "'new:' cannot be user-declared. Use 'init' instead");
				}
				if ( name.equals("init:") ) {
					if ( keywordList.get(0).getParameterList() == null || keywordList.get(0).getParameterList().size() == 0 ) {
						compiler_parsing.error(keywordList.get(0).getkeyword(),
								"'init:' methods should take parameters");
					}
				}
			}
			else {
				/*
				 * check if any keyword is 'init:' or 'new:'
				 */
				for ( final WrMethodKeywordWithParameters sel : keywordList ) {
					if ( sel.getName().equals("init:") || sel.getName().equals("new:") )
						compiler_parsing.error(sel.getkeyword(),
								"It is illegal to have a keyword with name 'init:' or 'new:'");
				}
			}
			break;
		default:
			// should be an operator
			if ( compiler_parsing.isOperator(compiler_parsing.getSymbol().token) ) {
				if ( compiler_parsing.getSymbol().token == Token.AND || compiler_parsing.getSymbol().token == Token.OR ) {
					compiler_parsing.error(compiler_parsing.getSymbol(), "'&&' and '||' cannot be method names. They can only be used with Boolean values");
				}
				final WrSymbol operatorSymbol = compiler_parsing.getSymbol();
				final WrMethodSignatureOperator mso = new WrMethodSignatureOperator(compiler_parsing.getSymbol(),
						compiler_parsing.getCurrentMethod());
				methodSignature = mso;
				compiler_parsing.next();
				if ( compiler_parsing.startType(compiler_parsing.getSymbol().token) ||
					 compiler_parsing.getSymbol().token == Token.LEFTPAR ||
					 compiler_parsing.getSymbol().token == Token.IDENT ) {  // this last is not really necessary but ...

					boolean leftpar = false;
					if ( compiler_parsing.getSymbol().token == Token.LEFTPAR ) {
						leftpar = true;
						compiler_parsing.next();
					}
					WrSymbolIdent parameterSymbol;
					WrExpr typeInDec = compiler_parsing.type();

					if ( compiler_parsing.getSymbol().token != Token.IDENT ) {

						if ( typeInDec instanceof WrExprIdentStar ) {
							final WrExprIdentStar eisType = (WrExprIdentStar ) typeInDec;
							if ( eisType.getIdentSymbolArray().size() > 1 ) {
								// found a prototype preceded by a package
								parameterSymbol = null;
							}
							else if ( Character.isUpperCase(eisType.getIdentSymbolArray().get(0).getSymbolString().charAt(0)) ) {
								/*
								 *  typeInDec is a type really
								 */
								parameterSymbol = null;
							}
							else {
								/*
								 * no package, starts with a lower case letter. It must be a parameter
								 */
								parameterSymbol = (WrSymbolIdent) eisType.getIdentSymbolArray().get(0);
								typeInDec = null;
							}

						}
						else {
							// typeInDec may be a generic prototype instantiation
							parameterSymbol = null;
						}
					}
					else {
						parameterSymbol = (WrSymbolIdent ) compiler_parsing.getSymbol();

						if ( Character.isUpperCase(parameterSymbol.getSymbolString().charAt(0)) )
							compiler_parsing.error(parameterSymbol, "Variables and parameters cannot start with an uppercase letter");


						compiler_parsing.next();
					}
					if ( leftpar ) {
						if ( compiler_parsing.getSymbol().token != Token.RIGHTPAR ) {
							compiler_parsing.error(compiler_parsing.getSymbol(), "')' expected." + foundSuch(compiler_parsing));
						}
						else {
							compiler_parsing.next();
						}
					}
					final WrParameterDec parameterDec =
							new WrParameterDec( parameterSymbol, typeInDec, compiler_parsing.getCurrentMethod());

					mso.setOptionalParameter( parameterDec );
				}
				else {
					// without parameters: then it should be a unary operator
					if ( ! Compiler.isUnaryOperator(operatorSymbol.token) ) {
						compiler_parsing.error(operatorSymbol, "This operator cannot be used as a unary method. It should take a parameter");
					}
				}
			}
			else {
				compiler_parsing.error(compiler_parsing.getSymbol(),  "A method name was expected. " + CyanMetaobjectConcept.foundSuch(compiler_parsing));
				return null;
			}
		}
		if ( compiler_parsing.getSymbol().token == Token.RETURN_ARROW ) {
			compiler_parsing.next();
//			if ( compiler_parsing.getSymbol().token == Token.REMAINDER ) {
//
//			}
//			else {
//			}
			returnType = compiler_parsing.type();
		}

		methodSignature.setReturnTypeExpr(returnType);
		return methodSignature;
	}


	private void parameterDecList(ICompiler_parsing compiler_parsing, List<WrParameterDec> parameterList) {

		if ( compiler_parsing.getSymbol().token == Token.LEFTPAR ) {
			compiler_parsing.next();
			parameterDecList(compiler_parsing, parameterList);
			if ( compiler_parsing.getSymbol().token != Token.RIGHTPAR )
				compiler_parsing.error(compiler_parsing.getSymbol(), "')' expected after parameter declaration." + foundSuch(compiler_parsing));
			else {
				compiler_parsing.next();
			}
		}
		else {
				paramDec(compiler_parsing, parameterList);
				while ( compiler_parsing.getSymbol().token == Token.COMMA ) {
					compiler_parsing.next();
					paramDec(compiler_parsing, parameterList);
				}
			}
	}

	/**
	 * return true if 'e' is a type, which should start with an upper-case letter without any dots as
	 * "Int", "Program" or it should be a package name followed by a prototype name as
	 * "main.Program", "cyan.lang.Int"
	 */
    static public boolean isType(WrExpr e) {

    	if ( e instanceof WrExprIdentStar ) {
    		final WrExprIdentStar eis = (WrExprIdentStar) e;
    		final String firstName = eis.getIdentSymbolArray().get(0).getSymbolString();
    		return eis.getIdentSymbolArray().size() > 1 ||
    				Character.isUpperCase(firstName.charAt(0));
    	}
    	else
    		return true;
    }


	private static void paramDec(ICompiler_parsing compiler_parsing,
			List<WrParameterDec> parameterList) {


		WrSymbol variableSymbol;
		WrExpr typeInDec = compiler_parsing.type();

		VariableKind parameterType;
		if ( compiler_parsing.getSymbol().token == Token.BITAND ) {
	    	parameterType = VariableKind.LOCAL_VARIABLE_REF;
	    	compiler_parsing.next();
		}
		else {
			parameterType = VariableKind.COPY_VAR;
		}

		if ( compiler_parsing.getSymbol().token != Token.IDENT ) {

			if ( isType(typeInDec) ) {
				variableSymbol = null;
			}
			else {
				// type is in fact the variable, which was given without the type as in
				//     func  at: x {  }
				variableSymbol = typeInDec.getFirstSymbol();
				typeInDec = null;
			}

		}
		else {
			variableSymbol = compiler_parsing.getSymbol();
			if ( Character.isUpperCase(variableSymbol.getSymbolString().charAt(0)) )
				compiler_parsing.error(variableSymbol, "Variables and parameters cannot start with an uppercase letter");

			compiler_parsing.next();
		}



		final WrParameterDec parameterDec = new WrParameterDec(
				(WrSymbolIdent ) variableSymbol, typeInDec, compiler_parsing.getCurrentMethod() );
		parameterDec.setVariableKind(parameterType);
		parameterList.add( parameterDec );

	}

	private static String foundSuch(ICompiler_parsing compiler_parsing) {
		String s = " Found '" + compiler_parsing.getSymbol().getSymbolString() + "'";
		if ( compiler_parsing.getSymbol() instanceof WrSymbolKeyword ) {
			s = s + " which is a Cyan keyword";
		}
		return s;
	}

	@Override
	public void parsing_actionPrototypeLater(ICompilerPrototypeLater_parsing compiler) {


		final List<Object> paramList = this.getAnnotation().getJavaParameterList();
		if ( paramList != null && paramList.size() == 1 && paramList.get(0) instanceof String ) {
			final String p = MetaHelper.removeQuotes((String ) paramList.get(0));
			if ( p.equals("test")  ) {
				if ( compiler.getParsingPhase() == ParsingPhase.dpaGeneric ) {
					/*
					 * the argument to the metaobject annotation is 'test' and the
					 * compiler is in the second parse phase
					 */
					/*
					 * if filename is the name of a file that holds a generic prototype,
					 * write test files in the test directory
					 */

					if ( ! isInstantiatedGenericPrototype(compiler)) {
						compiler.addToListAfter_afterResTypes(this);
					}

				}
				else if ( compiler.getParsingPhase() == ParsingPhase.dpaNonGeneric ) {

					if ( isInstantiatedGenericPrototype(compiler)) {
						compiler.addToListAfter_afterResTypes(this);
					}

				}
			}
		}

		final WrAnnotationAt annotation1 = this.getAnnotation();
		final IDeclaration dec = annotation1.getDeclaration();
		WrPrototype inside = null;
		if ( !(dec instanceof WrPrototype) ) {
			if ( dec instanceof WrType ) {
				final WrType t = ((WrType ) dec).getInsideType();
				if ( t instanceof WrPrototype ) {
					inside = (WrPrototype ) t;
				}
			}
		}
		else {
			inside = (WrPrototype ) dec;
		}
		if ( inside == null ) {
			compiler.error(this.annotation.getFirstSymbol(), "This metaobject should be attached to a prototype");
			return ;
		}
		compilationUnitGenericPrototype = inside.getCompilationUnit(compiler.getEnv());

	}

	@Override
	public WrCompilationUnit getCompilationUnit() {
		return compilationUnitGenericPrototype;
	}



	@Override
	public void after_afterResTypes_action(ICompiler_afterResTypes compiler) {
		// // List<Node> nodeList = (List<Node> ) this.metaobjectAnnotation.getInfo_parsing();


		final String filename = ((WrPrototype ) this.getAnnotation().getDeclaration())
				.getCompilationUnit(compiler.getEnv()).getFilename();
		final int index = filename.indexOf('(');
		WrEnv env = compiler.getEnv();
		if ( index >= 0 && index < filename.length() - 1 && Character.isDigit(filename.charAt(index+1)) ) {
			/*
			 * a generic prototype
			 */



			final WrAnnotationAt annotation = this.getAnnotation();
			final IDeclaration dec = annotation.getDeclaration();
			final WrPrototype pu = (WrPrototype ) dec;

			final String protoName = pu.getSimpleName() + "_Test";
			final StringBuffer s = new StringBuffer();
			final String dirName = protoName.toLowerCase();
			final String testPackageName = compiler.getPackageNameTest() + "." + dirName;
			s.append("package " + testPackageName + "\n\n");
			/*
			for ( String importedPackageName : cunit.getImportedPackageNameList() ) {
				s.append(importedPackageName + "\n");
			}
			*/
			s.append("object " + protoName + "\n");
			s.append("    func run {\n");
			final String name = pu.getFullName();
			s.append("        var " + name + " testVar;\n");
			s.append("        \n");
			s.append("    }\n\n");
			s.append("end\n");

			compiler.deleteDirOfTestDir(dirName);
			compiler.writeTestFileTo(s, protoName + "." + MetaHelper.cyanSourceFileExtension, dirName);

			final List<List<WrGenericParameter>> gpListList = pu.getGenericParameterListList(env);
			final HashSet<String> gpSet = new HashSet<>();
			for ( final List<WrGenericParameter> gpList : gpListList ) {
				for ( final WrGenericParameter gp : gpList ) {
					gpSet.add(gp.getName());
				}
			}
			for ( final Node node : nodeList ) {
				node.calcInternalTypes( compiler.getEnv(), gpSet );
			}

			createTestCasesForGenericParameters(nodeList, compiler, dirName);

		}
		else {
			/*
			 * an instantiated generic prototype or a regular prototype
			 */




			final WrAnnotationAt annotation = this.getAnnotation();
			final IDeclaration dec = annotation.getDeclaration();
			final WrPrototype pu = (WrPrototype ) dec;

			String protoName = pu.getName() + "_Axiom_Test";
			protoName = protoName.replaceAll("<", "_lt_");
			protoName = protoName.replaceAll(">", "_gt_");
			protoName = protoName.replaceAll("\\.", "_d_");
			protoName = protoName.replaceAll(",", "_c_");
			final StringBuffer s = new StringBuffer();
			final String dirName = protoName.toLowerCase();
			final String testPackageName = compiler.getPackageNameTest() + "." + dirName;
			s.append("package " + testPackageName + "\n\n");
			/*
			for ( String importedPackageName : cunit.getImportedPackageNameList() ) {
				s.append(importedPackageName + "\n");
			}
			*/
			s.append("object " + protoName + "\n");

			int numberNextMethod = 0;
 			StringBuffer allAxiomMethods = null;
 			for ( final Node node : nodeList ) {
 				final Tuple2<StringBuffer, Integer> t = node.axiomTestCode_semAn(numberNextMethod);
 				if ( t != null ) {
 	 				final StringBuffer other = t.f1;
 	 				if ( other != null ) {
 	 	 				numberNextMethod = t.f2;
 	 					if ( allAxiomMethods == null ) {
 	 						allAxiomMethods = new StringBuffer();
 	 					}
 	 					allAxiomMethods.append(other);
 	 				}
 				}
 			}
 			if ( allAxiomMethods != null ) {
 				s.append(allAxiomMethods);
 			}
			s.append("end\n");
 			if ( allAxiomMethods != null && allAxiomMethods.length() > 1 ) {
 				compiler.writeTestFileTo(s, protoName + "." + MetaHelper.cyanSourceFileExtension, dirName);
 			}

			compiler.deleteDirOfTestDir(dirName);
			compiler.writeTestFileTo(s, protoName + "." + MetaHelper.cyanSourceFileExtension, dirName);



		}
	}


	private List<ProtoInfo> topologicalSortingProtoInfoList(HashMap<String, ProtoInfo> map, WrEnv env) {
    	/*
    	 * program unit, super-prototype list, sub-prototype list
    	 */
    	final HashMap<String, Tuple2<List<ProtoInfo>, List<ProtoInfo>> > protoNameAdjList = new HashMap<>();
    	final List<ProtoInfo> noSuperList = new ArrayList<>();
    	final List<ProtoInfo> prototypeList = new ArrayList<>();
    	/**
    	 * collect the formal parameters
    	 */
    	for ( final String key : map.keySet() ) {
    		final ProtoInfo value = map.get(key);
    		prototypeList.add(value);
    		final List<ProtoInfo> superList = new ArrayList<>();
    		final List<ProtoInfo> subList = new ArrayList<>();
    		protoNameAdjList.put(value.name,  new Tuple2<>(superList, subList));
    	}
        /**
         * build the graph of sub-type and super-type relationships
         */
        for ( final ProtoInfo pu : prototypeList ) {
        	final Tuple2<List<ProtoInfo>, List<ProtoInfo>> t = protoNameAdjList.get(pu.name);
        	if ( t == null ) {
        		env.error(null,  "Internal error: program unit '" + pu.name + "' was not found in topological sorting (Concept metaobject)");
        		return null;
        	}
    		final List<ProtoInfo> superList = t.f1;

    		boolean foundSuper = false;
    		if ( pu.superprototypeList != null && pu.superprototypeList.size() != 0 ) {
    			for ( final String superName : pu.superprototypeList ) {
    				final ProtoInfo superProto =  map.get(superName);
    				if ( superProto != null ) {
    					// superProto is a generic parameter
    					superList.add(superProto);
    					final Tuple2<List<ProtoInfo>, List<ProtoInfo>> superT = protoNameAdjList.get(superName);
    					superT.f2.add(pu);
    					foundSuper = true;
    				}
    			}
    		}
    		if ( pu.interfaceImplementList != null && pu.interfaceImplementList.size() != 0 ) {
    			for ( final String interName : pu.interfaceImplementList ) {
    				final ProtoInfo interProto = map.get(interName);
    				if ( interProto != null ) {
    					superList.add(interProto);
    					final Tuple2<List<ProtoInfo>, List<ProtoInfo>> superT = protoNameAdjList.get(interName);
    					superT.f2.add(pu);
    					foundSuper = true;
    				}
    			}
    		}
    		if ( ! foundSuper ) {
    			noSuperList.add(pu);
    		}

        }
        /**
         * do the topological sorting
         */
        final List<ProtoInfo> sortedPrototype = new ArrayList<>();
        while ( noSuperList.size() > 0 ) {
        	final ProtoInfo pu = noSuperList.get(0);
        	noSuperList.remove(0);
        	sortedPrototype.add(pu);

        	final Tuple2<List<ProtoInfo>, List<ProtoInfo>> t = protoNameAdjList.get(pu.name);
    		final List<ProtoInfo> subPUList = t.f2;
    		/*
    		 * remove all edges from sub-types to the super-type pu
    		 */
    		for ( final ProtoInfo subProto : subPUList ) {
            	final Tuple2<List<ProtoInfo>, List<ProtoInfo>> subT = protoNameAdjList.get(subProto.name);
            	/*
            	 * remove pu from the list of super-types of subT
            	 */
            	int i = 0;
            	for ( final ProtoInfo superSubProto : subT.f1 ) {
            		if ( superSubProto == pu ) {
                    	subT.f1.remove(i);
            			break;
            		}
            		++i;
            	}
            	/*
            	 * pu must have been found in list subT.f1 --- no test if it was really found
            	 */
            	if ( subT.f1.size() == 0 ) {
            		/*
            		 * now subProto has no super-types (the edge was removed).
            		 */
            		noSuperList.add(subProto);
            	}

    		}
    		/*
    		 * remove all edges from the super-type pu to its sub-types
    		 */
    		t.f2.clear();
        }

        for ( final ProtoInfo pu : prototypeList ) {
        	final Tuple2<List<ProtoInfo>, List<ProtoInfo>> t = protoNameAdjList.get(pu.name);
    		final List<ProtoInfo> superList = t.f1;
    		if ( superList.size() > 0 ) {
        		final String puName = pu.name;
        		String s = puName;
        		for ( final ProtoInfo superPU : superList ) {
        			final String superFullName = superPU.name;
        			if ( superFullName.equals(puName) ) {
        				break;
        			}
        			s += ", " + superFullName;
        		}
        		s += ", " + puName;
        		env.error(this.getAnnotation().getFirstSymbol(), "Circular subtype relationship among the generic parameters. " +
     	    	       "Something like\nC subprototype B\nB subprototype A\nA subprototype C");
    		}

        }

        return sortedPrototype;
    }

	static boolean isCurrentPrototype(String name) {
		return name.equals("THISPROTOTYPE999");
	}

	/**
	 * the AST of the DSL of the annotation.
	 */
	private List<Node> nodeList;


	WrCompilationUnit compilationUnitGenericPrototype;
}
