/***************************************************************************
 *
 * SQL model : foreign key
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	SQL.SQLModel
 * 
 * Fichier			:	ForeignKey.java
 *
 * Description		:	Represents candidate keys of a database according to W3C database model.
 * 						A foreign key identifies a column or a set of columns of a table as a 
 * 						reference to a column or a set of columns of another table.
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

/**
 * @author   jh
 */
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
