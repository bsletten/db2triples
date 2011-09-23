/***************************************************************************
 *
 * R2RML : R2RML Mapper (http://www.antidot.net)
 *
 * (C) 2011 Antidot
 *
 * Module			:	R2RML
 * 
 * Fichier			:	R2RMLMapper.java
 *
 * Description		:	The R2RML Mapper constructs an RDF dataset from
 * 						a R2RML Mapping document and a MySQL database.  
 * 
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.r2rml.core;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antidot.r2rml.exception.InvalidR2RMLStructureException;
import antidot.r2rml.exception.InvalidR2RMLSyntaxException;
import antidot.r2rml.model.R2RMLMapping;
import antidot.rdf.impl.sesame.SesameDataSet;

public abstract class R2RMLMapper {
	
	// Log
	private static Log log = LogFactory.getLog(R2RMLMapper.class);
	
	private static Long start = 0l;
	
	/**
	 * Convert a MySQL database into a RDF graph from a database Connection
	 * and a R2RML instance (with native storage).
	 */
	public static SesameDataSet convertMySQLDatabase(Connection conn,
			String pathToR2RMLMappingDocument, String pathToNativeStore) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		if (log.isInfoEnabled()) log.info("[R2RMLMapper:convertMySQLDatabase] Start Mapping R2RML...");
		// Init time
		start = System.currentTimeMillis();
		// Extract R2RML Mapping object
		R2RMLMapping r2rmlMapping = null;
		try {
			r2rmlMapping = R2RMLMappingFactory.extractR2RMLMapping(pathToR2RMLMappingDocument);
		} catch (InvalidR2RMLStructureException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (InvalidR2RMLSyntaxException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// Connect database
		R2RMLEngine r2rmlEngine = new R2RMLEngine(conn);
		SesameDataSet result =  r2rmlEngine.runR2RMLMapping(r2rmlMapping, pathToNativeStore);
		if (log.isInfoEnabled()) log.info("[R2RMLMapper:convertMySQLDatabase] Mapping R2RML done.");
		Float stop = Float.valueOf(System.currentTimeMillis() - start) / 1000;
		if (log.isInfoEnabled()) log.info("[DirectMapper:convertDatabase] Database extracted in "
				+ stop + " seconds.");
		if (log.isInfoEnabled()) log.info("[DirectMapper:convertDatabase] Number of extracted triples : " +
				result.getSize());
		return result;
	}
	
	/**
	 * Convert a MySQL database into a RDF graph from a database Connection
	 * and a R2RML instance.
	 */
	public static SesameDataSet convertMySQLDatabase(Connection conn,
			String pathToR2RMLMappingDocument) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		return convertMySQLDatabase(conn, pathToR2RMLMappingDocument, null);
	}


}
