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
 * SQL model : table
 *
 * Represents table keys of a database according to W3C database model.
 * A table is a set of data elements (values) that is organized using 
 * a model of vertical columns (which are identified by their name) and
 * horizontal rows. A table has a specified number of columns, 
 * but can have any number of rows. Each row is identified by the values 
 * appearing in a particular column subset which has been 
 * identified as a candidate key.
 * 
 * In this model, a table contains :
 * 	1) a Header object
 * 	2) a list of candidate keys
 *  3) a set of foreign keys
 * 	4) a Body object
 * 
 * Reference : Direct Mapping Definition, 
 * A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011 
 *
 * @author jhomo
 *
 */
package antidot.sql.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antidot.sql.model.CandidateKey.KeyType;

/**
 * @author jh
 */
public class Table {

	// Log
	private static Log log = LogFactory.getLog(Table.class);

	/**
	 */
	private String tableName;
	/**
	 */
	private Header header;
	/**
	 */
	private ArrayList<CandidateKey> candidateKeys;
	// private HashSet<ForeignKey> foreignKeys;
	/**
	 */
	private Body body;

	private HashMap<ForeignKey, HashMap<HashSet<String>, HashSet<Row>>> indexedRowsByFk;

	public Table(String tableName, Header header,
			ArrayList<CandidateKey> candidateKeys,
			HashSet<ForeignKey> foreignKeys, Body body) {
		this(tableName);

		setHeader(header);
		setCandidateKeys(candidateKeys);
		setBody(body);
		setForeignKeys(foreignKeys);

		/*
		 * this.foreignKeys = new HashSet<ForeignKey>();
		 * this.foreignKeys.addAll(foreignKeys); this.foreignKeys = foreignKeys;
		 */
	}

	public Table(String tableName) {
		this.tableName = tableName;
		indexedRowsByFk = new HashMap<ForeignKey, HashMap<HashSet<String>, HashSet<Row>>>();
	}

	/**
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return
	 */
	public Header getHeader() {
		return header;
	}

	/**
	 * @param header
	 */
	public void setHeader(Header header) {
		this.header = header;
	}

	/**
	 * @return
	 */
	public ArrayList<CandidateKey> getCandidateKeys() {
		return candidateKeys;
	}

	/**
	 * @param candidateKeys
	 */
	public void setCandidateKeys(ArrayList<CandidateKey> candidateKeys) {
		int pks = 0;
		for (CandidateKey key : candidateKeys) {
			if (key.isType(KeyType.PRIMARY))
				pks++;
			if (pks > 1)
				throw new IllegalStateException(
						"Two primary keys can't be inserted in the same table.");
			for (String columnName : key.getColumnNames()) {
				if (!header.getColumnNames().contains(columnName))
					throw new IllegalStateException(
							"Column name in foreign key are not included in table header  : "
									+ columnName);
			}
		}
		this.candidateKeys = new ArrayList<CandidateKey>();
		this.candidateKeys.addAll(candidateKeys);
	}

	public HashSet<ForeignKey> getForeignKeys() {
		HashSet<ForeignKey> fks = new HashSet<ForeignKey>();
		for (CandidateKey key : candidateKeys) {
			if (key.isType(KeyType.FOREIGN))
				fks.add((ForeignKey) key);
		}
		return fks;
	}

	public void setForeignKeys(HashSet<ForeignKey> foreignKeys) {
		for (CandidateKey key : candidateKeys) {
			if (key.isType(KeyType.FOREIGN))
				candidateKeys.remove(key);
		}
		for (ForeignKey fk : foreignKeys) {
			for (String columnName : fk.getColumnNames()) {
				if (!header.getColumnNames().contains(columnName))
					throw new IllegalStateException(
							"Column name in foreign key are not included in table header  : "
									+ columnName);
			}
		}
		candidateKeys.addAll(foreignKeys);
	}

	public void indexesRows(ForeignKey fk) {
		HashMap<HashSet<String>, HashSet<Row>> indexedRows = new HashMap<HashSet<String>, HashSet<Row>>();
		HashSet<String> columnNames = new HashSet<String>();
		for (Row r : body.getRows()) {
			for (String columnName : fk.getReferenceKey().getColumnNames()) {
				// Save values of columns in the fk for current row
				columnNames.add(r.getValues().get(columnName));
			}
			if (log.isDebugEnabled())
				log.debug("[Table:indexesRows] Row r = " + r
						+ " columnNames = " + columnNames);
			if (indexedRows.get(columnNames) == null) {
				indexedRows.put(new HashSet<String>(columnNames), new HashSet<Row>());
			}
			indexedRows.get(columnNames).add(r);
			columnNames.clear();
		}
		indexedRowsByFk.put(fk, indexedRows);
	}

	public HashSet<Row> getIndexedRow(ForeignKey fk,
			HashSet<String> columnsValues) {
		if (indexedRowsByFk.size() == 0)
			throw new IllegalStateException(
					"[Table:getIndexedRow] A indexed row is required whereas this table has not been indexed.");
		if (indexedRowsByFk.get(fk) == null)
			if (log.isWarnEnabled())
				log
						.warn("[Table:getIndexedRow] No indexed row for this foreign key.");
		if (log.isDebugEnabled())
			log.debug("[Table:getIndexedRow] Table = " + tableName
					+ " indexedRow at this value = " + indexedRowsByFk.get(fk)
					+ " column values = " + columnsValues);
		return indexedRowsByFk.get(fk).get(columnsValues);
	}

	/**
	 * @return
	 */
	public Body getBody() {
		return body;
	}

	/**
	 * @param body
	 */
	public void setBody(Body body) {
		this.body = body;
	}

	/**
	 * Return the unique primary key of this table
	 * 
	 * @return
	 */
	public CandidateKey getPrimaryKey() {
		CandidateKey primaryKey = null;
		for (CandidateKey k : getCandidateKeys()) {
			if (k.getKeyType().equals(KeyType.PRIMARY)) {
				primaryKey = k;
				break;
			}
		}
		return primaryKey;
	}

	/**
	 * Returns names of column which are not targeted by a unary foreign key.
	 * 
	 * @return
	 */
	public HashSet<String> getLexicals() {
		HashSet<String> lexicalColumnNames = new HashSet<String>();
		for (String columnName : getHeader().getColumnNames()) {
			boolean isLexical = true;
			for (ForeignKey fk : getForeignKeys()) {
				isLexical &= !(fk.getColumnNames().contains(columnName) && fk
						.isUnary());
			}
			if (isLexical)
				lexicalColumnNames.add(columnName);
		}
		return lexicalColumnNames;
	}

	public String toString() {
		String result = "{[Table:toString]"
				+ System.getProperty("line.separator") + "\ttableName = ";
		result += tableName + ";" + System.getProperty("line.separator");
		result += "\theader = " + header;
		result += ";" + System.getProperty("line.separator")
				+ "\tcandidateKeys = [";
		for (CandidateKey key : candidateKeys)
			result += key;
		result += "];" + System.getProperty("line.separator")
				+ "\tforeignKeys = [";
		for (ForeignKey key : getForeignKeys())
			result += key;
		result += "];" + System.getProperty("line.separator") + "\tbody = "
				+ body;
		result += ";" + System.getProperty("line.separator")
				+ "\tindexed rows = " + indexedRowsByFk.keySet();
		return result;
	}

}
