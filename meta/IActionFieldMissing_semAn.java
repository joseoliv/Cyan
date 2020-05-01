package meta;

/**
 * This interface should be implemented by metaobject classes that want to intercept
 * missing get and set of fields.
   @author jose
 */

public interface IActionFieldMissing_semAn {


	/**
	 * return the code that replaces the getting of a field. fieldToGet may be an
	 * object of ExprIdentStar ('n' in 'k = n') or an object of ExprSelfPeriodIdent,
	 * self.id. The tuple returned is composed of the package name, prototype name,
	 * and expression that replaces the getting of the field.
	 *
	   @param fieldToGet
	   @return
	 */

	@SuppressWarnings("unused")
	default Tuple3<String, String, StringBuffer> semAn_replaceGetMissingField(
			WrExprSelfPeriodIdent fieldToGet, WrEnv env) {
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
	default StringBuffer semAn_replaceSetMissingField(
			WrExprSelfPeriodIdent fieldToSet, WrExpr rightHandSideAssignment, WrEnv env) {
		return null;
	}

}
