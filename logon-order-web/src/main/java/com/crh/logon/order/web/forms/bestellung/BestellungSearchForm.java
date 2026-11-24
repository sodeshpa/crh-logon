/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.bestellung;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.crh.logon.business.services.beans.GeneralDisplayNameDto;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_bestellung_BestellungSearchForm"
 */
public class BestellungSearchForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
	
	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(BestellungSearchForm.class);
	
	private List orderStatusList;
	private List orderCreationTypeList;
	private List distributionDestCdList;
	private List<MaterialPackageTypeBean> materialPackageTypeBean;
	private List productList;
	private Map json;
	private GeneralDisplayNameDto soldtoDto;
	private GeneralDisplayNameDto shiptoDto;
	private GeneralDisplayNameDto billtoDto;
	private GeneralDisplayNameDto sapMaterialDto;
	
	private String formAction;

	private boolean applyUserDefaultSettings = false;
	//user specific default values
	//->jsp
	private String defaultDeliveryFromDateString;
	private String defaultDeliveryToDateString;
	private List<Long> defaultOrderStatusList;
	private List<Long> defaultOrderCreationTypeList;
	private List<Long> defaultDeliveryTypeCdList;
	private List<String> defaultDestinationCdList;
	private List<Long> defaultCarrierIdList;
	private List<Long> defaultPlantCdList;
	
	
	//<-jsp
	private String inDeliveryFromDate;
	private String inDeliveryToDate;
	private String inOrderStatusList;
	private String inOrderCreationTypeList;
	private String inDestinationCdList;
	private String inCarrierIdList;
	private String inPlantCdList;
	private String matPackagingType;
	private Boolean liveFlag;
	private Long sapMaterialId;
	private Long sareaId;
	private Long soldtoSapBpId;
	private Long poolId;
	private Long execCarrierId;
	private String shiptoSofficeCd;
	private Long shiptoSapBpId;
	private Long shippingpointId;
	private String shiptoSgroupCd;
	private Long billtoSapBpId;
	private String vehicleCompositeId;
	private String vehicleName;
	private String inDeliveryTypeCdList;
	private String ldsDeliveryNoteNo;

	//todo orsika 06.05.2013 use map instead of separate variables
//	private HashMap<String, Long> columnWidthMap;
	private Long wOrderNumber;
	private Long wTimeToRedLine;
	private Long wDistributionDestCd;
	private Long wSoldtoName1;
	private Long wShiptoName1;
	private Long wShiptoCity;
	private Long wStatus;
	private Long wMaterialTxt;
	private Long wOrderedQuantity;
	private Long wOrderedUomTxt;
	private Long wLoadedQuantity;
	private Long wLoadedUomTxt;
	private Long wReturnedQuantity;
	private Long wReturnedUomTxt;
	private Long wDeliveredQuantity;
	private Long wDeliveredUomTxt;
	private Long wDeliveryTypeTxt;
	private Long wShippingpointName;
	private Long wDeliveryFromDat;
	private Long wDeliveryToDat;
	private Long wPlannedDeliveryDat;
	private Long wPoolName;
	private Long wCommCarrierName;
	private Long wExecCarrierName;
	private Long wBilltoName1;
	private Long wDriverFullName;
	private Long wVehicleName;
	private Long wOrderValidationCode;
	private Long wLdsDeliveryNoteNo;
	private Long wNoteOrderMgmt;
	private Long wNoteDelivery;
	private Long wNoteDispatching;
	private Long wNoteTruckDriver;
	private Long wNoteRailwayDisp;
	private Long wOrderNumberFromSeqUsage;
	private Long wLoadingTs;
	private Long wShiptoAddress;
	private Long wItrackStatusCd;


	//indexes of columns 
	private Long idxOrderNumber;
	private Long idxTimeToRedLine;
	private Long idxDistributionDestCd;
	private Long idxSoldtoName1;
	private Long idxShiptoName1;
	private Long idxShiptoCity;
	private Long idxStatus;
	private Long idxMaterialTxt;
	private Long idxOrderedQuantity;
	private Long idxOrderedUomTxt;
	private Long idxLoadedQuantity;
	private Long idxLoadedUomTxt;
	private Long idxReturnedQuantity;
	private Long idxReturnedUomTxt;
	private Long idxDeliveredQuantity;
	private Long idxDeliveredUomTxt;
	private Long idxDeliveryTypeTxt;
	private Long idxShippingpointName;
	private Long idxDeliveryFromDat;
	private Long idxDeliveryToDat;
	private Long idxPlannedDeliveryDat;
	private Long idxPoolName;
	private Long idxCommCarrierName;
	private Long idxExecCarrierName;
	private Long idxBilltoName1;
	private Long idxDriverFullName;
	private Long idxVehicleName;
	private Long idxOrderValidationCode;
	private Long idxLdsDeliveryNoteNo;
	private Long idxNoteOrderMgmt;
	private Long idxNoteDelivery;
	private Long idxNoteDispatching;
	private Long idxNoteTruckDriver;
	private Long idxNoteRailwayDisp;
	private Long idxOrderNumberFromSeqUsage;
	private Long idxLoadingTs;
	private Long idxShiptoAddress;
	private Long idxItrackStatusCd;
	

	
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	public List getProductList() {
		return productList;
	}

	public void setProductList(List productList) {
		this.productList = productList;
	}

	public List getOrderStatusList() {
		return orderStatusList;
	}

	public void setOrderStatusList(List orderStatusList) {
		this.orderStatusList = orderStatusList;
	}

	public List getOrderCreationTypeList() {
		return orderCreationTypeList;
	}

	public void setOrderCreationTypeList(List orderCreationTypeList) {
		this.orderCreationTypeList = orderCreationTypeList;
	}
	
	public List getDistributionDestCdList() {
		return distributionDestCdList;
	}
	
	public void setDistributionDestCdList(List distributionDestCdList) {
		this.distributionDestCdList = distributionDestCdList;
	}

	public List<MaterialPackageTypeBean> getMaterialPackageTypeBean() {
		return materialPackageTypeBean;
	}

	public void setMaterialPackageTypeBean(List<MaterialPackageTypeBean> materialPackageTypeBean) {
		this.materialPackageTypeBean = materialPackageTypeBean;
	}

	public Map getJson() {
		return json;
	}

	public void setJson(Map json) {
		this.json = json;
	}

	public GeneralDisplayNameDto getSoldtoDto() {
		return soldtoDto;
	}

	public void setSoldtoDto(GeneralDisplayNameDto soldtoDto) {
		this.soldtoDto = soldtoDto;
	}

	public GeneralDisplayNameDto getShiptoDto() {
		return shiptoDto;
	}

	public void setShiptoDto(GeneralDisplayNameDto shiptoDto) {
		this.shiptoDto = shiptoDto;
	}

	public GeneralDisplayNameDto getBilltoDto() {
		return billtoDto;
	}

	public void setBilltoDto(GeneralDisplayNameDto billtoDto) {
		this.billtoDto = billtoDto;
	}

	public GeneralDisplayNameDto getSapMaterialDto() {
		return sapMaterialDto;
	}

	public void setSapMaterialDto(GeneralDisplayNameDto sapMaterialDto) {
		this.sapMaterialDto = sapMaterialDto;
	}

	public boolean isApplyUserDefaultSettings() {
		return applyUserDefaultSettings;
	}

	public void setApplyUserDefaultSettings(boolean applyUserDefaultSettings) {
		this.applyUserDefaultSettings = applyUserDefaultSettings;
	}

	public String getDefaultDeliveryFromString() {
		return defaultDeliveryFromDateString;
	}
	
	public void setDefaultDeliveryFromDateString(String defaultDeliveryFromString) {
		this.defaultDeliveryFromDateString = defaultDeliveryFromString;
	}
	
	public String getDefaultDeliveryToString() {
		return defaultDeliveryToDateString;
	}

	public void setDefaultDeliveryToDateString(String defaultDeliveryToString) {
		this.defaultDeliveryToDateString = defaultDeliveryToString;
	}

	public List getDefaultOrderStatusList() {
		return defaultOrderStatusList;
	}

	public void setDefaultOrderStatusList(List defaultOrderStatusList) {
		this.defaultOrderStatusList = defaultOrderStatusList;
	}
	
	public List getDefaultOrderCreationTypeList() {
		return defaultOrderCreationTypeList;
	}

	public void setDefaultOrderCreationTypeList(List defaultOrderCreationTypeList) {
		this.defaultOrderCreationTypeList = defaultOrderCreationTypeList;
	}
	
	public String getInDeliveryFromDate() {
		return inDeliveryFromDate;
	}

	public void setInDeliveryFromDate(String inDeliveryFromDate) {
		this.inDeliveryFromDate = inDeliveryFromDate;
	}

	public String getInDeliveryToDate() {
		return inDeliveryToDate;
	}

	public void setInDeliveryToDate(String inDeliveryToDate) {
		this.inDeliveryToDate = inDeliveryToDate;
	}

	public String getInOrderStatusList() {
		return inOrderStatusList;
	}

	public void setInOrderStatusList(String inOrderStatusList) {
		this.inOrderStatusList = inOrderStatusList;
	}

	public List<Long> getDefaultDeliveryTypeCdList() {
		return defaultDeliveryTypeCdList;
	}

	public void setDefaultDeliveryTypeCdList(List<Long> deliveryTypeCdList) {
		this.defaultDeliveryTypeCdList = deliveryTypeCdList;
	}

	public List<String> getDefaultDestinationCdList() {
		return defaultDestinationCdList;
	}

	public void setDefaultDestinationCdList(List<String> defaultDestinationCdList) {
		this.defaultDestinationCdList = defaultDestinationCdList;
	}

	public String getInDestinationCdList() {
		return inDestinationCdList;
	}

	public void setInDestinationCdList(String inDestinationCdList) {
		this.inDestinationCdList = inDestinationCdList;
	}

	public String getMatPackagingType() {
		return matPackagingType;
	}

	public void setMatPackagingType(String matPackagingType) {
		this.matPackagingType = matPackagingType;
	}

	public Boolean getLiveFlag() {
		return liveFlag;
	}

	public void setLiveFlag(Boolean liveFlag) {
		this.liveFlag = liveFlag;
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}

	public Long getSareaId() {
		return sareaId;
	}

	public void setSareaId(Long sareaId) {
		this.sareaId = sareaId;
	}

	public Long getSoldtoSapBpId() {
		return soldtoSapBpId;
	}

	public void setSoldtoSapBpId(Long soldtoSapBpId) {
		this.soldtoSapBpId = soldtoSapBpId;
	}

	public Long getPoolId() {
		return poolId;
	}

	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}

	public Long getExecCarrierId() {
		return execCarrierId;
	}

	public void setExecCarrierId(Long execCarrierId) {
		this.execCarrierId = execCarrierId;
	}

	public String getInPlantCdList() {
		return inPlantCdList;
	}

	public void setInPlantCdList(String inPlantCdList) {
		this.inPlantCdList = inPlantCdList;
	}

	public List getDefaultPlantCdList() {
		return defaultPlantCdList;
	}

	public void setDefaultPlantCdList(List defaultPlantCdList) {
		this.defaultPlantCdList = defaultPlantCdList;
	}

	public String getShiptoSofficeCd() {
		return shiptoSofficeCd;
	}

	public void setShiptoSofficeCd(String shiptoSofficeCd) {
		this.shiptoSofficeCd = shiptoSofficeCd;
	}

	public Long getShiptoSapBpId() {
		return shiptoSapBpId;
	}

	public void setShiptoSapBpId(Long shiptoSapBpId) {
		this.shiptoSapBpId = shiptoSapBpId;
	}

	public List getDefaultCarrierIdList() {
		return defaultCarrierIdList;
	}

	public void setDefaultCarrierIdList(List defaultCarrierIdList) {
		this.defaultCarrierIdList = defaultCarrierIdList;
	}

	public Long getShippingpointId() {
		return shippingpointId;
	}

	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}

	public String getShiptoSgroupCd() {
		return shiptoSgroupCd;
	}

	public void setShiptoSgroupCd(String shiptoSgroupCd) {
		this.shiptoSgroupCd = shiptoSgroupCd;
	}

	public Long getBilltoSapBpId() {
		return billtoSapBpId;
	}

	public void setBilltoSapBpId(Long billtoSapBpId) {
		this.billtoSapBpId = billtoSapBpId;
	}

	public String getVehicleCompositeId() {
		return vehicleCompositeId;
	}

	public void setVehicleCompositeId(String vehicleCompositeId) {
		this.vehicleCompositeId = vehicleCompositeId;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public String getInDeliveryTypeCdList() {
		return inDeliveryTypeCdList;
	}
	
	public void setInDeliveryTypeCdList(String inDeliveryTypeCdList) {
		this.inDeliveryTypeCdList = inDeliveryTypeCdList;
	}

	public String getDefaultDeliveryFromDateString() {
		return defaultDeliveryFromDateString;
	}

	public String getDefaultDeliveryToDateString() {
		return defaultDeliveryToDateString;
	}

	public String getInOrderCreationTypeList() {
		return inOrderCreationTypeList;
	}

	public void setInOrderCreationTypeList(String inOrderCreationTypeList) {
		this.inOrderCreationTypeList = inOrderCreationTypeList;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}
	
	public String getLdsDeliveryNoteNo() {
		return ldsDeliveryNoteNo;
	}

	public void setLdsDeliveryNoteNo(String ldsDeliveryNoteNo) {
		this.ldsDeliveryNoteNo = ldsDeliveryNoteNo;
	}

	public Long getwTimeToRedLine() {
		return wTimeToRedLine;
	}
	
	public void setwTimeToRedLine(Long wTimeToRedLine) {
		this.wTimeToRedLine = wTimeToRedLine;
	}

	public Long getwOrderNumber() {
		return wOrderNumber;
	}

	public void setwOrderNumber(Long wOrderNumber) {
		this.wOrderNumber = wOrderNumber;
	}

	public Long getwDistributionDestCd() {
		return wDistributionDestCd;
	}

	public void setwDistributionDestCd(Long wDistributionDestCd) {
		this.wDistributionDestCd = wDistributionDestCd;
	}

	public Long getwSoldtoName1() {
		return wSoldtoName1;
	}

	public void setwSoldtoName1(Long wSoldtoName1) {
		this.wSoldtoName1 = wSoldtoName1;
	}

	public Long getwShiptoName1() {
		return wShiptoName1;
	}

	public void setwShiptoName1(Long wShiptoName1) {
		this.wShiptoName1 = wShiptoName1;
	}

	public Long getwShiptoCity() {
		return wShiptoCity;
	}

	public void setwShiptoCity(Long wShiptoCity) {
		this.wShiptoCity = wShiptoCity;
	}

	public Long getwStatus() {
		return wStatus;
	}

	public void setwStatus(Long wStatus) {
		this.wStatus = wStatus;
	}

	public Long getwOrderedQuantity() {
		return wOrderedQuantity;
	}

	public void setwOrderedQuantity(Long wOrderedQuantity) {
		this.wOrderedQuantity = wOrderedQuantity;
	}

	public Long getwOrderedUomTxt() {
		return wOrderedUomTxt;
	}

	public void setwOrderedUomTxt(Long wOrderedUomTxt) {
		this.wOrderedUomTxt = wOrderedUomTxt;
	}

	public Long getwLoadedQuantity() {
		return wLoadedQuantity;
	}

	public void setwLoadedQuantity(Long wLoadedQuantity) {
		this.wLoadedQuantity = wLoadedQuantity;
	}

	public Long getwLoadedUomTxt() {
		return wLoadedUomTxt;
	}

	public void setwLoadedUomTxt(Long wLoadedUomTxt) {
		this.wLoadedUomTxt = wLoadedUomTxt;
	}

	public Long getwReturnedQuantity() {
		return wReturnedQuantity;
	}

	public void setwReturnedQuantity(Long wReturnedQuantity) {
		this.wReturnedQuantity = wReturnedQuantity;
	}

	public Long getwReturnedUomTxt() {
		return wReturnedUomTxt;
	}

	public void setwReturnedUomTxt(Long wReturnedUomTxt) {
		this.wReturnedUomTxt = wReturnedUomTxt;
	}

	public Long getwDeliveredQuantity() {
		return wDeliveredQuantity;
	}

	public void setwDeliveredQuantity(Long wDeliveredQuantity) {
		this.wDeliveredQuantity = wDeliveredQuantity;
	}

	public Long getwMaterialTxt() {
		return wMaterialTxt;
	}

	public void setwMaterialTxt(Long wMaterialTxt) {
		this.wMaterialTxt = wMaterialTxt;
	}

	public Long getwDeliveredUomTxt() {
		return wDeliveredUomTxt;
	}

	public void setwDeliveredUomTxt(Long wDeliveredUomTxt) {
		this.wDeliveredUomTxt = wDeliveredUomTxt;
	}

	public Long getwDeliveryTypeTxt() {
		return wDeliveryTypeTxt;
	}

	public void setwDeliveryTypeTxt(Long wDeliveryTypeTxt) {
		this.wDeliveryTypeTxt = wDeliveryTypeTxt;
	}

	public Long getwShippingpointName() {
		return wShippingpointName;
	}

	public void setwShippingpointName(Long wShippingpointName) {
		this.wShippingpointName = wShippingpointName;
	}

	public Long getwDeliveryFromDat() {
		return wDeliveryFromDat;
	}

	public void setwDeliveryFromDat(Long wDeliveryFromDat) {
		this.wDeliveryFromDat = wDeliveryFromDat;
	}

	public Long getwDeliveryToDat() {
		return wDeliveryToDat;
	}

	public void setwDeliveryToDat(Long wDeliveryToDat) {
		this.wDeliveryToDat = wDeliveryToDat;
	}

	public Long getwPlannedDeliveryDat() {
		return wPlannedDeliveryDat;
	}

	public void setwPlannedDeliveryDat(Long wPlannedDeliveryDat) {
		this.wPlannedDeliveryDat = wPlannedDeliveryDat;
	}

	public Long getwPoolName() {
		return wPoolName;
	}

	public void setwPoolName(Long wPoolName) {
		this.wPoolName = wPoolName;
	}

	public Long getwCommCarrierName() {
		return wCommCarrierName;
	}

	public void setwCommCarrierName(Long wCommCarrierName) {
		this.wCommCarrierName = wCommCarrierName;
	}

	public Long getwExecCarrierName() {
		return wExecCarrierName;
	}

	public void setwExecCarrierName(Long wExecCarrierName) {
		this.wExecCarrierName = wExecCarrierName;
	}

	public Long getwBilltoName1() {
		return wBilltoName1;
	}

	public void setwBilltoName1(Long wBilltoName1) {
		this.wBilltoName1 = wBilltoName1;
	}

	public Long getwDriverFullName() {
		return wDriverFullName;
	}

	public void setwDriverFullName(Long wDriverFullName) {
		this.wDriverFullName = wDriverFullName;
	}

	public Long getwVehicleName() {
		return wVehicleName;
	}

	public void setwVehicleName(Long wVehicleName) {
		this.wVehicleName = wVehicleName;
	}

	public Long getwOrderValidationCode() {
		return wOrderValidationCode;
	}

	public void setwOrderValidationCode(Long wOrderValidationCode) {
		this.wOrderValidationCode = wOrderValidationCode;
	}

	public Long getwLdsDeliveryNoteNo() {
		return wLdsDeliveryNoteNo;
	}

	public void setwLdsDeliveryNoteNo(Long wLdsDeliveryNoteNo) {
		this.wLdsDeliveryNoteNo = wLdsDeliveryNoteNo;
	}

	public Long getwItrackStatusCd() {
		return wItrackStatusCd;
	}

	public void setwItrackStatusCd(Long wItrackStatusCd) {
		this.wItrackStatusCd = wItrackStatusCd;
	}

	public Long getIdxOrderNumber() {
		return idxOrderNumber;
	}

	public void setIdxOrderNumber(Long idxOrderNumber) {
		this.idxOrderNumber = idxOrderNumber;
	}

	public Long getIdxTimeToRedLine() {
		return idxTimeToRedLine;
	}

	public void setIdxTimeToRedLine(Long idxTimeToRedLine) {
		this.idxTimeToRedLine = idxTimeToRedLine;
	}

	public Long getIdxDistributionDestCd() {
		return idxDistributionDestCd;
	}

	public void setIdxDistributionDestCd(Long idxDistributionDestCd) {
		this.idxDistributionDestCd = idxDistributionDestCd;
	}

	public Long getIdxSoldtoName1() {
		return idxSoldtoName1;
	}

	public void setIdxSoldtoName1(Long idxSoldtoName1) {
		this.idxSoldtoName1 = idxSoldtoName1;
	}

	public Long getIdxShiptoName1() {
		return idxShiptoName1;
	}

	public void setIdxShiptoName1(Long idxShiptoName1) {
		this.idxShiptoName1 = idxShiptoName1;
	}

	public Long getIdxShiptoCity() {
		return idxShiptoCity;
	}

	public void setIdxShiptoCity(Long idxShiptoCity) {
		this.idxShiptoCity = idxShiptoCity;
	}

	public Long getIdxStatus() {
		return idxStatus;
	}

	public void setIdxStatus(Long idxStatus) {
		this.idxStatus = idxStatus;
	}

	public Long getIdxMaterialTxt() {
		return idxMaterialTxt;
	}

	public void setIdxMaterialTxt(Long idxMaterialTxt) {
		this.idxMaterialTxt = idxMaterialTxt;
	}

	public Long getIdxOrderedQuantity() {
		return idxOrderedQuantity;
	}

	public void setIdxOrderedQuantity(Long idxOrderedQuantity) {
		this.idxOrderedQuantity = idxOrderedQuantity;
	}

	public Long getIdxOrderedUomTxt() {
		return idxOrderedUomTxt;
	}

	public void setIdxOrderedUomTxt(Long idxOrderedUomTxt) {
		this.idxOrderedUomTxt = idxOrderedUomTxt;
	}

	public Long getIdxLoadedQuantity() {
		return idxLoadedQuantity;
	}

	public void setIdxLoadedQuantity(Long idxLoadedQuantity) {
		this.idxLoadedQuantity = idxLoadedQuantity;
	}

	public Long getIdxLoadedUomTxt() {
		return idxLoadedUomTxt;
	}

	public void setIdxLoadedUomTxt(Long idxLoadedUomTxt) {
		this.idxLoadedUomTxt = idxLoadedUomTxt;
	}

	public Long getIdxReturnedQuantity() {
		return idxReturnedQuantity;
	}

	public void setIdxReturnedQuantity(Long idxReturnedQuantity) {
		this.idxReturnedQuantity = idxReturnedQuantity;
	}

	public Long getIdxReturnedUomTxt() {
		return idxReturnedUomTxt;
	}

	public void setIdxReturnedUomTxt(Long idxReturnedUomTxt) {
		this.idxReturnedUomTxt = idxReturnedUomTxt;
	}

	public Long getIdxDeliveredQuantity() {
		return idxDeliveredQuantity;
	}

	public void setIdxDeliveredQuantity(Long idxDeliveredQuantity) {
		this.idxDeliveredQuantity = idxDeliveredQuantity;
	}

	public Long getIdxDeliveredUomTxt() {
		return idxDeliveredUomTxt;
	}

	public void setIdxDeliveredUomTxt(Long idxDeliveredUomTxt) {
		this.idxDeliveredUomTxt = idxDeliveredUomTxt;
	}

	public Long getIdxDeliveryTypeTxt() {
		return idxDeliveryTypeTxt;
	}

	public void setIdxDeliveryTypeTxt(Long idxDeliveryTypeTxt) {
		this.idxDeliveryTypeTxt = idxDeliveryTypeTxt;
	}

	public Long getIdxShippingpointName() {
		return idxShippingpointName;
	}

	public void setIdxShippingpointName(Long idxShippingpointName) {
		this.idxShippingpointName = idxShippingpointName;
	}

	public Long getIdxDeliveryFromDat() {
		return idxDeliveryFromDat;
	}

	public void setIdxDeliveryFromDat(Long idxDeliveryFromDat) {
		this.idxDeliveryFromDat = idxDeliveryFromDat;
	}

	public Long getIdxDeliveryToDat() {
		return idxDeliveryToDat;
	}

	public void setIdxDeliveryToDat(Long idxDeliveryToDat) {
		this.idxDeliveryToDat = idxDeliveryToDat;
	}

	public Long getIdxPlannedDeliveryDat() {
		return idxPlannedDeliveryDat;
	}

	public void setIdxPlannedDeliveryDat(Long idxPlannedDeliveryDat) {
		this.idxPlannedDeliveryDat = idxPlannedDeliveryDat;
	}

	public Long getIdxPoolName() {
		return idxPoolName;
	}

	public void setIdxPoolName(Long idxPoolName) {
		this.idxPoolName = idxPoolName;
	}

	public Long getIdxCommCarrierName() {
		return idxCommCarrierName;
	}

	public void setIdxCommCarrierName(Long idxCommCarrierName) {
		this.idxCommCarrierName = idxCommCarrierName;
	}

	public Long getIdxExecCarrierName() {
		return idxExecCarrierName;
	}

	public void setIdxExecCarrierName(Long idxExecCarrierName) {
		this.idxExecCarrierName = idxExecCarrierName;
	}

	public Long getIdxBilltoName1() {
		return idxBilltoName1;
	}

	public void setIdxBilltoName1(Long idxBilltoName1) {
		this.idxBilltoName1 = idxBilltoName1;
	}

	public Long getIdxDriverFullName() {
		return idxDriverFullName;
	}

	public void setIdxDriverFullName(Long idxDriverFullName) {
		this.idxDriverFullName = idxDriverFullName;
	}

	public Long getIdxVehicleName() {
		return idxVehicleName;
	}

	public void setIdxVehicleName(Long idxVehicleName) {
		this.idxVehicleName = idxVehicleName;
	}

	public Long getIdxOrderValidationCode() {
		return idxOrderValidationCode;
	}

	public void setIdxOrderValidationCode(Long idxOrderValidationCode) {
		this.idxOrderValidationCode = idxOrderValidationCode;
	}

	public Long getIdxLdsDeliveryNoteNo() {
		return idxLdsDeliveryNoteNo;
	}

	public void setIdxLdsDeliveryNoteNo(Long idxLdsDeliveryNoteNo) {
		this.idxLdsDeliveryNoteNo = idxLdsDeliveryNoteNo;
	}

	public String getInCarrierIdList() {
		return inCarrierIdList;
	}

	public void setInCarrierIdList(String inCarrierIdList) {
		this.inCarrierIdList = inCarrierIdList;
	}
	
		public Long getwNoteOrderMgmt() {
		return wNoteOrderMgmt;
	}

	public void setwNoteOrderMgmt(Long wNoteOrderMgmt) {
		this.wNoteOrderMgmt = wNoteOrderMgmt;
	}

	public Long getwNoteDelivery() {
		return wNoteDelivery;
	}

	public void setwNoteDelivery(Long wNoteDelivery) {
		this.wNoteDelivery = wNoteDelivery;
	}

	public Long getwNoteDispatching() {
		return wNoteDispatching;
	}

	public void setwNoteDispatching(Long wNoteDispatching) {
		this.wNoteDispatching = wNoteDispatching;
	}

	public Long getwNoteTruckDriver() {
		return wNoteTruckDriver;
	}

	public void setwNoteTruckDriver(Long wNoteTruckDriver) {
		this.wNoteTruckDriver = wNoteTruckDriver;
	}

	public Long getwNoteRailwayDisp() {
		return wNoteRailwayDisp;
	}

	public void setwNoteRailwayDisp(Long wNoteRailwayDisp) {
		this.wNoteRailwayDisp = wNoteRailwayDisp;
	}

	public Long getwOrderNumberFromSeqUsage() {
		return wOrderNumberFromSeqUsage;
	}

	public void setwOrderNumberFromSeqUsage(Long wOrderNumberFromSeqUsage) {
		this.wOrderNumberFromSeqUsage = wOrderNumberFromSeqUsage;
	}

	public Long getwLoadingTs() {
		return wLoadingTs;
	}

	public void setwLoadingTs(Long wLoadingTs) {
		this.wLoadingTs = wLoadingTs;
	}

	public Long getwShiptoAddress() {
		return wShiptoAddress;
	}

	public void setwShiptoAddress(Long wShiptoAddress) {
		this.wShiptoAddress = wShiptoAddress;
	}

	public Long getIdxNoteOrderMgmt() {
		return idxNoteOrderMgmt;
	}

	public void setIdxNoteOrderMgmt(Long idxNoteOrderMgmt) {
		this.idxNoteOrderMgmt = idxNoteOrderMgmt;
	}

	public Long getIdxNoteDelivery() {
		return idxNoteDelivery;
	}

	public void setIdxNoteDelivery(Long idxNoteDelivery) {
		this.idxNoteDelivery = idxNoteDelivery;
	}

	public Long getIdxNoteDispatching() {
		return idxNoteDispatching;
	}

	public void setIdxNoteDispatching(Long idxNoteDispatching) {
		this.idxNoteDispatching = idxNoteDispatching;
	}

	public Long getIdxNoteTruckDriver() {
		return idxNoteTruckDriver;
	}

	public void setIdxNoteTruckDriver(Long idxNoteTruckDriver) {
		this.idxNoteTruckDriver = idxNoteTruckDriver;
	}

	public Long getIdxNoteRailwayDisp() {
		return idxNoteRailwayDisp;
	}

	public void setIdxNoteRailwayDisp(Long idxNoteRailwayDisp) {
		this.idxNoteRailwayDisp = idxNoteRailwayDisp;
	}

	public Long getIdxOrderNumberFromSeqUsage() {
		return idxOrderNumberFromSeqUsage;
	}

	public void setIdxOrderNumberFromSeqUsage(Long idxOrderNumberFromSeqUsage) {
		this.idxOrderNumberFromSeqUsage = idxOrderNumberFromSeqUsage;
	}

	public Long getIdxLoadingTs() {
		return idxLoadingTs;
	}

	public void setIdxLoadingTs(Long idxLoadingTs) {
		this.idxLoadingTs = idxLoadingTs;
	}

	public Long getIdxShiptoAddress() {
		return idxShiptoAddress;
	}

	public void setIdxShiptoAddress(Long idxShiptoAddress) {
		this.idxShiptoAddress = idxShiptoAddress;
	}

	public Long getIdxItrackStatusCd() {
		return idxItrackStatusCd;
	}

	public void setIdxItrackStatusCd(Long idxItrackStatusCd) {
		this.idxItrackStatusCd = idxItrackStatusCd;
	}

	public void clearDefaults() {
		formAction = null;
		
		soldtoDto = null;
		shiptoDto = null;
		billtoDto = null;
		sapMaterialDto = null;
		applyUserDefaultSettings = false;
		
		defaultDeliveryFromDateString = null;
		defaultDeliveryToDateString = null;
		defaultOrderStatusList = null;
		defaultOrderCreationTypeList = null;
		defaultDeliveryTypeCdList = null;
		defaultCarrierIdList = null;
		defaultPlantCdList = null;
		inDeliveryFromDate = null;
		inDeliveryToDate = null;
		inOrderCreationTypeList = null;
		inOrderStatusList = null;
		inCarrierIdList = null;
		inPlantCdList = null;
		matPackagingType = null;
		sapMaterialId = 0L;
		sareaId = 0L;
		soldtoSapBpId = null;
		liveFlag = false;
		poolId = 0L;
		execCarrierId = 0L;
		shiptoSofficeCd = null;
		shiptoSapBpId = null;
		shippingpointId = 0L;
		shiptoSgroupCd = null;
		billtoSapBpId = null;
		vehicleCompositeId = null;
		vehicleName = null;
		inDeliveryTypeCdList = null;
		ldsDeliveryNoteNo = null;

		wTimeToRedLine = null;
		wDistributionDestCd = null;
		wSoldtoName1 = null;
		wShiptoName1 = null;
		wShiptoCity = null;
		wStatus = null;
		wMaterialTxt = null;
		wOrderedQuantity = null;
		wOrderedUomTxt = null;
		wLoadedQuantity = null;
		wLoadedUomTxt = null;
		wReturnedQuantity = null;
		wReturnedUomTxt = null;
		wDeliveredQuantity = null;
		wDeliveredUomTxt = null;
		wDeliveryTypeTxt = null;
		wShippingpointName = null;
		wDeliveryFromDat = null;
		wDeliveryToDat = null;
		wPlannedDeliveryDat = null;
		wPoolName = null;
		wCommCarrierName = null;
		wExecCarrierName = null;
		wBilltoName1 = null;
		wDriverFullName = null;
		wVehicleName = null;
		wOrderValidationCode = null;
		wLdsDeliveryNoteNo = null;
		wNoteOrderMgmt = null;
		wNoteDelivery = null;
		wNoteDispatching = null;
		wNoteTruckDriver = null;
		wNoteRailwayDisp = null;
		wOrderNumberFromSeqUsage = null;
		wLoadingTs = null;
		wShiptoAddress = null;
		wItrackStatusCd = null;

		idxOrderNumber = null;
		idxTimeToRedLine = null;
		idxDistributionDestCd = null;
		idxSoldtoName1 = null;
		idxShiptoName1 = null;
		idxShiptoCity = null;
		idxStatus = null;
		idxMaterialTxt = null;
		idxOrderedQuantity = null;
		idxOrderedUomTxt = null;
		idxLoadedQuantity = null;
		idxLoadedUomTxt = null;
		idxReturnedQuantity = null;
		idxReturnedUomTxt = null;
		idxDeliveredQuantity = null;
		idxDeliveredUomTxt = null;
		idxDeliveryTypeTxt = null;
		idxShippingpointName = null;
		idxDeliveryFromDat = null;
		idxDeliveryToDat = null;
		idxPlannedDeliveryDat = null;
		idxPoolName = null;
		idxCommCarrierName = null;
		idxExecCarrierName = null;
		idxBilltoName1 = null;
		idxDriverFullName = null;
		idxVehicleName = null;
		idxOrderValidationCode = null;
		idxLdsDeliveryNoteNo = null;
		idxNoteOrderMgmt = null;
		idxNoteDelivery = null;
		idxNoteDispatching = null;
		idxNoteTruckDriver = null;
		idxNoteRailwayDisp = null;
		idxOrderNumberFromSeqUsage = null;
		idxLoadingTs = null;
		idxShiptoAddress = null;
		idxItrackStatusCd = null;
	}
	
	/**
     * Called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			clearDefaults();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
}
