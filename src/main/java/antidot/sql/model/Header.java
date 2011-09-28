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
 * SQL model : header
 *
 * Represents header of a database according to W3C database model.
 * In this case, header contains information about types of data stored
 * in column of tables.
 * 
 * Reference : Direct Mapping Definition, 
 * A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011 
 *
 * @author jhomo
 *
 */
package antidot.sql.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Header {

	// Types stored
	/**
	 */
	private LinkedHashMap<String, String> datatypes;

	public Header(LinkedHashMap<String, String> datatypes) {
		this.datatypes = datatypes;
	}

	/**
	 * @return
	 */
	public LinkedHashMap<String, String> getDatatypes() {
		return datatypes;
	}

	/**
	 * @param  datatypes
	 */
	public void setDatatypes(LinkedHashMap<String, String> datatypes) {
		this.datatypes = datatypes;

	}

	public ArrayList<String> getColumnNames() {
		ArrayList<String> results = new ArrayList<String>();
		Set<Map.Entry<String, String>> mapSet = datatypes.entrySet();
		Iterator<Map.Entry<String, String>> iter = mapSet.iterator();
		while (iter.hasNext()) {
			String columnName = iter.next().getKey();
			results.add(columnName);
		}
		return results;
	}

	public String toString() {
		String result = "{[Header:toString] datatypes = ";
		int i = 0;
		for (String columnName : getColumnNames()) {
			i++;
			result += columnName + " => " + datatypes.get(columnName);
			if (i < datatypes.size())
				result += ", ";
		}
		result += "}";
		return result;
	}

}
