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
 * R2RML Model : RefObjectMap class
 *
 * An element of this class, called a RefObjectMap, contains the rules for 
 * generating the object component of the (predicate, object) pair generated 
 * by a RefPredicateObjectMap.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
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