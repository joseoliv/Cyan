package meta;

import ast.Type;

/*
 * This interface will be removed when unions like Int|String are implemented without a
 * prototype Union<Int, String>. So we left "Type" as parameter types instead of changing them
 * to "WrType"
 */
/**
 * this interface is used when assignments  should be changed in the generated Java code. The right-hand side
 * of the assignment, in Java, should be changed to the value returned by this method. The code
 * should be  changed not only in assignments but also in parameter passing and return value of
 * methods.
 * <br>
 * As an example, consider an Union {@code Union<Int, String>} and a variable {@code intStr} of this type.
 * an assignment <br>
 * {@code     intStr = 0}<br>
 * should be changed in the Java code to<br>
 * {@code     _intStr = _UnionJavaName._assign( new CyInt(0) )}
 *
   @author José
 */
public interface IActionAssignment_cge extends IStayPrototypeInterface {

	String cge_changeRightHandSideTo(Type leftType, String rightHandSideExprInJava, Type rightType);

}
