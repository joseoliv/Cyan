package meta.cyanLang;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_semAn;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IParseWithCyanCompiler_parsing;
import meta.InterpretationErrorException;
import meta.MetaHelper;
import meta.WrAnnotationAt;
import meta.WrStatement;

/**
 * <p>This metaobject produces a code that is the value returned by the Cyan statements. The package and the prototype of the value returned should be given as the first and second parameter. Usage example:</p>
<pre><code> let Int count1_10 =
    @eval("cyan.lang", "Int"){!
        var Int count = 0;
        for n in 1..10 {
            count = count + n
        }
        return count;
    !};
 assert count1_10 == 55;
</code></pre>
<blockquote>
<p>Written with <a href="https://stackedit.io/">StackEdit</a>.</p>
</blockquote>


   @author jose
 */
public class CyanMetaobjectEval extends CyanMetaobjectAtAnnot
		implements IParseWithCyanCompiler_parsing, IAction_semAn {

	public CyanMetaobjectEval() {
		super("eval", AnnotationArgumentsKind.TwoParameters);
	}


	@Override
	public void check() {
		WrAnnotationAt annot = this.getAnnotation();
		if ( ! (annot.getJavaParameterList().get(0) instanceof String) ||
				! (annot.getJavaParameterList().get(1) instanceof String) ) {
			this.addError("The parameters to this annotation should be strings");
		}
		else {
			this.packageOfType = CyanMetaobject.removeQuotes((String ) annot.getJavaParameterList().get(0));
			this.prototypeOfType = CyanMetaobject.removeQuotes((String ) annot.getJavaParameterList().get(1));
		}

	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler) {

		Object obj = MetaHelper.interpreterFor_MOPInterfaceMethod(
				statList,
				compiler,
				this,
				"semAn_codeToAdd",
				new String [] { "compiler" },
				new Object [] { compiler } ,
				Object.class);
		String ret = null;
		try {
			Method m = obj.getClass().getMethod("_asString");
			m.setAccessible(true);
			Object cyStr = m.invoke(obj);
			final Field f = cyStr.getClass().getField("s");
			ret = (String ) f.get(cyStr);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException |
				IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
			this.addError("Probably this metaobject did not return an object of the correct type: "
					+ this.packageOfType + "." + this.prototypeOfType);
		}
		return new StringBuffer( ret ) ;

	}


	@Override
	public String getPackageOfType() { return this.packageOfType; }

	@Override
	public String getPrototypeOfType() { return this.prototypeOfType; }

	@Override
	public boolean isExpression() { return true; }



	@Override
	public boolean shouldTakeText() { return true; }



	@Override
	public void parsing_parse(ICompiler_parsing cp) {
		try {
			cp.next();
			statList = MetaHelper.parseCyanStatementList(cp);
		}
		catch (InterpretationErrorException e) {
			addError(e.getMessage());
		}
		catch (CompileErrorException e) {
			// addError(e.getMessage());
		}

	}




	private String packageOfType;
	private String prototypeOfType;




	List<WrStatement> statList;

}

