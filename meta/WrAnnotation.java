package meta;

import java.util.List;
import ast.CompilationUnitSuper;
import ast.Annotation;
import lexer.Symbol;

public class WrAnnotation extends WrExpr {

	public WrAnnotation(Annotation hidden) {
		super(hidden);
	}


	@Override
	Annotation getHidden() { return (Annotation ) hidden; }


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}


	public String getPrototypeOfAnnotation() {
		return ((Annotation ) hidden).getPrototypeOfAnnotation();
	}

	public String getPackageOfAnnotation() {
		return ((Annotation ) hidden).getPackageOfAnnotation();
	}


	public WrSymbol getNextSymbol() {
		Symbol sym = ((Annotation ) hidden).getNextSymbol();
		return sym == null ? null : sym.getI();
	}

	public WrCompilationUnitSuper getCompilationUnit() {
		CompilationUnitSuper cunit = ((Annotation ) hidden).getCompilationUnit();
		return cunit == null ? null : cunit.getI();
	}


	public List<Tuple2<String, String>> getLocalVariableNameList() {
		return ((Annotation ) hidden).getLocalVariableNameList();
	}


	public void replaceAnnotationBy(String newCodegText) {
		((Annotation ) hidden).replaceAnnotationBy(newCodegText);
	}


	public int getMetaobjectAnnotationNumber() {
		return ((Annotation ) hidden).getMetaobjectAnnotationNumber();
	}
	
	public int getMetaobjectAnnotationNumberByKind() {
		return ((Annotation ) hidden).getMetaobjectAnnotationNumberByKind();
	}



	public CyanMetaobject getCyanMetaobject() {
		return ((Annotation ) hidden).getCyanMetaobject();
	}


	public WrSymbol getLastSymbol() {
		Symbol sym = ((Annotation ) hidden).getLastSymbol();
		return sym == null ? null : sym.getI();
	}


}
