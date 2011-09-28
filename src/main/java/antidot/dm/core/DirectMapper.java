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
 * Direct Mapper
 *
 * Direct Mapper convert a MySQL database into a RDF graph.
 *
 * @author jhomo
 *
 */
package antidot.dm.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import antidot.rdf.impl.sesame.SemiStatement;
import antidot.rdf.impl.sesame.SesameDataSet;
import antidot.rdf.tools.RDFToolkit;
import antidot.rdf.tools.SQLToRDFToolkit;
import antidot.sql.model.CandidateKey;
import antidot.sql.model.Database;
import antidot.sql.model.ForeignKey;
import antidot.sql.model.Header;
import antidot.sql.model.Row;
import antidot.sql.model.Table;
import antidot.sql.type.SQLType;
import antidot.xmls.type.XSDType;

/**
 * @author jh
 */
public class DirectMapper {

	// Log
	private static Log log = LogFactory.getLog(DirectMapper.class);

	// Namespaces
	public static HashMap<String, String> prefix = new HashMap<String, String>();

	// Attributes
	// Sesame valueFactory which generates values like litterals or URI
	private ValueFactory vf;
	// Map which store blankNodes associated with a converted subject of a Row
	private HashMap<Row, BNode> blankNodes;
	// Map which store URIs associated with a converted subject of a Row
	private HashMap<Row, URI> URIs;
	// Number of broken references
	/**
	 */
	private int nbBrokenReferences;

	private Long start = 0l;

	// Load prefix
	static {
		// prefix.put("foaf", "http://xmlns.com/foaf/0.1/");
		prefix.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefix.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		// prefix.put("rr", "http://www.w3.org/ns/r2rml#");
		prefix.put("xsd", "http://www.w3.org/2001/XMLSchema#");
		// prefix.put("fn", "http://www.w3.org/2005/xpath-functions");
	}

	private DirectMapper() {
		vf = new ValueFactoryImpl();
		blankNodes = new HashMap<Row, BNode>();
		URIs = new HashMap<Row, URI>();
		nbBrokenReferences = 0;

	}

	/**
	 * Convert a database model with a base URI into a rdf Graph.
	 * 
	 * @param db
	 * @param baseURI
	 * @throws UnsupportedEncodingException
	 */
	public static SesameDataSet generateDirectMapping(Database db,
			String baseURI) throws UnsupportedEncodingException {
		return generateDirectMapping(db, baseURI, null);
	}

	/**
	 * Convert a database model with a base URI into a rdf Graph (with native
	 * storage).
	 * 
	 * @param db
	 * @param baseURI
	 * @param useNativeStore
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static SesameDataSet generateDirectMapping(Database db,
			String baseURI, String fileToNativeStore)
			throws UnsupportedEncodingException {

		if (log.isDebugEnabled())
			log.debug("[DirectMapper:generateDirectMapping] db : " + db
					+ ", base URI : " + baseURI);
		SesameDataSet g = null;
		// Check if use of native store is required
		if (fileToNativeStore != null) {
			g = new SesameDataSet(fileToNativeStore, false);
		} else {
			g = new SesameDataSet();
		}

		DirectMapper dm = new DirectMapper();
		HashSet<Statement> triples = dm.convertDatabase(db, baseURI);
		for (Statement triple : triples) {
			if (log.isDebugEnabled())
				log
						.debug("[DirectMapper:generateDirectMapping] Triple generated : "
								+ triple);
			g.add(triple.getSubject(), triple.getPredicate(), triple
					.getObject());
		}

		if (log.isInfoEnabled())
			log
					.info("[DirectMapper:generateDirectMapping] Number of generated triples : "
							+ g.tuplePattern(null, null, null).size() + ".");
		if (log.isInfoEnabled())
			log
					.info("[DirectMapper:generateDirectMapping] Number of broken references : "
							+ dm.getNbBrokenReferences() + ".");
		return g;
	}

	/**
	 * Most of the functions defining the Direct Mapping are higher-order
	 * functions parameterized by a function φ (phi) : Row → Node. This function
	 * maps any row to a unique node IRI or Blank Node [35]/[36].
	 */
	private Resource phi(Table t, Row r, String baseURI) {
		if (log.isDebugEnabled())
			log.debug("[DirectMapper:phi] Table : " + t);
		CandidateKey primaryKey = t.getPrimaryKey();
		if (primaryKey != null) {
			// Unique Node IRI
			String stringURI = t.getTableName() + "/";
			int i = 0;
			for (String columnName : primaryKey.getColumnNames()) {
				i++;
				stringURI += columnName + "=" + r.getValues().get(columnName);
				if (i < primaryKey.getColumnNames().size())
					stringURI += ",";
			}
			if (URIs.containsKey(r))
				return URIs.get(r); // Use blankNode map
			// Check URI syntax
			if (!RDFToolkit.validURI(baseURI + stringURI)) {
				if (log.isWarnEnabled())
					log.warn("[DirectMapper:phi] This URI is not valid : "
							+ baseURI + stringURI);
			}
			URI uri = vf.createURI(baseURI, stringURI);
			URIs.put(r, uri);
			return uri;
		} else {
			// Blank node
			if (blankNodes.containsKey(r))
				return blankNodes.get(r); // Use blankNode map
			BNode bnode = vf.createBNode();
			blankNodes.put(r, bnode);
			return bnode;
		}
	}

	/**
	 * Denotational semantics function [37]/[38] : convert database into
	 * triples.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private HashSet<Statement> convertDatabase(Database db, String baseURI)
			throws UnsupportedEncodingException {
		// Init time
		start = System.currentTimeMillis();
		if (log.isDebugEnabled())
			log.debug("[DirectMapper:convertDatabase]");
		HashSet<Statement> result = new HashSet<Statement>();
		int nbTables = db.getTables().size();
		int cptTable = 1;
		for (Table t : db.getTables()) {
			if (log.isInfoEnabled())
				log.info("[DirectMapper:convertDatabase] ----- Table "
						+ cptTable + " / " + nbTables + " : "
						+ t.getTableName() + " ------ ");
			cptTable++;
			result.addAll(convertTable(db, t, baseURI));
		}
		Float stop = Float.valueOf(System.currentTimeMillis() - start) / 1000;
		if (log.isInfoEnabled())
			log.info("[DirectMapper:convertDatabase] Database extracted in "
					+ stop + " seconds.");
		return result;
	}

	/**
	 * Denotational semantics function [39]/[40] : convert table into triples.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private HashSet<Statement> convertTable(Database db, Table t, String baseURI)
			throws UnsupportedEncodingException {
		if (log.isDebugEnabled())
			log.debug("[DirectMapper:convertTable] Table : " + t);
		// Check BLOB types
		for (String column : t.getHeader().getDatatypes().keySet()) {
			SQLType.MySQLType type = SQLType.MySQLType.toMySQLType(t
					.getHeader().getDatatypes().get(column));
			if (type.isBlobType())
				if (log.isWarnEnabled())
					log
							.warn("[DirectMapper:convertTable] WARNING Table "
									+ t.getTableName()
									+ ", column "
									+ column
									+ " Forbidden BLOB type (binary stream not supported in XSD)"
									+ " => this column will be ignored.");
		}
		HashSet<Statement> result = new HashSet<Statement>();
		for (Row r : t.getBody().getRows()) {
			result.addAll(convertRow(db, r, baseURI));
		}
		return result;
	}

	/**
	 * Denotational semantics function [41]/[42] : convert row into triples.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private HashSet<Statement> convertRow(Database db, Row r, String baseURI)
			throws UnsupportedEncodingException {
		if (log.isDebugEnabled())
			log.debug("[DirectMapper:convertRow] Table : " + r);
		HashSet<Statement> result = new HashSet<Statement>();
		Table currentTable = db.getTable(r.getParentTableName());
		Resource s = phi(currentTable, r, baseURI);
		// Temporary set of triple used to store triples
		// before attribute them their subject
		// It's necessary for manage the Primary-is-Candidate-Key Exception
		// which can modify subject
		HashSet<SemiStatement> tmpResult = new HashSet<SemiStatement>();
		// Reference Triples : See [43]/[44]
		for (ForeignKey fk : currentTable.getForeignKeys()) {
			CandidateKey pk = currentTable.getPrimaryKey();
			if ((pk != null) && pk.matchSameColumns(fk)) {
				// Primary-is-Candidate-Key Exception
				// See 2.2.1 IRIs generated for the initial example
				// TODO : surveiller l'evolution de cette proposition dans le
				// draft
				HashSet<Statement> primaryIsCandidateKeyTriples = convertPrimaryIsCandidateKey(
						db, r, fk, baseURI);
				// Add these triples to result
				boolean firstTriple = true;
				for (Statement primaryIsCandidateKeyTriple : primaryIsCandidateKeyTriples) {
					// Modify subject
					if (firstTriple) {
						s = primaryIsCandidateKeyTriple.getSubject();
						firstTriple = false;
					}
					result.add(primaryIsCandidateKeyTriple);
				}
			} else {
				HashSet<SemiStatement> refSemiTriples = convertRef(db, r, fk,
						baseURI);
				for (SemiStatement refSemiTriple : refSemiTriples) {
					// Add these triples to temporary result (their subject is
					// null)
					if (log.isDebugEnabled())
						log
								.debug("[DirectMapper:convertRow] Ref semi triple extracted : "
										+ refSemiTriple);
					tmpResult.add(refSemiTriple);
				}
			}

		}
		// Construct ref triples with correct subject
		for (SemiStatement refSemiTriple : tmpResult) {
			Statement triple = vf.createStatement(s, refSemiTriple
					.getPredicate(), refSemiTriple.getObject());
			if (triple != null)
				result.add(triple);
		}
		// Literal Triples : See [45]/[46]
		for (String columnName : currentTable.getLexicals()) {
			Statement triple = convertLex(currentTable.getHeader(), r,
					columnName, baseURI);
			if (triple != null) {
				result.add(vf.createStatement(s, triple.getPredicate(), triple
						.getObject()));
			}
		}
		// Table Triples
		// TODO : check namespaces
		// URI typePredicate = vf.createURI("rdf:type");
		URI typePredicate = vf.createURI(prefix.get("rdf"), "type");
		// TODO : check if litteral or not
		// Literal typeObject = vf.createLiteral(currentTable.getTableName());

		URI typeObject = vf.createURI(baseURI, currentTable.getTableName());
		Statement typeTriple = vf.createStatement(s, typePredicate, typeObject);
		result.add(typeTriple);
		return result;
	}

	/**
	 * Primary-is-Candidate-Key Exception If the primary key is also a candidate
	 * key K to table R : - The shared subject is the subject of the referenced
	 * row in R. - The foreign key K generates no reference triple. - Even if K
	 * is a single-column foreign key, it generates a literal triple.
	 * 
	 * @param db
	 * @param r
	 * @param fk
	 * @param baseURI
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private HashSet<Statement> convertPrimaryIsCandidateKey(Database db, Row r,
			ForeignKey fk, String baseURI) throws UnsupportedEncodingException {
		HashSet<Statement> result = new HashSet<Statement>();
		// Generate URI subject

		Row targetRow = r;
		ForeignKey currentFk = fk;
		boolean primaryIsCandidateKey = true;
		Table targetTable = null;
		while (primaryIsCandidateKey) {

			primaryIsCandidateKey = false;
			// Search targetRow
			HashSet<Row> targetRows = db.dereference(targetRow, currentFk);
			if (targetRows.size() == 0) {
				nbBrokenReferences++;
				if (log.isWarnEnabled())
					log
							.warn("[DirectMapper:convertPrimaryIsCandidateKey] Broken reference from r : "
									+ r
									+ " with foreignKey : "
									+ currentFk
									+ " to targetRow : " + targetRow);
				return result;
			}
			// Unique by definition
			targetRow = targetRows.iterator().next();

			targetTable = db.getTable(targetRow.getParentTableName());
			for (ForeignKey targetFk : targetTable.getForeignKeys()) {
				CandidateKey currentPk = targetTable.getPrimaryKey();
				if (targetFk.matchSameColumns(currentPk)) {
					// This is not the top of hierarchy : get the reference
					// table again
					primaryIsCandidateKey = true;
					currentFk = targetFk;
				}
			}
		}
		Resource s = phi(targetTable, targetRow, baseURI);
		// Generate predicates and objects
		for (String columnName : fk.getColumnNames()) {
			// For each column in candidate key, a literal triple is generated
			Statement t = convertLex(db.getTable(r.getParentTableName())
					.getHeader(), r, columnName, baseURI);
			result.add(vf.createStatement(s, t.getPredicate(), t.getObject()));
		}
		return result;
	}

	/**
	 * @return
	 */
	public int getNbBrokenReferences() {
		return nbBrokenReferences;
	}

	/**
	 * Denotational semantics function [43]/[44] : convert foreign key columns
	 * into a triple with mapped (predicate, object).
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private HashSet<SemiStatement> convertRef(Database db, Row r,
			ForeignKey fk, String baseURI) throws UnsupportedEncodingException {
		HashSet<SemiStatement> result = new HashSet<SemiStatement>();
		ArrayList<String> columnNames = new ArrayList<String>();
		columnNames.addAll(fk.getColumnNames());
		URI p = convertCol(r, columnNames, baseURI);
		HashSet<Row> targetRows = db.dereference(r, fk);
		for (Row targetRow : targetRows) {
			if (log.isDebugEnabled())
				log
						.debug("[DirectMapper:convertRef] **** Dereference targetRow : "
								+ targetRow);
			// Get URI of target table
			Resource o = phi(db.getTable(targetRow.getParentTableName()),
					targetRow, baseURI);
			SemiStatement semiTriple = new SemiStatement(p, o);
			result.add(semiTriple);
		}
		return result;
	}

	/**
	 * Denotational semantics function [45]/[46] : convert lexical columns into
	 * a triple with mapped (predicate, object).
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private Statement convertLex(Header header, Row r, String columnName,
			String baseURI) throws UnsupportedEncodingException {
		if (log.isDebugEnabled())
			log.debug("[DirectMapper:convertLex] Table "
					+ r.getParentTableName() + ", column : " + columnName);
		Statement result = null;
		Literal l = null;
		ArrayList<String> columnNames = new ArrayList<String>();
		columnNames.add(columnName);
		URI p = convertCol(r, columnNames, baseURI);
		String v = r.getValues().get(columnName);
		String d = header.getDatatypes().get(columnName);
		if (v == null || v.equals("null")) {
			// Don't keep triple with null value
			return null;
		}
		XSDType type = null;
		SQLType.MySQLType mySQLType = SQLType.MySQLType.toMySQLType(d);
		if (mySQLType.isBlobType()) {
			if (log.isDebugEnabled())
				log
						.debug("[DirectMapper:convertLex] Table "
								+ r.getParentTableName()
								+ ", column "
								+ columnName
								+ " Forbidden BLOB type (binary stream not supported in XSD)"
								+ " => this triple will be ignored.");
			return null;
		} else {
			type = SQLToRDFToolkit.getEquivalentType(d);
			if (type == null)
				throw new IllegalStateException(
						"[DirectMapper:convertLex] Unknown XSD equivalent type of : "
								+ SQLType.MySQLType.toMySQLType(d)
								+ " in column : " + columnName + " in table : "
								+ r.getParentTableName());
		}
		if (type.toString().equals(XSDType.STRING.toString())) {
			l = vf.createLiteral(v);
		} else {

			URI datatype_iri = convertDatatype(d);
			if (datatype_iri == null) {
				l = vf.createLiteral(v);
			} else {
				l = vf.createLiteral(v, datatype_iri);
			}
		}
		result = vf.createStatement(null, p, (Value) l);
		return result;
	}

	/**
	 * Denotational semantics function [47]/[48] : convert row and columnNames
	 * into predicate URI.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private URI convertCol(Row row, ArrayList<String> columnNames,
			String baseURI) throws UnsupportedEncodingException {
		String label = URLEncoder.encode(row.getParentTableName(), "UTF-8")
				+ "#";
		int i = 0;
		for (String columnName : columnNames) {
			i++;
			label += URLEncoder.encode(columnName, "UTF-8");
			if (i < columnNames.size())
				label += ",";
		}
		// Check URI syntax
		if (!RDFToolkit.validURI(baseURI + label))
			if (log.isWarnEnabled())
				log.warn("[DirectMapper:convertCol] This URI is not valid : "
						+ baseURI + label);
		// Create value factory which build URI
		return vf.createURI(baseURI, label);
	}

	/**
	 * Denotational semantics function [49]/[50] : convert datatype from SQL
	 * into xsd datatype.
	 */
	private URI convertDatatype(String datatype) {
		String upDatatype = datatype.toUpperCase();
		if (!SQLToRDFToolkit.isValidSQLDatatype(upDatatype))
			if (log.isDebugEnabled())
				log.debug("[DirectMapper:convertDatatype] Unknown datatype : "
						+ datatype);
		String xsdDatatype = SQLToRDFToolkit.getEquivalentType(upDatatype)
				.toString();
		/*
		 * if (!equivalentTypes.keySet().contains(upDatatype)) if
		 * (log.isDebugEnabled())
		 * log.debug("[DirectMapper:convertDatatype] Unsupported datatype : " +
		 * datatype);
		 */
		return vf.createURI(prefix.get("xsd"), xsdDatatype);
	}

}
