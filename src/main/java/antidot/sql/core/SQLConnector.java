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
 * SQL Connector
 *
 * Functions to connect a database, update and extract data from it.
 *
 * @author jhomo
 *
 */
package antidot.sql.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antidot.sql.type.SQLType;

public abstract class SQLConnector {

	// Log
	private static Log log = LogFactory.getLog(SQLConnector.class);

	/**
	 * Try to connect a database and returns current connection.
	 * 
	 * @param userName
	 * @param password
	 * @param url
	 * @param driver
	 * @param database
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	static public Connection connect(String userName, String password,
			String url, String driver, String database) throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		if (log.isDebugEnabled()) log.debug("[SQLConnection:extractDatabase] Try to connect " + url);
		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url + database, userName,
				password);
		if (log.isDebugEnabled()) log
				.debug("[SQLConnection:extractDatabase] Database connection established");
		return conn;
	}

	

	/**
	 * Update a database, connected with c, with requests in SQL file.
	 * 
	 * @param c
	 * @param pathToSQLFile
	 * @throws SQLException
	 */
	public static void updateDatabase(Connection c, String pathToSQLFile)
			throws SQLException {
		if (log.isDebugEnabled()) log.debug("[SQLConnector:updateDatabase] pathToSQLFile = "
				+ pathToSQLFile);
		String s = new String();
		StringBuffer sb = new StringBuffer();
		try {
			FileReader fr = new FileReader(new File(pathToSQLFile));
			// be sure to not have line starting with "--" or "/*" or any other
			// non aplhabetical character
			BufferedReader br = new BufferedReader(fr);

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			br.close();
			// here is our splitter ! We use ";" as a delimiter for each request
			// then we are sure to have well formed statements
			String[] inst = sb.toString().split(";");
			Statement st = c.createStatement();
			for (int i = 0; i < inst.length; i++) {
				// we ensure that there is no spaces before or after the request
				// string
				// in order to not execute empty statements
				if (!inst[i].trim().equals("")) {
					st.executeUpdate(inst[i]);
					if (log.isDebugEnabled()) log.debug("[SQLConnector:updateDatabase] >> " + inst[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Drop all tables from database with connection c. Specific to MySQL
	 * databases.
	 * 
	 * @param c
	 * @throws SQLException
	 */
	public static void resetMySQLDatabase(Connection c) throws SQLException {
		// Get tables of database
		DatabaseMetaData meta = c.getMetaData();
		ResultSet tablesSet = meta.getTables(c.getCatalog(), null, "%", null);
		while (tablesSet.next()) {
			// Extract table name
			String tableName = tablesSet.getString("TABLE_NAME");
			// Get a statement from the connection
			Statement stmt = c.createStatement();
			// Execute the query
			stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
			stmt.execute("DROP TABLE " + tableName);
			stmt.close();
		}
	}

	/**
	 * Update a database, connected with c, with given request.
	 * 
	 * @param c
	 * @param query
	 * @throws SQLException
	 */
	public static void updateDatabaseQuery(Connection c, String query)
			throws SQLException {
		// Get a statement from the connection
		Statement stmt = c.createStatement();
		// Execute the query
		stmt.execute(query);
		// Close statement
		stmt.close();
	}

	// Convert type methods

	/**
	 * Convert a MySQL timestamp in a date format in a conform string date.
	 * 
	 * @param mySQLType
	 * @param timestamp
	 * @param timeZone
	 */
	public static String dateFormatToDate(SQLType.MySQLType mySQLType,
			Long timestamp, String timeZone) {
		if (log.isDebugEnabled()) log.debug("[SQLConnector:dateFormatToDate] mySQLType : " + mySQLType
				+ " timestamp : " + timestamp);
		// Constructs a Date object using the given milliseconds time value.
		// But, timestamp in MySQL is given in seconds.
		timestamp *= 1000;
		if (!mySQLType.isDateType())
			throw new IllegalStateException(
					"[SQLConnector:dateFormatToDate] MySQLType forbidden : it must be in a date format.");
		Date date = timestampToDate(timestamp);
		switch (mySQLType) {
		case DATETIME:
			return dateToISO8601(date, timeZone);

		case DATE:
			return dateToDate(date, timeZone);

		case TIMESTAMP:
			return dateToISO8601(date, timeZone);

		default:
			throw new IllegalStateException(
					"[SQLConnector:dateFormatToDate] Unknown format date.");
		}
	}

	/**
	 * Convert a timestamp into a Date object.
	 */
	public static Date timestampToDate(Long timestamp) {
		if (log.isDebugEnabled()) log.debug("[SQLConnector:timestampToDate] timestamp : " + timestamp);
		// Date object represents a unqiue time point like the timestamp
		// Timezone is not take into account here.
		Date date = new Date(timestamp);
		if (log.isDebugEnabled()) log.debug("[SQLConnector:timestampToDate] converted Date : " + date);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddz");
		String test = df.format(date);
		if (log.isDebugEnabled()) log.debug("[SQLConnector:timestampToDate] timezone : " + test);
		return date;
	}

	/**
	 * Convert a Date object into a string date (in xsd:date format)
	 * 
	 * @param date
	 * @param withTimeZone
	 * @return
	 */
	public static String dateToDate(Date date, String timeZone) {
		if (log.isDebugEnabled()) log.debug("[SQLConnector:dateToDate] date : " + date + " timeZone : "
				+ timeZone);
		String result = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		result = df.format(date);
		// xsd:date requires a ":" in timeZone
		result += timeZone;
		if (log.isDebugEnabled()) log.debug("[SQLConnector:dateToDate] result : " + result);
		return result;
	}

	/**
	 * Convert a Date object into a string date (in xsd:dateTime format)
	 * 
	 * @param date
	 * @param withTimeZone
	 * @return
	 */
	public static String dateToISO8601(Date date, String timeZone) {
		if (log.isDebugEnabled()) log.debug("[SQLConnector:dateToISO8601] date : " + date
				+ " timezone : " + timeZone);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String result = df.format(date);
		// xsd:dateTime requires a ":" in timeZone
		result += timeZone;
		if (log.isDebugEnabled()) log.debug("[SQLConnector:dateToISO8601] result : " + result);
		return result;
	}

	/**
	 * Get time zone stored in MySQL database. Reference :
	 * http://dev.mysql.com/doc/refman/5.5/en/time-zone-support.html The result
	 * can be NULL if the Timezone can't be determinated.
	 * 
	 * The appropriated timezone returned follow these priorities : 1) If
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static String getTimeZone(Connection conn) throws SQLException {
		if (log.isDebugEnabled()) log.debug("[SQLConnector:getTimeZone]");
		Statement stmt = conn.createStatement();
		String query = "SELECT @@global.time_zone, @@session.time_zone;";
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			// The global time_zone system variable indicates
			// the time zone the server currently is operating in.
			String globalMySQLTimeZone = rs.getString("@@global.time_zone");
			// Initially, the session variable takes its value from the global
			// time_zone variable, but the client can change its own time zone.
			String sessionMySQLTimeZone = rs.getString("@@session.time_zone");
			String mySQLTimeZone = globalMySQLTimeZone;
			if (!globalMySQLTimeZone.equals(sessionMySQLTimeZone)) {
				// Use session time zone in priority
				mySQLTimeZone = sessionMySQLTimeZone;
			}
			if (log.isDebugEnabled()) log.debug("[SQLConnector:getTimeZone] mySQLTimeZone extracted = "
					+ mySQLTimeZone);
			return getTimeZoneFromMySQLFormat(mySQLTimeZone);
		}
		if (log.isWarnEnabled()) log
				.warn("[SQLConnector:getTimeZone] Impossible to read timezone from database. Timezone of current system selected.");
		return timeZoneToStr(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * Return corresponding xsd timezone syntax from a given MySQL format.
	 * References : MySQL format :
	 * http://dev.mysql.com/doc/refman/5.5/en/time-zone-support.html XSD syntax
	 * : http://www.schemacentral.com/sc/xsd/t-xsd_dateTime.html
	 * 
	 * @param format
	 * @return
	 */
	public static String getTimeZoneFromMySQLFormat(String format) {
		String result = null;
		if (format.equals("SYSTEM")) {
			// Use timezone system
			result = timeZoneToStr(TimeZone.getDefault());
		} else if (format.indexOf(":") != -1) {
			// The value is given as a string indicating an offset from UTC
			// Complex problem : how determine time zome from its offset ?
			// But this MySQL format is already in xsd:date format
			if (format.equals("+00:00") || format.equals("-00:00"))
				result = "Z";
			else
				result = format;
		} else {
			String[] IDs = TimeZone.getAvailableIDs();
			for (String ID : IDs) {
				if (ID.equals(format)) {
					result = timeZoneToStr(TimeZone.getTimeZone(format));
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Convert timeZone into a valid xsd:dateTime format.
	 * 
	 * @param tz
	 * @return
	 */
	public static String timeZoneToStr(TimeZone tz) {
		if (tz == null)
			return null;
		// Exception if timeZone is UTC
		if (tz.getID().equals("UTC"))
			return "Z";
		// Convert timeZone into a valid xsd:dateTime format
		SimpleDateFormat df = new SimpleDateFormat("Z");
		df.setTimeZone(tz);
		String result = df.format(new Date());
		// xsd:dateTime requires a ":" in timeZone
		result = result.substring(0, result.length() - 2) + ":"
				+ result.substring(result.length() - 2);
		return result;
	}

	/**
	 * Tool : print in output meta data column names from a result set
	 * 
	 * @param tablesSet
	 * @throws SQLException
	 */
	public static void printMetaColumnsFromTable(ResultSet tablesSet)
			throws SQLException {
		if (log.isInfoEnabled()) log.info("[printMetaColumnsFromTable] ");
		for (int i = 0; i < tablesSet.getMetaData().getColumnCount(); i++) {
			String columnName = tablesSet.getMetaData().getColumnName(i + 1);
			Object value = tablesSet.getObject(i + 1);
			System.out.println(columnName + " = " + value);
		}
	}

}
