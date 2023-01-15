package lexer;

import java.util.HashMap;
import java.util.Map;

public class HighlightColor {

	public HighlightColor() {
		colorNameIntMap = new HashMap<String, Integer>();
		colorNameIntMap.put("field", HighlightColor.field);
		colorNameIntMap.put("booleanLiteral", HighlightColor.booleanLiteral);
		colorNameIntMap.put("byteLiteral", HighlightColor.byteLiteral);
		colorNameIntMap.put("charLiteral", HighlightColor.charLiteral);
		colorNameIntMap.put("cyanAnnotation", HighlightColor.cyanAnnotation);
		colorNameIntMap.put("cyanSymbol", HighlightColor.cyanSymbol);
		colorNameIntMap.put("doubleLiteral", HighlightColor.doubleLiteral);
		colorNameIntMap.put("floatLiteral", HighlightColor.floatLiteral);
		colorNameIntMap.put("intLiteral", HighlightColor.intLiteral);
		colorNameIntMap.put("keyword", HighlightColor.keyword);
		colorNameIntMap.put("literalObject", HighlightColor.literalObject);
		colorNameIntMap.put("literalObjectParsedWithCompiler", HighlightColor.literalObjectParsedWithCompiler);
		colorNameIntMap.put("longLiteral", HighlightColor.longLiteral);
		colorNameIntMap.put("macroKeyword", HighlightColor.macroKeyword);
		colorNameIntMap.put("shortLiteral", HighlightColor.shortLiteral);
		colorNameIntMap.put("stringLiteral", HighlightColor.stringLiteral);

	}
	public int getColor(String name) {
		Integer color = this.colorNameIntMap.get(name);
		if ( color == null )
			return -1;
		else
			return color;
	}

	private Map<String, Integer> colorNameIntMap;

	public static int field = 0x11BFC0;
	public static int booleanLiteral = 0x8E0B4A;
	public static int byteLiteral = 0x1041CB;
	public static int charLiteral = 0x1041CB;
	public static int cyanAnnotation = 0xCB1F10;
	public static int cyanSymbol = 0x1041CB;
	public static int doubleLiteral = 0x1041CB;
	public static int floatLiteral = 0x1041CB;
	public static int intLiteral = 0x1041CB;
	public static int keyword = 0x8E0B4A;
	public static int literalObject = 0xCB1F10;
	public static int literalObjectParsedWithCompiler = 0xCB1F10;
	public static int longLiteral = 0x1041CB;
	public static int macroKeyword = 0xCB1F10;
	public static int shortLiteral = 0x1041CB;
	public static int stringLiteral = 0x1041CB;
}
