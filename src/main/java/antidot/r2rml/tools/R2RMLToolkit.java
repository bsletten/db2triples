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
 * R2RML Toolkit
 *
 * Collection of useful tool-methods used in R2RML 
 *
 * @author jhomo
 *
 */
package antidot.r2rml.tools;

public abstract class R2RMLToolkit {

	public static boolean checkCurlyBraces(String value) {
		if (value == null)
			// No brace : valid
			return true;
		char[] chars = value.toCharArray();
		boolean openedBrace = false;
		boolean emptyBraceContent = false;
		boolean closedBrace = true;
		for (char c : chars) {
			switch (c) {
			case '{':
				// Already opened
				if (openedBrace)
					return false;
				openedBrace = true;
				closedBrace = false;
				emptyBraceContent = true;
				break;

			case '}':
				// Already closed or not opened or empty content
				if (closedBrace || !openedBrace || emptyBraceContent)
					return false;
				openedBrace = false;
				closedBrace = true;
				emptyBraceContent = true;
				break;

			default:
				if (openedBrace)
					emptyBraceContent = false;
				break;
			}
		}
		// All curly braces have to be closed
		return (!openedBrace && closedBrace);
	}

	public static boolean checkJoinCondition(String joinCondition) {
		if (joinCondition == null)
			// No joinCondition : valid
			return true;
		String childOrParent = "((\\{childAlias\\.\\}\\S*\\s*=\\s*\\{parentAlias\\.\\}\\S*)"
				+ "|(\\{parentAlias\\.\\}\\S*\\s*=\\s*\\{childAlias\\.\\}\\S*))";
		String andOthers = "(\\sAND\\s" + childOrParent + ")*";
		String regex = childOrParent + andOthers;
		return joinCondition.matches(regex);

	}

}
