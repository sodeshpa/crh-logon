/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.dao.util;

import java.lang.Exception;

/**
 * <P>An exception that provides information on a database access
 * error or other errors.
 */
public class DAOException extends Exception
{
	public static final int TYPE_UNKNOWN = -1;
	/**
	 * exception thrown if the input to a method is
	 * invalid
	 * Ex. calling an ins method with a null value object
	 */
	public static final int TYPE_INVALID_INPUT = 1;
	/**
	 * exception thrown if an db object needed to process can't
	 * be found
	 * Ex. nothing found by getRecByKey 
	 */
	public static final int TYPE_NOT_FOUND = 2;
	/**
	 * exception thrown if tcn is not up to date
	 */
	public static final int TYPE_DATA_NOT_UP_TO_DATE = 3;
	/**
	 * exception thrown if a PK is not unique
	 */
	public static final int TYPE_PK_NOT_UNIQUE = 4;
	/**
	 * Any SQL exception (jdbc) or User/System exception
	 */
	public static final int TYPE_DATA_TIER = 5;
	/**
	 * Any config related error
	 * Ex. unable to find datasource
	 */
	public static final int TYPE_CONFIG = 6;

	private int m_type = TYPE_UNKNOWN;
  
	/**
	 * Constructs a <code>DAOException</code> with the 
	 * specified detail message. 
	 *
	 * @param   msg   the detail message.
	 */
	public DAOException(String msg)
	{
		super(msg);
	}

	/**
	 * Constructs a <code>DAOException</code> with the 
	 * specified detail message. 
	 *
	 * @param   type   
	 * @param   msg   the detail message.
	 */
	public DAOException(int type, String msg)
	{
		super(msg);
		m_type = type;
	}
	
	/**
	 * Constructs a <code>DAOException</code> wrapping 
	 * an underlying exception. 
	 *
	 * @param   ex   the Throwable.
	 */
	public DAOException(Throwable ex)
	{
		super(ex.toString(), ex);
	}

	/**
	 * Constructs a <code>DAOException</code> wrapping 
	 * an underlying exception. 
	 *
	 * @param   type   
	 * @param   ex   the Throwable.
	 */
	public DAOException(int type, Throwable ex)
	{
		super(ex.toString(), ex);
		m_type = type;
	}

	/**
	 * Constructs a <code>DAOException</code> wrapping 
	 * an underlying exception and a message. 
	 *
	 * @param   msg   the detail message.
	 * @param   ex   the Throwable.
	 */
	public DAOException(String msg, Throwable ex)
	{
		super(msg, ex);
	}

	/**
	 * Constructs a <code>DAOException</code> wrapping 
	 * an underlying exception and a message. 
	 *
	 * @param   type   
	 * @param   msg   the detail message.
	 * @param   ex   the Throwable.
	 */
	public DAOException(int type, String msg, Throwable ex)
	{
		super(msg, ex);
	}

	public int getType() { return m_type; }

	public String toString() {
		StringBuffer msg = new StringBuffer(getMsgFromType());
		msg.append(": ");
		msg.append(super.getMessage());
		return msg.toString();
	}

	private String getMsgFromType() {
		switch (m_type) {
		  case TYPE_INVALID_INPUT:
			return "Invalid input";
		  case TYPE_NOT_FOUND:
			return "Not found";
		  case TYPE_DATA_NOT_UP_TO_DATE:
			return "Data not up to date";
		  case TYPE_PK_NOT_UNIQUE:
			return "PK not unique";
		  case TYPE_DATA_TIER:
			return "Data-tier";
		  case TYPE_CONFIG:
			return "Config";
		  default:
			return "Unknown";
		}
	}

}
