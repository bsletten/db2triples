/***************************************************************************
 *
 * Direct Mapping Main
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	Main
 * 
 * Fichier			:	DirectMapping.java
 *
 * Description			:	Interface between user and console.
 * 
 * Options d'execution	: Example : java -jar DirectMapping.jar -baseURI 
 * http://www.antidot.net/ -n -nativeout monNativeStore
 *  -user root -password root -sparql 
 * 	/path/to/transform.sparql -db my_db
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.dm.main;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.rio.RDFFormat;

import antidot.dm.core.DirectMapper;
import antidot.rdf.impl.sesame.SesameDataSet;
import antidot.sql.core.SQLConnector;
import antidot.sql.core.SQLExtractor;
import antidot.sql.model.Database;

@SuppressWarnings("static-access")
public class DirectMapping {

	// Log
	private static Log log = LogFactory.getLog(DirectMapping.class);

	private static Option userNameOpt = OptionBuilder.withArgName("userName")
			.hasArg().withDescription("Database user name").withLongOpt(
					"userName").create("user");

	private static Option passwordOpt = OptionBuilder.withArgName("password")
			.hasArg().withDescription("Database password").withLongOpt(
					"password").create("pass");

	private static Option URLOpt = OptionBuilder.withArgName("URL").hasArg()
			.withDescription(
					"URL of database (default : jdbc:mysql://localhost/)")
			.withLongOpt("URL").create("url");

	private static Option driverOpt = OptionBuilder.withArgName("driver")
			.hasArg().withDescription(
					"Driver to use (default : com.mysql.jdbc.Driver)")
			.withLongOpt("driver").create("driver");

	private static Option dbOpt = OptionBuilder.withArgName("databaseName")
			.hasArg().withDescription("database name").withLongOpt(
					"databaseName").create("db");

	private static Option baseURIOpt = OptionBuilder.withArgName("baseURI")
			.hasArg().withDescription(
					"base URI (default : http://foo.example/DB/)").withLongOpt(
					"baseURI").create("baseURI");

	private static Option nativeOpt = new Option("n",
			"Use native store (store in output directory path)");

	private static Option nativeStoreNameOpt = OptionBuilder.withArgName(
			"native_output").hasArg().withDescription(
			"native store output directory").withLongOpt("nativeOutput")
			.create("nativeout");

	private static Option forceOpt = new Option("f",
			"Force loading of existing repository (without remove data)");

	private static Option outputOpt = OptionBuilder.withArgName("output")
			.hasArg().withDescription("output (default : output)").withLongOpt(
					"output").create("out");

	private static Option transformSPARQLFile = OptionBuilder.withArgName(
			"sparql").hasArg().withDescription(
			"sparql transform request file (optionnal)").withLongOpt("sparql")
			.create("sparql");

	private static Option transformOutputFile = OptionBuilder
			.withArgName("outputSparql")
			.hasArg()
			.withDescription(
					"transformed graph output file (optionnal if sparql option is not specified, default : sparql_output otherwise)")
			.withLongOpt("sparqlOutput").create("sparqlout");

	private static Option rdfFormat = OptionBuilder
			.withArgName("format")
			.hasArg()
			.withDescription(
					"RDF syntax output format ('RDFXML', 'N3', 'NTRIPLES' or 'TURTLE')")
			.withLongOpt("format").create("format");

	public static void main(String[] args) {
		// Get all options
		Options options = new Options();
		options.addOption(userNameOpt);
		options.addOption(passwordOpt);
		options.addOption(URLOpt);
		options.addOption(driverOpt);
		options.addOption(dbOpt);
		options.addOption(baseURIOpt);
		options.addOption(forceOpt);
		options.addOption(nativeOpt);
		options.addOption(nativeStoreNameOpt);
		options.addOption(outputOpt);
		options.addOption(transformSPARQLFile);
		options.addOption(transformOutputFile);
		options.addOption(rdfFormat);

		// Init parameters
		String userName = null;
		String password = null;
		String url = null;
		String driver = null;
		String dbName = null;
		String baseURI = null;
		boolean useNativeStore = false;
		boolean forceExistingRep = false;
		String nativeOutput = null;
		String output = null;
		String sparql = null;
		String sparqlOutput = null;
		String format = null;

		// RDF Format output
		RDFFormat rdfFormat = RDFFormat.N3; // N3 by default

		// Option parsing
		// Create the parser
		CommandLineParser parser = new GnuParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			// Database settings
			// user name
			if (!line.hasOption("userName")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("DirectMapping", options);
				System.exit(-1);
			} else {
				userName = line.getOptionValue("userName");
			}
			// password
			if (!line.hasOption("password")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("DirectMapping", options);
				System.exit(-1);
			} else {
				password = line.getOptionValue("password");
			}
			// Database URL
			url = line.getOptionValue("URL", "jdbc:mysql://localhost/");
			// driver
			driver = line.getOptionValue("driver", "com.mysql.jdbc.Driver");
			// Database name
			if (!line.hasOption("databaseName")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("DirectMapping", options);
				System.exit(-1);
			} else {
				dbName = line.getOptionValue("databaseName");
			}
			// Base URI
			baseURI = line.getOptionValue("baseURI", "http://foo.example/DB/");
			// Use of native store ?
			useNativeStore = line.hasOption("n");
			// Name of native store
			if (useNativeStore && !line.hasOption("nativeOutput")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("DirectMapping", options);
				System.exit(-1);
			} else {
				nativeOutput = line.getOptionValue("nativeOutput");
			}
			// Force loading of repository
			forceExistingRep = line.hasOption("f");
			// Output
			output = line.getOptionValue("output", "output.n3");
			// SPARQL transformation
			if (line.hasOption("sparql")) {
				sparql = line.getOptionValue("sparql");
				sparqlOutput = line.getOptionValue("sparqlOutput",
						"output_sparql.n3");
			}
			// RDF Format
			if (line.hasOption("format")) {
				format = line.getOptionValue("format");
				if (format.equals("TURTLE"))
					rdfFormat = RDFFormat.TURTLE;
				else if (format.equals("RDFXML"))
					rdfFormat = RDFFormat.RDFXML;
				else if (format.equals("NTRIPLES"))
					rdfFormat = RDFFormat.NTRIPLES;
				else if (!format.equals("N3")) {
					if (log.isErrorEnabled())
						log
								.error("Unknown RDF format. Please use RDFXML, TURTLE, N3 or NTRIPLES.");
					System.exit(-1);
				}
			}

		} catch (ParseException exp) {
			// oops, something went wrong
			log.error("[DirectMapping:main] Parsing failed. Reason : "
					+ exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("DirectMapping", options);
			System.exit(-1);
		}

		// Open test database
		Connection conn = null;
		try {
			// Connect database
			conn = SQLConnector
					.connect(userName, password, url, driver, dbName);
			// Extract database model
			Database db = SQLExtractor.extractMySQLDatabase(conn, null);
			// Generate RDF graph
			SesameDataSet g = null;
			// Check nature of storage (memory by default)
			if (useNativeStore) {
				File pathToNativeOutputDir = new File(nativeOutput);
				if (pathToNativeOutputDir.exists() && !forceExistingRep) {
					if (log.isErrorEnabled())
						log
								.error("Directory "
										+ pathToNativeOutputDir
										+ "  already exists. Use -f option to force loading of existing repository.");
					System.exit(-1);
				}
				g = DirectMapper.generateDirectMapping(db, baseURI,
						nativeOutput);
			} else {
				File outputFile = new File(output);
				if (outputFile.exists() && !forceExistingRep) {
					if (log.isErrorEnabled())
						log
								.error("Output file "
										+ outputFile.getAbsolutePath()
										+ " already exists. Please remove it or modify ouput name option.");
					System.exit(-1);
				}
				g = DirectMapper.generateDirectMapping(db, baseURI);
				// Dump graph
				g.dumpRDF(output, rdfFormat);
			}
			if (sparql != null) {

				Long start = System.currentTimeMillis();
				String result = g.runSPARQLFromFile(sparql, rdfFormat);
				SesameDataSet gResult = new SesameDataSet();
				gResult.addString(result, rdfFormat);
				gResult.dumpRDF(sparqlOutput, rdfFormat);

				Float stop = Float.valueOf(System.currentTimeMillis() - start) / 1000;
				if (log.isInfoEnabled())
					log.info("[DirectMapping:main] SPARQL query executed in "
							+ stop + " seconds.");
				if (log.isInfoEnabled())
					log
							.info("[DirectMapping:main] Number of triples after transformation : "
									+ gResult.getSize());
				System.out.println("==========" + log.isInfoEnabled());

				/*
				 * Create Jena Graph String tmpFileName = "tmp_sesame_" +
				 * g.hashCode() + ".n3"; g.dumpRDF(tmpFileName, RDFFormat.N3);
				 * JenaGraph jDump = new JenaGraph(); // Load dumped RDF data
				 * jDump.loadDataFromFile(tmpFileName, "N3"); // Transform graph
				 * with SPARQL queries JenaGraph transformGraph = jDump
				 * .runSPARQLFromFile(sparql, "N3");
				 * transformGraph.dumpRDF(sparqlOutput, "N3");
				 */
			}
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
	}
}
