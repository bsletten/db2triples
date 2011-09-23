/***************************************************************************
 *
 * R2RML Model : RefPredicateMap class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	RefPredicateMap.java
 *
 * Description		:	An element of this class, called a RefPredicateMap,
 * 						contains the rules for generating the predicate 
 * 						component of the (predicate, object) pair generated
 * 						by a RefPredicateObjectMap.
 * 
 * Reference		:	RDF Vocabulary for R2RML
 * 						R2RML: RDB to RDF Mapping Language
 * 						W3C Working Draft 24 March 2011
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.r2rml.model;

import org.openrdf.model.URI;

/**
 * @author  jh
 */
public class RefPredicateMap implements IRefPredicateMap {
	
	/**
	 */
	private URI predicate;

	public RefPredicateMap(URI predicate) {
		super();
		// Check properties consistency
		if (predicate == null)
			throw new IllegalStateException(
					"[RefObjectMap:RefObjectMap] Predicate property have to be specified.");
		this.predicate = predicate;
	}

	/**
	 * @return
	 */
	public URI getPredicate() {
		return predicate;
	}

	/**
	 * @param predicate
	 */
	public void setPredicate(URI predicate) {
		this.predicate = predicate;
	}
}
