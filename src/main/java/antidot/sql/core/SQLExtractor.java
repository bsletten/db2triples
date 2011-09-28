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
 * SQL Extractor
 *
 * Functions to extract a Database model from a SQL database.
 *
 * @author jhomo
 *
 */
package antidot.sql.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antidot.sql.model.Body;
import antidot.sql.model.CandidateKey;
import antidot.sql.model.Database;
import antidot.sql.model.ForeignKey;
import antidot.sql.model.Header;
import antidot.sql.model.Row;
import antidot.sql.model.Table;
import antidot.sql.type.SQLType;


public class SQLExtractor {
	
	// Log
	private static Log log = LogFactory.getLog(SQLConnector.class);
	
	/**
	 * Extract a Database model from a MySQL database. An optionnal timezone can
	 * be mentionned to improve accurate of time data.
	 * 
	 * @param conn
	 * @param tz
	 * @return
	 */
	static public Database extractMySQLDatabase(Connection conn, TimeZone tz) {
		Long start = System.currentTimeMillis();
		
		String currentTimeZone = SQLConnector.timeZoneToStr(tz);
		Database db = null;
		try {
			// Check timezone
			if (currentTimeZone == null) {
				if (log.isWarnEnabled()) log
						.warn("[SQLConnector:extractMySQLDatabase] No time zone specified. Use database time zone by default.");
				currentTimeZone = SQLConnector.getTimeZone(conn);
			}
			// Convert into RDB Abstract Model
			// Extract metadata from DB
			DatabaseMetaData meta = conn.getMetaData();
			// Generates tables
			HashSet<Table> tables = new HashSet<Table>();
			ResultSet tablesSet = meta.getTables(conn.getCatalog(), null, "%",
					null);
			while (tablesSet.next()) {
				// Extract meta-data of table
				// Extract table name
				String tableName = tablesSet.getString("TABLE_NAME");

				// Extract header
				ResultSet columnsSet = meta.getColumns(null, null, tableName,
						null);
				LinkedHashMap<String, String> datatypes = new LinkedHashMap<String, String>();
				while (columnsSet.next()) {
					// Get datatypes
					datatypes.put(columnsSet.getString("COLUMN_NAME"),
							columnsSet.getString("TYPE_NAME"));
				}
				Header header = new Header(datatypes);
				// Extract candidate keys
				ArrayList<CandidateKey> candidateKeys = new ArrayList<CandidateKey>();
				// In particular : primary key
				ResultSet primaryKeySet = meta.getPrimaryKeys(
						conn.getCatalog(), null, tableName);
				ArrayList<String> columnNames = new ArrayList<String>();
				int size = 0;
				while (primaryKeySet.next()) {
					size++;
					String columnName = primaryKeySet.getString("COLUMN_NAME");
					columnNames.add(columnName);

				}
				ArrayList<String> sortedColumnNames = new ArrayList<String>();
				for (String columnName : header.getColumnNames()) {
					if (columnNames.contains(columnName))
						sortedColumnNames.add(columnName);
				}
				if (size != 0) {
					CandidateKey primaryKey = new CandidateKey(
							sortedColumnNames, tableName,
							CandidateKey.KeyType.PRIMARY);
					candidateKeys.add(primaryKey);
				}

				// Extract foreign key
				HashSet<ForeignKey> foreignKeys = new HashSet<ForeignKey>();
				ResultSet importedKeySet = meta.getImportedKeys(conn
						.getCatalog(), null, tableName);
				String currentPkTableName = null;
				ArrayList<String> pkColumnNames = new ArrayList<String>();
				ArrayList<String> fkColumnNames = new ArrayList<String>();
				while (importedKeySet.next()) {
					String pkTableName = importedKeySet
							.getString("PKTABLE_NAME");
					String pkColumnName = importedKeySet
							.getString("PKCOLUMN_NAME");
					String fkTableName = importedKeySet
							.getString("FKTABLE_NAME");
					String fkColumnName = importedKeySet
							.getString("FKCOLUMN_NAME");
					int fkSequence = importedKeySet.getInt("KEY_SEQ");

					// Consistency test
					if (!fkTableName.equals(tableName))
						throw new IllegalStateException(
								"[SQLConnection:extractDatabase] Unconsistency between source "
										+ "table of foreign key and current table : "
										+ tableName + " != " + fkTableName);

					if (fkSequence == 1) { // Sequence == order of column in
						// multi-column foreign key
						// New foreign key => store last key
						if (fkColumnNames.size() != 0)
							foreignKeys.add(new ForeignKey(fkColumnNames,
									tableName, new CandidateKey(pkColumnNames,
											currentPkTableName)));
						// TODO : check if this value is the same for another
						// SGBD than MySQL
						fkColumnNames = new ArrayList<String>();
						pkColumnNames = new ArrayList<String>();
					}
					currentPkTableName = pkTableName;
					pkColumnNames.add(pkColumnName);
					fkColumnNames.add(fkColumnName);
				}
				// Store last key
				if (fkColumnNames.size() != 0)
					foreignKeys
							.add(new ForeignKey(fkColumnNames, tableName,
									new CandidateKey(pkColumnNames,
											currentPkTableName)));
				// Rows generation
				HashSet<Row> rows = new HashSet<Row>();
				Statement s = conn.createStatement();
				// Construct SQL query
				String SQLQuery = "SELECT ";
				int i = 0;
				for (String columnName : header.getColumnNames()) {
					i++;
					// Extract MySQL date format in a ISO 8601 format
					SQLType.MySQLType type = SQLType.MySQLType
							.toMySQLType(header.getDatatypes().get(columnName));
					if (type == null) {
						throw new IllegalStateException(
								"[SQLConnector:extractMySQLDatabase] Unknown MySQL type : "
										+ header.getDatatypes().get(columnName)
										+ " from column : " + columnName);
					}
					if (type.isDateType()) {
						SQLQuery += "UNIX_TIMESTAMP(`" + columnName + "`)";
					} else {
						SQLQuery += "`" + columnName + "`";
					}
					if (i < header.getColumnNames().size())
						SQLQuery += ", ";
				}
				SQLQuery += " FROM " + tableName + ";";
				if (log.isDebugEnabled()) log
						.debug("[SQLConnection:extractDatabase] SQL generated request : "
								+ SQLQuery);
				ResultSet rs = s.executeQuery(SQLQuery);
				// Extract values in database
				while (rs.next()) {
					TreeMap<String, String> values = new TreeMap<String, String>();
					for (String columnName : header.getColumnNames()) {
						String value = null;
						SQLType.MySQLType type = SQLType.MySQLType
								.toMySQLType(header.getDatatypes().get(
										columnName));
						if (type.isDateType()) {
							SQLType.MySQLType.toMySQLType(header.getDatatypes()
									.get(columnName));
							// Convert date into timestamp
							value = rs.getString("UNIX_TIMESTAMP(`"
									+ columnName + "`)");
							if (value == null) {
								if (log.isDebugEnabled()) log
										.debug("[SQLConnection:extractDatabase] Null timestamp for type "
												+ header.getDatatypes().get(
														columnName)
												+ " from column "
												+ columnName
												+ " in table " + tableName);
								values.put(columnName, "null"); // TODO :
																// particular
																// NULLOBject ?
							} else {
								if (log.isDebugEnabled()) log
										.debug("[SQLConnection:extractDatabase] Timestamp value : "
												+ value);
								// Store date values in appropriate date format
								values.put(columnName, SQLConnector.dateFormatToDate(type,
										Long.valueOf(value), currentTimeZone));
							}
						} else {
							value = rs.getString(columnName);
							values.put(columnName, value);
						}
					}
					Row row = new Row(values, tableName);
					if (log.isDebugEnabled()) log.debug("[SQLConnection:extractDatabase] Inserted row : "
							+ row);
					rows.add(row);
				}
				Body body = new Body(rows);
				Table table = new Table(tableName, header, candidateKeys,
						foreignKeys, body);
				tables.add(table);
			}
			db = new Database(tables);

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if (log.isDebugEnabled()) log.debug("[SQLConnection:extractDatabase] Database generated : " + db);
		
		Float stop = Float.valueOf(System.currentTimeMillis() - start) / 1000;
		if (log.isInfoEnabled()) log.info("[SQLConnection:extractDatabase] Database extracted in " + stop + " seconds.");
		return db;
	}

}
