package meta;

/**
 * This exception is thrown if a metaobject tries to access private information
 * of an AST object that is outside its prototype (the prototype of its
 * annotation).
   @author jose
 */
public class MetaSecurityException extends RuntimeException {

	public MetaSecurityException() { }
	public MetaSecurityException(String message) { super(message); }
	public MetaSecurityException(WrEnv env, WrProgramUnit client) { super("Prototype '" + env.getCurrentProgramUnit().getFullName() + "' "
			+ "is trying to get information on prototype '" + client.getFullName() + "' that it does not have permission to"); }

	private static final long serialVersionUID = -9128178223597139159L;

}
