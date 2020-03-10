/**
 *
 */
package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrMessageKeywordWithRealParameters;
import meta.WrSymbol;
import meta.WrType;

/**
 * Represents a keyword with types such as
 *  1. "add: Int"  in
 *    public fun (add: Int)+
 *  2. "format: String, Int" in
 *    public fun (format: String, Int  println: (String)*)
 *
 * @author José
 *
 */
public class MessageKeywordWithTypes extends IMessageKeyword {

	public MessageKeywordWithTypes(WrSymbol keyword,
			List<WrExpr> typeList) {
		super();
		this.keyword = keyword;
		this.typeList = typeList;
	}


	public MessageKeywordWithTypes(WrSymbol keyword) {
		this.keyword = keyword;
		typeList = new ArrayList<WrExpr>();
	}

	public void addTypeOneMany( WrExpr aType ) {
		typeList.add(aType);
	}



	/**
	 * return the Java name of a method that has this keyword. If the
	 * keyword is
	 *           case: char
	 *  the generated name will be
	 *      case_p_char
	 *  if it is
	 *  	add: int, String, Shape_Figure
	 *  the generated name will be
	 *      add_p_int_p_CyString_p_Shape__Figure
	 */
	public String getJavaName() {
		String s;
		s = MetaHelper.getJavaNameOfkeyword(keyword.getSymbolString());
		final int i = s.indexOf(':');
		if ( i >= 0 )
		   s = s.substring(0, i);
		typeList.size();
		for ( final WrExpr p : typeList )
			s = s + "_p_" + p.getJavaName();

		return null;
	}


	public void setTypeList(List<WrExpr> typeList) {
		this.typeList = typeList;
	}
	public List<WrExpr> getTypeList() {
		return typeList;
	}

	@Override
	public String getStringType() {
		if ( typeList.size() == 0 )
			return "Any";
		else {
			String s = "";
			int size= typeList.size();
			for ( final WrExpr aType : typeList) {
				s = s + aType.asString();
				if ( --size > 0 )
					s = s + ", ";
			}
			return typeList.size() > 1 ? "Tuple<" + s + ">" : s;
		}
	}

	@Override
	public void calcInterfaceTypes(WrEnv env) {
		for ( final WrExpr aType : typeList )
			aType.calcInternalTypes(env);
	}

	@Override
	public String getFullName(WrEnv env) {
		String s = keyword.getSymbolString();
		if ( typeList != null && typeList.size() > 0 ) {
			int size = typeList.size();
			for ( final WrExpr e : typeList ) {
				s = s + e.ifPrototypeReturnsItsName(env);
				if ( --size > 0 )
					s = s + " ";
			}
		}
		return s;
	}

	@Override
	public String getName() {
		return keyword.getSymbolString();
	}


	@Override
	public Tuple2<String, String> parse(MessageKeywordLexer lexer, WrEnv env) {
		final WrMessageKeywordWithRealParameters messagekeyword = lexer.current();
		if ( messagekeyword == null )
			return null;
		if ( ! messagekeyword.getkeywordName().equals(keyword.getSymbolString()) )
			return null;
		else {
			final List<WrExpr> realExprList = messagekeyword.getExprList();
			if ( realExprList.size() != typeList.size() )
				return null;
			else {
				if ( typeList.size() == 0 ) {
					lexer.next();
					return new Tuple2<String, String>("Any", "Any");
				}
				String tupleType = "Tuple<";
				int n = 0;
				int sizeTypeList = typeList.size();
				for ( final WrExpr expr : realExprList ) {
					final WrType paramType = typeList.get(n).getType(env);
					if ( ! paramType.isSupertypeOf(expr.getType(env), env) )
						return null;
					tupleType += "f" + (n+1) + ", " + paramType.getFullName();
					if ( --sizeTypeList > 0 ) {
						tupleType += ", ";
					}
					++n;
				}
				tupleType += ">";
				lexer.next();
				if ( realExprList.size() == 1 )
					return new Tuple2<String, String>(realExprList.get(0).asString(), typeList.get(0).getType(env).getFullName() );
				else {
					String s = "[. ";
					int size = realExprList.size();
					for ( final WrExpr expr : realExprList ) {
						s += expr.asString();
						if ( --size > 0 ) {
							s += ", ";
						}
					}
					s += " .] ";
					return new Tuple2<String, String>(s, tupleType) ;
				}
			}
		}
	}


	@Override
	public boolean matchesEmptyInput() {
		return false;
	}

//	/**
//	 * astRootType should have a method<br>
//	 * <code>
//	 *     {@literal @}annot(gmast)<br>
//	 *     func sel: Type1 p1 Type2 p2, ... Typen pn -> astRootType<br>
//	 * </code>
//	 * in which Type1, Type2, ... Typen are the type of the parameters of this keyword.
//	 *
//	 */
//	@Override
//	public void setAstRootType(ObjectDec astRootType, Env env, Symbol first) {
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
//				if ( ms.getParameterList().size() != typeList.size() ) {
//					env.error(first,  "Method '" + t.f1.getName() + "' of Prototype '" + astRootType.getFullName() +
//							"' should have exactly " + typeList.size() + " parameters");
//				}
//				else {
//					List<ParameterDec> paramList = ms.getParameterList();
//					int n = 0;
//					for ( Expr typeExpr : typeList ) {
//						if ( typeExpr.getType(env) != paramList.get(n).getType(env) ) {
//							env.error(first,  "Parameter number '" + n +  "' of Method '" + t.f1.getName() +
//									"' of Prototype '" + astRootType.getFullName() +
//									"' should have type '" + typeExpr.getType(env).getFullName() + "'");
//						}
//						++n;
//					}
//					if ( ms.getReturnType(env) != astRootType ) {
//						env.error(first,  "Method '" + t.f1.getName() + "' of Prototype '" + astRootType.getFullName() +
//								"' should have '" + astRootType.getFullName() + " as return type");
//					}
//					this.astRootType = astRootType;
//				}
//			}
//		}
//	}
//

	/**
	 * the keyword. It is "println: in
	 *     println: String
	 * and "amount:" in
	 *     amount:  // no parameters
	 * In the first case, typeList contains a single element, "String"
	 */
	private final WrSymbol keyword;
	/**
	 * list of the types associated with keyword. It may be empty
	 * for a keyword may not have parameters.
	 */
	private List<WrExpr>  typeList;

}
