package ast;

import saci.Env;

/**
 * IReceiverCompileTimeMessageSend is implemented by all classes that represent elements that may receive compile-time
 * message sends such as prototype names and generic prototype instantiations. Examples:<br>
 * {@code var Function<Int, Boolean>.# writeCode f;} <br>
 * {@code let library.MyProto.# writeCode f;} <br>
   @author jose
 */
public interface IReceiverCompileTimeMessageSend {
	saci.TupleTwo<String, CompilationUnit> returnsNameWithPackage(Env env);
	String getName();
	String getPackageName();
	String getPrototypeName();
}
