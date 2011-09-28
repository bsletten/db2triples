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
 * XSD Types
 * 
 * Reference : http://www.w3.org/TR/xmlschema-2/ http://www.schemacentral.com/sc/xsd 
 *
 * @author jhomo
 *
 */
package antidot.xmls.type;

public enum XSDType {
	/**
	 */
	INTEGER("integer"),
	/**
	 */
	STRING("string"),
	/**
	 */
	BYTE("byte"), // -128 to 127 normal
	/**
	 */
	UNSIGNED_BYTE("unsignedByte"), // 0 to 255 UNSIGNED.
	/**
	 */
	SHORT("short"), // -32768 to 32767 normal
	/**
	 */
	UNSIGNED_SHORT("unsignedShort"),  // 0 to 65535 UNSIGNED.
	/**
	 */
	INT("int"), // -8388608 to 8388607 normal.
	/**
	 */
	UNSIGNED_INT("unsignedInt"), // 0 to 4294967295 UNSIGNED.
	/**
	 */
	LONG("long"), // -9223372036854775808 to 9223372036854775807 normal.
	/**
	 */
	UNSIGNED_LONG("unsignedLong"), // 0 to 18446744073709551615 UNSIGNED.
	/**
	 */
	FLOAT("float"), // A small number with a floating decimal point.
	/**
	 */
	DOUBLE("double"), // A large number with a floating decimal point.
	/**
	 */
	DECIMAL("decimal"), // A DOUBLE stored as a string , allowing for a fixed decimal point.
	/**
	 */
	DATE("date"),  // YYYY-MM-DD ("1000-01-01" - "9999-12-31").
	/**
	 */
	DATETIME("dateTime"), // YYYY-MM-DDTHH:MM:SS ("1000-01-01 00:00:00" - "9999-12-31 23:59:59").
	/**
	 */
	TIME("time"), // HH:MM:SS ("-838:59:59" - "838:59:59").
	/**
	 */
	GYEAR("gYear"), // YYYY (1900 - 2155).
	/**
	 */
	ENUMERATION("enumeration"), // See 4.3.5.2 XML Representation of enumeration Schema Components
	/**
	 */
	POSITIVE_INTEGER("positiveInteger"); // Based on xsd:nonNegativeInteger
	
	public static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
	
	private String type;
	
	private XSDType(String type){
		this.type = type;
	}
	
	public String toString(){
		return type;
	}
	
	/**
	 * Converts a XSDType from its display name (try with XSD namespace too).
	 * @param displayName
	 * @return
	 */
	public static XSDType toXSDType(String displayName) {
		if (displayName == null) return null;
		for (XSDType xsdType : XSDType.values()) {
			if (xsdType.toString().equals(displayName) || (XSD_NAMESPACE + xsdType).toString().equals(displayName))
				return xsdType;
		}
		throw new IllegalArgumentException("[XSDType:toXSDType] Unknown XSD type : " + displayName);
		
	}
	
	/**
	 * Is XSD type is in a date format.
	 * @param type
	 * @return
	 */
	public static boolean isDateType(XSDType type){
		return type.equals(DATETIME) || type.equals(DATE);
	}
	
	
}
