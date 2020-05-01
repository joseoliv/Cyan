package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_parsing;
import meta.IDeclaration;
import meta.IParseWithCyanCompiler_parsing;
import meta.ISlotSignature;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprLiteralTuple;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrPrototype;

public class CyanMetaobjectCodegFSM_DSL_Methods extends CyanMetaobjectAtAnnot
    implements IAction_afterResTypes,  IParseWithCyanCompiler_parsing
    {


	public CyanMetaobjectCodegFSM_DSL_Methods() {
		super("fsmDSLMethods", AnnotationArgumentsKind.ZeroOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC } );

		fsmFieldName = "fsm_" + MetaHelper.nextIdentifier();
		currentStateFieldName = "currentState_" + MetaHelper.nextIdentifier();
		fsmMakeTransitionMethodName = "makeTransition_" + MetaHelper.nextIdentifier() + ":";

		    // André, remova todas as instruções até o final deste construtor
			// inicialize o objeto fsmData em getUserInput
		fsmData = new FSMData();
		/*
		 *   q0, "q0q0:1" -> q0
  q0, "q0q1:2" -> q1
  q1, "q1:2 q1:1" -> q1
  q1, "q1q2" -> q2
  q2, "q2:0 q0:3" -> q0

		 */
		fsmData.initialState = "q0";
		fsmData.finalStateList = new ArrayList<String>();
		fsmData.finalStateList.add("q2");
		fsmData.transList = new ArrayList<>();
		fsmData.stateList = new ArrayList<>();

		String []statList = new String[] { "q0", "q1", "q2" };
		for ( String stat : statList ) {
			fsmData.stateList.add(new Tuple3<String, Integer, Integer>(stat, 0, 0));
		}
		String []transitions = {
				"q0", "q0", "q0q0:1",
				"q0", "q1", "q0q1:2",
				"q1", "q1", "q1:2 q1:1",
				"q1", "q2", "q1q2",
				"q2", "q0", "q2:0 q0:3"
		};
		for (int i = 0; i < transitions.length; i+=3) {
			Tuple3<String, String, String> t = new Tuple3<String, String, String>(
					transitions[i], transitions[i+1], transitions[i+2]
					);
			fsmData.transList.add(t);
		}  // transition to non-existing method; only methods that exist are changed by this mo; resetInFinal; DSL code
	}


	@Override
	public void check() {
		List<Object> paramList = this.getAnnotation().getJavaParameterList();
		if ( paramList != null && paramList.size() > 0 ) {
			if ( !( paramList.get(0) instanceof String ) ) {
				this.addError("Annotation '" + this.getName() + "' should take an identifer as parameter");
				return ;
			}
			this.resetMethodName = removeQuotes(((String )paramList.get(0)));
		}
	}

	private boolean checkStateExist(String state) {
		boolean foundState = false;
		for ( Tuple3<String, Integer, Integer> t : fsmData.stateList )  {
			if ( t.f1.equals(state) ) {
				foundState = true;
			}
		}
		return foundState;

	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {

		String slotStr = "let Tuple<initialState, String, finalStates, Array<String>, states, Array<String>, transitions, "
		         + "Array<Tuple<String, String, String>>> " + fsmFieldName + ";\n";
		StringBuffer s = new StringBuffer();
		s.append("    let Tuple<initialState, String, finalStates, Array<String>,\n" +
				 "   states, Array<String>, transitions,\n" +
				 "   Array<Tuple<String, String, String>>> " + fsmFieldName + " = [. \n" +
				 "initialState = \"" + fsmData.initialState + "\", finalStates = [ "
				 );

		if ( !this.checkStateExist(fsmData.initialState) ) {
			this.addError("State '" + fsmData.initialState + "' used as an initial state in annotation '" +
			        this.getName() + "' is not in the list of states of the FSM");
		}


		int size = fsmData.finalStateList.size();
		if ( size > 0 ) {
			for ( String fs : fsmData.finalStateList ) {
				s.append("\"" + fs + "\"");
				if ( --size > 0 ) { s.append(", "); }
				if ( !this.checkStateExist(fs) ) {
					this.addError("State '" + fs + "' used as a final state in annotation '" +
					        this.getName() + "' is not in the list of states of the FSM");
				}
			}
		}
		else {
			s.append("\"\"");
		}
		s.append(" ], \n        states = [ ");
		size = fsmData.stateList.size();
		for ( Tuple3<String, Integer, Integer> t : fsmData.stateList ) {
			s.append("\"" + t.f1 + "\"");
			if ( --size > 0 ) {
				s.append(", ");
			}
		}
		s.append(" ],\n        transitions = [");

		WrPrototype proto = (WrPrototype ) this.getAttachedDeclaration();
		WrEnv env = compiler.getEnv();

		size = fsmData.transList.size();
		for ( Tuple3<String, String, String> t : fsmData.transList ) {
			s.append("            [. \"" + t.f1 + "\", " + "\"" + t.f2 + "\", "  + "\"" + t.f3 + "\" .]" );
			if ( --size > 0 ) {
				s.append(", ");
			}
			s.append("\n");
			if ( !this.checkStateExist(t.f1) ) {
				this.addError("State '" + t.f1 + "' used in a transition in annotation '" +
				        this.getName() + "' is not in the list of states of the FSM");
			}
			if ( !this.checkStateExist(t.f2) ) {
				this.addError("State '" + t.f2 + "' used in a transition in annotation '" +
				        this.getName() + "' is not in the list of states of the FSM");
			}
			/*
			 * check if the method does exist
			 */
			List<WrMethodSignature> msList = proto.searchMethodPublicPackageSuperPublicPackage(t.f3, env);
			if (  msList == null || msList.size() == 0) {
				this.addError("Annotation '" + this.getName() + "' uses the method '" + t.f3 + "' which does not exist");
			}

		}

		s.append("        ] .];\n");

		slotStr += "var String " + currentStateFieldName + ";\n";
		slotStr += "func " + fsmMakeTransitionMethodName + " String origin, String label;\n";
		slotStr += "func " + this.resetMethodName + ";\n";

		s.append("    var String " + currentStateFieldName + " = \"" + fsmData.initialState + "\";\n");
		s.append("\n");
		s.append("    func " + fsmMakeTransitionMethodName + " String origin, String label {\n");
		s.append("       var Array<Tuple<String, String, String>> arrayT = " + fsmFieldName + " transitions;\n" +
				"        for aTrans in arrayT {\n" +
				"            if  aTrans f1 == origin &&\n" +
				"                aTrans f3 == label {\n" +
				"                " + currentStateFieldName + " = aTrans f2;\n" +
				"                return Nil\n" +
				"            }\n" +
				"        }\n");
		s.append(
				"        throw: ExceptionStr(\"Error in method call: the Finite State \" ++ \n" +
				"           \"Machine of " + this.getName() + " does not allow transition from '\" ++ origin ++\n" +
				"           \"' using method  '\" ++ label ++ \"'\");\n" +
				"");
		s.append("    }\n\n");
		s.append("    func " + this.resetMethodName + " { \n");
		s.append("        " + currentStateFieldName + " = \"" + fsmData.initialState + "\";\n");

		s.append("    }\n\n");
//		List<StringBuffer> array = new ArrayList<>();
//		array.add(s);
		return new Tuple2<StringBuffer, String>(s, slotStr);

	}


//	@Override
//	public List<StringBuffer> afterResTypes_codeToAdd(
//			ICompiler_afterResTypes compiler) {
//		StringBuffer s = new StringBuffer();
//		s.append("    let Tuple<initialState, String, finalStates, Array<String>,\n" +
//				 "   states, Array<String>, transitions,\n" +
//				 "   Array<Tuple<String, String, String>>> " + fsmFieldName + " = [. \n" +
//				 "initialState = \"" + fsmData.initialState + "\", finalStates = [ "
//				 );
//
//		if ( !this.checkStateExist(fsmData.initialState) ) {
//			this.addError("State '" + fsmData.initialState + "' used as an initial state in annotation '" +
//			        this.getName() + "' is not in the list of states of the FSM");
//		}
//
//
//		int size = fsmData.finalStateList.size();
//		if ( size > 0 ) {
//			for ( String fs : fsmData.finalStateList ) {
//				s.append("\"" + fs + "\"");
//				if ( --size > 0 ) { s.append(", "); }
//				if ( !this.checkStateExist(fs) ) {
//					this.addError("State '" + fs + "' used as a final state in annotation '" +
//					        this.getName() + "' is not in the list of states of the FSM");
//				}
//			}
//		}
//		else {
//			s.append("\"\"");
//		}
//		s.append(" ], \n        states = [ ");
//		size = fsmData.stateList.size();
//		for ( Tuple3<String, Integer, Integer> t : fsmData.stateList ) {
//			s.append("\"" + t.f1 + "\"");
//			if ( --size > 0 ) {
//				s.append(", ");
//			}
//		}
//		s.append(" ],\n        transitions = [");
//
//		WrPrototype proto = (WrPrototype ) this.getAttachedDeclaration();
//		WrEnv env = compiler.getEnv();
//
//		size = fsmData.transList.size();
//		for ( Tuple3<String, String, String> t : fsmData.transList ) {
//			s.append("            [. \"" + t.f1 + "\", " + "\"" + t.f2 + "\", "  + "\"" + t.f3 + "\" .]" );
//			if ( --size > 0 ) {
//				s.append(", ");
//			}
//			s.append("\n");
//			if ( !this.checkStateExist(t.f1) ) {
//				this.addError("State '" + t.f1 + "' used in a transition in annotation '" +
//				        this.getName() + "' is not in the list of states of the FSM");
//			}
//			if ( !this.checkStateExist(t.f2) ) {
//				this.addError("State '" + t.f2 + "' used in a transition in annotation '" +
//				        this.getName() + "' is not in the list of states of the FSM");
//			}
//			/*
//			 * check if the method does exist
//			 */
//			List<WrMethodSignature> msList = proto.searchMethodPublicPackageSuperPublicPackage(t.f3, env);
//			if (  msList == null || msList.size() == 0) {
//				this.addError("Annotation '" + this.getName() + "' uses the method '" + t.f3 + "' which does not exist");
//			}
//
//		}
//
//		s.append("        ] .];\n");
//		s.append("    var String " + currentStateFieldName + " = \"" + fsmData.initialState + "\";\n");
//		s.append("\n");
//		s.append("    func " + fsmMakeTransitionMethodName + " String origin, String label {\n");
//		s.append("       var Array<Tuple<String, String, String>> arrayT = " + fsmFieldName + " transitions;\n" +
//				"        for aTrans in arrayT {\n" +
//				"            if  aTrans f1 == origin &&\n" +
//				"                aTrans f3 == label {\n" +
//				"                " + currentStateFieldName + " = aTrans f2;\n" +
//				"                return Nil\n" +
//				"            }\n" +
//				"        }\n");
//		s.append(
//				"        throw: ExceptionStr(\"Error in method call: the Finite State \" ++ \n" +
//				"           \"Machine of " + this.getName() + " does not allow transition from '\" ++ origin ++\n" +
//				"           \"' using method  '\" ++ label ++ \"'\");\n" +
//				"");
//		s.append("    }\n\n");
//		s.append("    func " + this.resetMethodName + " { \n");
//		s.append("        " + currentStateFieldName + " = \"" + fsmData.initialState + "\";\n");
//
//		s.append("    }\n\n");
//		List<StringBuffer> array = new ArrayList<>();
//		array.add(s);
//		return array;
//	}
//

	@Override
	public List<Tuple3<String, StringBuffer, Boolean>> afterResTypes_beforeMethodCodeList(
			ICompiler_afterResTypes compiler) {
		List<Tuple3<String, StringBuffer, Boolean>> array = new ArrayList<>();
		IDeclaration dec = this.getAttachedDeclaration();
		if ( !(dec instanceof WrPrototype) || ((WrPrototype ) dec).isInterface()) {
			this.addError("Annotation '" + this.getName() + "' can be attached only to prototypes");
			return null;
		}
		WrPrototype proto = (WrPrototype ) dec;

		for ( WrMethodDec m : proto.getMethodDecList(compiler.getEnv()) ) {
			if ( m.getVisibility() == Token.PUBLIC ) {
				StringBuffer sb = new StringBuffer();

				String methodName = m.getMethodSignature().getName();
				boolean foundMethod = false;
				for ( Tuple3<String, String, String> t : fsmData.transList ) {
					if ( t.f3.equals(methodName) ) {
						foundMethod = true;
						break;
					}
				}
				if ( foundMethod ) {
					sb.append("    " + fsmMakeTransitionMethodName  + " " + currentStateFieldName + ", " +
				        "\"" + methodName + "\";");
					Tuple3<String, StringBuffer, Boolean> t = new Tuple3<>(
							methodName,
							sb,
							true);
					array.add(t);

				}


			}

		}


		return array;
	}

	private String fsmFieldName;
	private String currentStateFieldName;
	private String fsmMakeTransitionMethodName;
	private FSMData fsmData;
	private String resetMethodName = "resetFSM_Methods";



	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public void parsing_parse(ICompiler_parsing cp) {
//		 * Tuple<initialState, String, finalStates, Array<String>,\n" +
//				 "   states, Array<String>, transitions,\n" +
//				 "   Array<Tuple<String, String, String>>>
//
//   [. initialState = "q0",
//      finalStates = [ "q1", "q2" ],
//      states = [ "q0", "q1", "q2" ],
//      transitions = [ [. "q0", "q1", "0" .],
//                      [. "q0", "q0", "1" .] ]
//      .]
		cp.next();
		WrExpr e = cp.expr();
		if ( e instanceof WrExprLiteralTuple ) {
			WrExprLiteralTuple t = (WrExprLiteralTuple ) e;
			if ( ! t.getIsNamedTuple() ) {
				tupleError();
				return ;
			}
			List<WrExpr> array = t.getExprList();
			if ( array == null || array.size() != 8 ) {
				tupleError();
				return ;
			}
			if ( ! array.get(0).asString().equals("initialState") || !array.get(2).asString().equals("finalStates")
					|| !array.get(4).asString().equals("states") || !array.get(6).asString().equals("transitions")) {
				tupleError();
				return ;
			}
		}
		else {
			tupleError();
			return ;
		}
	}

	private void tupleError() {
		this.addError("The DSL code attached to the annotation '" + this.getName() +
				"' should be a tagged tuple of the form " +
				"   [. initialState = \"q0\",\r\n" +
				"      finalStates = [ \"q1\", \"q2\" ],\r\n" +
				"      states = [ \"q0\", \"q1\", \"q2\" ],\r\n" +
				"      transitions = [ [. \"q0\", \"q1\", \"0\" .],\r\n" +
				"                      [. \"q0\", \"q0\", \"1\" .] ]\r\n" +
				"      .]    \r\n" +
				"");

	}


	/*
	 * 	public String file;
	public String initialState;
	public List<String> finalStateList;
	public List<Tuple3<String, Integer, Integer>> stateList;

	 */
}