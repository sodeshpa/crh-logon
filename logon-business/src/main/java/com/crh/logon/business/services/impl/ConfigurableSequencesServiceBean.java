/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.beans.ConfigurableSequenceResult;
import com.crh.logon.business.services.dispatchers.ConfigurableSequencesServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigurableSequencesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.ConfSeqLock;
import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ConfigurableSequencesService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ConfSeqCounterEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PlantCompcodeEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.CompanycodeBsEntity;
import com.crh.logon.persistence.entities.refdata.MapExtraDnTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SeqUsageBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class ConfigurableSequencesServiceBean extends ConfigurableSequencesServiceDispatcher implements ConfigurableSequencesServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ConfigurableSequencesServiceBean.class);
	
	private static final int LOCKING_RETRY_INTERVAL_MS = 1000;
	private static final int LOCKING_MAX_ATTEMPTS = 60;
	private static final int LOCKING_MAX_VALIDITY_MI = 10;
	
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	
	private Date getCurrentDateFromDb(ConfSeqCounterEntity counter) {
		
		String query = "SELECT CURRENT_TIMESTAMP " +
				"FROM " + ConfSeqCounterEntity.class.getSimpleName() + " " +
				"WHERE " + ConfSeqCounterEntity.ATTR_CONF_SEQ_COUNTER_ID + " = ?1";
		
		return (Date)getDaoProvider().getDirectJpaDao().findSingle(query, counter.getConfSeqCounterId());
	}
	
	/**
	 * Locks all counter entities
	 * @param counters Set of counter id's to be locked. Must not be null.
	 * @return true if successfully locked, false if at least one of the counters is already locked
	 * @throws (Wrapped) OptimisticLockException if another thread concurrently writes one of these entities
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean lock (Set<Long> counters) {
		
		// validate
		if (counters.isEmpty()) {
			return true;
		}
		
		// load
		final ReadWriteDao<ConfSeqCounterEntity> dao = getDaoProvider().getConfSeqCounterEntityDao();
		final Filter<ConfSeqCounterEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.in(ConfSeqCounterEntity.ATTR_CONF_SEQ_COUNTER_ID, counters));
		List<ConfSeqCounterEntity> list = dao.find(filter);
		
		if (list.isEmpty()) {
			return true;
		}
		
		final Date now = getCurrentDateFromDb(list.get(0));
		
		/*
		 * Check whether all counters are currently free. This must be done in a separate loop in
		 * order to avoid a deadlock.
		 */
		for (ConfSeqCounterEntity counter : list) {
			if (Boolean.TRUE.equals(counter.getLocked()) && counter.getLockedTs() != null) {
				long age = now.getTime() - counter.getLockedTs().getTime();
				long maxAge = 1000 * 60L * LOCKING_MAX_VALIDITY_MI;
				if (age > maxAge) {
					String msg = String.format("Validity of lock for counter [%d] has timed out " +
							"after %d minutes. Allowing to lock again.",
							counter.getConfSeqCounterId(),
							LOCKING_MAX_VALIDITY_MI);
					LOG.warn(msg);
				}
				else {
					return false;
				}
			}
		}
		// update
		for (ConfSeqCounterEntity counter : list) {
			counter.setLocked(Boolean.TRUE);
			counter.setLockedTs(now);
		}
		return true;
	}
	
	/**
	 * Unlocks all counter entities
	 * @param counters Set of counter id's to be unlocked. Must not be null.
	 * @throws (Wrapped) OptimisticLockException if another thread concurrently writes one of these entities
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unlock (Set<Long> counters) {
		
		// validate
		if (counters.isEmpty()) {
			return;
		}
		
		// load
		final ReadWriteDao<ConfSeqCounterEntity> dao = getDaoProvider().getConfSeqCounterEntityDao();
		final Filter<ConfSeqCounterEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.in(ConfSeqCounterEntity.ATTR_CONF_SEQ_COUNTER_ID, counters));
		List<ConfSeqCounterEntity> list = dao.find(filter);
		
		// update
		for (ConfSeqCounterEntity counter : list) {
			counter.setLocked(Boolean.FALSE);
			counter.setLockedTs(null);
		}
	}
	
	/**
	 * Acquires a lock locking all sequence counter entities required to process the orders
	 * specified. If none of the orders requires a sequence counter, no locking will be done.
	 * @param contract
	 * @param sp
	 * @return A lock which encapsulates the orders and their counters
	 * @throws LogonException if the lock could not be acquired after LOCKING_MAX_ATTEMPTS
	 */
	public ConfSeqLock acquireLockForContractAndShippingpoint(SapContractEntity contract, ShippingpointEntity sp) {
		// [[
		ConfSeqLock result = null;
		Object[][] params = {{"contract", contract},
							{"sp", sp}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "acquireLockForContractAndShippingpoint", params);
		}
		// ]]
		try {
			
			final ConfSeqCounterEntity counter = getConfSeqCounter(contract, sp);
			final Long counterId = (counter != null) ? counter.getConfSeqCounterId() : null;
			
			final ConfSeqLock lock = new ConfSeqLock();
			lock.add(contract.getSapContractId(), sp.getShippingpointId(), counterId);
			
			acquireLock(lock);

			result = lock;
			return result;
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
	 * Acquires a lock locking all sequence counter entities required to process the orders
	 * specified. If none of the orders requires a sequence counter, no locking will be done.
	 * @param orders Set of order entities which will be processed in a single unit of work. Must not be null.
	 * @return A lock which encapsulates the orders and their counters
	 * @throws LogonException if the lock could not be acquired after LOCKING_MAX_ATTEMPTS
	 */
	public ConfSeqLock acquireLockForOrders(Collection<OrderEntity> orders) {
		// [[
		ConfSeqLock result = null;
		Object[][] params = {{"orders", orders}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "acquireLockForOrders", params);
		}
		// ]]
		try {
			
			// Counter and order entities should not leave this method since they should be loaded anew
			final ConfSeqLock lock = new ConfSeqLock();
			for (OrderEntity order : orders) {
				
				if (order.getOrderNumberFromSeqUsage() != null) {
					continue;
				}
				
				final SapContractEntity contract = order.getSapContract();
				final Long contractId = contract.getSapContractId();
				final ShippingpointEntity shippingpoint = order.getShippingpoint();
				final Long shippingpointId = shippingpoint.getShippingpointId();
				
				if (!lock.contains(contractId, shippingpointId)) {
					final ConfSeqCounterEntity counter = getConfSeqCounter(contract, shippingpoint);
					final Long counterId = (counter != null) ? counter.getConfSeqCounterId() : null;
					lock.add(contractId, shippingpointId, counterId);
				}
			}
			
			acquireLock(lock);

			result = lock;
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private void acquireLock(ConfSeqLock lock) {
		
		Set<Long> counters = lock.getCounters();
		
		boolean success = false;
		int attempts = 0;
		while (!success) {
			attempts++;
			try {
				success = getServiceFor(ConfigurableSequencesService.class).lock(counters);
			}
			catch (Exception t) {
				final String countersString = StringUtils.join(counters.toArray());
				final String msg = String.format("Exception while trying to acquire the lock for counters [%s].",
						countersString);
				LOG.warn(msg, t);
			}
			if (!success) {
				if (attempts >= LOCKING_MAX_ATTEMPTS) {
					final String countersString = StringUtils.join(counters.toArray());
					final String msg = String.format("Could not acquire lock for counters [%s] after %d attempts, aborting!",
							countersString, LOCKING_MAX_ATTEMPTS);
					throw new LogonException(msg, Reason.RES_ERROR_UNSPECIFIED);
				}
				try {
					Thread.sleep(LOCKING_RETRY_INTERVAL_MS);
				}
				catch (InterruptedException e) {
					LOG.warn("Sleep interrupted", e);
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	/**
	 * Releases the lock by unlocking all counters encapsulated by the lock.
	 * @param lock The lock to release. Must not be null.
	 * @throws LogonException if the lock could not be release after LOCKING_MAX_ATTEMPTS
	 */
	public void releaseLock(ConfSeqLock lock) {
		// [[
		Object[][] params = {{"lock", lock}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "releaseLock", params);
		}
		// ]]
		try {
			
			if (lock == null) {
				return;
			}
			final Set<Long> counters = lock.getCounters();
			
			int attempts = 0;
			while (true) {
				attempts++;
				try {
					getServiceFor(ConfigurableSequencesService.class).unlock(counters);
					return;
				}
				catch (Exception t) {
					final String countersString = StringUtils.join(counters.toArray());
					final String msg = String.format("Exception while trying to release the lock for counters [%s].",
							countersString);
					LOG.warn(msg, t);
				}
				if (attempts >= LOCKING_MAX_ATTEMPTS) {
					final String countersString = StringUtils.join(counters.toArray());
					final String msg = String.format("Could not release lock for counters [%s] after %d attempts!",
							countersString, LOCKING_MAX_ATTEMPTS);
					throw new LogonException(msg, Reason.RES_ERROR_UNSPECIFIED);
				}
				try {
					Thread.sleep(LOCKING_RETRY_INTERVAL_MS);
				}
				catch (InterruptedException e) {
					LOG.warn("Sleep interrupted", e);
					Thread.currentThread().interrupt();
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Finds the configurable sequence counter for a given order. If the order already has a
	 * sequence number, no counter entity will be returned.
	 * @param order The order for which the sequence counter shall be determined
	 * @return The corresponding counter, if any is required
	 */
	private ConfSeqCounterEntity getConfSeqCounter(SapContractEntity contract, ShippingpointEntity sp) {
		
		if (contract == null) {
			LOG.error("Cannot generate custom sequence number, no contract defined.");
			return null;
		}
		if (sp == null) {
			LOG.error("Cannot generate custom sequence number, no shippingpoint defined.");
			return null;
		}
			
		final String sapContractTypeCd = contract.getSapContractType().getSapContractTypeCd();
		
		SareaEntity sarea = contract.getSarea();
		Long sareaId = sarea.getSareaId();
		
		MapExtraDnTypeBsEntity dnTypeEntity = orderService.getMapExtraDN(sapContractTypeCd, sareaId);

		Long usageCd;
		if (dnTypeEntity != null && dnTypeEntity.getPrintCashSales()){
			usageCd = Constant.CASH_SALES_SEQ_USAGE_CD;
		}
		else if (dnTypeEntity != null && dnTypeEntity.getPrintExportInvoice()){
			usageCd = Constant.EXPORT_INVOICE_SEQ_USAGE_CD;
		}
		else{
			usageCd = Constant.DEFAULT_SEQ_USAGE_CD;
		}
		
		ConfSeqCounterEntity counter = getConfSeqCounterForUsageCd(sp, usageCd);
		
		if ((counter == null) && (!Constant.DEFAULT_SEQ_USAGE_CD.equals(usageCd))) {
			if (LOG.isInfoEnabled()) {
				LOG.info(String.format(
						"No TLGNC_CONF_SEQ_COUNTER for (shippingpointId=%d (plantCd=%s), usageCd=%d) fallback to DEFAULT_SEQ_USAGE_CD (usageCd=%d).",
						sp.getShippingpointId(),
						sp.getPlant().getPlantCd(),
						usageCd,
						Constant.DEFAULT_SEQ_USAGE_CD));
			}
			usageCd = Constant.DEFAULT_SEQ_USAGE_CD;
			counter = getConfSeqCounterForUsageCd(sp, usageCd);
		}
		
		return counter;
	}
	
	/**
	 * Finds the configurable sequence counter for a given order and usage code. Located in a
	 * separate method for reusing code when implementing the fall-back mechanism.
	 * @param order The order entity for which the sequence counter shall be determined. Must not be null.
	 * @param usageCd The usage code which has been determined beforehand
	 * @return The corresponding counter, if any is found
	 */
	private ConfSeqCounterEntity getConfSeqCounterForUsageCd(ShippingpointEntity sp, Long usageCd) {
		
		SeqUsageBsEntity usage = referentialDataService.findBsEntity(SeqUsageBsEntity.class, usageCd);
		if (usage == null) {
			if (!Constant.DEFAULT_SEQ_USAGE_CD.equals(usageCd)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("No SEQ_USAGE_CD " + usageCd + ", fallback to " + Constant.DEFAULT_SEQ_USAGE_CD);
				}
				usageCd = Constant.DEFAULT_SEQ_USAGE_CD;
				usage = referentialDataService.findBsEntity(SeqUsageBsEntity.class, usageCd);
			}
			if (usage == null) {
				LOG.error(MessageFormat.format("Cannot find conf seq counter usage entity for cd: {0}", usageCd));
				return null;
			}
		}
		
		final PlantBsEntity plant = sp.getPlant();
		final ReadWriteDao<PlantCompcodeEntity> plantCompcodeDao = getDaoProvider().getPlantCompcodeEntityDao();
		final Filter<PlantCompcodeEntity> filter = plantCompcodeDao.createFilter();
		filter.addExpression(Restrictions.eq(PlantCompcodeEntity.ATTR_PLANT, plant));
		final PlantCompcodeEntity plantCompcodeEntity = plantCompcodeDao.findSingle(filter);
		if (plantCompcodeEntity == null) {
			LOG.error(MessageFormat.format("Cannot generate custom sequence number, no PlantCompcodeEntity found for plant: {0}.", plant.getPlantCd()));
			return null;
		}
		
		return getConfSeqCounterForCompCdPlantAndUsage(plantCompcodeEntity.getCompanycode(), plant, usage);
	}
	
	/**
	 * Finds the configurable sequence counter for a given company code, plant and usage
	 * @param compCd company code. Must not be null.
	 * @param plant (delivery) plant. Must not be null.
	 * @param usage usage type (default, cash sales, export invoice). Must not be null.
	 * @return The corresponding counter, if any is found
	 */
	private ConfSeqCounterEntity getConfSeqCounterForCompCdPlantAndUsage(
			CompanycodeBsEntity compCd, PlantBsEntity plant, SeqUsageBsEntity usage) {
		
		ReadWriteDao<ConfSeqCounterEntity> dao = getDaoProvider().getConfSeqCounterEntityDao();
		/*
		 *  1: Most  specific: usage code, company code, plant, valid from
		 * read
		 */
		Filter<ConfSeqCounterEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_COMPANYCODE_CD,
				compCd.getCompanycodeCd()));
		filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_PLANT_CD,
				plant.getPlantCd()));
		filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_SEQ_USAGE_CD,
				usage.getSeqUsageCd()));
		filter.addExpression(Restrictions.le(ConfSeqCounterEntity.ATTR_VALID_FROM,
				DateTimeUtils.getMorningOfDay(new Date())));
		filter.addExpression(Order.desc(ConfSeqCounterEntity.ATTR_VALID_FROM));
		
	
		List<ConfSeqCounterEntity> counters = dao.find(filter);
		if(counters == null || counters.isEmpty()) {
			/*
			 *  2. Generic: usage code, company code, valid from
			 * read
			 */
			filter = new FilterImpl<>();
			filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_COMPANYCODE_CD,
					compCd.getCompanycodeCd()));
			filter.addExpression(Restrictions.isNull(ConfSeqCounterEntity.ATTR_PLANT_CD));
			filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_SEQ_USAGE_CD,
					usage.getSeqUsageCd()));
			filter.addExpression(Restrictions.le(ConfSeqCounterEntity.ATTR_VALID_FROM,
					DateTimeUtils.getMorningOfDay(new Date())));
			filter.addExpression(Order.desc(ConfSeqCounterEntity.ATTR_VALID_FROM));
			
		
			counters = dao.find(filter);
			
			
			if(counters == null || counters.isEmpty()) {
				/*
				 * not found
				 * read
				 */
				return null;
			}
		}
		
		//the first one of the list should be ok, because it is ordered ascending, and (ATTR_VALID_FROM and ATTR_COMPANYCODE_CD) are unique together
		return counters.get(0);
	}
	
	/**
	 * Retrieves the next sequence number based on the information in the lock
	 * TODO not used nor tested yet
	 * @param orderId The order id for which a sequence number shall be generated
	 * @param lock The lock providing serialization as well as the lock -> counter mapping. Must not be null.
	 * @return The next sequence number
	 */
	public ConfigurableSequenceResult next(Long contractId, Long shippingpointId, ConfSeqLock lock) {
		
		if (lock == null) {
			throw new IllegalArgumentException("Parameter 'lock' cannot be null!");
		}
		
		Long counterId = lock.get(contractId, shippingpointId);
		if (counterId == null) {
			return null;
		}
		
		final ReadWriteDao<ConfSeqCounterEntity> dao = getDaoProvider().getConfSeqCounterEntityDao();
		final ConfSeqCounterEntity counter = dao.findById(counterId);
		
		Long lastValue = counter.getLastValue();
		if(lastValue == null){
			lastValue = counter.getRangeFrom();
		}
		else{
			if(lastValue >= counter.getRangeTo()){
				//If value is exceeded, sequence starts with "Range From" again.
				//// FIXME (andras Apr 23, 2012): shouldnt be an exception here
				lastValue = counter.getRangeFrom();
			}else{
				lastValue++;
			}
		}
		counter.setLastValue(lastValue);
		dao.merge(counter);
		
		/*
		 * add leading zeros
		 */
		int maxLenght = counter.getRangeTo().toString().length(); // max length of the sequence number
		final String format = String.format("%%0%dd", maxLenght);
		final String lastValueSrting = String.format(format, lastValue);
		String prefix = counter.getPrefix();
		prefix = StringUtils.isEmpty(prefix) ? "" : prefix;
		
		final String sequenceNumber = prefix + lastValueSrting;
		
		return new ConfigurableSequenceResult(sequenceNumber, counter.getSeqUsage());
	}
	
	/**
	 * Retrieves the next sequence number if a counter is found for given company code, plant and
	 * usage.
	 * @deprecated Will be replaced by {@link ConfigurableSequencesServiceBean#next(Long, ConfSeqLock)}
	 * in next release
	 * @param companyCodeCd The company code
	 * @param plantCd The (delivery) plant code
	 * @param usageCd The usage code (default, cash sales, export invoice)
	 * @return The next sequence number or null, if no matching counter is found.
	 */
	@Deprecated
	public String next(String companyCodeCd, String plantCd, Long usageCd){
		// [[
		String result = null;
		Object[][] params = {{"companyCodeCd", companyCodeCd},
							{"plantCd", plantCd},
							{"usageCd", usageCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "next", params);
		}
		// ]]
		try {
			ReadWriteDao<ConfSeqCounterEntity> confSeqCounterEntityDao = getDaoProvider().getConfSeqCounterEntityDao();
			/*
			 *  1: Most  specific: usageCode, company, plant, valid_from
			 * read
			 */
			Filter<ConfSeqCounterEntity> filter = new FilterImpl<>();
			filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_COMPANYCODE_CD, companyCodeCd));
			filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_PLANT_CD, plantCd));
			filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_SEQ_USAGE_CD, usageCd));
			filter.addExpression(Restrictions.le(ConfSeqCounterEntity.ATTR_VALID_FROM, DateTimeUtils.getMorningOfDay(new Date())));
			filter.addExpression(Order.desc(ConfSeqCounterEntity.ATTR_VALID_FROM));
			
		
			List<ConfSeqCounterEntity> list = confSeqCounterEntityDao.find(filter);
			if(list == null || list.isEmpty()) {
				/*
				 *  2. Generic: usagecode, company, valid_from
				 * read
				 */
				filter = new FilterImpl<>();
				filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_COMPANYCODE_CD, companyCodeCd));
				filter.addExpression(Restrictions.isNull(ConfSeqCounterEntity.ATTR_PLANT_CD));
				filter.addExpression(Restrictions.eq(ConfSeqCounterEntity.ATTR_SEQ_USAGE_CD, usageCd));
				filter.addExpression(Restrictions.le(ConfSeqCounterEntity.ATTR_VALID_FROM, DateTimeUtils.getMorningOfDay(new Date())));
				filter.addExpression(Order.desc(ConfSeqCounterEntity.ATTR_VALID_FROM));
				
			
				list = confSeqCounterEntityDao.find(filter);
				
				
				if(list == null || list.isEmpty()) {
					/*
					 * not found
					 * read
					 */
					return null;
				}
			}
			
			//the first one of the list should be ok, because it is ordered ascending, and (ATTR_VALID_FROM and ATTR_COMPANYCODE_CD) are unique together
			ConfSeqCounterEntity counterEntity = list.get(0);
			 
			
			Long lastValue = counterEntity.getLastValue();
			if(lastValue == null){
				lastValue = counterEntity.getRangeFrom();
			}
			else{
				if(lastValue >= counterEntity.getRangeTo()){
					//If value is exceeded, sequence starts with "Range From" again.
					//// FIXME (andras Apr 23, 2012): shouldnt be an exception here
					lastValue = counterEntity.getRangeFrom();
				}else{
					/*
					 * increase
					 */
					lastValue++;
				}
			}
			counterEntity.setLastValue(lastValue);
			/*
			 * update, merge
			 */
			confSeqCounterEntityDao.merge(counterEntity);
			
			/*
			 * add leading zeros
			 */
			
			int maxLenght = counterEntity.getRangeTo().toString().length(); // max length of the sequence number
			String format = String.format("%%0%dd", maxLenght);
			String lastValueSrting = String.format(format, lastValue);
			String prefix = counterEntity.getPrefix();
			prefix = StringUtils.isEmpty(prefix) ? "" : prefix;
			/*
			 * return
			 */
			result = prefix + lastValueSrting;
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
}
