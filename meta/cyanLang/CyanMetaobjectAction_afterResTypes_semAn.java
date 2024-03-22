
package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.InterpreterPrototype;

/**
 * <p>
 * This metaobject implements interfaces
 * </p>
 * 
 * <pre>
 * <code>	IAction_semAn, IActionNewPrototypes_semAn,
	IAction_afterResTypes, IActionNewPrototypes_afterResTypes,
	IParseWithCyanCompiler_parsing,
	ICommunicateInPrototype_afterResTypes_semAn
</code>
 * </pre>
 * <p>
 * Its annotations take an attached DSL code of a language called
 * <em>Myan</em>.<br>
 * In this DSL, there may be field declarations and methods. Each method should
 * correspond to a method of a Java interface implemented by this metaobject
 * class. And it should not have parameters. For example, there is a method
 * <code>afterResTypes_codeToAddCurrentPrototype</code> in the Java interface
 * <code>meta.IAction_afterResTypes</code>. However, this method takes two
 * arguments and in the Cyan code below it has none. To simplify the coding of
 * metaobjects, the formal parameters of the methods are implicitly declared. In
 * the code below we list which are the parameter names. Inside the methods, two
 * variables can be used: <code>metaobject</code>, which is the metaobject that
 * received the message (it would be <code>self</code>) and <code>env</code>,
 * the environment. For those interface methods that have a
 * <code>compiler</code> parameter, there is a variable <code>compiler</code>
 * available in the corresponding <em>Myan</em> method.
 * </p>
 * 
 * <pre>
 * <code>    // field declarations
    func  afterResTypes_codeToAddCurrentPrototype {
    	// two variables are accessible:
        // ICompiler_afterResTypes compiler
        // List&lt;Tuple2&lt;Annotation,
        //            List&lt;ISlotInterfacePrototype&gt;&gt;&gt; infoList
        // the return value has type Tuple2&lt;StringBuffer, String&gt;

        // Cyan code
    }

    func  semAn_codeToAdd {
        // variable  ICompiler_semAn compiler is accessible
        // return value has type StringBuffer
        // Cyan code
    }

    func runUntilFixedPoint {
        // either 'return true' or 'return false'
    }

    func afterResTypes_beforeMethodCodeList {
        // variable ICompiler_afterResTypes compiler is accessible
        // the return value has type List&lt;Tuple2&lt;String, StringBuffer&gt;&gt;
        // Cyan code
    }

    func afterResTypes_renameMethod {
        // variable ICompiler_afterResTypes compiler is accessible
        // the return value has type List&lt;Tuple2&lt;String, String []&gt;&gt;
        // Cyan code
    }
	func semAn_NewPrototypeList {
		// variable ICompiler_semAn compiler is accessible
		// return value has type
		// List&lt;Tuple2&lt;String, StringBuffer&gt;&gt;
	}
	func afterResTypes_NewPrototypeList {
		// variable ICompiler_afterResTypes compiler is accessible
		// return value has type
		// List&lt;Tuple2&lt;String, StringBuffer&gt;&gt;
	}
	func afterResTypes_semAn_shareInfoPrototype {
		// the return value is Object
	}
	func afterResTypes_semAn_receiveInfoPrototype {
		// variable annotationInfoSet has type
		// Set&lt;Tuple4&lt;String, Integer, Integer, Object&gt;&gt;
	}
</code>
 * </pre>
 * <p>
 * Each Cyan method can also return a value. For example,
 * <code>afterResTypes_codeToAddCurrentPrototype</code> should return a value of
 * type
 * </p>
 * 
 * <pre>
 * <code>    Tuple2&lt;StringBuffer, String&gt;
</code>
 * </pre>
 * <p>
 * If there is no <code>return</code> statement inside the code, the Cyan
 * interpreter considers that the Java <code>null</code> value was returned.
 * </p>
 * <p>
 * All classes of the Cyan compiler and java.lang are implicitly imported, as
 * <code>meta.Tuple2</code> (Cyan compiler) and <code>StringBuffer</code>
 * (java.lang). So there is no need of importing them. It is necessary to import
 * <code>List</code> from <code>java.util</code>. If necessary, Java packages
 * (not classes) may be imported using the <code>import</code> keyword:
 * </p>
 * 
 * <pre>
 * <code>    import ufscar.dcomp.lib
</code>
 * </pre>
 * <p>
 * <code>import</code> statements should come before any others <em>inside</em>
 * a method. A jar file with the package should be in the
 * <code>--meta\meta</code> directory of the current package (of the compilation
 * unit in which the annotation is used). Or in the Java path. Java classes with
 * the full path can be used if a jar file with them are in
 * <code>--meta\meta</code>.
 * </p>
 * <p>
 * The workings of this metaobject class is simple: it declares a method for
 * every inherited interface method. This method just interprets the method with
 * the same name of the <em>Myan</em> code. The result is that metaprogramming
 * is made in interpreted Cyan, <em>Myan</em>.
 * </p>
 * <p>
 * In <em>Myan</em>, the <code>self</code> object has two important methods:
 * <code>runFile:</code> and <code>call:</code>. The first takes at least a
 * string with the name of a file that should be in a directory
 * <code>--data</code> of a package, with extension <code>myan</code>.
 * </p>
 * 
 * <pre>
 * <code>    @action_afterResTypes_semAn{*
        func semAn_codeToAdd {
            runFile: #printProtoData_semAn;
        }
    *}
</code>
 * </pre>
 * <p>
 * There should be a file <code>printProtoData_semAn.myan</code> in the
 * <code>--data</code> directory of the current package. To use this file in
 * other package, it is necessary to precede it with the package name. Import
 * declarations of the Cyan source code are not taken into consideration here.
 * Parameters can be passed to the file:
 * </p>
 * 
 * <pre>
 * <code>	runFile: "checkProto", "test", 0;
</code>
 * </pre>
 * <p>
 * There should be a file <code>--data\checkProto(P,Q).myan</code> in the
 * current package directory. Parameters <code>P</code> and <code>Q</code> can
 * be anyone. But there should be a file with <strong>two</strong> parameters
 * since the <code>runFile:</code> method has <strong>three</strong> parameters.
 * <code>P</code>and <code>Q</code> are textually replaced by the parameters
 * <code>"test"</code> and <code>0</code>. But only when they are full words
 * inside the file (In the file, <code>Proto</code> is not changed to
 * ``"test"roto).
 * </p>
 * <p>
 * Method <code>call:</code> calls an action metaobject. An <em>action
 * metaobject</em> class should inherit from class <code>CyanMetaobject</code>
 * and implement interface <code>IActionFunction</code>. A method
 * </p>
 * 
 * <pre>
 * <code>    Object eval(Object arg)
</code>
 * </pre>
 * <p>
 * should be defined. Inside <em>Myan</em> code, <code>call:</code> will call
 * <code>eval</code> passing as a parameter an object of
 * </p>
 * 
 * <pre>
 * <code>    Tuple6&lt;IAbstractCyanCompiler,
    CyanMetaobjectAtAnnot, List&lt;Object&gt;, WrSymbol,
    WrMethodDec, WrEnv&gt;
</code>
 * </pre>
 * <p>
 * The first tuple element is the <code>compiler</code>object passed as
 * parameter to several interface methods. It may be <code>null</code> because
 * some interfaces do not take a <code>compiler</code> parameter. The second
 * tuple element is the metaobject, the original one. The third is the list of
 * parameters. Using Cyan syntax, it would be
 * </p>
 * 
 * <pre>
 * <code>    [ "run", "at: Int put: String", 0 ]
</code>
 * </pre>
 * <p>
 * in<br>
 * call: “checkNumberCalls”, “at: Int put: String”, 0
 * </p>
 * <p>
 * The four element, of type <code>WrSymbol</code>, is a symbol that may be used
 * inside the <em>action metaobject</em> to issue errors. The fifth element is
 * the current method and the last one is the current environment. By the way,
 * the action metaobject <code>checkNumberCalls</code> does not exist. But
 * <code>shouldCallSuperMethod</code> does. It does not take parameters and
 * checks whether the method, the fifth tuple element, extends the
 * superprototype method. Its first statatement should be a call to the
 * superprototype method. Otherwise an error is issued.
 * </p>
 * <p>
 * <em>Action metaobjects</em> are used to tasks that Myan code cannot do. Like
 * visit AST nodes. It is necessary a Java object for that. Precisely, a Java
 * object of class <code>WrASTVisitor</code> is expected as parameter to method
 * <code>accept</code> of every AST class. Then the AST cannot be visited using
 * Cyan code. This job must be done using <em>action metaobjects</em>. A good
 * project is to create a <em>action metaobject</em> that calls Cyan code for
 * each AST class. The <em>action metaobject</em> would be called as
 * </p>
 * 
 * <pre>
 * <code>    @action_afterResTypes_semAn{*
          visit: """
                visit: WrMethodDec node, WrEnv env {
                  // visit a method
                }
                visit: WrStatementAssignmentList node,
                       WrEnv env {
                    // visit an assignment
                }
           """;
    *}
</code>
 * </pre>
 * <p>
 * The Cyan code would be able to visit methods and statements of a protototype,
 * for example.
 * </p>
 * 
 * @author jose
 */
public class CyanMetaobjectAction_afterResTypes_semAn extends CyanMetaobjectAtAnnot
		implements IInterpreterMethods_afterResTypes {

	public CyanMetaobjectAction_afterResTypes_semAn() {
		super("action_afterResTypes_semAn",
				AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.PROGRAM_DEC,
						AttachedDeclarationKind.PACKAGE_DEC,
						AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.METHOD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC,
						AttachedDeclarationKind.FIELD_DEC });
	}

	@Override
	public boolean shouldTakeText() {
		return true;
	}

	InterpreterPrototype interpreterPrototype = null;

	@Override
	public InterpreterPrototype getInterpreterPrototype() {
		return interpreterPrototype;
	}

	@Override
	public void setInterpreterPrototype(
			InterpreterPrototype interpreterPrototype) {
		this.interpreterPrototype = interpreterPrototype;
	}

	@Override
	public CyanMetaobjectAtAnnot getCyanMetaobject() {
		return this;
	}

	@Override
	public String[] methodToInterpertList() {
		return new String[] { "afterResTypes_codeToAdd", "semAn_codeToAdd",
				"semAn_NewPrototypeList", "afterResTypes_NewPrototypeList",
				"runUntilFixedPoint", "afterResTypes_beforeMethodCodeList",
				"afterResTypes_renameMethod",
				"afterResTypes_semAn_afterSemAn_shareInfoPrototype",
				"afterResTypes_semAn_afterSemAn_receiveInfoPrototype" };
	}
}
