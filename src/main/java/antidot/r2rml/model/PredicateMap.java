/* 
 * Copyright 2011 Antidot opensource@antidot.net
 * https://github.com/antidot/db2triples
 * 
 * DB2Triples is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * DB2Triples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * R2RML Model : PredicateMap class
 *
 * An element of this class, called a PredicateMap, contains the rules for 
 * generating the predicate component of the (predicate, object) pair generated 
 * by a PredicateObjectMap.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
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
