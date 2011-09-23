/***************************************************************************
 *
 * R2RML Model : RefObjectMap class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	RefObjectMap.java
 *
 * Description		:	An element of this class, called a RefObjectMap,
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
import antidot.r2rml.tools.R2RMLToolkit;

/**
 * @author  jh
 */
public class RefObjectMap implements IRefObjectMap {

	/**
	 */
	private String joinCondition;
	/**
	 */
	private TriplesMap parentTriplesMap;

	public RefObjectMap(String joinCondition, TriplesMap parentTriplesMap) throws InvalidR2RMLSyntaxException {
		super();

		// Check properties consistency
		if (parentTriplesMap == null || joinCondition == null)
			throw new IllegalStateException(
					"[RefObjectMap:RefObjectMap] Broken reference : parentTriplesMap and joinCondition have to be specified.");
		if (!R2RMLToolkit.checkCurlyBraces(joinCondition) || !R2RMLToolkit.checkJoinCondition(joinCondition)){
			throw new InvalidR2RMLSyntaxException("A synthax error have be found in your R2RML expression : " + joinCondition);
		}
		setJoinCondition(joinCondition);
		this.parentTriplesMap = parentTriplesMap;
	}

	/**
	 * @return
	 */
	public String getJoinCondition() {
		return joinCondition;
	}

	/**
	 * @param joinCondition
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setJoinCondition(String joinCondition) throws InvalidR2RMLSyntaxException {
		if (!R2RMLToolkit.checkCurlyBraces(joinCondition))
			throw new InvalidR2RMLSyntaxException(
					"A synthax error have be found in your R2RML expression : " + joinCondition);
		this.joinCondition = joinCondition;
	}

	/**
	 * @return
	 */
	public TriplesMap getParentTriplesMap() {
		return parentTriplesMap;
	}

	/**
	 * @param parentTriplesMap
	 */
	public void setParentTriplesMap(TriplesMap parentTriplesMap) {
		this.parentTriplesMap = parentTriplesMap;
	}
}