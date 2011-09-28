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
 * SQL model : database
 *
 * Represents a database according to W3C database model.
 * 
 * Reference : Direct Mapping Definition, 
 * A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011 
 *
 * @author jhomo
 *
 */
package antidot.sql.model;

import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jh
 */
public class Database {

	// Log
	private static Log log = LogFactory.getLog(Database.class);

	/**
	 */
	public HashSet<Table> tables;

	public Database(HashSet<Table> tables) {
		this.tables = tables;
		indexesTables();
	}

	private void indexesTables() {
		for (Table sourceTable : tables) {
			for (ForeignKey fk : sourceTable.getForeignKeys()) {
				getTable(fk.getTargetTableName()).indexesRows(fk);
			}
		}
	}

	/**
	 * @return
	 */
	public HashSet<Table> getTables() {
		return tables;
	}

	/**
	 * @param tables
	 */
	public void setTables(HashSet<Table> tables) {
		this.tables = tables;
	}

	public Table getTable(String tableName) {
		for (Table table : tables) {
			if (table.getTableName().equals(tableName))
				return table;
		}
		return null;
	}

	/**
	 * RDB accessor function [23] : returns rows which match with columns of row
	 * r for a foreign key fk.
	 * 
	 * @param r
	 * @param fk
	 * @return
	 */
	public HashSet<Row> dereference(Row r, ForeignKey fk) {
		if (log.isDebugEnabled())
			log.debug("[Database:dereference] Source row = " + r + " fk = "
					+ fk);
		Table rowTable = getTable(r.getParentTableName());
		// Test if fk exists
		if (!rowTable.getForeignKeys().contains(fk))
			throw new IllegalStateException(
					"[Database:dereference] Unconsistency : source column and target ."
							+ "column are different : maybe columns of foreign key are not in the same order.");
		HashSet<Row> result = new HashSet<Row>();
		Table targetTable = getTable(fk.getTargetTableName());
		if (targetTable == null)
			throw new IllegalStateException(
					"[Database:dereference] Reference table doesn't exist.");

		// Get values from source table of fk columns
		HashSet<String> columnValues = new HashSet<String>();

		for (String columnName : fk.getColumnNames()) {
			columnValues.add(r.getValues().get(columnName));
			if (log.isDebugEnabled())
				log.debug("[Database:dereference] Search value = "
						+ r.getValues().get(columnName) + " (" + r.getValues()
						+ ")...");
		}
		if (log.isDebugEnabled())
			log.debug("[Database:dereference] ...in column names = "
					+ fk.getReferenceKey().getColumnNames());
		HashSet<Row> targetRows = targetTable.getIndexedRow(fk, columnValues);
		if (targetRows != null)
			for (Row targetRow : targetRows) {
				if (log.isDebugEnabled())
					log.debug("[Database:dereference] Find target row ! => "
							+ targetRow);
				result.add(targetRow);
			}

		// for (Row targetRow : targetTable.getBody().getRows()) {
		// log.debug("[Database:dereference] Target row = " + targetRow);
		// boolean sameValues = true;
		// for (int i = 0; i < fk.getReferenceKey().getColumnNames().size();
		// i++) {
		// String sourceColumnName = fk.getColumnNames().get(i);
		// String targetColumnName = fk.getReferenceKey().getColumnNames()
		// .get(i);
		// log.debug("[Database:dereference] Source column name = "
		// + sourceColumnName);
		// log.debug("[Database:dereference] Target column name = "
		// + targetColumnName);
		//		
		// String targetValue = targetRow.getValues()
		// .get(targetColumnName);
		// // ...with source value
		// sameValues &= (r.getValues().get(sourceColumnName) != null)
		// && (r.getValues().get(sourceColumnName)
		// .equals(targetValue));
		// log.debug("[Database:dereference] Source value = "
		// + r.getValues().get(sourceColumnName));
		// log.debug("[Database:dereference] Target value = "
		// + targetValue);
		//		
		// }
		// if (sameValues) {
		// log.debug("[Database:dereference] => MATCH !");
		// result.add(targetRow);
		// }
		// }

		return result;
	}

	public String toString() {
		String result = "{[Row:toString] tables = ";
		int i = 0;
		for (Table table : tables) {
			i++;
			result += table;
			if (i < tables.size())
				result += "," + System.getProperty("line.separator");
		}
		result += "}";
		return result;
	}

}
