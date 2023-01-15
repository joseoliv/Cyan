/**
 *
 */
package meta.cyanLang;

import java.util.List;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrGenericParameter;
import meta.WrMessageKeywordWithRealParameters;
import meta.WrPrototype;
import meta.WrSymbol;
import meta.WrType;


/**
 * This class represents keywords like
 *     add: (Int)*
 * or
 *     println: (String)+
 *
 * @author José
 *
 */
public class MessageKeywordWithMany extends IMessageKeyword {

	public MessageKeywordWithMany(WrSymbol keyword, WrExpr type) {
		super();
		this.keyword = keyword;
		this.typeExpr = type;
	}



	/** A keyword like
	 *       add: (int)*
	 *  will have a Java name
	 *       add_int_star
	 *  In
	 *       add: (int | String | Is_a_boolean)+
	 *  the result of this method will be
	 *      left_int_CyString_Is__a__boolean_right_or_plus
	 *
	 */
	public String getJavaName() {
		return MetaHelper.getJavaNameOfkeyword(keyword.getSymbolString()) + "_" + typeExpr.getJavaName() +
				"_" + regularOperator.getSymbolString();
	}


	public void setkeyword(WrSymbol keyword) {
		this.keyword = keyword;
	}
	public WrSymbol getkeyword() {
		return keyword;
	}


	public WrSymbol getRegularOperator() {
		return regularOperator;
	}

	public void setRegularOperator(WrSymbol regularOperator) {
		this.regularOperator = regularOperator;
	}

	public WrExpr getTypeExpr() {
		return typeExpr;
	}

	public void setTypeExpr(WrExpr type) {
		this.typeExpr = type;
	}



	@Override
	public String getStringType() {
		// add: (Int)*  has typeExpr  Array<Int>
		return "Array<" + typeExpr.asString() + ">";
	}

	@Override
	public void calcInterfaceTypes(WrEnv env) {
		typeExpr.calcInternalTypes(env);
	}

	@Override
	public String getFullName(WrEnv env) {
		return keyword.getSymbolString() + " (" + typeExpr.ifPrototypeReturnsItsName(env)
		    + ")" + regularOperator.getSymbolString();
	}

	@Override
	public String getName() {
		return keyword.getSymbolString();
	}

	@Override
	public Tuple2<String, String> parse(MessageKeywordLexer lexer, WrEnv env) {
		final WrMessageKeywordWithRealParameters messagekeyword = lexer.current();
		if ( messagekeyword == null || ! messagekeyword.getkeywordName().equals(keyword.getSymbolString()) ) {
			return null;
		}
		final List<WrExpr> realExprList = messagekeyword.getExprList();
		if ( regularOperator.token == Token.PLUS ) {
			// one or more
			if ( realExprList == null || realExprList.size() == 0 )
				return null;
		}
		else if ( regularOperator.token == Token.QUESTION_MARK ) {
			// zero or one
			if ( realExprList != null && realExprList.size() > 1 )
				return null;
		}

		lexer.next();

		if ( realExprList == null || realExprList.size() == 0 ) {
			   // zero parameters, use something like "Array<Int>()"
			return new Tuple2<String, String>("Array<" + typeExpr.getType(env).getFullName() + ">()",
					"Array<" + typeExpr.getType(env).getFullName() + ">");
		}
		else {
			int size = realExprList.size();
			String s = " [ ";
			final WrType formalParamType = typeExpr.getType(env);
			final String formalTypeStr = formalParamType.getFullName();
			for ( final WrExpr expr : realExprList ) {
				final WrType exprType = expr.getType(env);
				if ( ! formalParamType.isSupertypeOf(exprType, env) )
					return null;
				if ( formalParamType != exprType ) {
					// s += formalTypeStr + " cast: (" + expr.asString() + ")";
					boolean isUnion = false;
					int numF = -1;
					if ( formalParamType instanceof WrPrototype ) {
						final WrPrototype proto = (WrPrototype ) formalParamType;
						isUnion = proto.getName().startsWith("Union<");
						for ( final List<WrGenericParameter> gpList : proto.getGenericParameterListList(env) ) {
							int n = 1;
							for ( final WrGenericParameter gp :  gpList ) {
								WrType wrType = gp.getType();
								if ( wrType != null ) {
									if ( wrType.isSupertypeOf(exprType, env)) {
										numF = n;
										break;
									}
								}
								++n;
							}
						}

					}
					if ( isUnion ) {
						s += "( " + formalTypeStr + " f" + numF + ": (" + expr.asString() + ") )";
					}
					else {
						s += "( Cast<" + formalTypeStr + "> asReceiver: (" + expr.asString() + ") )";
					}

				}
				else {
					s += expr.asString();
				}
				if ( --size > 0 )
					s += ", ";
			}
			return new Tuple2<String, String>(s + " ] ",
					"Array<" + typeExpr.getType(env).getFullName() + ">");
		}

	}

	@Override
	public boolean matchesEmptyInput() {
		return false;
	}

	/**
	 * astRootType should have a method<br>
	 * <code>
	 *     {@literal @}annot(gmast)<br>
	 *     func sel: Array<Type1> p1 -> astRootType<br>
	 * </code>
	 * in which Type1 is the type of this keyword.
	 *
	 */

//	@Override
//	void setAstRootType(ObjectDec astRootType, Env env, Symbol first) {
//		Tuple2<MethodDec, WrExprAnyLiteral> t = astRootType.searchMethodByFeature(MessageKeyword.annotAstBuildingMethod);
//		if ( t == null ) {
//			env.error(first,  "Prototype '" + astRootType.getFullName() + "' should have a method with annotation '"
//					+ MessageKeyword.annotAstBuildingMethod + "'. But it does not.");
//		}
//		else {
//			MethodSignature mss = t.f1.getMethodSignature();
//			if ( !(mss instanceof ast.MethodSignatureWithKeywords) || ( (MethodSignatureWithKeywords) mss).getParameterList() == null ||
//					( (MethodSignatureWithKeywords) mss).getParameterList().size() != 1	) {
//				env.error(first,  "Method '" + t.f1.getName() + "' of Prototype '" + astRootType.getFullName() +
//						"' should have exactly one keyword and it cannot have an operator name such as '+'");
//			}
//			else {
//				MethodSignatureWithKeywords ms = (MethodSignatureWithKeywords) mss;
//				if ( ms.getParameterList().size() != 1 ) {
//					env.error(first,  "Method '" + t.f1.getName() + "' of Prototype '" + astRootType.getFullName() +
//							"' should have exactly one parameter");
//				}
//				else {
//					Type paramType = ms.getParameterList().get(0).getType(env);
//					if ( !(paramType instanceof ObjectDec) ) {
//						env.error(first,  "Method '" + t.f1.getName() + "' of Prototype '" + astRootType.getFullName() +
//								"' should have exactly one parameter whose type is a prototype (it cannot be Dyn or an interface)");
//					}
//					ObjectDec protoType = (ObjectDec ) paramType;
//					if ( ! protoType.getFullName(env).equals("Array<" + typeExpr.getType(env.getI()).getFullName() + ">") ) {
//						env.error(first,  "Method '" + t.f1.getName() + "' of Prototype '" + astRootType.getFullName() +
//								"' should have exactly one parameter whose type is '" + "Array<" + typeExpr.getType(env.getI()).getFullName() + ">'" );
//					}
//					if ( ms.getReturnType(env) != astRootType ) {
//						env.error(first,  "Method '" + t.f1.getName() + "' of Prototype '" + astRootType.getFullName() +
//								"' should have '" + astRootType.getFullName() + " as return type");
//					}
//
//					this.astRootType = astRootType;
//				}
//			}
//		}
//	}
//


	/**
	 * the keyword. It is "add:" in
	 *     add: (int)*
	 */
	private WrSymbol keyword;

	/**
	 * the typeExpr between parenthesis such as int in
	 *     add: (int)*
	 * it may be several types separated by | such as in
	 *     add: (int | String | Person)+
	 */
	private WrExpr typeExpr;
	/**
	 *   + or *. It is Token.PLUS in
	 *        add: (int)+
	 */
	private WrSymbol regularOperator;
}
