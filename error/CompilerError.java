package error;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import lexer.Lexer;

public class CompilerError {

    public CompilerError( String filename, PrintWriter out ) {
          // output of an error is done in out
    	this.filename = filename;
        this.out = out;
        thereWasAnError = false;
        errorMessageList = new ArrayList<>();
    }

    public void setLexer( Lexer lexer ) {
        this.lexer = lexer;
    }

    public boolean wasAnErrorSignalled() {
        return thereWasAnError;
    }

    public void show( String strMessage ) {
        show( strMessage, false );
    }

    public void show( String strMessage, boolean goPreviousToken ) {
        // is goPreviousToken is true, the error is signalled at the line of the
        // previous token, not the last one.
        if ( goPreviousToken ) {
          out.println("File " + filename + " error at line " + lexer.getLineNumberBeforeLastToken() + ": ");
          out.println( lexer.getLineBeforeLastToken() );
        }
        else {
          out.println("File " + filename + " error at line " + lexer.getLineNumber() + ": ");
          out.println(lexer.getCurrentLine());
        }

        out.println( strMessage );
        out.flush();
        thereWasAnError = true;
    }


	public void showErrorFile(String string) {

		out.println(string);

	}



    public void signal( String strMessage ) {
        show( strMessage );
        out.flush();
        thereWasAnError = true;
        throw new RuntimeException();
    }

    static public void crash(String message) {
    	System.out.println(message);
    	System.exit(1);
    }

    public static List<String> getErrorMessageList() {
		return errorMessageList;
	}

	public static void addErrorMessage(String errorMessage) {
		errorMessageList.add(errorMessage);
	}

	public static void clearErrorMessage() {
		errorMessageList.clear();
	}

	private Lexer lexer;
    private PrintWriter out;
    private boolean thereWasAnError;
    private String filename;

    /**
     * list of error messages that are not associated to a particular piece of Cyan code.
     */
    private static List<String> errorMessageList;
}

