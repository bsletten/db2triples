/***************************************************************************
 *
 * R2RML : R2RML Engine
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RML
 * 
 * Fichier			:	R2RMLEngine.java
 *
 * Description		:	The R2RML engine is the link between a R2RML Mapping object
 * 						and a database connection. It constructs the final RDF graph
 * 						from the R2RML mapping document by extracting data in database.
 * 
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.r2rml.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import antidot.r2rml.model.ISubjectMap;
import antidot.r2rml.model.ObjectMap;
import antidot.r2rml.model.PredicateMap;
import antidot.r2rml.model.PredicateObjectMap;
import antidot.r2rml.model.R2RMLMapping;
import antidot.r2rml.model.RefObjectMap;
import antidot.r2rml.model.RefPredicateMap;
import antidot.r2rml.model.RefPredicateObjectMap;
import antidot.r2rml.model.TermType;
import antidot.r2rml.model.TriplesMap;
import antidot.rdf.impl.sesame.SesameDataSet;
import antidot.rdf.tools.SQLToRDFToolkit;
import antidot.sql.core.SQLConnector;
import antidot.xmls.type.XSDType;

public class R2RMLEngine {

	// Log
	private static Log log = LogFactory.getLog(R2RMLEngine.class);

	// SQL Connection
	private Connection conn;

	// Current logical table
	private ResultSet logicalTable;

	// Prefix used in this class
	private static HashMap<String, String> prefix = new HashMap<String, String>();
	static {
		prefix.put("xsd", "http://www.w3.org/2001/XMLSchema#");
		prefix.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefix.put("rr", "http://www.w3.org/ns/r2rml#");
	}

	// Value factory
	private static ValueFactory vf = new ValueFactoryImpl();

	// Default graph URI
	private static URI defaultGraph = vf.createURI(prefix.get("rr")
			+ R2RMLVocabulary.DEFAULT_GRAPH_OBJ);

	public R2RMLEngine(Connection conn) {
		super();
		if (conn == null)
			throw new IllegalStateException(
					"[R2RMLEngine:R2RMLEngine] SQL connection does not exists.");
		this.conn = conn;
		logicalTable = null;
	}

	/**
	 * Execute R2RML Mapping from a R2RML file in order to generate a RDF
	 * dataset. This dataset is built with Sesame API.
	 * 
	 * @param r2rmlMapping
	 * @return
	 * @throws SQLException
	 */
	public SesameDataSet runR2RMLMapping(R2RMLMapping r2rmlMapping,
			String pathToNativeStore) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("[R2RMLEngine:runR2RMLMapping] Run R2RML mapping... ");
		SesameDataSet sesameDataSet = null;
		// Check if use of native store is required
		if (pathToNativeStore != null) {
			if (log.isDebugEnabled())
				log.debug("[R2RMLEngine:runR2RMLMapping] Use native store "
						+ pathToNativeStore);
			sesameDataSet = new SesameDataSet(pathToNativeStore, false);
		} else {
			sesameDataSet = new SesameDataSet();
		}

		// Explore R2RML Mapping TriplesMap objects
		extractRDFFromTriplesMap(sesameDataSet, r2rmlMapping);

		// Close connection to logical table
		if (logicalTable != null) {

			logicalTable.getStatement().close();
			logicalTable.close();
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLEngine:runR2RMLMapping] R2RML mapping done. ");
		return sesameDataSet;
	}

	/**
	 * Extract all RDF triples which are contained in triplesMap of a R2RML
	 * Mapping object.
	 * 
	 * @param sesameDataSet
	 * @param r2rmlMapping
	 * @throws SQLException
	 */
	private void extractRDFFromTriplesMap(SesameDataSet sesameDataSet,
			R2RMLMapping r2rmlMapping) throws SQLException {
		if (log.isDebugEnabled())
			log
					.debug("[R2RMLEngine:extractRDFFromTriplesMap] Extract tripleMaps... ");
		HashSet<TriplesMap> triplesMaps = new HashSet<TriplesMap>(r2rmlMapping
				.getTriplesMaps());
		for (TriplesMap triplesMap : triplesMaps) {
			if (log.isDebugEnabled())
				log.debug("[R2RMLEngine:extractRDFFromTriplesMap]"
						+ " ---- Extract triplesMap...");
			// Construct current logical table of this triplesMap
			logicalTable = constructLogicalTable(triplesMap);
			if (log.isDebugEnabled())
				log.debug("[R2RMLEngine:extractRDFFromTriplesMap]"
						+ " Extracted logicalTable : "
						+ printLogicalTable(logicalTable));
			// Explore each row contained in the logical table
			while (logicalTable.next()) {
				// Extract subject
				ISubjectMap subjectMap = triplesMap.getSubjectMap();
				Resource subject = extractSubjectFromSubjectMap(logicalTable,
						subjectMap);
				if (subject == null) {
					/*
					 * throw new IllegalStateException(
					 * "[R2RMLEngine:extractRDFFromTriplesMap]" +
					 * " No subject extracted.");
					 */
					if (log.isWarnEnabled())
						log.warn("[R2RMLEngine:extractRDFFromTriplesMap]"
								+ " No subject extracted.");
				}
				if (log.isDebugEnabled())
					log.debug("[R2RMLEngine:extractRDFFromTriplesMap]"
							+ " Resource subject extracted : " + subject);
				// Extract graphs subject
				Set<URI> subjectGraphs = extractGraphFromSubjectMap(subjectMap);
				if (log.isDebugEnabled()) {
					for (URI graph : subjectGraphs)
						log.debug("[R2RMLEngine:extractRDFFromTriplesMap]"
								+ " Resource graph subject extracted : "
								+ graph);
				}
				// Construct class triple
				if (subjectMap.getRdfsClass() != null) {
					URI predicate = vf.createURI(prefix.get("rdf"), "type");
					Value type = vf.createURI(subjectMap.getRdfsClass());
					if (subjectGraphs.isEmpty())
						// Add to default graph...
						sesameDataSet.add(subject, predicate, type);
					else
						// ...or to other graphs
						for (URI graph : subjectGraphs) {
							if (log.isDebugEnabled())
								log
										.debug("[R2RMLEngine:extractRDFFromTriplesMap]"
												+ " Class subject extracted : "
												+ type);
							if (graph.equals(vf.createURI(prefix.get("rr")
									+ R2RMLVocabulary.DEFAULT_GRAPH_OBJ))) {
								// Default graph <=> No context
								sesameDataSet.add(subject, predicate, type);
							} else {
								sesameDataSet.add(subject, predicate, type,
										graph);
							}

						}
				}
				// TODO : Use of subject inverse expression ?
				// Extract predicates and objects
				for (PredicateObjectMap predicateObjectMap : triplesMap
						.getPredicateObjectMaps()) {
					// Extract graphs predicateObject
					Set<URI> predicateObjectsGraphs = extractGraphFromPredicateObjectMap(predicateObjectMap);
					if (log.isDebugEnabled()) {
						for (URI graph : predicateObjectsGraphs)
							log.debug("[R2RMLEngine:extractRDFFromTriplesMap]"
									+ " Resource graph predicateObjectsGraphs "
									+ "extracted : " + graph);
					}
					// Extract predicate
					URI predicate = extractPredicateFromPredicateMap(predicateObjectMap
							.getPredicateMap());
					if (log.isDebugEnabled())
						log.debug("[R2RMLEngine:extractRDFFromTriplesMap]"
								+ " Resource predicate extracted : "
								+ predicate);
					// TODO : Use of predicate inverse expression ?
					// Extract object
					// Extract object properties
					ObjectMap objectMap = predicateObjectMap.getObjectMap();
					XSDType datatype = extractDatatypeFromObjectMap(objectMap);
					Value object = extractObjectFromObjectMap(objectMap,
							datatype);

					if (object.stringValue() == null){
						if (log.isWarnEnabled())
							log
									.warn("R2RMLEngine:extractRDFFromTriplesMap] " +
											"Object of triple is null : this triple will be ignored.");
							continue;			
					}
					if (log.isDebugEnabled())
						log.debug("[R2RMLEngine:extractRDFFromTriplesMap]"
								+ " Resource object extracted : " + object);

					// TODO : Use of object inverse expression ?

					// Construct triple
					if (subjectGraphs.isEmpty()) {
						// No subject graph specified : use default graph
						sesameDataSet.add(subject, predicate, object);
					} else {
						// Add triple in the specified subject graphs
						for (URI subjectGraph : subjectGraphs) {
							if (subjectGraph.equals(defaultGraph)) {
								// Default graph <=> No context
								sesameDataSet.add(subject, predicate, object);
							} else {
								sesameDataSet.add(subject, predicate, object,
										subjectGraph);
							}
						}
					}
					// Add triple in the specified predicate object map too
					for (URI predicateObjectGraph : predicateObjectsGraphs) {
						if (predicateObjectGraph.equals(defaultGraph)) {
							// Default graph <=> No context
							sesameDataSet.add(subject, predicate, object);
						} else {
							sesameDataSet.add(subject, predicate, object,
									predicateObjectGraph);
						}
					}
				}
				// Extract ref predicates and ref objects
				for (RefPredicateObjectMap refPredicateObjectMap : triplesMap
						.getRefPredicateObjectMaps()) {
					// Extract properties
					Set<URI> refPredicateObjectsGraphs = extractGraphFromRefPredicateObjectMap(refPredicateObjectMap);
					if (log.isDebugEnabled()) {
						for (URI graph : refPredicateObjectsGraphs)
							log
									.debug("[R2RMLEngine:extractRDFFromTriplesMap]"
											+ " Resource graph refPredicateObjects extracted : "
											+ graph);
					}
					// Extract ref predicate
					URI refPredicate = extractPredicateFromRefPredicateMap(refPredicateObjectMap
							.getRefPredicateMap());
					if (log.isDebugEnabled())
						log.debug("[R2RMLEngine:extractRDFFromTriplesMap] "
								+ "URI ref predicate extracted : "
								+ refPredicate);
					// Extract ref object
					Value refObject = extractObjectFromRefObjectMap(refPredicateObjectMap
							.getRefObjectMap());
					
					if (refObject == null){
						if (log.isWarnEnabled())
							log
									.warn("R2RMLEngine:extractRDFFromTriplesMap] " +
											"RefObject of triple is null : this triple will be ignored.");
							continue;			
					}
					if (log.isDebugEnabled())
						log.debug("[R2RMLEngine:extractRDFFromTriplesMap] "
								+ "Resource ref object extracted : "
								+ refObject);
					// Construct triple
					if (subjectGraphs.isEmpty()) {
						// No subject graph specified : use default graph
						sesameDataSet.add(subject, refPredicate, refObject);
					} else {
						// Add triple in the specified subject graphs
						for (URI subjectGraph : subjectGraphs) {
							if (subjectGraph.equals(defaultGraph)) {
								// Default graph <=> No context
								sesameDataSet.add(subject, refPredicate,
										refObject);
							} else {
								sesameDataSet.add(subject, refPredicate,
										refObject, subjectGraph);
							}
						}
					}
					// Add triple in the specified predicate object map too
					// TODO : verifier ce comportement
					for (URI refPredicateObjectGraph : refPredicateObjectsGraphs) {
						sesameDataSet.add(subject, refPredicate, refObject,
								refPredicateObjectGraph);
					}
				}
			}
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLEngine:extractRDFFromTriplesMap] "
					+ "Extraction done.");
	}

	/**
	 * Extract datatype from an objectMap. This datatype can be already defined
	 * in object Map or it can be deducted from the database.
	 * 
	 * @param objectMap
	 * @return
	 * @throws SQLException
	 */
	private XSDType extractDatatypeFromObjectMap(ObjectMap objectMap)
			throws SQLException {
		XSDType datatype = objectMap.getDatatype();

		String column = objectMap.getColumn();
		if ((datatype == null) && (column != null)) {
			// The datatype is derived from the column definition of the logical
			// table.

			ResultSetMetaData meta = logicalTable.getMetaData();
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				if (meta.getColumnName(i).equals(column)) {
					String t = meta.getColumnTypeName(i);
					if (SQLToRDFToolkit.isValidSQLDatatype(t))
						datatype = SQLToRDFToolkit.getEquivalentType(t);
					else
						return null;
				}
			}
		}
		if (datatype != null && !datatype.equals(XSDType.STRING))
			return datatype;
		else
			// String is datatype by default
			// TODO : keep datatype instead of return NULL ?
			return null;
	}

	/**
	 * Extract graphs URI from an predicateObjectMap. These graphs can provide
	 * from different sources (rr:graph, rr:graphColumn or rr:graphTemplate).
	 * 
	 * @param predicateObjectMap
	 * @return
	 * @throws SQLException
	 */
	private Set<URI> extractGraphFromPredicateObjectMap(
			PredicateObjectMap predicateObjectMap) throws SQLException {
		HashSet<URI> result = new HashSet<URI>();
		// Check graph properties
		Set<URI> graphs = predicateObjectMap.getGraphs();
		if (graphs != null)
			// Graph already defined by user
			for (URI graph : graphs)
				result.add(graph);

		Set<String> graphColumns = predicateObjectMap.getGraphColumns();
		if (graphColumns != null) {
			// Extract graph from column name
			for (String graph : graphColumns)
				result.add(vf.createURI(logicalTable.getString(graph)));
		}

		Set<String> graphTemplates = predicateObjectMap.getGraphTemplates();
		if (graphTemplates != null) {
			for (String graphTemplate : graphTemplates) {
				// Extract graph from template
				// Replace curly braces contents
				String graphResult = replaceColumnNames(logicalTable,
						graphTemplate);
				result.add(vf.createURI(graphResult));
			}
		}
		return graphs;
	}

	/**
	 * Extract graphs URI from an refPredicateObjectMap. These graphs can
	 * provide from different sources (rr:graph, rr:graphColumn or
	 * rr:graphTemplate).
	 * 
	 * @param predicateObjectMap
	 * @return
	 * @throws SQLException
	 */
	private Set<URI> extractGraphFromRefPredicateObjectMap(
			RefPredicateObjectMap refPredicateObjectMap) throws SQLException {
		HashSet<URI> result = new HashSet<URI>();
		// Check graph properties
		Set<URI> graphs = refPredicateObjectMap.getGraphs();
		if (graphs != null)
			// Graph already defined by user
			for (URI graph : graphs)
				result.add(graph);

		Set<String> graphColumns = refPredicateObjectMap.getGraphColumns();
		if (graphColumns != null) {
			// Extract graph from column name
			for (String graph : graphColumns)
				result.add(vf.createURI(logicalTable.getString(graph)));
		}

		Set<String> graphTemplates = refPredicateObjectMap.getGraphTemplates();
		if (graphTemplates != null) {
			for (String graphTemplate : graphTemplates) {
				// Extract graph from template
				// Replace curly braces contents
				String graphResult = replaceColumnNames(logicalTable,
						graphTemplate);
				result.add(vf.createURI(graphResult));
			}
		}
		return graphs;
	}

	/**
	 * Construct logical table. Note : Run SQL Query against database calling
	 * the method Connection.commit can close the ResultSet objects that have
	 * been created during the current transaction. In some cases, however, this
	 * may not be the desired behavior. The ResultSet property holdability gives
	 * the application control over whether ResultSet objects (cursors) are
	 * closed when commit is called.
	 * 
	 * @param triplesMap
	 * @throws SQLException
	 */
	private ResultSet constructLogicalTable(TriplesMap triplesMap)
			throws SQLException {
		ResultSet rs = null;
		java.sql.Statement s = conn.createStatement(
				ResultSet.HOLD_CURSORS_OVER_COMMIT, ResultSet.CONCUR_READ_ONLY);
		if (triplesMap.getSQLQuery() != null) {
			s.executeQuery(triplesMap.getSQLQuery());
			rs = s.getResultSet();
			if (rs == null)
				throw new IllegalStateException(
						"[R2RMLEngine:extractRDFFromTriplesMap] SQL request "
								+ "failed : logical table is null.");

		} else {
			if (triplesMap.getTableOwner() == null)
				// Extract table owner information
				if (log.isInfoEnabled())
					// TODO : mySQL peut faire ça ????
					log.info("[R2RMLEngine:extractRDFFromTriplesMap]"
							+ " Table owner not specified. "
							+ "Default mode : TODO");
			// Run generic SQL Query against database
			s.executeQuery("SELECT * FROM " + triplesMap.getTableName());
			rs = s.getResultSet();
		}
		// Commit to held logical table (read-only)
		conn.setAutoCommit(false);
		conn.commit();
		return rs;
	}

	/**
	 * Extract graphs URI from an subjectMap. These graphs can provide from
	 * different sources (rr:graph, rr:graphColumn or rr:graphTemplate).
	 * 
	 * @param subjectMap
	 * @return
	 * @throws SQLException
	 */
	private Set<URI> extractGraphFromSubjectMap(ISubjectMap subjectMap)
			throws SQLException {
		HashSet<URI> result = new HashSet<URI>();
		// Check graph properties
		Set<URI> graphs = subjectMap.getGraphs();
		if (graphs != null)
			// Graph already defined by user
			for (URI graph : graphs)
				result.add(graph);

		Set<String> graphColumns = subjectMap.getGraphColumns();
		if (graphColumns != null) {
			// Extract graph from column name
			for (String graph : graphColumns) {
				result.add(vf.createURI(logicalTable.getString(graph)));
			}
		}

		Set<String> graphTemplates = subjectMap.getGraphTemplates();
		if (graphTemplates != null) {
			for (String graphTemplate : graphTemplates) {
				// Extract graph from template
				// Replace curly braces contents
				String graphResult = replaceColumnNames(logicalTable,
						graphTemplate);
				result.add(vf.createURI(graphResult));
			}
		}
		return result;
	}

	/**
	 * Extract resource subject from a subjectMap object. This resource can be
	 * extracted from different sources (rr:subject, rr:column or rr:template).
	 * 
	 * @param logicalTable
	 * @param subjectMap
	 * @return
	 * @throws SQLException
	 */
	private Resource extractSubjectFromSubjectMap(ResultSet logicalTable,
			ISubjectMap subjectMap) throws SQLException {
		// Check properties
		Resource subject = subjectMap.getSubject();
		if (subject != null)
			// Subject already defined by user
			return subject;

		String column = subjectMap.getColumn();
		if ((column != null) && (logicalTable.getString(column) != null)) {
			// Extract subject from column name
			// Check term type
			if (subjectMap.getTermType().equals(TermType.BLANK_NODE))
				return vf.createBNode(logicalTable.getString(column));
			else
				return vf.createURI(logicalTable.getString(column));
		}
		String template = subjectMap.getTemplate();
		String result = template;
		if (template != null) {
			// Extract subject from template
			// Replace curly braces contents
			result = replaceColumnNames(logicalTable, template);
			return vf.createURI(result);
		}
		return null;
	}

	/**
	 * Extract value object from a objectMap object. This resource can be
	 * extracted from different sources (rr:object, rr:column or rr:template).
	 * This object is associated with a datatype. If datatype is null, no type
	 * is associated with object.
	 * 
	 * @param logicalTable
	 * @param subjectMap
	 * @return
	 * @throws SQLException
	 */
	private Value extractObjectFromObjectMap(ObjectMap objectMap,
			XSDType datatype) throws SQLException {
		// Check properties
		Value object = objectMap.getObject();
		if (object != null)
			// Subject already defined by user
			return object;

		String column = objectMap.getColumn();
		String language = objectMap.getLanguage();
		if (column != null) {
			// Extract subject from column name
			// Check term type
			String columnValue = logicalTable.getString(column);
			
			if (objectMap.getTermType().equals(TermType.BLANK_NODE))
				return vf.createBNode(columnValue);
			else if (objectMap.getTermType().equals(TermType.LITERAL)) {

				if ((datatype != null) && XSDType.isDateType(datatype)) {
					// If a xsd date format is required by user the column value
					// have
					// be XSD-valid.
					long timestamp = logicalTable.getTimestamp(column)
							.getTime();
					String timeZone = SQLConnector.getTimeZone(conn);
					columnValue = SQLConnector.dateToISO8601(SQLConnector
							.timestampToDate(timestamp), timeZone);
				}
				if (datatype != null)
					return vf.createLiteral(columnValue, vf.createURI(prefix
							.get("xsd"), datatype.toString()));
				else
					if (language == null)
						return vf.createLiteral(columnValue);
					else 
						return vf.createLiteral(columnValue, language);
			} else
				return vf.createURI(columnValue);
		}
		String template = objectMap.getTemplate();
		String result = template;
		if (template != null) {
			// Extract subject from template
			// Replace curly braces contents
			result = replaceColumnNames(logicalTable, template);
			// TODO : template for Object => URI/Literal par defaut ? Non precise.
			// Ajustement propre a mon proto : prise en compte du rr:termtype
			if (objectMap.getTermType().equals(TermType.BLANK_NODE)) {
				return vf.createBNode(result);
			} else if (objectMap.getTermType().equals(TermType.IRI)){
				return vf.createURI(result);
			} else {
				if (language == null)
					return vf.createLiteral(result);
				else 
					return vf.createLiteral(result, language);
			}
		}
		return null;
	}

	/**
	 * Extract URI predicate from a predicateMap object. This resource can be
	 * extracted from different sources (rr:predicate, rr:column or
	 * rr:template).
	 * 
	 * @param predicateMap
	 * @return
	 * @throws SQLException
	 */
	private URI extractPredicateFromPredicateMap(PredicateMap predicateMap)
			throws SQLException {
		// Check properties
		URI predicate = predicateMap.getPredicate();
		if (predicate != null)
			// Predicate already defined by user
			return predicate;

		String column = predicateMap.getColumn();
		if (column != null) {
			// Extract predicate from column name
			return vf.createURI(logicalTable.getString(column));
		}
		String template = predicateMap.getTemplate();
		String result = template;
		if (template != null) {
			// Extract subject from template
			// Replace curly braces contents
			result = replaceColumnNames(logicalTable, template);
			return vf.createURI(result);
		}
		return null;
	}

	/**
	 * Extract URI ref predicate from a refPredicateMap object. This resource
	 * can be extracted from only one source rr:predicate.
	 * 
	 * @param predicateMap
	 * @return
	 * @throws SQLException
	 */
	private URI extractPredicateFromRefPredicateMap(
			RefPredicateMap refPredicateMap) {
		return refPredicateMap.getPredicate();
	}

	/**
	 * Extract Resource ref object from a refObjectMap object. This method use
	 * the joinCondition to retrieve subject of correct row in parent
	 * triplesMap. TODO : verifier la coherence des cles etrangeres avec le
	 * schema de base de donnees ? Avis : non, les tables logiques sont
	 * decorellees du schema.
	 * 
	 * @param refObjectMap
	 * @return
	 * @throws SQLException
	 */
	private Resource extractObjectFromRefObjectMap(RefObjectMap refObjectMap)
			throws SQLException {
		Resource result = null;
		TriplesMap parentTriplesMap = refObjectMap.getParentTriplesMap();

		ResultSet parentLogicalTable = constructLogicalTable(parentTriplesMap);
		List<String> parentColumnNames = getParentLogicalTableColumnNames(refObjectMap
				.getJoinCondition());
		List<String> childColumnNames = getChildLogicalTableColumnNames(refObjectMap
				.getJoinCondition());
		if (parentColumnNames.size() == 0 || childColumnNames.size() == 0)
			throw new IllegalStateException(
					"[R2RMLEngine:extractObjectFromRefObjectMap] Extraction of "
							+ "parent or child columns failed (empty result).");
		boolean rowFound = false; // Use for check unicity of result
		while (parentLogicalTable.next()) {

			boolean areTheSame = true; //
			// Check column names equality
			for (String parentColumnName : parentColumnNames) {
				for (String childColumnName : childColumnNames) {
					Object parentValue = parentLogicalTable
							.getObject(parentColumnName);
					Object childValue = logicalTable.getObject(childColumnName);
					areTheSame &= (parentValue.equals(childValue));
				}
			}
			if (areTheSame && rowFound) {
				throw new IllegalStateException(
						"[R2RMLEngine:extractObjectFromRefObjectMap] No unicity"
								+ " in results of join condition whereas a foreign key"
								+ " is defined as a pointer to a unqiue row.");
			} else if (areTheSame) {
				result = extractSubjectFromSubjectMap(parentLogicalTable,
						parentTriplesMap.getSubjectMap());
				rowFound = true;
			}
		}

		if (result == null)
			if (log.isWarnEnabled())
				log
						.warn("[R2RMLEngine:extractObjectFromRefObjectMap] No result for"
								+ " the join condition : "
								+ refObjectMap.getJoinCondition());
		return result;
	}

	// TOOLS

	/**
	 * Replace column names in template expressions with appropriates column
	 * values.
	 * 
	 * @param template
	 * @return
	 */
	private String replaceColumnNames(ResultSet logicalTable, String template)
			throws SQLException {
		String result = template;
		String[] opened = template.split("\\{");
		boolean first = true;
		for (String openedContent : opened) {
			if (first) {
				// Not in brace content
				first = false;
				continue;
			}
			String[] closed = openedContent.split("\\}");
			String columnName = closed[0];
			if (columnName == null)
				if (log.isErrorEnabled()) {
					// This error have not to be happened ! It must be
					// caught by the model !
					throw new IllegalStateException(
							"[R2RMLEngine:replaceColumnNames] Invalid R2RML"
									+ " syntax expression : " + result);
				}
			// Get column value
			String replacement = logicalTable.getString(columnName);

			result = result.replace("{" + columnName + "}", replacement);
		}
		return result;
	}

	/**
	 * Extract column names from parent alias from a join condition.
	 * 
	 * @param joinCondition
	 * @return
	 */
	private List<String> getParentLogicalTableColumnNames(String joinCondition) {
		List<String> columnNames = new ArrayList<String>();
		String[] andSplits = joinCondition.split(" AND ");
		String parentAlias = "{" + R2RMLVocabulary.JOIN_CONDITION_PARENT_ALIAS
				+ ".}";
		for (String andSplit : andSplits) {
			String[] equalSplits = andSplit.split("=");
			if (equalSplits[0].contains(parentAlias)) {
				columnNames.add(equalSplits[0].replace(parentAlias, "")
						.replaceAll("\\s", ""));
			} else {
				columnNames.add(equalSplits[1].replace(parentAlias, "")
						.replaceAll("\\s", ""));
			}
		}
		return columnNames;
	}

	/**
	 * Extract column names from children alias from a join condition.
	 * 
	 * @param joinCondition
	 * @return
	 */
	private List<String> getChildLogicalTableColumnNames(String joinCondition) {
		List<String> columnNames = new ArrayList<String>();
		String[] andSplits = joinCondition.split(" AND ");
		String childAlias = "{" + R2RMLVocabulary.JOIN_CONDITION_CHILD_ALIAS
				+ ".}";
		for (String andSplit : andSplits) {
			String[] equalSplits = andSplit.split("=");
			if (equalSplits[0].contains(childAlias)) {
				columnNames.add(equalSplits[0].replace(childAlias, "")
						.replaceAll("\\s", ""));
			} else {
				columnNames.add(equalSplits[1].replace(childAlias, "")
						.replaceAll("\\s", ""));
			}
		}
		return columnNames;
	}

	/**
	 * Print logical content in console. WARNING : this method use the same
	 * logical table object to explore it. The cursor in result set is reinit at
	 * its original position after method call.
	 * 
	 * @param logicalTable
	 * @return
	 * @throws SQLException
	 */
	private String printLogicalTable(ResultSet logicalTable)
			throws SQLException {
		if (logicalTable == null) {
			if (log.isWarnEnabled())
				log.warn("[R2RMLEngine:printLogicalTable] WARNING : a "
						+ "print request has been done against"
						+ " a null logical table.");
			return null;
		}
		int rowNumber = logicalTable.getRow();
		String result = "{[R2RMLEngine:printLogicalTable] (Row numbers : "
				+ rowNumber + ") : ";
		// Init cursor
		logicalTable.beforeFirst();
		int cpt = 1;
		while (logicalTable.next()) {
			result += "ROW " + cpt + System.getProperty("line.separator");
			for (int i = 1; i <= logicalTable.getMetaData().getColumnCount(); i++) {
				result += "\tcolumn name : "
						+ logicalTable.getMetaData().getColumnName(i)
						+ System.getProperty("line.separator");
				result += "\tcolumn value : " + logicalTable.getObject(i)
						+ System.getProperty("line.separator");
			}
			cpt++;
		}
		result += "}";
		// Reset cursor of resultSet
		logicalTable.beforeFirst();
		for (int i = 1; i <= rowNumber; i++)
			logicalTable.next();
		return result;
	}

}
