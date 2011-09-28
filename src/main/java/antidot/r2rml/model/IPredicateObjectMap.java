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
 * R2RML Model : PredicateObjectMap interface
 *
 * An element of a class which implements this interface, contains the rules
 * for generating a (predicate, object) pair for a row in the logical table
 * being mapped.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
package antidot.r2rml.model;

import java.util.Set;

import org.openrdf.model.URI;

import antidot.r2rml.exception.InvalidR2RMLSyntaxException;

/**
 * @author   jh
 */
public interface IPredicateObjectMap {

	/**
	 * This optional property specifies a graph IRI. The RDF triple generated from a logical table row, containing the (predicate, object) pair from this PredicateObjectMap, will be stored in the specified named graph. A special IRI, rr:defaultGraph, may be used to specify that the generated triples should be stored in the default graph. If the user specifies the default graph and also one or more named graphs, then the generated triple will be stored in the default graph as well as in each of the specified named graphs.
	 * @return
	 */
	public Set<URI> getGraphs();

	/**
	 * @param  graphs
	 */
	public void setGraphs(Set<URI> graphs);

	/**
	 * This optional property specifies the name of a column in the logical table. The value from this column of a logical table row is used as the graph name where the RDF triple generated from a logical table row, containing the (predicate, object) pair from this PredicateObjectMap, will be stored. There is no restriction on maximum cardinality of this property. If for a row, value from the specified column is NULL, then the generated triple will be stored in the default graph.
	 * @return
	 */
	public Set<String> getGraphColumns();

	/**
	 * @param  graphColumns
	 */
	public void setGraphColumns(Set<String> graphColumns);

	/**
	 * This optional property, for use in place of rr:graphColumn, specifies a template (or format string) to construct a value, for use as a graph IRI, based on one or more columns from a logical table row.
	 * @return
	 */
	public Set<String> getGraphTemplates();

	/**
	 * @param graphTemplates
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setGraphTemplates(Set<String> graphTemplates) throws InvalidR2RMLSyntaxException;

	/**
	 * This property specifies the PredicateMap component of a PredicateObjectMap.
	 * @return
	 */
	public PredicateMap getPredicateMap();

	/**
	 * @param  predicateMap
	 */
	public void setPredicateMap(PredicateMap predicateMap);

	/**
	 * This property specifies the ObjectMap component of a PredicateObjectMap.
	 * @return
	 */
	public ObjectMap getObjectMap();

	/**
	 * @param  objectMap
	 */
	public void setObjectMap(ObjectMap objectMap);

}
