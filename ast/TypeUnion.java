package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.WrType;
import meta.WrTypeUnion;
import saci.Env;
import saci.NameServer;

public class TypeUnion extends Type {

	public TypeUnion(ExprTypeUnion exprTypeUnion) {
		this.exprTypeUnion = exprTypeUnion;
		typeList = new ArrayList<>();
	}

	public void addType(Type t) {
		typeList.add(t);
	}


	@Override
	public void accept(ASTVisitor visitor) {
		for ( Type t : typeList ) {
			t.accept(visitor);
		}
	}

	@Override
	public java.lang.String getName() {
		String s = "";
		int size = typeList.size();
		for ( Type t : typeList ) {
			s += t.getName();
			if ( --size > 0 ) {
				s += "|";
			}
		}
		return s;
	}

	@Override
	public java.lang.String getPackageName() {
		return NameServer.cyanLanguagePackageName;
	}

	@Override
	public java.lang.String getFullName() {
		String s = "";
		int size = typeList.size();
		for ( Type t : typeList ) {
			if ( t instanceof TypeIntersection ) {
				s += "(" + t.getFullName() + ")";
			}
			else {
				s += t.getFullName();
			}
			if ( --size > 0 ) {
				s += "|";
			}
		}
		return s;
	}

	@Override
	public java.lang.String getFullName(Env env) {
		return getFullName();
	}

	@Override
	public java.lang.String getJavaName() {
		return "Object";
	}

	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		if ( methodName.equals("==") || methodName.equals("!=") ) {
			return Type.Any.searchMethodPublicPackage(methodName, env);
		}
		else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		if ( methodName.equals("==") || methodName.equals("!=") ) {
			return Type.Any.searchMethodPublicPackage(methodName, env);
		}
		else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<MethodSignature> searchMethodProtected(String methodName, Env env) {
		return new ArrayList<>();
	}


	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		if ( methodName.equals("==") || methodName.equals("!=") ) {
			return Type.Any.searchMethodPublicPackage(methodName, env);
		}
		else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<MethodSignature> searchMethodPublicPackageSuperPublicPackage(
			java.lang.String methodName, Env env) {
		if ( methodName.equals("==1") || methodName.equals("!=1") ) {
			return Type.Any.searchMethodPublicPackage(methodName, env);
		}
		else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<MethodSignature> searchMethodPublicPackage(
			java.lang.String methodName, Env env) {
		if ( methodName.equals("==") || methodName.equals("!=") ) {
			return Type.Any.searchMethodPublicPackage(methodName, env);
		}
		else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		if ( methodName.equals("==") || methodName.equals("!=") ) {
			return Type.Any.searchMethodPublicPackage(methodName, env);
		}
		else {
			return new ArrayList<>();
		}
	}

	@Override
	public boolean getIsFinal() {
		return true;
	}

	@Override
	public boolean isSupertypeOf(Type other, Env env) {
		if ( !(other instanceof TypeUnion) ) {
			for ( Type t : typeList ) {
				if ( t.isSupertypeOf(other, env) ) {
					return true;
				}
			}
			return false;
		}
		else if ( this == other ) { return true; }
		else {
			// two unions
			TypeUnion otherUnion = (TypeUnion ) other;
			/*
			 * every type of the 'other' union should be a subtype of some
			 * type of the 'this' union
			 */
			for ( Type elemOther : otherUnion.typeList ) {
				boolean foundSupertype = false;
				for ( Type elemThis : typeList ) {
					if ( elemThis.isSupertypeOf(elemOther, env) ) {
						foundSupertype = true;
						break;
					}
				}
				if ( !foundSupertype ) {
					return false;
				}
			}
			return true;
		}
	}


	@Override
	public WrType getI() {
		if ( iTypeUnion == null ) {
			iTypeUnion = new WrTypeUnion(this);
		}
		return iTypeUnion;
	}


	public WrTypeUnion getiTypeUnion() {
		return iTypeUnion;
	}

	public void setiTypeUnion(WrTypeUnion iTypeUnion) {
		this.iTypeUnion = iTypeUnion;
	}

	public List<Type> getTypeList() {
		return typeList;
	}

	@Override
	public Expr asExpr(Symbol sym) {
		return exprTypeUnion;
	}

	private WrTypeUnion iTypeUnion;

	/**
	 * list of type in the union, in the order they appear
	 */
	private List<Type> typeList;

	private ExprTypeUnion exprTypeUnion;
}
