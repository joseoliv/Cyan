package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionMessageSend_dsa;
import meta.IAction_dsa;
import meta.ICompiler_dsa;
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
      implements IActionMessageSend_dsa, IAction_dsa {

	public CyanMetaobjectCountNew() {
		super("countNew", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC } );
	}


	@Override
	public Tuple3<StringBuffer, String, String> dsa_analyzeReplaceKeywordMessage(
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
	public Tuple3<StringBuffer, String, String> dsa_analyzeReplaceUnaryMessage(WrExprMessageSendUnaryChainToExpr messageSendExpr, WrEnv env) {

		String strMessageSend = messageSendExpr.asString();
		WrType type = messageSendExpr.getReceiver().getType();


		StringBuffer s = genCountFunction(strMessageSend, type);

		return new Tuple3<StringBuffer, String, String>(s, null, null);
		//return new Tuple2<StringBuffer, WrType>(s, type);
	}

	@Override
	public
	Tuple3<StringBuffer, String, String> dsa_analyzeReplaceUnaryMessageWithoutSelf(
			WrExprIdentStar messageSendExpr, WrEnv env) {


		String strMessageSend = messageSendExpr.asString();
		WrType type = env.getCurrentProgramUnit();


		StringBuffer s = genCountFunction(strMessageSend, type);

		return new Tuple3<StringBuffer, String, String>(s, null, null);
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler) {

		WrMethodDec m = ((WrMethodDec ) this.getMetaobjectAnnotation().getDeclaration());
		String protoName = m.getDeclaringObject().getFullName();
		compiler.createNewGenericPrototype(this.getMetaobjectAnnotation().getFirstSymbol(),
				compiler.getEnv().getCurrentCompilationUnit(), compiler.getEnv().getCurrentProgramUnit(),
				MetaHelper.cyanLanguagePackageName + ".Function<" +
						protoName + ", " + protoName + ">",
		            "Error caused by method dsa_codeToAdd of metaobject '" +
		            		this.getMetaobjectAnnotation().getCyanMetaobject().getName() + "'. "
		            );

		compiler.createNewGenericPrototype(this.getMetaobjectAnnotation().getFirstSymbol(),
				compiler.getEnv().getCurrentCompilationUnit(), compiler.getEnv().getCurrentProgramUnit(),
				MetaHelper.cyanLanguagePackageName + ".Function<String, Int, Nil>",
		            "Error caused by method dsa_codeToAdd of metaobject '" +
		            		this.getMetaobjectAnnotation().getCyanMetaobject().getName() + "'. "
		            );


		compiler.createNewGenericPrototype(this.getMetaobjectAnnotation().getFirstSymbol(),
				compiler.getEnv().getCurrentCompilationUnit(), compiler.getEnv().getCurrentProgramUnit(),
				MetaHelper.cyanLanguagePackageName + ".HashMap<String, Int>",
		            "Error caused by method dsa_codeToAdd of metaobject '" +
		            		this.getMetaobjectAnnotation().getCyanMetaobject().getName() + "'. "
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

