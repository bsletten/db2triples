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
 * R2RML Model : TriplesMap Interface
 *
 * An element of this class, called a TriplesMap, contains the rules for 
 * generating the RDF triples for a row in the logical table that is being mapped.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
package antidot.r2rml.model;

import java.util.HashSet;

public interface ITriplesMap {

	/**
	 * Property used to specify to logical table. Name of a table or view.
	 * 
	 * @return
	 */
	public String getTableName();

	/**
	 * Property used to specify to logical table. A valid SQL query.
	 * 
	 * @return
	 */
	public String getSQLQuery();

	/**
	 * Property used to specify to logical table. Name of table owner. Note
	 * (ISSUE-34) : “Table owner” is a somewhat vendor-specific term. It has
	 * been suggested that this should be better replaced with standard SQL
	 * terminology, such as “catalog” and “schema”.
	 * 
	 * @return
	 */
	public String getTableOwner();

	/**
	 * This property specifies a SubjectMap that contains the rules for
	 * generating the subject for a logical table row that will be used in all
	 * the RDF triples generated from the row.
	 * 
	 * @return
	 */
	public ISubjectMap getSubjectMap();

	/**
	 * This property specifies a PredicateObjectMap that contains the rules for
	 * generating a (predicate, object) pair from a logical table row that will
	 * be combined with the subject for the row to generate an RDF triple.
	 * 
	 * @return
	 */
	public HashSet<PredicateObjectMap> getPredicateObjectMaps();

	/**
	 * This property specifies a RefPredicateObjectMap that contains the rules
	 * for generating a (predicate, object) pair from a logical table row and
	 * the logical table row in another TriplesMap based upon a foreign key
	 * constraint.
	 * 
	 * @return
	 */
	public HashSet<RefPredicateObjectMap> getRefPredicateObjectMaps();

}
