package meta;

/**
 * This interface should be implemented by all metaobjects that are attached to a local variable declaration (let or var) and
 * that need to add code after the declaration
   @author jose
 */
public interface IActionVariableDeclaration_dsa {

	StringBuffer dsa_codeToAddAfter(WrEnv env);

}
