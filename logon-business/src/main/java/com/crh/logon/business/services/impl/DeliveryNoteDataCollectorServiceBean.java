/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DeliveryNoteDataCollectorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteReferenceLabel;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.stores.domain.AssignmentPartStoreInterface;
import com.crh.logon.business.stores.domain.SapMaterialPlantNormStoreInterface;
import com.crh.logon.business.stores.domain.SapMaterialPlantStoreInterface;
import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.pdf.deliverynote.AddressEntryData;
import com.crh.logon.business.services.beans.pdf.deliverynote.BagItemData;
import com.crh.logon.business.services.beans.pdf.deliverynote.BaseOrderItemData;
import com.crh.logon.business.services.beans.pdf.deliverynote.BodyData;
import com.crh.logon.business.services.beans.pdf.deliverynote.BulkItemData;
import com.crh.logon.business.services.beans.pdf.deliverynote.BusinessAddressData;
import com.crh.logon.business.services.beans.pdf.deliverynote.CalibrationData;
import com.crh.logon.business.services.beans.pdf.deliverynote.CrossBoundaryData;
import com.crh.logon.business.services.beans.pdf.deliverynote.DeliveryNoteDataFactory;
import com.crh.logon.business.services.beans.pdf.deliverynote.FooterData;
import com.crh.logon.business.services.beans.pdf.deliverynote.GeneralLoadData;
import com.crh.logon.business.services.beans.pdf.deliverynote.GeneralOrderData;
import com.crh.logon.business.services.beans.pdf.deliverynote.HeaderData;
import com.crh.logon.business.services.beans.pdf.deliverynote.LogoData;
import com.crh.logon.business.services.beans.pdf.deliverynote.LogoLayout;
import com.crh.logon.business.services.beans.pdf.deliverynote.MaterialLayoutType;
import com.crh.logon.business.services.beans.pdf.deliverynote.MaterialLogoData;
import com.crh.logon.business.services.beans.pdf.deliverynote.OrderItemData;
import com.crh.logon.business.services.beans.pdf.deliverynote.RemarkData;
import com.crh.logon.business.services.beans.pdf.deliverynote.ServiceItemData;
import com.crh.logon.business.services.beans.pdf.deliverynote.TitleData;
import com.crh.logon.business.services.beans.pdf.deliverynote.DeliveryNotePdfDataSource;
import com.crh.logon.business.services.beans.pdf.deliverynote.OrderHeaderData;
import com.crh.logon.business.services.beans.pdf.deliverynote.SalesOrganizationData;
import com.crh.logon.business.services.beans.pdf.deliverynote.TransportData;
import com.crh.logon.business.services.dispatchers.DeliveryNoteDataCollectorServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.DeliveryNoteLookupServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MaterialEnhancementsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.UOMConverter;
import com.crh.logon.business.stores.domain.AssignmentStoreInterface;
import com.crh.logon.business.stores.domain.DriverStoreInterface;
import com.crh.logon.business.stores.domain.MaterialPlantStoreInterface;
import com.crh.logon.business.stores.domain.PlantCompcodeStoreInterface;
import com.crh.logon.business.stores.domain.SapMaterialDescriptionInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.MatPlantTxEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.PlantCompcodeEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.SignatureEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SorgBsEntity;
import com.crh.logon.persistence.entities.refdata.SorgTxEntity;
import com.crh.logon.persistence.entities.refdata.TextTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapMatPlantNormEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;
import com.crh.logon.utils.WagonNumberUtils;

/**
 * @author AdNovum Informatik AG
 */

@AdnTrace
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class DeliveryNoteDataCollectorServiceBean extends DeliveryNoteDataCollectorServiceDispatcher
	implements DeliveryNoteDataCollectorServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryNoteDataCollectorServiceBean.class);
	private static final int ROUNDING_SCALE = 2;
	
	private @EJB DeliveryNoteLookupServiceLocalInterface deliveryNoteLookupService;
	
	private @EJB
	SapMaterialPlantStoreInterface sapMaterialPlantStore;
	private @EJB PlantCompcodeStoreInterface plantCompcodeStore;
	private @EJB AssignmentStoreInterface assignmentStore;
	private @EJB
	AssignmentPartStoreInterface assignmentPartStore;
	private @EJB MaterialPlantStoreInterface materialPlantStore;
	private @EJB DriverStoreInterface driverStore;
	private @EJB
	SapMaterialPlantNormStoreInterface sapMaterialPlantNormStore;
	private @EJB SapMaterialDescriptionInterface sapMaterialDescriptionStore;
	
	private @EJB
	GenericBlobServiceLocalInterface blobService;
	private @EJB RefDataGenericsServiceLocalInterface referentialDataService;
	private @EJB MaterialEnhancementsServiceLocalInterface materialEnhancementsService;
	private @EJB
	PDFGeneratorServiceLocalInterface pdfGeneratorService;
	private @EJB
	TimeZoneServiceLocalInterface timezoneService;
	
	public DeliveryNotePdfDataSource collectData(Locale locale, OrderEntity order) {
		AssignmentEntity assignment = assignmentStore.getAssignmentForOrderId(order.getOrderId());
		MatPlantEntity matPlantEntity = materialPlantStore.getMaterialPlantFromOrder(order);
		TimeZone deliveryPlantTZ = timezoneService.loadForShippingPoint(order.getShippingpoint(), locale);
		
		DeliveryNotePdfDataSource deliveryNotePdfDataSource = DeliveryNoteDataFactory.createDeliveryNoteBase(locale);
		deliveryNotePdfDataSource.setHeader(createHeaderData(locale, order, assignment, deliveryPlantTZ));
		deliveryNotePdfDataSource.setBody(createBodyData(locale, order, matPlantEntity));
		deliveryNotePdfDataSource.setFooter(createFooterData(locale, order, assignment, deliveryPlantTZ));
		
		return deliveryNotePdfDataSource;
	}
	
	public FooterData createFooterData(Locale locale, OrderEntity order, AssignmentEntity assignment,
			TimeZone deliveryPlantTZ) {
		FooterData footerData = new FooterData();
		
		List<OrderItemEntity> orderItems = order.getOrderItems(); 
		SorgBsEntity sorgBs = null;
		String divisionCd = null;
		SareaEntity sareaBs = order.getSarea();
		String distributionChannel = sareaBs.getDistributionChannel();
		
		if (sareaBs != null) {
			sorgBs = sareaBs.getSorg();
			
			if (sareaBs.getDivision() != null) {
				divisionCd = sareaBs.getDivision().getDivisionCd();
			}
		}
		
		String footerTitle = pdfGeneratorService.
				formatText(DeliveryNoteReferenceLabel.FOOTER_TITLE.getMessageId(),
						locale, null, order.getDeliveryType(), sorgBs, distributionChannel, divisionCd);
		String footerText = pdfGeneratorService.
				formatText(DeliveryNoteReferenceLabel.FOOTER_TEXT.getMessageId(),
						locale, null, order.getDeliveryType(), sorgBs, distributionChannel, divisionCd);
		String calibrationText = pdfGeneratorService.
				formatText(DeliveryNoteReferenceLabel.CALIBRATION_TEXT.getMessageId(), 
						locale, null, order.getDeliveryType(), sorgBs, distributionChannel, divisionCd);
		
		footerData.setCalibrationText(calibrationText);
		footerData.setFooterTitle(footerTitle);
		footerData.setFooterText(footerText);
		footerData.setRemarkData(createRemarkData(locale, order, assignment));
		
		List<CalibrationData> calibrationDatas = new ArrayList<CalibrationData>();
		for (OrderItemEntity orderItem : orderItems) {
			CalibrationData calibrationData = createCalibrationData(
					locale, orderItem, order.getDeliveryType(), sorgBs, distributionChannel, divisionCd, deliveryPlantTZ);
			if (calibrationData != null) {
				calibrationDatas.add(calibrationData);
			}
		}
		
		footerData.setCalibrationDatas(calibrationDatas);

		return footerData;
	}
	
	public CalibrationData createCalibrationData( Locale locale, OrderItemEntity orderItem,
			DeliveryTypeBsEntity deliveryType, SorgBsEntity sorgBs, String distributionChannel,
			String divisionCd, TimeZone deliveryPlantTZ) {

		CalibrationData calibrationData = null;
		LoadEntity load = orderItem.getLoad();

		if (load != null) {
			BigDecimal tareWeightT = load.getTareWeightT();
			BigDecimal netQuantity = load.getNetQuantity();
			BigDecimal grossWeightT = load.getGrossWeightT();
			BigDecimal wsOriginalTareMeasureT = load.getWsOriginalTareMeasureT();
			BigDecimal wsOriginalGrossMeasureT = load.getWsOriginalGrossMeasureT();
			String wsEichbeleg1 = load.getWsEichbeleg1();
			String wsEichbeleg2 = load.getWsEichbeleg2();
			String balance1 = load.getWsEichbeleg1Balance();
			String balance2 = load.getWsEichbeleg2Balance();
			
			if ((netQuantity != null)
					&& (tareWeightT != null)
					&& (grossWeightT != null)
					&& (wsOriginalTareMeasureT != null)
					&& (wsOriginalGrossMeasureT != null)
					&& (wsEichbeleg1 != null)
					&& (wsEichbeleg2 != null)
					&& (balance1 != null)
					&& (balance2 != null)) {
				
				if ((tareWeightT.compareTo(wsOriginalTareMeasureT) == 0) &&
						(grossWeightT.compareTo(wsOriginalGrossMeasureT) == 0)) {
					
					wsEichbeleg1 = StringUtils.defaultString(wsEichbeleg1);
					String wsEichbeleg1Time = deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(load.getWsEichbeleg1Time());
					final String wsLoadingpoint1;
					if (load.getWsStartLoadingpoint() != null) {
						wsLoadingpoint1 = ", " + load.getWsStartLoadingpoint().getLoadingpointName();
					}
					else {
						wsLoadingpoint1 = "";
					}
					
					wsEichbeleg2 = StringUtils.defaultString(wsEichbeleg2);
					String wsEichbeleg2Time = deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(load.getWsEichbeleg2Time());
					final String wsLoadingpoint2;
					if (load.getWsEndLoadingpoint() != null) {
						wsLoadingpoint2 = ", " + load.getWsEndLoadingpoint().getLoadingpointName();
					} else {
						wsLoadingpoint2 = "";
					}
					
					final String tareEichbelegTxt =
							pdfGeneratorService.formatText(DeliveryNoteReferenceLabel.CALIBRATION_TARE.getMessageId(), locale,
									new Object[] {balance1, wsEichbeleg1, wsEichbeleg1Time,
									wsLoadingpoint1},
									deliveryType, sorgBs, distributionChannel, divisionCd);

					final String grossEichbelegTxt =
							pdfGeneratorService.formatText(DeliveryNoteReferenceLabel.CALIBRATION_GROSS.getMessageId(), locale,
									new Object[] {balance2, wsEichbeleg2, wsEichbeleg2Time,
									wsLoadingpoint2},
									deliveryType, sorgBs, distributionChannel, divisionCd);

					calibrationData = new CalibrationData();
					calibrationData.setGrossData(grossEichbelegTxt);
					calibrationData.setTareData(tareEichbelegTxt);
				}
			}
			
		}
		
		return calibrationData;
	}

	public RemarkData createRemarkData(Locale locale, OrderEntity order, AssignmentEntity assignment) {
		RemarkData remarkData = new RemarkData();
		SareaEntity salesArea = order.getSarea();

		String lnfAddress = LogonUtil.createLNFAddressString(order);
		String comment = order.getDeliverynoteSignature() != null ?
				StringUtils.defaultString(order.getCustDeliveryNoteComment()) :
				StringUtils.defaultString(order.getNoteDelivery());
				
		remarkData.setShowDispatcherDriver(salesArea.getDnShowDispatcherDriverFlag());
				
		if (assignment != null) {
			remarkData.setDispatcherId(assignment.getLdsDispatcherId());
			remarkData.setDispatcherName(assignment.getLdsDispatcherName());
			remarkData.setDispatcherTel(assignment.getLdsDispatcherTel());
			
			remarkData.setDriverId(assignment.getLdsDriverId());
			remarkData.setDriverName(assignment.getLdsDriverName());
			remarkData.setDriverCardNo(assignment.getLdsDriverCardNo());
			
			DriverEntity driver = driverStore.getDriverFromTruck(assignment.getTruck());
			
			if (driver != null) {
				UserEntity user = driver.getUser();
				PersonEntity person = user.getPerson();
				
				if (person != null) {
					String driverFullName = StringUtils.defaultString(person.getFirstname()) +
					" " + StringUtils.defaultString(person.getName());
					remarkData.setDriverFullName(driverFullName);
				}
				
				if (user!= null && user.getSignature() != null) {
					BlobDTO driverSignature = new BlobDTO();
					SignatureEntity signature = user.getSignature();
					
					driverSignature.setExtension(signature.getDataMimeType().getFileExtension());
					driverSignature.setStream(signature.getData());
					
					remarkData.setDriverSignature(driverSignature);
				}
			}
		}
		
		String fixRemarkTxt = pdfGeneratorService.lookUpTxText(
				DeliveryNoteReferenceLabel.REMARKS_TEXT.getMessageId(), locale);
		
		if (order.getDeliverynoteSignature() != null) {
			BlobDTO customerSignature = new BlobDTO();
			SignatureEntity signature = order.getDeliverynoteSignature();
			
			customerSignature.setExtension(signature.getDataMimeType().getFileExtension());
			customerSignature.setStream(signature.getData());
			
			remarkData.setCustomerSignature(customerSignature);
		}
		
		remarkData.setCustomerName(order.getContactPersonSignatureTxt());
		
		// fix remark 
		remarkData.setShowFixTextInRemark(salesArea.getDnShowFixText());
		remarkData.setFixRemark(fixRemarkTxt);
		
		remarkData.setLnfAddress(lnfAddress);
		remarkData.setComment(comment);
		
		return remarkData;
	}

	public BodyData createBodyData(Locale locale, OrderEntity order, MatPlantEntity materialPlant) {
		BodyData body = new BodyData();
		
		body.setMaterialLogo(createMaterialLogoData(locale, order, materialPlant));
		body.setTransport(createTransportData(locale, order, materialPlant));
		body.setOrderItem(createOrderItemData(locale, order));

		return body;
	}
	
	public String getSapNumber(OrderItemEntity orderItem) {
		String result = "";
		SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
		
		if (sapMaterial != null) {
			result = sapMaterial.getSapMaterialNo().replaceFirst("^0+(?!$)", "");
		}
		
		return result;
	}
	
	public String getMaterialName(Locale locale, OrderItemEntity orderItem) {
		String materialName = null;
		SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
		PlantBsEntity productionPlant = orderItem.getProductionPlant();
		
		if (sapMaterial !=  null) {
			if (productionPlant != null) {
				MatPlantEntity materialPlant = materialPlantStore.
						getMaterialPlantByMaterialIdAndPlantCd(sapMaterial.getSapMaterialId(), productionPlant.getPlantCd());
				if (materialPlant != null) {
					materialName = materialPlant.getBrandSalesName();
				}
			}
			
			if ( materialName == null ) {
				materialName = sapMaterialDescriptionStore.getMaterialDescription(sapMaterial
						.getSapMaterialId(), locale);
			}
		}
		
		materialName = StringUtils.isEmpty(materialName) ? "-" : materialName;
		return materialName;
	}
	
	public String getMaterialDescription(OrderItemEntity orderItem) {
		String hierarchyDesc = "";
		String cpdNumber = "";
		SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
		PlantBsEntity productionPlant = orderItem.getProductionPlant();
		
		MatPlantEntity materialPlant = materialPlantStore.getMaterialPlantByMaterialIdAndPlantCd(
				sapMaterial.getSapMaterialId(), productionPlant.getPlantCd());
		
		if (materialPlant != null) {
			hierarchyDesc = (StringUtils.isEmpty(materialPlant.getNormDesc())) ? "-"
					: materialPlant.getNormDesc();
			cpdNumber = StringUtils.defaultString(materialPlant.getCpdNumber());
		}
		
		return hierarchyDesc + " " + cpdNumber;
	}

	public OrderItemData createOrderItemData(Locale locale, OrderEntity order) {
		OrderItemData orderItemData = new OrderItemData();
		List<OrderItemEntity> orderItems = order.getOrderItems();
		BigDecimal numberOfReturnedPallets = BigDecimal.ZERO;
		boolean orderHasReturn = deliveryNoteLookupService.orderHasReturn(order);
		
		boolean orderHasBagItem = false;
		
		for( OrderItemEntity orderItem : orderItems) {
			Long packageCdOfOrderItem = deliveryNoteLookupService.getPackageCdOfOrderItem(orderItem);
			String materialName = getMaterialName(locale, orderItem);
			String materialDescription = getMaterialDescription(orderItem);
			
			boolean isBulkItem = Constant.PACKAGE_TYPE_BULK_POWDER == packageCdOfOrderItem
						|| Constant.PACKAGE_TYPE_BULK_DUMPED == packageCdOfOrderItem;
			boolean isBagItem = Constant.PACKAGE_TYPE_BBAG == packageCdOfOrderItem;
			boolean isService = Constant.PACKAGE_TYPE_SERVICE == packageCdOfOrderItem;
			boolean isReturnItem = orderItem.getReturnedQuantity() != null;
			boolean isReturnPalletItem = isBagItem && isReturnItem &&
					LogonPackageHandlingUtil.isPallet(orderItem);
			
			if (isReturnPalletItem) {
				numberOfReturnedPallets = numberOfReturnedPallets.add(orderItem.getReturnedQuantity());
			}
			
			BaseOrderItemData baseOrderItem = null;
			
			if (isBagItem) {
				BagItemData bag = createBagItemData(locale, orderItem, order);
				orderItemData.getBagItems().add(bag);
				baseOrderItem = bag;
				orderHasBagItem = true;
			}
			else if (isBulkItem) {
				BulkItemData bulk = createBulkItemData(locale, orderItem, order);
				orderItemData.getBulkItems().add(bulk);
				baseOrderItem = bulk;

			}
			else if (isService) {
				ServiceItemData service = createServiceItemData(orderItem);
				orderItemData.getServiceItems().add(service);
				baseOrderItem = service;
			
			}
			
			baseOrderItem.setMaterialDescription(materialDescription);
			baseOrderItem.setMaterialName(materialName);
		}
		
		
		
		MaterialLayoutType materialLayoutType = null;
		
		
		if (orderHasBagItem) {
			// if the order has bag items, display the BAG header(ordered/delivered/returned/netto) for the whole order
			materialLayoutType = MaterialLayoutType.BAG;
		}		
		else {
			// if the order has no bag items, display the BULK header(Brutto/Tara/Netto) for the whole order
			materialLayoutType = MaterialLayoutType.BULK;
		}
		
		orderItemData.setShowReturn(orderHasReturn);
		orderItemData.setMaterialLayoutType(materialLayoutType);
		orderItemData.setDeliveryTypeInfo(getDeliveryTypeInfo(locale, order));
		
		if (numberOfReturnedPallets.compareTo(BigDecimal.ZERO) > 0) {
			orderItemData.setNumberOfReturnedPallets(numberOfReturnedPallets.toString());
		}
		
		return orderItemData;
	}
	
	public String getDeliveryTypeInfo(Locale locale, OrderEntity order) {
		List<OrderItemEntity> orderItemList = order.getOrderItems();
		SorgBsEntity sorgBs = null;
		String divisionCd = null;
		SareaEntity sareaBs = order.getSarea();
		String distributionChannel = sareaBs.getDistributionChannel();
		
		String deliveryTypeInfo = null;
		String bimodalWagonNumber = null;
		String bimodalOrderNumber = null;
		String bimodalDeliveredQuantity = null;
		
		if (sareaBs != null) {
			sorgBs = sareaBs.getSorg();
			
			if (sareaBs.getDivision() != null) {
				divisionCd = sareaBs.getDivision().getDivisionCd();
			}
		}
		
		for (OrderItemEntity orderItem : orderItemList) {
			boolean isBimodalItem = orderItem.getBimodalRailOrderItem() != null;
			
			if (isBimodalItem) {
				OrderEntity bimodalOrder = orderItem.getBimodalRailOrderItem().getOrder();
				AssignmentPartEntity assignmentPart = assignmentPartStore.getActivePartForOrder(bimodalOrder);
				Long orderNumber = bimodalOrder.getOrderNumber();
				
				if (orderNumber != null) {
					bimodalOrderNumber = orderNumber.toString();
				}
				
				OrderItemEntity bimodalOrderItem = bimodalOrder.getOrderItems().get(0);
				BigDecimal deliveredQuantity = bimodalOrderItem.getDeliveredQuantity();
				
				if (deliveredQuantity != null) {
					UomBsEntity deliveredUom = bimodalOrderItem.getDeliveredUom();
					String uom = deliveryNoteLookupService.getUomName(locale, deliveredUom);
					bimodalDeliveredQuantity = deliveredQuantity.toPlainString() + " " + uom;
				}
				
				if ((assignmentPart != null) && (assignmentPart.getAssignment() != null) &&
						(assignmentPart.getAssignment().getWagon() != null)) {
					bimodalWagonNumber = assignmentPart.getAssignment().getWagon().getWagonNo();
				}
			}
		}
		
		if (bimodalOrderNumber != null) {
			bimodalWagonNumber  = StringUtils.isNotEmpty(bimodalWagonNumber) ?
					WagonNumberUtils.toDisplayNo(bimodalWagonNumber) : "";
					
			deliveryTypeInfo = pdfGeneratorService.formatText(
					DeliveryNoteReferenceLabel.BIMODAL_ORD_WAGON_INF.getMessageId(), locale,
					new Object[] { bimodalOrderNumber, bimodalWagonNumber, bimodalDeliveredQuantity },
					order.getDeliveryType(), sorgBs, distributionChannel, divisionCd);
		}
		else {
			deliveryTypeInfo = pdfGeneratorService.formatText(
					DeliveryNoteReferenceLabel.DELIVERY_TYPE.getMessageId(), locale, null,
					order.getDeliveryType(), sorgBs, distributionChannel, divisionCd);
		}

		return deliveryTypeInfo;
	}
	
	public BulkItemData createBulkItemData(Locale locale, OrderItemEntity orderItem, OrderEntity order) {
		BulkItemData bulkItemData = new BulkItemData();
		
		BigDecimal gross = null;
		BigDecimal tare = null;
		BigDecimal net = null;
		BigDecimal returned = null;
		String netQuantityUom = "";
		
		boolean isReturnItem = orderItem.getReturnedQuantity() != null;
		LoadEntity load = orderItem.getLoad();
		String sapNumber = getSapNumber(orderItem);
		String materialMixDesign = deliveryNoteLookupService.buildMaterialMixDesignInfo(orderItem);
		String materialMixDesignPlantName = deliveryNoteLookupService.getMaterialMixDesignProdPlantName(orderItem);
		
		if (load != null) {
			netQuantityUom = deliveryNoteLookupService.getUomName(locale, load.getNetQuantityUom());
			
			tare = load.getTareWeightT();
			if (tare != null) {
				tare = tare.setScale(ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
			}
			
			gross = load.getGrossWeightT();
			if (gross != null) {
				gross = gross.setScale(ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
			}
			
			if (order.getActualDeliveryDat() == null) {
				net = load.getNetQuantity();
			}
			else {
				net = orderItem.getDeliveredQuantity();
			}
			
			if (net != null) {
				net = net.setScale(ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
			}
			
			if (isReturnItem) {
				// FIXME 21-Nov-2012/romek: Make sure UoM is correct!
				returned = orderItem.getReturnedQuantity();
				returned = returned.setScale(ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
			}
		}
		
		if (returned != null) {
			bulkItemData.setReturnWeight(returned, netQuantityUom);
		}
		bulkItemData.setTareWeight(tare, Constant.UOM_TONS_TXT);
		bulkItemData.setGrossWeight(gross, Constant.UOM_TONS_TXT);
		bulkItemData.setNetWeight(net, netQuantityUom);
		bulkItemData.setSapMaterialNumber(sapNumber);
		bulkItemData.setMixDesign(materialMixDesign);
		bulkItemData.setProductionPlant(materialMixDesignPlantName);
		
		return bulkItemData;
	}
	
	/**
	 * check here if they are equal because for services it is acceptable to be fuzzy at this point too, even because ordered uom is used which is legally fuzzy
		the rail return/repair empty wagon order items are service materials, seems to be a specification gap that their base uom is TO
	 * @param orderItem
	 * @return
	 */
	public ServiceItemData createServiceItemData(OrderItemEntity orderItem) {
		ServiceItemData serviceItemData = new ServiceItemData();
		BigDecimal netWeight = null;
		String uomString = null;
		
		if (orderItem.getOrderedQuantity() != null) {
			SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
			UomBsEntity uom = orderItem.getOrderedUom();
			BigDecimal quantity = orderItem.getOrderedQuantity();
			BigDecimal baseUomQ = null;
			
			if ( uom != null && sapMaterial.getBaseUom() != null  && uom.getUomCd().equals(sapMaterial.getBaseUom().getUomCd()) ) {
				baseUomQ = quantity;
			}
			else {
				baseUomQ = UOMConverter.convertUomToAnotherUomUsingCommonDimension(uom, sapMaterial.getBaseUom(), quantity, false);
			}
			
			if ( baseUomQ != null ) {
				netWeight = baseUomQ;
				uomString = sapMaterial.getBaseUom().getUomCd();
			}
			else {
				LOG.error(String.format(
						"Could not convert quantity to base uom. Order item id=%d, Sap material no=%s, Uom=%s, Quantity=%s, Base uom=%s",
						orderItem.getOrderItemId(),
						(sapMaterial != null) ? sapMaterial.getSapMaterialNo() : "null",
						(uom != null) ? uom.getUomCd() : "null",
						String.valueOf(quantity),
						((sapMaterial != null) && (sapMaterial.getBaseUom() != null)) ? sapMaterial.getBaseUom().getUomCd() : "null"
				));
				netWeight = quantity;
				uomString = uom.getUomCd();
			}
		}
		
		serviceItemData.setNetWeight(netWeight, uomString);
		return serviceItemData;
	}
	
	public BagItemData createBagItemData(Locale locale, OrderItemEntity orderItem, OrderEntity order) {
		BagItemData bagItemData = new BagItemData();
		
		BigDecimal ordered = null;
		String orderedUom = "";
		BigDecimal delivered = null;
		String deliveredUom = "";
		BigDecimal netBag = null;
		String netUom = "";

		boolean isReturnOrder = LogonUtil.isReturnPalletOnly(order.getDeliveryType().getDeliveryTypeCd());
		LoadEntity load = orderItem.getLoad();
		UomBsEntity orderItemOrderedUom = orderItem.getOrderedUom();
		UomBsEntity originalDeliveredUom = orderItem.getDeliveredUom();
		String sapNumber = getSapNumber(orderItem);
		String materialMixDesign = deliveryNoteLookupService.buildMaterialMixDesignInfo(orderItem);
		String materialMixDesignPlantName = deliveryNoteLookupService.getMaterialMixDesignProdPlantName(orderItem);
		
		if (orderItem.getOrderedUom() != null && orderItem.getOrderedQuantity() != null) {
			orderedUom = deliveryNoteLookupService.getUomName(locale, orderItemOrderedUom);
			ordered = orderItem.getOrderedQuantity();
		}
		
		UomBsEntity netQuantityUom = load != null ? load.getNetQuantityUom() : null;
		BigDecimal netQuantity = load != null ? load.getNetQuantity() : null;
		
		boolean isDeliveredFieldsSet = (orderItem.getDeliveredQuantity() != null) && (originalDeliveredUom != null);
		boolean isLoadedFieldsSet = (netQuantity != null && netQuantityUom != null );
		
		if (isDeliveredFieldsSet || isLoadedFieldsSet) {
			BigDecimal quantity = null;
			UomBsEntity uom = null;
			SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
			
			if (isDeliveredFieldsSet) {
				quantity = orderItem.getDeliveredQuantity();
				uom = originalDeliveredUom;	
			}
			else {
				quantity = orderItem.getLoadedQuantity();
				uom = orderItem.getLoadedUom();
			}
			
			if (quantity != null && uom != null && sapMaterial != null) {
				BigDecimal quantityInBaseUom = UOMConverter.convertAltUomToMaterialBaseUom(uom, quantity, orderItem);
				UomBsEntity baseUom = sapMaterial.getBaseUom();
				
				if (orderItemOrderedUom != null && uom.getUomCd().equals(orderItemOrderedUom.getUomCd())) {
					delivered = quantity.stripTrailingZeros();
					deliveredUom = deliveryNoteLookupService.getUomName(locale, uom);
				}
				
				if (quantityInBaseUom != null) {
					netBag = quantityInBaseUom.stripTrailingZeros();
					netUom = deliveryNoteLookupService.getUomName(locale, baseUom);
					
					if (delivered == null) {
						Long orderItemOrderedUomNumerator = orderItem.getOrderedUomNumerator();
						Long orderItemOrderedUomDenominator = orderItem.getOrderedUomDenominator();
						if (orderItemOrderedUom != null && orderItemOrderedUomNumerator != null && orderItemOrderedUomDenominator != null) {
							BigDecimal orderedUomNum = new BigDecimal(orderItemOrderedUomNumerator);
							BigDecimal orderedUomDen = new BigDecimal(orderItemOrderedUomDenominator);
							BigDecimal quantityInOrderedUom = quantityInBaseUom.multiply(orderedUomDen).divide(orderedUomNum, ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
							delivered = quantityInOrderedUom.stripTrailingZeros();
							deliveredUom =  deliveryNoteLookupService.getUomName(locale, orderItemOrderedUom);
						}
					}
				}
				else {
					quantityInBaseUom = UOMConverter.convertUomToAnotherUomUsingCommonDimension(uom, baseUom, quantity, false);
					if (quantityInBaseUom != null) {
						netBag = quantityInBaseUom.stripTrailingZeros();
						netUom = deliveryNoteLookupService.getUomName(locale, baseUom);
					}
					
					BigDecimal quantityInOrderedUom = UOMConverter.convertUomToAnotherUomUsingCommonDimension(uom, orderItemOrderedUom, quantity, false);
					if (quantityInOrderedUom != null) {
						delivered = quantityInOrderedUom.stripTrailingZeros();
						deliveredUom = deliveryNoteLookupService.getUomName(locale, orderItemOrderedUom);
					}
					
					if (quantityInOrderedUom == null && quantityInBaseUom != null && orderItemOrderedUom != null) {
						if (orderItemOrderedUom.getFuzzyQuantityFlag() != null && !orderItemOrderedUom.getFuzzyQuantityFlag().booleanValue()) {
							Long orderItemOrderedUomNumerator = orderItem.getOrderedUomNumerator();
							Long orderItemOrderedUomDenominator = orderItem.getOrderedUomDenominator();
							if (orderItemOrderedUomNumerator != null && orderItemOrderedUomDenominator != null) {
								BigDecimal orderedUomNum = new BigDecimal(orderItemOrderedUomNumerator);
								BigDecimal orderedUomDen = new BigDecimal(orderItemOrderedUomDenominator);
								
								quantityInOrderedUom = quantityInBaseUom.multiply(orderedUomDen).divide(orderedUomNum, ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
								delivered = quantityInOrderedUom.stripTrailingZeros();
								deliveredUom = deliveryNoteLookupService.getUomName(locale, orderItemOrderedUom);
							}
						}
					}
				}
			}
		}
		
		// LOGON-6761 / LRQ 018.2
		if (isReturnOrder && !LogonPackageHandlingUtil.isService(orderItem)) {
			if (ordered != null) {
				ordered = ordered.negate();
			}
			if (delivered != null) {
				delivered = delivered.negate();
			}
			if (netBag != null) {
				netBag = netBag.negate();
			}
		}
		
		bagItemData.setDeliveredQuantity(delivered, deliveredUom);
		bagItemData.setNetQuantity(netBag, netUom);
		bagItemData.setOrderedQuantity(ordered, orderedUom);
		bagItemData.setMixDesign(materialMixDesign);
		bagItemData.setProductionPlant(materialMixDesignPlantName);
		bagItemData.setSapMaterialNumber(sapNumber);
		
		return bagItemData;
	}

	public MaterialLogoData createMaterialLogoData(Locale locale, OrderEntity order,
			MatPlantEntity materialPlant) {
		MaterialLogoData materialLogoData = new MaterialLogoData();
		
		SareaEntity sarea = order.getSarea();
		LogoLayout logoLayout = LogoLayout.MATERIAL_PLANT;
		boolean showZipCode = sarea != null && sarea.getExpShowZcode12Flag();
		List<LogoData> logoDatas = null;
		LogoData logoGeneric = null;
		
		if (materialPlant != null) {
			if (materialPlant.getPrioOverSapMatNormFlag()) {
				logoDatas = createMaterialPlantLogoData(locale, materialPlant, showZipCode);
			}
			else {
				List<SapMatPlantNormEntity> sapMatPlantNormList = 
						sapMaterialPlantNormStore.getSapMatPlantNormList(materialPlant, order);
				
				if (sapMatPlantNormList != null && !sapMatPlantNormList.isEmpty()) {
					logoLayout = LogoLayout.SAP_NORM_LIST;
					logoDatas = createNormLogoData(locale, sapMatPlantNormList, showZipCode);
				}
				else {
					logoDatas = createMaterialPlantLogoData(locale, materialPlant, showZipCode);
				}
			}
			
			logoGeneric = createGenericLogoData(locale, materialPlant.getLogoGeneric());
		}

		if (logoGeneric != null) {
			if (logoDatas.size() > 1) {
				logoDatas.add(1, logoGeneric);
			}
			else {
				logoDatas.add(logoGeneric);
			}
		}

		materialLogoData.setLogoList(logoDatas);
		materialLogoData.setLogoLayout(logoLayout);
		
		return materialLogoData;
	}
	
	public LogoData createGenericLogoData(Locale locale, String imageName) {
		return createLogoData(locale, null, null, null, imageName, true);
	}
	
	public LogoData createLogoData(Locale locale, String text1, String text2, String text3, String imageName) {
		return createLogoData(locale, text1, text2, text3, imageName, false);
	}
	
	public LogoData createLogoData(Locale locale, String text1, String text2, String text3, String imageName, boolean isGeneric) {
		LogoData logoData = new LogoData();
		BlobDTO logoImage = pdfGeneratorService.fetchPdfLogo(locale, imageName);
		
		if (logoImage != null) {
			logoData.setLogo(logoImage);
			logoData.setText1(text1);
			logoData.setText2(text2);
			logoData.setText3(text3);
			logoData.setGenericLogo(isGeneric);
			return logoData;
		}
		
		return null;
	}
	
	public List<LogoData> createMaterialPlantLogoData(Locale locale, 
			MatPlantEntity materialPlant, boolean showZipcode) {
		List<LogoData> logoDatas = new ArrayList<LogoData>();
		
		LogoData logo1 = createLogoData(
				locale,
				materialPlant.getLogo1Z1(),
				materialPlant.getLogo1Z2(),
				materialPlant.getNationalNorm(),
				materialPlant.getLogo1());
		
		LogoData logo2 = createLogoData(
				locale,
				materialPlant.getLogo2Z1(),
				materialPlant.getLogo2Z2(),
				materialPlant.getLogo2Z3(),
				materialPlant.getLogo2());
		
		LogoData logo3 = createLogoData(
				locale,
				materialPlant.getLogo3Z1(),
				materialPlant.getLogo3Z2(),
				materialPlant.getLogo3Z3(),
				materialPlant.getLogo3());
		
		LogoData logo4 = createLogoData(
				locale,
				materialPlant.getLogo4Z1(),
				materialPlant.getLogo4Z2(),
				materialPlant.getLogo4Z3(),
				materialPlant.getLogo4());
		
		LogoData logo5 = createLogoData(
				locale,
				materialPlant.getLogo5Z1(),
				materialPlant.getLogo5Z2(),
				materialPlant.getLogo5Z3(),
				materialPlant.getLogo5());
		
		LogoData logo6 = createLogoData(
				locale,
				materialPlant.getLogo6Z1(),
				materialPlant.getLogo6Z2(),
				materialPlant.getLogo6Z3(),
				materialPlant.getLogo6());
		
		if (logo1 != null) {
			logoDatas.add(logo1);
		}
		if (logo2 != null) {
			logoDatas.add(logo2);
		}
		if (logo3 != null) {
			logoDatas.add(logo3);
		}
		if (logo4 != null) {
			logoDatas.add(logo4);
		}
		if (logo5 != null) {
			logoDatas.add(logo5);
		}
		if (logo6 != null) {
			logoDatas.add(logo6);
		}
		
		return logoDatas;
	}
	
	public List<LogoData> createNormLogoData(Locale locale, 
			List<SapMatPlantNormEntity> sapMatPlantNormList, boolean showZipcode) {
		List<LogoData> logoDatas = new ArrayList<LogoData>();
		
		for (SapMatPlantNormEntity sapMaterialPlantNorm: sapMatPlantNormList) {
			String logoName = sapMaterialPlantNorm.getDeliveryNoteLogoImageName();
			
			String text1, text2, text3;
			if (showZipcode) {
				text1 = sapMaterialPlantNorm.getZcode1() + " " + sapMaterialPlantNorm.getZcode2();
				text2 = sapMaterialPlantNorm.getText1();
				text3 = sapMaterialPlantNorm.getText2();
			}
			else {
				text1 = sapMaterialPlantNorm.getText1();
				text2 = sapMaterialPlantNorm.getText2();
				text3 = sapMaterialPlantNorm.getText3();
			}
			
			LogoData logoData = createLogoData(locale, text1, text2, text3, logoName);
			if (logoData != null) {
				logoDatas.add(logoData);
			}
		}
		
		return logoDatas;
	}

	public TransportData createTransportData(Locale locale, OrderEntity order, MatPlantEntity materialPlant) {
		TransportData transportData = new TransportData();

		if (order.getDeliveryType().getDeliveryTypeCd() == Constant.DELIVERY_TYPE_PICKUP) {
			transportData.setCustomerResponsible(true);
		}
		else if (order.getCommCarrier() != null && order.getCommCarrier().getSapVendor().getVnIsInternal()) {
			transportData.setCrhResponsible(true);
		}		
		else {
			transportData.setCarrierResponsible(true);
		}
		
		BigDecimal chromatReductionMonths = order.getShiptoSapBp().getChromatReductionMonths();
		if (chromatReductionMonths != null && materialPlant != null) {
			MatPlantTxEntity matPlantTxEntity = materialEnhancementsService.loadMatPlantTx(materialPlant.getMatPlantId(), locale);
			if (matPlantTxEntity != null) {
				String chromatText = matPlantTxEntity.getChromatReductionTxt();
				
				if (chromatText == null) {
					chromatText = Message.lookup(locale, DeliveryNoteLabel.CHROMAT_MISSING.getMessageId());
				}
				
				transportData.setChromatReductionComment(MessageFormat.format(chromatText, chromatReductionMonths));
			}
		}
		
		return transportData;
	}

	public HeaderData createHeaderData(Locale locale, OrderEntity order, AssignmentEntity assignment,
			TimeZone deliveryPlantTZ) {
		HeaderData header = new HeaderData();
		
		SareaEntity sareaBs = order.getSarea();
		SorgBsEntity sorgBs = sareaBs.getSorg();
		
		header.setTitle(createTitleData(locale, order, deliveryPlantTZ));
		header.setSalesOrganization(createSalesOrganizationData(locale, sorgBs));
		header.setOrderHeader(createOrderHeaderData(locale, order, assignment, deliveryPlantTZ));
		header.setBusinessAddress(createBusinessAddressData(locale, order));
		header.setCrossBoundary(createCrossboundaryData(locale, order, sorgBs));
		
		return header;
	}
	
	/**
	 * Creates the {@link CrossBoundaryData} for Delivery Note PDF generation
	 * @param locale the {@link Locale}
	 * @param order the {@link OrderEntity}
	 * @param sorgBs the {@link SorgBsEntity} for banner image name
	 * @return the {@link CrossBoundaryData} for Delivery Note PDF generation
	 */
	public CrossBoundaryData createCrossboundaryData(Locale locale, OrderEntity order, SorgBsEntity sorgBs) {
		
		CrossBoundaryData crossBoundaryData = new CrossBoundaryData();
		
		if (sorgBs != null) {
			SorgTxEntity sorgTx = referentialDataService.findTxEntity(SorgTxEntity.class, locale, sorgBs);
			
			String rawBannerImageName = sorgTx.getImageDelNoteGeneralInfo();
			String bannerImageName = StringUtils.defaultString(rawBannerImageName);
			crossBoundaryData.setBannerImageName(bannerImageName);

			// load the image, if the name is not empty
			if (StringUtils.isNotEmpty(bannerImageName)) {
			
				BlobDTO bannerImage = blobService.fetchPdfImage(crossBoundaryData.getBannerImageName());
			
				crossBoundaryData.setBannerImage(bannerImage);
			}
		
		}
		
		if ((order.getShiptoSapBp() != null) && (order.getShippingpoint() != null)
				&& (order.getShippingpoint().getPlant() != null)) {
			PlantBsEntity plant = order.getShippingpoint().getPlant();
			PlantCompcodeEntity plantCompcodeEntity = plantCompcodeStore.getCodeForPlant(plant);
			
			String companyCode = plantCompcodeEntity != null ?
					plantCompcodeEntity.getCompanycode().getCompanycodeCd() : "";
			String countryCode = order.getShiptoSapBp().getCountry().getSapCountryCd();
					
			StringBuilder crossboundaryKey = new StringBuilder();
			crossboundaryKey.append(DeliveryNoteReferenceLabel.CROSSBOUNDARY.getMessageId());
			crossboundaryKey.append('.') ;
			crossboundaryKey.append(companyCode);
			crossboundaryKey.append(countryCode);

			TextTxEntity resultText = referentialDataService.findTxEntity(TextTxEntity.class, locale, crossboundaryKey.toString());
			crossBoundaryData.setCrossBoundary(resultText != null ? resultText.getTxt() : "");
			
			if (order.getOrderItems() != null) {
				OrderItemEntity orderItem = order.getOrderItems().get(0);
				SapMaterialPlantEntity sapMatPlant = sapMaterialPlantStore.findByMaterialAndPlant(plant, orderItem.getSapMaterial());
				
				if (sapMatPlant != null) {
					crossBoundaryData.setNhm(sapMatPlant.getNhmCode());
				}
			}
		}
		
		return crossBoundaryData;
	}
	
	public SalesOrganizationData createSalesOrganizationData(Locale locale, 
			SorgBsEntity sorgBs) {
		SalesOrganizationData salesOrganizationData = null;
		
		if (sorgBs != null) {
			SorgTxEntity sorgTx = referentialDataService.findTxEntity(SorgTxEntity.class, locale, sorgBs);
			salesOrganizationData = new SalesOrganizationData();
		
			salesOrganizationData.setCrhLogoName(sorgBs.getLogoImageName());
			
			salesOrganizationData.setCrhLogo(blobService.fetchPdfImage(salesOrganizationData.getCrhLogoName()));
			
			salesOrganizationData.setDestinationAddress1(StringUtils.defaultString(sorgTx.getAddressName()));
			salesOrganizationData.setDestinationAddress2(StringUtils.defaultString(sorgBs.getAddressStreet()));
			salesOrganizationData.setDestinationAddress3(StringUtils.defaultString(sorgBs.getAddressCountrycity()));
			salesOrganizationData.setDestinationFax(StringUtils.defaultString(sorgTx.getLineFax()));
			salesOrganizationData.setDestinationTel(StringUtils.defaultString(sorgTx.getLinePhone()));
			salesOrganizationData.setDestinationTax(StringUtils.defaultString(sorgTx.getLineTax()));
			salesOrganizationData.setDestinationEmail(StringUtils.defaultString(sorgTx.getLineEmail()));
		}
		
		return salesOrganizationData;
	}
	
	public GeneralOrderData createGeneralOrderData(Locale locale, OrderEntity order, AssignmentEntity assignment) {
		GeneralOrderData generalOrderData = new GeneralOrderData();
		List<OrderItemEntity> orderItemList = order.getOrderItems();
		
		String containerNrs = "";
		String bimodalWagonNo = null;
		
		for (OrderItemEntity orderItem : orderItemList) {
			ContainerEntity container = orderItem.getDeliveredLoadContainer() != null ?
					orderItem.getDeliveredLoadContainer() : (orderItem.getLoad() != null ?
					orderItem.getLoad().getContainer() : null );
			
			boolean isBimodalItem = orderItem.getBimodalRailOrderItem() != null;
			boolean isContainerItem = container != null;
			
			if (isBimodalItem) {
				OrderEntity bimodalOrder = orderItem.getBimodalRailOrderItem().getOrder();
				AssignmentPartEntity assignmentPart = assignmentPartStore.getActivePartForOrder(bimodalOrder);
				
				if ((assignmentPart != null) && (assignmentPart.getAssignment() != null) &&
						(assignmentPart.getAssignment().getWagon() != null)) {
					bimodalWagonNo = assignmentPart.getAssignment().getWagon().getWagonNo();
				}
			}
			
			if (isContainerItem) {
				containerNrs += (StringUtils.isNotEmpty(containerNrs) ? ", ": "") + container.getContainerNo();
			}
		}
		
		generalOrderData.setContainerNumbers(containerNrs);
		
		if (assignment != null) {
			if (assignment.getTruck() != null && StringUtils.isNotEmpty(bimodalWagonNo)){
				bimodalWagonNo = WagonNumberUtils.toDisplayNo(bimodalWagonNo);
			}
			else if (assignment.getWagon() != null) {
				bimodalWagonNo = WagonNumberUtils.toDisplayNo(assignment.getWagon().getWagonNo());
			}
			
			generalOrderData.setBimodalWagonNumber(bimodalWagonNo);
		}
		
		return generalOrderData;
	}
	
	public GeneralLoadData createGeneralLoadData(Locale locale, OrderEntity order, TimeZone deliveryPlantTZ) {
		GeneralLoadData generalLoadData = new GeneralLoadData();
		List<OrderItemEntity> orderItemList = order.getOrderItems();
		
		Date plantEntrance = null;
		Date plantExit = null;
		String wsTrailerPlateNo = null;
		String loadTrailerPlateNo = null;
		String wsTruckPlateNo = null;
		String loadTruckPlateNo = null;
		String deliverySilo = null;
		Long latestOrderItemNo = orderItemList.get(0).getOrderItemNo();
		
		for (OrderItemEntity orderItem : orderItemList) {
			LoadEntity load = orderItem.getLoad();
			
			if (load != null){
				if (latestOrderItemNo >= orderItem.getOrderItemNo()){
					latestOrderItemNo = orderItem.getOrderItemNo();
					
					wsTrailerPlateNo = load.getWsTrailerPlateNo();
					plantEntrance = load.getWsEnterShippingpointTime();
					plantExit = load.getWsLeaveShippingpointTime();
					deliverySilo = load.getWsSiloNo();
				}
				else if (wsTrailerPlateNo == null){
					wsTrailerPlateNo = load.getWsTrailerPlateNo();
				}

				if (wsTrailerPlateNo == null && load.getTrailer() != null){
					loadTrailerPlateNo = load.getTrailer().getTrailerPlateNo();
				}
				
				if (load.getTruck() != null){
					wsTruckPlateNo = wsTruckPlateNo == null ? load.getWsTruckPlateNo() : wsTruckPlateNo;
					loadTruckPlateNo = loadTruckPlateNo == null ? load.getTruck().getTruckPlateNo() : loadTruckPlateNo;
				}
			}
		}
		
		boolean useLoadTruckPlateNo = !order.getDeliveryType().getDeliveryTypeCd().
				equals(Constant.DELIVERY_TYPE_PICKUP) || wsTruckPlateNo == null;
		boolean useLoadTrailerPlateNo = wsTrailerPlateNo == null;
		
		if (plantEntrance != null) {
			generalLoadData.setPlantEntrance(deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(plantEntrance));
		}
		if (plantExit != null) {
			generalLoadData.setPlantExit(deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(plantExit));
		}
		
		generalLoadData.setDeliverySilo(deliverySilo);	
		generalLoadData.setTruckPlateNumber(useLoadTruckPlateNo ? loadTruckPlateNo : wsTruckPlateNo);
		generalLoadData.setTrailerPlateNumber(useLoadTrailerPlateNo ? loadTrailerPlateNo : wsTrailerPlateNo);
		
		return generalLoadData;
	}
	
	public OrderHeaderData createOrderHeaderData(Locale locale, OrderEntity order,
			AssignmentEntity assignment, TimeZone deliveryPlantTZ) {
		OrderHeaderData orderHeaderData = new OrderHeaderData();
		SareaEntity sareaBs = order.getSarea();
		CustorderEntity custorder = order.getCustorder();
		SapContractEntity sapContract = order.getSapContract();
		SapBpEntity sapBpEntity = order.getShiptoSapBp();
		
		String customerRefNumber = null;
		String sapContractNumber = null;
		String deliveryType = null;
		
		orderHeaderData.setShowDeliveryWindow(sareaBs.getDocumentDelTimeWindowFlag());
		orderHeaderData.setShowDistance(sareaBs.getDocumentDistanceFlag());
		orderHeaderData.setShowCarrierCrm(sareaBs.getDnShowCarrierCrmFlag());
		orderHeaderData.setShowSealsNumbers(sareaBs.getDnShowSealsInfoFlag());
		orderHeaderData.setShowPlantCycle(sareaBs.getDnShowPlantCycleFlag());
		orderHeaderData.setShowSiloNumber(sareaBs.getDnShowSiloNoFlag());
		orderHeaderData.setShowTrailer(sareaBs.getDnShowTrailerFlag());
		
		String deliveryWindow = deliveryNoteLookupService.getDeliveryWindow(
				locale, sapBpEntity, order.getDeliveryFromDat(), order.getDeliveryToDat());
		orderHeaderData.setDeliveryWindow(deliveryWindow);
		
		String deliveryTimeString = deliveryNoteLookupService.getDeliveryTimeString(order, locale);
		orderHeaderData.setDeliveryTime(deliveryTimeString);
		
		String endLoadingString = deliveryNoteLookupService.
				getLatestLoadingDate(order.getOrderItems(), deliveryPlantTZ);
		orderHeaderData.setEndLoadDate(endLoadingString);
		
		DeliveryTypeTxEntity deliveryTypeTxEntity = referentialDataService.findTxEntity(
				DeliveryTypeTxEntity.class, locale, order.getDeliveryType().getDeliveryTypeCd());
		if(deliveryTypeTxEntity != null) {
			deliveryType = deliveryTypeTxEntity.getTxt();
		}
		orderHeaderData.setDeliveryType(deliveryType);
		
		if (assignment != null) {
			orderHeaderData.setSealsNumbers(assignment.getLdsSealNumbers());
			orderHeaderData.setCarrierCrm(assignment.getLdsCarrierCrm());
		}
		if (order.getCommercialDistanceM() != null) {
			orderHeaderData.setDistance(Long.toString(order.getCommercialDistanceM() / 1000));
		}
		if (custorder != null) {
			customerRefNumber = custorder.getCustorderCustrefNo();
			orderHeaderData.setCustomerRefContractNumber(custorder.getCustorderCustrefNo());
		}
		if (sapContract != null) {
			sapContractNumber = sapContract.getSapContractNo();
			
			orderHeaderData.setIncoterm(sapContract.getIncotermsCd());
			orderHeaderData.setTransportReasonIsStpo(sapContract.getSapContractType().getContractTypeIsStpoFlag());
		}
		
		if (StringUtils.isNotEmpty(customerRefNumber) || StringUtils.isNotEmpty(sapContractNumber)) {
			StringBuilder customerRefContractNumberBuilder = new StringBuilder();
			StringBuilder label_customerRefContractNumberBuilder = new StringBuilder();
		
			if (StringUtils.isNotEmpty(customerRefNumber)) {
				label_customerRefContractNumberBuilder.append(pdfGeneratorService.formatText(
						DeliveryNoteReferenceLabel.CUST_REF_NO.getMessageId(), locale));
				customerRefContractNumberBuilder.append(customerRefNumber);
			}
			if (StringUtils.isNotEmpty(sapContractNumber)) {
				if (StringUtils.isNotEmpty(customerRefNumber)) {
					label_customerRefContractNumberBuilder.append(" / ");
					customerRefContractNumberBuilder.append(" / ");
				}
		
				label_customerRefContractNumberBuilder.append(pdfGeneratorService.formatText(
						DeliveryNoteReferenceLabel.CONTRACT_NO.getMessageId(), locale));
				customerRefContractNumberBuilder.append(sapContractNumber);
			}
			
			orderHeaderData.setCustomerRefContractNumber(customerRefContractNumberBuilder.toString());
			orderHeaderData.setLabel_customerRefContractNumber(label_customerRefContractNumberBuilder.toString());
		}
		
		orderHeaderData.setGeneralOrderData(createGeneralOrderData(locale, order, assignment));
		orderHeaderData.setGeneralLoadData(createGeneralLoadData(locale, order, deliveryPlantTZ));
		
		return orderHeaderData;
	}
	
	public AddressEntryData createSoldToData(SapBpEntity soldtoSapBp, AddressEntryData oneTimeSoldTo) {
		AddressEntryData addressEntryData = new AddressEntryData();
		
		if (soldtoSapBp != null) {
			if (!soldtoSapBp.getTreatAsOnetimeFlag().booleanValue()) {
				
				addressEntryData.setAddress1(StringUtils.defaultString(soldtoSapBp.getName1()) + " "
						+ StringUtils.defaultString(soldtoSapBp.getName2()));
				addressEntryData.setAddress2(StringUtils.defaultString(soldtoSapBp.getStreet()) + " "
						+ StringUtils.defaultString(soldtoSapBp.getHouseNumber()));
				addressEntryData.setAddress3(StringUtils.defaultString(soldtoSapBp.getCountry().getSapCountryCd()) + " "
						+ StringUtils.defaultString(soldtoSapBp.getPostalCode()) + " "
						+ StringUtils.defaultString(soldtoSapBp.getCity()));
				addressEntryData.setPhone(StringUtils.defaultString(soldtoSapBp.getPhoneNumber()));
				addressEntryData.setVat(StringUtils.defaultString(soldtoSapBp.getVatRegistrationNumber()));	
			}
			else {
				addressEntryData = oneTimeSoldTo.deepCopy();
			}
			addressEntryData.setId(StringUtils.defaultString(soldtoSapBp.getSapId()));
		}
		
		addressEntryData.setName(DeliveryNoteLabel.ADDRESS_SOLDTO.getMessageId());
		
		return addressEntryData;
	}

	/**
	 * Returns the Ship-To Contact Name (Telephone)
	 * @param shipToSapBp SapBpEntity containing the contact name and phone number 
	 * @param order The order to get the Sales Area
	 * @return null, if there is no contact/not selected to display in sales area, 
	 * or the {@link String} containing the contact name and phone number 
	 */
	public String getShipToContact(SapBpEntity shipToSapBp, OrderEntity order) {
		
		String result = null;	
	
		if (shipToSapBp != null) {			

				// Ship To Contact data
				// if showShiptoContact flag is not available, skip setting contact information
				if (order != null &&
					order.getSarea() != null &&	
					order.getSarea().getDnShowShiptoContactFlag() != null) {
			
					// set flag, if data available
					boolean shipToContact = order.getSarea().getDnShowShiptoContactFlag();
				    
				    // contact name could be null
				    String contactName = shipToSapBp.getContactName();
				    
				    // contact phone could be null
				    String contactPhone = shipToSapBp.getContactPhone();
				  
				    // The line is only to be printed if the contact name is filled and if 
					// the sales area is configured accordingly (Show ship-to contact information is checked)
				    if (shipToContact && contactName != null) {
				    	
				    	StringBuilder sb = new StringBuilder();
				    	sb.append(contactName);
				    	
				    	// check if phone number available
						// If the phone number is empty, the parenthesis surrounding it must not
						// be printed neither.
				    	if (contactPhone != null) {
				    		sb.append(" ");
				    		sb.append("(");
				    		sb.append(contactPhone);
				    		sb.append(")");
				    	}
				    	
				    	result = sb.toString();
				    }
				  
				  }
				}
				
			return result;
	}
	
	public AddressEntryData createShipToData(SapBpEntity shipToSapBp, AddressEntryData oneTimeShipTo, OrderEntity order) {
		AddressEntryData addressEntryData = new AddressEntryData();
		
		if ((shipToSapBp != null)) {			
			if (!shipToSapBp.getTreatAsOnetimeFlag()) {
				addressEntryData.setAddress1(StringUtils.defaultString(shipToSapBp.getName1()) + " " +
						StringUtils.defaultString(shipToSapBp.getName2()));
				addressEntryData.setAddress2(StringUtils.defaultString(shipToSapBp.getStreet()) + " "
						+ StringUtils.defaultString(shipToSapBp.getHouseNumber()));
				addressEntryData.setAddress3(StringUtils.defaultString(shipToSapBp.getCountry().getSapCountryCd()) + " "
						+ StringUtils.defaultString(shipToSapBp.getPostalCode()) + " "
						+ StringUtils.defaultString(shipToSapBp.getCity()));
				// set the ship-to contact String, could be null
				addressEntryData.setContact(getShipToContact(shipToSapBp, order));
			} 
			else {
				addressEntryData = oneTimeShipTo.deepCopy();
			}
			addressEntryData.setId(StringUtils.defaultString(shipToSapBp.getSapId()));
		}
		
		addressEntryData.setName(DeliveryNoteLabel.ADDRESS_SHIPTO.getMessageId());
		
		return addressEntryData;
	}
	
	public AddressEntryData createCommCarrierData(CarrierEntity commCarrier, SareaEntity sareaBs) {
		AddressEntryData addressEntryData = new AddressEntryData();
	
		if ((commCarrier != null) && (commCarrier.getSapVendor() != null)) {
			addressEntryData.setId(StringUtils.defaultString(commCarrier.getSapVendor().getSapId()));
			
			addressEntryData.setAddress1(StringUtils.defaultString(commCarrier.getSapVendor().getName1()) + " "
					+ StringUtils.defaultString(commCarrier.getSapVendor().getName2()));
			addressEntryData.setAddress2(StringUtils.defaultString(commCarrier.getSapVendor().getStreet()) + " "
					+ StringUtils.defaultString(commCarrier.getSapVendor().getHouseNumber()));
			addressEntryData.setAddress3((commCarrier.getSapVendor().getCountry() != null ?
					commCarrier.getSapVendor().getCountry().getSapCountryCd() + " " : "")
					+ StringUtils.defaultString(commCarrier.getSapVendor().getPostalCode()) + " "
					+ StringUtils.defaultString(commCarrier.getSapVendor().getCity()));
			addressEntryData.setPhone(StringUtils.defaultString(commCarrier.getSapVendor().getPhoneNumber()));
			addressEntryData.setTax(StringUtils.defaultString(commCarrier.getSapVendor().getVatRegistrationNumber()) + " " +
					StringUtils.defaultString(commCarrier.getSapVendor().getTaxNumber3()));
		}
		
		addressEntryData.setName(DeliveryNoteLabel.ADDRESS_COMM_CARRIER.getMessageId());
		addressEntryData.setVisible(sareaBs.getDocumentCommCarrierFlag());
		
		return addressEntryData;
	}
	
	public AddressEntryData createExecCarrierData(CarrierEntity execCarrier, SareaEntity sareaBs) {
		AddressEntryData addressEntryData = new AddressEntryData();
		
		if ((execCarrier != null) && (execCarrier.getSapVendor() != null)) {
			addressEntryData.setId(StringUtils.defaultString(execCarrier.getSapVendor().getSapId()));
			addressEntryData.setAddress1(StringUtils.defaultString(execCarrier.getSapVendor().getName1()) + " "
					+ StringUtils.defaultString(execCarrier.getSapVendor().getName2()));
			addressEntryData.setAddress2(StringUtils.defaultString(execCarrier.getSapVendor().getStreet()) + " "
					+ StringUtils.defaultString(execCarrier.getSapVendor().getHouseNumber()));
			addressEntryData.setAddress3((execCarrier.getSapVendor().getCountry() != null ?
					execCarrier.getSapVendor().getCountry().getSapCountryCd() + " " : "")
					+ StringUtils.defaultString(execCarrier.getSapVendor().getPostalCode()) + " "
					+ StringUtils.defaultString(execCarrier.getSapVendor().getCity()));
			addressEntryData.setPhone(StringUtils.defaultString(execCarrier.getSapVendor().getPhoneNumber()));
			addressEntryData.setTax(StringUtils.defaultString(execCarrier.getSapVendor().getVatRegistrationNumber()) + " " +
					StringUtils.defaultString(execCarrier.getSapVendor().getTaxNumber3()));
		}
		
		addressEntryData.setVisible(sareaBs.getDocumentExecCarrierFlag());
		addressEntryData.setName(DeliveryNoteLabel.ADDRESS_EXEC_CARRIER.getMessageId());
		
		return addressEntryData;
	}
	
	public AddressEntryData createBillToData(SapBpEntity billtoSapBp, SapBpEntity soldtoSapBp, AddressEntryData oneTimeSoldTo) {
		AddressEntryData addressEntryData = new AddressEntryData();
		
		if (billtoSapBp != null) {
			addressEntryData.setId(StringUtils.defaultString(billtoSapBp.getSapId()));
			addressEntryData.setAddress1(StringUtils.defaultString(billtoSapBp.getName1()) + " " + StringUtils.defaultString(billtoSapBp.getName2()));
			addressEntryData.setAddress2(StringUtils.defaultString(billtoSapBp.getStreet()) + " " + StringUtils.defaultString(billtoSapBp.getHouseNumber()));
			addressEntryData.setAddress3(StringUtils.defaultString(billtoSapBp.getCountry().getSapCountryCd()) + " " +
					StringUtils.defaultString(billtoSapBp.getPostalCode()) + " " + StringUtils.defaultString(billtoSapBp.getCity()));
		}
		
		if (soldtoSapBp != null && soldtoSapBp.getTreatAsOnetimeFlag()) {
			if (billtoSapBp != null && soldtoSapBp.getSapBpId().equals(billtoSapBp.getSapBpId())) {
				addressEntryData.setAddress1(oneTimeSoldTo.getAddress1());
				addressEntryData.setAddress2(oneTimeSoldTo.getAddress2());
				addressEntryData.setAddress3(oneTimeSoldTo.getAddress3());
				addressEntryData.setId(StringUtils.defaultString(soldtoSapBp.getSapId()));
			}
		}
		
		addressEntryData.setName(DeliveryNoteLabel.ADDRESS_BILLTO.getMessageId());
		
		return addressEntryData;
	}
	
	public AddressEntryData createPayToData(SapBpEntity payerSapBp, SapBpEntity soldtoSapBp,
			AddressEntryData oneTimeSoldTo, AddressEntryData billTo) {
		AddressEntryData addressEntryData = new AddressEntryData();
		
		if (payerSapBp != null) {
			addressEntryData.setId(StringUtils.defaultString(payerSapBp.getSapId()));
			addressEntryData.setAddress1(StringUtils.defaultString(payerSapBp.getName1()) + " " +
					StringUtils.defaultString(payerSapBp.getName2()));
			addressEntryData.setAddress2(StringUtils.defaultString(payerSapBp.getStreet()) + " " + StringUtils.defaultString(payerSapBp.getHouseNumber()));
			addressEntryData.setAddress3(StringUtils.defaultString(payerSapBp.getCountry().getSapCountryCd()) + " " +
					StringUtils.defaultString(payerSapBp.getPostalCode()) + " " + StringUtils.defaultString(payerSapBp.getCity()));
		}
		
		if (soldtoSapBp != null && soldtoSapBp.getTreatAsOnetimeFlag()) {
			if (payerSapBp != null && soldtoSapBp.getSapBpId().equals(payerSapBp.getSapBpId())) {
				addressEntryData.setAddress1(oneTimeSoldTo.getAddress1());
				addressEntryData.setAddress2(oneTimeSoldTo.getAddress2());
				addressEntryData.setAddress3(oneTimeSoldTo.getAddress3());
				addressEntryData.setId(StringUtils.defaultString(soldtoSapBp.getSapId()));
			}
		}
		
		if (billTo != null && StringUtils.equals(billTo.getId(), addressEntryData.getId()) ) {
			addressEntryData.setVisible(false);
		}
		
		addressEntryData.setName(DeliveryNoteLabel.ADDRESS_PAYER.getMessageId());
		
		return addressEntryData;
	}
	
	public AddressEntryData createOneTimeShipToData(OrderEntity order) {
		AddressEntryData addressEntryData = new AddressEntryData();
		
		StringBuilder lnfCountryZipCity = new StringBuilder();
		lnfCountryZipCity.append(StringUtils.defaultString(order.getLnfSiteCountry()));
		lnfCountryZipCity.append(' ');
		lnfCountryZipCity.append(StringUtils.defaultString(order.getLnfSitePostalCode()));
		lnfCountryZipCity.append(' ');
		lnfCountryZipCity.append(StringUtils.defaultString(order.getLnfSiteCity()));
		
		addressEntryData.setAddress1(order.getLnfSiteName());
		addressEntryData.setAddress2(order.getLnfSiteStreetHouseNo());
		addressEntryData.setAddress3(lnfCountryZipCity.toString());
		addressEntryData.setVat(order.getLnfSiteVatNo());
		
		return addressEntryData;
	}
	
	public AddressEntryData createOneTimeSoldToData(OrderEntity order) {
		AddressEntryData addressEntryData = new AddressEntryData();
		
		StringBuilder oneTimeCountryZipCity = new StringBuilder();
		oneTimeCountryZipCity.append(StringUtils.defaultString(order.getOneTimeSoldtoCountry()));
		oneTimeCountryZipCity.append(' ');
		oneTimeCountryZipCity.append(StringUtils.defaultString(order.getOneTimeSoldtoPostalCode()));
		oneTimeCountryZipCity.append(' ');
		oneTimeCountryZipCity.append(StringUtils.defaultString(order.getOneTimeSoldtoCity()));

		addressEntryData.setAddress1(order.getOneTimeSoldtoName());
		addressEntryData.setAddress2(order.getOneTimeSoldtoStreetHouseno());
		addressEntryData.setAddress3(oneTimeCountryZipCity.toString());
		addressEntryData.setVat(order.getOneTimeSoldtoVatNo());
		
		return addressEntryData;
	}
	
	public BusinessAddressData createBusinessAddressData(Locale locale, OrderEntity order) {
		BusinessAddressData businessAddressData = new BusinessAddressData();
		SareaEntity sareaBs = order.getSarea();
		SapBpEntity soldtoSapBp = order.getSoldtoSapBp();
		SapBpEntity shipToSapBp = order.getShiptoSapBp();
		SapBpEntity billtoSapBp = order.getBilltoSapBp();
		SapBpEntity payerSapBp = order.getPayerSapBp();
		CarrierEntity execCarrier =  order.getExecCarrier();
		CarrierEntity commCarrier = order.getCommCarrier();
		
		AddressEntryData oneTimeSoldToData = createOneTimeSoldToData(order);
		AddressEntryData oneTimeShipToData = createOneTimeShipToData(order);
		
		businessAddressData.setSoldTo(createSoldToData(soldtoSapBp, oneTimeSoldToData));
		businessAddressData.setShipTo(createShipToData(shipToSapBp, oneTimeShipToData, order));
		businessAddressData.setCommCarrier(createCommCarrierData(commCarrier, sareaBs));
		businessAddressData.setExecCarrier(createExecCarrierData(execCarrier, sareaBs));
		businessAddressData.setBillTo(createBillToData(billtoSapBp, soldtoSapBp, oneTimeSoldToData));
		businessAddressData.setPayer(createPayToData(payerSapBp, soldtoSapBp, oneTimeSoldToData, businessAddressData.getBillTo()));
		
		return businessAddressData;
	}
	
	public TitleData createTitleData(Locale locale, OrderEntity order, TimeZone deliveryPlantTZ) {
		SareaEntity sareaBs = order.getSarea();
		final String logonOrderNumber = (order.getOrderNumber() != null ? order.getOrderNumber().toString() : "");
		final String seqOrderNumber = order.getOrderNumberFromSeqUsage();
		final String wsOrderNumber = order.getLdsDeliveryNoteNo();
		
		String orderNumber = null;
		String altOrderNumber = null;
		String altOrderNumber2 = null;
		String label_altOrderNumber = null;
		String label_altOrderNumber2 = null;
		
		TitleData title = new TitleData();
		
		if (StringUtils.isNotEmpty(seqOrderNumber)) {
			orderNumber = seqOrderNumber;
			altOrderNumber = logonOrderNumber;
			label_altOrderNumber = DeliveryNoteReferenceLabel.LOGON_ORDER_NUMBER.getMessageId();
			if (StringUtils.isNotEmpty(wsOrderNumber)) {
				altOrderNumber2 = wsOrderNumber;
				label_altOrderNumber2 = DeliveryNoteReferenceLabel.LDS_ORDER_NUMBER.getMessageId();
			}
			else {
				altOrderNumber2 = null;
				label_altOrderNumber2 = null;
			}
		}
		else if (StringUtils.isNotEmpty(wsOrderNumber)) {
			if (sareaBs.getDelnoteUsesWsNumberFlag()) {
				orderNumber = wsOrderNumber;
				altOrderNumber = logonOrderNumber;
				label_altOrderNumber = DeliveryNoteReferenceLabel.LOGON_ORDER_NUMBER.getMessageId();
			}
			else {
				orderNumber = logonOrderNumber;
				altOrderNumber = wsOrderNumber;
				label_altOrderNumber = DeliveryNoteReferenceLabel.LDS_ORDER_NUMBER.getMessageId();
			}
			altOrderNumber2 = null;
			label_altOrderNumber2 = null;
		}
		else {
			orderNumber = logonOrderNumber;
			altOrderNumber = null;
			altOrderNumber2 = null;
			label_altOrderNumber = null;
			label_altOrderNumber2 = null;
		}

		String delNoteVersionStr = deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(new Date());
		String plantName = "";
		String shippingPoint = "";
		ShippingpointEntity shippingpoint = order.getShippingpoint();
		
		if (shippingpoint != null) {
			if (shippingpoint.getShippingpointName() != null) {
				shippingPoint = shippingpoint.getShippingpointName();
			}
			
			if (shippingpoint.getPlant() != null) {
				plantName = shippingpoint.getPlant().getPlantName();
			}
		}
		
		title.setDeliveryPlant(plantName);
		title.setShippingPoint(shippingPoint);
		title.setDeliveryNoteDate(delNoteVersionStr);
		title.setOrderNumber(orderNumber);
		title.setAltOrderNumber(altOrderNumber);
		title.setAltOrderNumber2(altOrderNumber2);
		
		title.setLabel_deliveryNote(
				Message.lookup(locale, DeliveryNoteLabel.ORDER_NUMBER.getMessageId()));
		title.setLabel_altOrderNumber(
				pdfGeneratorService.formatText(label_altOrderNumber, locale));
		title.setLabel_altOrderNumber2(
				pdfGeneratorService.formatText(label_altOrderNumber2, locale));
		
		return title;
	}

}
