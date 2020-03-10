package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpa;
import meta.ICompilerAction_dpa;

/**
 * This metaobject creates methods for prototype <code>Function{@literal<}Nil></code>.
   @author jose
 */
public class CyanMetaobjectCreateCatchMethodsForFunctionNil extends CyanMetaobjectAtAnnot
		implements IAction_dpa {

	public CyanMetaobjectCreateCatchMethodsForFunctionNil() {
		super("createCatchMethodsForFunctionNil", AnnotationArgumentsKind.ZeroParameters);
	}


	@Override
	public StringBuffer dpa_codeToAdd(ICompilerAction_dpa compiler) {

		StringBuffer s = new StringBuffer();

		if ( ! compiler.getCurrentPrototypeName().equals("Function<Nil>") ) {
			compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
					"' should only be used in prototype Function<Nil>");
			return null;
		}
		for (int i = 1; i <= maxCatch; ++i) {
			s.append("    @checkCatchParameter    \n");
			s.append("    final func ");
			for (int j = 1; j <= i; ++j) {
				s.append("catch: Any f" + j + "  ");
			}
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


			s.append("    @checkCatchParameter    \n");
			s.append("    final func ");
			for (int j = 1; j <= i; ++j) {
				s.append("catch: Any f" + j + "  ");
			}
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


			s.append("    @checkCatchParameter    \n");
			s.append("    final func ");
			for (int j = 1; j <= i; ++j) {
				s.append("catch: Any f" + j + "  ");
			}
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


			s.append("    @checkCatchParameter    \n");
			s.append("    final func ");
			for (int j = 1; j <= i; ++j) {
				s.append("catch: Any f" + j + "  ");
			}
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



		return s;
	}
	final static int maxCatch = 5;
}

