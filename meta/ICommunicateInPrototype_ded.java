package meta;

import java.util.HashSet;
import java.util.Set;


/**
 * Metaobject classes that implement this interface want to share information with
 * metaobject annotations of the same prototype. That is, all metaobject annotations
 * (of metaobject classes that implement {@link ICommunicateInPrototype_ded}) made
 * in the same prototype can share information. This is made at editing time and all
 * metaobject classes that implement this interface should also implement interface
 * {@link meta.ICodeg}
 *
 * @author José
 */
public interface ICommunicateInPrototype_ded {
	/**
	 * return information from the metaobject annotation that is to be used by other metaobject annotations.
	 * The information is shared during editing time.
	 * The object returned by this method is packed, together with other information, in a tuple
	 * that is one of the elements returned by method {@link #ded_receiveInfoPrototype(HashSet)}.
	   @return
	 */
	default Object ded_shareInfoPrototype() {
		return null;
	}
	/**
	 * If the annotation of the metaobject is in a prototype <code>P</code>, this method
	   is called by the compiler passing as parameter information on all metaobject annotations
	   made in prototype <code>P</code>. This interface is implemented by a metaobject
	   that is associated to a metaobject annotation.
	   The information returned by method {@link #ded_shareInfoPrototype()} of the metaobject is
	   included in the set passed as parameter to this method. That is, if a prototype
	   has only one metaobject annotation (whose metaobject implements this interface) then
	   the set <code>annotationInfoSet</code> has one element.
         <br>
 *
	 * @param 	annotationInfoSet	 Every tuple in this set correspond to an annotation of a metaobject.
		 * Every tuple is composed of a metaobject name, the number of this metaobject
		 * considering all metaobjects in the same prototype, the number of this metaobject
		 * considering only the metaobjects with the same name (both numbers starts at
		 * <code>Annotation.firstAnnotationNumber</code>, which should be 1), and the information
		 * this metaobject annotation wants to share with other metaobject annotations, which is the value returned
		 * by {@link #ded_shareInfoPrototype()}.
	 */
	default void ded_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet) {

	}
}
