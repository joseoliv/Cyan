/**
 *
 */
package ast;

import saci.CyanEnv;

/**
 * @author Jos�
 *
 */
public interface GenCyan {
	void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions);
	// void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions);
	String asString();
}
