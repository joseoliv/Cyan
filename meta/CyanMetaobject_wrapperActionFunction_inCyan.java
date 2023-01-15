package meta;

import cyan.reflect._CyanMetaobject;
import cyan.reflect._IActionFunction;

public class CyanMetaobject_wrapperActionFunction_inCyan extends CyanMetaobject implements IActionFunction {


	private _IActionFunction actionFunctionInCyan;

	public CyanMetaobject_wrapperActionFunction_inCyan(_IActionFunction actionFunctionInCyan) {
		this.actionFunctionInCyan = actionFunctionInCyan;
		this.setMetaobjectInCyan( (_CyanMetaobject ) this.actionFunctionInCyan);
	}

	@Override
	public String getName() {
		return ((_CyanMetaobject ) this.actionFunctionInCyan)._getName().s;
	}

	@Override
	public String getPackageOfType() { return actionFunctionInCyan._getPackageOfType().s; }

	@Override
	public String getPrototypeOfType() { return actionFunctionInCyan._getPrototypeOfType().s; }

	@Override
	public Object eval(Object input) {
		return actionFunctionInCyan._eval_1(input);
	}

}
