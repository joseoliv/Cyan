package meta;

import java.util.List;
import java.util.Map;
import ast.EvalEnv;

public class WrEvalEnv {

	public WrEvalEnv(WrEnv env, Object selfObject,
			WrSymbol symbolForErrorMessage
			) {
		hidden = new EvalEnv(meta.GetHiddenItem.getHiddenEnv(env).getCyanLangDir(),
				 selfObject, symbolForErrorMessage);
	}

	public WrEvalEnv(String cyanLangDir, Object selfObject,
			WrSymbol symbolForErrorMessage
			) {
		hidden = new EvalEnv(cyanLangDir,
			    selfObject, symbolForErrorMessage);
	}

	public void error(WrSymbol errSymbol, String msg) {
		hidden.error(errSymbol, msg);
	}


	public void addVariable(String varName, Object value) {
		hidden.addVariable(varName, value);
	}


	public void setFieldMemory(Map<String, Object> fieldMemory) {
		hidden.setFieldMemory(fieldMemory);
	}

	public Object getReturnValue() {
		return hidden.getReturnValue();
	}

	public void setSelfObject(Object selfObject) {
		hidden.setSelfObject(selfObject);
	}

	public WrMethodDec getCurrentMethod() {
		return currentMethod;
	}

	public void setCurrentMethod(WrMethodDec currentMethod) {
		this.currentMethod = currentMethod;
	}


	public WrEnv getWrEnvToBeUsedInVisitor() {
		return wrEnvToBeUsedInVisitor;
	}

	public void setWrEnvToBeUsedInVisitor(WrEnv wrEnvToBeUsedInVisitor) {
		this.wrEnvToBeUsedInVisitor = wrEnvToBeUsedInVisitor;
	}


	public WrPrototype getCurrentPrototype() {
		return currentPrototype;
	}

	public void setCurrentPrototype(WrPrototype currentPrototype) {
		this.currentPrototype = currentPrototype;
	}


	public WrCompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(WrCompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}


	public List<Object> getCreatedJavaObjectList() {
		return hidden.getCreatedJavaObjectList();
	}


	EvalEnv hidden;
	private WrMethodDec currentMethod = null;
	private WrPrototype currentPrototype = null;
	private WrCompilationUnit compilationUnit = null;

	private WrEnv wrEnvToBeUsedInVisitor = null;
}
