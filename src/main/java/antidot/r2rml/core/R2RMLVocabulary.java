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
 * R2RML Vocabulary 
 *
 * Enum class which contains R2RML vocabulary for syntax Turtle.
 * 
 * Reference : RDF Vocabulary for R2RML, R2RML: RDB to RDF Mapping Language
 * W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
package antidot.r2rml.core;

/**
 * @author   jh
 */
public enum R2RMLVocabulary {

	/**
	 */
	TRIPLES_MAP_CLASS("TriplesMapClass"), 
	/**
	 */
	SQL_QUERY_PROP("SQLQuery"),
	/**
	 */
	SUBJECT_PROP("subject"),
	/**
	 */
	SUBJECT_MAP_PROP("subjectMap"),
	/**
	 */
	PREDICATE_OBJECT_MAP_PROP("predicateObjectMap"),
	/**
	 */
	PREDICATE_MAP_PROP("predicateMap"), 
	/**
	 */
	OBJECT_MAP_PROP("objectMap"),
	/**
	 */
	GRAPH_PROP("graph"),
	/**
	 */
	TABLE_NAME_PROP("tableName"),
	/**
	 */
	TABLE_OWNER_PROP("tableOwner"),
	/**
	 */
	COLUMN_PROP("column"),
	/**
	 */
	CLASS_PROP("class"),
	/**
	 */
	GRAPH_COLUMN_PROP("graphColumn"),
	/**
	 */
	TEMPLATE_PROP("template"),
	/**
	 */
	GRAPH_TEMPLATE_PROP("graphTemplate"),
	/**
	 */
	INVERSE_EXPRESSION_PROP("inverseExpression"),
	/**
	 */
	TERM_TYPE_PROP("termtype"),
	/**
	 */
	DATATYPE_PROP("datatype"),
	/**
	 */
	LANGUAGE_PROP("language"),
	/**
	 */
	OBJECT_PROP("object"),
	/**
	 */
	PREDICATE_PROP("predicate"),
	/**
	 */
	REF_PREDICATE_OBJECT_MAP_PROP("refPredicateObjectMap"),
	/**
	 */
	REF_PREDICATE_MAP_PROP("refPredicateMap"),
	/**
	 */
	REF_OBJECT_MAP_PROP("refObjectMap"),
	/**
	 */
	JOIN_CONDITION_PROP("joinCondition"),
	/**
	 */
	PARENT_TRIPLES_MAP_PROP("parentTriplesMap"),
	/**
	 */
	JOIN_CONDITION_PARENT_ALIAS("parentAlias"),
	/**
	 */
	JOIN_CONDITION_CHILD_ALIAS("childAlias"),
	/**
	 */
	DEFAULT_GRAPH_OBJ("defaultGraph"),
	/**
	 */
	RDFS_CLASS_PROP("class");

	private String displayName;

	private R2RMLVocabulary(String displayName) {
		this.displayName = displayName;
	}

	public String toString() {
		return displayName;
	}

}
