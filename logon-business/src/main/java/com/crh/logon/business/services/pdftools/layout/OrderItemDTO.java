/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import java.math.BigDecimal;
import java.text.MessageFormat;

import com.crh.logon.business.services.utils.InvoiceBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.pdftool.labelprovider.DTOMarker;
/**
 * DTO used to store order item data for the delivery note tables
 * 
 * @author AdNovum Informatik AG
 */
public class OrderItemDTO implements DTOMarker {
	
	private static final String valueFormat = "{0} {1}";
	
	private final Long pkgType;
	private final String orderNr;
	private final String sapNr;
	private final String materialName;
	private final String materialDescription;
	// bulk
	private final BigDecimal netWeight;
	private final BigDecimal grossWeight;
	private final BigDecimal tareWeight;
	private final BigDecimal ret;
	private final String bulkUom;
	// bag
	private final BigDecimal ordered;
	private final String orderedUom;
	private final BigDecimal delivered;
	private final String deliveredUom;
	private final BigDecimal netBag;
	private final String netBagUom;
	
	private String truckNr;
	private String loadingTime;
	private String deliveryTime;
	
	private InvoiceBean invoiceBean;

	// ignore not yet loaded order weight
	private boolean notLoaded = false;
	
	private String materialMixDesign;
	private String materialMixDesignPlantName;

	private boolean isAdditionalMaterialInfo = false;
	
	/**
	 * 
	 * @param orderNr
	 * @param sapNr
	 * @param materialName
	 * @param materialDescription
	 * @param truckNr
	 * @param loadingTime
	 * @param deliveryTime
	 * @param netWeight
	 * @param grossWeight
	 * @param tareWeight
	 * @param ret
	 * @param uom
	 */
	public OrderItemDTO(String orderNr, String sapNr, String materialName,
			String materialDescription, String truckNr, String loadingTime, String deliveryTime,
			BigDecimal netWeight, BigDecimal grossWeight, BigDecimal tareWeight, BigDecimal ret, String uom,
			String materialMixDesign, String materialMixDesignPlantName) {
		super();
		pkgType = Constant.PACKAGE_TYPE_BULK_POWDER;
		this.orderNr = orderNr;
		this.sapNr = sapNr;
		this.materialName = materialName;
		this.materialDescription = materialDescription;
		this.truckNr = truckNr;
		this.loadingTime = loadingTime;
		this.deliveryTime = deliveryTime;
		
		this.netWeight = netWeight;
		this.grossWeight = grossWeight;
		this.tareWeight = tareWeight;
		this.ret = ret;
		this.bulkUom = uom;
		
		this.ordered = null;
		this.orderedUom = null;
		this.delivered = null;
		this.deliveredUom = null;
		this.netBag = null;
		this.netBagUom = null;

		this.materialMixDesign = materialMixDesign;
		this.materialMixDesignPlantName = materialMixDesignPlantName;
	}
	
	/**
	 * Constructor for bag type
	 * 
	 * @param orderNr
	 * @param sapNr
	 * @param materialName
	 * @param materialDescription
	 * @param truckNr
	 * @param loadingTime
	 * @param deliveryTime
	 * @param ordered
	 * @param delivered
	 * @param netBag
	 * @param orderedUom
	 * @param deliveredUom
	 * @param netUom
	 */
	public OrderItemDTO(String orderNr, String sapNr, String materialName,
			String materialDescription, String truckNr, String loadingTime, String deliveryTime,
			BigDecimal ordered, BigDecimal delivered, BigDecimal netBag, String orderedUom, String deliveredUom, 
			String netUom, String materialMixDesign, String materialMixDesignPlantName) {
		super();
		pkgType = Constant.PACKAGE_TYPE_BBAG;
		this.orderNr = orderNr;
		this.sapNr = sapNr;
		this.materialName = materialName;
		this.materialDescription = materialDescription;
		this.truckNr = truckNr;
		this.loadingTime = loadingTime;
		this.deliveryTime = deliveryTime;
		
		this.ordered = ordered;
		this.orderedUom = orderedUom;
		this.delivered = delivered;
		this.deliveredUom = deliveredUom;
		this.netBag = netBag;
		this.netBagUom = netUom;
		
		this.netWeight = null;
		this.grossWeight = null;
		this.tareWeight = null;
		this.ret = null;
		this.bulkUom = null;

		this.materialMixDesign = materialMixDesign;
		this.materialMixDesignPlantName = materialMixDesignPlantName;
	}
	
	/**
	 * Constructor for service type
	 * 
	 * @param orderNr
	 * @param sapNr
	 * @param materialName
	 * @param materialDescription
	 * @param truckNr
	 * @param loadingTime
	 * @param deliveryTime
	 * @param net
	 * @param uom
	 */
	public OrderItemDTO(String orderNr, String sapNr, String materialName,
			String materialDescription, String truckNr, String loadingTime, String deliveryTime, BigDecimal net, 
			String uom, String materialMixDesign, String materialMixDesignPlantName) {
		super();
		pkgType = Constant.PACKAGE_TYPE_SERVICE;
		
		this.orderNr = orderNr;
		this.sapNr = sapNr;
		this.materialName = materialName;
		this.materialDescription = materialDescription;
		this.truckNr = truckNr;
		this.loadingTime = loadingTime;
		this.deliveryTime = deliveryTime;
		
		this.ordered = null;
		this.orderedUom = null;
		this.delivered = null;
		this.deliveredUom = null;
		this.netBag = net;
		this.netBagUom = uom;
		
		this.netWeight = net;
		this.grossWeight = null;
		this.tareWeight = null;
		this.ret = null;
		this.bulkUom = uom;

		this.materialMixDesign = materialMixDesign;
		this.materialMixDesignPlantName = materialMixDesignPlantName;
	}
	
	/**
	 * Constructor for other package types
	 * 
	 * @param orderNr
	 * @param sapNr
	 * @param materialName
	 * @param materialDescription
	 * @param truckNr
	 * @param loadingTime
	 * @param deliveryTime
	 * @param delivered
	 * @param deliveredUom
	 * @param pkgType
	 */
	public OrderItemDTO(String orderNr, String sapNr, String materialName,
			String materialDescription, String truckNr, String loadingTime, String deliveryTime, 
			BigDecimal delivered, String deliveredUom, Long pkgType,
			String materialMixDesign, String materialMixDesignPlantName) {
		super();
		this.pkgType = pkgType;
		
		this.orderNr = orderNr;
		this.sapNr = sapNr;
		this.materialName = materialName;
		this.materialDescription = materialDescription;
		this.truckNr = truckNr;
		this.loadingTime = loadingTime;
		this.deliveryTime = deliveryTime;
		
		this.ordered = null;
		this.orderedUom = null;
		this.delivered = delivered;
		this.deliveredUom = deliveredUom;
		this.netBag = null;
		this.netBagUom = null;
		
		this.netWeight = null;
		this.grossWeight = null;
		this.tareWeight = null;
		this.ret = null;
		this.bulkUom = null;

		this.materialMixDesign = materialMixDesign;
		this.materialMixDesignPlantName = materialMixDesignPlantName;
	}
	
	/**
	 * Constructor for additional info DTO for delivery note
	 * @param sapNr
	 * @param plantMixDesign
	 */
	public OrderItemDTO(String sapNr, String plantMixDesign, InvoiceBean invoiceBean) {
		this(sapNr, plantMixDesign);
		this.invoiceBean = invoiceBean;
		this.isAdditionalMaterialInfo = false;
	}
	
	/**
	 * Constructor for additional info DTO for delivery note
	 * @param materialName
	 * @param materialDescription
	 */
	public OrderItemDTO(String materialName, String materialDescription) {
		super();
		this.pkgType = null;
		
		this.orderNr = null;
		this.sapNr = null;
		this.materialName = materialName;
		this.materialDescription = materialDescription;
		this.truckNr = null;
		this.loadingTime = null;
		this.deliveryTime = null;
		
		this.ordered = null;
		this.orderedUom = null;
		this.delivered = null;
		this.deliveredUom = null;
		this.netBag = null;
		this.netBagUom = null;
		
		this.netWeight = null;
		this.grossWeight = null;
		this.tareWeight = null;
		this.ret = null;
		this.bulkUom = null;

		this.isAdditionalMaterialInfo = true;
	}
	
	/**
	 * @return the orderNr
	 */
	public String getOrderNr() {
		return orderNr;
	}
	/**
	 * @return the sapNr
	 */
	public String getSapNr() {
		return sapNr;
	}
	/**
	 * @return the materialName
	 */
	public String getMaterialName() {
		return materialName;
	}
	/**
	 * @return the truckNr
	 */
	public String getTruckNr() {
		return truckNr;
	}
	/**
	 * @return the loadingTime
	 */
	public String getLoadingTime() {
		return loadingTime;
	}
	/**
	 * @return the deliveryTime
	 */
	public String getDeliveryTime() {
		return deliveryTime;
	}
	/**
	 * @return the netWeight
	 */
	public BigDecimal getNetWeight() {
		return netWeight;
	}
	/**
	 * @return the uom
	 */
	public String getBulkUom() {
		return bulkUom;
	}
	
	/**
	 * @return the materialDescription
	 */
	public String getMaterialDescription() {
		return materialDescription;
	}

	/**
	 * @return the grossWeight
	 */
	public BigDecimal getGrossWeight() {
		return grossWeight;
	}

	/**
	 * @return the tareWeight
	 */
	public BigDecimal getTareWeight() {
		return tareWeight;
	}
	
	/**
	 * @return the return
	 */
	public BigDecimal getReturn() {
		return ret;
	}

	/**
	 * @return the pkgType
	 */
	public Long getPkgType() {
		return pkgType;
	}

	/**
	 * @return the ordered
	 */
	public BigDecimal getOrdered() {
		return ordered;
	}

	/**
	 * @return the orderedUom
	 */
	public String getOrderedUom() {
		return orderedUom;
	}

	/**
	 * @return the delivered
	 */
	public BigDecimal getDelivered() {
		return delivered;
	}

	/**
	 * @return the deliveredUom
	 */
	public String getDeliveredUom() {
		return deliveredUom;
	}

	/**
	 * @return the netBag
	 */
	public BigDecimal getNetBag() {
		return netBag;
	}

	/**
	 * @return the netUom
	 */
	public String getNetBagUom() {
		return netBagUom;
	}

	/**
	 * @param loadingTime the loadingTime to set
	 */
	public void setLoadingTime(String loadingTime) {
		this.loadingTime = loadingTime;
	}

	/**
	 * @param deliveryTime the deliveryTime to set
	 */
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	/**
	 * @param truckNr the truckNr to set
	 */
	public void setTruckNr(String truckNr) {
		this.truckNr = truckNr;
	}
	
	public static BigDecimal getPkgDependantWeight(OrderItemDTO dto) {
		if (dto.isNotLoaded()) {
			return null;
		}
		BigDecimal weight;
		if (isBulkType(dto.getPkgType())) {
			weight = dto.getNetWeight();
		}
		else if (Long.valueOf(Constant.PACKAGE_TYPE_BBAG).equals(dto.getPkgType())) {
			weight = dto.getNetBag();
		}
		else if (Long.valueOf(Constant.PACKAGE_TYPE_SERVICE).equals(dto.getPkgType())) {
			weight = dto.getNetBag();
		}
		else {
			weight = dto.getDelivered();
		}
		return weight;
	}
	
	public static String getPkgDependantUom(OrderItemDTO dto) {
		if (getPkgDependantWeight(dto) == null) {
			return "";
		}
		String uom;
		if (isBulkType(dto.getPkgType())) {
			uom = dto.getBulkUom();
		}
		else if (Long.valueOf(Constant.PACKAGE_TYPE_BBAG).equals(dto.getPkgType())) {
			uom = dto.getNetBagUom();
		}
		else if (Long.valueOf(Constant.PACKAGE_TYPE_SERVICE).equals(dto.getPkgType())) {
			uom = dto.getNetBagUom();
		}
		else {
			uom = dto.getDeliveredUom();
		}
		return uom;
	}

	static boolean isBulkType(Long pkgTypeCd)
	{
		return Constant.PACKAGE_TYPE_BULK_DUMPED == pkgTypeCd ||
				Constant.PACKAGE_TYPE_BULK_POWDER == pkgTypeCd;
	}

	/**
	 * @return the ignoreNotLoaded
	 */
	public boolean isNotLoaded() {
		return notLoaded;
	}

	/**
	 * @param ignoreNotLoaded the ignoreUndelivered to set
	 */
	public void setNotLoaded(boolean ignoreNotLoaded) {
		this.notLoaded = ignoreNotLoaded;
	}
	
	public static String getValueOrEmptyString(BigDecimal decimal, String uom) {
		if (decimal == null) {
			return "-";
		}
		return MessageFormat.format(valueFormat, decimal, uom);
	}
	
	public static String getValueOrEmptyStringWeight(BigDecimal decimal, String uom) {
		if (decimal == null) {
			return "-";
		}
		return PdfLayoutGenerationTool.WEIGHT_FORMAT.format(decimal) + " " + uom;
	}
	
	/**
	 * @return the InvoiceBean
	 */
	public InvoiceBean getInvoiceBean() {
		return invoiceBean;
	}

	public String getMaterialMixDesign() {
		return materialMixDesign;
	}

	public String getMaterialMixDesignPlantName() {
		return materialMixDesignPlantName;
	}

	public boolean isAdditionalMaterialInfo() {
		return isAdditionalMaterialInfo;
	}
}