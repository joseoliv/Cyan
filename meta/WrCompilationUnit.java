package meta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ast.CompilationUnit;
import ast.CyanPackage;
import ast.ExprIdentStar;
import ast.Prototype;

/**
 * Represents a 'compilation unit', which is a file with import declarations (keyword 'import')
 * and a list of prototypes (although currently only a single prototype is allowed).
 *
 *
   @author jose
 */
public class WrCompilationUnit extends WrCompilationUnitSuper {

	public WrCompilationUnit(CompilationUnit hidden) {
		super(hidden);
	}


    /**
     * part of the implementation of the visitor pattern. A program unit
     * can only visit itself.
     *
       @param visitor
       @param env
     */

	public void accept(WrASTVisitor visitor, WrEnv env) {

		visitor.preVisit(this, env);

		for ( WrPrototype pu : this.getPrototypeList() ) {
			if ( env.getCurrentPrototype().hidden == pu.hidden ) {
				pu.accept(visitor, env);
			}
		}
		visitor.visit(this, env);
	}


//	 * All methods that take a WrEnv parameter are restricted. Unless said otherwise,
//	 * they can only be called inside the compilation unit itself. If not, an
//	 * exception MetaSecurityException is thrown.


	List<String> iImportedPackageList = null;
	boolean thisMethod_wasNeverCalled = true;



//	public List<WrExprIdentStar> getImportedPackageList() {
//		if ( thisMethod_wasNeverCalled ) {
//
//			List<ExprIdentStar> fromList = ((CompilationUnit ) hidden).getImportPackageList();
//			if ( fromList == null ) {
//					// unnecessary, just to document
//				iImportedPackageList = null;
//			}
//			else {
//				iImportedPackageList = new ArrayList<>();
//				for ( ExprIdentStar from : fromList ) {
//					//from.getName()
//					iImportedPackageList.add( from.getI() );
//				}
//			}
//			thisMethod_wasNeverCalled = false;
//
//		}
//		return iImportedPackageList;
//	}




	/**
	 * return the only public prototype of this compilation unit
	   @return
	 */
	public WrPrototype getPublicPrototype() {
		Prototype pu = ((CompilationUnit ) hidden).getPublicPrototype();
		return pu == null ? null : pu.getI();
	}


	/**
	 * return the filename of this compilation unit
	   @return
	 */
	public String getFilename() {
		return ((CompilationUnit ) hidden).getFilename();
	}

	/**
	 * return the package of this compilation unit
	   @return
	 */
	public WrCyanPackage getCyanPackage() {
		CyanPackage cp = ((CompilationUnit ) hidden).getCyanPackage();
		return cp == null ? null : cp.getI();
	}

	/**
	 * return the list of imported packages as strings
	   @return
	 */
	public List<String> getImportedPackageList() {
		if ( thisMethod_wasNeverCalled ) {

			List<ExprIdentStar> fromList = ((CompilationUnit ) hidden).getImportPackageList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iImportedPackageList = null;
			}
			else {
				iImportedPackageList = new ArrayList<>();
				for ( ExprIdentStar from : fromList ) {
					//from.getName()
					iImportedPackageList.add( from.getName() );
				}
			}
			thisMethod_wasNeverCalled = false;

		}
		return iImportedPackageList;
	}


	/**
	 * return the set of imported packages of this compilation unit
	   @return
	 */
	public Set<WrCyanPackage> getImportPackageSet() {
		if ( iSetCyanPackage == null ) {
			iSetCyanPackage = new HashSet<>();
			Set<CyanPackage> cps = ((CompilationUnit ) hidden).getImportPackageSet();
			if ( cps != null ) {
				for ( final CyanPackage p : cps) {
					this.iSetCyanPackage.add(p.getI());
				}
			}
		}
		return this.iSetCyanPackage;
	}

	Set<WrCyanPackage> iSetCyanPackage = null;

	/**
	 * return the package of this compilation unit
	   @return
	 */
	public String getPackageName() {
		return ((CompilationUnit ) hidden).getPackageName();
	}



	List<WrPrototype> iPrototypeList = null;
	boolean thisMethod_wasNeverCalled2 = true;


	/**
	 * return the list of program units of this compilation unit
	   @return
	 */
	public List<WrPrototype> getPrototypeList() {
		if ( thisMethod_wasNeverCalled2 ) {
			thisMethod_wasNeverCalled2 = false;


			List<Prototype> fromList = ((CompilationUnit ) hidden).getPrototypeList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iPrototypeList = null;
			}
			else {
				iPrototypeList = new ArrayList<>();
				for ( Prototype from : fromList ) {
					iPrototypeList.add( from.getI() );
				}
			}

		}
		return iPrototypeList;
	}


	/**
	 * return the name of the public prototype of this compilation unit
	   @return
	 */
	public String getNamePublicPrototype() {
		return ((CompilationUnit ) hidden).getNamePublicPrototype();
	}

}

