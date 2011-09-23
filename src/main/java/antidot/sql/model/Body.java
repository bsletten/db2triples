/***************************************************************************
 *
 * SQL model : body
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	SQL.SQLModel
 * 
 * Fichier			:	Body.java
 *
 * Description		:	Represents body of a database according to W3C database model..
 * 						The body contains all rows of a table.
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

import java.util.HashSet;

/**
 * @author   jh
 */
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
