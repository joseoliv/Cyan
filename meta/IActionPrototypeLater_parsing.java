package meta;

/**
 * This interface should be implemented by all metaobjects that should take actions
 * after the parsing of a generic prototype
 * 
   @author José
 */

public interface IActionPrototypeLater_parsing {
	
	void parsing_actionPrototypeLater(ICompilerPrototypeLater_parsing compiler);

}
