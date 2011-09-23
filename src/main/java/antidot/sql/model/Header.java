/***************************************************************************
 *
 * SQL model : header
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	SQL.SQLModel
 * 
 * Fichier			:	Header.java
 *
 * Description		:	Represents header of a database according to W3C database model..
 * 						In this case, header contains information about types of data stored in column of tables.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author   jh
 */
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
