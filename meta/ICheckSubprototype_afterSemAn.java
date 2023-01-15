package meta;

import ast.Type;
import metaRealClasses.Compiler_afterResTypes;

/**
 * This interface should be implemented by any metaobject that needs to check
 * subprototypes. That is, if a metaobject attached to a prototype implements
 * this interface, method {@link ICheckSubprototype_afterSemAn#afterResTypes_checkSubprototype(Type)}
 * of it will be called whenever the prototype is inherited directly or indirectly.
 * That is, this method will be called for any descendants of the prototype to which the
 * metaobject annotation is attached. This method is called in the second SEM_AN phase of the compilation.
   @author jose
 */
public interface ICheckSubprototype_afterSemAn extends ICheck_afterResTypes_afterSemAn, IStayPrototypeInterface {
	/**
	 * Suppose a prototype Proto has an attached metaobject that implements this interface.
	 * When Proto is inherited, the compiler calls method
	 * {@link ICheckSubprototype_afterSemAn#afterResTypes_checkSubprototype(Compiler_afterResTypes, Type)}  with the sub-prototype as parameter.
	 * That is, if we have
	 *            object Sub extends Proto ... end
	 *  the compiler calls {@link ICheckSubprototype_afterSemAn#afterResTypes_checkSubprototype(compiler_afterResTypes, Type)} passing
	 *  a compiler and Sub as parameters.
	 *
	 */
	void afterSemAn_checkSubprototype(ICompiler_semAn compiler_semAn, WrPrototype subPrototype);

}
