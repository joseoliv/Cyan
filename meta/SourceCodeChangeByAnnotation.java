package meta;

import ast.Annotation;


abstract public class SourceCodeChangeByAnnotation implements Comparable<SourceCodeChangeByAnnotation>   {
	
    public int offset; 
    private Annotation cyanAnnotation;
	
	public SourceCodeChangeByAnnotation(int offset, Annotation cyanAnnotation) {
		this.offset = offset;
		this.cyanAnnotation = cyanAnnotation;
	}

	@Override
	public int compareTo(SourceCodeChangeByAnnotation other) {

		if ( offset < other.offset )
			return -1;
		else if ( offset > other.offset )
			return 1;
		else
			return 0;
	}
	
	public StringBuffer getTextToAdd() { return null; }

	/**
	 * number of characters that this change will add in <code>text</code> at 
	 * position <code>offset</code>  (parameter to the constructor). If the number
	 * returned by getSizeToDelete is equal to getSizeToAdd then the text
	 * remains with the same size.
	 * 
	   @param text
	   @return the number of characters that will be added
	 */
    abstract public int getSizeToAdd();

	/**
	 * 
	   @return the number of characters that should be deleted from position <code>offset</code> 
	   (parameter to the constructor) before any text is added 
	 */
	public int getSizeToDelete() {
		return 0;
	}

	public Annotation getCyanAnnotation() {
		return cyanAnnotation;
	}
}