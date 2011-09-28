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
 * R2RML Model : SubjectMap class
 *
 * An element of a class which implements this interface, contains the rules
 * for generating the subject for a row in the logical table being mapped.
 * 
 * Reference : R2RML: RDB to RDF Mapping Language, W3C Working Draft 24 March 2011
 *
 * @author jhomo
 *
 */
package antidot.r2rml.model;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;

import antidot.r2rml.exception.InvalidR2RMLStructureException;
import antidot.r2rml.exception.InvalidR2RMLSyntaxException;
import antidot.r2rml.tools.R2RMLToolkit;

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
