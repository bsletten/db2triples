/***************************************************************************
 *
 * Invalid R2RML Structure Exception 
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	TEST
 * 
 * Fichier			:	InvalidR2RMLStructureException.java
 *
 * Description		:	Exception raised when R2RML structure of file is invalid.
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.r2rml.exception;

public class InvalidR2RMLStructureException extends Exception {

	public InvalidR2RMLStructureException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
