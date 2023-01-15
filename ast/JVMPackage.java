package ast;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import meta.WrJVMPackage;

public class JVMPackage implements ASTNode {


	public JVMPackage(String pathToJar, String packageName) {
		this.pathToJar = pathToJar;
		this.packageName = packageName;
		this.jvmTypeClassMap = new HashMap<>();
	}


	@Override
	public WrJVMPackage getI() {
		if ( iJVMPackage == null ) {
			iJVMPackage = new WrJVMPackage(this);
		}
		return iJVMPackage;
	}

	private WrJVMPackage iJVMPackage = null;


	@Override
	public void accept(ASTVisitor visitor) {

		visitor.preVisit(this);
		visitor.visit(this);
	}



	public String getPackageName() {
		return packageName;
	}

	public TypeJavaRef searchJVMClass(String name) {
		return jvmTypeClassMap.get(name);
	}

	public String getPathToJar() {
		return pathToJar;
	}


	public void setPathToJar(String pathToJar) {
		this.pathToJar = pathToJar;
	}


	public TypeJavaRef put(String name, TypeJavaRef javaClass) {
		return this.jvmTypeClassMap.put(name, javaClass);
	}

	public Map<String, TypeJavaRef> getJvmTypeClassMap() {
		return jvmTypeClassMap;
	}


	public void setJvmTypeClassMap(Map<String, TypeJavaRef> jvmTypeClassMap) {
		this.jvmTypeClassMap = jvmTypeClassMap;
	}


	public boolean getClassesWereLoaded() {
		return classesWereLoaded;
	}


	public void setClassesWereLoaded(boolean classesWereLoaded) {
		this.classesWereLoaded = classesWereLoaded;
	}

	public URL[] getUrls() {
		return urls;
	}

	public void setUrls(URL[] urls) {
		this.urls = urls;
	}



	private String packageName;
	private String pathToJar;

	private Map<String, TypeJavaRef> jvmTypeClassMap;
	private boolean classesWereLoaded;

	private URL[] urls;


}
