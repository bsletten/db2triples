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

/***************************************************************************
 *
 * R2RML : R2RML Mapping Factory absctract class
 *
 * Factory responsible of R2RML Mapping generation.
 *
 * @author jhomo
 *
 ****************************************************************************/
package antidot.r2rml.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;

import antidot.r2rml.exception.InvalidR2RMLStructureException;
import antidot.r2rml.exception.InvalidR2RMLSyntaxException;
import antidot.r2rml.model.ISubjectMap;
import antidot.r2rml.model.ObjectMap;
import antidot.r2rml.model.PredicateMap;
import antidot.r2rml.model.PredicateObjectMap;
import antidot.r2rml.model.R2RMLMapping;
import antidot.r2rml.model.RefObjectMap;
import antidot.r2rml.model.RefPredicateMap;
import antidot.r2rml.model.RefPredicateObjectMap;
import antidot.r2rml.model.SubjectMap;
import antidot.r2rml.model.TermType;
import antidot.r2rml.model.TriplesMap;
import antidot.rdf.impl.sesame.SesameDataSet;
import antidot.xmls.type.XSDType;

public abstract class R2RMLMappingFactory {

	// Log
	private static Log log = LogFactory.getLog(R2RMLMappingFactory.class);

	// Namespaces
	public static HashMap<String, String> prefix = new HashMap<String, String>();

	// Load prefix
	static {
		// prefix.put("foaf", "http://xmlns.com/foaf/0.1/");
		prefix.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefix.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		prefix.put("rr", "http://www.w3.org/ns/r2rml#");
		prefix.put("xsd", XSDType.XSD_NAMESPACE);
		// prefix.put("fn", "http://www.w3.org/2005/xpath-functions");
	}

	/**
	 * Extract R2RML Mapping object from a R2RML file written with Turtle
	 * syntax. This syntax is recommanded in R2RML : RDB to RDF Mapping Language
	 * (W3C Working Draft 24 March 2011)"An R2RML mapping document is any
	 * document written in the Turtle [TURTLE] RDF syntax that encodes an R2RML
	 * mapping graph."
	 * 
	 * @param fileToR2RMLFile
	 * @return
	 * @throws InvalidR2RMLSyntaxException
	 * @throws InvalidR2RMLStructureException
	 */
	public static R2RMLMapping extractR2RMLMapping(String fileToR2RMLFile)
			throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
		// Load RDF data from R2RML Mapping document
		SesameDataSet r2rmlMappingGraph = new SesameDataSet();
		r2rmlMappingGraph.loadDataFromFile(fileToR2RMLFile, RDFFormat.TURTLE);
		log
				.debug("[R2RMLFactory:extractR2RMLMapping] Number of R2RML triples in file "
						+ fileToR2RMLFile + " : " + r2rmlMappingGraph.getSize());

		// Construct R2RML Mapping object

		// Construct TriplesMap objects
		URI p = r2rmlMappingGraph.URIref(prefix.get("rdf") + "type");
		URI o = r2rmlMappingGraph.URIref(prefix.get("rr")
				+ R2RMLVocabulary.TRIPLES_MAP_CLASS);
		List<Statement> statements = r2rmlMappingGraph.tuplePattern(null, p, o);
		log
		.debug("[R2RMLFactory:extractR2RMLMapping] Number of R2RML triples with "
				+ " type " + R2RMLVocabulary.TRIPLES_MAP_CLASS + " in file "
				+ fileToR2RMLFile + " : " + statements.size());
		// Store each triplesMap in order to
		// retrieve objects relations like
		// with the property rr:parentTriplesMap
		HashMap<URI, TriplesMap> storedTriplesMaps = new HashMap<URI, TriplesMap>();
		// Creates each triplesMap object
		for (Statement triplesMapStmt : statements) {
			// Extract each triplesMap
			TriplesMap triplesMap = new TriplesMap();
			// Store triple map
			storedTriplesMaps
					.put((URI) triplesMapStmt.getSubject(), triplesMap);
		}
		// Fill each triplesMap object
		for (URI triplesMapSubject : storedTriplesMaps.keySet()) {
			// Extract each triplesMap
			extractTriplesMap(storedTriplesMaps.get(triplesMapSubject),
					r2rmlMappingGraph, triplesMapSubject, storedTriplesMaps);
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractR2RMLMapping] New TriplesMap object : "
								+ storedTriplesMaps.get(triplesMapSubject));
		}
		// Generate R2RMLMapping object
		R2RMLMapping result = new R2RMLMapping(storedTriplesMaps.values());
		return result;
	}

	/**
	 * Extract triplesMap contents.
	 * @param triplesMap
	 * @param r2rmlMappingGraph
	 * @param triplesMapSubject
	 * @param storedTriplesMaps
	 * @throws InvalidR2RMLStructureException
	 * @throws InvalidR2RMLSyntaxException
	 */
	private static void extractTriplesMap(TriplesMap triplesMap,
			SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject,
			HashMap<URI, TriplesMap> storedTriplesMaps)
			throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
		if (log.isDebugEnabled())
			log
					.debug("[R2RMLFactory:extractTriplesMap] Found TriplesMap object : "
							+ triplesMapSubject.stringValue());

		// Extract TriplesMap properties
		String tableName = extractStringProperty(r2rmlMappingGraph,
				triplesMapSubject, R2RMLVocabulary.TABLE_NAME_PROP);
		String tableOwner = extractStringProperty(r2rmlMappingGraph,
				triplesMapSubject, R2RMLVocabulary.TABLE_OWNER_PROP);
		String SQLQuery = extractStringProperty(r2rmlMappingGraph,
				triplesMapSubject, R2RMLVocabulary.SQL_QUERY_PROP);

		// Extract objects in relation with triplesMap object
		ISubjectMap subjectMap = extractSubjectMap(r2rmlMappingGraph,
				triplesMapSubject);
		HashSet<PredicateObjectMap> predicateObjectMaps = extractPredicateObjectMaps(
				r2rmlMappingGraph, triplesMapSubject);
		HashSet<RefPredicateObjectMap> refPredicateObjectMaps = extractRefPredicateObjectMaps(
				r2rmlMappingGraph, triplesMapSubject, storedTriplesMaps);

		triplesMap.setProperties(tableName, tableOwner, SQLQuery, subjectMap,
				predicateObjectMaps, refPredicateObjectMaps);

		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractTriplesMap] Done.");
	}

	/**
	 * Extract property from a Sesame Graph and a resource.
	 * 
	 * @param r2rmlMappingGraph
	 * @param subject
	 * @return
	 */
	private static String extractStringProperty(
			SesameDataSet r2rmlMappingGraph, Resource subject,
			R2RMLVocabulary property) {
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractStringProperty] From subject : "
					+ subject.stringValue() + " with property : " + property);
		URI p = r2rmlMappingGraph.URIref(prefix.get("rr") + property);
		List<Statement> propertyStatements = r2rmlMappingGraph.tuplePattern(
				subject, p, null);
		if (propertyStatements.size() > 1) {
			// This list must have 0 or 1 element
			String propertyValue = propertyStatements.get(0).getObject()
					.stringValue();
			if (log.isWarnEnabled())
				log.warn("[R2RMLFactory:extractStringProperty] Two or more "
						+ property + " are found, first used by default : "
						+ propertyValue + " (triple " + subject.stringValue()
						+ ")");
			return propertyValue;
		}
		if (propertyStatements.isEmpty()) {
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractStringProperty] Property not specified.");
			return null;
		} else {
			String propertyValue = propertyStatements.get(0).getObject()
					.stringValue();

			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractStringProperty] Extracted value : "
								+ propertyValue);
			return propertyValue;
		}
	}

	/**
	 * Extract property from a Sesame Graph and a resource.
	 * 
	 * @param r2rmlMappingGraph
	 * @param subject
	 * @return
	 */
	private static Value extractValueProperty(SesameDataSet r2rmlMappingGraph,
			Resource subject, R2RMLVocabulary property) {
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractValueProperty] From subject : "
					+ subject.stringValue() + " with property : " + property);
		URI p = r2rmlMappingGraph.URIref(prefix.get("rr") + property);
		List<Statement> propertyStatements = r2rmlMappingGraph.tuplePattern(
				subject, p, null);
		if (propertyStatements.size() > 1) {
			// This list must have 0 or 1 element
			Value propertyValue = propertyStatements.get(0).getObject();
			if (log.isWarnEnabled())
				log.warn("[R2RMLFactory:extractStringProperty] Two or more "
						+ property + " are found, first used by default : "
						+ propertyValue + " (triple " + subject.stringValue()
						+ ")");
			return propertyValue;
		}

		if (propertyStatements.isEmpty()) {
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractStringProperty] Property not specified.");
			return null;
		} else {
			Value propertyValue = propertyStatements.get(0).getObject();
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractValueProperty] Extracted value : "
								+ propertyValue);
			return propertyValue;
		}
	}

	/**
	 * Extract properties from a subject map.
	 * 
	 * @param r2rmlMappingGraph
	 * @param triplesMapSubject
	 * @return
	 * @throws InvalidR2RMLSyntaxException
	 * @throws InvalidR2RMLStructureException
	 */
	private static ISubjectMap extractSubjectMap(
			SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject)
			throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractSubjectMap] -- From subject : "
					+ triplesMapSubject.stringValue());
		ISubjectMap result = null;
		URI p = r2rmlMappingGraph.URIref(prefix.get("rr")
				+ R2RMLVocabulary.SUBJECT_MAP_PROP);
		List<Statement> subjectMapStatements = r2rmlMappingGraph.tuplePattern(
				triplesMapSubject, p, null);
		/*
		 * if ((subjectMapStatements.isEmpty()) || (subjectMapStatements.size()
		 * > 1)) { // This list must have exactly 1 element throw new
		 * IllegalStateException(
		 * "[R2RMLFactory:extractSubjectMap] Exactly one subjectMap have to be specified "
		 * + " (triple " + triplesMapSubject.stringValue() + ")"); } else {
		 */
		// Extract subjectMap properties
		if ((subjectMapStatements != null) && (!subjectMapStatements.isEmpty())) {
			Value o = subjectMapStatements.get(0).getObject();
			Resource subjectMapSubject = null;
			try {
				subjectMapSubject = (Resource) o;
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log
							.error("[R2RMLFactory:extractSubjectMap] subjectMap predicate can't be associated with a literal.");
				}
				e.printStackTrace();
			}
			Resource subject = (Resource) extractValueProperty(
					r2rmlMappingGraph, subjectMapSubject,
					R2RMLVocabulary.SUBJECT_PROP);
			String column = extractStringProperty(r2rmlMappingGraph,
					subjectMapSubject, R2RMLVocabulary.COLUMN_PROP);
			String template = extractStringProperty(r2rmlMappingGraph,
					subjectMapSubject, R2RMLVocabulary.TEMPLATE_PROP);
			Set<Value> uncastedGraph = extractValuesProperty(r2rmlMappingGraph,
					subjectMapSubject, R2RMLVocabulary.GRAPH_PROP);
			// Cast graph
			Set<URI> graphs = new HashSet<URI>();
			for (Value v : uncastedGraph)
				graphs.add((URI) v);
			String rdfsClass = extractStringProperty(r2rmlMappingGraph,
					subjectMapSubject, R2RMLVocabulary.RDFS_CLASS_PROP);
			Set<String> graphColumns = extractStringsProperty(r2rmlMappingGraph,
					subjectMapSubject, R2RMLVocabulary.GRAPH_COLUMN_PROP);
			Set<String> graphTemplates = extractStringsProperty(
					r2rmlMappingGraph, subjectMapSubject,
					R2RMLVocabulary.GRAPH_TEMPLATE_PROP);
			String inverseExpression = extractStringProperty(r2rmlMappingGraph,
					subjectMapSubject, R2RMLVocabulary.INVERSE_EXPRESSION_PROP);
			String termType = extractStringProperty(r2rmlMappingGraph,
					subjectMapSubject, R2RMLVocabulary.TERM_TYPE_PROP);
			// Construct final subjectMap object
			result = new SubjectMap(subject, column, template, rdfsClass,
					graphs, graphColumns, graphTemplates, inverseExpression,
					TermType.toTermType(termType));
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractSubjectMap] -- Done.");
		return result;
	}

	/**
	 * Extract property from a Sesame Graph and a resource.
	 * 
	 * @param r2rmlMappingGraph
	 * @param subject
	 * @return
	 */
	private static Set<String> extractStringsProperty(
			SesameDataSet r2rmlMappingGraph, Resource resource,
			R2RMLVocabulary property) {
		HashSet<String> strings = new HashSet<String>();
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractStringsProperty] From resource : "
					+ resource.stringValue() + " with property : " + property);
		URI p = r2rmlMappingGraph.URIref(prefix.get("rr") + property);
		List<Statement> propertyStatements = r2rmlMappingGraph.tuplePattern(
				resource, p, null);
		if (propertyStatements.isEmpty()) {
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractStringsProperty] Property not specified.");
		} else {
			for (Statement propertyStatement : propertyStatements) {
				String propertyValue = propertyStatement.getObject()
						.stringValue();
				strings.add(propertyValue);
				if (log.isDebugEnabled())
					log
							.debug("[R2RMLFactory:extractStringsProperty] Extracted value : "
									+ propertyValue);
			}
		}
		return strings;
	}

	/**
	 * Extract property values from a Sesame Graph and a resource.
	 * 
	 * @param r2rmlMappingGraph
	 * @param subject
	 * @return
	 */
	private static Set<Value> extractValuesProperty(
			SesameDataSet r2rmlMappingGraph, Resource subject,
			R2RMLVocabulary property) {
		Set<Value> values = new HashSet<Value>();
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractValuesProperty] From subject : "
					+ subject.stringValue() + " with property : " + property);
		URI p = r2rmlMappingGraph.URIref(prefix.get("rr") + property);
		List<Statement> propertyStatements = r2rmlMappingGraph.tuplePattern(
				subject, p, null);
		if (propertyStatements.isEmpty()) {
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractValuesProperty] Property not specified.");
		} else {
			for (Statement propertyStatement : propertyStatements) {
				Value propertyValue = propertyStatement.getObject();
				if (log.isDebugEnabled())
					log
							.debug("[R2RMLFactory:extractValuesProperty] Extracted value : "
									+ propertyValue);
				if (propertyValue.equals(r2rmlMappingGraph.URIref(prefix.get("rr") + R2RMLVocabulary.DEFAULT_GRAPH_OBJ))){
					// Default graph => it is the default behaviour of
				}
				values.add(propertyValue);
			}
		}
		return values;
	}

	/**
	 * Extract properties from a predicateObject map.
	 * 
	 * @param r2rmlMappingGraph
	 * @param triplesMapSubject
	 * @return
	 * @throws InvalidR2RMLSyntaxException
	 * @throws InvalidR2RMLStructureException
	 */
	private static HashSet<PredicateObjectMap> extractPredicateObjectMaps(
			SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject)
			throws InvalidR2RMLSyntaxException, InvalidR2RMLStructureException {
		HashSet<PredicateObjectMap> result = new HashSet<PredicateObjectMap>();
		if (log.isDebugEnabled())
			log
					.debug("[R2RMLFactory:extractPredicateObjectMaps] From triplesMapSubject : "
							+ triplesMapSubject.stringValue());

		URI p = r2rmlMappingGraph.URIref(prefix.get("rr")
				+ R2RMLVocabulary.PREDICATE_OBJECT_MAP_PROP);
		List<Statement> predicateObjectMapStatements = r2rmlMappingGraph
				.tuplePattern(triplesMapSubject, p, null);
		int cpt = 1;
		for (Statement statement : predicateObjectMapStatements) {
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractPredicateObjectMaps] ----- Extract PredicateObjectMap "
								+ cpt
								+ " / "
								+ predicateObjectMapStatements.size() + ".");
			// Extract predicateMapObject properties
			Value o = statement.getObject();
			Resource predicateObjectMapSubject = null;
			try {
				predicateObjectMapSubject = (Resource) o;
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log
							.error("[R2RMLFactory:extractSubjectMap] predicateObjectMap can't be associated with a literal.");
				}
				e.printStackTrace();
			}

			Set<Value> uncastedGraphs = extractValuesProperty(r2rmlMappingGraph,
					predicateObjectMapSubject, R2RMLVocabulary.GRAPH_PROP);
			// Cast graph
			Set<URI> graphs = new HashSet<URI>();
			for (Value v : uncastedGraphs)
				graphs.add((URI) v);
			Set<String> graphColumns = extractStringsProperty(r2rmlMappingGraph,
					predicateObjectMapSubject,
					R2RMLVocabulary.GRAPH_COLUMN_PROP);
			Set<String> graphTemplates = extractStringsProperty(r2rmlMappingGraph,
					predicateObjectMapSubject,
					R2RMLVocabulary.GRAPH_TEMPLATE_PROP);
			ObjectMap objectMap = extractObjectMap(r2rmlMappingGraph,
					predicateObjectMapSubject);
			PredicateMap predicateMap = extractPredicateMap(r2rmlMappingGraph,
					predicateObjectMapSubject);

			// Construct PredicateObjectMap object
			PredicateObjectMap predicateObjectMap = new PredicateObjectMap(
					graphs, graphColumns, graphTemplates, objectMap, predicateMap);
			result.add(predicateObjectMap);
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractPredicateObjectMaps] ----- PredicateObjectMap extracted.");
			cpt++;
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractPredicateObjectMaps] --- Done.");
		return result;
	}

	/**
	 * Extract properties from a object map.
	 * 
	 * @param r2rmlMappingGraph
	 * @param predicateObjectMapSubject
	 * @return
	 * @throws InvalidR2RMLStructureException
	 * @throws InvalidR2RMLSyntaxException
	 */
	private static ObjectMap extractObjectMap(SesameDataSet r2rmlMappingGraph,
			Resource predicateObjectMapSubject)
			throws InvalidR2RMLSyntaxException, InvalidR2RMLStructureException {
		if (log.isDebugEnabled())
			log
					.debug("[R2RMLFactory:extractObjectMap] -- From predicateObjectMapSubject : "
							+ predicateObjectMapSubject.stringValue());
		ObjectMap result = null;
		URI p = r2rmlMappingGraph.URIref(prefix.get("rr")
				+ R2RMLVocabulary.OBJECT_MAP_PROP);
		List<Statement> objectMapStatements = r2rmlMappingGraph.tuplePattern(
				predicateObjectMapSubject, p, null);
		if ((objectMapStatements.isEmpty()) || (objectMapStatements.size() > 1)) {
			// This list must have exactly 1 element
			throw new IllegalStateException(
					"[R2RMLFactory:extractObjectMap] Exactly one objectMap have to be specified"
							+ " instead of " + objectMapStatements.size());
		} else {
			// Extract objectMap properties
			Value o = objectMapStatements.get(0).getObject();
			Resource objectSubjectMap = null;
			try {
				objectSubjectMap = (Resource) o;
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log
							.error("[R2RMLFactory:extractObjectMap] objectMap predicate can't be associated with a literal.");
				}
				e.printStackTrace();
			}
			Value object = extractValueProperty(r2rmlMappingGraph,
					objectSubjectMap, R2RMLVocabulary.OBJECT_PROP);
			String column = extractStringProperty(r2rmlMappingGraph,
					objectSubjectMap, R2RMLVocabulary.COLUMN_PROP);
			String template = extractStringProperty(r2rmlMappingGraph,
					objectSubjectMap, R2RMLVocabulary.TEMPLATE_PROP);
			String datatype = extractStringProperty(r2rmlMappingGraph,
					objectSubjectMap, R2RMLVocabulary.DATATYPE_PROP);
			String language = extractStringProperty(r2rmlMappingGraph,
					objectSubjectMap, R2RMLVocabulary.LANGUAGE_PROP);
			String inverseExpression = extractStringProperty(r2rmlMappingGraph,
					objectSubjectMap, R2RMLVocabulary.INVERSE_EXPRESSION_PROP);
			String termType = extractStringProperty(r2rmlMappingGraph,
					objectSubjectMap, R2RMLVocabulary.TERM_TYPE_PROP);
			// Construct final objectMap object
			result = new ObjectMap(object, column, template, XSDType
					.toXSDType(datatype), language, inverseExpression, TermType
					.toTermType(termType));
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractObjectMap] -- Done.");
		return result;
	}

	/**
	 * Extract properties from a predicate map.
	 * 
	 * @param r2rmlMappingGraph
	 * @param predicateObjectMapSubject
	 * @return
	 * @throws InvalidR2RMLSyntaxException
	 * @throws InvalidR2RMLStructureException
	 */
	private static PredicateMap extractPredicateMap(
			SesameDataSet r2rmlMappingGraph, Resource predicateObjectMapSubject)
			throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {

		if (log.isDebugEnabled())
			log
					.debug("[R2RMLFactory:extractPredicateMap] -- From predicateMapSubject : "
							+ predicateObjectMapSubject.stringValue());
		PredicateMap result = null;
		URI p = r2rmlMappingGraph.URIref(prefix.get("rr")
				+ R2RMLVocabulary.PREDICATE_MAP_PROP);
		List<Statement> predicateMapStatements = r2rmlMappingGraph
				.tuplePattern(predicateObjectMapSubject, p, null);
		if ((predicateMapStatements.isEmpty())
				|| (predicateMapStatements.size() > 1)) {
			// This list must have exactly 1 element
			throw new IllegalStateException(
					"[R2RMLFactory:extractPredicateMap] Exactly one predicateMap have to be specified "
							+ " (triple "
							+ predicateObjectMapSubject.stringValue() + ")");
		} else {
			// Extract predicateMap properties
			Value o = predicateMapStatements.get(0).getObject();
			Resource predicateSubjectMap = null;
			try {
				predicateSubjectMap = (Resource) o;
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log
							.error("[R2RMLFactory:extractPredicateMap] predicateMap predicate can't be associated with a literal.");
				}
				e.printStackTrace();
			}
			URI predicate = (URI) extractValueProperty(r2rmlMappingGraph,
					predicateSubjectMap, R2RMLVocabulary.PREDICATE_PROP);
			String column = extractStringProperty(r2rmlMappingGraph,
					predicateSubjectMap, R2RMLVocabulary.COLUMN_PROP);
			String template = extractStringProperty(r2rmlMappingGraph,
					predicateSubjectMap, R2RMLVocabulary.TEMPLATE_PROP);
			String inverseExpression = extractStringProperty(r2rmlMappingGraph,
					predicateSubjectMap,
					R2RMLVocabulary.INVERSE_EXPRESSION_PROP);
			// Construct final predicateMap object
			result = new PredicateMap(predicate, column, template,
					inverseExpression);
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractPredicateMap] -- Done.");
		return result;
	}

	/**
	 * Extract properties from a refPredicateObject map.
	 * 
	 * @param r2rmlMappingGraph
	 * @param triplesMapSubject
	 * @param storedTriplesMaps
	 * @return
	 * @throws InvalidR2RMLStructureException
	 * @throws InvalidR2RMLSyntaxException
	 */
	private static HashSet<RefPredicateObjectMap> extractRefPredicateObjectMaps(
			SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject,
			HashMap<URI, TriplesMap> storedTriplesMaps)
			throws InvalidR2RMLSyntaxException, InvalidR2RMLStructureException {
		HashSet<RefPredicateObjectMap> result = new HashSet<RefPredicateObjectMap>();
		if (log.isDebugEnabled())
			log
					.debug("[R2RMLFactory:extractRefPredicateObjectMaps] From triplesMapSubject : "
							+ triplesMapSubject.stringValue());

		URI p = r2rmlMappingGraph.URIref(prefix.get("rr")
				+ R2RMLVocabulary.REF_PREDICATE_OBJECT_MAP_PROP);
		List<Statement> refPredicateObjectMapStatements = r2rmlMappingGraph
				.tuplePattern(triplesMapSubject, p, null);
		int cpt = 1;
		for (Statement statement : refPredicateObjectMapStatements) {
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractRefPredicateMaps] ----- Extract RefPredicateMap "
								+ cpt
								+ " / "
								+ refPredicateObjectMapStatements.size() + ".");
			// Extract refPredicateMapObject properties
			Value o = statement.getObject();
			Resource refPredicateObjectMapSubject = null;
			try {
				refPredicateObjectMapSubject = (Resource) o;
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log
							.error("[R2RMLFactory:extractRefPredicateObjectMaps] refPredicateObjectMap can't be associated with a literal.");
				}
				e.printStackTrace();
			}

			Set<Value> uncastedGraphs = extractValuesProperty(r2rmlMappingGraph,
					refPredicateObjectMapSubject, R2RMLVocabulary.GRAPH_PROP);
			// Cast graph
			Set<URI> graphs = new HashSet<URI>();
			for (Value v : uncastedGraphs)
				graphs.add((URI) v);
			Set<String> graphColumns = extractStringsProperty(r2rmlMappingGraph,
					refPredicateObjectMapSubject,
					R2RMLVocabulary.GRAPH_COLUMN_PROP);
			Set<String> graphTemplates = extractStringsProperty(r2rmlMappingGraph,
					refPredicateObjectMapSubject,
					R2RMLVocabulary.GRAPH_TEMPLATE_PROP);
			RefObjectMap refObjectMap = extractRefObjectMap(r2rmlMappingGraph,
					refPredicateObjectMapSubject, storedTriplesMaps);
			RefPredicateMap refPredicateMap = extractRefPredicateMap(
					r2rmlMappingGraph, refPredicateObjectMapSubject);

			// Construct PredicateObjectMap object
			RefPredicateObjectMap refPredicateObjectMap = new RefPredicateObjectMap(
					graphs, graphColumns, graphTemplates, refPredicateMap,
					refObjectMap);
			result.add(refPredicateObjectMap);
			if (log.isDebugEnabled())
				log
						.debug("[R2RMLFactory:extractRefPredicateObjectMaps] ----- RefPredicateObjectMap extracted.");
			cpt++;
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractRefPredicateObjectMaps] --- Done.");
		return result;
	}

	/**
	 * Extract properties from a refPredicate map.
	 * 
	 * @param r2rmlMappingGraph
	 * @param refPredicateObjectMapSubject
	 * @return
	 */
	private static RefPredicateMap extractRefPredicateMap(
			SesameDataSet r2rmlMappingGraph,
			Resource refPredicateObjectMapSubject) {
		if (log.isDebugEnabled())
			log
					.debug("[R2RMLFactory:extractRefPredicateMap] -- From refPredicateMapSubject : "
							+ refPredicateObjectMapSubject.stringValue());
		RefPredicateMap result = null;
		URI p = r2rmlMappingGraph.URIref(prefix.get("rr")
				+ R2RMLVocabulary.REF_PREDICATE_MAP_PROP);
		List<Statement> refPredicateMapStatements = r2rmlMappingGraph
				.tuplePattern(refPredicateObjectMapSubject, p, null);
		if ((refPredicateMapStatements.isEmpty())
				|| (refPredicateMapStatements.size() > 1)) {
			// This list must have exactly 1 element
			throw new IllegalStateException(
					"[R2RMLFactory:extractPredicateMap] Exactly one objectMap have to be specified "
							+ " (triple "
							+ refPredicateObjectMapSubject.stringValue() + ")");
		} else {
			// Extract subjectMap properties
			Value o = refPredicateMapStatements.get(0).getObject();
			Resource refPredicateSubjectMap = null;
			try {
				refPredicateSubjectMap = (Resource) o;
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log
							.error("[R2RMLFactory:extractPredicateMap] subjectMap predicate can't be associated with a literal.");
				}
				e.printStackTrace();
			}
			URI predicate = (URI) extractValueProperty(r2rmlMappingGraph,
					refPredicateSubjectMap, R2RMLVocabulary.PREDICATE_PROP);
			// Construct final predicateMap object
			result = new RefPredicateMap(predicate);
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractPredicateMap] -- Done.");
		return result;
	}

	/**
	 * @param r2rmlMappingGraph
	 * @param refPredicateObjectMapSubject
	 * @param storedTriplesMaps
	 * @return
	 * @throws InvalidR2RMLSyntaxException
	 */
	private static RefObjectMap extractRefObjectMap(
			SesameDataSet r2rmlMappingGraph,
			Resource refPredicateObjectMapSubject,
			HashMap<URI, TriplesMap> storedTriplesMaps)
			throws InvalidR2RMLSyntaxException {
		if (log.isDebugEnabled())
			log
					.debug("[R2RMLFactory:extractRefObjectMap] -- From refObjectMapSubject : "
							+ refPredicateObjectMapSubject.stringValue());
		RefObjectMap result = null;
		URI p = r2rmlMappingGraph.URIref(prefix.get("rr")
				+ R2RMLVocabulary.REF_OBJECT_MAP_PROP);
		List<Statement> refObjectMapStatements = r2rmlMappingGraph
				.tuplePattern(refPredicateObjectMapSubject, p, null);
		if ((refObjectMapStatements.isEmpty())
				|| (refObjectMapStatements.size() > 1)) {
			// This list must have exactly 1 element
			throw new IllegalStateException(
					"[R2RMLFactory:extractRefObjectMap] Exactly one objectMap have to be specified "
							+ " (triple "
							+ refPredicateObjectMapSubject.stringValue() + ")");
		} else {
			// Extract subjectMap properties
			Value o = refObjectMapStatements.get(0).getObject();
			Resource refObjectSubjectMap = null;
			try {
				refObjectSubjectMap = (Resource) o;
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log
							.error("[R2RMLFactory:extractRefObjectMap] subjectMap predicate can't be associated with a literal.");
				}
				e.printStackTrace();
			}
			URI triplesMapURI = (URI) extractValueProperty(r2rmlMappingGraph,
					refObjectSubjectMap,
					R2RMLVocabulary.PARENT_TRIPLES_MAP_PROP);
			// Found triplesMap associated object
			TriplesMap parentTriplesMap = storedTriplesMaps.get(triplesMapURI);
			log
					.debug("[R2RMLFactory:extractRefObjectMap] Parent triplesMap object : "
							+ parentTriplesMap);
			String joinCondition = extractStringProperty(r2rmlMappingGraph,
					refObjectSubjectMap, R2RMLVocabulary.JOIN_CONDITION_PROP);
			// Construct final predicateMap object
			result = new RefObjectMap(joinCondition, parentTriplesMap);
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractRefObjectMap] -- Done.");
		return result;
	}
}
