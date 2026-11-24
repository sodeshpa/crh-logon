/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.utils.BindingsDTO;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.actions.auftrag.OrderDetailAction;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderRepEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.frozen.OrderFinalEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnQuantHandlingBsEntity;
import com.crh.logon.persistence.entities.refdata.SapOrderExtraCostBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeParser;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.JsonUtils;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.lang.time.DateUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_auftrag_OrderDetailForm"
 */
@AdnTrace
public class OrderDetailForm extends ActionForm {

	private static final int EQUAL = 0;
	private static final long serialVersionUID = -8910104051337644556L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderDetailForm.class);
    public static final String SESSION_RFIDFORM_NAME = "order_auftrag_OrderDetailForm";
    public static final String REQUEST_BIZEVENT_AUDIT_LIST = "bizeventAuditList";
    
	//selected order
	private OrderEntity order;
	private OrderFinalEntity orderFinal;
	private AssignmentEntity assignment;
	private ShiptoEntity shipto;
	private ShiptoSareaEntity shiptoSarea;
	private SapBpSareaEntity sapBpSarea;
	
	//planning material
	private Long planningMaterialContractItemId;
	private List<PlanningMaterialBean> planningMaterialReplacementList;
	private boolean isPlanningMaterialStillEditable;
	
	private Boolean bagOrderFlag;
	private String reloadOrderId;
	private String formAction;
	private String title;
	private Boolean recalculateCostsFlag;

	//beans
	private OrderDetailBean orderBean;
	private MaterialTabBean materialBean; 
	private ServicesExtraCostTabBean servicesExtraCostBean;
	private MapTabBean mapBean;
	private ReportingTabBean reportingBean;
	private MngTabBean mngBean;
	private RepTabBean repBean;
	private ChangeBpBean changeBpBean;
	private CustOrderTabBean custOrderTabBean;
	
	private OrderDetailUploadAttachmentBean uploadBean;

	private String allowedVehiclesCombo;
	
	private boolean payerIgnored;
	
	//selected tab name
	private String selectedTab = OrderDetailAction.FORWARD_MATERIAL_TAB;
    private Locale locale;

	//lists for drop downs
	private List deliveryTypeList; 
	private List <SupplyPlantDTO> plantList;
	private Map shPointMap;
	private Map ldPointMap;
	private List poolList;
	private Map commCarrierMap;
	private List<CarrierEntity> execCarrierList;
	private List truckList;
	private List truckTypeList;
	private List driverList;
	private List spiList;
	private Map <String, ShiptoBilltoBean> billtoMap;
	private Map <Long, PayerSapBpBean> payerMap;

	//return reason variables
	private Map<String, ReturnOrderReasonBsEntity> returnReasonMap; //ReturnOrderReasonBsEntity -> Required for setting the item's return reason.
	private Long selectedOrderItemId;
	private String selectedReturnReasonCd;
	private ReturnOrderReasonBsEntity returnOrderReasonRailUnloadingProblem;
	private String returnReasonPopupTitle;

	//private long[] authParams;
	private List<Long> authParamList;
	
	//one-time customer
	private boolean billtoSameAsOneTimeSoldto = false;
	private boolean payerSameAsOneTimeSoldto = false;
	
	// used to print an error message if a file upload had been failed
	private Boolean lastUploadFailed = false;
	private String lastUnsupportedFileExtension = "";
	
	// used to store whether the attachment list was changed
	private Boolean attachmentListChanged = false;
	
	// used to decide of materialTab or services / extra cost tab was opened last time.
	private boolean materialTabOpened = false;
	
	private DeliverySettingSalesAreaMap salesAreaMap;
	
	// used to build up the allowed vehicle type multiselect dropdown
	private List<TruckTypeWrapper> truckTypeWrappers;

	private TimeZone shiptoTZ;

	// 3rd Party Freighters Tab
	private List<BindingsDTO> existingBindings;
	   
	/**
	 * @return the existingBindings
	 */
	public List<BindingsDTO> getExistingBindings() {
		return existingBindings;
	}

	/**
	 * @param existingBindings the existingBindings to set
	 */
	public void setExistingBindings(List<BindingsDTO> existingBindings) {
		this.existingBindings = existingBindings;
		LOG.debug("Existing bindings: " + existingBindings);
	}

	//params sent in case authorized in authParams available in authParamList
	public static final long PARAM_ORDER_DETAIL_DELIVERY_DATE_TIME=1l;	 // Order Detail (Main): write access for delivery date/time)			
	public static final long PARAM_ORDER_DETAIL_MATERIAL_BILLTO_PARTY=2l;	 	// Order Detail Material: change billto party		
	public static final long PARAM_ORDER_DETAIL_MATERIAL_FORWARDING_AGENT=3l;// Order Detail Material: change forwarding agent
	public static final long PARAM_ORDER_DETAIL_MATERIAL_CUSTOMS_OFFICE=4l;	 // Order Detail Material: change customs office		
	public static final long PARAM_ORDER_DETAIL_MATERIAL_CUSTOMS_SERIAL_NO = 5l;// Order Detail Material: change customs serial number
	public static final long PARAM_ORDER_DETAIL_MATERIAL_QUANTITY_UOM = 6l;// Order Detail Material: change order quantity and UoM
	public static final long PARAM_ORDER_DETAIL_MATERIAL_W = 7l;// Order Detail Material: write access (except for 6 special functions)
	public static final long PARAM_ORDER_DETAIL_W = 8l;
    public static final long PARAM_ORDER_DETAIL_MATERIAL_DELIVERY_NOTE = 9l; // Order Detail Material: change comment add/on delivery note
    public static final long PARAM_ORDER_DETAIL_REPORT_W = 10l; // Order Detail Report: write access (exept for 6 special functions)
    public static final long PARAM_ORDER_DETAIL_REPORT_SERVICE_LEVEL_FLAG = 11l; // Order Detail Report: change service level flag
    public static final long PARAM_ORDER_DETAIL_REPORT_RATING_COMMNT_CRH = 12l; // Order Detail Report: change rating comment crh
    public static final long PARAM_ORDER_DETAIL_REPORT_RATING_COMMNT_CARR = 13l; // Order Detail Report: change rating comment carrier
    public static final long PARAM_ORDER_DETAIL_REPORT_COMMENT_DISPATCHING = 14l; // Order Detail Report: change comment dispatching (rail/road)
    public static final long PARAM_ORDER_DETAIL_REPORT_COMMENT_DRIVER = 15l; // Order Detail Report: change comment truck driver
    public static final long PARAM_ORDER_DETAIL_REPORT_COMMENT_SHIPPING = 16l; // Order Detail Report: change comment shipping (rail/road)
    public static final long PARAM_ORDER_DETAIL_ORDER_MANAGEMENT = 17l; // Order Detail Order Management
    public static final long PARAM_ORDER_DETAIL_WAGON_SPECIAL_ORDER_CREATE_EDIT = 18l; // Wagon Special Order: create/edit access    
	public static final long PARAM_ORDER_DETAIL_MATERIAL_PAYER_PARTY = 19l; // Order Detail Material: change payer party
	public static final long PARAM_ORDER_DETAIL_MATERIAL_ONETIMESOLDTO_ADDR = 20l; // Order Detail Material: change one-time soldto address
	public static final long PARAM_ORDER_DETAIL_SPI = 21l; // Order Detail (Main): change and store the special processing indicator (SP Indicator)
	public static final long PARAM_ORDER_DETAIL_ON_HOLD_W = 23l; // Order Detail (Main): Write Access to On-hold functionality
	public static final long PARAM_ORDER_DETAIL_EDIT_LDS_NO = 24l; //Order Detail Material: Edit LDS Delivery note number.
	// Pda Or Rail Event
	// 3rd Party Freighters
	public static final long PARAM_ORDER_DETAIL_BINDINGS = 22l;

	/**
	 * Holds the text of distance validation rules
	 */
	private String distanceValidationPopUpText = "";


	//management tab field
	public boolean getReleaseSapFlagDisabled() {
		if (order.getOrderStatus().getOrderStatusCd() == Constant.ORDER_STATUS_FINISHED) {
			return true;
		}
		return false;
	}

	private boolean getOrderStatusITrackValid() {
		if (order.getOrderStatus().getOrderStatusCd() >= Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK
				&& order.getOrderStatus().getOrderStatusCd() <= Constant.ORDER_STATUS_UNLOADING) {
			return true;
		}
		return false;
	}

	public boolean getOrderITrackAvailable() {
		return getOrderStatusITrackValid() && Boolean.TRUE.equals(order.getITrackAvailableFlag());
	}

	/** checks if order was already dispatched
	 * @return true if order was dispatched
	 */
	public boolean getDispatchedFlag() {
			long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
			
			if (orderStatusCd == Constant.ORDER_STATUS_NEW) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK) {
				return false;
			}
			
			return true;
	}

	/** checks if order was already dispatched
	 * @return true if order was dispatched
	 */
	public boolean getTransmittedToDriver() {
			long orderStatusCd = order.getOrderStatus().getOrderStatusCd();

			if (orderStatusCd == Constant.ORDER_STATUS_NEW) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_DISPATCHED) {
				return false;
			}

			return true;
	}
	
	/** checks if order was already loaded
	 * @return true if order was loaded
	 */
	public boolean getLoadedFlag() {
			long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
			
			if (orderStatusCd == Constant.ORDER_STATUS_NEW) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_DISPATCHED) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_LOADING) {
				return false;
			}
			
			return true;
	}
	
	
	/** checks if order was already loaded
	 * @return true if order was loaded
	 */
	public boolean getTransmittedToRailFlag() {
			long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
			
			if (orderStatusCd == Constant.ORDER_STATUS_NEW) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_DISPATCHED) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_LOADING) {
				return false;
			}
			else if (orderStatusCd == Constant.ORDER_STATUS_LOADED) {
				return false;
			}
			
			return true;
	}
	
	public RepTabBean getRepBean() {
		return repBean;
	}

	public void setRepBean(RepTabBean repBean) {
		this.repBean = repBean;
	}
	
	public CustOrderTabBean getCustOrderTabBean() {
		return custOrderTabBean;
	}

	public void setCustOrderTabBean(CustOrderTabBean custOrderTabBean) {
		this.custOrderTabBean = custOrderTabBean;
	}
	
	public Boolean getRecalculateCostsFlag() {
		return recalculateCostsFlag;
	}

	public void setRecalculateCostsFlag(Boolean recalculateCostsFlag) {
		this.recalculateCostsFlag = recalculateCostsFlag;
	}
	
	public OrderFinalEntity getOrderFinal() {
		return orderFinal;
	}
	
	public void setOrderFinal(OrderFinalEntity orderFinal) {
		this.orderFinal = orderFinal;
	}
	
	public Boolean getBagOrderFlag() {
		return bagOrderFlag;
	}

	public void setBagOrderFlag(Boolean bagOrderFlag) {
		this.bagOrderFlag = bagOrderFlag;
	}

	public Map <String, ShiptoBilltoBean> getBilltoMap() {
		return billtoMap;
	}

	public void setBilltoMap(Map <String, ShiptoBilltoBean> billtoMap) {
		this.billtoMap = billtoMap;
	}
	
	public Map <Long, PayerSapBpBean> getPayerMap() {
		return payerMap;
	}

	public void setPayerMap(Map <Long, PayerSapBpBean> payerMap) {
		this.payerMap = payerMap;
	}

	public String getReloadOrderId() {
		return reloadOrderId;
	}

	public void setReloadOrderId(String reloadOrderId) {
		this.reloadOrderId = reloadOrderId;
	}

	public Map getShPointMap() {
		return shPointMap;
	}

	public void setShPointMap(Map shPointMap) {
		this.shPointMap = shPointMap;
	}

	public Map getLdPointMap() {
		return ldPointMap;
	}

	public void setLdPointMap(Map ldPointMap) {
		this.ldPointMap = ldPointMap;
	}
	
	public SapBpSareaEntity getSapBpSarea() {
		return sapBpSarea;
	}

	public void setSapBpSarea(SapBpSareaEntity sapBpSarea) {
		this.sapBpSarea = sapBpSarea;
	}
	
	public Long getPlanningMaterialContractItemId() {
		return planningMaterialContractItemId;
	}
	
	public void setPlanningMaterialContractItemId(Long planningMaterialContractItemId) {
		this.planningMaterialContractItemId = planningMaterialContractItemId;
	}
	
	public List<PlanningMaterialBean> getPlanningMaterialReplacementList() {
		return planningMaterialReplacementList;
	}
	
	public void setPlanningMaterialReplacementList(
			List<PlanningMaterialBean> planningMaterialReplacementList) {
		this.planningMaterialReplacementList = planningMaterialReplacementList;
	}
	
	public boolean isPlanningMaterialStillEditable() {
		return isPlanningMaterialStillEditable;
	}

	public void setPlanningMaterialStillEditable(boolean isPlanningMaterialStillEditable) {
		this.isPlanningMaterialStillEditable = isPlanningMaterialStillEditable;
	}

	public ShiptoEntity getShipto() {
		return shipto;
	}

	public void setShipto(ShiptoEntity shipto) {
		this.shipto = shipto;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public AssignmentEntity getAssignment() {
		return assignment;
	}

	public void setAssignment(AssignmentEntity assignment) {
		this.assignment = assignment;
	}

	public MapTabBean getMapBean() {
		return mapBean;
	}

	public void setMapBean(MapTabBean mapBean) {
		this.mapBean = mapBean;
	}
	
	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}
	
	public ReportingTabBean getReportingBean() {
		return reportingBean;
	}

	public void setReportingBean(ReportingTabBean reportingBean) {
		this.reportingBean = reportingBean;
	}
	
	public MngTabBean getMngBean() {
		return mngBean;
	}

	public void setMngBean(MngTabBean mngBean) {
		this.mngBean = mngBean;
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public MaterialTabBean getMaterialBean() {
		return materialBean;
	}

	public void setMaterialBean(MaterialTabBean materialBean) {
		this.materialBean = materialBean;
	}
	
	public List getTruckTypeList() {
		return truckTypeList;
	}

	public void setTruckTypeList(List truckTypeList) {
		this.truckTypeList = truckTypeList;
	}	
	
	public List getTruckList() {
		return truckList;
	}

	public void setTruckList(List truckList) {
		this.truckList = truckList;
	}

	public List getDriverList() {
		return driverList;
	}

	public void setDriverList(List driverList) {
		this.driverList = driverList;
	}
	
	public List getPoolList() {
		return poolList;
	}

	public void setPoolList(List poolList) {
		this.poolList = poolList;
	}

	public List <SupplyPlantDTO> getPlantList() {
		return plantList;
	}

	public void setPlantList(List<SupplyPlantDTO> plantList) {
		this.plantList = plantList;
	}

	public List getDeliveryTypeList() {
		return deliveryTypeList;
	}

	public void setDeliveryTypeList(List deliveryTypeList) {
		this.deliveryTypeList = deliveryTypeList;
	}
	
	public OrderDetailBean getOrderBean() {
		return orderBean;
	}

	public void setOrderBean(OrderDetailBean orderBean) {
		this.orderBean = orderBean;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}


	
	/**
	 * Default constructor.
	 */
	public OrderDetailForm() {
		init();
	}

	/**
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the ordermgr.web form.
	 */
	private void init() {

			//reset beands
			sapBpSarea = null;
			salesAreaMap = null;
			orderBean = null;
			materialBean = null;
			mapBean = null;
			reportingBean = null;
			mngBean = null;
			repBean = null;
			changeBpBean = null;
			selectedOrderItemId = null;
			selectedReturnReasonCd = null;
			bagOrderFlag = null;
			planningMaterialReplacementList = null;
			planningMaterialContractItemId = null;
			isPlanningMaterialStillEditable = false;
			servicesExtraCostBean = null;
			custOrderTabBean = null;
			
			formAction = null;
			deliveryTypeList = new ArrayList(); 
			commCarrierMap = new HashMap();
			execCarrierList = new ArrayList<CarrierEntity>();
			plantList = new ArrayList();
			shPointMap = new HashMap();
			ldPointMap = new HashMap();
			poolList = new ArrayList();
			truckList = new ArrayList();
			truckTypeList = new ArrayList();
			driverList = new ArrayList();
			this.spiList = new ArrayList();
			billtoMap = new HashMap<String, ShiptoBilltoBean>();
			payerMap = new HashMap<Long, PayerSapBpBean>();
			returnReasonMap = new HashMap<String, ReturnOrderReasonBsEntity>();
			
			billtoSameAsOneTimeSoldto = false;
			payerSameAsOneTimeSoldto = false;
			setExistingBindings(null);
			
	}

	/**
	 * Clears all the input fields on the ordermgr.web form, discards any existing
	 * search results, and resets any existing pages. Called in response to
	 * clicking the reset button on the ordermgr.web form.
	 * Called also when opening order detail popup (to reset all values from the 
	 * previously opened order).
	 */
	public void clear() {
			init();
	}

	/**
	 * Override struts method.
	 * Note: this is not associated with or invoked by the reset button in the
	 * ordermgr.web form, but rather is called on every request before the struts action
	 * method gets control and, more importantly, before the request parameters
	 * are automatically copied into the form. The primary use is to reset
	 * checkboxes and struts multiboxes and the like for which the browser only
	 * sends the form parameters when they are set.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
			formAction = "";
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Reset all checkboxes!");
			}
			
			if (OrderDetailAction.FORWARD_MNG_TAB.equals(selectedTab) && (mngBean != null)) {
				mngBean.setReleaseSapFlag(false);
				mngBean.setNoPdaOrRailEventsFlag(false);
				mngBean.setCustFreeOfChargeFlag(false);
				mngBean.setSapSuppressMinChargeFlag(false);
			}
			
			if (OrderDetailAction.FORWARD_REP_MAINT_TAB.equals(selectedTab) && (repBean != null)) {
				repBean.setOfferDesiredFlag(false);
				repBean.setInvoicePaidFlag(false);
				repBean.setDocumentationAcceptFlag(false);
			}
			
			if (orderBean != null) {
				orderBean.setOnHoldOrderFlag(false);
				orderBean.setAutoPoolAssignmentFlag(false);
				orderBean.setAutoCommCarrierAssignmentFlag(false);
				
				if (orderBean.getServiceOnlyOrderFlag() == null) {
					orderBean.setServiceOnlyOrderFlag(false);
				}
			}
			
			if (reportingBean  != null) {
				for (Iterator it = reportingBean.getOrderItemFBeanList().iterator(); it.hasNext();) {
					OrderItemFinalBean oiBean = (OrderItemFinalBean) it.next();
					oiBean.setCreditNote(false);
				}
				reportingBean.setRelevantServiceLevelFlag(false);
			}
	}
	
	public void validateOrderDetail(HttpServletRequest request ) {

			//-------main part-------//
			
			if (getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_W)) {
				Authorization.authorize(Authorization.ORDER_DETAIL_W);
				
				//delivery type
				if (Constant.INVALID_OBJECT_ID == orderBean.getDeliveryTypeCd()) {
					MessageUtils.addActionError(request, "orderdetail.main.errors.required.delivery.type");
				}
				else {
					order.getDeliveryType().setDeliveryTypeCd(orderBean.getDeliveryTypeCd());
				}
				
				//shipping point		
				if (Constant.INVALID_OBJECT_ID_STR.equals(orderBean.getShPointNo())) {
					if (orderBean.getServiceOnlyOrderFlag() == null || !orderBean.getServiceOnlyOrderFlag().booleanValue()) {
						MessageUtils.addActionError(request, "orderdetail.main.errors.required.shpoint");
					}
				}
				else if (Constant.INVALID_OBJECT_ID_STR.equals(orderBean.getPlantCd())) {
					MessageUtils.addActionError(request, "orderdetail.main.errors.required.plant");
				}			
				
				if ((orderBean.getAutoCommCarrierAssignmentFlag()) && (!orderBean.getAutoPoolAssignmentFlag())) {
					// Carrier checked -> not possible 
					MessageUtils.addActionError(request, "orderdetail.main.errors.order.status");
				}
			}
			
			//-------management tab-------//
			if (mngBean != null) {
				if (getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_ORDER_MANAGEMENT)) {
					Authorization.authorize(Authorization.ORDER_DETAIL_ORDER_MANAGEMENT);
					
					if (StringUtils.nvl(mngBean.getNoteOrderMgmt()).length() > OrderEntity.DBSIZE_NOTE_ORDER_MGMT) {
						MessageUtils.addActionError(request, "orderdetail.mng.errors.note.order.mng.tooLong");
					}
					else {
						order.setNoteOrderMgmt(mngBean.getNoteOrderMgmt());
					}
					
					if (StringUtils.nvl(mngBean.getNoteDispatching()).length() > OrderEntity.DBSIZE_NOTE_DISPATCHING) {
						MessageUtils.addActionError(request, "orderdetail.mng.errors.note.disp.tooLong");
					}
					else {
						order.setNoteDispatching(mngBean.getNoteDispatching());
					}
					
					if (StringUtils.nvl(mngBean.getNoteCustWeborder()).length() > OrderEntity.DBSIZE_CUST_WEBORDER_NOTE) {
						MessageUtils.addActionError(request, "orderdetail.mng.errors.note.cust.web.tooLong");
					}
					else {
						order.setCustWeborderNote(mngBean.getNoteCustWeborder());
					}
					
					if (StringUtils.nvl(mngBean.getNoteTruckDriver()).length() > OrderEntity.DBSIZE_NOTE_TRUCK_DRIVER) {
						MessageUtils.addActionError(request, "orderdetail.mng.errors.note.truck.driver.tooLong");
					}
					else {
						order.setNoteTruckDriver(mngBean.getNoteTruckDriver());
					}
					
					if (StringUtils.nvl(mngBean.getNoteDelivery()).length() > OrderEntity.DBSIZE_NOTE_DELIVERY) {
						MessageUtils.addActionError(request, "orderdetail.mng.errors.note.delivery.tooLong");
					}
					else {
						order.setNoteDelivery(mngBean.getNoteDelivery());
					}
					
					if (StringUtils.nvl(mngBean.getNoteRailwayDisp()).length() > OrderEntity.DBSIZE_NOTE_RAILWAY_DISP) {
						MessageUtils.addActionError(request, "orderdetail.mng.errors.note.rail.disp.tooLong");
					}
					else {
						order.setNoteRailwayDisp(mngBean.getNoteRailwayDisp());
					}
					
				}
			}
			
			//--------------------------//
			//-------material tab-------//
			//--------------------------//
			//If the total of pallets exceeds 21, a warning message is displayed.
			BigDecimal totalPalletCount = new BigDecimal("0");
			
			for (int i = 0; i < materialBean.getOrderItemList().size(); i++) {
				OrderItemBean orderItemBean = materialBean.getOrderItemList().get(i);
				OrderItemEntity orderItemEntity = (OrderItemEntity) order.getOrderItems().get(i);
			
				//------ordered quantity------//
				final String beanOrderedUom = orderItemBean.getOrderedUom();
				if (getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_QUANTITY_UOM)) {
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_QUANTITY_UOM);
			
					//ordered quantity
					if (StringUtils.notEmpty(orderItemBean.getOrderedQuantity())) {
						try {
							orderItemEntity.setOrderedQuantity(new BigDecimal(
									orderItemBean.getOrderedQuantity()));
							
							if (Constant.UOM_BAG_P40.equals(beanOrderedUom) ||
									Constant.UOM_BAG_P48.equals(beanOrderedUom)) {
								totalPalletCount = totalPalletCount.add(orderItemEntity.getOrderedQuantity());
							}
							
						}
						catch (NumberFormatException e) {
							MessageUtils.addActionError(request, "orderdetail.mat.errors.format.ordered.quantity");
						}
						
						/* If the order's unit of measure is truck, then at least one truck type must
						 * have been selected in the order.
						 */
						if (Constant.UOM_LKW.equals(beanOrderedUom) && orderBean.getSelectedVehicles().isEmpty()) {
							MessageUtils.addActionError(request, "orderdetail.mat.errors.required.truck.type");
						}
					}
					else {
						MessageUtils.addActionError(request, "orderdetail.mat.errors.format.ordered.quantity");
					}
					
					//ordered uom
					if (Constant.INVALID_OBJECT_ID_STR.equals(beanOrderedUom)) {
						if (Constant.MATERIAL_PACKAGE_BULK_POWDER.equals(orderItemBean.getMaterialPackageCd()) 
								|| (StringUtils.notEmpty(orderItemBean.getOrderedQuantity()))) {
							MessageUtils.addActionError(request, "orderdetail.mat.errors.required.ordered.oum");
						}
						else {
							orderItemEntity.setOrderedUom(null);
						}
					}
					else {
						boolean isOrderedQuantityDisabled = true;
						//TODO orsika 20130213 condition must be the same as it is in MaterialTabItem.jsp for 
						//setting isOrderedQuantityDisabled variable -> should be set only once in a single variable, and queried from jsp
						if (!orderItemBean.getIsRailReturnRepair() && (orderItemBean.getIsNew() || !getDispatchedFlag())) {
							isOrderedQuantityDisabled = false;
						}
						
						if (!isOrderedQuantityDisabled) {
							Map <String, UomTxEntity>uomMap = orderItemBean.getUomMap();
							final UomTxEntity uomTxEntity = uomMap.get(beanOrderedUom);
							if ( uomTxEntity == null ) {
								MessageUtils.addActionError(request, "orderdetail.mat.errors.required.ordered.oum");							
							}
							else {							
								orderItemEntity.setOrderedUom(uomTxEntity.getUomTxPK().getUomBs());
							}
						}
					}
					
					//return reason
					if (StringUtils.notEmpty(orderItemBean.getReturnedQuantity())) {
						
						validateReturnedQuantity(request, orderItemBean, orderItemEntity);

						//return quantity is set
						if (Constant.INVALID_OBJECT_ID_STR.equals(orderItemBean.getReturnReason())) {
							//return reason not set
							MessageUtils.addActionError(request, "orderdetail.mat.errors.required.return.reason");
						}
						else {
							//return reason set
							ReturnOrderReasonBsEntity returnOrderReasonBsEntity	= returnReasonMap.get(orderItemBean.getReturnReason());
							orderItemEntity.setReturnOrderReason(returnOrderReasonBsEntity);
						}
					}
					else {
						//return quantity is not set
						if (!Constant.INVALID_OBJECT_ID_STR.equals(orderItemBean.getReturnReason())) {
							//return reason is set.
							//selectedReturnReasonCd = null;
							orderItemEntity.setReturnOrderReason(null);
							MessageUtils.addActionError(request, "orderdetail.mat.warning.required.quantity.reason");
						}
					}
					
					// Check if ordered quantity > 1 if UoM would normally create multiple orders.
					BigDecimal orderedQuantity = orderItemEntity.getOrderedQuantity();
					UomBsEntity orderedUom = orderItemEntity.getOrderedUom();
					
					if (orderItemEntity.getOrderedUom() != null && orderedUom != null
							&& orderedUom.getCreateMultiOrderFlag().booleanValue()
							&& orderedQuantity.compareTo(BigDecimal.ONE) > 0) {
						MessageUtils.addActionError(request,
								"orderdetail.message.error.ordereduom.createmultiorderflag",
								orderedUom.getUomCd());
					}
				}
				
				//------loaded quantity--------//
				//------tare, net weights------//
				if (!LogonUtil.isSentToSap(order)) {
					if (Authorization.isAllowed(Authorization.ORDER_ADJUSTMENT_WEIGHTS)) {
						setLoadedQuantity(request, orderItemBean, orderItemEntity);
						setTareAndNetWeights(request, orderItemBean, orderItemEntity, order);
						
						// if the order hasn't been sent to SAP, the returned quantity can be changed
						orderItemEntity = changeReturnedQuantity(request, i, orderItemBean,
								orderItemEntity);
					}
				}
				else {
					if (Authorization.isAllowed(Authorization.ORDER_CORRECTION_WEIGHTS_AND_QUANTITIES)) {
						setLoadedQuantity(request, orderItemBean, orderItemEntity);
						setTareAndNetWeights(request, orderItemBean, orderItemEntity, order);
					}
			
					if (Authorization.isAllowed(Authorization.CREDIT_NOTE_CREATE)) {
						
						ReturnQuantHandlingBsEntity returnQuantHandling = orderItemEntity.getReturnQuantHandling();
						
						//if Credit Note is set 
						if (!orderItemBean.getIsCreditNoteDisabled()) {
							
							long handlingCd = Constant.RETURN_QUANT_HANDLING_NOT_SET;
							
							if (returnQuantHandling != null) {
								handlingCd = returnQuantHandling.getReturnQuantHandlingCd();
							}
							
							if (orderItemBean.getCreditNoteFlag()) {
								//do not need to save CN because value did not change
								if (handlingCd != Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_NEEDED) {
									//change Credit Note status
									RefDataGenericsService refDataService = ServiceLocatorProvider.getServiceLocator().getRefDataGenericsService();
									ReturnQuantHandlingBsEntity ret = refDataService.findBsEntity(ReturnQuantHandlingBsEntity.class, Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_NEEDED);
									orderItemEntity.setReturnQuantHandling(ret);
								}
								
								//check return quantity. It cannot be null!
								if (StringUtils.empty(orderItemBean.getReturnedQuantity())) {
									MessageUtils.addActionError(request, "orderdetail.mat.errors.required.returned.quantity");
								}
								else {
									orderItemEntity = changeReturnedQuantity(request, i, orderItemBean, orderItemEntity);
								}
							}
							else if (handlingCd == Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_NEEDED) {

								//change Credit Note status
								RefDataGenericsService refDataService = ServiceLocatorProvider.getServiceLocator().getRefDataGenericsService();
								ReturnQuantHandlingBsEntity ret = refDataService.findBsEntity(
										ReturnQuantHandlingBsEntity.class,
										Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT);
								orderItemEntity.setReturnQuantHandling(ret);

								orderItemEntity.setReturnedQuantity(null);
								orderItemEntity.setReturnedUom(null);
								orderItemEntity.setReturnOrderReason(returnOrderReasonRailUnloadingProblem);
							}
						}
					}
				}
			
				
				//------delivered quantity------//
				String deliveredQuantityString = orderItemBean.getDeliveredQuantity();
				
				if (StringUtils.notEmpty(deliveredQuantityString)) {
					try {
						// value from the UI, not null
						BigDecimal uiDeliveredQuantity = new BigDecimal(deliveredQuantityString);

						orderItemEntity.setDeliveredQuantity(uiDeliveredQuantity);
						orderItemEntity = changeReturnedQuantity(request, i, orderItemBean,
								orderItemEntity);

					}
					catch (NumberFormatException e) {
						MessageUtils.addActionError(request,
								"orderdetail.mat.errors.format.delivered.quantity");
					}

					//delivered uom
					final String deliveredUom = orderItemBean.getDeliveredUom();
					if (StringUtils.empty(deliveredUom)) {
						MessageUtils.addActionError(request, "orderdetail.mat.errors.required.delivered.oum");
					}
					else {
						Map <String, UomTxEntity> uomMap = orderItemBean.getUomMap();
						final UomTxEntity uomTxEntity = uomMap.get(deliveredUom);
						if (uomTxEntity == null ) {
							MessageUtils.addActionError(request, "orderdetail.mat.errors.required.delivered.oum");
						}
						else {
							orderItemEntity.setDeliveredUom(uomTxEntity.getUomTxPK().getUomBs());
						}
					}
				}
				else {
					orderItemEntity.setDeliveredQuantity(null);
					orderItemEntity.setDeliveredUom(null);
				}
				
				
				//other order item fields 
				if (getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_W)) {
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_W);
					
					//production plant
					// for services it should be possible later on to be able to set it to empty. !orderItemBean.isServiceMaterial() && 
					if ( !Constant.INVALID_OBJECT_ID_STR.equals(orderItemBean.getPlantCd())) {
						for (PlantBsEntity plantBs : (List<PlantBsEntity>) orderItemBean.getProdPlantList()) {
							if (orderItemBean.getPlantCd().equals(plantBs.getPlantCd())) {
								orderItemEntity.setProductionPlant(plantBs);
								break;
							}
						}
					}
					Long orderItemId = orderItemEntity.getOrderItemId();
					SapMaterialEntity sapMaterial = orderItemEntity.getSapMaterial();
					if (orderItemId != null &&
							orderItemEntity.getProductionPlant() == null &&
							!LogonUtil.isPlanningMaterial(sapMaterial)) {
						MessageUtils.addActionError(request, "orderdetail.main.errors.required.prodplant");
					}
					
					// loading point
					if ( (orderItemEntity.getLoad() == null) || (orderItemEntity.getLoad().getLoadingpoint() == null) ) {
						// In order detail, you can only edit the 'planned' LP on the order item. This only makes sense to edit when the actual LP is not set yet.
						if ( (orderItemEntity.getLoad() != null) && ((orderItemBean.getLoadingpointId() == null) || ((orderItemBean.getLoadingpointId() != null) && (Constant.INVALID_OBJECT_ID == orderItemBean.getLoadingpointId()))) ) {
							orderItemEntity.setLoadingpoint(null);
						} 
						else {
							orderItemEntity.setLoadingpoint((LoadingpointEntity) ldPointMap.get(orderItemBean.getLoadingpointId()));
						}
					}
					
					// Agg/Rmx code
					if (orderItemBean.getMixDesignId() != null) {
						OrderDetailService orderDetailService = ServiceLocatorProvider.getServiceLocator().getOrderDetailService();
						FireqMaterialMixdesignEntity mixDesign = orderDetailService.fetchMixDesignById(orderItemBean.getMixDesignId());
						orderItemEntity.setMixDesign(mixDesign);
					}
				}
			}
			
			if (totalPalletCount.compareTo(new BigDecimal("21")) > 0) {
				MessageUtils.addActionWarning(request, "orderdetail.mat.warning.too.many.pallets");
			}
			
			////-------soldto-------//
			if (getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_ONETIMESOLDTO_ADDR)
					&& isOneTimeSoldtoEditable()) {
				boolean error = false;
				
				if (StringUtils.empty(materialBean.getSoldtoName())) {
					MessageUtils.addActionError(request, "orderdetail.mat.errors.onetimesoldto.name.required");
					error = true;
				}
				if (StringUtils.empty(materialBean.getSoldtoPostalCode())) {
					MessageUtils.addActionError(request, "orderdetail.mat.errors.onetimesoldto.postalcode.required");
					error = true;
				}
				if (StringUtils.empty(materialBean.getSoldtoCity())) {
					MessageUtils.addActionError(request, "orderdetail.mat.errors.onetimesoldto.city.required");
					error = true;
				}
				
				if (!error) {
					order.setOneTimeSoldtoName(materialBean.getSoldtoName());
					order.setOneTimeSoldtoStreetHouseno(materialBean.getSoldToStreetHouseNumber());
					order.setOneTimeSoldtoCity(materialBean.getSoldtoCity());
					order.setOneTimeSoldtoPostalCode(materialBean.getSoldtoPostalCode());
					order.setOneTimeSoldtoCountry(materialBean.getSoldtoCountry());
					order.setOneTimeSoldtoVatNo(materialBean.getSoldToVatNumber());
				}
			}		
			
			////-----weborder on-hold functionality-----//// 
			if (getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_ON_HOLD_W)) {
							
				Authorization.authorize(Authorization.ORDER_DETAIL_ON_HOLD_FUNCTIONALITY);
							
				order.setOnHoldOrderAndLockedFlag(orderBean.getOnHoldOrderFlag());
			}
			
			////-------billto-------//
			if (getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_BILLTO_PARTY)) {
				
				Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_BILLTO_PARTY);
				// TODO Jul 16, 2015/timre: this flag should be make editable with introduce a new Func and assign it to Role Customer: LOGON-9445 
				//set this flag as it is because there is already logic to display it.
				
				ShiptoBilltoBean shiptoBilltoBean = billtoMap.get(materialBean.getBilltoSapBpId());
				if (shiptoBilltoBean != null) {
					order.setBilltoSapBp(shiptoBilltoBean.getBilltoSapBpEntity());
				}
				else {
					MessageUtils.addActionError(request, "orderspecial.errors.billto.required");
					order.setBilltoSapBp(null);
				}
				
			}
			
			////-------payer-------//
			if (getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_PAYER_PARTY)) {
				Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_PAYER_PARTY);
				
				String payerIdStr = materialBean.getPayerId();
				PayerSapBpBean payerSapBpBean = null;
			
				if (StringUtils.notEmpty(payerIdStr)) {
					Long payerId = Long.valueOf(payerIdStr);
					payerSapBpBean = payerMap.get(payerId);
				}
				else if (!isPayerIgnored()) { // Payer is required when auto payer-determination flags are OFF!
					String payerFieldName = MessageUtils.getMessage(request, locale, "fields.payer.payer");
					MessageUtils.addActionError(request,"error.field.required", payerFieldName);
				}
				order.setPayerSapBp(payerSapBpBean != null ? payerSapBpBean.getSapBp() : null);
			}
			
			
			//-------Order Final-------//
			if ((orderFinal != null) && (reportingBean != null)) {
				if (StringUtils.nvl(reportingBean.getNoteCrh()).length() > OrderFinalEntity.DBSIZE_REPORT_NOTE_FROM_CRH) {
					MessageUtils.addActionError(request, "orderdetail.rep.errors.note.crh");
				}	
				else {
					orderFinal.setReportNoteFromCrh(reportingBean.getNoteCrh());
				}
				
				if (StringUtils.nvl(reportingBean.getNoteCarrier()).length() > OrderFinalEntity.DBSIZE_REPORT_NOTE_FROM_CARRIER) {
					MessageUtils.addActionError(request, "orderdetail.rep.errors.note.carrier");
				}	
				else {
					orderFinal.setReportNoteFromCarrier(reportingBean.getNoteCarrier());
				}
				
				orderFinal.setReportSvcLevelRelevantFlag(reportingBean.getRelevantServiceLevelFlag());
			}
			
			
			//-------Rep/Maint part-------//
			if ((repBean != null) && (repBean.getOrderRep() != null)) {
				
				OrderRepEntity orderRep = repBean.getOrderRep();
				orderRep.setOrderRepMaintTypes(repBean.getOrderRepMaintTypeSaveList());
				
				//email
				if (OrderDetailAction.FORWARD_SEND_EMAIL.equals(formAction)) {
					orderRep.setEmailLastSentDat(new Date());
				}
				
				if (StringUtils.nvl(repBean.getEmailTo()).length() > OrderRepEntity.DBSIZE_EMAIL_TO_ADRESSES) {
					MessageUtils.addActionError(request, "orderdetail.repair.errors.email.to.tooLong");
				}
				else {
					orderRep.setEmailToAdresses(repBean.getEmailTo());
				}
				
				if (StringUtils.nvl(repBean.getEmailCc()).length() > OrderRepEntity.DBSIZE_EMAIL_CC_ADRESSES) {
					MessageUtils.addActionError(request, "orderdetail.repair.errors.email.cc.tooLong");
				}
				else {
					orderRep.setEmailCcAdresses(repBean.getEmailCc());
				}
				
				if (StringUtils.nvl(repBean.getEmailBcc()).length() > OrderRepEntity.DBSIZE_EMAIL_BCC_ADRESSES) {
					MessageUtils.addActionError(request, "orderdetail.repair.errors.email.bcc.tooLong");
				}
				else {
					orderRep.setEmailBccAdresses(repBean.getEmailBcc());
				}
				
				if (StringUtils.nvl(repBean.getEmailText()).length() > OrderRepEntity.DBSIZE_EMAIL_ADDITIONAL_REMARKS) {
					MessageUtils.addActionError(request, "orderdetail.repair.errors.email.remarks.tooLong");
				}
				else {
					orderRep.setEmailAdditionalRemarks(repBean.getEmailText());
				}		
				
				
				//offer
				if (repBean.getOfferDesiredFlag() != null) {
					orderRep.setOfferDesiredFlag(repBean.getOfferDesiredFlag());
				}
				else {
					orderRep.setOfferDesiredFlag(false);
				}
				
				if ( !StringUtils.empty(repBean.getOfferAcceptDate())) {
					try {
						Date date= DateTimeParser.parseLogonDayMonthYear(locale, repBean.getOfferAcceptDate());
						Date dateTime = DateTimeUtils.parseHoursMinutes(repBean.getOfferAcceptTime(), date);
						orderRep.setOfferAcceptDat(dateTime);
					}
					catch (ParseException e) {
						MessageUtils.addActionError(request, "orderdetail.repair.errors.offer.accept.dat.format");
					}
				}
				else {
					orderRep.setOfferAcceptDat(null);
				}
				
				if ( !StringUtils.empty(repBean.getEnterPlantDate())) {					
					try {
						Date date= DateTimeParser.parseLogonDayMonthYear(locale, repBean.getEnterPlantDate());
						Date dateTime = DateTimeUtils.parseHoursMinutes(repBean.getEnterPlantTime(), date);
						orderRep.setEnterPlantDat(dateTime);
					}
					catch (ParseException e) {
						MessageUtils.addActionError(request, "orderdetail.repair.errors.enter.plant.dat.format");
					}
				}
				else {
					orderRep.setEnterPlantDat(null);
				}
				
				if ( !StringUtils.empty(repBean.getLeavePlantDate())) {
					
					try {
						Date date= DateTimeParser.parseLogonDayMonthYear(locale, repBean.getLeavePlantDate());
						Date dateTime = DateTimeUtils.parseHoursMinutes(repBean.getLeavePlantTime(), date);
						orderRep.setLeavePlantDat(dateTime);
					}
					catch (ParseException e) {
						MessageUtils.addActionError(request, "orderdetail.repair.errors.leave.plant.dat.format");
					}
				}
				else {
					orderRep.setLeavePlantDat(null);
				}
				
				if ( !StringUtils.empty(repBean.getOfferAmount())) {					
					try {
						BigDecimal amount = new BigDecimal(repBean.getOfferAmount());
						orderRep.setOfferAmount(amount);
						
						if (Constant.INVALID_OBJECT_ID_STR.equals(repBean.getOfferAmountCur())) {
							MessageUtils.addActionError(request, "orderdetail.repair.errors.offer.curr.required");
						}
						else {
							orderRep.setOfferCurrency((CurrencyBsEntity)repBean.getCurrencyBsEntityMap().get(repBean.getOfferAmountCur()));
						}
					} 
					catch (NumberFormatException e) {
						MessageUtils.addActionError(request, "orderdetail.repair.errors.offer.amount.format");
					}
				}
				else {
					orderRep.setOfferAmount(null);
					orderRep.setOfferCurrency(null);
				}
				
				
				//invoice
				if (repBean.getInvoicePaidFlag() != null) {
					orderRep.setInvoicePaidFlag(repBean.getInvoicePaidFlag());
				}
				else {
					orderRep.setInvoicePaidFlag(false);
				}
				
				if ( !StringUtils.empty(repBean.getInvoiceDate())) {										
					try {
						Date date= DateTimeParser.parseLogonDayMonthYear(locale, repBean.getInvoiceDate());
						Date dateTime = DateTimeUtils.parseHoursMinutes(repBean.getInvoiceTime(), date);
						orderRep.setInvoiceDat(dateTime);
					}
					catch (ParseException e) {
						MessageUtils.addActionError(request, "orderdetail.repair.errors.invoice.dat.format");
					}
				}
				else {
					orderRep.setInvoiceDat(null);
				}
				
				if ( !StringUtils.empty(repBean.getInvoiceNo())) {										
					try {
						Long no = Long.valueOf(repBean.getInvoiceNo());
						orderRep.setInvoiceNo(no);
					} 
					catch (NumberFormatException e) {
						MessageUtils.addActionError(request, "orderdetail.repair.errors.invoice.no.format");
					}
				}
				else {
					orderRep.setInvoiceNo(null);
				}
				
				if ( !StringUtils.empty(repBean.getInvoiceAmount())) {										
					try {
						BigDecimal amount = new BigDecimal(repBean.getInvoiceAmount());
						orderRep.setInvoiceAmount(amount);
						
						if (Constant.INVALID_OBJECT_ID_STR.equals(repBean.getInvoiceAmountCur())) {
							MessageUtils.addActionError(request, "orderdetail.repair.errors.invoice.amount.curr.required");
						}
						else {
							orderRep.setInvoiceCurrency((CurrencyBsEntity)repBean.getCurrencyBsEntityMap().get(repBean.getInvoiceAmountCur()));
						}
					} 
					catch (NumberFormatException e) {
						MessageUtils.addActionError(request, "orderdetail.repair.errors.invoice.amount.format");
					}
				}
				else {
					orderRep.setInvoiceAmount(null);
					orderRep.setInvoiceCurrency(null);
				}
				
				if (StringUtils.nvl(repBean.getInternalRemark()).length() > OrderRepEntity.DBSIZE_REMARKS_INTERNAL) {
					MessageUtils.addActionError(request, "orderdetail.repair.errors.internal.remark.tooLong");
				}
				else {
					orderRep.setRemarksInternal(repBean.getInternalRemark());
				}
				if (StringUtils.nvl(repBean.getDocumentationRemarks()).length() > OrderRepEntity.DBSIZE_DOCUMENTATION_REMARKS) {
					MessageUtils.addActionError(request, "orderdetail.repair.errors.documentation.remark.tooLong");
				}
				else {
					orderRep.setDocumentationRemarks(repBean.getDocumentationRemarks());
				}
				if (StringUtils.nvl(repBean.getOfferRemarks()).length() > OrderRepEntity.DBSIZE_OFFER_REMARKS) {
					MessageUtils.addActionError(request, "orderdetail.repair.errors.offer.remark.tooLong");
				}
				else {
					orderRep.setOfferRemarks(repBean.getOfferRemarks());
				}
				orderRep.setDocumentationAcceptFlag(repBean.getDocumentationAcceptFlag());
				orderRep.setOfferAcceptFlag(repBean.getOfferAcceptFlag());
				
			}
			
			//-------Services/Extra cost-------//
			if (servicesExtraCostBean != null) {
				
				if (servicesExtraCostBean.getExtraCostWaitingTimeMin() != null) {
					Long extraCostWaitingTimeMin = servicesExtraCostBean.getExtraCostWaitingTimeMin();
					
					order.setExtraCostWaitingTimeMin(extraCostWaitingTimeMin);
					
					//textfield returns '0' instead of 'null' in empty case
					if (extraCostWaitingTimeMin.equals(0L)) {
						order.setExtraCostWaitingTimeMin(null);
					}
				}
				
				List<SapOrderExtraCostBsEntity> activeExtraCostList = servicesExtraCostBean.getActiveExtraCostList();
				int size = activeExtraCostList.size();
				
				if (1 <= size) {
					order.setExtraCost1(activeExtraCostList.get(0));
					
					if (2 <= size) {
						order.setExtraCost2(activeExtraCostList.get(1));
						
						if (3 <= size) {
							order.setExtraCost3(activeExtraCostList.get(2));
							
							if (4 == size) {
								order.setExtraCost4(activeExtraCostList.get(3));
							}
							else {
								order.setExtraCost4(null);
							}
						}
						else {
							order.setExtraCost3(null);
							order.setExtraCost4(null);
						}
					}
					else {
						order.setExtraCost2(null);
						order.setExtraCost3(null);
						order.setExtraCost4(null);
					}
				}
				else {
					order.setExtraCost1(null);
					order.setExtraCost2(null);
					order.setExtraCost3(null);
					order.setExtraCost4(null);
				}
			}
			
			//EDIT LDS NUMBER
			if (getAuthParamList().contains(PARAM_ORDER_DETAIL_EDIT_LDS_NO)) {		
				Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_LDS_ORDERNO);
				order.setOrderNumberFromSeqUsage(orderBean.getOrderNumberFromSeqUsage());
			}
	}

	public void validateDeliveryWindow(HttpServletRequest request) {
			if (getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_DELIVERY_DATE_TIME)) {
				Authorization.authorize(Authorization.ORDER_DETAIL_DELIVERY_DATE_TIME);
				try {
			
					//parse delivery dates
					Date delivDateFrom = shiptoTZ.parseDayMonthYearHourMinute(
							orderBean.getDelivDateFrom(),
							orderBean.getDelivTimeFrom());
			
					Date delivDateTo = shiptoTZ.parseDayMonthYearHourMinute(
							orderBean.getDelivDateTo(),
							orderBean.getDelivTimeTo());
			
					if (delivDateFrom == null || delivDateTo == null) {
						MessageUtils.addActionError(request, "orderdetail.main.errors.wrong.date.format");
					}
					else if (delivDateFrom.compareTo(delivDateTo) > 0) {
						MessageUtils.addActionError(request, "orderdetail.main.errors.wrong.deliv.time");
					}
					else {

						Date origDeliveryToDate = order.getDeliveryToDat();
						Date origDeliveryFromDate = order.getDeliveryFromDat();
						order.setDeliveryFromDat(delivDateFrom);
						order.setDeliveryToDat(delivDateTo);
						if(delivDateTo.before(origDeliveryToDate)){
							try {
								validateLeadTimeCheck(order);
							} catch (ValidationException e) {
								order.setDeliveryFromDat(origDeliveryFromDate);
								order.setDeliveryToDat(origDeliveryToDate);
								throw e;
							}
						}
					}
					
				}
				catch (ParseException e) {
					MessageUtils.addActionError(request, "orderdetail.main.errors.wrong.time.format");
				}


				
			}
	}

	
	/**
	 * These checks should be made even the order has already been existed as user have right to chnage the order delivery from or/and to date
	 * @param order
	 * 
	 * @return
	 */
	public void validateLeadTimeCheck(OrderEntity order) throws ValidationException {
		
		if ((order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_ROAD_DIRECT) ||
			order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_BIMODAL_ROAD) ||
			order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_RETURN_DELIVERED)) &&
			!Authorization.isAllowed(Authorization.ORDER_DETAIL_OVERRULE_LEAD_TIME_CHECK))
		{
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			OrderService orderService = serviceLocator.getOrderService();
			orderService.validateLeadTimeCheck(order);
		}
	}
	
	/**
	 * Changes the returned quantity for the {@link OrderItemEntity} using in the {@link OrderItemBean} 
	 * @param request the {@link HttpServletRequest}
	 * @param i the index of the {@link OrderItemEntity} in the list of the {@link OrderEntity}
	 * @param orderItemBean {@link OrderItemBean}
	 * @param orderItemEntity the {@link OrderItemEntity}
	 * @return the changed {@link OrderItemEntity}
	 */
	private OrderItemEntity changeReturnedQuantity(HttpServletRequest request, int i,
			OrderItemBean orderItemBean, OrderItemEntity orderItemEntity) {
		
		
		//------returned quantity------//
		if (StringUtils.notEmpty(orderItemBean.getReturnedQuantity())) {
			orderItemEntity = applyReturnQuantity(request, orderItemBean, orderItemEntity);
			order.getOrderItems().set(i, orderItemEntity);
		}
		else {
			orderItemEntity.setReturnedQuantity(null);
			orderItemEntity.setReturnedUom(null);
		}
		return orderItemEntity;
	}

	/**
	 * Decide whether returned quantity is correct
	 * 
	 * Setup an error message if it is
	 * - not a number
	 * - zero
	 * - greater than the loaded and/or delivered quantity AND
	 *     + orderitem is neither pallet nor service OR
	 *     + CN flag is on OR
	 *     + some SAP document was already sent out
	 * 
	 * @param request
	 * @param orderItemBean
	 */
	private void validateReturnedQuantity(HttpServletRequest request, OrderItemBean orderItemBean,
			OrderItemEntity orderItem) {
		String returnedQuantityAsText = orderItemBean.getReturnedQuantity();
		BigDecimal returnedQuantity = BigDecimal.ZERO;
		try {
			if (returnedQuantityAsText != null) {
				returnedQuantity = new BigDecimal(returnedQuantityAsText);
			}
		}
		catch (NumberFormatException e) {
			MessageUtils.addActionError(request, "orderdetail.mat.errors.format.returned.quantity");
			return;
		}
		if (returnedQuantity.equals(BigDecimal.ZERO)) {
			MessageUtils.addActionError(request, "orderdetail.mat.errors.format.returned.quantity");
			return;
		}
		boolean noDocumentWasSentToSAP = !LogonUtil.isSentToSap(order);
		boolean isPallet = LogonPackageHandlingUtil.isPallet(orderItem);
		boolean isService = LogonPackageHandlingUtil.isService(orderItem);
		if ((isPallet || isService) && noDocumentWasSentToSAP) {
			return;
		}

		if (orderItem.getReturnQuantHandling() != null && Long.valueOf(Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT).equals(orderItem.getReturnQuantHandling().getReturnQuantHandlingCd()) &&
			!returnedQuantity.equals(orderItem.getReturnedQuantity())) {
			MessageUtils.addActionError(request, "orderdetail.mat.errors.return.change.not.allowed");
			return;
		}
		String loadedQuantityAsText = orderItemBean.getLoadedQuantity();
		BigDecimal loadedQuantity = null;
		try {
			if (loadedQuantityAsText != null) {
				loadedQuantity = new BigDecimal(loadedQuantityAsText);
			}
		}
		catch (NumberFormatException numberFormatException) {
			loadedQuantity = null;
		}
		if (loadedQuantity != null && returnedQuantity.compareTo(loadedQuantity) > EQUAL) {
			MessageUtils.addActionError(request,
					"orderdetail.mat.errors.returned.quantity.greater.than.loaded");
		}
	}

	/** Validate and set tare and net weights
	 * @param request
	 * @param orderItemBean
	 * @param orderItemEntity
	 * @param order
	 */
	private void setTareAndNetWeights(HttpServletRequest request, OrderItemBean orderItemBean,
			OrderItemEntity orderItemEntity, OrderEntity order) {
			if (orderItemBean.getTareNetWeightsAreNotNullFlag()
					&& orderItemBean.getOrderItemAndLoadRel11Flag()) {
				
				String tareWeightQuantity = orderItemBean.getTareWeightQuantity();
				String netWeightQuantity = orderItemBean.getNetWeightQuantity();
				String grossWeightQuantity = orderItemBean.getGrossWeightQuantity();

				//Tare and net are required if they were set before.
				if (StringUtils.empty(tareWeightQuantity)) {
					MessageUtils.addActionError(request, "orderdetail.mat.errors.required.tare");
				}
				if (StringUtils.empty(netWeightQuantity)) {
					MessageUtils.addActionError(request, "orderdetail.mat.errors.required.net");
				}	
				else {
					LoadEntity load = orderItemEntity.getLoad();

					//if both are not empty
					if (StringUtils.notEmpty(tareWeightQuantity) && load != null) {

						BigDecimal originalNetQuantity = load.getNetQuantity();
						BigDecimal originalTareWeightT = load.getTareWeightT();
						BigDecimal originalGrossWeightT = load.getGrossWeightT();

						BigDecimal newNetWeghtQuantity = null;
						BigDecimal newTareWeightQuantity = null;
						BigDecimal newGrossWeightQuantity = null;
						try {
							newNetWeghtQuantity = new BigDecimal(netWeightQuantity);
						}
						catch (NumberFormatException e) {
							MessageUtils.addActionError(request,
									"orderdetail.mat.errors.format.net.weight.quantity");
						}

						try {
							newTareWeightQuantity = new BigDecimal(tareWeightQuantity);
						}
						catch (NumberFormatException e) {
							MessageUtils.addActionError(request,
									"orderdetail.mat.errors.format.tare.weight.quantity");
						}

						try {
							newGrossWeightQuantity = StringUtils.empty(grossWeightQuantity) ? null : new BigDecimal(grossWeightQuantity);
						}
						catch (NumberFormatException e) {
							MessageUtils.addActionError(request,
									"orderdetail.mat.errors.format.gross.weight.quantity");
						}

						// do not process further if the order is already 'Finished' and the values was not changed by the user
						if (Constant.ORDER_STATUS_FINISHED == order.getOrderStatus().getOrderStatusCd().longValue() &&
								isValueNotChanged(originalNetQuantity, newNetWeghtQuantity) &&
								isValueNotChanged(originalTareWeightT, newTareWeightQuantity) &&
								isValueNotChanged(originalGrossWeightT, newGrossWeightQuantity)){
							return;
						}

						load.setNetQuantity(newNetWeghtQuantity);
						load.setTareWeightT(newTareWeightQuantity);

						ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
						OrderService orderService = serviceLocator.getOrderService();
						
						BigDecimal grossWeightT = orderService.getUpdatedGrossWeightT(load, null);
						load.setGrossWeightT(grossWeightT);
					}
				}
			}
	}

	private boolean isValueNotChanged(BigDecimal oldValue, BigDecimal newValue) {
		if ((oldValue == null && newValue != null) ||
				oldValue != null && newValue == null) {
			return false;
		}

		if (oldValue == null && newValue == null ){
			return true;
		}

		return oldValue.compareTo(newValue) == 0;
	}

	/** validate and set loaded quantity
	 * @param request
	 * @param orderItemBean
	 * @param orderItemEntity
	 */
	private void setLoadedQuantity(HttpServletRequest request, OrderItemBean orderItemBean,
			OrderItemEntity orderItemEntity) {
			// The '&& !orderItemBean.getOrderItemAndLoadRel11Flag()' part has been removed, see LOGON-6268
			if (orderItemBean.getTareNetWeightsAreNotNullFlag()) {
			
				if (StringUtils.notEmpty(orderItemBean.getLoadedQuantity())) {
					try {
						orderItemEntity.setLoadedQuantity(new BigDecimal(
								orderItemBean.getLoadedQuantity()));
					}
					catch (NumberFormatException e) {
						MessageUtils.addActionError(request, "orderdetail.mat.errors.format.loaded.quantity");
					}
					
					//loaded uom
					final String beanLoadedUom = orderItemBean.getLoadedUom();
					if (Constant.INVALID_OBJECT_ID_STR.equals(beanLoadedUom)) {
						MessageUtils.addActionError(request, "orderdetail.mat.errors.required.loaded.oum");
					}
					else {
						boolean isLoadedQuantityDisabled = true;
						if (materialBean.getSentToSapIsNullFlag()) {
							if (orderItemBean.getTareNetWeightsAreNotNullFlag() 
									&& !orderItemBean.getOrderItemAndLoadRel11Flag()) {
								isLoadedQuantityDisabled = false;
							}
						}
						else {
							if (orderItemBean.getTareNetWeightsAreNotNullFlag() 
											&& !orderItemBean.getOrderItemAndLoadRel11Flag()) { 
								isLoadedQuantityDisabled = false;
							}
						}
						
						if (!isLoadedQuantityDisabled) {
							Map <String, UomTxEntity> uomMap = orderItemBean.getUomMap();
							final UomTxEntity uomTxEntity = uomMap.get(beanLoadedUom);
							if (uomTxEntity == null ) {
								MessageUtils.addActionError(request, "orderdetail.mat.errors.required.loaded.oum");							
							}
							else {
								orderItemEntity.setLoadedUom(uomTxEntity.getUomTxPK().getUomBs());
							}
						}
					}
				}
				else {
					orderItemEntity.setLoadedQuantity(null);
					orderItemEntity.setLoadedUom(null);
				}
			}
	}


	/**
	 * Converts the returned quantity {@link String} from {@link OrderItemBean} to {@link BigDecimal}. If null, empty, or invalid number, returns null.
	 * @param orderItemBean the {@link OrderItemBean}
	 * @return {@link BigDecimal} or null.
	 */
	private BigDecimal getReturnedQuantityFromUI(OrderItemBean orderItemBean) {
		BigDecimal returnedQuantity = null;
		try {
			String returnedQuantityString = orderItemBean.getReturnedQuantity();

			if (returnedQuantityString != null && returnedQuantityString.length() > 0) {

				returnedQuantity = new BigDecimal(returnedQuantityString);
			}
		}
		catch (NumberFormatException e) {
			// just return null
		}

		return returnedQuantity;

	}
	
	/**
	 * Changes the returned quantity, UoM, return reason, etc. for the given <i>orderItemEntity</i> {@link OrderItemEntity}. 
	 * The data should be prepared in the <i>orderItemBean</i> {@link OrderItemBean} in the proper format (quantity is stored in a {@link String}). 
	 * The method calls the {@link OrderDetailService#adjustReturnedOrderItemAttributes(OrderItemEntity, BigDecimal, UomBsEntity)}.
	 * @param request the {@link HttpServletRequest}
	 * @param orderItemBean the {@link OrderItemBean}
	 * @param orderItemEntity the {@link OrderItemEntity}
	 * @return the <i>orderItemEntity</i>
	 */
	private OrderItemEntity applyReturnQuantity(HttpServletRequest request, OrderItemBean orderItemBean,
			OrderItemEntity orderItemEntity) {
			BigDecimal returnedQuantity = null;
			try {
				returnedQuantity = new BigDecimal(orderItemBean.getReturnedQuantity());
			}
			catch (NumberFormatException e) {
				MessageUtils.addActionError(request, "orderdetail.mat.errors.format.returned.quantity");
			}
			
			//returned uom
			UomBsEntity returnedUom = null;
			if (Constant.INVALID_OBJECT_ID_STR.equals(orderItemBean.getReturnedUom())) {
				MessageUtils.addActionError(request, "orderdetail.mat.errors.required.returned.oum");
			}
			else {
				returnedUom = orderItemEntity.getLoadedUom();
			}
			
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			OrderDetailService orderDetailService = serviceLocator.getOrderDetailService();
			orderItemEntity = orderDetailService.adjustReturnedOrderItemAttributes(orderItemEntity, returnedQuantity, 
					returnedUom);
			
			
			//return reason
			ReturnOrderReasonBsEntity returnOrderReasonBsEntity = returnReasonMap.get(orderItemBean.getReturnReason());
			orderItemEntity.setReturnOrderReason(returnOrderReasonBsEntity);
			orderItemEntity.setReturnMaterialUsage(orderItemBean.getReturnMaterialUsage());
			orderItemEntity.setReturnPlant(orderItemBean.getReturnPlant());
			orderItemEntity.setReturnShippingpoint(orderItemBean.getReturnShippingpoint());
			
			return orderItemEntity;
	}

	/**
	 * Override struts method.
	 * Perform form-specific validation of input parameters.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
			ActionErrors errors = super.validate(mapping, request);
			
			if (errors == null) {
				errors = new ActionErrors();
			}
			
			// validation is done in validateOrderDetail method which is called from action because it is a popup screen  and we cannot use struts validation 
			return errors;
	}


	public void setAuthParams(long[] authParams) {
		this.authParamList = new ArrayList<Long>();
		if (authParams != null){
			List<Long> returnList = new ArrayList<Long>();
			for (int i=0; i< authParams.length; i++){
				returnList.add(authParams[i]);
			}
			this.authParamList = returnList;
		}
	}

	public List<Long> getAuthParamList() {
		return authParamList;
	}


	public ChangeBpBean getChangeBpBean() {
		return changeBpBean;
	}


	public void setChangeBpBean(ChangeBpBean changeBpBean) {
		this.changeBpBean = changeBpBean;
	}


	public OrderDetailUploadAttachmentBean getUploadBean() {
		return uploadBean;
	}


	public void setUploadBean(OrderDetailUploadAttachmentBean uploadBean) {
		this.uploadBean = uploadBean;
	}

	/**
	 * @return the allowedVehiclesCombo
	 */
	public String getAllowedVehiclesCombo() {
		return allowedVehiclesCombo;
	}

	/**
	 * @param allowedVehiclesCombo the allowedVehiclesCombo to set
	 */
	public void setAllowedVehiclesCombo(String allowedVehiclesCombo) {
		this.allowedVehiclesCombo = allowedVehiclesCombo;
	}

	public boolean isOneTimeSoldto() {
		if (order == null) {
			return false;
		}
		
		SapBpEntity soldtoSapBp = order.getSoldtoSapBp();
		return soldtoSapBp.getTreatAsOnetimeFlag().booleanValue();
	}

	public boolean isOneTimeSoldtoEditable() {
		if (!isOneTimeSoldto()) {
			return false;
		}
		
		// Must only be editable after the order is loaded.
		switch ((int) (long) order.getOrderStatus().getOrderStatusCd()) {
		case (int) Constant.ORDER_STATUS_LOADED:
		case (int) Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL:
		case (int) Constant.ORDER_STATUS_SHIPPING:
		case (int) Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT:
		case (int) Constant.ORDER_STATUS_UNLOADING:
		case (int) Constant.ORDER_STATUS_DELIVERED:
		case (int) Constant.ORDER_STATUS_FINISHED:
			break;
		default:
			return false;
		}
		
		return true;
	}
	
	// TODO 05-Dec-2012/stich: check whether used, yes=correct, no=remove
	public boolean isOrderFinished() {		
		return (Constant.ORDER_STATUS_FINISHED <= order.getOrderStatus().getOrderStatusCd());
	}

	public Map getCommCarrierMap() {
		return commCarrierMap;
	}

	public void setCommCarrierMap(Map commCarrierMap) {
		this.commCarrierMap = commCarrierMap;
	}
		
	public List<CarrierEntity> getExecCarrierList() {
		return execCarrierList;
	}

	public void setExecCarrierList(List<CarrierEntity> execCarrierList) {
		this.execCarrierList = execCarrierList;
	}

	public Map<String, ReturnOrderReasonBsEntity> getReturnReasonMap() {
		return returnReasonMap;
	}

	public void setReturnReasonMap(Map<String, ReturnOrderReasonBsEntity> returnReasonMap) {
		this.returnReasonMap = returnReasonMap;
	}

	public Long getSelectedOrderItemId() {
		return selectedOrderItemId;
	}

	public void setSelectedOrderItemId(Long selectedOrderItemId) {
		this.selectedOrderItemId = selectedOrderItemId;
	}

	public String getSelectedReturnReasonCd() {
		return selectedReturnReasonCd;
	}

	public void setSelectedReturnReasonCd(String selectedReturnReasonCd) {
		this.selectedReturnReasonCd = selectedReturnReasonCd;
	}

	public String getReturnReasonPopupTitle() {
		return returnReasonPopupTitle;
	}

	public void setReturnReasonPopupTitle(String returnReasonPopupTitle) {
		this.returnReasonPopupTitle = returnReasonPopupTitle;
	}

	public ReturnOrderReasonBsEntity getReturnOrderReasonRailUnloadingProblem() {
		return returnOrderReasonRailUnloadingProblem;
	}

	public void setReturnOrderReasonRailUnloadingProblem(
			ReturnOrderReasonBsEntity returnOrderReasonRailUnloadingProblem) {
		this.returnOrderReasonRailUnloadingProblem = returnOrderReasonRailUnloadingProblem;
	}

	public List getSpiList() {
		return spiList;
	}

	public void setSpiList(List spiList) {
		this.spiList = spiList;
	}

	public boolean isBilltoSameAsOneTimeSoldto() {
		return billtoSameAsOneTimeSoldto;
	}

	public void setBilltoSameAsOneTimeSoldto(boolean billtoSameAsOneTimeSoldto) {
		this.billtoSameAsOneTimeSoldto = billtoSameAsOneTimeSoldto;
	}

	public boolean isPayerSameAsOneTimeSoldto() {
		return payerSameAsOneTimeSoldto;
	}

	public void setPayerSameAsOneTimeSoldto(boolean payerSameAsOneTimeSoldto) {
		this.payerSameAsOneTimeSoldto = payerSameAsOneTimeSoldto;
	}

	public ServicesExtraCostTabBean getServicesExtraCostBean() {
		return servicesExtraCostBean;
	}

	public void setServicesExtraCostBean(ServicesExtraCostTabBean servicesExtraCostBean) {
		this.servicesExtraCostBean = servicesExtraCostBean;
	}
	
	public Boolean isLastUploadFailed() {
		return lastUploadFailed;
	}

	public void setLastUploadFailed(Boolean lastUploadFailed) {
		this.lastUploadFailed = lastUploadFailed;
	}

	public String getLastUnsupportedFileExtension() {
		return lastUnsupportedFileExtension;
	}

	public void setLastUnsupportedFileExtension(String lastUnsupportedFileExtension) {
		this.lastUnsupportedFileExtension = lastUnsupportedFileExtension;
	}

	public Boolean getAttachmentListChanged() {
		return attachmentListChanged;
	}

	public void setAttachmentListChanged(Boolean dmgOrAttachmentListChanged) {
		this.attachmentListChanged = dmgOrAttachmentListChanged;
	}

	public boolean isMaterialTabOpened() {
		return materialTabOpened;
	}

	public void setMaterialTabOpened(boolean materialTabOpened) {
		this.materialTabOpened = materialTabOpened;
	}

	public DeliverySettingSalesAreaMap getSalesAreaMap() {
		return salesAreaMap;
	}

	public void setSalesAreaMap(DeliverySettingSalesAreaMap salesAreaMap) {
		this.salesAreaMap = salesAreaMap;
	}

	/**
	 * Returns <tt>true</tt> if the order delivery type needs to show the
	 * comm/exec carrier information, otherwise <tt>false</tt>. The delivery types that needs 
	 * carrier info are defined by the set {@link Constant#ORD_DET_CARRIER_INFO_SET}.
	 * 
	 * @return
	 */
	public OrderItemBean getFirstMaterialItemBean(){
		for (OrderItemBean bean : materialBean.getOrderItemList()) {
			if (!LogonPackageHandlingUtil.isService(bean.getLogonPackageTypeCd())) {
				return bean;
			}
		}
		return null;
	}

	public List<TruckTypeWrapper> getTruckTypeWrappers() {
		return truckTypeWrappers;
	}

	public void setTruckTypeWrappers(List<TruckTypeWrapper> truckTypeWrappers) {
		this.truckTypeWrappers = truckTypeWrappers;
	}
	
	public String getAllowedVehicleTypesJSON()
	{
		return JsonUtils.getJsonSerializer().serialize(getTruckTypeWrappers());
	}
	
	public boolean getDisplayCarrierInfo() {
		if (this.order == null || this.order.getDeliveryType() == null) {
			LOG.warn("The order, or the orders delivery type is not defined!");
			
			return false;
		}
			
		Long delTypeCd = this.order.getDeliveryType().getDeliveryTypeCd();
		return Constant.ORD_DET_CARRIER_INFO_SET.contains(delTypeCd);
	}

	/**
	 * Returns the codes of the truck types enabled in the order.  
	 */
	public List<Long> truckTypeCodesEnabledInOrder()
	{
			if (order.getOrderVehicleTypes() == null)
			{
				// For graceful failing.
				LOG.warn("No list of vehicle types found in order '{}'.", new Object[] { order });
				return new LinkedList<Long>();
			}
			else
			{
				@SuppressWarnings("unchecked")
				final List<OrderTruckTypeEntity> orderTruckTypes = (List<OrderTruckTypeEntity>) order.getOrderVehicleTypes();
				final List<Long> typeCodes = new LinkedList<Long>();
				
				for (OrderTruckTypeEntity v : orderTruckTypes)
				{
					typeCodes.add(v.getTruckType().getTruckTypeCd());
				}
				
				return typeCodes;
			}
	}
	
	public String getTruckTypeCodesEnabledInOrderJSON()
	{
		return JsonUtils.getJsonSerializer().serialize(truckTypeCodesEnabledInOrder());
	}

	public boolean isPayerIgnored() {
		return payerIgnored;
	}

	public void setPayerIgnored(boolean payerIgnored) {
		this.payerIgnored = payerIgnored;
	}

	public void setShiptoTimeZone(TimeZone shiptoTZ) {
		this.shiptoTZ = shiptoTZ;
	}

	public TimeZone getShiptoTZ() {
		return shiptoTZ;
	}

	public boolean getDisplayShipToUTCOffset() {
		return !shiptoTZ.isSame(UserProvider.getTimezone());
	}

	public String getShiptoTimeZoneAsUTCOffset() {
		return shiptoTZ.fmtAsUTCOffset(orderBean.getDelivDateFromDat());
	}
	
	/**
	 * Returns the Customer Order's Delivery Window From Date as Month/Day/Year String for Ext.form.DatePicker.
	 * 
	 * @return the Customer Order's Delivery Window From Date as Month/Day/Year String for Ext.form.DatePicker
	 */
	public String getCustomerOrderDeliveryWindowFromDateAsMDY() {
		CustorderEntity custorder = order.getCustorder();
		String result = "";
		if (custorder != null) {
			result = UserProvider.getTimezone().fmtAsDayMonthYear(
					custorder.getDeliveryFromDat());
		}
		return result;
	}
	
	/**
	 * Returns the Customer Order's Delivery Window From Date as Month/Day/Year String for Ext.form.DatePicker.
	 * 
	 * @return the Customer Order's Delivery Window From Date as Month/Day/Year String for Ext.form.DatePicker
	 */
	public String getCustomerOrderDeliveryWindowToDateAsMDY() {
		CustorderEntity custorder = order.getCustorder();
		String result = "";
		if (custorder != null) {
			result = UserProvider.getTimezone().fmtAsDayMonthYear(
					custorder.getDeliveryToDat());
		}
		return result;
	}

	/**
	 * Returns true, if the customer order's delivery window start date and end date is on the same day.
	 * @return true, if the customer order's delivery window start date and end date is on the same day.
	 */
	private boolean isSingleDayCustomerOrderDeliveryWindow() {
		CustorderEntity custorder = order.getCustorder();
		Boolean result = false; 
		if(custorder != null){
			Date fromDate = custorder.getDeliveryFromDat();
			Date toDate = custorder.getDeliveryToDat();
			result = DateUtils.isSameDay(fromDate, toDate);
		}
		return result;
	}
	
	/**
	 * Returns the minimum value for the Order's delivery time interval for the time drop-down in the Order Detail Screen.
	 * 
	 * When the delivery date window for the customer order is a one-day interval, the time minimum value will be the start time of the delivery window.
	 * Otherwise the minimum value will be an empty String.
	 * 
	 * @return the minimum value for the Order's delivery time interval for the time drop-down in the Order Detail Screen
	 */
	public String getOrderDeliveryTimeMinValue() {
		String result = "";
		if (isSingleDayCustomerOrderDeliveryWindow()) {
			Date deliveryFrom = order.getCustorder().getDeliveryFromDat();
			result = UserProvider.getTimezone().fmtAsHourMinute(deliveryFrom);
		}
		return result;
	}
	
	/**
	 * Returns the maximum value for the Order's delivery time interval for the time drop-down in the Order Detail Screen.
	 * 
	 * When the delivery date window for the customer order is a one-day interval, the time maximum value will be the start time of the delivery window.
	 * Otherwise the maximum value will be an empty String.
	 * 
	 * @return the maximum value for the Order's delivery time interval for the time drop-down in the Order Detail Screen
	 */
	public String getOrderDeliveryTimeMaxValue() {
		String result = "";
		if (isSingleDayCustomerOrderDeliveryWindow()) {
			Date deliveryTo = order.getCustorder().getDeliveryToDat();
			result = UserProvider.getTimezone().fmtAsHourMinute(deliveryTo);
		}
		return result;
	}
	
	/**
	 * Returns a {@link String} containing the Customer Order's Delivery window From and To date/time String concatenated with a semicolon character ";".
	 * Format: "yyyy,mm,dd,HH,MM;yyyy,mm,dd,HH,MM" 
	 * @return {@link String} containing the Customer Order's Delivery window From and To date/time String concatenated with a semicolon character ";".
	 */
	public String getCustomerOrderDeliveryWindowDateTimeFromToString() {
		CustorderEntity custorder = order.getCustorder();
		String result = "";
		if (custorder != null) {
			String fromDate = UserProvider.getTimezone().fmtAsYearMonthDayHourMinute(
					custorder.getDeliveryFromDat());
			String toDate = UserProvider.getTimezone().fmtAsYearMonthDayHourMinute(
					custorder.getDeliveryToDat());
			result = fromDate + ";" + toDate;
		}
		return result;
	}

	public String getDistanceValidationPopUpText() {
		return distanceValidationPopUpText;
	}

	public void setDistanceValidationPopUpText(String distanceValidationPopUpText) {
		this.distanceValidationPopUpText = distanceValidationPopUpText;
	}

	public ShiptoSareaEntity getShiptoSarea() {
		return shiptoSarea;
	}

	public void setShiptoSarea(ShiptoSareaEntity shiptoSarea) {
		this.shiptoSarea = shiptoSarea;
	}
}
