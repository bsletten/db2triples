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
 * SQL model : foreign key
 *
 * Represents candidate keys of a database according to W3C database model.
 * A foreign key identifies a column or a set of columns of a table as a 
 * reference to a column or a set of columns of another table.
 * 
 * Reference : Direct Mapping Definition, 
 * A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011 
 *
 * @author jhomo
 *
 */
package antidot.sql.model;

import java.util.ArrayList;

public class ForeignKey extends CandidateKey {

	// This foreign key targets another key in another table called reference key 
	/**
	 */
	private CandidateKey referenceKey;

	public ForeignKey(ArrayList<String> columnNames, CandidateKey referenceKey) {
		super(columnNames, CandidateKey.KeyType.FOREIGN);
		this.referenceKey = referenceKey;
	}

	public ForeignKey(ArrayList<String> columnNames, String sourceTable,
			CandidateKey referenceKey) {
		super(columnNames, sourceTable, CandidateKey.KeyType.FOREIGN);
		this.referenceKey = referenceKey;
	}

	/**
	 * Get the referency key.  This foreign key targets another key in another table called reference key.
	 * @return
	 */
	public CandidateKey getReferenceKey() {
		return referenceKey;
	}

	/**
	 * Set the referency key.  This foreign key targets another key in another table called reference key.
	 * @return
	 */
	public void setReferenceKey(CandidateKey referenceKey) {
		this.referenceKey = referenceKey;
	}
	
	/**
	 * A foreign key is unary if it's composed of only one column.
	 * @return
	 */
	public boolean isUnary(){
		return getColumnNames().size() == 1;
	}
	
	public String getTargetTableName(){
		return this.referenceKey.getSourceTable();
	}

	public String toString() {
		String result = "{[ForeignKey:toString] parent = " + super.toString()
				+ "; referenceKey = " + referenceKey.toString() + "}";
		return result;
	}

}
