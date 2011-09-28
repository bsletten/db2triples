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
 * SQLToRDF Toolkit
 *
 * Collection of useful tool-methods used for conversion between SQL and RDF. 
 *
 * @author jhomo
 *
 */
package antidot.rdf.tools;

import java.util.HashMap;
import java.util.Map;

import antidot.sql.type.SQLType;
import antidot.sql.type.SQLType.MySQLType;
import antidot.xmls.type.XSDType;

public abstract class SQLToRDFToolkit {

	/**
	 * Equivalence datatype between standard SQL types and XSD types according
	 * to XML Schema Part 2: Datatypes Second Edition (W3C Recommendation 28
	 * October 2004) See : http://www.w3.org/TR/xmlschema-2/
	 */
	private static Map<SQLType.MySQLType, XSDType> equivalentTypes = new HashMap<SQLType.MySQLType, XSDType>();
	
	static {
		// Text types
		equivalentTypes.put(MySQLType.CHAR, XSDType.STRING); // A fixed
		// section
		// from
		// 0 to
		// 255
		// characters long.
		equivalentTypes.put(MySQLType.VARCHAR, XSDType.STRING); // A
		// variable
		// section
		// from
		// 0
		// to 255 characters long.
		equivalentTypes.put(MySQLType.TINYTEXT, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 255 characters.
		equivalentTypes.put(MySQLType.TEXT, XSDType.STRING); // A string
		// with
		// a
		// maximum
		// length
		// of 65535 characters.
		equivalentTypes.put(MySQLType.BLOB, XSDType.STRING); // A string
		// with
		// a
		// maximum
		// length
		// of 65535 characters.
		equivalentTypes.put(MySQLType.MEDIUMTEXT, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 16777215
		// characters.
		equivalentTypes.put(MySQLType.MEDIUMBLOB, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 16777215
		// characters.
		equivalentTypes.put(MySQLType.LONGTEXT, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 4294967295
		// characters.
		equivalentTypes.put(MySQLType.LONGBLOB, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 4294967295
		// characters.

		// Number types
		equivalentTypes.put(MySQLType.BIT, XSDType.BYTE); // 0 or 1
		equivalentTypes.put(MySQLType.TINYINT, XSDType.BYTE); // -128 to
		// 127
		// normal
		equivalentTypes.put(MySQLType.UNSIGNED_TINYINT, XSDType.UNSIGNED_BYTE); // 0
																				// to
																				// 255
		// UNSIGNED.
		equivalentTypes.put(MySQLType.SMALLINT, XSDType.SHORT); // -32768
		// to
		// 32767
		// normal
		equivalentTypes
				.put(MySQLType.UNSIGNED_SMALLINT, XSDType.UNSIGNED_SHORT); // 0
																			// to
																			// 65535
		// UNSIGNED.
		equivalentTypes.put(MySQLType.MEDIUMINT, XSDType.INT); // -8388608
		// to
		// 8388607
		// normal.
		// // TODO : find another
		// equivalent ?
		equivalentTypes.put(MySQLType.UNSIGNED_MEDIUMINT, XSDType.INT); // 0
		// to
		// 16777215
		// UNSIGNED. // TODO
		// : find another
		// equivalent ?
		equivalentTypes.put(MySQLType.INT, XSDType.INT); // -2147483648
		// to
		// 2147483647
		// normal.
		equivalentTypes.put(MySQLType.UNSIGNED_INT, XSDType.UNSIGNED_INT); // 0
																			// to
																			// 4294967295
		// UNSIGNED.
		equivalentTypes.put(MySQLType.BIGINT, XSDType.LONG); // -9223372036854775808
		// to
		// 9223372036854775807 normal.
		equivalentTypes.put(MySQLType.UNSIGNED_BIGINT, XSDType.UNSIGNED_LONG); // 0
																				// to
		// 18446744073709551615
		// UNSIGNED.
		equivalentTypes.put(MySQLType.FLOAT, XSDType.FLOAT); // A small
		// number
		// with
		// a
		// floating
		// decimal point.
		equivalentTypes.put(MySQLType.UNSIGNED_FLOAT, XSDType.FLOAT); // A
		// small
		// number
		// with
		// a
		// floating
		// decimal point.
		equivalentTypes.put(MySQLType.DOUBLE, XSDType.DOUBLE); // A
		// small
		// number
		// with
		// a
		// floating
		// decimal point.
		equivalentTypes.put(MySQLType.UNSIGNED_DOUBLE, XSDType.DOUBLE); // A
		// large
		// number
		// with
		// a
		// floating decimal point.
		equivalentTypes.put(MySQLType.DECIMAL, XSDType.DECIMAL); // A
		// large
		// number
		// with
		// a
		// floating decimal point.
		equivalentTypes.put(MySQLType.UNSIGNED_DECIMAL, XSDType.DECIMAL); // A
		// DOUBLE
		// stored
		// as
		// a
		// string , allowing for a
		// fixed decimal point.

		// Date types
		equivalentTypes.put(MySQLType.DATE, XSDType.DATE); // YYYY-MM-DD
		// ("1000-01-01"
		// -
		// "9999-12-31").
		equivalentTypes.put(MySQLType.DATETIME, XSDType.DATETIME); // YYYY-MM-DD
		// HH:MM:SS
		// ("1000-01-01 00:00:00" - "9999-12-31 23:59:59").
		// xsd:datetime doesn't match because the letter T is required ?
		// No, it's valid. See W3C Working Draft (24/03/2011), Section 2.3.4.
		equivalentTypes.put(MySQLType.TIMESTAMP, XSDType.DATETIME); // YYYYMMDDHHMMSS
		// (19700101000000 - 2037+).
		// TODO : equivalent ?
		equivalentTypes.put(MySQLType.TIME, XSDType.TIME); // HH:MM:SS
		// ("-838:59:59"
		// -
		// "838:59:59").
		equivalentTypes.put(MySQLType.YEAR, XSDType.GYEAR); // YYYY
		// (1900 -
		// 2155).

		// Misc types
		// TODO : equivalent ?
		equivalentTypes.put(MySQLType.ENUM, XSDType.ENUMERATION); // Short
		// for
		// ENUMERATION which
		// means
		// that each column may have one of
		// a specified possible values.
		equivalentTypes.put(MySQLType.SET, XSDType.ENUMERATION); // Similar
		// to
		// ENUM
		// except each
		// column
		// may have more than one of the
		// specified possible values.
	}
	
	public static XSDType getEquivalentType(MySQLType mySQLType){
		return equivalentTypes.get(mySQLType);
	}
	
	public static XSDType getEquivalentType(String mySQLType){
		return equivalentTypes.get(SQLType.MySQLType.toMySQLType(mySQLType));
	}
	
	public static boolean isValidSQLDatatype(String datatype){
		return equivalentTypes.keySet().contains(SQLType.MySQLType.toMySQLType(datatype));
	}

}
