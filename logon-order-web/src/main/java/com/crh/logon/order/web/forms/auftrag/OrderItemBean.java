/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.services.orderdetail.dto.BimodalTruckOrderItemDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.utils.comparator.UomComparator;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnMaterialUsageBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;

/**
 * @author AdNovum Informatik AG
 */
public class OrderItemBean implements Serializable{

	private static final long serialVersionUID = 1L;

	//not to get NPE in the future
	private CommercialTabBean commercialBean = new CommercialTabBean();

	private Long orderItemId;
	private String orderItemNo;
	private Long contractItemNo;
	private Long sapContractItemId;
	private Long sapMaterialId;
	private String sapMaterialName;
	private String orderedQuantity;
	private String orderedUom;
	private String loadedQuantity;
	private String loadedUom;
	private Boolean tareNetWeightsAreNotNullFlag = false;
	private Boolean orderItemAndLoadRel11Flag = false;
	private String returnedQuantity;
	private String returnedUom;
	private Boolean isReturnedQuantityDisabled = false;
	private Long returnQuantHandling;
	private Boolean creditNoteFlag = false;
	private Boolean isCreditNoteDisabled = true;
	private String deliveredQuantity;
	private String deliveredUom;
	private Long logonPackageTypeCd;
	private Boolean isNew = false;
	private String returnReason = Constant.INVALID_OBJECT_ID_STR;
	private ReturnMaterialUsageBsEntity returnMaterialUsage;
	private PlantBsEntity returnPlant;
	private ShippingpointEntity returnShippingpoint;	
	
	// Handling MixDesign
	private List<FireqMaterialMixdesignEntity> fireqMatMixdesignList;
	private Long mixDesignId;
	private String aggRmxCode; // not unique
	
	
	
	//LS-info
	private String sapMaterialNo;
	private String materialPackageCd;
	private String materialPackageName;
	private String materialNorm;
	private String materialCENorm;
	private String plantCd;
	private String plantName;
	private List <PlantBsEntity> prodPlantList;
	private String certification;
	private String cpdNumber;
	private Long loadingpointId;
	private String carrierStock;
	private String loadId;
	private String parallelVerladId;
	private String tareWeightQuantity;
	private String grossWeightQuantity;
	private String netWeightQuantity;
//	private Boolean isTareGrossNetDisabledTech = true;
	private String netQuantityUom;
	private String containerNo;
	private String containerName;
	private String siloName;
	private String siloVerificationType;
	
	private String bimodalTruckOrderId;
	private String bimodalTruckOrderNo;
	private String bimodalTruckWagonNo;
	
	private String bimodalTruckNetWeight;
	
	//structure storing the valid lists for the order item
	private DeliveryOptionConfigurationDTO optionConfiguration;
	private List<UomTxEntity> uomList;
	private Map<String, UomTxEntity> uomMap;
	
	private Boolean isRailReturnRepair = false;
	private boolean authorizedBimodalTruckOrder = false;
	private boolean archivedBimodalRailOrder = false;
	private List<BimodalTruckOrderItemDTO> bimodalTruckOrderItems;
	private boolean serviceMaterial;
	
	private final OrderItemConvFactors convFactors = new OrderItemConvFactors();
	
	public String getCarrierStock() {
		return carrierStock;
	}
	public void setCarrierStock(String carrierStock) {
		this.carrierStock = carrierStock;
	}
	public List <PlantBsEntity> getProdPlantList() {
		return prodPlantList;
	}
	public void setProdPlantList(List <PlantBsEntity>  prodPlantList) {
		this.prodPlantList = prodPlantList;
	}
	public String getMaterialPackageName() {
		return materialPackageName;
	}
	public void setMaterialPackageName(String materialPackageName) {
		this.materialPackageName = materialPackageName;
	}
	public Boolean getOrderItemAndLoadRel11Flag() {
		return orderItemAndLoadRel11Flag;
	}
	public void setOrderItemAndLoadRel11Flag(Boolean orderItemAndLoadRel11Flag) {
		this.orderItemAndLoadRel11Flag = orderItemAndLoadRel11Flag;
	}
	public Boolean getTareNetWeightsAreNotNullFlag() {
		return tareNetWeightsAreNotNullFlag;
	}
	public void setTareNetWeightsAreNotNullFlag(Boolean tareNetWeightsAreNotNullFlag) {
		this.tareNetWeightsAreNotNullFlag = tareNetWeightsAreNotNullFlag;
	}
	public Long getReturnQuantHandling() {
		return returnQuantHandling;
	}
	public void setReturnQuantHandling(Long returnQuantHandling) {
		this.returnQuantHandling = returnQuantHandling;
	}
	public Boolean getIsCreditNoteDisabled() {
		return isCreditNoteDisabled;
	}
	public void setIsCreditNoteDisabled(Boolean isCreditNoteDisabled) {
		this.isCreditNoteDisabled = isCreditNoteDisabled;
	}
	public Boolean getCreditNoteFlag() {
		return creditNoteFlag;
	}
	public void setCreditNoteFlag(Boolean creditNoteFlag) {
		this.creditNoteFlag = creditNoteFlag;
	}
	public String getLoadId() {
		return loadId;
	}
	public void setLoadId(String loadId) {
		this.loadId = loadId;
	}
	public String getParallelVerladId() {
		return parallelVerladId;
	}
	public void setParallelVerladId(String parallelVerladId) {
		this.parallelVerladId = parallelVerladId;
	}
	public String getGrossWeightQuantity() {
		return grossWeightQuantity;
	}
	public void setGrossWeightQuantity(String grossWeightQuantity) {
		this.grossWeightQuantity = grossWeightQuantity;
	}	
	public List<BimodalTruckOrderItemDTO> getBimodalTruckOrderItems() {
		return bimodalTruckOrderItems;
	}
	public void setBimodalTruckOrderItems(List<BimodalTruckOrderItemDTO> bimodalTruckOrderItems) {
		this.bimodalTruckOrderItems = bimodalTruckOrderItems;
	}
	public String getBimodalTruckOrderId() {
		return bimodalTruckOrderId;
	}
	public void setBimodalTruckOrderId(String bimodalTruckOrderId) {
		this.bimodalTruckOrderId = bimodalTruckOrderId;
	}
	public String getBimodalTruckOrderNo() {
		return bimodalTruckOrderNo;
	}
	public void setBimodalTruckOrderNo(String bimodalTruckOrderNo) {
		this.bimodalTruckOrderNo = bimodalTruckOrderNo;
	}
	public String getBimodalTruckWagonNo() {
		return bimodalTruckWagonNo;
	}
	public void setBimodalTruckWagonNo(String bimodalTruckWagonNo) {
		this.bimodalTruckWagonNo = bimodalTruckWagonNo;
	}
	public Long getLoadingpointId() {
		return loadingpointId;
	}
	public void setLoadingpointId(Long loadingpointId) {
		this.loadingpointId = loadingpointId;
	}
	public String getSiloName() {
		return siloName;
	}
	public void setSiloName(String siloName) {
		this.siloName = siloName;
	}
	public String getSiloVerificationType() {
		return siloVerificationType;
	}
	public void setSiloVerificationType(String siloVerificationType) {
		this.siloVerificationType = siloVerificationType;
	}
	public String getContainerNo() {
		return containerNo;
	}
	public void setContainerNo(String containerNo) {
		this.containerNo = containerNo;
	}
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getNetQuantityUom() {
		return netQuantityUom;
	}
	public void setNetQuantityUom(String netQuantityUom) {
		this.netQuantityUom = netQuantityUom;
	}
	public String getTareWeightQuantity() {
		return tareWeightQuantity;
	}
	public void setTareWeightQuantity(String tareWeightQuantity) {
		this.tareWeightQuantity = tareWeightQuantity;
	}
	public String getNetWeightQuantity() {
		return netWeightQuantity;
	}
	public void setNetWeightQuantity(String netWeightQuantity) {
		this.netWeightQuantity = netWeightQuantity;
	}
	public CommercialTabBean getCommercialBean() {
		return commercialBean;
	}
	public void setCommercialBean(CommercialTabBean commercialBean) {
		this.commercialBean = commercialBean;
	}
	public Long getSapMaterialId() {
		return sapMaterialId;
	}
	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}
	public Long getSapContractItemId() {
		return sapContractItemId;
	}
	public void setSapContractItemId(Long sapContractItemId) {
		this.sapContractItemId = sapContractItemId;
	}
	public Long getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}
	public String getSapMaterialName() {
		return sapMaterialName;
	}
	public void setSapMaterialName(String sapMaterialName) {
		this.sapMaterialName = sapMaterialName;
	}
	public String getLoadedQuantity() {
		return loadedQuantity;
	}
	public void setLoadedQuantity(String loadedQuantity) {
		this.loadedQuantity = loadedQuantity;
	}
	public String getLoadedUom() {
		return loadedUom;
	}
	public void setLoadedUom(String loadedUom) {
		this.loadedUom = loadedUom;
	}
	public String getReturnedQuantity() {
		return returnedQuantity;
	}
	public void setReturnedQuantity(String returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}
	public String getReturnedUom() {
		return returnedUom;
	}
	public void setReturnedUom(String returnedUom) {
		this.returnedUom = returnedUom;
	}
	public Boolean getIsReturnedQuantityDisabled() {
		return isReturnedQuantityDisabled;
	}
	public void setIsReturnedQuantityDisabled(Boolean isReturnedQuantityDisabled) {
		this.isReturnedQuantityDisabled = isReturnedQuantityDisabled;
	}
	public String getReturnReason() {
		return returnReason;
	}
	public void setReturnReason(String returnReason) {
		this.returnReason = returnReason;
	}
	public String getMaterialPackageCd() {
		return materialPackageCd;
	}
	public void setMaterialPackageCd(String materialPackageCd) {
		this.materialPackageCd = materialPackageCd;
	}
	public String getMaterialNorm() {
		return materialNorm;
	}
	public void setMaterialNorm(String materialNorm) {
		this.materialNorm = materialNorm;
	}
	public String getMaterialCENorm() {
		return materialCENorm;
	}
	public void setMaterialCENorm(String materialCENorm) {
		this.materialCENorm = materialCENorm;
	}
	public String getPlantCd() {
		return plantCd;
	}
	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public String getCertification() {
		return certification;
	}
	public void setCertification(String certification) {
		this.certification = certification;
	}
	public String getCpdNumber() {
		return cpdNumber;
	}
	public void setCpdNumber(String cpdNumber) {
		this.cpdNumber = cpdNumber;
	}
	public String getSapMaterialNo() {
		return sapMaterialNo;
	}
	public void setSapMaterialNo(String sapMaterialNo) {
		this.sapMaterialNo = sapMaterialNo;
	}	
	public String getOrderItemNo() {
		return orderItemNo;
	}
	public void setOrderItemNo(String orderItemNo) {
		this.orderItemNo = orderItemNo;
	}
	public String getDeliveredQuantity() {
		return deliveredQuantity;
	}
	public void setDeliveredQuantity(String deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}
	public String getDeliveredUom() {
		return deliveredUom;
	}
	public void setDeliveredUom(String deliveredUom) {
		this.deliveredUom = deliveredUom;
	}
	public String getOrderedQuantity() {
		return orderedQuantity;
	}
	public void setOrderedQuantity(String orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}
	public String getOrderedUom() {
		return orderedUom;
	}
	public void setOrderedUom(String orderedUom) {
		this.orderedUom = orderedUom;
	}
	public boolean getAuthorizedBimodalTruckOrder() {
		return authorizedBimodalTruckOrder;
	}
	public void setAuthorizedBimodalTruckOrder(boolean authorizedBimodalTruckOrder) {
		this.authorizedBimodalTruckOrder = authorizedBimodalTruckOrder;
	}

	public String getBimodalTruckNetWeight() {
		return bimodalTruckNetWeight;
	}
	
	public void setBimodalTruckNetWeight(String bimodalTruckNetWeight) {
		this.bimodalTruckNetWeight = bimodalTruckNetWeight;
	}

	/**
	 * @return the logonPackageTypeCd
	 */
	public Long getLogonPackageTypeCd() {
		return logonPackageTypeCd;
	}
	/**
	 * @param logonPackageTypeCd the logonPackageTypeCd to set
	 */
	public void setLogonPackageTypeCd(Long logonPackageTypeCd) {
		this.logonPackageTypeCd = logonPackageTypeCd;
	}
	
	public Long getContractItemNo() {
		return contractItemNo;
	}
	
	public void setContractItemNo(Long contractItemNo) {
		this.contractItemNo = contractItemNo;
	}
	public boolean isServiceMaterial() {
		return serviceMaterial;
	}
	public void setServiceMaterial(boolean serviceMaterial) {
		this.serviceMaterial = serviceMaterial;
	}
	public List<UomTxEntity> getUomList(){
		return uomList;
	}
	public List<ShippingpointDTO> getShippingpointList(){
		return this.optionConfiguration.getShippingpoinList();
	}
	public List<SupplyPlantDTO> getSupplyPlantList(){
		return this.optionConfiguration.getSupplyPlantList();
	}
	
	public void setOptionConfiguration(DeliveryOptionConfigurationDTO optionConfiguration) {
		this.optionConfiguration = optionConfiguration;
		
		// TODO romek 23-Nov-2012/romek: Function should be renamed, not a simple setter anymore.
		uomList = this.optionConfiguration.getUomList();
		uomList = new ArrayList<UomTxEntity>(uomList);
		UomComparator comp = new UomComparator();
		Collections.sort(uomList, comp);
	}
	
	public Map<String, UomTxEntity> getUomMap() {
		return uomMap;
	}
	public void setUomMap(Map<String, UomTxEntity> uomMap) {
		this.uomMap = uomMap;
	}
	public Boolean getIsNew() {
		return isNew;
	}
	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
	public Boolean getIsRailReturnRepair() {
		return isRailReturnRepair;
	}
	public void setIsRailReturnRepair(Boolean isRailReturnRepair) {
		this.isRailReturnRepair = isRailReturnRepair;
	}
	public List<FireqMaterialMixdesignEntity> getFireqMatMixdesignList() {
		return this.fireqMatMixdesignList;
	}
	public void setFireqMatMixdesignList(List<FireqMaterialMixdesignEntity> fireqMatMixdesignList) {
		this.fireqMatMixdesignList = fireqMatMixdesignList;
	}
	public String getAggRmxCode() {
		return aggRmxCode;
	}
	public void setAggRmxCode(String aggRmxCode) {
		this.aggRmxCode = aggRmxCode;
	}
	public ReturnMaterialUsageBsEntity getReturnMaterialUsage() {
		return returnMaterialUsage;
	}
	public void setReturnMaterialUsage(ReturnMaterialUsageBsEntity returnMaterialUsage) {
		this.returnMaterialUsage = returnMaterialUsage;
	}
	public PlantBsEntity getReturnPlant() {
		return returnPlant;
	}
	public void setReturnPlant(PlantBsEntity returnPlant) {
		this.returnPlant = returnPlant;
	}
	public ShippingpointEntity getReturnShippingpoint() {
		return returnShippingpoint;
	}
	public void setReturnShippingpoint(ShippingpointEntity returnShippingpoint) {
		this.returnShippingpoint = returnShippingpoint;
	}
	public Long getMixDesignId() {
		return mixDesignId;
	}
	public void setMixDesignId(Long mixDesignId) {
		this.mixDesignId = mixDesignId;
	}
	
	public OrderItemConvFactors getConvFactors() {
		return convFactors;
	}
	public boolean isArchivedBimodalRailOrder() {
		return archivedBimodalRailOrder;
	}
	public void setArchivedBimodalRailOrder(boolean archivedBimodalRailOrder) {
		this.archivedBimodalRailOrder = archivedBimodalRailOrder;
	}
	
}
