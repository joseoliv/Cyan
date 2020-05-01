/**
 *
 */
package ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import lexer.Symbol;
import meta.MetaHelper;
import saci.CyanEnv;
import saci.Env;

/**
 * Represents a literal  such as 1b, 23, 3.1415, 45E+12
 * This is the superclass of all classes that represent literal numbers,
 * characters etc.
 *
 * @author José
 *
 */
abstract public class ExprLiteral extends ExprAnyLiteral {

	/**
	 *
	 */
	public ExprLiteral(Symbol symbol, MethodDec method) {
		super(method);
		this.symbol = symbol;
	}


	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}


	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public boolean isNRE(Env env) {
		return true;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

	    pw.print(symbol.symbolString);
	}


	@Override
	public Symbol getFirstSymbol() {
		return symbol;
	}

	@Override
	public String getJavaName() {
		return MetaHelper.getJavaNameOfkeyword(symbol.symbolString);
	}

	/**
	 * remove underscore and suffixes in numbers. Then if the input is
	 * "3.1415D" the return is "3.1415". If the input is
	 *      "1_000_000Long" the return is  "1000000"
	   @param strnum
	   @return
	 */
	static String removeUnderscoreAndSuffix(String strnum) {
		String ret = "";
		for (int i = 0; i < strnum.length(); ++i) {
			char ch = strnum.charAt(i);
			if ( Character.isDigit(ch) )
				ret = ret + ch;
		}
		return ret;
	}


	static public Object evalLiteral(ExprLiteral exprLiteral, String typeName, EvalEnv ee, Class<?> rawClass) {
		return ExprLiteral.evalLiteral(exprLiteral, typeName, ee, exprLiteral.getJavaValue(), rawClass);
	}


	static public Object evalLiteral(ExprLiteral exprLiteral, String typeName, EvalEnv ee,
			Object constructorParameter, Class<?> rawClass) {
		//TypeJavaRef classRef = ee.getCyanLangPackage().getJvmTypeClassMap().get( MetaHelper.getJavaName(typeName) );

		//Class<?> cyanClass = classRef.getaClass(ee.env, exprLiteral.getFirstSymbol());
        Object ret = null;

        try {
        	// System.out.println(constructorParameter.getClass().getName());
    		Constructor<?> constructor;

    		switch (typeName) {
    		case "Boolean" : constructor = EvalEnv.cyBooleanConstructor ; break;
    		case "Char"    : constructor = EvalEnv.cyCharConstructor; break;
    		case "Byte"    : constructor = EvalEnv.cyByteConstructor; break;
    		case "Int"     : constructor = EvalEnv.cyIntConstructor; break;
    		case "Short"   : constructor = EvalEnv.cyShortConstructor; break;
    		case "Long"    : constructor = EvalEnv.cyLongConstructor; break;
    		case "Float"   : constructor = EvalEnv.cyFloatConstructor; break;
    		case "Double"  : constructor = EvalEnv.cyDoubleConstructor; break;
    		case "String"  : constructor = EvalEnv.cyStringConstructor; break;
    		default:
    			Class<?> cyanClass = ee.searchPrototypeAsType(typeName);
    			if ( cyanClass == null ) {
    				ee.error(exprLiteral.getFirstSymbol(), "Prototype or class '" + typeName + "' was not found");
    				return null;
    			}
        		constructor = cyanClass.getConstructor(rawClass);
        		constructor.setAccessible(true);
    		}

            ret = constructor.newInstance(constructorParameter);
        }
        catch (InvocationTargetException | InstantiationException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException tmp2359) {
        	ee.error(exprLiteral.getFirstSymbol(), "An exception was "
        			+ "thrown by calling a constructor for literal object'" +  exprLiteral.asString() + "'");
        }
        return ret;
	}



	protected Symbol symbol;


}
