/**
 *
 */
package ast;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import cyan.lang._Any;
import error.ErrorKind;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.CyanMetaobjectAtAnnot;
import meta.Function0;
import meta.IActionAssignment_cge;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrExprLiteralArray;
import saci.CompilerManager;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;


/** represents a literal array such as
 *    [ 1, 2, 3 [
 *    [ [ "um", "dois" ], [ "one", "two" ] ]
 *
 * @author José
 *
 */
public class ExprLiteralArray extends ExprAnyLiteral {

	/**
	 *
	 */
	public ExprLiteralArray( Symbol startSymbol, Symbol endSymbol,
			                 List<Expr> exprList, MethodDec method) {
		super(method);
		this.startSymbol = startSymbol;
		this.endSymbol = endSymbol;
		this.exprList = exprList;
	}


	@Override
	public WrExprLiteralArray getI() {
		return new WrExprLiteralArray(this);
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
		for ( final Expr e : exprList ) {
			if ( ! e.isNRE(env) )
				return false;
		}
		return true;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.print(" [ ");
		int n = exprList.size();
		for ( final Expr e : exprList ) {
			e.genCyan(pw, false, cyanEnv, genFunctions);
			--n;
			if ( n > 0 )
				pw.print(", ");
		}
		pw.print(" ] ");

	}


	@Override
	public String metaobjectParameterAsString(Function0 inError) {
		final StringBuffer s = new StringBuffer();
		s.append(" [ ");
		int n = exprList.size();
		for ( final Expr e : exprList ) {
			if ( !(e instanceof ExprAnyLiteral) ) {
				inError.eval();
			}
			s.append( ((ExprAnyLiteral) e).metaobjectParameterAsString(inError) );
			--n;
			if ( n > 0 )
				s.append(", ");
		}
		s.append(" ] ");
		return s.toString();
	}


	/*
	public void genCyanReplacingGenericParameters(PWInterface pw, CyanEnv cyanEnv) {
		pw.print("{# ");
		int n = exprList.size();
		for ( Expr e : exprList ) {
			if ( e instanceof ExprLiteral )
				((ExprLiteral ) e).genCyanReplacingGenericParameters(pw, cyanEnv);
			else
				e.genCyan(pw, cyanEnv);
			--n;
			if ( n > 0 )
				pw.print(", ");
		}
		pw.print(" #}");
	}
	*/


	@Override
	public String genJavaExpr(PWInterface pw, Env env) {

		final String literalArrayTmpVar = NameServer.nextJavaLocalVariableName();

		// String javaTypeFirstExpr = exprList.get(0).getType(env).getJavaName();

		final String javaArrayType = MetaHelper.getJavaName("Array<" + this.arrayElementType.getFullName() + ">");
		pw.printlnIdent( javaArrayType + " " + literalArrayTmpVar + " = new " + javaArrayType +
				  "( new CyInt(" +
		                this.exprList.size() + ") );"   );

		String tmpVar;
		for ( final Expr e : exprList ) {
			tmpVar = e.genJavaExpr(pw, env);

			final Tuple2<IActionAssignment_cge, ObjectDec> cyanMetaobjectPrototype =
					MetaInfoServer.getChangeAssignmentCyanMetaobject(env, arrayElementType);
			IActionAssignment_cge changeCyanMetaobject = null;
	        ObjectDec prototypeFoundMetaobject = null;
	        if ( cyanMetaobjectPrototype != null ) {
	        	changeCyanMetaobject = cyanMetaobjectPrototype.f1;
	        	prototypeFoundMetaobject = cyanMetaobjectPrototype.f2;

					if ( changeCyanMetaobject != null ) {

						try {
							tmpVar = changeCyanMetaobject.cge_changeRightHandSideTo(
									prototypeFoundMetaobject,
			   	           			tmpVar, e.getType());
						}
						catch ( final error.CompileErrorException er ) {
						}
						catch ( final NoClassDefFoundError e1 ) {
							env.error(
									meta.GetHiddenItem.getHiddenSymbol(((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation().getFirstSymbol()),
									e1.getMessage() + " " + NameServer.messageClassNotFoundException);
						}
						catch ( final RuntimeException er ) {
							final WrAnnotation annotation = ((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation();
							env.thrownException(
									meta.GetHiddenItem.getHiddenCyanAnnotation(annotation),
									meta.GetHiddenItem.getHiddenSymbol(annotation.getFirstSymbol()), er);
						}
						finally {
		   					env.errorInMetaobject( (meta.CyanMetaobject ) changeCyanMetaobject, this.getFirstSymbol());
						}
	   				}
	        }

			pw.printlnIdent(literalArrayTmpVar + "." + NameServer.javaNameAddMethod + "( " + tmpVar + ");");
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


		for ( final Expr expr : exprList )
			expr.calcInternalTypes(env);

		arrayElementType = exprList.get(0).getType(env);


		Type insideArrayElementType = arrayElementType.getInsideType();
		if ( !(insideArrayElementType instanceof Prototype)
				&& arrayElementType != Type.Dyn &&
				!(insideArrayElementType instanceof TypeUnion) ) {
			env.error(true, exprList.get(0).getFirstSymbol(), "The type of this expression should be a Cyan prototype", null, ErrorKind.prototype_as_type_expected_inside_method);
		}
		else {

			for ( final Expr expr : exprList ) {
				if ( ! arrayElementType.isSupertypeOf(expr.getType(), env) ) {
					env.error(expr.getFirstSymbol(), "This expression should be subtype of the type of the first "
							+ "expression of this literal array, '" + arrayElementType.getFullName() + "'"
							+ ". It is not");
				}
			}


			final Symbol sym = this.getFirstSymbol();

			final SymbolIdent symbolIdent = new SymbolIdent(Token.IDENT, "Array", sym.getStartLine(),
					sym.getLineNumber(), sym.getColumnNumber(), sym.getOffset(), sym.getCompilationUnit() );
			final ExprIdentStar typeIdent = new ExprIdentStar(null, symbolIdent);

			final List<List<Expr>> realTypeListList = new ArrayList<List<Expr>>();
			final List<Expr> realTypeList = new ArrayList<Expr>();

			/* # ExprIdentStar exprElementType = new ExprIdentStar( new SymbolIdent(Token.IDENT, p.getName(), -1, -1, -1, -1) );

			realTypeList.add(exprElementType); */
			realTypeList.add( arrayElementType.asExpr(this.getFirstSymbol()) );
			realTypeListList.add(realTypeList);

			final ExprGenericPrototypeInstantiation gpi = new ExprGenericPrototypeInstantiation( typeIdent,
					realTypeListList, env.getCurrentPrototype(), null, null);
			type = CompilerManager.createGenericPrototype(gpi, env);

			/* String typeName = "Array<" + p.getName() + ">";
			type = env.searchVisiblePrototype(typeName, exprList.get(0).getFirstSymbol(), true); */
			assert type != null;
		}

		super.calcInternalTypes(env);

	}

	@Override
	public Object getJavaValue() {
		final Object []objArray = new Object[exprList.size()];
		int i = 0;
		for ( final Expr e : exprList ) {
			if ( e instanceof ExprAnyLiteral ) {
			    objArray[i] = ((ExprAnyLiteral ) e).getJavaValue();
			    ++i;
			}
		}
		return objArray;
	}

	@Override
	public StringBuffer getStringJavaValue() {
		final StringBuffer s = new StringBuffer();
		s.append("new ");
		if ( exprList.size() == 0 )
			s.append("Object[] { } ");
		else {
			s.append(((ExprAnyLiteral ) exprList.get(0)).getJavaType());
			s.append("[] { ");
			int size = exprList.size();
			for ( final Expr e : exprList ) {
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
		final String firstTypeName = exprList.get(0).getClass().getName();
		for (int i = 1; i < exprList.size() - 1; ++i) {
			if ( ! exprList.get(i).getClass().getName().equals(firstTypeName) ) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Object eval(EvalEnv ee) {
		Object array[];
		if ( exprList == null || exprList.size() == 0 ) {
			array = new Object[0];
//			ee.error(meta.GetHiddenItem.getHiddenSymbol(ee.symbolForErrorMessage),
//					"Literal array without elements. This is illegal");
		}
		else {
			array = new Object[exprList.size()];
		}
		int n = 0;

		for ( final Expr e : exprList ) {
			Object elem = e.eval(ee);
			array[n] = elem;
			++n;
		}

		String realArrayTypeStr = "Array<";
		Object first = array[0];
		//Class<?> firstType = first.getClass();
		if ( EvalEnv.any.isAssignableFrom(first.getClass()) ) {
			String protoName = ((_Any ) first)._prototypeName().s;
			if ( MetaHelper.isSelectedCyanLangProtototype(protoName) ) {
				realArrayTypeStr += protoName;
			}
			else {
				realArrayTypeStr += ((_Any ) first)._prototypePackageName().s + "." +
						protoName;
			}
		}

		Class<?> javaClass;
		realArrayTypeStr += ">";
		javaClass = ee.searchPrototypeAsType("cyan.lang." + realArrayTypeStr);
		if ( javaClass == null ) {
			String arrayTypeStr;

			arrayTypeStr = "Array<Dyn>";

			javaClass = ee.searchPrototypeAsType("cyan.lang.Array<Dyn>");
			if ( javaClass == null ) {
				ee.error(this.getFirstSymbol(), "Error creating an object of '" + arrayTypeStr +
						"'. This type must exist before this code is interpreted");
			}
			else {
				try {
					final java.lang.reflect.Constructor<?> constructor = javaClass.getConstructor();
					final Object newArray = constructor.newInstance();
					/* java.lang.reflect.Method addMethod = newArray.getClass().getMethod(NameServer.javaNameAddMethod,
							elemClass); */
					for ( final Object elem : array ) {
						Statement.sendMessage(newArray, NameServer.javaNameAddMethod, "add",
								new Object [] { elem }, "add:", this.getFirstSymbol().getI(), ee
								 );

						// addMethod.invoke(newArray, elem);
					}
					return newArray;
				}
				catch (NoSuchMethodException | SecurityException | InstantiationException |
						IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					ee.error(this.getFirstSymbol(), "Error creating an object of '" + arrayTypeStr +
							"'. Probably method 'add' was not found");
				}

			}
		}
		else {
			try {
				final java.lang.reflect.Constructor<?> constructor = javaClass.getConstructor();
				final Object newArray = constructor.newInstance();
				/* java.lang.reflect.Method addMethod = newArray.getClass().getMethod(NameServer.javaNameAddMethod,
						elemClass); */
				for ( final Object elem : array ) {
					Statement.sendMessage(newArray, NameServer.javaNameAddMethod, "add", new Object [] { elem },
							"add:", this.getFirstSymbol().getI(), ee
					    );

					// addMethod.invoke(newArray, elem);
				}
				return newArray;
			}
			catch (NoSuchMethodException | SecurityException | InstantiationException |
					IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				ee.error(this.getFirstSymbol(), "Error creating an object of '" + realArrayTypeStr +
						"'. Probably method 'add' was not found");
			}

		}

//		final JVMPackage aPackage = ee.getJvmPackageMap().get("cyan.lang");
//		if ( aPackage != null ) {
//			final TypeJavaRef javaRef = aPackage.searchJVMClass(MetaHelper.getJavaName(arrayTypeStr));
//			if ( javaRef != null ) {
//				javaClass = javaRef.getaClass(ee.env, this.getFirstSymbol());
//				try {
//					final java.lang.reflect.Constructor<?> constructor = javaClass.getConstructor();
//					final Object newArray = constructor.newInstance();
//					/* java.lang.reflect.Method addMethod = newArray.getClass().getMethod(NameServer.javaNameAddMethod,
//							elemClass); */
//					for ( final Object elem : array ) {
//						Statement.sendMessage(newArray, NameServer.javaNameAddMethod, "add", new Object [] { elem },
//								() -> {
//									ee.error(this.getFirstSymbol(), "Error creating an object of '" + arrayTypeStr +
//											"'. This type must exist before this code is interpreted");
//								} );
//
//						// addMethod.invoke(newArray, elem);
//					}
//					return newArray;
//				}
//				catch (NoSuchMethodException | SecurityException | InstantiationException |
//						IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//					ee.error(this.getFirstSymbol(), "Error creating an object of '" + arrayTypeStr +
//							"'. Probably method 'add' was not found");
//				}
//			}
//		}
//		ee.error(this.getFirstSymbol(), "Error creating an object of '" + arrayTypeStr +
//				"'. This type must exist before this code is interpreted");
		return null;
	}

	/**
	 * symbols representing [ and ]
	 */
	private Symbol startSymbol, endSymbol;
	private List<Expr> exprList;
	/**
	 * the type of the array elements, which is the type of the first array element
	 */
	private Type arrayElementType;
}
