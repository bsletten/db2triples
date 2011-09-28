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
 * R2RML Model : RefPredicateMap class
 *
 * An element of this class, called a RefObjectMap, contains the rules for 
 * generating the predicate component of the (predicate, object) pair generated 
 * by a RefPredicateObjectMap.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
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
