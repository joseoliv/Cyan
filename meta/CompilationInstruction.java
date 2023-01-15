package meta;

/**
 * compilation instructions; that is, what the compiler is allowed
 * to do and what it should do. There is a Figure in Chapter "Metaobjects" in the Cyan
 * manual with the compilation steps. Each compilation step has a
 * set of {@link meta.CompilationInstruction} objects associated to it.
 * Each compilation step has some
 * permissions and actions. Then not everything is allowed in every
 * step and in each of them the compiler should take some actions
 * that are not taken in other steps. This enumeration gives the compiler
 * permissions and  what it is expected to do.
 *
 *
 *
   @author José
 */
public enum CompilationInstruction {
	/**
	 * parsing actions are allowed. parsing = During Parsing
	 */
	parsing_actions,
	/**
	 * AFTER_RES_TYPES actions are allowed. AFTER_RES_TYPES = After Typing Interfaces
	 */
	afterResTypes_actions,
	/**
	 * SEM_AN actions are allowed. SEM_AN = During Semantic Analysis
	 */
	semAn_actions,
	/**
	 * check actions in the code in step 9 of the compilation, calculate internal types (cit)
	 */
	semAn_check,
	/**
	 * check actions made at the end of step 8 of the compilation, calculate interfaces. Therefore, after typing interfaces (AFTER_RES_TYPES)
	 */
	ati3_check,
	/**
	 * add method 'prototype'  to the prototype
	 */
	pp_addCode,
	/**
	 * generic prototypes instantiation is allowed
	 */
	genericPrototypeInstantiation,
	/**
	 * add some methods to every prototype that did not define
	 * them. For example, defaultValue and clone. For each
	 * 'init' and 'init:' method, add 'new' and 'new:' methods.
	 */
	new_addCode,
	/**
	 * add an inner prototype for each function and method of a prototype
	 */
	inner_addCode,
	/**
	 * first compilation phase in which the text of each compilation unit is
	 * the original one supplied by the user
	 */
	parsing_originalSourceCode,
	/**
	 * the source code may have annotations to metaobjects that implement interface {@link IInformCompilationError}.
	 * This interface should be implemented by all metaobjects that need to inform the compiler that
	 * an error should be issued (by the compiler). If this enumerated constant is used, the compiler should
	 * check whether there are any errors that should be issued (according to the metaobjects that implement {@link IInformCompilationError})
	 * but were not.
	 * This is only done in the last step of compilation.
	 */
	matchExpectedCompilationErrors,
	/**
	 * the source being compiled is from the project file, extension <code>".pyan"</code>. If this
	 * option is used, no other should be but parsing_actions
	 */
	pyanSourceCode,
	/**
	 * when this options is used, to each interface in the Cyan source code
	 * there should be created a prototype that represents the interface when it is used as an expression
	 */
	createPrototypesForInterfaces,
	/**
	 * when this option is used, the compiler should apply options {@link #pp_addCode} and {@link #new_addCode}
	 * to inner prototypes
	 */
	pp_new_inner_addCode,

}
