/***************************************************************************
 *
 * R2RMLModel : R2RML Mapping class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	R2RMLMappingClass.java
 *
 * Description		:	Represents a set of TriplesMap objects which can compare with a mapping of a all tables
 * 						of a database.
 * 
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.r2rml.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author  jh
 */
public class R2RMLMapping {
	
	/**
	 */
	private Collection<TriplesMap> triplesMaps;

	public R2RMLMapping(Collection<TriplesMap> triplesMaps) {
		super();
		this.triplesMaps = new HashSet<TriplesMap>();
		this.triplesMaps.addAll(triplesMaps);
	}

	/**
	 * @return
	 */
	public Collection<TriplesMap> getTriplesMaps() {
		return triplesMaps;
	}
}
