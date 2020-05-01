package meta;

/**
 * Annotations of metaobjects that inherit from this interface are kept in
 * the prototype interface when a .iyan file is created. When
 * a prototype or package is compiled to be later used only in compiled form,
 * a .iyan file is created only with the prototype(s) interfaces. If
 * the metaobject class of an annotation attached to a method or prototype
 * inherits from this interface, then the .iyan file will have this
 * annotation even if the metaobject class does not inherits from the
 * following selected group of interfaces:
 *     - IActionMessageSend_semAn
 *     - IActionMethodMissing_semAn
 *     - ICheckSubprototype_afterSemAn
 *     - ICheckOverride_afterSemAn
 *     - ICheckMessageSend_afterSemAn
 *     - IActionAssignment_cge
 *
   @author jose
 */
public interface IStayPrototypeInterface {

}
