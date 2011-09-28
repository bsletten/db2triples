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
 * R2RML Tools
 *
 * Tools for execute Direct Mapping directly from static methods. 
 *
 * @author jhomo
 *
 */
package antidot.r2rml.main;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.rio.RDFFormat;

import antidot.r2rml.core.R2RMLMapper;
import antidot.rdf.impl.sesame.SesameDataSet;
import antidot.sql.core.SQLConnector;

public class R2RMLTools {
	
	// Log
	private static Log log = LogFactory.getLog(R2RMLTools.class);

	
	public static String runR2RML(String userName, String password,
			String url, String driver, String dbName, String r2rmlFile,
			String format) {
		// RDF Format
		RDFFormat rdfFormat = RDFFormat.N3;
		
		if (format.equals("TURTLE"))
			rdfFormat = RDFFormat.TURTLE;
		else if (format.equals("RDFXML"))
			rdfFormat = RDFFormat.RDFXML;
		else if (format.equals("NTRIPLES"))
			rdfFormat = RDFFormat.NTRIPLES;
		else if (!format.equals("N3")) {
			if (log.isErrorEnabled())
				log
						.error("Unknown RDF format. Please use RDFXML," +
								" TURTLE, N3 or NTRIPLES.");
			System.exit(-1);
		}
		// R2RML instance test
		File r2rmlFileTest = new File(r2rmlFile);
		if (!r2rmlFileTest.exists()){
			log.error("[R2RML:main] R2RML file does not exists.");
			System.exit(-1);
		}
		// Open test database
		Connection conn = null;
		// Connect database
		try {
			conn = SQLConnector.connect(userName, password, url, driver, dbName);
			// Generate RDF graph
			SesameDataSet g = R2RMLMapper.convertMySQLDatabase(conn, r2rmlFile);
			// Return string
			String result = g.printRDF(rdfFormat);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close db connection
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


}
