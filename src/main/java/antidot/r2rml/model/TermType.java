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
 * R2RML Model : TermType class
 *
 * A string indicating whether subject or object generated using the value column
 * name specified for rr:column should be an IRI reference, blank node or
 * (if object) literal.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
package antidot.r2rml.model;

public enum TermType {
	/**
	 */
	IRI("IRI"), /**
	 */
	BLANK_NODE("BlankNode"), /**
	 */
	LITERAL("Literal");

	/**
	 */
	private String displayName;

	private TermType(String displayName) {
		this.displayName = displayName;
	}

	public String toString() {
		return displayName;
	}

	/**
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Converts a termType from its display name.
	 * 
	 * @param displayName
	 * @return
	 */
	public static TermType toTermType(String displayName) {
		for (TermType termType : TermType.values()) {
			if (termType.getDisplayName().equals(displayName))
				return termType;
		}
		return null;
	}

}
