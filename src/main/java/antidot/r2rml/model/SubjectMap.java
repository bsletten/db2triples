/***************************************************************************
 *
 * R2RML Model : SubjectMap class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	SubjectMap.java
 *
 * Description		:	An element of this class, called a SubjectMap,
 * 						contains the rules for generating the subject
 * 						for a row in the logical table being mapped.
 * 
 * Reference		:	RDF Vocabulary for R2RML
 * 						R2RML: RDB to RDF Mapping Language
 * 						W3C Working Draft 24 March 2011
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package antidot.r2rml.model;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;

import antidot.r2rml.exception.InvalidR2RMLStructureException;
import antidot.r2rml.exception.InvalidR2RMLSyntaxException;
import antidot.r2rml.tools.R2RMLToolkit;

/**
 * @author  jh
 */
public class SubjectMap implements ISubjectMap {

	/**
	 */
	private Resource subject;
	/**
	 */
	private String column;
	/**
	 */
	private String template;
	/**
	 */
	private String rdfsClass;
	/**
	 */
	private Set<URI> graphs;
	/**
	 */
	private Set<String> graphColumns;
	/**
	 */
	private Set<String> graphTemplates;
	/**
	 */
	private String inverseExpression;
	/**
	 */
	private TermType termType;

	public SubjectMap(Resource subject, String column, String template,
			String rdfsClass, Set<URI> graphs, Set<String> graphColumns,
			Set<String> graphTemplates, String inverseExpression,
			TermType termType) throws InvalidR2RMLStructureException,
			InvalidR2RMLSyntaxException {
		super();

		// Check properties consistency
		if ((column == null) && (subject == null) && (template == null)) {
			throw new InvalidR2RMLStructureException(
					"[SubjectMap:SubjectMap] column or subject or template have to be specified.");
		} else if ((column != null) && (subject != null)) {
			throw new InvalidR2RMLStructureException(
					"[SubjectMap:SubjectMap] Ambiguity between column and subject : the two "
							+ "are specified but just one is required.");
		} else if ((column != null) && (template != null)) {
			throw new InvalidR2RMLStructureException(
					"[SubjectMap:SubjectMap] Ambiguity between column and template : the two "
							+ "are specified but just one is required.");
		} else if ((subject != null) && (template != null)) {
			throw new InvalidR2RMLStructureException(
					"[SubjectMap:SubjectMap] Ambiguity between subject and template : the two "
							+ "are specified but just one is required.");
		}
	
		// Check termType
		// IRI by default
		if (termType == null)
			termType = TermType.IRI;
		// "Literal" is not valid for a SubjectMap
		if (termType.equals(TermType.LITERAL))
			throw new InvalidR2RMLStructureException(
					"[SubjectMap:SubjectMap] \"Literal\" term type is not valid for a subjectMap object.");

		this.subject = subject;
		this.column = column;
		setTemplate(template);
		this.rdfsClass = rdfsClass;
		setGraphs(graphs);
		setGraphColumns(graphColumns);
		setGraphTemplates(graphTemplates);
		this.inverseExpression = inverseExpression;
		this.termType = termType;
	}

	/**
	 * @return
	 */
	public Resource getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 */
	public void setSubject(Resource subject) {
		this.subject = subject;
	}

	/**
	 * @return
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @param column
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * @return
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setTemplate(String template) throws InvalidR2RMLSyntaxException {
		if (!R2RMLToolkit.checkCurlyBraces(template))
			throw new InvalidR2RMLSyntaxException(
					"A synthax error have be found in your R2RML expression : "
							+ template);
		this.template = template;
	}

	/**
	 * @return
	 */
	public Set<URI> getGraphs() {
		return graphs;
	}

	/**
	 * @param graphs
	 */
	public void setGraphs(Set<URI> graphs) {
		this.graphs = new HashSet<URI>();
		for (URI graph : graphs) {
			this.graphs.add(graph);
		}
	}

	/**
	 * @return
	 */
	public Set<String> getGraphColumns() {
		return graphColumns;
	}

	/**
	 * @param graphColumns
	 */
	public void setGraphColumns(Set<String> graphColumns) {
		this.graphColumns = new HashSet<String>();
		for (String graphColumn : graphColumns) {
			this.graphColumns.add(graphColumn);
		}
	}

	/**
	 * @return
	 */
	public Set<String> getGraphTemplates() {
		return graphTemplates;
	}

	/**
	 * @param graphTemplates
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setGraphTemplates(Set<String> graphTemplates) throws InvalidR2RMLSyntaxException {
		this.graphTemplates = new HashSet<String>();
		for (String graphTemplate : graphTemplates) {
			if (!R2RMLToolkit.checkCurlyBraces(graphTemplate))
				throw new InvalidR2RMLSyntaxException(
						"A synthax error have be found in your R2RML expression : "
								+ graphTemplate);
			this.graphTemplates.add(graphTemplate);
		}
	}

	/**
	 * @return
	 */
	public String getInverseExpression() {
		return inverseExpression;
	}

	/**
	 * @param inverseExpression
	 */
	public void setInverseExpression(String inverseExpression) {
		this.inverseExpression = inverseExpression;
	}

	/**
	 * @return
	 */
	public String getRdfsClass() {
		return rdfsClass;
	}

	/**
	 * @param rdfsClass
	 */
	public void setRdfsClass(String rdfsClass) {
		this.rdfsClass = rdfsClass;

	}

	/**
	 * @return
	 */
	public TermType getTermType() {
		return termType;
	}

	/**
	 * @param termType
	 */
	public void setTermType(TermType termType) {
		this.termType = termType;

	}

}
