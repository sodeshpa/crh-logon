/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

//------------------------------------------------------------------------

package com.crh.logon.persistence.dao.util;


import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.math.BigDecimal;

import com.crh.logon.utils.StringUtils;

public final class JDBCImplUtil {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(JDBCImplUtil.class);

	/**
	 * Generate the where clause for the select SQL query.
	 * 
	 * @param restriction
	 * 			List of restriction columns.
	 * 
	 * @return
	 * 			SQL where clause.
	 */
	public static String generateWhereClause(Map<String, ColumnRestriction> restriction) {
		// [[
		String result = null;
		Object[][] params = {{"restriction", restriction}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(JDBCImplUtil.class, "generateWhereClause", params);
		}
		// ]]
		try {
			if (restriction == null || restriction.isEmpty()) {
				return result = "";
			}
			
			StringBuilder builder = new StringBuilder();
			builder.append(" where ");
			
			for (String columnName : restriction.keySet()) {
				ColumnRestriction restr = restriction.get(columnName);
			
				if (restr == null) {
					continue; // nothing to do
				}
				
				if (StringUtils.empty(restr.getColumnName()) || restr.getColumnValue() == null
						|| restr.getExpr() == null) {
					continue;
				}
				builder.append(restr.getColumnName());
				builder.append(" ");
				builder.append(restr.getExpr().toString());
				builder.append(" ");
				builder.append("?");
				builder.append(" and ");
			}
			return result = builder.substring(0, builder.length() - 5);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * 
	 * @param restriction
	 * @return
	 */
	public static List getValues(Map<String, ColumnRestriction> restriction) {
		// [[
		List result1 = null;
		Object[][] params = {{"restriction", restriction}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(JDBCImplUtil.class, "getValues", params);
		}
		// ]]
		try {
			if (restriction == null || restriction.isEmpty()) {
				return result1 = null;
			}
			
			List result = new ArrayList();
			for (String columnName : restriction.keySet()) {
				ColumnRestriction restr = restriction.get(columnName);
			
				if (restr == null) {
					continue; // nothing to do
				}
				if (restr.getColumnValue() == null) {
					continue;
				}
				result.add(restr.getColumnValue());
			}
			return result1 = result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}

	/*******************************************************************************
	***																			 ***
	*******************************************************************************/
	//------------------------------------------------------------------------
	public static int convertToint(BigDecimal value) {
		return (value != null) ? value.intValue() : 0;
	}

	//------------------------------------------------------------------------
	public static long convertTolong(BigDecimal value) {
		return (value != null) ? value.longValue() : (long) 0;
	}

	//------------------------------------------------------------------------
	public static BigDecimal convertToBigDecimal(BigDecimal value) {
		return value;
	}

	//------------------------------------------------------------------------
	public static String convertToString(BigDecimal value) {
		return (value != null) ? value.toString() : null;
	}

	//------------------------------------------------------------------------
	public static Integer convertToInteger(BigDecimal value) {
		return (value != null) ? new Integer(value.intValue()) : null;
	}

	//------------------------------------------------------------------------
	public static Long convertToLong(BigDecimal value) {
		return (value != null) ? new Long(value.longValue()) : null;
	}

	/*******************************************************************************
	***																			 ***
	*******************************************************************************/
	//------------------------------------------------------------------------
	public static int convertToint(int value) {
		return value;
	}

	//------------------------------------------------------------------------
	public static long convertTolong(int value) {
		return (long) value;
	}

	//------------------------------------------------------------------------
	public static BigDecimal convertToBigDecimal(int value) {
		return new BigDecimal(Integer.toString(value));
	}

	//------------------------------------------------------------------------
	public static String convertToString(int value) {
		return Integer.toString(value);
	}

	//------------------------------------------------------------------------
	public static Integer convertToInteger(int value) {
		return new Integer(value);
	}

	//------------------------------------------------------------------------
	public static Long convertToLong(int value) {
		return new Long((long) value);
	}

	/*******************************************************************************
	***																			 ***
	*******************************************************************************/
	//------------------------------------------------------------------------
	public static int getRsValueAsint(ResultSet rs, String columnName, boolean nullable)
			throws SQLException, DAOException {
		int res = rs.getInt(columnName);

		if (!nullable && rs.wasNull()) {
			String info = "fetched column value is NULL (columnName=" + columnName + ")";
			LOG.error(info);
			throw new DAOException(DAOException.TYPE_DATA_TIER, info);
		}

		return res;
	}

	//------------------------------------------------------------------------
	public static long getRsValueAslong(ResultSet rs, String columnName, boolean nullable)
			throws SQLException, DAOException {
		long res = rs.getLong(columnName);

		if (!nullable && rs.wasNull()) {
			String info = "fetched column value is NULL (columnName=" + columnName + ")";
			LOG.error(info);
			throw new DAOException(DAOException.TYPE_DATA_TIER, info);
		}

		return res;
	}

	//------------------------------------------------------------------------
	public static BigDecimal getRsValueAsBigDecimal(ResultSet rs, String columnName,
			boolean nullable) throws SQLException, DAOException {
		BigDecimal res = rs.getBigDecimal(columnName);

		if (!nullable && rs.wasNull()) {
			String info = "fetched column value is NULL (columnName=" + columnName + ")";
			LOG.error(info);
			throw new DAOException(DAOException.TYPE_DATA_TIER, info);
		}
		if (rs.wasNull()) {
			return null;
		}
		return res;
	}

	//------------------------------------------------------------------------
	public static String getRsValueAsString(ResultSet rs, String columnName, boolean nullable)
			throws SQLException, DAOException {
		String res = rs.getString(columnName);

		if (!nullable && rs.wasNull()) {
			String info = "fetched column value is NULL (columnName=" + columnName + ")";
			LOG.error(info);
			throw new DAOException(DAOException.TYPE_DATA_TIER, info);
		}

		if (rs.wasNull()) {
			return null;
		}
		return res;
	}

	//------------------------------------------------------------------------
	public static Integer getRsValueAsInteger(ResultSet rs, String columnName, boolean nullable)
			throws SQLException, DAOException {
		int res = rs.getInt(columnName);

		if (!nullable && rs.wasNull()) {
			String info = "fetched column value is NULL (columnName=" + columnName + ")";
			LOG.error(info);
			throw new DAOException(DAOException.TYPE_DATA_TIER, info);
		}

		if (rs.wasNull()) {
			return null;
		}

		return new Integer(res);
	}

	//------------------------------------------------------------------------
	public static Long getRsValueAsLong(ResultSet rs, String columnName, boolean nullable)
			throws SQLException, DAOException {
		long res = rs.getLong(columnName);

		if (!nullable && rs.wasNull()) {
			String info = "fetched column value is NULL (columnName=" + columnName + ")";
			LOG.error(info);
			throw new DAOException(DAOException.TYPE_DATA_TIER, info);
		}

		if (rs.wasNull()) {
			return null;
		}

		return new Long(res);
	}

	//------------------------------------------------------------------------
	public static java.util.Date getRsValueAsDate(ResultSet rs, String columnName, boolean nullable)
			throws SQLException, DAOException {
		java.util.Date res = rs.getDate(columnName);

		if (!nullable && rs.wasNull()) {
			String info = "fetched column value is NULL (columnName=" + columnName + ")";
			LOG.error(info);
			throw new DAOException(DAOException.TYPE_DATA_TIER, info);
		}

		if (rs.wasNull()) {
			return null;
		}

		return res;
	}

	//------------------------------------------------------------------------
	public static java.sql.Timestamp getRsValueAsTimestamp(ResultSet rs, String columnName,
			boolean nullable) throws SQLException, DAOException {
		java.sql.Timestamp res = rs.getTimestamp(columnName);

		if (!nullable && rs.wasNull()) {
			String info = "fetched column value is NULL (columnName=" + columnName + ")";
			LOG.error(info);
			throw new DAOException(DAOException.TYPE_DATA_TIER, info);
		}

		if (rs.wasNull()) {
			return null;
		}

		return res;
	}

	//------------------------------------------------------------------------
	public static Boolean getRsValueAsBoolean(ResultSet rs, String columnName, boolean nullable)
			throws SQLException, DAOException {
		Boolean res = rs.getBoolean(columnName);

		if (!nullable && rs.wasNull()) {
			String info = "fetched column value is NULL (columnName=" + columnName + ")";
			LOG.error(info);
			throw new DAOException(DAOException.TYPE_DATA_TIER, info);
		}
		if (rs.wasNull()) {
			return null;
		}
		return res;
	}

	/*******************************************************************************
	***																			 ***
	*******************************************************************************/

	public static void setPsValue(PreparedStatement ps, int index, Object value)
			throws SQLException {
		if (value instanceof String) {
			JDBCImplUtil.setPsValue(ps, index, false, (String) value);
		}
		else if (value instanceof Long) {
			JDBCImplUtil.setPsValue(ps, index, false, (Long) value);
		}
		else if (value instanceof Date) {
			JDBCImplUtil.setPsValue(ps, index, false, (Date) value);
		}
		else if (value instanceof Boolean) {
			JDBCImplUtil.setPsValue(ps, index, false, (Boolean) value);
		}
	}

	//------------------------------------------------------------------------
	public static void setPsValue(PreparedStatement ps, int idx, boolean nullable, int value)
			throws SQLException {
		ps.setInt(idx, value);
	}

	public static void setPsValue(PreparedStatement ps, int idx, boolean nullable, Boolean value)
			throws SQLException {
		ps.setBoolean(idx, value);
	}

	//------------------------------------------------------------------------
	public static void setPsValue(PreparedStatement ps, int idx, boolean nullable, long value)
			throws SQLException {
		ps.setLong(idx, value);
	}

	//------------------------------------------------------------------------
	public static void setPsValue(PreparedStatement ps, int idx, boolean nullable, BigDecimal value)
			throws SQLException {
		if (nullable && (value == null)) {
			//ps.setString(idx, "");
			ps.setNull(idx, Types.DECIMAL);
		}
		else {
			ps.setBigDecimal(idx, value);
		}
	}

	//------------------------------------------------------------------------
	public static void setPsValue(PreparedStatement ps, int idx, boolean nullable, String value)
			throws SQLException {
		if (nullable && (value == null)) {
			//ps.setString(idx, "");
			ps.setNull(idx, Types.VARCHAR);
		}
		else {
			ps.setString(idx, value);
		}
	}

	//------------------------------------------------------------------------
	public static void setPsValue(PreparedStatement ps, int idx, boolean nullable, Integer value)
			throws SQLException {
		if (nullable && (value == null)) {
			//ps.setString(idx, "");
			ps.setNull(idx, Types.INTEGER);
		}
		else {
			ps.setInt(idx, value.intValue());
		}
	}

	//------------------------------------------------------------------------
	public static void setPsValue(PreparedStatement ps, int idx, boolean nullable, Long value)
			throws SQLException {
		if (nullable && (value == null)) {
			//ps.setString(idx, "");
			ps.setNull(idx, Types.BIGINT);
		}
		else {
			ps.setLong(idx, value.intValue());
		}
	}

	//------------------------------------------------------------------------
	public static void setPsValue(PreparedStatement ps, int idx, boolean nullable,
			java.util.Date value) throws SQLException {
		if (nullable && (value == null)) {
			//ps.setString(idx, "");
			ps.setNull(idx, Types.DATE);
		}
		else {
			ps.setDate(idx, new java.sql.Date(value.getTime()));
		}
	}

	//------------------------------------------------------------------------
	public static void setPsValue(PreparedStatement ps, int idx, boolean nullable,
			java.sql.Timestamp value) throws SQLException {
		if (nullable && (value == null)) {
			//ps.setString(idx, "");
			ps.setNull(idx, Types.TIMESTAMP);
		}
		else {
			ps.setTimestamp(idx, value);
		}
	}

	/*******************************************************************************
	***																			 ***
	*******************************************************************************/
	//------------------------------------------------------------------------
	public static void updateRsValue(ResultSet rs, int idx, boolean nullable, int value)
			throws SQLException {
		rs.updateInt(idx, value);
	}

	//------------------------------------------------------------------------
	public static void updateRsValue(ResultSet rs, int idx, boolean nullable, long value)
			throws SQLException {
		rs.updateLong(idx, value);
	}

	//------------------------------------------------------------------------
	public static void updateRsValue(ResultSet rs, int idx, boolean nullable, BigDecimal value)
			throws SQLException {
		if (nullable && (value == null)) {
			rs.updateNull(idx);
		}
		else {
			rs.updateBigDecimal(idx, value);
		}
	}

	//------------------------------------------------------------------------
	public static void updateRsValue(ResultSet rs, int idx, boolean nullable, String value)
			throws SQLException {
		if (nullable && ((value == null) || (value.length() == 0))) {
			rs.updateNull(idx);
		}
		else {
			rs.updateString(idx, value);
		}
	}

	//------------------------------------------------------------------------
	public static void updateRsValue(ResultSet rs, int idx, boolean nullable, Integer value)
			throws SQLException {
		if (nullable && (value == null)) {
			rs.updateNull(idx);
		}
		else {
			rs.updateInt(idx, value.intValue());
		}
	}

	//------------------------------------------------------------------------
	public static void updateRsValue(ResultSet rs, int idx, boolean nullable, Long value)
			throws SQLException {
		if (nullable && (value == null)) {
			rs.updateNull(idx);
		}
		else {
			rs.updateLong(idx, value.longValue());
		}
	}

	//------------------------------------------------------------------------
	public static void updateRsValue(ResultSet rs, int idx, boolean nullable, Boolean value)
			throws SQLException {
		if (nullable && (value == null)) {
			rs.updateNull(idx);
		}
		else {
			rs.updateBoolean(idx, value);
		}
	}

	//------------------------------------------------------------------------
	public static void updateRsValue(ResultSet rs, int idx, boolean nullable, java.util.Date value)
			throws SQLException {
		if (nullable && (value == null)) {
			rs.updateNull(idx);
		}
		else {
			rs.updateDate(idx, new java.sql.Date(value.getTime()));
		}
	}

	//------------------------------------------------------------------------
	public static void updateRsValue(ResultSet rs, int idx, boolean nullable,
			java.sql.Timestamp value) throws SQLException {
		if (nullable && (value == null)) {
			rs.updateNull(idx);
		}
		else {
			rs.updateTimestamp(idx, value);
		}
	}

	/*******************************************************************************
	***																			 ***
	*******************************************************************************/
	//------------------------------------------------------------------------
	public static int handleWildcards(int value) {
		return value; // do nothing
	}

	//------------------------------------------------------------------------
	public static long handleWildcards(long value) {
		return value; // do nothing
	}

	//------------------------------------------------------------------------
	public static BigDecimal handleWildcards(BigDecimal value) {
		return value; // do nothing
	}

	//------------------------------------------------------------------------
	public static Integer handleWildcards(Integer value) {
		return value; // do nothing
	}

	//------------------------------------------------------------------------
	public static Long handleWildcards(Long value) {
		return value; // do nothing
	}

	//------------------------------------------------------------------------
	public static java.util.Date handleWildcards(java.util.Date value) {
		return value; // do nothing
	}

	//------------------------------------------------------------------------
	public static java.sql.Timestamp handleWildcards(java.sql.Timestamp value) {
		return value; // do nothing
	}

	//------------------------------------------------------------------------
	private JDBCImplUtil() {
		;
	}

}
