package meta;

import ast.Annotation;

/**
 * This class is a superclass of all classes that represent changes in the source code caused by 
 * metaobject annotations
   @author José
 */
public class SourceCodeChangeAddText extends SourceCodeChangeByMetaobjectAnnotation {
	
	public SourceCodeChangeAddText(int offset, StringBuffer textToAdd, Annotation cyanMetaobjectAnnotation) {
		super(offset, cyanMetaobjectAnnotation);
		this.textToAdd = textToAdd;
		
	}

	@Override
	public int getSizeToAdd() {
		return textToAdd.length();
	}
	
	@Override
	public StringBuffer getTextToAdd() {
		return textToAdd;
	}
	
    public StringBuffer textToAdd;
    
}