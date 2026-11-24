package com.crh.logon.business.stores.sap;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.stagingarea.BpnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.MnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.MnnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.SdnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.StagingAreaNewDataAvailableBaseEntity;
import com.crh.logon.persistence.entities.views.stagingarea.VehnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.VnStagingAreaNewDataAvailableEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class StagingAreaNewDataAvailableStore extends GenericReadOnlyProvider<StagingAreaNewDataAvailableBaseEntity> implements StagingAreaNewDataAvailableStoreInterface {
	

	public StagingAreaNewDataAvailableBaseEntity getLastNewDataAvailableForCronName(
			String cronstatusName) {
		if (cronstatusName.equals(Constant.CRONSTATUS_SAPIMPORT_BPN)) {
			return getBPNLastNew();
		}
		else if (cronstatusName.equals(Constant.CRONSTATUS_SAPIMPORT_MN)) {
			return getMNLastNew();
		}
		else if (cronstatusName.equals(Constant.CRONSTATUS_SAPIMPORT_SDN)) {
			return getSDNLastNew();
		}
		else if (cronstatusName.equals(Constant.CRONSTATUS_SAPIMPORT_MNN)) {
			return getMNNLastNew();
		}
		else if (cronstatusName.equals(Constant.CRONSTATUS_SAPIMPORT_VEHN)) {
			return getVEHNLastNew();
		}
		else if (cronstatusName.equals(Constant.CRONSTATUS_SAPIMPORT_VN)) {
			return getVNLastNew();
		}
		throw new LogonException("Unsupported cron job " + cronstatusName, Reason.RES_ERROR_IMPL);
	}

	
	/**
	 * Returns the last new data available timestamp for the given sap import related
	 * view.
	 * @return
	 */
	private StagingAreaNewDataAvailableBaseEntity getLastNewDataAvailable(Class<? extends StagingAreaNewDataAvailableBaseEntity> clazz) {
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		EntityType<? extends StagingAreaNewDataAvailableBaseEntity> entity = em.getEntityManagerFactory().getMetamodel().entity(clazz);
		CriteriaQuery<? extends StagingAreaNewDataAvailableBaseEntity> createQuery = criteriaBuilder.createQuery(clazz);
		createQuery.from(entity);
		
		TypedQuery<? extends StagingAreaNewDataAvailableBaseEntity> typedQuery = em.createQuery(createQuery);

		try {			
			return typedQuery.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}


	public BpnStagingAreaNewDataAvailableEntity getBPNLastNew() {
		return (BpnStagingAreaNewDataAvailableEntity) getLastNewDataAvailable(BpnStagingAreaNewDataAvailableEntity.class);
	}


	public SdnStagingAreaNewDataAvailableEntity getSDNLastNew() {
		return (SdnStagingAreaNewDataAvailableEntity) getLastNewDataAvailable(SdnStagingAreaNewDataAvailableEntity.class);
	}


	public MnnStagingAreaNewDataAvailableEntity getMNNLastNew() {
		return (MnnStagingAreaNewDataAvailableEntity) getLastNewDataAvailable(MnnStagingAreaNewDataAvailableEntity.class);
	}


	public VehnStagingAreaNewDataAvailableEntity getVEHNLastNew() {
		return (VehnStagingAreaNewDataAvailableEntity) getLastNewDataAvailable(VehnStagingAreaNewDataAvailableEntity.class);
	}

	public VnStagingAreaNewDataAvailableEntity getVNLastNew() {
		return (VnStagingAreaNewDataAvailableEntity) getLastNewDataAvailable(VnStagingAreaNewDataAvailableEntity.class);
	}


	public MnStagingAreaNewDataAvailableEntity getMNLastNew() {
		return (MnStagingAreaNewDataAvailableEntity) getLastNewDataAvailable(MnStagingAreaNewDataAvailableEntity.class);
	}

}
