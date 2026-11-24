/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.store;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.stores.domain.SapMaterialPlantNormStoreInterface;
import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.impl.OrderServiceBean;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MaterialEnhancementsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.EichbelegDTO;
import com.crh.logon.business.services.pdftools.layout.MaterialTotalsTextDTO;
import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.business.services.pdftools.layout.delnote.CashSalesPriceType;
import com.crh.logon.business.services.pdftools.layout.delnote.ExportInvoiceMaterialPriceType;
import com.crh.logon.business.services.utils.InvoiceBean;
import com.crh.logon.business.services.utils.LangUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.SchedulingUtils;
import com.crh.logon.business.services.utils.UOMConverter;
import com.crh.logon.constants.Constant;
import com.crh.logon.pdftool.labelprovider.DTOMarker;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.MatPlantTxEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PlantCompcodeEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SapCountryBsEntity;
import com.crh.logon.persistence.entities.refdata.SapMaterialNormBsEntity;
import com.crh.logon.persistence.entities.refdata.SapPaymentMethodTxEntity;
import com.crh.logon.persistence.entities.refdata.SorgBsEntity;
import com.crh.logon.persistence.entities.refdata.SorgTxEntity;
import com.crh.logon.persistence.entities.refdata.TextTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapMatPlantNormEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.BeanFactory;
import com.crh.logon.utils.WagonNumberUtils;
import com.crh.logon.utils.text.Transliterator;

/**
 * Provides the labels and data for the delivery note from the input order.
 * @author AdNovum Informatik AG
 */
@AdnTrace
@Named(value="deliveryNotePdfDataStore")
public class DeliveryNotePdfDataStore extends AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> {
	
	private enum PriceToPrint {
		DONTPRINT, ZERO, ACTUALPRICE
	}
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryNotePdfDataStore.class);
	
	private static final String RESULT_MAP_KEY_PLANT_NAME = "plantName";
	private static final String RESULT_MAP_KEY_RETURN_DATA_FLAG = "returnDataFlag";
	private static final String RESULT_MAP_KEY_BIMODAL = "bimodalWagonOrderNo";
	private static final String RESULT_MAP_KEY_BIMODAL_WAGONNO = "bimodalWagonNo";
	private static final String RESULT_MAP_KEY_BIMODAL_DEL_QTY = "bimodalWagonDelQty";
	private static final String RESULT_MAP_KEY_CONTAINER_NO = "containerNo";
	private static final String RESULT_MAP_KEY_PKG_CD = "pkgCd";
	private static final String RESULT_MAP_KEY_RETURNED_PALLETS = "returnedPallets";
	
	public static final Long BULK_PWDR_PKG_CD = Constant.PACKAGE_TYPE_BULK_POWDER;
	public static final Long BULK_DMPD_PKG_CD = Constant.PACKAGE_TYPE_BULK_DUMPED;
	public static final Long BAG_PKG_CD = Constant.PACKAGE_TYPE_BBAG;
	public static final Long SERVICE_PKG_CD = Constant.PACKAGE_TYPE_SERVICE;
	public static final Long BAG_AND_SERVICE = Constant.MIXED_ORDER_BAG_AND_SERVICE;
	public static final Long BULK_AND_SERVICE = Constant.MIXED_ORDER_BULK_AND_SERVICE;
	
	private static final String INITIAL_DATA_STR = "-";
	private static final int ROUNDING_SCALE = 2;

	private static final String NO_COUNTRY = null;
	
	@Inject GenericBlobServiceLocalInterface genericBlobService;
	@Inject RefDataGenericsServiceLocalInterface referentialDataService;
	@Inject
	PDFGeneratorServiceLocalInterface pdfGeneratorService;
	@Inject MaterialEnhancementsServiceLocalInterface materialEnhancementsService;
	@Inject OrderServiceLocalInterface orderService;
	@Inject
	DispokarteServiceLocalInterface dispokarteService;
	@Inject
	TimeZoneServiceLocalInterface timezoneService;
	@Inject
	TruckAdminServiceLocalInterface truckAdminService;
	@Inject DispoServiceLocalInterface dispoService;
	@Inject
	ConfigServiceLocalInterface configService;
	@Inject
	SapMaterialPlantNormStoreInterface sapMaterialPlantNormStore;
	
	protected DaoProvider daoProvider;

	protected MatPlantEntity matPlantEntity;

	private List<SapMatPlantNormEntity> matPlantNormList = null;

	private DriverEntity driver = null;

	protected List<OrderItemDTO> orderItemDTOs = new ArrayList<OrderItemDTO>();

	private List<EichbelegDTO> eichbelegList = new ArrayList<EichbelegDTO>();

	public DeliveryNotePdfDataStore() {
		this(null);
	}
	
	public DeliveryNotePdfDataStore(Locale locale) {
		super(locale);
	}
	
	public DeliveryNotePdfDataStore (DaoProvider daoProvider, Locale locale, OrderEntity order, boolean isExportInvoice, boolean isCashSales, Transliterator transliterator) {
		super(locale, transliterator);
		if (order != null) {
			initializeData(order, isExportInvoice, isCashSales);
		}		
	}
	
	public static DeliveryNotePdfDataStore getDeliveryNotePdfDataStore(DaoProvider daoProvider, Locale locale, OrderEntity order, boolean isExportInvoice, boolean isCashSales, Transliterator transliterator) {
		DeliveryNotePdfDataStore store = BeanFactory.createBean("deliveryNotePdfDataStore", DeliveryNotePdfDataStore.class);

		store.setDaoProvider(daoProvider);
		store.initStore(locale, transliterator);
		
		if (order != null) {
			store.initializeData(order, isExportInvoice, isCashSales);
		}
		return store;
	}
	

	/**
	 * Get the generic blob service. Required for logo printing
	 * @return
	 */
	public final GenericBlobServiceLocalInterface getGenericBlobService() {
		return genericBlobService;
	}
	
	/**
	 * Get the material plant entity. Required for logo printing.
	 * @return
	 */
	public final MatPlantEntity getMatPlantEntity() {
		return matPlantEntity;
	}
	
	/**
	 * Get the SAP material plant norm list. Required for logo printing.
	 * @return
	 */
	public List<SapMatPlantNormEntity> getMatPlantNormList() {
		return matPlantNormList;
	}

	/* (non-Javadoc)
	 * @see com.crh.logon.pdftool.store.AbstractPdfTextStore#initializeLabels()
	 */
	@Override
	protected void initializeLabels() {
		if(locale != null) {
			for (DeliveryNoteLabel label : DeliveryNoteLabel.values()) {
				addLabel(label, Message.lookup(locale, label.getMessageId()));
			}			
		}
	}
	
	protected AssignmentEntity getAssignment(Long orderId) {
		ReadWriteDao<AssignmentPartEntity> asspDao = daoProvider.getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> filter = new FilterImpl<AssignmentPartEntity>();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, orderId));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, true));
		AssignmentPartEntity assPart = asspDao.findSingle(filter);
		return (assPart != null) ? assPart.getAssignment() : null;
	}
	
	protected void setDestAddress(SorgBsEntity sorgBs) {
		SorgTxEntity sorgTx = referentialDataService.findTxEntity(SorgTxEntity.class, locale, sorgBs);
		
		addData(DeliveryNoteData.DEST_ADDRESS1, StringUtils.defaultString(sorgTx.getAddressName()));
		addData(DeliveryNoteData.DEST_ADDRESS2, StringUtils.defaultString(sorgBs.getAddressStreet()));
		addData(DeliveryNoteData.DEST_ADDRESS3, StringUtils.defaultString(sorgBs.getAddressCountrycity()));
		addData(DeliveryNoteData.DEST_TEL, StringUtils.defaultString(sorgTx.getLinePhone()));
		addData(DeliveryNoteData.DEST_FAX, StringUtils.defaultString(sorgTx.getLineFax()));
		addData(DeliveryNoteData.DEST_TAX, StringUtils.defaultString(sorgTx.getLineTax()));
		addData(DeliveryNoteData.DEST_EMAIL, StringUtils.defaultString(sorgTx.getLineEmail()));
		addData(DeliveryNoteData.BANNER_IMG_NAME, StringUtils.defaultString(sorgTx.getImageDelNoteGeneralInfo()));
		// crh logo
		addData(DeliveryNoteData.CRH_LOGO, sorgBs.getLogoImageName());
	}

	protected void setDeliveryWindow(SapBpEntity sapBpEntity, Date beginDate, Date endDate) {
		if (beginDate != null && endDate != null) {
			TimeZone shipToTZ = getTimeZoneForShipTo(sapBpEntity);

			StringBuilder fromTime = new StringBuilder();
			fromTime.append(shipToTZ.fmtAsHourMinuteWithOffset(beginDate))
					.append(" (")
					.append(shipToTZ.fmtAsDayMonth(beginDate))
					.append(')');

			StringBuilder toTime = new StringBuilder();
			toTime.append(shipToTZ.fmtAsHourMinuteWithOffset(endDate))
				  .append(" (")
				  .append(shipToTZ.fmtAsDayMonth(endDate))
				  .append(')');

			StringBuilder deliveryWindowTime = new StringBuilder();
			deliveryWindowTime.append(fromTime).append(" - ").append(toTime);

			addData(DeliveryNoteData.DELIVERY_WINDOW, deliveryWindowTime.toString());
			addData(DeliveryNoteData.DELIVERY_WINDOW_FROM, fromTime.toString());
			addData(DeliveryNoteData.DELIVERY_WINDOW_TO, toTime.toString());
		}
	}

	private void initializeData(OrderEntity order, boolean isExportInvoice, boolean isCashSales) {
		// select assignment	
		final AssignmentEntity assignment = getAssignment(order.getOrderId());

		//load the map
		final Map<String, ?> matPlantMap = getMatPlant(locale, order);

		matPlantEntity = (MatPlantEntity) matPlantMap.get(MatPlantEntity.class.getName());

		if (matPlantEntity != null) {
			matPlantNormList = getSapMatPlantNormList(matPlantEntity, order);
		}

		// Sales area and organization (replaces order.getVko())
		SareaEntity sareaBs = order.getSarea();
		// set visibility related settings from sareaEntity
		SorgBsEntity sorgBs = null;

		if (sareaBs != null) {
			sorgBs = sareaBs.getSorg();
			setVisibilitySettings(sareaBs);
		}

		if (sorgBs != null) {
			setDestAddress(sorgBs);
		}

		// Set the primary and the alternative order number
		final String logonOrderNumber = (order.getOrderNumber() != null ? order.getOrderNumber().toString()
				: "");
		final String seqOrderNumber = order.getOrderNumberFromSeqUsage();
		final String wsOrderNumber = order.getLdsDeliveryNoteNo();

		final String orderNumber;
		String altOrderNumber;
		String altOrderNumber2;
		final String altOrderNumberTextCd;
		final String altOrderNumber2TextCd;

		if (StringUtils.isNotEmpty(seqOrderNumber)) {
			orderNumber = seqOrderNumber;
			altOrderNumber = logonOrderNumber;
			altOrderNumberTextCd = "pdf.deliverynote.lt";
			if (StringUtils.isNotEmpty(wsOrderNumber)) {
				altOrderNumber2 = wsOrderNumber;
				altOrderNumber2TextCd = "pdf.deliverynote.lds";
			}
			else {
				altOrderNumber2 = null;
				altOrderNumber2TextCd = null;
			}
		}
		else if (StringUtils.isNotEmpty(wsOrderNumber)) {
			if (sareaBs.getDelnoteUsesWsNumberFlag()) {
				orderNumber = wsOrderNumber;
				altOrderNumber = logonOrderNumber;
				altOrderNumberTextCd = "pdf.deliverynote.lt";
			}
			else {
				orderNumber = logonOrderNumber;
				altOrderNumber = wsOrderNumber;
				altOrderNumberTextCd = "pdf.deliverynote.lds";
			}
			altOrderNumber2 = null;
			altOrderNumber2TextCd = null;
		}
		else {
			orderNumber = logonOrderNumber;
			altOrderNumber = null;
			altOrderNumber2 = null;
			altOrderNumberTextCd = null;
			altOrderNumber2TextCd = null;
		}

		if (altOrderNumber != null) {
			altOrderNumber = formatText(altOrderNumberTextCd, locale, null, null, null, null, null)
					+ ": " + altOrderNumber;
		}
		if (altOrderNumber2 != null) {
			altOrderNumber2 = formatText(altOrderNumber2TextCd, locale, null, null, null, null,
					null) + ": " + altOrderNumber2;
		}
		addData(DeliveryNoteData.ORDER_NR, orderNumber);
		addData(DeliveryNoteData.ORDER_NR_ALT, altOrderNumber);
		addData(DeliveryNoteData.ORDER_NR_ALT2, altOrderNumber2);

		//time of the generation of this delivery note
		TimeZone deliveryPlantTZ = getTimeZoneForDeliveryPlant(order.getShippingpoint());
		String delNoteVersionStr = deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(new Date());
		addData(DeliveryNoteData.DELIVERY_NOTE_DATE, delNoteVersionStr);

		// production plant and shipping point data
		String plantName = (String) matPlantMap.get(RESULT_MAP_KEY_PLANT_NAME);
		String shippingPointData = (order.getShippingpoint() != null) ? order.getShippingpoint().getShippingpointName()
				: "";
		addData(DeliveryNoteData.PLANT, plantName);
		addData(DeliveryNoteData.SHIPPING_POINT, shippingPointData);

		String bimodalWagonNo = (String) matPlantMap.get(RESULT_MAP_KEY_BIMODAL_WAGONNO);
		String containerNrs = (String) matPlantMap.get(RESULT_MAP_KEY_CONTAINER_NO);

		//set wagon
		if (assignment != null) {
			if (assignment.getTruck() != null && StringUtils.isNotEmpty(bimodalWagonNo)) {//bimodal case
				addData(DeliveryNoteData.WAGON_NR, WagonNumberUtils.toDisplayNo(bimodalWagonNo));
			}
			else if (assignment.getWagon() != null) {
				addData(DeliveryNoteData.WAGON_NR,
						WagonNumberUtils.toDisplayNo(assignment.getWagon().getWagonNo()));
			}
			
		}

		if (StringUtils.isNotEmpty(containerNrs)) {
			addData(DeliveryNoteData.CONTAINER_NR, containerNrs);
		}

		// Eastern Europe specific
		if (!isExportInvoice) {
			String fixRemarkTxt = pdfGeneratorService.lookUpTxText("pdf.deliverynote.remarks.text",
					locale);
			if (StringUtils.isNotEmpty(fixRemarkTxt) && order.getSarea().getDnShowFixText()) {
				addData(DeliveryNoteData.FIX_REMARK_TXT, fixRemarkTxt);
			}
		}

		// Eastern Europe specific
		if (assignment != null) {
			if (StringUtils.isNotEmpty(assignment.getLdsSealNumbers())
					&& order.getSarea().getDnShowSealsInfoFlag()) {
				addData(DeliveryNoteData.SEALS_NUMBERS, assignment.getLdsSealNumbers());
			}
			if (StringUtils.isNotEmpty(assignment.getLdsCarrierCrm())
					&& order.getSarea().getDnShowSealsInfoFlag()) {
				addData(DeliveryNoteData.CARRIER_CRM, assignment.getLdsCarrierCrm());
			}
			if (StringUtils.isNotEmpty(assignment.getLdsDispatcherId())
					&& order.getSarea().getDnShowDispatcherDriverFlag()) {
				addData(DeliveryNoteData.DISPATCHER_ID, assignment.getLdsDispatcherId());
			}
			if (StringUtils.isNotEmpty(assignment.getLdsDispatcherName())
					&& order.getSarea().getDnShowDispatcherDriverFlag()) {
				addData(DeliveryNoteData.DISPATCHER_NAME, assignment.getLdsDispatcherName());
			}
			if (StringUtils.isNotEmpty(assignment.getLdsDispatcherTel())
					&& order.getSarea().getDnShowDispatcherDriverFlag()) {
				addData(DeliveryNoteData.DISPATCHER_TEL, assignment.getLdsDispatcherTel());
			}
			if (StringUtils.isNotEmpty(assignment.getLdsDriverCardNo())
					&& order.getSarea().getDnShowDispatcherDriverFlag()) {
				addData(DeliveryNoteData.DRIVER_CARD_NO, assignment.getLdsDriverCardNo());
			}
			if (StringUtils.isNotEmpty(assignment.getLdsDriverId())
					&& order.getSarea().getDnShowDispatcherDriverFlag()) {
				addData(DeliveryNoteData.DRIVER_ID, assignment.getLdsDriverId());
			}
			if (StringUtils.isNotEmpty(assignment.getLdsDriverName())
					&& order.getSarea().getDnShowDispatcherDriverFlag()) {
				addData(DeliveryNoteData.DRIVER_NAME, assignment.getLdsDriverName());
			}
		}

		// layout changes based on this data
		boolean isBimodalWithContainer = false;
		if (assignment != null && assignment.getTruck() != null
				&& StringUtils.isNotEmpty(assignment.getTruck().getTruckPlateNo())
				&& !getData(DeliveryNoteData.WAGON_NR).isEmpty()
				&& !getData(DeliveryNoteData.CONTAINER_NR).isEmpty()) {
			isBimodalWithContainer = true;
		}
		addData(DeliveryNoteData.IS_BIMODAL_WITH_CONTAINER,
				Boolean.toString(isBimodalWithContainer));

		// distance
		if (order.getCommercialDistanceM() != null) {
			addData(DeliveryNoteData.DISTANCE, Long.toString(order.getCommercialDistanceM() / 1000));
		}

		setDeliveryWindow(order.getShiptoSapBp(), order.getDeliveryFromDat(),
				order.getDeliveryToDat());

		// customer reference number + SAP contract number
		CustorderEntity custorder = order.getCustorder();
		String custRefNo = custorder != null ? custorder.getCustorderCustrefNo() : null;
		SapContractEntity sapContract = order.getSapContract();
		final SareaEntity sarea = order.getSarea();
		String contractNo = null;
		String contractCurrencyCode = "";
		if (sapContract != null) {
			contractNo = sapContract.getSapContractNo();
			contractCurrencyCode = sapContract.getCurrency().getCurrencyCd();
		}
		if (custRefNo != null || contractNo != null) {
			final StringBuilder custRefNoAndContractNoTitleSb = new StringBuilder();
			final StringBuilder custRefNoAndContractNoDataSb = new StringBuilder();

			if (custRefNo != null) {
				custRefNoAndContractNoTitleSb.append(formatText("pdf.deliverynote.custrefno",
						locale, null, null, null, null, null));
				custRefNoAndContractNoDataSb.append(custRefNo);
			}
			if (contractNo != null) {
				if (custRefNo != null) {
					custRefNoAndContractNoTitleSb.append(" / ");
					custRefNoAndContractNoDataSb.append(" / ");
				}

				custRefNoAndContractNoTitleSb.append(formatText("pdf.deliverynote.contractno",
						locale, null, null, null, null, null));
				contractNo = StringUtils.stripStart(contractNo, "0");
				custRefNoAndContractNoDataSb.append(contractNo);

				custRefNoAndContractNoTitleSb.append(':');
			}
			addData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_TILE,
					custRefNoAndContractNoTitleSb.toString());
			addData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_DATA,
					custRefNoAndContractNoDataSb.toString());

			addData(DeliveryNoteData.CUST_REF_NO, custRefNo);
			addData(DeliveryNoteData.SAP_CONTRACT_NO, contractNo);
		}

		// Cash Sales Invoice Form
		if (isCashSales) {
			// SAP payment method
			if (sapContract != null && sapContract.getPaymentMethod() != null
					&& sarea.getCsShowPaymentMthFlag()) {
				final String contractPaymentMethodCd = sapContract.getPaymentMethod().getSapPaymentMethodCd();
				final SapPaymentMethodTxEntity sapPaymentTxEntity = referentialDataService.findTxEntity(
						SapPaymentMethodTxEntity.class, locale, contractPaymentMethodCd);
				if (sapPaymentTxEntity != null && sapPaymentTxEntity.getTxt() != null) {
					addData(DeliveryNoteData.CASH_SALES_PAYMENT_METHOD, sapPaymentTxEntity.getTxt());
				}
				else {
					LOG.warn(
							"Could not load the SapPaymentMethodTxEntity for contractPaymentMethodCd: '{}'.",
							new Object[] { contractPaymentMethodCd });
				}
				
			}
			// Tax number of the sold to
			final SapBpEntity soldTo = order.getSoldtoSapBp();
			String soldToTaxNumber1 = soldTo.getTaxNumber1();
			final String taxNumber1Label = getLabel(DeliveryNoteLabel.TAX_NO);
			if (sarea.getCsShowCustTaxCodeFlag() && soldToTaxNumber1 != null
					&& !soldToTaxNumber1.isEmpty()) {
				soldToTaxNumber1 = taxNumber1Label + "   " + soldToTaxNumber1;
				addData(DeliveryNoteData.CASH_SALES_TAX_NUMBER1, soldToTaxNumber1);
			}
			// Price type
			final Long cashSalesPriceType = sarea.getCsPriceType();
			addData(DeliveryNoteData.CASH_SALES_PRICE_TYPE, cashSalesPriceType.toString());
			// Show totals as text
			final Boolean showTotalsTextFlag = sarea.getCsShowTotalsTextFlag();
			addData(DeliveryNoteData.CASH_SALES_SHOW_TOTAL_AS_TEXT, showTotalsTextFlag.toString());
			
			// Fixed Text before Material Lines
			final String companyCode = sarea.getSorg().getCompanycode().getCompanycodeCd();
			final SapBpEntity shiptoSapBp = order.getShiptoSapBp();
			final SapCountryBsEntity countryBs = shiptoSapBp.getCountry();
			final String shipToCountryCode = countryBs.getSapCountryCd();
			final String key = Constant.CASH_SALES_INVOICE_MATERIAL_HEADER_KEY + "." + companyCode
					+ shipToCountryCode;
			final TextTxEntity textTxEntity = referentialDataService.findTxEntity(
					TextTxEntity.class, locale, key);
			if (textTxEntity != null && textTxEntity.getTxt() != null) {
				final String fixedText = textTxEntity.getTxt();
				addData(DeliveryNoteData.CASH_SALES_FIXED_TEXT, fixedText);
			}
		}

		//set endload time
		Date latestLoadingTs = null;
		for (OrderItemEntity item : (List<OrderItemEntity>) order.getOrderItems()) {
			if (item.getLoad() != null && item.getLoad().getLoadingTs() != null) {
				Date loadingTs = item.getLoad().getLoadingTs();
				if (latestLoadingTs == null || loadingTs.after(latestLoadingTs)) {
					latestLoadingTs = loadingTs;
				}
			}
		}

		if (latestLoadingTs != null) {
			String loadDate = deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(latestLoadingTs);
			addData(DeliveryNoteData.LOAD_DATE, loadDate);
		}

		// delivery time
		final String deliveryTimeString = getDeliveryTimeString(order);
		addData(DeliveryNoteData.DELIVERY_DATE, deliveryTimeString);

		// one time solto and shipto data
		final List<String> oneTimeSoldTo = new ArrayList<String>();
		oneTimeSoldTo.add(order.getOneTimeSoldtoName());
		oneTimeSoldTo.add(order.getOneTimeSoldtoStreetHouseno());

		final StringBuilder oneTimeCountryZipCity = new StringBuilder();
		oneTimeCountryZipCity.append(StringUtils.defaultString(order.getOneTimeSoldtoCountry()));
		oneTimeCountryZipCity.append(' ');
		oneTimeCountryZipCity.append(StringUtils.defaultString(order.getOneTimeSoldtoPostalCode()));
		oneTimeCountryZipCity.append(' ');
		oneTimeCountryZipCity.append(StringUtils.defaultString(order.getOneTimeSoldtoCity()));
		oneTimeSoldTo.add(oneTimeCountryZipCity.toString());
		oneTimeSoldTo.add(order.getOneTimeSoldtoVatNo());

		final List<String> oneTimeShipTo = new ArrayList<String>();
		oneTimeShipTo.add(order.getLnfSiteName());
		oneTimeShipTo.add(order.getLnfSiteStreetHouseNo());

		final StringBuilder lnfCountryZipCity = new StringBuilder();
		lnfCountryZipCity.append(StringUtils.defaultString(order.getLnfSiteCountry()));
		lnfCountryZipCity.append(' ');
		lnfCountryZipCity.append(StringUtils.defaultString(order.getLnfSitePostalCode()));
		lnfCountryZipCity.append(' ');
		lnfCountryZipCity.append(StringUtils.defaultString(order.getLnfSiteCity()));
		oneTimeShipTo.add(lnfCountryZipCity.toString());
		if (!StringUtils.isEmpty(order.getLnfSiteVatNo())) {
			oneTimeShipTo.add(order.getLnfSiteVatNo());
		}

		
		setAddresses(
				order.getSoldtoSapBp(), order.getShiptoSapBp(), order.getBilltoSapBp(), order.getPayerSapBp(), 
				order.getCommCarrier(), order.getExecCarrier(), 
				oneTimeSoldTo, oneTimeShipTo, order);
		
		// delivery type
		final DeliveryTypeTxEntity deliveryTypeTxEntity = 
				referentialDataService.findTxEntity(DeliveryTypeTxEntity.class, locale, order.getDeliveryType().getDeliveryTypeCd());
		if (deliveryTypeTxEntity != null) {
			addData(DeliveryNoteData.DELIVERY_TYPE, deliveryTypeTxEntity.getTxt());
		}
		// incoterm
		addData(DeliveryNoteData.INCOTERM, sapContract != null ? sapContract.getIncotermsCd() : null);
		
		// reason of transport
		String reasonOfTransport = null;
		if (sapContract != null && sapContract.getSapContractType().getContractTypeIsStpoFlag()) {
			reasonOfTransport = "pdf.coll.reason_transport.stpo";
		}
		else {
			reasonOfTransport = "pdf.coll.reason_transport.sales";
		}
		addData(DeliveryNoteData.REASON_OF_TRANSPORT, Message.lookup(locale, reasonOfTransport));

		// nhm code & crossboundary
		if ((order.getShiptoSapBp() != null) && (order.getShippingpoint() != null)
				&& (order.getShippingpoint().getPlant() != null)) {
			
			final String countryCode = order.getShiptoSapBp().getCountry().getSapCountryCd();
			
			final PlantBsEntity plant = order.getShippingpoint().getPlant();
			final ReadOnlyDao<PlantCompcodeEntity> plantCompcodeEntityDao = daoProvider.getPlantCompcodeEntityDao();
			final Filter<PlantCompcodeEntity> plantCompcodeEntityFilter = plantCompcodeEntityDao.createFilter();
			plantCompcodeEntityFilter.addExpression(Restrictions.eq(PlantCompcodeEntity.ATTR_PLANT,
					plant));
			
			final PlantCompcodeEntity plantCompcodeEntity = plantCompcodeEntityDao.findSingle(plantCompcodeEntityFilter);
			final String companyCode = plantCompcodeEntity != null ? plantCompcodeEntity.getCompanycode().getCompanycodeCd()
					: "";
			
			if (isExportInvoice) {

				final StringBuilder crossBoundaryKey1 = new StringBuilder();
				crossBoundaryKey1.append("pdf.delivnote.export.crossboundary1");
				crossBoundaryKey1.append('.');
				crossBoundaryKey1.append(companyCode);
				crossBoundaryKey1.append(countryCode);

				TextTxEntity crossBoundaryText1 = referentialDataService.findTxEntity(
						TextTxEntity.class, locale, crossBoundaryKey1.toString());
				addData(DeliveryNoteData.CROSSBOUNDARY_EXPORT_INVOICE_LINE1,
						crossBoundaryText1 != null ? crossBoundaryText1.getTxt() : "");

				final StringBuilder crossBoundaryKey2 = new StringBuilder();
				crossBoundaryKey2.append("pdf.delivnote.export.crossboundary2");
				crossBoundaryKey2.append('.');
				crossBoundaryKey2.append(companyCode);
				crossBoundaryKey2.append(countryCode);

				TextTxEntity crossBoundaryText2 = referentialDataService.findTxEntity(
						TextTxEntity.class, locale, crossBoundaryKey2.toString());
				addData(DeliveryNoteData.CROSSBOUNDARY_EXPORT_INVOICE_LINE2,
						crossBoundaryText2 != null ? crossBoundaryText2.getTxt() : "");
			}
			else {

				final StringBuilder crossboundaryKey = new StringBuilder();
				crossboundaryKey.append("pdf.deliverynote.crossboundary");
				crossboundaryKey.append('.');
				crossboundaryKey.append(companyCode);
				crossboundaryKey.append(countryCode);

				// call service find method directly: getText->formatText would log an error if result is null
				final TextTxEntity resultText = referentialDataService.findTxEntity(
						TextTxEntity.class, locale, crossboundaryKey.toString());
				addData(DeliveryNoteData.CROSSBOUNDARY, resultText != null ? resultText.getTxt()
						: "");
			}
			//If > 1 show the first item's NHM code
			if (order.getOrderItems() != null) {
				final OrderItemEntity oi = (OrderItemEntity) order.getOrderItems().get(0);
				
				final ReadOnlyDao<SapMaterialPlantEntity> sapDao = daoProvider.getSapMaterialPlantEntityDao();
				final Filter<SapMaterialPlantEntity> sapFilter = sapDao.createFilter();
				sapFilter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_PLANT,
						oi.getProductionPlant()));
				sapFilter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_SAP_MATERIAL,
						oi.getSapMaterial()));
				
				final SapMaterialPlantEntity sapMatPlant = sapDao.findSingle(sapFilter);
				
				if (sapMatPlant != null) {
					addData(DeliveryNoteData.NHM, sapMatPlant.getNhmCode());
				}
			}
			
		}

		// codes for message lookups
		String distributionChannel = null;
		String divisionCd = null;
		if (sareaBs != null) {
			distributionChannel = sareaBs.getDistributionChannel();
			if (sareaBs.getDivision() != null) {
				divisionCd = sareaBs.getDivision().getDivisionCd();
			}
		}

		// stattion title
		String bimodalWagonOrderNo = (String) matPlantMap.get(RESULT_MAP_KEY_BIMODAL);
		if (bimodalWagonOrderNo != null) {
			String bimodalWagonDelQty = (String) matPlantMap.get(RESULT_MAP_KEY_BIMODAL_DEL_QTY);
			
			String wagonNumber = StringUtils.isNotEmpty(bimodalWagonNo) ? WagonNumberUtils.toDisplayNo(bimodalWagonNo)
					: "";
			
			addData(DeliveryNoteData.STATION_TITLE_PAR,
					formatText("pdf.deliverynote.bimodal_ord_wagon_inf", locale, new Object[] {
							bimodalWagonOrderNo, wagonNumber, bimodalWagonDelQty },
							order.getDeliveryType(), sorgBs, distributionChannel, divisionCd));
		}
		else {
			addData(DeliveryNoteData.STATION_TITLE_PAR,
					formatText("pdf.deliverynote.deliverytype", locale, null,
							order.getDeliveryType(), sorgBs, distributionChannel, divisionCd));
		}

		String responsibilityText;
		if (order.getDeliveryType().getDeliveryTypeCd() == Constant.DELIVERY_TYPE_PICKUP) {
			responsibilityText = "pdf.transport_responsible.customer";
		}
		else if (order.getCommCarrier() != null
				&& order.getCommCarrier().getSapVendor().getVnIsInternal()) {
			responsibilityText = "pdf.transport_responsible.crh";
		}
		else {
			responsibilityText = "pdf.transport_responsible.carrier";
		}
		addData(DeliveryNoteData.TRANSPORT_RESPONSIBILITY_INFORMATION,
				Message.lookup(locale, responsibilityText));

		// chromat reduction month
		// FIXME (pdavid Jul 19, 2012): don't display chromat reduction if order is landfill or aggregates material based
		BigDecimal chrRedMonths = order.getShiptoSapBp().getChromatReductionMonths();
		if (chrRedMonths != null && matPlantEntity != null) {
			MatPlantTxEntity matPlantTxEntity = materialEnhancementsService.loadMatPlantTx(
					matPlantEntity.getMatPlantId(), locale);
			if (matPlantTxEntity != null) {
				String chromatText = matPlantTxEntity.getChromatReductionTxt();
				// no chromat text found, add missing text label
				if (chromatText == null) {
					chromatText = getLabel(DeliveryNoteLabel.CHROMAT_MISSING);
				}
				addData(DeliveryNoteData.CHROMAT_REDUCTION_COMM,
						MessageFormat.format(chromatText, chrRedMonths));
			}
		}

		// comment
		final StringBuilder commentDataStr = new StringBuilder();
		final String lnfAddress = LogonUtil.createLNFAddressString(order);

		commentDataStr.append(lnfAddress);
		if (StringUtils.isNotBlank(lnfAddress)) {
			commentDataStr.append('\n');
		}

		if (order.getDeliverynoteSignature() != null) {
			commentDataStr.append(StringUtils.defaultString(order.getCustDeliveryNoteComment()));
		}
		else {
			commentDataStr.append(StringUtils.defaultString(order.getNoteDelivery()));
		}

		addData(DeliveryNoteData.COMMENT, commentDataStr.toString());

		// driver
		if (assignment != null) {
			driver = getDriver(assignment.getTruck());
			
			if (driver != null) {
				if (driver.getUser().getPerson() != null) {
					addData(DeliveryNoteData.DRIVER,
							StringUtils.defaultString(driver.getUser().getPerson().getFirstname())
									+ " "
									+ StringUtils.defaultString(driver.getUser().getPerson().getName())
									+ " / ");
				}

				// add drivers signature
				if ((driver.getUser().getSignature() != null)
						&& (driver.getUser().getSignature().getData() != null)) {
					addByteStream(DeliveryNoteData.DRIVER_SIG_BYTE_STREAM,
							driver.getUser().getSignature().getData());
				}
			}
			
		}
		// customer
		final String customerName = (StringUtils.isEmpty(order.getContactPersonSignatureTxt())) ? ""
				: order.getContactPersonSignatureTxt() + " / ";
		addData(DeliveryNoteData.CUSTOMER, customerName);

		// customer sognature
		if ((order.getDeliverynoteSignature() != null)
				&& (order.getDeliverynoteSignature().getData() != null)) {
			addByteStream(DeliveryNoteData.CUST_SIG_BYTE_STREAM,
					order.getDeliverynoteSignature().getData());
		}

		// footer
		String footerTitle = formatText("pdf.deliverynote.footer.title", locale, null,
				order.getDeliveryType(), sorgBs, distributionChannel, divisionCd);
		String footerText = formatText("pdf.deliverynote.footer.text", locale, null,
				order.getDeliveryType(), sorgBs, distributionChannel, divisionCd);
		addData(DeliveryNoteData.FOOTER_TITLE, footerTitle);
		addData(DeliveryNoteData.FOOTER_TEXT, footerText);

		// eichbeleg text
		addData(DeliveryNoteData.EICHBELEG_TEXT,
				formatText("pdf.delivnote.eichbeleg.intro", locale, null, order.getDeliveryType(),
						sorgBs, distributionChannel, divisionCd));

		// return data flag
		addData(DeliveryNoteData.RETURN_DATA_FLAG,
				matPlantMap.get(RESULT_MAP_KEY_RETURN_DATA_FLAG).toString());

		// set number of returned pallets
		BigDecimal returnedPallets = (BigDecimal) matPlantMap.get(RESULT_MAP_KEY_RETURNED_PALLETS);
		if (returnedPallets.compareTo(BigDecimal.ZERO) > 0) {
			addData(DeliveryNoteData.RETURNED_PALLETS, returnedPallets.toString());
		}

		// is it bulk or bag or mixed?
		addData(DeliveryNoteData.IS_BULK,
				Boolean.toString(matPlantMap.get(RESULT_MAP_KEY_PKG_CD).equals(BULK_PWDR_PKG_CD)
						|| matPlantMap.get(RESULT_MAP_KEY_PKG_CD).equals(BULK_DMPD_PKG_CD)));
		addData(DeliveryNoteData.IS_BAG,
				Boolean.toString(matPlantMap.get(RESULT_MAP_KEY_PKG_CD).equals(BAG_PKG_CD)));
		addData(DeliveryNoteData.IS_SERVICE_ONLY,
				Boolean.toString(matPlantMap.get(RESULT_MAP_KEY_PKG_CD).equals(SERVICE_PKG_CD)));
		addData(DeliveryNoteData.IS_BAG_AND_SERVICE,
				Boolean.toString(matPlantMap.get(RESULT_MAP_KEY_PKG_CD).equals(BAG_AND_SERVICE)));
		addData(DeliveryNoteData.IS_BULK_AND_SERVICE,
				Boolean.toString(matPlantMap.get(RESULT_MAP_KEY_PKG_CD).equals(BULK_AND_SERVICE)));

		// Display TLGNS_SAP_MAT_PLANT_NORM.ZCODE1, ZCODE2 instead of text1
		if (sarea.getExpShowZcode12Flag()) {
			addData(DeliveryNoteData.EXPORT_INVOICE_SHOW_ZCODE1_2_FLAG, Boolean.TRUE.toString());
		}

		// add dtos
		if (order.getOrderItems() != null) {
			boolean vatIntegrityError = false;
			
			// set flags for layouting
			if (isCashSales) {
				addData(DeliveryNoteData.IS_CASH_SALES, Boolean.toString(isCashSales));
				addLabel(DeliveryNoteLabel.ORDER_NUMBER,
						Message.lookup(locale, "pdf.cashsales.receipt"));
			}
			else if (isExportInvoice) {
				SapBpEntity shiptoSapBp = order.getShiptoSapBp();
				SapCountryBsEntity countryBs = shiptoSapBp.getCountry();
				String shipToCountryCode = countryBs.getSapCountryCd();

				String oneTimeShipToCountryCode = order.getLnfSiteCountry();
				
				addData(DeliveryNoteData.IS_EXPORT_INVOICE, Boolean.toString(isExportInvoice));
				if (Constant.COUNTRY_CODE_IT.equals(shipToCountryCode)
						|| Constant.COUNTRY_CODE_IT.equals(oneTimeShipToCountryCode)) {
					addLabel(DeliveryNoteLabel.ORDER_NUMBER,
							Message.lookup(locale, "pdf.exportinvoice_it.title"));
				}
				else {
					addLabel(DeliveryNoteLabel.ORDER_NUMBER,
							Message.lookup(locale, "pdf.exportinvoice.title"));
				}
				
				if (order.getSarea().getExpShowIssueDatLocFlag() && sorgBs != null) {
					addData(DeliveryNoteData.EXPORT_INVOICE_ISSUE_LOCATION,
							sorgBs.getAddressCountrycity());
				}
				
				if (order.getSarea().getExpShowCtrlNoFlag()) {
					final String controllerNo = sapContract.getControllerNo();
					addData(DeliveryNoteData.EXPORT_INVOICE_CONTROLLER_NUMBER, controllerNo);
				}

				// Advance Payment Invoice Number
				// Comma separated list of distinct advanced invoice numbers of the related contract items.
				if (order.getSarea().getExpShowAdInvoiceNoFlag()) {
					final List<OrderItemEntity> orderItems = order.getOrderItems();
					final Set<String> advanceInvoiceNumberSet = new HashSet<String>();
					for (OrderItemEntity orderItem : orderItems) {
						final List<String> advanceInvoiceNumbersList = pdfGeneratorService.getAdvanceInvoiceNumbers(orderItem);
						advanceInvoiceNumberSet.addAll(advanceInvoiceNumbersList);
					}
					if (!advanceInvoiceNumberSet.isEmpty()) {
						final String advanceInvoiceNumbersText = StringUtils.join(
								advanceInvoiceNumberSet, ", ");
						addData(DeliveryNoteData.EXPORT_INVOICE_ADVANCE_PAYMENT_INVOICE_NUMBER,
								advanceInvoiceNumbersText);
					}
				}

				// Export Invoice Price type
				final Long exportinvoicePriceType = sarea.getExpMatPriceType();
				addData(DeliveryNoteData.EXPORT_INVOICE_PRICE_TYPE,
						exportinvoicePriceType.toString());

				final String companyCode = sarea.getSorg().getCompanycode().getCompanycodeCd();

				// Total Text
				if (order.getSarea().getExpShowTotalTxtFlag()) {
					final String totalTextKey = Constant.EXPORT_INVOICE_TOTAL_TEXT_KEY + "."
							+ companyCode + shipToCountryCode;
					final TextTxEntity textTxEntity = referentialDataService.findTxEntity(
							TextTxEntity.class, locale, totalTextKey);
					if (textTxEntity != null && textTxEntity.getTxt() != null) {
						final String totalText = textTxEntity.getTxt();
						addData(DeliveryNoteData.EXPORT_INVOICE_TOTAL_TEXT, totalText);
					}
				}

				// Legal Text 1
				if (order.getSarea().getExpShowLegalText1Flag()) {
					final String legalText1Key = Constant.EXPORT_INVOICE_LEGAL_TEXT_1_KEY + "."
							+ companyCode + shipToCountryCode;
					final TextTxEntity textTxEntity = referentialDataService.findTxEntity(
							TextTxEntity.class, locale, legalText1Key);
					if (textTxEntity != null && textTxEntity.getTxt() != null) {
						final String legalText1 = textTxEntity.getTxt();
						addData(DeliveryNoteData.EXPORT_INVOICE_LEGAL_TEXT_1, legalText1);
					}
				}

				// Legal Text 2
				if (order.getSarea().getExpShowLegalText2Flag()) {
					final String legalText2Key = Constant.EXPORT_INVOICE_LEGAL_TEXT_2_KEY + "."
							+ companyCode + shipToCountryCode;
					final TextTxEntity textTxEntity = referentialDataService.findTxEntity(
							TextTxEntity.class, locale, legalText2Key);
					if (textTxEntity != null && textTxEntity.getTxt() != null) {
						final String legalText2 = textTxEntity.getTxt();
						addData(DeliveryNoteData.EXPORT_INVOICE_LEGAL_TEXT_2, legalText2);
					}
				}
			}
			//otherwise remains default DeliveryNoteLabel.ORDER_NUMBER litdict value

			if (isCashSales || isExportInvoice) {
				// check cash sales integritiy
				BigDecimal vatPercentage = null;
				for (Object object : order.getOrderItems()) {
					OrderItemEntity orderItem = (OrderItemEntity) object;
					if (orderItem.getVatPercentage() == null) {
						break;
					}
					if (vatPercentage == null) {
						vatPercentage = orderItem.getVatPercentage();
					}
					if (vatPercentage.compareTo(orderItem.getVatPercentage()) != 0) {
						vatIntegrityError = true;
						break;
					}
				}
			}

			processOrderItemDTOs(order, sorgBs, isCashSales || isExportInvoice,
					contractCurrencyCode, deliveryTimeString, distributionChannel, divisionCd,
					vatIntegrityError, deliveryPlantTZ);
			
		}
	}

	/**
	 * Loops over the {@link OrderItemEntity}s of an {@link OrderEntity} and populates the
	 * orderItemDTOs field.
	 * If parameter <i>isCashSales</i> set to true additional price information is added.
	 */
	private void processOrderItemDTOs(OrderEntity order, SorgBsEntity sorgBs, boolean isCashSalesOrExportInvoice,
			String contractCurrencyCode, String deliveryTimeString, String distributionChannel,
			String divisionCd, boolean vatIntegrityError, TimeZone deliveryPlantTZ) {
		
		final InvoiceBean totalInvoiceBean = new InvoiceBean(contractCurrencyCode, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

		String wsTrailerPlateNo = null;     // trailer plate number coming from the weighing system
		String loadTrailerPlateNo = null;   // trailer plate number coming from LOGON
		String wsTruckPlateNo = null;       // truck plate number coming from the weighing system
		String loadTruckPlateNo = null;     // truck plate number coming from LOGON
		Date plantEntrance = null;
		Date plantExit = null;
		String deliverySilo = null;
		Long latestOrderItemNo = order.getOrderItems().get(0).getOrderItemNo();
		boolean onePriceHasDontPrint = false;
		boolean onePriceHasZeroOrActual = false;
		
		final boolean isExportInvoice = Boolean.valueOf(getData(DeliveryNoteData.IS_EXPORT_INVOICE));
		final boolean isCashSales = Boolean.valueOf(getData(DeliveryNoteData.IS_CASH_SALES));
		Boolean csShowContractCurrencyFlag = order.getSarea().getCsShowContractCurrencyFlag();
		for (OrderItemEntity orderItem : order.getOrderItems()) {

			LoadEntity load = orderItem.getLoad();
			
			if (load != null){
				// We need the load of the very first order item.
				if (latestOrderItemNo >= orderItem.getOrderItemNo()){
					latestOrderItemNo = orderItem.getOrderItemNo();
					
					wsTrailerPlateNo = load.getWsTrailerPlateNo();
					plantEntrance = load.getWsEnterShippingpointTime();
					plantExit = load.getWsLeaveShippingpointTime();
					deliverySilo = load.getWsSiloNo();
				}// If the wsTrailerPlate of the load of an earlier orderItem was null, then get it from a subsequent one.
				else if (wsTrailerPlateNo == null){
					wsTrailerPlateNo = load.getWsTrailerPlateNo();
				}
	
				// if the load doesn't contain a WS trailer plate, then get it from LOGON
				if (wsTrailerPlateNo == null && load.getTrailer() != null){
					loadTrailerPlateNo = load.getTrailer().getTrailerPlateNo();
				}
				
				// truck plate - only update if the earlier values were all nulls
				if (load.getTruck() != null){
					wsTruckPlateNo = wsTruckPlateNo == null ? load.getWsTruckPlateNo() : wsTruckPlateNo;
					loadTruckPlateNo = loadTruckPlateNo == null ? load.getTruck().getTruckPlateNo() : loadTruckPlateNo;
				}
			}
			
			orderItemDTOs.add(buildOrderItemDTO(orderItem, deliveryTimeString, deliveryPlantTZ));
			orderItemDTOs.add(buildAdditionalInfoDTO(orderItem));

			if (isCashSalesOrExportInvoice) {

				if (vatIntegrityError) {
					orderItemDTOs.add(new OrderItemDTO("", Message.lookup(locale, "pdf.cashsales.diffvaterror")));
				}
				else {
					// if order is a "Cash-sales" or "Export-invoice" contract type order, a cash sale DTO must be added as well
					if (isExportInvoice) {
						// this case, the material price has to be printed in EUR too
						final OrderItemDTO expInvoiceOrderItemDTO = buildMatPriceInEurForExportInvoice(orderItem);
						if (expInvoiceOrderItemDTO != null) {
							orderItemDTOs.add(expInvoiceOrderItemDTO);
						}
					}
					
					// creates price for either the Cash Sales Invoice or the Export Invoice
					OrderItemDTO priceOrderItemDTO = buildPriceInfoForInvoice(orderItem,contractCurrencyCode);
					orderItemDTOs.add(priceOrderItemDTO);
					
					if (isCashSales && csShowContractCurrencyFlag) {
						final String currencyLabel = getLabel(DeliveryNoteLabel.CURRENCY);
						final OrderItemDTO sareaCurrencyDTO = new OrderItemDTO(currencyLabel + ": " + contractCurrencyCode, "");
						orderItemDTOs.add(sareaCurrencyDTO);
					}
					//Cash sales bean (originally only used for "Cash-sales" contract type) is also used for "Export-invoice" contract type
					InvoiceBean invoiceBean = priceOrderItemDTO.getInvoiceBean();
					if (invoiceBean !=null) {
						
						if (isExportInvoice){
							
							PriceToPrint priceToPrint = whatPriceToPrint(orderItem, invoiceBean.getNetPrice());
							switch (priceToPrint) {
							case ACTUALPRICE:
								onePriceHasZeroOrActual = true;
								totalInvoiceBean.setNetPrice(totalInvoiceBean.getNetPrice().add(invoiceBean.getNetPrice()));
								totalInvoiceBean.setVatValue(totalInvoiceBean.getVatValue().add(invoiceBean.getVatValue()));
								totalInvoiceBean.setGrossPrice(totalInvoiceBean.getGrossPrice().add(invoiceBean.getGrossPrice()));
								break;
							case DONTPRINT:
								// no need to add anything
								onePriceHasDontPrint = true;
								break;
							case ZERO:
								// no need to add anything
								onePriceHasZeroOrActual = true;
								break;
							default:
								throw new IllegalArgumentException("Illegal value of enum in Export invoice price printing");
							}
						}
						else {
							totalInvoiceBean.setNetPrice(totalInvoiceBean.getNetPrice().add(invoiceBean.getNetPrice()));
							totalInvoiceBean.setVatValue(totalInvoiceBean.getVatValue().add(invoiceBean.getVatValue()));
							totalInvoiceBean.setGrossPrice(totalInvoiceBean.getGrossPrice().add(invoiceBean.getGrossPrice()));
						}
					}
				}

			}

			final List<EichbelegDTO> dtos;
			dtos = buildEichbelegDTOs(orderItem,
									  order.getDeliveryType(),
									  sorgBs,
									  distributionChannel,
									  divisionCd,
									  deliveryPlantTZ);
			eichbelegList.addAll(dtos);

		}
		
		if (wsTrailerPlateNo == null){
			wsTrailerPlateNo = loadTrailerPlateNo;
		}
		// If the delivery type is PICKUP, then the truck plate number should come from the weighing system. 
		// If that value is null, we use the one from LOGON
		if (!order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_PICKUP) || wsTruckPlateNo == null){
			wsTruckPlateNo = loadTruckPlateNo;
		}
		
		if (StringUtils.isNotEmpty(wsTruckPlateNo)){
			addData(DeliveryNoteData.TRUCK_PLATE_NR, wsTruckPlateNo);
		}
		if (StringUtils.isNotEmpty(wsTrailerPlateNo) && order.getSarea().getDnShowTrailerFlag()){
			addData(DeliveryNoteData.TRAILER_PLATE_NR, wsTrailerPlateNo);
		}
		if (plantEntrance != null && order.getSarea().getDnShowPlantCycleFlag()){
			addData(DeliveryNoteData.PLANT_ENTRY, deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(plantEntrance));
		}
		if (plantExit != null && order.getSarea().getDnShowPlantCycleFlag()){
			addData(DeliveryNoteData.PLANT_EXIT, deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(plantExit));
		}
		if (StringUtils.isNotEmpty(deliverySilo) && order.getSarea().getDnShowSiloNoFlag()){
			addData(DeliveryNoteData.DELIVERY_SILO, deliverySilo);
		}
		
		if (isCashSalesOrExportInvoice) {
			
			MaterialTotalsTextDTO totalsDTO;
			
			if (onePriceHasDontPrint && !onePriceHasZeroOrActual){
				// if all the prices are 'DONTPRINT' then we don't print the TOTAL line either
				totalsDTO = null;
			}
			else{			
				String netPrice	= getLabel(DeliveryNoteLabel.TOTAL_PRICE) + ": " 
						+ getLabel(DeliveryNoteLabel.NET_PRICE)	+ ": " 
						+ totalInvoiceBean.getNetPrice().toPlainString() + " " 
						+ totalInvoiceBean.getCurrency();
				
				String vat = getLabel(DeliveryNoteLabel.VAT) + ": " 
						+ totalInvoiceBean.getVatValue().toPlainString() + " " 
						+ totalInvoiceBean.getCurrency();
				
				String grossPrice = getLabel(DeliveryNoteLabel.GROSS_PRICE) + ": " 
						+ totalInvoiceBean.getGrossPrice().toPlainString() + " " 
						+ totalInvoiceBean.getCurrency();
				
				totalsDTO = new MaterialTotalsTextDTO(netPrice, vat, grossPrice);			
			}
			
			addObjectData(DeliveryNoteData.CASH_SALES_TOTAL_RECORD, totalsDTO);
			
			final String showTotalsAsTextString = getData(DeliveryNoteData.CASH_SALES_SHOW_TOTAL_AS_TEXT);
			if (!StringUtils.isEmpty(showTotalsAsTextString)) {
				final boolean showTotalsAsText = Boolean.valueOf(showTotalsAsTextString);  
				if (showTotalsAsText) {
					final String cashSalesTotalAsText = LangUtil.getNumberAsText(totalInvoiceBean.getGrossPrice(), locale);
					if (!StringUtils.isEmpty(cashSalesTotalAsText)) {
						addObjectData(DeliveryNoteData.CASH_SALES_TOTAL_AS_TEXT, cashSalesTotalAsText);
					}
				}
			}
		}
		
	}
	
	/**
	 * @param sareaBs
	 */
	protected void setVisibilitySettings(SareaEntity sareaBs) {
		if (!sareaBs.getDocumentDelTimeWindowFlag()) {
			addData(DeliveryNoteData.HIDE_DEL_WINDOW, "true");
		}
		if (!sareaBs.getDocumentDistanceFlag()) {
			addData(DeliveryNoteData.HIDE_DISTANCE, "true");
		}
		if (!sareaBs.getDocumentCommCarrierFlag()) {
			addData(DeliveryNoteData.HIDE_COMM_CARRIER, "true");
		}
		if (!sareaBs.getDocumentExecCarrierFlag()) {
			addData(DeliveryNoteData.HIDE_EXEC_CARRIER, "true");
		}
	}

	/**
	 * Sets the address data into the map.
	 * @param soldtoSapBp "Sold To" SAP Business Partner
	 * @param shipToSapBp "Ship To" SAP Business Partner
	 * @param billtoSapBp "Bill To" SAP Business Partner
	 * @param payerSapBp  "Payer" SAP Business Partner
	 * @param commCarrier Commercial Carrier
	 * @param execCarrier Executing Carrier
	 * @param oneTimeSoldToData if one-time shipment is used, contains the sold-to address as a list of {@link String}s
	 * @param oneTimeShiptoData if one-time shipment is used, contains the ship-to address as a list of {@link String}s
	 * @param order {@link OrderEntity} to get the Sales Area/show Ship To Contact flag. Could be null.
	 */
	protected void setAddresses(SapBpEntity soldtoSapBp, SapBpEntity shipToSapBp,
			SapBpEntity billtoSapBp, SapBpEntity payerSapBp, CarrierEntity commCarrier,
			CarrierEntity execCarrier, List<String> oneTimeSoldToData, List<String> oneTimeShiptoData, OrderEntity order) {
		
		boolean oneTimeSoldToDefined = oneTimeSoldToData != null;
		boolean oneTimeShiptoDefined = oneTimeShiptoData != null;
		
		//set addresses: client (SOLD_TO)
		String sap = "";
		String add1 = "";
		String add2 = ""; 
		String add3 = "";
		String phone = "";
		String vatCode = "";
		String pivaIa = "";
		
		if (soldtoSapBp != null) {
			sap = StringUtils.defaultString(soldtoSapBp.getSapId());
			
			if (!soldtoSapBp.getTreatAsOnetimeFlag().booleanValue()) {
				add1 = (StringUtils.defaultString(soldtoSapBp.getName1()) + " "
						+ StringUtils.defaultString(soldtoSapBp.getName2()));
				add2 = (StringUtils.defaultString(soldtoSapBp.getStreet()) + " "
						+ StringUtils.defaultString(soldtoSapBp.getHouseNumber()));
				add3 = (StringUtils.defaultString(soldtoSapBp.getCountry().getSapCountryCd()) + " "
						+ StringUtils.defaultString(soldtoSapBp.getPostalCode()) + " "
						+ StringUtils.defaultString(soldtoSapBp.getCity()));
				phone = StringUtils.defaultString(soldtoSapBp.getPhoneNumber());
				vatCode = StringUtils.defaultString(soldtoSapBp.getVatRegistrationNumber());	
			}
			else if (oneTimeSoldToDefined) {
				add1 = StringUtils.defaultString(oneTimeSoldToData.get(0));
				add2 = StringUtils.defaultString(oneTimeSoldToData.get(1));
				add3 = StringUtils.defaultString(oneTimeSoldToData.get(2));
				vatCode = StringUtils.defaultString(oneTimeSoldToData.get(3));
			}
		}
		
		addData(DeliveryNoteData.SOLDTO_SAP, sap);
		addData(DeliveryNoteData.SOLDTO_ADDRESS1, add1);
		addData(DeliveryNoteData.SOLDTO_ADDRESS2, add2);
		addData(DeliveryNoteData.SOLDTO_ADDRESS3, add3);
		addData(DeliveryNoteData.SOLDTO_PHONE, phone);
		addData(DeliveryNoteData.SOLDTO_VATREG, vatCode);
		
		sap = ""; add1 = ""; add2 = "";  add3 = ""; phone = ""; vatCode = ""; pivaIa = "";
		
		//set addresses: recipient (SHIP_TO)  
		if ((shipToSapBp != null)) {			
			sap = StringUtils.defaultString(shipToSapBp.getSapId());
			if (!shipToSapBp.getTreatAsOnetimeFlag()) {				
				add1 = StringUtils.defaultString(shipToSapBp.getName1()) + " " +
						StringUtils.defaultString(shipToSapBp.getName2());
				add2 = StringUtils.defaultString(shipToSapBp.getStreet()) + " "
						+ StringUtils.defaultString(shipToSapBp.getHouseNumber());
				add3 = StringUtils.defaultString(shipToSapBp.getCountry().getSapCountryCd()) + " "
						+ StringUtils.defaultString(shipToSapBp.getPostalCode()) + " "
						+ StringUtils.defaultString(shipToSapBp.getCity());

				vatCode = getShiptoContactString(shipToSapBp, order);
				
			} 
			// one-time ship-to
			else if (oneTimeShiptoDefined) {
				Iterator<String> it = oneTimeShiptoData.iterator();
				add1 = StringUtils.defaultString(it.hasNext() ? it.next() : "");
				add2 = StringUtils.defaultString(it.hasNext() ? it.next() : "");
				add3 = StringUtils.defaultString(it.hasNext() ? it.next() : "");
				vatCode = StringUtils.defaultString(it.hasNext() ? it.next() : "");
			}
		}
		
		addData(DeliveryNoteData.SHIPTO_SAP, sap);
		addData(DeliveryNoteData.SHIPTO_ADDRESS1, add1);
		addData(DeliveryNoteData.SHIPTO_ADDRESS2, add2);
		addData(DeliveryNoteData.SHIPTO_ADDRESS3, add3);
		addData(DeliveryNoteData.SHIPTO_VAT, vatCode);
		
		sap = ""; add1 = ""; add2 = "";  add3 = ""; phone = ""; vatCode = ""; pivaIa = "";
		
		//set addresses: comm. carrier 
		if ((commCarrier != null) && (commCarrier.getSapVendor() != null)) {
			sap = StringUtils.defaultString(commCarrier.getSapVendor().getSapId());
			add1 = StringUtils.defaultString(commCarrier.getSapVendor().getName1()) + " "
					+ StringUtils.defaultString(commCarrier.getSapVendor().getName2());
			add2 = StringUtils.defaultString(commCarrier.getSapVendor().getStreet()) + " "
					+ StringUtils.defaultString(commCarrier.getSapVendor().getHouseNumber());
			add3 = (commCarrier.getSapVendor().getCountry() != null ? commCarrier.getSapVendor().getCountry().getSapCountryCd() + " " : "")
					+ StringUtils.defaultString(commCarrier.getSapVendor().getPostalCode()) + " "
					+ StringUtils.defaultString(commCarrier.getSapVendor().getCity());
			phone = StringUtils.defaultString(commCarrier.getSapVendor().getPhoneNumber());
			pivaIa = StringUtils.defaultString(commCarrier.getSapVendor().getVatRegistrationNumber()) + " " +
					StringUtils.defaultString(commCarrier.getSapVendor().getTaxNumber3());
		}
		
		addData(DeliveryNoteData.COMM_CARRIER_SAP, sap);
		addData(DeliveryNoteData.COMM_CARRIER_ADDRESS1, add1);
		addData(DeliveryNoteData.COMM_CARRIER_ADDRESS2, add2);
		addData(DeliveryNoteData.COMM_CARRIER_ADDRESS3, add3);
		addData(DeliveryNoteData.COMM_CARRIER_PHONE, phone);
		addData(DeliveryNoteData.COMM_CARRIER_PIVA_IA, pivaIa);
		
		
		sap = ""; add1 = ""; add2 = "";  add3 = ""; phone = ""; vatCode = ""; pivaIa = "";
		
		//set addresses: exec. carrier 
		if ((execCarrier != null) && (execCarrier.getSapVendor() != null)) {
			sap = StringUtils.defaultString(execCarrier.getSapVendor().getSapId());
			add1 = StringUtils.defaultString(execCarrier.getSapVendor().getName1()) + " "
					+ StringUtils.defaultString(execCarrier.getSapVendor().getName2());
			add2 = StringUtils.defaultString(execCarrier.getSapVendor().getStreet()) + " "
					+ StringUtils.defaultString(execCarrier.getSapVendor().getHouseNumber());
			add3 = (execCarrier.getSapVendor().getCountry() != null ? execCarrier.getSapVendor().getCountry().getSapCountryCd() + " " : "")
					+ StringUtils.defaultString(execCarrier.getSapVendor().getPostalCode()) + " "
					+ StringUtils.defaultString(execCarrier.getSapVendor().getCity());
			phone = StringUtils.defaultString(execCarrier.getSapVendor().getPhoneNumber());
			pivaIa = StringUtils.defaultString(execCarrier.getSapVendor().getVatRegistrationNumber()) + " " +
					StringUtils.defaultString(execCarrier.getSapVendor().getTaxNumber3());
		}
		
		addData(DeliveryNoteData.EXEC_CARRIER_SAP, sap);
		addData(DeliveryNoteData.EXEC_CARRIER_ADDRESS1, add1);
		addData(DeliveryNoteData.EXEC_CARRIER_ADDRESS2, add2);
		addData(DeliveryNoteData.EXEC_CARRIER_ADDRESS3, add3);
		addData(DeliveryNoteData.EXEC_CARRIER_PHONE, phone);
		addData(DeliveryNoteData.EXEC_CARRIER_PIVA_IA, pivaIa);
		
		sap = ""; add1 = ""; add2 = "";  add3 = ""; phone = ""; vatCode = ""; pivaIa = "";
		
		//set addresses: BILL-TO 
		if (billtoSapBp != null) {
			sap = StringUtils.defaultString(billtoSapBp.getSapId());
			add1 = StringUtils.defaultString(billtoSapBp.getName1()) + " " + StringUtils.defaultString(billtoSapBp.getName2());
			add2 = StringUtils.defaultString(billtoSapBp.getStreet()) + " " + StringUtils.defaultString(billtoSapBp.getHouseNumber());
			add3 = StringUtils.defaultString(billtoSapBp.getCountry().getSapCountryCd()) + " " + StringUtils.defaultString(billtoSapBp.getPostalCode()) + " " + StringUtils.defaultString(billtoSapBp.getCity());
		}
		
		if (soldtoSapBp != null && soldtoSapBp.getTreatAsOnetimeFlag()) {
			if (billtoSapBp != null && soldtoSapBp.getSapBpId().equals(billtoSapBp.getSapBpId()) && oneTimeSoldToDefined) {
				sap = StringUtils.defaultString(soldtoSapBp.getSapId());
				Iterator<String> it = oneTimeSoldToData.iterator();
				add1 = StringUtils.defaultString(it.hasNext() ? it.next() : "");
				add2 = StringUtils.defaultString(it.hasNext() ? it.next() : "");
				add3 = StringUtils.defaultString(it.hasNext() ? it.next() : "");
			}
		}
		
		addData(DeliveryNoteData.BILLTO_SAP, sap);
		addData(DeliveryNoteData.BILLTO_ADDRESS1, add1);
		addData(DeliveryNoteData.BILLTO_ADDRESS2, add2);
		addData(DeliveryNoteData.BILLTO_ADDRESS3, add3);
		
		sap = ""; add1 = ""; add2 = "";  add3 = ""; phone = ""; vatCode = ""; pivaIa = "";
		
		//set addresses: PAYER
		if (payerSapBp != null) {
			sap = StringUtils.defaultString(payerSapBp.getSapId());
			add1 = StringUtils.defaultString(payerSapBp.getName1()) + " " +
					StringUtils.defaultString(payerSapBp.getName2());
			add2 = StringUtils.defaultString(payerSapBp.getStreet()) + " " + StringUtils.defaultString(payerSapBp.getHouseNumber());
			add3 = StringUtils.defaultString(payerSapBp.getCountry().getSapCountryCd()) + " " + StringUtils.defaultString(payerSapBp.getPostalCode()) + " " + StringUtils.defaultString(payerSapBp.getCity());
		}
		
		if (soldtoSapBp != null && soldtoSapBp.getTreatAsOnetimeFlag()) {
			if (payerSapBp != null && soldtoSapBp.getSapBpId().equals(payerSapBp.getSapBpId()) && oneTimeSoldToDefined) {
				sap = StringUtils.defaultString(soldtoSapBp.getSapId());
				add1 = StringUtils.defaultString(oneTimeSoldToData.get(0));
				add2 = StringUtils.defaultString(oneTimeSoldToData.get(0));
				add3 = StringUtils.defaultString(oneTimeSoldToData.get(0));
			}
		}
		
		addData(DeliveryNoteData.PAYER_SAP, sap);
		addData(DeliveryNoteData.PAYER_ADDRESS1, add1);
		addData(DeliveryNoteData.PAYER_ADDRESS2, add2);
		addData(DeliveryNoteData.PAYER_ADDRESS3, add3);
		
	}

	/**
	 * Returns the ship-to contact String or empty {@link String} (if the ship-to contact is not available)
	 * @param shipToSapBp the {@link SapBpEntity}
	 * @param order the {@link OrderEntity}
	 * @return  the ship-to contact String or empty {@link String}
	 */
	private String getShiptoContactString(SapBpEntity shipToSapBp, OrderEntity order) {
		
		String result = "";
		
		if (order != null &&
			order.getSarea() != null &&	
			order.getSarea().getDnShowShiptoContactFlag() != null) {

			// set flag, if data available
			boolean shipToContact = order.getSarea().getDnShowShiptoContactFlag();
		
		    // get the localized version of "Contact:"
		    String contactLabel = getLabel(DeliveryNoteLabel.CONTACT_SHIPTO);
		    
		    // contact name could be null
		    String contactName = shipToSapBp.getContactName();
		    
		    // contact phone could be null
		    String contactPhone = shipToSapBp.getContactPhone();
		  
		    // The line is only to be printed if the contact name is filled and if 
			// the sales area is configured accordingly (Show ship-to contact information is checked)
		    if (shipToContact && contactName != null) {
		    	
		    	StringBuilder sb = new StringBuilder();
		    	// "Contact:"
		    	sb.append(contactLabel);
		    	sb.append(" ");
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
		    	
		    	// vatCode is the fourth line in the "ship to" address
		    	result = sb.toString();
		    }
		  
		}
		return result;
	}

	protected String formatText(String textCd, Locale locale, Object[] messageArguments, DeliveryTypeBsEntity delType, SorgBsEntity sorg, 
			String distChannel, String divisionCd) {
		if (delType == null || sorg == null || distChannel == null || divisionCd == null) {
			return pdfGeneratorService.formatText(textCd, locale, messageArguments);
		}
		return pdfGeneratorService.formatTextWithDelNoteSareaLookup(textCd, locale, delType, sorg, 
				distChannel, divisionCd, messageArguments);
	}

	private Map<String, ?> getMatPlant(Locale loc, OrderEntity order) {
		// True if return data exists
		Boolean returnDataFlag = orderHasReturn(order);
		// Number of returned pallets
		BigDecimal numberOfReturnedPallets = BigDecimal.ZERO;
		// Wagon order number of the bimodal truck delivery
		String bimodalWagonOrderNo = null;
		// Wagon number of the bimodal truck delivery
		String bimodalWagonNo = null;
		// Total quantity of wagon of the bimodal truck delivery
		String bimodalWagonDelQty = "";

		Long orderPkg = null;

		MatPlantEntity matPlantEntity = null;
		String plantName = "";
		String containerNrs = "";

		PlantBsEntity supplyPlant = null;
		if (order.getShippingpoint() != null) {
			supplyPlant = order.getShippingpoint().getPlant();
		}

		if (supplyPlant != null) {
			plantName = supplyPlant.getPlantName();
		}

		// loop through all order-items, and any of "unknown"(not bulk or
		// bag) types should simply be printed as text
		for (OrderItemEntity orderItem : (List<OrderItemEntity>) order.getOrderItems()) {

			// detect package type of item
			Long packageCdOfOrderItem = getPackageCdOfOrderItem(orderItem);
			orderPkg = decidePackageTypeOfWholeOrder(orderPkg, packageCdOfOrderItem);
			
			// Bulk order item
			if (BULK_PWDR_PKG_CD.equals(packageCdOfOrderItem)
					|| BULK_DMPD_PKG_CD.equals(packageCdOfOrderItem)) {
				if (matPlantEntity == null) {
					matPlantEntity = getLogoRules(orderItem.getSapMaterial().getSapMaterialId(),
							orderItem.getProductionPlant().getPlantCd());
				}
			}
			// Bag
			else if (BAG_PKG_CD.equals(packageCdOfOrderItem)) {
				// check material packaging type for determine if it's a pallet
				boolean addReturnedPalets = LogonPackageHandlingUtil.isPallet(orderItem)
						&& orderItem.getReturnedQuantity() != null;
				if (Boolean.valueOf(addReturnedPalets)) {
					numberOfReturnedPallets = numberOfReturnedPallets.add(orderItem.getReturnedQuantity());
				}
			}
			
			// bimodal truck case
			if (orderItem.getBimodalRailOrderItem() != null) {
				
				OrderEntity biOrder = orderItem.getBimodalRailOrderItem().getOrder();
				Long orderNo = biOrder.getOrderNumber();
				if (orderNo != null) {
					bimodalWagonOrderNo = orderNo.toString();
				}

				//total quantity of wagon. There is only one order item of wagon order
				OrderItemEntity oi = (OrderItemEntity) biOrder.getOrderItems().get(0);
				BigDecimal deliveredQuantity = oi.getDeliveredQuantity();

				if (deliveredQuantity != null) {
					UomBsEntity deliveredUom = oi.getDeliveredUom();
					String delUom = getUomName(loc, deliveredUom);
					bimodalWagonDelQty = deliveredQuantity.toPlainString() + " " + delUom;
				}
				
				//select wagon no of bimodal case
				final ReadWriteDao<AssignmentPartEntity> assDao = daoProvider.getAssignmentPartEntityDao();
				final Filter<AssignmentPartEntity> filter = assDao.createFilter();
				filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER,
						orderItem.getBimodalRailOrderItem().getOrder()));
				filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "."
						+ AssignmentEntity.ATTR_ACTIVE_FLAG, true));
				final AssignmentPartEntity assPart = assDao.findSingle(filter);

				if ((assPart != null) && (assPart.getAssignment() != null)
						&& (assPart.getAssignment().getWagon() != null)) {
					bimodalWagonNo = assPart.getAssignment().getWagon().getWagonNo();
				}
			}
			//container
			ContainerEntity container = orderItem.getDeliveredLoadContainer() != null ? orderItem.getDeliveredLoadContainer()
					: (orderItem.getLoad() != null ? orderItem.getLoad().getContainer() : null);
			if (container != null) {
				containerNrs += (StringUtils.isNotEmpty(containerNrs) ? ", " : "")
						+ container.getContainerNo();
			}
		}

		final Map<String, Object> resultData = new HashMap<String, Object>();
		resultData.put(MatPlantEntity.class.getName(), matPlantEntity);
		resultData.put(RESULT_MAP_KEY_PLANT_NAME, plantName);
		// TODO need for header construction
		resultData.put(RESULT_MAP_KEY_RETURN_DATA_FLAG, returnDataFlag);
		resultData.put(RESULT_MAP_KEY_BIMODAL, bimodalWagonOrderNo);
		resultData.put(RESULT_MAP_KEY_BIMODAL_WAGONNO, bimodalWagonNo);
		resultData.put(RESULT_MAP_KEY_CONTAINER_NO, containerNrs);
		resultData.put(RESULT_MAP_KEY_BIMODAL_DEL_QTY, bimodalWagonDelQty);
		// TODO need for header construction
		resultData.put(RESULT_MAP_KEY_PKG_CD, orderPkg);
		// returned pallets
		resultData.put(RESULT_MAP_KEY_RETURNED_PALLETS, numberOfReturnedPallets);

		return resultData;
	}

	/**
	 * This method sets the material/package type of the whole order depending on 
	 * packageCd of the order item and the package type of the whole order
	 * <br><br>
	 * It can set bag, bulk, service and mixed values.
	 * 
	 * @param orderPkg
	 * @param packageCdOfOrderItem
	 * @return the updated orderPkg
	 */
	private Long decidePackageTypeOfWholeOrder(Long orderPkg, Long packageCdOfOrderItem) {
		if (packageCdOfOrderItem != null) {
												
			// Bulk powder
			if (BULK_PWDR_PKG_CD.equals(packageCdOfOrderItem) || BULK_DMPD_PKG_CD.equals(packageCdOfOrderItem)) { 
										
				if (SERVICE_PKG_CD.equals(orderPkg)){ // a former order item was service already -> mixed order
					orderPkg = BULK_AND_SERVICE;
				} else if (BULK_DMPD_PKG_CD.equals(packageCdOfOrderItem)){
					orderPkg = BULK_DMPD_PKG_CD;
				} else{
					orderPkg = BULK_PWDR_PKG_CD;
				}
			} 
			// bag
			else if (BAG_PKG_CD.equals(packageCdOfOrderItem)) { 
				
				if (SERVICE_PKG_CD.equals(orderPkg)){ // a former order item was service already -> mixed order
					orderPkg = BAG_AND_SERVICE;
				} else{
					orderPkg = BAG_PKG_CD;
				}
			}
			// service
			else if (SERVICE_PKG_CD.equals(packageCdOfOrderItem)){
				if (BULK_DMPD_PKG_CD.equals(orderPkg) || BULK_PWDR_PKG_CD.equals(orderPkg)){ // a former order item was bulk already -> mixed order
					orderPkg = BULK_AND_SERVICE;
				} else if (BAG_PKG_CD.equals(orderPkg)){ // a former order item was bag already -> mixed order
					orderPkg = BAG_AND_SERVICE;
				} else{
					orderPkg = SERVICE_PKG_CD;
				}
			}
			// something else (e.g. unknown)
			else {
				orderPkg = packageCdOfOrderItem;
			}
		}
		return orderPkg;
	}
	
	protected String getDeliveryTimeString(OrderEntity order) {
		// set delivery time
		String deliveryTime = null;
		Filter<AssignmentPartEntity> filter = new FilterImpl<AssignmentPartEntity>();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, order.getOrderId()));
		ReadWriteDao<AssignmentPartEntity> asspDao = daoProvider.getAssignmentPartEntityDao();
		AssignmentPartEntity assp = asspDao.findSingle(filter);
		
		if (assp != null ) {
			TimeZone shipToTZ = getTimeZoneForShipTo(order.getShiptoSapBp());

			if (order.getNoPdaOrRailEventsFlag() || assp.getRealStart() == null ) {
				// case non-PDA OR the real start is not yet known:  add symbol "~" and round to 10 minutes. In case calculated delivery time is before given time window, use start time of customer time window. 
		
				//start with the planned value
				Date deliveryTimeUncertain = assp.getPlannedStart();
				
				if ( assp.getRealStart() != null ) {
					//if it is definite use that
					deliveryTimeUncertain = assp.getRealStart();
				}
				else {
					//else consider the IS values of the assignment, have to show the value signed on dispo screen for truck, and planned delivery for rail
					try {								
						deliveryTimeUncertain = SchedulingUtils.getCalculatedTimeOfDelivery(order, orderService.findAssignmentById(assp.getAssignment().getAssignmentId()), dispoService, configService, truckAdminService);
					}
					catch (Exception e) {
						LOG.error("Could not calculate delivery time  [order: " + order.getOrderId() + ", assignment: " + assp.getAssignment().getAssignmentId() + "]");
					}
				}
				
				if (deliveryTimeUncertain != null) {
				
					if ((order.getDeliveryFromDat() != null) && (deliveryTimeUncertain.before(order.getDeliveryFromDat()))) {
						deliveryTimeUncertain = order.getDeliveryFromDat();
					}
					
					Calendar cal = shipToTZ.asCalendar(deliveryTimeUncertain);

					if (!order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_PICKUP)) {
						//round to 10 minutes
						int minute = cal.get(Calendar.MINUTE);
						BigDecimal minBD = new BigDecimal(minute);
						minBD = minBD.multiply(new BigDecimal("0.1"));
						minBD = minBD.setScale(0, BigDecimal.ROUND_HALF_UP);
						minBD = minBD.multiply(new BigDecimal("10"));
						cal.set(Calendar.MINUTE, minBD.intValue());
					}

					deliveryTime = "~" + shipToTZ.fmtAsDayMonthYearHourMinuteWithOffset(cal.getTime());
				}
			}
			else {
				// case PDA: entry time geofence customer  (don't print if not reached)
				if (assp.getRealStart() != null) {
					deliveryTime = shipToTZ.fmtAsDayMonthYearHourMinuteWithOffset(assp.getRealStart());
				}
			}
		}
		return deliveryTime;
	}

	/**
	 * 
	 * @param orderItem
	 * @param delType
	 * @param sorgBs
	 * @param distributionChannel
	 * @param divisionCd
	 * @return
	 */
	private List<EichbelegDTO> buildEichbelegDTOs(
			OrderItemEntity orderItem,
			DeliveryTypeBsEntity delType,
			SorgBsEntity sorgBs, String distributionChannel,
			String divisionCd,
			TimeZone deliveryPlantTZ) {
		/*
		 * NOTE: "Eichbeleg", or calibration record, is a legal document received from
		 * the weighing system, that proves that the weighing was done with a calibrated
		 * balance.
		 */

		final List<EichbelegDTO> dtos = new LinkedList<EichbelegDTO>();
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
							formatText("pdf.delivnote.eichbeleg.tare", locale,
									new Object[] {balance1, wsEichbeleg1, wsEichbeleg1Time,
									wsLoadingpoint1},
									delType, sorgBs, distributionChannel, divisionCd);

					final String grossEichbelegTxt =
							formatText("pdf.delivnote.eichbeleg.gross", locale,
									new Object[] {balance2, wsEichbeleg2, wsEichbeleg2Time,
									wsLoadingpoint2},
									delType, sorgBs, distributionChannel, divisionCd);

					dtos.add(new EichbelegDTO(tareEichbelegTxt));
					dtos.add(new EichbelegDTO(grossEichbelegTxt));
				}
			}
			
		}
		
		return dtos;
	}
	
	protected OrderItemDTO buildOrderItemDTO(OrderItemEntity orderItem, String deliveryDate, TimeZone deliveryPlantTZ) {
		OrderEntity order = orderItem.getOrder();
		final boolean returnOrder = LogonUtil.isReturnPalletOnly(order.getDeliveryType().getDeliveryTypeCd());

		// fields used to build the DTO
		String orderNr = order.getOrderNumber().toString();
		String materialName = "";
		String materialDesc = "";
		String sapNr = "";
		String vehicle = "";
		String loadDate = null;
		String materialMixDesign = "";
		String materialMixDesignPlantName = "";

		// True if return data exists
		Boolean returnDataFlag = false;
		MatPlantEntity matPlantEntity = null;
		Long pkgCd = orderItem.getSapMaterial().getMaterialPackage().getLogonPackageType().getPackageTypeCd();

		materialMixDesign = buildMaterialMixDesignInfo(orderItem);
		materialMixDesignPlantName = getMaterialMixDesignProdPlantName(orderItem);

		// set material name
		String matDesc = null;
		if (orderItem.getSapMaterial() != null && orderItem.getProductionPlant() != null) {
			matDesc = getBrandNameForMaterialPlant(orderItem.getSapMaterial().getSapMaterialId(),
					orderItem.getProductionPlant().getPlantCd());
		}
		if (matDesc == null) {
			matDesc = getMaterialDesc(orderItem.getSapMaterial().getSapMaterialId(), locale);
		}

		// logos will be printed just for bulk orders but we need to selcet for CPD number.
		matPlantEntity = getLogoRules(orderItem.getSapMaterial().getSapMaterialId(),
				orderItem.getProductionPlant().getPlantCd());

		// set material hierarchy
		String hierarchyDesc = "";
		String cpdNumber = "";

		if (matPlantEntity != null) {
			hierarchyDesc = (StringUtils.isEmpty(matPlantEntity.getNormDesc())) ? INITIAL_DATA_STR
					: matPlantEntity.getNormDesc();
			cpdNumber = StringUtils.defaultString(matPlantEntity.getCpdNumber());
		}

		// map to keep one material row
		materialName = transliterate(StringUtils.isEmpty(matDesc) ? INITIAL_DATA_STR : matDesc);
		materialDesc = transliterate(hierarchyDesc + " " + cpdNumber);

		// set return data flag
		returnDataFlag = orderItem.getReturnedQuantity() != null;

		// sap nr.
		sapNr = getSapNumber(orderItem);

		// set vehicle and pda info
		AssignmentEntity assignment = getAssignment(order.getOrderId());
		if (assignment != null) {
			if (assignment.getTruck() != null) {
				if (orderItem.getLoad() != null)
					vehicle = orderItem.getLoad().getWsTruckPlateNo();
				if (vehicle == null)
					vehicle = assignment.getTruck().getTruckPlateNo();
			}
			else if (assignment.getWagon() != null) {
				vehicle = assignment.getWagon().getWagonNo();
			}
		}

		// set load date
		LoadEntity load = orderItem.getLoad();
		if (load != null && load.getLoadingTs() != null) {
			loadDate = deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(load.getLoadingTs());
		}

		OrderItemDTO dto;

		// Bulk
		if (BULK_PWDR_PKG_CD.equals(pkgCd) || BULK_DMPD_PKG_CD.equals(pkgCd)) {
			BigDecimal gross = null;
			BigDecimal tare = null;
			BigDecimal net = null;
			BigDecimal ret = null;
			String netQuantityUom = "";
			
			// get tare, net and gross (tare+net) data
			// Iterate over order's items. If order status is < unloaded,
			// get tare and net weight from item's load.
			// If unloaded, get net weight from item (delivered quantity)
			
			if (load != null) {

				netQuantityUom = getUomName(locale, load.getNetQuantityUom());

				tare = load.getTareWeightT();

				if (tare != null) {
					tare = tare.setScale(ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
				}

				gross = load.getGrossWeightT();

				if (gross != null) {
					gross = gross.setScale(ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
				}

				if (order.getActualDeliveryDat() == null) {
					// not yet delivered
					net = load.getNetQuantity();
				}
				else {
					net = orderItem.getDeliveredQuantity();
				}

				if (net != null) {
					net = net.setScale(ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
				}

				// return data
				if (returnDataFlag) {
					// FIXME 21-Nov-2012/romek: Make sure UoM is correct!
					ret = orderItem.getReturnedQuantity();
					ret = ret.setScale(ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
				}

			}
			dto = new OrderItemDTO(orderNr, sapNr, materialName, materialDesc, vehicle, loadDate,
					deliveryDate, net, gross, tare, ret, netQuantityUom, materialMixDesign,
					materialMixDesignPlantName);
		}
		// bag
		else if (BAG_PKG_CD.equals(pkgCd)) {
			BigDecimal ordered = null;
			String orderedUom = "";
			BigDecimal delivered = null;
			String deliveredUom = "";
			BigDecimal netBag = null;
			String netUom = "";
			
			// ordered data string (ordered quantity in ordered UoM)
			if (orderItem.getOrderedUom() != null && orderItem.getOrderedQuantity() != null) {
				orderedUom = getUomName(locale, orderItem.getOrderedUom());
				ordered = orderItem.getOrderedQuantity();
			}
			
			UomBsEntity dUom = orderItem.getDeliveredUom();
			UomBsEntity netQuantityUom = load != null ? load.getNetQuantityUom() : null;
			BigDecimal netQuantity = load != null ? load.getNetQuantity() : null;

			boolean isDeliveredFieldsSet = (orderItem.getDeliveredQuantity() != null)
					&& (dUom != null);
			boolean isLoadedFieldsSet = (netQuantity != null && netQuantityUom != null);
			
			if (isDeliveredFieldsSet || isLoadedFieldsSet) {
				// delivered quantity
				BigDecimal quantity = null;
				UomBsEntity uom = null;
				
				if (isDeliveredFieldsSet) {
					//if the delivered is known, than use that one
					quantity = orderItem.getDeliveredQuantity();
					uom = dUom;
				}
				else {
					//else try to use the loaded quantity if known
					quantity = orderItem.getLoadedQuantity();
					uom = orderItem.getLoadedUom();
				}
				
				if (quantity != null && uom != null && orderItem.getSapMaterial() != null) {
					UomBsEntity orderItemOrderedUom = orderItem.getOrderedUom();
					if (orderItemOrderedUom != null
							&& uom.getUomCd().equals(orderItemOrderedUom.getUomCd())) {
						// delivered quantity is already in the ordered UoM (preffered in bag case)
						delivered = quantity.stripTrailingZeros();
						deliveredUom = getUomName(locale, uom);
					}
					
					BigDecimal quantityInBase = UOMConverter.convertAltUomToMaterialBaseUom(uom,
							quantity, orderItem);
					UomBsEntity baseUom = orderItem.getSapMaterial().getBaseUom();
					if (quantityInBase != null) {
						// net quantity in bag case should be displayed in the materials base UoM if possible
						netBag = quantityInBase.stripTrailingZeros();
						netUom = getUomName(locale, baseUom);

						// delivered data string is still the initial one; convert delivered quantity from base UoM to ordered UoM if possible
						if (delivered == null) {
							Long orderItemOrderedUomNumerator = orderItem.getOrderedUomNumerator();
							Long orderItemOrderedUomDenominator = orderItem.getOrderedUomDenominator();
							if (orderItemOrderedUom != null && orderItemOrderedUomNumerator != null
									&& orderItemOrderedUomDenominator != null) {
								BigDecimal orderedUomNum = new BigDecimal(
										orderItemOrderedUomNumerator);
								BigDecimal orderedUomDen = new BigDecimal(
										orderItemOrderedUomDenominator);
								BigDecimal quantityInOrderedUom = quantityInBase.multiply(
										orderedUomDen).divide(orderedUomNum, ROUNDING_SCALE,
										BigDecimal.ROUND_HALF_UP);
								delivered = quantityInOrderedUom.stripTrailingZeros();
								deliveredUom = getUomName(locale, orderItemOrderedUom);
							}
						}
					}
					else {
						BigDecimal quantityInBaseUom = UOMConverter.convertUomToAnotherUomUsingCommonDimension(
								uom, baseUom, quantity, false);
						if (quantityInBaseUom != null) {
							netBag = quantityInBaseUom.stripTrailingZeros();
							netUom = getUomName(locale, baseUom);
						}

						BigDecimal quantityInOrderedUom = UOMConverter.convertUomToAnotherUomUsingCommonDimension(
								uom, orderItemOrderedUom, quantity, false);
						if (quantityInOrderedUom != null) {
							delivered = quantityInOrderedUom.stripTrailingZeros();
							deliveredUom = getUomName(locale, orderItemOrderedUom);
						}
						
						if (quantityInOrderedUom == null && quantityInBaseUom != null
								&& orderItemOrderedUom != null) {
							// use the ordered numerator/denominator for conversion back to ordered UoM if possible
							if (orderItemOrderedUom.getFuzzyQuantityFlag() != null
									&& !orderItemOrderedUom.getFuzzyQuantityFlag().booleanValue()) {
								Long orderItemOrderedUomNumerator = orderItem.getOrderedUomNumerator();
								Long orderItemOrderedUomDenominator = orderItem.getOrderedUomDenominator();
								if (orderItemOrderedUomNumerator != null
										&& orderItemOrderedUomDenominator != null) {
									BigDecimal orderedUomNum = new BigDecimal(orderItemOrderedUomNumerator);
									BigDecimal orderedUomDen = new BigDecimal(orderItemOrderedUomDenominator);
									quantityInOrderedUom = quantityInBaseUom.multiply(orderedUomDen).divide(
											orderedUomNum, ROUNDING_SCALE, BigDecimal.ROUND_HALF_UP);
									delivered = quantityInOrderedUom.stripTrailingZeros();
									deliveredUom = getUomName(locale, orderItemOrderedUom);
								}
							}
						}
					}
				}
			}

			// display quantities with negative sign in case of return orders (e.g. return pallet orders) if they are not service items, see LOGON-6761 / LRQ 018.2
			if (returnOrder && !LogonPackageHandlingUtil.isService(orderItem)) {
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

			dto = new OrderItemDTO(orderNr, sapNr, materialName, materialDesc, vehicle, loadDate,
					deliveryDate, ordered, delivered, netBag, orderedUom, deliveredUom, netUom,
					materialMixDesign, materialMixDesignPlantName);
		}
		else if (SERVICE_PKG_CD.equals(pkgCd)) {
			BigDecimal net = null;
			String uomString = null;
			if (orderItem.getOrderedQuantity() != null) {
				SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
				UomBsEntity uom = orderItem.getOrderedUom();
				BigDecimal quantity = orderItem.getOrderedQuantity();
				BigDecimal baseUomQ = null;
				if (uom != null && sapMaterial.getBaseUom() != null
						&& uom.getUomCd().equals(sapMaterial.getBaseUom().getUomCd())) {
					//check here if they are equal because for services it is acceptable to be fuzzy at this point too, even because ordered uom is used which is legally fuzzy
					//the rail return/repair empty wagon order items are service materials, seems to be a specification gap that their base uom is TO
					baseUomQ = quantity;
				}
				else {
					baseUomQ = UOMConverter.convertUomToAnotherUomUsingCommonDimension(uom,
							sapMaterial.getBaseUom(), quantity, false);
				}
				if (baseUomQ != null) {
					net = baseUomQ;
					uomString = sapMaterial.getBaseUom().getUomCd();
				}
				else {
					LOG.error(String.format(
							"Could not convert quantity to base uom. Order item id=%d, order number=%d, order id=%d, Sap material no=%s, Uom=%s, Quantity=%s, Base uom=%s",
							orderItem.getOrderItemId(),
							order.getOrderNumber(),
							order.getOrderId(),
							(sapMaterial != null) ? sapMaterial.getSapMaterialNo() : "null",
							(uom != null) ? uom.getUomCd() : "null",
							String.valueOf(quantity),
							((sapMaterial != null) && (sapMaterial.getBaseUom() != null)) ? sapMaterial.getBaseUom().getUomCd()
									: "null"));
					net = quantity;
					uomString = uom.getUomCd();
				}
			}
			
			dto = new OrderItemDTO(orderNr, sapNr, materialName, materialDesc, vehicle, loadDate,
					deliveryDate, net, uomString, materialMixDesign, materialMixDesignPlantName);
		}
		//all other types, which might not be handled yet by logon				
		else {
			BigDecimal delivered = null;
			String deliveredUom = null;
			if (orderItem.getDeliveredQuantity() != null) {
				deliveredUom = getUomName(locale, orderItem.getDeliveredUom());
				delivered = orderItem.getDeliveredQuantity();
			}
			dto = new OrderItemDTO(orderNr, sapNr, materialName, materialDesc, vehicle, loadDate,
					deliveryDate, delivered, deliveredUom, pkgCd, materialMixDesign,
					materialMixDesignPlantName);
		}

		return dto;
	}
	

	/**
	 * @param orderItem
	 * @return
	 */
	private OrderItemDTO buildAdditionalInfoDTO(OrderItemEntity orderItem) {
		final String sapMatNumber = getSapNumber(orderItem);
		final LoadEntity load = orderItem.getLoad();

		final String productionPlantName = orderItem.getProductionPlant().getPlantName();

		final StringBuilder prodPlantMixDesign = new StringBuilder();
		prodPlantMixDesign.append(transliterate(productionPlantName));
		prodPlantMixDesign.append(' ');

		// TODO 2013-12-03/tito: is this an either-or situation? that is, if the
		// mix design is set on the LoadEntity, is it null on the OrderItem? If not
		// this code can potentially append the mix design information twice.
		final FireqMaterialMixdesignEntity mixDesign;
		if ((load != null) && (load.getLoadingTs() != null)) {
			mixDesign = load.getMixDesign();
			if (mixDesign != null) {
				prodPlantMixDesign.append(mixDesign.getMixDesignCd());
				prodPlantMixDesign.append(" (");
				prodPlantMixDesign.append(mixDesign.getAggRmxCode());
				prodPlantMixDesign.append(')');
				
			}
		}
		else {
			mixDesign = orderItem.getMixDesign();
		}
		if (mixDesign != null) {
			prodPlantMixDesign.append(mixDesign.getMixDesignCd());
			prodPlantMixDesign.append(" (");
			prodPlantMixDesign.append(mixDesign.getAggRmxCode());
			prodPlantMixDesign.append(')');
			
		}
		return new OrderItemDTO(sapMatNumber, prodPlantMixDesign.toString());
		
	}

	private String buildMaterialMixDesignInfo(OrderItemEntity orderItem) {
		StringBuilder buf = new StringBuilder();

		FireqMaterialMixdesignEntity mixDesignEntity = getMixDesignEntity(orderItem);
		if (mixDesignEntity != null) {
			buf.append(mixDesignEntity.getMixDesignCd());
			buf.append(" (");
			buf.append(mixDesignEntity.getAggRmxCode());
			buf.append(')');
		}

		return buf.toString();
	}

	private String getMaterialMixDesignProdPlantName(OrderItemEntity orderItem) {
		String plantName = null;

		FireqMaterialMixdesignEntity mixDesignEntity = getMixDesignEntity(orderItem);
		if (mixDesignEntity != null) {
			PlantBsEntity plant = mixDesignEntity.getPlant();
			if (plant != null) {
				plantName = plant.getPlantName();
			}
		}

		// TODO 2013-11/30/tito: does this fallback make sense?
		if (StringUtils.isEmpty(plantName) && orderItem.getProductionPlant() != null) {
			plantName = orderItem.getProductionPlant().getPlantName();
		}

		return transliterate(plantName);
	}

	private FireqMaterialMixdesignEntity getMixDesignEntity(OrderItemEntity orderItem) {
		FireqMaterialMixdesignEntity mixDesign = null;

		LoadEntity load = orderItem.getLoad();
		if (load != null && load.getLoadingTs() != null) {
			mixDesign = load.getMixDesign();
		}

		if (mixDesign == null) {
			mixDesign = orderItem.getMixDesign();
		}

		return mixDesign;
	}

	/**
	 * Builds the price information for material lines (orderItems).
	 * Creates a cash sales {@link OrderItemDTO} using the parameters.
	 * @param orderItem - used to find corresponding {@link InvoiceBean} information from {@link OrderServiceBean}
	 * @param currency - to be set on the {@link InvoiceBean} field of the returned instance
	 * @return
	 */
	private OrderItemDTO buildPriceInfoForInvoice(OrderItemEntity orderItem, String currency) {
		InvoiceBean invoiceBean = orderService.getInvoiceBean(orderItem);
		if (invoiceBean == null) {
			String cashSalesPriceError = Message.lookup(locale, "pdf.cashsales.priceerror");
			return new OrderItemDTO("", transliterate(cashSalesPriceError));
		}
		
		final boolean isCashSales = Boolean.valueOf(getData(DeliveryNoteData.IS_CASH_SALES));
		
		// Assumption: Cash Sales and Export Invoice could not happen in a same case.
		if (isCashSales) {
			return buildPriceForCashSales(invoiceBean, currency);
		}
		else {
			// Export Invoice
			return buildPriceForExportInvoice(invoiceBean, orderItem, currency);
		}
	}
	
	private OrderItemDTO buildPriceForCashSales(InvoiceBean csb, String currency) {
		int priceTypeCd = -1;
		final String cashSalesPriceType = getData(DeliveryNoteData.CASH_SALES_PRICE_TYPE);
		if (!StringUtils.isEmpty(cashSalesPriceType)) {
			priceTypeCd = Integer.valueOf(cashSalesPriceType);
		}
		else {
			// Use the standard price type if none provided
			priceTypeCd = CashSalesPriceType.STANDARD.getPriceTypeCode();
		}
		
		String sapMatPriceInfoPart1 = null;
		String sapMatPriceInfoPart2 = null;
		
		final String netPriceString = getLabel(DeliveryNoteLabel.NET_PRICE) + ": " + csb.getNetPrice().toPlainString() + " " + currency;
		 
		final String vatString = getLabel(DeliveryNoteLabel.VAT) + ": "
				+ csb.getVatValue().toPlainString();
		final String vatRateString = getLabel(DeliveryNoteLabel.VAT_RATE) + ": "
				+ csb.getVatRate().toPlainString() + "%";
		final String grossPriceString = getLabel(DeliveryNoteLabel.GROSS_PRICE) + ": "
				+ csb.getGrossPrice().toPlainString() + " " + currency;
		final String totalPriceString = getLabel(DeliveryNoteLabel.TOTAL_PRICE) + ": "
				+ csb.getGrossPrice().toPlainString() + " " + currency;
		
		if (priceTypeCd == CashSalesPriceType.STANDARD.getPriceTypeCode()) {
			// Net price: 1000.00 CHF	VAT: 80.00 CHF	Gross price: 1080.00 CHF
			sapMatPriceInfoPart1 = netPriceString;
			sapMatPriceInfoPart2 = vatString + " " + grossPriceString;
		}
		else if (priceTypeCd == CashSalesPriceType.BULGARIAN.getPriceTypeCode()) {
			// Net price: 1000.000 CHF	VAT Rate: 12%	VAT: 120.000 CHF	Total: 1120.000
			sapMatPriceInfoPart1 = netPriceString;
			sapMatPriceInfoPart2 = vatRateString + " " + vatString + " " + totalPriceString;
		}
		else if (priceTypeCd == CashSalesPriceType.CZECH.getPriceTypeCode()) {
			// Net Price per ton: 952.10 CHF	Net Price: 23.612 CHF	VAT Rate: 21%	VAT: 4.96 CHF	Total: 28.57 CHF
			
			// "Net Price per" + Base UoM (tlgnr_uom_tx)
			final UomTxEntity baseUomTxEntity = referentialDataService.findTxEntity(
					UomTxEntity.class, locale, csb.getBaseUoM());
			final String baseUomTxt = baseUomTxEntity.getTxt();
			final String netPricePerBaseUoMLabel = getLabel(DeliveryNoteLabel.NET_PRICE_PER_BASEUOM)
					+ " " + baseUomTxt;
			final String netPricePerBaseUoMString = netPricePerBaseUoMLabel + ": "
					+ csb.getNetPricePerBaseUoM().toPlainString() + " " + currency;

			sapMatPriceInfoPart1 = netPricePerBaseUoMString;
			sapMatPriceInfoPart2 = netPriceString + " " + vatRateString + " " + vatString + " " + totalPriceString;
		}
		
		return new OrderItemDTO(sapMatPriceInfoPart1, sapMatPriceInfoPart2, csb);
	}
	
	private OrderItemDTO buildPriceForExportInvoice(InvoiceBean csb, OrderItemEntity orderItem, String currency) {
		int priceTypeCd = -1;
		final String exportinvoicePriceType = getData(DeliveryNoteData.EXPORT_INVOICE_PRICE_TYPE);
		if (!StringUtils.isEmpty(exportinvoicePriceType)) {
			priceTypeCd = Integer.valueOf(exportinvoicePriceType);
		}
		else {
			// Use the standard price type if none provided
			priceTypeCd = ExportInvoiceMaterialPriceType.DO_NOT_DISPLAY.getMaterialPriceTypeCode();
		}
		
		String sapMatPriceInfoPart1 = null;
		String sapMatPriceInfoPart2 = null;
		
		// ExportInvoiceMaterialPriceType.DO_NOT_DISPLAY --> no need to display anything.
		final boolean display = priceTypeCd == ExportInvoiceMaterialPriceType.DISPLAY.getMaterialPriceTypeCode();
		final boolean displayAndExtraLine = priceTypeCd == ExportInvoiceMaterialPriceType.DISPLAY_AND_EXTRA_LINE_IF.getMaterialPriceTypeCode();
		if (display || displayAndExtraLine) {
			
			// Mat Price = Net Price per base Uom
			final String netPricePerBaseUoMLabel = getLabel(DeliveryNoteLabel.EXPORT_INVOICE_MAT_PRICE);
			final String netPricePerBaseUoMString = buildSinglePriceStringForExportInvoice(netPricePerBaseUoMLabel, 
					orderItem, csb.getNetPricePerBaseUoM(), currency);
			// Total Net Price
			final String totalNetPriceLabel = getLabel(DeliveryNoteLabel.EXPORT_INVOICE_TOTAL_NET_PRICE);
			final String totalNetPriceString = buildSinglePriceStringForExportInvoice(totalNetPriceLabel,
					orderItem, csb.getTotalNetPrice(), currency);
			
			// Mat. price (RSD): 4578.00		Total Net Price (RSD): 4578.00
			sapMatPriceInfoPart1 = netPricePerBaseUoMString;
			sapMatPriceInfoPart2 = totalNetPriceString;
		}
		
		return new OrderItemDTO(sapMatPriceInfoPart1, sapMatPriceInfoPart2, csb);
	}
	
	/**
	 * This method constructs the price strings which will appear in the Export Invoice.
	 * It can happen that the returned string is either "0.00" or "" or "label (currency): x.xx".
	 * For further information see LOGON-7361 
	 * <br><br>
	 * Examples for output: <br>
	 * - Mat. price (EUR): 0.00 <br>
	 * - Total netto price (HUF): 0.00
	 * 
	 * @param label 
	 * @param orderItem
	 * @param price
	 * @param currency
	 * @return
	 */
	String buildSinglePriceStringForExportInvoice(String label, OrderItemEntity orderItem, BigDecimal price, String currency){

		PriceToPrint priceToPrint = whatPriceToPrint(orderItem, price);
		
		switch (priceToPrint) {
		case ZERO:			
			return label + " (" + currency + "): "	+ BigDecimal.ZERO.setScale(Constant.MONEY_SCALE).toPlainString();			
		case DONTPRINT:			
			return "";			
		case ACTUALPRICE:			
			return label + " (" + currency + "): " + price.toPlainString();
		default:
			throw new IllegalArgumentException("Illegal value of enum in Export invoice price printing");
		}
	}
	
	/**
	 * This method decides what to print as the price of an order item
	 * depending on the price, contract type and order.FREE_DELIVERY_FLAG.
	 * 
	 * More information: LOGON-7361 and buildSinglePriceStringForExportInvoice method.
	 * 
	 * @param orderItem
	 * @param price
	 * @return
	 */
	private PriceToPrint whatPriceToPrint(OrderItemEntity orderItem, BigDecimal price){
		
		SapContractEntity sapContract = orderItem.getSapContractItem().getSapContract();
		String sapContractTypeCd = sapContract.getSapContractType().getSapContractTypeCd();
		boolean freeOfCharge = sapContractTypeCd.equals(Constant.SAP_CONTRACT_TYPE_FREE_OF_CHARGE);
		boolean freeDeliveryFlag = orderItem.getOrder().getFreeDeliveryFlag();
		boolean priceIsZero = BigDecimal.ZERO.compareTo(price) == 0;
		
		if (LogonPackageHandlingUtil.isService(orderItem)){
			return PriceToPrint.ACTUALPRICE;
		}
		else if (priceIsZero){
			if (freeOfCharge){
				return PriceToPrint.ZERO;
			} 
			else {
				return PriceToPrint.DONTPRINT;
			}
		}
		else {
			if (freeDeliveryFlag) {
				return PriceToPrint.ZERO;
			}
			else {
				return PriceToPrint.ACTUALPRICE;
			}
		}
	}
	
	private OrderItemDTO buildMatPriceInEurForExportInvoice(OrderItemEntity orderItem) {
		final InvoiceBean csb = orderService.getInvoiceBean(orderItem);
		if (csb == null) {
			return new OrderItemDTO("", Message.lookup(locale, "pdf.cashsales.priceerror"));
		}
		
		String currency = Constant.CURRENCY_CODE_EUR;
		
		final String exportinvoicePriceType = getData(DeliveryNoteData.EXPORT_INVOICE_PRICE_TYPE);
		if (StringUtils.isEmpty(exportinvoicePriceType) || csb.getNetPricePerBaseUomInEur() == null) {
			return null;
		}
		final int priceTypeCd = Integer.valueOf(exportinvoicePriceType);
		String sapMatPriceInfoPart1 = null;
		if (priceTypeCd == ExportInvoiceMaterialPriceType.DISPLAY_AND_EXTRA_LINE_IF.getMaterialPriceTypeCode()) {
			// Mat. price (EUR): 40.00 
			
			final String netPricePerBaseUoMLabel = getLabel(DeliveryNoteLabel.EXPORT_INVOICE_MAT_PRICE);
			sapMatPriceInfoPart1 = buildSinglePriceStringForExportInvoice(netPricePerBaseUoMLabel, 
					orderItem,csb.getNetPricePerBaseUomInEur(), currency);
			
			return new OrderItemDTO(sapMatPriceInfoPart1, "", csb);
		}
		return null;
	}
	
	/**
	 * @param orderItem
	 * @return
	 */
	private String getSapNumber(OrderItemEntity orderItem) {
		return orderItem.getSapMaterial().getSapMaterialNo().replaceFirst("^0+(?!$)", "");
	}
	
	private String getMaterialDesc(Long sapMaterialId, Locale locale) {
		String result = "";
		final ReadOnlyDao<SapMaterialDescEntity> descDao = daoProvider.getSapMaterialDescEntityDao();
		final Filter<SapMaterialDescEntity> filter = new FilterImpl<SapMaterialDescEntity>();
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID,
				sapMaterialId));
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD,
				locale.getLanguage()));

		final SapMaterialDescEntity descEntity = descDao.findSingle(filter);
		if (descEntity != null) {
			result = descEntity.getTxt();
		}
		return result;
	}
	
	/**
	 * Returns the brand_sales_name for a given material and plant combination. Can return null,
	 * that means no error, just that there is no need to override the sap_material_description in
	 * the delivery note.
	 * @param sapMaterialId
	 * @param plantCode
	 * @return
	 */
	private String getBrandNameForMaterialPlant(Long sapMaterialId, String plantCode) {
		final ReadOnlyDao<MatPlantEntity> dao = daoProvider.getMatPlantEntityDao();
		final Filter<MatPlantEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, plantCode));
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));

		final MatPlantEntity entity = dao.findSingle(filter);
		if (entity == null) {
			LOG.error("Found no tlgnc_mat_plant entry for sap_material_id=" + sapMaterialId
					+ ", plant_cd=" + plantCode);
			return null;
		}
		return entity.getBrandSalesName();
	}
	
	/** get name of the UOM 
	 * @param loc locale
	 * @param uom UomBsEntity
	 * @return
	 */
	private String getUomName(Locale loc, UomBsEntity uom) {
		String uomStr = "";

		if (uom != null) {
			String uomCd = uom.getUomCd(); // Not nullable!
			
			// "TO" will be the UOM we see 99% of all cases.
			if (uomCd.equals(Constant.UOM_TONS)) {
				uomStr = Constant.UOM_TONS_TXT;
			}
			else if (uomCd.equals(Constant.UOM_MTQ)) {
				uomStr = Constant.UOM_MTQ_TXT;
			}
			else if (uomCd.equals(Constant.UOM_KG)) {
				uomStr = Constant.UOM_KG_TXT;
			}
			else {
				/*
				 * FIXME 05-Nov-2012/romek: The resulting text may be too wide, in which case
				 * jcan-print will not show the whole line!
				 */
				UomTxEntity uomTxEntity = referentialDataService.findTxEntity(UomTxEntity.class,
						loc, uom);
				
				if (uomTxEntity != null) {
					String originUomStr = uomTxEntity.getTxt();
					// Shortening UOMs to 4 Characters (Bahnwagen -> Bahn)
					uomStr = StringUtils.left(originUomStr, 4);
				}
			}
		}

		return uomStr;
	}
	
	private MatPlantEntity getLogoRules(Long sapMaterialId, String plantCd) {
		boolean debugEnabled = LOG.isDebugEnabled();
		if (debugEnabled) {
			LOG.debug("Selecting MatPlantEntity with sap_material_id = " + sapMaterialId
					+ " and plant_cd = " + plantCd);
		}

		final ReadWriteDao<MatPlantEntity> matPlantDao = daoProvider.getMatPlantEntityDao();
		final Filter<MatPlantEntity> filter = matPlantDao.createFilter();
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, plantCd));

		final MatPlantEntity matPlantEntitiy = matPlantDao.findSingle(filter);

		if (debugEnabled) {
			LOG.debug("MatPlantEntity for sap_material_id = " + sapMaterialId + " and plant_cd = "
					+ plantCd + " : " + matPlantEntitiy);
		}

		return matPlantEntitiy;
	}
	
	/**
	 * Gets all the SAP logos from TLGNS_SAP_MAT_PLANT_NORM table.
	 */
	private List<SapMatPlantNormEntity> getSapMatPlantNormList(MatPlantEntity materialPlant,
			OrderEntity order) {
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.debug("Selecting SapMatPlantNormEntity list with mat_plant_id = "
					+ materialPlant.getMatPlantId());
		}
		//The returned list will contain:
		// a) Exact match (Production Plant, Material Sales Organization)
		// b) Entries which match (Production Plant, Material), and have sorg_cd == null

		final ReadOnlyDao<SapMatPlantNormEntity> dao = daoProvider.getSapMatPlantNormEntityDao();

		//Get the set of material IDs for Filter 1 items, excluding service materials
		final List<Long> sapMaterialIds = new ArrayList<Long>();
		List<OrderItemEntity> orderItems = order.getOrderItems();
		for (OrderItemEntity orderItem : orderItems) {
			if (orderItem.getSapMaterial().getMaterialPackage().getLogonPackageType().getPackageTypeCd() != SERVICE_PKG_CD) {
				sapMaterialIds.add(orderItem.getSapMaterial().getSapMaterialId());
			}
		}
		
		if (sapMaterialIds.isEmpty()) { // Orders with service only materials doesn't have any norm info
			LOG.info("Only service item in the order. No MatPlanNorm info available.");
			return new ArrayList<SapMatPlantNormEntity>();
		}

		String shipToCountry = order.getShiptoSapBp().getCountry().getSapCountryCd();

		final Filter<SapMatPlantNormEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SapMatPlantNormEntity.ATTR_PLANT_CD,
				materialPlant.getPlant().getPlantCd()));
		filter.addExpression(Restrictions.in(SapMatPlantNormEntity.ATTR_SAP_MATERIAL_ID,
				sapMaterialIds));
		filter.addExpression(Restrictions.or(Restrictions.eq(SapMatPlantNormEntity.ATTR_SORG_CD,
				order.getSarea().getSorg().getSorgCd()),
				Restrictions.isNull(SapMatPlantNormEntity.ATTR_SORG_CD)));
		filter.addExpression(Restrictions.eq(SapMatPlantNormEntity.ATTR_SHOW_LOGO_FLAG, true));
		filter.addExpression(Restrictions.eq(SapMatPlantNormEntity.ATTR_MARKED_FOR_DELETION, false));
		filter.addExpression(Restrictions.eq(SapMatPlantNormEntity.ATTR_SAP_LANGUAGE,
				order.getShiptoSapBp().getSapLanguage()));
		filter.addExpression(Restrictions.or(
				Restrictions.isNotNull(SapMatPlantNormEntity.ATTR_SAP_MATERIAL_NORM + "."
						+ SapMaterialNormBsEntity.ATTR_DN_LOGO_IMAGE_NAME),
				Restrictions.isNotNull(SapMatPlantNormEntity.ATTR_DN_LOGO_IMAGE_NAME)));

		List<SapMatPlantNormEntity> foundNorms = dao.find(filter);

		ArrayList<SapMatPlantNormEntity> materialPlantNorms = null;
		try {
			materialPlantNorms = new ArrayList<SapMatPlantNormEntity>();

			// LOGON-6634: first we prefer entries where the country matches; if we find none we fallback to entries where destCountry == null
			Map<String, List<SapMatPlantNormEntity>> foundNormsByNormCode = new HashMap<String, List<SapMatPlantNormEntity>>();

			for (SapMatPlantNormEntity currentNorm : foundNorms) {
				SapMaterialNormBsEntity sapMaterialNorm = currentNorm.getSapMaterialNorm();
				String currentNormCode = sapMaterialNorm.getSapMaterialNormCd();
				List<SapMatPlantNormEntity> normsForCurrentNormCode = foundNormsByNormCode.get(currentNormCode);
				if (normsForCurrentNormCode == null) {
					normsForCurrentNormCode = new ArrayList<SapMatPlantNormEntity>();
					foundNormsByNormCode.put(currentNormCode, normsForCurrentNormCode);
				}
				normsForCurrentNormCode.add(currentNorm);
			}

			Set<String> normCodes = foundNormsByNormCode.keySet();
			for (String normCode : normCodes) {
				List<SapMatPlantNormEntity> materialPlantNormsForNormCode = foundNormsByNormCode.get(normCode);
				List<SapMatPlantNormEntity> materialPlantNormsOfCountry = getMaterialPlantNormsOfCountry(
						shipToCountry, materialPlantNormsForNormCode);
				if (materialPlantNormsOfCountry.isEmpty()) {
					List<SapMatPlantNormEntity> normsWithoutCountry = getMaterialPlantNormsOfCountry(
							NO_COUNTRY, materialPlantNormsForNormCode);
					materialPlantNorms.addAll(normsWithoutCountry);
				}
				else {
					materialPlantNorms.addAll(materialPlantNormsOfCountry);
				}
			}
			Collections.sort(materialPlantNorms, new MaterialPlantNormComparator());
			return materialPlantNorms;
		}
		catch (Exception exception) {
			LOG.error("Failed to filter SapMatPlantNormEntity list by destCountry!", exception);
			return null;
		}
	}

	/**
	 * Collect material plant norms belonging to a destination country
	 * @param country
	 * @param materialPlantNorms
	 * @return
	 */
	List<SapMatPlantNormEntity> getMaterialPlantNormsOfCountry(String country,
			List<SapMatPlantNormEntity> materialPlantNorms) {
		List<SapMatPlantNormEntity> materialPlantNormsOfCountry = new ArrayList<SapMatPlantNormEntity>();
		for (SapMatPlantNormEntity materialPlantNorm : materialPlantNorms) {
			String destinationCountry = materialPlantNorm.getDestCountry();
			if (destinationCountry == null && country == null) {
				materialPlantNormsOfCountry.add(materialPlantNorm);
			}
			else if (destinationCountry != null
					&& destinationCountry.equals(country)) {
				materialPlantNormsOfCountry.add(materialPlantNorm);
			}
		}
		return materialPlantNormsOfCountry;
	}

	private DriverEntity getDriver(TruckEntity truck) {
		if (truck != null) {
			final ReadWriteDao<DriverEntity> dao = daoProvider.getDriverEntityDao();
			final Filter<DriverEntity> filter = dao.createFilter();
			Long truckId = truck.getTruckId();
			filter.addExpression(Restrictions.eq(DriverEntity.ATTR_CURRENT_TRUCK_ID, truckId));
			return dao.findSingle(filter);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see AbstractPdfDataStore#getDTOList(java.lang.Class)
	 */
	@Override
	public <E extends DTOMarker> List<E> getDTOList(Class<E> clazz) {
		List<E> dtos = new ArrayList<E>();
		if (OrderItemDTO.class.equals(clazz)) {
			for (OrderItemDTO dto : orderItemDTOs) {
				dtos.add(clazz.cast(dto));
			}
		}
		else if (EichbelegDTO.class.equals(clazz)) {
			for (EichbelegDTO dto : eichbelegList) {
				dtos.add(clazz.cast(dto));
			}
		}
		return dtos;
	}
	
	/**
	 * This method is to detect the order's package type for the delivery note.
	 */
	private Long getPackageCdOfOrderItem(OrderItemEntity orderItem) {
		if (LogonPackageHandlingUtil.isBag(orderItem)) {
			return BAG_PKG_CD;
		}
		else if (LogonPackageHandlingUtil.isBulkDumped(orderItem)) {
			return BULK_DMPD_PKG_CD;
		}
		else if (LogonPackageHandlingUtil.isBulkPowder(orderItem)) {
			return BULK_PWDR_PKG_CD;
		}
		else if (LogonPackageHandlingUtil.isService(orderItem)) {
			return SERVICE_PKG_CD;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Detects if the order has return value.
	 * !!!Currently there is only "BAG" and "BULK" type orders, service only orders are not yet existing!!! 
	 * !!!Please update this method when the service only orders are implemented!!!
	 */
	private boolean orderHasReturn(OrderEntity order) {
		for (OrderItemEntity item : order.getOrderItems()) {
			if (item.getReturnedQuantity() != null) {
				if (LogonPackageHandlingUtil.isBag(item)) {
					return true;
				}
				else if (LogonPackageHandlingUtil.isBulkDumped(item)) {
					return true;
				}
				else if (LogonPackageHandlingUtil.isBulkPowder(item)) {
					return true;
				}
			}
		}
		return false;
	}

	protected TimeZone getTimeZoneForShipTo(SapBpEntity sapBpEntity) {
		ShiptoEntity shipto = dispokarteService.fetchAndInitializeShiptoBySapId(sapBpEntity.getSapId());

		return timezoneService.loadForShipTo(shipto, locale);
	}

	protected TimeZone getTimeZoneForDeliveryPlant(ShippingpointEntity shippingPoint) {
		return this.timezoneService.loadForShippingPoint(shippingPoint, locale);
	}
	
	/**
	 * @param daoProvider the daoProvider to set
	 */
	public void setDaoProvider(DaoProvider daoProvider) {
		this.daoProvider = daoProvider;
	}
	
}