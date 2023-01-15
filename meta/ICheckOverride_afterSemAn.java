package meta;

/**
 * this interface should be implemented by all metaobject classes that need to check methods that were overridden.
   @author jose
 */
public interface ICheckOverride_afterSemAn extends ICheck_afterResTypes_afterSemAn, IStayPrototypeInterface {

	void afterSemAn_checkOverride(ICompiler_semAn compiler, WrMethodDec method);

}
