/***************************************************************************
 *
 * SQL model : row
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	SQL.SQLModel
 * 
 * Fichier			:	CandidateKey
 *
 * Description		:	Represents candidate keys of a database according to W3C database model.
 * 						a row consists of one set of attributes (or one tuple) corresponding to one instance 
 * 						of the entity that a table schema describes.
 * 
 * Reference		:	Direct Mapping Definition
 * 						A Direct Mapping of Relational Data to RDF 
 * 						W3C Working Draft 24 March 2011 
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.sql.model;

import java.util.TreeMap;

/**
 * @author   jh
 */
public class Row {

	/**
	 */
	TreeMap<String, String> values; // TODO : use SQL type ?
	/**
	 */
	String parentTableName;

	/**
	 * @return
	 */
	public String getParentTableName() {
		return parentTableName;
	}

	/**
	 * @param  tableName
	 */
	public void setParentTableName(String tableName) {
		this.parentTableName = tableName;
	}

	public Row(TreeMap<String, String> values, String parentTableName) {
		this.values = values;
		this.parentTableName = parentTableName;
	}

	/**
	 * @return
	 */
	public TreeMap<String, String> getValues() {
		return values;
	}

	/**
	 * @param  values
	 */
	public void setValues(TreeMap<String, String> values) {
		this.values = values;
	}

	public String toString() {
		String result = "{[Row:toString] parentTableName = " + parentTableName + "; values = ";
		int i = 0;
		for (String columnName : values.navigableKeySet()) {
			i++;
			result += columnName + " => " + values.get(columnName);
			if (i < values.size())
				result += ", ";
		}
		result += "}";
		return result;
	}

}
