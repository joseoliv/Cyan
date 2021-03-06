package meta;

import java.util.List;

public interface IActionNewPrototypes_parsing {
	/**
	 * this method should return a list of tuples. Each tuple is composed of  
	 * a prototype name and the code of the compilation unit in which the prototype is ---
	 * the full text of the file. 
	 * The compiler will create this prototype in the current package. 
	 * @param compiler 
	 */
	List<Tuple2<String, StringBuffer>> parsing_NewPrototypeList(ICompilerAction_parsing compiler);	
}
