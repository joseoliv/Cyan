package error;

/**
 * Represents an error in a compilation unit (object or interface declaration)
 */
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import ast.Annotation;
import ast.CompilationUnit;
import ast.CompilationUnitSuper;
import lexer.Symbol;
import meta.WrUnitError;
import saci.MyFile;

public class UnitError implements Comparable<UnitError> {


	public UnitError( Symbol symbol, String objectInterfaceName, String filename,
			          String line, String message,
			          int lineNumber, int columnNumber, CompilationUnitSuper compilationUnit) {
		MyFile.write(compilationUnit);
		this.symbol = symbol;
		this.objectInterfaceName = objectInterfaceName;
		this.filename = filename;
		this.line = line;
		this.message = message;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
 		this.compilationUnit = compilationUnit;
 		if ( staticErrorList == null ) {
 			staticErrorList = new ArrayList<>();
 			staticErrorList.add(this);
 		}
 		if ( compilationUnit instanceof CompilationUnit ) {
 			CompilationUnit cunit = (CompilationUnit ) compilationUnit;
 			if ( cunit.getCyanPackage() != null ) {
 	 			Annotation annot = cunit.getCyanPackage().searchAnnotationCreatedPrototype(objectInterfaceName);
 	 			if ( annot != null ) {
 	 				this.message += ". Prototype '" + objectInterfaceName + "' was created by the metaobject associated with annotation '" +
 	 						annot.getCyanMetaobject().getName() + "' of line " + annot.getFirstSymbol().getLineNumber() +
 	 						" of file " + annot.getFirstSymbol().getCompilationUnit().getFullFileNamePath();
 	 			}
 			}
 		}
	}

	public WrUnitError getI() {
		return new WrUnitError(this);
	}

	@Override
	public int compareTo(UnitError other) {

		if ( lineNumber < other.lineNumber )
			return -1;
		else if ( lineNumber > other.lineNumber )
			return 1;
		else if ( columnNumber < other.columnNumber )
			return -1;
		else if ( columnNumber > other.columnNumber )
			return 1;
		else
			return 0;
	}

	public void print( PrintWriter printWriter ) {
		if ( filename != null )
			printWriter.println("In file " + filename + " (line " + lineNumber +
					 " column " + columnNumber + ") ");
		if ( objectInterfaceName != null )
			printWriter.println("object/interface " + objectInterfaceName);
		printWriter.println(message);
		if ( line != null )
		     printWriter.println(line);
	}

	public String asString() {
		StringBuffer s = new StringBuffer();
		if ( filename != null )
			s.append("In file " + filename + " (line " + lineNumber +
					 " column " + columnNumber + ") ");
		if ( objectInterfaceName != null )
			s.append("object/interface '" + objectInterfaceName + "' ");
		s.append(message);
		if ( line != null )
			s.append(line);
		return s.toString();
	}

	public void setobjectInterfaceName(String objectInterfaceName) {
		this.objectInterfaceName = objectInterfaceName;
	}
	public String getobjectInterfaceName() {
		return objectInterfaceName;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getLine() {
		return line;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public CompilationUnitSuper getCompilationUnit() {
		return compilationUnit;
	}
	public Object getFilename() {
		return filename;
	}


	private String objectInterfaceName, filename, line, message;
	private int lineNumber, columnNumber;
	private Symbol	symbol;
	private CompilationUnitSuper	compilationUnit;

	static private List<UnitError> staticErrorList;
	static boolean wasStaticErrorListPrinted = false;

	public static boolean wasStaticErrorListPrinted() {
		return wasStaticErrorListPrinted;
	}

	public static void initStaticErrorList() {
		staticErrorList = null;
		wasStaticErrorListPrinted = false;
	}
	public static List<UnitError> getStaticErrorList() {
		return staticErrorList;
	}
	public static boolean isThereWasError() {
		return staticErrorList != null;
	}

    public static void printStaticErrorList(PrintWriter printWriter) {

    	wasStaticErrorListPrinted = true;
    	if ( staticErrorList != null ) {
    		for ( final UnitError unitError : staticErrorList ) {
    			unitError.print(printWriter);
    			printWriter.println("");
    			// System.out.println();
    		}
    	}
    }

}



