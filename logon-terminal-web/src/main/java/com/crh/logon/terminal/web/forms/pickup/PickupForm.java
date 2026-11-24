/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.forms.pickup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.terminal.web.forms.TerminalForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.SoldshipFlexSrchEntity;
import com.crh.logon.terminal.web.forms.PickupTerminalOrderDTO;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * 
 * @author AdNovum Informatik AG
 * @version $Revision$ $Date$
 * 
 * @struts.form name="terminal_PickupForm"
 */
public class PickupForm extends TerminalForm<PickupTerminalOrderDTO> {

	private static final long serialVersionUID = 1;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PickupForm.class);
	
	public static final String FORM_NAME = "terminal_PickupForm";

	private ShiptoEntity shipTo;
	private List shiptoSalesAreas;
	private SapBpEntity soldTo;

	private String shiptoSoldtoCompositeId;

	private PickupCustorderBean custorderBean;

	private List shiptoList;


	private Long inputCustOrderId;

	private HashMap<Integer,String> productTypeMap;
	private Integer productType;
	private String compositeId;
	private String selectedCustorderItemBeanIndex;
	private String selectedPalletItemBeanIndex;
	private String inputQuantity;
	private String inputBilltoId;
	private String inputReturnPalets;
	private String inputDeliveryNote;
	private String orderValidationCode;

	private Map<String, DeliverySettingDTO> availableDeliverySettings;
	
	private Map uomCdToUomTxMap;
	
	/**
	 * Default constructor.
	 */
	public PickupForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "PickupForm");
		}
		// ]]
		try {
			init();
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			init();
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the order.web form.
	 */
	private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			orderDTOList = null;
			inputCustOrderId = null;
			selectedCustorderItemBeanIndex = null;
			inputQuantity = null;
			inputReturnPalets = null;
			inputDeliveryNote = null;
			inputBilltoId = null;
			shiptoList = null;
			uomCdToUomTxMap = null;
			selectedPalletItemBeanIndex = null;
			compositeId = null;
			ordersLoadableTerminalFlag = null;
			clearCreate();

		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * @return the inputDeliveryNote
	 */
	public String getInputDeliveryNote() {
		return inputDeliveryNote;
	}

	/**
	 * @param inputDeliveryNote
	 *            the inputDeliveryNote to set
	 */
	public void setInputDeliveryNote(String inputDeliveryNote) {
		this.inputDeliveryNote = inputDeliveryNote;
	}

	/**
	 * @param pickupForm
	 * @param shipToList
	 */
	public void presetShipto() {
		// if there is only one shipto
		if (shiptoList != null && shiptoList.size() == 1) {
			SoldshipFlexSrchEntity sapSoldShip = (SoldshipFlexSrchEntity) shiptoList
					.get(0);
			setShiptoAndSoldTo(sapSoldShip);
		}
	}

	/**
	 * 
	 */
	public void clearCreate() {
		custorderBean = null;
		shipTo = null;
		soldTo = null;
		shiptoSoldtoCompositeId = null;
		presetShipto();
		inputCustOrderId = null;
		selectedCustorderItemBeanIndex = null;
		compositeId = null;
		selectedPalletItemBeanIndex = null;
	}

	/**
	 * Override struts method. Perform form-specific validation of input
	 * parameters.
	 */
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = { { "mapping", mapping }, { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			ActionErrors errors = super.validate(mapping, request);
			return result = errors;
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public ShiptoEntity getShipTo() {
		return shipTo;
	}

	public void setShipTo(ShiptoEntity shipTo) {
		this.shipTo = shipTo;
	}

	public List getShiptoSalesAreas() {
		return shiptoSalesAreas;
	}

	public void setShiptoSalesAreas(List shiptoSalesAreas) {
		this.shiptoSalesAreas = shiptoSalesAreas;
	}

	public SapBpEntity getSoldTo() {
		return soldTo;
	}
	
	public boolean isCreateMode() {
		return getCustorderBean().getCustorderEntity() != null && getCustorderBean().getCustorderEntity().getCustorderId() == null;
	}

	public void setSoldTo(SapBpEntity soldTo) {
		this.soldTo = soldTo;
	}

	public PickupCustorderBean getCustorderBean() {
		return custorderBean;
	}

	public void setOrderBean(PickupCustorderBean orderBean) {
		this.custorderBean = orderBean;
	}

	public List getShiptoList() {
		return shiptoList;
	}

	public void setShiptoList(List shiptoList) {
		this.shiptoList = shiptoList;
	}

	/**
	 * @param sapSoldShip
	 */
	public void setShiptoSoldtoCompositeId(String compositeId) {
		Iterator it = shiptoList.iterator();
		while (it.hasNext()) {
			SoldshipFlexSrchEntity viewEntity = (SoldshipFlexSrchEntity) it
					.next();
			if (viewEntity.getCompositeId().equals(compositeId)) {
				setShiptoAndSoldTo(viewEntity);
			}
		}
		this.shiptoSoldtoCompositeId = compositeId;
	}

	/**
	 * @param sapSoldShip
	 */
	public void setShiptoAndSoldTo(SoldshipFlexSrchEntity sapSoldShip) {
		Authorization
				.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
		String shiptoSapId = sapSoldShip.getShiptoNr();
		DispokarteService dispokarteService = ServiceLocatorProvider
				.getServiceLocator().getDispokarteService();
		ShiptoEntity shipto = dispokarteService
				.fetchAndInitializeShiptoBySapId(shiptoSapId);
		setShipTo(shipto);
		List shiptoSalesAreas = dispokarteService.fetchShiptoSareas(
				shipto, sapSoldShip.getSoldtoSapBpId(), true);
		setShiptoSalesAreas(shiptoSalesAreas);

		String soldtoSapId = sapSoldShip.getSoldtoNr();
		setSoldTo(dispokarteService.fetchSoldtoSapBpBySapId(soldtoSapId));
	}

	public String getShiptoSoldtoCompositeId() {
		return shiptoSoldtoCompositeId;
	}

	public String getInputQuantity() {
		return inputQuantity;
	}

	public void setInputQuantity(String inputQuantity) {
		this.inputQuantity = inputQuantity;
	}

	public String getInputBilltoId() {
		return inputBilltoId;
	}

	public void setInputBilltoId(String inputBilltoId) {
		this.inputBilltoId = inputBilltoId;
	}

	public String getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
	}

	public String getSelectedCustorderItemBeanIndex() {
		return selectedCustorderItemBeanIndex;
	}

	public void setSelectedCustorderItemBeanIndex(
			String selectedCustorderItemBeanIndex) {
		this.selectedCustorderItemBeanIndex = selectedCustorderItemBeanIndex;
	}

	public String getSelectedPalletItemBeanIndex() {
		return selectedPalletItemBeanIndex;
	}

	public void setSelectedPalletItemBeanIndex(String selectedPalletItemBeanIndex) {
		this.selectedPalletItemBeanIndex = selectedPalletItemBeanIndex;
	}

	public String getInputReturnPalets() {
		return inputReturnPalets;
	}

	public void setInputReturnPalets(String inputReturnPalets) {
		this.inputReturnPalets = inputReturnPalets;
	}

	public Map getUomCdToUomTxMap() {
		return uomCdToUomTxMap;
	}

	public void setUomCdToUomTxMap(Map uomCdToUomTxMap) {
		this.uomCdToUomTxMap = uomCdToUomTxMap;
	}

	public Long getInputCustOrderId() {
		return inputCustOrderId;
	}

	public void setInputCustOrderId(Long inputCustOrderId) {
		this.inputCustOrderId = inputCustOrderId;
	}

	public Integer getProductType() {
		return productType;
	}
	
	/**
	 * Prepares information for the client side whether the user selected bulk
	 * as material type at the beginning of the order creation
	 * @return
	 */
	public boolean isProductTypeBulkSelected() {
		return (productType != null && productType == Constant.PRODUCT_TYPE_BULK);
	}

	/**
	 * Prepares information for the client side whether the current/selected order 
	 * is a bulk order 
	 * @return
	 */
	public boolean isBulkOrderSelected() {
		return (custorderBean != null && LogonPackageHandlingUtil.isBulk(custorderBean.getCustorderEntity()));
	}
	
	public boolean isBuyProductAvailable() {
		if (custorderBean != null && custorderBean.getCustorderEntity() != null) {
			final DeliveryTypeBsEntity deliveryType = custorderBean.getCustorderEntity().getDeliveryType();
			if (deliveryType != null && LogonUtil.isReturnPalletOnly(deliveryType.getDeliveryTypeCd())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isReturnProductAvailable() {
		if (isBulkOrderSelected()) {
			return false;
		}
		
		if (custorderBean != null && custorderBean.getCustorderEntity() != null) {
			final DeliveryTypeBsEntity deliveryType = custorderBean.getCustorderEntity().getDeliveryType();
			if (deliveryType != null && !LogonUtil.isReturnPalletOnly(deliveryType.getDeliveryTypeCd())) {
				return false;
			}
		}
		return true;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	public HashMap<Integer,String> getProductTypeMap() {
		return productTypeMap;
	}

	public void setProductTypeMap(HashMap<Integer,String> productTypeMap) {
		this.productTypeMap = productTypeMap;
	}

	public String getOrderValidationCode() {
		return orderValidationCode;
	}
	
	public void setOrderValidationCode(String orderValidationCode) {
		this.orderValidationCode = orderValidationCode;
	}
	
	public Map<String, DeliverySettingDTO> getAvailableDeliverySettings() {
		return availableDeliverySettings;
	}

	public void setAvailableDeliverySettings(Map<String, DeliverySettingDTO> map) {
		this.availableDeliverySettings = map;
	}

	public Map<String, DeliverySettingDTO> getAvailableBuyDeliverySettings() {
		return getAvailableDeliverySettings(false);
	}
	
	public Map<String, DeliverySettingDTO> getAvailableReturnDeliverySettings() {
		return getAvailableDeliverySettings(true);
	}
	
	public Map<String, DeliverySettingDTO> getAvailableDeliverySettings(final boolean isOnlyReturn) {
		final Map<String, DeliverySettingDTO> result = new HashMap<String, DeliverySettingDTO>();
		if (this.availableDeliverySettings != null && !this.availableDeliverySettings.isEmpty()) {
			for (final Map.Entry<String, DeliverySettingDTO> entry : this.availableDeliverySettings.entrySet()) {
				if (entry != null && entry.getKey() != null && entry.getValue() != null) {
					final DeliveryTypeBsEntity deliveryType = entry.getValue().getDeliveryType();
					
					if (isOnlyReturn) {
						if (deliveryType != null && LogonUtil.isReturnPalletOnly(deliveryType.getDeliveryTypeCd())) {
							result.put(entry.getKey(), entry.getValue());
						}
					}
					else {
						if (deliveryType == null || !LogonUtil.isReturnPalletOnly(deliveryType.getDeliveryTypeCd())) {					
							result.put(entry.getKey(), entry.getValue());
						}
					}
				}
			}
		}
		
		return result;
	}
	
}
