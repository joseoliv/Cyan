package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.IDeclaration;
import meta.Token;
import meta.Tuple3;
import meta.WrMethodDec;
import meta.WrPrototype;

/**
 *     Annotation 'log', when attached to a prototype P, without paramters,
    adds a statement to each method 'm':
        "Calling method 'm' of prototype 'P'" println

    The same result is got by attaching 'log' to method 'm'

    Annotation may take a single parameter that is either an identifier
    or a String. The metaobject creates a hashtable HmetCount of type
        HashMap<String, Int>
    and inserts it in the global table with the key that is the identifier.
    In the hashtable HmetCount, each key is a full method name of the form
          packageName.prototypeName::methodName
    The value is an Int with the number of times the method was called.
    See the example below to discover how to use the tables.

        @log(mylog)
        object Log

            func run {

                zero;
                one: 0 two: 1;
                self one: 0 two: 1;
                self three: "a", "b", "c";
                self three: "a", "b", "c";
                self three: "a", "b", "c";


                cast mapStrDyn = System globalTable get: "mylog" {
                    var HashMap<String, Int> logMap = mapStrDyn;
                    for elem in logMap asArray {
                        // Tuple<key, K, value, V>
                        Out println: "Method: " ++ elem key ++
                            " Count: " ++ elem value;
                    }
                }

            }

            func zero  {
            }
            func one: Int n two: Int nn { }
            func three: String a, String b, String  c {
                var s = "after calling 'three'";
                assert s size > 10;
            }

        end

   @author jose
 */

public class CyanMetaobjectLog extends CyanMetaobjectAtAnnot implements IAction_afterResTypes { // , IAction_semAn {

	public CyanMetaobjectLog() {
		super("log", AnnotationArgumentsKind.ZeroOrMoreParameters,
				new AttachedDeclarationKind[]{ AttachedDeclarationKind.METHOD_DEC, AttachedDeclarationKind.PROTOTYPE_DEC
						});
	}

	@Override
	public List<Tuple3<String, StringBuffer, Boolean>> afterResTypes_beforeMethodCodeList(
			ICompiler_afterResTypes compiler) {
		List<Tuple3<String, StringBuffer, Boolean>> array = new ArrayList<>();
		IDeclaration dec = this.getAttachedDeclaration();
		List<Object> paramList = this.getAnnotation().getJavaParameterList();
		String logMapName = null;

		if ( paramList != null ) {
			if ( paramList.size() == 1 ) {
				if ( paramList.get(0) instanceof String ) {
					logMapName = CyanMetaobject.removeQuotes((String ) paramList.get(0));
				}
				else {
					this.addError("Annotation '" + this.getName() + "' should take no parameters or a String parameter");
				}
			}
			else if ( paramList.size() != 0 ) {
				this.addError("Annotation '" + this.getName() + "' should take zero or one parameter");
			}
		}




		if ( dec instanceof WrMethodDec && !((WrMethodDec ) dec).isAbstract() ) {
			WrMethodDec m = (WrMethodDec ) dec;
			StringBuffer sb;
			if ( logMapName != null ) {
				sb = CyanMetaobjectLog.genLogCount(m.getMethodSignature().getName(),  compiler.getEnv().getCurrentPrototype().getFullName(), logMapName);
			}
			else {
				sb = new StringBuffer("    \"Calling method '" +
						m.getMethodSignature().getName() +
						"' of prototype '" + m.getDeclaringObject().getName() +
						"\" println;");
			}
			Tuple3<String, StringBuffer, Boolean> t = new Tuple3<>(
					m.getMethodSignature().getName(),
					sb, true);
			array.add(t);
			return array;

		}
		else if ( dec instanceof WrPrototype && ! ((WrPrototype ) dec).isInterface() ) {
			WrPrototype pu = (WrPrototype ) dec;
			for ( WrMethodDec m : pu.getMethodDecList(compiler.getEnv()) ) {
				if ( m.getVisibility() == Token.PUBLIC  ) {
					StringBuffer sb;
					if ( logMapName != null ) {
						sb = CyanMetaobjectLog.genLogCount(m.getMethodSignature().getName(),
								pu.getFullName(), logMapName);
					}
					else {
						sb = new StringBuffer("    \"Calling method '" +
								m.getMethodSignature().getName() +
								"' of prototype '" + m.getDeclaringObject().getName() +
								"'\" println;");
					}

					Tuple3<String, StringBuffer, Boolean> t = new Tuple3<>(
							m.getMethodSignature().getName(),
							sb, true);
					array.add(t);
				}
			}
			return array;
		}
		else {
			this.addError("Annotation '" + this.getName() + "' can be attached only to prototypes and non-abstract methods");
		}
		return null;
	}


	private static StringBuffer genLogCount(String methodName, String prototypeName, String logMapName) {
		StringBuffer s = new StringBuffer();

        s.append("        {\n");
        s.append("            var HashMap<String, Int> tmpVar3;\n");
        s.append("            cast tmpVar2 = System globalTable get: \"" + logMapName + "\" {\n");
        s.append("                tmpVar3 = tmpVar2;\n");
        s.append("            }\n");
        s.append("            else {\n");
        s.append("                tmpVar3 = HashMap<String, Int>();\n");
        s.append("                System globalTable at: \"" + logMapName + "\" put: tmpVar3;\n");
        s.append("            }\n");
        s.append("            var Int tmpVar5;\n");
        String key = prototypeName + "::" + methodName;
        s.append("            cast Int tmpVar4 = tmpVar3 get: \""+ key + "\" {\n");
        s.append("                tmpVar5 = tmpVar4\n");
        s.append("            }\n");
        s.append("            else {\n");
        s.append("                tmpVar5 = 0\n");
        s.append("            }\n");
        s.append("            tmpVar3[\"" + key + "\"] = tmpVar5 + 1;\n");
        s.append("        } eval;\n");
		return s;
	}

}
