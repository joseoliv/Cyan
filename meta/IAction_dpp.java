package meta;

/**
 * only metaobjects whose annotations are used inside .pyan files can implement this interface.
 * Method {@link IAction_dpp#dpp_action(ICompiler_dpp)} is called during the parsing of
 * a project (hence dpp --- during parsing of the project) and it may call any methods of {@link ICompiler_dpp}.
   @author jose
 */
public interface IAction_dpp {
	void dpp_action(ICompiler_dpp project);
}
