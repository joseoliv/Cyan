package meta;

import ast.Annotation;

public class SourceCodeChangeDeleteText extends SourceCodeChangeByAnnotation {


	public SourceCodeChangeDeleteText(int offset, int numCharToDelete, Annotation cyanAnnotation) {
		super(offset, cyanAnnotation);
		this.numCharToDelete = numCharToDelete;
	}
	
	@Override
	public int getSizeToAdd() {
		return 0;
	}
	
	@Override
	public int getSizeToDelete() {
		return this.numCharToDelete;
	}
	
	/**
	 * number of characters to delete in the text from position <code>offset</code>
	 */
	private int	numCharToDelete;

}
