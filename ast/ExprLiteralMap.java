package ast;

import java.util.ArrayList;
import java.util.List;
import error.ErrorKind;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.Function0;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprLiteralMap;
import saci.CompilerManager;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;


/** represents a literal map such as
*    [ 0 -> "zero", 1 -> "one" ]
*
* @author José
*
*/
public class ExprLiteralMap extends ExprAnyLiteral {

	/**
	 *
	 */
	public ExprLiteralMap( Symbol startSymbol, Symbol endSymbol,
			                 List<Expr> exprList, MethodDec method) {
		super(method);
		this.startSymbol = startSymbol;
		this.endSymbol = endSymbol;
		this.exprList = exprList;
	}


	@Override
	public WrExprLiteralMap getI() {
		return new WrExprLiteralMap(this);
	}

	public void setExprList(List<Expr> exprList) {
		this.exprList = exprList;
	}

	public List<Expr> getExprList() {
		return exprList;
	}

	public void setStartSymbol(Symbol startSymbol) {
		this.startSymbol = startSymbol;
	}

	public Symbol getStartSymbol() {
		return startSymbol;
	}

	public void setEndSymbol(Symbol endSymbol) {
		this.endSymbol = endSymbol;
	}

	public Symbol getEndSymbol() {
		return endSymbol;
	}

	@Override
	public boolean isNRE(Env env) {
		for ( Expr e : exprList ) {
			if ( ! e.isNRE(env) )
				return false;
		}
		return true;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.print(" [ ");
		int size = exprList.size();
		for (int i = 0; i < size; ++i) {
			Expr e = exprList.get(i);
			e.genCyan(pw, false, cyanEnv, genFunctions);
			pw.print(" -> ");
			++i;
			e = exprList.get(i);
			e.genCyan(pw, false, cyanEnv, genFunctions);
			if ( i < size - 1 ) {
				pw.print(", ");
			}
		}
		pw.print(" ] ");

	}


	@Override
	public String metaobjectParameterAsString(Function0 inError) {
		StringBuffer s = new StringBuffer();
		s.append(" [ ");

		int size = exprList.size();
		for (int i = 0; i < size; ++i) {
			Expr e = exprList.get(i);
			if ( !(e instanceof ExprAnyLiteral) ) {
				inError.eval();
			}
			s.append( ((ExprAnyLiteral) e).metaobjectParameterAsString(inError) );
			s.append( " -> " );
			++i;
			e = exprList.get(i);
			if ( !(e instanceof ExprAnyLiteral) ) {
				inError.eval();
			}
			s.append( ((ExprAnyLiteral) e).metaobjectParameterAsString(inError) );
			if ( i < size - 1 ) {
				s.append(", ");
			}
		}
		s.append(" ] ");
		return s.toString();
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {

		String literalArrayTmpVar = NameServer.nextJavaLocalVariableName();

		// String javaTypeFirstExpr = exprList.get(0).getType(env).getJavaName();

		String javaArrayType = MetaHelper.getJavaName("HashMap<" + keyType.getFullName() + ", "
				+ valueType.getFullName()
		   + ">");

		pw.printlnIdent( javaArrayType + " " + literalArrayTmpVar + " = new " + javaArrayType +
				  "();"   );



		String tmpVar, tmpVar2;

		int size = exprList.size();
		for (int i = 0; i < size; ++i) {
			Expr e = exprList.get(i);
			tmpVar = e.genJavaExpr(pw, env);
			++i;
			e = exprList.get(i);
			tmpVar2 = e.genJavaExpr(pw, env);
			pw.printlnIdent(literalArrayTmpVar + "." + NameServer.javaNameAddMethodTwoParameters + "( " + tmpVar + ", " + tmpVar2 + ");");
		}


		return literalArrayTmpVar;
	}

	/*
	@Override
	public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		throw new ExceptionGenJavaExprWithoutTmpVar();
	}
	*/


	@Override
	public Symbol getFirstSymbol() {
		return startSymbol;
	}




	@Override
	public void calcInternalTypes(Env env) {


		for ( Expr expr : exprList )
			expr.calcInternalTypes(env);

		keyType = exprList.get(0).getType(env);
		valueType = exprList.get(1).getType(env);


		Type insideKeyType = keyType.getInsideType();
		Type insideValueType = valueType.getInsideType();
		if ( !(insideKeyType instanceof Prototype || insideKeyType instanceof TypeUnion
				|| keyType == Type.Dyn) ||
				!(insideValueType instanceof Prototype || insideValueType instanceof TypeUnion
						|| valueType == Type.Dyn) ) {
			env.error(true, exprList.get(0).getFirstSymbol(),
				"The type of this expression should be a Cyan prototype", null, ErrorKind.prototype_as_type_expected_inside_method);
		}
		else {

			int size = exprList.size();
			for (int i = 0; i < size; ++i) {
				Expr e = exprList.get(i);
				if ( ! keyType.isSupertypeOf(e.getType(), env)) {
					env.error(e.getFirstSymbol(), "This expression should be subtype of the type of the first "
							+ "key expression of this literal map, '" + keyType.getFullName() + "'"
							+ ". It is not");
				}
				++i;
				e = exprList.get(i);
				if ( ! valueType.isSupertypeOf(e.getType(), env)) {
					env.error(e.getFirstSymbol(), "This expression should be subtype of the type of the first "
							+ "key expression of this literal map, '" + valueType.getFullName() + "'"
							+ ". It is not");
				}
			}

			Symbol sym = this.getFirstSymbol();

			SymbolIdent symbolIdent = new SymbolIdent(Token.IDENT, "HashMap", sym.getStartLine(),
					sym.getLineNumber(), sym.getColumnNumber(), sym.getOffset(), sym.getCompilationUnit() );
			ExprIdentStar typeIdent = new ExprIdentStar(null, symbolIdent);

			List<List<Expr>> realTypeListList = new ArrayList<List<Expr>>();
			List<Expr> realTypeList = new ArrayList<Expr>();

			/* # ExprIdentStar exprElementType = new ExprIdentStar( new SymbolIdent(Token.IDENT, p.getName(), -1, -1, -1, -1) );

			realTypeList.add(exprElementType); */
			realTypeList.add( keyType.asExpr(this.getFirstSymbol()) );
			realTypeList.add( valueType.asExpr(this.getFirstSymbol()) );
			realTypeListList.add(realTypeList);

			ExprGenericPrototypeInstantiation gpi = new ExprGenericPrototypeInstantiation( typeIdent,
					realTypeListList, env.getCurrentPrototype(), null, null);
			type = null;
			List<Expr> interfaceExprList = ((ObjectDec ) CompilerManager.createGenericPrototype(gpi, env)).getInterfaceList();
			for ( Expr interfaceExpr : interfaceExprList ) {
				InterfaceDec anInterface = (InterfaceDec ) interfaceExpr.getType();
				if ( anInterface.getName().startsWith(NameServer.IMapName + "<") ) {
					type = anInterface;
				}
			}
			if ( type == null ) {
				env.error(this.getFirstSymbol(), "It was expected that 'cyan.lang.HashMap' implemented interface 'cyan.lang."
						+ NameServer.IMapName + "<...>'" );
			}
			/* String typeName = "Array<" + p.getName() + ">";
			type = env.searchVisiblePrototype(typeName, exprList.get(0).getFirstSymbol(), true); */
			assert type != null;
		}

		super.calcInternalTypes(env);

	}

	@Override
	public Object getJavaValue() {
		Object []objArray = new Object[exprList.size()];
		int i = 0;
		for ( Expr e : exprList ) {
			if ( e instanceof ExprAnyLiteral ) {
			    objArray[i] = ((ExprAnyLiteral ) e).getJavaValue();
			    ++i;
			}
		}
		return objArray;
	}

	@Override
	public StringBuffer getStringJavaValue() {
		StringBuffer s = new StringBuffer();
		s.append("new ");
		if ( exprList.size() == 0 )
			s.append("Object[] { } ");
		else {
			s.append(((ExprAnyLiteral ) exprList.get(0)).getJavaType());
			s.append("[] { ");
			int size = exprList.size();
			for ( Expr e : exprList ) {
				s.append( ((ExprAnyLiteral ) e).getStringJavaValue() );
				if ( --size > 0 )
					s.append(", ");
			}

			s.append(" }");
		}
		return s;
	}

	@Override
	public String getJavaType() {
		if ( exprList.size() == 0 )
			return "Object[]";
		else {
			return ((ExprAnyLiteral ) exprList.get(0)).getJavaType() + "[]";
		}
	}

	@Override
	public boolean isValidMetaobjectFeatureParameter() {
		String keyTypeName = exprList.get(0).getClass().getName();
		String valueTypeName = exprList.get(1).getClass().getName();
		for (int i = 2; i < exprList.size() - 1; ++i) {
			if ( ! exprList.get(i).getClass().getName().equals(keyTypeName) ) {
				return false;
			}
			++i;
			if ( ! exprList.get(i).getClass().getName().equals(valueTypeName) ) {
				return false;
			}
		}

		return true;
	}


	/**
	 * symbols representing [ and ]
	 */
	private Symbol startSymbol, endSymbol;
	private List<Expr> exprList;
	/**
	 * the type of key elements
	 */
	private Type keyType;
	/**
	 * type of value elements
	 */
	private Type valueType;
}
