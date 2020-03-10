/**

 */
package meta;

import cyan.lang.CyInt;
import cyan.lang._Array_LT_GP_CyByte_GT;
import cyan.lang._Array_LT_GP_CyString_GT;

/**
 * Represent a metaobject annotationed using the syntax
 *        @meta(parameters)<++ ... ++>
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
		return ((WrAnnotationAt ) metaobjectAnnotation).getDeclaration();
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
	public WrAnnotationAt getMetaobjectAnnotation() {
		return (WrAnnotationAt ) metaobjectAnnotation;
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
