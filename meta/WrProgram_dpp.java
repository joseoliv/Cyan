package meta;

import java.util.ArrayList;
import java.util.List;
import ast.CyanPackage;
import ast.JVMPackage;
import ast.Program;

public class WrProgram_dpp extends WrProgram  {

	public WrProgram_dpp(Program hidden) {
		super(hidden);
	}

//	public void accept(WrASTVisitor visitor) {
//
//		visitor.preVisit(this);
//		for ( final WrCyanPackage cp :  getPackageList() ) {
//			cp.accept(visitor);
//		}
//		for ( final WrJVMPackage cp : getJvmPackageList() ) {
//			cp.accept(visitor);
//		}
//		visitor.visit(this);
//	}


    public void addDocumentText(String doc, String docKind) {
        hidden.addDocumentText(doc, docKind);
    }
    public void addDocumentExample(String example, String exampleKind) {
        hidden.addDocumentExample(example, exampleKind);
    }


	public void addProgramKeyValueSet(String key, String value) {
		hidden.addProgramKeyValueSet(key, value);
	}


	public void setProgramKeyValue(String varName, Object value) {
		hidden.setProgramKeyValue(varName, value);
	}

    public void addFeature(Tuple2<String, WrExprAnyLiteral> feature) {
        hidden.addFeature(feature);
    }


	private List<WrCyanPackage> iPackageList = null;
	boolean thisMethod_wasNeverCalled2 = true;

	public List<WrCyanPackage> getPackageList() {
		if ( thisMethod_wasNeverCalled2 ) {

			List<CyanPackage> fromList = hidden.getPackageList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iPackageList = null;
			}
			else {
				iPackageList = new ArrayList<>();
				for ( CyanPackage from : fromList ) {
					iPackageList.add( from.getI() );
				}
			}
			thisMethod_wasNeverCalled2 = false;

		}
		return iPackageList;
	}




	private List<WrJVMPackage> iJvmPackageList = null;
	boolean thisMethod_wasNeverCalled = true;

	public List<WrJVMPackage> getJvmPackageList() {
		if ( thisMethod_wasNeverCalled ) {

			List<JVMPackage> fromList = hidden.getJvmPackageList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iJvmPackageList = null;
			}
			else {
				iJvmPackageList = new ArrayList<>();
				for ( JVMPackage from : fromList ) {
					iJvmPackageList.add( from.getI() );
				}
			}
			thisMethod_wasNeverCalled = false;

		}
		return iJvmPackageList;
	}


}
