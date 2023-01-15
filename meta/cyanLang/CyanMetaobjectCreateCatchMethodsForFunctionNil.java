package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.Tuple2;
import meta.WrAnnotation;

/**
 * This metaobject creates methods for prototype <code>Function{@literal<}Nil></code>.
   @author jose
 */
public class CyanMetaobjectCreateCatchMethodsForFunctionNil extends CyanMetaobjectAtAnnot
		implements IAction_afterResTypes //, IAction_parsing
		{

	public CyanMetaobjectCreateCatchMethodsForFunctionNil() {
		super("createCatchMethodsForFunctionNil", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC });
	}


	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
			List<ISlotSignature>>> infoList) {

		String signatureList = "";



		StringBuffer s = new StringBuffer();

		if ( ! compiler.getCurrentPrototypeName().equals("Function<Nil>") ) {
			compiler.error(this.getAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
					"' should only be used in prototype Function<Nil>");
			return null;
		}
		for (int i = 1; i <= maxCatch; ++i) {
			signatureList += "    final func ";
			s.append("    @checkCatchParameter    \n");
			s.append("    final func ");
			for (int j = 1; j <= i; ++j) {
				signatureList += "catch: Any f" + j + "  ";
				s.append("catch: Any f" + j + "  ");
			}
			signatureList += ";\n";
			s.append(" { \n");
			s.append("        @javacode{*    \n");
			s.append("            try {    \n");
			s.append("                this._eval();    \n");
			s.append("            } catch (ExceptionContainer__ t) {    \n");
			s.append("                catchException( new Object[] { ");
			for (int j = 1; j <= i; ++j) {
				s.append("_f" + j);
				if ( j < i )
					s.append(", ");
			}
			s.append(" }, t);    \n");
			s.append("            }    \n");
			s.append("        *}    \n");
			s.append("    return Nil\n");
			s.append("    }    \n");


			signatureList += "    final func ";

			s.append("    @checkCatchParameter    \n");
			s.append("    final func ");
			for (int j = 1; j <= i; ++j) {
				signatureList += "catch: Any f" + j + "  ";
				s.append("catch: Any f" + j + "  ");
			}
			signatureList += " finally: Function<Nil> ;\n";
			s.append(" finally: Function<Nil> fin { \n");
			s.append("        @javacode{*    \n");
			s.append("            try {    \n");
			s.append("                this._eval();    \n");
			s.append("            } catch (ExceptionContainer__ t) {    \n");
			s.append("                catchException( new Object[] { ");
			for (int j = 1; j <= i; ++j) {
				s.append("_f" + j);
				if ( j < i )
					s.append(", ");
			}
			s.append(" }, t);    \n");
			s.append("            }    \n");
			s.append("            finally {   \n");
			s.append("                _fin._eval();   \n");
			s.append("            }   \n");
			s.append("        *}    \n");
			s.append("    return Nil\n");
			s.append("    }    \n");

			signatureList += "    final func ";

			s.append("    @checkCatchParameter    \n");
			s.append("    final func ");
			for (int j = 1; j <= i; ++j) {
				s.append("catch: Any f" + j + "  ");
				signatureList += "catch: Any f" + j + "  ";
			}
			signatureList += " retry: Function<Nil>;\n";
			s.append(" retry: Function<Nil> r { \n");
			s.append("        @javacode{*    \n");
			s.append("            boolean retry = false;\n");
			s.append("            while ( true ) {\n");
			s.append("                try {    \n");
			s.append("                    this._eval();    \n");
			s.append("                } catch (ExceptionContainer__ t) {    \n");
			s.append("                    catchException( new Object[] { ");
			for (int j = 1; j <= i; ++j) {
				s.append("_f" + j);
				if ( j < i )
					s.append(", ");
			}
			s.append(" }, t);    \n");
			s.append("                    retry = true; \n");
			s.append("                }    \n");
			s.append("                if ( ! retry )   \n");
			s.append("                    break;   \n");
			s.append("                else {\n");
			s.append("                    _r._eval();\n");
			s.append("                    retry = false;\n");
			s.append("                }\n");
			s.append("            }   \n");
			s.append("        *}    \n");
			s.append("    return Nil\n");
			s.append("    }    \n");


			signatureList += "    final func ";

			s.append("    @checkCatchParameter    \n");
			s.append("    final func ");
			for (int j = 1; j <= i; ++j) {
				signatureList += "catch: Any f" + j + "  ";
				s.append("catch: Any f" + j + "  ");
			}
			signatureList += "tryWhileTrue: Function<Boolean>; \n";
			s.append(" tryWhileTrue: Function<Boolean> tryIfTrue { \n");
			s.append("        @javacode{*    \n");
			s.append("            boolean retry = false;\n");
			s.append("            while ( true ) {\n");
			s.append("                try {    \n");
			s.append("                    this._eval();    \n");
			s.append("                } catch (ExceptionContainer__ t) {    \n");
			s.append("                    catchException( new Object[] { ");
			for (int j = 1; j <= i; ++j) {
				s.append("_f" + j);
				if ( j < i )
					s.append(", ");
			}
			s.append(" }, t);    \n");
			s.append("                    retry = true;\n");
			s.append("                }    \n");
			s.append("                if ( retry ) {\n");
			s.append("                    if ( ! _tryIfTrue._eval().b )\n");
			s.append("                        break;\n");
			s.append("                    retry = false;\n");
			s.append("                }\n");
			s.append("                else\n");
			s.append("                    break;\n");
			s.append("            }   \n");
			s.append("        *}    \n");
			s.append("    return Nil\n");
			s.append("    }    \n");


		}

		return new Tuple2<StringBuffer, String>(s, signatureList);
	}


//	@Override
//	public StringBuffer parsing_codeToAdd(ICompilerAction_parsing compiler) {
//
//		StringBuffer s = new StringBuffer();
//
//		if ( ! compiler.getCurrentPrototypeName().equals("Function<Nil>") ) {
//			compiler.error(this.getAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
//					"' should only be used in prototype Function<Nil>");
//			return null;
//		}
//		for (int i = 1; i <= maxCatch; ++i) {
//			s.append("    @checkCatchParameter    \n");
//			s.append("    final func ");
//			for (int j = 1; j <= i; ++j) {
//				s.append("catch: Any f" + j + "  ");
//			}
//			s.append(" { \n");
//			s.append("        @javacode{*    \n");
//			s.append("            try {    \n");
//			s.append("                this._eval();    \n");
//			s.append("            } catch (ExceptionContainer__ t) {    \n");
//			s.append("                catchException( new Object[] { ");
//			for (int j = 1; j <= i; ++j) {
//				s.append("_f" + j);
//				if ( j < i )
//					s.append(", ");
//			}
//			s.append(" }, t);    \n");
//			s.append("            }    \n");
//			s.append("        *}    \n");
//			s.append("    return Nil\n");
//			s.append("    }    \n");
//
//
//			s.append("    @checkCatchParameter    \n");
//			s.append("    final func ");
//			for (int j = 1; j <= i; ++j) {
//				s.append("catch: Any f" + j + "  ");
//			}
//			s.append(" finally: Function<Nil> fin { \n");
//			s.append("        @javacode{*    \n");
//			s.append("            try {    \n");
//			s.append("                this._eval();    \n");
//			s.append("            } catch (ExceptionContainer__ t) {    \n");
//			s.append("                catchException( new Object[] { ");
//			for (int j = 1; j <= i; ++j) {
//				s.append("_f" + j);
//				if ( j < i )
//					s.append(", ");
//			}
//			s.append(" }, t);    \n");
//			s.append("            }    \n");
//			s.append("            finally {   \n");
//			s.append("                _fin._eval();   \n");
//			s.append("            }   \n");
//			s.append("        *}    \n");
//			s.append("    return Nil\n");
//			s.append("    }    \n");
//
//
//			s.append("    @checkCatchParameter    \n");
//			s.append("    final func ");
//			for (int j = 1; j <= i; ++j) {
//				s.append("catch: Any f" + j + "  ");
//			}
//			s.append(" retry: Function<Nil> r { \n");
//			s.append("        @javacode{*    \n");
//			s.append("            boolean retry = false;\n");
//			s.append("            while ( true ) {\n");
//			s.append("                try {    \n");
//			s.append("                    this._eval();    \n");
//			s.append("                } catch (ExceptionContainer__ t) {    \n");
//			s.append("                    catchException( new Object[] { ");
//			for (int j = 1; j <= i; ++j) {
//				s.append("_f" + j);
//				if ( j < i )
//					s.append(", ");
//			}
//			s.append(" }, t);    \n");
//			s.append("                    retry = true; \n");
//			s.append("                }    \n");
//			s.append("                if ( ! retry )   \n");
//			s.append("                    break;   \n");
//			s.append("                else {\n");
//			s.append("                    _r._eval();\n");
//			s.append("                    retry = false;\n");
//			s.append("                }\n");
//			s.append("            }   \n");
//			s.append("        *}    \n");
//			s.append("    return Nil\n");
//			s.append("    }    \n");
//
//
//			s.append("    @checkCatchParameter    \n");
//			s.append("    final func ");
//			for (int j = 1; j <= i; ++j) {
//				s.append("catch: Any f" + j + "  ");
//			}
//			s.append(" tryWhileTrue: Function<Boolean> tryIfTrue { \n");
//			s.append("        @javacode{*    \n");
//			s.append("            boolean retry = false;\n");
//			s.append("            while ( true ) {\n");
//			s.append("                try {    \n");
//			s.append("                    this._eval();    \n");
//			s.append("                } catch (ExceptionContainer__ t) {    \n");
//			s.append("                    catchException( new Object[] { ");
//			for (int j = 1; j <= i; ++j) {
//				s.append("_f" + j);
//				if ( j < i )
//					s.append(", ");
//			}
//			s.append(" }, t);    \n");
//			s.append("                    retry = true;\n");
//			s.append("                }    \n");
//			s.append("                if ( retry ) {\n");
//			s.append("                    if ( ! _tryIfTrue._eval().b )\n");
//			s.append("                        break;\n");
//			s.append("                    retry = false;\n");
//			s.append("                }\n");
//			s.append("                else\n");
//			s.append("                    break;\n");
//			s.append("            }   \n");
//			s.append("        *}    \n");
//			s.append("    return Nil\n");
//			s.append("    }    \n");
//
//
//		}
//
//
//
//		return s;
//	}

	final static int maxCatch = 5;








}

