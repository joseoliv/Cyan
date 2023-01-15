package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.WrType;
import meta.WrTypeIntersection;
import saci.Env;
import saci.NameServer;

public class TypeIntersection extends Type {

	public TypeIntersection(ExprTypeIntersection exprTypeIntersection) {
		this.exprTypeIntersection = exprTypeIntersection;
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
				s += " & ";
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
			if ( t instanceof TypeUnion ) {
				s += "(" + t.getFullName() + ")";
			}
			else {
				s += t.getFullName();
			}
			if ( --size > 0 ) {
				s += " & ";
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

		for ( Type t : this.typeList ) {
			List<MethodSignature> ms = t.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodName, env);
			if ( ms != null && ms.size() > 0 ) {
				return ms;
			}
		}
		return new ArrayList<>();
	}

	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		for ( Type t : this.typeList ) {
			List<MethodSignature> ms = t.searchMethodProtectedPublicPackage(methodName, env);
			if ( ms != null && ms.size() > 0 ) {
				return ms;
			}
		}
		return new ArrayList<>();
	}

	@Override
	public List<MethodSignature> searchMethodProtected(String methodName, Env env) {
		return new ArrayList<>();
	}


	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		for ( Type t : this.typeList ) {
			List<MethodSignature> ms = t.searchMethodPrivateProtectedPublicPackage(methodName, env);
			if ( ms != null && ms.size() > 0 ) {
				return ms;
			}
		}
		return new ArrayList<>();
	}

	@Override
	public List<MethodSignature> searchMethodPublicPackageSuperPublicPackage(
			java.lang.String methodName, Env env) {

		for ( Type t : this.typeList ) {
			List<MethodSignature> ms = t.searchMethodPublicPackageSuperPublicPackage(methodName, env);
			if ( ms != null && ms.size() > 0 ) {
				return ms;
			}
		}
		return new ArrayList<>();
	}

	@Override
	public List<MethodSignature> searchMethodPublicPackage(
			java.lang.String methodName, Env env) {

		for ( Type t : this.typeList ) {
			List<MethodSignature> ms = t.searchMethodPublicPackage(methodName, env);
			if ( ms != null && ms.size() > 0 ) {
				return ms;
			}
		}
		return new ArrayList<>();
	}

	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
			java.lang.String methodName, Env env) {

		for ( Type t : this.typeList ) {
			List<MethodSignature> ms = t.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(methodName, env);
			if ( ms != null && ms.size() > 0 ) {
				return ms;
			}
		}
		return new ArrayList<>();
	}

	@Override
	public boolean getIsFinal() {
		return true;
	}

	@Override
	public boolean isSupertypeOf(Type other, Env env) {
		//    other <: S1 and other <: S2, then other <: S1 & S2

		if ( this == other ) { return true; }
		else if ( !(other instanceof TypeIntersection) ) {
			// if other is subtype of every Ti, then other is subtype of T1&...&Tn
			for ( Type t : typeList ) {
				if ( !t.isSupertypeOf(other, env) ) {
					return false;
				}
			}
		}
		else {
			// two unions
			TypeIntersection otherIntersection = (TypeIntersection ) other;
			/*
			 * if, for every type tT of 'this' there is a type uU of
			 * 'other' such that tT is supertype of uU, then other <: this
			 */
			for ( Type tT : this.typeList ) {
				boolean isSuper = false;
				for ( Type uU : otherIntersection.typeList ) {
					if ( tT.isSupertypeOf(uU, env) ) {
						isSuper = true;
						break;
					}
				}
				if ( ! isSuper ) {
					return false;
				}
			}
		}
		return true;

	}


	@Override
	public WrType getI() {
		if ( iTypeIntersection == null ) {
			iTypeIntersection = new WrTypeIntersection(this);
		}
		return iTypeIntersection;
	}


	public WrTypeIntersection getiTypeIntersection() {
		return iTypeIntersection;
	}

	public void setiTypeIntersection(WrTypeIntersection iTypeIntersection) {
		this.iTypeIntersection = iTypeIntersection;
	}

	public List<Type> getTypeList() {
		return typeList;
	}

	@Override
	public Expr asExpr(Symbol sym) {
		return exprTypeIntersection;
	}

	private WrTypeIntersection iTypeIntersection;

	/**
	 * list of type in the intersection, in the order they appear
	 */
	private List<Type> typeList;

	private ExprTypeIntersection exprTypeIntersection;
}
