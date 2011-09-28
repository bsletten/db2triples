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
 * SQL model : body
 *
 * Represents candidate keys of a database according to W3C database model.
 * A candidate key of a relation is a minimal superkey for that relation;
 * that is, a set of attributes such that :
 * 		1) the relation does not have two distinct tuples (i.e. rows or
 * 		   records in common database language) with the same values for these
 * 		   attributes (which means that the set of attributes is a superkey)
 * 		
 * 		2) there is no proper subset of these attributes for which (1)
 * 		   holds (which means that the set is minimal).
 * 
 * Reference : Direct Mapping Definition, 
 * A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011 
 *
 * @author jhomo
 *
 */
package antidot.sql.model;

import java.util.ArrayList;

/**
 * @author   jh
 */
public class CandidateKey {

	/**
	 * Describe type of key. For instance, a key can be primary (unique for a table and allow to identify a row)  or foreign (allow to specify an entity of another table)
	 */
	public enum KeyType {
		/**
		 */
		PRIMARY, /**
		 */
		FOREIGN;
	}

	/**
	 */
	private ArrayList<String> columnNames;
	/**
	 */
	private String sourceTable;
	/**
	 */
	private KeyType type;

	public CandidateKey(ArrayList<String> columnNames) {
		if (columnNames == null)
			throw new IllegalStateException(
					"[CandidateKey:construct] ColumNames don't exist.");
		this.columnNames = new ArrayList<String>();
		this.columnNames.addAll(columnNames);
		this.sourceTable = null;
		this.type = null;
	}

	public CandidateKey(ArrayList<String> columnNames, KeyType type) {
		this(columnNames);
		this.type = type;
	}

	public CandidateKey(ArrayList<String> columnNames, String sourceTable) {
		this(columnNames);
		this.sourceTable = sourceTable;
	}

	public CandidateKey(ArrayList<String> columnNames, String sourceTable,
			KeyType type) {
		this(columnNames, sourceTable);
		this.type = type;
	}

	/**
	 * Returns table name which contains this key.
	 * @return
	 */
	public String getSourceTable() {
		return sourceTable;
	}

	/**
	 * Returns type of this key.
	 * @return
	 */
	public KeyType getKeyType() {
		return type;
	}

	/**
	 * @param  sourceTable
	 */
	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	public String toString() {
		String result = "{[CandidateKey:toString]";
		result += " columnNames = " + columnNames + "; sourceTable = "
				+ sourceTable + "; type = " + type +"}";
		return result;
	}

	public boolean isType(KeyType type) {
		return (this.type == type);
	}
	
	public boolean matchSameColumns(CandidateKey key){
		boolean result = columnNames.size() == key.getColumnNames().size();
		for (String columnName : key.getColumnNames()){
			result &= columnNames.contains(columnName);
		}
		return result;
	}

}
