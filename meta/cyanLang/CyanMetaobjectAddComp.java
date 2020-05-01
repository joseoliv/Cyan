package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_semAn;
import meta.ISlotSignature;
import meta.Tuple2;
import meta.WrASTVisitor;
import meta.WrAnnotation;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrFieldDec;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrPrototype;

/**
 * This is a demonstration metaobject. <br>
 * Just ignore it.
   @author jose
 */
public class CyanMetaobjectAddComp extends CyanMetaobjectAtAnnot
           implements IAction_afterResTypes, ICheckDeclaration_afterSemAn   {

	public CyanMetaobjectAddComp() {
		super("addComp", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
			AttachedDeclarationKind.FIELD_DEC, AttachedDeclarationKind.METHOD_DEC,
			AttachedDeclarationKind.PROTOTYPE_DEC, AttachedDeclarationKind.PACKAGE_DEC,
			AttachedDeclarationKind.PROGRAM_DEC });
	}

    /*
    @Override
	public Tuple2<String, ExprAnyLiteral> infoToAddPrototype() {
		// List<Expr> exprList = ((AnnotationAt ) annotation).getRealParameterList();
		@SuppressWarnings("unchecked")
		Tuple2<String, ExprAnyLiteral> t = (Tuple2<String, ExprAnyLiteral> ) getAnnotation().getInfo_parsing();
		return t;
	}
    */


	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList){

		final WrAnnotationAt annot = this.getAnnotation();

		// ObjectDec é a classe da AST que representa protótipos que não são
		// interfaces
        // =============================== A ==============================
			/* conferir se T e um prototipo (nao pode ser interface);  */

		final WrPrototype prototype = (WrPrototype ) annot.getDeclaration();
		if ( prototype.isInterface() )  {
        	this.addError("Tipo do prototipo nao pode ser uma interface");
        	return null;
		}
        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



        // ObjectDec prototype = (ObjectDec) annot.getDeclaration();
		final String prototypeName = prototype.getName();
		final String methodName = "<1";
		List<WrMethodSignature> mList;
		WrMethodDec lessThanMethod = null;
		mList = compiler.getPrototype().searchMethodPrivateProtectedPublic(methodName, compiler.getEnv());
		if ( mList != null && mList.size() > 0 ) {
			if ( mList.get(0).getParameterList().get(0).getType() != compiler.getPrototype() ) {
				// erro
				this.addError("Tipo de parâmetro errado para método <");
				return null;
			}
			else {
				lessThanMethod = mList.get(0).getMethod();
			}
		}

		final StringBuffer out = new StringBuffer();

		String strSlotList = "";
        // =============================== B ==============================
	        /* Conferir se
	            T possui um metodofunc < T -> Boolean
	            se nao tiver, crie-o baseado nas variaveis de instancia como mostrado no prototipo Student
	            anterior. Assuma que os tipos de todas as variaveis de instancia tenham um metodo <.
	            A ordem em que as variaveis de instancia aparecem no mtodo < do prototipo anotado, comoStudent,
	            deve ser a ordem de declarac¸ao. Que e a ordem que o MOP retorna estas variaveis;
	        */
			if (lessThanMethod == null){
				strSlotList += "    func < " + prototypeName + " other -> Boolean;\n";
				out.append("    func < " + prototypeName + " other -> Boolean {\n");
				out.append("        return ");
				final List<WrFieldDec> ivList = compiler.getFieldList();
				if ( ivList == null || ivList.size() == 0 ) {
					this.addError("no iv");
					return null;
				}
				int size = ivList.size();
				for (final WrFieldDec iv : ivList) {
					String getMethod = iv.getName();
					if ( getMethod.startsWith("_") ) {
						getMethod = getMethod.substring(1);
					}
					else {
						getMethod = "get" + Character.toUpperCase(getMethod.charAt(0))
								+ getMethod.substring(1);
					}
					out.append(iv.getName() + " < other " + getMethod);
					if ( --size > 0 ) {
						out.append(" && ");
					}
				}
				out.append("\n    }\n");
			}
        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


		// =============================== C ==============================
			/* Adicionar um metodo " == Dyn other -> Boolean ". Se other nao eh do tipo T, retorne
				false. Se e, o metodo deve comparar todas as variaveis de instancia usando o metodo ==
				de cada uma delas. Se nao ha variaveis de instancia, sinalize erro; */
				strSlotList += "    func == Dyn other -> Boolean;\n";
				out.append("    override\n");
				out.append("    func == Dyn other -> Boolean {\n");
				out.append("        type other\n");
				out.append("            case " + prototypeName + " proto {\n");
				out.append("                return ");
				final List<WrFieldDec> ivList = compiler.getFieldList();
				if ( ivList == null || ivList.size() == 0 ) {
					this.addError("no iv");
					return null;
				}
				int size = ivList.size();
				for (final WrFieldDec iv : ivList) {
					String getMethod = iv.getName();
					if ( getMethod.startsWith("_") ) {
						getMethod = getMethod.substring(1);
					}
					else {
						getMethod = "get" + Character.toUpperCase(getMethod.charAt(0))
								+ getMethod.substring(1);
					}
					out.append(" " + iv.getName() + " == proto " + getMethod);
					if ( --size > 0 ) {
						out.append(" && ");
					}
				}
				out.append("\n            }\n");
				out.append("            case Dyn d {\n");
				out.append("                return false\n");

				out.append("            }\n");
				out.append("    }\n");
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


		// =============================== D ==============================
			/* Adicionar metodos
			    <= T -> Boolean,
			    > T -> Boolean e
			    >= T -> Boolean a T.
			Use os metodos < e == para isto.
			O metodo != ja eh herdado de Any e retorna o oposto de ==; */

			strSlotList += "    func <= " + prototypeName + " other -> Boolean;\n";
			out.append("    func <= " + prototypeName + " other -> Boolean {\n");
			out.append("        return ((self < other) || (self == other))\n");
			out.append("    }\n");

			strSlotList += "    func > " + prototypeName + " other -> Boolean;\n";
			out.append("    func > " + prototypeName + " other -> Boolean {\n");
			out.append("        return (!(self <= other))\n");
			out.append("    }\n");

			strSlotList += "    func >= " + prototypeName + " other -> Boolean;\n";
			out.append("    func >= " + prototypeName + " other -> Boolean {\n");
			out.append("        return ((self > other) || (self == other))\n");
			out.append("    }\n");
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


		// =============================== E ==============================
			/* Insira um metodo
				func allMethods -> Array<String>
				que retorna um vetor com o nome de todos os metodos declarados no prototipo no qual
				addComp esta acoplado; */

			strSlotList += "    func allMethods -> Array<String>;\n";
			out.append("    func allMethods -> Array<String> {\n");
			final List<WrMethodDec> methodList = prototype.getMethodDecList(compiler.getEnv());
			size = methodList.size();
			out.append("        return [ ");
			for (final WrMethodDec m : methodList) {
				out.append("\"" + m.getName() + "\"");
				if ( --size > 0 ) {
					out.append(", ");
				}
			}
			out.append("            ]\n");
			out.append("    }\n");
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


		return new Tuple2<StringBuffer, String>(out, strSlotList);
	}


	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler)  {
		final WrAnnotationAt annot = this.getAnnotation();


		final String methodName = "<1";
		List<WrMethodSignature> mList;
		WrMethodDec lessThanMethod = null;

		mList = compiler.getEnv().getCurrentPrototype()
				.searchMethodPrivateProtectedPublic(methodName, compiler.getEnv());
		lessThanMethod = mList.get(0).getMethod();
		lessThanMethod.accept(new WrASTVisitor() {
			// esta parte está obviamente errada
			@Override
			public void visit(WrMethodDec node, WrEnv env) {
				System.out.println(node.getName());
			}
		}, compiler.getEnv());

	}

}
