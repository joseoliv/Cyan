package saci;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import ast.CyanPackage;
import ast.ExprIdentStar;
import ast.Program;
import ast.ReturnValueEvalEnvException;
import ast.StatementImport;
import cyan.lang.CyString;
import cyan.lang._ExceptionStr;
import cyanruntime.ExceptionContainer__;
import error.CompileErrorException;
import meta.ICompiler_parsing;
import meta.InterpretationErrorException;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.WrEvalEnv;
import meta.WrExprIdentStar;
import meta.WrStatement;
import meta.WrSymbol;

public class CyanCodeSnippet {

    // private List<UnitError> errorList;
    // private List<ProjectError> projectErrorList;
    private Program program;
    private Project project;

    /**
     * This method may throw exception ExceptionContainer__
     */
    public Object evalCyanCode(String cyanCode) {
        return evalCyanCode(cyanCode, new Object(), null, null, null);
    }

    /**
     * This method may throw exception ExceptionContainer__
     */
    public Object evalCyanCode(String cyanCode, Object selfObject) {
        return evalCyanCode(cyanCode, selfObject, null, null, null);
    }

    /**
     * This method may throw exception ExceptionContainer__
     */
    public Object evalCyanCode(String cyanCode, Object selfObject, String[] varNameList, Object[] objList) {
        return this.evalCyanCode(cyanCode, selfObject, null, varNameList, objList);
    }

    /**
     * This method may throw exception ExceptionContainer__
     */
    public Object evalCyanCode(String cyanCode, Object selfObject, String varName, Object value) {
        return this.evalCyanCode(cyanCode, selfObject, null, new String[] { varName }, new Object[] { value });
    }

    /**
     * This method may throw exception ExceptionContainer__
     */
    public Object evalCyanCode(String cyanCode, Object selfObject, String cyanLangDir_param, String[] varNameList,
            Object[] objList) {

        this.cyanLangDir = cyanLangDir_param;

        String errMsg = setCyanLangDir();
        if ( errMsg != null ) {
            System.out.println(errMsg);
        }

        program = new Program(false, null);
        program.setCyanLangDir(cyanLangDir);
        program.setJavaLibDir(cyanLangDir);

        /*
        project = new Project( program, String mainPackage, String mainObject,
                List<String> authorArray,
                List<String> cyanPathArray,
                List<String> importList, String projectCanonicalPath, String execFileName )
        */
        project = new Project(program, null, null, null, null, null, null, null);

        project.setCallJavac(false);
        project.setExec(false);
        project.setCmdLineArgs("");
        program.setProject(project);
        project.setProgram(program);

        try {
            wrEvalEnv = new WrEvalEnv(this.cyanLangDir, selfObject, null);
            final CyanPackage cyanPackage = new CyanPackage(this.program, "anonymousPackage", this.project,
                    "anonymousPackage", null, null, null);

            final char[] text = new char[cyanCode.length() + 1];
            final char[] nonSlashZeroText = cyanCode.toCharArray();
            boolean foundNonWhiteSpace = false;
            for (int i = 0; i < nonSlashZeroText.length; ++i) {
                text[i] = nonSlashZeroText[i];
            }
            for (char ch : nonSlashZeroText) {
                if ( !Character.isWhitespace(ch) ) {
                    foundNonWhiteSpace = true;
                    break;
                }
            }

            if ( !foundNonWhiteSpace ) {
                return null;
            }
            text[cyanCode.length()] = '\0';
            final ICompiler_parsing cp = CompilerManager.getCompilerToInternalDSL(text, "anonymous source code",
                    "anonymousPackage\\anonymous source code", cyanPackage.getI());
            cp.setParsingForInterpreter(true);

            final List<WrStatement> statList = CyanCodeSnippet.parseCyanCodeShell(cp);
            if ( statList.size() == 0 ) {
                return null;
            }
            final Tuple2<Object, String> t = CyanCodeSnippet.evalCyanCode(statList, wrEvalEnv, varNameList, objList);
            if ( t == null ) {
                RuntimeException rte = new cyanruntime.ExceptionContainer__(new _ExceptionStr(
                        new CyString("Exception when evaluating Cyan statements. Probably an internal error")));
                throw rte;
            }
            else if ( t.f2 != null ) {
                throw new cyanruntime.ExceptionContainer__(new _ExceptionStr(new CyString(t.f2)));
            }
            else {
                return t.f1;
            }
        } catch (CompileErrorException e) {
            throw new ExceptionContainer__(new _ExceptionStr(new CyString(e.getMessage())));
        } catch (InterpretationErrorException e) {
            throw new ExceptionContainer__(new _ExceptionStr(new CyString(e.getMessage())));
        }

    }

    private String setCyanLangDir() {
        if ( cyanLangDir == null ) {
            cyanLangDir = System.getenv("CYAN_HOME");
            if ( cyanLangDir == null ) {
                return "In order to interpret Cyan code at " + "runtime it is necessary to set the "
                        + "System environment variable CYAN_HOME "
                        + "with the directory 'lib' that contains the jar files"
                        + " of the Cyan libraries (cyanLang.jar, cyanruntime.jar, etc). "
    					+ "Reinstall the Cyan compiler";
            }
            File f = new File(cyanLangDir);
            if ( !f.exists() || !f.isDirectory() ) {
                return "Directory '" + cyanLangDir + "' does not exist. '" + cyanLangDir
                        + "' is the value of the System variable CYAN_HOME. "
            					+ "Reinstall the Cyan compiler";
            }
        }
        return null;
    }

    public static List<WrStatement> parseCyanCodeShell(ICompiler_parsing compiler_parsing) {
        final List<WrStatement> statList = new ArrayList<>();
        while (compiler_parsing.getSymbol().token != Token.EOLO) {
            if ( compiler_parsing.getSymbol().token == Token.IMPORT ) {
                CyanCodeSnippet.importPackages(compiler_parsing, statList);
            }
            WrStatement lastStat = compiler_parsing.statement();
            statList.add(lastStat);
            compiler_parsing.removeLastExprStat();
            if ( lastStat.demandSemicolon() ) {
                if ( compiler_parsing.getSymbol().token == Token.SEMICOLON ) {
                    compiler_parsing.next();
                }
                else {
                    WrSymbol sym = compiler_parsing.getSymbol();
                    if ( sym.token == Token.EOF || sym.token == Token.EOLO ) {
                        sym = lastStat.getFirstSymbol();
                    }
                    throw new CompileErrorException(
                            "Error in line " + sym.getLineNumber() + "(" + sym.getColumnNumber() + "): ';' expected");
                }
            }
        }
        return statList;
    }

    private static Tuple2<Object, String> evalCyanCode(List<WrStatement> statList, WrEvalEnv ee, String[] varNameList,
            Object[] objList) {
        if ( statList.size() == 0 ) {
            return null;
        }

        if ( (varNameList == null && objList != null) || (varNameList != null && objList == null) ) {
            final Tuple2<Object, String> t = new Tuple2<>(null,
                    "The list of variable names or the list of objects, just one of them, is null.");
            return t;
        }
        if ( (varNameList != null && objList != null) && (varNameList.length != objList.length) ) {
            final Tuple2<Object, String> t = new Tuple2<>(null,
                    "The number of variables passed as parameters is different from the number of objects");
            return t;
        }

        if ( varNameList != null ) {
            int i = 0;
            for (String varName : varNameList) {
                Object value = objList[i];
                ee.addVariable(varName, value);
                ++i;
            }
        }
        // Object last = null;
        for (final WrStatement is : statList) {
            try {
                is.eval(ee);
            } catch (InterpretationErrorException e) {
                WrSymbol sym = e.getSymbol();
                final Tuple2<Object, String> t = new Tuple2<>(null,
                        "Error in line " + sym.getLineNumber() + "(" + sym.getColumnNumber() + "): " + e.getMessage());
                return t;
            } catch (ReturnValueEvalEnvException e) {
                final Tuple2<Object, String> t = new Tuple2<>(ee.getReturnValue(), null);
                return t;
            } catch (final Throwable e) {
                final Tuple2<Object, String> t = new Tuple2<>(null,
                        "An exception was thrown by statement of line " + is.getFirstSymbol().getLineNumber() + "("
                                + is.getFirstSymbol().getColumnNumber() + ")");
                return t;
            }

        }
        final Tuple2<Object, String> t = new Tuple2<>(null, null);
        return t;
    }

    static public void importPackages(ICompiler_parsing cp, List<WrStatement> statList) {

        while (cp.getSymbol().token == Token.IMPORT) {
            cp.next();
            if ( cp.getSymbol().token != Token.IDENT ) {
                cp.error(cp.getSymbol(), "package name expected in import declaration");
            }
            else {
                final WrExprIdentStar importPackage = cp.ident();
                if ( importPackage.getName().startsWith(MetaHelper.cyanLanguagePackageName) ) {
                    if ( importPackage.getName().equals(MetaHelper.cyanLanguagePackageName) ) {
                        cp.error(importPackage.getFirstSymbol(),
                                "Package 'cyan.lang' is automatically imported. It cannot be imported by the user");
                    }
                    else {
                        cp.error(importPackage.getFirstSymbol(),
                                "It is not legal to have a package that starts with 'cyan.lang'");
                    }
                }
                statList.add(
                        (new StatementImport((ExprIdentStar) meta.GetHiddenItem.getHiddenExpr(importPackage), null))
                                .getI());
            }
            if ( cp.getSymbol().token == Token.SEMICOLON ) {
                cp.next();
            }
        }

    }

    public String disposeCreatedJavaObjects(String... list) {
        if ( wrEvalEnv != null ) {
            List<Object> createJavaObjectList = wrEvalEnv.getCreatedJavaObjectList();
            if ( createJavaObjectList != null ) {
                if ( list == null || list.length == 0 ) {
                    for (Object obj : createJavaObjectList) {
                        Class<?> objClass = obj.getClass();
                        try {
                            Method m = objClass.getMethod("setVisible", boolean.class);
                            if ( m != null ) {
                                m.setAccessible(true);
                                try {
                                    m.invoke(obj, false);
                                    break;
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    return "Error when calling method 'setVisible' of an object of class "
                                            + objClass.getCanonicalName();
                                }
                            }
                        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {

                        }
                    }
                }
                else {
                    if ( list.length % 2 != 0 ) {
                        return "The list parameter to disposeCreatedJavaObjects should contains pairs ('packageName', 'methodName')";
                    }
                    for (Object obj : createJavaObjectList) {
                        Class<?> objClass = obj.getClass();
                        String packageNameObjectClass = objClass.getPackage().getName();
                        for (int i = 0; i < list.length;) {
                            String packageName = list[i];
                            String setVisibleMethodName = list[i + 1];
                            i += 2;

                            if ( packageNameObjectClass.contains(packageName) ) {
                                try {
                                    Method m = objClass.getMethod(setVisibleMethodName, boolean.class);
                                    if ( m != null ) {
                                        m.setAccessible(true);
                                        try {
                                            m.invoke(obj, false);
                                            break;
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            return "Error when calling method " + setVisibleMethodName
                                                    + " of an object of class " + objClass.getCanonicalName();
                                        }
                                    }
                                } catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String cyanLangDir;

    private WrEvalEnv wrEvalEnv = null;

}

/*
/ *
 * load metaobjects of the imported package
 * /
String importedPackageName = importedPackage.getName();
CyanPackage importedPackage = compilationUnit.getCyanPackage().getProject()
		.searchPackage(importedPackageName);
if ( importedPackage == null ) {
	importJava(importJVMPackageSet, importJVMJavaRefSet,
			importedPackage, importedPackageName);
}
else {
	importList.add(importedPackage);
	compilationUnit.loadCyanMetaobjects(importedPackage.getMetaobjectList(), importSymbol, this);
	lexer.addMetaObjectLiteralObjectSeqTable(compilationUnit.getMetaObjectLiteralObjectSeqTable());
	lexer.addMetaobjectLiteralNumberTable(compilationUnit.getMetaObjectLiteralNumber());
	// lexer.setMetaObjectLiteralObjectIdentSeqTable(compilationUnit.getMetaObjectLiteralObjectIdentSeqTable());
	lexer.addMetaobjectLiteralStringTable(compilationUnit.getMetaobjectLiteralObjectString());

	importPackageSet.add(importedPackage);
}


while ( cp.getSymbol().token == Token.COMMA || cp.getSymbol().token == Token.IDENT ) {
	if ( cp.getSymbol().token == Token.IDENT ) {
		if ( !symbol.getSymbolString().equals("open") ) {
			error2(symbol, "',' expected between imported packages." + foundSuch());
		}
		else {
			break;
		}
	}
	else {
		lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
		next();
	}
	if ( cp.getSymbol().token != Token.IDENT )
		error(true, symbol, "Package name expected." + foundSuch(), null, ErrorKind.package_name_expected);
	Symbol packageSymbol = symbol;
	importedPackage = ident();
	/ *
	 * load metaobjects of the imported package
	 * /
	importedPackageName = importedPackage.getName();
	importedPackage = compilationUnit.getCyanPackage().getProject()
			.searchPackage(importedPackageName);
	if ( importedPackage == null ) {
		importJava(importJVMPackageSet, importJVMJavaRefSet,
				importedPackage, importedPackageName);
	}
	else {
		importList.add(importedPackage);
		compilationUnit.loadCyanMetaobjects(importedPackage.getMetaobjectList(), packageSymbol, this);

		lexer.addMetaObjectLiteralObjectSeqTable(compilationUnit.getMetaObjectLiteralObjectSeqTable());
		lexer.addMetaobjectLiteralNumberTable(compilationUnit.getMetaObjectLiteralNumber());
		// lexer.setMetaObjectLiteralObjectIdentSeqTable(compilationUnit.getMetaObjectLiteralObjectIdentSeqTable());
		lexer.addMetaobjectLiteralStringTable(compilationUnit.getMetaobjectLiteralObjectString());
		importPackageSet.add(importedPackage);
	}
}
*/