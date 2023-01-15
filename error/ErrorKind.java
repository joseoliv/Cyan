package error;

/**
 * Very important: the words "inside" and "outside" used below refer to the place in which
 * the statement/command that caused the error is. So, identifier_expected_inside_method
 * refers to the error "identifier expected" and this error is inside a method such as
 *         fun test [ y = 0 ]  // y is not a field
 *
 * identifier_expected_outside_method refers to the error "identifier expected" and this
 * error is outside a method such as
 *
 *       object Test extends
 *          fun test [ ]
 *       end
 *
 *  There is not identifier after "extends" as expected. The parameters to Siel are
 *  different in both cases.
 *
   @author José
 */
public enum ErrorKind {

	illegal_file_name( "Illegal file name", new String[] { } ),

	unreachable_code( "Unreachabel code", new String [] {
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),


	variable_was_not_declared( "Variable was not declared", new String [] { "identifier",
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),
	parameter_is_being_redeclared( "Parameter is being redeclared", new String [] { "identifier",
					"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList", "methodList" } ),

	local_variable_is_being_redeclared( "Variable is being redeclared", new String [] { "identifier",
							"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
							"visibleLocalVariableList", "fieldList", "methodList" } ),

	local_variable_has_same_name_method_context_object( "Variable has the same name as an unary method of the enclosing context object",
			new String [] { "identifier",
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),
	identifier_was_not_declared( "Identifier was not declared", new String [] { "identifier",
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),

    identifier_was_not_declared_inside_method( "Identifier was not declared", new String [] { "identifier",
					"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList", "methodList" } ),

	identifier_was_not_declared_outside_method( "Identifier expected", new String [] { "identifier",
							"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodList" } ),

	identifier_expected_inside_method( "Identifier expected", new String [] { "identifier",
					"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList", "methodList" } ),
	identifier_expected_outside_method( "Identifier expected", new String [] { "identifier",
							"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodList" } ),


	identifier_expected_outside_prototype( "Identifier expected", new String [] { "identifier", "prototypeName", "supertype", "implementedInterfaces",
									 } ),

	prototype_as_type_expected_inside_method( "It is expected that the type of this expression be a prototype",
			new String [] { "identifier",
					"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList" }),

	tuple_field_name_expected( "It is expected that the type of this expression be a prototype",
			new String [] { "identifier",
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList" }),
	formal_parameter_starting_with_lower_case_letter("Formal parameter of a generic prototype starting with a lower-case letter",
			new String [] { "identifier", "prototypeName", "supertype", "implementedInterfaces",
			 }),

		/* it is necessary to change the String array. There are several possibilities:
		 * - the error is inside a method;
		 * - the error is in the interface of a method;
		 * - the error is outside a method
		 *
		 */
	real_parameter_of_generic_prototype_expected("Real parameter of a generic prototype expected",
			new String [] { "identifier" } ),

    generic_parameter_expected("Formal parameter of a generic prototype was expected",
						new String [] { "identifier", "prototypeName", "supertype", "implementedInterfaces",
						 }),
	mixing_of_different_parameter_kinds_in_generic_prototype("Parameters of different kinds (real parameters, formal parameters, formal parameters with +) are mixed in the declaration of generic prototype",
									new String [] { "identifier", "prototypeName", "supertype", "implementedInterfaces",
									 }),
	more_than_one_formal_plus_parameter_in_generic_prototype("More than one formal parameter followed by + in a generic prototype",
												new String [] { "identifier", "prototypeName", "supertype", "implementedInterfaces",
												 }),

	method_is_not_visible_here( "Method is not visible here", new String [] { "identifier",
									"prototypeName", "supertype", "implementedInterfaces",
									"fieldList", "methodList" } ),

	instance_variable_is_not_visible_here( "field is not visible here", new String [] { "identifier",
	    "prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodList", "statementText" } ),

	readonly_variable_expected_outside_method( "Identifier expected", new String [] { "identifier",
									"prototypeName", "supertype", "implementedInterfaces",
									"fieldList", "methodList" } ),

	identifier_cannot_be_used_in_the_left_hand_side_of_an_assignment(
			"Identifier cannot be used in the left-hand side of an assignment", new String [] { "identifier",
					"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList", "methodList" } ),
	unary_method_cannot_be_used_in_the_left_hand_side_of_an_assignment(
		"Unary method cannot be used in the left-hand side of an assignment", new String [] { "identifier",
									"statementText", "methodSignature", "prototypeName",
									"supertype", "implementedInterfaces",
									"visibleLocalVariableList", "fieldList", "methodList" } ),
		/**
		 * prototype was not found. The prototype is in a statement of a method
		 */
    prototype_was_not_found_inside_method("Prototype was not found", new String [] { "identifier",
		"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
		"visibleLocalVariableList", "fieldList", "methodList" } ),
		// prototype was not found. The prototype appears inside a prototype but ouside a method, such as
		// field type. Or in a clause 'extends' or 'mixin'
	prototype_was_not_found_inside_prototyped("Prototype was not found", new String [] { "identifier",
		"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodList" } ),
	prototype_was_not_found_outside_prototype("Prototype was not found", new String [] { "identifier",
				 } ),

    prototype_cannot_be_used_in_the_left_hand_side_of_an_assignment(
		"Unary method cannot be used in the left-hand side of an assignment", new String [] { "identifier",
		"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
		"visibleLocalVariableList", "fieldList", "methodList" } ),
	package_was_not_found_inside_method("Package was not found", new String [] { "identifier",
		"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
		"visibleLocalVariableList", "fieldList", "methodList" } ),
	package_was_not_found_outside_prototype("Package was not found", new String [] { "identifier",
				 } ),
	package_has_a_wrong_name("Package name is different from expected", new String[] {"identifier" } ),
	package_is_importing_itself("This source file is importing its own package", new String[] { "identifier",
			"packageName" } ),

	keyword_package_expected("Keyword 'package' was expected", new String[] { } ),
	package_name_expected("A package name was expected", new String[] { "identifier" } ),
	package_name_not_start_with_lower_case_letter("A package name should start with a lower-case ASCII letter", new String[] { "identifier" } ),

	indexing_method_was_not_found_inside_method("Indexing method 'at:' or 'at:put:' was not found", new String [] {
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList",
			"fieldList", "methodList"
	}),
	indexing_method_was_not_found_outside_method("Indexing method 'at:' or 'at:put:' was not found", new String [] {
			"prototypeName",  "supertype", "implementedInterfaces", "fieldList", "methodList"
	}),
	expression_cannot_be_indexed_by_this_index_inside_method("Expression cannot be indexed by an expression of this type",
			new String [] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList",
			"fieldList", "methodList" } ),
	expression_cannot_be_indexed_by_this_index_outside_method("Expression cannot be indexed by an expression of this type",
					new String [] {  "prototypeName", "supertype", "implementedInterfaces", "visibleLocalVariableList",
					"fieldList", "methodList" } ),

	expression_expected_inside_method("An expression is expected",
							new String [] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList",
			"fieldList", "methodList" } ),

  	empty_literal_array("Illegal empty literal array",
					new String [] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
	                    "visibleLocalVariableList", "fieldList", "methodList" } ),

    empty_literal_tuple("Illegal empty literal tuple",
	        					new String [] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
	        	                    "visibleLocalVariableList", "fieldList", "methodList" } ),
    colon_expected("':' expected",
			new String [] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
            "visibleLocalVariableList", "fieldList", "methodList" } ),

    semicolon_expected("';' expected",
        			new String [] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
                    "visibleLocalVariableList", "fieldList", "methodList" } ),

    dot_square_backet_expected("'.]' expected",
        			new String [] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
                    "visibleLocalVariableList", "fieldList", "methodList" } ),

	duplicate_method("duplicated method", new String [] { "previousMethodSignature",
			"currentMethodSignature", "prototypeName", "supertype", "implementedInterfaces" }),
	prototype_imported_from_two_or_more_packages_outside_method("Prototype is imported from two or more packages",
			new String[] { "prototypeName", "packageName", "importList"}),
	prototype_imported_from_two_or_more_packages_inside_method("Prototype is imported from two or more packages",
			new String [] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList",
			"fieldList", "methodList" } ),

	prototype_cannot_inherit_from_an_interface("Prototype cannot inherit from an interface",
			new String[] { "prototypeName", "supertype", "implementedInterfaces", "interfaceName"}),

	init_new_methods_cannot_be_declared_in_interfaces("Interfaces cannot declare 'init', 'init:', 'new', or 'new:' methods",
			new String[] { "interfaceName", "methodSignature", }),

	qualifier_cannot_preced_method_signature_in_interfaces("'public', 'protected', and 'private' cannot appear in a method signature of an interface",
					new String[] { "interfaceName", "methodSignature", "qualifier" }),

	override_cannot_preced_method_signature_in_interfaces("Keyword 'override' cannot appear in a method signature of an interface",
							new String[] { "interfaceName", "methodSignature", }),
	abstract_cannot_preced_method_signature_in_interfaces("Keyword 'abstract' cannot appear in a method signature of an interface",
									new String[] { "interfaceName", "methodSignature", }),

    abstract_methods_cannot_be_declared_in_interfaces("Interfaces cannot declare abstract methods",
			new String[] { "interfaceName", "methodSignature" }),
    interface_name_expected("interface name expected", new String[] { "packageName" }),

	two_or_more_init_methods("Two or more 'init' methods. There should be only one",
			new String [] { "identifier",
			"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodSignature", "methodList" } ),

	both_init_and_new_methods("Both an 'init' and a 'new' methods. There should be only one of them",
			new String [] { "identifier",
			"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodSignature", "methodList" } ),
	init_should_return_Nil("Method 'init' should return 'Nil' or nothing at all",
					new String [] { "identifier",
					"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodSignature", "methodList", "returnType" } ),
	init_should_not_be_declared_with_override("Method 'init' should not be preceded by 'override'",
							new String [] { "identifier",
							"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodSignature", "methodList" } ),

    init_should_not_be_final("Method 'init' cannot be final",
									new String [] { "identifier",
									"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodSignature", "methodList" } ),


	init_new_should_be_public("Method 'init' should not be preceded by 'override'",
									new String [] { "identifier",
									"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodSignature", "methodList" } ),
	init_should_not_be_abstract("Method 'init' should not be preceded by 'override'",
			new String [] { "identifier", "prototypeName", "supertype", "implementedInterfaces", "methodSignature", "fieldList", "methodList" } ),
	init_new_should_not_appear_in_grammar_method("'init:' and 'new:' keywords cannot appear in a grammar method",
			new String [] { "identifier",
			"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodSignature", "methodList" } ),
	init_new_cannot_be_preceded_by_indexing_operator("'init:' and 'new:' keywords cannot be used with indexing operator '[]'",
					new String [] { "identifier",
					"prototypeName", "supertype", "implementedInterfaces",
					"fieldList", "methodSignature", "methodList" } ),


    new_with_illegal_return_type("'new' methods should have the prototype as the return type",
					new String [] { "identifier",
					"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodSignature", "methodList" } ),
	new_cannot_be_declared_with_override("'new' methods cannot be declared with keyword 'override'",
							new String [] { "identifier",
							"prototypeName", "supertype", "implementedInterfaces",
							"fieldList", "methodSignature", "methodList" } ),
	new_cannot_be_abstract("'new' methods cannot be declared with keyword 'abstract'",
									new String [] { "identifier",
									"prototypeName", "supertype", "implementedInterfaces",
									"fieldList", "methodSignature", "methodList" } ),

	new_cannot_be_final("'new' methods cannot be declared with keyword 'final'",
											new String [] { "identifier",
											"prototypeName", "supertype", "implementedInterfaces",
											"fieldList", "methodSignature", "methodList" } ),

	new_methods_should_be_public("'new' methods should be declared 'public'",
									new String [] { "identifier",
									"prototypeName", "supertype", "implementedInterfaces",
									"fieldList", "methodSignature", "methodList" } ),

	method_is_duplicated("Method is duplicated",
		new String [] { "identifier",
			"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodList",
			"method0", "method1", "methodName" } ),


	method_should_be_declared_after_previous_method_with_the_same_keywords(
			"Method should be declared after method with the same keywords",
			new String [] { "identifier",
			"prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodList", "method0", "method1" } ),

	method_is_overridden_method_with_different_visibility(
					"Method is overridden a method with a different visibility (public, protected)",
					new String [] { "identifier",
					"prototypeName", "supertype", "implementedInterfaces",
					"fieldList", "methodList", "method0", "method1" } ),
	methods_with_the_same_keywords_with_and_without_override(
							"Methods with the same keywords should be declared both with 'override' or both without 'override'",
							new String [] { "identifier",
							"prototypeName", "supertype", "implementedInterfaces",
							"fieldList", "methodList", "method0", "method1" } ),
	methods_with_the_same_keywords_with_and_without_final(
		"Methods with the same keywords should be declared both with 'final' or both without 'final'",
		new String [] { "identifier",
		   "prototypeName", "supertype", "implementedInterfaces",
		   "fieldList", "methodList", "method0", "method1" } ),


    methods_with_the_same_keywords_and_different_return_types(
					"Methods with the same keywords should be declared with the same return type",
					new String [] { "identifier",
					"prototypeName", "supertype", "implementedInterfaces",
					"fieldList", "methodList", "method0", "method1" } ),

    methods_with_the_same_keywords_and_different_visibilities(
		"Methods with the same keywords should be declared with the same visibility",
		new String [] { "identifier", "prototypeName", "supertype", "implementedInterfaces",
				"fieldList", "methodList", "method0", "method1" } ),
	incompatible_return_type_in_subprototype_method("Incompatible return type in sub-prototype method",
			new String[] { "prototypeName0", "supertype0", "prototypeName1", "supertype1", "implementedInterfaces0",
			"implementedInterfaces1",
			 "method0", "method1", "methodName0", "returnType0", "methodName1", "returnType1" } ),
    private_method_declared_with_override(
    		"Private method declared with 'override'",
    		new String [] { "identifier",
    		   "prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodList" }),

    private_method_declared_with_final(
    		    		"Private method declared with 'final'",
    		    		new String [] { "identifier",
    		    		   "prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodList" }),

    private_method_declared_with_abstract(
    		    		    		"Private abstract method is illegal",
    		    		    		new String [] { "identifier",
    		    		    		   "prototypeName", "supertype", "implementedInterfaces",
    		    		    		   "fieldList", "methodList" }),

    abstract_method_declared_with_final("Abstract method declared with 'final'",
    		   new String [] { "identifier", "prototypeName", "supertype", "implementedInterfaces",
    		"fieldList", "methodList" }),
    abstract_method_in_a_non_abstract_prototype("Abstract method cannot belong to a non-abstract prototype",
    		new String[] { "identifier", "methodSignature", "prototypeName", "supertype",
    		"implementedInterfaces"
 		   }),

    method_is_not_overriding_any_super_type_method(
    		"This is no method with this same signature in super-prototypes",
    		    		    		new String [] { "identifier", "supertype",
    		    		    		   "prototypeName", "supertype", "implementedInterfaces",
    		    		    		   "fieldList", "methodList" }),
    override_without_supertype("'override' cannot be used without a supertype",
         new String [] { "identifier", "methodSignature",
    	   "prototypeName",  "implementedInterfaces", "fieldList", "methodList" }),
    attempt_to_extend_a_final_type("Final types cannot have sub-prototypes or sub-types",
    	            new String [] { "identifier",
    	       	   "prototypeName", "supertype", "implementedInterfaces" }),
    method_was_not_found_in_prototype_or_super_prototypes("Method was not found in the receiver´s prototype or in its super-prototypes",
    		new String[] {"statementText", "methodSignature", "methodName", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList", "method0",
			"prototypeReceiver", "receiverExpr", "superprototypeList"} ),
			// superprototypeList is the list of all supertypes of the prototype separated by commas

	method_was_not_found_in_current_prototype_or_super_prototypes("Method was not found in the current prototype or in its super-prototypes",
		    		new String[] {"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList", "methodList", "method0" } ),
	use_of_super_without_a_super_prototype("This prototype does not have a superprototype",
			    		new String[] {"statementText", "methodSignature",
			"prototypeName", "implementedInterfaces",
							"visibleLocalVariableList", } ),

	backquote_not_followed_by_a_string_variable("Backquote should be followed by a String variable",
						    		new String[] {"statementText", "methodSignature", "prototypeName",
			"supertype", "implementedInterfaces",
										"visibleLocalVariableList", "fieldList" } ),

	two_or_more_backquotes_in_unary_chain("Two or more backquotes in a chain of unary messages",
									    		new String[] {"statementText", "methodSignature", "prototypeName",
			"supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList" } ),
	illegal_use_of_backquote("Illegal use of hasBackquote. Unary message chain should have only one unary message when character ` is used",
		new String[] {"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
		   "visibleLocalVariableList", "fieldList" } ),
    method_cannot_be_indexing_method("This method cannot be an indexing method. Only 'at:' and 'at:put:' methods can be indexing methods and be used with '[]'",
    		new String [] { "identifier", "supertype",
		    		   "prototypeName", "supertype", "implementedInterfaces", "fieldList", "methodList" }),

    cyan_metaobject_error_message_inside_method("Error in metaobject",
    		new String [] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
    		"visibleLocalVariableList",
			"fieldList", "methodList" }),
    cyan_metaobject_error_message_inside_prototype("Error in metaobject",
	    		new String [] {  "prototypeName", "supertype", "implementedInterfaces",
    		"visibleLocalVariableList",
					"fieldList", "methodList" }),
    cyan_metaobject_error_message_outside_prototype("Error in metaobject",
				    		new String [] {  "prototypeName", "supertype", "implementedInterfaces", }),

    error_in_literal_object("Error in literal object",
    		new String [] {
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),

    error_in_macro("Error in macro",
		    		new String [] {
					"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList", "methodList" } ),

	no_public_protected_prototype_found_in_source_file("This source file should declare at least a public or protected prototype",
			new String[] { "identifier" } ),
	two_or_more_public_protected_prototype_found_in_source_file(
			"There should be exactly one 'public' or 'protected' prototype in every source file",
					new String[] { "identifier" } ),
	non_generic_prototype_in_the_same_source_file_with_generic_prototype(
			"Non-generic protype was declared in a source file that has a generic prototype",
			new String[] { "identifier", "prototype0" }),

	non_public_generic_prototype("Non-public generic prototype", new String[] {"prototypeName",
			"supertype", "implementedInterfaces" }),

	mixins_of_generic_and_non_generic_parameters("Mixing of generic and non-generic parameters in the same generic prototype",
			new String [] {}),
	two_or_more_generic_prototype_in_the_same_source_file("Two or more generic prototypes in the same source file",
			new String[] { "prototypeName" } ),
	keyword_object_expected("Keyword 'object' expected", new String[] {} ),
	keyword_program_expected("Keyword 'program' expected", new String[] {} ),
	keyword_end_expected("Keyword 'end' expected", new String[] { "prototypeName",
			"supertype", "implementedInterfaces",
			"packageName" }),
	right_parenthesis_expected_in_mixin_declaration("')' expected",
			new String[] {"mixinName", "prototypeName", "supertype", "implementedInterfaces",
			"packageName"}),
	method_cannot_be_user_defined("This method cannot be user-defined",
    		new String [] {
			"methodSignature", "prototypeName", "supertype", "implementedInterfaces", "methodList" } ),
	metaobject_cannot_be_attached_to_prototype("This metaobject cannot be attached to a prototype",
			new String [] { "prototypeName", "supertype", "implementedInterfaces", "metaobject"}),
    metaobject_was_not_found("Metaobject was not found",
					new String [] { "identifier", "metaobject"
					 }),


	metaobject_was_not_found_inside_method("Metaobject was not found",
			new String [] { "identifier", "metaobject",
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" }),

	metaobject_was_not_found_outside_method("Metaobject was not found",
			new String [] { "identifier", "metaobject",
			 "prototypeName", "supertype", "implementedInterfaces",
			 "fieldList", "methodList" }),

	metaobject_was_not_found_outside_prototype("Metaobject was not found",
									new String [] { "prototypeName", "supertype", "implementedInterfaces",
			"metaobject"}),
	metaobject_annotation_error_missing_args_symbols("')', ']', or '}' expected after in this metaobject annotation",
			new String[] { "metaobject" } ),

	metaobject_annotation_error_missing_text("A text was expected after in this metaobject annotation between two sequences of symbols",
					new String[] { "metaobject" } ),


	metaobject_wrong_number_of_parameters("Wrong number of parameters in metaobject annotation",
			new String[] { "metaobject" } ),

	metaobject_error_reading_codeg_info_file("Codeg info file cannot be found or there was an error reading it",
					new String [] { "identifier", "metaobject"
					 }),


	literal_string_expected_after_comma("A literal string was expected after ',' in an import list",
			new String[] { "projectDirectoryOrName" }),
	literal_string_expected("A literal string was expected",
					new String[] { "projectDirectoryOrName" }),

	file_does_not_exist("This file does not exist", new String[] { "filename" } ),
	file_should_be_directory("This file should be a directory", new String[] { "filename" } ),

	file_error("Error when handling file", new String[] { "filename" } ),
	file_does_not_have_cyan_extension("File does not have a '.cyan' extension", new String[] { "filename"} ),

	file_name_incorrect_in_compilation_unit("Incorrect file name for source file", new String[] {}),

	metaobject_attempt_to_add_two_methods_with_the_same_name_to_a_prototype("Two metaobjects are attempting to insert two methods with the same name to the same prototype",
			new String[] { "packageName", "prototypeName", "supertype", "implementedInterfaces" }),
	metaobject_attempt_to_add_two_readonly_variables_with_the_same_name_to_a_prototype(
			"Two metaobjects are attempting to insert two read only variables with the same name to the same prototype",
					new String[] { "packageName", "prototypeName", "supertype", "implementedInterfaces" }),
	metaobject_attempt_to_add_two_instance_variables_with_the_same_name_to_a_prototype(
							"Two metaobjects are attempting to insert two read only variables with the same name to the same prototype",
									new String[] { "packageName", "prototypeName", "supertype", "implementedInterfaces" }),

	metaobject_error("Error in metaobject annotation", new String[]{} ),
	internal_error("Internal compiler error",     		new String [] {
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),

	non_public_cast_method("'cast:' method should be public", new String[] {
			"methodSignature", "prototypeName", "supertype", "implementedInterfaces" } ),
	abstract_cast_method("'cast:' method should be public", new String[] {
					"methodSignature", "prototypeName", "supertype", "implementedInterfaces" } ),
	attempt_to_assign_a_value_to_a_readonly_variable("Attempt to assign a value to a read only variable",
			new String[] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList"  }),
	final_cast_method("'cast:' method cannot be final", new String[] {
					"methodSignature", "prototypeName", "supertype", "implementedInterfaces" } ),
	typeof_used_in_union("'typeof' cannot be used in union types", new String[] {
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces"
	}),
	right_square_bracket_expected_in_indexing_method("']' was expected in an indexing method",
					new String[] {"methodSignature", "prototypeName", "supertype", "implementedInterfaces"} ),
	function_returning_values_of_different_types("Function returning values of different types", new String[] {
			"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces"
	}),
	cyan_prototype_expected_as_type("A Cyan prototype was expected as type",
			new String[] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),
	type_error_return_value_type_is_not_a_subtype_of_the_method_return_type("Type error: the type of the returned value is not a subtype of the method return type",
			new String[] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList", "returnType" } ),

	type_error_type_of_right_hand_side_of_assignment_is_not_a_subtype_of_the_type_of_left_hand_side(
			"Type error: the type of the left-hand side of the assignment is not a subtype of the type of the right-hand side",
					new String[] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList", "methodList",
					"leftExpr", "rightExpr", "leftType", "rightType" } ),

	type_error_return_value_type_is_not_a_subtype_of_the_function_return_type(
			"Type error: the type of the returned value is not a subtype of the function return type",
			new String[] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),

	return_with_caret_outside_a_function(
					"Return of a function with '^ expr'. But this statement is not inside a function. This statement cannot be used as in 'if i < 10 { ^ 0 }'. The same applies to the 'while' statement",
					new String[] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList", "methodList" } ),
	syntax_error("Syntax error", new String[] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),
	syntax_error_object("Syntax error: keyword 'object' is missplaced. Maybe an attempt to declare an inner object",
			new String[] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
					"visibleLocalVariableList", "fieldList", "methodList" } ),
	parsing_compilation_phase_literal_objects_and_macros_are_not_allowed("Literal objects and macros in a compilation step that does not allow literal objects or macros",
			new String[] {"statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),
	right_symbol_sequence_expected("A right symbol sequence of a metaobject annotation was expected",
			new String[] { "statementText", "methodSignature", "prototypeName", "supertype", "implementedInterfaces",
			"visibleLocalVariableList", "fieldList", "methodList" } ),
	;



	ErrorKind(String generalMessage, String []fieldList) {
		this.generalMessage = generalMessage;
		this.fieldList = fieldList;
	}

	@Override public String toString() {
		String all = generalMessage;
		for ( String s : fieldList )
			all = all + " " + s;
		return all;
	}

	public String getGeneralMessage() {
		return generalMessage;
	}



	public String[] getFieldList() {
		return fieldList;
	}

	private String generalMessage;


	private String []fieldList;


}

