package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import ast.MessageKeywordGrammarList;
import ast.MessageKeywordGrammarOrList;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFunction;
import meta.IActionMethodMissing_semAn;
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
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrMessageWithKeywords;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrMethodSignatureGrammar;
import meta.WrMethodSignatureWithKeywords;
import meta.WrSymbol;
import meta.WrType;

/**
 * This metaobjects implement grammar methods. See the Cyan manual.
 *
   @author jose
 */
public class CyanMetaobjectGrammarMethod extends CyanMetaobjectAtAnnot
       implements IParseWithCyanCompiler_parsing, // ICheckDeclaration_afterResTypes2,
       IActionMethodMissing_semAn, IAction_afterResTypes {


	public CyanMetaobjectGrammarMethod() {
			super("grammarMethod", AnnotationArgumentsKind.ZeroOrMoreParameters,
					new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC },
					Token.PUBLIC);
	}

	@Override
	public boolean shouldTakeText() { return true; }


	public WrMethodSignatureGrammar methodSignatureGrammarForMetaobject(ICompiler_parsing cp) {


		final WrSymbol first = cp.getSymbol();
		final MessageKeywordGrammar sg = keywordGrammar(cp);

		if ( sg.matchesEmptyInput() ) {
			cp.error(first, "This regular expression matches an empty input, which is illegal");
		}
		return new WrMethodSignatureGrammar(sg);
	}

	private MessageKeywordGrammar keywordGrammar(ICompiler_parsing cp) {

		final WrSymbol firstSymbol = cp.getSymbol();

		MessageKeywordGrammar akeywordGrammar;

		if ( cp.getSymbol().token != Token.LEFTPAR ) {
			cp.error(cp.getSymbol(), "'(' expected");
		}
		else {
			cp.next();
		}
		IMessageKeyword keyword = keywordUnit(cp);
		final List<IMessageKeyword>  keywordArray = new ArrayList<>();
		keywordArray.add(keyword);
		if ( cp.getSymbol().token == Token.BITOR ) {
			while ( cp.getSymbol().token == Token.BITOR ) {
				cp.next();
				keyword = keywordUnit(cp);
				keywordArray.add(keyword);
			}
			akeywordGrammar = new MessageKeywordGrammarOrList(keywordArray, firstSymbol);
		}
		else {
			while ( cp.getSymbol().token == Token.IDENTCOLON ||
					cp.getSymbol().token == Token.LEFTPAR ) {
				keyword = keywordUnit(cp);
				keywordArray.add(keyword);
			}
			akeywordGrammar = new MessageKeywordGrammarList(keywordArray, firstSymbol);
		}
		if ( cp.getSymbol().token != Token.RIGHTPAR ) {
			cp.error(cp.getSymbol(), "')' expected" );
		}
		else {
			cp.next();
		}
		switch ( cp.getSymbol().token ) {
		case PLUS:
			akeywordGrammar.setRegularOperator(cp.getSymbol());
			cp.next();
			break;
		case MULT:
			akeywordGrammar.setRegularOperator(cp.getSymbol());
			cp.next();
			break;
		case QUESTION_MARK:
			akeywordGrammar.setRegularOperator(cp.getSymbol());
			cp.next();
			break;
		default:
			break;
		}

		return akeywordGrammar;
	}


	private IMessageKeyword keywordUnit(ICompiler_parsing cp) {
		if ( cp.getSymbol().token == Token.LEFTPAR )
			return keywordGrammar(cp);
		else {
			// here comes the analysis of the rule SelecGrammarElem
			if ( cp.getSymbol().token != Token.IDENTCOLON ) {
				cp.error(cp.getSymbol(), "Id:  expected" );
			}
			final String namekeyword = cp.getSymbol().getSymbolString();
			if ( namekeyword.compareTo("init:") == 0 || namekeyword.compareTo("new:") == 0 ) {
				cp.error(cp.getSymbol(), "'init:' and 'new:' keywords cannot appear in a grammar method"
						);
			}
			final WrSymbol symbolIdent = cp.getSymbol();
			cp.next();
			if ( startType(cp.getSymbol().token) ) {
				// IdColon TypeOneManyList
				final List<WrExpr> typeList = new ArrayList<WrExpr>();
				WrExpr t;

				try {
					cp.setProhibitTypeof(true);
					t = cp.type();
				}
				finally {
					cp.setProhibitTypeof(false);
				}


				typeList.add(t);
				while ( cp.getSymbol().token == Token.COMMA ) {
					cp.next();

					try {
						cp.setProhibitTypeof(true);
						t = cp.type();
					}
					finally {
						cp.setProhibitTypeof(false);
					}

					typeList.add(t);
				}
				// it is easy to debug by first putting the object in a
				// variable such as keywordWithTypes and only after
				// that returning it
				final MessageKeywordWithTypes keywordWithTypes =
					new MessageKeywordWithTypes(symbolIdent, typeList);
				return keywordWithTypes;
			}
			else if ( cp.getSymbol().token == Token.LEFTPAR ) {
				if ( startType(cp.next(0).token) ) {
				cp.next();
				WrExpr t;

				try {
					cp.setProhibitTypeof(true);
					t = cp.type();
				}
				finally {
					cp.setProhibitTypeof(false);
				}


				final MessageKeywordWithMany keywordWithMany =
					new MessageKeywordWithMany(symbolIdent, t);
				if ( cp.getSymbol().token != Token.RIGHTPAR ) {
					cp.error(cp.getSymbol(), "')' expected" );
				}
				cp.next();
				if ( cp.getSymbol().token != Token.MULT &&
						cp.getSymbol().token != Token.PLUS ) {
					cp.error(cp.getSymbol(), "'+' or '*' expected" );
				}
				else {
					keywordWithMany.setRegularOperator(cp.getSymbol());
					cp.next();
				}
				return keywordWithMany;
				}
				else
					return new MessageKeywordWithTypes(symbolIdent);

			}
			else {
				// a keyword without types such as "read:" in
				// public func (open: String read:) :t UTuple<String, void>
				final MessageKeywordWithTypes keywordWithTypes =
					new MessageKeywordWithTypes(symbolIdent, new ArrayList<WrExpr>());
				return keywordWithTypes;
			}
		}
	}


	@Override
	public void parsing_parse(ICompiler_parsing cp) {

		// Compiler_parsing compiler_parsing = (Compiler_parsing ) cp;
		//saci.Compiler compiler = compiler_parsing.getCompiler();
		//ICompiler_parsing compiler = compiler_parsing;
		cp.next();
		grammarMethodSignatureInDSL = methodSignatureGrammarForMetaobject(cp);

		while ( cp.getSymbol().token == Token.IDENT ) {
			if ( actionFunctionMetaobjectList == null ) {
				actionFunctionMetaobjectList = new ArrayList<>();
			}
			final String checkerName = cp.getSymbol().getSymbolString();
			cp.next();
			final IActionFunction actionFunction = cp.searchActionFunction(checkerName);
			if ( actionFunction == null ) {
				this.addError(cp.getSymbol(), "Function metaobject '" + checkerName + "' was not found");
				return ;
			}
			actionFunctionMetaobjectList.add(actionFunction);
			if ( cp.getSymbol().token == Token.COMMA ) {
				cp.next();
				if ( cp.getSymbol().token != Token.IDENT ) {
					this.addError(cp.getSymbol(), "A function metaobject name was expected after ','");
					return ;
				}
			}
			else {
				break;
			}
		}

		if ( cp.getSymbol().token != Token.EOLO ) {
			cp.error(cp.getSymbol(), "Unexpected symbol: '" + cp.getSymbol().getSymbolString() + "'");
		}

		if ( cp.getCurrentPrototype() == null ) {
			this.addError("Annotation '" + this.getName() + "' can only be attached to a method.");
			return ;
		}

		/*
		 * the type that the grammar method should have is in the string
		 *     msg.getkeywordGrammar().getStringType().
		 * Method Compiler.parseSingleTypeFromString converts that to an object of the AST.
		 * This object is added to the a list of statements and expressions of the cp
		 * by statement
		 *      		cp.addExprStat(type);
		 * During semantic analysis, the compiler will find the type for this expression and it will
		 * create all instantiations of generic prototypes that  it needs.

		 */
		final WrExpr type = ICompiler_parsing.parseSingleTypeFromString(grammarMethodSignatureInDSL.getkeywordGrammar().getStringType(),
				this.annotation.getFirstSymbol(), "Internal error: ",
				cp.getCurrentCompilationUnit(), cp.getCurrentPrototype());

		cp.addExprStat(type);

		/*
		 * this is not currently used. It may be so in the future for
		 * replacying the AST by a single prototype.
		 */
		astRootTypeExpr = null;
		final WrAnnotationAt annotation = (WrAnnotationAt ) this.annotation;
		if ( annotation.getJavaParameterList() != null && annotation.getJavaParameterList().size() == 1 ) {
			if ( annotation.getJavaParameterList().get(0) instanceof String ) {
				// there is a prototype name as parameter
				final String prototypeName = MetaHelper.removeQuotes( (String ) annotation.getJavaParameterList().get(0) );
				astRootTypeExpr = ICompiler_parsing.parseSingleTypeFromString(prototypeName,
						this.annotation.getFirstSymbol(), "'" + prototypeName + "' is not a valid prototype name",
						cp.getCurrentCompilationUnit(), cp.getCurrentPrototype());

				cp.addExprStat(astRootTypeExpr);
			}
			else {
				this.addError("Wrong argument type for this metaobject annotation. It may take a prototype name as parameter as in '"
					+ this.getName() + "(Graph)' or in '" + this.getName() + "(myutil.Graph)'"	);
			}
		}



		//this.getAnnotation().setInfo_parsing( new Tuple2<Expr, WrMethodSignatureGrammar>(astRootTypeExpr, msg) );
		// info = new Tuple2<Expr, WrMethodSignatureGrammar>(astRootTypeExpr, msg);

	}


	private void checkDeclaration(ICompiler_afterResTypes compiler_afterResTypes) {

		final WrAnnotationAt annotation = (WrAnnotationAt ) this.annotation;
		final IDeclaration dec = annotation.getDeclaration();

		final WrMethodDec method = (WrMethodDec ) dec;
		final WrMethodSignature mss = method.getMethodSignature();
		if ( !(mss instanceof WrMethodSignatureWithKeywords) ) {
			compiler_afterResTypes.error(method.getFirstSymbol(compiler_afterResTypes.getEnv()),
					"Metaobject '" + this.getName() + "' is attached to this method. Then it "
					+ "should be a method with one keyword that is not an operator. Like 'add: Array<Int>'");
			return ;
		}
		final WrMethodSignatureWithKeywords msws = (WrMethodSignatureWithKeywords ) mss;
		if ( msws.getNameWithoutParamNumber().equals("init:") ) {
			compiler_afterResTypes.error(method.getFirstSymbol(compiler_afterResTypes.getEnv()), "Metaobject '" + this.getName() + "' is attached to an 'init:' method. This is illegal");
			return ;
		}

		//Expr astRootTypeExpr = ((Tuple2<Expr, WrMethodSignatureGrammar> ) annotation.getInfo_parsing()).f1;

		WrType astRootType = null;
		if ( astRootTypeExpr != null ) {
			astRootType = astRootTypeExpr.getType( compiler_afterResTypes.getEnv());
			if ( !astRootType.isObjectDec() ) {
				compiler_afterResTypes.error(annotation.getFirstSymbol(),
						"The parameter to this metaobject annotation should be a prototype. It cannot be Dyn or an interface");
				return ;
			}
		}



	}

//	@Override
//	public void ati2_checkDeclaration(ICompiler_afterResTypes compiler_afterResTypes) {
//		this.checkDeclaration(compiler_afterResTypes);
//	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
			List<ISlotSignature>>> infoList) {

		final WrAnnotationAt annotation = (WrAnnotationAt ) this.annotation;
		final IDeclaration dec = annotation.getDeclaration();
		final WrMethodDec method = (WrMethodDec ) dec;
		final WrMethodSignature mss = method.getMethodSignature();
		final WrMethodSignatureWithKeywords msNonGrammar = (WrMethodSignatureWithKeywords ) mss;
		//WrMethodSignatureGrammar msg = info.f2;
		if ( msNonGrammar.getKeywordArray() == null || msNonGrammar.getKeywordArray().size() != 1 ||
			 msNonGrammar.getKeywordArray().get(0).getParameterList() == null ||
					 msNonGrammar.getKeywordArray().get(0).getParameterList().size() != 1 ) {
			compiler.error(method.getFirstSymbol(compiler.getEnv()), "Metaobject '" + this.getName() +
					"' is attached to this method. Then it "
					+ "should be a method with one keyword that is not an operator. Like 'add: Array<Int>'");
		}
		final WrType type = ICompiler_parsing.singleTypeFromString(
				this.grammarMethodSignatureInDSL.getkeywordGrammar().getStringType(),
				msNonGrammar.getFirstSymbol(), "Internal error: ", compiler.getEnv().getCurrentCompilationUnit(),
				compiler.getEnv().getCurrentPrototype(), compiler.getEnv());


		final WrType methodParameterType = msNonGrammar.getKeywordArray().get(0).getParameterList().get(0).getType();
		if ( type != methodParameterType ) {
			final String fn = type.getFullName().replace(",", ", ");

			compiler.error(method.getFirstSymbol(compiler.getEnv()),  "The parameter of this method has type '" +
					methodParameterType.getFullName() + "' but according to metaobject '" + this.getName()+ "' attached to it "
					+ "this method should have type '" + fn + "'");
		}

		this.checkDeclaration(compiler);

		return null;
	}

//	@Override
//	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {
//
//
//		final WrAnnotationAt annotation = (WrAnnotationAt ) this.metaobjectAnnotation;
//		final IDeclaration dec = annotation.getDeclaration();
//		final WrMethodDec method = (WrMethodDec ) dec;
//		final WrMethodSignature mss = method.getMethodSignature();
//		final WrMethodSignatureWithKeywords msNonGrammar = (WrMethodSignatureWithKeywords ) mss;
//		//WrMethodSignatureGrammar msg = info.f2;
//		if ( msNonGrammar.getKeywordArray() == null || msNonGrammar.getKeywordArray().size() != 1 ||
//			 msNonGrammar.getKeywordArray().get(0).getParameterList() == null ||
//					 msNonGrammar.getKeywordArray().get(0).getParameterList().size() != 1 ) {
//			compiler.error(method.getFirstSymbol(compiler.getEnv()), "Metaobject '" + this.getName() +
//					"' is attached to this method. Then it "
//					+ "should be a method with one keyword that is not an operator. Like 'add: Array<Int>'");
//		}
//		final WrType type = ICompiler_parsing.singleTypeFromString(
//				this.grammarMethodSignatureInDSL.getkeywordGrammar().getStringType(),
//				msNonGrammar.getFirstSymbol(), "Internal error: ", compiler.getEnv().getCurrentCompilationUnit(),
//				compiler.getEnv().getCurrentPrototype(), compiler.getEnv());
//
//
//		final WrType methodParameterType = msNonGrammar.getKeywordArray().get(0).getParameterList().get(0).getType();
//		if ( type != methodParameterType ) {
//			final String fn = type.getFullName().replace(",", ", ");
//
//			compiler.error(method.getFirstSymbol(compiler.getEnv()),  "The parameter of this method has type '" +
//					methodParameterType.getFullName() + "' but according to metaobject '" + this.getName()+ "' attached to it "
//					+ "this method should have type '" + fn + "'");
//		}
//
//	}

	@Override
	public Tuple3<StringBuffer, String, String> semAn_missingKeywordMethod(WrExpr receiver,
			 WrMessageWithKeywords message, WrEnv env) {


		final MessageKeywordLexer lexer = new MessageKeywordLexer( message.getkeywordParameterList() );

		// WrMethodSignatureGrammar gmSignature = info.f2;

		final WrAnnotationAt annotation = (WrAnnotationAt ) this.annotation;
		final IDeclaration dec = annotation.getDeclaration();
		final WrMethodDec method = (WrMethodDec ) dec;


		final Tuple2<String, String> t = this.grammarMethodSignatureInDSL.getkeywordGrammar().parse(lexer, env);


		if ( t != null && lexer.current() == null ) {
			if ( actionFunctionMetaobjectList != null ) {
				for ( final IActionFunction callable : this.actionFunctionMetaobjectList ) {
					final String errorMessage = (String ) callable.eval(new Tuple2<WrExpr, WrMessageWithKeywords>(receiver, message));
					if ( errorMessage != null ) {
						this.addError(message.getFirstSymbol(), "Checker metaobject '" + ((CyanMetaobject) callable).getName() +
								"' signaled an error message: " + errorMessage);
					}
				}

			}
			WrType returnType = method.getMethodSignature().getReturnType(env);
			return new Tuple3<StringBuffer, String, String>(new StringBuffer ( ( receiver != null ? receiver.asString() : "" ) + " " +
					method.getNameWithoutParamNumber() + " " + t.f1 ),
					returnType.getPackageName(), returnType.getName()
					);

		}
		else {
			return null;
		}
	}


	private static boolean startType(Token t) {
		return t == Token.IDENT ||
		       t == Token.TYPEOF  ||
		       t == Token.STRING ||
		       t == Token.DYN ||
		       isBasicType(t);

	}

	public static boolean isBasicType(Token t) {
		return 	t == Token.BYTE || t ==  Token.SHORT || t ==  Token.INT ||
		t ==  Token.LONG || t ==  Token.FLOAT || t ==  Token.DOUBLE ||
		t ==  Token.CHAR || t ==  Token.BOOLEAN || t == Token.NIL || t == Token.STRING;
	}


	/**
	 * the signature in the DSL of the metaobject annotation
	 */
	private WrMethodSignatureGrammar grammarMethodSignatureInDSL;
	/**
	 * the prototype that is parameter to the annotation.
	 * In the future this will be considered as the type of the
	 * parameter of the method to which the annotation is attached.
	 * Then this will be the type of the AST built from the
	 * message send.
	 */
	private WrExpr astRootTypeExpr;

	private List<IActionFunction> actionFunctionMetaobjectList;


}
