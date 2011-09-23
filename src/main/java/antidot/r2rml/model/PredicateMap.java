/***************************************************************************
 *
 * R2RML Model : PredicateMap class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	PredicateMap.java
 *
 * Description		:	An element of this class, called a PredicateMap,
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

import antidot.r2rml.exception.InvalidR2RMLStructureException;
import antidot.r2rml.exception.InvalidR2RMLSyntaxException;
import antidot.r2rml.tools.R2RMLToolkit;

/**
 * @author  jh
 */
public class PredicateMap implements IPredicateMap {

	/**
	 */
	private URI predicate;
	/**
	 */
	private String column;
	/**
	 */
	private String template;
	/**
	 */
	private String inverseExpression;

	public PredicateMap(URI predicate, String column, String template,
			String inverseExpression) throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
		super();
		
		// Check properties consistency 
		if ((column == null) && (predicate == null) && (template == null)) {
			throw new InvalidR2RMLStructureException(
					"[PredicateMap:PredicateMap] column or predicate or template have to be specified.");
		} else if ((column != null) && (predicate != null)) {
			throw new InvalidR2RMLStructureException(
					"[PredicateMap:PredicateMap] Ambiguity between column and predicate : the two "
					+ "are specified but just one is required.");
		} else if ((column != null) && (template != null)) {
			throw new InvalidR2RMLStructureException(
					"[PredicateMap:PredicateMap] Ambiguity between column and template : the two "
					+ "are specified but just one is required.");
		} else if ((predicate != null) && (template != null)) {
			throw new InvalidR2RMLStructureException(
					"[PredicateMap:PredicateMap] Ambiguity between predicate and template : the two "
					+ "are specified but just one is required.");
		}
		
		this.predicate = predicate;
		this.column = column;
		setTemplate(template);
		setInverseExpression(inverseExpression);
	}

	/**
	 * @return
	 */
	public URI getPredicate() {
		return predicate;
	}

	/**
	 * @param predicate
	 */
	public void setPredicate(URI predicate) {
		this.predicate = predicate;
	}

	/**
	 * @return
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @param column
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * @return
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setTemplate(String template) throws InvalidR2RMLSyntaxException {
		if (!R2RMLToolkit.checkCurlyBraces(template))
			throw new InvalidR2RMLSyntaxException(
					"[PredicateMap:setTemplate] A synthax error have be found in your R2RML expression : " + template);
		this.template = template;
	}

	/**
	 * @return
	 */
	public String getInverseExpression() {
		return inverseExpression;
	}

	/**
	 * @param inverseExpression
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setInverseExpression(String inverseExpression) throws InvalidR2RMLSyntaxException {
		if (!R2RMLToolkit.checkCurlyBraces(inverseExpression))
			throw new InvalidR2RMLSyntaxException(
					"[PredicateMap:setInverseExpression] A synthax error have be found in your R2RML expression : " + inverseExpression);
		this.inverseExpression = inverseExpression;
	}

}
