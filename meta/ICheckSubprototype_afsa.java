package meta;

import ast.Type;
import metaRealClasses.Compiler_afti;

/**
 * This interface should be implemented by any metaobject that needs to check
 * subprototypes. That is, if a metaobject attached to a prototype implements
 * this interface, method {@link ICheckSubprototype_afsa#afti_checkSubprototype(Type)}
 * of it will be called whenever the prototype is inherited directly or indirectly.
 * That is, this method will be called for any descendants of the prototype to which the
 * metaobject annotation is attached. This method is called in the second dsa phase of the compilation.
   @author jose
 */
public interface ICheckSubprototype_afsa extends ICheck_afti_afsa, IStayPrototypeInterface {
	/**
	 * Suppose a prototype Proto has an attached metaobject that implements this interface.
	 * When Proto is inherited, the compiler calls method
	 * {@link ICheckSubprototype_afsa#afti_checkSubprototype(Compiler_afti, Type)}  with the sub-prototype as parameter.
	 * That is, if we have
	 *            object Sub extends Proto ... end
	 *  the compiler calls {@link ICheckSubprototype_afsa#afti_checkSubprototype(compiler_afti, Type)} passing
	 *  a compiler and Sub as parameters.
	 *
	 */
	void afsa_checkSubprototype(ICompiler_dsa compiler_dsa, WrProgramUnit subPrototype);

}
