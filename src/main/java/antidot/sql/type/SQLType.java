/***************************************************************************
 *
 * SQL types
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	SQL
 * 
 * Fichier			:	SQLTypes.java
 *
 * Description		:	Represents SQL types in different formalism (MySQL for instance).
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.sql.type;

import java.util.ArrayList;

public interface SQLType {

	/**
	 * MySQL Types and their display name. References : http://www.htmlite.com/mysql003.php http://dev.mysql.com/doc/refman/5.0/en/numeric-types.html
	 */
	enum MySQLType {

		/**
		 */
		CHAR("CHAR"), // A fixed section from 0 to 255 characters long.
		/**
		 */
		VARCHAR("VARCHAR"), // A variable section from 0 to 255 characters long.
		/**
		 */
		TINYTEXT("TINYTEXT"), // A string with a maximum length of 255
								// characters.
		
		TINYBLOB("TINYBLOB"),
		/**
		 */
		TEXT("TEXT"), // A string with a maximum length of 65535 characters.
		/**
		 */
		BLOB("BLOB"), // A string with a maximum length of 65535 characters.
		/**
		 */
		MEDIUMTEXT("MEDIUMTEXT"), // A string with a maximum length of 16777215
									// characters.
		/**
		 */
		MEDIUMBLOB("MEDIUMBLOB"), // A string with a maximum length of 16777215
									// characters.
		/**
		 */
		LONGTEXT("LONGTEXT"), // A string with a maximum length of 4294967295
								// characters.
		/**
		 */
		LONGBLOB("LONGBLOB"), // A string with a maximum length of 4294967295
								// characters.
		/**
		 */
		BIT("BIT"), // 0 or 1, equivalent to TINYINT(1) (since MySQL 5.0.3)
		/**
		 */
		TINYINT("TINYINT"), // -128 to 127 normal
		/**
		 */
		UNSIGNED_TINYINT("TINYINT UNSIGNED"), // 0 to 255 UNSIGNED.
		/**
		 */
		SMALLINT("SMALLINT"), // -32768 to 32767 normal
		/**
		 */
		UNSIGNED_SMALLINT("SMALLINT UNSIGNED"), // 0 to 65535 UNSIGNED.
		/**
		 */
		MEDIUMINT("MEDIUMINT"), // -8388608 to 8388607 normal.
		/**
		 */
		UNSIGNED_MEDIUMINT("MEDIUMINT UNSIGNED"), // 0 to 16777215 UNSIGNED.
		/**
		 */
		INT("INT"), // -2147483648 to 2147483647 normal.
		/**
		 */
		UNSIGNED_INT("INT UNSIGNED"), // 0 to 4294967295 UNSIGNED.
		/**
		 */
		BIGINT("BIGINT"), // -9223372036854775808 to 9223372036854775807 normal.
		/**
		 */
		UNSIGNED_BIGINT("BIGINT UNSIGNED"), // 0 to 18446744073709551615
											// UNSIGNED.
		/**
		 */
		FLOAT("FLOAT"), // A small number with a floating decimal point.
		/**
		 */
		UNSIGNED_FLOAT("FLOAT UNSIGNED"), // A small positive number with a floating decimal point.
		/**
		 */
		DOUBLE("DOUBLE"), // A large number with a floating decimal point.
		/**
		 */
		UNSIGNED_DOUBLE("DOUBLE UNSIGNED"), // A large positive number with a floating decimal point.
		/**
		 */
		DECIMAL("DECIMAL"), // A DOUBLE stored as a string , allowing for a
							// fixed decimal
		/**
		 */
		UNSIGNED_DECIMAL("DECIMAL UNSIGNED"), // A positive DOUBLE stored as a string , allowing for a
		// fixed decimal
		// point.
		/**
		 */
		DATE("DATE"), // YYYY-MM-DD ("1000-01-01" - "9999-12-31").
		/**
		 */
		DATETIME("DATETIME"), // YYYY-MM-DD HH:MM:SS ("1000-01-01 00:00:00" -
		// "9999-12-31 23:59:59").
		/**
		 */
		TIMESTAMP("TIMESTAMP"), // YYYYMMDDHHMMSS (19700101000000 - 2037+).
		/**
		 */
		TIME("TIME"), // HH:MM:SS ("-838:59:59" -"838:59:59").
		/**
		 */
		YEAR("YEAR"), // YYYY (1900 - 2155).
		/**
		 */
		SET("SET"), // // Similar to ENUM except each column may have more than
					// one of
		// the specified possible values.
		/**
		 */
		ENUM("ENUM");
		// Short for ENUMERATION which means that each column may have one of a
		// specified possible values.

		/**
		 */
		private String displayName;

		private MySQLType(String displayName) {
			this.displayName = displayName;
		}

		public String toString() {
			return displayName;
		}

		/**
		 * @return
		 */
		public String getDisplayName() {
			return displayName;
		}

		public static ArrayList<MySQLType> getDateTypes() {
			ArrayList<MySQLType> result = new ArrayList<MySQLType>();
			result.add(DATE);
			result.add(DATETIME);
			result.add(TIMESTAMP);
			return result;
		}
		
		public static ArrayList<MySQLType> getBlobTypes() {
			ArrayList<MySQLType> result = new ArrayList<MySQLType>();
			result.add(TINYBLOB);
			result.add(MEDIUMBLOB);
			result.add(LONGBLOB);
			result.add(BLOB);
			return result;
		}

		public boolean isDateType() {
			return getDateTypes().contains(this);
		}
		
		public boolean isBlobType(){
			return getBlobTypes().contains(this);
		}

		/**
		 * Converts a mySQLType from its display name.
		 * @param displayName
		 * @return
		 */
		public static MySQLType toMySQLType(String displayName) {
			for (MySQLType mySQLType : MySQLType.values()) {
				if (mySQLType.getDisplayName().equals(displayName))
					return mySQLType;
			}
			return null;
		}

	}

}
