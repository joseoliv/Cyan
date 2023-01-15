package ast;

import java.util.List;
import lexer.Symbol;
import meta.WrExpr;
import meta.WrExprTypeUnion;
import saci.CyanEnv;
import saci.Env;
import saci.TupleTwo;

public class ExprTypeUnion extends Expr {


	public ExprTypeUnion(MethodDec currentMethod, List<Expr> typeList) {
		super(currentMethod);
		this.typeList = typeList;
	}

	@Override
	public void calcInternalTypes(Env env) {

		TypeUnion t = new TypeUnion(this);
		int partialSize = 0;
		int sizeLessOne = typeList.size() - 1;
		for ( Expr e : this.typeList ) {
			try {
				env.setSemAnInUnionType(true);
				e.calcInternalTypes(env);
			}
			finally {
				env.setSemAnInUnionType(false);
			}
			Type etype = e.getType();
			t.addType(etype);
			for ( int i = 0; i < partialSize; ++i ) {
				Type ithType = t.getTypeList().get(i);
				if ( ithType == etype ) {
					env.error(e.getFirstSymbol(), "Every type can only appear one time in an union type. '" +
				       etype.getFullName() + "' is appearing two times");
				}
				else if ( ithType.isSupertypeOf(etype, env) && etype != Type.Dyn ) {
					env.error(e.getFirstSymbol(), "Type '" + ithType.getFullName() + "' appear before type "
							+ "'" + etype.getFullName() + "' in this union type although the former a supertype"
									+ " of the later. This is illegal");
				}

			}
			if ( etype == Type.Dyn && partialSize != sizeLessOne ) {
				// Dyn can only appear in the last position
				env.error(e.getFirstSymbol(), "'Dyn' can only appear in the last position of an union type");
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

	private WrExprTypeUnion iExprTypeUnion = null;

	@Override
	public WrExpr getI() {
		if ( iExprTypeUnion == null ) {
			iExprTypeUnion = new WrExprTypeUnion(this);
		}
		return iExprTypeUnion;
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
				pw.print("|");
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
				s += "|";
			}
		}
		return s;
	}

}
