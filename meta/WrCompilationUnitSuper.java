package meta;

import java.util.ArrayList;
import java.util.List;
import ast.CompilationUnitSuper;
import error.UnitError;

public class WrCompilationUnitSuper extends WrASTNode {

	CompilationUnitSuper hidden;

	public WrCompilationUnitSuper(CompilationUnitSuper hidden) {
		this.hidden = hidden;
	}


	@Override
	CompilationUnitSuper getHidden() {
		return hidden;
	}

	public String getFullFileNamePath() {
		return hidden.getFullFileNamePath();
	}


	public char[] getOriginalText() {
		return hidden.getOriginalText();
	}



	public List<WrUnitError> getErrorList() {
		List<UnitError> errorList = hidden.getErrorList();
		if ( errorList == null ) {
			return null;
		}
		else {
			List<WrUnitError> wrErrorList = new ArrayList<>();
			for ( UnitError unitError : errorList ) {
				wrErrorList.add(unitError.getI());
			}
			return wrErrorList;

		}
	}


	public WrSymbol[] getSymbolList() {
		return hidden.getWrSymbolList();
	}

	public int getSizeSymbolList() {
		return hidden.getSizeSymbolList();
	}


}
