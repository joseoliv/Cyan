package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnotCodeg;
import meta.IAction_semAn;
import meta.ICodeg;
import meta.ICompiler_ded;
import meta.ICompiler_semAn;
import meta.Tuple2;
import meta.Tuple4;
import meta.WrAnnotationAt;

public class CyanMetaobjectCodegColorColor extends CyanMetaobjectAtAnnotCodeg
             implements ICodeg, IAction_semAn {

	public CyanMetaobjectCodegColorColor() {
		super("colorColor", AnnotationArgumentsKind.OneOrMoreParameters);
	}


	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		System.out.println( this.getAnnotation().getJavaParameterList().get(1));
		System.out.println( this.getAnnotation().getJavaParameterList().get(2));
		final byte []info = this.getAnnotation().getCodegInfo();
		if ( info == null ) {
			return new StringBuffer("12");
		}
		else {
			final StringBuffer s = new StringBuffer();
			for ( final byte b : info ) {
				s.append( (char ) b);
			}
			return s;
		}
		/*StringBuffer s = new StringBuffer(info.toString());
		return s; */
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
		System.out.println("Local variable list: ");
		for ( final Tuple2<String, String> t : compiler_ded.getLocalVariableList() ) {
			System.out.println( "name = " + t.f1 + " type = " + (t.f2 == null ? "implicit" : t.f2) );
		}
		System.out.println("field list: ");
		for ( final Tuple2<String, String> t : compiler_ded.getFieldList() ) {
			System.out.println( "name = " + t.f1 + " type = " + (t.f2 == null ? "implicit" : t.f2) );
		}

		return "32".getBytes();
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
