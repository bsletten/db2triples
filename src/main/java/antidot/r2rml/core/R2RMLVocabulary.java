/***************************************************************************
 *
 * R2RML Vocabulary 
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	TEST
 * 
 * Fichier			:	R2RMLVocabulary.java
 *
 * Description		:	Enum class which contains R2RML vocabulary 
 * 						for syntax Turtle.
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
/**
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
