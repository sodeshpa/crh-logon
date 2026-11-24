/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.lang.reflect.Method;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.interceptor.Interceptors;

import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.DirectJpaDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.GenericBlobServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.reporting.PDFUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderOutputdocEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderOutputdocEntity;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.persistence.entities.common.SignatureEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.persistence.entities.views.OutputdocAccessEntity;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Named
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class GenericBlobServiceBean extends GenericBlobServiceDispatcher implements GenericBlobServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GenericBlobServiceBean.class);

	public BlobDTO fetchBlobWithStringKeyValue(String entity, String keyName, String keyValue, boolean authorized) {
		return fetchBlob(entity, keyName, keyValue, authorized, false);		
	}
	
	/**
	 * fetches a blob from any table containing blob data.
	 * this service will stream the data from the first blob attribute
	 * of the table (there should be no more blob attribute per table!)
	 * 
	 * @param entity
	 * @param keyName the attribute on the DB which contains the key
	 * @param keyValue the value of the attribute
	 * @return a BlobDTO which contains the data and mime type
	 */
	public BlobDTO fetchBlob(String entity, String keyName, Object keyValue, boolean authorized) {
		return fetchBlob(entity, keyName, keyValue, authorized, true);
	}

	private BlobDTO fetchBlob(String entity, String keyName, Object keyValue, boolean authorized, boolean convertToLong) {
		// [[
		BlobDTO result = null;
		Object[][] params = {{"entity", entity},
							{"keyName", keyName},
							{"keyValue", keyValue},
							{"authorized", authorized + ""},
							{"convertToLong", convertToLong + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchBlob", params);
		}
		// ]]
		try {
			BlobDTO bDTO = new BlobDTO();
			DirectJpaDao baseDao = getDaoProvider().getDirectJpaDao();
			String keyValueStr = keyValue.toString();
			
			if(convertToLong) {
				try {
					Long val = Long.parseLong(keyValueStr);
					keyValue = val;
				}
				catch (NumberFormatException ex) {
					// this is ok... just don't look for integers...
				}				
			}
			
			Object obj = baseDao.findSingle(
					"select obj from " + entity + " obj where " + keyName + "=?1",
					keyValue
			);
			// TODO: define a 'not_found' image and always return that...
			if (obj == null) {
				/* TODO: this would be better...
				return fetchBlob(Image.class.getName(), "imageId", IMG_NOTFOUND_ID);
				*/
				return result = bDTO;
			}
			
			if (!authorized) {
				authorize(obj);
			}
			Class clazz = obj.getClass();
			Method dataMeth = findMethodReturning(clazz, byte[].class);
			Method mimeMeth = findMethodReturning(clazz, MimeTypeBsEntity.class);
			
			if (obj instanceof OutputdocEntity) {
				OutputdocEntity doc = (OutputdocEntity)obj;
				if (doc.getDataMimeType().getMimeTypeCd().equals(Constant.MIME_TYPE_PDF)) {
					// TODO (emoke Oct 14, 2009): Do not know if this is needed for other types as well, but for the moment this will do,
					// boon 20100430: Setting the name for other filetypes has influence on the HTTP headers (see GenericBlobServlet).
					bDTO.setName(doc.getOutputdocName() + "." + doc.getDataMimeType().getFileExtension());
				}
			}
			
			byte[] stream = (byte[])invokeMethod(obj, dataMeth, (Object[])null);
			MimeTypeBsEntity mimeType = (MimeTypeBsEntity) invokeMethod(obj, mimeMeth, (Object[])null);
			bDTO.setStream(stream);
			bDTO.setMime(mimeType.getMimeType());
			bDTO.setExtension(mimeType.getFileExtension());
			
			return result = bDTO;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}		
	}

		
	public BlobDTO fetchPdfImage(String imageName) {
		BlobDTO blobDTO = null;
		
		if (StringUtils.isNotEmpty(imageName)) {
			blobDTO = fetchBlobWithStringKeyValue("ImageEntity", "imageName", imageName, true);
			if (blobDTO != null) {
				PDFUtils.checkImageIntegrity(blobDTO, imageName);
				if ((blobDTO.getStream() == null) 
						|| (blobDTO.getStream().length <= 0)) {
					return null;
				}
			}
		}
		
		return blobDTO;
	}

    private static Object invokeMethod(Object dto, Method m, Object[] args) {
        Object ret;
        try {
            if (m == null) {
                throw new IllegalArgumentException("Method m must not be NULL!");
            }
            ret = m.invoke(dto, args);
            return ret;
        }
        catch (Throwable t) {
            throw new IllegalStateException("Couldn't invoke " + m +
                    " method in " + "Object: " + dto, t);
    	}
    }

    /**
     * finds the method inside a class which returns data[].
     * This is used to determine the data part of blob-entity.
     * 
     * @param clazz
     * @return
     */
	private static Method findMethodReturning(Class clazz, Class returnType) {
		// [[
		Method result = null;
		Object[][] params = {{"clazz", clazz},
							{"returnType", returnType}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GenericBlobServiceBean.class, "findMethodReturning", params);
		}
		// ]]
		try {
			Method[] methods = clazz.getMethods();
			for (Method m: methods) {
				if (m.getReturnType().equals(returnType)) {
					LOG.debug("---> method found: " + m);
					if (result == null) {
						result = m;
					}
					else {
						throw new LogonException("Multiple methods found in " + clazz + " returning " + returnType, Reason.RES_ERROR_IMPL);
					}
				}
			}
			
			return result;
		}
		catch (Throwable t) {
			ExceptionUtil.errException(LOG, t, params);
			return result = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private void authorize(Object obj) {
		
		Long userId = getUserId();
		if (userId == null) {
			
			throw new LogonException("No userID found", Reason.RES_ERROR_SECURITY_TAMPERING);
		}
		if (AuthUtils.technical.equals(userId)) {
			
			return;
		}
		UserEntity user = getDaoProvider().getUserEntityDao().findById(userId);
		if (user == null) {
			
			throw new LogonException("User not found", Reason.RES_ERROR_SECURITY_TAMPERING);
		}
		Class clazz = obj.getClass();
		if (clazz.equals(SignatureEntity.class)) {
			SignatureEntity signature = (SignatureEntity)obj;
			if ( (user.getSignature() == null) || (!user.getSignature().getSignatureId().equals(signature.getSignatureId())) ) {
				
				throw new LogonException("User " + user.getUserId() + " is not allowed to read " + obj.getClass().getSimpleName() + " " + signature.getSignatureId(), Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
			}
		}
		else if (clazz.equals(OutputdocEntity.class)) {
			OutputdocEntity outputdoc = (OutputdocEntity)obj;
			authorizeOutputDocAccess(outputdoc, user);
		}
	}

	/**
	 * Function checking if the user has access to the given {@link OutputdocEntity}. Throws exception, in case not.
	 * An {@link OutputdocEntity} belongs either to a {@link CustorderOutputdocEntity} or an {@link OrderOutputdocEntity}, transitively to
	 * an {@link OrderEntity} or a {@link CustorderEntity}.
	 * Restriction attributes are included in the {@link OutputdocAccessEntity} view and are constructed to come either from the order
	 * or from the customer order.
	 * 
	 * Do NOT load the {@link OutputdocEntity}.DATA content ever for authorization check (performance reasons)!
	 * 
	 * @param obj
	 * @param user
	 */
	private void authorizeOutputDocAccess(OutputdocEntity outputdoc, UserEntity user) {
		
		Filter filter = new FilterImpl();

		filter.addExpression(Restrictions.eq(OutputdocAccessEntity.ATTR_OUTPUTDOC_ID, outputdoc.getOutputdocId()));
		JPAQuery<OutputdocAccessEntity> orderOutputDocAccessjpaQuery = getDaoProvider().getOutputdocAccessEntityDao().createJPAQuery();
		orderOutputDocAccessjpaQuery.setFilter(filter);
		orderOutputDocAccessjpaQuery.addSelect(SelectExpression.simpleSelect(OutputdocAccessEntity.ATTR_OUTPUTDOC_ID));
		Long cnt = getDaoProvider().getOutputdocAccessEntityDao().count(filter);
		
		if (cnt == 0) {
				throw new LogonException("User " + user.getUserId() + " is not allowed to read " + OutputdocAccessEntity.class.getSimpleName() + " " + outputdoc.getOutputdocId(), Reason.RES_ERROR_SECURITY_NOT_ALLOWED);		
		}
	}
	
}
