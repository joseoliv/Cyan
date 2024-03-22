package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionMessageSend_semAn;
import meta.IAction_semAn;
import meta.ICompiler_semAn;
import meta.MetaHelper;
import meta.Tuple3;
import meta.WrEnv;
import meta.WrExprIdentStar;
import meta.WrExprMessageSendUnaryChainToExpr;
import meta.WrExprMessageSendWithKeywordsToExpr;
import meta.WrMethodDec;
import meta.WrType;

/** An annotation of this metaobject should be attached to a 'new' or 'new:' method to count
 * how many times it was called. The result is put in the global table as a hash table. See the
 * example
 *
        cast table = (System globalTable get: "CountNew") {
            type table get: "main.Program"
                case Int count2 {
                    "$count2 objects of Program were created" println
                }
        }
 *
   @author jose
 */
public class CyanMetaobjectCountNew extends CyanMetaobjectAtAnnot
      implements IActionMessageSend_semAn, IAction_semAn {

	public CyanMetaobjectCountNew() {
		super("countNew", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC } );
	}


	@Override
	public Tuple3<StringBuffer, String, String> semAn_analyzeReplaceKeywordMessage(
			WrExprMessageSendWithKeywordsToExpr messageSendExpr, WrEnv env) {

		String strMessageSend = messageSendExpr.asString();
		WrType type = messageSendExpr.getReceiverExpr().getType();
		StringBuffer s = genCountFunction(strMessageSend, type);

		return new Tuple3<StringBuffer, String, String>(s, null, null);
		//return new Tuple2<StringBuffer, WrType>(s, type);
	}

	/**
	 *
	 */
	@Override
	public Tuple3<StringBuffer, String, String> semAn_analyzeReplaceUnaryMessage(WrExprMessageSendUnaryChainToExpr messageSendExpr, WrEnv env) {

		String strMessageSend = messageSendExpr.asString();
		WrType type = messageSendExpr.getReceiver().getType();


		StringBuffer s = genCountFunction(strMessageSend, type);

		return new Tuple3<StringBuffer, String, String>(s, null, null);
		//return new Tuple2<StringBuffer, WrType>(s, type);
	}

	@Override
	public
	Tuple3<StringBuffer, String, String> semAn_analyzeReplaceUnaryMessageWithoutSelf(
			WrExprIdentStar messageSendExpr, WrEnv env) {


		String strMessageSend = messageSendExpr.asString();
		WrType type = env.getCurrentPrototype();


		StringBuffer s = genCountFunction(strMessageSend, type);

		return new Tuple3<StringBuffer, String, String>(s, null, null);
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler) {

		WrMethodDec m = ((WrMethodDec ) this.getAnnotation().getDeclaration());
		String protoName = m.getDeclaringObject().getFullName();
		compiler.createNewGenericPrototype(this.getAnnotation().getFirstSymbol(),
				compiler.getEnv().getCurrentCompilationUnit(), compiler.getEnv().getCurrentPrototype(),
				MetaHelper.cyanLanguagePackageName + ".Function<" +
						protoName + ", " + protoName + ">",
		            "Error caused by method semAn_codeToAdd of metaobject '" +
		            		this.getAnnotation().getCyanMetaobject().getName() + "'. "
		            );

		compiler.createNewGenericPrototype(this.getAnnotation().getFirstSymbol(),
				compiler.getEnv().getCurrentCompilationUnit(), compiler.getEnv().getCurrentPrototype(),
				MetaHelper.cyanLanguagePackageName + ".Function<String, Int, Nil>",
		            "Error caused by method semAn_codeToAdd of metaobject '" +
		            		this.getAnnotation().getCyanMetaobject().getName() + "'. "
		            );


		compiler.createNewGenericPrototype(this.getAnnotation().getFirstSymbol(),
				compiler.getEnv().getCurrentCompilationUnit(), compiler.getEnv().getCurrentPrototype(),
				MetaHelper.cyanLanguagePackageName + ".HashMap<String, Int>",
		            "Error caused by method semAn_codeToAdd of metaobject '" +
		            		this.getAnnotation().getCyanMetaobject().getName() + "'. "
		            );

		return null;
	}



	/**
	   @param strMessageSend
	   @param receiverType
	   @return
	 */
	private static StringBuffer genCountFunction(String strMessageSend,
			WrType receiverType) {
		String tmpVar = MetaHelper.nextIdentifier();
		String prototypeName = receiverType.getFullName();
		StringBuffer s = new StringBuffer();

        s.append("        {\n");
        s.append("            var " + tmpVar + " = " + strMessageSend + "; \n");
        s.append("            var HashMap<String, Int> tmpVar3;\n");
        s.append("            cast tmpVar2 = System globalTable get: \"CountNew\" {\n");
        s.append("                tmpVar3 = tmpVar2;\n");
        s.append("            }\n");
        s.append("            else {\n");
        s.append("                tmpVar3 = HashMap<String, Int>();\n");
        s.append("                System globalTable at: \"CountNew\" put: tmpVar3;\n");
        s.append("            }\n");
        s.append("            var Int tmpVar5;\n");
        s.append("            cast Int tmpVar4 = tmpVar3 get: \""+ prototypeName + "\" {\n");
        s.append("                tmpVar5 = tmpVar4\n");
        s.append("            }\n");
        s.append("            else {\n");
        s.append("                tmpVar5 = 0\n");
        s.append("            }\n");
        s.append("            tmpVar3[\"" + prototypeName + "\"] = tmpVar5 + 1;\n");
        // s.append("            (\"\"\" \"tmpVar5\" = \"\"\" ++ tmpVar5) println;");
        s.append("            ^" + tmpVar + "\n" );
        s.append("        } eval\n");
		return s;
	}


}

