package meta;

/**
 * This interface should be implemented by metaobject classes that want to intercept  get and set of a field.
   @author jose
 */
public interface IActionFieldAccess_semAn {

	/**
	 * return the code that replaces the getting of a field. fieldToGet may be an
	 * object of ExprIdentStar ('n' in 'k = n') or an object of ExprSelfPeriodIdent
	 *
	   @param fieldToGet
	   @return
	 */
	@SuppressWarnings("unused")
	default StringBuffer semAn_replaceGetField(WrExpr fieldToGet, WrEnv env) {
		return null;
	}

	/**
	 * return the code that replaces the assignment to a field. fieldToSet may be an
	 * object of ExprIdentStar ('n' in 'k = n') or an object of ExprSelfPeriodIdent
	 *
	   @param fieldToGet
	   @return
	 */

	@SuppressWarnings("unused")
	default StringBuffer semAn_replaceSetField(WrExpr fieldToSet, WrExpr rightHandSideAssignment, WrEnv env) {
		return null;
	}


	@SuppressWarnings("unused")
	default StringBuffer semAn_replacePlusPlusField(WrStatement plusPlusField, WrEnv env) {
		return null;
	}

	@SuppressWarnings("unused")
	default StringBuffer semAn_replaceMinusMinusField(WrStatement minusMinusField, WrEnv env) {
		return null;
	}


}
