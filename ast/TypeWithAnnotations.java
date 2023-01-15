package ast;

import java.util.List;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._IActionAttachedType__semAn;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.IActionAttachedType_semAn;
import meta.WrType;
import meta.WrTypeWithAnnotations;
import saci.Env;
import saci.NameServer;

public class TypeWithAnnotations extends Type {

	public TypeWithAnnotations(Type insideType, List<AnnotationAt> annotationToTypeList) {
		this.insideType = insideType;
		this.annotationToTypeList = annotationToTypeList;
	}


	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	public void checkAnnotation(Env env) {


		for ( AnnotationAt annot : annotationToTypeList ) {
			annot.setTypeAttached(insideType);

			try {
				CyanMetaobject metaobject =  annot.getCyanMetaobject();
				_CyanMetaobject  other = metaobject.getMetaobjectInCyan();
				if ( other == null ) {
					((IActionAttachedType_semAn ) metaobject).checkAnnotation();
				}
				else {
					((_IActionAttachedType__semAn ) other)._checkAnnotation();
				}
			}
			catch ( error.CompileErrorException e ) {
			}
			catch ( NoClassDefFoundError e ) {
				env.error(annot.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
			}
			catch ( RuntimeException e ) {
				env.thrownException(annot, annot.getFirstSymbol(), e);
			}
			finally {
				env.errorInMetaobjectCatchExceptions(annot.getCyanMetaobject());
			}
		}
	}


	@Override
	public String getNameWithAttachedTypes() {
		String ret = getName();
		if ( annotationToTypeList != null ) {
			for ( AnnotationAt annotType : annotationToTypeList ) {
				ret += annotType.asString();
			}
		}
		return ret;
	}

	@Override
	public java.lang.String getName() {
		return insideType.getName();
	}

	@Override
	public String getPackageName() {
		return insideType.getPackageName();
	}


	@Override
	public java.lang.String getFullName() {
		return insideType.getFullName();
	}

	@Override
	public java.lang.String getFullName(Env env) {
		return insideType.getFullName(env);
	}

	@Override
	public java.lang.String getJavaName() {
		return insideType.getJavaName();
	}

	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		return insideType.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodName, env);
	}


	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackage(String methodName, Env env) {
		return this.insideType.searchMethodProtectedPublicPackage(methodName, env);
	}


	@Override
	public List<MethodSignature> searchMethodProtected(String methodName, Env env) {
		return this.insideType.searchMethodProtected(methodName, env);
	}

	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackage(String methodName, Env env) {
		return insideType.searchMethodPrivateProtectedPublicPackage(methodName, env);
	}

	@Override
	public List<MethodSignature> searchMethodPublicPackageSuperPublicPackage(
			java.lang.String methodName, Env env) {
		return insideType.searchMethodPublicPackageSuperPublicPackage(methodName, env);
	}

	@Override
	public List<MethodSignature> searchMethodPublicPackage(String methodName, Env env) {
		return insideType.searchMethodPublicPackage(methodName, env);
	}


	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		return insideType.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(methodName, env);
	}

	@Override
	public boolean getIsFinal() {
		return insideType.getIsFinal();
	}

	@Override
	public boolean isSupertypeOf(Type other, Env env) {
		return insideType.isSupertypeOf(other, env);
	}

	@Override
	public Type getInsideType() {
		return insideType;
	}

	public void setInsideType(Type insideType) {
		this.insideType = insideType;
	}

	public List<AnnotationAt> getAnnotationToTypeList() {
		return annotationToTypeList;
	}

	public void setAnnotationToTypeList(
			List<AnnotationAt> annotationList) {
		this.annotationToTypeList = annotationList;
	}

	public AttachedDeclarationKind getKind() {
		return AttachedDeclarationKind.PROTOTYPE_DEC;
	}


	/**
	 * a list of metaobject annotations that is attached to this generic prototype instantiation
	 */
	private List<AnnotationAt> annotationToTypeList;

	/**
	 * this class is a wrapper for types. The type wrapped is insideType.
	 */
	private Type insideType;

	@Override
	public WrType getI() {
		if ( iTypeWithAnnotations == null ) {
			iTypeWithAnnotations = new WrTypeWithAnnotations(this);
		}
		return iTypeWithAnnotations;
	}

	private WrTypeWithAnnotations iTypeWithAnnotations = null;
}
