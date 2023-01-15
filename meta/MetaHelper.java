package meta;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ast.CyanPackage;
import ast.EvalEnv;
import ast.ExprIdentStar;
import ast.Program;
import ast.ReturnValueEvalEnvException;
import ast.StatementImport;
import cyan.lang.CyInt;
import cyan.lang.CyString;
import cyan.lang._Array_LT_GP_CyString_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT;
import cyan.lang._Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT;
import cyanruntime.Ref;
import error.CompileErrorException;
import meta.lexer.MetaLexer;
import saci.CompilerManager;
import saci.CyanTypeCompiler;
import saci.NameServer;
import saci.Project;

public class MetaHelper {

    static {

        MetaHelper.cyanJavaBasicTypeTable = new Hashtable<>();
        MetaHelper.cyanJavaBasicTypeTable.put("Byte", "CyByte");
        MetaHelper.cyanJavaBasicTypeTable.put("Short", "CyShort");
        MetaHelper.cyanJavaBasicTypeTable.put("Int", "CyInt");
        MetaHelper.cyanJavaBasicTypeTable.put("Long", "CyLong");
        MetaHelper.cyanJavaBasicTypeTable.put("Float", "CyFloat");
        MetaHelper.cyanJavaBasicTypeTable.put("Double", "CyDouble");
        MetaHelper.cyanJavaBasicTypeTable.put("Char", "CyChar");
        MetaHelper.cyanJavaBasicTypeTable.put("Boolean", "CyBoolean");
        MetaHelper.cyanJavaBasicTypeTable.put("String", "CyString");
        MetaHelper.cyanJavaBasicTypeTable.put("Dyn", "Object");

    }

    /**
       @param annotationInfoSet
       @param tupleSet
     */
    public static _Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT_GT cyanSetTupleStringIntIntDyn_toJava(
            Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet) {

        _Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT_GT tupleSet = new _Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT_GT();

        for (Tuple4<String, Integer, Integer, Object> elem : annotationInfoSet) {
            tupleSet._add_1(new _Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT(new CyString(elem.f1),
                    new CyInt(elem.f2), new CyInt(elem.f3), elem.f4));
        }
        return tupleSet;
    }

    /**
       @param r
     */
    public static List<Tuple2<String, String[]>> cyanTupleStringArrayString_tojava(
            _Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT array) {
        int size = array._size().n;
        if ( size == 0 ) {
            return null;
        }
        else {
            List<Tuple2<String, String[]>> renameMethodList = new ArrayList<>();
            // Array<Tuple<String, Array<String>>>
            for (int k = 0; k < size; ++k) {
                _Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT t = array._at_1(new CyInt(k));
                _Array_LT_GP_CyString_GT strArray = t._f2();
                int sizeStrArray = strArray._size().n;
                String[] javaStrArray = null;
                if ( sizeStrArray != 0 ) {
                    javaStrArray = new String[sizeStrArray];
                    for (int j = 0; j < sizeStrArray; ++j) {
                        javaStrArray[j] = strArray._at_1(new CyInt(j)).s;
                    }
                }
                renameMethodList.add(new Tuple2<>(t._f1().s, javaStrArray));
            }
            return renameMethodList;
        }
    }

    /**
       @param statsList
       @param array
       @return
     */
    public static List<Tuple2<String, StringBuffer>> cyanArrayTupleStringString_toJava(
            _Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array) {

        List<Tuple2<String, StringBuffer>> statsList = null;
        int size = array._size().n;
        if ( size != 0 ) {
            statsList = new ArrayList<>();
            for (int k = 0; k < size; ++k) {
                _Tuple_LT_GP_CyString_GP_CyString_GT t = array._at_1(new CyInt(k));
                statsList.add(new Tuple2<>(t._f1().s, new StringBuffer(t._f2().s)));
            }
        }
        return statsList;
    }

    public static List<Tuple3<String, StringBuffer, Boolean>> cyanArrayTupleStringStringBoolean_toJava(
            _Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT array) {

        ArrayList<Tuple3<String, StringBuffer, Boolean>> statsList = null;
        int size = array._size().n;
        if ( size != 0 ) {
            statsList = new ArrayList<>();
            for (int k = 0; k < size; ++k) {
                _Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT t = array._at_1(new CyInt(k));
                statsList.add(
                        new Tuple3<>(t._f1().s, new StringBuffer(t._f2().s), t._f3().b));
            }
        }
        return statsList;
    }

    /** interpreterPrototype should have a (key, value) pair whose key is interfaceMethodName.
     * If it does, value is a statement list that is evaluated (interpreted). Errors
     * are directed to cyanMetaobject. varNameList and objList are names and values of variables
     * injected in the Cyan interpreter. If returnTypeInterfaceMethod is not null, the
     * return value returned by the interpretation should be assignable to returnTypeInterfaceMethod.
     * If returnTypeInterfaceMethodInner1 is not null, the object returned should be an object of<br>
     * <code>returnTypeInterfaceMethod<returnTypeInterfaceMethodInner1></code><br>
     *
     * The return value of the code is returned, null in error.
     *
       @param compiler
       @param interpreterPrototype
       @param cyanMetaobject
       @param interfaceMethodName
       @param varNameList
       @param objList
       @param returnTypeInterfaceMethod
       @return
     */
    static public Object interpreterFor_MOPInterfaceMethod(IAbstractCyanCompiler compiler, WrEnv wrEnv,
            InterpreterPrototype interpreterPrototype, CyanMetaobjectAtAnnot cyanMetaobject, String interfaceMethodName,
            String[] varNameList, Object[] objList, Class<?> returnTypeInterfaceMethod,
            String returnTypeInterfaceMethodName) {

        if ( interpreterPrototype == null ) {
            return null;
        }
        List<WrStatement> statList = interpreterPrototype.mapMethodName_Body.get(interfaceMethodName);
        if ( statList == null || statList.size() == 0 ) {
            return null;
        }
        WrStatement fs = statList.get(0);

        try {
            WrEvalEnv ee = MetaHelper.getNewWrEvalEnv(wrEnv, interpreterPrototype, cyanMetaobject, fs.getFirstSymbol(),
                    varNameList, objList, cyanMetaobject);

            ee.addVariable("metaobject", cyanMetaobject);
            ee.addVariable("env", wrEnv);

            Object selfObject = MetaHelper.createSelfObject(compiler, cyanMetaobject, ee, wrEnv);

            ee.setSelfObject(selfObject);
            ee.setCurrentMethod(wrEnv.getCurrentMethod());
            ee.setCurrentPrototype(wrEnv.getCurrentPrototype());
            ee.setCompilationUnit(wrEnv.getCurrentCompilationUnit());

            // add imported packages to the imported package list of ee
            for (WrStatementImport wrImport : interpreterPrototype.importList) {
                wrImport.eval(ee);
            }
            Object obj = MetaHelper.evalCode(statList, ee);
            if ( obj != null && returnTypeInterfaceMethod != null
                    && !(returnTypeInterfaceMethod.isAssignableFrom(obj.getClass())) ) {
                cyanMetaobject.addError("Code for '" + interfaceMethodName + "' should return an object of class '"
                        + returnTypeInterfaceMethodName + "'. It returned an object of class '"
                        + obj.getClass().getName() + "'");
                return null;
            }
            ee.setCurrentMethod(null);
            return obj;
        } catch (InterpretationErrorException e) {
            cyanMetaobject.addError(e.getSymbol(), e.getMessage());
        } catch (CompileErrorException e) {
            cyanMetaobject.addError("Internal error in method MetaHelper::interpreterFor_MOPInterfaceMethod");
        }
        return null;
    }

    /**
     * interpret 'statList' and returns what it returns with a 'return' statement.
       @param statList
       @param compiler_semAn
       @param cyanMetaobject
       @param interfaceMethodName
       @param varNameList
       @param objList
       @param returnTypeInterfaceMethod
       @return
     */
    static public Object interpreterFor_MOPInterfaceMethod(List<WrStatement> statList, ICompiler_semAn compiler_semAn,
            CyanMetaobjectAtAnnot cyanMetaobject, String interfaceMethodName, String[] varNameList, Object[] objList,
            Class<?> returnTypeInterfaceMethod) {

        if ( statList == null || statList.size() == 0 ) {
            return null;
        }
        WrStatement fs = statList.get(0);

        try {
            WrEnv env = compiler_semAn.getEnv();
            WrEvalEnv ee = MetaHelper.getNewWrEvalEnv(env, null, fs.getFirstSymbol(), varNameList, objList,
                    cyanMetaobject);
            ee.addVariable("metaobject", cyanMetaobject);
            ee.addVariable("env", compiler_semAn.getEnv());
            Object selfObject = MetaHelper.createSelfObject(compiler_semAn, cyanMetaobject, ee,
                    compiler_semAn.getEnv());

            ee.setSelfObject(selfObject);
            ee.setCurrentMethod(env.getCurrentMethod());
            ee.setCurrentPrototype(env.getCurrentPrototype());
            ee.setCompilationUnit(env.getCurrentCompilationUnit());

            Object obj = MetaHelper.evalCode(statList, ee);
            if ( obj != null && returnTypeInterfaceMethod != null
                    && !(returnTypeInterfaceMethod.isAssignableFrom(obj.getClass())) ) {
                cyanMetaobject.addError("Code for '" + interfaceMethodName + "' should return an object of class '"
                        + returnTypeInterfaceMethod.getName() + "'. It returned an object of class '"
                        + obj.getClass().getName() + "'");
                return null;
            }
            if ( returnTypeInterfaceMethod == null && obj != null ) {
                cyanMetaobject.addError("Code for '" + interfaceMethodName + "' should NOT return a value. But "
                        + "it returned an object of class '" + obj.getClass().getName() + "'");
                return null;
            }

            ee.setCurrentMethod(null);
            return obj;
        } catch (InterpretationErrorException e) {
            cyanMetaobject.addError(e.getSymbol(), e.getMessage());
        } catch (CompileErrorException e) {
            cyanMetaobject.addError("Internal error in method MetaHelper::interpreterFor_MOPInterfaceMethod");
        }

        return null;
    }

    static public boolean interpreterFor_runUntilFixedPoint(InterpreterPrototype interpreterPrototype,
            CyanMetaobjectAtAnnot cyanMetaobject) {

        if ( interpreterPrototype == null ) {
            return false;
        }
        List<WrStatement> statList = interpreterPrototype.mapMethodName_Body.get("runUntilFixedPoint");
        if ( statList == null || statList.size() == 0 ) {
            return false;
        }
        if ( statList.size() != 1 || !(statList.get(0) instanceof WrStatementReturn) ) {
            cyanMetaobject.addError("Code for 'runUntilFixedPoint' should be either 'return true;' or 'return false;'");
            return false;
        }
        WrStatementReturn sr = (WrStatementReturn) statList.get(0);
        if ( !(sr.getExpr() instanceof meta.WrExprLiteralBoolean) ) {
            cyanMetaobject.addError("Code for 'runUntilFixedPoint' should be either 'return true;' or 'return false;'");
            return false;
        }
        WrExprLiteralBoolean b = ((WrExprLiteralBoolean) sr.getExpr());
        return (boolean) b.getJavaValue();
    }

    public static List<WrStatement> loadCyanStatementList(IAbstractCyanCompiler cp, String packageName1,
            String filename, List<String> paramNameList) {

        final Tuple5<FileError, char[], String, String, WrCyanPackage> t5 = cp.readTextFileFromPackage(filename,
                MetaHelper.extensionMyanFile, packageName1, DirectoryKindPPP.DATA,
                paramNameList == null ? 0 : paramNameList.size(), paramNameList);

        if ( t5 != null && t5.f1 == FileError.package_not_found ) {
            throw new InterpretationErrorException(
                    "Cannot find package '" + packageName1 + "' used to load statements for the Cyan interpreter",
                    null);
        }
        if ( t5 == null || t5.f1 != FileError.ok_e ) {
            throw new InterpretationErrorException("Cannot read concept from file '" + filename + "."
                    + MetaHelper.extensionMyanFile + "' from package '" + packageName1
                    + "'. This file should be in directory '" + DirectoryKindPPP.DATA + "' of the package directory",
                    null);
        }

        final String cyanCode = new String(t5.f2);

        final ICompiler_parsing newCompiler_parsing = MetaHelper.createNewCompiler_parsing(cyanCode);

        List<WrStatement> statList = null;
        try {
            statList = MetaHelper.parseCyanStatementList(newCompiler_parsing);
        } catch (CompileErrorException e) {
            List<WrUnitError> wrUnitErrorList = newCompiler_parsing.getCompilationUnit().getErrorList();
            if ( wrUnitErrorList != null && wrUnitErrorList.size() > 0 ) {
                // only first error
                throw new InterpretationErrorException(wrUnitErrorList.get(0).getMessage(), null);
            }
        } catch (Throwable e) {
            throw new InterpretationErrorException("File '" + packageName1 + "." + filename
                    + "' has Cyan statements that were loaded probably by "
                    + "a message send 'self runFile: filename'. " + "One of the statements has thrown an exception '"
                    + e.getClass().getName() + "' whose message is '" + e.getMessage() + "'. ", null);
        }
        return statList;
    }

    public static Tuple2<String, String> extractPackageName(String package_Name) {
        int indexLastDot = package_Name.lastIndexOf('.');
        if ( indexLastDot < 0 ) {
            return new Tuple2<>(null, package_Name);
        }
        else {
            return new Tuple2<>(package_Name.substring(0, indexLastDot),
                    package_Name.substring(indexLastDot + 1));
        }
    }

    /**
     * Create a new object with 'call' methods with one, two, three, and four parameter. All of them
     * have type java.lang.String. The first one is the name of the action function (metaobject) to be
     * called. The others are parameters to the action function.
       @param compiler
       @param thisMetaobject
       @param ee
       @return
     */
    public static Object createSelfObject(IAbstractCyanCompiler compiler, final CyanMetaobjectAtAnnot thisMetaobject,
            WrEvalEnv ee, WrEnv env) {
        return new Object() {

//			@SuppressWarnings("unused")
//			public Object call(cyan.lang.CyString actionFunctionName, Object ...args) {
//				return call(actionFunctionName.s, args);
//			}

            @SuppressWarnings("unused")
            public Object callArgs(String actionFunctionName, Object... args) {
                final IActionFunction actionFunction = env.searchActionFunction(actionFunctionName);
                if ( actionFunction == null ) {
                    thisMetaobject.addError("Function metaobject '" + actionFunctionName + "' was not found. "
                            + "If it is a file of a directory '" + meta.DirectoryKindPPP.DATA.toString()
                            + "', it should be preceded by a directory name");
                    return null;
                }
                List<Object> paramList = new ArrayList<>();
                if ( args != null ) {
                    Collections.addAll(paramList, args);
                }
                Object obj = actionFunction.eval(
                        new Tuple6<>(
                                compiler, thisMetaobject, paramList, thisMetaobject.getAnnotation().getFirstSymbol(),
                                ee.getCurrentMethod(), ee.getWrEnvToBeUsedInVisitor()));
                return obj;
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName) {
                final IActionFunction actionFunction = env.searchActionFunction(actionFunctionName);
                if ( actionFunction == null ) {
                    thisMetaobject.addError("Function metaobject '" + actionFunctionName + "' was not found. "
                            + "If it is a file of a directory '" + meta.DirectoryKindPPP.DATA.toString()
                            + "', it should be preceded by a directory name");
                    return null;
                }
                List<Object> paramList = new ArrayList<>();
                Object obj = actionFunction.eval(
                        new Tuple6<>(
                                compiler, thisMetaobject, paramList, thisMetaobject.getAnnotation().getFirstSymbol(),
                                ee.getCurrentMethod(), ee.getWrEnvToBeUsedInVisitor()));
                return obj;
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1) {
                return callArgs(actionFunctionName, p1);
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1, Object p2) {
                return callArgs(actionFunctionName, p1, p2);
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1, Object p2, Object p3) {
                return callArgs(actionFunctionName, p1, p2, p3);
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1, Object p2, Object p3, Object p4) {
                return callArgs(actionFunctionName, p1, p2, p3, p4);
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1, Object p2, Object p3, Object p4, Object p5) {
                return callArgs(actionFunctionName, p1, p2, p3, p4, p5);
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1, Object p2, Object p3, Object p4, Object p5,
                    Object p6) {
                return callArgs(actionFunctionName, p1, p2, p3, p4, p5, p6);
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1, Object p2, Object p3, Object p4, Object p5,
                    Object p6, Object p7) {
                return callArgs(actionFunctionName, p1, p2, p3, p4, p5, p6, p7);
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1, Object p2, Object p3, Object p4, Object p5,
                    Object p6, Object p7, Object p8) {
                return callArgs(actionFunctionName, p1, p2, p3, p4, p5, p6, p7, p8);
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1, Object p2, Object p3, Object p4, Object p5,
                    Object p6, Object p7, Object p8, Object p9) {
                return callArgs(actionFunctionName, p1, p2, p3, p4, p5, p6, p7, p8, p9);
            }

            @SuppressWarnings("unused")
            public Object call(String actionFunctionName, Object p1, Object p2, Object p3, Object p4, Object p5,
                    Object p6, Object p7, Object p8, Object p9, Object p10) {
                return callArgs(actionFunctionName, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName) {
                return this.runFile(moFileName, new String[] {});
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1) {
                return this.runFile(moFileName, new String[] { p1 });
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1, String p2) {
                return this.runFile(moFileName, new String[] { p1, p2 });
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1, String p2, String p3) {
                return this.runFile(moFileName, new String[] { p1, p2, p3 });
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1, String p2, String p3, String p4) {
                return this.runFile(moFileName, new String[] { p1, p2, p3, p4 });
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1, String p2, String p3, String p4, String p5) {
                return this.runFile(moFileName, new String[] { p1, p2, p3, p4, p5 });
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1, String p2, String p3, String p4, String p5, String p6) {
                return this.runFile(moFileName, new String[] { p1, p2, p3, p4, p5, p6 });
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1, String p2, String p3, String p4, String p5, String p6,
                    String p7) {
                return this.runFile(moFileName, new String[] { p1, p2, p3, p4, p5, p6, p7 });
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1, String p2, String p3, String p4, String p5, String p6,
                    String p7, String p8) {
                return this.runFile(moFileName, new String[] { p1, p2, p3, p4, p5, p6, p7, p8 });
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1, String p2, String p3, String p4, String p5, String p6,
                    String p7, String p8, String p9) {
                return this.runFile(moFileName, new String[] { p1, p2, p3, p4, p5, p6, p7, p8, p9 });
            }

            @SuppressWarnings("unused")
            public Object runFile(String moFileName, String p1, String p2, String p3, String p4, String p5, String p6,
                    String p7, String p8, String p9, String p10) {
                return this.runFile(moFileName, new String[] { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10 });
            }

            public Object runFile(String moFileName, String... args) {
                Tuple2<String, String> t = MetaHelper.extractPackageName(moFileName);
                if ( t.f1 == null ) {
                    t.f1 = ee.getCompilationUnit().getPackageName();
                }
                else if ( t.f2 == null ) {
                    thisMetaobject.addError("The first parameter of 'run:' " + "is not correct. " + "The parameter is '"
                            + moFileName + "'");
                    throw new InterpretationErrorException("The first parameter of 'runFile:' " + "is not correct. "
                            + "The parameter is '" + moFileName + "'", null);
                }
                List<String> paramNameList = new ArrayList<>();
                Collections.addAll(paramNameList, args);
                List<WrStatement> statList = MetaHelper.loadCyanStatementList(compiler, t.f1, t.f2, paramNameList);

                return MetaHelper.evalCode(statList, ee);
            }

        };
    }

    /**
     * return the Java name of the method whose keywords are 'keyword1' and 'keyword2' with numParam1  and numParam2 parameters
     */
    static public String getJavaNameOfMethodWith(String keyword1, int numParam1, String keyword2, int numParam2) {
        return MetaHelper.getJavaNameOfkeyword(keyword1) + numParam1 + MetaHelper.getJavaNameOfkeyword(keyword2)
                + numParam2;
    }

    /**
     * return the Java name of the method whose keyword is 'keyword1' with numParam1 parameters
     */
    static public String getJavaNameOfMethodWith(String keyword1, int numParam1) {
        return MetaHelper.getJavaNameOfkeyword(keyword1) + numParam1;
    }

    /**
     * return the Java name of the method whose keywords are given in the list keywordList and whose
     * number of parameters of each keyword is given by numParamList
     */
    static public String getJavaNameOfMethod(String[] keywordList, int[] numParamList) {
        StringBuilder javaName = new StringBuilder();
        int i = 0;
        for (String keyword : keywordList) {
            javaName.append(MetaHelper.getJavaNameOfkeyword(keyword)).append("_").append(numParamList[i]);
            ++i;
        }
        return javaName.toString();
    }

    public static boolean isBasicType(String name) {
        return MetaHelper.cyanJavaBasicTypeTable.get(name) != null;
    }

    /**
     * get the Java name corresponding to this keyword. It is
     * equal to "_symbolString" except when there is a underscore.
     * All underscore characters are duplicated. So,
     *       Is_A_Number
     * results in
     *       _Is__A__Number
     * The ending character ':' is changed to "_dot". So
     *    "eval:" produces  "_eval_dot"
     * @param lineNumber
     */
    static public String getJavaNameOfkeyword(String symbolString) {

        String alpha = MetaHelper.alphaName(symbolString);
        if ( alpha != null ) {
            return alpha;
            /*
            int size = symbolString.length();
            alpha = "";
            for (int i = 0; i < size; i++) {
            	String s = symbolToAlpha.get("" + symbolString.charAt(i));
            	if ( s == null )
            		return null;
                alpha = alpha + "_" + s;
            }
            return alpha;
            */
        }
        else {
            StringBuffer s = new StringBuffer("_");
            for (int i = 0; i < symbolString.length(); i++) {
                char ch = symbolString.charAt(i);
                if ( ch == '_' ) {
                    s.append("__");
                }
                else if ( ch == ':' ) {
                    s.append("_");
                }
                else {
                    s.append(ch);
                }
            }
            return s.toString();

        }
    }

    /**
     * return the alphanumeric name of a method composed of symbols. That is, if the
     * method is '+', this method returns "_plus". If the method were '<*' (if possible) this
     * method would return "_lessThan_star"
     */

    static public String alphaName(String symbolName) {
        int size = symbolName.length();
        StringBuilder alpha = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String s = NameServer.symbolToAlpha.get("" + symbolName.charAt(i));
            if ( s == null ) {
                return null;
            }
            alpha.append("_").append(s);
        }
        return alpha.toString();
    }

    public static String removeCyanLangChange(String name) {
        String s;
        if ( name.startsWith(NameServer.cyanLanguagePackageName_p_Dot) ) {
            s = name.substring(MetaHelper.sizeCyanLanguagePackageName_p_Dot);
        }
        else {
            s = name;
        }
        String javaBasicTypeName = MetaHelper.cyanJavaBasicTypeTable.get(s);
        if ( javaBasicTypeName != null ) {
            return javaBasicTypeName;
        }

        /*
         * put '_' in front of the prototype name, if there is one
         */
        boolean start = true;
        boolean doNotAddUnderscore = false;
        StringBuffer ret = new StringBuffer("");
        for (int j = 0; j < s.length(); ++j) {
            char ch = s.charAt(j);
            if ( start && Character.isUpperCase(ch) ) {
                ret.append("_");
                doNotAddUnderscore = true;
            }
            if ( ch == '.' ) {
                start = true;
                doNotAddUnderscore = true;
            }
            else {
                start = false;
            }
            ret.append(ch);
        }
        if ( doNotAddUnderscore ) {
            return ret.toString();
        }
        else {
            // a simple name such as 'other'
            return "_" + ret.toString();
        }

    }

    /**
       @param cyanName
       @return

    public static String getJavaNameFromCyanNameWithoutPackage(String cyanName) {
    	char ch;
    	String innerProtoName = "";
    	String javaName = "";
    	for (int n = 0; n < cyanName.length(); ++n) {
    		ch = cyanName.charAt(n);
    		if ( Character.isWhitespace(ch) )
    			continue;
    		if ( Character.isAlphabetic(ch) || Character.isDigit(ch) ) {
    			innerProtoName += ch;
    		}
    		else if ( ch == '_' ) {
    			innerProtoName += "__";
    		}
    		else if ( ch == '<' ) {
    			//++insideGPI;
    			javaName += MetaHelper.removeCyanLangChange(innerProtoName) + "_LT_GP_";
    			innerProtoName = "";
    		}
    		else if ( ch == ',' ) {
    			javaName += MetaHelper.removeCyanLangChange(innerProtoName) + "_GP_";
    			innerProtoName = "";
    		}
    		else if ( ch == '>' ) {
    			javaName += MetaHelper.removeCyanLangChange(innerProtoName) + "_GT";
    			innerProtoName = "";
    			//--insideGPI;
    		}
    		else if ( ch == '.' ){
    			innerProtoName += "_p_";
    		}
    		else if ( ch == '|') {
    			NameServer.print("" + 0/0);
    		}
    		else {
    			String s = MetaHelper.alphaName(cyanName);
    			if ( s != null ) {
    				return s;
    			}
    		}


    	}
    	if ( innerProtoName.length() > 0 )
    		javaName += MetaHelper.removeCyanLangChange(innerProtoName);

    	return javaName;
    }


    public static String new_getJavaNameFromCyanNameWithoutPackage(String cyanName) {
    	char ch;
    	String innerProtoName = "";
    	String javaName = "";
    	for (int n = 0; n < cyanName.length(); ++n) {
    		ch = cyanName.charAt(n);
    		if ( Character.isWhitespace(ch) )
    			continue;
    		if ( Character.isAlphabetic(ch) || Character.isDigit(ch) ) {
    			innerProtoName += ch;
    		}
    		else if ( ch == '_' ) {
    			innerProtoName += "__";
    		}
    		else if ( ch == '<' ) {
    			//++insideGPI;
    			javaName += MetaHelper.removeCyanLangChange(innerProtoName) + "_LT_GP_";
    			innerProtoName = "";
    		}
    		else if ( ch == ',' ) {
    			javaName += MetaHelper.removeCyanLangChange(innerProtoName) + "_GP_";
    			innerProtoName = "";
    		}
    		else if ( ch == '>' ) {
    			javaName += MetaHelper.removeCyanLangChange(innerProtoName) + "_GT";
    			innerProtoName = "";
    			//--insideGPI;
    		}
    		else if ( ch == '.' ){
    			innerProtoName += "_p_";
    		}
    		else {
    			String s = MetaHelper.alphaName(cyanName);
    			if ( s != null ) {
    				return s;
    			}
    		}


    	}
    	if ( innerProtoName.length() > 0 )
    		javaName += MetaHelper.removeCyanLangChange(innerProtoName);

    	return javaName;
    }
    */

    /**
     *
       @param cyanName
       @return
     */
    static public Tuple2<String, String> getJavaNameTuple(String cyanName) {
        char ch;
        String packageName = "";

        int k = 0, lastDotIndex = 0;
        int size = cyanName.length();
        boolean done = false;
        while (k < size) {
            ch = cyanName.charAt(k);
            if ( ch == '.' ) {
                lastDotIndex = k;
            }
            else if ( ch == '<' ) {
                if ( lastDotIndex > 0 ) {
                    // '.' before '<' like in cyan.lang.Function<Int>
                    String protoName = cyanName.substring(lastDotIndex + 1, k);
                    String javaBasicTypeName = MetaHelper.cyanJavaBasicTypeTable.get(protoName);
                    if ( javaBasicTypeName != null ) {
                        cyanName = javaBasicTypeName + cyanName.substring(k);
                        done = true;
                    }

                }
                break;
            }
            ++k;
        }
        if ( lastDotIndex > 0 ) {
            packageName = cyanName.substring(0, lastDotIndex);
        }
        if ( !done ) {
            if ( cyanName.charAt(lastDotIndex) == '.' ) {
                ++lastDotIndex;
            }
            cyanName = cyanName.substring(lastDotIndex);
        }

//		return new Tuple2<String, String>(packageName, MetaHelper.getJavaNameFromCyanNameWithoutPackage(cyanName));
        return new Tuple2<>(packageName, CyanTypeCompiler.cyanType_noPackage_ToJavaName(cyanName)
//				MetaHelper.getJavaNameFromCyanNameWithoutPackage(cyanName)
        );

    }

    public static String removeSpaces(String str) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            if ( !Character.isWhitespace(str.charAt(i)) ) {
                s.append(str.charAt(i));
            }
        }
        return s.toString();
    }

    public static String addQuotes(String cyanStr) {
        if ( cyanStr.charAt(0) != '\"' ) {
            cyanStr = "\"" + MetaLexer.escapeJavaString(cyanStr) + "\"";
        }
        return cyanStr;
    }

    static boolean isUnionType(String name) {
        int size = name.length();
        int insideGenericArgsNesting = 0;
        for (int i = 0; i < size; ++i) {
            char ch = name.charAt(i);
            if ( ch == '<' ) {
                ++insideGenericArgsNesting;
            }
            else if ( ch == '>' ) {
                --insideGenericArgsNesting;
            }
            else if ( ch == '|' && insideGenericArgsNesting == 0 ) {
                return true;
            }
        }
        return false;
    }

    /**
     * return the Java name of 'cyanName' without the package.
       @param cyanName
       @return
     */
    static public String getJavaName(String cyanName) {
        if ( MetaHelper.isUnionType(cyanName) ) {
            return "Object";
        }
        Tuple2<String, String> t = MetaHelper.getJavaNameTuple(cyanName);
        int protoNameSize = t.f2.length();
        if ( t.f1.length() > 0 ) {
            if ( protoNameSize >= NameServer.MAX_CHAR_JAVA_NAME ) {
                return t.f1 + "." + NameServer.stubName(t.f2);
            }
            else {
                return t.f1 + "." + t.f2;
            }
        }
        else if ( protoNameSize >= NameServer.MAX_CHAR_JAVA_NAME ) {
            return NameServer.stubName(t.f2);
        }
        else {
            return t.f2;
        }
    }

    static public String nextIdentifier() {
        return "tmp" + NameServer.numberLocalVariable++ + "__";
    }

    public static String removeQuotes(String cyanStr) {
        if ( cyanStr.charAt(0) == '\"' ) {
            if ( cyanStr.charAt(cyanStr.length() - 1) == '\"' ) {
                return cyanStr.substring(1, cyanStr.length() - 1);
            }
            else {
                return cyanStr.substring(1);
            }
        }
        else if ( cyanStr.charAt(cyanStr.length() - 1) == '\"' ) {
            return cyanStr.substring(0, cyanStr.length() - 1);
        }
        else {
            return cyanStr;
        }
    }

    public static int numGenericParamInType(String s) {
        int k = s.length();
        int count = 0;
        int n = 1;
        for (int j = 0; j < k; ++j) {
            char ch = s.charAt(j);
            if ( ch == '<' ) {
                ++count;
            }
            else if ( ch == '>' ) {
                --count;
            }
            else if ( ch == ',' && count == 1 ) {
                ++n;
            }
        }
        return n;

    }

    private static String setCyanLangDir() {
        if ( MetaHelper.cyanLangDir == null ) {
            MetaHelper.cyanLangDir = System.getenv("CYAN_HOME");
            if ( MetaHelper.cyanLangDir == null ) {
                return "In order to interpret Cyan code at " + "runtime it is necessary to set the "
                        + "System environment variable CYAN_HOME "
                        + "with the directory 'lib' that contains the jar files"
                        + " of the Cyan libraries (cyanLang.jar, cyanruntime.jar, etc). "
    					+ "Reinstall the Cyan compiler";
            }
            File f = new File(MetaHelper.cyanLangDir);
            if ( !f.exists() || !f.isDirectory() ) {
                return "Directory '" + MetaHelper.cyanLangDir + "' does not exist. '" + MetaHelper.cyanLangDir
                        + "' is the value of the System variable CYAN_HOME. "
            					+ "Reinstall the Cyan compiler";
            }
        }
        return null;
    }

    /**
     * using Cyan syntax, return either
     *     [. null, map .]
     * or
     *     [. errMessage, null .]
     *
     * errMessage is an error message. map is composed of method names and statements
     * of these methods.
     *
       @param cyanCode
       @param selfObject
       @param varNameList
       @param objList
       @return
     */
    static public Tuple2<String, InterpreterPrototype> getMapMethodName_Body(String cyanCode) {

        final ICompiler_parsing cp = MetaHelper.createNewCompiler_parsing(cyanCode);
        final Tuple2<String, InterpreterPrototype> err_proto = MetaHelper.parseCyanCode(cp);
        return err_proto;

    }

    /**
       @param cyanCode
       @return
     */
    public static ICompiler_parsing createNewCompiler_parsing(String cyanCode) {
        MetaHelper.setCyanLangDir();

        Program program = new Program(false, null);
        program.setCyanLangDir(MetaHelper.cyanLangDir);
        program.setJavaLibDir(MetaHelper.cyanLangDir);

        /*
        project = new Project( program, String mainPackage, String mainObject,
                List<String> authorArray,
                List<String> cyanPathArray,
                List<String> importList, String projectCanonicalPath, String execFileName )
        */
        Project project = new Project(program, null, null, null, null, null, null, null);

        project.setCallJavac(false);
        project.setExec(false);
        project.setCmdLineArgs("");
        program.setProject(project);
        project.setProgram(program);

        final CyanPackage cyanPackage = new CyanPackage(program, "anonymousPackage", project, "anonymousPackage", null,
                null, null);

        final char[] text = new char[cyanCode.length() + 1];
        final char[] nonSlashZeroText = cyanCode.toCharArray();
        for (int i = 0; i < nonSlashZeroText.length; ++i) {
            text[i] = nonSlashZeroText[i];
        }
        text[cyanCode.length()] = '\0';
        final ICompiler_parsing cp = CompilerManager.getCompilerToInternalDSL(text, "anonymous source code",
                "anonymousPackage\\anonymous source code", cyanPackage.getI());
        return cp;
    }

    static public WrEvalEnv getNewWrEvalEnv(WrEnv wrEnv, InterpreterPrototype interpreterPrototype, Object selfObject,
            WrSymbol symForError, String[] varNameList, Object[] objList, CyanMetaobject cyanMetaobject) {

        WrEvalEnv ee = MetaHelper.getNewWrEvalEnv(wrEnv, selfObject, symForError, varNameList, objList, cyanMetaobject);
        if ( ee != null ) {
            ee.setFieldMemory(interpreterPrototype.fieldMemory);
        }

        return ee;
    }

    /**
       @param wrEnv
       @param selfObject
       @param symForError
       @param varNameList
       @param objList
       @return
     * @throws InterpretationErrorCheckedException
     */
    public static WrEvalEnv getNewWrEvalEnv(WrEnv wrEnv, Object selfObject, WrSymbol symForError, String[] varNameList,
            Object[] objList, CyanMetaobject cyanMetaobject) {

        WrEvalEnv ee = new WrEvalEnv(wrEnv, selfObject, symForError);
        ee.setWrEnvToBeUsedInVisitor(wrEnv);

        if ( (varNameList == null && objList != null) || (varNameList != null && objList == null) ) {
            wrEnv.error(symForError,
                    "The list of variable names or the list of objects to be add to the interpreter, just one of them, is null.");
            return null;
        }
        if ( (varNameList != null && objList != null) && (varNameList.length != objList.length) ) {
            wrEnv.error(symForError, "The number of variables passed as parameters in the interpreter "
                    + "is different from the number of objects");
            return null;
        }

        if ( varNameList != null ) {
            int i = 0;
            for (String varName : varNameList) {
                Object value = objList[i];
                ee.addVariable(varName, value);
                ++i;
            }
        }
        return ee;
    }

    /**
     * See the documentation for {@link meta.CyanMetaobjectAction_afterResTypes_semAn} relating the contents of files with
     * extension <code>mo</code> The compiler passed as parameter here is supposed to refer to a text
     * with the same contents as a <code>mo</code> file.
     * Using Cyan syntax, this method returns either
     *     [. null, proto .]
     * or
     *     [. errMessage, null .]
     *
     *in which <code>proto</proto> represents a Cyan prototype for the interpreter.
    */

    static public Tuple2<String, InterpreterPrototype> parseCyanCode(ICompiler_parsing cp) {

        Map<String, List<WrStatement>> mapMethodName_Body = new HashMap<>();
        Map<String, Object> fieldMemory = new HashMap<>();
        List<WrStatementImport> importList = new ArrayList<>();

        while (cp.getSymbol().token != Token.EOLO) {
            WrSymbol sym = cp.getSymbol();
            if ( sym.token == Token.IMPORT ) {
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
                    importList.add(
                            (new StatementImport((ExprIdentStar) meta.GetHiddenItem.getHiddenExpr(importPackage), null))
                                    .getI());
                }
                if ( cp.getSymbol().token == Token.SEMICOLON ) {
                    cp.next();
                }
            }
            else if ( sym.token == Token.VAR ) {
                cp.next();
                WrExpr typeVar = cp.type();
                String id = null;
                if ( cp.getSymbol().token == Token.IDENT ) {
                    // 'var Type id' or 'var Type id = expr'
                    id = cp.getSymbol().getSymbolString();
                    cp.next();

                }
                else {
                    // type was in fact the 'id'
                    if ( !(typeVar instanceof WrExprIdentStar) ) {
                        Tuple2<String, InterpreterPrototype> t = new Tuple2<>("In line "
                                + cp.getSymbol().getLineNumber() + ", after 'var' it was expected something like "
                                + "'Type id' or 'Type id = expr' or 'id = expr'", null);

                        return t;
                    }
                    id = ((WrExprIdentStar) typeVar).asString();

                }
                Object ins;
                Ref<String> errMsg = new Ref<>();
                try {
                    EvalEnv.loadBasicCyanPrototypes((String msg) -> {
                        errMsg.elem = "Error when loading basic Cyan prototypes";
                    });
                    ins = EvalEnv.cyStringConstructor.newInstance("no value");
                    if ( errMsg.elem != null ) {
                        Tuple2<String, InterpreterPrototype> t = new Tuple2<>(errMsg.elem, null);

                        return t;
                    }
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    Tuple2<String, InterpreterPrototype> t = new Tuple2<>(
                            "When interpreting Cyan code, failed to create a String", null);

                    return t;
                }
                fieldMemory.put(id, ins);
                if ( cp.getSymbol().token != Token.SEMICOLON ) {
                    Tuple2<String, InterpreterPrototype> t = new Tuple2<>(
                            "In line " + cp.getSymbol().getLineNumber() + ", ';' expected after variable declaration",
                            null);

                    return t;
                }
                cp.next();
            }
            else if ( sym.token == Token.FUNC ) {
                cp.next();
                if ( cp.getSymbol().token != Token.IDENT ) {
                    Tuple2<String, InterpreterPrototype> t = new Tuple2<>(
                            "In line " + cp.getSymbol().getLineNumber() + ", identifier expected after 'func'", null);

                    return t;
                }
                String funcName = cp.getSymbol().getSymbolString();
                cp.next();

                List<WrStatement> statList = new ArrayList<>();
                if ( cp.getSymbol().token != Token.LEFTCB ) {
                    Tuple2<String, InterpreterPrototype> t = new Tuple2<>(
                            "In line " + cp.getSymbol().getLineNumber() + ", '{' expected after the method name", null);

                    return t;
                }
                cp.next();
                while (cp.getSymbol().token != Token.EOLO && cp.getSymbol().token != Token.RIGHTCB) {
                    WrStatement lastStat = cp.statement();
                    statList.add(lastStat);
                    cp.removeLastExprStat();
                    if ( lastStat.demandSemicolon() ) {
                        if ( cp.getSymbol().token == Token.SEMICOLON ) {
                            cp.next();
                        }
                        else {
                            WrSymbol symb = cp.getSymbol();
                            if ( sym.token == Token.EOF || sym.token == Token.EOLO ) {
                                sym = lastStat.getFirstSymbol();
                            }
                            throw new CompileErrorException("Error in line " + symb.getLineNumber() + "("
                                    + symb.getColumnNumber() + "): ';' expected");
                        }
                    }
                }
                if ( cp.getSymbol().token != Token.RIGHTCB ) {
                    Tuple2<String, InterpreterPrototype> t = new Tuple2<>(
                            "In line " + cp.getSymbol().getLineNumber() + ", '}' expected after method body", null);

                    return t;
                }
                cp.next();
                mapMethodName_Body.put(funcName, statList);

            }
            else {
                Tuple2<String, InterpreterPrototype> t = new Tuple2<>(
                        "In line " + cp.getSymbol().getLineNumber()
                                + ", 'func' or 'var' was expected instead of symbol '" + sym.getSymbolString() + "'",
                        null);
                return t;
            }

        }
        InterpreterPrototype ip = new InterpreterPrototype();
        ip.fieldMemory = fieldMemory;
        ip.mapMethodName_Body = mapMethodName_Body;
        ip.importList = importList;
        return new Tuple2<>(null, ip);
    }

    public static List<WrStatement> parseCyanStatementList(ICompiler_parsing cp) {
        List<WrStatement> statList = new ArrayList<>();
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

        while (cp.getSymbol().token != Token.EOLO && cp.getSymbol().token != Token.RIGHTCB) {
            WrStatement lastStat = cp.statement();
            statList.add(lastStat);
            cp.removeLastExprStat();
            if ( lastStat.demandSemicolon() ) {
                if ( cp.getSymbol().token == Token.SEMICOLON ) {
                    cp.next();
                }
                else {
                    WrSymbol sym = cp.getSymbol();
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

    static public Object evalCode(List<WrStatement> statList, WrEvalEnv ee) {

        if ( statList != null && statList.size() > 0 ) {
            for (WrStatement is : statList) {
                try {
                    is.eval(ee);
                } catch (InterpretationErrorException e) {
                    // do nothing
                    throw e;
                } catch (ReturnValueEvalEnvException e) {
                    return ee.getReturnValue();
                } catch (final Throwable e) {
                    WrSymbol sym = is.getFirstSymbol();
                    ee.error(sym, "Exception '" + e.getClass().getCanonicalName() + "' was thrown. Its message is '"
                            + e.getMessage() + "'.");
                }

            }
        }

        return null;
    }

    /**
     * return true if the prototype name is one of the basic types or Array or Tuple
     */
    public static boolean isSelectedCyanLangProtototype(String prototypeName) {
        if ( NameServer.isBasicPrototype_andString(prototypeName) ) {
            return true;
        }
        else {
            int indexDot = prototypeName.indexOf('.');
            int indexLessThan = prototypeName.indexOf('<');
            if ( indexDot >= 0 && indexDot < indexLessThan ) {
                // cyan.Lang.Tuple<...>
                String s = prototypeName.substring(0, indexLessThan);
                int i = s.lastIndexOf('.');
                prototypeName = prototypeName.substring(i + 1);
            }
            else if ( indexDot < 0 && indexLessThan >= 0 ) {
                prototypeName = prototypeName.substring(0, indexLessThan);
            }
            return prototypeName.equals("Array") || prototypeName.equals("Tuple");
        }
    }

    // name of the Cyan language package
    public static final String cyanLanguagePackageName = "cyan.lang";
    // name of the Cyan language package
    public static final String cyanLanguagePackageNameDot = "cyan.lang.";
    /**
     * Name of the Cyan Exception prototype
     */
    public static final String cyExceptionPrototype = "CyException";
    public static final String popCompilationContextName = "popCompilationContext";
    public static final String pushCompilationContextName = "pushCompilationContext";
    public static final String pushCompilationContextStatementName = "pushCompilationContextStatement";
    public static final String initShared = "initShared";
    public static final String markDeletedCodeName = "markDeletedCode";
    // name of the Cyan language directory
    public static String cyanLanguagePackageDirectory = "cyan" + File.separator + "lang";
    public static final int sizeCyanLanguagePackageName_p_Dot = NameServer.cyanLanguagePackageName_p_Dot.length();
    // the extension of Cyan source files should be "cyan" as in "ChooseFoldersCyanInstallation.cyan"
    static final public String cyanSourceFileExtension = "cyan";
    // the extension of Pyan source files should be "pyan" as in "myProject.pyan"
    static final public String pyanSourceFileExtension = "pyan";
    // dot cyanSourceFileExtension
    // the extension of Cyan source files should be "cyan" as in "ChooseFoldersCyanInstallation.cyan"
    static final public String dotCyanSourceFileExtension = ".cyan";
    static final public int sizeCyanSourceFileExtensionPlusOne = MetaHelper.cyanSourceFileExtension.length() + 1;
    /**
     * name of type DYN
     */
    public static final String dynName = "Dyn";
    public static String noneArgumentNameForFunctions = "none";
    static final public String javaName_assign = MetaHelper.getJavaNameOfMethodWith("assign:", 1);
    // "nil" in Java
    public static final String NilInJava = MetaHelper.getJavaName("Nil");
    public static final String BooleanInJava = MetaHelper.getJavaName("Boolean");
    public static final String CharInJava = MetaHelper.getJavaName("Char");
    public static final String ByteInJava = MetaHelper.getJavaName("Byte");
    public static final String ShortInJava = MetaHelper.getJavaName("Short");
    public static final String IntInJava = MetaHelper.getJavaName("Int");
    public static final String LongInJava = MetaHelper.getJavaName("Long");
    public static final String FloatInJava = MetaHelper.getJavaName("Float");
    public static final String DoubleInJava = MetaHelper.getJavaName("Double");
    public static final String StringInJava = MetaHelper.getJavaName("String");
    // public static final String CySymbolInJava = getJavaName("CySymbol");
    public static final String AnyInJava = MetaHelper.getJavaName("Any");
    static final public String javaName_eq = MetaHelper.getJavaNameOfMethodWith("eq:", 1);
    public static Hashtable<String, String> cyanJavaBasicTypeTable;
    public static String suffixTestPackageName = "_ut";
    public static String prefixNonPackageDir = "--";
    public static final int MAX_NUM_TUPLE_ELEMS_INTERPRETED_CYAN = 7;

    public static final String cyanLangJarFileName = "cyan.lang.jar";
    public static final String cyanLangRuntimeFileName = "cyanruntime.jar";

    private static String cyanLangDir = null;

    public static final String extensionMyanFile = "myan";
    public static final String dotExtensionMyanFile = ".myan";
    public static final String maxnumroundsfixmetastr = "maxnumroundsfixmetastr";
    public static final int maxNumRoundsFixMetaDefaultValue = 5;

    /**
     * seconds a metaobject method can take to run
     */
    public static final int timeoutMillisecondsMetaobjectsDefaultValue = -1; // 2000;
    public static final String timeoutMillisecondsMetaobjectsStr = "timeoutMillisecondsMetaobjects";

}
