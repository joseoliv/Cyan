package meta;

import java.util.List;
import ast.AnnotationAt;
import ast.ICalcInternalTypes;
import ast.Prototype;
import ast.Type;
import lexer.SymbolCyanAnnotation;

public class WrAnnotationAt extends WrAnnotation {

	public WrAnnotationAt(AnnotationAt hidden) {
		super(hidden);
	}

	@Override
	public
	AnnotationAt getHidden() { return (AnnotationAt ) hidden; }

	public IDeclaration getDeclaration() {
		return ((AnnotationAt ) hidden).getDeclaration();
	}

	public List<Object> getJavaParameterList() {
		return ((AnnotationAt ) hidden).getJavaParameterList();
	}

	public byte[] getCodegInfo() {
		return ((AnnotationAt ) hidden).getCodegInfo();
	}

	public char []getTextAttachedDSL() {
		return ((AnnotationAt ) hidden).getTextAttachedDSL();
	}

	public List<WrExprAnyLiteral> getRealParameterList() {
		return ((AnnotationAt ) hidden).getRealParameterList();
	}

	public String javaParameterAt(int i) {
		return ((AnnotationAt ) hidden).javaParameterAt(i);
	}

	public WrPrototype getCurrentPrototype() {
		Prototype pu = ((AnnotationAt ) hidden).getCurrentPrototype();
		return pu == null ? null : pu.getI();
	}

	@Override
	public String getPackageOfAnnotation() {
		return ((AnnotationAt ) hidden).getPackageOfAnnotation();
	}

	@Override
	public CyanMetaobjectAtAnnot getCyanMetaobject() {
		return ((AnnotationAt ) hidden).getCyanMetaobject();
	}


	public WrType getTypeAttached() {
		Type t = ((AnnotationAt ) hidden).getTypeAttached();
		return t == null ? null : t.getI();
	}

	public
	WrSymbolCyanAnnotation getSymbolAnnotation() {
		SymbolCyanAnnotation sym = ((AnnotationAt ) hidden).getSymbolAnnotation();
		return sym == null ? null : sym.getI();
	}

	public String getCompleteName() {
		return ((AnnotationAt ) hidden).getCompleteName();
	}

	public void setCodegInfo(byte[] codegFileData) {
		((AnnotationAt ) hidden).setCodegInfo(codegFileData);
	}

	public String filenameAnnotationInfo(String newFirstParameter) {
		return ((AnnotationAt ) hidden).filenameAnnotationInfo(newFirstParameter);
	}

	public boolean getInsideMethod() {
		return ((AnnotationAt ) hidden).getInsideMethod();
	}

	public List<ICalcInternalTypes> getExprStatList() {
		return ((AnnotationAt ) hidden).getExprStatList();
	}


	public String newAnnotationText(String newFirstParameter) {
		return ((AnnotationAt ) hidden).newAnnotationText(newFirstParameter);
	}

	public String newAnnotationTextReplaceDSL(String newDSLText) {
		return ((AnnotationAt ) hidden).newAnnotationTextReplaceDSL(newDSLText);

	}



}
