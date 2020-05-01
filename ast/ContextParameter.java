/**
 *
 */
package ast;

import java.util.List;
import java.util.Stack;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.WrContextParameter;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple5;
import meta.VariableKind;
import saci.CyanEnv;

/** represents a context parameter such s in
 *
 * object Sum( Int &s)
 *     public fun eval: Int elem [
 *         s = s + elem
 *     ]
 * end
 *
 * If s is a value parameter, as in
 *
 * object F(Float %y)
 *    public fun eval: Float f1 -> float [
 *            ^ Math expr: f1, y;
 *    ]
 * end

  then variable VariableKind is value. Otherwise it is ref.

  Context parameters are just regular fields that may be declared
  with types &T, %T, and *T

 * @author José
 *
 */
public class ContextParameter extends FieldDec  {

	public ContextParameter(ObjectDec currentObj, SymbolIdent variableSymbol, VariableKind variableKind, Expr typeInDec, Token visibility,
			Symbol firstSymbol, List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList,
			Stack<Tuple5<String, String, String, String, Integer>> annotContextStack) {

		super(currentObj, variableSymbol, typeInDec, null, visibility, false,
				nonAttachedAnnotationList, attachedAnnotationList,
				firstSymbol, false, annotContextStack);
		setVariableKind(variableKind);
		if ( variableKind != VariableKind.COPY_VAR )
			this.setRefType(true);
	}


	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}



	@Override
	public WrContextParameter getI() {
		if ( iFieldDec == null ) {
			iFieldDec = new WrContextParameter(this);
		}
		return (WrContextParameter ) iFieldDec;
	}

	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		Token visible = this.getVisibility();
		if ( visible != Token.PRIVATE ) {
			if ( visible == Token.PUBLIC )
				pw.print("public ");
			else if ( visible == Token.PROTECTED )
				pw.print("protected ");
		}
		/*else {
			pw.print("var ");
		} */

		if ( typeInDec != null )
			typeInDec.genCyan(pw, false, cyanEnv, genFunctions);
		else {
			   // used only in inner objects
			String name = type.getFullName();
			int indexOfCyanLang = name.indexOf(MetaHelper.cyanLanguagePackageName);
			if ( indexOfCyanLang >= 0 )
				name = name.substring(indexOfCyanLang);
			pw.print(name);
		}


		pw.print(" ");
		if ( this.getVariableKind() == VariableKind.LOCAL_VARIABLE_REF )
			pw.print(getVariableKind().toString());
		pw.print(getName());
	}



	@Override
	public boolean isContextParameter() {
		return true;
	}



	/*
	public boolean getDoNotCreateField() {
		return doNotCreateField;
	}


	public void setDoNotCreateField(boolean doNotCreateField) {
		this.doNotCreateField = doNotCreateField;
	}
	*/

	/**
	 * if true, use this context parameters in the 'init' method but a field should not
	 * be created for it
	 */
	// private boolean doNotCreateField = false;

}
