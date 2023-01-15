package ast;

import java.util.List;
import lexer.Symbol;
import meta.WrExpr;
import meta.WrExprTypeIntersection;
import saci.CyanEnv;
import saci.Env;
import saci.TupleTwo;

public class ExprTypeIntersection extends Expr {


	public ExprTypeIntersection(MethodDec currentMethod, List<Expr> typeList) {
		super(currentMethod);
		this.typeList = typeList;
	}


	@Override
	public void calcInternalTypes(Env env) {

		TypeIntersection t = new TypeIntersection(this);
		int partialSize = 0;
		for ( Expr e : this.typeList ) {
			e.calcInternalTypes(env);
			Type etype = e.getType();
			t.addType(etype);
			if ( ! (etype instanceof InterfaceDec) ) {
				boolean issueError = true;
				if ( etype instanceof TypeUnion ) {
					issueError = false;
					TypeUnion utype = (TypeUnion ) etype;
					for ( Type ut : utype.getTypeList() ) {
						if ( !(ut instanceof InterfaceDec) && !(ut instanceof TypeIntersection) ) {
							env.error(e.getFirstSymbol(), "Only interfaces or unions of interfaces can be used in intersection types. " +
								    "Type '" + ut.getFullName() + "' of the union '" +
									utype.getFullName() + "' is not an interface");
							issueError = true;
							break;
						}
					}
				}
				if ( issueError ) {
					env.error(e.getFirstSymbol(), "Only interfaces or unions of interfaces can be used in intersection types. '" +
						      etype.getFullName() + "' is not an interface");
				}
			}
			for ( int i = 0; i < partialSize; ++i ) {
				Type ithType = t.getTypeList().get(i);
				if ( ithType == etype ) {
					env.error(e.getFirstSymbol(), "Every type can only appear one time in an intersection type. '" +
				       etype.getFullName() + "' is appearing two times");
				}
				else if ( ithType.isSupertypeOf(etype, env) || etype.isSupertypeOf(ithType, env)) {
					env.error(e.getFirstSymbol(), "Type '" + ithType.getFullName() + "' is subtype or supertype of"
							+ "'" + etype.getFullName() + "'. This is illegal, use only the subtype");
				}

			}
			++partialSize;
		}
		type = t;
		super.calcInternalTypes(env);
	}

	@Override
	public String getJavaName() {
		return "Object";
	}

	@Override
	public TupleTwo<String, Type> ifPrototypeReturnsNameWithPackageAndType(Env env) {
		this.getType(env);
		return new TupleTwo<String, Type>(type.getFullName(), type);
	}


	public void setTypeList(List<Expr> typeList) {
		this.typeList = typeList;
	}

	private WrExprTypeIntersection iExprTypeIntersection = null;

	@Override
	public WrExpr getI() {
		if ( iExprTypeIntersection == null ) {
			iExprTypeIntersection = new WrExprTypeIntersection(this);
		}
		return iExprTypeIntersection;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		for ( Expr e : this.typeList ) {
			e.accept(visitor);
		}
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		return "Object";
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
		int size = typeList.size();
		for ( Expr e : typeList ) {
			e.genCyanReal(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			if ( --size > 0 ) {
				pw.print(" & ");
			}
		}
	}

	@Override
	public Symbol getFirstSymbol() {
		return typeList.get(0).getFirstSymbol();
	}

	public List<Expr> getTypeList() {
		return typeList;
	}

	private List<Expr> typeList;

	public String getName() {
		String s = "";
		int size = typeList.size();
		for ( Expr e : typeList ) {
			s += e.ifPrototypeReturnsItsName();
			if ( --size > 0 ) {
				s += " & ";
			}
		}
		return s;
	}

}
