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
 * R2RML Model : PredicateObjectMap class
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

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;

import antidot.r2rml.exception.InvalidR2RMLSyntaxException;
import antidot.r2rml.tools.R2RMLToolkit;

/**
 * @author  jh
 */
public class PredicateObjectMap implements IPredicateObjectMap {

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
	private ObjectMap objectMap;
	/**
	 */
	private PredicateMap predicateMap;
	
	public PredicateObjectMap(Set<URI> graphs, Set<String> graphColumns,
			Set<String> graphTemplates, ObjectMap objectMap, PredicateMap predicateMap) throws InvalidR2RMLSyntaxException {
		super();
		setGraphs(graphs);
		setGraphColumns(graphColumns);
		setGraphTemplates(graphTemplates);
		this.objectMap = objectMap;
		this.predicateMap = predicateMap;
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
	public ObjectMap getObjectMap() {
		return objectMap;
	}

	/**
	 * @param objectMap
	 */
	public void setObjectMap(ObjectMap objectMap) {
		this.objectMap = objectMap;
	}

	/**
	 * @return
	 */
	public PredicateMap getPredicateMap() {
		return predicateMap;
	}

	/**
	 * @param predicateMap
	 */
	public void setPredicateMap(PredicateMap predicateMap) {
		this.predicateMap = predicateMap;
	}
}
