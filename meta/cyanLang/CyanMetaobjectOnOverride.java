package meta.cyanLang;

import java.util.List;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckOverride_afterSemAn;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IParseWithCyanCompiler_parsing;
import meta.InterpretationErrorException;
import meta.MetaHelper;
import meta.Token;
import meta.WrMethodDec;
import meta.WrStatement;


/**
<p>Annotations of this metaobject should be attached to methods or method signatures. There should be an attached DSL code that is run each time the method/method signature is overridden. The <code>self</code> object has methods for calling Cyan code in the <code>--data</code> directory of packages and function metaobjects, metaobjects whose classes inherit from interface <code>IActionFunction</code>. Message <code>call:</code> to <code>self</code> should be followed by the name of a function metaobject as a string:</p>
<pre><code>    call: #shouldCallSuperMethod;
    call: &quot;shouldCallSuperMethod&quot;;
</code></pre>
<p>Function metaobject <code>shouldCallSuperMethod</code> is in package <code>cyan.lang</code> which is always imported by any package. To use a function metaobject, it is necessary to import the package in which it is defined:</p>
<pre><code>    import myPack
    object Test
        {@literal @}onOverride{*
            call: &quot;checkOverriddenMethod&quot;, #aaa, &quot;bbb&quot;, &quot;ccc&quot;;
        {@literal *}}
    end
</code></pre>
<p>All parameters to a function metaobject should be strings.</p>
<p>Cyan code in the <code>--data</code> directory of a package can be run by method <code>callmo:</code> that takes the full path of the file as parameter, without the extension <code>mo</code>, and the <code>String</code> parameters:</p>
<pre><code>    object Test
        {@literal @}onOverride{{@literal *}
            callmo: &quot;cyan.lang.overriding&quot;, &quot;AAAA&quot;, &quot;BBB&quot;;
        {@literal *}}
    end
</code></pre>
<p>There should be a file <code>overriding(T,U).mo</code> in directory <code>--data</code> of package <code>cyan.lang</code>. The <code>T</code> and <code>U</code> are parameters that are textually replaced by the real parameters to the call, <code>&quot;AAAA&quot;</code> and <code>&quot;BBB&quot;</code>. The parameter names of the file can be different, of course, but the number of parameters in the file name should be equal to the number of arguments following the file name in the message send</p>
<pre><code>    callmo: &quot;cyan.lang.overriding&quot;, &quot;AAAA&quot;, &quot;BBB&quot;
</code></pre>
<p>Inside the attached DSL code three variables can be used:</p>
<ul>
<li><code>compiler</code>, whose type is <code>ICompiler_semAn</code>, the first parameter to method <code>afterSemAn_checkOverride</code>;</li>
<li><code>overridedMethod</code>, whose type is <code>WrMethodDec</code>, the second parameter to <code>afterSemAn_checkOverride</code>;</li>
<li>env, whose type is <code>WrEnv</code>, the same as <code>compiler getEnv</code>.</li>
</ul>
 */
public class CyanMetaobjectOnOverride extends CyanMetaobjectAtAnnot
implements ICheckOverride_afterSemAn, IParseWithCyanCompiler_parsing {

	public CyanMetaobjectOnOverride() {
		super("onOverride", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC },
				Token.PUBLIC);
	}


	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public void afterSemAn_checkOverride(ICompiler_semAn compiler,
			WrMethodDec method) {
		MetaHelper.interpreterFor_MOPInterfaceMethod(
				statList,
				compiler,
				this,
				"afterSemAn_checkOverride",
				new String [] { "compiler", "method" },
				new Object [] { compiler, method } ,
				null);
	}

//		if ( statList == null || statList.size() == 0 ) { return ; }
//		WrStatement fs = statList.get(0);
//
//		final CyanMetaobjectAtAnnot thisMetaobject = this;
//		try {
//			WrEvalEnv ee = MetaHelper.getNewWrEvalEnv(compiler_semAn.getEnv(), null,
//					fs.getFirstSymbol(), new String[] { "compiler", "overridedMethod", "metaobject" },
//					new Object[] { compiler_semAn, overridedMethod, this } );
//			ee.addVariable("env", compiler_semAn.getEnv());
//			ee.setCurrentMethod(overridedMethod);
//			Object selfObject = MetaHelper.createSelfObject(compiler_semAn, thisMetaobject, ee);
//			ee.setSelfObject(selfObject);
//
//			Object obj = MetaHelper.evalCode(statList, ee);
//			if ( obj != null ) {
//				this.addError("This code should not return a value");
//			}
//			ee.setCurrentMethod(null);
//		}
//		catch (InterpretationErrorCheckedException e) {
//		}




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


	List<WrStatement> statList;


}
