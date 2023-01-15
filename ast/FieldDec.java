
package ast;

import java.util.List;
import java.util.Stack;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.AttachedDeclarationKind;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple5;
import meta.VariableKind;
import meta.WrFieldDec;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 * Represents a field declaration
 *
 * @author José
 *
 */

public class FieldDec extends SlotDec
		implements VariableDecInterface, GetNameAsInSourceCode {

	public FieldDec(ObjectDec currentObj, SymbolIdent variableSymbol,
			Expr typeInDec, Expr expr, Token visibility, boolean shared,
			List<AnnotationAt> nonAttachedSlotAnnotationList,
			List<AnnotationAt> attachedSlotAnnotationList, Symbol firstSymbol,
			boolean isReadonly,
			Stack<Tuple5<String, String, String, String, Integer>> annotContextStack) {

		super(currentObj, visibility, attachedSlotAnnotationList,
				nonAttachedSlotAnnotationList);
		this.variableSymbol = variableSymbol;
		this.typeInDec = typeInDec;
		this.expr = expr;
		this.shared = shared;
		variableKind = VariableKind.COPY_VAR;
		this.firstSymbol = firstSymbol;
		javaName = MetaHelper.getJavaName(this.getName());
		this.isReadonly = isReadonly;
		javaPublic = false;
		typeWasChanged = false;
		this.setWasInitialized(expr != null);

		if ( annotContextStack != null && annotContextStack.size() > 0 ) {
			// Stack<Tuple5<String, String, String, String, Integer>> stack =
			// new Stack<>();
			this.annotContextStack = new Stack<>();
			for (final Tuple5<String, String, String, String, Integer> t : annotContextStack) {
				final Tuple5<String, String, String, String, Integer> newT = new Tuple5<String, String, String, String, Integer>(
						t.f1, t.f2, t.f3, t.f4, t.f5);

				this.annotContextStack.push(newT);
			}
			// this.annotContextStack = (Stack<Tuple5<String, String, String,
			// String, Integer>>) annotContextStack.clone();
		}
		else {
			this.annotContextStack = annotContextStack;
		}

	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		if ( expr != null ) {
			expr.accept(visitor);
		}
	}

	@Override
	public void setTypeInDec(Expr typeInDec) {
		this.typeInDec = typeInDec;
	}

	@Override
	public Expr getTypeInDec() {
		return typeInDec;
	}

	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	public Expr getExpr() {
		return expr;
	}

	public void setVariableSymbol(SymbolIdent variableSymbol) {
		this.variableSymbol = variableSymbol;
	}

	@Override
	public SymbolIdent getVariableSymbol() {
		return variableSymbol;
	}

	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {

		super.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.printIdent(NameServer.getVisibilityString(visibility) + " ");
		if ( shared ) pw.print("shared ");
		if ( isReadonly )
			pw.print("let ");
		else
			pw.print("var ");
		if ( typeInDec != null )
			typeInDec.genCyan(pw, false, cyanEnv, genFunctions);
		else {
			// used only in inner objects
			String name1 = type.getFullName();
			final int indexOfCyanLang = name1
					.indexOf(MetaHelper.cyanLanguagePackageName);
			if ( indexOfCyanLang >= 0 )
				name1 = name1.substring(indexOfCyanLang);
			pw.print(name1);
		}
		pw.print(" ");
		switch (variableKind) {
		case LOCAL_VARIABLE_REF:
			pw.print("&");
			break;
		default:
			break;
		}
		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			pw.print(cyanEnv.formalGenericParamToRealParam(
					variableSymbol.getSymbolString()));
		}
		else {
			pw.print(variableSymbol.getSymbolString());
		}

		if ( expr != null ) {
			pw.print(" = ");
			expr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			pw.print(";");
		}
		pw.println();
	}

	@Override
	public void genJava(PWInterface pw, Env env) {

		super.genJava(pw, env);
		pw.printIdent("");
		if ( shared ) pw.print("static ");
		if ( this.javaPublic )
			pw.print("public ");
		else if ( this.visibility == Token.PROTECTED )
			pw.print("protected ");
		else
			pw.print("private ");

		String typeName;
		if ( type != null )
			typeName = type.getFullName();
		/*
		 * else if ( typeInDec != null ) typeName =
		 * typeInDec.ifPrototypeReturnsItsName();
		 */
		else if ( expr != null )
			typeName = expr.getType(env).getFullName();
		else
			typeName = Type.Dyn.getName();

		if ( variableKind != VariableKind.COPY_VAR || refType )
			pw.printIdent("Ref<" + MetaHelper.getJavaName(typeName) + ">");
		else if ( type instanceof TypeJavaRef ) {
			pw.printIdent(typeName);
		}
		else {
			pw.printIdent(MetaHelper.getJavaName(typeName));
		}
		pw.println(" " + MetaHelper.getJavaName(getName()) + ";");
	}

	@Override
	public String getName() {
		if ( name == null ) {
			name = variableSymbol.getSymbolString();
		}
		return name;
	}

	@Override
	public String getNameWithDeclaredTypes() {
		return typeInDec.asString() + " "
				+ this.variableSymbol.getSymbolString();
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public void calcInterfaceTypes(Env env) {
		if ( typeInDec != null && type == null )
			type = typeInDec.ifRepresentsTypeReturnsType(env);
		if ( type == null ) type = Type.Dyn;
	}

	@Override
	public void calcInternalTypes(Env env) {

		super.calcInternalTypesNONAttachedAnnotations(env);
		if ( expr != null ) {
			try {
				env.pushCheckUsePossiblyNonInitializedPrototype(true);
				expr.calcInternalTypes(env);
			}
			finally {
				env.popCheckUsePossiblyNonInitializedPrototype();
			}

			if ( !type.isSupertypeOf(expr.getType(), env) ) {
				env.error(expr.getFirstSymbol(),
						"Expression type is not subtype of the field type");
			}
			if ( shared ) {
				if ( !expr.isNREForInitShared(env) ) {
					env.error(expr.getFirstSymbol(),
							"The expression is not valid for initializing a shared variable. It should be"
									+ " a literal value or the creation of an object of a prototype of package cyan.lang."
									+ " See the Cyan manual for more information.",
							true, false);

				}
			}
			else {
				if ( !expr.isNRE(env) ) {
					env.error(expr.getFirstSymbol(),
							"The expression is not valid for initializing a field. It should be"
									+ " a literal value or the creation of an object."
									+ " See the Cyan manual for more information.",
							true, false);
				}
			}

		}
		final List<MethodSignature> msList = env.getCurrentObjectDec()
				.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
						getName(), env);
		if ( msList != null && msList.size() > 0 ) {
			String thisStr = "Field '" + this.getName() + "' of line "
					+ this.getFirstSymbol().getLineNumber();
			if ( this.annotContextStack != null
					&& this.annotContextStack.size() > 0 ) {
				thisStr += " created by the following stack of annotations\n";
				for (final Tuple5<String, String, String, String, Integer> t : this.annotContextStack) {
					thisStr += "    annotation '" + t.f2 + "' of line " + t.f5
							+ " of file " + t.f4 + "\n";
				}
			}
			final MethodDec anotherMethod = msList.get(0).getMethod();
			String anotherStr = "Method '" + anotherMethod.getName()
					+ "' of line "
					+ anotherMethod.getFirstSymbol().getLineNumber();
			if ( anotherMethod.getAnnotContextStack() != null
					&& anotherMethod.getAnnotContextStack().size() > 0 ) {
				anotherStr += " created by the following stack of annotations\n";
				for (final Tuple5<String, String, String, String, Integer> t : anotherMethod
						.getAnnotContextStack()) {
					anotherStr += "    annotation '" + t.f2 + "' of line "
							+ t.f5 + " of file " + t.f4 + "\n";
				}
			}
			ObjectDec anotherProto = anotherMethod.getDeclaringObject();
			String apName = "";
			if ( anotherProto != this.getDeclaringObject() ) {
				apName = " of prototype " + anotherProto.getFullName();
			}
			env.error(this.getFirstSymbol(),
					"Field name equal to unary method:\n" + thisStr + "\n"
							+ anotherStr + apName);

		}
		final FieldDec anotherField = env.getCurrentObjectDec()
				.searchFieldPrivateProtectedSuperProtected(getName());
		if ( anotherField != this ) {
			String thisStr = "Field '" + this.getName() + "' of line "
					+ this.getFirstSymbol().getLineNumber();
			if ( this.annotContextStack != null
					&& this.annotContextStack.size() > 0 ) {
				thisStr += " created by the following stack of annotations\n";
				for (final Tuple5<String, String, String, String, Integer> t : this.annotContextStack) {
					thisStr += "    annotation '" + t.f2 + "' of line " + t.f5
							+ " of file " + t.f4 + "\n";
				}
			}
			String anotherStr = "Field '" + anotherField.getName()
					+ "' of line "
					+ anotherField.getFirstSymbol().getLineNumber();
			if ( anotherField.getAnnotContextStack() != null
					&& anotherField.getAnnotContextStack().size() > 0 ) {
				anotherStr += " created by the following stack of annotations\n";
				for (final Tuple5<String, String, String, String, Integer> t : anotherField
						.getAnnotContextStack()) {
					anotherStr += "    annotation '" + t.f2 + "' of line "
							+ t.f5 + " of file " + t.f4 + "\n";
				}
			}
			env.error(this.getFirstSymbol(),
					"Duplicated field name:\n" + thisStr + "\n" + anotherStr);

		}
		super.calcInternalTypesAttachedAnnotations(env);
		env.checkSlot(this);

	}

	public boolean isShared() {
		return shared;
	}

	public VariableKind getVariableKind() {
		return variableKind;
	}

	public void setVariableKind(VariableKind variableKind) {
		this.variableKind = variableKind;
		refType = variableKind == VariableKind.LOCAL_VARIABLE_REF;
	}

	@Override
	public Symbol getFirstSymbol() {
		return firstSymbol;
	}

	@Override
	public boolean getRefType() {
		return refType;
	}

	@Override
	public void setRefType(boolean refType) {
		this.refType = refType;
		if ( refType )
			variableKind = VariableKind.LOCAL_VARIABLE_REF;
		else
			variableKind = VariableKind.COPY_VAR;
	}

	@Override
	public String getJavaName() {
		return javaName;
	}

	@Override
	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	public AttachedDeclarationKind getKind() {
		return AttachedDeclarationKind.FIELD_DEC;
	}

	@Override
	public boolean isReadonly() {
		return isReadonly;
	}

	public boolean isContextParameter() {
		return false;
	}

	public void setJavaPublic(boolean javaPublic) {
		this.javaPublic = javaPublic;
	}

	/**
	 * see {@link VariableDecInterface#setTypeWasChanged(boolean)}
	 */

	@Override
	public void setTypeWasChanged(boolean typeWasChanged) {
		this.typeWasChanged = typeWasChanged;
	}

	/**
	 * see {@link VariableDecInterface#setTypeWasChanged(boolean)}
	 */
	@Override
	public boolean getTypeWasChanged() {
		return typeWasChanged;
	}

	public boolean getWasInitialized() {
		return wasInitialized;
	}

	public void setWasInitialized(boolean wasInitialized) {
		this.wasInitialized = wasInitialized;
	}

	public Stack<Tuple5<String, String, String, String, Integer>> getAnnotContextStack() {
		return annotContextStack;
	}

	@Override
	public Object eval(EvalEnv ee) {
		return valueInInterpreter;
	}

	@Override
	public Object getValueInInterpreter() {
		return valueInInterpreter;
	}

	@Override
	public void setValueInInterpreter(Object value) {
		this.valueInInterpreter = value;
	}

	@Override
	public WrFieldDec getI() {
		if ( iFieldDec == null ) {
			iFieldDec = new WrFieldDec(this);
		}
		return iFieldDec;
	}

	@Override
	public String codeAddsRuntimeTypeInfo() {
		Symbol first;
		boolean wasTypeSupplied;
		int fileOffset;
		if ( getTypeInDec() == null ) {
			// type was not supplied
			fileOffset = getFirstSymbol().getOffset();
			first = getFirstSymbol();
			wasTypeSupplied = false;
		}
		else {
			fileOffset = getTypeInDec().getFirstSymbol().getOffset();
			first = getTypeInDec().getFirstSymbol();
			wasTypeSupplied = true;
		}
		/*
		 *
		 * String methodName =
		 * outerCurrentMethod.getMethodSignature().getFullNameWithReturnType();
		 *
		 */

		ObjectDec proto = this.getDeclaringObject();
		ObjectDec protoOuter = proto.getOuterObject();
		if ( protoOuter != null ) {
			String methodName = proto.getExprFunctionForThisPrototype()
					.getCurrentMethod().getMethodSignature()
					.getFullNameWithReturnType();
			proto = protoOuter;
			String prototypeFullName = proto.getFullName();
			String fullJavaName = getJavaName() + (this.refType ? ".elem" : "");

			return "cyanruntime.CyanRuntime.addDynLocalVariableInfo(\""
					+ prototypeFullName + "\", \"" + proto.getSHA256Code()
					+ "\", " + "\"" + methodName + "\", \"" + getName() + "\", "
					+ first.getLineNumber() + ", " + first.getColumnNumber()
					+ ", " + fileOffset + ", " + wasTypeSupplied + ", "
					+ fullJavaName + " instanceof cyan.lang._Nil ? \"Nil\" : ("
					+ "((cyan.lang._Any ) " + fullJavaName
					+ ")._prototypePackageName().s + \".\" +"
					+ "((cyan.lang._Any ) " + fullJavaName
					+ ")._prototypeName().s" + " ) );";

		}
		else {
			String prototypeFullName = proto.getFullName();
			String fullJavaName = getJavaName() + (this.refType ? ".elem" : "");
			return "cyanruntime.CyanRuntime.addDynFieldInfo(\""
					+ prototypeFullName + "\", \""
					+ this.getDeclaringObject().getSHA256Code() + "\", \""
					+ getName() + "\", " + first.getLineNumber() + ", "
					+ first.getColumnNumber() + ", " + fileOffset + ", "
					+ wasTypeSupplied + ", " + fullJavaName
					+ " instanceof cyan.lang._Nil ? \"Nil\" : ("
					+ "((cyan.lang._Any ) " + fullJavaName
					+ ")._prototypePackageName().s + \".\" +"
					+ "((cyan.lang._Any ) " + fullJavaName
					+ ")._prototypeName().s" + " ) );";
		}

	}


	public boolean createdByMetaobjects() {
		return annotContextStack != null && !this.annotContextStack.isEmpty();
	}

	WrFieldDec iFieldDec = null;

	/**
	 * when this class is used for interpreting Cyan code, this is the
	 * valueInInterpreter of the variable
	 */
	private Object													valueInInterpreter;

	/**
	 * see {@link VariableDecInterface#setTypeWasChanged(boolean)}
	 */
	private boolean													typeWasChanged;

	private String													javaName;

	protected SymbolIdent											variableSymbol;
	protected Expr													typeInDec;
	private Expr													expr;
	/**
	 * type of this field
	 */
	protected Type													type;

	/**
	 * true if this variable is shared
	 */
	private final boolean											shared;

	/**
	 * the kind of variable. Regular variables are COPY_VAR variables. But when
	 * the compiler creates a regular prototype from the declaration of a
	 * context object, a field may be "a field parameter" or a "reference
	 * parameter". That is, from a context object object Test(:f1 &Int, :y
	 * *Char, :z %Boolean) ... end the compiler will create a regular object
	 * that has fields f1, y, and z:
	 *
	 * object Test public init: (:f1 &Int, :y *Char, :z %Boolean) [ self.x = f1;
	 * self.y = y; self.z = z; ]
	 *
	 * private :f1 &Int; :y *Char; :z Boolean; end
	 *
	 */
	private VariableKind											variableKind;

	/**
	 * true if this variable was used as a reference type. That is, refType is
	 * true for a variable p if p was used where a reference was expected. For
	 * example, suppose p was used in Sum(p) in which Sum was declared as object
	 * Sum(Int &s) ... end then refType should be true.
	 */
	private boolean													refType;

	/**
	 * first symbol of this declaration
	 */
	private final Symbol											firstSymbol;
	/**
	 * true if this field is read only
	 */
	private final boolean											isReadonly;
	/**
	 * true if the generated code in Java for this field should be a public
	 * field
	 */
	private boolean													javaPublic;

	/**
	 * true if this variable was initialized in the place of its declaration or
	 * previously in an 'init' or 'init:' method
	 */
	private boolean													wasInitialized;

	private String													name	= null;

	/**
	 * the stack of metaobject annotations that created this field
	 */
	private Stack<Tuple5<String, String, String, String, Integer>>	annotContextStack;


}
