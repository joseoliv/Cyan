
package meta;

import java.util.List;
import saci.Project;

public class WrProject extends WrASTNode {

	public WrProject(Project hidden) {
		this.hidden = hidden;
	}


	List<WrCyanPackage> iPackageList = null;
	boolean thisMethod_wasNeverCalled = true;

//	public List<WrCyanPackage> getPackageList() {
//		if ( thisMethod_wasNeverCalled ) {
//			thisMethod_wasNeverCalled = false;
//
//
//			List<CyanPackage> fromList = hidden.getPackageList();
//			if ( fromList == null ) {
//					// unnecessary, just to document
//				iPackageList = null;
//			}
//			else {
//				iPackageList = new ArrayList<>();
//				for ( CyanPackage from : fromList ) {
//					iPackageList.add( from.getI() );
//				}
//			}
//
//		}
//		return iPackageList;
//	}
//


	public String getProjectDir() {
		return hidden.getProjectDir();
	}

	public char[] getText() {
		return hidden.getText();
	}

	public String getProjectName() {
		return hidden.getProjectName();
	}

	public String getProjectCanonicalPath() {
		return hidden.getProjectCanonicalPath();
	}

	@Override
	Project getHidden() {
		return hidden;
	}

	Project hidden;

	public WrProgram getProgram() {
		return hidden.getProgram().getI();
	}

}