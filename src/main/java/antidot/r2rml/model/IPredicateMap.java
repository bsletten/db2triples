/***************************************************************************
 *
 * R2RML Model : PredicateMap interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	PredicateMap.java
 *
 * Description		:	An element of a class which implements this interface
 * 						contains the rules for generating the predicate 
 * 						component of the (predicate, object) pair generated 
 * 						by a PredicateObjectMap.
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

import org.openrdf.model.URI;

import antidot.r2rml.exception.InvalidR2RMLSyntaxException;

/**
 * @author   jh
 */
public interface IPredicateMap {

	/**
	 * This property specifies an RDF property. The specified property will be used as the predicate for the generated triple.
	 * @return
	 */
	public URI getPredicate();

	/**
	 * @param  predicate
	 */
	public void setPredicate(URI predicate);

	/**
	 * When the property name is not a constant and instead comes from the values in a column, this optional property (used in place of rr:predicate) allows the user to specify the column name. Note that this property should always be a column name in the logical table.
	 * @return
	 */
	public String getColumn();

	/**
	 * @param  column
	 */
	public void setColumn(String column);

	/**
	 * This optional property, for use in place of rr:column, specifies a template (or format string) to construct a value, for use as a predicate IRI, based on one or more columns from a logical table row.
	 * @return
	 */
	public String getTemplate();

	/**
	 * @param template
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setTemplate(String template) throws InvalidR2RMLSyntaxException;

	/**
	 * This optional property, for use in place of rr:graphColumn, specifies a template (or format string) to construct a value, for use as a graph IRI, based on one or more columns from a logical table row. Column names are case-sensitive and must be enclosed within curly braces. Use of curly braces in the template, for any purpose other than enclosing column names, must be escaped by a backslash character.
	 * @return
	 */
	public String getInverseExpression();

	/**
	 * @param inverseExpression
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setInverseExpression(String inverseExpression) throws InvalidR2RMLSyntaxException;

}
