/***************************************************************************
 *
 * R2RML Model : TermType class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	TermType.java
 *
 * Description		:	A string indicating whether subject or object generated using the value
 * 						column name specified for rr:column should be an IRI reference,
 * 						blank node or (if object) literal.
 * 
 * Reference		:	RDF Vocabulary for R2RML
 * 						R2RML: RDB to RDF Mapping Language
 * 						W3C Working Draft 24 March 2011
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.r2rml.model;

/**
 * @author   jh
 */
public enum TermType {
	/**
	 */
	IRI("IRI"), /**
	 */
	BLANK_NODE("BlankNode"), /**
	 */
	LITERAL("Literal");

	/**
	 */
	private String displayName;

	private TermType(String displayName) {
		this.displayName = displayName;
	}

	public String toString() {
		return displayName;
	}

	/**
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Converts a termType from its display name.
	 * 
	 * @param displayName
	 * @return
	 */
	public static TermType toTermType(String displayName) {
		for (TermType termType : TermType.values()) {
			if (termType.getDisplayName().equals(displayName))
				return termType;
		}
		return null;
	}

}
