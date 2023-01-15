package meta;

import ast.CompilationUnitSuper;
import ast.Annotation;
import lexer.CompilerPhase;
import saci.Env;

public class SourceCodeChangeShiftPhase extends SourceCodeChangeByAnnotation {


	public SourceCodeChangeShiftPhase(char[] text, Env env, int offset, CompilerPhase phase,
			CompilationUnitSuper compilationUnit, Annotation cyanAnnotation) {
		super(offset, cyanAnnotation);
		this.compilationUnit = compilationUnit;
		this.sizeToDelete = 0;
		calcSizes(text, env, phase, offset);
	}

	public int getOffsetPhase() {
		return offsetPhase;
	}

	@Override
	public int getSizeToDelete() {
		return sizeToDelete;
	}

	@Override
	public int getSizeToAdd() {
		return sizeToAdd;
	}

	private void calcSizes(char[] text, Env env, CompilerPhase phase, int offsetAnnotation) {

		if ( text[offsetAnnotation]  != '@' ) {
			env.error(null, "'@' expected at position " + offsetAnnotation + " of file '" + compilationUnit.getFilename() + "'", true, true );
			return ;
		}
		else {
			int k = offsetAnnotation;
			++k;
			offsetPhase = -1;
			whatToAdd = new StringBuffer("@");
			while ( Character.isLetter(text[k]) || text[k] == '_' || text[k] == '#' || Character.isDigit(text[k])) {
				if ( text[k] == '#' ) {
					if ( offsetPhase >= 0 )
						env.error(null, "Internal error in class 'SourceCodeChangeShiftPhase': two characters '#' in a metaobject annotation", true, true);
					offsetPhase = k+1;
				}
				if ( offsetPhase < 0 )
					whatToAdd.append(text[k]);
				k++;
			}
			sizeToDelete = k - offsetAnnotation;
			/*
			if ( offsetPhase >= 0 ) {
				// found a suffix such as in "@init#afterResTypes(name)". Then characteres will be deleted before adding
				// characters for the new phase
				sizeToAdd = sizeToDelete;
			}
			else {
				sizeToAdd = sizeToDelete + phase.getName().length() + 1;
			}
			*/
			whatToAdd.append("#" + phase.getName());
			sizeToAdd = whatToAdd.length();
		}
	}

	@Override
	public StringBuffer getTextToAdd() {
		return whatToAdd;
	}


	private CompilationUnitSuper compilationUnit;
	/**
	 * the exact offset in which the change will be made. field <code>offset</code>
	 * points to the beginning of the metaobject annotation. It would be '@' in "@init(name)".
	 * offsetPhase would point to '('.
	 */
	private int offsetPhase;
	/**
	 * number of characters to delete before inserting any text
	 */
	private int	sizeToDelete;
	/**
	 * number of characters that will be added to the text.
	 * The text will be greater by <code>sizeToAdd - sizeToDelete</code> characters (or smaller).
	 */
	private int sizeToAdd;
	/**
	 * text to be added in the source code
	 */
	private StringBuffer whatToAdd;

}

