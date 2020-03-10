package meta.cyanLang;

import meta.Tuple2;
import meta.WrEnv;

abstract public class IMessageKeyword {

	abstract public void calcInterfaceTypes(WrEnv env);

	abstract public String getStringType();

	abstract public String getFullName(WrEnv env);

	abstract public String getName();

	abstract public boolean matchesEmptyInput();

	abstract public Tuple2<String, String> parse(MessageKeywordLexer lexer, WrEnv env);
}
