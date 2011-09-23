/***************************************************************************
 *
 * Direct Mapping Tools
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	Main
 * 
 * Fichier			:	DirectMappingTools.java
 *
 * Description		:   Tools for execute Direct Mapping directly from
 * 						static methods 
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.dm.main;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.rio.RDFFormat;

import antidot.dm.core.DirectMapper;
import antidot.rdf.impl.sesame.SesameDataSet;
import antidot.sql.core.SQLConnector;
import antidot.sql.core.SQLExtractor;
import antidot.sql.model.Database;

public class DirectMappingTools {
	
	// Log
	private static Log log = LogFactory.getLog(DirectMappingTools.class);

	
	public static String runDirectMapping(String userName, String password,
			String url, String driver, String dbName, String baseURI,
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
		
		// Open test database
		Connection conn = null;
		// Connect database
		try {
			conn = SQLConnector.connect(userName, password, url, driver, dbName);
			// Extract database model
			Database db = SQLExtractor.extractMySQLDatabase(conn, null);
			// Generate RDF graph
			SesameDataSet g = DirectMapper.generateDirectMapping(db, baseURI);
			// Return N3 string
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
