/***************************************************************************
 *
 * Semi Statement
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	RDFModel
 * 
 * Fichier			:	SemiStatement.java
 *
 * Description			:	Represents a semi-statement which is statement-like
 * 							without subject.
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.rdf.impl.sesame;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class SemiStatement {
	
	private URI p;
	private Value o;
	
	public SemiStatement(URI p, Value o){
		this.p = p;
		this.o = o;
	}
	
	
	public URI getPredicate() {
		return p;
	}

	public void setPredicate(URI p) {
		this.p = p;
	}

	public Value getObject() {
		return o;
	}

	public void setObject(Value o) {
		this.o = o;
	}
	
	public String toString(){
		return "[SemiStatement:toString] predicate = " + p + ", object = " + o;
	}

}
