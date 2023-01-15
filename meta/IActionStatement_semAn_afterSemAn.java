package meta;

/**
 * metaobjects that implement this interface should be attached to a statement
   @author jose
 */
public interface IActionStatement_semAn_afterSemAn {
	/**
	 * if the statement is a while or repeat-until statement, this method is called in
	 * phase semAn for replacying the statement expression by another expression,
	 * returned by this method
	 */
	StringBuffer semAn_replaceExpr(WrEnv env, WrExpr e);

	/**
	 * if the statement is a while or repeat-until statement, this method is called in
	 * phase afterResTypes for adding a statement after the last while of repeat-until
	 * statement
	 */
	StringBuffer semAn_addStat(WrEnv env);
	/**
	 * check the statement after the semantic analysis for checking the statement
	 */
	void afterSemAn_check(WrEnv env);
}
