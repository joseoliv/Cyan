package meta;

import java.util.HashSet;
import java.util.Set;

/**
 * Metaobject classes that implement this interface want to share information with
 * metaobject annotations of the same prototype. That is, all metaobject annotations
 * (of metaobject classes that implement {@link ICommunicateInPrototype_afterResTypes_semAn_afterSemAn}) made
 * in the same prototype can share information
 *
 * @author José
 */
public interface ICommunicateInPrototype_afterResTypes_semAn_afterSemAn {


	/**
	 * return information from the metaobject annotation that is to be used by other metaobject annotations.
	 * This information can vary from compiler phase to compiler phase. That is, during parsing,
	 * the information returned by this method may be different from the information
	 * returned during the semantic analysis.<br>
	 * The object returned by this method is packed, together with other information, in a tuple
	 * that is one of the elements returned by method {@link #afterResTypes_semAn_afterSemAn_receiveInfoPrototype(HashSet)}.
	   @return
	 */
	@SuppressWarnings("unused")
	default Object afterResTypes_semAn_afterSemAn_shareInfoPrototype(WrEnv env) {
		return null;
	}
	/**
	 * If the annotation of the metaobject is in a prototype <code>P</code>, this method
	   is called by the compiler passing as parameter information on all metaobject annotations
	   made in prototype <code>P</code>. This interface is implemented by a metaobject
	   that is associated to a metaobject annotation.
	   The information returned by method {@link #afterResTypes_semAn_afterSemAn_shareInfoPrototype()} of the metaobject is
	   included in the set passed as parameter to this method. That is, if a prototype
	   has only one metaobject annotation (whose metaobject implements this interface) then
	   the set <code>annotationInfoSet</code> has one element.
         <br>
	 * This method is not called if the metaobject annotation  is outside a prototype. <br>
	 *
 *
	 * @param 	annotationInfoSet	 Every tuple in this set correspond to an annotation of a metaobject.
		 * Every tuple is composed of a metaobject name, the number of this metaobject
		 * considering all metaobjects in the same prototype, the number of this metaobject
		 * considering only the metaobjects with the same name (both numbers starts at
		 * <code>Annotation.firstAnnotationNumber</code>, which should be 1), and the information
		 * this metaobject annotation wants to share with other metaobject annotations, which is the value returned
		 * by {@link #afterResTypes_semAn_afterSemAn_shareInfoPrototype()}.
	 */
	@SuppressWarnings("unused")
	default void afterResTypes_semAn_afterSemAn_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet, WrEnv env) {

	}

}
