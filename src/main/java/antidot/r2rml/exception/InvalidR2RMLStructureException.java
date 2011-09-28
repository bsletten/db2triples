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
 * Invalid R2RML Structure Exception 
 *
 * Exception raised when R2RML structure of file is invalid.
 *
 * @author jhomo
 *
 */
package antidot.r2rml.exception;

public class InvalidR2RMLStructureException extends Exception {

	public InvalidR2RMLStructureException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
