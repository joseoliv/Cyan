package meta.cyanLang;

import java.util.List;
import cyanruntime.Ref;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAbstractCyanCompiler;
import meta.IActionFunction;
import meta.ICompiler_semAn;
import meta.Tuple4;
import meta.WrASTVisitor;
import meta.WrEnv;
import meta.WrMethodDec;
import meta.WrPrototype;
import meta.WrStatementAssignmentList;

/**
 * demonstration metaobject
   @author jose
 */
public class CyanMetaobjectAddInfoMethods extends CyanMetaobject implements IActionFunction {

	@Override
	public String getName() {
		return "addInfoMethods";
	}


	@SuppressWarnings("unchecked")
	@Override
	public Object eval(Object input) {
		final StringBuffer s = new StringBuffer();
		if ( input instanceof Tuple4<?,?,?,?>) {

//			Tuple4<String, IAbstractCyanCompiler, CyanMetaobjectAtAnnot, List<String>> t =
//					(Tuple4<String, IAbstractCyanCompiler, CyanMetaobjectAtAnnot,
//						List<String>> ) input;
			Tuple4<?,?,?,?> t = (Tuple4<?,?,?,?> ) input;
			if ( !(t.f1 instanceof String) || !(t.f2 instanceof IAbstractCyanCompiler) ||
					!(t.f3 instanceof CyanMetaobjectAtAnnot) || !(t.f4 instanceof List<?>) ) {
				return null;
			}
			if ( t.f1.equals("semAn_codeToAdd") ) {
				s.append("\"\"\"");
				ICompiler_semAn compiler = (ICompiler_semAn ) t.f2;
				CyanMetaobjectAtAnnot mo = (CyanMetaobjectAtAnnot ) t.f3;
				List<String> paramNameList = (List<String> ) t.f4;
				WrPrototype pu = mo.getCurrentPrototype();
				final Ref<Integer> count = new Ref<Integer>();
				count.elem = 0;
				pu.accept(new WrASTVisitor() {
					@Override
					public void visit(WrMethodDec node, WrEnv env) {
						for ( String param : paramNameList) {
							if ( param.equals(node.getName()) ) {
								s.append("visited: " + param + "\n");
							}
						}

					}
					@Override
					public void visit(WrStatementAssignmentList node, WrEnv env) {
						s.append("assign: " + node.getExprList().get(0).asString() + "\n");
						++count.elem;
					}

				}, compiler.getEnv());
				for ( String p : paramNameList ) {
					s.append("parameter: " + p + "\n");
				}
				s.append("\"\"\" println;");
			}

		}


		return s.length() == 0 ? null : s;
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

