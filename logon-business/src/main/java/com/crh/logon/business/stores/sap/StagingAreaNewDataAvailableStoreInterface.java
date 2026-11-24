package com.crh.logon.business.stores.sap;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.views.stagingarea.BpnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.MnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.MnnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.SdnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.StagingAreaNewDataAvailableBaseEntity;
import com.crh.logon.persistence.entities.views.stagingarea.VehnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.VnStagingAreaNewDataAvailableEntity;

@Local
public interface StagingAreaNewDataAvailableStoreInterface extends GenericReadOnlyInterface<StagingAreaNewDataAvailableBaseEntity> {

	/**
	 * Returns the timestamp when the last unprocessed request arrived and was persisted
	 * in the SAP staging area.
	 * 
	 * returns {#link {@link StagingAreaNewDataAvailableBaseEntity} containing the representant fields or null if no such request is present
	 */
	public StagingAreaNewDataAvailableBaseEntity getLastNewDataAvailableForCronName(String cronstatusName);
	
	
	/**
	 * Returns the view entity representing the last new unprocessed MN request.
	 * 
	 * @return BpnStagingAreaNewDataAvailableEntity
	 */
	public BpnStagingAreaNewDataAvailableEntity getBPNLastNew();
	
	/**
	 * Returns the view entity representing the last new unprocessed MN request.
	 * 
	 * @return BpnStagingAreaNewDataAvailableEntity
	 */
	public SdnStagingAreaNewDataAvailableEntity getSDNLastNew();
	
	/**
	 * Returns the view entity representing the last new unprocessed MN request.
	 * 
	 * @return MnnStagingAreaNewDataAvailableEntity
	 */
	public MnnStagingAreaNewDataAvailableEntity getMNNLastNew();
	
	/**
	 * Returns the view entity representing the last new unprocessed MN request.
	 * 
	 * @return VehnStagingAreaNewDataAvailableEntity
	 */
	public VehnStagingAreaNewDataAvailableEntity getVEHNLastNew();
	
	/**
	 * Returns the view entity representing the last new unprocessed MN request.
	 * 
	 * @return VnStagingAreaNewDataAvailableEntity
	 */
	public VnStagingAreaNewDataAvailableEntity getVNLastNew();
	
	/**
	 * Returns the view entity representing the last new unprocessed MN request.
	 * 
	 * @return MnStagingAreaNewDataAvailableEntity
	 */
	public MnStagingAreaNewDataAvailableEntity getMNLastNew();

}
