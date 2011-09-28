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
 * SQL model : row
 *
 * Represents candidate keys of a database according to W3C database model.
 * A row consists of one set of attributes (or one tuple) corresponding to one 
 * instance of the entity that a table schema describes.
 * 
 * Reference : Direct Mapping Definition, 
 * A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011 
 *
 * @author jhomo
 *
 */
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
