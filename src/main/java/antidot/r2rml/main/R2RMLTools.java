/***************************************************************************
 *
 * R2RML Tools
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	Main
 * 
 * Fichier			:	R2RMLTools.java
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
