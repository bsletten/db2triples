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
 * R2RML Model : RefObjectMap interface
 *
 * An element of a class which implements this interface, contains the rules
 * for generating the object component of the (predicate, object) pair generated
 * by a RefPredicateObjectMap.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
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
