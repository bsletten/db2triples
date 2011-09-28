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
 * R2RML Model : SubjectMap interface
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

import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;

import antidot.r2rml.exception.InvalidR2RMLSyntaxException;

/**
 * @author   jh
 */
public interface ISubjectMap {

	/**
	 * This property specifies an RDF IRI or blank node. The specified value will be used as the subject for all the RDF triples from a logical table row.
	 * @return
	 */
	public Resource getSubject();

	/**
	 * @param  subject
	 */
	public void setSubject(Resource subject);

	/**
	 * This property specifies the name of a column in the logical table. The value in this column of a logical table row is used as the subject for all the RDF triples generated from the row.
	 * @return
	 */
	public String getColumn();

	/**
	 * @param  column
	 */
	public void setColumn(String column);

	/**
	 * This optional property, for use in place of rr:column, specifies a template (or format string) to construct a value for use as a subject, based on values from one or more columns from a logical table row. Column names are case-sensitive and must be enclosed within curly braces. Use of curly braces in the template, for any purpose other than enclosing column names, must be escaped by a backslash character.
	 * @return
	 */
	public String getTemplate();

	/**
	 * @param template
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setTemplate(String template) throws InvalidR2RMLSyntaxException;

	/**
	 * This optional property specifies an RDFS class. The subject value generated for a logical table row will be asserted as an instance of this RDFS class.
	 * @return
	 */
	public String getRdfsClass();

	/**
	 * @param  rdfsClass
	 */
	public void setRdfsClass(String rdfsClass);

	/**
	 * This optional property specifies a graph IRI. All the RDF triples generated from a logical table row will be stored in the specified named graph. A special IRI, rr:defaultGraph, may be used to specify that the generated triples should be stored in the default graph. If the user specifies the default graph and also one or more named graphs, then the generated triples will be stored in the default graph as well as in each of the specified named graphs.
	 * @return
	 */
	public Set<URI> getGraphs();

	/**
	 * @param  graph
	 */
	public void setGraphs(Set<URI> graph);

	/**
	 * This optional property specifies the name of a column in the logical table. The value from this column of a logical table row is used as the graph name where all the triples generated from a logical table row will be stored. There is no restriction on maximum cardinality of this property. If for a row, value from the specified column is NULL, then the triples generated from that row will be stored in the default graph.
	 * @return
	 */
	public Set<String> getGraphColumns();

	/**
	 * @param  graphColumns
	 */
	public void setGraphColumns(Set<String> graphColumns);

	/**
	 * This optional property, for use in place of rr:graphColumn, specifies a template (or format string) to construct a value, for use as a graph IRI, based on one or more columns from a logical table row. Column names are case-sensitive and must be enclosed within curly braces. Use of curly braces in the template, for any purpose other than enclosing column names, must be escaped by a backslash character.
	 * @return
	 */
	public Set<String> getGraphTemplates();

	/**
	 * @param graphTemplates
	 * @throws InvalidR2RMLSyntaxException
	 */
	public void setGraphTemplates(Set<String> graphTemplates) throws InvalidR2RMLSyntaxException;

	/**
	 * This optional property specifies an expression that allows, at query processing time, use of indexes on any (underlying) relational table when accessing based on a value of a column (defined as an expression) in the logical table. The specified expression must be usable in the WHERE clause of a SQL query. Specifically, all the column names must be actual column names in the associated logical table. Case-sensitive column names must be enclosed within curly braces.
	 * @return
	 */
	public String getInverseExpression();

	/**
	 * @param  inverseExpression
	 */
	public void setInverseExpression(String inverseExpression);

	/**
	 * This property specifies an RDF term type. It is relevant in a SubjectMap only if the SubjectMap has a rr:column property or a rr:template property. The generated subject component will be of the specified RDF term type. The maximum cardinality of this property is 1. If not specified, the generated subject component will be an IRI. Note : the term type is only "IRI" or "BlankNode".
	 * @return
	 */
	public TermType getTermType();

	/**
	 * @param  type
	 */
	public void setTermType(TermType type);

}
