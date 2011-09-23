/***************************************************************************
 *
 * Invalid R2RML Syntax Exception 
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	TEST
 * 
 * Fichier			:	InvalidR2RMLSyntaxException.java
 *
 * Description		:	Exception raised when R2RML syntax of file is invalid.
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.r2rml.exception;

public class InvalidR2RMLSyntaxException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidR2RMLSyntaxException(String message) {
		super(message);
	}

}
