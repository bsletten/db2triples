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
 * RDF Toolkit
 *
 * Collection of useful tool-methods used for RDF.
 *
 * @author jhomo
 *
 */
package antidot.rdf.tools;

public abstract class RDFToolkit {

	/**
	 * Check if URI is valid. The URI syntax consists of a URI scheme name (such
	 * as "http", "ftp", "mailto" or "file") followed by a colon character, and
	 * then by a scheme-specific part. The specifications that govern the
	 * schemes determine the syntax and semantics of the scheme-specific part,
	 * although the URI syntax does force all schemes to adhere to a certain
	 * generic syntax that, among other things, reserves certain characters for
	 * special purposes (without always identifying those purposes). The syntax
	 * respects the standard RFC 3986. Currently, this function uses the Java
	 * checking of URI object which throws an IllegalArgumentException if the
	 * given string violates RFC 2396 (ancestor of RDF 3986). // TODO : adapt
	 * this method for RFC 3986. Reference : http://tools.ietf.org/html/rfc3986
	 */
	public static boolean validURI(String strURI) {
		boolean isValid = true;
		try {
			java.net.URI.create(strURI);
		} catch (IllegalArgumentException e) {
			isValid = false;
		}
		return isValid;
	}

}
