/***************************************************************************
 *
 * R2RML Model : RefObjectMap interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	IRefObjectMap.java
 *
 * Description		:	An element of a class which implements this interface,
 * 						contains the rules for generating the object component 
 * 						of the (predicate, object) pair generated
 * 						by a RefPredicateObjectMap.
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

import antidot.r2rml.exception.InvalidR2RMLSyntaxException;

/**
 * @author   jh
 */
public interface IRefObjectMap {

	/**
	 * This property specifies the join condition for joining the (child) logical table in the current TriplesMap with the (parent) logical table specified by rr:parentTriplesMap. Column names are case-sensitive. Also, the column names specified must follow either "{childAlias.}" or "{parentAlias.}". Use of curly braces in the template, for any other purpose must be escaped by a backslash character.
	 * @return
	 */
	public String getJoinCondition();

	/**
	 * @param joinCondition
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setJoinCondition(String joinCondition) throws InvalidR2RMLSyntaxException;

	/**
	 * This property specifies the TriplesMap corresponding to the parent (or referenced) table component of the foreign key constraint. The following example identifies the TriplesMap corresponding to the parent (or referenced) logical table.
	 * @return
	 */
	public TriplesMap getParentTriplesMap();

	/**
	 * @param  parentTriplesMap
	 */
	public void setParentTriplesMap(TriplesMap parentTriplesMap);

}
