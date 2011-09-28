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
 * R2RML Model : TriplesMap class
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antidot.r2rml.exception.InvalidR2RMLStructureException;



/**
 * @author  jh
 */
public class TriplesMap implements ITriplesMap {

	// Log
	private static Log log = LogFactory.getLog(TriplesMap.class);

	/**
	 */
	private String tableName;
	/**
	 */
	private String SQLQuery;
	/**
	 */
	private String tableOwner;
	/**
	 */
	private ISubjectMap subjectMap;
	/**
	 */
	private HashSet<PredicateObjectMap> predicateObjectMaps;
	/**
	 */
	private HashSet<RefPredicateObjectMap> refPredicateObjectMaps;

	public TriplesMap(String tableName, String SQLQuery, String tableOwner,
			ISubjectMap subjectMap,
			HashSet<PredicateObjectMap> predicateObjectMaps,
			HashSet<RefPredicateObjectMap> refPredicateObjectMaps) throws InvalidR2RMLStructureException {
		super();
		this.tableName = tableName;
		setSQLQuery(SQLQuery);
		this.tableOwner = tableOwner;
		this.subjectMap = subjectMap;
		setPredicateObjectMaps(predicateObjectMaps);
		setRefPredicateObjectMaps(refPredicateObjectMaps);
		checkConsistencyOfProperties();
	}

	public TriplesMap() {
		super();
		this.tableName = null;
		this.SQLQuery = null;
		this.tableOwner = null;
		this.subjectMap = null;
		this.predicateObjectMaps = new HashSet<PredicateObjectMap>();
		this.refPredicateObjectMaps = new HashSet<RefPredicateObjectMap>();
	}

	/**
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Set properties of this triplesMap according to the "R2RML: RDB to RDF Mapping Language" document.
	 * A TriplesMap contains the rules for mapping a logical table row to a set of RDF triples. 
	 * It consists of one SubjectMap structure and one or more PredicateObjectMap structure(s).
	 * @param tableName
	 * @param tableOwner
	 * @param SQLQuery
	 * @param subjectMap
	 * @param predicateObjectMaps
	 * @param refPredicateObjectMaps
	 * @throws InvalidR2RMLStructureException 
	 */
	public void setProperties(String tableName, String tableOwner,
			String SQLQuery, ISubjectMap subjectMap,
			HashSet<PredicateObjectMap> predicateObjectMaps,
			HashSet<RefPredicateObjectMap> refPredicateObjectMaps) throws InvalidR2RMLStructureException {
		if (log.isDebugEnabled())
			log.debug("[TriplesMap:setProperties] New properties, tableName = "
					+ tableName + ", tableOwner = " + tableOwner
					+ ", SQLQuery = " + SQLQuery);
		// Checking structure : see 1.4 Mapping overview
		if (subjectMap == null)
			throw new InvalidR2RMLStructureException("[TriplesMap:setProperties] subjectMap have to be specified.");
		if (predicateObjectMaps.size() == 0 && refPredicateObjectMaps.size() == 0)
			throw new InvalidR2RMLStructureException("[TriplesMap:setProperties] One or more predicateObjectMap/refPredicateObjectMap have to be specified.");
		this.tableName = tableName;
		this.tableOwner = tableOwner;
		setSQLQuery(SQLQuery);
		this.subjectMap = subjectMap;
		setPredicateObjectMaps(predicateObjectMaps);
		setRefPredicateObjectMaps(refPredicateObjectMaps);
		checkConsistencyOfProperties();
	}

	/**
	 * @return
	 */
	public String getSQLQuery() {
		return SQLQuery;
	}

	/**
	 * @return
	 */
	public String getTableOwner() {
		return tableOwner;
	}

	/**
	 * @return
	 */
	public ISubjectMap getSubjectMap() {
		return subjectMap;
	}

	/**
	 * @return
	 */
	public HashSet<PredicateObjectMap> getPredicateObjectMaps() {
		return predicateObjectMaps;
	}

	/**
	 * @param predicateObjectMaps
	 */
	private void setPredicateObjectMaps(
			HashSet<PredicateObjectMap> predicateObjectMaps) {
		this.predicateObjectMaps = new HashSet<PredicateObjectMap>();
		this.predicateObjectMaps.addAll(predicateObjectMaps);
	}

	/**
	 * @return
	 */
	public HashSet<RefPredicateObjectMap> getRefPredicateObjectMaps() {
		return refPredicateObjectMaps;
	}

	/**
	 * @param SQLQuery
	 */
	private void setSQLQuery(String SQLQuery) {
		if (SQLQuery == null)
			this.SQLQuery = null;
		else
			// Convert into a valid SQL query
			this.SQLQuery = SQLQuery.replaceAll("\\s+", " ").replaceAll(
					"\\s\\,", ",").trim();
	}

	/**
	 * @param refPredicateObjectMaps
	 */
	private void setRefPredicateObjectMaps(
			HashSet<RefPredicateObjectMap> refPredicateObjectMaps) {
		this.refPredicateObjectMaps = new HashSet<RefPredicateObjectMap>();
		this.refPredicateObjectMaps.addAll(refPredicateObjectMaps);
	}

	/**
	 * According to the "R2RML: RDB to RDF Mapping Language" document :
	 * Each TriplesMap contains a reference to a logical table in the input database. 
	 * A logical table can be one of the following :
	 * - A base table that exists in the input SQL schema.
	 * - A view that exists in the input SQL schema.
	 * - A valid SQL query against the input schema.
	 * @throws InvalidR2RMLStructureException 
	 */
	private void checkConsistencyOfProperties() throws InvalidR2RMLStructureException {
		// Check properties consistency
		if ((tableName == null) && (SQLQuery == null)) {
			// This may be done either using rr:tableOwner and rr:tableName
			// to specify the owner and the name of a table or view, or
			// using rr:SQLQuery to specify a valid SQL query
			throw new InvalidR2RMLStructureException(
					"[TriplesMap:checkConsistencyOfProperties] tableName or SQLQuery have to be specified.");
		} else if ((tableName != null) && (SQLQuery != null)) {
			throw new InvalidR2RMLStructureException(
					"[TriplesMap:checkConsistencyOfProperties] Ambiguity between tableName and SQLQuery : the two "
							+ "are specified but just one is required.");
		}
		if ((tableName != null) && (tableOwner == null)) {
			// When using rr:tableName, use of rr:tableOwner is optional. If
			// rr:tableOwner is not specified in a mapping, the table owner
			// information for the table will be derived based upon the
			// database connection at the time of processing of the mapping
			// specification.
			if (log.isInfoEnabled())
				log
						.info("[TriplesMap:TriplesMap] ownerTable "
								+ "is not specified : it will be extracted from database during query processing time.");
		}
	}
	

	public String toString() {
		String result = "{[TriplesMap:toString] tableName = " + tableName
				+ ", tableOwner = " + tableOwner + ", SQLQuery = " + SQLQuery
				+ ", subjectMap =  " + subjectMap
				+ ", number of predicateObjectMaps : "
				+ predicateObjectMaps.size()
				+ ", number of refPredicateObjectMaps : "
				+ refPredicateObjectMaps.size() + "}";
		return result;
	}

}
