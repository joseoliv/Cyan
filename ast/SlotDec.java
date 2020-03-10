package ast;

import java.util.ArrayList;
import java.util.List;
import error.CompileErrorException;
import lexer.Symbol;
import meta.Token;
import meta.Tuple2;
import meta.WrExprAnyLiteral;
import saci.CyanEnv;
import saci.Env;

/**
 * This class is superclass of all classes describing slots: FieldDec and MethodDec.
 * @author José
 *
 */
abstract public class SlotDec implements Declaration, ASTNode {

	public SlotDec(ObjectDec currentObj, Token visibility, List<AnnotationAt> attachedSlotMetaobjectAnnotationList,
			List<AnnotationAt> nonAttachedSlotMetaobjectAnnotationList) {
		this.declaringObject = currentObj;
		this.visibility = visibility;
		this.attachedMetaobjectAnnotationList = attachedSlotMetaobjectAnnotationList;
		this.nonAttachedMetaobjectAnnotationList = nonAttachedSlotMetaobjectAnnotationList;
		this.lastSlot = false;
	}
	public void setVisibility(Token visibility) {
		this.visibility = visibility;
	}
	public Token getVisibility() {
		return visibility;
	}

	abstract public String getName();
	abstract public Symbol getFirstSymbol();

	public void calcInterfaceTypes(Env env) {
		try {
			//this.calcInternalTypes(env);
			calcInternalTypesNONAttachedAnnotations(env);
			calcInternalTypesAttachedAnnotations(env);
		}
		catch ( CompileErrorException e ) {
		}

	}

	/*
	public void calcInternalTypes(Env env) {
		calcInternalTypesNONAttachedAnnotations(env);
	}
	*/

	/**
	 * calculates internal types of annotations that precede the slot
	   @param env
	 */
	public void calcInternalTypesNONAttachedAnnotations(Env env) {
		if ( nonAttachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : nonAttachedMetaobjectAnnotationList )
				annotation.calcInternalTypes(env);
		}
	}

	/**
	 * calculates internal types of annotations attached to the slot
	   @param env
	 */
	public void calcInternalTypesAttachedAnnotations(Env env) {
		if ( attachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : attachedMetaobjectAnnotationList )
				annotation.calcInternalTypes(env);
		}
	}


	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		cyanEnv.setPrintNewLineAfterAnnotation(true);
		try {
			if ( nonAttachedMetaobjectAnnotationList != null ) {
				for ( AnnotationAt annotation : nonAttachedMetaobjectAnnotationList ) {
					annotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
				}
			}
			if ( attachedMetaobjectAnnotationList != null ) {
				for ( AnnotationAt annotation : attachedMetaobjectAnnotationList ) {
					annotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
				}
			}
		}
		finally {
			cyanEnv.setPrintNewLineAfterAnnotation(false);
		}

	}

	public void genJava(PWInterface pw, Env env) {

		if ( this.attachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : attachedMetaobjectAnnotationList ) {
				annotation.genJava(pw, env);
				/* if ( annotation instanceof meta.IAction_cge )
					pw.print( ((IAction_cge) annotation).cge_codeToAddAtMetaobjectAnnotation() ); */
			}
		}
		if ( this.nonAttachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : nonAttachedMetaobjectAnnotationList ) {
				annotation.genJava(pw, env);
				/* if ( annotation instanceof meta.IAction_cge )
					pw.print( ((IAction_cge) annotation).cge_codeToAddAtMetaobjectAnnotation() ); */
			}
		}

	}

	public List<AnnotationAt> getAttachedMetaobjectAnnotationList() {
		return attachedMetaobjectAnnotationList;
	}
	public void setMetaobjectAnnotationList(List<AnnotationAt> ctmetaobjectAnnotationList) {
		this.attachedMetaobjectAnnotationList = ctmetaobjectAnnotationList;
	}


	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		return featureList;
	}

	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature) {
		if ( featureList == null )
			featureList = new ArrayList<>();
		/*else {
			int size = featureList.size();
			for ( int i = 0; i < size; ++i) {
				if ( featureList.get(i).f1.equals(feature.f1) ) {
					// replace
					featureList.set(i, feature);
					return;
				}
			}
		} */
		featureList.add(feature);
	}

	public void addFeatureList( List<Tuple2<String, WrExprAnyLiteral>> featureList1) {
		for ( Tuple2<String, WrExprAnyLiteral> t : featureList1 ) {
			this.addFeature(t);
		}
	}



	public List<WrExprAnyLiteral> searchFeature(String name) {
		if ( featureList == null ) return null;

		List<WrExprAnyLiteral> eList = null;
		for ( Tuple2<String, WrExprAnyLiteral> t : featureList ) {
			if ( t.f1.equals(name) ) {
				if ( eList == null ) {
					eList = new ArrayList<>();
				}
				eList.add(t.f2);
			}
		}
		return eList;
	}



	public void addDocumentText(String doc, String docKind) {
		if ( documentTextList == null ) {
			documentTextList = new ArrayList<>();
		}
		documentTextList.add( new Tuple2<String, String>(doc, docKind));
	}

	public void addDocumentExample(String example, String exampleKind) {
		if ( exampleTextList == null ) {
			exampleTextList = new ArrayList<>();
		}
		exampleTextList.add( new Tuple2<String, String>(example, exampleKind));

	}

	public List<Tuple2<String, String>> getDocumentTextList() {
		return documentTextList;
	}

	public List<Tuple2<String, String>> getDocumentExampleList() {
		return exampleTextList;
	}


	public ObjectDec getDeclaringObject() {
		return declaringObject;
	}

	public void calcInternalTypes(Env env) {
	}


	public boolean getLastSlot() {
		return lastSlot;
	}
	public void setLastSlot(boolean lastSlot) {
		this.lastSlot = lastSlot;
	}

	/**
	 * list of pairs (doc, docKind) of documentation for this declaration. See interface
	 * {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>> documentTextList;
	/**
	 * list of pairs (example, exampleKind) of examples for this declaration. See interface
	 * {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>> exampleTextList;


	/**
	 * the list of features associated to this slot
	 */
	private List<Tuple2<String, WrExprAnyLiteral>> featureList;


	/**
	 * metaobject annotations before this slot declaration such as in <br>
	 * <code>
	 *     {@literal @}getset var Int name<br>
	 * </code><br>
	 * These annotations are attached to the slot declaration.
	 */
	protected List<AnnotationAt> attachedMetaobjectAnnotationList;

	/**
	 * metaobject annotations before this program unit such as <br>
	 * <code>
	 *     {@literal @}javacode{*  void asString() { ... } *}
	 *     var Int name<br>
	 * </code><br>
	 * These annotations are NOT attached to the slot declaration.
	 */
	protected List<AnnotationAt> nonAttachedMetaobjectAnnotationList;

	protected Token visibility;


	/**
	 * object in which the method is declared
	 */
	protected ObjectDec		declaringObject;


	/**
	 * true if this is the last slot declared in the program unit, not considering slots
	 * introduced by the compiler itself.
	 */
	private boolean lastSlot;


}
