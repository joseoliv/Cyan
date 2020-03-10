package meta.tmp;

import java.util.List;
import cyan.lang.CyInt;
import meta.CyanMetaobject;
import meta.IActionFunction;
import meta.Tuple6;

/**
 * return the factorial of a number. The parameter to eval can be either an Integer or
 * an Int. The returned value is of type cyan.lang.Long.
 * This is a demonstration metaobject.
   @author jose
 */
public class CyanMetaobjectActionFunctionFatorial extends CyanMetaobject
		implements IActionFunction {

	@SuppressWarnings("unchecked")
	@Override
	public Object eval(Object input) {
		Tuple6<?,?,?,?,?,?> t = (Tuple6<?,?,?,?,?,?> ) input;
		Object number = ((List<Object> ) t.f3).get(0);
		if ( number instanceof Integer ) {
			//return new cyan.lang.CyLong( fat( (Integer ) number));
			return "" + fat( (Integer ) number) + "L";
		}
		else if ( number instanceof CyInt ) {
			int n = ((CyInt) number).n;
			//return new cyan.lang.CyLong( fat(n) );
			return "" + fat(n) + "L";
		}
		return null;
	}

	private static long fat(int n) {
		if ( n <= 0 ) { return 1; }
		else {
			long p = 1;
			while ( n > 1 ) {
				p = p*n;
				--n;
			}
			return p;
		}
	}

	@Override
	public String getName() {
		return "fat";
	}

	@Override
	public String getPackageOfType() {
		return "cyan.lang";
	}

	@Override
	public String getPrototypeOfType() {
		return "Long";
	}

}
