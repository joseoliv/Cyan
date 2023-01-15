package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnotCodeg;
import meta.DirectoryKindPPP;
import meta.IAction_semAn;
import meta.ICodeg;
import meta.ICompiler_ded;
import meta.ICompiler_semAn;
import meta.Tuple4;
import meta.WrAnnotationAt;

public class CyanMetaobjectCodegMyColor extends CyanMetaobjectAtAnnotCodeg
             implements ICodeg, IAction_semAn {

	public CyanMetaobjectCodegMyColor() {
		super("myColor", AnnotationArgumentsKind.OneParameter);
	}



	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		compiler_semAn.writeTextFile( new char[] { 'a', 'b', 'c' },  "videoComments.txt", "ChooseFoldersCyanInstallation", "main", DirectoryKindPPP.DATA);
		return new StringBuffer(this.getAnnotation().getCodegInfo().toString());
	}

	@Override
	public String getPackageOfType() { return "cyan.lang"; }

	@Override
	public String getPrototypeOfType() { return "Int"; }


	@Override
	public byte []getUserInput(ICompiler_ded compiler_ded, byte []previousUserInput) {
		/*
		 * inside a Codeg one can access the current prototype by calling
		 *    this.getAnnotation().getPrototype()
		 *
		 * the list of local variables visible at the point of declaration is given by
		 *      this.getAnnotation().getLocalVariableNameList()
		 */

		return null;
	}

	@Override
	public List<Tuple4<Integer, Integer, Integer, Integer>>  getColorList() {
		final String strColor = this.getAnnotation().getCodegInfo().toString();
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
		final int column = annotation.getFirstSymbol().getColumnNumber();
		final int columnLeftPar = column + 1 + this.getName().length() + 1;
		final WrAnnotationAt annotation = this.getAnnotation();
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
