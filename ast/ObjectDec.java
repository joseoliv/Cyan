package ast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import ast.GenericParameter.GenericParameterKind;
import cyan.reflect._CyanMetaobjectAtAnnot;
import cyan.reflect._IActionMethodMissing__semAn;
import error.CompileErrorException;
import error.ErrorKind;
import lexer.Lexer;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAssignment_cge;
import meta.IActionMethodMissing_semAn;
import meta.ICompiler_semAn;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.Tuple6;
import meta.WrAnnotation;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

public class ObjectDec extends Prototype {


	public ObjectDec() {
	}

	public static void initObjectDec(ObjectDec newObjectDec, ObjectDec outerObject, Token visibility,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList,
			Lexer lexer ) {

        Prototype.initPrototype(newObjectDec, visibility,
            nonAttachedAnnotationList, attachedAnnotationList, outerObject);
        lexer.setPrototype(newObjectDec);

        newObjectDec.isAbstract = false;
        newObjectDec.isFinal = true;
        newObjectDec.openPackage = false;

        newObjectDec.interfaceList = new ArrayList<Expr>();
        //contextParameterArray = new ArrayList<ContextParameter>();
        newObjectDec.fieldList = new ArrayList<FieldDec>();
        newObjectDec.methodDecList = new ArrayList<MethodDec>();
        newObjectDec.initNewMethodDecList = new ArrayList<MethodDec>();
        newObjectDec.functionList = new ArrayList<ExprFunction>();
        newObjectDec.superobjectExpr = null;
        newObjectDec.slotList = new ArrayList<>();
        newObjectDec.beforeInnerObjectNonAttachedAnnotationList = null;
        newObjectDec.beforeInnerObjectAttachedAnnotationList = null;
        newObjectDec.exprFunctionForThisPrototype = null;
        newObjectDec.abstractMethodList = new ArrayList<>();
        newObjectDec.javaInterfaceList = null;
        newObjectDec.createdInitMethod = false;
        newObjectDec.hasContextParameter = false;

	}
	public ObjectDec( ObjectDec outerObject, Token visibility,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList,
			Lexer lexer) {
		super(visibility, nonAttachedAnnotationList, attachedAnnotationList, outerObject);
		lexer.setPrototype(this);

		this.isAbstract = false;
		this.isFinal = true;
		this.openPackage = false;

		this.interfaceList = new ArrayList<Expr>();
		//contextParameterArray = new ArrayList<ContextParameter>();
		this.fieldList = new ArrayList<FieldDec>();
		this.methodDecList = new ArrayList<MethodDec>();
		this.initNewMethodDecList = new ArrayList<MethodDec>();
		this.functionList = new ArrayList<ExprFunction>();
		this.superobjectExpr = null;
		this.slotList = new ArrayList<>();
		this.beforeInnerObjectNonAttachedAnnotationList = null;
		this.beforeInnerObjectAttachedAnnotationList = null;
		this.exprFunctionForThisPrototype = null;
		this.abstractMethodList = new ArrayList<>();
		this.javaInterfaceList = null;
		this.createdInitMethod = false;
		this.hasContextParameter = false;
	}

	@Override
	public ObjectDec clone() {
		return (ObjectDec ) super.clone();
	}

	@Override
	public void accept(ASTVisitor visitor) {

		visitor.preVisit(this);

		if ( this.superContextParameterList != null ) {
			for ( final ContextParameter cp : this.superContextParameterList ) {
				cp.accept(visitor);
			}
		}

		for ( final MethodDec m : this.initNewMethodDecList ) {
			m.accept(visitor);
		}


		for ( final MethodDec m : this.methodDecList ) {
			m.accept(visitor);
		}
		for ( final FieldDec iv : this.fieldList ) {
			iv.accept(visitor);
		}
		visitor.visit(this);
	}


	public void addSlot(SlotDec slot) {
		slotList.add(slot);
	}

	public void removeSlot(SlotDec field) {

		slotList.remove(field);
	}



	public void addField(FieldDec field) {

		fieldList.add(field);
	}


	public void removeField(FieldDec field) {

		fieldList.remove(field);
	}


	public void addMethod( MethodDec methodDec ) {
		final String name = methodDec.getNameWithoutParamNumber();
		if ( name.compareTo("init") == 0 ||
			 name.compareTo("init:") == 0 ||
			 name.compareTo("new") == 0 ||
			 name.compareTo("new:") == 0 )
			 initNewMethodDecList.add(methodDec);
		else {
			methodDecList.add(methodDec);
			if ( methodDec.isAbstract() ) {
				this.abstractMethodList.add(methodDec);
			}
		}
	}



	public void setSuperobjectExpr(Expr superobject) {
		this.superobjectExpr = superobject;
	}
	public Expr getSuperobjectExpr() {
		return superobjectExpr;
	}

	/**
	 * returns the super-prototype of this object. Returns null if this prototype is "Any"
	 * @return
	 */

	public ObjectDec getSuperobject() {
		return (ObjectDec ) superobject;

	}


	public void setInterfaceList(List<Expr> interfaceList) {
		this.interfaceList = interfaceList;
	}
	public List<Expr> getInterfaceList() {
		return interfaceList;
	}

	/**
	 * returns true if this is a context object. Since a context object
	 * may have no parameters, as in
	 *      object List()
	 *         ...
	 *      end
	 * we do not demand that the list have at least one element.
	 * @return
	 */
	public boolean isContextObject() {
		return contextParameterArray != null;
	}



	@Override
	public void genCyan(PWInterface pw, CyanEnv cyanEnv, boolean genFunctions) {

		cyanEnv.atBeginningOfPrototype(this);


		final ExprGenericPrototypeInstantiation exprGPI = cyanEnv.getExprGenericPrototypeInstantiation();

		super.genCyan(pw, cyanEnv, genFunctions);
		//#$ {

//		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
//			pw.println("@genericPrototypeInstantiationInfo(\"" +
//		         cyanEnv.getPackageNameInstantiation() + "\", \"" + cyanEnv.getPrototypeNameInstantiation()
//			  + "\", " + exprGPI.getFirstSymbol().getLineNumber() + ", " + exprGPI.getFirstSymbol().getColumnNumber() + ")");
//		}
		//#$ }


		pw.println("");
		if ( visibility != null && visibility != Token.PUBLIC ) {
			pw.print(NameServer.getVisibilityString(visibility) + " ");
		}
		if ( isAbstract )
			pw.print("abstract ");
		if ( !isFinal )
			pw.print("open ");
		pw.print("object ");
		this.genCyanPrototypeName(pw, cyanEnv);

		if ( contextParameterArray != null ) {
			pw.print("(");
			int size = contextParameterArray.size();
			for ( final ContextParameter p : contextParameterArray ) {
				p.genCyan(pw, false, cyanEnv, true);
				--size;
				if ( size > 0 )
					pw.print(", ");
			}
			pw.print(")");

		}

		pw.print(" ");
		if ( moListBeforeExtendsMixinImplements != null ) {
			for ( final AnnotationAt annotation : moListBeforeExtendsMixinImplements ) {
				annotation.genCyan(pw, false, cyanEnv, genFunctions);
				pw.print(" ");
			}
		}

		if ( superobjectExpr != null ) {
			pw.print("extends ");
			superobjectExpr.genCyan(pw, false, cyanEnv, true);
		}
		pw.println("");
		int size = interfaceList.size();
		if ( size > 0 ) {
			pw.print("          implements ");
			for ( final Expr t : interfaceList ) {
				t.genCyan(pw, false, cyanEnv, true);
				--size;
				if ( size > 0 )
					pw.print(", ");
			}
			pw.println("");
		}
		pw.add();


		for ( final MethodDec m : initNewMethodDecList )
			m.genCyan(pw, false, cyanEnv, true);
		for ( final MethodDec m : methodDecList ) {
			m.genCyan(pw, false, cyanEnv, true);
		}

		if ( ! cyanEnv.getGenInterfacesForCompiledCode() ) {
			pw.println("");
			for ( final FieldDec v : fieldList ) {
				if ( ! v.isContextParameter() )
					v.genCyan(pw, false, cyanEnv, genFunctions);
			}

			if ( this.getCompilationUnit().getProgram().getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_10 ) {
				for (final ObjectDec objDec : innerPrototypeList ) {
					objDec.genCyan(pw, cyanEnv, genFunctions);
				}
			}

			if ( beforeEndNonAttachedAnnotationList != null ) {
				for ( final AnnotationAt c : this.beforeEndNonAttachedAnnotationList )
					c.genCyan(pw, true, cyanEnv, genFunctions);
			}
		}

		pw.sub();
		pw.println("");
		pw.println("end");
		pw.println("");
		cyanEnv.atEndOfCurrentPrototype();

	}


	public void setContextParameterArray(List<ContextParameter> contextParameterArray) {
		this.contextParameterArray = contextParameterArray;
	}

	public List<ContextParameter> getContextParameterArray() {
		return contextParameterArray;
	}

	/**
	 * generate an interface in Java containing all public methods of
	 * this prototype. The interface name in Java will be <code>iname</code>. The
	 * file will be put in the same directory as the prototype itself.
	 *
	 */

	public void generateInterface(PWInterface pw, Env env) {


		env.atBeginningOfObjectDec(this);


		final String thisPackageName = this.compilationUnit.getPackageIdent().getName();
		pw.print("package " + thisPackageName + ";");


		pw.println();

		if ( this.visibility == Token.PRIVATE )
			pw.print("private ");
		else if ( this.visibility != Token.PACKAGE )
			pw.print("public ");

		pw.printIdent("interface " + MetaHelper.getJavaName("I" + this.getName()));

		pw.println(" {");
		pw.add();
		for ( final MethodDec meth : this.methodDecList ) {
			final MethodSignature ms = meth.getMethodSignature();

			if ( !meth.getShared() ) {
				ms.genJava(pw, env);
				pw.println(";");
			}
		}

		pw.sub();
		pw.printlnIdent("}");

		env.atEndOfObjectDec();


	}

	public static String saveSerializedToFile(Prototype prototype, String filename) {
		try(java.io.FileOutputStream file = new java.io.FileOutputStream(filename);
				java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(file)) {
	        out.writeObject(prototype);
		}
		catch (IOException e) {
			return "Error when saving serialized object '" + prototype.getFullName() + "' to file '" + filename + "'";
		}
		return null;
	}

	public static Tuple2<Prototype, String> loadSerializedFromFile(String filename) {
		try (   java.io.FileInputStream file = new java.io.FileInputStream(filename);
				java.io.ObjectInputStream in = new java.io.ObjectInputStream(file) ) {
			 Object obj = in.readObject();
			 if ( obj instanceof Prototype ) {
				 Prototype prototype = (Prototype ) obj;
				 return new Tuple2<Prototype, String>(prototype, null);
			 }
			 else {
					return new Tuple2<Prototype, String>(null,
							"Error when loading serialized object from file '" + filename +
							"'. The object type is not 'Prototype'. Probably it is 'Prototype' but from a wrong version");
			 }
		}
		catch (FileNotFoundException e) {
			return new Tuple2<Prototype, String>(null, "Error when loading serialized object from file '" + filename +
					"'. File was not found");
		}
		catch (IOException e) {
			return new Tuple2<Prototype, String>(null, "Error when loading serialized object from file '" + filename +
					"'. There was an IO exception");
		}
		catch (ClassNotFoundException e) {
			return new Tuple2<Prototype, String>(null, "Error when loading serialized object from file '" + filename +
					"'. The class was not found");
		}
	}



	@Override
	public void genJava(PWInterface pw, Env env) {

//		if ( this.getName().equals("ChooseFoldersCyanInstallation") ) {
//			saci.MyFile.write(this.compilationUnit);
//		}

		if ( isGeneric() ) {
			// should not generate code for generic prototypes. In fact, genJava should not even have have been
			// called in this case
			return ;
		}



		env.atBeginningOfObjectDec(this);


		genJavaCodeBeforeClassAnnotations(pw, env);



		int size;

		pw.printlnIdent("");
		pw.printlnIdent("@SuppressWarnings( { \"unused\", \"cast\", \"hiding\" } )");
		if ( this.outerObject == null ) {
			if ( this.visibility == Token.PRIVATE )
				pw.print("private ");
			else if ( this.visibility != Token.PACKAGE )
				pw.print("public ");

			if ( this.isFinal )
				pw.print("final ");
		}
		else {
			// inner classes are always private
			pw.print("private ");
		}

		pw.print("class ");
		final String className = getJavaNameWithoutPackage();


		pw.print( className );
		pw.print(" ");

		if ( moListBeforeExtendsMixinImplements != null ) {
			for ( final AnnotationAt annotation : moListBeforeExtendsMixinImplements ) {
				annotation.genJava(pw, env);
				pw.print(" ");
			}
		}

		if ( superobject != null ) {
			pw.print("extends " + MetaHelper.getJavaName(superObjectName));
		}

		pw.println("");



		size = interfaceList.size();
		if ( size > 0 ) {
			pw.print("  implements ");
			for ( final Expr t : interfaceList ) {
				pw.print(" " + t.getType().getJavaName());
				--size;
				if ( size > 0 )
					pw.print(", ");
			}
			if ( superobject == null ) {
				   // Any or Nil
				for ( final String iname : ObjectDec.interfacesImplementedByAny ) {
					pw.print(", " + iname);
				}
				// pw.print(", Cloneable");
			}
			if ( javaInterfaceList != null ) {
				for ( final String s : javaInterfaceList ) {
					pw.print(", " + s);
				}
			}
		}
		else {
			if ( superobject == null ) {
				   // Any or Nil
				// pw.print("      implements Cloneable");
				pw.print("  implements ");
				int sizeiiba = ObjectDec.interfacesImplementedByAny.length;
				for ( final String iname : ObjectDec.interfacesImplementedByAny ) {
					pw.print(iname);
					if ( --sizeiiba > 0 )
						pw.print(", ");
				}
			}

			if ( javaInterfaceList != null ) {
				if ( superobject != null )
					pw.print("  implements");
				else
					pw.print(",");
				int size2 = this.javaInterfaceList.size();
				if ( javaInterfaceList != null ) {
					for ( final String s : javaInterfaceList ) {
						pw.print(" " + s);
						if ( --size2 > 0 )
							pw.print(", ");
					}
				}
			}
		}
		pw.println(" {");


		pw.println("");
		int hashCodeClassName = className.hashCode();
		long serialVersionUID = ((long ) hashCodeClassName)*((long ) hashCodeClassName)*hashCodeClassName;
		pw.println("	private static final long serialVersionUID = " + serialVersionUID + "L;");

		List<MethodSignature> initList = this.searchInitNewMethod("init");
		boolean hasInitMethod =  initList != null && initList.size() > 0;
		final boolean hasInitSharedMethod = this.searchMethodPrivate(MetaHelper.initShared) != null;

		boolean protoFromInterface = NameServer.isPrototypeFromInterface(className);
		if ( this.outerObject == null ) {
			if (  hasInitMethod || protoFromInterface ) {
				pw.println("    static { ");
				if ( hasInitSharedMethod ) {
					pw.println("          _initShared();");
				}
				/*
                       This prototype is referenced indirectly in a method called
                       in its 'init' method. This
                       happens in this example:
                           object InitError
                               func init {
                                   MakeError accessInitError;
                               }
                           end
                       Method 'accessInitError' is just
                           func accessInitError {
                               InitError prototypeName println;
                           }
                       Then InitError is referenced before the 'init'
                       method has built the object that represents the
         		*/
				pw.println("        try {\r\n");
				pw.println("          " + className + ".prototype = new " + className + "();");
				pw.println("        }\r\n" +
                        "        catch ( ExceptionInInitializerError e ) {\r\n" +
                        "            System.out.println(\"Probably this prototype is referenced indirectly in a method called \" + \r\n" +
                        "\"in its 'init' method. This \" + \r\n" +
                        "\"happens in this example:\\n\" + \r\n" +
                        "\"    object InitError\\n\" + \r\n" +
                        "\"        func init { \\n\" + \r\n" +
                        "\"            MakeError accessInitError;\\n\" + \r\n" +
                        "\"        }\\n\" + \r\n" +
                        "\"    end\\n\" + \r\n" +
                        "\"Method 'accessInitError' is just\\n\" + \r\n" +
                        "\"    func accessInitError { \\n\" + \r\n" +
                        "\"        InitError prototypeName println; \\n\" + \r\n" +
                        "\"    }\\n\" + \r\n" +
                        "\"Then InitError is referenced before the 'init' \" + \r\n" +
                        "\"method has built the object that represents the \" + \r\n" +
                        "\"prototype\"); \r\n" +
                        "       }\r\n");

				pw.println("    }");

			}
			else {
				pw.println("    static { ");
				if ( hasInitSharedMethod ) {
					pw.println("          _initShared();");
				}
				pw.println("          " + className + ".prototype = new " + className + "(new NonExistingJavaClass());");
				pw.println("          " + className + ".prototype.initPrototype();");
				pw.println("    }");

			}

		}

		// start of Java class generation

		final StringBuffer refVarInitStr = new StringBuffer();
		final StringBuffer refVarInitStrForInitPrototype = new StringBuffer();

		String outs;
		for ( final FieldDec v : fieldList ) {
			if ( ! v.isShared() ) {
				if ( v.getRefType() ) {
					if ( !(v instanceof ast.ContextParameter) ) {
						outs = "        this." + v.getJavaName() + " = new Ref<" + v.getType().getJavaName() + ">();\n";
						refVarInitStr.append(outs);
						refVarInitStrForInitPrototype.append(outs);
					}
					else {
						// v is a context parameter. Then the Ref object should be created only for initializing the prototype
						// itself
						refVarInitStrForInitPrototype.append("        this." + v.getJavaName() + " = new Ref<" + v.getType().getJavaName() + ">();\n");
					}
					if ( v.getExpr() != null ) {
						final Type leftType = v.getType();
						final Tuple2<IActionAssignment_cge, ObjectDec> cyanMetaobjectPrototype = MetaInfoServer.getChangeAssignmentCyanMetaobject(env, leftType);
						IActionAssignment_cge changeCyanMetaobject = null;
				        ObjectDec prototypeFoundMetaobject = null;
				        if ( cyanMetaobjectPrototype != null ) {
				        	changeCyanMetaobject = cyanMetaobjectPrototype.f1;
				        	prototypeFoundMetaobject = cyanMetaobjectPrototype.f2;
				        }
				        final Expr rightExpr = v.getExpr();
				        final Tuple2<String, String> t = rightExpr.genTmpVarJavaAsString(env);
				        String rightExprTmpVar = t.f1;
						if ( changeCyanMetaobject != null ) {
				   			/*
				   			 * assignment is changed by the metaobject attached to the prototype that is
				   			 * the type of the right-hand side
				   			 */

							try {
								rightExprTmpVar = changeCyanMetaobject.cge_changeRightHandSideTo(
					        			prototypeFoundMetaobject,
					        			rightExprTmpVar, rightExpr.getType(env));
							}
							catch ( final error.CompileErrorException e ) {
							}
							catch ( final NoClassDefFoundError e ) {
								final WrAnnotation annotation = ((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation();
								env.error(meta.GetHiddenItem.getHiddenSymbol(annotation.getFirstSymbol()), e.getMessage() + " " + NameServer.messageClassNotFoundException);
							}
							catch ( final RuntimeException e ) {
								final WrAnnotation annotation = ((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation();
								env.thrownException(meta.GetHiddenItem.getHiddenCyanAnnotation(annotation),
										meta.GetHiddenItem.getHiddenSymbol(annotation.getFirstSymbol()), e);
							}
							finally {
			   					env.errorInMetaobject( (meta.CyanMetaobject ) changeCyanMetaobject, this.getFirstSymbol());
							}


						}
		       			// regular assignment
						outs = "";
						if ( t.f2 != null && t.f2.length() > 0 )
							outs += "    " + t.f2;
						outs += "\n    this." + v.getJavaName() + ".elem = " + rightExprTmpVar + ";\n";

						if ( env.getAddTypeInfo() && (leftType == null || leftType == Type.Dyn) ) {
							outs += "    " + v.codeAddsRuntimeTypeInfo() + "\r\n";
						}

						refVarInitStr.append(outs);
						refVarInitStrForInitPrototype.append(outs);

					}
				}
				else if ( v.getExpr() != null ) {


					final Type leftType = v.getType();
					final Tuple2<IActionAssignment_cge, ObjectDec> cyanMetaobjectPrototype = MetaInfoServer.getChangeAssignmentCyanMetaobject(env, leftType);
					IActionAssignment_cge changeCyanMetaobject = null;
			        ObjectDec prototypeFoundMetaobject = null;
			        if ( cyanMetaobjectPrototype != null ) {
			        	changeCyanMetaobject = cyanMetaobjectPrototype.f1;
			        	prototypeFoundMetaobject = cyanMetaobjectPrototype.f2;
			        }
			        final Expr rightExpr = v.getExpr();
			        final Tuple2<String, String> t = rightExpr.genTmpVarJavaAsString(env);
			        String rightExprTmpVar = t.f1;

					if ( changeCyanMetaobject != null ) {
			   			/*
			   			 * assignment is changed by the metaobject attached to the prototype that is
			   			 * the type of the right-hand side
			   			 */

						try {
							rightExprTmpVar = changeCyanMetaobject.cge_changeRightHandSideTo(
				        			prototypeFoundMetaobject,
				        			rightExprTmpVar, rightExpr.getType(env));
						}
						catch ( final error.CompileErrorException e ) {
						}
						catch ( final NoClassDefFoundError e ) {
							final WrAnnotation annotation = ((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation();
							env.error(meta.GetHiddenItem.getHiddenSymbol(
									annotation.getFirstSymbol()), e.getMessage() + " " + NameServer.messageClassNotFoundException);
						}
						catch ( final RuntimeException e ) {
							final WrAnnotation annotation = ((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation();
							env.thrownException(
									meta.GetHiddenItem.getHiddenCyanAnnotation(annotation),
									meta.GetHiddenItem.getHiddenSymbol(annotation.getFirstSymbol()), e);
						}
						finally {
		   					env.errorInMetaobject( (meta.CyanMetaobject ) changeCyanMetaobject, this.getFirstSymbol());
						}


					}
					else {
						if ( leftType == Type.Any && rightExpr.getType() instanceof InterfaceDec ) {
							rightExprTmpVar = "( " + MetaHelper.AnyInJava + " ) " + rightExprTmpVar;
						}
					}
	       			// regular assignment
					outs = "";
					if ( t.f2 != null && t.f2.length() > 0 )
						outs += "    " + t.f2;
					outs += "\n    this." + v.getJavaName() + " = " + rightExprTmpVar + ";\n";

					if ( env.getAddTypeInfo() && (leftType == null || leftType == Type.Dyn) ) {
						outs += "    " + v.codeAddsRuntimeTypeInfo() + "\r\n";
					}

					refVarInitStr.append(outs);
					refVarInitStrForInitPrototype.append(outs);
				}
				else {
					String value = "";
					final Type t = v.getType();
					if ( t == Type.Boolean )
						value = MetaHelper.BooleanInJava + "." + "cyFalse";
					else if ( t == Type.Byte )
						value = "new CyByte(0)";
					else if ( t == Type.Char )
						value = "new CyChar('\0')";
					/*else if ( t == Type.CySymbol )
						value = "new _CySymbol(\"\")";  */
					else if ( t == Type.Double )
						value = "new CyDouble(0)";
					else if ( t == Type.Float )
						value = "new CyFloat(0)";
					else if ( t == Type.Int )
						value = "CyInt.zero";
					else if ( t == Type.Long )
						value = "new CyLong(0)";
					else if ( t == Type.Short )
						value = "new CyShort(0)";
					else if ( t == Type.String )
						value = "new CyString(\"\")";
					else if ( t == Type.Dyn )
						value = "_Nil.prototype";
					else {
						if ( t instanceof InterfaceDec ) {
							value = MetaHelper.getJavaName(NameServer.prototypeFileNameFromInterfaceFileName(t.getFullName())) + ".prototype";
						}
						else if ( t instanceof TypeJavaRef ) {
							value = "null";
						}
						else if ( t instanceof TypeUnion ) {
							value = "null";
						}
						else {
							value = t.getJavaName() + ".prototype";
						}
					}

					refVarInitStrForInitPrototype.append( "this." + v.javaNameWithRef()  + " = " + value + ";");
				}
			}
		}

		env.setStrInitRefVariables(refVarInitStr.toString());


		pw.add();
		if ( ! hasInitMethod ) {
			pw.printlnIdent("public " + className + "(NonExistingJavaClass doNotExit) {");
			pw.add();
			if ( this.superobject != null ) {
				List<MethodSignature> superInitList = ((ObjectDec ) superobject).searchInitNewMethod("init");
				boolean hasSuperInitMethod =  superInitList != null && superInitList.size() > 0;
				if ( hasSuperInitMethod ) {
					pw.printlnIdent("super();");
				}
				else {
					pw.printlnIdent("super(doNotExit);");
				}
			}
			if ( this.outerObject == null ) {
				for ( final ObjectDec innerObject : this.innerPrototypeList ) {
					pw.printlnIdent("prototype" + innerObject.getName() +
							" = this.new " + innerObject.getJavaNameWithoutPackage() + "();");
				}

				if ( hasInitSharedMethod ) {
					pw.printlnIdent( MetaHelper.getJavaName(MetaHelper.initShared) + "();");
				}

			}
			pw.sub();
			pw.printlnIdent("}");
		}


		if ( this.outerObject == null && ! hasInitMethod )
			createInitPrototypeMethod(pw, refVarInitStrForInitPrototype);


		if ( attachedAnnotationList != null ) {
			for ( final AnnotationAt c : attachedAnnotationList )
				c.genJava(pw, env);
		}

		/*
		pw.printlnIdent("public " + getJavaName() + "() { ");
		pw.add();
		for ( FieldDec v : fieldList ) {
			if ( v.getRefType() )
				pw.printlnIdent(v.getJavaName() + " = new Ref<" + v.getType().getJavaName() + ">();");
		}
		pw.sub();
		pw.printlnIdent("}");
		*/
		/*String classNameWithOuter = className;
		if ( outerObject != null )
			classNameWithOuter = outerObject.getJavaName() + "." + classNameWithOuter;
		env.setCurrentClassNameWithOuter(classNameWithOuter);  */
		for ( final FieldDec v : fieldList )
			v.genJava(pw, env);


		final List<MethodSignature> msInitList = this.searchInitNewMethod("init");
		if ( msInitList.size() == 0 ) {
			pw.printlnIdent("public " + this.getJavaNameWithoutPackage() + "() { }");
		}


		env.setIsInsideInitMethod(true);


		for ( final MethodDec m : initNewMethodDecList )
			m.genJava(pw, env);

		env.setIsInsideInitMethod(false);

		for ( final MethodDec m : methodDecList ) {
			m.genJava(pw, env);
		}
		if ( this.multiMethodListList != null ) {
			for ( final List<MethodDec> multiMethodList : this.multiMethodListList ) {
				multiMethodList.get(0).genJavaOverloadedMethod(pw, env, multiMethodList);
			}
		}

		if ( this.outerObject == null ) {
			//pw.printlnIdent("public static " + className + " prototype = new " + className + "();");
			pw.printlnIdent("public static " + className + " prototype;");

			for ( final ObjectDec innerObject : this.innerPrototypeList ) {
				pw.printlnIdent("private static " + innerObject.getJavaNameWithoutPackage() + " prototype" + innerObject.getName() + ";" );
						//" = prototype.new " + innerObject.getJavaNameWithoutPackage() + "();");

				genJavaVariables(pw, env, true, innerObject.getName(), innerObject);
			}
			genJavaVariables(pw, env, false, "", this);
		}




		boolean hasSharedVariable = false;
		/*
		if ( fieldList.size() > 0 ) {
			pw.printlnIdent("// initialize the fields");
			pw.printlnIdent("public void initObject() { ");
			pw.add();
			for ( FieldDec v : fieldList ) {
				if ( v.isShared() )
					hasSharedVariable = true;
				else if ( v.getExpr() != null ) {
					String exprStr = v.getExpr().genJavaExpr(pw, env);
					if ( v.getRefType() )
						pw.printlnIdent(v.getJavaName() + ".elem = " + exprStr + ";");
					else
						pw.printlnIdent(v.getJavaName() + " = " + exprStr + ";");
				}

			}
			pw.sub();
			pw.printlnIdent("}");
		}
		*/
		if ( this.outerObject == null ) {
			/*
			 * inner classes in Java cannot have static fields
			 */
			boolean hasSharedNonInitializedVariable = false;
			String nonInitStrList = "";
			for ( final FieldDec v : fieldList ) {
				if ( v.isShared() ) {
					hasSharedVariable = true;
					if ( v.getExpr() == null ) {
						hasSharedNonInitializedVariable = true;
						if ( nonInitStrList.length() == 0 )
							nonInitStrList += v.getName();
						else
							nonInitStrList += ", " + v.getName();
					}
					break;
				}
			}
			// boolean hasInitSharedMethod = this.searchMethodPrivate(NameServer.initShared) != null;

			if ( hasSharedNonInitializedVariable && ! hasInitSharedMethod ) {
				env.error(this.getSymbol(), "This prototype has at least one shared instance "
						+ "variable (" + nonInitStrList + ") that is not initialized in its declaration and it does not "
						+ "have an 'initShared' method. This is illegal.", true, false);
			}
			if ( hasSharedVariable || hasInitSharedMethod ) {
				pw.printlnIdent("static { ");
				pw.add();
				if ( hasSharedVariable ) {
					for ( final FieldDec v : fieldList ) {
						if ( v.isShared() ) {
							if ( v.getRefType() ) {
								pw.printlnIdent(v.getJavaName() + " = new Ref<" + v.getType().getJavaName() + ">();");
							}
							if ( v.getExpr() != null ) {
								final String exprStr = v.getExpr().genJavaExpr(pw, env);
								if ( v.getRefType() )
									pw.printlnIdent(v.getJavaName() + ".elem = " + exprStr + ";");
								else
									pw.printlnIdent(v.getJavaName() + " = " + exprStr + ";");
							}
						}
					}
				}
				//
				pw.sub();
				pw.printlnIdent("}");
			}
		}


		if ( beforeEndNonAttachedAnnotationList != null ) {
			for ( final AnnotationAt c : this.beforeEndNonAttachedAnnotationList )
				c.genJava(pw, env);
		}
		/**
		 * list of full method names
		 */


		/*
		boolean foundFunctionProto = false;
		if ( this.superobject instanceof ObjectDec ) {
			ObjectDec p = (ObjectDec ) this.superobject;
			while ( p != null && p != Type.Any ) {
				if ( p.getIdent().equals("Function") ) {
					foundFunctionProto = true;
					break;
				}
				else
					p = p.getSuperobject(env);
			}
		}
		*/

		/*
		 * methods as objects was temporarily removed from the language
		 */
		/*
		if ( ! foundFunctionProto ) {
			// not Function of sub-prototype of it. Then methods are objects
			/**
			 * list of inner prototypes that represent the methods of this prototype
			 * /

			pw.printlnIdent("static " + Type.Any.getJavaName() + " []prototypeMethodList = { ");
			pw.add();
			int sizePML = methodDecList.size();
			for ( MethodDec m : methodDecList ) {
				pw.printIdent(m.getPrototypeNameForMethod() + ".prototype");
				if ( --sizePML > 0 )
					pw.print(",");
				pw.println();
			}
			pw.printlnIdent("};");
			pw.sub();

		}


		pw.printlnIdent(Type.Any.getJavaName() + " getPrototypeForMethod(String s) { ");
		pw.add();


		if ( foundFunctionProto ) {
			  /*
			   * if this prototype inherits from any of the Function (or is a Function), its methods
			   * are not objects. Therefore this method should always return null
			   * /
			pw.printlnIdent("return null;");
		}
		else {
			pw.printlnIdent("for( int i = 0; i < methodNameList.length; ++i) ");
			pw.add();
			pw.printlnIdent("if ( methodNameList[i].s.equal(s) ) return prototypeMethodList[i];");
			pw.sub();
			//pw.printlnIdent("}");
			pw.sub();
			if ( symbol.getSymbolString().compareTo("Any") != 0 )
				pw.printlnIdent("super.getPrototypeForMethod(s);");
			else
				pw.printlnIdent("return null;");
		}

		pw.printlnIdent("}");
		*/

		/*
		 * for each public or protected method in the super-prototype, generate a private Java method that calls the super method.
		 * Then the method <br>
		 * <code>
		 *    fun m: Int n -> Int { ... } <br>
		 * <code>
		 * in the super-prototype will cause the creation of<br>
		 * <code>
		 *     CyInt  __super_m(CyInt _n) { return super._m(_n); }
		 * </code><br>
		 * in this prototype. This is necessary because for each function is created an inner Java class (in the future,
		 * for each method too). The code of method 'eval:' or 'eval' of this class may use 'super'. But
		 * here 'super' would mean the superclass of this inner Java class, which would be wrong.
		 * Therefore it is necessary to call a private method that calls super.
		 */
		if ( superobject != null && superobject instanceof ObjectDec ) {
			final ObjectDec superObj = (ObjectDec ) superobject;
			final List<MethodDec> superMethodList = new ArrayList<>();
			for ( final MethodDec m : superObj.methodDecList ) {
				if ( m.getVisibility() == Token.PUBLIC || m.getVisibility() == Token.PROTECTED )  {
					superMethodList.add(m);
				}
			}

			if ( this.getOuterObject() == null ) {
				createMethodsToCallSuperMethodsInInnerClasses(pw, env, superMethodList);
			}
		}
		// ! foundFunctionProto &&
		if ( innerPrototypeList != null && innerPrototypeList.size() > 0 ) {
			/*
			 * if this is an inner prototype that inherits from Function, it should not have
			 * inner prototypes corresponding to its methods. These inner
			 * prototypes start their names with NameServer.methodProtoName
			 */
			for ( final ObjectDec innerObject : this.innerPrototypeList ) {
				if ( ! NameServer.isNameInnerProtoForMethod(innerObject.getName()) ) {
					innerObject.genJava(pw, env);
					pw.println();
				}
			}
		}
		if ( this != Type.Nil ) {
			if ( this.outerObject == null )
				genJavaMethods(pw, env, false, "");
			else
				genJavaMethods(pw, env, true, getName());
		}

		if ( this.outerObject == null ) {
			/*
			 * inner Java classes cannot have static sections
			 */
			genJavaCodeStaticSectionAnnotations(pw, env);
		}


		genJavaClassBodyDemandedByAnnotations(pw, env);


		pw.sub();
		pw.println("");
		pw.println("}");
		env.atEndOfObjectDec();
	}

	/** create method initPrototype that initializes all fields of the prototype with
	 *  default values.
	   @param pw
	   @param refVarInitStr
	 */
	private void createInitPrototypeMethod(PWInterface pw, StringBuffer refVarInitStrForInitPrototype) {
		pw.printlnIdent("public void initPrototype() {");
		pw.add();  //		hasBeenInitialized_" + className

		//pw.println("if ( ! hasBeenInitialized_____ ) { ");
		//pw.add();
		if ( superobject != null ) {
			//pw.printlnIdent("super.initPrototype();");
		}
		pw.printlnIdent(refVarInitStrForInitPrototype);
		// pw.printlnIdent(refVarInitStr);

		//pw.sub();
		//pw.println("}");
		pw.sub();
		pw.printlnIdent("}");
	}

	/**
	   @param pw
	   @param env
	   @param superMethodList
	 */
	@SuppressWarnings("static-method")
	private void createMethodsToCallSuperMethodsInInnerClasses(PWInterface pw, Env env,
			List<MethodDec> superMethodList) {
		/*
		 *  Given
		 *         func m: Int n = n
		 *  in the super-prototype, generate
		 * 	     private CyInt _m_super__(CyInt n)  { return super.m(n); }

		 */
		for ( final MethodDec m : superMethodList ) {
			if ( m.isAbstract() )
				continue;

			final MethodSignature ms = m.getMethodSignature();
			Type returnType = ms.getReturnType(env);
			String returnTypeStr = returnType == Type.Nil ? "void" : returnType.getJavaName();
			pw.printIdent("private " + returnTypeStr + " " );

			if ( ms instanceof MethodSignatureWithKeywords ) {

				final MethodSignatureWithKeywords msng = (MethodSignatureWithKeywords ) ms;
				pw.print( NameServer.getNamePrivateMethodForSuperclassMethod(msng.getJavaName()) + "( ");
				int sizesa = msng.getParameterList().size();
				int i = 0;
				for ( final ParameterDec p : msng.getParameterList() ) {
					final Type paramType = p.getType(env);
					if ( paramType instanceof TypeJavaRef ) {
						pw.print(paramType.getFullName(env) + " p" + i);
					}
					else {
						pw.print(MetaHelper.getJavaName(paramType.getFullName(env)) + " p" + i);
					}
					if ( --sizesa > 0 )
						pw.print(", ");
					++i;

				}
				pw.print(" ) { ");
				if ( returnType != Type.Nil ) {
					pw.print("return ");
				}
				pw.print("super." + msng.getJavaName() + "( ");

				sizesa = msng.getParameterList().size();
				i = 0;
				for ( @SuppressWarnings("unused") final ParameterDec p : msng.getParameterList() ) {
					pw.print("p" + i);
					if ( --sizesa > 0 )
						pw.print(", ");
					++i;
				}

				pw.println(" ); }");

			}
			else if ( ms instanceof MethodSignatureUnary ) {
				/*
				 *  Given
				 *         fun m -> Int = 0
				 *  in the super-prototype, generate
				 * 	     private CyInt __super_m()  { return super.m(); }

				 */

				final MethodSignatureUnary msng = (MethodSignatureUnary ) ms;
				pw.print(NameServer.getNamePrivateMethodForSuperclassMethod(
						msng.getJavaName()) + "() { ");
				if ( returnType != Type.Nil ) {
					pw.print("return ");
				}
				pw.print("super.");


				pw.println(msng.getJavaName() + "(); }");
			}
			else if ( ms instanceof MethodSignatureOperator ) {
				final MethodSignatureOperator mso = (MethodSignatureOperator ) ms;
				if ( mso.getOptionalParameter() == null ) {
					// unary operator
					pw.print( NameServer.getNamePrivateMethodForSuperclassMethod(mso.getJavaName()) + "() { ");
					if ( returnType != Type.Nil ) {
						pw.print("return ");
					}
					pw.print("super.");
					pw.println(mso.getJavaName() + "(); }");
				}
				else {
					// binary
					final ParameterDec paramDec = mso.getOptionalParameter();
					pw.print( NameServer.getNamePrivateMethodForSuperclassMethod(mso.getJavaName()) + "(" + paramDec.getType().getJavaName() +
							" " + paramDec.getJavaName() + ") { ");
					if ( returnType != Type.Nil ) {
						pw.print("return ");
					}
					pw.print("super.");
					pw.println(mso.getJavaName() + "(" + paramDec.getJavaName() +  "); }");
				}

			}
		}
	}

	protected void genJavaMethods(PWInterface pw, Env env, boolean isInnerProto, String innerProtoName) {

		pw.print("    public String []getFieldTypeList() { \n");
		pw.print("        return fieldTypeList");
		if ( isInnerProto ) pw.print(innerProtoName);
		pw.print(";\n");
		pw.print("    }\n");

		pw.print("    public String []getFieldList() { \n");
		pw.print("        return fieldList");
		if ( isInnerProto ) pw.print(innerProtoName);
		pw.print(";\n");
		pw.print("    }\n");

		final String currentPrototypeName = getName();
		String currentPrototypeTypeName;
		if ( getCompilationUnit().getIsPrototypeInterface() ) {
			// was created from an interface by the compiler. Use the interface
			// name as parameter
			currentPrototypeTypeName = NameServer.interfaceNameFromPrototypeName(currentPrototypeName);
		}
		else
			currentPrototypeTypeName = currentPrototypeName;

		//List<MethodSignature> methodSignatureList;
		//methodSignatureList = searchMethodPrivateProtectedPublic("asString:1");
		final String defaultIdentNumberJavaName = NameServer.javaNameObjectAny + "." + "defaultIdentNumber__";


		pw.println("    public String asString(int ident) {");
		pw.println("        String s =  \"" + currentPrototypeTypeName + " {\\n\";");
		if ( this.superobject != null ) {
			pw.println("        s = s + \"super(" + superObjectName
					+ "):\"  + super.asStringThisOnly" //+ NameServer.javaName_asStringThisOnly
					+ "( ident + " + defaultIdentNumberJavaName
					+ " );");
		}
		pw.println("        s = s + asStringThisOnly( ident + " + defaultIdentNumberJavaName + ");\n");
		pw.print("        s = s + getWhiteSpaces(ident) + \"}\\n\";\n");
		pw.print("        return s;\n");
		pw.print("    } \n");



		if ( this.superobject != null ) {
			pw.println("    @Override ");
		}

		pw.println("    protected String asStringThisOnly(int ident) {");
		pw.println("        String s = getWhiteSpaces(ident);");

		for (final FieldDec iv : getFieldList()) {
			final String ivJavaName = MetaHelper.getJavaName(iv.getName());
			pw.print("        s = s + getWhiteSpaces(ident)" + " + \""
			    + iv.getName() + ": \" + " + ivJavaName);
			if ( iv.getRefType() ) pw.print(".elem");
			Type type = iv.getType();
			if ( type instanceof Prototype || type instanceof TypeWithAnnotations ) {
				pw.print("._asString().s ");
			}
			/*
			 * if ( NameServer.isBasicType(iv.getType()) ) {
			 *
			 * } pw.print(".asString(ident + " + defaultIdentNumberJavaName +
			 * ")");
			 */
			pw.print("+ \"\\n\";\n");
		}
		pw.print("        return s;\n");
		pw.print("    } \n");

		/**
		 * Define only the Java method 'parent'
		 */
		pw.print("    protected " + MetaHelper.AnyInJava + " parent() {\n");
		String parentRetValue;

		if ( currentPrototypeName.compareTo("Any") == 0 ) {
			parentRetValue = MetaHelper.AnyInJava;
		}
		else {

			/*
			 * prototype is not Any
			 */
			String superName;
			if ( getSuperobjectExpr() == null )
				superName = "Any";
			else
				superName = getSuperobjectExpr().ifPrototypeReturnsItsName(env);
			parentRetValue = MetaHelper.getJavaName(superName);
		}
		pw.print("        return " + parentRetValue + ".prototype;\n");
		pw.print("    }\n");


		/**
		 * Define only the Java method 'parent'
		 */
		pw.print("    protected " + MetaHelper.StringInJava + " prototypePackage() {\n");

		pw.print("        return new " + MetaHelper.StringInJava + "( \"" +
		    this.compilationUnit.getPackageName() + "\" );\n");
		pw.print("    }\n");



		if ( NameServer.isPrototypeFromInterface(currentPrototypeName) ) {
			pw.print("    static final String prototypeName = \"" +
		          NameServer.interfaceNameFromPrototypeName(currentPrototypeName) + "\";\n");
		}
		else {
			pw.print("    static final String prototypeName = \"" + currentPrototypeName + "\";\n");
		}

		/*
		 * add method getWhiteSpaces to prototype Any
		 */
		if ( getName().compareTo("Any") == 0 ) {
			/*
			pw.print("    protected String getWhiteSpaces(int n) { \n");
			pw.print("        String s = \"\";\n");
			pw.print("        for (int i = 0; i < n; ++i) \n");
			pw.print("            s = s + \" \";\n");
			pw.print("        return s;\n");
			pw.print("    }\n");
			pw.print("    ");
			*/
		}
		else
			pw.print("    @Override ");


		pw.print("    public String getPrototypeName() { return prototypeName; }\n");

		// pw.print(" static final boolean isInterfaceVar = " +
		// ((currentPrototype instanceof InterfaceDec) ? "true" : "false") +
		// ";\n");
		pw.print("    protected boolean isInterface() { return "
				+ (getCompilationUnit().getIsPrototypeInterface() ? "true" : "false") + "; }\n\n");

		pw.print("    ");
		if ( this.getSuperobject() != null ) pw.print("@Override");
		pw.print(" public " + NameServer.featureListTypeJavaName + " getFeatureList() { return featureList");
		if ( isInnerProto ) pw.print(innerProtoName);

		pw.println("; }");

		pw.print("    ");
		if ( this.getSuperobject() != null ) pw.print("@Override");
		pw.print(
				" public " + NameServer.slotFeatureListTypeJavaName + " getSlotFeatureList() { return slotFeatureList");
		if ( isInnerProto ) pw.print(innerProtoName);

		pw.println("; }");

		pw.print("    ");
		if ( this.getSuperobject() != null ) pw.print("@Override");
		pw.print(" public " + NameServer.annotListTypeJavaName + " getAnnotList() { return annotList");
		if ( isInnerProto ) pw.print(innerProtoName);
		pw.println("; }");

	}

	protected void genJavaVariables(PWInterface pw, Env env, boolean isInnerProto, String innerProtoName, ObjectDec prototype) {
		pw.print("\n    static final String []fieldList");
		if ( isInnerProto )
			pw.print(innerProtoName);
		pw.print(" = { ");
		int size = getFieldList().size();
		for ( final FieldDec iv : getFieldList() ) {
			pw.print("\"" + iv.getName() + "\"");
			if ( --size > 0 )
				pw.print(", ");
		}
		pw.print(" };\n");

		pw.print("    static final String []fieldTypeList");
		if ( isInnerProto )
			pw.print(innerProtoName);

		pw.print(" = { ");
		size = getFieldList().size();
		if ( this. getFieldList() != null ) {
			for ( final FieldDec iv : getFieldList() ) {
				pw.print("\"" + iv.getType().getFullName() + "\"");
				if ( --size > 0 )
					pw.print(", ");
			}
		}
		pw.print(" };\n");

		pw.print("    public static " + NameServer.featureListTypeJavaName + " featureList");
		if ( isInnerProto )
			pw.print(innerProtoName);
		pw.print(" = new " + NameServer.featureListTypeJavaName +
				"();\n");
		pw.print("    public static " + NameServer.annotListTypeJavaName + " annotList");
		if ( isInnerProto )
			pw.print(innerProtoName);

		pw.print(" = new " + NameServer.annotListTypeJavaName + "();\n");

		pw.print("\n");
		pw.print("    static final " + NameServer.slotFeatureListTypeJavaName
				+ " slotFeatureList");
		if ( isInnerProto )
			pw.print(innerProtoName);

		pw.print(" = new " + NameServer.slotFeatureListTypeJavaName + "();\n");

		pw.printIdent("static CyString []methodNameList");
		if ( isInnerProto )
			pw.print(innerProtoName);
		pw.println(" = { ");
		pw.add();
		int sizeML = prototype.getMethodDecList().size();
		for ( final MethodDec m : prototype.getMethodDecList() ) {
			pw.printIdent("new CyString(\"" + m.getMethodSignature().getFullNameWithReturnType(env)
					+ "\")");
			if ( --sizeML > 0 )
				pw.print(",");
			pw.println();
		}
		pw.printlnIdent("};");
		pw.sub();
		if ( ! isInnerProto && this != Type.Nil ) {
			pw.printIdent("");
			if ( this != Type.Any ) {
				pw.print("@Override ");
			}
			pw.println("protected CyString [] getMethodNameList2() { return methodNameList; }");
			pw.printIdent("");
			if ( this != Type.Any ) {
				pw.print("@Override ");
			}
			pw.println("protected String [] getFieldList2() { return fieldList; }");

		}

		pw.print("\n");

	}


	/**
	 * return the field whose name is "name". null if not found
	 */
	@Override
	public FieldDec searchField(String name) {
		for ( final FieldDec iv :  fieldList )
			if ( iv.getName().compareTo(name) == 0 )
				return iv;
		return null;
	}

	public void setIsAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean getIsAbstract() {
		return isAbstract;
	}

	public void setIsFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	@Override
	public boolean getIsFinal() {
		return isFinal;
	}

	public List<ExprFunction> getFunctionList() {
		return functionList;
	}

	public void addToBeCreatedFunction(ExprFunction function) {
		functionList.add(function);
	}
	/**
	 * returns the field whose name is varName of this prototype and
	 * protected fields of super-prototypes
	 */
	@Override
	public FieldDec searchFieldDec(String varName) {
		for (final FieldDec fieldDec : this.fieldList)
			if ( fieldDec.getName().compareTo(varName) == 0 )
				return fieldDec;
		return null;
	}


	/**
	 * returns the field whose name is varName of this prototype and
	 * protected fields of super-prototypes
	 */
	@Override
	public FieldDec searchFieldPrivateProtectedSuperProtected(String varName) {
		for (final FieldDec fieldDec : this.fieldList)
			if ( fieldDec.getName().compareTo(varName) == 0 )
				return fieldDec;
		if ( this.superobject != null && this.superobject instanceof ObjectDec ) {
			return ((ObjectDec ) superobject).searchFieldDecProtected(varName);
		}
		return null;
	}

	/**
	 * returns the protected field of this prototype whose name is varName.
	 * It includes inherited ivs.
	 */
	@Override
	public FieldDec searchFieldDecProtected(String varName) {
		for (final FieldDec fieldDec : this.fieldList)
			if ( fieldDec.getVisibility() == Token.PROTECTED &&
			     fieldDec.getName().compareTo(varName) == 0 )
				return fieldDec;
		if ( this.superobject != null && this.superobject instanceof ObjectDec ) {
			return ((ObjectDec ) superobject).searchFieldDecProtected(varName);
		}
		return null;
	}


	/**
	 * search methods with name 'methodName'. This method name says all. First in the list are the sub-prototype methods.
	 */

	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(String methodName, Env env) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();

//		String s = "";
		for ( final MethodDec m : methodDecList ) {
//			s += "'" + m.getName() + "'  ";
			if ( m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}


		for ( final MethodDec m : initNewMethodDecList ) {
			if ( (m.getVisibility() == Token.PUBLIC || m.getVisibility() == Token.PACKAGE) && m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}

		ObjectDec proto = this.getSuperobject();
		while ( proto != null ) {
			List<MethodSignature> superMSList = new ArrayList<MethodSignature>();
			superMSList = proto.searchMethodProtectedPublicPackage(methodName);
			if ( superMSList != null )
				methodSignatureList.addAll(superMSList);
			proto = proto.getSuperobject();
		}
		return methodSignatureList;
	}



	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackage(String methodName, Env env) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();

		for ( final MethodDec m : methodDecList ) {
			if ( (m.getVisibility() == Token.PUBLIC || m.getVisibility() == Token.PACKAGE
					|| m.getVisibility() == Token.PROTECTED )
					&& m.getName().equals(methodName) ) {
				methodSignatureList.add(m.getMethodSignature());
			}
		}

		for ( final MethodDec m : initNewMethodDecList ) {
			if ( (m.getVisibility() == Token.PUBLIC || m.getVisibility() == Token.PACKAGE) && m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}

		return methodSignatureList;
	}

	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackage(String methodName, Env env) {

		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();
		for ( final MethodDec m : methodDecList ) {
			if ( m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}
		for ( final MethodDec m : initNewMethodDecList ) {
			Token methodVisibility = m.getVisibility();
			if ( (methodVisibility == Token.PUBLIC ||
					methodVisibility == Token.PACKAGE || methodVisibility == Token.PRIVATE ||
					methodVisibility == Token.PROTECTED )
				 && m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}
		return methodSignatureList;
	}

	/**
	 * search methods with name 'methodName' with visibility 'public' or 'package'.
	 * The method name says it all. First in the list are the sub-prototype methods.
	 */
	@Override
	public List<MethodSignature> searchMethodPublicPackageSuperPublicPackage(
			String methodName, Env env) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();

//		String s = "";
		for ( final MethodDec m : methodDecList ) {
			if ( (m.getVisibility() == Token.PUBLIC || m.getVisibility() == Token.PACKAGE) && m.getName().equals(methodName) ) {
				methodSignatureList.add(m.getMethodSignature());
			}
//			s += "'" + m.getName() + "'  ";
		}

		for ( final MethodDec m : initNewMethodDecList ) {
			if ( (m.getVisibility() == Token.PUBLIC || m.getVisibility() == Token.PACKAGE) && m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}


		final ObjectDec proto = this.getSuperobject();
		if ( proto != null ) {
			List<MethodSignature> superMSList = new ArrayList<MethodSignature>();
			superMSList = proto.searchMethodPublicPackageSuperPublicPackage(methodName, env);
			if ( superMSList != null )
				methodSignatureList.addAll(superMSList);
		}
		return methodSignatureList;
	}


	@Override
	public List<MethodSignature> searchMethodPublicPackage(String methodName, Env env) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();

		for ( final MethodDec m : methodDecList ) {
			if ( (m.getVisibility() == Token.PUBLIC || m.getVisibility() == Token.PACKAGE) && m.getName().equals(methodName) ) {
				methodSignatureList.add(m.getMethodSignature());
			}
		}

		for ( final MethodDec m : initNewMethodDecList ) {
			if ( (m.getVisibility() == Token.PUBLIC || m.getVisibility() == Token.PACKAGE) && m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}
		return methodSignatureList;
	}



	@Override
	public List<MethodSignature> searchMethodProtected(String methodName, Env env) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();

		for ( final MethodDec m : methodDecList ) {
			if ( m.getVisibility() == Token.PROTECTED && m.getName().equals(methodName) ) {
				methodSignatureList.add(m.getMethodSignature());
			}
		}

		for ( final MethodDec m : initNewMethodDecList ) {
			if ( m.getVisibility() == Token.PROTECTED && m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}
		return methodSignatureList;
	}

	/**
	 * search methods with name 'methodName' in super-prototypes and in implemented interfaces
	 */
	public List<MethodSignature> searchMethodPublicPackageSuperPublicPackageProtoAndInterfaces(
			String methodName, Env env) {
		List<MethodSignature> methodSignatureList = this.searchMethodPublicPackageSuperPublicPackage(methodName, env);
		if ( methodSignatureList == null ) {
			methodSignatureList = new ArrayList<MethodSignature>();

		}
		if ( interfaceList != null ) {
			for ( final Expr exprInter : this.interfaceList ) {
				final InterfaceDec inter = (InterfaceDec ) exprInter.getType();
				for ( final MethodSignature ms : inter.getAllMethodSignatureList() ) {
					if ( ms.getName().equals(methodName) ) {
						methodSignatureList.add(ms);
					}
				}
			}
		}
		return methodSignatureList;
	}

	/**
	 * search methods with name 'methodName' in super-prototypes
	 */
	public List<MethodSignature> searchMethodPublicSuperPublicProto(
			String methodName, Env env) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();

//		String s = "";
		for ( final MethodDec m : methodDecList ) {
			if ( m.getVisibility() == Token.PUBLIC && m.getName().equals(methodName) ) {
				methodSignatureList.add(m.getMethodSignature());
			}
//			s += "'" + m.getName() + "'  ";
		}

		for ( final MethodDec m : initNewMethodDecList ) {
			if ( m.getVisibility() == Token.PUBLIC && m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}


		final ObjectDec proto = this.getSuperobject();
		if ( proto != null ) {
			List<MethodSignature> superMSList = new ArrayList<MethodSignature>();
			superMSList = proto.searchMethodPublicSuperPublicProto(methodName, env);
			if ( superMSList != null )
				methodSignatureList.addAll(superMSList);
		}
		return methodSignatureList;
	}

	/**
	 * search methods with name 'methodName' in implemented interfaces
	 */
	public List<MethodSignature> searchMethodPublicSuperPublicImplementedInterfaces(
			String methodName, Env env) {

		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();
		if ( interfaceList != null ) {
			for ( final Expr exprInter : this.interfaceList ) {
				final InterfaceDec inter = (InterfaceDec ) exprInter.getType(env);
				for ( final MethodSignature ms : inter.getAllMethodSignatureList() ) {
					if ( ms.getName().equals(methodName) ) {
						methodSignatureList.add(ms);
					}
				}
			}
		}
		return methodSignatureList;
	}



	/**
	 * searches for a method called methodName in this prototype and all its super-prototypes.
	 * Public and protected methods are considered. The signatures of all methods with name "methodName"
	 * are returned. First in the list are the sub-prototype methods.
	 *
	 * @param methodName
	 * @param env
	 * @return
	 */
	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackageSuperProtectedPublicPackage(String methodName, Env env) {

		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();
		for ( final MethodDec m : getMethodDecList() ) {
			if (  (m.getVisibility() == Token.PUBLIC || m.getVisibility() == Token.PROTECTED || m.getVisibility() == Token.PACKAGE)
					&& m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}

		for ( final MethodDec m : initNewMethodDecList ) {
			if ( (m.getVisibility() == Token.PUBLIC ||
				  m.getVisibility() == Token.PROTECTED || m.getVisibility() == Token.PACKAGE) && m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}


		final ObjectDec superObjectDec = this.getSuperobject();
		if ( superObjectDec != null ) {
			final List<MethodSignature> methodSignatureListSuper =
					superObjectDec.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(methodName, env);
			methodSignatureList.addAll(methodSignatureListSuper);
		}
		return methodSignatureList;
	}

//	/**
//	 * searches for methods with the same signature as methodSig but with different parameter types (at least one
//	 * parameter type should be different). The search is made in this prototype and in super-prototypes
//	 * called methodName in this prototype and all its super-prototypes.
//	 * Public and protected methods are considered. It is returned a list with signatures of all methods with the same name
//	 * as methodSig but with different signature.
//	 *
//	 * @param methodName
//	 * @param env
//	 * @return
//	 */
//
//	public List<MethodSignature> searchMethodDiffNameProtectedPublicSuperProtectedPublic(MethodSignature methodSig, Env env) {
//		final List<MethodSignature> methodSignatureList = this.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(methodSig.getName(), env);
//		final List<MethodSignature> methodSignatureListNonEqual = new ArrayList<>();
//		final String methodFullName = methodSig.getFullName(env);
//		for ( final MethodSignature ms : methodSignatureList ) {
//			if ( ! ms.getFullName(env).equals(methodFullName) ) {
//				methodSignatureListNonEqual.add(ms);
//			}
//		}
//		return methodSignatureListNonEqual;
//	}

	/**
	 * return a list of methods in the implemented interfaces that have name methodName. That includes
	 * super-interfaces of implemented interfaces. First in the list are the sub-prototype methods.
	   @param methodName
	   @param env
	   @return
	 */
	public List<MethodSignature> searchMethodImplementedInterface(String methodName, Env env) {
		final List<MethodSignature> msList = new ArrayList<>();
		for ( final Expr exprInterface : this.interfaceList ) {
			final InterfaceDec interDec = (InterfaceDec ) exprInterface.getType(env);
			final List<MethodSignature> interList = interDec.searchMethodPublicSuperPublicOnlyInterfaces(methodName, env);
			if ( interList != null ) {
				msList.addAll(interList);
			}
		}
		return msList;
	}
	/**
	 * returns the methods of this prototype with name methodName.
	 * The searches includes public and protected methods.
	 * Super-prototypes are not considered.
	 * @param methodName
	 * @return
	 */

	public List<MethodSignature> searchMethodProtectedPublicPackage(String methodName) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();
		for ( final MethodDec m : methodDecList ) {
			if ( m.getName().equals(methodName)  &&
					(m.getVisibility() == Token.PUBLIC ||
					 m.getVisibility() == Token.PROTECTED ||
					m.getVisibility() == Token.PACKAGE ) )
				methodSignatureList.add(m.getMethodSignature());
		}
		return methodSignatureList;
	}

	/**
	 * returns the method of this prototype with name methodName.
	 * The searches includes private, protected, and public methods.
	 * Super-prototypes are not considered.
	 * @param methodName with the keywords and number of parameters as <code>"with:2 do:1"</code>
	 * @return
	 */

	public List<MethodSignature> searchMethodPrivateProtectedPublic(String methodName) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();
		for ( final MethodDec m : methodDecList ) {
//			final String s = m.getName();
			if ( m.getName().equals(methodName) )
				methodSignatureList.add(m.getMethodSignature());
		}
		return methodSignatureList;
	}


	/**
	 * returns all methods of this prototype with interface equal to methodInterface.
	 * An interface is the method signature without the return value type and parameter names.
	 * It is the value returned by method getMethodInterface() of MethodDec.
	 * The searches includes private, protected, and public methods.
	 * Super-prototypes are not considered.
	 * @param methodName
	 * @return
	 */

	public List<MethodDec> search_Method_Private_Protected_Public_By_Interface(Env env, String methodInterface) {
		final List<MethodDec> methodDecArray = new ArrayList<MethodDec>();
		for ( final MethodDec m : methodDecList ) {
			if ( m.getMethodInterface(env).compareTo(methodInterface) == 0 )
				methodDecArray.add(m);
		}
		return methodDecArray;
	}

	/**
	 * returns the method of this prototype with name methodName.
	 * The searches includes public and protected methods.
	 * Super-prototypes are not considered.
	 * @param methodName
	 * @return
	 */

	public MethodDec searchMethodPrivate(String methodName) {
		for ( final MethodDec m : methodDecList ) {
			if ( m.getName().equals(methodName)  &&
					m.getVisibility() == Token.PRIVATE )
				return m;
		}
		return null;
	}

	/**
	 * Search for a init, init:, new, or new: method in this prototype only using
	 * the method name 'name'. That is, something like 'unary' or 'at:1 do:2'.
	   @return
	 */
	public List<MethodSignature> searchInitNewMethod(String name) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();
		for ( final MethodDec m : initNewMethodDecList ) {
			//final String other = m.getNameWithoutParamNumber();
			final String other = m.getName();
			if ( other.compareTo(name) == 0 )
				methodSignatureList.add(m.getMethodSignature());
		}
		return methodSignatureList;
	}

	/**
	 * Search for a init, init:, new, or new: method in this prototype only using
	 * the method name 'name'. That is, something like 'unary' or 'at:1 do:2'.
	   @return
	 */
	public List<MethodSignature> searchInitNewMethodBySelectorList(String name) {
		final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();
		for ( final MethodDec m : initNewMethodDecList ) {
			final String other = m.getNameWithoutParamNumber();
			if ( other.compareTo(name) == 0 )
				methodSignatureList.add(m.getMethodSignature());
		}
		return methodSignatureList;
	}



	public List<FieldDec> getFieldList() {
		return fieldList;
	}

	public List<MethodDec> getMethodDecList() {
		return methodDecList;
	}

	public List<MethodDec> getAllMethodDecList() {
		List<MethodDec> allList = new ArrayList<>();
		allList.addAll(this.methodDecList);
		ObjectDec p = this.getSuperobject();
		while ( p != null ) {
			allList.addAll(p.getMethodDecList());
			p = this.getSuperobject();
		}
		return allList;
	}


	@Override
	public boolean isInnerPrototype() {
		return this.outerObject != null;
	}

	@SuppressWarnings("null")
	@Override
	public void calcInternalTypes(ICompiler_semAn compiler_semAn, Env env) {

		env.atBeginningOfObjectDec(this);




		//# possible error
		if ( this.outerObject == null ) {

			List<Annotation> metaobjectAnnotationList = new ArrayList<>();
			metaobjectAnnotationList.addAll(completeAnnotationList);

			metaobjectAnnotationList.addAll(
					this.getCompilationUnit().getCyanPackage().getAttachedAnnotationList());

			metaobjectAnnotationList.addAll(
					this.getCompilationUnit().getCyanPackage().getProgram().getAttachedAnnotationList());

			makeAnnotationsCommunicateInPrototype(metaobjectAnnotationList, env);


			super.calcInternalTypes(compiler_semAn, env);
		}

		if ( env.isThereWasError() || this.compilationUnit.getErrorList() != null &&
				this.compilationUnit.getErrorList().size() > 0 ) {
			  /*
			   * there was some error signalled by attached metaobjects (probably
			   * implementing interface ICheckPrototype_bsa such as
			   * CyanMetaobjectConcept
			   */
			return ;
		}

		if ( superobject != null ) {
			if ( superobject.getIsFinal() ) {
				env.error(symbol,
						"Prototype or type " + superobject.getFullName() +
								" is not 'open' (it is 'final'). It cannot be inherited",
						true, false);
			}
			if ( !(superobject instanceof ObjectDec) ) {
				env.error(symbol, "Type '" + superobject.getFullName() + "' is not a prototype. It cannot be inherited");
			}
			final ObjectDec superProto = (ObjectDec ) superobject;
			if ( superProto.getOpenPackage() && this.getCompilationUnit().getCyanPackage() != superProto.getCompilationUnit().getCyanPackage() ) {
				env.error(symbol, "The superprototoype, '" + superobject.getFullName() +
						"' is 'open(package)'. It can only be inherited by prototypes of its package, '"
						+ superProto.getCompilationUnit().getPackageName() + "'");
			}

		}

		if ( interfaceList.size() > 0 && env.getProject().getCompilerManager().getCompilationStep().ordinal() >= CompilationStep.step_5.ordinal()
				// prototypes created from interface use the 'default' methods from interfaces.
				// they do not need to implement the interface methods
			 && ! NameServer.isPrototypeFromInterface(this.getName())
				) {
			for ( final Expr interfaceExpr : interfaceList )  {
				final InterfaceDec anInterface = (InterfaceDec ) interfaceExpr.getType(env);
				for ( final MethodSignature ms : anInterface.getMethodSignatureList() ) {
					final List<MethodSignature> other_msList = this.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(ms.getName(), env);
					boolean found = false;
					// ms.calcInterfaceTypes(env);
					final String fullName = ms.getFullName(env);
					for ( final MethodSignature other_ms : other_msList ) {
						if ( other_ms.getFullName(env).equals(fullName) && ms.getReturnType(env).isSupertypeOf(other_ms.getReturnType(env), env)) {
							found = true;
							if (  other_ms.getMethod().getDeclaringObject() == this &&
									! other_ms.getMethod().getHasOverride() ) {
								env.error(other_ms.getFirstSymbol(), "Prototype '" + this.getName() + "' implements interface '"
										+ anInterface.getFullName() + "' and defines method '"
										+ ms.getFullNameWithReturnType(env) + "' of this interface. This method declaration should be preceded by 'override'",
										true, false);
							}
							break;
						}
					}
					if ( ! found ) {
						env.error(this.symbol, "Prototype '" + this.getName() + "' implements interface '"
								+ anInterface.getFullName() + "' but method '"
								+ ms.getFullNameWithReturnType(env) + "' of this interface is not defined in this prototype", true, false);
					}
				}

			}
		}




		final Hashtable<String, MethodDec> methodTable = new Hashtable<String, MethodDec>();
		final Hashtable<String, MethodDec> keywordsOnlyTable = new Hashtable<String, MethodDec>();
		   // only the names of the keywords are put in this list.

		MethodDec lastMethodDec = null;
		final List<MethodDec> allMethodList = new ArrayList<MethodDec>();
		allMethodList.addAll(methodDecList);
		allMethodList.addAll(initNewMethodDecList);


		/**
		 * if the prototype declares context parameters, every field that is not a context parameter should be
		 * initialized in its declaration
		 */
		if ( this.hasContextParameter ) {
			for ( final FieldDec varDec : this.fieldList ) {
				if ( ! varDec.isContextParameter() ) {
					if ( varDec.getExpr() == null ) {
						String s = "";
						for ( final FieldDec cp : this.fieldList ) {
							if ( cp.isContextParameter() )
								s = s + cp.getName() + " ";
						}
						env.error(varDec.getFirstSymbol(),  "field '" + varDec.getName() + "' is not being initialized by the default "
					       + "constructor built from the context parameters " + s, true, false
								);
					}
				}
			}
		}



		for ( final SlotDec s : this.slotList ) {
			MethodDec methodDec;
			if ( s instanceof FieldDec ) {
				//if ( !(s instanceof ContextParameter) )
				//s.calcInternalTypesCTMOCallsPreced(env);
				s.calcInternalTypes(env);
			}
			else if ( s instanceof MethodDec ) {
				methodDec = (MethodDec ) s;

				try {

					final String methodSignatureString = methodDec.getMethodSignature().getFullName(env);
					methodDec.calcInternalTypes(env);


					if ( methodDec.getVisibility() == Token.PRIVATE ) {
						if ( methodDec.getHasOverride() )
							env.error(methodDec.getFirstSymbol(),
									"Private methods cannot be declared with 'override' ",
									true, false);
						if ( methodDec.getIsFinal() && ! this.isFinal )
							env.error(methodDec.getFirstSymbol(),
									"Private methods cannot be declared with 'final' ",
									true, false);
						if ( methodDec.isAbstract() )
							env.error(methodDec.getFirstSymbol(),
									"Private methods cannot be abstract",
									true, false);

					}
					if ( methodDec.getHasOverride() ) {
						if ( superobject == null )
							env.error(methodDec.getFirstSymbol(),
									"'override' cannot be used without a supertype",
									true, false);
						else {
							final String methodName = methodDec.getName();
							List<MethodSignature> superMethodSignatureList =
									((ObjectDec ) superobject).searchMethodPublicPackageSuperPublicPackageProtoAndInterfaces(methodName, env);
							List<MethodSignature> superProtectedMethodSignatureList =
									((ObjectDec ) superobject).searchMethodProtected(methodName, env);
							superMethodSignatureList.addAll(superProtectedMethodSignatureList);
									//PublicPackageSuperPublicPackageProtoAndInterfaces(methodName, env);
							final List<MethodSignature> superInterMethodSignatureList = this.searchMethodPublicSuperPublicImplementedInterfaces(methodName, env);
							if ( methodDec.getVisibility() == Token.PROTECTED &&
									(superMethodSignatureList == null || superMethodSignatureList.size() == 0)) {
								if ( this.superobject != null && this.superobject != Type.Any && this.superobject != Type.Nil ) {
									superMethodSignatureList = this.superobject.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(methodName, env);
								}
							}
							if ( (superMethodSignatureList == null || superMethodSignatureList.size() == 0) &&
									(superInterMethodSignatureList == null || superInterMethodSignatureList.size() == 0) ) {
								env.error(methodDec.getFirstSymbol(),
										"This is no method with this same name in super-prototypes. Therefore it should not be preceded by keyword 'override'",
										true, false);
							}
							else {

								if ( superMethodSignatureList != null && superMethodSignatureList.size() > 0 &&
										methodDec.getVisibility() != superMethodSignatureList.get(0).getMethod().getVisibility() )
									env.error(methodDec.getFirstSymbol(),
											"Method is overridden a method with a different visibility (public, protected, package)",
											true, false);
							}
						}
					}

					if ( methodDec.isAbstract() &&  methodDec.getIsFinal() && ! this.isFinal )
						env.error(methodDec.getFirstSymbol(),
								"Abstract methods cannot be final",
								true, false);


					MethodDec other = methodTable.put(methodSignatureString, methodDec);
					if ( other != null ) {
						if ( other.getCompilerCreatedMethod() )
							env.error(methodDec.getFirstSymbol(), "Internal error at ObjectDec::calcInternalTypes",
									true, false);
						else
							env.error(methodDec.getFirstSymbol(), "Method of line " + other.getFirstSymbol().getLineNumber() +
									" is being duplicated in line " + methodDec.getFirstSymbol().getLineNumber(),
									true, false);
					}
					//String keywordsOnly = methodDec.getMethodSignature().getName();
					final String keywordsOnly = methodDec.getMethodSignature().getName();
					final String keywordsNameOnly = methodDec.getMethodSignature().getNameWithoutParamNumber();
					if ( methodDec.isIndexingMethod() ) {
						if ( ! keywordsNameOnly.equals("at:") && ! keywordsNameOnly.equals("at:put:") )
							env.error(methodDec.getFirstSymbol(),
									"This method cannot be an indexing method. Only 'at:' and 'at:put:' methods can be indexing methods and be preceded by '[]'",
									true, false);
					}

					// end

					if ( keywordsNameOnly.equals("new") || keywordsNameOnly.equals("new:") ) {
						if ( methodDec.getMethodSignature().getReturnType(env) != this ) {
							boolean foundError = true;
							if ( NameServer.isPrototypeFromInterface(getName()) ) {
								final String interfaceName = NameServer.interfaceNameFromPrototypeName(getName());
								final Prototype interfaceProto = this.getCompilationUnit().getCyanPackage().searchPublicNonGenericPrototype(interfaceName);
								if ( methodDec.getMethodSignature().getReturnType(env) == interfaceProto )
									foundError = false;
							}
							if ( foundError )
								env.error(methodDec.getFirstSymbol(), "'new' with return type different from the prototype",
										true, false);
						}
						/*#
						if ( methodDec.getVisibility() != Token.PUBLIC )
							env.error(methodDec.getFirstSymbol(), "'new' methods should be public",
									true, false);
						*/
						if ( methodDec.getHasOverride() )
							env.error(methodDec.getFirstSymbol(), "'new' methods cannot be declared with keyword 'override'",
									true, false);
						if ( methodDec.isAbstract() )
							env.error(
									methodDec.getFirstSymbol(), "'new' methods cannot be declared with keyword 'abstract'",
									true, false);
						if ( methodDec.getIsFinal() && ! this.isFinal )
							env.error(
									methodDec.getFirstSymbol(), "'new' methods cannot be declared with keyword 'final'",
									true, false);


					}
					else if ( keywordsNameOnly.equals("init") || keywordsNameOnly.equals("init:") ) {
						final Type returnTypeInit = methodDec.getMethodSignature().getReturnType(env);
						if (  returnTypeInit != null && returnTypeInit != Type.Nil ) {
							env.error(
									methodDec.getFirstSymbol(), "'init' with return type different from 'Nil'",
									true, false);
						}
						/*#
						if ( methodDec.getVisibility() != Token.PUBLIC )
							env.error(
									methodDec.getFirstSymbol(), "'init' methods should be public",
									true, false);
						*/
						if ( methodDec.getHasOverride() )
							env.error(
									methodDec.getFirstSymbol(), "'init' methods cannot be declared with keyword 'override'",
									true, false);
						if ( methodDec.isAbstract() )
							env.error(
									methodDec.getFirstSymbol(), "'init' methods cannot be declared with keyword 'abstract'",
									true, false);
						if ( methodDec.getIsFinal() && ! this.isFinal && ! this.isFinal )
							env.error(
									methodDec.getFirstSymbol(), "'init' methods cannot be declared with keyword 'final'",
									true, false);
					}


					other = keywordsOnlyTable.put(keywordsOnly, methodDec);
					if ( other != null ) {
						if ( other.getHasOverride() != methodDec.getHasOverride() )
							env.error(methodDec.getFirstSymbol(), "Methods of lines " +  other.getFirstSymbol().getLineNumber() +
											" and " + methodDec.getFirstSymbol().getLineNumber() + " have the same keywords. Both should either " +
											"be declared with keyword 'override' or without it",
									//methodSignatureString,
									true, false
									/*ErrorKind.methods_with_the_same_keywords_with_and_without_override,
									"method0 = \"" + other.getMethodSignatureAsString() + "\"", "method1 = \"" + methodDec.getMethodSignatureAsString() + "\"" */
									);

						if ( other.getIsFinal() != methodDec.getIsFinal() )
							env.error(methodDec.getFirstSymbol(), "Methods of lines " + other.getFirstSymbol().getLineNumber() +
											" and " +  methodDec.getFirstSymbol().getLineNumber() + " have the same keywords. Both should either " +
											"be declared with keyword 'final' or without it",
									/*methodSignatureString,
									ErrorKind.methods_with_the_same_keywords_with_and_without_override,
									"method0 = \"" + other.getMethodSignatureAsString() + "\"", "method1 = \"" + methodDec.getMethodSignatureAsString() + "\""
									*/
									true, false
									);

						if ( other.getVisibility() != methodDec.getVisibility() ) {
							final String methodName = other.getNameWithoutParamNumber();
							if ( !methodName.equals("new:") && !methodName.equals("init:") ) {
								env.error(methodDec.getFirstSymbol(),
										"Methods of lines " + other.getFirstSymbol().getLineNumber() +
												" and " +  methodDec.getFirstSymbol().getLineNumber() + " have the same keywords." +
												" They should be declared with the same visibility (public, package, protected, private)",
												true, false /*
										methodSignatureString,
										ErrorKind.methods_with_the_same_keywords_and_different_visibilities,
										"method0 = \"" + other.getMethodSignatureAsString() + "\"", "method1 = \"" + methodDec.getMethodSignatureAsString() + "\""
										*/ );
							}

						}


						if ( lastMethodDec.getMethodSignature().getName().compareTo(keywordsOnly) != 0 ) {
							boolean foundError = true;
							if ( methodDec.getMethodSignature() instanceof MethodSignatureOperator &&
								 other.getMethodSignature() instanceof MethodSignatureOperator ) {
								final boolean methodDecHasParameter = ((MethodSignatureOperator ) methodDec.getMethodSignature()).getOptionalParameter() == null;
								final boolean otherHashParameter = ((MethodSignatureOperator) other.getMethodSignature()).getOptionalParameter() == null;
								foundError = methodDecHasParameter == otherHashParameter;
							}
							if ( foundError ) {
								if ( other.getCompilerCreatedMethod() )
									env.error(methodDec.getFirstSymbol(), "Internal error at ObjectDec::calcInternalTypes",
											true, false);
								else if ( ! methodDec.getCompilerCreatedMethod() )
									env.error(methodDec.getFirstSymbol(), "Method of line " + methodDec.getFirstSymbol().getLineNumber() +
											" should be declared right after the method of line " + other.getFirstSymbol().getLineNumber(),
										/*
										methodSignatureString,
										ErrorKind.method_should_be_declared_after_previous_method_with_the_same_keywords,
										"method0 = \"" + methodDec.getMethodSignatureAsString() + "\"", "method1 = \"" + other.getMethodSignatureAsString() + "\""
										*/
										true, false);
							}

						}
						final Type methodDecReturnType = methodDec.getMethodSignature().getReturnType(env);
						final Type otherReturnType = other.getMethodSignature().getReturnType(env);
						if ( methodDecReturnType != null && otherReturnType != null ) {

							if ( methodDecReturnType.getFullName().compareTo(otherReturnType.getFullName()) != 0 ) {
								if ( other.getCompilerCreatedMethod() || methodDec.getCompilerCreatedMethod() )
									env.error(true, methodDec.getFirstSymbol(), "Internal error at ObjectDec::calcInternalTypes",
											methodSignatureString, ErrorKind.internal_error);

								else
									env.error(methodDec.getFirstSymbol(), "Method of line " +  methodDec.getFirstSymbol().getLineNumber() +
													" should be declared with the same return type as the method of line " +  other.getFirstSymbol().getLineNumber(),
											/*
											methodSignatureString,
											ErrorKind.methods_with_the_same_keywords_and_different_return_types,
											"method0 = \"" + other.getMethodSignatureAsString() + "\"", "method1 = \"" + methodDec.getMethodSignatureAsString() + "\""
											*/
											true, false
											);

							}

						}
						else {
							if ( methodDecReturnType != otherReturnType )
								env.error(methodDec.getFirstSymbol(), "Method of line " + other.getFirstSymbol().getLineNumber() +
												" should be declared with the same return type as the method of line " +  methodDec.getFirstSymbol().getLineNumber(),
										/*
										methodSignatureString, ErrorKind.methods_with_the_same_keywords_and_different_return_types, other.getMethodSignatureAsString(), methodDec.getMethodSignatureAsString()
										*/
										true, false												);
						}
					}
					lastMethodDec = methodDec;

				}
				catch ( final CompileErrorException e ) {
					return ;
				}
			}
			else {
				env.error(this.getSymbol(), "Internal error at MethodDec::calcInternalTypes: unknown slot class", true, true);
			}
			if ( s.getLastSlot() ) {
				if ( beforeInnerObjectNonAttachedAnnotationList != null ) {
					for ( final AnnotationAt annotation : beforeInnerObjectNonAttachedAnnotationList )
						annotation.calcInternalTypes(env);
				}

				if ( beforeInnerObjectAttachedAnnotationList != null ) {
					for ( final AnnotationAt annotation : beforeInnerObjectAttachedAnnotationList )
						annotation.calcInternalTypes(env);
				}
			}
		}


		if ( this.innerPrototypeList != null ) {
			if ( this.compilationUnit.getErrorList() == null || this.compilationUnit.getErrorList().size() == 0 ) {
				calcInternalTypesInnerPrototypes(compiler_semAn, env);
			}
		}

		if ( beforeEndNonAttachedAnnotationList != null ) {
			for ( final AnnotationAt annotation : beforeEndNonAttachedAnnotationList )
				annotation.calcInternalTypes(env);
		}

		if ( this.outerObject == null ) {
			env.checkIfContextStackEmpty(this.getFirstSymbol());
		}

		//super.calcInternalTypes(compiler_semAn, env);

		final int envLineShift = env.getLineShift();
		env.setLineShift(0);
		checkInitMethods(env);

		if ( this.getHasOperatorMethod() && this != Type.Any ) {
			/**
			 * prototypes that define at least one operator should have only
			 * read-only fields
			 */
			String list = "";
			int countFields = 0;
			for ( final FieldDec iv : this.fieldList ) {
				if ( ! iv.isReadonly() ) {
					if ( countFields > 0 ) list += ", ";
					list += iv.getName();
					++countFields;
				}
			}
			if ( countFields > 0 ) {
				env.error(getFirstSymbol(), "This prototype defines one or more operator methods "
						+ "(like + or *). Hence, all of its fields should be read-only "
						+ "(declared with 'let' or without 'var'). However, the following fields were"
						+ " declared with 'var': \r\n" + list);
			}
		}

		for ( final FieldDec iv : this.fieldList ) {
			final String name = iv.getName();
			if ( name.equals("init") || name.equals("new") ) {
				env.error(iv.getFirstSymbol(), "This field has the name 'init' or 'new'. Both are illegal",
						true, false);
			}
			else {
				for ( final MethodDec method : this.methodDecList ) {
					if ( method.getMethodSignature() instanceof ast.MethodSignatureUnary && method.getName().equals(name) ) {
						env.error(iv.getFirstSymbol(), "field '" + name + "' has the same name "
								+ "as the unary method of line " + method.getMethodSignature().getFirstSymbol().getLineNumber(),
								true, false);
					}
				}
			}
		}


		if ( this.createdInitMethod ) {
			/*
			 * the init method created above will call, in the generated Java code, the super init method. Therefore
			 * the init method of the superprototype should be public
			 */

			final ObjectDec superProto = (ObjectDec ) superobject;
			if ( superProto != null ) {
				final List<MethodSignature> initList = superProto.searchInitNewMethod("init");
				if ( initList != null && initList.size() > 0 ) {
					final Token visibilitySuperInit = initList.get(0).getMethod().getVisibility();
					if ( visibilitySuperInit == Token.PRIVATE ) {
						env.error(getFirstSymbol(), "This prototype does not define an 'init' method. "
								+ "According to the language manual, the compiler should create one 'init' method for this prototype "
								+ "that will call the superprototype 'init' method. "
								+ "However, that cannot be done because the superprototype 'init' method is private", true, false);
					}
					else if ( visibilitySuperInit == Token.PACKAGE ) {
						if ( this.getCompilationUnit().getCyanPackage() != superProto.getCompilationUnit().getCyanPackage() ) {
							env.error(getFirstSymbol(), "This prototype does not define an 'init' method. "
									+ "According to the language manual, the compiler should create one 'init' method for this prototype "
									+ "that will call the superprototype 'init' method. "
									+ "However, that cannot be done because the superprototype 'init' method has visibility 'package'"
									+ " and the package of this prototype, '" + this.getCompilationUnit().getCyanPackage().getName()
									+ "', and the package of the superprototype, '"
									+ superProto.getCompilationUnit().getCyanPackage().getName()
									+ "', are different", true, false);
						}
					}
				}

			}
		}


		/* if ( this.getName().equals("ChooseFoldersCyanInstallation") &&
			this.getCompilationUnit().getProgram().getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_9 )
			MyFile.write(this.getCompilationUnit());  */

		env.setLineShift(envLineShift);
		env.atEndOfObjectDec();

	}

	/**
	 * check whether there are two 'init:' methods with the same number of parameters and a parameter type that is supertype
	 * of the corresponding parameter type of the other 'init:' method.
	 */
	private void checkInitMethods(Env env) {

		final List<MethodDec> initDotList = new ArrayList<>();
		for ( final MethodDec method : initNewMethodDecList ) {
			if ( method.getNameWithoutParamNumber().equals("init:") ) {
				initDotList.add(method);
			}
		}
		final int size = initDotList.size();
		int analyzed = 0;
		int numParam = 1;
		while ( analyzed < size ) {
			final List<MethodDec> initListSameParamNumber = new ArrayList<>();
			for (int i = 0; i < size; ++i) {
				final MethodDec anInit = initDotList.get(i);
				if ( anInit.getMethodSignature() instanceof MethodSignatureWithKeywords) {
					final ast.MethodSignatureWithKeywords ms = (ast.MethodSignatureWithKeywords ) anInit.getMethodSignature();
					if ( ms.getKeywordArray().get(0).getParameterList().size() == numParam ) {
						initListSameParamNumber.add(anInit);
					}
				}
			}
			if ( initListSameParamNumber.size() > 1 ) {
				for (int k = 0; k < initListSameParamNumber.size(); ++k ) {
					final MethodDec firstInit =  initListSameParamNumber.get(k);
					final List<ParameterDec> firstParamList = ((MethodSignatureWithKeywords )
							firstInit.getMethodSignature()).getKeywordArray().get(0).getParameterList();
					for (int j = k + 1; j < initListSameParamNumber.size(); ++j ) {
						final MethodDec secondInit = initListSameParamNumber.get(j);
						final List<ParameterDec> secondParamList = ((MethodSignatureWithKeywords )
								secondInit.getMethodSignature()).getKeywordArray().get(0).getParameterList();
						boolean foundTwoParameterTypesNotRelatedByInheritance = true;
						for (int p = 0; p < numParam; ++p ) {
							Type firstType = firstParamList.get(p).getType(env);
							Type secondType = secondParamList.get(p).getType(env);
							if ( firstType != secondType && !firstType.isSupertypeOf(secondType, env)
									&& ! secondType.isSupertypeOf(firstType, env) ) {
								foundTwoParameterTypesNotRelatedByInheritance = false;

//								if ( firstType.isSupertypeOf(secondType, env) )  {
//									env.error( firstInit.getFirstSymbol(),
//											"The types of the " + NameServer.ordinal(p+1) + " parameter of methods 'init:' of lines " +
//									       firstInit.getFirstSymbol().getLineNumber() + " and " +
//									         secondInit.getFirstSymbol().getLineNumber() + " are incompatible. "
//									         		+ "Type '" + firstType.getFullName() + "' is supertype of "
//															+ "type '" + secondType.getFullName() + "'. That is illegal because it can cause "
//											+ "ambiguity when method 'new:' is called. Use Unions instead of two 'init:' methods. For "
//											+ "example, replace 'func init: Animal p { }' and 'func init: Cow p { }' with "
//											+ "'func init: Cow|Animal p { }'",
//											true, false, false);
//								}
//								else if ( secondType.isSupertypeOf(firstType, env) ) {
//									env.error( firstInit.getFirstSymbol(),
//											"The types of the " + NameServer.ordinal(p+1) + " parameter of methods 'init:' of lines " +
//									       firstInit.getFirstSymbol().getLineNumber() + " and " +
//									         secondInit.getFirstSymbol().getLineNumber() + " are incompatible. "
//									         		+ "Type '" + secondType.getFullName() + "' is supertype of "
//															+ "type '" + firstType.getFullName() + "'. That is illegal because it can cause "
//											+ "ambiguity when method 'new:' is called. Use Unions instead of two 'init:' methods. For "
//											+ "example, replace 'func init: Animal p { }' and 'func init: Cow p { }' with "
//											+ "'func init: Cow|Animal p { }'",
//											true, false, false);
//								}
//								else {
//									foundTwoParameterTypesNotRelatedByInheritance = false;
//								}
							}
						}
						if ( foundTwoParameterTypesNotRelatedByInheritance ) {
							env.error( firstInit.getFirstSymbol(),
									"Methods 'init:' of lines " +
							       firstInit.getFirstSymbol().getLineNumber() + " and " +
							         secondInit.getFirstSymbol().getLineNumber() + " are incompatible. There "
							         + "should be at least one number n such that the n-th parameter of "
							         + "a constructor is not supertype or subtype of the n-the parameter of "
							         + "the other constructor. "
															+ "That is illegal because it can cause "
									+ "ambiguity when method 'new:' is called. "
									+ "Join the methods in a single one and use unions for the parameter types. "
									+ "For example, replace 'func init: Animal p { }' and 'func init: Cow p { }' with "
									+ "'func init: Cow|Animal p { }'",
									true, false, false);
						}
					}
				}

			}
			/*
			 * firstParamList.get(p).getType(env) != null && firstParamList.get(p).getType(env) != null
			 */

			analyzed += initListSameParamNumber.size();
			++numParam;
		}

	}

	/** several types of parameters of 'init:' and 'new:' methods and fields are just 'Any'
	 * because the compiler, during parsing, did not have sufficient information to discover
	 * the types. Now, during 'calcInternalTypes', these types are calculated --- they are the
	 * types that are in the parameters and external variables used by the function that caused
	 * the creation of the inner prototype. This method retrieves these types and initializes
	 * the 'init:', 'new:', and fields (context parameters) with their correct types.
	   @param env
	 */
	private void calcInternalTypesInnerPrototypes(ICompiler_semAn compiler_semAn, Env env) {
		//List<Tuple6<String, VariableDecInterface, Type, Type, String, String>> infoFunList;
		int funIndex = 0;
		/* set the return type of 'eval' or 'eval:' methods  */
		for ( final ObjectDec innerProto : this.innerPrototypeList ) {
			if ( NameServer.isNameInnerProtoForFunction(innerProto.getName()) ) {


				innerProto.setExprFunctionForThisPrototype(functionList.get(funIndex));

				/* if ( count == 0 ) {
					MyFile.write(this.compilationUnit);
				}
				++count;  */

				final ExprFunction exprFunc = functionList.get(funIndex);
				if ( exprFunc.getAccessedVariableParameters() != null ) {
					/*
					 * for each local variable that the function uses there is a context parameter
					 * in the inner prototype representing the function. These context parameters
					 * have type Any (all of them) initially. After calculating the types of
					 * local fields, which is made in ObjectDec::calcInternalTypes,
					 * the compiler should change the types of the context parameters and of the
					 * init: and new: methods.
					 */
					List<MethodSignature> msList;
					MethodSignatureWithKeywords msInit, msNew;
					msList = innerProto.searchInitNewMethodBySelectorList("init:");
					if ( msList.size() != 1 )
						env.error(msList.get(0).getFirstSymbol(), "Internal error in calcInternalTypesInnerPrototypes: just one 'init:' method was expected");
					msInit = (MethodSignatureWithKeywords ) msList.get(0);
					msList = innerProto.searchInitNewMethodBySelectorList("new:");
					if ( msList.size() != 1 )
						env.error(msList.get(0).getFirstSymbol(), "Internal error in calcInternalTypesInnerPrototypes: just one 'new:' method was expected");
					msNew = (MethodSignatureWithKeywords ) msList.get(0);


					for ( final VariableDecInterface v : exprFunc.getAccessedVariableParameters() ) {

						for ( final ContextParameter cp : innerProto.getContextParameterArray() ) {

							if ( cp.getName().equals(v.getName()) ) {
								cp.setType(v.getType());
								cp.setJavaName(v.getJavaName());
								cp.calcInterfaceTypes(env);
								break;
							}

						}
						for ( final ParameterDec p : msInit.getParameterList() ) {
							if ( p.getName().equals(v.getName()) ) {
								p.setType(v.getType());
								p.setJavaName(v.getJavaName());
								break;
							}
						}
						for ( final ParameterDec p : msNew.getParameterList() ) {
							if ( p.getName().equals(v.getName()) ) {
								p.setType(v.getType());
								p.setJavaName(v.getJavaName());
								break;
							}
						}

					}

				}

				final List<MethodSignature> methodSignatureList = new ArrayList<MethodSignature>();
				/*
				 * search for an 'eval' or 'eval:' method in inner objects created from functions.
				 */
				for ( final MethodDec m : innerProto.getMethodDecList() ) {
					if ( NameServer.isMethodNameEval(m.getNameWithoutParamNumber()) )
						methodSignatureList.add(m.getMethodSignature());
				}
				final Type newReturnType = this.functionList.get(funIndex).getReturnType();
				final Expr newReturnTypeExpr = newReturnType.asExpr(
						innerProto.getSuperobjectExpr().getFirstSymbol());

				if ( methodSignatureList.size() != 1 ) {
					env.error(null,  "Internal error at ObjectDec::calcInterfaceTypes", true, true);
				}
				else {
					/*
					 * change the return type of the 'eval' or 'eval:' method.
					 */
					//methodSignatureList.get(0).setReturnType(this.functionList.get(funIndex).getReturnType());
					methodSignatureList.get(0).setReturnType(newReturnType);
					methodSignatureList.get(0).setReturnTypeExpr(newReturnTypeExpr);
				}

				/*
				if ( infoFunList != null ) {
					/*
					 * change the type of new: method

					methodSignatureList = innerProto.searchInitNewMethod("new:");
					for ( MethodSignature ms : methodSignatureList ) {
						for ( ParameterDec param : ms.getParameterList() ) {
							for ( Tuple6<String, VariableDecInterface, Type, Type, String, String> t : infoFunList ) {
								if ( param.getName().equals(t.f1) ) {
									/*
									 * found a context parameter with name equal to a local variable or parameter
									 * that had its type changed just for the literal function whose index is funIndex.
									 * The type of this local variable or parameter has been changed in the literal
									 * function. It should be changed in the object representing the function too.

									param.setType(t.f3);
									param.setJavaName(t.f5);
								}
							}

						}
					}
				}
				*/
				/*
				 * now change the supertype of the inner prototype. It is Function<A1, ... Ak>...<X1, ... Xn, Any>. "Any" is changed
				 * to the real return type, which is the return type of functionList.get(funIndex).getReturnType.
				 */
				final ExprGenericPrototypeInstantiation superProto = (ExprGenericPrototypeInstantiation ) innerProto.getSuperobjectExpr();
				final List<List<Expr>> realListList = superProto.getRealTypeListList();
				final List<Expr> realList = realListList.get(realListList.size()-1);

				realList.set( realList.size()-1, newReturnTypeExpr);
				++funIndex;
				superProto.clearNameWithPackageAndType();
				/*
				 * the superobjectExpr of innerProto is correct. But field superobject, of type Type, is
				 * unchanged. We change it by calling calcInterfaceTypeSuperobject.
				 */
				innerProto.calcInterfaceTypeSuperobject(env);

			}

			if ( innerProto.exprFunctionForThisPrototype != null ) { // && NameServer.isMethodNameEval(methodDec.getNameWithoutParamNumber()) ) {
				/**
				 * this prototype was created for a function. Change the types of fields that should be changed.
				 * The types were changed because a metaobject asked for it.
				 */

				final List<Tuple6<String, VariableDecInterface, Type, Type, String, String>> infoFunList =
						innerProto.exprFunctionForThisPrototype.getVarNameNewCodeOldCodeList();
				if ( infoFunList != null ) {
					for ( final ContextParameter cp : innerProto.contextParameterArray ) {
						for ( final Tuple6<String, VariableDecInterface, Type, Type, String, String> t : infoFunList ) {
							if ( cp.getName().equals(t.f1) ) {
								/*
								 * found a context parameter with name equal to a local variable or parameter
								 * that had its type changed just for the literal function whose index is funIndex.
								 * The type of this local variable or parameter has been changed in the literal
								 * function. It should be changed in the object representing the function too.
								*/
								// t.f2 = cp;
								t.f4 = cp.getType();
								t.f6 = cp.getJavaName();

								cp.setType(t.f3);
								cp.setJavaName(t.f5);
								// cp.setTypeWasChanged(false);
							}
						}
					}
				}

			}

		}
		/*
		 * calculates the interfaces of inner prototypes before calculating their internal types (semantic analysis)
		 */
		for ( final ObjectDec innerProto : this.innerPrototypeList ) {
			innerProto.calcInterfaceTypes(env);
			if ( ! NameServer.isNameInnerProtoForContextFunction(innerProto.getName()) ) {
				innerProto.getContextParameterArray().get(0).setType(this);
				innerProto.getContextParameterArray().get(0).setTypeInDec(this.asExpr(this.getSymbol()));
			}


		}


		/*
		 * the first context parameter of each inner object has name "self__" and has
		 * the type of the outer object.
		 * <code> <br>
		 * object F0(B self__, Int p1) ... end <br>
		 * </code>
		 * In this example, 'B' is the name of the outer object.
		 */
		funIndex = 0;
		for ( final ObjectDec innerProto : this.innerPrototypeList ) {
			innerProto.calcInternalTypes(compiler_semAn, env);

			/*
			if ( NameServer.isNameInnerProtoForFunction(innerProto.getName()) ) {
				infoFunList = functionList.get(funIndex).getVarNameNewCodeOldCodeList();
				if ( infoFunList != null ) {
					for ( ContextParameter cp : innerProto.getContextParameterArray() ) {
						for ( Tuple6<String, VariableDecInterface, Type, Type, String, String> t : infoFunList ) {
							if ( cp.getName().equals(t.f1) ) {
								/*
								 * found a context parameter with name equal to a local variable or parameter
								 * that had its type changed just for the literal function whose index is funIndex.
								 * The type of this local variable or parameter has been changed in the literal
								 * function. The code below restores the previous type.
								 * /
								cp.setType(t.f4);
								cp.setJavaName(t.f6);
							}
						}
					}
				}
				++funIndex;

			}
			*/
		}

		/**
		 * change again the type of context parameters of inner prototypes created for functions. This is necessary
		 * for code generation
		 */
		for ( final ObjectDec innerProto : this.innerPrototypeList ) {
			if ( innerProto.exprFunctionForThisPrototype != null ) {
				/**
				 * this prototype was created for a function. Change the types of fields to their original type
				 * The types were changed because a metaobject asked for it.
				 */

				final List<Tuple6<String, VariableDecInterface, Type, Type, String, String>> infoFunList =
						innerProto.exprFunctionForThisPrototype.getVarNameNewCodeOldCodeList();
				if ( infoFunList != null ) {
					for ( final ContextParameter cp : innerProto.contextParameterArray ) {
						for ( final Tuple6<String, VariableDecInterface, Type, Type, String, String> t : infoFunList ) {
							if ( cp.getName().equals(t.f1) ) {
								/*
								 * found a context parameter with name equal to a local variable or parameter
								 * that had its type changed just for the literal function whose index is funIndex.
								 * The type of this local variable or parameter has been changed in the literal
								 * function. It should be changed in the object representing the function too.
								*/
								cp.setType(t.f4);
								// cp.setJavaName(t.f6);

							}
						}
					}
				}

			}

		}
	}

	private static int count = 0;

	public void calcInterfaceSuperTypes(Env env) {
		if ( this.superobject == null ) {
			if ( this.superobjectExpr != null ) {
				if ( this.superobjectExpr.type == null )  {
					this.superobjectExpr.calcInternalTypes(env);
  			    }
				this.superobject = this.superobjectExpr.type;
			}
		}
	}


	/**
	 * see comment on ChooseFoldersCyanInstallation::calculatesTypes(Env env)
	 */
	@Override
	public void calcInterfaceTypes(Env env) {

		env.atBeginningOfObjectDec(this);

		super.calcInternalTypesNONAttachedAnnotations(env);

		//if ( superobjectExpr != null ) superobjectExpr.calcInternalTypes(env);
		for ( final List<GenericParameter> genericParameterList : genericParameterListList )
			for ( final GenericParameter genericParameter : genericParameterList )
				genericParameter.calcInternalTypes(env);


		calcInterfaceTypeSuperobject(env);
		ObjectDec superProto = this.getSuperobject();
		if ( superProto != null && superProto.getHasPackageMethods() ) {
			if ( ! superProto.getPackageName().equals(this.getPackageName()) ) {
				env.error(this.getFirstSymbol(), "Prototype '" +
			      superProto.getFullName() + "' declare at least one method with 'package' visibility"
			      		+ " and is being inherited by '" + this.getFullName() + "' that is in a "
			      				+ "different package. This is illegal");
			}
		}
		for ( final Expr anInterface : interfaceList ) {
			anInterface.calcInternalTypes(env);
			if ( anInterface.getType() instanceof TypeWithAnnotations ) {
				env.error(anInterface.getFirstSymbol(), "Implemented interface '" + anInterface.asString() +
						"' has an attached annotation."
						+ " This is illegal");
			}
			Type interType = anInterface.getType(env);
			if ( !(interType instanceof InterfaceDec) ) {
				env.error(anInterface.getFirstSymbol(), "After 'implements', interface names are expected. '"
						+ interType.getFullName() + "' is not an interface");
			}
		}


		if ( this.superobject instanceof TypeWithAnnotations ) {
			env.error(this.superobjectExpr.getFirstSymbol(), "The superprototype has an attached annotation."
					+ " This is illegal");
		}

		/**
		 * the context parameters are in fact fields so
		 * we don´t have to call calcInternalTypes over them.
		 */

		for ( final SlotDec s : this.slotList ) {
			s.calcInterfaceTypes(env);
			if ( s instanceof MethodDec ) {
				MethodDec md = (MethodDec ) s;
				if ( md.getVisibility() == Token.PACKAGE ) {
					this.hasPackageMethods = true;
				}
			}
		}


		super.calcInterfaceTypes(env);

		env.atEndOfObjectDec();

	}

	/**
	   @param env
	 */
	private void calcInterfaceTypeSuperobject(Env env) {
		if ( superobjectExpr == null ) {
			final String prototypeName = symbol.getSymbolString();
			if ( ! prototypeName.equals("Any") && ! prototypeName.equals("Nil") ) {
				superobject = Type.Any;
				superObjectName = "Any";
			}
		}
		else {
			try {
				env.setAllowCreationOfPrototypesInLastCompilerPhases(true);
				superobjectExpr.calcInternalTypes(env);
			}
			finally {
				env.setAllowCreationOfPrototypesInLastCompilerPhases(false);
			}
			//superObjectName = superobjectExpr.ifPrototypeReturnsItsName();
			superobject = superobjectExpr.getType();
			if ( !(superobject instanceof ObjectDec) ) {
				env.error(superobjectExpr.getFirstSymbol(), "Superprototype should be a Cyan prototype");
			}
			superObjectName = superobject.getFullName();

			if ( superobject instanceof InterfaceDec ) {
				env.error( true, superobjectExpr.getFirstSymbol(),
						"Prototype cannot inherit from an interface", superObjectName, ErrorKind.prototype_cannot_inherit_from_an_interface);
			}
			/*
			if ( superobject instanceof ObjectDec ) {
				if ( this.isAbstract && ! ((ObjectDec) superobject).isAbstract ) {

				}

			}
			*/

		}
	}



	@Override
	public boolean isSupertypeOf(Type otherType, Env env) {
		otherType = otherType.getInsideType();
		if ( otherType instanceof TypeDynamic )
			return true;
		if ( this == otherType )
			return true;


		if ( symbol.getSymbolString().equals("Union") ) {
			/*
			 * Union<A, B> is considered, in practice, a supertype of both A and B
			 */
			if ( genericParameterListList.size() > 0  ) {
				for ( final GenericParameter gp : genericParameterListList.get(0) ) {
					if ( gp.getKind() != GenericParameterKind.LowerCaseSymbol && gp.getKind() != GenericParameterKind.FormalParameter ) {
						if ( gp.getParameter().getType(env).isSupertypeOf(otherType, env) )
							return true;
					}
				}
				return false;
			}
		}
		if ( otherType == Type.Nil || this == Type.Nil ) {
			   // Nil is the only subtype or supertype of Nil
			return this == otherType;
		}

		// String thisName = this.getName();
		// String otherTypeName = otherType.getName();

		if ( otherType instanceof InterfaceDec ) {
			return this == Type.Any;
		}
		else if ( otherType instanceof ObjectDec ) {
			ObjectDec otherProto = (ObjectDec ) otherType;
			while ( otherProto != null && this != otherProto )
				otherProto = otherProto.getSuperobject();
			return otherProto != null;
		}
		else {
			if ( otherType instanceof TypeJavaClass ) {

				final String cyanTypeName = getName();
				final String javaTypeName = otherType.getName();
				String cyanNameFromWrapper = null;

				return javaTypeName.equals(Character.toLowerCase(cyanTypeName.charAt(0)) + cyanTypeName.substring(1)) ||
						javaTypeName.equals("String") && javaTypeName.equals("String") ||
						//NameServer.javaWrapperClassToCyanName(javaTypeName).equals(cyanTypeName);
						(cyanNameFromWrapper = NameServer.javaWrapperClassToCyanName(javaTypeName)) != null &&
						cyanNameFromWrapper.equals(cyanTypeName);

			}
			else if ( otherType instanceof TypeUnion ) {
				TypeUnion tu = (TypeUnion ) otherType;
				for ( Type t : tu.getTypeList() ) {
					if ( ! this.isSupertypeOf(t, env) ) {
						return false;
					}
				}
				return true;
			}
			else if ( otherType instanceof TypeIntersection ) {
				TypeIntersection ti = (TypeIntersection ) otherType;
				for ( Type t : ti.getTypeList() ) {
					if ( this.isSupertypeOf(t, env) ) {
						return true;
					}
				}
				return false;

			}
			env.error(null,  "Internal error in InterfaceDec::isSupertypeOf: unknown type", true, true);
		}
		return false;
	}



	public ExprFunction getExprFunctionForThisPrototype() {
		return exprFunctionForThisPrototype;
	}

	public void setExprFunctionForThisPrototype(ExprFunction exprFunctionForThisPrototype) {
		this.exprFunctionForThisPrototype = exprFunctionForThisPrototype;
	}





	public void addOverloadMethodList(List<MethodDec> multiMethodList) {
		if ( this.multiMethodListList == null ) {
			this.multiMethodListList = new ArrayList<>();
		}
		this.multiMethodListList.add(multiMethodList);
	}



	public List<List<MethodDec>> getMultiMethodListList() {
		return multiMethodListList;
	}

	public boolean getHasContextParameter() {
		return this.hasContextParameter;
	}

	public void setHasContextParameter(boolean hasContextParameter) {
		this.hasContextParameter = hasContextParameter;
	}

	public List<ContextParameter> getSuperContextParameterList() {
		return superContextParameterList;
	}

	public void setSuperContextParameterList(List<ContextParameter> superContextParameterList) {
		this.superContextParameterList = superContextParameterList;
	}


	public List<MethodDec> getAbstractMethodList() {
		return abstractMethodList;
	}

	public void addAbstractMethod(MethodDec abstractMethod) {
		this.abstractMethodList.add(abstractMethod);
	}

	/**
	 * add an interface to the list of interfaces that the Java class for this prototype should implement
	   @param param
	 */

	public boolean addJavaInterface(String param) {
		if ( javaInterfaceList == null ) {
			javaInterfaceList = new ArrayList<>();
		}
		for ( final String iname : ObjectDec.interfacesImplementedByAny ) {
			if ( iname.equals(param) )
				return false;
		}
		for ( final String iname : javaInterfaceList ) {
			if ( iname.equals(param) )
				return false;
		}

		javaInterfaceList.add(param);
		return true;
	}

	public List<AnnotationAt> getAnnotationThisAndSuperCTDNUList() {
		if ( metaobjectAnnotationListThisAndSuperCTDNU == null ) {
			metaobjectAnnotationListThisAndSuperCTDNU = new ArrayList<>();
			List<AnnotationAt> metaobjectAnnotationList = this.getAttachedAnnotationList();
			if ( metaobjectAnnotationList != null ) {
				for ( final AnnotationAt annotation : metaobjectAnnotationList ) {

					final CyanMetaobjectAtAnnot cyanMetaobject = annotation.getCyanMetaobject();

					_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot ) cyanMetaobject.getMetaobjectInCyan();

					boolean actionMissing = cyanMetaobject instanceof IActionMethodMissing_semAn
							||
					        (other != null && other instanceof _IActionMethodMissing__semAn)
					        ;
					if ( actionMissing ) {
						metaobjectAnnotationListThisAndSuperCTDNU.add(annotation);
					}
				}
			}
			for ( final MethodDec aMethod : this.methodDecList ) {
				metaobjectAnnotationList = aMethod.getAttachedAnnotationList();
				if ( metaobjectAnnotationList != null ) {
					for ( final AnnotationAt annotation : metaobjectAnnotationList ) {
						final CyanMetaobjectAtAnnot cyanMetaobject = annotation.getCyanMetaobject();

						_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot ) cyanMetaobject.getMetaobjectInCyan();

						boolean actionMissing = cyanMetaobject instanceof IActionMethodMissing_semAn
								||
						        (other != null && other instanceof _IActionMethodMissing__semAn)
						        ;
						if ( actionMissing ) {
							metaobjectAnnotationListThisAndSuperCTDNU.add(annotation);
						}
					}
				}
			}

			if ( this.superobject != null && superobject instanceof ObjectDec ) {
				final ObjectDec superObj = (ObjectDec ) superobject;
				metaobjectAnnotationListThisAndSuperCTDNU.addAll(superObj.getAnnotationThisAndSuperCTDNUList());
			}
		}
		return metaobjectAnnotationListThisAndSuperCTDNU;
	}


//	/**
//	 * returns the first method of this prototype that has a feature called 'name' (in the first field of the tuple). Returns the
//	 * value associated with this feature in the second element of the tuple
//	   @param name
//	   @return
//	 */
//	public Tuple2<MethodDec, WrExprAnyLiteral> searchMethodByFeature(String name) {
//		for ( final MethodDec method : this.methodDecList ) {
//			if ( method.getFeatureList() != null ) {
//				for ( final Tuple2<String, WrExprAnyLiteral> t : method.getFeatureList()) {
//					if ( t.f1.equals(name) ) {
//						return new Tuple2<MethodDec, WrExprAnyLiteral>(method, t.f2);
//					}
//				}
//			}
//		}
//		return null;
//	}
//


	public List<MethodDec> getInitNewMethodDecList() {
		return initNewMethodDecList;
	}

	public List<String> getJavaInterfaceList() {
		return javaInterfaceList;
	}

	public boolean getOpenPackage() {
		return openPackage;
	}

	public void setOpenPackage(boolean openPackage) {
		this.openPackage = openPackage;
	}

//	/**
//	 * list of all interfaces implemented by this prototype if this prototype is not an interface. Or the
//	 * list of super interfaces if this prototype is an interface
//	 */
//	private List<InterfaceDec> completeSuperInterfaceList;

	/*
	public List<InterfaceDec> getCompleteSuperInterfaceList() {
		List<InterfaceDec> list = new ArrayList<>();
		HashMap<InterfaceDec> interStack = new Stack<>();
		for ( Expr interExpr : this.getInterfaceList() ) {
			InterfaceDec inter = (InterfaceDec ) interExpr.getType();
			list.add(inter);
		}
		return list;
	}
	*/

	public void checkIfImmutable(Env env) {
		if ( this.getImmutable() ) {
			ObjectDec proto = this;

			while ( proto != Type.Any ) {
				proto = proto.getSuperobject();
				if ( !proto.getImmutable() ) {
					env.error(this.getFirstSymbol(), "Immutable prototype '" + this.getName() + "' inherits from mutable prototype '"
						+ proto.getFullName() + "'");
				}
			}
			for ( final FieldDec iv : getFieldList() ) {
				if ( !iv.isReadonly() ) {
					env.error(iv.getFirstSymbol(), "This prototype is declared with annotation '"
							+ this.getName() + "'. Therefore all fields should be read only (declared with 'let')");
				}
				else {
					final String ivTypeName = iv.getType().getName();
					if ( ! MetaHelper.isBasicType(ivTypeName) ) {
						final Type ivType = iv.getType();
						if (  !(ivType instanceof Prototype) || ! ((Prototype) ivType).getImmutable() ) {
							env.error(iv.getFirstSymbol(), "This prototype is declared with annotation '"
									+ this.getName() +
									"'. Therefore all fields should have types that are basic types or immutable types"
									+ " field '" + iv.getName() + "' has a type '" + iv.getType().getFullName() +
									"' that is not immutable. To make a prototype immutable, attach to it the annotation @immutable");
						}
					}
				}
			}

		}
	}

	public List<SlotDec> getSlotList() {
		return slotList;
	}

	public boolean getCreatedInitMethod() {
		return createdInitMethod;
	}

	public boolean getHasPackageMethods() {
		return hasPackageMethods;
	}

	/**
	 * sha256 code, as string, of the source code in which this prototype is
	   @return
	 */
	private String sha256Code = null;

	public String getSHA256Code() {
		if ( sha256Code == null ) {
			sha256Code = cyanruntime.CyanRuntime.sha256(this.compilationUnit.getText());
		}
		return this.sha256Code;
	}

	public boolean allFieldsInitializedInDeclaration() {
		for ( FieldDec f : this.fieldList ) {
			if ( f.getExpr() == null ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * list of metaobject annotations attached to this prototype and
	 * super-prototypes that implement interface {@link meta#IActionMethodMissing_semAn}
	 */
	private List<AnnotationAt> metaobjectAnnotationListThisAndSuperCTDNU;

	private List<String> javaInterfaceList;


	/**
	 * true if this object is abstract
	 */
	private boolean isAbstract;
	/**
	 * true if this object is final
	 */
	private boolean isFinal;

	/**
	 * true if this object is declared as open(package)
	 */
	private boolean openPackage;

	/**
	 * the superobjectExpr of this object. Similar to superclass.
	 * Types are represented as expressions.
	 */
	private Expr superobjectExpr;

	/**
	 * the superobject of this object.
	 */
	private Type superobject;

	/**
	 * list of interfaces implemented by this object
	 */
	private List<Expr> interfaceList;

	/**
	 * list of context parameters of this object
	 */
	private List<ContextParameter> contextParameterArray;

	/**
	 * list of all fields of this object
	 */
	private List<FieldDec> fieldList;



	/**
	 * list of all method declarations of this object except those with
	 * names init, init:, new, and new:
	 */
	private List<MethodDec> methodDecList;

	/**
	 * list of all methods with names init, init:, new, and new:
	 */
	private List<MethodDec> initNewMethodDecList;

	/**
	 * list of functions declared in this object.
	 */
	private List<ExprFunction> functionList;

	/**
	 * name of the super prototype
	 */
	private String superObjectName;


	/**
	 * slot list of this object. A slot is a field or method.
	 * This list is important because it preserves the order of the slot declaration
	 */
	protected List<SlotDec> slotList;


	/**
	 * If this is an inner prototype created for a literal function, this variable
	 * refers to the literal function. Otherwise it is null
	 */
	private ExprFunction exprFunctionForThisPrototype;


	/**
	 * list of lists. The inner list contains all multi-methods with the same name in
	 * the prototype. In Cyan syntax and using method signatures, it could be <br>
	 * <code>
	 *    [  [ "at: Int", "at: String" ],  [ "print: Int", "print: Shape" ] ]
	 * </code>
	 */
	private List<List<MethodDec>> multiMethodListList;

	/**
	 * true if this prototype declares at least one context parameter. If true
	 * all fields that are not context parameters should be initialized in their declarations
	 */
	private boolean hasContextParameter;
	/**
	 * the context parameter list of the super-prototype. It would contain 'aa' and 'bb' in<br>
	 * {@code <br>
	 * object B(Int aa, Int bb, Int cc) extends A(aa, bb) <br>
	 *   ...<br>
	 * end <br>
	 * }
	 */
	private List<ContextParameter> superContextParameterList;

	/**
	 * list of methods declared as 'abstract'
	 */
	private List<MethodDec> abstractMethodList;


	public void setCreatedInitMethod(boolean createdInitMethod) {
		this.createdInitMethod = createdInitMethod;
	}

	static final public String []interfacesImplementedByAny = { "Cloneable", "java.io.Serializable" };

	/**
	 * true if the compiler created an 'init' method for this prototype
	 */
	private boolean createdInitMethod;

	/**
	 * true if the prototype as any methods with 'package' visibility. If it has,
	 * then the prototype can only be inherited by prototypes of the same package.
	 */
	private boolean hasPackageMethods;


}

