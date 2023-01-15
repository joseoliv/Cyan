/**
 *
 */

package saci;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import ast.Annotation;
import ast.AnnotationAt;
import ast.AnnotationMacroCall;
import ast.CodeWithError;
import ast.CompilationUnit;
import ast.CompilationUnitDSL;
import ast.CompilationUnitSuper;
import ast.ContextParameter;
import ast.CyanPackage;
import ast.Expr;
import ast.ExprFunction;
import ast.ExprIdentStar;
import ast.ExprLiteral;
import ast.FieldDec;
import ast.GenericParameter;
import ast.GetNameAsInSourceCode;
import ast.INextSymbol;
import ast.InterfaceDec;
import ast.JVMPackage;
import ast.MethodDec;
import ast.MethodKeywordWithParameters;
import ast.MethodSignature;
import ast.MethodSignatureOperator;
import ast.MethodSignatureUnary;
import ast.MethodSignatureWithKeywords;
import ast.ObjectDec;
import ast.PWCharArray;
import ast.ParameterDec;
import ast.Prototype;
import ast.SlotDec;
import ast.Statement;
import ast.StatementList;
import ast.StatementLocalVariableDec;
import ast.StatementTry;
import ast.Type;
import ast.TypeJavaRef;
import ast.VariableDecInterface;
import cyan.reflect._CyanMetaobject;
import error.CompileErrorException;
import error.ErrorInMetaobjectException;
import error.ErrorKind;
import error.UnitError;
import lexer.CompilerPhase;
import lexer.Symbol;
import lexer.SymbolCyanAnnotation;
import meta.CompilationPhase;
import meta.CompilationStep;
import meta.CyanMetaobject;
import meta.CyanMetaobjectError;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.IActionFunction;
import meta.IAction_semAn;
import meta.ISlotSignature;
import meta.LocalVarInfo;
import meta.MetaHelper;
import meta.SourceCodeChangeAddText;
import meta.SourceCodeChangeByAnnotation;
import meta.SourceCodeChangeDeleteText;
import meta.SourceCodeChangeShiftPhase;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrPrototype;
import meta.WrType;
import meta.lexer.MetaLexer;

/**
 * This class provides the environment in which a semantic analysis is made.
 * That is, is provides the symbol table and an error list.
 *
 * An object of this class is passed as a parameter to every method that does
 * semantic analysis. Every object of the ast passes it way down in the
 * hierarchy, updating the symbol table appropriately. For example, if a method
 * public void check(Env env) of class MethodDec does semantic analysis, it adds
 * to the symbol table the parameters of the method (and at the end it removes
 * them). Parameter env is passed to "check" methods of objects referenced by
 * "this" that do semantic analysis.
 *
 * It is also used for collecting all instantiations of generic objects.
 *
 * @author José
 *
 */
public class Env implements Cloneable {

    public Env(Project project) {
        this.project = project;
        publicSourceFileNameTable = new Hashtable<>();
        privatePrototypeTable = new Hashtable<>();
        slotDecTable = new Hashtable<>();
        variableDecTable = new Hashtable<>();
        functionList = new ArrayList<>();
        variableDecStack = new Stack<>();
        functionStack = new Stack<>();
        statementStack = new Stack<>();
        enclosingObjectDec = null;
        thereWasError.elem = false;
        compInstSet = new HashSet<>();
        this.setOfChanges = new HashMap<>();
        cyanMetaobjectCompilationContextStack = new Stack<>();
        lineMessageList = null;
        metaobjectAnnotationParseWithCompilerStack = new Stack<>();
        compilationUnitToWriteList = null;
        stackVariableLevel = new Stack<>();
        creatingInnerPrototypesInsideEval = false;
        offsetPushCompilationContextStack = new Stack<>();
        lineShift = 0;
        mapPackageSpacePrototypeNameToSubprototypeList = null;
        mapPackageSpaceInterfaceNameToSubinterfaceList = null;
        mapPackageSpaceInterfaceNameToImplementedList = null;
        mapCompUnitErrorList = null;
        stackLocalVarInfo = new Stack<>();
        repetitionStatStack = new Stack<>();
        controlFlowStack = new Stack<>();
        mapNewTypeToType = null;
        checkUsePossiblyNonInitializedPrototypeStack = new Stack<>();
        checkUsePossiblyNonInitializedPrototypeStack.push(false);
        /*
        														 * if an
        														 * instance
        														 * variable is
        														 * added here,
        														 * maybe it
        														 * should be
        														 * added to the
        														 * clone method
        														 * too.
        														 */
    }

    @SuppressWarnings("unchecked")
    @Override
    public Env clone() {

        try {
            final Env newObj = (Env) super.clone();

            newObj.stackVariableLevel = (Stack<Tuple3<VariableDecInterface, String, Integer>>) newObj.stackVariableLevel
                    .clone();
            if ( newObj.cyException != null ) {
                newObj.cyException = newObj.cyException.clone();
            }
            newObj.variableDecTable = (Hashtable<String, VariableDecInterface>) newObj.variableDecTable.clone();
            newObj.variableDecStack = (Stack<VariableDecInterface>) newObj.variableDecStack.clone();
            newObj.slotDecTable = (Hashtable<String, SlotDec>) newObj.slotDecTable.clone();
            newObj.privatePrototypeTable = (Hashtable<String, Prototype>) newObj.privatePrototypeTable.clone();
            if ( newObj.genericPrototypeFormalParameterTable != null ) {
                newObj.genericPrototypeFormalParameterTable = (Hashtable<String, GenericParameter>) newObj.genericPrototypeFormalParameterTable
                        .clone();
            }
            // do not clone currentCompilationUnit
            /*
             * if ( newObj.currentCompilationUnit != null ) {
             * newObj.currentCompilationUnit =
             * newObj.currentCompilationUnit.clone(); }
             */
//			if ( newObj.currentPrototype != null ) {
//				newObj.currentPrototype = newObj.currentPrototype.clone();
//			}
            newObj.functionList = (List<ExprFunction>) ((ArrayList<ExprFunction>) newObj.functionList).clone();
            newObj.statementStack = (Stack<CodeWithError>) newObj.statementStack.clone();
            newObj.functionStack = (Stack<ExprFunction>) newObj.functionStack.clone();
            newObj.cyanMetaobjectCompilationContextStack = (Stack<Tuple7<String, String, String, String, Integer, Integer, List<ISlotSignature>>>) newObj.cyanMetaobjectCompilationContextStack
                    .clone();
            newObj.metaobjectAnnotationParseWithCompilerStack = (Stack<Annotation>) newObj.metaobjectAnnotationParseWithCompilerStack
                    .clone();
            if ( newObj.compilationUnitToWriteList != null ) {
                newObj.compilationUnitToWriteList = (HashSet<CompilationUnit>) newObj.compilationUnitToWriteList
                        .clone();
            }
            newObj.offsetPushCompilationContextStack = (Stack<Integer>) newObj.offsetPushCompilationContextStack
                    .clone();
            if ( newObj.prototypeForGenericPrototypeList != null ) {
                newObj.prototypeForGenericPrototypeList = (ArrayList<Prototype>) ((ArrayList<Prototype>) newObj.prototypeForGenericPrototypeList)
                        .clone();
            }
            if ( stackLocalVarInfo != null ) {
                newObj.stackLocalVarInfo = stackLocalVarInfo;
            }
            if ( repetitionStatStack != null ) {
                newObj.repetitionStatStack = repetitionStatStack;
            }
            newObj.mapNewTypeToType = mapNewTypeToType;
            return newObj;
        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }

    public WrEnv getI() {
        return new WrEnv(this, this.getCompilationStep());
    }

    /**
     * number used to generate unique identifiers in Cyan programs
     */
    private static int magicIdNumber = 1;

    /**
     * unique name for local variable, shared variable or field of
     * the current prototype
     *
     * @return
     */
    public String getNewUniqueVariableName() {
        if ( this.currentCompilationUnit == null || this.currentPrototype == null ) {
            return null;
        }
        else {
            String name;
            while (true) {
                name = "var" + Env.magicIdNumber++;
                if ( this.currentMethod == null ) {
                    if ( this.searchField(name) == null ) {
                        break;
                    }
                }
                else if ( this.searchLocalVariableParameter(name) == null && this.searchField(name) == null ) {
                    break;
                }
            }
            return name;
        }
    }

    /**
     * execute the SEM_AN actions asked through the call to methods
     * {@link #addCodeAtAnnotation(CyanMetaobject, StringBuffer, boolean)},
     * {@link #removeCodeAnnotation(CyanMetaobject)}, and
     * {@link #addSuffixToChange(CyanMetaobject)}.
     */
    public void semAn_actions() {
        Saci.makeChanges(setOfChanges, this, CompilationPhase.semAn);
    }

    /**
     * add code produced by <code>cyanMetaobject</code> after the call of one of the metaobject methods.
     * It is assumed that the annotation of the metaobject is in the current compilation unit.
     * The code <code>codeToAdd</code> is added at offset
     * <code>offsetToAdd</code> in the compilation unit of the metaobject annotation.
     *
     * @param cyanMetaobject
     * @param codeToAdd
     * @return
     */
    public String addCodeAtAnnotation(CyanMetaobject cyanMetaobject, StringBuffer codeToAdd, int offsetToAdd) {

        final Annotation cyanAnnotation = meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
        final CompilationUnitSuper compilationUnitAnnotation = cyanAnnotation.getCompilationUnit();
        /*
         * add change to the list of changes
         */
        List<SourceCodeChangeByAnnotation> changeList = setOfChanges.get(compilationUnitAnnotation);
        if ( changeList == null ) {
            changeList = new ArrayList<>();
            setOfChanges.put(compilationUnitAnnotation, changeList);
        }
        final String code = " " + Env.getCodeToAddWithContext(cyanMetaobject, codeToAdd.toString(), null, null) + " ";
        /*
        if ( cyanAnnotation.getInExpr() ) {
        	code = " ( " + code + " ) ";
        }
        */

        // if ( offsetToAdd < 0 ) offsetToAdd =
        // cyanAnnotation.getNextSymbol().getOffset() - 1;
        final Symbol lastSymbol = cyanAnnotation.getLastSymbol();
        if ( offsetToAdd < 0 ) {
            if ( lastSymbol instanceof SymbolCyanAnnotation ) {
                offsetToAdd = lastSymbol.getOffset() + lastSymbol.getSymbolString().length() + 1;
            }
            else {
                offsetToAdd = lastSymbol.getOffset() + lastSymbol.getSymbolString().length();
            }
        }
        String annotStr = "" + lastSymbol.getCompilationUnit().getText()[offsetToAdd];
        annotStr = lastSymbol.getSymbolString() + "  " + lastSymbol.getSymbolString().length();
        // for (int k = 0; k < )
        changeList.add(new SourceCodeChangeAddText(offsetToAdd, new StringBuffer(code), cyanAnnotation));

        return code;
    }

    /**
     * remove the code of a metaobject annotation
     *
     * @return
     */
    public boolean removeCodeAnnotation(CyanMetaobject cyanMetaobject) {

        if ( cyanMetaobject instanceof IAction_semAn ) {
            // IAction_semAn cyanMetaobjectCodeGen = (IAction_semAn )
            // cyanMetaobject;

            final Annotation cyanAnnotation = meta.GetHiddenItem
                    .getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
            final CompilationUnitSuper compilationUnitAnnotation = cyanAnnotation.getCompilationUnit();
            /*
             * add change to the list of changes
             */
            List<SourceCodeChangeByAnnotation> changeList = setOfChanges.get(compilationUnitAnnotation);
            if ( changeList == null ) {
                changeList = new ArrayList<>();
                setOfChanges.put(compilationUnitAnnotation, changeList);
            }

            final int offsetStart = cyanMetaobject.getAnnotation().getFirstSymbol().getOffset();

            final Annotation annotation = meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
            if ( annotation instanceof AnnotationMacroCall ) {
                /*
                 * this instanceof should not be here. I know that.
                 */
                final AnnotationMacroCall macroCall = (AnnotationMacroCall) annotation;

                final Symbol lastSymbol = macroCall.getLastSymbolMacroCall();
                final int offsetEndLastMacroSymbol = lastSymbol.getOffset() + lastSymbol.getSymbolString().length();

                final SourceCodeChangeByAnnotation thisChange = new SourceCodeChangeDeleteText(offsetStart,
                        offsetEndLastMacroSymbol - offsetStart, annotation);

                int numSourceCodeChanges = 1;
                changeList.add(thisChange);
                final int numLinesToDelete = this.numLinesBetween(compilationUnitAnnotation.getText(), offsetStart,
                        offsetEndLastMacroSymbol);

                if ( numLinesToDelete > 0 ) {
                    changeList.add(new meta.SourceCodeChangeAddText(offsetStart,
                            new StringBuffer(" @" + MetaHelper.markDeletedCodeName + "(" + numLinesToDelete + ") "),
                            annotation));
                    ++numSourceCodeChanges;
                }
                /**
                 * remove all source code changes asked by metaobjects, macros,
                 * or message sends (compile-time does not understand).
                 */
                // int sizeChangeList = changeList.size();
                Env.removeChangesFromTo(changeList, offsetStart, offsetEndLastMacroSymbol, numSourceCodeChanges);

            }
            else {
                int offsetNext = cyanMetaobject.getAnnotation().getNextSymbol().getOffset();
                /*
                 * keep the spaces before the next token till the start of the
                 * line
                 */
                final char[] text = compilationUnitAnnotation.getText();
                int i = offsetNext - 1;
                while (i > 0 && Character.isWhitespace(text[i]) && text[i] != '\n') {
                    --i;
                }
                if ( i >= 0 && text[i] == '\n' || i > 0 && !Character.isWhitespace(text[i]) ) {
                    offsetNext = i + 1;
                }

                final SourceCodeChangeByAnnotation thisChange = new SourceCodeChangeDeleteText(offsetStart,
                        offsetNext - offsetStart, annotation);
                changeList.add(thisChange);
                int numSourceCodeChanges = 1;
                final int numLinesToDelete = this.numLinesBetween(compilationUnitAnnotation.getText(), offsetStart,
                        offsetNext);

                if ( numLinesToDelete > 0 ) {
                    changeList.add(new meta.SourceCodeChangeAddText(offsetStart,
                            new StringBuffer(" @" + MetaHelper.markDeletedCodeName + "(" + numLinesToDelete + ") "),
                            annotation));
                    ++numSourceCodeChanges;
                }
                /**
                 * remove all source code changes asked by metaobjects, macros,
                 * or message sends (compile-time does not understand).
                 */
                Env.removeChangesFromTo(changeList, offsetStart, offsetNext, numSourceCodeChanges);

            }

        }
        return true;
    }

    public boolean replaceMessageSendByExpression() {
        return true;
    }

    /**
     * remove the code of unary message send unaryMessageSend that is in offset
     * offsetToAdd of the text of compilationUnit. This is being asked by
     * metaobject annotation annotation. The message send is replaced by codeToAdd whose
     * type is codeType
     *
     */
    /*
    public boolean removeAddCodeExprIdentStar(ExprIdentStar unaryMessageSend, CompilationUnitSuper compilationUnit,
    		AnnotationAt annotation, StringBuffer codeToAdd, Type codeType, int offsetToAdd) {

    	if ( !removeCodeExprMessageSend(unaryMessageSend, compilationUnit, annotation) ) {
    		return false;
    	}
    	unaryMessageSend.setCodeThatReplacesThisExpr(codeToAdd);

    	return addCodeAtMessageSend(annotation, compilationUnit, codeToAdd, codeType, offsetToAdd);
    }
    */

    /**
     * remove the code of the statement 'stat' of the text of compilationUnit. This is being asked by
     * metaobject annotation 'annotation'. The statement is replaced by 'code' whose
     * type is codeType
     *
     * @param stat
     * @param compilationUnit
     * @param annotation
     * @param code
     * @param offsetToAdd
     * @return
     */
    public boolean replaceStatementByCode(Statement stat, AnnotationAt annotation, StringBuffer code, Type codeType) {

        final CompilationUnitSuper cunit = stat.getFirstSymbol().getCompilationUnit();
        if ( !removeCodeExpr(stat, cunit, annotation) ) {
            return false;
        }
        stat.setCodeThatReplacesThisExpr(code);

        return addCodeAtOffset(annotation, cunit, code, codeType, stat.getFirstSymbol().getOffset());
    }

    public boolean removeAddCodeFromToOffset(Statement statement, int offsetNext, AnnotationAt annotation,
            StringBuffer codeToAdd, Type codeType) {

        final CompilationUnitSuper cunit = statement.getFirstSymbol().getCompilationUnit();
        if ( !removeCodeFromToOffset(statement.getFirstSymbol().getOffset(), offsetNext, cunit, annotation) ) {
            return false;
        }
        statement.setCodeThatReplacesThisExpr(codeToAdd);

        return addCodeAtOffset(annotation, cunit, codeToAdd, codeType, statement.getFirstSymbol().getOffset());
    }

    /**
     * remove the code of expression/anything 'expr' of the text of compilationUnit. This is being asked by
     * metaobject annotation 'annotation'.
     *
     */
    private boolean removeCodeExpr(INextSymbol expr, CompilationUnitSuper compilationUnit, AnnotationAt annotation) {

        final int offsetStart = expr.getFirstSymbol().getOffset();

        final int offsetNext = expr.getNextSymbol().getOffset();

        return removeCodeFromToOffset(offsetStart, offsetNext, compilationUnit, annotation);
    }

    /**
     * remove the code of expression/anything from offsetStart to offsetNext of the
     * text of compilationUnit. This is being asked by metaobject annotation 'annotation'.
     *
     */
    private boolean removeCodeFromToOffset(int offsetStart, int offsetNext, CompilationUnitSuper compilationUnit,
            AnnotationAt annotation) {

        /*
         * add change to the list of changes
         */
        List<SourceCodeChangeByAnnotation> changeList = setOfChanges.get(compilationUnit);
        if ( changeList == null ) {
            changeList = new ArrayList<>();
            setOfChanges.put(compilationUnit, changeList);
        }

        /*
         *
         */
        final char[] text = compilationUnit.getText();
        int i = offsetNext - 1;
        while (i > 0 && Character.isWhitespace(text[i])) {
            --i;
        }
        /*
         * since this method is removing a message send, necessarily i > 0 and
         * text[i] is not a white space
         *
         */
        if ( i == 0 ) {
            return false;
        }
        offsetNext = i + 1;

        final SourceCodeChangeByAnnotation thisChange = new SourceCodeChangeDeleteText(offsetStart,
                offsetNext - offsetStart, annotation);
        changeList.add(thisChange);
        int numSourceCodeChanges = 1;
        final int numLinesToDelete = this.numLinesBetween(text, offsetStart, offsetNext);

        if ( numLinesToDelete > 0 ) {
            changeList.add(new meta.SourceCodeChangeAddText(offsetStart,
                    new StringBuffer(" @" + MetaHelper.markDeletedCodeName + "(" + numLinesToDelete + ") "),
                    annotation));
            ++numSourceCodeChanges;
        }

        /**
         * remove all source code changes asked by metaobjects or message sends
         * (compile-time does not understand).
         */
        Env.removeChangesFromTo(changeList, offsetStart, offsetNext, numSourceCodeChanges);

        return true;
    }

    private boolean addCodeAtOffset(Annotation cyanAnnotation, CompilationUnitSuper compilationUnit,
            StringBuffer codeToAdd, Type codeType, int offsetToAdd) {

        final CyanMetaobject cyanMetaobject = cyanAnnotation.getCyanMetaobject();
        /*
         * add change to the list of changes
         */
        List<SourceCodeChangeByAnnotation> changeList = setOfChanges.get(compilationUnit);
        if ( changeList == null ) {
            changeList = new ArrayList<>();
            setOfChanges.put(compilationUnit, changeList);
        }
        String code = " " + Env.getCodeToAddWithContext(cyanMetaobject, codeToAdd.toString(), codeType, null);
        if ( cyanAnnotation.getInExpr() ) {
            code = " ( " + code + " ) ";
        }

        changeList.add(new SourceCodeChangeAddText(offsetToAdd, new StringBuffer(code), cyanAnnotation));

        return true;
    }

    private static void removeChangesFromTo(List<SourceCodeChangeByAnnotation> changeList, int offsetStart,
            int offsetNext, int numSourceCodeChanges) {
        final List<Integer> indexToDelete = new ArrayList<>();
        for (int i = 0; i < changeList.size() - numSourceCodeChanges; ++i) {
            final SourceCodeChangeByAnnotation change = changeList.get(i);
            if ( change.offset >= offsetStart && change.offset < offsetNext ) {
                indexToDelete.add(i);
            }
        }
        for (int i = indexToDelete.size() - 1; i >= 0; --i) {
            changeList.remove((int) indexToDelete.get(i));
        }
    }

    /**
     * add a metaobject annotation that should change the suffix. That is, something
     * like <code>{@literal @}myMO(10)</code> should be changed to
     * <code>{@literal @}myMO#semAn(10)</code>.
     */
    public boolean addSuffixToChange(CyanMetaobject cyanMetaobject) {

        if ( cyanMetaobject instanceof IAction_semAn ) {
            final Annotation cyanAnnotation = meta.GetHiddenItem
                    .getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
            final CompilationUnitSuper compilationUnitAnnotation = cyanAnnotation.getCompilationUnit();
            /*
             * add change to the list of changes
             */
            List<SourceCodeChangeByAnnotation> changeList = setOfChanges.get(compilationUnitAnnotation);
            if ( changeList == null ) {
                changeList = new ArrayList<>();
                setOfChanges.put(compilationUnitAnnotation, changeList);
            }
            changeList.add(new SourceCodeChangeShiftPhase(compilationUnitAnnotation.getText(), this,
                    cyanAnnotation.getFirstSymbol().getOffset(), CompilerPhase.SEM_AN, compilationUnitAnnotation,
                    cyanAnnotation));
        }
        return true;
    }

    /**
     * for each compilation unit that should be changed by metaobjects or by
     * adding "#SEM_AN" to metaobject annotations, there is associated list of code
     * changes
     */

    private HashMap<CompilationUnitSuper, List<SourceCodeChangeByAnnotation>> setOfChanges = new HashMap<>();

    public GenericParameter getGenericPrototypeFormalParameter(String key) {
        return genericPrototypeFormalParameterTable.get(key);
    }

    public GenericParameter addGenericPrototypeFormalParameter(String key, GenericParameter genericParameter) {
        if ( genericParameter.isRealPrototype() ) {
            return null;
        }
        else {
            return genericPrototypeFormalParameterTable.put(key, genericParameter);
        }

    }

    /**
     * searches for <code>methodSignature</code> in
     * <code>methodSignatureList</code>, a list of method signatures that have
     * the same name as <code>methodSignature</code>. The signature found in
     * <code>methodSignatureList</code> is returned. A method signature is
     * considered equal to other if the keywords are the same and the parameter
     * types too.
     *
     * @param methodSignatureList
     * @param env
     * @return
     */
    public MethodSignature searchMethodSignature(MethodSignature methodSignature,
            List<MethodSignature> methodSignatureList) {

        if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {
            return null;
        }
        if ( methodSignature instanceof MethodSignatureUnary ) {
            if ( methodSignatureList.get(0).getReturnType(this) == methodSignature.getReturnType(this) ) {
                return methodSignatureList.get(0);
            }
            else {
                return null;
            }
        }
        else if ( methodSignature instanceof MethodSignatureOperator ) {
            final MethodSignatureOperator methodSignatureOperator = (MethodSignatureOperator) methodSignature;
            if ( methodSignatureOperator.getOptionalParameter() != null ) {
                // binary
                removeAllLocalVariableDec();
                methodSignatureOperator.calcInterfaceTypes(this);

                final Type paramType = methodSignatureOperator.getOptionalParameter().getType(this);
                final Type returnType = methodSignatureOperator.getReturnType(this);
                for (final MethodSignature ms : methodSignatureList) {
                    if ( ms instanceof MethodSignatureOperator ) {
                        // this should always be true
                        final MethodSignatureOperator msOther = (MethodSignatureOperator) ms;
                        if ( msOther.getOptionalParameter() != null
                                && msOther.getOptionalParameter().getType(this) == paramType
                                && msOther.getReturnType(this) == returnType ) {
                            return ms;
                        }
                    }
                }
                return null;
            }
            else {
                // unary operator
                for (final MethodSignature ms : methodSignatureList) {

                    if ( ms instanceof MethodSignatureOperator
                            && ((MethodSignatureOperator) ms).getOptionalParameter() == null ) {
                        if ( ms.getReturnType(this) == methodSignature.getReturnType(this) ) {
                            return ms;
                        }
                        else {
                            return null;
                        }
                    }
                }
                return null;
            }
        }
        else if ( methodSignature instanceof MethodSignatureWithKeywords ) {
            final MethodSignatureWithKeywords msng = (MethodSignatureWithKeywords) methodSignature;
            final List<MethodKeywordWithParameters> keywordList = msng.getKeywordArray();
            for (final MethodSignature ms : methodSignatureList) {
                removeAllLocalVariableDec();
                ms.calcInterfaceTypes(this);
                if ( ms instanceof MethodSignatureWithKeywords ) {
                    final MethodSignatureWithKeywords msother = (MethodSignatureWithKeywords) ms;
                    boolean allTypesEqual = true;
                    if ( keywordList.size() != msother.getKeywordArray().size() ) {
                        allTypesEqual = false;
                    }
                    else {
                        int n = 0;
                        final List<MethodKeywordWithParameters> keywordOtherList = msother.getKeywordArray();
                        for (final MethodKeywordWithParameters sel : keywordList) {
                            // for each keyword of methodSignature, try to
                            // match with keywordOtherList
                            final MethodKeywordWithParameters selOther = keywordOtherList.get(n);
                            if ( sel.getParameterList() != null && selOther.getParameterList() != null ) {
                                if ( sel.getParameterList().size() == selOther.getParameterList().size() ) {
                                    int i = 0;
                                    for (final ParameterDec param : sel.getParameterList()) {
                                        final ParameterDec paramOther = selOther.getParameterList().get(i);
                                        if ( param.getType(this) != paramOther.getType(this) ) {
                                            allTypesEqual = false;
                                            break;
                                        }
                                        ++i;
                                    }
                                }
                                else {
                                    allTypesEqual = false;
                                }
                            }
                            else if ( sel.getParameterList() == null && selOther.getParameterList() != null ) {
                                allTypesEqual = false;
                            }
                            else if ( sel.getParameterList() != null && selOther.getParameterList() == null ) {
                                allTypesEqual = false;
                            }

                            ++n;
                        }
                    }
                    if ( allTypesEqual && (ms.getReturnType(this) == methodSignature.getReturnType(this)) ) {
                        return ms;
                    }
                }
            }
            return null;
        }
        else {
            return null;
        }
    }

    /**
     * return the prototype whose name is prototypeName and that was imported by
     * the current compilation unit or was declared as 'public' in the current
     * compilation uniot. The prototype name is "Person" for prototype "object
     * Person ... end" and "Stack(Int)" for the instantiation Stack<Int>. For
     * short, the prototype name is the name of the file in which the public
     * prototype is.
     *
     *
     *
     * public Prototype searchPublicPrototype(String prototypeName) { return
     * publicPrototypeTable.get(prototypeName); }
     */

    public Object addSourceFileName(String key, Prototype value) {
        return publicSourceFileNameTable.put(key, value);
    }

    public Prototype searchPrivatePrototype(String key) {
        return privatePrototypeTable.get(key);
    }

    public Object addPrivatePrototype(String key, Prototype value) {
        return privatePrototypeTable.put(key, value);
    }

    public VariableDecInterface getLocalVariableDec(String key) {
        return variableDecTable.get(key);
    }

    /*
     * private StatementLocalVariableDec addLocalVariableDec(String key,
     * StatementLocalVariableDec value) { return variableDecTable.put(key,
     * value); }
     */

    public VariableDecInterface removeLocalVariableDec(String key) {
        return variableDecTable.remove(key);
    }

    public void removeAllLocalVariableDec() {
        variableDecTable.clear();
        variableDecStack.clear();
    }

    public SlotDec getSlotDec(String key) {
        return slotDecTable.get(key);
    }

    public SlotDec addSlotDec(String key, SlotDec value) {
        return slotDecTable.put(key, value);
    }

    @SuppressWarnings("hiding")
    public void atBeginningOfCurrentMethod(MethodDec currentMethod) {
        this.currentMethod = currentMethod;
        variableDecStack.clear();
        variableDecTable.clear();
    }

    public void atBeginningOfCurrentCompilationUnit(CompilationUnit currentCompilationUnit1) {

        this.currentCompilationUnit = currentCompilationUnit1;
        enclosingObjectDec = null;
        lineMessageList = currentCompilationUnit1.getLineMessageList();
        lineShift = 0;

        /**
         * add all private program units to table privatePrototypeTable
         */
        if ( currentCompilationUnit1.getPrototypeList() != null ) {
            // it is null in a .pyan file
            for (final Prototype prototype : currentCompilationUnit1.getPrototypeList()) {
                if ( !prototype.isGeneric() && prototype.getVisibility() == Token.PRIVATE ) {
                    this.addPrivatePrototype(prototype.getName(), prototype);
                }
            }

        }

    }

    public void atEndOfCurrentCompilationUnit() {

        currentCompilationUnit = null;
        privatePrototypeTable.clear();
        functionList.clear();
        if ( mapNewTypeToType != null ) {
            mapNewTypeToType.clear();
        }

    }

    public CompilationUnit getCurrentCompilationUnit() {
        return currentCompilationUnit;
    }

    public void addError(UnitError error) {
        thereWasError.elem = true;
        currentCompilationUnit.addError(error);

        List<UnitError> errorList;
        if ( mapCompUnitErrorList == null ) {
            mapCompUnitErrorList = new HashMap<>();
            errorList = new ArrayList<>();
            errorList.add(error);
        }
        else {
            errorList = mapCompUnitErrorList.get(this.currentCompilationUnit);
            if ( errorList == null ) {
                errorList = new ArrayList<>();
            }
            errorList.add(error);
        }
        mapCompUnitErrorList.put(this.currentCompilationUnit, errorList);

    }

    /**
     * If necessary, add a context message to the error message. This context
     * informs that the error was caused by code introduced by such and such
     * metaobject annotations OR by code introduced by the compiler.
     *
     * @param msg
     * @return
     */
    public static String addContextMessage(
            Stack<Tuple7<String, String, String, String, Integer, Integer, List<ISlotSignature>>> contextStack,
            String msg) {

        if ( !contextStack.isEmpty() ) {
            /*
             * there is a context. Then the code that caused this compilation
             * error was introduced by some metaobject annotation or by the compiler
             */
            Tuple7<String, String, String, String, Integer, Integer, List<ISlotSignature>> t = contextStack.peek();

            if ( t.f3 == null || t.f4 == null || t.f5 == null ) {
                msg = msg + " This internal error was caused by code introduced by the compiler in step '" + t.f2
                        + "'. Check the documentation of "
                        + meta.cyanLang.CyanMetaobjectCompilationContextPush.class.getName();
            }
            else {
                String cyanMetaobjectName = t.f2;
                String packageName = t.f3;
                String prototypeName = t.f4;
                int lineNumber = t.f5;
                msg = msg + " This error was caused by code introduced initially by metaobject annotation '"
                        + cyanMetaobjectName + "' at line " + lineNumber + " of " + packageName + "." + prototypeName;
                if ( contextStack.size() > 1 ) {
                    StringBuilder s = new StringBuilder(". The complete stack of ")
                            .append("context (metaobject name, package.prototype, line number) is: ");
                    for (int kk = 1; kk < contextStack.size(); ++kk) {
                        t = contextStack.get(kk);
                        cyanMetaobjectName = t.f2;
                        packageName = t.f3;
                        prototypeName = t.f4;
                        lineNumber = t.f5;
                        s.append("(").append(cyanMetaobjectName).append(", ").append(packageName).append(".")
                                .append(prototypeName).append(", ").append(lineNumber).append(") ");
                    }
                    msg = msg + s.toString();
                }
            }

        }
        return msg;
    }

    /**
     * If there was any previous call to a method of a metaobject implementing
     * {@link meta#IInformCompilationError} this method checks whether this
     * error message was foreseen. If it was not, a warning is signaled.
     *
     * @param sym
     * @param lineNumber
     * @param specificMessage
     * @return true if there was a previous call to a method of {@link meta#IInformCompilationError}
     */
    private boolean checkErrorMessage(Symbol sym, int lineNumber, String specificMessage) {

        if ( lineMessageList == null || lineMessageList.size() == 0 ) {
            return false;
        }
        else {
            /*
             * a method of  {@link meta#IInformCompilationError} has been called in this compilation
             * unit
             */
            int i = 0;
            boolean found = false;
            boolean throwCEE = false;
            for (final Tuple3<Integer, String, Boolean> t : lineMessageList) {

                if ( lineNumber < 0 && t.f1 < 0 || t.f1 == lineNumber ) {
                    try {
                        // found the correct line number
                        this.warning(sym, "The expected error message at line " + lineNumber + " was '" + t.f2
                                + "'. The message given by the compiler was '" + specificMessage + "'");
                    } catch (final CompileErrorException e) {
                        throwCEE = true;
                    }
                    lineMessageList.get(i).f3 = true;
                    found = true;
                    break;
                }
                ++i;
            }
            if ( !found ) {
                this.errorWithoutCheckingMetaobjectCompilationError(sym, "The compiler issued the error message '"
                        + specificMessage
                        + "'. However, no metaobject implementing 'IInformCompilationError' has foreseen this error",
                        true);
            }
            if ( throwCEE ) {
                throw new CompileErrorException("Error in line " + lineNumber + ": " + specificMessage);
                // throw new CompileErrorException(specificMessage);
            }
            return true;
        }
    }

    public void warning(Symbol sym, String msg) {
        error(sym, msg, false, false);
    }

    public void errorInMetaobjectCatchExceptions(CyanMetaobject cyanMetaobject) {
        final List<CyanMetaobjectError> errorList = cyanMetaobject.getErrorMessageList_cleanAll();
        if ( errorList != null ) {
            for (final CyanMetaobjectError moError : errorList) {
                try {
                    error(meta.GetHiddenItem.getHiddenSymbol(moError.getSymbol()), moError.getMessage());
                } catch (final error.CompileErrorException e) {
                    throw new ErrorInMetaobjectException(e.getMessage());
                } catch (meta.InterpretationErrorException e) {
                    return;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void errorInMetaobject(CyanMetaobject cyanMetaobject, Symbol symbolWithError) {
        _CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
        List<CyanMetaobjectError> errorList;
        if ( other == null ) {
            errorList = cyanMetaobject.getErrorMessageList_cleanAll();
        }
        else {

            errorList = other._getErrorList();
        }

        if ( errorList != null ) {
            for (final CyanMetaobjectError moError : errorList) {
                try {
                    if ( meta.GetHiddenItem.getHiddenCompilationUnitSuper(
                            moError.getSymbol().getCompilationUnit()) instanceof CompilationUnitDSL ) {
                        error(symbolWithError, moError.getMessage());
                    }
                    else {
                        error(moError.getSymbol() != null ? meta.GetHiddenItem.getHiddenSymbol(moError.getSymbol())
                                : symbolWithError, moError.getMessage());
                    }
                } catch (final error.CompileErrorException e) {

                }
            }
        }
    }

    public void thrownException(Annotation annotation, Symbol firstSymbol, RuntimeException e) {
        final String fileName = annotation.getCompilationUnit().getFilename();
        final String packageName = annotation.getPackageOfAnnotation();
        final int lineNumber = annotation.getFirstSymbol().getLineNumber();
        error(firstSymbol,
                "Metaobject annotation '" + annotation.getCyanMetaobject().getName() + "' at line number " + lineNumber
                        + " in file '" + fileName + "' of package '" + packageName + "' has thrown exception '"
                        + e.getClass().getName() + "'"
                        + (e.getMessage() != null ? ". The exception message is '" + e.getMessage() + "'" : ""));

    }

    public void error(int lineNumber, int columnNumber, String message) {
        thereWasError.elem = true;

        error(null, lineNumber, columnNumber, message, true, true);
    }

    public void error(Symbol symbol, String message) {
        thereWasError.elem = true;

        error(symbol, message, true, true);
    }

    public void error(Symbol symbol, String message, boolean checkMessage) {
        error(symbol, message, checkMessage, true);
    }

    // error(lineNumber, columnNumber, message, true, true);

    public void error(Symbol symbol, String message, boolean checkMessage, boolean throwException) {
        error(symbol, message, checkMessage, throwException, true);
    }

    public void error(Symbol symbol, String message, boolean checkMessage, boolean throwException,
            boolean shiftLineNumber) {
        thereWasError.elem = true;

        if ( symbol != null ) {
            if ( this.cyanMetaobjectCompilationContextStack.isEmpty() ) {
                if ( shiftLineNumber ) {
                    symbol.setLineNumber(symbol.getLineNumber() + this.lineShift);
                }
            }
            else {
                /*
                 * error was in code produced by the compiler, maybe with the
                 * help of metaobjects. Give to the user the line number of the
                 * file with the expanded source code
                 */
                message = message
                        + "\nLook for the error in the expanded source code, not in the original one. If the source code is 'ChooseFoldersCyanInstallation.cyan' of package 'main', the "
                        + "expanded source code should be in 'full-main-ChooseFoldersCyanInstallation.cyan' in the directory of the project.";
            }
        }

        message = Env.addContextMessage(this.cyanMetaobjectCompilationContextStack, message);

        if ( checkMessage && checkErrorMessage(symbol, symbol == null ? -1 : symbol.getLineNumber(), message) ) {
            return;
        }

        errorWithoutCheckingMetaobjectCompilationError(symbol, message, throwException);

    }

    public void error(Symbol symbol, int lineNumber, int columnNumber, String message, boolean checkMessage,
            boolean throwException) {
        thereWasError.elem = true;

        if ( symbol != null ) {
            if ( this.cyanMetaobjectCompilationContextStack.isEmpty() ) {
                symbol.setLineNumber(symbol.getLineNumber() - this.lineShift);
            }
            else {
                /*
                 * error was in code produced by the compiler, maybe with the
                 * help of metaobjects. Give to the user the line number of the
                 * file with the expanded source code
                 */
                message = "Look for the error in the expanded source code, not in the original one. If the source code is 'ChooseFoldersCyanInstallation.cyan' of package 'main', the "
                        + "expanded source code should be in 'full-main-ChooseFoldersCyanInstallation.cyan' in the directory of the project. \n"
                        + message;
            }
        }

        message = Env.addContextMessage(this.cyanMetaobjectCompilationContextStack, message);

        if ( checkMessage && checkErrorMessage(symbol, lineNumber, message) ) {
            return;
        }

        errorWithoutCheckingMetaobjectCompilationError(symbol, lineNumber, columnNumber, message, throwException);

    }

    public void error(CompilationUnit cunitError, Symbol symbol, String message) {
        thereWasError.elem = true;

        cunitError.error(symbol, symbol == null ? -1 : symbol.getLineNumber(), message, null, this);
    }

    /**
     * @param symbol
     * @param message
     * @param throwException
     *            TODO
     */
    private void errorWithoutCheckingMetaobjectCompilationError(Symbol symbol, String message, boolean throwException) {
        errorWithoutCheckingMetaobjectCompilationError(symbol, -1, 0, message, throwException);
        /*
        thereWasError.elem = true;
        if ( prefixErrorMessage != null ) {
        	message = prefixErrorMessage + message;
        	prefixErrorMessage = null;
        }

        if ( symbol == null ) {
        	if ( currentCompilationUnit != null ) {
        		currentCompilationUnit.error(symbol, -1, message, null, this);
        	}
        	else {
        		this.project.error(message);
        	}
        }
        else {
        	if ( symbol.getCompilationUnit() != null ) {
        		// error(true, symbol, lineNumber, msg);
        		CompilationUnitSuper cunit = symbol.getCompilationUnit();
        		try {
        			cunit.error(throwException, symbol, symbol.getLineNumber(), message, null, this);
        		}
        		finally {
        			UnitError lastError = cunit.getErrorList().get(cunit.getErrorList().size()-1);
        			List<UnitError> errorList;
        			if ( mapCompUnitErrorList == null ) {
        				mapCompUnitErrorList = new HashMap<>();
        				errorList = new ArrayList<>();
        				errorList.add(lastError);
        			}
        			else {
        				errorList = mapCompUnitErrorList.get(this.currentCompilationUnit);
        				if ( errorList == null ) {
        					errorList = new ArrayList<>();
        				}
        				errorList.add(lastError);
        			}
        			mapCompUnitErrorList.put(this.currentCompilationUnit,  errorList);
        		}



        	}
        	else {
        		this.project.error(message);
        	}
        }
        */
    }

    private void errorWithoutCheckingMetaobjectCompilationError(Symbol symbol, int lineNumber, int columnNumber,
            String message, boolean throwException) {
        thereWasError.elem = true;
        if ( prefixErrorMessage != null ) {
            message = prefixErrorMessage + message;
            prefixErrorMessage = null;
        }

        if ( symbol == null ) {
            if ( currentCompilationUnit != null ) {
                currentCompilationUnit.error(symbol, lineNumber, columnNumber, message, null, this);
            }
            else {
                this.project.error(message);
            }
        }
        else if ( symbol.getCompilationUnit() != null ) {
            // error(true, symbol, lineNumber, msg);
            final CompilationUnitSuper cunit = symbol.getCompilationUnit();
            try {
                cunit.error(throwException, symbol, symbol.getLineNumber(), message, null, this);
            } finally {
                if ( !(cunit instanceof CompilationUnit) ) {
                    final UnitError lastError = cunit.getErrorList().get(cunit.getErrorList().size() - 1);
                    List<UnitError> errorList;
                    if ( mapCompUnitErrorList == null ) {
                        mapCompUnitErrorList = new HashMap<>();
                        errorList = new ArrayList<>();
                        errorList.add(lastError);
                    }
                    else {
                        errorList = mapCompUnitErrorList.get(this.currentCompilationUnit);
                        if ( errorList == null ) {
                            errorList = new ArrayList<>();
                        }
                        errorList.add(lastError);
                    }
                    mapCompUnitErrorList.put(this.currentCompilationUnit, errorList);
                }
            }

        }
        else {
            this.project.error(message);
        }
    }

    public void error(boolean checkMessage, Symbol symbol, String specificMessage, String identifier,
            ErrorKind errorKind, String... furtherArgs) {

        if ( (symbol != null) && this.cyanMetaobjectCompilationContextStack.isEmpty() ) {
            symbol.setLineNumber(symbol.getLineNumber() - this.lineShift);
            /*
             * else { / * error was in code produced by the compiler, maybe with
             * the help of metaobjects. Give to the user the line number of the
             * file with the expanded source code / specificMessage =
             * "Look for the error in the expanded source code, not in the original one. If the source code is 'ChooseFoldersCyanInstallation.cyan', the "
             * +
             * "expanded source code should be in 'full-ChooseFoldersCyanInstallation.cyan' in the directory of the project."
             * + specificMessage; }
             */
        }
        if ( checkMessage
                && checkErrorMessage(symbol, symbol == null ? -1 : symbol.getLineNumber(), specificMessage) ) {
            return;
        }

        /*
         * do not delete this call before copying the code of error(...) to
         * here. The line number must be shift
         */
        error(symbol, specificMessage, true, true);

        thereWasError.elem = true;

        final List<String> sielCode = new ArrayList<>();
        sielCode.add("error = \"" + specificMessage + "\"");
        for (final String field : errorKind.getFieldList()) {
            switch (field) {
            case "methodName":
                sielCode.add("methodName = \"" + this.currentMethod.getName() + "\"");
                break;
            case "identifier":
                sielCode.add("identifier = \"" + identifier + "\"");
                break;
            case "statementText":
                if ( !(getCurrentPrototype() instanceof InterfaceDec) ) {
                    sielCode.add("statementText = \"" + stringCurrentStatement() + "\"");
                }
                break;
            case "methodSignature":
                if ( getCurrentMethod() != null ) {
                    sielCode.add("methodSignature = \"" + stringSignatureCurrentMethod() + "\"");
                }
                break;
            case "prototypeName":
                sielCode.add("prototypeName = \"" + getCurrentObjectDec().getName() + "\"");
                break;
            case "interfaceName":
                sielCode.add("interfaceName = \"" + identifier + "\"");
                break;
            case "visibleLocalVariableList":
                sielCode.add("visibleLocalVariableList = \"" + getStringVisibleLocalVariableList() + "\"");
                break;
            case "fieldList":
                sielCode.add("fieldList = \"" + getStringVisibleLocalVariableList() + "\"");
                break;
            case "methodList":
                sielCode.add("methodList = \"" + getStringSignatureAllMethods() + "\"");
                break;
            case "packageName":
                sielCode.add("packageName = \"" + this.getCurrentCompilationUnit().getPackageName());
                break;
            case "importList":
                StringBuilder strImportList = new StringBuilder();
                for (final ExprIdentStar e : getCurrentCompilationUnit().getImportPackageList()) {
                    strImportList.append(" ").append(e.getName());
                }
                sielCode.add("importList = \"" + strImportList.append("\"").toString());
                break;
            case "receiver":
                sielCode.add("receiver = \"" + furtherArgs[0]);
                break;
            case "supertype":
                String supertypeName = "";
                if ( currentPrototype instanceof ObjectDec ) {
                    final ObjectDec superProto = ((ObjectDec) currentPrototype).getSuperobject();
                    if ( superProto != null ) {
                        supertypeName = superProto.getFullName();
                    }
                }
                else if ( currentPrototype instanceof InterfaceDec ) {
                    final List<Expr> superInterfaceList = ((InterfaceDec) currentPrototype).getSuperInterfaceExprList();
                    if ( superInterfaceList != null && superInterfaceList.size() > 0 ) {
                        supertypeName = "";
                        int size = superInterfaceList.size();
                        for (final Expr superInterface : superInterfaceList) {
                            supertypeName += superInterface.asString();
                            if ( --size > 0 ) {
                                supertypeName += ", ";
                            }
                        }
                    }
                }
                sielCode.add("supertype = \"" + supertypeName + "\"");
                break;
            case "implementedInterfaces":
                String implInterfacesStr = "";
                if ( currentPrototype instanceof ObjectDec ) {
                    final List<Expr> superInterfaceList = ((ObjectDec) currentPrototype).getInterfaceList();
                    if ( superInterfaceList != null && superInterfaceList.size() > 0 ) {
                        implInterfacesStr = "";
                        int size = superInterfaceList.size();
                        for (final Expr superInterface : superInterfaceList) {
                            implInterfacesStr += superInterface.asString();
                            if ( --size > 0 ) {
                                implInterfacesStr += ", ";
                            }
                        }
                    }

                }
                sielCode.add("implementedInterfaces = \"" + implInterfacesStr + "\"");
                break;
            default:
                String keyValue = null;
                String fieldName;
                for (final String other : furtherArgs) {
                    int i = other.indexOf("=");
                    if ( i > 0 ) {
                        while (i > 0 && other.charAt(i - 1) == ' ') {
                            --i;
                        }
                        if ( i > 0 ) {
                            fieldName = other.substring(0, i);
                            if ( fieldName.equals(field) ) {
                                keyValue = other;
                                break;
                            }
                        }

                    }
                    else {
                        error(null, "Internal error in Env::error: error called without a key/value pair", true, true);
                    }

                }
                if ( keyValue != null ) {
                    sielCode.add(keyValue);
                }
                else {
                    error(null, "Internal error in Env::error: field " + field + " of Siel was not recognized", true,
                            true);
                }
                return;
            }
        }

        Collections.addAll(sielCode, furtherArgs);
        char[] compUnitText = null;
        String filename = null;
        if ( currentCompilationUnit != null ) {
            compUnitText = currentCompilationUnit.getText();
            filename = currentCompilationUnit.getFilename();
        }
        /*
         * SignalCompilerError.signalCompilerError( compUnitText, filename,
         * symbol == null ? -1 : symbol.getLineNumber(), symbol == null ? -1 :
         * symbol.getColumnNumber(), sielCode );
         */
        // throw new CompileErrorException();
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void pushVariableDec(VariableDecInterface localVariableDec) {
        /*
         * context function have a first parameter called "self". This should
         * not be inserted or popped from the stack
         */
        final String namevar = localVariableDec.getName();
        if ( namevar != null && !namevar.equals("self") ) {
            variableDecStack.push(localVariableDec);
            variableDecTable.put(namevar, localVariableDec);
        }
    }

    /**
     * pop num variable declarations from the stack of local variables
     *
     * @param num
     */
    public void popNumLocalVariableDec(int num) {
        while (num-- > 0) {
            /*
             * context function have a first parameter called "self". This
             * should not be inserted or popped from the stack
             */
            final String name = variableDecStack.pop().getName();
            if ( !name.equals("self") ) {
                variableDecTable.remove(name);
            }
        }
    }

    /**
     * return the number of local variables declared till this point
     */
    public int numberOfLocalVariables() {
        return variableDecStack.size();
    }

    /**
     * return a string containing the local variables visible in this point,
     * including the parameters of the method and functions. The variables are
     * separated by commas like "index, myStack, i"
     */
    public String getStringVisibleLocalVariableList() {
        String stringVisibleLocalVariableList;
        final int size = variableDecStack.size();
        int count = size;
        if ( size == 0 ) {
            return "(Array<String> new)";
        }
        else {
            stringVisibleLocalVariableList = " [ ";
            for (final VariableDecInterface variableDec : variableDecStack) {
                stringVisibleLocalVariableList = stringVisibleLocalVariableList + "\"" + variableDec.getName() + "\"";
                if ( --count > 0 ) {
                    stringVisibleLocalVariableList = stringVisibleLocalVariableList + ", ";
                }
            }
            stringVisibleLocalVariableList += " ] ";
            return stringVisibleLocalVariableList;
        }
    }

    /**
     * return a string with the list of all fields of the current
     * prototype
     */
    public String getStringFieldList() {

        if ( currentPrototype != null && currentPrototype instanceof ObjectDec ) {
            final ObjectDec prototype = (ObjectDec) currentPrototype;
            final List<FieldDec> fieldDecList = prototype.getFieldList();
            int count = fieldDecList.size();
            if ( count > 0 ) {
                StringBuilder stringFieldList = new StringBuilder(" [ ");
                for (final FieldDec fieldDec : fieldDecList) {
                    stringFieldList.append("\"").append(fieldDec.getName()).append("\"");
                    if ( --count > 0 ) {
                        stringFieldList.append(", ");
                    }
                }
                stringFieldList.append(" ] ");
                return stringFieldList.toString();
            }
        }
        return "(Array<String> new)";
    }

    /**
     * to be called at the beginning of a function
     */
    public void atBeginningFunctionDec() {

    }

    /**
     * at the end of a function, all local variables of the functions should be
     * removed from the local table
     */
    public void atEndFunctionDec() {

    }

    /**
     * at the end of a method declaration, all parameters and fields
     * should be eliminated from the environment
     */
    public void atEndMethodDec() {
        currentMethod = null;
        variableDecTable.clear();
        variableDecStack.clear();
        stackLocalVarInfo.clear();
    }

    /**
     * at the beginning of an object or interface declaration
     */
    public void atBeginningOfObjectDec(Prototype currentObjectDec) {

        this.currentPrototype = currentObjectDec;
        if ( currentPrototype.getVisibility() == Token.PRIVATE ) {
            this.privatePrototypeTable.put(currentPrototype.getName(), currentPrototype);
        }

        if ( currentObjectDec instanceof ObjectDec ) {
            this.enclosingObjectDec = ((ObjectDec) currentObjectDec).getOuterObject();
        }

        genericPrototypeFormalParameterTable = new Hashtable<>();
        if ( currentObjectDec.isGeneric() ) {
            for (final List<GenericParameter> genericList : currentObjectDec.getGenericParameterListList()) {
                for (final GenericParameter g : genericList) {
                    // only add generic parameters that are not real prototypes.
                    // That is,
                    // "T" in "object Stack<:T> ... end" is added. But "Int" in
                    // "object Stack<Int> ... end" would not.
                    if ( !g.isRealPrototype() ) {
                        addGenericPrototypeFormalParameter(g.getName(), g);
                    }
                }
            }
        }
    }

    /**
     * at the end of an object declaration, all fields and methods
     * should be eliminated from the environment
     */
    public void atEndOfObjectDec() {
        this.currentPrototype = null;
        slotDecTable.clear();
        genericPrototypeFormalParameterTable.clear();
        this.enclosingObjectDec = null;
    }

    public ObjectDec getCurrentObjectDec() {
        if ( currentPrototype instanceof ObjectDec ) {
            return (ObjectDec) currentPrototype;
        }
        else {
            return null;
        }
    }

    public ObjectDec getCurrentOuterObjectDec() {
        if ( currentPrototype instanceof ObjectDec ) {

            final ObjectDec currentObjectDec = (ObjectDec) currentPrototype;
            if ( currentObjectDec.getOuterObject() == null ) {
                return currentObjectDec;
            }
            else {
                return currentObjectDec.getOuterObject();
            }
        }
        else {
            return null;
        }
    }

    public MethodDec getCurrentMethod() {
        return currentMethod;
    }

    public void setCurrentMethod(MethodDec currentMethod) {
        this.currentMethod = currentMethod;
    }

    public List<ExprFunction> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<ExprFunction> functionList) {
        this.functionList = functionList;
    }

    public Set<CyanPackage> getImportedPackageSet() {
        if ( this.currentCompilationUnit == null ) {
            return null;
        }
        else {
            return this.currentCompilationUnit.getImportPackageSet();
        }
    }

    /**
     * returns a string containing the current statement. If there is no current
     * statement, returns null
     *
     * @return
     */
    public String stringCurrentStatement() {
        final char[] currentCompilationUnitText = getCurrentCompilationUnit().getText();
        final int start = peekCode().getFirstSymbol().getOffset();
        final Symbol lastSymbol = peekCode().getLastSymbol();
        final int theEnd = lastSymbol.getOffset() + lastSymbol.symbolString.length();
        final int size = theEnd - start;
        final char[] statementText = new char[size];
        /*
         * for (int i = 0; i < size; ++i) { statementText[i] =
         * currentCompilationUnitText[start + i]; }
         */
        System.arraycopy(currentCompilationUnitText, start, statementText, 0, size);

        return new String(statementText);
    }

    public void pushCode(Statement statement) {
        statementStack.push(statement);
    }

    public CodeWithError peekCode() {
        return statementStack.peek();
    }

    public CodeWithError popCode() {
        return statementStack.pop();
    }

    /**
     * returns the signature of the current method
     *
     * @return
     */
    public String stringSignatureCurrentMethod() {
        final PWCharArray pwChar = new PWCharArray();
        if ( currentMethod.getHasOverride() ) {
            pwChar.print("override ");
        }
        if ( currentMethod.getVisibility() == Token.PUBLIC ) {
            pwChar.print("public ");
        }
        else if ( currentMethod.getVisibility() == Token.PRIVATE ) {
            pwChar.print("private ");
        }
        else if ( currentMethod.getVisibility() == Token.PROTECTED ) {
            pwChar.print("protected ");
        }
        if ( currentMethod.isAbstract() ) {
            pwChar.print("abstract ");
        }
        pwChar.print("func ");
        currentMethod.getMethodSignature().genCyan(pwChar, false, NameServer.cyanEnv, true);
        return pwChar.getGeneratedString().toString();
    }

    /**
     * returns a string with a literal array with the signatures of all methods
     * of the current program unit (prototype or interface)
     *
     * @return
     */
    public String getStringSignatureAllMethods() {
        String s;
        if ( currentPrototype instanceof ObjectDec ) {
            final ObjectDec currentObjectDec = (ObjectDec) currentPrototype;
            final List<MethodDec> methodDecList = currentObjectDec.getMethodDecList();
            int count = methodDecList.size();
            if ( count > 0 ) {
                s = " [ ";
                for (final MethodDec methodDec : methodDecList) {
                    s += "\"";
                    s += methodDec.getMethodSignatureAsString().trim();
                    s += "\"";
                    if ( --count > 0 ) {
                        s += ", ";
                    }
                }
                s += " ] ";
            }
            else {
                s = "(Array<String> new)";
            }
        }
        else if ( currentPrototype instanceof InterfaceDec ) {
            final InterfaceDec currentInterfaceDec = (InterfaceDec) currentPrototype;
            final List<MethodSignature> methodSignatureList = currentInterfaceDec.getMethodSignatureList();
            int count = methodSignatureList.size();
            if ( count > 0 ) {
                s = " [ ";
                for (final MethodSignature methodSignature : methodSignatureList) {
                    s += methodSignature.getNameWithoutParamNumber().trim();
                    // methodSignature.genCyan(pwChar, false,
                    // NameServer.cyanEnv, true);
                    if ( --count > 0 ) {
                        s += ", ";
                    }
                }
                s += " ] ";
            }
            else {
                s = "(Array<String> new)";
            }
        }
        else {
            s = "(Array<String> new)";
        }
        return s;

    }

    /**
     * returns a string with a literal array with the signatures of all methods
     * of the current program unit (prototype or interface)
     *
     * @return
     */
    public String getStringSignatureAllMethods2() {
        final PWCharArray pwChar = new PWCharArray();
        if ( currentPrototype instanceof ObjectDec ) {
            final ObjectDec currentObjectDec = (ObjectDec) currentPrototype;
            final List<MethodDec> methodDecList = currentObjectDec.getMethodDecList();
            int count = methodDecList.size();
            if ( count > 0 ) {
                pwChar.print(" [ ");
                for (final MethodDec methodDec : methodDecList) {
                    pwChar.print("\"");
                    // methodDec.getMethodSignatureAsString()
                    methodDec.getMethodSignature().genCyan(pwChar, false, NameServer.cyanEnv, true);
                    pwChar.print("\"");
                    if ( --count > 0 ) {
                        pwChar.print(", ");
                    }
                }
                pwChar.print(" ] ");
            }
            else {
                pwChar.println("(Array<String> new)");
            }
        }
        else if ( currentPrototype instanceof InterfaceDec ) {
            final InterfaceDec currentInterfaceDec = (InterfaceDec) currentPrototype;
            final List<MethodSignature> methodSignatureList = currentInterfaceDec.getMethodSignatureList();
            int count = methodSignatureList.size();
            if ( count > 0 ) {
                pwChar.print(" [ ");
                for (final MethodSignature methodSignature : methodSignatureList) {
                    methodSignature.genCyan(pwChar, false, NameServer.cyanEnv, true);
                    if ( --count > 0 ) {
                        pwChar.print(", ");
                    }
                }
                pwChar.print(" ] ");
            }
            else {
                pwChar.println("(Array<String> new)");
            }
        }
        else {
            pwChar.println("(Array<String> new)");
        }
        return pwChar.getGeneratedString().toString();

    }

    public Prototype getCurrentPrototype() {
        return currentPrototype;
    }

    public void setCurrentPrototype(Prototype currentPrototype) {
        this.currentPrototype = currentPrototype;
    }

    /**
     * return prototype whose source file name is sourceFileName and that was
     * imported by the current compilation unit.
     */

    public Prototype searchPrototypeBySourceFileName(String sourceFileName, Symbol firstSymbol, boolean insideMethod) {
        // return this.publicSourceFileNameTable.get(sourceFileName);

        // prototype name without the generic parameters
        String rawPrototypeName = sourceFileName;
        final int lessIndex = sourceFileName.indexOf('(');
        if ( lessIndex >= 0 ) {
            rawPrototypeName = sourceFileName.substring(0, lessIndex);
        }
        final int lastDot = rawPrototypeName.lastIndexOf('.');
        if ( lastDot >= 0 ) {
            /*
             * there is a package preceding the prototype name
             */
            final String packageName = sourceFileName.substring(0, lastDot);
            final String realPrototypeName = sourceFileName.substring(lastDot + 1);
            final CyanPackage aPackage = getProject().searchPackage(packageName);
            if ( aPackage == null ) {
                return null;
            }
            else {
                return aPackage.searchPrototypeBySourceFileName(realPrototypeName);
            }
        }
        else {
            /*
             * no package preceding the prototype name
             */

            /*
             * program units defined in the current compilation unit have
             * precedence over the imported ones.
             */
            if ( sourceFileName.equals(this.currentCompilationUnit.getFileNameWithoutExtension()) ) {
                return this.currentCompilationUnit.getPublicPrototype();
            }

            Map<String, String> conflictPrototypeTable = this.currentCompilationUnit.getConflictPrototypeTable();
            if ( conflictPrototypeTable != null && conflictPrototypeTable.get(rawPrototypeName) != null ) {
                // prototypeName is imported from two or more packages
                String packageNameList = "";
                for (final CyanPackage cp : this.currentCompilationUnit.getImportedCyanPackageSet()) {
                    if ( cp.searchPrototypeBySourceFileName(rawPrototypeName) != null ) {
                        packageNameList += cp.getPackageName() + " ";
                    }
                }
                if ( insideMethod ) {
                    this.error(true, firstSymbol,
                            "Prototype '" + rawPrototypeName + "' is imported from two or more packages: "
                                    + packageNameList,
                            rawPrototypeName, ErrorKind.prototype_imported_from_two_or_more_packages_inside_method);
                }
                else {
                    this.error(true, firstSymbol,
                            "Prototype '" + rawPrototypeName + "' is imported from two or more packages: "
                                    + packageNameList,
                            rawPrototypeName, ErrorKind.prototype_imported_from_two_or_more_packages_outside_method);
                }
            }
            else {
                Set<CyanPackage> importedCyanPackageTable = this.currentCompilationUnit.getImportedCyanPackageSet();
                if ( importedCyanPackageTable != null ) {
                    for (final CyanPackage cp : importedCyanPackageTable) {
                        Prototype pu;
                        if ( (pu = cp.searchPrototypeBySourceFileName(sourceFileName)) != null ) {
                            return pu;
                        }

                    }
                }
            }
        }

        return null;
    }

    public Prototype searchVisiblePrototype(String prototypeName, Function1<String> inErrorCall) {

        // prototype name without the generic parameters
        String rawPrototypeName = prototypeName;
        final int lessIndex = prototypeName.indexOf('<');
        if ( lessIndex >= 0 ) {
            rawPrototypeName = prototypeName.substring(0, lessIndex);
        }
        final int lastDot = rawPrototypeName.lastIndexOf('.');
        if ( lastDot >= 0 ) {
            /*
             * there is a package preceding the prototype name
             */
            final String packageName = prototypeName.substring(0, lastDot);
            final String realPrototypeName = prototypeName.substring(lastDot + 1);
            final CyanPackage aPackage = getProject().searchPackage(packageName);
            if ( aPackage == null ) {
                return null;
            }
            else {
                return aPackage.searchPublicNonGenericPrototype(realPrototypeName);
            }
        }
        else {
            /*
             * no package preceding the prototype name
             */

            /**
             * search in inner prototypes first
             */
            if ( currentPrototype != null ) {
                List<ObjectDec> innerPrototypeList = null;
                if ( currentPrototype.getOuterObject() != null ) {
                    /*
                     * if the current prototype is an inner prototype, search in
                     * the inner prototypes of the outer prototype. The test
                     * "currentPrototype.getOuterObject().getInnerPrototypeList()"
                     * should always return a value != null, so nowadays this
                     * test is unnecessary
                     */
                    if ( currentPrototype.getOuterObject().getInnerPrototypeList() != null ) {
                        innerPrototypeList = currentPrototype.getOuterObject().getInnerPrototypeList();
                    }
                }
                else {
                    // current prototype is NOT an inner prototype. Search in
                    // its inner prototypes
                    innerPrototypeList = currentPrototype.getInnerPrototypeList();
                }
                if ( innerPrototypeList != null ) {
                    for (final ObjectDec innerObj : innerPrototypeList) {
                        if ( prototypeName.compareTo(innerObj.getName()) == 0 ) {
                            return innerObj;
                        }
                    }
                }
            }
            /*
             * program units defined in the current compilation unit have
             * precedence over the imported ones.
             */
            if ( currentCompilationUnit.getPrototypeList() != null ) {
                /*
                 * it is null if the compilation unit is the project file, the
                 * .pyan file
                 */
                for (final Prototype prototype : this.currentCompilationUnit.getPrototypeList()) {
                    if ( prototypeName.compareTo(prototype.getName()) == 0 ) {
                        return prototype;
                    }
                }

            }

            Map<String, String> conflictPrototypeTable = this.currentCompilationUnit.getConflictPrototypeTable();

            if ( conflictPrototypeTable != null && conflictPrototypeTable.get(rawPrototypeName) != null ) {
                // prototypeName is imported from two or more packages
                final String packageNameList = this.currentCompilationUnit.getConflictPrototypeTable()
                        .get(rawPrototypeName);

                /*
                 * for ( CyanPackage cp :
                 * this.currentCompilationUnit.getImportedCyanPackageTable() ) {
                 * if ( cp.searchPublicNonGenericPrototype(prototypeName) !=
                 * null ) packageNameList += cp.getPackageName() + " "; }
                 */
                inErrorCall.eval(packageNameList);
            }
            else {
                Set<CyanPackage> importedCyanPackageTable = currentCompilationUnit.getImportedCyanPackageSet();
                if ( importedCyanPackageTable != null ) {
                    for (final CyanPackage cp : importedCyanPackageTable) {
                        Prototype pu;
                        if ( (pu = cp.searchPublicNonGenericPrototype(prototypeName)) != null ) {
                            return pu;
                        }

                    }
                }
                /**
                 * if prototypeForGenericPrototypeList is not null, then we
                 * are doing semantic analysis in a generic prototype. This is
                 * only done by metaobject 'concept'.
                 */
                if ( prototypeForGenericPrototypeList != null ) {
                    for (final Prototype pu : prototypeForGenericPrototypeList) {
                        if ( pu.getName().equals(prototypeName) ) {
                            return pu;
                        }
                    }
                }
            }

            return null;
        }

    }

    @SuppressWarnings("unused")
    public Prototype searchVisiblePrototype(String prototypeName, Symbol firstSymbol, boolean insideMethod) {
        return searchVisiblePrototype(prototypeName, (String packageNameList) -> {
            this.error(firstSymbol,
                    "Prototype '" + prototypeName + "' is imported from two or more packages: " + packageNameList);
        });

    }

    /**
     * searches for a Java class whose name is 'className' and whose package is 'packageName'.
     * If packageName is null, search in the Java classes imported by the current
     * compilation unit
       @param packageName
       @param className
       @return
     */
    public TypeJavaRef searchPackageJavaClass(String packageName, String className) {
        if ( packageName != null && packageName.length() != 0 ) {
            final JVMPackage jvmPackage = this.project.getProgram().searchJVMPackage(packageName);
            if ( jvmPackage != null ) {
                return jvmPackage.searchJVMClass(className);
            }
        }
        else {
            return this.searchVisibleJavaClass(className);
        }
        return null;
    }

    public JVMPackage searchPackageJava(String packageName) {
        if ( packageName != null && packageName.length() != 0 ) {
            return this.project.getProgram().searchJVMPackage(packageName);
        }
        return null;
    }

    public boolean wasJavaPackageImported(String packageName) {
        return this.searchPackageJava(packageName) != null;
    }

    public boolean wasJavaClassImported(String packageName, String className) {
        final JVMPackage javaPackage = this.searchPackageJava(packageName);
        if ( javaPackage == null ) {
            return false;
        }
        return javaPackage.searchJVMClass(className) != null;
    }

    public boolean wasJavaClassImported(String fullClassName) {
        return this.searchVisibleJavaClass(fullClassName) != null;
        /*
        if ( this.searchVisibleJavaClass(fullClassName) != null ) {
        	return true;
        }
        else {
        	int lastDot = fullClassName.lastIndexOf('.');
        	if ( lastDot < 0 ) { return false; }
        	String packageName = fullClassName.substring(0, lastDot);
        	String className = fullClassName.substring(lastDot + 1);
        	JVMPackage javaPackage = this.searchPackageJava(packageName);
        	if ( javaPackage == null ) { return false; }
        	return javaPackage.searchJVMClass(className) != null;
        }
        */
    }

    public boolean wasCyanPrototypedImported(String prototypeName) {
        return this.searchVisiblePrototype(prototypeName, (String s) -> {
        }) != null;
    }

    public boolean wasCyanPackageImported(String packageName) {
        return this.searchPackage(packageName) != null;
    }

    /**
     * return the Java class (TypeJavaRef) that has name className and that was imported
     * by the current compilation unit.
     *
     * If className can be imported from two or more packages, an error is
     * signaled. If className is not found, returns null.
     *
     * firstSymbol is the first symbol of 'className'. It is used to signal
     * errors

     * @param className
     * @param firstSymbol
     * @return
     */
    public TypeJavaRef searchVisibleJavaClass(String className) {

        // prototype name without the generic parameters
        String rawPrototypeName = className;
        final int lessIndex = className.indexOf('<');
        if ( lessIndex >= 0 ) {
            rawPrototypeName = className.substring(0, lessIndex);
        }
        final int lastDot = rawPrototypeName.lastIndexOf('.');
        if ( lastDot >= 0 ) {
            /*
             * there is a package preceding the prototype name
             */
            final String packageName = className.substring(0, lastDot);
            final String realPrototypeName = className.substring(lastDot + 1);
            final JVMPackage jvmPackage = this.project.getProgram().searchJVMPackage(packageName);

            if ( jvmPackage == null ) {
                return null;
            }
            else {
                return jvmPackage.searchJVMClass(realPrototypeName);
            }
        }
        else {
            /*
             * no package preceding the prototype name
             */
            Map<String, JVMPackage> mapNamePackage = this.currentCompilationUnit.getImportJVMPackageSet();
            if ( mapNamePackage != null ) {
                for (final Map.Entry<String, JVMPackage> entry : mapNamePackage.entrySet()) {
                    final TypeJavaRef javaClass = entry.getValue().searchJVMClass(rawPrototypeName);
                    if ( javaClass != null ) {
                        return javaClass;
                    }
                }
            }
            /*
            for (Map.Entry<String, TypeJavaRef> entry : this.currentCompilationUnit.getImportJVMJavaRefSet().entrySet() ) {
            	if ( entry.getKey().equals(rawPrototypeName) ) {
            		return entry.getValue();
            	}
            }
            */
            Map<String, TypeJavaRef> importJVMJavaRefSet = this.currentCompilationUnit.getImportJVMJavaRefSet();
            if ( importJVMJavaRefSet != null ) {
                return importJVMJavaRefSet.get(rawPrototypeName);
            }
            else {
                return null;
            }
        }
    }

    public void setCurrentCompilationUnit(CompilationUnit currentCompilationUnit) {
        this.currentCompilationUnit = currentCompilationUnit;
    }

    public void pushFunction(ExprFunction function) {
        functionStack.push(function);
    }

    public ExprFunction popFunction() {
        return functionStack.pop();
    }

    public ExprFunction peekFunction() {
        return functionStack.peek();
    }

    public Stack<ExprFunction> getFunctionStack() {
        return functionStack;
    }

    /**
     * searches for a variable "name" in the local variables and parameters.
     *
     * @param name
     * @return
     */
    public VariableDecInterface searchLocalVariableParameter(String name) {
        return this.variableDecTable.get(name);
    }

    /**
     * searches for a variable "name" in the lists of local variables,
     * parameters, and fields. The search is
     * made in the list of fields only if no local
     * variable or parameter is found. This method should not be used in inner
     * prototypes.
     *
     * @param name
     * @return an object of VariableDecInterface (local variable, parameter) or
     *         an object of FieldDec
     */
    public VariableDecInterface searchVariable(String name) {

        VariableDecInterface ret = this.searchLocalVariableParameter(name);

        if ( ret == null ) {
            ret = currentPrototype.searchFieldPrivateProtectedSuperProtected(name);
        }
        return ret;
    }

    /**
     * search for a variable <code>name</code> inside an 'eval' or 'eval:eval:
     * ...' method of an inner prototype created for a function or a method.
     *
     * @param name
     * @return
     */
    public VariableDecInterface searchVariableInEvalOfInnerPrototypes(String name) {

        /*
         * the search order is: local variables, parameters, fields
         * that are context parameters, and fields of the
         * outer object (enclosingObjectDec)
         */
        VariableDecInterface ret = this.searchLocalVariableParameter(name);

        if ( ret == null ) {
            ret = currentPrototype.searchFieldDec(name);
            if ( ret == null || !(ret instanceof ContextParameter) ) {
                /*
                 * found a field of the inner prototype that is not
                 * a context parameter or did not find anything.
                 */
                ret = enclosingObjectDec.searchFieldDec(name);
            }
        }
        return ret;
    }

    /**
     * search for a variable <code>name</code> inside an 'bindToFunction' method
     * of a prototype representing a context function
     *
     * @param name
     * @return
     */
    public VariableDecInterface searchVariableInBindToFunction(String name) {

        /*
         * the search order is: local variables, parameters, fields
         * that are context parameters, and fields of the
         * outer object (enclosingObjectDec)
         */
        VariableDecInterface ret = this.searchLocalVariableParameter(name);

        if ( ret == null ) {
            ret = currentPrototype.searchFieldDec(name);
            if ( ret != null && ret instanceof ContextParameter ) {
                ret = null;
            }
        }
        return ret;
    }

    /**
     * search for a variable <code>name</code> inside a method that is not an
     * 'eval' or 'eval:eval: ...' method of an inner prototype created for a
     * function or a method.
     *
     * @param name
     * @return
     */
    public VariableDecInterface searchVariableIn_NOT_EvalOfInnerPrototypes(String name) {
        /*
         * the search order is: local variables, parameters, fields
         * that are NOT context parameters.
         */
        VariableDecInterface ret = this.searchLocalVariableParameter(name);

        if ( ret == null ) {
            ret = currentPrototype.searchFieldDec(name);
            if ( ret != null && ret instanceof ContextParameter ) {
                ret = null;
                /*
                 * found a field of the inner prototype that is a
                 * context parameter
                 */
            }
        }
        return ret;
    }

    public FieldDec searchField(String name) {
        /*
         * if ( enclosingObjectDec != null ) return
         * enclosingObjectDec.searchFieldDec(name); else
         */
        FieldDec iv = currentPrototype.searchFieldDec(name);
        if ( iv != null ) {
            return iv;
        }
        else {
            if ( currentPrototype instanceof ObjectDec ) {
                ObjectDec proto = ((ObjectDec) currentPrototype).getSuperobject();
                while (proto != null && proto != Type.Any) {
                    iv = proto.searchField(name);
                    if ( iv != null && iv.getVisibility() == Token.PROTECTED ) {
                        return iv;
                    }
                    else {
                        proto = proto.getSuperobject();
                    }
                }
            }
            return null;
        }
    }

    public WrType createNewGenericPrototype(Symbol symUsedInError, CompilationUnitSuper compUnit, Prototype currentPU,
            String fullPrototypeName, String errorMessage) {
        try {
            this.setPrefixErrorMessage(errorMessage);
            final Expr newPrototype = saci.Compiler.parseSingleTypeFromString(fullPrototypeName, symUsedInError,
                    errorMessage, compUnit, currentPU);
            newPrototype.calcInternalTypes(this);
            Type t = newPrototype.getType();
            return t == null ? null : t.getI();
        } catch (final CompileErrorException cee) {
            this.setThereWasError(true);
        } finally {
            this.setPrefixErrorMessage(null);
        }
        return null;
    }

    /*
     * public Project getCurrentProject() { return
     * currentCompilationUnit.getCyanPackage().getProject(); }
     */

    public ObjectDec getEnclosingObjectDec() {
        return enclosingObjectDec;
    }

    public void setEnclosingObjectDec(ObjectDec enclosingObjectDec) {
        this.enclosingObjectDec = enclosingObjectDec;
    }

    public CyanPackage searchPackage(String packageName) {
        return project.searchPackage(packageName);
    }

    public Prototype searchPackagePrototype(String packageName, String prototypeName) {
        final CyanPackage pack = project.searchPackage(packageName);
        if ( pack == null ) {
            return null;
        }
        else {
            return pack.searchPublicNonGenericPrototype(prototypeName);
        }
    }

    public Type searchPackagePrototype(String packagePrototypeName, Symbol symUsedInError) {

        /*
         * Prototype singleTypeFromString(String typeAsString, Symbol
         * symUsedInError, String message, CompilationUnit compUnit, Prototype
         * currentPU, Env env)
         */

        CompilationUnit cunit = this.currentCompilationUnit;
        Prototype pu = this.currentPrototype;
        CompilationUnitSuper cunitSuper = symUsedInError.getCompilationUnit();
        if ( cunitSuper instanceof CompilationUnit ) {
            cunit = (CompilationUnit) cunitSuper;
            pu = cunit.getPublicPrototype();
            if ( pu == null ) {
                cunit = this.currentCompilationUnit;
                pu = this.currentPrototype;
            }
        }

        return Compiler.singleTypeFromString(packagePrototypeName, symUsedInError,
                "Prototype '" + packagePrototypeName + "' was not found", cunit, pu, this);
    }

    public boolean isThereWasError() {
        return thereWasError.elem;
    }

    public HashSet<meta.CompilationInstruction> getCompInstSet() {
        return compInstSet;
    }

    public void setCompInstSet(HashSet<meta.CompilationInstruction> compInstSet) {
        this.compInstSet = compInstSet;
    }

    public CompilationStep getCompilationStep() {
        return project.getCompilerManager().getCompilationStep();
    }

    public static String getCodeToAddWithContext(CyanMetaobject cyanMetaobject, String codeToAdd, Type codeType) {
        return Env.getCodeToAddWithContext(cyanMetaobject, codeToAdd, codeType, null);
    }

    /**
     * add context to the code codeToAdd. This context is composed of a push
     * compilation context and a pop compilation context. It is considered that
     * this code has type codeType if this parameter is not-null. Otherwise, if
     * cyanMetaobject is an expression, it is considered that codeToAdd has type
     * given by cyanMetaobject.getAnnotation().
     *
     * @param cyanMetaobject
     * @param codeToAdd
     * @param cyanAnnotation
     * @return
     */
    public static String getCodeToAddWithContext(CyanMetaobject cyanMetaobject, String codeToAdd, Type codeType,
            String strSlotList) {
        final Annotation cyanAnnotation = meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
        final String name = cyanMetaobject.getName();
        String str;
        if ( cyanAnnotation.getInsideProjectFile() ) {
            str = "\"the project file \"";
        }
        else {
            str = cyanAnnotation.getPackageOfAnnotation();
        }

        String codeToAddWithContext;
        if ( strSlotList != null ) {
            strSlotList = Env.changeNewLineToSpaces(strSlotList);
        }
        if ( codeType != null ) {
            final Tuple2<String, String> packagePrototype = CompilerManager
                    .separatePackagePrototype(codeType.getFullName());

            codeToAddWithContext = " @" + MetaHelper.pushCompilationContextName + "(" + "atisemAn_id_"
                    + Env.atisemAnIdNumber + ", \"" + name + "\", " + str + ", \""
                    + cyanAnnotation.getCompilationUnit().getFullFileNamePath() + "\", "
                    + cyanAnnotation.getFirstSymbol().getLineNumber()
                    + (strSlotList == null ? "" : ", \"" + strSlotList + "\"") + ") " + codeToAdd
                    + " @popCompilationContext(" + "atisemAn_id_" + Env.atisemAnIdNumber + ", \"" + packagePrototype.f1
                    + "\", \"" + packagePrototype.f2 + "\") \n";
        }
        else if ( cyanMetaobject.isExpression() ) {
            codeToAddWithContext = " @" + MetaHelper.pushCompilationContextName + "(" + "atisemAn_id_"
                    + Env.atisemAnIdNumber + ", \"" + name + "\", " + str + ", \""
                    + cyanAnnotation.getCompilationUnit().getFullFileNamePath() + "\", "
                    + cyanAnnotation.getFirstSymbol().getLineNumber()
                    + (strSlotList == null ? "" : ", \"" + strSlotList + "\"") + ") " + codeToAdd
                    + " @popCompilationContext(" + "atisemAn_id_" + Env.atisemAnIdNumber + ", \""
                    + cyanMetaobject.getPackageOfType() + "\", \"" + cyanMetaobject.getPrototypeOfType() + "\") \n";
        }
        else {
            codeToAddWithContext = " @" + MetaHelper.pushCompilationContextStatementName + "(" + "atisemAn_id_"
                    + Env.atisemAnIdNumber + ", \"" + name + "\", " + str + ", \""
                    + cyanAnnotation.getCompilationUnit().getFullFileNamePath() + "\", "
                    + cyanAnnotation.getFirstSymbol().getLineNumber()
                    + (strSlotList == null ? "" : ", \"" + strSlotList + "\"") + ") " + codeToAdd
                    + " @popCompilationContext(" + "atisemAn_id_" + Env.atisemAnIdNumber + ") \n";

        }
        ++Env.atisemAnIdNumber;

        /*
         * msg = msg +
         * " This error was caused by code introduced initially by metaobject '"
         * + cyanMetaobjectName + "' called at line " + lineNumber + " of " +
         * packageName + " of file " + sourceFileName;
         *
         */
        return codeToAddWithContext;
    }

    private static String changeNewLineToSpaces(String s) {
        StringBuilder ns = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            if ( ch == '\r' || ch == '\n' ) {
                ns.append("  ");
            }
            else {
                ns.append(ch);
            }
        }
        return ns.toString();
    }

    /**
     * push a context used to issue errors in code generated by metaobjects
     *
     * @param id
     * @param cyanMetaobjectName
     * @param packageName
     * @param prototypeName
     * @param lineNumber
     * @param lineNumberPushContext
     *            line number of the annotation of metaobject
     *            {@link meta#CyanMetaobjectCompilationContextPush}
     */
    public void pushCompilationContext(String id, String cyanMetaobjectName, String packageName, String prototypeName,
            int lineNumber, int offsetPushContext, List<ISlotSignature> slotList) {
        cyanMetaobjectCompilationContextStack.push(new Tuple7<>(id, cyanMetaobjectName, packageName, prototypeName,
                lineNumber, offsetPushContext, slotList));
        offsetPushCompilationContextStack.push(offsetPushContext);

    }

    /**
     * pop a context used to issue errors in code generated by metaobjects
     *
     * @param id
     * @param metaobjectSymbol
     * @param offsetPopContext,
     *            the offset inside the compilation unit of the annotation
     *            {@literal @}popCompilationContext
     * @return
     */
    public boolean popCompilationContext(String id, Symbol metaobjectSymbol) {

        if ( cyanMetaobjectCompilationContextStack.isEmpty() ) {
            this.error(true, metaobjectSymbol,
                    "Attempt to pop a context through @popCompilationContext in an empty stack", null,
                    ErrorKind.metaobject_error);
            return false;
        }
        else if ( !id.equals(cyanMetaobjectCompilationContextStack.peek().f1) ) {
            this.error(true, metaobjectSymbol,
                    "Attempt to pop a context through @popCompilationContext with a wrong id. It should be '"
                            + cyanMetaobjectCompilationContextStack.peek().f1 + "'",
                    null, ErrorKind.metaobject_error);
            return false;
        }
        else {
            /*
             * the "+ 1" in the line below refer to the line of
             *
             * @popCompilationContext(atisemAn_id_0) which just occupy just one
             * line
             */
            lineShift = lineShift + this.numLinesBetween(this.currentCompilationUnit.getText(),
                    offsetPushCompilationContextStack.pop(), metaobjectSymbol.getOffset()) + 1;
            cyanMetaobjectCompilationContextStack.pop();
            return true;
        }
    }

    public void checkIfContextStackEmpty(Symbol errSymbol) {
        if ( !cyanMetaobjectCompilationContextStack.isEmpty() ) {
            this.error(errSymbol, "Internal error: context stack was expected to be empty");
        }

    }

    /**
     * return the number of lines between offsetPushContext and offsetPopContext
     * of the current compilation unit
     *
     * @param f6
     * @param offsetPopContext
     * @return
     */
    public int numLinesBetween(char[] myText, int offsetPushContext, int offsetPopContext) {
        int n = 0;
        int i = offsetPushContext;
        if ( offsetPopContext > myText.length ) {
            return 0;
        }
        while (i < offsetPopContext) {
            if ( myText[i] == '\n' ) {
                ++n;
            }
            ++i;
        }
        return n;
    }

    /**
     * return the number of lines of <code>s</code>
     *
     * @param s
     * @return
     */
    static public int numLinesOf(StringBuffer s) {
        int n = 0;
        int i = 0;
        while (i < s.length()) {
            if ( s.charAt(i) == '\n' ) {
                ++n;
            }
            ++i;
        }
        return n;
    }

    public void writePrototypesToFile() {
        MyFile.writePrototypesToFile(compilationUnitToWriteList, "-full");
    }

    public void addCompilationUnitToWrite(CompilationUnit compilationUnit) {
        if ( compilationUnitToWriteList == null ) {
            compilationUnitToWriteList = new HashSet<>();
        }
        compilationUnitToWriteList.add(compilationUnit);
    }

    /**
     * return a list of pairs (lineNumber, expectedMessage). Each pair gives the
     * line number and an error message that the compiler should issue at that
     * line of the current compilation unit.
     *
     * @return
     */
    public List<Tuple3<Integer, String, Boolean>> getLineMessageList() {
        return lineMessageList;
    }

    /**
     * push a metaobject annotation that implements IParseWithCyanCompiler_parsing or
     * IParseMacro_parsing
     */
    public void pushAnnotationParseWithCompiler(Annotation cyanAnnotation) {
        metaobjectAnnotationParseWithCompilerStack.push(cyanAnnotation);
    }

    /**
     * pop a metaobject annotation that implements IParseWithCyanCompiler_parsing or
     * IParseMacro_parsing
     */
    public Annotation popAnnotationParseWithCompiler() {
        return metaobjectAnnotationParseWithCompilerStack.pop();
    }

    public int sizeStackAnnotationParseWithCompiler() {
        return metaobjectAnnotationParseWithCompilerStack.size();
    }

    public Prototype getCyException() {
        if ( cyException == null ) {
            cyException = this.searchPackagePrototype(MetaHelper.cyanLanguagePackageName,
                    MetaHelper.cyExceptionPrototype);
            if ( cyException == null ) {
                this.error(null, "Prototype '" + MetaHelper.cyanLanguagePackageName + "."
                        + MetaHelper.cyExceptionPrototype + "' was not found", true, true);
            }
        }
        return cyException;
    }

    public boolean getHasJavaCode() {
        return hasJavaCode;
    }

    public void setHasJavaCode(boolean hasJavaCode) {
        this.hasJavaCode = hasJavaCode;
    }

    public int getLexicalLevel() {
        return lexicalLevel;
    }

    public void setLexicalLevel(int lexicalLevel) {
        this.lexicalLevel = lexicalLevel;
    }

    public void addLexicalLevel() {
        ++this.lexicalLevel;
    }

    public void subLexicalLevel() {
        --this.lexicalLevel;
    }

    public void pushVariableAndLevel(VariableDecInterface theVar, String varName) {
        stackVariableLevel.push(new Tuple3<>(theVar, varName, lexicalLevel));
    }

    public void removeVariablesLastLevel() {
        while (!stackVariableLevel.empty() && this.stackVariableLevel.peek().f3 == this.lexicalLevel) {
            this.stackVariableLevel.pop();
        }
    }

    public void removeLocalVarInfoLastLevel() {
        while (!this.stackLocalVarInfo.empty() && this.stackLocalVarInfo.peek().lexicalLevel > this.lexicalLevel) {
            this.stackLocalVarInfo.pop();
        }
    }

    public void clearStackVariableLevel() {
        this.stackVariableLevel.clear();
    }

    public LocalVarInfo pushVariableInfo(StatementLocalVariableDec localVar) {
        final LocalVarInfo localVarInfo = new LocalVarInfo(localVar, this.lexicalLevel);
        this.stackLocalVarInfo.push(localVarInfo);
        return localVarInfo;
    }

    public void setLocalVariableAsInitializedWith(StatementLocalVariableDec localVar, Expr expr) {
        final int size = stackLocalVarInfo.size();
        if ( size > 0 ) {
            for (int i = size - 1; i >= 0; --i) {
                LocalVarInfo varInfo = this.stackLocalVarInfo.get(i);

                if ( varInfo.localVar == localVar ) {
                    if ( varInfo.lexicalLevel != this.lexicalLevel ) {
                        /*
                         * create a copy of the variable information in the current lexical level
                         */
                        varInfo = varInfo.clone();
                        varInfo.lexicalLevel = this.lexicalLevel;
                        this.stackLocalVarInfo.push(varInfo);
                    }
                    varInfo.initialized = true;
                    if ( expr instanceof ast.ExprLiteral ) {
                        if ( varInfo.literalList == null ) {
                            varInfo.literalList = new ArrayList<>();
                        }
                        varInfo.literalList.add(((ExprLiteral) expr).getJavaValue());
                    }
                    else {
                        varInfo.initializedWithNonLiteral = true;
                    }
                    return;
                }
            }
        }
        else {
            this.error(localVar.getFirstSymbol(),
                    "Internal error: variable '" + localVar.getName() + "' was not found in stack stackLocalVarInfo");
        }
    }

    public LocalVarInfo getLocalVariableInfo(StatementLocalVariableDec localVar) {
        final int size = stackLocalVarInfo.size();
        if ( size > 0 ) {
            for (int i = size - 1; i >= 0; --i) {
                LocalVarInfo varInfo = this.stackLocalVarInfo.get(i);
                if ( varInfo.localVar == localVar ) {
                    if ( varInfo.lexicalLevel != this.lexicalLevel ) {
                        /*
                         * create a copy of the variable information in the current lexical level
                         */
                        varInfo = varInfo.clone();
                        varInfo.lexicalLevel = this.lexicalLevel;
                        this.stackLocalVarInfo.push(varInfo);
                    }

                    return varInfo;
                }
            }
        }
        else {
            this.error(localVar.getFirstSymbol(),
                    "Internal error: variable '" + localVar.getName() + "' was not found in stack stackLocalVarInfo");
        }
        return null;
    }

    /**
     * add information on the variable localVar in the stack of local variable information.
     * It should be called when a variable is used (ExprIdentStar).
       @param localVar
     */
    public void addLocalVariableInfoToCurrentLexicalLevel(StatementLocalVariableDec localVar) {
        final int size = stackLocalVarInfo.size();
        if ( size > 0 ) {
            for (int i = size - 1; i >= 0; --i) {
                final LocalVarInfo varInfo = this.stackLocalVarInfo.get(i);
                if ( varInfo.localVar == localVar && varInfo.lexicalLevel != this.lexicalLevel ) {
                    /*
                     * create a copy of the variable information in the current lexical level
                     */
                    final LocalVarInfo newObj = varInfo.clone();
                    newObj.lexicalLevel = this.lexicalLevel;
                    this.stackLocalVarInfo.push(newObj);
                    return;
                }
            }
        }
        final LocalVarInfo newObj = new LocalVarInfo(localVar, this.lexicalLevel);
        this.stackLocalVarInfo.push(newObj);
    }

    /**
     * add information on the variable localVar in the stack of local variable information.
     * It should be called when a variable is declared
       @param localVar
     */
    public void addLocalVariableInfoToCurrentLexicalLevel(StatementLocalVariableDec localVar, Expr expr) {
        final LocalVarInfo varInfo = new LocalVarInfo(localVar, this.lexicalLevel);
        this.stackLocalVarInfo.push(varInfo);
        if ( expr != null ) {
            varInfo.initialized = true;
            if ( expr instanceof ast.ExprLiteral ) {
                if ( varInfo.literalList == null ) {
                    varInfo.literalList = new ArrayList<>();
                }
                varInfo.literalList.add(((ExprLiteral) expr).getJavaValue());
            }
            else {
                varInfo.initializedWithNonLiteral = true;
            }

        }
    }

    public List<LocalVarInfo> getLocalVarInfoPreviousLevel() {
        List<LocalVarInfo> list = null;
        final int size = stackLocalVarInfo.size();
        if ( size > 0 ) {
            for (int i = size - 1; i >= 0; --i) {
                final LocalVarInfo varInfo = this.stackLocalVarInfo.get(i);
                if ( varInfo.lexicalLevel == this.lexicalLevel + 1
                        && varInfo.localVar.getLevel() <= this.lexicalLevel ) {
                    if ( list == null ) {
                        list = new ArrayList<>();
                    }
                    list.add(varInfo);
                }
            }
        }
        return list;
    }

    public boolean isLocalVarInList(List<LocalVarInfo> localVarInfoList, StatementLocalVariableDec varDec) {
        for (final LocalVarInfo varInfo : localVarInfoList) {
            if ( varInfo.localVar == varDec ) {
                return true;
            }
        }
        return false;
    }

    // breakIsLastStatement
    public void setLocalVarInitializedThisLevel(List<List<LocalVarInfo>> localVarInfoListList) {

        if ( localVarInfoListList != null ) {
            final int sizeListList = localVarInfoListList.size();

            final List<LocalVarInfo> localVarInfoList = localVarInfoListList.get(0);
            final int sizeList = localVarInfoList.size();
            for (int j = 0; j < sizeList; ++j) {
                final LocalVarInfo varInfo = localVarInfoList.get(j);
                // is varInfo in the other lists?
                boolean inAllOthers = true;
                for (int k = 1; k < sizeListList; ++k) {
                    if ( !isLocalVarInList(localVarInfoListList.get(k), varInfo.localVar) ) {
                        inAllOthers = false;
                    }
                }
                if ( inAllOthers ) {
                    /*
                     * variable varInfo.localVar was initialized in all branches of a if statement,
                     * for example
                     */
                    final int size = stackLocalVarInfo.size();
                    if ( size > 0 ) {
                        // int count = 0;
                        for (int i = size - 1; i >= 0; --i) {
                            LocalVarInfo varInfoStack = this.stackLocalVarInfo.get(i);
                            if ( varInfoStack.localVar == varInfo.localVar ) {
                                if ( varInfoStack.lexicalLevel != this.lexicalLevel ) {
                                    /*
                                     * create a copy of the variable information in the current lexical level
                                     */
                                    varInfoStack = varInfo.clone();
                                    varInfoStack.lexicalLevel = this.lexicalLevel;
                                    this.stackLocalVarInfo.push(varInfoStack);
                                }
                                varInfoStack.initialized = true;
                                // ++count;
                                break;
                            }
                        }
                        /*
                        if ( count > 1 ) {
                        	System.out.println("Count > 1 in " + varInfo.localVar.getName() + " " + this.getCurrentCompilationUnit().getFilename());
                        }
                        */
                    }

                }
            }

        }

    }

    public void setLocalVarInitializedPreviousLevel(List<List<LocalVarInfo>> localVarInfoListList) {

        if ( localVarInfoListList != null ) {
            final int sizeListList = localVarInfoListList.size();

            final List<LocalVarInfo> localVarInfoList = localVarInfoListList.get(0);
            final int sizeList = localVarInfoList.size();
            for (int j = 0; j < sizeList; ++j) {
                final LocalVarInfo varInfo = localVarInfoList.get(j);
                // is varInfo in the other lists?
                boolean inAllOthers = true;
                for (int k = 1; k < sizeListList; ++k) {
                    if ( !isLocalVarInList(localVarInfoListList.get(k), varInfo.localVar) ) {
                        inAllOthers = false;
                    }
                }
                if ( inAllOthers ) {
                    /*
                     * variable varInfo.localVar was initialized in all branches of a if statement,
                     * for example
                     */
                    final int size = stackLocalVarInfo.size();
                    if ( size > 0 ) {
                        for (int i = size - 1; i >= 0; --i) {
                            LocalVarInfo varInfoStack = this.stackLocalVarInfo.get(i);
                            if ( varInfoStack.localVar == varInfo.localVar ) {
                                if ( varInfoStack.lexicalLevel != this.lexicalLevel - 1 ) {
                                    /*
                                     * create a copy of the variable information in the current lexical level
                                     */
                                    varInfoStack = varInfo.clone();
                                    varInfoStack.lexicalLevel = this.lexicalLevel;
                                    this.stackLocalVarInfo.add(i, varInfoStack);
                                }
                                varInfoStack.initialized = true;
                            }
                        }
                    }

                }
            }

        }

    }

    public void clearStackLocalVarInfo() {
        stackLocalVarInfo.clear();
    }

    public void transferLocalVarInitializedPreviousLevel(List<LocalVarInfo> localVarInfoList) {
        /*
         * variable varInfo.localVar was initialized in all branches of a if statement,
         * for example
         */
        final int size = stackLocalVarInfo.size();
        for (final LocalVarInfo varInfo : localVarInfoList) {
            for (int i = size - 1; i >= 0; --i) {
                LocalVarInfo varInfoStack = this.stackLocalVarInfo.get(i);
                if ( varInfoStack.localVar == varInfo.localVar && !varInfoStack.breakIsLastStatement ) {
                    if ( varInfoStack.lexicalLevel != this.lexicalLevel ) {
                        /*
                         * create a copy of the variable information in the current lexical level
                         */
                        varInfoStack = varInfo.clone();
                        varInfoStack.lexicalLevel = this.lexicalLevel;
                        this.stackLocalVarInfo.push(varInfoStack);
                    }
                    varInfoStack.initialized = true;

                }
            }

        }

    }

    public void pushControlFlowStack(Statement s) {
    	this.controlFlowStack.push(s);
    }

    public void popControlFlowStack() {
    	this.controlFlowStack.pop();
    }

    public Statement peekControlFlowStack() {
    	return this.controlFlowStack.peek();
    }

    public boolean hasTryCatchWithFinallyInControlFlowStack() {
    	for ( Statement s : controlFlowStack ) {
    		if ( s instanceof StatementTry ) {
    			StatementTry statTry = (StatementTry ) s;
    			StatementList statList = statTry.getFinallyStatementList();
    			if ( statList != null ) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

    public boolean isTopTryCatchStatement() {
    	return this.controlFlowStack.size() > 0 &&
    			this.controlFlowStack.peek() instanceof StatementTry;
    }


    public void pushRepetitionStatStack(char ch) {
        repetitionStatStack.push(ch);
    }

    public void popRepetitionStatStack() {
        this.repetitionStatStack.pop();
    }

    public char peekRepetitionStatStack() {
        return this.repetitionStatStack.peek();
    }

    public boolean isEmptyRepetitionStatStack() {
        return this.repetitionStatStack.isEmpty();
    }

    public String getCurrentClassNameWithOuter() {
        return currentClassNameWithOuter;
    }

    public void setCurrentClassNameWithOuter(String currentClassNameWithOuter) {
        this.currentClassNameWithOuter = currentClassNameWithOuter;
    }

    public boolean getCreatingInnerPrototypesInsideEval() {
        return creatingInnerPrototypesInsideEval;
    }

    public void setCreatingInnerPrototypesInsideEval(boolean creatingInnerPrototypesInsideEval) {
        this.creatingInnerPrototypesInsideEval = creatingInnerPrototypesInsideEval;
    }

    /**
     * sets the string with code to initialize reference variables, which are
     * variables referred by anonymous functions
     *
     * @param refVarInitStr
     */
    public void setStrInitRefVariables(String strInitRefVariables) {
        this.strInitRefVariables = strInitRefVariables;
    }

    public String getStrInitRefVariables() {
        return strInitRefVariables;
    }

    public void setIsInsideInitMethod(boolean isInsideInitMethod) {
        this.isInsideInitMethod = isInsideInitMethod;
    }

    public boolean getIsInsideInitMethod() {
        return isInsideInitMethod;
    }

    public Stack<Tuple3<VariableDecInterface, String, Integer>> getStackVariableLevel() {
        return stackVariableLevel;
    }

    public Stack<VariableDecInterface> getVariableDecStack() {
        return variableDecStack;
    }

    public void addToLineShift(int toAdd) {
        lineShift += toAdd;
    }

    public void setThereWasError(boolean thereWasError) {
        this.thereWasError.elem = thereWasError;
    }

    public boolean getDuring_semAn_actions() {
        return during_semAn_actions;
    }

    public List<Prototype> getPrototypeForGenericPrototypeList() {
        return prototypeForGenericPrototypeList;
    }

    public void setPrototypeForGenericPrototypeList(List<Prototype> prototypeForGenericPrototypeList) {
        this.prototypeForGenericPrototypeList = prototypeForGenericPrototypeList;
    }

    /**
     * if the current program unit was created from a generic prototype
     * instantiation, the instantiation is in package packageNameInstantiation,
     * prototype prototypeNameInstantiation, line number
     * lineNumberInstantiation, and column number columnNumberInstantiation. The
     * methods below are the getters and setters for these variables. In regular
     * prototypes packageNameInstantiation and prototypeNameInstantiation are
     * null.
     */

    public String getPackageNameInstantiation() {
        if ( currentCompilationUnit == null ) {
            return null;
        }
        return currentCompilationUnit.getPackageNameInstantiation();
    }

    public void setPackageNameInstantiation(String packageNameInstantiation) {
        if ( currentCompilationUnit == null ) {
            error(null, "Attempt to set package name of a prototype instantiation outside a compilation unit");
            return;
        }
        currentCompilationUnit.setPackageNameInstantiation(packageNameInstantiation);
    }

    public String getPrototypeNameInstantiation() {
        if ( currentCompilationUnit == null ) {
            return null;
        }
        return currentCompilationUnit.getPrototypeNameInstantiation();
    }

    public void setPrototypeNameInstantiation(String prototypeNameInstantiation) {
        if ( currentCompilationUnit == null ) {
            error(null, "Attempt to set prototype name of a prototype instantiation outside a compilation unit");
            return;
        }
        currentCompilationUnit.setPrototypeNameInstantiation(prototypeNameInstantiation);
    }

    public int getLineNumberInstantiation() {
        if ( currentCompilationUnit == null ) {
            return -1;
        }
        return currentCompilationUnit.getLineNumberInstantiation();
    }

    public void setLineNumberInstantiation(int lineNumberInstantiation) {
        if ( currentCompilationUnit == null ) {
            error(null, "Attempt to set the line number of a prototype instantiation outside a compilation unit");
            return;
        }
        currentCompilationUnit.setLineNumberInstantiation(lineNumberInstantiation);
    }

    public int getColumnNumberInstantiation() {
        if ( currentCompilationUnit == null ) {
            return -1;
        }
        return currentCompilationUnit.getColumnNumberInstantiation();
    }

    public void setColumnNumberInstantiation(int columnNumberInstantiation) {
        if ( currentCompilationUnit == null ) {
            error(null, "Attempt to set the column number of a prototype instantiation outside a compilation unit");
            return;
        }
        currentCompilationUnit.setColumnNumberInstantiation(columnNumberInstantiation);
    }

    /**
     * stack with the variables initialize in each lexical level. It is
     * something like<br>
     * <code>
     * [ [. objectFor_i, "i", 0 .], [. objectFor_j, "j", 0 .], [. objectFor_k, "k", 1 .], [. objectFor_name, "name", 2 .] ]
     * </code><br>
     * using the Cyan syntax for arrays and tuples
     */
    private Stack<Tuple3<VariableDecInterface, String, Integer>> stackVariableLevel;

    /**
     * used to discover if some variable was not initialized
     */
    private int lexicalLevel;

    /**
     * points to
     */
    private Prototype cyException;

    /**
     * a table of local variables, which includes method parameters, function
     * parameters, and function local variables.
     */
    private Hashtable<String, VariableDecInterface> variableDecTable;

    /**
     * a stack of local variables, which includes method parameters, function
     * parameters, and function local variables.
     */
    private Stack<VariableDecInterface> variableDecStack;

    /**
     * contains fields declared in the current prototype
     * (that being analyzed at this moment)
     */
    private Hashtable<String, SlotDec> slotDecTable;

    /**
     * contains all private Prototypes of the current compilation Unit. That
     * is, of the current file being compiled.
     */
    private Hashtable<String, Prototype> privatePrototypeTable;

    /**
     * contains all source files names of packages imported by the compilation
     * Unit (a file) being analyzed. The importation is made by method
     * loadPackage
     */
    private Hashtable<String, Prototype> publicSourceFileNameTable;

    /**
     * if this object or interface is generic, genericObjectInterfaceTable is
     * the list of the generic parameters. It would contain T and R in the
     * object object Table<:T, :R> ... end
     */
    private Hashtable<String, GenericParameter> genericPrototypeFormalParameterTable;

    /**
     * current compilation unit.
     *
     */
    private CompilationUnit currentCompilationUnit;

    /**
     * the list of compilation units with the errors in each of them signalled using Env
     */
    private Map<CompilationUnit, List<UnitError>> mapCompUnitErrorList;
    /**
     * the project. Used to find the imported packages of an interface or object
     * declaration.
     */
    private Project project;

    /**
     * current object or interface. That is, the object that is being checked at
     * this moment, if there is one. null if what is being checked is not an
     * object.
     */
    private Prototype currentPrototype;

    /**
     * current method being compiled, if the checking is made inside a method.
     * null otherwise.
     */
    private MethodDec currentMethod;

    /**
     * list of functions declared in the current compilation unit
     */
    private List<ExprFunction> functionList;
    /**
     * stack of statements. The current statement should be passed as parameter
     * to method signalCompilerError of class SignalCompilerError.
     */
    private Stack<CodeWithError> statementStack;
    /**
     * stack of functions. At the end of the code generation/checking of a
     * function, it is removed from this stack.
     *
     */
    private Stack<ExprFunction> functionStack;

    /**
     * For each Function the compiler creates a brand new prototype that
     * inherits something as Function<R, T>. However, inside this prototype it
     * is legal to access the fields and methods of the enclosing
     * prototype of the function (including the inherited ones). This variable
     * is a link to this enclosing prototype. Methods and fields are
     * searched for first in this link if it is non-null
     */
    private ObjectDec enclosingObjectDec;

    /**
     * true if there was an error during the compilation
     */
    private final Ref<Boolean> thereWasError = new Ref<>();

    /**
     * the instruction set to the compilation
     */
    private HashSet<meta.CompilationInstruction> compInstSet;

    /**
     * the context of the code generated by a metaobject annotation. The elements of
     * each tuple are: an identifier, the metaobject name, the package name of
     * the metaobject annotation, the prototype name of the metaobject annotation, the
     * line number of the call, the offset in the source code of the annotation,
     * and a list of fields and methods the metaobject added
     * to a prototype in phase AFTER_RES_TYPES (phase 3). If this stack is not empty and there is a
     * compilation error, then the code that caused the error was introduced by
     * a metaobject annotation. This code was generated by a metaobject annotation in the
     * prototype specified in the tuple. That is, the statement
     * </p>
     * <code>cyanMetaobjectCompilationContextStack.push(new Tuple5<...>(...))</code>
     * </p>
     * is called before the compilation of the code generated by the metaobject
     * and
     * </p>
     * <code>cyanMetaobjectCompilationContextStack.pop()</code>
     * </p>
     * is called after the compilation of the code generated by the metaobject.
     * The compiler itself, when generating code for a metaobject, inserts annotations
     *  <code>{@literal @}pushCompilationContext</code> and
     * <code>{@literal @}popCompilationContext</code>.
     *
     * The last number in each tuple is the offset in the source code of the
     * first character of the annotation
     * <code>{@literal @}pushCompilationContext</code>
     */
    private Stack<Tuple7<String, String, String, String, Integer, Integer, List<ISlotSignature>>> cyanMetaobjectCompilationContextStack;

    /**
     * number for the next identifier of pushCompilationContext. The identifiers
     * will be "atisemAn_id_" + atisemAnIdNumber
     */
    public static int atisemAnIdNumber = 0;

    /**
     * Code may be inserted into a compilation unit by the compiler itself (see
     * Figure of chapter Metaobjects of the Cyan manual) or by metaobject annotations.
     * Code may be deleted from a compilation unit by metaobject annotations.
     * Therefore when the compiler finds an error it may point to the wrong
     * line. If code was inserted before the error the message would point to a
     * line number greater than it is. The opposite happens when code was
     * deleted. Variable lineShift keeps how many lines were inserted in the
     * code. If negative, -lineShift is how many lines were deleted.
     */
    private int lineShift;
    /**
     * list of pairs <code>(lineNumber, errorMessage, used)</code>. A metaobject
     * that is an instance of {@link meta#IInformCompilationError} signaled that
     * there should be an error in this source file (being compiled) at line
     * <code>lineNumber</code>. The possible error message is
     * <code>errorMessage</code>. 'used' is true if this error has been signaled
     * by the compiler
     */
    private List<Tuple3<Integer, String, Boolean>> lineMessageList;

    /**
     * a stack of literal objects. In <br>
     * <code>
     *    [* [* 1, 2 *] [* 3 *] *] <br>
     * </code> the stack will have two elements when calculating the internal
     * types of '2'. These literal objects include all metaobject annotations of
     * metaobjects that implement IParseWithCyanCompiler_parsing or IParseMacro_parsing.
     */
    private Stack<Annotation> metaobjectAnnotationParseWithCompilerStack;

    /**
     * A metaobject annotation such as<br>
     * <code>
     * Function<String>.#writeCode
     * </code> demand that the source code of Function<String> be written in the
     * directory in which this prototype is. The set below keeps all compilation
     * units prototypes for which the source code should be written to a file.
     * Of course, this only makes sense because metaobjects and the Cyan
     * compiler adds code to prototypes.
     */
    private HashSet<CompilationUnit> compilationUnitToWriteList;

    /**
     * true if a method being analyzed has inside it a metaobject javacode. It
     * it has, the compiler cannot deduce if the method will ever return a value
     * or not
     */
    private boolean hasJavaCode;

    /**
     * the name of a inner class representing a function or a method with the
     * outer class name. For example, it may be "_Program._Fun_0__"
     */
    private String currentClassNameWithOuter;

    /**
     * true if Env is being used for generating Java code for method 'eval' or
     * 'eval:' of an inner prototype
     */
    private boolean creatingInnerPrototypesInsideEval;

    private String strInitRefVariables;

    /**
     * true if the compiler is inside an 'init' or 'init:' method during code
     * generation
     */
    private boolean isInsideInitMethod;

    public void setFirstMethodStatement(boolean b) {
        firstMethodStatement = b;
    }

    public boolean getFirstMethodStatement() {
        return this.firstMethodStatement;
    }
    /*
     * inside an 'init' or 'init:' method, only the first statement can be a
     * call 'super init' or 'super init: a, b, c'. The flag firstMethodStatment
     * is used to check this.
     *
     */

    private boolean firstMethodStatement = false;

    public void begin_semAn_actions() {
        during_semAn_actions = true;
    }

    public void end_semAn_actions() {
        during_semAn_actions = false;

    }

    public boolean isInPackageCyanLang(String name) {
        return this.project.getProgram().isInPackageCyanLang(name);
    }

    /**
     * true if the compiler is during some SEM_AN action. That is, during semantic
     * analysis in the statements of methods the compiler is adding or removing
     * code. This is used to prevent, for example, a macro calling inside it
     * another macro or a literal expression
     */
    private boolean during_semAn_actions;

    /**
     * stack with the line numbers that start a pushCompilationContext
     */
    private Stack<Integer> offsetPushCompilationContextStack;

    /**
     * a list of non-existing prototypes, one for each generic parameter. This
     * is necessary for metaobject concept
     */
    private List<Prototype> prototypeForGenericPrototypeList;

    public void calculateSubtypes() {
        HashMap<String, Set<WrPrototype>> m;
        m = mapPackageSpaceCyanTypeNameToSubtypeList = new HashMap<>();
        for (final CyanPackage p : this.getProject().getPackageList()) {
            final String packageName = p.getPackageName() + ".";
            for (final CompilationUnit cunit : p.getCompilationUnitList()) {
                final Prototype pu = cunit.getPublicPrototype();
                if ( !pu.isGeneric() ) {
                    m.put(packageName + pu.getName(), new HashSet<WrPrototype>());
                }
            }
        }

        for (final CyanPackage p : this.getProject().getPackageList()) {
            for (final CompilationUnit cunit : p.getCompilationUnitList()) {
                final Prototype pu = cunit.getPublicPrototype();
                if ( !pu.isGeneric() ) {
                    if ( pu instanceof ObjectDec ) {
                        final ObjectDec obj = (ObjectDec) pu;
                        if ( obj.getSuperobject() != null ) {
                            final ObjectDec superObj = obj.getSuperobject();
                            final Set<WrPrototype> set = m
                                    .get(superObj.getCompilationUnit().getPackageName() + "." + superObj.getName());
                            if ( set == null ) {
                                this.error(null, "Internal error: prototype '" + superObj.getFullName()
                                        + "' was not found in calculateSubtypes()");
                            }
                            else {
                                // set is the set of subtypes of 'obj'
                                // super-type
                                set.add(pu.getI());
                            }
                        }
                        final List<Expr> exprInterList = obj.getInterfaceList();
                        if ( exprInterList != null ) {
                            for (final Expr exprInter : exprInterList) {
                                final InterfaceDec superInter = (InterfaceDec) exprInter.getType();
                                if ( superInter == null ) {
                                    this.error(null, "Internal error: superinterface of prototype '" + pu.getFullName()
                                            + "' was not found in calculateSubtypes()");
                                }
                                else {
                                    final Set<WrPrototype> set = m.get(superInter.getCompilationUnit().getPackageName()
                                            + "." + superInter.getName());
                                    if ( set == null ) {
                                        this.error(null, "Internal error: prototype '" + superInter.getFullName()
                                                + "' was not found in calculateSubtypes()");
                                    }
                                    else {
                                        // set is the set of subtypes of 'obj'
                                        // super-type
                                        set.add(pu.getI());
                                    }
                                }

                            }
                        }
                    }
                    else if ( pu instanceof InterfaceDec ) {
                        final InterfaceDec inter = (InterfaceDec) pu;
                        if ( inter.getSuperInterfaceList() != null ) {
                            for (final InterfaceDec superInter : inter.getSuperInterfaceList()) {
                                final Set<WrPrototype> set = m.get(
                                        superInter.getCompilationUnit().getPackageName() + "." + superInter.getName());
                                if ( set == null ) {
                                    this.error(null, "Internal error: prototype '" + superInter.getFullName()
                                            + "' was not found in calculateSubtypes()");
                                }
                                else {
                                    // set is the set of subtypes of 'obj'
                                    // super-type
                                    set.add(pu.getI());
                                }

                            }
                        }
                    }
                }
            }
        }

    }

    public int getLineShift() {
        return lineShift;
    }

    public void setLineShift(int lineShift) {
        this.lineShift = lineShift;
    }

    public String getCyanLangDir() {
        return this.project.getProgram().getCyanLangDir();
    }

    public String getCyanLangJarFile() {
        final String s = this.project.getProgram().getCyanLangDir();
        if ( s == null ) {
            return null;
        }
        else {
            return s + NameServer.fileSeparator + NameServer.cyanLangJar;
        }
    }

    public String getCyanRuntimeJarFile() {
        return this.project.getProgram().getCyanLangDir() + NameServer.fileSeparator + NameServer.cyanRuntime;
    }

    public void addNewTypeDef(String typename, WrType newtype) {
        if ( mapNewTypeToType == null ) {
            mapNewTypeToType = new HashMap<>();
        }
        mapNewTypeToType.put(typename, newtype);
    }

    public WrType searchType(String typename) {
        if ( this.mapNewTypeToType != null ) {
            return mapNewTypeToType.get(typename);
        }
        else {
            return null;
        }
    }

    public void clearMapNewTypeToType() {
        this.mapNewTypeToType.clear();
    }

    public IActionFunction searchActionFunction(String metaobjectName) {
        if ( this.currentCompilationUnit.getActionFunctionTable() != null ) {
            return this.currentCompilationUnit.getActionFunctionTable().get(metaobjectName);
        }
        return null;
    }

    /**
     * return a map with a key for each prototype or interface. The value for
     * the key is a set with all direct subtypes of the prototype or interface.
     * This map is only created on demand. The key has the format: the package
     * name, a single space, prototype name. It can be, for example,<br>
     * <code>
     * "br.main ChooseFoldersCyanInstallation"
     * </code><br>
     * The package name is "br.main" and the prototype name is "ChooseFoldersCyanInstallation".
     *
     */

    public HashMap<String, Set<WrPrototype>> getMapPrototypeSubtypeList() {
        if ( mapPackageSpaceCyanTypeNameToSubtypeList == null ) {
            calculateSubtypes();
            /*
             * try { } catch ( RuntimeException e ) { e.printStackTrace(); }
             */
        }
        return mapPackageSpaceCyanTypeNameToSubtypeList;
    }

    // throw new ExceptionContainer__(new _ExceptionCast(
    // "Cannot cast expression '(`sprint: \"aa\", 'A' `swith: 0, 3.14)' of line '62'
    // of file 'ChooseFoldersCyanInstallation.cyan' to cyan.lang.Any") );
    public String javaCodeForCastException(Expr exprToCast, Type leftType) {
        return "new _ExceptionCast( new CyString(\"Cannot cast expression '"
                + MetaLexer.escapeJavaString(exprToCast.asString()) + "' to '" + leftType.getFullName() + "' in line "
                + exprToCast.getFirstSymbol().getLineNumber() + " of file " + getCurrentCompilationUnit().getFilename()
                + "\") )";
    }

    public String javaCodeForCastException(VariableDecInterface varToCast, Type leftType) {
        return "new _ExceptionCast( new CyString(\"Cannot cast variable '" + varToCast.getName() + " to '"
                + leftType.getFullName() + "' in line " + varToCast.getFirstSymbol().getLineNumber() + "' of file '"
                + getCurrentCompilationUnit().getFilename() + "\") )";
    }

    /**
     * A metaobject informs which fields and methods it produces in phase AFTER_RES_TYPES (3).
     * This method uses information on the stack of push annotations to check if
     * metaobjects produced the fields and methods they said they should produce.
       @param methodSignature
     */
    public void checkSlot(GetNameAsInSourceCode slotToCheck) {
        // Stack<Tuple7<String, String, String, String, Integer, Integer,
        // List<ISlotSignature>>>
        if ( !this.cyanMetaobjectCompilationContextStack.isEmpty() ) {
            Tuple7<String, String, String, String, Integer, Integer, List<ISlotSignature>> t = this.cyanMetaobjectCompilationContextStack
                    .peek();
            List<ISlotSignature> slotList = this.cyanMetaobjectCompilationContextStack.peek().f7;
            if ( slotList != null ) {
                for (ISlotSignature slot : slotList) {
                    if ( slot.getNameWithDeclaredTypes().equals(slotToCheck.getNameWithDeclaredTypes()) ) {
                        // found the method or field. Remove it from the list
                        slotList.remove(slot);
                        return;
                    }
                }
                // slot was not found. Then the metaobject produced a slot it did not say it was
                // going to produce
                /*
                 * 	 * each tuple are: an identifier, the metaobject name, the package name of
                * the metaobject annotation, the prototype name of the metaobject annotation, the
                * line number of the call, and a list of fields and methods the metaobject added
                * to a prototype in phase AFTER_RES_TYPES (phase 3). If this stack is not empty and there is a

                 */
                this.error(slotToCheck.getFirstSymbol(),
                        "Annotation '" + t.f2 + "' of line '" + t.f5
                                + "' informed that it would produce a set of fields and methods (possible none). "
                                + "The field or method '" + slotToCheck.getNameWithDeclaredTypes()
                                + "' is not among them. It should.");
            }
        }
    }

    public void checkPushStackEmpty() {
        if ( !this.cyanMetaobjectCompilationContextStack.isEmpty() ) {
            Tuple7<String, String, String, String, Integer, Integer, List<ISlotSignature>> t = this.cyanMetaobjectCompilationContextStack
                    .peek();
            List<ISlotSignature> slotList = this.cyanMetaobjectCompilationContextStack.peek().f7;
            if ( slotList != null && slotList.size() > 0 ) {
                int size = slotList.size();
                StringBuilder s = new StringBuilder();
                for (ISlotSignature slot : slotList) {
                    s.append("'").append(slot.getNameWithDeclaredTypes()).append("'");
                    if ( --size > 0 ) {
                        s.append(", ");
                    }
                }
                this.error(this.getCurrentPrototype().getFirstSymbol(), "Annotation '" + t.f2 + "' of line '" + t.f5
                        + "' informed that it would produce a set of fields and methods (possible none). "
                        + "However, the following fields and methods were not produced: [ " + s.append(" ]").toString()

                );
            }
        }

    }

    public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(String fileName,
            String packageName, DirectoryKindPPP hiddenDirectory, int numParameters, List<String> realParamList) {

        String extension = "";

        int i = fileName.lastIndexOf('.');
        if ( i > 0 ) {
            extension = fileName.substring(i + 1);
            fileName = fileName.substring(0, i);
        }

        return readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);
    }

    public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(String fileName,
            String extension, String packageName, DirectoryKindPPP hiddenDirectory, int numParameters,
            List<String> realParamList) {
        return this.project.getCompilerManager().readTextFileFromPackage(fileName, extension, packageName,
                hiddenDirectory, numParameters, realParamList);

    }

    public Tuple4<FileError, char[], String, String> readTextFileFromProgram(String fileName, String extension,
            DirectoryKindPPP hiddenDirectory, int numParameters, List<String> realParamList) {
        return this.project.getCompilerManager().readTextFileFromProject(fileName, extension, hiddenDirectory,
                numParameters, realParamList);
    }

    public Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(String fileName, String packageName,
            DirectoryKindPPP hiddenDirectory) {
        return this.project.getCompilerManager().getAbsolutePathHiddenDirectoryFile(fileName, packageName,
                hiddenDirectory);
    }

    public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(String fileName, String packageName) {
        return this.project.getCompilerManager().readBinaryDataFileFromPackage(fileName, packageName);

    }

    public boolean deleteDirOfTestDir(String dirName) {
        final String testPackageName = this.getPackageNameTest();
        return this.project.getCompilerManager().deleteDirOfTestDir(dirName, testPackageName);
    }

    /**
     * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
     * Return an object of FileError indicating any errors.
     */
    public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName) {
        final String testPackageName = this.getPackageNameTest();
        return this.project.getCompilerManager().writeTestFileTo(data, fileName, dirName, testPackageName);
    }

    /**
     * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
     * Return an object of FileError indicating any errors.
     */
    public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
        return this.project.getCompilerManager().writeTestFileTo(data, fileName, dirName, packageName);
    }

    public String getPackageNameTest() {
        return this.currentCompilationUnit.getPackageName() + MetaHelper.suffixTestPackageName;
    }

    public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName, String packageName,
            DirectoryKindPPP hiddenDirectory) {
        return this.project.getCompilerManager().writeTextFile(charArray, fileName, prototypeFileName, packageName,
                hiddenDirectory);
    }

    public FileError writeTextFile(String str, String fileName, String prototypeFileName, String packageName,
            DirectoryKindPPP hiddenDirectory) {
        return this.project.getCompilerManager().writeTextFile(str, fileName, prototypeFileName, packageName,
                hiddenDirectory);
    }

    public String getPathFileHiddenDirectory(String prototypeFileName, String packageName,
            DirectoryKindPPP hiddenDirectory) {
        return this.project.getCompilerManager().getPathFileHiddenDirectory(prototypeFileName, packageName,
                hiddenDirectory);

    }

    /**
     * a set of all subtype of each program prototype or interface. This list is
     * only created on demand. The key to this map is the package name, space,
     * prototype name. It can be, for example,<br>
     * <code>
     * "br.main ChooseFoldersCyanInstallation"
     * </code><br>
     * The package name is "br.main" and the prototype name is "ChooseFoldersCyanInstallation".
     *
     */
    HashMap<String, Set<WrPrototype>> mapPackageSpaceCyanTypeNameToSubtypeList;

    /**
     * get a set of all sub prototypes of each program prototype (interfaces
     * excluded). This list is only created on demand. The key to this map is
     * the package name, space, prototype name. It can be, for example,<br>
     * <code>
     * "br.main ChooseFoldersCyanInstallation"
     * </code><br>
     * The package name is "br.main" and the prototype name is "ChooseFoldersCyanInstallation".
     *
     */
    HashMap<String, Set<ObjectDec>> mapPackageSpacePrototypeNameToSubprototypeList;

    /**
     * get a set of all sub interfaces of each program interface. This list is
     * only created on demand. The key to this map is the package name, space,
     * interface name. It can be, for example,<br>
     * <code>
     * "br.main WrType"
     * </code><br>
     * The package name is "br.main" and the interface name is "WrType".
     *
     */
    HashMap<String, Set<ObjectDec>> mapPackageSpaceInterfaceNameToSubinterfaceList;

    /**
     * get a set of all prototypes that implement a given interface. This list
     * is only created on demand. The key to this map is the package name,
     * space, interface name. It can be, for example,<br>
     * <code>
     * "br.main WrType"
     * </code><br>
     * The package name is "br.main" and the interface name is "WrType".
     *
     */
    HashMap<String, Set<ObjectDec>> mapPackageSpaceInterfaceNameToImplementedList;

    public void setPrefixErrorMessage(String errorMessage) {
        prefixErrorMessage = errorMessage;
    }

    /**
     * message to be added in front of the first error message the Env issue
     */
    private String prefixErrorMessage;

    public Map<CompilationUnit, List<UnitError>> getMapCompUnitErrorList() {
        return mapCompUnitErrorList;
    }

    public boolean getAllowCreationOfPrototypesInLastCompilerPhases() {
        return allowCreationOfPrototypesInLastCompilerPhases;
    }

    public void setAllowCreationOfPrototypesInLastCompilerPhases(
            boolean allowCreationOfPrototypesInLastCompilerPhases) {
        this.allowCreationOfPrototypesInLastCompilerPhases = allowCreationOfPrototypesInLastCompilerPhases;
    }

    public boolean getTopLevelStatements() {
        return topLevelStatements;
    }

    public void setTopLevelStatements(boolean topLevelStatements) {
        this.topLevelStatements = topLevelStatements;
    }

    public boolean peekCheckUsePossiblyNonInitializedPrototype() {
        return checkUsePossiblyNonInitializedPrototypeStack.peek();
    }

    public void pushCheckUsePossiblyNonInitializedPrototype(boolean checkUse) {
        this.checkUsePossiblyNonInitializedPrototypeStack.push(checkUse);
    }

    public void popCheckUsePossiblyNonInitializedPrototype() {
        this.checkUsePossiblyNonInitializedPrototypeStack.pop();
    }

    public boolean getIsArgumentToIsA() {
        return isArgumentToIsA;
    }

    public void setIsArgumentToIsA(boolean isArgumentToIsA) {
        this.isArgumentToIsA = isArgumentToIsA;
    }

    public boolean getSemAnInUnionType() {
        return semAnInUnionType;
    }

    public void setSemAnInUnionType(boolean semAnInUnionType) {
        this.semAnInUnionType = semAnInUnionType;
    }

    public void setProjectResTypes(boolean projectResTypes) {
        this.projectResTypes = projectResTypes;
    }

    public boolean getProjectResTypes() {
        return this.projectResTypes;
    }

    public boolean getAddTypeInfo() {
        return addTypeInfo;
    }

    public void setAddTypeInfo(boolean addTypeInfo) {
        this.addTypeInfo = addTypeInfo;
    }

    public String getFileNameToAddTypeInfo() {
		return fileNameToAddTypeInfo;
	}

	public void setFileNameToAddTypeInfo(String fileNameToAddTypeInfo) {
		this.fileNameToAddTypeInfo = fileNameToAddTypeInfo;
	}

	/**
     * true during the phase resType of the project file
     */
    private boolean projectResTypes;
    /**
     * true if the compiler is allowed to create instantiations of generic prototypes in
     * compilation phases >= 7.
     */
    private boolean allowCreationOfPrototypesInLastCompilerPhases;

    /**
     * true if the statements currently being analyzed are the top level statements of a method
     */
    private boolean topLevelStatements = true;

    /**
     * stack containing the set of local variables in each level and if each variable has been initialized
     */
    private Stack<LocalVarInfo> stackLocalVarInfo;
    /**
     * stack of repetition statements. Use 'f' for 'for', 'w' for 'while', and 'r' for 'repeat'
     */
    private Stack<Character> repetitionStatStack;

    /**
     * Stack of repetition and try-catch statements
     */
    private Stack<Statement> controlFlowStack;
    /**
     * map with keys (newType, type) in which an identifier newType is associated to a type. It
     * could be used in a future typedef declaration:
     *      typedef T is Array<Int>;
     *
     * currently it is used in the concept metaobject
     */
    private Map<String, WrType> mapNewTypeToType;

    /**
     * If the receiver of a message send is a prototype, then one of two things must
     * happen:
     *     . the method that corresponds to the message should be a 'final' method
     *       of prototype 'Any' OR;
     *     . the prototype should have a non-argument constructor ('init' method)
     *
     * When the receiver of a message send is a prototype, it is represented by
     * an AST object of class ExprIdentStar. In this case, ExprIdentStar::calcInternalTypes
     * is called after setting this field to 'true'. This field is then used to
     * check the two conditions given above.
     *
     * if true, check if a prototype being used as an expression has an 'init' method
     * OR it is being used as the receiver of a message send whose method
     * is 'final' and declared in 'Any'
     */
    private Stack<Boolean> checkUsePossiblyNonInitializedPrototypeStack;

    /**
     * true if the expressions being analyzed is a parameter to method 'isA:' of Any
     */
    private boolean isArgumentToIsA = false;
    /**
     * true if the semantic analysis is being made in an union type
     */
    private boolean semAnInUnionType = false;

    /**
     * true if this program should be compiled to be later used by program
     * addType that adds types to otherwise Dyn parameters and local variables
     */
    boolean addTypeInfo;

    /**
     * if addTypeInfo is true, this is the file name of the file to which type information
     * will be added at runtime of the program execution
     */
    String fileNameToAddTypeInfo;

}
