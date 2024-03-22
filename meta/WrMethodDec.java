package meta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ast.AnnotationAt;
import ast.CompilationUnit;
import ast.FieldDec;
import ast.MethodDec;
import ast.MethodSignature;
import ast.Prototype;
import ast.StatementList;

public class WrMethodDec extends WrSlotDec implements IDeclarationWritable {

    public WrMethodDec(MethodDec hiddenMethodDec) {
        this.hidden = hiddenMethodDec;
    }
    MethodDec hidden;

    @Override
    MethodDec getHidden() { return hidden; }

    /**
     * part of the implementation of the visitor pattern
       @param visitor
       @param env
     */
    public void accept(WrASTVisitor visitor, WrEnv env) {

    	securityCheck(env);


		visitor.preVisit(this, env);

		this.getMethodSignature().accept(visitor, env);

    	if ( env.getCompilationStep().ordinal() > CompilationStep.step_5.ordinal() ) {
    		WrStatementList isList = this.getStatementList(env);
    		if ( isList != null ) {
    			isList.accept(visitor, env);
    		}
    		else {
        		WrExpr e = this.getExpr(env);
        		if ( e != null ) {
        			e.accept(visitor, env);
        		}
    		}
    	}

		visitor.visit(this, env);
	}

	/**
	 * For unary and operator methods, it returns the same as {@link #getNameWithoutParamNumber()}.
	 * For regular methods, it returns the names of all keywords plus its number of
	 * parameters concatenated. That is, the return for method<br>
	 * <code>with: Int n, Char ch plus: Float f</code><br>
	 * would be <code>"with:2 plus:1"</code>
	 */

    @Override
    public String getName() {
        return hidden.getName();
    }

    /**
     * return AttachedDeclarationKind.METHOD_DEC
     */
    @Override
    public AttachedDeclarationKind getKind(WrEnv env) {
        return hidden.getKind();
    }

    /**
     * add a document text to the documentation of this method. docKind is
     * the kind of document, a user-defined field with no pre-defined meaning.
     */
    @Override
    public void addDocumentText(String doc, String docKind, WrEnv env) {
    	securityCheck(env);
    	checkAdditionInfo(env);


    	hidden.addDocumentText(doc, docKind);
    }


    /**
     * add a feature to the list of features of this method.
     */

    @Override
    public void addFeature(Tuple2<String, WrExprAnyLiteral> feature, WrEnv env) {
    	securityCheck(env);

    	checkAdditionInfo(env);


        hidden.addFeature(feature);
    }

    /**
     * add a code example to the documentation of this method. exampleKind is
     * the kind of example, an user-defined field with no pre-defined meaning.
     */
    @Override
    public void addDocumentExample(String example, String exampleKind, WrEnv env) {
    	securityCheck(env);

    	checkAdditionInfo(env);


        hidden.addDocumentExample(example, exampleKind);
    }

	/**
	   @param env
	 */
	private static void checkAdditionInfo(WrEnv env) {
		CompilationStep step = env.getCompilationStep();
    	if ( step != CompilationStep.step_1 &&
             	 step != CompilationStep.step_4 &&
             	 step != CompilationStep.step_7 &&
             	step != CompilationStep.step_9 ) {
    		throw new MetaSecurityException();
    	}
	}

    /**
     * return the list of documents associated to this method. Each tuple
     * is composed of a document text and an user-defined document kind.
     */

    @Override
    public List<Tuple2<String, String>> getDocumentTextList(WrEnv env) {
    	checkGetInfo(env);

        return hidden.getDocumentTextList();
    }

    /**
     * return the list of examples associated to this method. Each tuple
     * is composed of a code example and an user-defined example kind
     */

    @Override
    public List<Tuple2<String, String>> getDocumentExampleList(WrEnv env) {
    	checkGetInfo(env);

        return hidden.getDocumentExampleList();
    }

	/** Check if the program unit of env is the same as the program unit of
	 * this method
	   @param env
	 */
	private void securityCheck(WrEnv env) {
		if ( env.getCurrentPrototype().hidden != hidden.getDeclaringObject() ) {
    		throw new MetaSecurityException();
    	}

	}

	/**
	 * return the list of features associated to this method
	 */
    @Override
    public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList(WrEnv env) {
    	checkGetInfo(env);
        return hidden.getFeatureList();
    }

    /**
     * search for feature 'name' in the list of features of this method
     */
    @Override
    public List<WrExprAnyLiteral> searchFeature(String name, WrEnv env) {
    	checkGetInfo(env);
        return hidden.searchFeature(name);
    }

    /**
     * return the method signature of this method
       @return
     */
	public WrMethodSignature getMethodSignature() {
		MethodSignature ms = hidden.getMethodSignature();
		return ms == null ? null : ms.getI();
	}

	/**
	 * return the first symbol of this method
	   @param env
	   @return
	 */
	public WrSymbol getFirstSymbol(WrEnv env) {
    	securityCheck(env);

		return hidden.getFirstSymbol().getI();
	}

	/**
	 * return true if this method is declared with 'final', false otherwise
	   @return
	 */
	public boolean getIsFinal() {
		return this.hidden.getIsFinal();
	}

	/**
	 * return true if this method was declared with keyword 'override'
	   @return
	 */
	public boolean getHasOverride() {
		return this.hidden.getHasOverride();
	}

	/**
	 * return the name of this method without the number of parameters
	 * of each keyword. For <br>
	 * <code> func with: Int n, String s do: Function<Nil> f</code><br>
	 * the value returned would be<br>
	 * <code> "with:do:"</code><br>
	 * For method <br>
	 * <code> func + Int n -> Int </code><br>
	 * the value returned would be<br>
	 * <code> "+"</code>
	   @return
	 */
	public String getNameWithoutParamNumber() {
		return this.hidden.getNameWithoutParamNumber();
	}

	/**
	 * return the Program Unit that declared this method.
	   @return
	 */
	public WrPrototype getDeclaringObject() {
		Prototype pu = hidden.getDeclaringObject();
		return pu == null ? null : pu.getI();
	}

	/**
	 * return true if some non-shared field was assigned to inside this method.
	 * That is, if some non-shared field appears in the left-hand side of
	 * an assignment
	 */

	public boolean someNonSharedFieldAssignedTo(WrEnv env) {
		securityCheck(env);

		return hidden.someNonSharedFieldAssignedTo();
	}

	/**
	 * return true if some non-shared field was assigned to inside this method.
	 * That is, if some non-shared field appears in the left-hand side of
	 * an assignment
	 */

	public boolean someSharedFieldAssignedTo(WrEnv env) {
		securityCheck(env);

		return hidden.someSharedFieldAssignedTo();
	}

	/**
	 * return the list of fields that receive values in assignments inside
	 * this method
	   @param env
	   @return
	 */
	public List<WrFieldDec> getAssignedToFieldList(WrEnv env) {
    	securityCheck(env);

		if ( fieldList == null ) {
			fieldList = new ArrayList<>();
			if ( hidden.getAssignedToFieldList() != null ) {
				for ( final FieldDec f : hidden.getAssignedToFieldList() ) {
					fieldList.add(f.getI());
				}
			}
		}
		return fieldList;
	}

	List<WrFieldDec> fieldList = null;

	/**
	 * return true if this method is abstract
	   @return
	 */
	public boolean isAbstract() {
		return hidden.isAbstract();
	}

	/**
	 * return the list of statements of this method. It may be null
	 * if the method is declared with '=' as in <br>
	 * <code>   func getName -> Int = name;</code><br>	 *
	 *
	 * An exception MetaSecurityException is thrown if the current
	 * compilation step is different from 6 or 9;
	   @param env
	   @return
	 */
	public WrStatementList getStatementList(WrEnv env) {
    	securityCheck(env);
    	int compStep = env.getCompilationStep().ordinal();
    	if ( compStep != CompilationStep.step_6.ordinal() &&
    			compStep != CompilationStep.step_9.ordinal()	) {
    		throw new MetaSecurityException();
    	}

		StatementList sl = hidden.getStatementList();
		return sl == null ? null : sl.getI();
	}

	/**
	 * return true if 'self' is passed as parameter in a message send
	   @param env
	   @return
	 */
	public boolean getSelfLeak(WrEnv env) {
		securityCheck(env);

		return hidden.getSelfLeak();
	}

	public Set<WrFieldDec> getAccessedFieldSet() {
		if ( iaccessedFieldSet == null ) {
			Set<FieldDec> set = hidden.getAccessedFieldSet();
			if ( set != null ) {
				iaccessedFieldSet = new HashSet<WrFieldDec>();
				for ( FieldDec field : set) {
					iaccessedFieldSet.add(field.getI());
				}
			}
		}
		return iaccessedFieldSet;
	}

	private Set<WrFieldDec> iaccessedFieldSet = null;

	/**
	 * return the visibility of the method: Token.PUBLIC, Token.PRIVATE,
	 * Token.PACKAGE, or Token.PROTECTED.
	   @return
	 */
	public Token getVisibility() {
		return hidden.getVisibility();
	}

	/**
	 * return the compilation unit in which the method is.
	   @param env
	   @return
	 */
	public WrCompilationUnit getCompilationUnit(WrEnv env) {
    	securityCheck(env);

		CompilationUnit cunit = hidden.getDeclaringObject().getCompilationUnit();
		return cunit == null ? null : cunit.getI();
	}

	public String getNameAsInSourceCode() {
		return hidden.getNameWithDeclaredTypes();
	}


	/**
	 * return the expression that is the method body if the method is
	 * declared with '=' as in <br>
	 * <code>   func getName -> Int = name;</code><br>
	 * An exception MetaSecurityException is thrown if the current
	 * compilation step is less than 5.
	   @param env
	   @return
	 */
	public WrExpr getExpr(WrEnv env) {
    	securityCheck(env);

    	int compStep = env.getCompilationStep().ordinal();
    	if ( compStep != CompilationStep.step_6.ordinal() &&
    			compStep != CompilationStep.step_9.ordinal()	) {
    		throw new MetaSecurityException();
    	}



		ast.Expr e = hidden.getExpr();
		return e == null ? null : e.getI();
	}

	public List<WrAnnotationAt> getAttachedAnnotationList(WrEnv env) {
    	//securityCheck(env);

		List<AnnotationAt> annotList = hidden.getAttachedAnnotationList();
		if ( annotList == null ) {
			return null;
		}
		else {
			List<WrAnnotationAt> newList = new ArrayList<>();
			for (AnnotationAt annot : annotList) {
				if ( annot.getCyanMetaobject().getVisibility() == Token.PUBLIC ) {
					newList.add(annot.getI());
				}
				else if ( annot.getCyanMetaobject().getVisibility() == Token.PACKAGE ) {
					if ( env.getCurrentPrototype().hidden.getPackageName().equals(hidden.getDeclaringObject().getPackageName())
							) {
						newList.add(annot.getI());
			    	}
				}
			}
			return newList;
		}
	}

	public boolean createdByMetaobjects() {
		return hidden.createdByMetaobjects();
	}

	public boolean isUnary() {
		return hidden.isUnary();
	}
}
