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
 * R2RML Model : R2RML Mapping class
 *
 * Represents a set of TriplesMap objects which can compare with a mapping of 
 * a all tables of a database.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
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
