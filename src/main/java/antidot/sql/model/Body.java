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
 * Represents body of a database according to W3C database model. 
 * The body contains all rows of a table.
 * 
 * Reference : Direct Mapping Definition, 
 * A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011 
 *
 * @author jhomo
 *
 */
package antidot.sql.model;

import java.util.HashSet;

public class Body {
	
	/**
	 */
	private HashSet<Row> rows;
	
	public Body(HashSet<Row> rows){
		this.rows = rows;
	}

	/**
	 * @return
	 */
	public HashSet<Row> getRows() {
		return rows;
	}

	/**
	 * @param  rows
	 */
	public void setRows(HashSet<Row> rows) {
		this.rows = rows;
	}
	
	public String toString(){
		String result = "{[Body:toString] rows = ";
		int i = 0;
		for (Row row : rows) {
			i++;
			result += row;
			if (i < rows.size())
				result += ", ";
		}
		result += "}";
		return result;
	}

}
