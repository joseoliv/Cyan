package meta;

/**
 * this interface should be implemented by all metaobject classes that need to check methods that were overridden.
   @author jose
 */
public interface ICheckOverride_afsa extends ICheck_afti_afsa, IStayPrototypeInterface {

	void afsa_checkOverride(ICompiler_dsa compiler_dsa, WrMethodDec method);

}
