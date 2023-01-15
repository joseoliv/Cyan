/**

 */
package meta;

import cyan.lang.CyInt;
import cyan.lang._Array_LT_GP_CyByte_GT;
import cyan.lang._Array_LT_GP_CyString_GT;
import saci.Env;

/**
 * Represent a metaobject annotation using the syntax<br>
 *        {@literal @}meta(parameters)<++ ... ++>
 *
   @author José
 */

public class CyanMetaobjectAtAnnot extends CyanMetaobject {


	public CyanMetaobjectAtAnnot myClone() {
		try {
			return (CyanMetaobjectAtAnnot ) super.clone();
		}
		catch (final CloneNotSupportedException e) {
			return null;
		}
	}


	/**
	 *
	   @param name
	   @param parameterKind
	   @param decKindList
	   @param visibility should be Token.PRIVATE unless other prototypes should
	   be able to see the annotation when it is attached to a prototype or
	   a public method. In this case, it should be Token.PUBLIC.

	   As an example, suppose annotation myAnnot is attached to prototype Q.
	   When a metaobject method of a prototype P accesses a prototype Q that is
	   the type of one of its fields (or method return type or any other type),
	   annotation myAnnot will be listed as attached to Q if it is public. That
	   is, if method getVisibility() of the annotation returns Token.PUBLIC.
	 */
	public CyanMetaobjectAtAnnot(String name, AnnotationArgumentsKind parameterKind,
			AttachedDeclarationKind []decKindList, Token visibility) {
		this.name = name;
		this.parameterKind = parameterKind;
		this.attachedDecKindList = decKindList;
		this.visibility = visibility;
	}

	public CyanMetaobjectAtAnnot(String name, AnnotationArgumentsKind parameterKind,
			AttachedDeclarationKind []decKindList) {
		this.name = name;
		this.parameterKind = parameterKind;
		this.attachedDecKindList = decKindList;
		this.visibility = Token.PRIVATE;
	}


	public CyanMetaobjectAtAnnot(String name, AnnotationArgumentsKind parameterKind) {
		this.name = name;
		this.parameterKind = parameterKind;
		this.attachedDecKindList = null;
		this.visibility = Token.PRIVATE;
	}


	@Feature("nocopy")
	static public AnnotationArgumentsKind fromStringAnnotationArgumentsKind(String kind) {
		for ( AnnotationArgumentsKind aKind : AnnotationArgumentsKind.values() ) {
			if ( aKind.toString().equals(kind) ) {
				return aKind;
			}
		}
		throw new InitMetaobjectErrorException(AnnotationArgumentsKind.class.getName() + " '" + kind + "' does not exist");
	}


	@Feature("nocopy")
	static public AttachedDeclarationKind fromStringAttachedDeclarationKind(String kind) {
		for ( AttachedDeclarationKind aKind : AttachedDeclarationKind.values() ) {
			if ( aKind.toString().equalsIgnoreCase(kind) ) {
				return aKind;
			}
		}
		throw new InitMetaobjectErrorException(AttachedDeclarationKind.class.getName() + " '" + kind + "' does not exist");
	}

	@Feature("nocopy")
	static public AttachedDeclarationKind []fromStringAttachedDeclarationKind(_Array_LT_GP_CyString_GT kindList) {
		int size = kindList._size().n;
//		System.out.println("fromStringAttachedDeclarationKind. size array = " + size);
		AttachedDeclarationKind []r = new AttachedDeclarationKind[size];
		for ( int i = 0; i < size; ++i ) {
			String s = kindList._at_1(new CyInt(i)).s;
//			System.out.println("fromStringAttachedDeclarationKind. kind = " + s);
			r[i] = fromStringAttachedDeclarationKind(s);
//			System.out.println("fromStringAttachedDeclarationKind. kind2 = " + r[i].toString());
		}
		return r;
	}


	@Feature("nocopy")
	static public Token fromStringVisibility(String visibility) {
		switch ( visibility.toLowerCase() ) {
		case "public": return Token.PUBLIC;
		case "private": return Token.PRIVATE;
		case "protected": return Token.PROTECTED;
		case "package": return Token.PACKAGE;
		default:
			throw new InitMetaobjectErrorException("Visibility '" + visibility + "' does not exist");
		}
	}

	@Feature("nocopy")
	public static String []fromCyanArrayStringToJavaArrayString(_Array_LT_GP_CyString_GT array) {
		int size = array._size().n;
		String []r = new String[size];
		for ( int i = 0; i < size; ++i ) {
			r[i] = array._at_1(new CyInt(i)).s;
		}
		return r;
	}

	@Feature("nocopy")
	public static byte []fromCyanArrayByteToJavaArrayByte(_Array_LT_GP_CyByte_GT array) {
		int size = array._size().n;
		byte []r = new byte[size];
		for ( int i = 0; i < size; ++i ) {
			r[i] = array._at_1(new CyInt(i)).n;
		}
		return r;
	}


	@Override
	final public String getName() {
		return name;
	}



	final public AnnotationArgumentsKind getParameterKinds() {
		return parameterKind;
	}


	/**
	 * return true if this metaobject is attached to a
	 * declaration (variable, method, prototype) or statement.
	 * As an example, metaobject javacode is not attached to
	 * anything.
	 *
	   @return
	 */
	final public boolean shouldBeAttachedToSomething() {
		if ( attachedDecKindList == null ) {
			return false;
		}
		else {
			for ( final AttachedDeclarationKind kind : attachedDecKindList ) {
				if ( kind == AttachedDeclarationKind.NONE_DEC ) {
					return false;
				}
			}
			return true;
		}
	}

	final public boolean mayBeAttachedToSomething() {
		if ( attachedDecKindList == null ) {
			return false;
		}
		else {
			for ( final AttachedDeclarationKind kind : attachedDecKindList ) {
				if ( kind != AttachedDeclarationKind.NONE_DEC ) {
					return true;
				}
			}
			return false;
		}
	}


	/**
	 * return an array with all kinds of declaration to which this metaobject may be attached.
	 */
	final public AttachedDeclarationKind []getAttachedDecKindList() {
		return attachedDecKindList;
	}

	/**
	 * return true if the list returned by getAttachedDecKindList is a subset of
	 * the list exclusiveList
	   @param otherList
	   @return
	 */
	final public boolean attachedDecKindListSubsetOf(AttachedDeclarationKind ...otherList) {
		for ( AttachedDeclarationKind attachedDeclarationKind : attachedDecKindList ) {
			boolean found = false;
			for ( AttachedDeclarationKind other : otherList ) {
				if ( other == attachedDeclarationKind ) {
					found = true;
					break;
				}
			}
			if ( ! found ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * return true if this metaobject may be attached to <code>decKind</code>
	 */
	final public boolean mayBeAttachedTo(AttachedDeclarationKind decKind) {
		if ( attachedDecKindList == null )
			return false;
		else {
			for ( final AttachedDeclarationKind d : attachedDecKindList ) {
				if ( d == decKind )
					return true;
			}
			return false;
		}
	}

	final public String attachedListAsString() {
		String s = "";
		if ( attachedDecKindList != null ) {
			int size = attachedDecKindList.length;
			for ( final AttachedDeclarationKind d : attachedDecKindList ) {
				s = s + d.toString();
				if ( --size > 0 )
					s = s + ", ";
			}
		}
		return s;
	}

	/**
	 * returns the declaration to which this metaobject is attached to. It may
	 * be MethodDec, FieldDec etc.
	 */
	final public IDeclaration getAttachedDeclaration() {
		return getAnnotation().getDeclaration();
	}



	/**
	 * return true if this metaobject should necessarily be followed by a text
	 * between two sequences of symbols. For example, metaobject <code>javacode</code>
	 * should always be followed by a Java code: <br>
	 * <code>
	 *          {@literal @}javacode<<*    return _index; *>>  <br>
	 * </code><br>
	 */
	public boolean shouldTakeText() { return false; }

	@Override
	public WrAnnotationAt getAnnotation() {
		return (WrAnnotationAt ) annotation;
	}

	/**
	 * return true if this metaobject is an expression by itself. Then it can be used as in
	 * <code><br>
	 * var n = {@literal @}color(red);
	 * </code><br>
	 *
	 * If this method returns true,
	 *
	 */
	@Override
	public boolean isExpression() {
		return false;
	}



	@Override
	public String getPackageOfType() { return null; }
	/**
	 * If the metaobject annotation has type <code>packageName.prototypeName</code>, this method returns
	 * <code>prototypeName</code>.  See {@link CyanMetaobjectLiteralObject#getPackageOfType()}
	   @return
	 */

	@Override
	public String getPrototypeOfType() { return null; }

	/**
	 * check if the metaobject annotation is semantically correct just after it is found
	 * during parsing, before analyzing its attached DSL.
	 * Returns <code>null</code> if it is. Otherwise returns a list of error messages
	 */
	public void check() {
	}


	public Token getVisibility() {
		return visibility;
	}

	/**
	 * replace statement 'stat' by 'code' that has type 'codeType' (if it is an expression).
	 * 'codeType' can be 'null' if it is a typeless statement. The current environment
	 * is 'wrEnv'.
	 *
	   @param stat
	   @param code
	   @param codeType
	   @param wrEnv
	   @return
	 */
	protected
	boolean replaceStatementByCode(WrStatement stat,
			StringBuffer code, WrType codeType,
			WrEnv wrEnv
			) {

		if ( wrEnv.getCompilationPhase() != CompilationPhase.semAn ) {
			this.addError("The metaobject associated to this annotation is trying to replace"
					+ " statement\n    " + stat.asString() + "\nby\n    " + code +
					"\nin the compilation phase "  +
					wrEnv.getCompilationPhase().name() + ". " +
					"This is illegal because this replacement can only be done in phase semAn (" +
					"by method replaceStatementByCode of " + this.getClass().getCanonicalName()
					);
		}
		/*
		 * cases to consider:
		 * 		a) annot is attached to a prototype. Check if
		 *            stat is inside the prototype or not
		 *      b) annot is attached to a method. Check if
		 *            stat is inside the method
		 *      c) annot is inside a method. Check if stat is inside the method
		 */
		WrAnnotationAt annot = this.getAnnotation();
		WrMethodDec statMethod = stat.getCurrentMethod();
		IDeclaration annotDec = annot.getDeclaration();
		if ( annotDec instanceof WrPrototype ) {
			// annotation is attached to a program unit

			// is statement inside the prototype of the annotation?
			if ( statMethod.getDeclaringObject() != (WrPrototype ) annotDec ) {
				throw new MetaSecurityException(
						"Annotation " + annot.getCyanMetaobject().getName()
						+ " is trying to replace a statement in " +
				       "another prototype. This is illegal");
			}
		}
		else if ( annotDec instanceof WrMethodDec ) {
			WrMethodDec annotMethod = (WrMethodDec ) annotDec;
			// annotation is attached to 'annotMethod'
			if ( statMethod != annotMethod ) {
				throw new MetaSecurityException(
						"Annotation " + annot.getCyanMetaobject().getName()
						+ " of method " + annotMethod.getName() +
								" is trying to replace a statement in " +
				       " method " + statMethod.getName() + ". This is illegal. " +
						"A metaobject can only replace statements in its own annotation method"		);
			}
		}
		else {
			// annotation is attached to something else or not attached to anything
			// annotMethod is the method of the current annotation
			WrMethodDec annotMethod = annot.getCurrentMethod();
			if ( statMethod != annotMethod ) {
				throw new MetaSecurityException(
						"Annotation " + annot.getCyanMetaobject().getName()
						+ " of method " + annotMethod.getName() +
								" is trying to replace a statement in " +
				       " method " + statMethod.getName() + ". This is illegal. " +
						"A metaobject can only replace statements in its own annotation method"		);
			}

		}
		Env env = meta.GetHiddenItem.getHiddenEnv(wrEnv);
		if ( env.getCompilationStep().ordinal() > CompilationStep.step_6.ordinal() ) {
			this.addError(annot.getFirstSymbol(), "The metaobject associated to "
					+ "this annotation is trying to replace code after step 6 of the compilation. This is illegal");
		}
		return env.replaceStatementByCode(
				meta.GetHiddenItem.getHiddenStatement(stat),
				meta.GetHiddenItem.getHiddenCyanMetaobjectWithAtAnnotation(annot),
				code, codeType != null ? meta.GetHiddenItem.getHiddenType(codeType) : null);
	}




	static public String extractSlotInterface(String code) {
		int i = 0;
		char ch = code.charAt(i);
		StringBuffer s = new StringBuffer();
		while ( Character.isWhitespace(ch) ) {
			ch = code.charAt(i);
			++i;
		}

		return s.toString();
	}



	/**
	 * Token.PUBLIC if this metaobject is attached to a public method or any prototype AND
	 *
	 */
	private Token visibility;

	/**
	 * kind of parameter that this metaobject demands
	 */

	protected AnnotationArgumentsKind	parameterKind;

	protected AttachedDeclarationKind []attachedDecKindList;

	/**
	 * the metaobject name. This should be the annotation name
	 */
	private final String name;

}
