package meta.cyanLang;

import java.util.List;
import java.util.Formatter;
import meta.CyanMetaobject;
import meta.IActionFunction;
import meta.Tuple2;
import meta.WrExpr;
import meta.WrExprLiteralString;
import meta.WrMessageKeywordWithRealParameters;
import meta.WrMessageWithKeywords;

public class CyanMetaobjectCheckPrintf extends CyanMetaobject implements IActionFunction {

	@SuppressWarnings("unchecked")
	@Override
	public Object eval(Object toBeChecked) {
		if ( !(toBeChecked instanceof Tuple2<?, ?>) ) {
			return "Metaobject '" + CyanMetaobjectCheckPrintf.class.getName() + "' used incorrectly";
		}
		else {
			Tuple2<WrExpr, WrMessageWithKeywords> t = (Tuple2<WrExpr, WrMessageWithKeywords> ) toBeChecked;
			if ( t.f2.getkeywordParameterList().get(0).getExprList().size() > 1 ) {
				WrMessageWithKeywords message = t.f2;
				List<WrMessageKeywordWithRealParameters>  selList = message.getkeywordParameterList();
				WrMessageKeywordWithRealParameters sel = selList.get(0);
				WrExpr format = sel.getExprList().get(0);
				if ( format instanceof WrExprLiteralString ) {
					String strFormat = (String ) ((WrExprLiteralString) format).getJavaValue();
					StringBuffer sb = new StringBuffer();
					Object []objList = new Object[sel.getExprList().size()-1];
					int i = 0;
					boolean first = true;
					for ( WrExpr e : sel.getExprList() ) {
						if ( first ) {
							first = false;
							continue;
						}
						switch ( e.getType().getName() ) {
						case "Int" : objList[i] = 0;  break;
						case "Byte" : objList[i] = (byte) 0;  break;
						case "Char" : objList[i] = 'a';  break;
						case "Boolean" : objList[i] = true;  break;
						case "Long" : objList[i] = (long ) 0;  break;
						case "Short" : objList[i] = (short ) 0;  break;
						case "Float" : objList[i] = (float ) 0;  break;
						case "Double" : objList[i] = (double ) 0;  break;
						case "String" : objList[i] = "";  break;
						}
						++i;
					}

					try(Formatter formatter = new Formatter(sb); ) {
						// formatter.format("%d %s", "k", 6);
						formatter.format(strFormat, objList);
						int countPercent = 0;
						for ( int k = 0; k < strFormat.length(); ++k ) {
							char ch = strFormat.charAt(k);
							if ( ch == '%' && k < strFormat.length() - 1 && strFormat.charAt(k+1) != '%' ) {
								++countPercent;
							}
						}
						if ( countPercent < sel.getExprList().size()-1 )
							return "Too many expressions following the format string";
						else if ( countPercent > sel.getExprList().size()-1  )
							return "Too few expressions following the format string";
					}
					catch ( Throwable th ) {
						return "The format string does not match the parameters";
					}
				}
				return null;
			}

		}
		return null;
	}

	@Override
	public String getName() {
		return "checkPrintf";
	}

	@Override
	public String getPackageOfType() {
		return null;
	}

	@Override
	public String getPrototypeOfType() {
		return null;
	}

}
