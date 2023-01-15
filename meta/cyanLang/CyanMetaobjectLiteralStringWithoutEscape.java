package meta.cyanLang;

import meta.CyanMetaobjectLiteralString;
import meta.ICompiler_semAn;
import meta.lexer.MetaLexer;

/**
 * <code>n"c:\dropbox\cyan\files"</code> is a literal string in which the escape characters are not considered.
 * Then <code>n"\n"</code> has two characters.
   @author jose
 */
public class CyanMetaobjectLiteralStringWithoutEscape extends CyanMetaobjectLiteralString
       {

		public CyanMetaobjectLiteralStringWithoutEscape() {
			super(new String[] { "n", "N" });
		}

//		@Override
//		public void parsing_parse(ICompilerAction_parsing compilerAction, String code) {
//
//			// // setInfo( new StringBuffer("\"" + Lexer.escapeJavaString(code) + "\"") );
//			this.codeToGenerate = new StringBuffer("\"" + MetaLexer.escapeJavaString(code) + "\"");
//		}

		@Override
		public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
			// // return (StringBuffer ) getInfo();

			String code = getUsefulString();
			// this.codeToGenerate = new StringBuffer("\"" + MetaLexer.escapeJavaString(code) + "\"");
			return new StringBuffer("\"" + MetaLexer.escapeJavaString(code) + "\"");
		}

		@Override
		public String getPackageOfType() {
			return "cyan.lang";
		}

		@Override
		public String getPrototypeOfType() {
			return "String";
		}

		/**
		 * code to be generated
		 */
		private StringBuffer codeToGenerate;

}



