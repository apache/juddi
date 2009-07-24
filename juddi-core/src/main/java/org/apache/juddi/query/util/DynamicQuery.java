/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.juddi.query.util;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.EntityManager;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author Steve Viens (steve@viens.net)
 */
public class DynamicQuery {
	public static String CLAUSE_WHERE = "where";
	public static String CLAUSE_GROUPBY = "group by";
	public static String CLAUSE_ORDERBY = "order by";
	public static String CLAUSE_HAVING = "having";
	public static String OPERATOR_OR = "or";
	public static String OPERATOR_AND = "and";
	public static String PREDICATE_EQUALS = "=";
	public static String PREDICATE_NOTEQUALS = "<>";
	public static String PREDICATE_LIKE = "like";
	public static String PREDICATE_IN = "in";
	public static String PREDICATE_GREATERTHAN = ">";
	public static String PREDICATE_LESSTHAN = "<";
	public static String SORT_ASC = "asc";
	public static String SORT_DESC = "desc";
	public static String WILDCARD = "%";
	
	private Vector<Object> values = null;
	private StringBuffer sql = null;

	public DynamicQuery() {
		this.values = new Vector<Object>();
		this.sql = new StringBuffer();
	} 

	public DynamicQuery(String sql) {
		this.values = new Vector<Object>();
		this.sql = new StringBuffer(sql);
	}

	public DynamicQuery append(String sql) {
		this.sql.append(sql);
		return this;
	}

	public DynamicQuery pad() {
		this.sql.append(" ");
		return this;
	}
	
	public DynamicQuery openParen() {
		this.sql.append("(");
		return this;
	}

	public DynamicQuery closeParen() {
		this.sql.append(")");
		return this;
	}

	public DynamicQuery param() {
		this.sql.append("?");
		return this;
	}
	
	public DynamicQuery comma() {
		this.sql.append(",");
		return this;
	}

	public DynamicQuery AND() {
		this.sql.append(OPERATOR_AND);
		return this;
	}
	
	public DynamicQuery OR() {
		this.sql.append(OPERATOR_OR);
		return this;
	}

	public DynamicQuery WHERE() {
		this.sql.append(CLAUSE_WHERE);
		return this;
	}

	public DynamicQuery IN() {
		this.sql.append(PREDICATE_IN);
		return this;
	}

	public DynamicQuery GROUPBY() {
		this.sql.append(CLAUSE_GROUPBY);
		return this;
	}

	public DynamicQuery ORDERBY() {
		this.sql.append(CLAUSE_ORDERBY);
		return this;
	}

	public DynamicQuery HAVING() {
		this.sql.append(CLAUSE_HAVING);
		return this;
	}
	
	public DynamicQuery appendGroupedAnd(Parameter... params) {
		return appendCondition(OPERATOR_AND, params);
	}

	public DynamicQuery appendGroupedOr(Parameter... params) {
		return appendCondition(OPERATOR_OR, params);
	}

	public DynamicQuery appendCondition(String operator, Parameter... params) {
		if (params == null || operator == null)
			return this;
		
		openParen();
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				append(params[i].getName()).pad().append(params[i].getPredicate()).pad().param();
				addValue(params[i].getValue());
				
				if (i + 1 < params.length) {
					pad().append(operator).pad();
				}
			}
		}
		closeParen().pad();
		return this;
		
	}
	
	public DynamicQuery appendInListWithAnd(String term, List<?> list) {
		if (list == null || list.size() == 0)
			return this;
		
		AND().pad();
		
		return appendInList(term, list);
	}
	
	public DynamicQuery appendInList(String term, List<?> list) {
		if (list == null || list.size() == 0)
			return this;
		
		append(term).pad().IN().pad().openParen().pad();
		int count = 0;
		for (Object item : list) {
			param();
			addValue(item);
			
			if (count + 1 < list.size())
				comma().pad();
			
			count++;
		}
		closeParen().pad();
		return this;
	}

	public void addValue(Object obj) {
		this.values.addElement(obj);
	}
	
	public Query buildJPAQuery(EntityManager em) {
		StringTokenizer tokenizer = new StringTokenizer(sql.toString(),"?");
		StringBuffer sqlBuffer = new StringBuffer();
		int numberOfTokens = tokenizer.countTokens();
		for (int i=1; i<numberOfTokens; i++) {
			sqlBuffer.append(tokenizer.nextToken() + "?" + i);
		}
		if (tokenizer.hasMoreTokens()) sqlBuffer.append(tokenizer.nextToken());
		Query qry = em.createQuery(sqlBuffer.toString());
		
		for (int i = 0; i < values.size(); i++)
			qry.setParameter(i + 1, values.elementAt(i));
			
		return qry;
	}

	public String toString() { 
		StringBuffer buffer = new StringBuffer(sql.toString());
		buffer.append("\n\n");
	
		for (int i=0; i<values.size(); i++) {
			Object obj = values.elementAt(i);
	  
			buffer.append(i+1);
			buffer.append("\t");
			buffer.append(obj.getClass().getName());
			buffer.append("\t");
			buffer.append(obj.toString());
			buffer.append("\n");
		}
	    
		return buffer.toString();
	}
	
	public static class Parameter {
		private String name;
		private Object value;
		private String predicate;
		
		public Parameter(String name, Object value, String predicate) {
			this.name = name;
			this.value = value;
			this.predicate = predicate;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}

		public String getPredicate() {
			return predicate;
		}
		public void setPredicate(String predicate) {
			this.predicate = predicate;
		}

		
	}
}
