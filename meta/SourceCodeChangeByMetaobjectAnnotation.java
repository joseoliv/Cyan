package meta;

import ast.Annotation;


abstract public class SourceCodeChangeByMetaobjectAnnotation implements Comparable<SourceCodeChangeByMetaobjectAnnotation>   {
	
    public int offset; 
    private Annotation cyanMetaobjectAnnotation;
	
	public SourceCodeChangeByMetaobjectAnnotation(int offset, Annotation cyanMetaobjectAnnotation) {
		this.offset = offset;
		this.cyanMetaobjectAnnotation = cyanMetaobjectAnnotation;
	}

	@Override
	public int compareTo(SourceCodeChangeByMetaobjectAnnotation other) {

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

	public Annotation getCyanMetaobjectAnnotation() {
		return cyanMetaobjectAnnotation;
	}
}