/*
 * Author			 : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * The default implementation of the {@link JPAQuery} interface.
 * <br>
 * <b>Note</b>: The constructor of the class is <b>public</b>,
 * however, it is encouraged to use the corresponding method
 * of the DAOs to create a new instance of {@link JPAQueryImpl}.
 * 
 * @author AdNovum Informatik AG
 * 
 * @see JPAQuery
 * @see SelectExpression
 * @see GroupBy
 * @see Filter
 * @see FilterImpl
 */
public class JPAQueryImpl<T> implements JPAQuery<T> {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(JPAQueryImpl.class);
	
	private Class<T> clazz;
	
	private List<SelectExpression> selectExpressions = new ArrayList<SelectExpression>();
	private boolean distinct = false;
	private Filter<T> filter;
	private List<GroupBy> groupBys = new ArrayList<GroupBy>();

	public JPAQueryImpl(Class<T> clazz) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"clazz", clazz}};
			LOG.entering(this, "JPAQueryImpl", params);
		}
		// ]]
		try {
			this.clazz = clazz;
			String identificationVariable = ThreadLocalIdentificationVariable.getIdentificationVariable();
			if (StringUtils.isBlank(identificationVariable)) {
				identificationVariable = "x";
			}
			ThreadLocalIdentificationVariable.setIdentificationVariable(identificationVariable);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void addSelect(SelectExpression expression) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"expression", expression}};
			LOG.entering(this, "addSelectExpression", params);
		}
		// ]]
		try {
			if (expression == null) {
				return;
			}
			selectExpressions.add(expression);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public Filter<T> getFilter() {
		return filter;
	}

	public void setFilter(Filter<T> filter) {
		this.filter = filter;
	}

	public void addGroupBy(String propertyName) {
		if (StringUtils.isBlank(propertyName)) {
			return;
		}
		groupBys.add(new GroupBy(propertyName));
	}
	
	public StringBuilder toJPAQL() {
		// [[
		StringBuilder result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "toJPAQL");
		}
		// ]]
		try {
			StringBuilder sb = new StringBuilder();
			
			if (selectExpressions.isEmpty()) {
				selectExpressions.add(SelectExpression.simpleSelect());
			}
			
			sb.append("select ");
			if (distinct) {
				sb.append("distinct ");
			}
			for (Iterator<SelectExpression> it = selectExpressions.iterator(); it.hasNext();) {
				SelectExpression expression = it.next();
				sb.append(expression.toJPAQL());
				if (it.hasNext()) {
					sb.append(", ");
				}
			}
			sb.append(" from ");
			sb.append(clazz.getSimpleName());
			sb.append(" as ");
			sb.append(ThreadLocalIdentificationVariable.getIdentificationVariable());
			
			if (filter != null) {
				sb.append(filter.toJPAQL());
			}
			
			if (!groupBys.isEmpty()) {
				sb.append(" group by ");
			}
			for (Iterator<GroupBy> it = groupBys.iterator(); it.hasNext();) {
				GroupBy expression = it.next();
				sb.append(expression.toJPAQL());
				if (it.hasNext()) {
					sb.append(", ");
				}
			}
			
			if (filter != null) {
				List<Order> orders = filter.getOrderByExpressions();
				if (!orders.isEmpty()) {
					sb.append(" order by ");
				}
				for (Iterator it = orders.iterator(); it.hasNext();) {
					Order order = (Order) it.next();
					sb.append(order.toJPAQL());
					if (it.hasNext()) {
						sb.append(", ");
					}
				}
			}
			
			return result = sb;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	@Override
	public String toString() {
		ToStringBuilder tbs = new ToStringBuilder(this);
        tbs.append(super.toString());
        tbs.append("jpaSnippet", toJPAQL().toString());
        return tbs.toString();
	}

	@Override
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
}
