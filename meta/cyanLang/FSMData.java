package meta.cyanLang;

import java.util.List;
import meta.Tuple3;

/**
 * coloque esta classe dentro de CyanMetaobjectCodegFSM
 * @author josed
 *
 */
public class FSMData {
	
	public String file;
	public String initialState;
	public List<String> finalStateList;
	public List<Tuple3<String, Integer, Integer>> stateList;
	/**
	 * lista de transições, sendo cada uma delas uma tupla no qual o primeiro 
	 * elemento é o nome do estado inicial, o segundo o nome do estado final e
	 * o terceiro é a etiqueta 
	 */
	public List<Tuple3<String, String, String>> transList;
	
	
	public String getInitialState() {
		return initialState;
	}
	
	public void setInitialState(String initialState) {
		this.initialState = initialState;
	}
	
	public void addFinalState(String finalState) {
		this.finalStateList.add(finalState);
	}
	
	public void addState(String state, int x, int y) {
		this.stateList.add(new Tuple3<String, Integer, Integer>(state, x, y));
	}
	
	public void addTransition(String origin, String destiny, String action) {
		this.transList.add(new Tuple3<String, String, String>(origin, destiny, action));
	}
	
	public void setFile(String file) {
		this.file = file;
	}

}
