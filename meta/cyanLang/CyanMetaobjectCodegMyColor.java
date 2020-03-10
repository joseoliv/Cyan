package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnotCodeg;
import meta.DirectoryKindPPP;
import meta.IAction_dsa;
import meta.ICodeg;
import meta.ICompiler_ded;
import meta.ICompiler_dsa;
import meta.Tuple4;
import meta.WrAnnotationAt;

public class CyanMetaobjectCodegMyColor extends CyanMetaobjectAtAnnotCodeg
             implements ICodeg, IAction_dsa {

	public CyanMetaobjectCodegMyColor() {
		super("myColor", AnnotationArgumentsKind.OneParameter);
	}



	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		compiler_dsa.writeTextFile( new char[] { 'a', 'b', 'c' },  "videoComments.txt", "Program", "main", DirectoryKindPPP.DATA);
		return new StringBuffer(this.getMetaobjectAnnotation().getCodegInfo().toString());
	}

	@Override
	public String getPackageOfType() { return "cyan.lang"; }

	@Override
	public String getPrototypeOfType() { return "Int"; }


	@Override
	public byte []getUserInput(ICompiler_ded compiler_ded, byte []previousUserInput) {
		/*
		 * inside a Codeg one can access the current prototype by calling
		 *    this.getMetaobjectAnnotation().getProgramUnit()
		 *
		 * the list of local variables visible at the point of declaration is given by
		 *      this.getMetaobjectAnnotation().getLocalVariableNameList()
		 */

		return null;
	}

	@Override
	public List<Tuple4<Integer, Integer, Integer, Integer>>  getColorList() {
		final String strColor = this.getMetaobjectAnnotation().getCodegInfo().toString();
		/*
		 * convert strColor to int color, put it in colorNumber
		 *
		 */
		int colorNumber = 0;
		try {
			colorNumber = Integer.parseInt(strColor);
		}
		catch ( final NumberFormatException e ) {
			return null;
		}
		final int column = metaobjectAnnotation.getFirstSymbol().getColumnNumber();
		final int columnLeftPar = column + 1 + this.getName().length() + 1;
		final WrAnnotationAt annotation = this.getMetaobjectAnnotation();
		final String red = (String ) annotation.getJavaParameterList().get(0);

		final List<Tuple4<Integer, Integer, Integer, Integer>> array = new ArrayList<>();
		final Tuple4<Integer, Integer, Integer, Integer> t = new Tuple4<>(colorNumber, 1, columnLeftPar, red.length());
		array.add(t);

		return array;


	}

	@Override
	public boolean isExpression() {
		return true;
	}

}
