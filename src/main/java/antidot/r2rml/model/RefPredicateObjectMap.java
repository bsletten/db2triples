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
 * R2RML Model : RefPredicateObject class
 *
 * An element of a class which implements this interface, contains the rules for
 * generating a (predicate, object) pair from a logical table row based on
 * a foreign key relationship. It consists of a RefPredicateMap and a RefObjectMap. 
 * 
 * Conceptually, a RefPredicateObjectMap is similar to a PredicateObjectMap, 
 * but involves a child (or referring) table and a parent (or referred) table. 
 * 
 * The RefPredicateMap specifies how to generate the predicate component
 * based on the key of the foreign key constraint definition in the child 
 * table and the RefObjectMap specifies how to generate the object component 
 * from the two logical tables, the child and the parent.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
package antidot.r2rml.model;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;

import antidot.r2rml.exception.InvalidR2RMLStructureException;
import antidot.r2rml.exception.InvalidR2RMLSyntaxException;
import antidot.r2rml.tools.R2RMLToolkit;

/**
 * @author  jh
 */
public class RefPredicateObjectMap implements IRefPredicateObjectMap {

	/**
	 */
	private Set<URI> graphs;
	/**
	 */
	private Set<String> graphColumns;
	/**
	 */
	private Set<String> graphTemplates;
	/**
	 */
	private RefPredicateMap refPredicateMap;
	/**
	 */
	private RefObjectMap refObjectMap;

	public RefPredicateObjectMap(Set<URI> graphs, Set<String> graphColumns,
			Set<String> graphTemplates, RefPredicateMap refPredicateMap,
			RefObjectMap refObjectMap) throws InvalidR2RMLSyntaxException, InvalidR2RMLStructureException {
		super();
		setGraphs(graphs);
		setGraphColumns(graphColumns);
		setGraphTemplates(graphTemplates);
		this.refPredicateMap = refPredicateMap;
		this.refObjectMap = refObjectMap;
	}

	/**
	 * @return
	 */
	public Set<URI> getGraphs() {
		return graphs;
	}

	/**
	 * @param graphs
	 */
	public void setGraphs(Set<URI> graphs) {
		this.graphs = new HashSet<URI>();
		for (URI graph : graphs) {
			this.graphs.add(graph);
		}
	}

	/**
	 * @return
	 */
	public Set<String> getGraphColumns() {
		return graphColumns;
	}

	/**
	 * @param graphColumns
	 */
	public void setGraphColumns(Set<String> graphColumns) {
		this.graphColumns = new HashSet<String>();
		for (String graphColumn : graphColumns) {
			this.graphColumns.add(graphColumn);
		}
	}

	/**
	 * @return
	 */
	public Set<String> getGraphTemplates() {
		return graphTemplates;
	}

	/**
	 * @param graphTemplates
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setGraphTemplates(Set<String> graphTemplates) throws InvalidR2RMLSyntaxException {
		this.graphTemplates = new HashSet<String>();
		for (String graphTemplate : graphTemplates) {
			if (!R2RMLToolkit.checkCurlyBraces(graphTemplate))
				throw new InvalidR2RMLSyntaxException(
						"A synthax error have be found in your R2RML expression : "
								+ graphTemplate);
			this.graphTemplates.add(graphTemplate);
		}
	}

	/**
	 * @return
	 */
	public RefPredicateMap getRefPredicateMap() {
		return refPredicateMap;
	}

	/**
	 * @param refPredicateMap
	 */
	public void setRefPredicateMap(RefPredicateMap refPredicateMap) {
		this.refPredicateMap = refPredicateMap;
	}

	/**
	 * @return
	 */
	public RefObjectMap getRefObjectMap() {
		return refObjectMap;
	}

	/**
	 * @param refObjectMap
	 */
	public void setRefObjectMap(RefObjectMap refObjectMap) {
		this.refObjectMap = refObjectMap;
	}
}
