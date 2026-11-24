/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static java.util.stream.Collectors.toSet;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import javax.mail.internet.MimeBodyPart;
import javax.persistence.NonUniqueResultException;
import javax.print.PrintException;

import ch.nevis.gdi.jpa.DirectJpaDao;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.containers.AssignmentPartContainer;
import com.crh.logon.business.factories.AssignmentPartFactory;
import com.crh.logon.business.kafka.KafkaPublisherService;
import com.crh.logon.business.ordercreation.AutoaddMaterialHelper;
import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.InvalidMapSupport;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.OrderDeliveryMinMaxDateDTO;
import com.crh.logon.business.ordercreation.dto.OrderItemDeliverySetting;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.beans.ConfigurableSequenceResult;
import com.crh.logon.business.services.beans.GoogleMapsDistanceMatrixResultBean;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.beans.pdf.PdfGeneratingResult;
import com.crh.logon.business.services.dispatchers.OrderServiceDispatcher;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.interfaces.GoogleMapsService;
import com.crh.logon.business.services.interfaces.OrderArchivingService;
import com.crh.logon.business.services.localinterfaces.AssignmentServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.BizeventAuditServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigurableSequencesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ContributionCalcServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CustorderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliveryDateServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliverySettingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.EdifactExportServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.EmailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.FaxAndSmsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.FrozenTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GoodsMovementServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LDSServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.NotificationServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderChangeServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PricingCallServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.QuantityRestrictionServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RailSchedulingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SapOutgoingDataServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SimpleVehicleSchedulingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentStatusUtil;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.business.services.utils.ConfigInitializer;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.DefaultFallbackLogic;
import com.crh.logon.business.services.utils.DeliveryDistanceValidationUtils;
import com.crh.logon.business.services.utils.DistanceValidationInfoBean;
import com.crh.logon.business.services.utils.InvoiceBean;
import com.crh.logon.business.services.utils.LangUtil;
import com.crh.logon.business.services.utils.LeadTimeValidationBaseBean;
import com.crh.logon.business.services.utils.LogonInvalidDurationException;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonPrintException;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.MailPropertiesConfiguration;
import com.crh.logon.business.services.utils.MaintenanceUtil;
import com.crh.logon.business.services.utils.OrderSpecialUtils;
import com.crh.logon.business.services.utils.OrderTruckTypeComparator;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.QuantityRestrictionCalculation;
import com.crh.logon.business.services.utils.QuantityRestrictionError;
import com.crh.logon.business.services.utils.SapMaterialUtil;
import com.crh.logon.business.services.utils.SchedulingUtils;
import com.crh.logon.business.services.utils.ShiptoSiloVerificationWrapper;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil;
import com.crh.logon.business.services.utils.UOMConverter;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.business.stores.domain.OrderStoreInterface;
import com.crh.logon.business.stores.sap.AlternativeUnitOfMeasurementInterface;
import com.crh.logon.business.stores.sap.ContractItemStoreInterface;
import com.crh.logon.business.stores.sap.ShiptoSareaStoreInterface;
import com.crh.logon.business.stores.system.UserStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.constants.CustorderLifecycleReturnCode;
import com.crh.logon.constants.EventSource;
import com.crh.logon.constants.ITrackStatus;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.CreditNoteEntity;
import com.crh.logon.persistence.entities.common.CurrencyExchRateEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.CustorderOutputdocEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.ExtMaterialEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.ExtSiteEntity;
import com.crh.logon.persistence.entities.common.FreightCostEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MaintRhythmDependEntity;
import com.crh.logon.persistence.entities.common.MaintRhythmEntity;
import com.crh.logon.persistence.entities.common.MatShippingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemContractItemEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderOutputdocEntity;
import com.crh.logon.persistence.entities.common.OrderPrescheduleEntity;
import com.crh.logon.persistence.entities.common.OrderRepAttEntity;
import com.crh.logon.persistence.entities.common.OrderRepDmgcodeTypeEntity;
import com.crh.logon.persistence.entities.common.OrderRepEntity;
import com.crh.logon.persistence.entities.common.OrderRepMaintTypeEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.PlantCompcodeEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.PricingCallEntity;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.ShiptoWagonTypeEntity;
import com.crh.logon.persistence.entities.common.SignatureEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TrafficJamEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.TruckRouteEntity;
import com.crh.logon.persistence.entities.common.WagonDmgcodeTypeEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.common.WagonMaintEntity;
import com.crh.logon.persistence.entities.common.WagonRepAttEntity;
import com.crh.logon.persistence.entities.frozen.OrderFinalEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignPartStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignTrStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.CustorderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DailyOrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DamagecodeTxEntity;
import com.crh.logon.persistence.entities.refdata.DelivNotePrintTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestBsEntity;
import com.crh.logon.persistence.entities.refdata.ItrackStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.MaintRhythmTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MapExtraDnTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.PrinterBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnQuantHandlingBsEntity;
import com.crh.logon.persistence.entities.refdata.SapContractTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapTrspModeBsEntity;
import com.crh.logon.persistence.entities.refdata.SeqUsageBsEntity;
import com.crh.logon.persistence.entities.refdata.SiloVerifTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SorgTxEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialSareaEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.CommExecCarrierPfEntity;
import com.crh.logon.persistence.entities.views.DispoPlanningEntity;
import com.crh.logon.persistence.entities.views.OrderReadyToFinishEntity;
import com.crh.logon.persistence.entities.views.RailRepSoldtoShiptoEntity;
import com.crh.logon.persistence.entities.views.RailSpecialAssignmentEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.ShipmentdocAssignmentEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.persistence.utils.RefDataUtil;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.NumberUtils;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.WagonNumberUtils;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.webservices.clients.LogonToSap;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentDTO;
import com.crh.logon.webservices.clients.beans.saplogisticreport.SapLogisticReportDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentDocumentDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.TransferDeliveryDocumentDTO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTimeComparator;

/**
 * @author AdNovum Informatik AG
 */
@Named
@Stateless
@Interceptors(UserInfoInterceptor.class)
@AdnTrace
public class OrderServiceBean extends OrderServiceDispatcher implements OrderServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderServiceBean.class);

	private static final LoadingpointEntity NO_LOADINGPOINT = null;

	private static final boolean ORDER_INITIALIZE_ONLY = false;

	private static final boolean SEND_GOODS_MOVEMENTS = true;

	public static final boolean SEND_FAX = true;

	public static final boolean SEND_EMAIL = true;

	public static final boolean PRINT_PAPER = true;

	public static final boolean DO_NOT_SEND_FAX = false;

	public static final boolean DO_NOT_SEND_EMAIL = false;

	@EJB
	OrderServiceLocalInterface orderService;

	@EJB
	GoodsMovementServiceLocalInterface goodsMovementService;

	@EJB
	AlternativeUnitOfMeasurementInterface sapMaterialAlternativeUnitOfMeasurementStore;

	@EJB
	DeliverySettingServiceLocalInterface deliverySettingService;

	@EJB
	OrderStoreInterface orderStore;

	@EJB
	UserStoreInterface userStore;

	@EJB
	ContractItemStoreInterface sapContractItemStore;

	@EJB
	PricingCallServiceLocalInterface pricingCallService;

	@EJB
	ConfigurableSequencesServiceLocalInterface configurableSequencesService;

	@EJB
	EdifactExportServiceLocalInterface edifactExportService;

	@EJB
	SapOutgoingDataServiceLocalInterface sapOutgoingDataService;

	@EJB
	QuantityRestrictionServiceLocalInterface quantityRestrictionService;

	@EJB
	UomConverterServiceLocalInterface unitOfMeasurementConverterService;

	@EJB
	RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB
	SimpleVehicleSchedulingServiceLocalInterface simpleVehicleSchedulingService;

	@EJB
	PDFGeneratorServiceLocalInterface pdfGeneratorService;

	@EJB
	RailSchedulingServiceLocalInterface railSchedulingService;

	@EJB
	BestellungServiceLocalInterface bestellungService;

	@EJB
	MonitoringTablesServiceLocalInterface monitoringTablesService;

	@EJB
	OrderDetailServiceLocalInterface orderDetailService;

	@EJB
	CustorderServiceLocalInterface customerOrderService;

	@EJB
	OrderChangeServiceLocalInterface orderChangeService;

	@EJB
	DispoServiceLocalInterface dispoService;

	@EJB
	LDSServiceLocalInterface ldsService;

	@EJB
	AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;

	@EJB
	FaxAndSmsServiceLocalInterface faxAndSmsService;

	@EJB
	AssignmentServiceLocalInterface assignmentService;

	@EJB
	ContributionCalcServiceLocalInterface contributionCalcService;

	@EJB
	DeliveryDateServiceLocalInterface deliveryDateService;

	@EJB
	DispokarteServiceLocalInterface dispokarteService;

	@EJB
	FrozenTablesServiceLocalInterface frozenTablesService;

	@EJB
	TimeZoneServiceLocalInterface timeZoneService;

	@EJB
	EmailServiceLocalInterface emailService;

	@EJB
	TruckAdminServiceLocalInterface truckAdminService;

	@EJB
	ConfigServiceLocalInterface configService;

	@EJB
	UserServiceLocalInterface userService;

	@EJB
	NotificationServiceLocalInterface notificationService;

	@EJB
	ShiptoSareaStoreInterface shiptoSareaStore;

	@EJB
	OrderArchivingService orderArchivingService;

	@EJB
	BizeventAuditServiceLocalInterface bizeventAuditService;

	@EJB
	KafkaPublisherService kafkaPublisherService;

	private static Map<Long, Set<Long>> stateTransitionMap = new HashMap<Long, Set<Long>>();
	private static Set<Long> canBeArchivingDeletable = new HashSet<>();

	private static Set<Long> assignmentShouldNotExist = new HashSet<>();

	private static final Set<Long> orderTakingWithoutRouteInformation = Stream.of(
			Constant.ORDER_CREATION_TYPE_EXTERNAL_SYSTEM,
			Constant.ORDER_CREATION_TYPE_OC_CUSTOMER_PORTAL,
			Constant.ORDER_CREATION_TYPE_OC_INTERFACE)
			.collect(toSet());

	static {
		final Long[] toNew = { Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK };

		final Long[] toTrPool = { Constant.ORDER_STATUS_NEW, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK };

		final Long[] toTrPoolAck = { Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK };

		final Long[] toTrCarrier = { Constant.ORDER_STATUS_NEW, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK };

		final Long[] toTrCarrierAck = { Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER, Constant.ORDER_STATUS_DISPATCHED,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,
				Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT, Constant.ORDER_STATUS_LOADING,
				Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_SHIPPING,
				Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT, Constant.ORDER_STATUS_UNLOADING,
				Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL };

		final Long[] toDispatched = { Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_SHIPPING,
				Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT, Constant.ORDER_STATUS_UNLOADING };

		final Long[] toTrDriver = { Constant.ORDER_STATUS_DISPATCHED, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_SHIPPING,
				Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT, Constant.ORDER_STATUS_UNLOADING };

		final Long[] toTrDriverAck = { Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT, Constant.ORDER_STATUS_LOADING,
				Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_SHIPPING,
				Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT, Constant.ORDER_STATUS_UNLOADING };

		final Long[] toArrLoad = { Constant.ORDER_STATUS_DISPATCHED, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_LOADING,
				Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_SHIPPING,
				Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT, Constant.ORDER_STATUS_UNLOADING };

		final Long[] toLoading = { Constant.ORDER_STATUS_DISPATCHED, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_SHIPPING,
				Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT, Constant.ORDER_STATUS_UNLOADING };

		final Long[] toLoaded = { Constant.ORDER_STATUS_DISPATCHED, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_SHIPPING,
				Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT, Constant.ORDER_STATUS_UNLOADING };

		final Long[] toTransmittedToRail = { Constant.ORDER_STATUS_LOADED };

		final Long[] toShipping = { Constant.ORDER_STATUS_DISPATCHED, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,
				Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT, Constant.ORDER_STATUS_UNLOADING,
				Constant.ORDER_STATUS_DELIVERED };

		final Long[] toArrUnload = { Constant.ORDER_STATUS_DISPATCHED, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,
				Constant.ORDER_STATUS_SHIPPING, Constant.ORDER_STATUS_UNLOADING };

		final Long[] toUnloading = { Constant.ORDER_STATUS_DISPATCHED, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,
				Constant.ORDER_STATUS_SHIPPING, Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT };

		final Long[] toDelivered = { Constant.ORDER_STATUS_DISPATCHED, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,
				Constant.ORDER_STATUS_SHIPPING, Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
				Constant.ORDER_STATUS_UNLOADING, Constant.ORDER_STATUS_DELIVERED_PENDING_LDS_DATA };

		final Long[] toDeliveredPendingLdsData = { Constant.ORDER_STATUS_DISPATCHED, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,
				Constant.ORDER_STATUS_SHIPPING, Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
				Constant.ORDER_STATUS_UNLOADING };

		final Long[] toFinished = { Constant.ORDER_STATUS_DELIVERED };

		final Long[] toDeleted = { Constant.ORDER_STATUS_NEW, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK };

		stateTransitionMap.put(Constant.ORDER_STATUS_NEW, new HashSet<Long>(Arrays.asList(toNew)));
		stateTransitionMap.put(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL, new HashSet<Long>(Arrays.asList(toTrPool)));
		stateTransitionMap.put(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK,
				new HashSet<Long>(Arrays.asList(toTrPoolAck)));
		stateTransitionMap.put(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				new HashSet<Long>(Arrays.asList(toTrCarrier)));
		stateTransitionMap.put(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,
				new HashSet<Long>(Arrays.asList(toTrCarrierAck)));
		stateTransitionMap.put(Constant.ORDER_STATUS_DISPATCHED, new HashSet<Long>(Arrays.asList(toDispatched)));
		stateTransitionMap.put(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
				new HashSet<Long>(Arrays.asList(toTrDriver)));
		stateTransitionMap.put(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,
				new HashSet<Long>(Arrays.asList(toTrDriverAck)));
		stateTransitionMap.put(Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				new HashSet<Long>(Arrays.asList(toArrLoad)));
		stateTransitionMap.put(Constant.ORDER_STATUS_LOADING, new HashSet<Long>(Arrays.asList(toLoading)));
		stateTransitionMap.put(Constant.ORDER_STATUS_LOADED, new HashSet<Long>(Arrays.asList(toLoaded)));
		stateTransitionMap.put(Constant.ORDER_STATUS_SHIPPING, new HashSet<Long>(Arrays.asList(toShipping)));
		stateTransitionMap.put(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
				new HashSet<Long>(Arrays.asList(toArrUnload)));
		stateTransitionMap.put(Constant.ORDER_STATUS_UNLOADING, new HashSet<Long>(Arrays.asList(toUnloading)));
		stateTransitionMap.put(Constant.ORDER_STATUS_DELIVERED, new HashSet<Long>(Arrays.asList(toDelivered)));
		stateTransitionMap.put(Constant.ORDER_STATUS_DELIVERED_PENDING_LDS_DATA, new HashSet<Long>(Arrays.asList(toDeliveredPendingLdsData)));
		stateTransitionMap.put(Constant.ORDER_STATUS_FINISHED, new HashSet<Long>(Arrays.asList(toFinished)));
		stateTransitionMap.put(Constant.ORDER_STATUS_DELETED, new HashSet<Long>(Arrays.asList(toDeleted)));
		stateTransitionMap.put(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,
				new HashSet<Long>(Arrays.asList(toTransmittedToRail)));

		canBeArchivingDeletable.add(Constant.ORDER_STATUS_NEW);
		canBeArchivingDeletable.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);
		canBeArchivingDeletable.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK);
		canBeArchivingDeletable.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
		canBeArchivingDeletable.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);
		canBeArchivingDeletable.add(Constant.ORDER_STATUS_DISPATCHED);
		canBeArchivingDeletable.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);
		canBeArchivingDeletable.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK);

		final Long[] assignmentShouldNotExistArray = { Constant.ORDER_STATUS_NEW, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK };

		assignmentShouldNotExist.addAll(Arrays.asList(assignmentShouldNotExistArray));

	}

	public List<OrderEntity> finalizeOrderCreation(final List<OrderEntity> orders) {
		/**
		 * This is a workaround, as certain authorization calls end up in
		 * flush calls, therefore the new order items which are not yet
		 * initialized has to be detached from the order in order that they
		 * do not trigger an insert which violates the not nullable field's
		 * contstraint. See also BUSINESS2635: Endload fails in case exact
		 * customer order quantity flag is on.
		 *
		 * Steps: detach new items, initialize, and add to the order.
		 *
		 * See also {@link OrderServiceBean#processOnMerge} At some point
		 * this problem needs to be addressed.
		 */

		List<SapContractItemEntity> sapContractItems = new ArrayList<SapContractItemEntity>();

		// as a first step, detach all new orders
		List<OrderEntity> newOrders = new ArrayList<OrderEntity>();
		final Iterator<OrderEntity> ordersIterator = orders.iterator();
		while (ordersIterator.hasNext()) {

			OrderEntity order = ordersIterator.next();
			List<OrderItemEntity> orderItems = order.getOrderItems();
			if (order.getOrderId() == null) {
				ordersIterator.remove();
				newOrders.add(order);
				if (orderItems != null) {
					for (OrderItemEntity orderItem : orderItems) {
						if (orderItem.getSapContractItem() != null) {
							sapContractItems.add(orderItem.getSapContractItem());
						}
					}
				}
			}
		}

		Map<Long, QuantityRestrictionCalculation> quantityRestrCalcMap = quantityRestrictionService.calculateOpenQuantityForContractItems(
				sapContractItems, Collections.<Long> emptyList());

		// then, initialize and persist
		final ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		OrderEntity previousOrder = null;
		DeliverySettingSalesAreaMap salesAreaMap = null;
		for (OrderEntity order : newOrders) {
			/**
			 * Will add a new material to the order, if it is required to be so.
			 */
			orderService.addAutoaddMaterial(order);

			if (areOrdersDifferent(previousOrder, order)) {
				salesAreaMap = initSalesAreaMapForOrderValidation(order, true);
			}
			finalInitOrder(order, salesAreaMap);
			validateOrder(order, salesAreaMap, quantityRestrCalcMap, false);
			previousOrder = orderDao.persist(order, true);
			if (kafkaPublisherService != null) {
				kafkaPublisherService.publishNewOrder(previousOrder);
			}
		}

		return newOrders;
	}

	public List<OrderEntity> finalizeNewOrderCreation(List<OrderEntity> orders,
			DeliverySettingSalesAreaMap salesAreaMap, boolean filterFutureContracts) {
		Map<Long, QuantityRestrictionCalculation> quantityRestrCalcMap = quantityRestrictionService.getQuantityRestrCalcMap(orders);

		// Process all delivery orders (calculate costs, etc...)
		DeliverySettingSalesAreaMap settingSalesAreaMap = salesAreaMap;
		for (OrderEntity order : orders) {

			/**
			 * Will add a new material to the order, if it is required to be so.
			 */
			orderService.addAutoaddMaterial(order);

			if (settingSalesAreaMap == null) {
				settingSalesAreaMap = initSalesAreaMapForOrderValidation(order, filterFutureContracts);
			}
			finalInitOrder(order, settingSalesAreaMap);
			validateOrder(order, settingSalesAreaMap, quantityRestrCalcMap, false);
		}

		return orders;
	}

	private boolean areOrdersDifferent(OrderEntity previousOrder, OrderEntity order) {
		if (previousOrder == null) {
			return true;
		}
		final SapBpEntity shiptoSapBpEntity = order.getShiptoSapBp();
		final SapBpEntity soldtoSapBp = order.getSoldtoSapBp();
		final SareaEntity sarea = order.getSarea();
		final SapContractEntity sapContract = order.getSapContract();

		final SapBpEntity previousShiptoSapBpEntity = previousOrder.getShiptoSapBp();
		final SapBpEntity previousSoldtoSapBp = previousOrder.getSoldtoSapBp();
		final SareaEntity previousSarea = previousOrder.getSarea();
		final SapContractEntity previousSapContract = previousOrder.getSapContract();

		return !(shiptoSapBpEntity.getSapBpId().equals(previousShiptoSapBpEntity.getSapBpId())
				&& soldtoSapBp.getSapBpId().equals(previousSoldtoSapBp.getSapBpId())
				&& sarea.getSareaId().equals(previousSarea.getSareaId()) && sapContract.getSapContractId().equals(
				previousSapContract.getSapContractId()));
	}

	public OrderEntity findOrderById(Long id) {
		return orderStore.findAndIntializeById(id);
	}

	/**
	 * Returns a list of orders with the given orderId.
	 */
	public List<OrderEntity> findOrdersByIds(Long[] ids) {
		List<OrderEntity> result = new ArrayList<OrderEntity>();
		if (ids == null) {
			return result;
		}
		List<Long> newOrderIdList = Arrays.asList(ids);
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		final Filter<OrderEntity> f = orderDao.createFilter();
		f.addExpression(Restrictions.in(OrderEntity.ATTR_ORDER_ID, newOrderIdList));
		return result = orderDao.find(f);
	}

	/**
	 * Fetches the order by its order number.
	 * @param orderNumber The order number to be queried for
	 * @param initialize Set this to true if the order and the rfidobjects on the order items'
	 * loading point and ship-to silos are to be initialized
	 */
	public OrderEntity findOrderByOrderNumber(Long orderNumber, boolean initialize) {
		final ReadOnlyDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		final Filter<OrderEntity> f = orderDao.createFilter();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));

		final OrderEntity order = orderDao.findSingle(f);
		if (order == null) {
			return order;
		}
		if (!initialize) {
			return order;
		}
		order.initialize();
		List<OrderItemEntity> orderItems = order.getOrderItems();
		if (orderItems == null) {
			return order;
		}
		for (OrderItemEntity orderItem : orderItems) {
			LoadingpointEntity loadingpoint = orderItem.getLoadingpoint();
			if (loadingpoint != null) {
				RfidobjectEntity rfidobject = loadingpoint.getRfidobject();
				if (rfidobject != null) {
					rfidobject.initialize();
				}
			}
			ShiptoSiloEntity shiptoSilo = orderItem.getShiptoSilo();
			if (shiptoSilo != null) {
				RfidobjectEntity rfidobject = shiptoSilo.getRfidobject();
				if (rfidobject != null) {
					rfidobject.initialize();
				}
			}
		}

		return order;
	}

	private void removeTemporaryData(OrderEntity order) {
		final ReadOnlyDao<OrderOutputdocEntity> dao = getDaoProvider().getOrderOutputdocEntityDao();
		final Filter<OrderOutputdocEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(OrderOutputdocEntity.ATTR_ORDER, order));

		final List<OrderOutputdocEntity> docList = dao.find(filter);
		for (OrderOutputdocEntity orderOutputdoc : docList) {
			final OutputdocEntity psDoc = orderOutputdoc.getPsOutputdoc();
			if (psDoc != null) {
				orderOutputdoc.setPsOutputdoc(null);
				getDaoProvider().getOutputdocEntityDao().remove(psDoc);
			}
		}
	}

	/**
	 * Deletes the order related to the event.
	 * mandatory fields:
	 * - order
	 */
	private void deleteOrder(BizeventEntity event) {
		OrderEntity order = event.getOrder();

		//first - in case this is the only order related to the assignment, call the normal remove assignment
		AssignmentPartEntity assignmentPartUnload = getAssignmentPartUnload(order);

		if (assignmentPartUnload != null) {
			OrderEntity removedOrder = removeOrderFromAssignment(assignmentPartUnload);
			AssignmentEntity assignment = assignmentPartUnload.getAssignment();
			if (removedOrder != null) {
				cancelOnWeighSystem(assignment, removedOrder.getShippingpoint());
				transmitToWeighSystem(assignment);
			}
		}

		discardOrderPreschedule(order);
		removeTemporaryData(order);

		Long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		if (canBeArchivingDeletable.contains(orderStatusCd)) {
			orderStore.markOrderAsDeletable(order.getOrderId());
		}
		changeOrderStatus(order, Constant.ORDER_STATUS_DELETED);
	}

	/**
	 * @param order
	 * @return An assignment part of type unload containing the order, if it exists
	 */
	public AssignmentPartEntity getAssignmentPartUnload(OrderEntity order) {
		final ReadOnlyDao<AssignmentPartEntity> apdao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> f = getAssignmentPartUnloadFilter(order);
		return apdao.findSingle(f);
	}

	private Filter<AssignmentPartEntity> getAssignmentPartUnloadFilter(OrderEntity order) {
		final Filter<AssignmentPartEntity> f = new FilterImpl<AssignmentPartEntity>();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, order.getOrderId()));
		return f;
	}

	/**
	 * Returns the unload part where the assignment is in status {@link Constant#ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER}
	 * @param order
	 * @return
	 */
	private AssignmentPartEntity getAssignmentPartUnloadPrescheduled(OrderEntity order) {
		final ReadOnlyDao<AssignmentPartEntity> apdao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> f = getAssignmentPartUnloadFilter(order);
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "."
				+ AssignmentEntity.ATTR_ASSIGN_STATUS_CD,
				AssignmentStatusUtil.ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER.getAssignStatusCd()));
		return apdao.findSingle(f);
	}

	/**
	 * @param load
	 * @return An assignment part of type load containing the order, if it exists
	 */
	public AssignmentPartEntity getAssignmentPartLoad(LoadEntity load) {
		ReadWriteDao<AssignmentPartEntity> apDao = getDaoProvider().getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> filter = apDao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD_ID, load.getLoadId()));
		AssignmentPartEntity loadPart = apDao.findSingle(filter);
		return loadPart;
	}

	public List<AssignmentPartEntity> getAssignmentPartTo(Long assignmentId) {
		ReadWriteDao<AssignmentPartEntity> apDao = getDaoProvider().getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> toPartFilter = new FilterImpl<AssignmentPartEntity>();
		toPartFilter.addExpression(Restrictions.and(
				Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, assignmentId),
				Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD, Constant.ASSIGNMENT_PART_TYPE_TO)));
		List<AssignmentPartEntity> toPartList = apDao.find(toPartFilter);
		return toPartList;
	}

	protected OrderStatusBsEntity findOrderStatusBs(Long statusCd) {
		OrderStatusBsEntity orderStatusBs = referentialDataService.findBsEntity(OrderStatusBsEntity.class, statusCd);
		return orderStatusBs;
	}

	/**
	 * Simply updates the order status to deleted.
	 * Only possible from order detail. The order will exist in SAP too will need manual correction.
	 * In case frozen table entry exists that will be updated with the order status deleted too.
	 * Nothing else should be changed, only the status.
	 */
	private OrderEntity deleteClosedOrder(Long orderId) {
		OrderEntity order = getDaoProvider().getOrderEntityDao().findById(orderId);

		//--- Step 1: update the status on the order ---//
		OrderStatusBsEntity newStatusRef = referentialDataService.findBsEntity(OrderStatusBsEntity.class,
				Constant.ORDER_STATUS_DELETED);
		order.setOrderStatus(newStatusRef);

		//--- Step 2: Check for a frozen entry and update appropriately ---//
		frozenTablesService.updateFrozenOrderStatus(order);

		//--- Step 3/Step 4: Update quantity controlled items ---//
		if (order.getSentSalesdocToSapTs() != null) {
			updateQuantityControlledOrderItems(order.getOrderItems());
		}

		setCustorderToFinishedAutomatically(order);

		removeTemporaryData(order);

		if (kafkaPublisherService != null) {
			kafkaPublisherService.publishCancelledOrder(order);
		}

		return order;
	}

	/**
	 * For quantity controlled contracts, update the corresponding entry in TLGNC_ORDER_ITEM_CONTRACT_ITEM.
	 * In addition, we adjust the TOTAL_SENT_QUANTITY cached value on the order item's contract item to
	 * reflect the the adjusted amount
	 *
	 * @precondition: this method is only called for orders which are already transmitted to SAP
	 */
	private void updateQuantityControlledOrderItems(List<OrderItemEntity> orderItems) {
		//--- Collect ids for 1 DB query ---//
		List<Long> orderItemIds = new ArrayList<Long>();
		for (OrderItemEntity orderItem : orderItems) {
			SapContractItemEntity contractItem = orderItem.getSapContractItem();
			if (contractItem != null && contractItem.getIsApGroupLeader()) {
				orderItemIds.add(orderItem.getOrderItemId());
			}
		}

		if (orderItemIds.isEmpty()) {
			LOG.info("No quantity controlled items to update");
			return;
		}

		//--- now load our order_item_contract_item entries ---//
		ReadWriteDao<OrderItemContractItemEntity> dao = getDaoProvider().getOrderItemContractItemEntityDao();

		Filter<OrderItemContractItemEntity> f = dao.createFilter();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(OrderItemContractItemEntity.ATTR_ORDER_ITEM_ID,
				orderItemIds));

		//--- Update our order contract item mappings ---//
		List<OrderItemContractItemEntity> items = dao.find(f);

		// since we can have the same order item mapping to multiple contract items,
		// we need to save the distinct contract item group leaders to update their
		// total sent quantity
		//
		// in order to simplify the code a bit, what we actually save in our map is
		// order-item.id => order-item. From the order item we have access to the
		// contract item group leader as well as the total delivered quantity.
		Map<Long, OrderItemEntity> distinctOrderItems = new HashMap<Long, OrderItemEntity>();
		for (OrderItemContractItemEntity item : items) {
			item.setDeletedFlag(Boolean.TRUE);

			OrderItemEntity orderItem = item.getOrderItem();
			distinctOrderItems.put(orderItem.getOrderItemId(), orderItem);
		}

		// now update the total sent quantity for our group leaders
		for (OrderItemEntity orderItem : distinctOrderItems.values()) {
			SapContractItemEntity groupLeader = orderItem.getSapContractItem();

			// adjust the total_sent_quantity
			BigDecimal deliveredQty = unitOfMeasurementConverterService.convertOrderItemQtyToContractItemUoM(orderItem,
					orderItem.getDeliveredQuantity(), orderItem.getDeliveredUom(), groupLeader);

			BigDecimal totalSentQty = groupLeader.getTotalSentQuantity();
			if (totalSentQty == null) {
				throw new LogonException(String.format(
						"No total_sent_quantity set on contract item %s for order item %s_%s. "
								+ "Precondition: This method is only called for FINISHED orders!",
						groupLeader.getSapContractItemId(), orderItem.getOrderItemId(), orderItem.getOrderItemNo()),
						Reason.RES_IMPL_BUG);
			}

			groupLeader.setTotalSentQuantity(totalSentQty.subtract(deliveredQty));
		}
	}

	private void publishOrderStatusChange(OrderEntity o) {
		if (kafkaPublisherService != null) {
			kafkaPublisherService.publishOrderStatusChange(o);
		}
	}

	/**
	 * Updates TLGNC_ORDER and TLGNC_ORDER_STATUS
	 */
	public void changeOrderStatus(OrderEntity order, Long statusCd) {
		if (order == null)
			throw new IllegalArgumentException("order cannot be null!");

		long currentStatus = order.getOrderStatus().getOrderStatusCd().longValue();

		if (currentStatus == statusCd) {
			return; //nothing to do
		}

		Set<Long> allowedFromStates = stateTransitionMap.get(statusCd);
		if (!allowedFromStates.contains(currentStatus)) {
			LOG.error("state transition not allowed from " + currentStatus + " to " + statusCd);
			throw new LogonException(Reason.RES_ORDER_WRONG_STATUS);
		}

		OrderStatusBsEntity newStatusRef = findOrderStatusBs(statusCd);
		order.setOrderStatus(newStatusRef);

		CustorderEntity custorder = order.getCustorder();

		if (custorder != null) {

			// If SDO is loading ...
			if (statusCd == Constant.ORDER_STATUS_LOADING || statusCd == Constant.ORDER_STATUS_LOADED
					|| statusCd == Constant.ORDER_STATUS_SHIPPING) {

				// ... and the custorder was in state NEW before we set to OPEN
				if (custorder.getCustorderStatus().getCustorderStatusCd() == Constant.CUSTORDER_STATUS_NEW) {
					custorder = setNewStatusForCustorder(custorder, Constant.CUSTORDER_STATUS_OPEN);
					order.setCustorder(custorder);
					publishOrderStatusChange(order);
					return;
				}

			}
			// If the Custorder is OPEN...
			else if (custorder.getCustorderStatus().getCustorderStatusCd() == Constant.CUSTORDER_STATUS_OPEN) {

				Long[] statusCodes = new Long[] { Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_LOADED,
						Constant.ORDER_STATUS_SHIPPING, Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
						Constant.ORDER_STATUS_UNLOADING, Constant.ORDER_STATUS_FINISHED,
						Constant.ORDER_STATUS_DELIVERED, Constant.ORDER_STATUS_DELETED };

				ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
				Filter<OrderEntity> orderFilter = orderDao.createFilter();

				orderFilter.addExpression(Restrictions.eq(OrderEntity.ATTR_CUSTORDER_ID, custorder.getCustorderId()));
				orderFilter.addExpression(Restrictions.in(OrderEntity.ATTR_ORDER_STATUS_CD, Arrays.asList(statusCodes)));

				List<OrderEntity> foundOrders = orderDao.find(orderFilter);

				// and the Custorder has no SDO which 'should' make change it back to NEW
				if (foundOrders.isEmpty()) {
					custorder = setNewStatusForCustorder(custorder, Constant.CUSTORDER_STATUS_NEW);
					order.setCustorder(custorder);
					publishOrderStatusChange(order);
					return;
				}
			}
		}

		setCustorderToFinishedAutomatically(order);
		publishOrderStatusChange(order);
	}

	/**
	 * Updates TLGNC_EXT_ORDER
	 */
	public void changeExternalOrderStatus(ExtOrderEntity order, Long statusCd) {
		if (order == null) {
			throw new IllegalArgumentException("external order cannot be null!");
		}

		long currentStatus = order.getOrderStatus().getOrderStatusCd().longValue();

		if (currentStatus == statusCd) {
			return; //nothing to do
		}

		Set<Long> allowedFromStates = stateTransitionMap.get(statusCd);
		if (!allowedFromStates.contains(currentStatus)) {
			LOG.error("state transition not allowed from " + currentStatus + " to " + statusCd);
			throw new LogonException(Reason.RES_ORDER_WRONG_STATUS);
		}

		OrderStatusBsEntity newStatusRef = referentialDataService.findBsEntity(OrderStatusBsEntity.class, statusCd);
		order.setOrderStatus(newStatusRef);
	}

	public AssignmentEntity findAssignmentById(Long id) {
		ReadWriteDao<AssignmentEntity> assignmentDao = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = assignmentDao.findById(id);
		if (assignment != null) {
			assignment.initialize();
		}
		return assignment;
	}

	/**
	 * Assigns the load of the given truck or wagon or container(rail) to the given assignment.
	 * An existing load can be taken over by the assignment (re-routing to a different customer),
	 * or a new load is created.
	 *
	 * @param truck
	 * @param wagon
	 * @param container
	 * @param assignmentPart with type loading, to which the load will be assigned
	 * @param orderItem order item for which the load is created
	 * @return LoadEntity of assignment part after takoever
	 */
	private LoadEntity checkLoadTakeover(TruckEntity truck, WagonEntity wagon, ContainerEntity container,
			AssignmentPartEntity assignmentPart, OrderItemEntity orderItem) {
		final Long loadOrderItemId = (assignmentPart.getLoad() != null) ? assignmentPart.getLoad().getLoadOrderItemId() : null;

		// The truck's trailer, if any. A value of null indicates there is no trailer.
		final TrailerEntity trailer;

		LOG.debug("Checking load takeover for order item {} with material {}",
				new Object[] { orderItem.getOrderItemNo(), orderItem.getSapMaterial().getSapMaterialNo() });

		if (truck != null) {
			LOG.debug("Truck is present, fetching trailer");
			// Fetch trailer
			ReadWriteDao<TrailerEntity> trailerDao = getDaoProvider().getTrailerEntityDao();
			Filter<TrailerEntity> trailerFilter = trailerDao.createFilter();
			trailerFilter.addExpression(Restrictions.eq(TrailerEntity.ATTR_CURRENT_TRUCK_ID, truck.getTruckId()));
			trailer = trailerDao.findSingle(trailerFilter);
			LOG.debug("Found trailer: {}", new Object[] { trailer });

			// Fetch container
			ReadWriteDao<ContainerEntity> containerDao = getDaoProvider().getContainerEntityDao();
			Filter<ContainerEntity> containerFilter = containerDao.createFilter();
			if (trailer == null) {
				containerFilter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRUCK_ID, truck.getTruckId()));
			}
			else {
				containerFilter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRAILER_ID,
						trailer.getTrailerId()));
			}
			container = containerDao.findSingle(containerFilter);
			LOG.debug("Found container: {}", new Object[] { container });
		}
		else if (wagon == null) {
			return null;
		}
		else {
			trailer = null;
		}

		//if orderItem already has a load, unlink that load from the order item
		//the assignmentPart needs to have the same load set, or the data model is not correct
		if (orderItem.getLoad() != null) {
			LOG.debug("Order item already has a load: {}", new Object[] { orderItem.getLoad() });
			LoadEntity load = orderItem.getLoad();
			if (((wagon != null) && (container == null) && (load.getWagon() != null) && load.getWagon().getWagonId().equals(
					wagon.getWagonId()))
					|| ((container != null) && (load.getContainer() != null) && load.getContainer().getContainerId().equals(
							container.getContainerId()))) {
				// load is aleady assigned to order and wagon/container, need only to assign
				// it to assignment part (needed for wagon loading process when order is selected on the Parallel system,
				// does no harm in other cases)
				LOG.debug("Load is already assigned to order/wagon/container, assigning it " + "to assignment part.");
				assignmentPart.setLoad(load);
				if (load.getLoadingTs() != null) {
					assignmentPart.setRealStart(new Date());
					if (orderItem.getOrder().getLdsDeliveryNoteNo() == null) {
						orderItem.getOrder().setLdsDeliveryNoteNo(load.getWsDeliveryNoteNo());
					}
				}

				return load;
			}
			LOG.debug("Load is not yet assigned to container/wagon");
			orderItem.setLoad(null);
			// check if load has other order items
			LOG.debug("Loading other order items for load");
			final ReadOnlyDao<OrderItemEntity> orderItemDao = getDaoProvider().getOrderItemEntityDao();
			final Filter<OrderItemEntity> f = orderItemDao.createFilter();
			f.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD_ID, load.getLoadId()));
			f.addExpression(Restrictions.neq(OrderItemEntity.ATTR_ORDER_ITEM_ID, orderItem.getOrderItemId()));
			final List<OrderItemEntity> ooi = orderItemDao.find(f);
			LOG.debug("Found {} other order items that have this load.", new Object[] { ooi.size() });
			if (ooi.isEmpty()) {
				// if no other order items, unlink load from assignment part, unless the
				// assignment part already references a different load (moved in a previous step)
				if (assignmentPart.getLoad() != null && assignmentPart.getLoad().getLoadId().equals(load.getLoadId())) {
					assignmentPart.setLoad(null);
				}

				if (load.getLoadingTs() == null) {
					// clear load reference for invalid assignment parts
					final ReadOnlyDao<AssignmentPartEntity> assignmentPartDao = getDaoProvider().getAssignmentPartEntityDao();
					final Filter<AssignmentPartEntity> assignmentPartFilter = assignmentPartDao.createFilter();

					assignmentPartFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD_ID,
							load.getLoadId()));
					final List<AssignmentPartEntity> assignmentPartsWithLoadId = assignmentPartDao.find(assignmentPartFilter);

					if (!assignmentPartsWithLoadId.isEmpty()) {
						for (AssignmentPartEntity ap : assignmentPartsWithLoadId) {
							ap.setLoad(null);
							getDaoProvider().getAssignmentPartEntityDao().merge(ap);
						}
					}
					getDaoProvider().getLoadEntityDao().remove(load);

					/**

					 * Here cancel should be sent to LDS, but unfortunately rail parallel preloading makes no assignment -> there is no

					 * assignment id to call ldsService.sendCancel. TODO clarify

					 */

				}
			}
			//force session flush to make changes known to other queries
			LOG.debug("Flushing order item to DB");
			orderItem = getDaoProvider().getOrderItemEntityDao().merge(orderItem);
		}
		else {
			LOG.debug("Order item does not yet have a load.");
		}

		//Check if truck has any compatible loads
		final ReadOnlyDao<LoadEntity> loadDao = getDaoProvider().getLoadEntityDao();
		final Filter<LoadEntity> loadFilter = loadDao.createFilter();
		if (truck != null) {
			loadFilter.addExpression(Restrictions.eq(LoadEntity.ATTR_TRUCK_ID, truck.getTruckId()));
		}
		else if (container != null) {
			loadFilter.addExpression(Restrictions.eq(LoadEntity.ATTR_CONTAINER_ID, container.getContainerId()));
		}
		else if (wagon != null) {
			loadFilter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON_ID, wagon.getWagonId()));
		}
		else {
			return null;
		}
		//			f.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
		loadFilter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
		loadFilter.addExpression(Restrictions.eq(LoadEntity.ATTR_SAP_MATERIAL_ID,
				orderItem.getSapMaterial().getSapMaterialId()));
		loadFilter.addExpression(Restrictions.eq(LoadEntity.ATTR_PRODUCTION_PLANT_CD,
				orderItem.getProductionPlant().getPlantCd()));
		loadFilter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_NET_QUANTITY));
		loadFilter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_NET_QUANTITY_UOM_CD));

		final List<LoadEntity> loads = loadDao.find(loadFilter);
		LOG.debug("Found {} loads that fit the bill", new Object[] { loads.size() });

		BigDecimal orderedQuantity = orderItem.getOrderedQuantity();
		UomBsEntity orderedUom = orderItem.getOrderedUom();
		SapMaterialEntity sapMaterial = orderItem.getSapMaterial(); // Not nullable!

		for (final LoadEntity load : loads) {
			if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {
				//check if load belongs to another assignment (part)
				if (load.getLoadingTs() == null) {
					continue;
				}

				/* LOGON-6231 / marius (2013-01-03):
				 * Only do quantity check in load takeover for non-fuzzy UOMs.
				 * If the customer orders a fuzzy quantity such as LKW, and there is a truck
				 * with an existing load of the correct material, then just take over that load
				 * regardless of its quantity.
				 * Therefore I am adding the check of the fuzzyQuantityFlag to the if clause
				 * below.
				 */
				if (wagon == null && !orderedUom.getFuzzyQuantityFlag().booleanValue()) {
					// Allow load-takeover if loaded qty < ordered qty + 5%. (See LOGON-2838)
					// cosmin Aug 18, 2009: ordered quantity means maximum ordered quantity
					UomBsEntity netQuantityUom = load.getNetQuantityUom();
					BigDecimal orderedQuantityInNetQuantityUom;

					try {
						UomConversionResult ucr = unitOfMeasurementConverterService.convert(orderedQuantity,
								orderedUom, netQuantityUom, sapMaterial, null, null, false);

						orderedQuantityInNetQuantityUom = ucr.getQuantity();
					}
					catch (LogonConversionException e) {
						if (!e.isReason(Reason.RES_ERROR_UOM_CONV_FUZZY_UOM)) {
							LOG.warn("Cannot convert ordered quantity in " + orderedUom.getUomCd()
									+ " to net quantity uom " + netQuantityUom.getUomCd() + " (sapMaterialId = "
									+ sapMaterial.getSapMaterialId() + "): " + e.getMessage());
						}

						continue;
					}

					BigDecimal orderedQuantityInNetQtyUomWithTolerance = new BigDecimal(
							1.05d * orderedQuantityInNetQuantityUom.doubleValue());
					BigDecimal netQuantity = load.getNetQuantity();

					if (netQuantity.compareTo(orderedQuantityInNetQtyUomWithTolerance) >= 0) {
						continue;
					}
				}
			}

			AssignmentPartEntity otherAssignmentPart = getAssignmentPartLoad(load);

			if ((otherAssignmentPart == null)
					|| (otherAssignmentPart.getAssignment().getAssignmentId().equals(
							assignmentPart.getAssignment().getAssignmentId()) && LogonPackageHandlingUtil.isBag(orderItem))) {
				// share load between assignment parts with same material only for bag
				assignmentPart.setLoad(load);
				/**
				 * For rail orders this makes no harm see sooner setRealStart() on line 858. In the meantime
				 * probably not the perfect solution here, as probably transmit to driver function
				 * should set this value? Around this whole function there are a lot of uncertainties see
				 * LOGON-6063 and also LOGON-5612.
				 * What is also unclear to me if the statement above in line 852 is correct or not, and if
				 * the case that wagon is null but container parameter is specified exists at all?
				 * My assumption was that this case does not exist.
				 */
				if (wagon != null && load.getLoadingTs() != null) {
					assignmentPart.setRealStart(new Date());
				}

				orderItem.setLoad(load);

				orderItem.setLoadedQuantity(orderItem.getLoad().getNetQuantity());
				orderItem.setLoadedUom(orderItem.getLoad().getNetQuantityUom());

				updateOrderItemNumeratorAndDenominator(orderItem, Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR);

				if (orderItem.getOrder().getLdsDeliveryNoteNo() == null) {
					orderItem.getOrder().setLdsDeliveryNoteNo(load.getWsDeliveryNoteNo());
				}

				return load;
			}
		}

		/*
		 * Check if truck has loads which can be shared. This part has been introduced to fix
		 * LOGON-6507: If a tour with shared loads is moved from one truck to another
		 * (moveAssignment), two separate new loads are generated, only one of which is referred
		 * to by a LOAD assignment part (see illustration below).
		 * The code below fixes that by checking whether an inactive load can be shared, along
		 * the same lines as in mergeAssignments.
		 *
		 * Before moveAssignments:
		 *
		 * Load AP    Unload AP1    Unload AP2
		 *    |          |               |
		 *    v          v               v
		 *    L1 <----- OI1             OI2
		 *     ^                         /
		 *      \-----------------------/
		 *
		 * After moveAssignments before fix (inconsistent):
		 *
		 * Load AP    Unload AP1    Unload AP2
		 *    |          |               |
		 *    v          v               v
		 *    L3        OI1             OI2
		 *     ^         |               /
		 *      \        v              /
		 *       \       L2            /
		 *        \-------------------/
		 *
		 * After moveAssignments after fix (consistent):
		 *
		 * Load AP    Unload AP1    Unload AP2
		 *    |          |               |
		 *    v          v               v
		 *    L2 <----- OI1             OI2
		 *     ^                         /
		 *      \-----------------------/
		 */
		final ReadOnlyDao<AssignmentPartEntity> apDao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> apFilter = apDao.createFilter();

		UomBsEntity netQuantityUom = AssignmentUtils.appropriateUomForLoad(orderItem);

		if (truck != null) {
			apFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_TRUCK_ID,
					truck.getTruckId()));
		}
		else if (container != null) {
			apFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_CONTAINER_ID,
					container.getContainerId()));
		}
		else if (wagon != null) {
			apFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_WAGON_ID,
					wagon.getWagonId()));
		}
		apFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID,
				assignmentPart.getAssignment().getAssignmentId()));
		apFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_SAP_MATERIAL_ID,
				orderItem.getSapMaterial().getSapMaterialId()));
		apFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "."
				+ LoadEntity.ATTR_PRODUCTION_PLANT_CD, orderItem.getProductionPlant().getPlantCd()));
		apFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "."
				+ LoadEntity.ATTR_NET_QUANTITY_UOM_CD, netQuantityUom.getUomCd()));
		apFilter.addExpression(Restrictions.isNull(AssignmentPartEntity.ATTR_LOAD + '.' + LoadEntity.ATTR_LOADING_TS));

		Authorizer.disableNextAccessRestriction();
		List<AssignmentPartEntity> apList = apDao.find(apFilter);

		LOG.debug("Found {} assignment parts that contain a load which could be shared", new Object[] { apList.size() });

		if (!apList.isEmpty() && LogonPackageHandlingUtil.isBag(orderItem)) {
			LoadEntity targetLoad = apList.get(0).getLoad();
			orderItem.setLoad(targetLoad);
			return targetLoad;
		}

		LOG.debug("Creating new load");
		LoadEntity newLoad = new LoadEntity();
		long nrChambers = 1;
		if (container == null) {
			if (trailer != null) {
				if ((trailer.getNrChambers() != null) && (trailer.getNrChambers().longValue() > 1)) {
					nrChambers = trailer.getNrChambers().longValue();
				}
			}
			else if (truck != null) {
				if ((truck.getNrChambers() != null) && (truck.getNrChambers().longValue() > 1)) {
					nrChambers = truck.getNrChambers().longValue();
				}
			}
		}
		long chamber = 1;
		if ((nrChambers > 1) && (assignmentPart.getAssignment().getAssignmentId() != null)) {
			TreeSet<Long> chambers = new TreeSet<Long>();
			for (long i = 1; i <= nrChambers; i++) {
				chambers.add(new Long(i));
			}
			final ReadOnlyDao<AssignmentPartEntity> apeDao = getDaoProvider().getAssignmentPartEntityDao();
			final Filter<AssignmentPartEntity> apeFilter = apeDao.createFilter();
			apeFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_TRUCK, truck));
			apeFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID,
					assignmentPart.getAssignment().getAssignmentId()));
			apeFilter.addExpression(Order.desc(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_CHAMBER_NUMBER));

			final Iterator<AssignmentPartEntity> aps = getDaoProvider().getAssignmentPartEntityDao().find(apeFilter).iterator();

			while (aps.hasNext()) {
				chambers.remove(aps.next().getLoad().getChamberNumber());
			}
			if (!chambers.isEmpty()) {
				chamber = chambers.iterator().next().longValue();
			}
		}

		newLoad.setChamberNumber(new Long(chamber));
		newLoad.setNetQuantityUom(netQuantityUom);
		newLoad.setProductionPlant(orderItem.getProductionPlant());
		newLoad.setSapMaterial(orderItem.getSapMaterial());
		newLoad.setWagon(wagon);
		if (wagon != null) {
			newLoad.setContainer(container);
		}
		newLoad.setTruck(truck);
		newLoad.setAccurateQuantitiesFlag(Boolean.TRUE);
		newLoad.setLdsOfflineMode(Boolean.FALSE);
		newLoad.setWsTareQuitWarnFlag(Boolean.FALSE);
		newLoad.setLoadNumber(fetchSequenceNextVal("SLGNC_LOAD_NUMBER"));

		/**
		 * If the previous load already has a loadOrderItemId then LDS knows about that load already,
		 * and it will be confused if this changes.
		 */
		newLoad.setLoadOrderItemId(loadOrderItemId);

		assignmentPart.setLoad(newLoad);
		orderItem.setLoad(newLoad);
		LOG.debug("Persisting new load for assignment part with seq no {}",
				new Object[] { assignmentPart.getAssignmentPartSeqNo() });
		LoadEntity res = getDaoProvider().getLoadEntityDao().persist(newLoad);
		LOG.debug("Persisted load is {}", new Object[] { res });
		return res;
	}

	/**
	 * It creates a road assignment with it's assignment parts except if the unloadAssignmentPart's assignment of the order
	 * is reusable. Otherwise the new assignment part's order will be
	 * the following:
	 *
	 * LOADING, DRIVING_TO, UNLOAD, DRIVING_FROM, FINISHED
	 *
	 * @param truck
	 * @param order
	 * @param externalOrder
	 * @param shipto
	 * @param startTime
	 * @param loadDuration
	 * @param transportDuration
	 * @param unloadDuration
	 * @return assignment
	 */
	private AssignmentEntity createNewForwardRoadAssignment(TruckEntity truck, OrderEntity order,
															ExtOrderEntity externalOrder, ShiptoEntity shipto, Date startTime, TimeZone timeZone, int loadDuration,
															int transportDuration, int unloadDuration) {
		final AssignmentEntity assignment = createTruckAssignment(truck, startTime, timeZone, order, externalOrder);
		AssignmentPartFactory assignmentPartFactory = new AssignmentPartFactory(assignment, referentialDataService);
		AssignmentPartContainer assignmentParts = new AssignmentPartContainer();

		final Calendar plannedStartTime = timeZone.asCalendar(startTime);

		if (order == null) {
			AssignmentPartEntity partLoading = assignmentPartFactory.createLoading(plannedStartTime, order,
					externalOrder);
			assignmentParts.add(partLoading);
			plannedStartTime.add(Calendar.MINUTE, loadDuration);
		}
		else {
			Collection<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
			for (OrderItemEntity orderItem : orderItems) {

				if (LOG.isDebugEnabled()) {
					LOG.debug("Creating assignment load part for order item [" + orderItem.getOrderItemNo() + "]");
				}

				AssignmentPartEntity partLoading = assignmentPartFactory.createLoading(plannedStartTime, order,
						externalOrder);
				assignmentParts.add(partLoading);

				partLoading.setShippingpoint(order.getShippingpoint());

				Long plannedLoadingTimeInMinutes = orderItem.getPlannedLoadingTimeMin();
				plannedStartTime.add(Calendar.MINUTE, plannedLoadingTimeInMinutes.intValue());
				checkLoadTakeover(truck, null, null, partLoading, orderItem);

				if (LOG.isDebugEnabled()) {
					LOG.debug("Load is [" + partLoading.getLoad() + "]");
				}

				updateOrderItemNumeratorAndDenominator(orderItem, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);

			}
		}

		final AssignmentPartEntity partTo = assignmentPartFactory.createDrivingTo(plannedStartTime);
		assignmentParts.add(partTo);
		if (order != null) {
			partTo.setShippingpoint(order.getShippingpoint());
		}
		plannedStartTime.add(Calendar.MINUTE, transportDuration);
		final AssignmentPartEntity partUnload = assignmentPartFactory.createUnloading(plannedStartTime, order,
				externalOrder, shipto);
		assignmentParts.add(partUnload);
		plannedStartTime.add(Calendar.MINUTE, unloadDuration);
		AssignmentPartEntity partFrom = assignmentPartFactory.createDrivingFrom(plannedStartTime, shipto, partUnload);
		assignmentParts.add(partFrom);
		plannedStartTime.add(Calendar.MINUTE, transportDuration);
		AssignmentPartEntity partFinished = assignmentPartFactory.createFinishedForTruck(plannedStartTime, truck,
				order, externalOrder, partUnload.getShipto());

		partFinished.setHomebaseFlag(Boolean.FALSE);
		partFinished.setLnfShiptoFlag(Boolean.FALSE);

		assignmentParts.add(partFinished);
		assignment.setAssignmentParts(assignmentParts.getAsList());

		return assignment;
	}

	/**
	 * It creates a road assignment with it's assignment parts except if the unloadAssignmentPart's assignment of the order
	 * is reusable. Otherwise the new assignment part's order will be
	 * the following:
	 *
	 * LOADING, DRIVING_TO, UNLOAD, DRIVING_FROM, FINISHED
	 *
	 * @param truck
	 * @param order
	 * @param externalOrder
	 * @param shipto
	 * @param startTime
	 * @param loadDuration
	 * @param transportDuration
	 * @param unloadDuration
	 * @return assignment
	 */
	private AssignmentEntity createNewReverseRoadAssignment(TruckEntity truck, OrderEntity order,
			ExtOrderEntity externalOrder, ShiptoEntity shipto, Date startTime, TimeZone timeZone, int loadDuration,
			int transportDuration, int unloadDuration) {
		final AssignmentEntity assignment = createTruckAssignment(truck, startTime, timeZone, order, externalOrder);
		AssignmentPartFactory assignmentPartFactory = new AssignmentPartFactory(assignment, referentialDataService);
		AssignmentPartContainer assignmentParts = new AssignmentPartContainer();

		final Calendar plannedStartTime = timeZone.asCalendar(startTime);

		if (order == null) {
			AssignmentPartEntity partLoading = assignmentPartFactory.createLoading(plannedStartTime, order,
					externalOrder);
			assignmentParts.add(partLoading);
			partLoading.setShipto(shipto);
			plannedStartTime.add(Calendar.MINUTE, loadDuration);
		}
		else {
			Collection<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
			for (OrderItemEntity orderItem : orderItems) {

				if (LOG.isDebugEnabled()) {
					LOG.debug("Creating assignment load part for order item [" + orderItem.getOrderItemNo() + "]");
				}

				AssignmentPartEntity partLoading = assignmentPartFactory.createLoading(plannedStartTime, order,
						externalOrder);
				assignmentParts.add(partLoading);

				partLoading.setShipto(shipto);

				Long plannedLoadingTimeInMinutes = orderItem.getPlannedLoadingTimeMin();
				plannedStartTime.add(Calendar.MINUTE, plannedLoadingTimeInMinutes.intValue());
				checkLoadTakeover(truck, null, null, partLoading, orderItem);

				if (LOG.isDebugEnabled()) {
					LOG.debug("Load is [" + partLoading.getLoad() + "]");
				}

				updateOrderItemNumeratorAndDenominator(orderItem, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);

			}
		}

		final AssignmentPartEntity partTo = assignmentPartFactory.createDrivingTo(plannedStartTime);
		assignmentParts.add(partTo);
		partTo.setShipto(shipto);
		plannedStartTime.add(Calendar.MINUTE, transportDuration);

		final AssignmentPartEntity partUnload = assignmentPartFactory.createUnloading(plannedStartTime, order,
				externalOrder, shipto);
		assignmentParts.add(partUnload);
		plannedStartTime.add(Calendar.MINUTE, unloadDuration);

		final AssignmentPartEntity partFrom = assignmentPartFactory.createDrivingFromBasedOnOrder(plannedStartTime,
				shipto, order);
		assignmentParts.add(partFrom);
		plannedStartTime.add(Calendar.MINUTE, transportDuration);

		AssignmentPartEntity partFinished = assignmentPartFactory.createFinishedForTruck(plannedStartTime, truck,
				order, externalOrder, shipto);
		assignmentParts.add(partFinished);
		assignment.setAssignmentParts(assignmentParts.getAsList());

		return assignment;
	}

	/**
	 * It creates and sets an assignment based on the truck and orders
	 *
	 * @param truck
	 * @param startTime
	 * @param order
	 * @param externalOrder
	 * @return assignment
	 */
	private AssignmentEntity createTruckAssignment(TruckEntity truck, Date startTime, TimeZone timeZone,
			OrderEntity order, ExtOrderEntity externalOrder) {
		final AssignmentTypeBsEntity assignmentType = getAssignmentType(order);
		final AssignTrStatusBsEntity assignmentTransmitStatus;
		assignmentTransmitStatus = referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
				Constant.ASSIGN_STATUS_TR_NOT_TRANSMITTED);

		final AssignmentEntity assignment = AssignmentUtils.createTruckAssignment(truck, assignmentType,
				fetchSequenceNextVal("SLGNC_ASSIGNMENT_NUMBER"), startTime, timeZone);

		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_NEW);
		assignment.setAssignTrStatus(assignmentTransmitStatus);

		// External order related values
		if (externalOrder != null) {
			ExtMaterialEntity externalMaterial = externalOrder.getExtMaterial();
			BigDecimal orderedQuantity = externalOrder.getOrderedQuantity();
			UomBsEntity orderedUom = externalOrder.getOrderedUom();
			String externalMaterialName = externalMaterial.getMaterialName();

			assignment.setMaterialName(externalMaterialName);
			assignment.setMaterialQuantity(orderedQuantity);
			assignment.setMaterialUom(orderedUom);
		}

		// Normal order related stuff
		if (order != null) {
			assignment.setCarrierTruckDriverNote(order.getCarrierTruckDriverNote());
			assignment.setCarrierDispatchingNote(order.getCarrierDispatchingNote());

			OrderEntity[] orderA = { order };
			updateCarrierForOrders(truck, Arrays.asList(orderA), null);
		}

		return assignment;
	}

	/**
	 * It returns the assignment's type based on the order. If order is
	 * null then it will be external otherwise internal.
	 * @param order
	 * @return assignmentType
	 */
	private AssignmentTypeBsEntity getAssignmentType(OrderEntity order) {
		final AssignmentTypeBsEntity assignmentType;
		if (order == null) {
			assignmentType = referentialDataService.findBsEntity(AssignmentTypeBsEntity.class,
					Constant.ASSIGNMENT_TYPE_EXTERNAL);
		}
		else {
			assignmentType = referentialDataService.findBsEntity(AssignmentTypeBsEntity.class,
					Constant.ASSIGNMENT_TYPE_INTERNAL);
		}
		return assignmentType;
	}

	/**
	 * Updates the order item's ordered/loadedReturned numerator/denominator.
	 */
	public void updateOrderItemNumeratorAndDenominator(OrderItemEntity orderItem, final String orderItemNumDenomType) {
		if (orderItem == null) {
			throw new IllegalArgumentException("orderItem is null");
		}

		final SapMaterialEntity orderMaterial = orderItem.getSapMaterial();

		// The UOM used in the order and the base UOM of the material.
		final UomBsEntity orderUom = appropriateOrderUom(orderItem, orderItemNumDenomType);
		final UomBsEntity materialBaseUom = orderMaterial.getBaseUom();

		if (orderUom == null) {
			final String msg = String.format("Updating order item numerator and denominator "
					+ "failed [orderItem = %d, type = %s].", orderItem.getOrderItemId(), orderItemNumDenomType);
			LOG.error(msg);
			throw new IllegalArgumentException("orderItemParamUom is null");
		}
		if (materialBaseUom == null) {
			throw new IllegalArgumentException("base uom cd in SAP material unexpectedly null");
		}

		final boolean isFuzzy = isAnyUomFuzzy(orderUom, materialBaseUom);

		/* Fuzzy UOM conversions are only allowed for numerator/denominator of type
		 * "ordered". If an order has already been scheduled on a truck, it would
		 * be crazy to make such inexact conversions.
		 */
		if (isFuzzy && !Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED.equals(orderItemNumDenomType)) {
			throw new LogonException(Reason.RES_ERROR_UOM_CONV_FUZZY_UOM);
		}

		String orderUomCd = orderUom.getUomCd();
		/* If the ordered UoM is the same as the base UoM of the material then
		 * the numerator/denominator will be one and no conversion is needed.
		 */
		if (orderUomCd.equals(materialBaseUom.getUomCd())) {
			if (Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED.equals(orderItemNumDenomType)) {
				orderItem.setOrderedUomNumerator(1L);
				orderItem.setOrderedUomDenominator(1L);
				return;
			}
			else if (Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR.equals(orderItemNumDenomType)) {
				orderItem.setLrUomNumerator(1L);
				orderItem.setLrUomDenominator(1L);
				return;
			}
			else {
				throw new IllegalArgumentException("unknown NUM/DENOM type");
			}
		}

		/* The truck and/or wagon type of the order (if they exist) are required for the conversion
		 * in case the order UOM is something fuzzy like wagon or LKW.
		 * They may be null!
		 */
		final TruckTypeBsEntity truckType;
		final WagonTypeBsEntity wagonType;

		if (Constant.UOM_LKW.equals(orderUomCd)) {
			truckType = appropriateTruckTypeForUOMConversion(orderItem.getOrder());
			wagonType = null;
		}
		else if (Constant.UOM_WAGON.equals(orderUomCd) || Constant.UOM_EMPTY_WAGON_27T.equals(orderUomCd)
				|| Constant.UOM_EMPTY_WAGON_54T.equals(orderUomCd)) {
			wagonType = appropriateWagonTypeForUOMConversion(orderItem.getOrder());
			truckType = null;
		}
		else {
			truckType = null;
			wagonType = null;
		}

		/* If the order's unit of measure is truck, then at least one truck type must
		 * have been selected in the order.
		 */
		if (Constant.UOM_LKW.equals(orderUomCd) && truckType == null) {
			throw new LogonException(Reason.RES_ERROR_ORDER_TRUCK_UOM_NO_TRUCKS_SELECTED);
		}

		final UomConversionResult conversion;

		/* Since we are only interested in the numerator/denominator, we can
		 * pass one for the quanitity.
		 */
		try {
			conversion = unitOfMeasurementConverterService.convert(BigDecimal.ONE, orderUom, materialBaseUom,
					orderMaterial, truckType, wagonType, /* allow fuzzy conversion = */
					isFuzzy);
		}
		catch (LogonConversionException e) {
			final String msg = "Could not convert from order UOM " + orderUom + " to base UOM " + materialBaseUom
					+ " for order material " + orderMaterial + "(" + e.getMessage() + ")";
			LOG.error(msg);
			throw new LogonException(msg, e);
		}

		if (Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED.equals(orderItemNumDenomType)) {
			orderItem.setOrderedUomNumerator(conversion.getNumerator());
			orderItem.setOrderedUomDenominator(conversion.getDenominator());
		}
		else if (Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR.equals(orderItemNumDenomType)) {
			orderItem.setLrUomNumerator(conversion.getNumerator());
			orderItem.setLrUomDenominator(conversion.getDenominator());
		}
		else {
			throw new IllegalArgumentException("unknown NUM/DENOM type");
		}
	}

	/**
	 * Finds the truck type most suitable for UOM conversion if an order's UOM
	 * is "LKW" (= truck). If an order item's unit of measurement is
	 * <code>Constant.UOM_LKW</code>, then we need to use a truck's capacity.
	 * This capacity is defined per truck type, and this method returns
	 * the most suitable truck type of all those enabled for the order.
	 * @param order The order for which the truck type should be determined.
	 * @return The truck type that can be used for the UOM conversions, or null
	 * if no truck type was found in the order.
	 */
	private TruckTypeBsEntity appropriateTruckTypeForUOMConversion(final OrderEntity order) {
		final List<OrderTruckTypeEntity> orderTruckTypes = order.getOrderVehicleTypes();
		if (orderTruckTypes == null || orderTruckTypes.isEmpty()) {
			LOG.debug("Order has no truck types, returning null.");
			return null;
		}
		/* If they are several enabled truck types, the best guess is to
		 * take that of the highest priority.
		 */
		final OrderTruckTypeEntity maxPrioTruckType = Collections.max(orderTruckTypes, new OrderTruckTypeComparator());
		return maxPrioTruckType.getTruckType();
	}

	/**
	 * Finds the wagon type most suitable for UOM conversion if an order's UOM
	 * is "LKW" (= wagon). If an order item's unit of measurement is
	 * <code>Constant.UOM_WAGON</code>, then we need to use a wagon's capacity.
	 * This capacity is defined per wagon type, and this method returns
	 * the most suitable wagon type of all those enabled for the order.
	 * @param order The order for which the wagon type should be determined.
	 * @return The wagon type that can be used for the UOM conversions, or null
	 * if no wagon type was found in the order.
	 */
	private WagonTypeBsEntity appropriateWagonTypeForUOMConversion(final OrderEntity order) {
		/* (marius Oct 20, 2012):
		 * As long as we don't save wagon types per order, we use the
		 * maximum prio wagon of the order's shipto (result of
		 * discussion with lajos).
		 */
		Authorizer.disableNextAccessRestriction();
		final ShiptoEntity shipTo = findShiptoBySapBpId(order);
		if (shipTo == null) {
			// If there is no shipto, we can't find any wagons.
			return null;
		}
		final List<ShiptoWagonTypeEntity> stWagonTypes;
		stWagonTypes = (List<ShiptoWagonTypeEntity>) wagonTypesForShipto(shipTo);

		if (stWagonTypes == null || stWagonTypes.isEmpty()) {
			return null;
		}
		final Comparator<ShiptoWagonTypeEntity> comp;
		comp = new Comparator<ShiptoWagonTypeEntity>() {
			public int compare(ShiptoWagonTypeEntity w1, ShiptoWagonTypeEntity w2) {
				return TruckWagonTypeWrapperUtil.compareWagonTypeEntity(w1, w2);
			}
		};
		return Collections.max(stWagonTypes, comp).getWagonType();
	}

	/**
	 * Returns the wagon type for a shipto.
	 * @param shipto A shipto entity
	 * @return The list of wagon types of the shipto.
	 */
	private List<ShiptoWagonTypeEntity> wagonTypesForShipto(final ShiptoEntity shipto) {
		final ReadWriteDao<ShiptoWagonTypeEntity> dao = getDaoProvider().getShiptoWagonTypeEntityDao();
		Filter<ShiptoWagonTypeEntity> f = dao.createFilter();
		f.addExpression(Restrictions.eq(ShiptoWagonTypeEntity.ATTR_SHIPTO_ID, shipto.getShiptoId()));
		return dao.find(f);
	}

	/**
	 * Checks if any of the passed UOMs is a fuzzy UOM.
	 * @param uoms A list of UOMs
	 * @return true if at least one of the uoms has the fuzzy flag set, false otherwise.
	 */
	private boolean isAnyUomFuzzy(UomBsEntity... uoms) {
		LOG.debug("Checking fuzziness of the UOMs {}", new Object[] { uoms });
		if (uoms == null) {
			throw new IllegalArgumentException("invalid input");
		}

		for (UomBsEntity uom : uoms) {
			if (uom.getFuzzyQuantityFlag().booleanValue() == true) {
				LOG.debug("UOM {} is fuzzy, returning true.", new Object[] { uom });
				return true;
			}
		}

		LOG.debug("No fuzzy UOMs found, returning false.");
		return false;
	}

	/**
	 * Returns the appropriate unit of measure depending on the numerator and
	 * denominator type. An order item has more than one (numerator, denominator)
	 * pair. Which one is appropriate depends on the status of the order, whether
	 * it is only ordered or has already been assigned etc.
	 * @param item An order item.
	 * @param numDenomType The type of numerator/denominator for which to return the
	 * UOM. Must be one of <code>Constant.ORDER_ITEM_NUM_DENOM_TYPE_*</code>.
	 * @return The appropriate UOM.
	 * value
	 */
	private UomBsEntity appropriateOrderUom(final OrderItemEntity item, final String numDenomType) {
		// Input validation.
		if (item == null) {
			throw new IllegalArgumentException("item must not be null.");
		}
		if (numDenomType == null) {
			throw new IllegalArgumentException("numDenomType must not be null.");
		}

		if (Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED.equals(numDenomType)) {
			return item.getOrderedUom();
		}
		else if (Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR.equals(numDenomType)) {
			return item.getLoadedUom();
		}
		else {
			throw new IllegalArgumentException("unknown order item numerator/denominator type");
		}
	}

	public BigDecimal convertAltUomToMaterialBaseUom(UomBsEntity altUom, BigDecimal quantity,
			SapMaterialEntity sapMaterial) {
		ReadOnlyDao<SapMaterialAltUomEntity> sapMatAltUomDao = getDaoProvider().getSapMaterialAltUomEntityDao();
		return UOMConverter.convertAltUomToMaterialBaseUom(altUom, quantity, sapMaterial, sapMatAltUomDao);
	}

	/**
	 * Handles the carrier attribute updates, and rejects certain state transitions
	 * depending on the:
	 * <ul>
	 * <li>order carrier attributes,</li>
	 * <li>order status,</li>
	 * <li>truck attributes.</li>
	 * </ul>
	 *
	 * This method is called on:
	 * <ul>
	 * <li>creating a new assignment (originalTruck is null),</li>
	 * <li>moving an exiting assignment from truck to truck (originalTruck is NOT null).</li>
	 * </ul>
	 *
	 * @param newTruck The new truck to which the assignment is moved to.
	 * @param orders The list of orders from the assignment.
	 * @param originalTruck The truck on which the assignment was scheduled.
	 * (Optional parameter, so this can be nullable.)
	 */
	private void updateCarrierForOrders(TruckEntity newTruck, List<OrderEntity> orders, TruckEntity originalTruck) {
		for (OrderEntity order : orders) {
			final boolean newTruckIsPlanning = LogonUtil.isPlanningTruck(newTruck);
			CarrierEntity carrier = newTruck.getCarrier();
			SapVendorEntity sapVendor = carrier.getSapVendor();
			final boolean newTruckCarrierIsCommCarrier = sapVendor.getVnIsCommCarrier();
			final boolean newTruckCarrierIsExecCarrier = sapVendor.getVnIsExecCarrier();

			if (order.getCommCarrier() == null && newTruckCarrierIsCommCarrier) {
				setCommercialCarrier(order, carrier);
			}
			if (!newTruckIsPlanning && newTruckCarrierIsExecCarrier) {
				setExecutingCarrier(order, carrier);
			}

			/*
			 * LOGON-6079 LRQ 112.6 Auto Changing Carriers
			 */
			if (originalTruck != null && newTruckCarrierIsCommCarrier) {
				final boolean commCarrierIsSet = order.getCommCarrier() != null;
				final boolean execCarrierIsSet = order.getExecCarrier() != null;
				final boolean orderIsInDispatchedState = order.getOrderStatus().getOrderStatusCd().equals(
						Constant.ORDER_STATUS_DISPATCHED);
				final boolean originalTruckIsPlanning = LogonUtil.isPlanningTruck(originalTruck);

				/*
				 * Should be possible to change the comm. carrier of the order by allocating
				 * the assignment from planning truck to another planning truck (from another pool).
				 */
				final boolean shouldChangeCommCarrier = commCarrierIsSet && orderIsInDispatchedState
						&& originalTruckIsPlanning && newTruckIsPlanning;

				LOG.debug(
						"## orderNr: '{}', commCarrierIsSet: '{}', execCarrierIsSet: '{}', orderIsInDispatchedState: '{}'"
								+ ", originalTruckIsPlanning: '{}', ==> shouldChangeCommCarrier: '{}'", new Object[] {
								order.getOrderNumber(), commCarrierIsSet, execCarrierIsSet, orderIsInDispatchedState,
								originalTruckIsPlanning, shouldChangeCommCarrier });

				if (shouldChangeCommCarrier) {
					setCommercialCarrier(order, carrier);
					LOG.info("## Comm. Carrier is changed to the trucks carrier: '{}'",
							new Object[] { carrier.getCarrierId() });
				}

				/*
				 * If the exec carrier set check if it has relationship with the newly set
				 * comm. carrier and if not, then delete the exec. carrier.
				 */
				if (shouldChangeCommCarrier && execCarrierIsSet) {
					final CarrierEntity execCarrier = order.getExecCarrier();

					final boolean isExecCarrierInRelationWithCommCarrier = isExecCarrierInRelationWithCommCarrier(
							execCarrier, carrier);

					if (!isExecCarrierInRelationWithCommCarrier) {
						LOG.info("## Setting Exec. Carrier to null.");
						order.setExecCarrier(null);
					}
				}
			}
		}
	}

	/**
	 * Decides if the given exec. carrier is in relation with the given comm. carrier.
	 *
	 * @param execCarrier An exec. Carrier which to check the relation with the comm. Carrier.
	 * @param commCarrier A comm. Carrier to which check the relation with the exec. Carrier.
	 *
	 * @return <code>True<code> if the given exec. carrier is in relation with the given comm.
	 * carrier, <code>false</code> otherwise.
	 */
	private boolean isExecCarrierInRelationWithCommCarrier(CarrierEntity execCarrier, CarrierEntity commCarrier) {
		if (execCarrier == null || commCarrier == null) {
			return false;
		}
		final ReadOnlyDao<CommExecCarrierPfEntity> dao = getDaoProvider().getCommExecCarrierPfEntityDao();

		final Filter<CommExecCarrierPfEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(CommExecCarrierPfEntity.ATTR_EXEC_CARRIER_ID, execCarrier.getCarrierId()));
		filter.addExpression(Restrictions.eq(CommExecCarrierPfEntity.ATTR_COMM_CARRIER_ID, commCarrier.getCarrierId()));

		final long countOfRelations = dao.count(filter);

		LOG.info("## Count of relations between comm Carrier ('{}') and exec Carrier ('{}'): '{}'", new Object[] {
				commCarrier.getCarrierId(), execCarrier.getCarrierId(), countOfRelations });

		return (countOfRelations > 0);
	}

	private Date[] getDepartureArrivalDate(Long drId, Long arId, Date startDate) {
		ReadWriteDao<RailTimetableEntity> railTimetableEntityDao = getDaoProvider().getRailTimetableEntityDao();
		Filter<RailTimetableEntity> filter = railTimetableEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_ARR_RAILWAY_STATION_ID, arId));
		filter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_DEP_RAILWAY_STATION_ID, drId));
		filter.addExpression(Restrictions.ge(RailTimetableEntity.ATTR_DEP_SWITCHING_TIME_MIN,
				DateTimeUtils.getMinAfterMidnight(startDate)));
		filter.addExpression(Order.asc(RailTimetableEntity.ATTR_DEP_SWITCHING_TIME_MIN));
		List<RailTimetableEntity> tList = railTimetableEntityDao.find(filter);

		if (tList.isEmpty()) {
			Calendar c = Calendar.getInstance();
			c.setTime(DateTimeUtils.cleanTimeWithDefaultTimeZone(startDate));
			c.add(Calendar.DAY_OF_YEAR, 1);
			int day = c.get(Calendar.DAY_OF_WEEK);
			if ((day == Calendar.SATURDAY) || (day == Calendar.SUNDAY)) {
				c.add(Calendar.DAY_OF_YEAR, 2);
			}
			startDate = c.getTime();
			filter = railTimetableEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_ARR_RAILWAY_STATION_ID, arId));
			filter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_DEP_RAILWAY_STATION_ID, drId));
			filter.addExpression(Order.asc(RailTimetableEntity.ATTR_DEP_SWITCHING_TIME_MIN));
			tList = railTimetableEntityDao.find(filter);

			if (tList.isEmpty()) {
				return null;
			}
		}

		RailTimetableEntity timetable = tList.get(0);

		Date cleanDate = DateTimeUtils.cleanTimeWithDefaultTimeZone(startDate);

		Calendar c = Calendar.getInstance();
		c.setTime(cleanDate);
		c.add(Calendar.MINUTE, timetable.getDepSwitchingTimeMin().intValue());
		Date departureDate = c.getTime();

		c.setTime(cleanDate);
		c.add(Calendar.DAY_OF_YEAR, timetable.getArrOffsetDays().intValue());
		int day = c.get(Calendar.DAY_OF_WEEK);
		if ((day == Calendar.SATURDAY) || (day == Calendar.SUNDAY)) {
			c.add(Calendar.DAY_OF_YEAR, 2);
		}
		c.add(Calendar.MINUTE, timetable.getArrSwitchingTimeMin().intValue());

		Date arrivalDate = c.getTime();

		Date[] result = new Date[2];
		result[0] = departureDate;
		result[1] = arrivalDate;

		return result;
	}

	private AssignmentEntity createNewRailAssignment(WagonEntity wagon, OrderEntity order, Date startDate) {
		AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
		partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_NEW);

		AssignmentEntity assignment = new AssignmentEntity();
		assignment.setManuallyScheduled(Boolean.TRUE);
		assignment.setWagon(wagon);
		assignment.setActiveFlag(Boolean.TRUE);
		assignment.setAssignmentNumber(fetchSequenceNextVal("SLGNC_ASSIGNMENT_NUMBER"));

		AssignmentTypeBsEntity at = new AssignmentTypeBsEntity();
		at.setAssignmentTypeCd(Constant.ASSIGNMENT_TYPE_INTERNAL);

		assignment.setAssignmentType(at);
		assignment.setAssignmentDat(DateTimeUtils.cleanTimeWithDefaultTimeZone(startDate));

		if (order.getShippingpoint() == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TLGNC_ORDER.SHIPPINGPOINT is null! Order id: " + order.getOrderId());
			}
			return null;
		}
		if (order.getShippingpoint().getRailwayStation() == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TLGNC_ORDER.SHIPPINGPOINT.RAILWAY_STATION is null! Order id: " + order.getOrderId());
			}
			return null;
		}

		if (order.getShiptoSapBp().getRailwayStation() == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TLGNC_ORDER.SHIPPINGPOINT.RAILWAY_STATION is null! Order id: " + order.getOrderId());
			}
			return null;
		}

		Date[] dates = getDepartureArrivalDate(order.getShippingpoint().getRailwayStation().getRailwayStationId(),
				order.getShiptoSapBp().getRailwayStation().getRailwayStationId(), startDate);

		if (dates == null) {
			return null;
		}

		long seqno = 0;

		List<AssignmentPartEntity> assignmentParts = new ArrayList<AssignmentPartEntity>();
		AssignmentPartTypeBsEntity partType = new AssignmentPartTypeBsEntity();
		partType.setAssignmentPartTypeCd(Constant.ASSIGNMENT_PART_TYPE_LOAD);

		ReadWriteDao<ContainerEntity> containerDao = getDaoProvider().getContainerEntityDao();
		Filter<ContainerEntity> containerFilter = containerDao.createFilter();
		containerFilter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_WAGON_ID, wagon.getWagonId()));
		Authorizer.disableNextAccessRestriction();
		List<ContainerEntity> cList = containerDao.find(containerFilter);

		Iterator<ContainerEntity> containers = null;
		if (!cList.isEmpty()) {
			containers = cList.iterator();
		}

		Collection<OrderItemEntity> materialItems = LogonPackageHandlingUtil.getMaterialItems(order);

		for (OrderItemEntity oi : materialItems) {
			AssignmentPartEntity partLoading = new AssignmentPartEntity();
			partLoading.setAssignment(assignment);
			assignmentParts.add(partLoading);
			partLoading.setAssignmentPartSeqNo(seqno);
			partLoading.setAssignmentPartType(partType);
			partLoading.setPlannedStart(dates[0]);
			partLoading.setAssignPartStatus(partStatus);
			partLoading.setHomebaseFlag(Boolean.FALSE);
			partLoading.setShippingpoint(order.getShippingpoint());
			partLoading.setLnfShiptoFlag(Boolean.FALSE);
			seqno++;

			checkLoadTakeover(null, wagon, (containers == null) ? null : containers.next(), partLoading, oi);
			updateOrderItemNumeratorAndDenominator(oi, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);

		}

		AssignmentPartEntity partTo = new AssignmentPartEntity();
		partTo.setAssignment(assignment);
		assignmentParts.add(partTo);
		partTo.setAssignmentPartSeqNo(seqno);
		partType = new AssignmentPartTypeBsEntity();
		partType.setAssignmentPartTypeCd(Constant.ASSIGNMENT_PART_TYPE_TO);
		partTo.setAssignmentPartType(partType);
		partTo.setPlannedStart(dates[0]);
		partTo.setAssignPartStatus(partStatus);
		partTo.setHomebaseFlag(Boolean.FALSE);
		partTo.setShippingpoint(order.getShippingpoint());
		partTo.setLnfShiptoFlag(Boolean.FALSE);
		seqno++;

		AssignmentPartEntity partUnload = new AssignmentPartEntity();
		partUnload.setAssignment(assignment);
		assignmentParts.add(partUnload);
		partUnload.setAssignmentPartSeqNo(seqno);
		partType = new AssignmentPartTypeBsEntity();
		partType.setAssignmentPartTypeCd(Constant.ASSIGNMENT_PART_TYPE_UNLOAD);
		partUnload.setAssignmentPartType(partType);
		partUnload.setPlannedStart(dates[1]);
		partUnload.setAssignPartStatus(partStatus);

		ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
		Filter<ShiptoEntity> shiptoFilter = shiptoDao.createFilter();
		shiptoFilter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID,
				order.getShiptoSapBp().getSapBpId()));
		Authorizer.disableNextAccessRestriction();
		ShiptoEntity shipto = shiptoDao.findSingle(shiptoFilter);

		partUnload.setShipto(shipto);
		partUnload.setOrder(order);
		partUnload.setHomebaseFlag(Boolean.FALSE);
		partUnload.setLnfShiptoFlag(Boolean.FALSE);
		seqno++;

		AssignmentPartEntity partFinished = new AssignmentPartEntity();
		partFinished.setAssignment(assignment);
		assignmentParts.add(partFinished);
		partFinished.setAssignmentPartSeqNo(seqno);
		partType = new AssignmentPartTypeBsEntity();
		partType.setAssignmentPartTypeCd(Constant.ASSIGNMENT_PART_TYPE_FINISHED);
		partFinished.setAssignmentPartType(partType);
		partFinished.setPlannedStart(dates[1]);
		partFinished.setAssignPartStatus(partStatus);
		partFinished.setShipto(partUnload.getShipto());
		partFinished.setHomebaseFlag(Boolean.FALSE);
		partFinished.setLnfShiptoFlag(Boolean.FALSE);

		assignment.setAssignmentParts(assignmentParts);
		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_NEW);
		AssignTrStatusBsEntity assignTrStatus = referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
				Constant.ASSIGN_STATUS_TR_NOT_TRANSMITTED);
		assignment.setAssignTrStatus(assignTrStatus);
		seqno++;

		return assignment;
	}

	/**
	 * Creates a new assignment for the order and truck using all data from the db and supposing that the planned start of the order is
	 * the actual date.
	 *
	 * @param truck
	 * @param order
	 * @return
	 */
	public AssignmentEntity createAssignmentForTruck(TruckEntity truck, OrderEntity order) {
		int loadDuration = SchedulingUtils.calculateLoadingTimeForOrder(order, getConfigBean()).intValue();
		int transportDuration = order.getTransportDurationMin().intValue();
		int unloadDuration = order.getPlannedUnloadingTimeMin().intValue();
		return createAssignment(truck, null, order, new Date(), TimeZone.getAppTimeZone(), loadDuration,
				transportDuration, unloadDuration);
	}

	public AssignmentEntity createNewAssignment(Long truckId, Date startTime, TimeZone timeZone, int loadDuration,
			int transportDuration, int unloadDuration) {
		ReadWriteDao<TruckEntity> truckDao = getDaoProvider().getTruckEntityDao();
		TruckEntity truck = truckDao.findById(truckId);
		return createNewRoadAssignment(truck, null, null, null, startTime, timeZone, loadDuration, transportDuration,
				unloadDuration);
	}

	/**
	 * creates an Assignment with the given data (incl load and Assignment_parts)
	 * changes order status to dispatched.
	 *
	 * @adnlog.exclude return
	 */
	@Deprecated
	public AssignmentEntity createAssignment(Long truckId, Long wagonId, Long orderNr, Date startTime,
			TimeZone timeZone, int loadDuration, int transportDuration, int unloadDuration) {
		TruckEntity truck = null;
		if (truckId != null) {
			truck = getDaoProvider().getTruckEntityDao().findById(truckId);
		}
		WagonEntity wagon = null;
		if (wagonId != null) {
			wagon = getDaoProvider().getWagonEntityDao().findById(wagonId);
		}

		final ReadOnlyDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter<OrderEntity> f = orderDao.createFilter();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNr));
		OrderEntity order = orderDao.findSingle(f);

		final Long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		/*
		 * LOGON-6950 Deleted order could be allocated
		 * Do not allow orders to be assigned if the status is not before Loading.
		 */
		if ((truck != null && LogonStatusUtils.isBeforeLoading(orderStatusCd))
				|| (wagon != null && LogonStatusUtils.isBeforeDelivered(orderStatusCd))) {
			return createAssignment(truck, wagon, order, startTime, timeZone, loadDuration, transportDuration,
					unloadDuration);
		}
		throw new LogonException("Assignment creation failed for order " + orderNr + ".", Reason.RES_ORDER_WRONG_STATUS);
	}

	@Deprecated
	private AssignmentEntity createNewRoadAssignment(TruckEntity truck, OrderEntity order, ExtOrderEntity extOrder,
			ShiptoEntity shipto, Date startTime, TimeZone timeZone, int loadDuration, int transportDuration,
			int unloadDuration) {
		if (order != null) {
			AssignmentPartEntity assignmentPartUnload = getAssignmentPartUnload(order);
			/**
			 * An assignment part assigned to a planning truck can exist for an order, if exists -> use that assignment further on, and move the order from
			 * planning to real truck.
			 */
			if (assignmentPartUnload != null) {
				if (assignmentPartUnload.getAssignment().getAssignStatus().getAssignStatusCd().longValue() == AssignmentStatusUtil.ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER.getAssignStatusCd()) {

					AssignmentEntity assignment = moveAssignment(truck.getTruckId(),
							assignmentPartUnload.getAssignment().getAssignmentId());
					return assignment;
				}
				//assignment was already created concurrently, nothing to do
				return null;
			}
		}
		// Load required refdata types
		final AssignmentTypeBsEntity at;
		final AssignPartStatusBsEntity statusNew;
		final AssignmentPartTypeBsEntity partTypeFrom;
		final AssignmentPartTypeBsEntity partTypeLoad;
		final AssignmentPartTypeBsEntity partTypeTo;
		final AssignmentPartTypeBsEntity partTypeUnload;
		final AssignmentPartTypeBsEntity partTypeFinished;
		statusNew = referentialDataService.findBsEntity(AssignPartStatusBsEntity.class, Constant.ASSIGN_PART_STATUS_NEW);
		partTypeFrom = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class,
				Constant.ASSIGNMENT_PART_TYPE_FROM);
		partTypeLoad = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class,
				Constant.ASSIGNMENT_PART_TYPE_LOAD);
		partTypeTo = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class,
				Constant.ASSIGNMENT_PART_TYPE_TO);
		partTypeUnload = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class,
				Constant.ASSIGNMENT_PART_TYPE_UNLOAD);
		partTypeFinished = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class,
				Constant.ASSIGNMENT_PART_TYPE_FINISHED);
		if (order == null) {
			at = referentialDataService.findBsEntity(AssignmentTypeBsEntity.class, Constant.ASSIGNMENT_TYPE_EXTERNAL);
		}
		else {
			at = referentialDataService.findBsEntity(AssignmentTypeBsEntity.class, Constant.ASSIGNMENT_TYPE_INTERNAL);
		}

		// The assignment to create
		final AssignmentEntity assignment = AssignmentUtils.createTruckAssignment(truck, at,
				fetchSequenceNextVal("SLGNC_ASSIGNMENT_NUMBER"), startTime, timeZone);

		long seqno = 0;

		final Calendar c = timeZone.asCalendar(startTime);

		final List<AssignmentPartEntity> assignmentParts = new LinkedList<AssignmentPartEntity>();

		// 1. 'Load' part

		// External orders
		if (order == null) {
			AssignmentPartEntity partLoading = new AssignmentPartEntity();
			assignmentParts.add(partLoading);
			partLoading.setAssignment(assignment);

			if (extOrder != null) {
				final ExtSiteEntity loadSite = extOrder.getLoadingExtSite();
				partLoading.setExtOrder(extOrder);
				partLoading.setSiteCity(loadSite.getSiteCity());
				partLoading.setSiteContactPersonTxt(loadSite.getSiteContactPersonTxt());
				partLoading.setSiteCountry(loadSite.getSiteCountry());
				partLoading.setSiteLoadUnload(loadSite.getSiteLoadUnload());
				partLoading.setSiteName(loadSite.getSiteName());
				partLoading.setSitePostalCode(loadSite.getSitePostalCode());
				partLoading.setSiteStreetHouseNo(loadSite.getSiteStreetHouseNo());
				partLoading.setCoordLat(loadSite.getCoordLat());
				partLoading.setCoordLon(loadSite.getCoordLon());
			}
			partLoading.setAssignmentPartSeqNo(seqno);
			partLoading.setAssignmentPartType(partTypeLoad);
			partLoading.setPlannedStart(c.getTime());
			partLoading.setOrder(null);
			partLoading.setAssignPartStatus(statusNew);
			partLoading.setHomebaseFlag(Boolean.FALSE);
			partLoading.setLnfShiptoFlag(Boolean.FALSE);
			c.add(Calendar.MINUTE, loadDuration);
			seqno++;

		}
		// Logon orders
		else {
			Collection<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
			for (OrderItemEntity orderItem : orderItems) {
				LOG.debug("Creating assignment load part for order item {}",
						new Object[] { orderItem.getOrderItemNo() });
				AssignmentPartEntity partLoading = new AssignmentPartEntity();
				assignmentParts.add(partLoading);
				partLoading.setAssignment(assignment);

				partLoading.setAssignmentPartSeqNo(seqno);
				partLoading.setAssignmentPartType(partTypeLoad);
				partLoading.setPlannedStart(c.getTime());
				partLoading.setAssignPartStatus(statusNew);
				partLoading.setHomebaseFlag(Boolean.FALSE);
				if (LogonUtil.isLandfill(order)) {
					partLoading.setShipto(shipto);
				}
				else {
					partLoading.setShippingpoint(order.getShippingpoint());
				}

				partLoading.setLnfShiptoFlag(Boolean.FALSE);
				c.add(Calendar.MINUTE, orderItem.getPlannedLoadingTimeMin().intValue());
				seqno++;
				checkLoadTakeover(truck, null, null, partLoading, orderItem);
				LOG.debug("Load is {}", new Object[] { partLoading.getLoad() });
				updateOrderItemNumeratorAndDenominator(orderItem, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);

			}
		}

		// 2. 'To' part
		final AssignmentPartEntity partTo = new AssignmentPartEntity();
		assignmentParts.add(partTo);
		partTo.setAssignment(assignment);

		partTo.setAssignmentPartSeqNo(seqno);
		partTo.setAssignmentPartType(partTypeTo);
		partTo.setPlannedStart(c.getTime());
		partTo.setAssignPartStatus(statusNew);
		partTo.setHomebaseFlag(Boolean.FALSE);
		partTo.setLnfShiptoFlag(Boolean.FALSE);
		if (order != null) {
			if (LogonUtil.isLandfill(order)) {
				partTo.setShipto(shipto);
			}
			else {
				partTo.setShippingpoint(order.getShippingpoint());
			}
		}
		seqno++;

		// 3. 'Unload' part
		final AssignmentPartEntity partUnload = new AssignmentPartEntity();
		assignmentParts.add(partUnload);
		partUnload.setAssignment(assignment);

		partUnload.setAssignmentPartSeqNo(seqno);
		partUnload.setAssignmentPartType(partTypeUnload);
		c.add(Calendar.MINUTE, transportDuration);
		partUnload.setPlannedStart(c.getTime());
		partUnload.setAssignPartStatus(statusNew);
		partUnload.setOrder(order);
		partUnload.setExtOrder(extOrder);
		partUnload.setHomebaseFlag(Boolean.FALSE);
		if (order != null) {
			if (!LogonUtil.isLandfill(order)) {
				partUnload.setShipto(shipto);
				partUnload.setLnfShiptoFlag((shipto != null)
						&& shipto.getShiptoType().getShiptoTypeCd().equals(Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED));
				partUnload.setSiteName(order.getLnfSiteName());
				partUnload.setCoordLat(order.getLnfCoordLat());
				partUnload.setCoordLon(order.getLnfCoordLon());
				partUnload.setSiteCountry(order.getLnfSiteCountry());
				partUnload.setSiteCity(order.getLnfSiteCity());
				partUnload.setSitePostalCode(order.getLnfSitePostalCode());
				partUnload.setSiteStreetHouseNo(order.getLnfSiteStreetHouseNo());
			}
			else {
				partUnload.setShippingpoint(order.getShippingpoint());
				partUnload.setSiteName(order.getShippingpoint().getShippingpointName());
				partUnload.setCoordLat(order.getShippingpoint().getCoordLat());
				partUnload.setCoordLon(order.getShippingpoint().getCoordLon());
			}
		}
		else if (extOrder != null) { // External order
			ExtSiteEntity unloadSite = extOrder.getUnloadingExtSite();
			partUnload.setSiteCity(unloadSite.getSiteCity());
			partUnload.setSiteContactPersonTxt(unloadSite.getSiteContactPersonTxt());
			partUnload.setSiteCountry(unloadSite.getSiteCountry());
			partUnload.setSiteLoadUnload(unloadSite.getSiteLoadUnload());
			partUnload.setSiteName(unloadSite.getSiteName());
			partUnload.setSitePostalCode(unloadSite.getSitePostalCode());
			partUnload.setSiteStreetHouseNo(unloadSite.getSiteStreetHouseNo());
			partUnload.setCoordLat(unloadSite.getCoordLat());
			partUnload.setCoordLon(unloadSite.getCoordLon());
		}
		seqno++;

		Date lastPlannedStartBeforeFinish = null;

		//in case of landfill do not create return assignment part, because the unloading happens at the plant
		if (!LogonUtil.isLandfill(order)) {
			// 4. 'From' part
			AssignmentPartEntity partFrom = new AssignmentPartEntity();
			partFrom.setAssignment(assignment);
			assignmentParts.add(partFrom);
			partFrom.setAssignmentPartSeqNo(seqno);
			partFrom.setAssignmentPartType(partTypeFrom);
			c.add(Calendar.MINUTE, unloadDuration);
			partFrom.setPlannedStart(c.getTime());
			lastPlannedStartBeforeFinish = partFrom.getPlannedStart();
			partFrom.setAssignPartStatus(statusNew);
			partFrom.setShipto(shipto);
			partFrom.setHomebaseFlag(Boolean.FALSE);
			partFrom.setLnfShiptoFlag(partUnload.getLnfShiptoFlag());
			partFrom.setSiteName(partUnload.getSiteName());
			partFrom.setCoordLat(partUnload.getCoordLat());
			partFrom.setCoordLon(partUnload.getCoordLon());
			partFrom.setSiteCountry(partUnload.getSiteCountry());
			partFrom.setSiteCity(partUnload.getSiteCity());
			partFrom.setSitePostalCode(partUnload.getSitePostalCode());
			partFrom.setSiteStreetHouseNo(partUnload.getSiteStreetHouseNo());
			seqno++;
		}
		else {
			lastPlannedStartBeforeFinish = partUnload.getPlannedStart();
		}

		// 5. 'Finished' part
		AssignmentPartEntity partFinished = new AssignmentPartEntity();
		partFinished.setAssignment(assignment);
		assignmentParts.add(partFinished);
		partFinished.setAssignmentPartSeqNo(seqno);
		partFinished.setAssignmentPartType(partTypeFinished);

		//in case of landfill do not create return assignment part, because the unloading happens at the plant
		if (LogonUtil.isLandfill(order)) {
			c.add(Calendar.MINUTE, unloadDuration);
		}
		else {
			c.add(Calendar.MINUTE, transportDuration);
		}
		partFinished.setPlannedStart(c.getTime());
		partFinished.setAssignPartStatus(statusNew);
		partFinished.setLnfShiptoFlag(Boolean.FALSE);
		if (order != null) {
			/*
			 * Please see LOGON-4999 Implement Truck Home Plant related changes!
			 * The assignment part type 4 (driving back to plant) is created using the home plant
			 * of the truck (if maintained) else the delivery plant information from the order.
			 */
			if (truck.getReturnShippingpoint() != null) {
				partFinished.setShippingpoint(truck.getReturnShippingpoint());
				/*
				 * Setting the planned start of the FINISHED part to the planned start of the FROM part.
				 *
				 * Reason: This is required because we need to re-calculate the duration of the FINISHED part
				 * after setting the shippingpoint. By doing this it will invalidate the FINISHED assigment part.
				 * The scheduling screen will correct the invalid assignment part and we will get the correct duration.
				 */
				partFinished.setPlannedStart(lastPlannedStartBeforeFinish);
			}
			else {
				partFinished.setShippingpoint(order.getShippingpoint());
			}
		}
		else if (extOrder != null) {
			ExtSiteEntity loadSite = extOrder.getLoadingExtSite();
			partFinished.setSiteCity(loadSite.getSiteCity());
			partFinished.setSiteContactPersonTxt(loadSite.getSiteContactPersonTxt());
			partFinished.setSiteCountry(loadSite.getSiteCountry());
			partFinished.setSiteLoadUnload(loadSite.getSiteLoadUnload());
			partFinished.setSiteName(loadSite.getSiteName());
			partFinished.setSitePostalCode(loadSite.getSitePostalCode());
			partFinished.setSiteStreetHouseNo(loadSite.getSiteStreetHouseNo());
			partFinished.setCoordLat(loadSite.getCoordLat());
			partFinished.setCoordLon(loadSite.getCoordLon());
		}
		partFinished.setHomebaseFlag(Boolean.FALSE);

		assignment.setAssignmentParts(assignmentParts);
		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_NEW);
		assignment.setAssignTrStatus(referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
				Constant.ASSIGN_STATUS_TR_NOT_TRANSMITTED));
		seqno++;

		// External order related values
		if (extOrder != null) {
			assignment.setMaterialName(extOrder.getExtMaterial().getMaterialName());
			assignment.setMaterialQuantity(extOrder.getOrderedQuantity());
			assignment.setMaterialUom(extOrder.getOrderedUom());
		}

		// Normal order related stuff
		if (order != null) {
			assignment.setCarrierTruckDriverNote(order.getCarrierTruckDriverNote());
			assignment.setCarrierDispatchingNote(order.getCarrierDispatchingNote());

			OrderEntity[] orderA = { order };
			updateCarrierForOrders(truck, Arrays.asList(orderA), null);
		}

		return assignment;
	}

	/**
	 * creates an Assignment with the given data (incl load and Assignment_parts)
	 * changes order status to dispatched.
	 *
	 * @adnlog.exclude return
	 */
	@Deprecated
	public AssignmentEntity createAssignment(TruckEntity truck, WagonEntity wagon, OrderEntity order, Date startTime,
			TimeZone timeZone, int loadDuration, int transportDuration, int unloadDuration) {
		AssignmentEntity assignment = null;

		if (truck != null) {
			ShiptoEntity shipto = findShiptoBySapBpId(order);
			assignment = createNewRoadAssignment(truck, order, null, shipto, startTime, timeZone, loadDuration,
					transportDuration, unloadDuration);

			if (assignment == null) {
				//assignment was already created concurrently, nothing to do
				return null;
			}
		}
		else {

			if (assignmentExists(order)) {
				//assignment was already created concurrently, nothing to do
				return null;
			}

			assignment = createNewRailAssignment(wagon, order, startTime);

			String dn = "";
			if ((order.getNoteRailwayDisp() != null) && (order.getNoteRailwayDisp().length() > 0)) {
				dn = order.getNoteRailwayDisp();
			}
			if (dn.length() > 250) {
				dn = dn.substring(0, 250);
			}
			assignment.setCarrierDispatchingNote(dn);

		}

		if (assignment.getAssignmentId() == null) {
			assignment = getDaoProvider().getAssignmentEntityDao().persist(assignment);
		}

		if (LogonStatusUtils.isBeforeDispatched(order.getOrderStatus())) {
			final BizeventEntity event = new BizeventEntity();
			event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DISPATCHED);
			event.setOrder(order);
			event.setEventTs(new Date());

			monitoringTablesService.processBizevent(event);

			changeOrderStatus(order, new Long(Constant.ORDER_STATUS_DISPATCHED));
		}

		if (truck == null) {
			boolean orderLoaded = (countNotLoadedItems(null, order, true) == 0);
			if (orderLoaded) {
				changeOrderStatus(order, new Long(Constant.ORDER_STATUS_LOADED));
			}
		}

		return assignment;
	}

	/**
	 * creates an Assignment for Rail with the given data (incl load and Assignment_parts)
	 * changes order status to dispatched or loaded
	 *
	 * @param wagon
	 * @param order
	 * @param startTime
	 * @param loadDuration
	 * @param transportDuration
	 * @param unloadDuration
	 * @return AssignmentEntity
	 *
	 * @adnlog.exclude return
	 */
	public AssignmentEntity createAssignmentForRailWithOrderDispatchedOrLoaded(Long wagonId, Long orderNr,
			Date startTime, int loadDuration, int transportDuration, int unloadDuration) {
		WagonEntity wagon = getDaoProvider().getWagonEntityDao().findById(wagonId);

		final ReadOnlyDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter<OrderEntity> f = orderDao.createFilter();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNr));
		OrderEntity order = orderDao.findSingle(f);

		final boolean isContainerOrder = LogonUtil.isContainerOrder(order);
		if (wagon != null && !isContainerOrder) {
			final boolean isActiveAssignmentExistsForWagon = assignmentService.isActiveAssignmentExistsForWagon(wagon);
			if (isActiveAssignmentExistsForWagon) {
				LOG.warn("There is already an active assignment for the wagon: " + wagon.getWagonNo() + ".");
				throw new LogonException(Reason.RES_ORDER_ASSIGNMENT_EXISTS_FOR_WAGON);
			}
		}

		OrderStatusBsEntity orderStatus = order.getOrderStatus();
		final Long orderStatusCd = orderStatus.getOrderStatusCd();

		if (wagon != null && LogonStatusUtils.isBeforeDelivered(orderStatusCd)) {
			return createAssignmentForRailWithOrderDispatchedOrLoaded(wagon, order, startTime, loadDuration,
					transportDuration, unloadDuration);
		}
		throw new LogonException("Assignment creation failed for order " + orderNr + ".", Reason.RES_ORDER_WRONG_STATUS);
	}

	/**
	 * creates an Assignment for Rail with the given data (incl load and Assignment_parts)
	 * changes order status to dispatched or loaded
	 *
	 * @param wagon
	 * @param order
	 * @param startTime
	 * @param loadDuration
	 * @param transportDuration
	 * @param unloadDuration
	 * @return AssignmentEntity
	 *
	 * @adnlog.exclude return
	 */
	private AssignmentEntity createAssignmentForRailWithOrderDispatchedOrLoaded(WagonEntity wagon, OrderEntity order,
			Date startTime, int loadDuration, int transportDuration, int unloadDuration) {
		if (assignmentExists(order)) {
			return null;
		}
		LoadEntity LOAD_IS_UNAVAILABLE = null;
		Boolean STRICT = true;

		dispoService.transferEtranToOrder(wagon, order);

		AssignmentEntity assignment = createNewRailAssignment(wagon, order, startTime);

		String carrierDispatchingNote = "";
		if ((order.getNoteRailwayDisp() != null) && (order.getNoteRailwayDisp().length() > 0)) {
			carrierDispatchingNote = order.getNoteRailwayDisp();
			if (carrierDispatchingNote.length() > 250) {
				carrierDispatchingNote = carrierDispatchingNote.substring(0, 250);
			}
			assignment.setCarrierDispatchingNote(carrierDispatchingNote);
		}

		assignment = finalizeAssignmentCreationWithOrderStatusChange(assignment, order);

		boolean orderLoaded = (countNotLoadedItems(LOAD_IS_UNAVAILABLE, order, STRICT) == 0);
		if (orderLoaded) {
			changeOrderStatus(order, Constant.ORDER_STATUS_LOADED);
		}

		return assignment;
	}

	/**
	 * creates an Assignment for truck with the given data (incl load and Assignment_parts)
	 * changes order status to dispatched.
	 * @param truck
	 * @param orderNumber
	 * @param startTime
	 * @param loadDuration
	 * @param transportDuration
	 * @param unloadDuration
	 * @return AssignmentEntity
	 * @adnlog.exclude return
	 */
	public AssignmentEntity createAssignmentForTruckWithOrderDispatched(Long truckId, Long orderNumber, Date startTime,
			TimeZone timeZone, int loadDuration, int transportDuration, int unloadDuration) {
		OrderEntity order = findOrderByOrderNumber(orderNumber, ORDER_INITIALIZE_ONLY);
		return createAssignmentForTruckWithOrderDispatched(truckId, order, startTime, timeZone, loadDuration,
				transportDuration, unloadDuration);
	}

	/**
	 * creates an Assignment for truck with the given data (incl load and Assignment_parts)
	 * changes order status to dispatched.
	 * @param truck
	 * @param order
	 * @param startTime
	 * @param loadDuration
	 * @param transportDuration
	 * @param unloadDuration
	 * @return AssignmentEntity
	 * @adnlog.exclude return
	 */
	public AssignmentEntity createAssignmentForTruckWithOrderDispatched(Long truckId, OrderEntity order,
			Date startTime, TimeZone timeZone, int loadDuration, int transportDuration, int unloadDuration) {
		TruckEntity truck = getDaoProvider().getTruckEntityDao().findById(truckId);
		OrderStatusBsEntity orderStatus = order.getOrderStatus();
		final Long orderStatusCd = orderStatus.getOrderStatusCd();
		if (truck != null && LogonStatusUtils.isBeforeLoading(orderStatusCd)) {
			return createAssignmentForTruckWithOrderDispatched(truck, order, startTime, timeZone, loadDuration,
					transportDuration, unloadDuration);
		}
		throw new LogonException("Assignment creation failed for order " + order.getOrderNumber() + ".",
				Reason.RES_ORDER_WRONG_STATUS);
	}

	/**
	 * creates an Assignment for truck with the given data (incl load and Assignment_parts)
	 * changes order status to dispatched.
	 * @param truck
	 * @param order
	 * @param startTime
	 * @param loadDuration
	 * @param transportDuration
	 * @param unloadDuration
	 * @return AssignmentEntity
	 * @adnlog.exclude return
	 */
	private AssignmentEntity createAssignmentForTruckWithOrderDispatched(TruckEntity truck, OrderEntity order,
			Date startTime, TimeZone timeZone, int loadDuration, int transportDuration, int unloadDuration) {
		ExtOrderEntity EXTERNAL_ORDER_UNAVAILABLE = null;
		ShiptoEntity shipto = findShiptoBySapBpId(order);
		AssignmentEntity assignment;
		if (LogonUtil.isOrderWithReversedDirection(order)) {
			assignment = createNewReverseRoadAssignment(truck, order, EXTERNAL_ORDER_UNAVAILABLE, shipto, startTime,
					timeZone, loadDuration, transportDuration, unloadDuration);
		}
		else {
			assignment = createNewForwardRoadAssignment(truck, order, EXTERNAL_ORDER_UNAVAILABLE, shipto, startTime,
					timeZone, loadDuration, transportDuration, unloadDuration);
		}
		return finalizeAssignmentCreationWithOrderStatusChange(assignment, order);
	}

	/**
	 * It's persisting the assignment if needed and if the related order's status is before ORDER_STATUS_DISPATCHED
	 * then it will be changed to ORDER_STATUS_DISPATCHED
	 * @param assignment
	 * @param order
	 * @return assignmentEntity
	 */
	private AssignmentEntity finalizeAssignmentCreationWithOrderStatusChange(AssignmentEntity assignment,
			OrderEntity order) {
		if (assignment == null) {
			return null;
		}
		if (assignment.getAssignmentId() == null) {
			assignment = getDaoProvider().getAssignmentEntityDao().persist(assignment);
		}

		OrderStatusBsEntity orderStatus = order.getOrderStatus();
		Long orderStatusCd = orderStatus.getOrderStatusCd();
		if (LogonStatusUtils.isBeforeDispatched(orderStatusCd)) {
			final BizeventEntity event = new BizeventEntity();
			event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DISPATCHED);
			event.setOrder(order);
			event.setAssignment(assignment);
			event.setEventTs(new Date());
			monitoringTablesService.processBizevent(event);
			changeOrderStatus(order, Constant.ORDER_STATUS_DISPATCHED);
		}
		return assignment;
	}

	/**
	 * Sets manually scheduled flag in the corresponding assignment of the order according
	 * to the given weigh system lp (lds/logon). If no assignment found for the order, or
	 * given weighing system is pda, nothing happens.
	 *
	 * @param order - the order whose flag will be set
	 * @param weighSystemLp - constant indicating the caller client
	 *
	 */
	public void setManuallyScheduledFlag(OrderEntity order, long weighSystemLp) {
		AssignmentPartEntity assignmentPartUnload = getAssignmentPartUnload(order);
		if (assignmentPartUnload != null && assignmentPartUnload.getAssignment() != null) {
			AssignmentEntity assignment = assignmentPartUnload.getAssignment();
			if (weighSystemLp == Constant.WEIGH_SYSTEM_LP_WS) {
				assignment.setManuallyScheduled(false);
			}
			else if (weighSystemLp == Constant.WEIGH_SYSTEM_LP_LOGON) {
				assignment.setManuallyScheduled(true);
			}
		}
	}

	/**
	 * Checks if an assignment already exists for the current order.
	 * More specifically, the method checks if there is any active assignment with
	 * an assignment part that belongs to the specified order.
	 * @param order A delivery order
	 * @return true If there exists an active assignment with a part that belongs to <code>order</code>,
	 * false otherwise.
	 */
	private boolean assignmentExists(OrderEntity order) {
		final ReadWriteDao<AssignmentPartEntity> assignmentPartDao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> f = assignmentPartDao.createFilter();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, order.getOrderId()));
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG,
				Boolean.TRUE));
		return assignmentPartDao.count(f) > 0;
	}

	private ShiptoEntity findShiptoBySapBpId(OrderEntity order) {
		/* marius/2012-10-24: In certain cases this method is called with a newly
		 * initialized order that doesn't have a shipto SAP BP set yet.
		 * Then we just fail gracefully and indicate that no shipto was found.
		 */
		if (order.getShiptoSapBp() == null || order.getShiptoSapBp().getSapBpId() == null) {
			LOG.debug("Asking for shipto of order where no shiptoSapBP is set, returning null.");
			return null;
		}
		ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
		Filter<ShiptoEntity> f = shiptoDao.createFilter();
		f.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, order.getShiptoSapBp().getSapBpId()));
		f.addExpression(Restrictions.eq(ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		return shiptoDao.findSingle(f);
	}

	/**
	 * creates an Assignment with the given data (incl load and Assignment_parts)
	 * changes order status to dispatched.
	 *
	 * @adnlog.exclude return
	 */
	public AssignmentEntity createAssignmentForExternalOrder(Long truckId, Long extOrderNr, Date startTime,
			TimeZone timeZone, int loadDuration, int transportDuration, int unloadDuration) {
		ReadWriteDao<ExtOrderEntity> extOrderEntityDao = getDaoProvider().getExtOrderEntityDao();
		Filter<ExtOrderEntity> filter1 = extOrderEntityDao.createFilter();
		filter1.addExpression(Restrictions.eq(ExtOrderEntity.ATTR_EXT_ORDER_NUMBER, extOrderNr));
		ExtOrderEntity extOrder = extOrderEntityDao.findSingle(filter1);
		ReadWriteDao<AssignmentPartEntity> assignmentPartDao = getDaoProvider().getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> filter2 = assignmentPartDao.createFilter();
		filter2.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_EXT_ORDER_ID, extOrder.getExtOrderId()));
		filter2.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "."
				+ AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		long assignmentPartCount = assignmentPartDao.count(filter2);
		if (assignmentPartCount > 0) {
			//assignment was already created concurrently, nothing to do
			return null;
		}

		AssignmentEntity assignment = null;
		TruckEntity truck = null;
		if (truckId != null) {
			truck = getDaoProvider().getTruckEntityDao().findById(truckId);
		}

		if (truck != null) {
			assignment = createNewRoadAssignment(truck, null, extOrder, null, startTime, timeZone, loadDuration,
					transportDuration, unloadDuration);
			assignment.setCarrierTruckDriverNote(extOrder.getNoteTruckDriver());
			assignment.setCarrierDispatchingNote("");
			extOrder.setCarrier(truck.getCarrier());
		}
		else {
			throw new IllegalArgumentException("Truck cannot be null in case of external orders");
		}

		assignment = getDaoProvider().getAssignmentEntityDao().persist(assignment);

		if (LogonStatusUtils.isBeforeDispatched(extOrder.getOrderStatus())) {
			changeExternalOrderStatus(extOrder, new Long(Constant.ORDER_STATUS_DISPATCHED));
		}

		return assignment;
	}

	/**
	 * Creates and dispatches a special assignment for a rail wagon
	 * @param wagon the wagon that the order will be assigned to.
	 * @param order unpersisted OrderEntity containing the order and order item data, created like in creteRailReturnOrder() or cereateRailMantenanceOrder()
	 *		          an existing open load on the wagon will be taken over implicitly if the weight, material and production plant are the same, else a new load is created.
	 * @param startTime time at which the order is to be scheduled (it will be scheduled at the next "Stellzeit" after startTime)
	 * @return The returned order will be in status loaded (but not "transmitted to SBB"). Transmission to SBB will be handled automatically when the wagon arrives
			at the departure shipto (if it is not already there), or needs to be handled explicitly by the caller (e.g. with a save & transmit button).
	 */
	public AssignmentEntity createSpecialRailAssignment(WagonEntity wagon, OrderEntity order, Date startTime,
			OrderRepEntity orderRep) {
		wagon = getDaoProvider().getWagonEntityDao().merge(wagon);

		// LOGON-5514
		ConfigBean configBean = getConfigBean();
		DefaultFallbackLogic defaultFallbackLogic = new DefaultFallbackLogic(configBean);

		final ShiptoSareaEntity shiptoSarea = fetchShiptoSarea(order.getShiptoSapBp(), order.getSarea());
		final ShiptoEntity shipto = shiptoSarea.getShipto();

		if (order.getPlannedUnloadingTimeMin() == null) {
			OrderItemEntity firstOrderItem = order.getOrderItems().get(0);
			Long plannedUnloadingTimeMin = defaultFallbackLogic.getPlannedUnloadingTimeValue(firstOrderItem, shipto);
			order.setPlannedUnloadingTimeMin(plannedUnloadingTimeMin);
		}

		defaultFallbackLogic.setPlannedLoadingTimeForOrderItem(shipto, order.getOrderItems());

		if (order.getSapSuppressMinChargeFlag() == null) {
			order.setSapSuppressMinChargeFlag(false);
		}
		if (order.getNoPdaOrRailEventsFlag() == null) {
			order.setNoPdaOrRailEventsFlag(false);
		}
		if (order.getReschedulingFlag() == null) {
			order.setReschedulingFlag(false);
		}
		// end LOGON-5514

		order = persistOrder(order);
		if (kafkaPublisherService != null) {
			kafkaPublisherService.publishNewOrder(order);
		}

		if (order.getOrderNumber() == null) {
			order.setOrderNumber(fetchSequenceNextVal("SLGNC_ORDER_NUMBER"));
			order.setArchivingStatus(referentialDataService.findBsEntity(ArchivingStatusBsEntity.class,
				Constant.ARCHIVING_STATUS_NEW));
		}

		if (order.getShippingpoint().getRailwayStation() != null && order.getShiptoSapBp().getRailwayStation() != null
				&& startTime != null) {

			Date[] dates = getDepartureArrivalDate(order.getShippingpoint().getRailwayStation().getRailwayStationId(),
					order.getShiptoSapBp().getRailwayStation().getRailwayStationId(), startTime);

			if (dates != null && order.getActualDeliveryDat() == null) {
				order.setActualDeliveryDat(dates[1]);
			}
		}

		if (orderRep != null) {
			orderRep.setOrder(order);
			orderRep.setEnterPlantDat(order.getActualDeliveryDat());
			getDaoProvider().getOrderRepEntityDao().persist(orderRep);
			if (LOG.isDebugEnabled()) {
				LOG.debug("OrderRepEntity was successfully persisted. OrderRepEntity id = " + orderRep.getOrderRepId());
			}

		}
		AssignmentEntity assignment = createNewRailAssignment(wagon, order, startTime);
		if (assignment != null) {
			changeOrderStatus(order, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);
			changeOrderStatus(order, Constant.ORDER_STATUS_DISPATCHED);
			getDaoProvider().getAssignmentEntityDao().persist(assignment);

			BizeventEntity specialEvent = new BizeventEntity();
			changeOrderStatus(order, Constant.ORDER_STATUS_LOADED);

			specialEvent.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class,
					Constant.BIZEVENT_TYPE_NEW));
			specialEvent.setAssignment(assignment);
			specialEvent.setCoordLat(order.getShippingpoint().getCoordLat());
			specialEvent.setCoordLon(order.getShippingpoint().getCoordLon());
			specialEvent.setEventTs(new Date());
			specialEvent.setOrder(order);
			specialEvent.setRailwayStation(order.getShippingpoint().getRailwayStation());
			specialEvent.setShippingpoint(order.getShippingpoint());
			specialEvent.setWagon(wagon);

			List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();

			for (AssignmentPartEntity part : assignmentParts) {
				if (part.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
					//in case the return order has a only a service item, this might not be reached since there is no load part, but it can also contain normal material too!
					part.setRealStart(specialEvent.getEventTs());
					LoadEntity load = part.getLoad();
					if (load != null) {
						OrderItemEntity loadOrderItem = null;
						Collection<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
						for (OrderItemEntity orderItem : orderItems) {
							if (orderItem.getLoad().getLoadId().equals(load.getLoadId())) {
								loadOrderItem = orderItem;
								break;
							}
						}
						if (load.getLoadingTs() == null) {
							load.setLoadingTs(specialEvent.getEventTs());
						}
						if (loadOrderItem != null) {
							//set the loaded quantity in case of bag item, just as it is always pre-set in normal case ->LOGON-3770
							if (LogonPackageHandlingUtil.isBag(loadOrderItem.getSapMaterial())) {
								loadOrderItem.setLoadedQuantity(loadOrderItem.getOrderedQuantity());
								loadOrderItem.setLoadedUom(loadOrderItem.getLoadedUom());
								updateOrderItemNumeratorAndDenominator(loadOrderItem,
										Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR);
							}
							load.setNetQuantityUom(loadOrderItem.getOrderedUom());
							if (load.getNetQuantity() == null) {
								load.setNetQuantity(loadOrderItem.getOrderedQuantity());
								updateGrossWeightT(load, null);
							}
						}
					}
				}
			}

			monitoringTablesService.processBizevent(specialEvent);

			//Investigate what was here for 1.6 probably it logged error? if so there is no need to audit here.
			//getServices().getBizeventAuditService().persistAuditData(specialEvent);
			return assignment;
		}

		throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
	}

	/**
	 * changes the truck assigned to the order
	 */
	public AssignmentEntity moveAssignment(Long truckId, Long assignmentId) {
		ReadWriteDao<OrderItemEntity> orderItemEntityDao = getDaoProvider().getOrderItemEntityDao();
		ReadWriteDao<TruckEntity> truckDao = getDaoProvider().getTruckEntityDao();
		TruckEntity newTruck = truckDao.findById(truckId);
		ReadWriteDao<AssignmentEntity> assignmentDao = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = assignmentDao.findById(assignmentId);

		AssignTrStatusBsEntity previousTrStatus = assignment.getAssignTrStatus();

		final TruckEntity originalTruck = assignment.getTruck();

		setPrePlannedData(newTruck, assignment);
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
		for (AssignmentPartEntity assignmentPartEntity : assignmentParts) {
			if (assignmentPartEntity.getAssignmentPartType().getAssignmentPartTypeCd().longValue() != Constant.ASSIGNMENT_PART_TYPE_LOAD) {
				continue;
			}
			LoadEntity load = assignmentPartEntity.getLoad();
			if (load == null) {
				continue;
			}
			Filter<OrderItemEntity> filter = orderItemEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD_ID, load.getLoadId()));
			List<OrderItemEntity> orderItemList = orderItemEntityDao.find(filter);
			for (OrderItemEntity orderItemEntity : orderItemList) {
				checkLoadTakeover(newTruck, null, null, assignmentPartEntity, orderItemEntity);
				updateOrderItemNumeratorAndDenominator(orderItemEntity, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);
			}
		}
		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_NEW);
		assignment.setAssignTrStatus(referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
				Constant.ASSIGN_STATUS_TR_NOT_TRANSMITTED));
		assignment.setTruck(newTruck);

		final List<OrderEntity> ordersFromAssignment = AssignmentUtils.getOrdersFromAssignment(assignment);

		updateCarrierForOrders(newTruck, ordersFromAssignment, originalTruck);

		assignment.setManuallyScheduled(Boolean.TRUE);

		final boolean fromPlanningToRealTruck = LogonUtil.isPlanningTruck(originalTruck)
				&& !LogonUtil.isPlanningTruck(newTruck);

		final boolean wasTransmittedToTruckOriginally = previousTrStatus.getAssignTrStatusCd().compareTo(
				Long.valueOf(Constant.ASSIGN_STATUS_TR_TRANSMITTED)) == 0;

		if (fromPlanningToRealTruck && wasTransmittedToTruckOriginally) {
			// second and third parameter not used in the actual implementation as of 2015.02.23
			dispoService.transmitToDriver(assignmentId, -1, -1, BizEventUtil.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK, true);
		}
		return assignment;
	}

	/**
	 * Case from planning to real truck:
	 *
	 * The order can be a subject of prescheduling. In this case whenever the assignment is deallocated it is
	 * set into status {@link Constant#ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER}, which precedes
	 * status {@link Constant#ASSIGNMENT_STATUS_LOADING}. Whenever the assignment is in this status the order
	 * can be below status {@link Constant#ORDER_STATUS_DISPATCHED}. If someone is trying to create a new assignment
	 * in case there is such an assignment in the background that means that the assignment in status {@link Constant#ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER}
	 * has to be continued, guaranteeing, that all that data, which is done by the prescheduler is backed up in the
	 * dedicated fields {@link AssignmentEntity#getPlanningTruck()} and {@link AssignmentPartEntity#getPrePlannedStart()}
	 * which is not going to change anymore from this point on.
	 *
	 * Case from real truck to planning truck:
	 *
	 * In case the order
	 *
	 *
	 * @param truck
	 * @param assignment
	 */
	private void setPrePlannedData(TruckEntity truck, AssignmentEntity assignment) {
		final boolean fromPlanningToRealTruck = LogonUtil.isPlanningTruck(assignment.getTruck())
				&& !LogonUtil.isPlanningTruck(truck);
		final boolean fromRealToPlanningTruck = !LogonUtil.isPlanningTruck(assignment.getTruck())
				&& LogonUtil.isPlanningTruck(truck);

		if (fromPlanningToRealTruck) {
			assignment.setPlanningTruck(assignment.getTruck());
		}
		else if (fromRealToPlanningTruck) {
			final TruckEntity planningTruck = assignment.getPlanningTruck();
			if (planningTruck == null) {
				/**
				 * There was no planning involved, react as if this would be the first moveAssignment,
				 * simply move to the planning truck. There is no need to restore the planning starts
				 * as actually there is no such data what could be restored.
				 */
				return;
			}
			assignment.setPlanningTruck(null);
			assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER);
		}
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
		for (AssignmentPartEntity assignmentPartEntity : assignmentParts) {
			if (fromPlanningToRealTruck) {
				/**
				 * Back up the planning data to the dedicated fields, as the planning is not going to change anymore.
				 */
				assignmentPartEntity.setPrePlannedStart(assignmentPartEntity.getPlannedStart());
			}
			else if (fromRealToPlanningTruck) {
				/**
				 * The order is deallocated from the real truck ->
				 */
				assignmentPartEntity.setPlannedStart(assignmentPartEntity.getPrePlannedStart());
				assignmentPartEntity.setPrePlannedStart(null);
			}
		}
	}

	/**
	 * Deletes the assignment on the weighing system.
	 */
	public void cancelOnWeighSystem(AssignmentEntity assignment, ShippingpointEntity sp) {
		ldsService.sendDeleteLoadOrder(assignment, sp);
	}

	/**
	 * Assigns the pre-existing load of the assingment's truck to the assignment
	 */
	public void takeoverLoad(Long assignmentId) {
		ReadOnlyDao<AssignmentEntity> assignmentDao = getDaoProvider().getAssignmentEntityDao();
		final AssignmentEntity assignment = assignmentDao.findById(assignmentId);

		if (assignment == null) {
			LOG.warn("Couldn't find assignment with assignmentId: " + assignmentId);
			return;
		}

		final long status = assignment.getAssignStatus().getAssignStatusCd().longValue();
		if ((status == Constant.ASSIGNMENT_STATUS_UNLOADING) || (status == Constant.ASSIGNMENT_STATUS_RETURNING)
				|| (status == Constant.ASSIGNMENT_STATUS_FINISHED)) {
			return;
		}

		final List<AssignmentPartEntity> loadAssignmentParts = new ArrayList<AssignmentPartEntity>();
		final List<AssignmentPartEntity> unloadAssignmentParts = new ArrayList<AssignmentPartEntity>();
		final List<AssignmentPartEntity> sortedAssignmentParts = AssignmentUtils.getSortedAssignmentParts(assignment,
				loadAssignmentParts, unloadAssignmentParts);

		// don't allow takeover if there are more then one load or more then one unload part
		if (loadAssignmentParts.size() > 1 || unloadAssignmentParts.size() > 1) {
			return;
		}

		AssignmentPartEntity loadPart = null;
		AssignmentPartEntity prePart = null;
		OrderItemEntity item = null;

		for (AssignmentPartEntity ap : sortedAssignmentParts) {
			if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD) {
				prePart = ap;
			}
			else if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
				loadPart = ap;
			}
			else if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
				final OrderEntity order = ap.getOrder();
				if (order != null && order.getOrderItems() != null) {
					final List<OrderItemEntity> materialOrderItems = LogonPackageHandlingUtil.getMaterialItems(order.getOrderItems());
					if (materialOrderItems != null && materialOrderItems.size() == 1) {
						item = materialOrderItems.get(0);
					}
				}
			}
		}

		if (loadPart != null && item != null) {
			final OrderEntity order = item.getOrder();
			final LoadEntity load = checkLoadTakeover(assignment.getTruck(), null, null, loadPart, item);
			if (load.getLoadingTs() != null) {
				cancelOnWeighSystem(assignment, order.getShippingpoint());
				if (order.getOrderStatus().getOrderStatusCd().longValue() != Constant.ORDER_STATUS_DISPATCHED) {
					changeOrderStatus(order, Constant.ORDER_STATUS_SHIPPING);
					order.setReschedulingFlag(true);
					assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_SHIPPING);

					final int indexOfLoadPart = sortedAssignmentParts.indexOf(loadPart);
					final Date now = new Date();
					if (indexOfLoadPart > -1 && sortedAssignmentParts.size() > (indexOfLoadPart + 1)) {
						final AssignmentPartEntity nextPart = sortedAssignmentParts.get(indexOfLoadPart + 1);
						nextPart.setRealStart(now);
						if (loadPart.getRealStart() == null) {
							loadPart.setRealStart(nextPart.getRealStart());
						}
						if (prePart != null && prePart.getRealStart() == null) {
							prePart.setRealStart(nextPart.getRealStart());
						}
					}

					if (!assignment.getTruck().getPdaAvailableFlag().booleanValue() && (
							assignment.getTruck().getITrackAvailableFlag() == null || !assignment.getTruck()
									.getITrackAvailableFlag())) {
						closeAfterLoad(assignment, now);
					}
					else if (order.getSarea().getITrackAvailableFlag() != null && order.getSarea().getITrackAvailableFlag()) {
						final Boolean isOrderITrackAvailable = order.getOrderItems()
								.stream()
								.anyMatch(orderItem -> {
									SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
									MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
									String materialPackageCode = materialPackage.getMaterialPackageCd();
									if (Constant.bulkOrderMaterialPackageCds.contains(materialPackageCode)) {
										return true;
									}
									return false;
								});
						order.setITrackAvailableFlag(isOrderITrackAvailable);
					}
					else {
						order.setITrackAvailableFlag(Boolean.FALSE);
					}
				}
			}
		}
	}

	/**
	 * Sets the return shipping point for the assignment.
	 * The caller has also to pass the duration of the trip from the shipto to the new shipping point.
	 * mandatory fields:
	 * 	- truck
	 *
	 *  assignment passed separately, possibly not persisted
	 */
	public void saveAssignment(BizeventEntity event, AssignmentEntity assignment) {
		// Truck filled if coming from Scheduling Screen
		// Simple Vehicle filled from Simple Vehichle Scheduling Screen
		if (event.getTruck() == null && assignment.getSimpleVehicle() == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		ReadWriteDao<AssignmentEntity> apDao = getDaoProvider().getAssignmentEntityDao();
		if (assignment.getAssignmentId() == null) {
			apDao.persist(assignment);
			event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_CREATE_EXTERNAL_ASSIGNMENT);
		}
		else {
			assignment = apDao.merge(assignment);
		}
		event.setAssignment(assignment);
	}
	private void setDelivered(BizeventEntity event, boolean createReturnOrder) {
		setDelivered(event, createReturnOrder, false);
	}

	private void setDelivered(BizeventEntity event, boolean createReturnOrder, boolean isLdsOffline) {
		OrderEntity order = event.getOrder();
		AssignmentEntity assignment = event.getAssignment();
		if (order != null) {
			updateServiceItemDeliveredFields(order, assignment);
		}

		if ((order != null) && order.getOrderStatus().getOrderStatusCd().longValue() != Constant.ORDER_STATUS_FINISHED) {
			long orderStatus = Constant.ORDER_STATUS_DELIVERED;
			if (isLdsOffline) {
					orderStatus = Constant.ORDER_STATUS_DELIVERED_PENDING_LDS_DATA;
			}

			changeOrderStatus(order, orderStatus);
		}

		List<AssignmentPartEntity> assignmentPartList = AssignmentUtils.getSortedAssignmentParts(assignment);

		if (!AssignmentUtils.isInternal(assignment)) {
			for (AssignmentPartEntity ap : assignmentPartList) {
				if ((ap.getExtOrder() != null)
						&& (ap.getExtOrder().getOrderStatus().getOrderStatusCd().longValue() != Constant.ORDER_STATUS_FINISHED)) {
					changeExternalOrderStatus(ap.getExtOrder(), Constant.ORDER_STATUS_DELIVERED);
					ap.getExtOrder().setDeliveredTruck(assignment.getTruck());
					break;
				}
			}
		}

		//copy back lnf adress if necessary -> first find unloading ap
		AssignmentPartEntity apunload = null;
		if (order != null) {
			for (AssignmentPartEntity currAp : assignmentPartList) {
				if (AssignmentUtils.isUnloadPart(currAp) && (currAp.getOrder() != null)
						&& (currAp.getOrder().getOrderId().longValue() == order.getOrderId().longValue())) {
					apunload = currAp;
					break;
				}
			}

			// check for lnf flag and copy fields
			if ((apunload != null) && (apunload.getShipto() != null) && apunload.getLnfShiptoFlag().booleanValue()) {
				order.setLnfCoordLat(apunload.getCoordLat());
				order.setLnfCoordLon(apunload.getCoordLon());
				order.setLnfSiteCity(apunload.getSiteCity());
				order.setLnfSiteCountry(apunload.getSiteCountry());
				order.setLnfSiteName(apunload.getSiteName());
				order.setLnfSitePostalCode(apunload.getSitePostalCode());
				order.setLnfSiteStreetHouseNo(apunload.getSiteStreetHouseNo());
			}

			//Cost Calculation (Don't calculate for return orders)
			List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
			if (!orderItems.isEmpty()
					&& (orderItems.get(0)).getSapMaterial().getBaseUom().getUomCd().equals(Constant.UOM_TONS)) {
				try {
					contributionCalcService.updateCosts(order);
					getDaoProvider().getDirectJpaDao().flush(); // order final needs this to be persisted because it uses a view on this data
				}
				catch (LogonException e) {
					LOG.error("Cost Calculation Error: " + e.getReason() + ", " + e.getMessage());
				}
			}
			// Since pickup orders are set to delivered in closeAfterLoad
			// (non-pda), this will never be called, but for consistency it's here as well.

			Long deliveryTypeCd = order.getDeliveryType().getDeliveryTypeCd();
			if (LogonUtil.isPickupOrder(deliveryTypeCd) || LogonUtil.isShipPickupOrder(deliveryTypeCd)
					|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd)) {
				generateAndSendPDFWithoutExplicitLoadingpoint(order, true, true, false);
			}
			updateRepData(event);
		}

		if(order != null && Boolean.TRUE.equals(order.getITrackAvailableFlag())) {
			ItrackStatusBsEntity itrackStatusBsEntity = referentialDataService.findBsEntity(ItrackStatusBsEntity.class,
					ITrackStatus.DELIVERED_UNLOAD_INFORMATION_MISSING.getStatusCode());
			order.setItrackStatus(itrackStatusBsEntity);
		}

		Date ts = event.getEventTs();
		Iterator<AssignmentPartEntity> it = assignmentPartList.iterator();
		AssignmentPartEntity ap = it.next();
		while (it.hasNext()) {
			AssignmentPartEntity prevAp = ap;
			ap = it.next();
			switch (ap.getAssignmentPartType().getAssignmentPartTypeCd().intValue()) {
			case (int) Constant.ASSIGNMENT_PART_TYPE_UNLOAD:
				if ((ap.getOrder() != null) && !ap.getOrder().getOrderId().equals(order.getOrderId())) {
					if (ap.getOrder().getOrderStatus().getOrderStatusCd().longValue() < Constant.ORDER_STATUS_DELIVERED) {
						// there's more orders to deliver
						assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_SHIPPING);
						prevAp.setRealStart(ts);
						return;
					}
				}
				if ((ap.getRealStart() != null) && ap.getRealStart().after(ts)) {
					ts = ap.getRealStart();
				}
				break;
			case (int) Constant.ASSIGNMENT_PART_TYPE_FROM:
				ap.setRealStart(ts);
				assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_RETURNING);
				return;
			case (int) Constant.ASSIGNMENT_PART_TYPE_FINISHED:
				ap.setRealStart(ts);
				assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_FINISHED);
				break;
			default:
				break;
			}
		}

		checkNextAssignment(event, createReturnOrder);
	}

	private final void updateRepData(BizeventEntity event) {
		final OrderEntity order = event.getOrder();
		if (!LogonUtil.isRailRepairOrder(order.getDeliveryType().getDeliveryTypeCd(),
				order.getDistributionDest().getDistributionDestCd())) {
			return;
		}

		ReadWriteDao<OrderRepEntity> orderRepEntityDao = getDaoProvider().getOrderRepEntityDao();
		Filter<OrderRepEntity> filter = orderRepEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(OrderRepEntity.ATTR_ORDER, order));
		final OrderRepEntity orderRep = orderRepEntityDao.findSingle(filter);

		if (orderRep == null) {
			throw new LogonException("A 'REP' Order does not have a corresponding OrderRep entity, "
					+ "cannot refresh last/next maintenance time on the Wagon!", Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		final WagonEntity wagon = event.getWagon();
		final List<MaintTypeBsEntity> orderRepMaintTypes;
		final List<MaintTypeBsEntity> allRepMaintTypes;
		final Map<MaintTypeBsEntity, MaintRhythmEntity> rhythmMap;
		final Map<MaintTypeBsEntity, WagonMaintEntity> wmeMap;

		// maintenance types for this rep order
		orderRepMaintTypes = getOrderRepMaintTypes(orderRep);

		// all maintenance types
		allRepMaintTypes = referentialDataService.findBsEntityList(MaintTypeBsEntity.class);

		// rhythm map for all maintenance types
		rhythmMap = getMaintRhythmMap(wagon.getMaintRhythmType(), allRepMaintTypes);

		// get wagon maintenance types
		wmeMap = getWagonMaintenence(wagon);

		if (orderRep.getLeavePlantDat() == null) {
			orderRep.setLeavePlantDat(event.getEventTs());
		}
		final Date lastMaintDate = orderRep.getLeavePlantDat();

		Date repDate;
		MaintRhythmEntity mrEntity;
		WagonMaintEntity wmEntity;
		WagonMaintEntity wmDependantEntity;
		for (MaintTypeBsEntity repMaintType : allRepMaintTypes) {

			Date nextMaintDate = null;
			// get the wagon maintenance for this maintType
			wmEntity = wmeMap.get(repMaintType);
			if (wmEntity == null) {
				// there is no wagon maintenance for this maintenance type -> skip
				continue;
			}

			// get the rhythm for this maintType
			mrEntity = rhythmMap.get(repMaintType);

			if (mrEntity == null) {
				// there is no rhythm rule for this maintenance type -> next date is undetermined
				if (orderRepMaintTypes.contains(repMaintType)) {
					wmEntity.setNextDat(null);
					wmEntity.setLastDat(lastMaintDate);
				}
				continue;
			}

			if (orderRepMaintTypes.contains(repMaintType)) {
				if (wmEntity.getShiptoSapBp() == null) {
					wmEntity.setShiptoSapBp(event.getOrder().getShiptoSapBp());
				}
				wmEntity.setLastDat(lastMaintDate);

			}
			//					else {

			Date calculatedNextMaintDate = null;
			final List<MaintRhythmDependEntity> dependencies = mrEntity.getDependencies();
			if (LOG.isDebugEnabled()) {
				LOG.debug("updateRepData:[----------------------------------------------------------------------------------------------------------------------------]");
				LOG.debug("updateRepData:[MAINT_RHYTHM_CD=" + mrEntity.getMaintType().getMaintTypeCd()
						+ ", number of dependencies" + dependencies.size() + "]");
			}

			for (MaintRhythmDependEntity dependency : dependencies) {

				if (LOG.isDebugEnabled()) {
					LOG.debug("updateRepData:MAINT_RHYTHM_DEPEND_ID=" + dependency.getMaintRhythmDependId()
							+ ". [MAINT_RHYTHM_CD=" + mrEntity.getMaintType().getMaintTypeCd()
							+ ", DEPEND_MAINT_TYPE_CD=" + dependency.getDependMaintType().getMaintTypeCd() + "]");
				}
				wmDependantEntity = wmeMap.get(dependency.getDependMaintType());
				if (wmDependantEntity == null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Did not find the TLGNR_MAINT_TYPE_BS.MAINT_TYPE_CD in wagon maintenance map retrieved for TLGNC_WAGON_MAINT.WAGON_ID="
								+ wagon.getWagonId() + ".");
					}
					continue;
				}

				// recalculate this next maintenance for all depending on this
				if (!orderRepMaintTypes.contains(dependency.getDependMaintType())) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("updateRepData:Order rep DOES NOT contain DEPEND_MAINT_TYPE_CD="
								+ dependency.getDependMaintType().getMaintTypeCd()
								+ " using last dat from DB. [WAGON_MAINT_TYPE_CD="
								+ wmDependantEntity.getMaintType().getMaintTypeCd() + " LAST_DAT="
								+ wmDependantEntity.getLastDat() + "]");
					}
					repDate = wmDependantEntity.getLastDat();
				}
				else {
					if (LOG.isDebugEnabled()) {
						LOG.debug("updateRepData:Order rep DOES contain DEPEND_MAINT_TYPE_CD="
								+ dependency.getDependMaintType().getMaintTypeCd()
								+ " using LEAVE_PLANT_DAT. [WAGON_MAINT_TYPE_CD="
								+ wmDependantEntity.getMaintType().getMaintTypeCd() + "]");
					}
					repDate = lastMaintDate;
				}

				// recalculate this next maintenance
				calculatedNextMaintDate = MaintenanceUtil.getNextMaintDate(repDate, mrEntity);

				if (calculatedNextMaintDate != null) {
					if ((nextMaintDate == null) || (nextMaintDate.after(calculatedNextMaintDate))) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("updateRepData:Calculated date is the soonest from now DATE='"
									+ (DateTimeUtils.formatDayMonthYear(calculatedNextMaintDate, Locale.ENGLISH) + " " + DateTimeUtils.formatHoursMinutes(calculatedNextMaintDate))
									+ "'");
						}
						nextMaintDate = calculatedNextMaintDate;
					}
				}
			}
			//					}

			if (LOG.isDebugEnabled()) {
				LOG.debug("updateRepData:Setting next dat of "
						+ wmEntity.getMaintType().getMaintTypeCd()
						+ " for wagon "
						+ wagon.getWagonId()
						+ " to NEXT_DAT='"
						+ (DateTimeUtils.formatDayMonthYear(nextMaintDate, Locale.ENGLISH) + " " + DateTimeUtils.formatHoursMinutes(nextMaintDate))
						+ "'");
				LOG.debug("updateRepData:[----------------------------------------------------------------------------------------------------------------------------]");
			}

			wmEntity.setNextDat(nextMaintDate);
		}

		clearWagonMaintDataAtDelivery(wagon);
	}

	/**
	 * This method clears the following data:
	 * <li>Note Maintenance from {@link WagonEntity}</li>
	 * <li>All the {@link WagonDmgcodeTypeEntity} associated with the wagon</li>
	 * <li>All the {@link WagonRepAttEntity} associated with the wagon</li>
	 * <br />
	 * <b>WARNING: This method modifies the parameter!</b>
	 * @param wagon The wagon which aforementioned fields are to cleared
	 */
	private void clearWagonMaintDataAtDelivery(WagonEntity wagon) {
		wagon.setWagonNoteMaintenance("");
		wagon.getDamageCdList().clear();
		wagon.getWagonRepAttachments().clear();
		em.merge(wagon);
	}

	private final List<MaintTypeBsEntity> getOrderRepMaintTypes(OrderRepEntity orderRep) {
		final List<MaintTypeBsEntity> orderRepMaintTypes = new ArrayList<MaintTypeBsEntity>();
		final List<OrderRepMaintTypeEntity> orderRepMaintTypeEntities = orderRep.getOrderRepMaintTypes();
		for (OrderRepMaintTypeEntity orderRepMaintTypeEntity : orderRepMaintTypeEntities) {
			orderRepMaintTypes.add(orderRepMaintTypeEntity.getMaintType());
		}
		return orderRepMaintTypes;
	}

	private final Map<MaintTypeBsEntity, MaintRhythmEntity> getMaintRhythmMap(MaintRhythmTypeBsEntity maintRhythmType,
			List<MaintTypeBsEntity> maintTypes) {
		final Map<MaintTypeBsEntity, MaintRhythmEntity> maintRhythmMap = new HashMap<MaintTypeBsEntity, MaintRhythmEntity>();
		ReadWriteDao<MaintRhythmEntity> maintRhythmEntityDao = getDaoProvider().getMaintRhythmEntityDao();
		Filter<MaintRhythmEntity> filter = maintRhythmEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(MaintRhythmEntity.ATTR_MAINT_RHYTHM_TYPE, maintRhythmType));
		filter.addExpression(Restrictions.in(MaintRhythmEntity.ATTR_MAINT_TYPE, maintTypes));

		Authorizer.disableNextAccessRestriction();
		final List<MaintRhythmEntity> mres = maintRhythmEntityDao.find(filter);
		for (MaintRhythmEntity maintRhythm : mres) {
			maintRhythm.initialize();
			maintRhythmMap.put(maintRhythm.getMaintType(), maintRhythm);
		}
		return maintRhythmMap;
	}

	private final Map<MaintTypeBsEntity, WagonMaintEntity> getWagonMaintenence(WagonEntity wagon) {
		final Map<MaintTypeBsEntity, WagonMaintEntity> wmeMap = new HashMap<MaintTypeBsEntity, WagonMaintEntity>();
		ReadWriteDao<WagonMaintEntity> wagonMaintEntityDao = getDaoProvider().getWagonMaintEntityDao();
		Filter<WagonMaintEntity> filter = wagonMaintEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(WagonMaintEntity.ATTR_WAGON_ID, wagon.getWagonId()));

		Authorizer.disableNextAccessRestriction();
		final List<WagonMaintEntity> wagonMaintEntities = wagonMaintEntityDao.find(filter);
		for (WagonMaintEntity wme : wagonMaintEntities) {
			wmeMap.put(wme.getMaintType(), wme);
		}
		return wmeMap;
	}

	/**
	 * After a rail order has been set to status "delivered", this method checks if there is need
	 * to create either a maintenance order or a return order, and does so if necessary.
	 * For non-rail orders (with delivery type not in (bimodal rail, rail direct) ), this method
	 * does nothing at all.
	 * @param event The bizevent that triggered the state change to "delivered"
	 * @param createReturnOrder Set to true if a return order must be created (this is only taken
	 * into account if no maintenance order has to be created)
	 */
	private void checkNextAssignment(BizeventEntity event, boolean createReturnOrder) {
		if (event.getOrder() == null) {
			return;
		}
		long dt = event.getOrder().getDeliveryType().getDeliveryTypeCd().longValue();
		if (dt != Constant.DELIVERY_TYPE_BIMODAL_RAIL && dt != Constant.DELIVERY_TYPE_RAIL_DIRECT) {
			return;
		}

		AssignmentEntity specialAssignment = null;
		OrderEntity specialOrder = null;
		BizeventEntity specialEvent = new BizeventEntity();
		Date eventDat = event.getEventTs();
		if (event.getWagon().getArrivalLastKnownStationDat().after(eventDat)) {
			eventDat = event.getWagon().getArrivalLastKnownStationDat();
		}

		final Long shiptoSapBpId = event.getOrder().getShiptoSapBp().getSapBpId();
		final ShiptoEntity shipto = findShiptoBySapBpId(shiptoSapBpId);

		final ShippingpointEntity railChainShippingpoint = shipto.getRailChainShippingpoint();
		if (railChainShippingpoint == null) {
			LOG.info("Shipto " + shipto.getShiptoSapBp().getName1() + "(" + shipto.getShiptoSapBp().getSapId()
					+ ") has no rail_chain_shippingpoint");
			return;
		}

		//check existing special assignments
		List<RailSpecialAssignmentEntity> specialOrders = findSpecialRailAssignments(eventDat,
				railChainShippingpoint.getShippingpointId(), event.getWagon().getWagonId());

		if (specialOrders.size() > 0) {
			/* Case 1:
			 * There is already an existing special order for this wagon.
			 */
			RailSpecialAssignmentEntity rsae = specialOrders.get(0);
			Authorizer.disableNextAccessRestriction();
			specialAssignment = getDaoProvider().getAssignmentEntityDao().findById(rsae.getAssignmentId());
			Authorizer.disableNextAccessRestriction();
			specialOrder = getDaoProvider().getOrderEntityDao().findById(rsae.getOrderId());
			specialEvent.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class,
					Constant.BIZEVENT_TYPE_TRANSMITTED_TO_RAIL));
		}
		else {

			//check if wea are at a plant or rep shippingpoint
			RailwayStationEntity railwayStation = railChainShippingpoint.getRailwayStation();
			if (railwayStation == null) {
				LOG.info("Rail chain shippingpoint railway station could not be determined [wagon: "
						+ event.getWagon().getWagonNo() + ", shipppingpoint "
						+ railChainShippingpoint.getShippingpointName() + "]");
				return;
			}
			final Long railwayStationId = railwayStation.getRailwayStationId();
			final long nbRepairShippingpoints = countRailRepairShippingpoints(railwayStationId);
			if (!(nbRepairShippingpoints > 0)) {

				List<WagonMaintEntity> maint = getDueRepairMaintenaceTypes(event.getWagon(), eventDat);

				if (maint.size() != 0) {
					/*
					 * Case 2:
					 * There is mainenance due, so we create a maintenance order.
					 */
					//check maintenance schedule
					specialOrder = createRailMaintenanceOrder(event.getWagon(), railChainShippingpoint, eventDat,
							event.getOrder().getPool(), event.getOrder().getCommCarrier(), maint);

					if (specialOrder == null) {
						//repair order could not be created for some reason do not create return order either
						return;
					}
				}
			}

			//create return order
			if (createReturnOrder && (specialOrder == null)) {
				/* Case 3:
				 * No maintenance order; but a return order was requested, so we must
				 * create a new return order.
				 */

				specialOrder = createRailReturnOrder(event.getOrder(), event.getWagon(), shipto, eventDat);
			}

			if (specialOrder != null) {
				specialAssignment = createNewRailAssignment(event.getWagon(), specialOrder, eventDat);
				changeOrderStatus(specialOrder, Constant.ORDER_STATUS_DISPATCHED);
				getDaoProvider().getAssignmentEntityDao().persist(specialAssignment);
				specialEvent.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class,
						Constant.BIZEVENT_TYPE_NEW));
			}
		}
		if ((specialAssignment != null)
				&& (specialAssignment.getAssignTrStatus().getAssignTrStatusCd().longValue() != Constant.ASSIGN_STATUS_TR_TRANSMITTED)
				&& (specialOrder != null)) {
			if (specialOrder.getOrderStatus().getOrderStatusCd().longValue() < Constant.ORDER_STATUS_LOADED) {
				changeOrderStatus(specialOrder, Constant.ORDER_STATUS_LOADED);
				Iterator<AssignmentPartEntity> i = specialAssignment.getAssignmentParts().iterator();
				while (i.hasNext()) {
					AssignmentPartEntity part = i.next();
					if (part.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
						part.setRealStart(event.getEventTs());
					}
				}
			}

			specialEvent.setAssignment(specialAssignment);
			specialEvent.setCoordLat(specialOrder.getShippingpoint().getCoordLat());
			specialEvent.setCoordLon(specialOrder.getShippingpoint().getCoordLon());
			specialEvent.setEventTs(event.getEventTs());
			specialEvent.setOrder(specialOrder);
			specialEvent.setRailwayStation(specialOrder.getShippingpoint().getRailwayStation());
			specialEvent.setShippingpoint(specialOrder.getShippingpoint());
			specialEvent.setWagon(event.getWagon());
			monitoringTablesService.processBizevent(specialEvent);
			transmitToRailCompany(specialEvent);
		}
	}

	/**
	 * Counts the number of "rail" or "rail repair" shipping points of a railway station
	 * @param railwayStationId The ID of the railway station whose shipping points to count
	 * @return The number of shipping points of type "rail" or "repair" that belong to the
	 * specified station.
	 */
	private long countRailRepairShippingpoints(final Long railwayStationId) {
		final ReadOnlyDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
		final Filter<ShippingpointEntity> f = dao.createFilter();
		f.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_RAILWAY_STATION_ID, railwayStationId));
		f.addExpression(Restrictions.or(
				Restrictions.eq(ShippingpointEntity.ATTR_SHIPPPOINT_TYPE_CD,
						Long.valueOf(Constant.SHIPPINGPOINT_TYPE_RAIL)),
				Restrictions.eq(ShippingpointEntity.ATTR_SHIPPPOINT_TYPE_CD,
						Long.valueOf(Constant.SHIPPINGPOINT_TYPE_RAIL_REPAIR))));
		Authorizer.disableNextAccessRestriction();
		return dao.count(f);
	}

	/**
	 * Fetches any existing special rail assignments from the DB.
	 * @param eventDat The date of the assignment (the time of the day, i.e., hours, minutes,
	 * seconds, etc. are ignored)
	 * @param shippingpointId The shipping point ID of the order for which the special assignment
	 * is created.
	 * @param wagonId The ID of the wagon to which the assignment belongs
	 * @return The list of matching assignment information (rows from
	 * VLGNC_RAIL_SPECIAL_ASSIGNMENT)
	 */
	private List<RailSpecialAssignmentEntity> findSpecialRailAssignments(Date eventDat, final Long shippingpointId,
			final Long wagonId) {
		final ReadOnlyDao<RailSpecialAssignmentEntity> railSpecAssDao = getDaoProvider().getRailSpecialAssignmentEntityDao();
		final Filter<RailSpecialAssignmentEntity> railAssgFilter = railSpecAssDao.createFilter();
		railAssgFilter.addExpression(Restrictions.eq(RailSpecialAssignmentEntity.ATTR_WAGON_ID, wagonId));
		railAssgFilter.addExpression(Restrictions.eq(RailSpecialAssignmentEntity.ATTR_SHIPPINGPOINT_ID, shippingpointId));
		railAssgFilter.addExpression(Restrictions.le(RailSpecialAssignmentEntity.ATTR_ASSIGNMENT_DAT,
				DateTimeUtils.cleanTimeWithDefaultTimeZone(eventDat)));
		railAssgFilter.addExpression(Restrictions.eq(RailSpecialAssignmentEntity.ATTR_DELIVERY_TYPE_CD,
				Constant.DELIVERY_TYPE_RAIL_DIRECT));
		railAssgFilter.addExpression(Restrictions.or(
				Restrictions.in(RailSpecialAssignmentEntity.ATTR_DISTRIBUTION_DEST_CD,
						Arrays.asList(Constant.DISTRIBUTION_DEST_SPECIAL)),
				Restrictions.eq(RailSpecialAssignmentEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_LOADED)));
		railAssgFilter.addExpression(Order.asc(RailSpecialAssignmentEntity.ATTR_DELIVERY_TO_DAT));

		Authorizer.disableNextAccessRestriction();
		return railSpecAssDao.find(railAssgFilter);
	}

	/**
	 * Fetches a shipto entity from the DB by its SAP BP ID (unique ID)
	 * @param sapBpId The SAP BP ID of the shipto to find
	 * @return The matching shipto entity, or <code>null</code> if no entity with this ID was
	 * found.
	 */
	private ShiptoEntity findShiptoBySapBpId(final Long sapBpId) {
		final ReadOnlyDao<ShiptoEntity> dao = getDaoProvider().getShiptoEntityDao();
		final Filter<ShiptoEntity> f = dao.createFilter();
		f.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, sapBpId));
		return dao.findSingle(f);
	}

	/**
	 * Sets the timestamp for leaving the shipto
	 *
	 * mandatory event fields:
	 * 	- assignment
	 *  - order
	 */
	public void leaveShipto(BizeventEntity event) {
		if ((event.getAssignment() == null)) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		if (((event.getCoordLat() == null) || (event.getCoordLon() == null)) && (event.getShipto() != null)) {
			if ((event.getShipto().getShiptoType().getShiptoTypeCd().longValue() == Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED)
					&& (event.getOrder() != null)) {
				event.setCoordLat(event.getOrder().getLnfCoordLat());
				event.setCoordLon(event.getOrder().getLnfCoordLon());
			}
			else {
				event.setCoordLat(event.getShipto().getCoordLat());
				event.setCoordLon(event.getShipto().getCoordLon());
			}
		}
		if (event.getAssignment().getAssignStatus().getAssignStatusCd().longValue() == Constant.ASSIGNMENT_STATUS_SHIPPING) {
			setDelivered(event, true);
		}
		event.getOrder();
	}

	private void setShipping(AssignmentEntity assignment, Date date) {
		boolean realStartSet = false;
		boolean alreadyShipping = false;

		List<LoadEntity> loads = AssignmentUtils.getLoadsFromAssignment(assignment);

		for (LoadEntity load : loads) {
			if (load != null && load.getNetQuantity() == null) {
				throw new LogonException(Reason.RES_ERROR_LOADING_NOT_YET_SHIPPING);
			}
		}

		List<AssignmentPartEntity> assignmentPartList = AssignmentUtils.getSortedAssignmentParts(assignment);

		for (AssignmentPartEntity ap : assignmentPartList) {
			if (!realStartSet && AssignmentUtils.isDrivingToPart(ap)) {
				if (ap.getRealStart() == null) {
					// Set the real start of the first assignment part of type 'to'
					ap.setRealStart(date);
				}
				realStartSet = true;
			}

			if (AssignmentUtils.isUnloadPart(ap)) {
				if (assignment.getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_INTERNAL) {
					// Set all assigned orders for this assignment to 'shipping' if status < shipping
					final OrderEntity order = ap.getOrder();
					final OrderStatusBsEntity orderStatusBs = order.getOrderStatus();
					long orderStatusCd = orderStatusBs.getOrderStatusCd();
					if (LogonStatusUtils.isLoadedOrBefore(orderStatusBs)
							|| orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL) {
						changeOrderStatus(order, Constant.ORDER_STATUS_SHIPPING);
					}
					else {
						// At least one of the orders already passed the 'shipping' status. Therefore, the assignment status will not be updated again.
						alreadyShipping = true;
					}
				}
				else if (ap.getExtOrder() != null) {
					changeExternalOrderStatus(ap.getExtOrder(), Constant.ORDER_STATUS_SHIPPING);
				}
			}
		}

		if (!alreadyShipping) {
			assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_SHIPPING);
		}

		if (assignment.getWagon() != null) {
			dispoService.setWagonTrack(new String[] { assignment.getWagon().getWagonNo() }, null);
		}
	}

	/**
	 * Sets the status for leaving the shipping point
	 *
	 * mandatory event fields:
	 * 	- assignment
	 */
	public void leaveShippingPoint(BizeventEntity event) {
		if (event.getAssignment() == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		BizEventUtil.processCoordinates(event);
		setShipping(event.getAssignment(), event.getEventTs());
	}

	public void setUnloaded(BizeventEntity event, OrderItemEntity orderItem, ShiptoSiloEntity silo,
			Boolean restmengeFlag, Boolean createReturnOrder, boolean timestampIsAccurate) {
		setUnloaded(event, orderItem, silo, restmengeFlag, null, createReturnOrder, timestampIsAccurate);
	}

	public void setUnloaded(BizeventEntity event, OrderItemEntity orderItem, ShiptoSiloEntity silo,
			Boolean restmengeFlag, BigDecimal returnedQuantity, Boolean createReturnOrder, boolean timestampIsAccurate) {
		if ((event.getAssignment() == null)
				|| ((event.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_INTERNAL) && (event.getOrder() == null))) {

			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		OrderEntity order = event.getOrder();
		AssignmentEntity assignment = event.getAssignment();

		boolean palletReturned = order != null && LogonPackageHandlingUtil.isPallet(orderItem)
				&& !LogonUtil.isReturnPalletOnly(order);
		if (returnedQuantity != null && palletReturned) {
			UomBsEntity returnedUom = orderItem.getLoadedUom();
			orderItem = orderDetailService.adjustReturnedOrderItemAttributes(orderItem, returnedQuantity, returnedUom);

			//todo setReturnOrderReason should be set?
			orderDetailService.updateOrder(order, true, true, null, false, null);
		}

		if (((event.getCoordLat() == null) || (event.getCoordLon() == null)) && (event.getShipto() != null)) {
			if ((event.getShipto().getShiptoType().getShiptoTypeCd().longValue() == Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED)
					&& (order != null)) {
				event.setCoordLat(order.getLnfCoordLat());
				event.setCoordLon(order.getLnfCoordLon());
			}
			else {
				event.setCoordLat(event.getShipto().getCoordLat());
				event.setCoordLon(event.getShipto().getCoordLon());
			}
		}
		else {
			checkShiptoCoords(event);
		}

		ShiptoEntity shipTo = null;
		boolean isLoadOffline = false;
		if (order != null) {
			// Set order item's delivered quantity
			if ((order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_SHIPPING)
					|| (order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL)) {
				enterUnloadingSite(event, timestampIsAccurate);
			}
			LoadEntity load = orderItem.getLoad();
			if (load != null) {
				updateOrderItemDeliveredFields(orderItem);
				isLoadOffline = load.getLdsOfflineMode();
			}

			shipTo = dispoService.fetchShipto(order.getShiptoSapBp().getSapBpId());
			List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
			if (shipTo.getShiptoType().getShiptoTypeCd().longValue() == Constant.SHIPTO_TYPE_CONTAINER_TERMINAL) {
				if (assignment.getTruck() != null) {
					// Make this check just to be sure, rail orders ariving at container terminal should never call this
					// as they are set to state delivered directly at arrivan (with setAtShipto)
					setAtContainerTerminal(event.getTruck(), event.getEventTs(), load,
							shipTo.getMatChainShippingpoint());
				}
			}
			else {
				// Check if all order items of the order are unloaded
				boolean orderUnloaded = true;
				for (OrderItemEntity otherOrderItem : orderItems) {
					if ((otherOrderItem.getDeliveredQuantity() == null)
							&& (otherOrderItem.getSapMaterial().getMaterialPackage().getLogonPackageType() != null)) {
						orderUnloaded = false;
						break;
					}
				}
				// Set load's unloadingTs if this was the last order item that was delivered
				if ((load != null) && (load.getUnloadingTs() == null)) {
					List<OrderItemEntity> loadOrderItems = getOrderItems(load);
					boolean loadUnloaded = true;
					for (OrderItemEntity otherOrderItem : loadOrderItems) {
						if ((otherOrderItem.getDeliveredQuantity() == null)
								&& (otherOrderItem.getSapMaterial().getMaterialPackage().getLogonPackageType() != null)) {
							// there's still an order item to be unloaded from this load
							loadUnloaded = false;
							break;
						}
					}
					if (loadUnloaded) {
						load.setUnloadingTs(event.getEventTs());
					}
				}
				if (!orderUnloaded) {
					return;
				}
			}
			updateServiceItemDeliveredFields(order, assignment);

			DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
			Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
			if (LogonUtil.isTruckOrder(deliveryTypeCd) || LogonUtil.isSimpleVehicleOrder(deliveryTypeCd)
					|| LogonUtil.isShipPickupOrder(deliveryTypeCd) || LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd)) {
				ReturnQuantHandlingBsEntity rqs = new ReturnQuantHandlingBsEntity();
				rqs.setReturnQuantHandlingCd(Constant.RETURN_QUANT_HANDLING_ADJUSMENT_NEEDED);
				SiloVerifTypeBsEntity siloNotVerified = null;
				for (OrderItemEntity oi : orderItems) {
					final SapMaterialEntity sapMaterial = oi.getSapMaterial();
					if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(sapMaterial)) {
						if (LogonUtil.isTruckOrder(deliveryTypeCd) && silo != null) { // do not overwrite silo with null
							final ShiptoSiloEntity oldSilo = oi.getShiptoSilo();

							if (oldSilo != null && oldSilo.getShiptoSiloId() != null
									&& oldSilo.getShiptoSiloId().equals(silo.getShiptoSiloId())) {
								oi.setShiptoSilo(silo); // no real change, silo verification type could be the same
							}
							else {
								oi.setShiptoSilo(silo);

								if (siloNotVerified == null) {
									siloNotVerified = referentialDataService.findBsEntity(SiloVerifTypeBsEntity.class,
											Constant.SILO_VERIFICATION_TYPE_NOT_VERIFIED);
								}
								oi.setShiptoSiloVerifType(siloNotVerified);
							}
						}

						if (restmengeFlag.booleanValue()) {
							oi.setReturnQuantHandling(rqs);
						}
					}
				}
				if (restmengeFlag.booleanValue()) {
					order.setReleaseSapFlag(Boolean.FALSE);
				}
				order.setCarrierTruckDriverNote(assignment.getCarrierTruckDriverNote());
				order.setCarrierDispatchingNote(assignment.getCarrierDispatchingNote());
			}
			else {
				RailwayStationEntity rws = order.getShiptoSapBp().getRailwayStation();
				if (rws != null) {
					if ((event.getWagon().getLastKnownRailwayStation() == null)
							|| !event.getWagon().getLastKnownRailwayStation().getRailwayStationId().equals(
									rws.getRailwayStationId())) {
						event.getWagon().setLastKnownRailwayStation(rws);
						event.getWagon().setArrivalLastKnownStationDat(event.getEventTs());
					}
					else if (event.getEventTs().before(event.getWagon().getArrivalLastKnownStationDat())) {
						event.getWagon().setArrivalLastKnownStationDat(event.getEventTs());
					}
					ReadWriteDao<ContainerEntity> containerEntityDao = getDaoProvider().getContainerEntityDao();
					Filter<ContainerEntity> filter = containerEntityDao.createFilter();
					filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_WAGON_ID,
							event.getWagon().getWagonId()));
					List<ContainerEntity> containers = containerEntityDao.find(filter);
					for (ContainerEntity containerEntity : containers) {
						Date ts = event.getEventTs();
						if (event.getWagon().getArrivalLastKnownStationDat().after(ts)) {
							ts = event.getWagon().getArrivalLastKnownStationDat();
						}
						containerEntity.setArrivalLastKnownSpDat(event.getEventTs());
						containerEntity.setLastKnownShippingpoint(event.getShipto().getMatChainShippingpoint());
					}
				}
			}
			order.setActualDeliveryDat(event.getEventTs());
			copyVehicleDataToOrder(assignment, order);

			order.setDeliveredWagon(assignment.getWagon());
			if (order.getOrderStatus().getOrderStatusCd().longValue() != Constant.ORDER_STATUS_FINISHED) {
				long orderStatus = Constant.ORDER_STATUS_DELIVERED;
				if (isLoadOffline) {
					orderStatus = Constant.ORDER_STATUS_DELIVERED_PENDING_LDS_DATA;
				}
				// for rail orders the function can be called several times (once per item)
				changeOrderStatus(order, orderStatus);
			}

			if(LogonUtil.isTruckOrder(deliveryTypeCd)){
				TruckEntity truck = event.getTruck();
				if (truck.getTruckTransportCat().getTruckTransportCatCd() == Constant.TRUCK_TRSP_CAT_CONTAINER) {
					detachCurrentTruckAndTrailerFromContainer(event.getContainer());
				}
			}

			final BizeventEntity bizevent = new BizeventEntity();
			BizeventTypeBsEntity bs = BizEventUtil.BIZEVENT_TYPE_EDIT_FIELD;
			bizevent.setBizeventType(bs);
			bizevent.setOrder(order);

			//initialize event_ts to "now"/systimestamp"
			bizevent.setEventTs(new Date());

			//will only persist the edit bizevent if something changes
			bizeventAuditService.persistAuditData(bizevent);
		}
		if (assignment.getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_EXTERNAL) {
			enterUnloadingSite(event, timestampIsAccurate);
			List<AssignmentPartEntity> assignmentParts = event.getAssignment().getAssignmentParts();
			for (AssignmentPartEntity ap : assignmentParts) {
				if (ap.getExtOrder() != null) {
					if (ap.getExtOrder().getDeliveredQuantity() == null) {
						ap.getExtOrder().setDeliveredQuantity(ap.getExtOrder().getLoadedQuantity());
					}
					break;
				}
			}
		}
		if ((shipTo == null)
				|| (shipTo.getShiptoType().getShiptoTypeCd().longValue() != Constant.SHIPTO_TYPE_CONTAINER_TERMINAL)
				|| (event.getWagon() == null)) {
			// In case of container terminal, setDelivered is called from setAtShipto
			setDelivered(event, createReturnOrder.booleanValue(), isLoadOffline);
		}
	}

	/**
	 * Trigger unloading of all items of an order. This method triggers the unloading of all order
	 * items of the order specified in <code>event.getOrder()</code>.
	 * @param event A bizevent object containing all information related to the unloading event.
	 * @param silo The shipto silo where the order is unloaded; set to <code>null</code> if this
	 * is not known.
	 * @param restmengeFlag Set to <code>true</code> if there are returned quantities.
	 * In this case, nothing will be sent to SAP.
	 * @param createReturnOrder If <code>true</code>, create a new order to return the
	 * undelivered materials.
	 * @param timestampIsAccurate Set to <code>true</code> if the event's timestamp should be
	 * set as the real start of the assignment part. This is for realtime events, e.g. when the
	 * loading system sends the exact time of the event. If the event timestamp is set by logon
	 * when it creates the event, but does not necessarily correspond to the time the event
	 * occurred in reality, set this to <code>false</code>.
	 */
	public void setUnloaded(BizeventEntity event, ShiptoSiloEntity silo, Boolean restmengeFlag,
			Boolean createReturnOrder, boolean timestampIsAccurate) {
		if ((event.getAssignment() == null)
				|| ((event.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_INTERNAL) && event.getOrder() == null)) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		if (event.getOrder() != null) {
			List<OrderItemEntity> orderItems = event.getOrder().getOrderItems();
			for (OrderItemEntity orderItem : orderItems) {
				setUnloaded(event, orderItem, silo, restmengeFlag, null, createReturnOrder, timestampIsAccurate);
			}
		}
		else {
			setUnloaded(event, null, silo, restmengeFlag, null, createReturnOrder, timestampIsAccurate);
		}
	}

	/**
	 * Updates the service items delivered fields. As far for these there is no load, loaded fields remain
	 * empty. Besides as far the truck composition is not known (there is no load from where it would be known)
	 * only truck and wagon fields are set.
	 * @param order
	 * @param assignment
	 */
	private void updateServiceItemDeliveredFields(OrderEntity order, AssignmentEntity assignment) {
		Collection<OrderItemEntity> serviceItems = LogonPackageHandlingUtil.getServiceItems(order);
		if (serviceItems.isEmpty()) {
			return;
		}
		for (OrderItemEntity item : serviceItems) {
			//if it is not present in the list used everywhere in the workflow (all material items) copy the ordered to delivered
			if (item.getDeliveredQuantity() != null && item.getDeliveredUom() != null) {
				continue;
			}
			SapMaterialEntity sapMaterial = item.getSapMaterial();
			BigDecimal quantityInBaseUom = null;
			item.setDeliveredQuantity(item.getOrderedQuantity());
			item.setDeliveredUom(item.getOrderedUom());
			if (sapMaterial != null && sapMaterial.getBaseUom() != null) {
				if (!item.getOrderedUom().getUomCd().equals(item.getSapMaterial().getBaseUom().getUomCd())) {
					quantityInBaseUom = convertToBaseUom(sapMaterial, item.getOrderedQuantity(), item.getOrderedUom());
					if (quantityInBaseUom != null) {
						item.setDeliveredQuantity(quantityInBaseUom);
						item.setDeliveredUom(sapMaterial.getBaseUom());
					}
				}
				item.setDeliveredLoadWagon(assignment.getWagon());
				item.setDeliveredLoadTruck(assignment.getTruck());
			}
		}
	}

	private void setAtContainerTerminal(TruckEntity truck, Date ts, LoadEntity load, ShippingpointEntity lastKnownToSet) {
		ReadWriteDao<ContainerEntity> containerEntityDao = getDaoProvider().getContainerEntityDao();
		Filter<ContainerEntity> filter = containerEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRUCK_ID, truck.getTruckId()));
		List<ContainerEntity> containers = containerEntityDao.find(filter);
		for (ContainerEntity ce : containers) {
			Date eventTs = ts;
			ce.setArrivalLastKnownSpDat(eventTs);
			ce.setLastKnownShippingpoint(lastKnownToSet);
			ce.setCurrentTruck(null);
			ce.setCurrentTrailer(null);
		}
		load.setTruck(null);
		load.setTrailer(null);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _storeBizevent(Long bizeventTypeCd, OrderEntity order) {
		BizeventEntity bizevent = new BizeventEntity();
		bizevent.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class, bizeventTypeCd));
		bizevent.setEventTs(new Date());
		bizevent.setOrder(order);
		monitoringTablesService.processBizevent(bizevent);
	}

	/**
	 * The orderId/creditNoteId is given because this method runs in a new
	 * transaction (so the entity needs to be in the new persistency context,
	 * alternatively an em.refresh/em.merge could've been done, but by giving
	 * the orderId it's clear other changes will not be persisted by this
	 * method.
	 *
	 * If this method 'hangs', it probably means the OrderEntity was modified
	 * in the calling method (currently suspended transaction) leading to a
	 * lock on the DB.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _sendSapSalesDocument(Long orderId, List<Long> creditNoteIdList) {

		List<CreditNoteEntity> creditNoteList = new ArrayList<CreditNoteEntity>();
		if (creditNoteIdList != null) {
			for(Long noteId : creditNoteIdList) {
				CreditNoteEntity creditNote = getDaoProvider().getCreditNoteEntityDao().findById(noteId);
				if (creditNote.getSentSalesdocToSapTs() != null) {
					LOG.error("Sales doc for credit note " + noteId + " already sent to SAP on "
							+ creditNote.getSentSalesdocToSapTs());

					return;
				}
				creditNote.setSentSalesdocToSapTs(new Date());
				creditNoteList.add(creditNote);
			}
		}
		OrderEntity order = getDaoProvider().getOrderEntityDao().findById(orderId);
		if (creditNoteIdList == null) {
			if (order.getSentSalesdocToSapTs() != null) {
				LOG.error("Sales doc for order " + orderId + " already sent to SAP on "
						+ order.getSentSalesdocToSapTs());

				return;
			}
			order.setSentSalesdocToSapTs(new Date());
		}
		try {
			SalesDocumentDTO salesDocument = sapOutgoingDataService.getSalesDocument(order, creditNoteList);

			sapOutgoingDataService.sendSalesDocument(salesDocument);
			orderService._storeBizevent(
					creditNoteIdList != null ? Constant.BIZEVENT_TYPE_CN_DELIVERYDOC_SUCCESS : Constant.BIZEVENT_TYPE_DELIVERYDOC_SUCCESS,
					order);
		}
		catch (LogonException e) {
			if (e.isReason(Reason.RES_ERROR_SAP_SHIPPINGPOINT_MISCONFIGURED)) {
				LOG.error(e.getMessage());
			}
			else {
				throw e;
			}
		}
		catch (RuntimeException e) {
			orderService._storeBizevent(
					creditNoteIdList != null ? Constant.BIZEVENT_TYPE_CN_DELIVERYDOC_FAIL : Constant.BIZEVENT_TYPE_DELIVERYDOC_FAIL,
					order);

			throw e;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _sendSapTransferDocuments(Long orderId, Long creditNoteId) {

		CreditNoteEntity creditNote = null;
		if (creditNoteId != null) {
			creditNote = getDaoProvider().getCreditNoteEntityDao().findById(creditNoteId);
			if (creditNote.getSentSalesdocToSapTs() != null) {
				LOG.error("Salesdoc/transfer docs for credit note " + creditNoteId + " already sent to SAP on "
						+ creditNote.getSentSalesdocToSapTs());

				return;
			}
			creditNote.setSentSalesdocToSapTs(new Date());
		}
		OrderEntity order = getDaoProvider().getOrderEntityDao().findById(orderId);
		if (creditNoteId == null) {
			if (order.getSentSalesdocToSapTs() != null) {
				LOG.error("Salesdoc/transfer docs for order " + orderId + " already sent to SAP on "
						+ order.getSentSalesdocToSapTs());

				return;
			}

			order.setSentSalesdocToSapTs(new Date());

			// we need to update the sent_order_item_no as it is done for sales documents
			// in contrast to sales documents, for transfer documents advance payment is not possible
			for (OrderItemEntity item : order.getOrderItems()) {
				item.setSentOrderItemNo(item.getOrderItemNo());
			}
		}
		try {
			final TransferDeliveryDocumentDTO inboundTransferDocument = sapOutgoingDataService.getTransferDeliveryDocumentInbound(
					order, creditNote);
			final TransferDeliveryDocumentDTO outboundTransferDocument = sapOutgoingDataService.getTransferDeliveryDocumentOutbound(
					order, creditNote);

			sapOutgoingDataService.sendTransferDeliveryDocument(inboundTransferDocument);
			sapOutgoingDataService.sendTransferDeliveryDocument(outboundTransferDocument);

			orderService._storeBizevent(Constant.BIZEVENT_TYPE_TRANSFERDOC_SUCCESS, order);
		}
		catch (LogonException e) {
			if (e.isReason(Reason.RES_ERROR_SAP_SHIPPINGPOINT_MISCONFIGURED)) {
				LOG.error(e.getMessage());
			}
			else {
				throw e;
			}
		}
		catch (RuntimeException e) {
			orderService._storeBizevent(Constant.BIZEVENT_TYPE_TRANSFERDOC_FAIL, order);

			throw e;
		}
	}

	/**
	 * Transmit a creditNote to SAP.
	 * This method can be called from a running transaction or from no
	 * transaction. In any way, the creditNote entity MUST NOT be
	 * modified when this method is being called.
	 */
	public void transmitToSap(List<Long> creditNoteIdList) {
		Long creditNoteId = null;
		if(creditNoteIdList != null && !creditNoteIdList.isEmpty()) {
			creditNoteId = creditNoteIdList.get(0);
		}
		if (creditNoteId == null) {
			throw new LogonException("CreditNote cannot be null", Reason.RES_ERROR_IMPL);
		}

		CreditNoteEntity creditNote = getDaoProvider().getCreditNoteEntityDao().findById(creditNoteId);
		if (creditNote.getSentSalesdocToSapTs() == null) {
			final OrderEntity order = creditNote.getOrderItem().getOrder();
			if (order.getSapContract() == null) {
				throw new LogonException("Order SAP contract cannot be null", Reason.RES_ERROR_IMPL);
			}
			if (order.getSapContract().getSapContractType().getContractTypeIsStpoFlag()) {
				orderService._sendSapTransferDocuments(order.getOrderId(), creditNoteId);
			}
			else {
				orderService._sendSapSalesDocument(order.getOrderId(), creditNoteIdList);
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _setToFinished(Long orderId) {
		final OrderEntity order = getDaoProvider().getOrderEntityDao().findById(orderId);
		final BizeventEntity bizevent = new BizeventEntity();
		bizevent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_FINISHED);
		bizevent.setEventTs(new Date());
		bizevent.setOrder(order);
		monitoringTablesService.processBizevent(bizevent);

		removeTemporaryData(order);
		changeOrderStatus(order, Constant.ORDER_STATUS_FINISHED);

		final long deliveryTypeCd = order.getDeliveryType().getDeliveryTypeCd();
		if (!LogonUtil.isRailOrder(deliveryTypeCd) && !LogonUtil.isPickupOrder(deliveryTypeCd)
				&& !LogonUtil.isShipPickupOrder(deliveryTypeCd) && !LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd)
				&& !LogonUtil.isServiceOnlyOrder(deliveryTypeCd) && !LogonUtil.isScheduledPickupOrder(order)) {
			// Electronic delivery note to be sent out per email and fax
			// No need to send paper-based delivery again, as it is already sent during event endLoad
			// No need to send delivery note for rail orders, as it is already sent during transmitToRailCompany
			// No need to send delivery note for pickup orders, as it is already sent during endLoad
			// No need to send delivery note for Service Material Only Order (see LRQ 070.2 / LOGON-4743)
			generateAndSendPDFWithoutExplicitLoadingpoint(order, true, true, false);
		}
		frozenTablesService.freezeOrder(order);
	}

	@ScheduledTask(name = "update_orders", second = "5", minute = "1/10", hour = "*", configurationMethodName = "getUpdateOrdersCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void updateOrders() {
		// Send goodsmovement to SAP which are loaded to a virtual stock (usually truck)
		goodsMovementService.sendAllUnsentGoodsMovementNotificationsToSap();
		// Send orders to SAP, all 3 interfaces, either after loading or delivery.
		sendOrdersToSap();
		// Delivered orders which need to be set to finished.
		finishOrders();
		sendCreditNotes();
	}

	/**
	 * Acquires a list of OrderFinalEntity IDs, for every Entity which has not been reported yet to SAP BW.
	 *  Calls sendSapLogisticReport() with every ID separately.
	 *  @return
	*/
	@ScheduledTask(name = "send_logistic_report", second = "5", minute = "1/10", hour = "*", configurationMethodName = "getSendLogisticReportCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendLogisticReportIdocs() {
		//Filter for missing SENT_KPI_TO_SAP_BW_TS timestamp
		Filter<OrderFinalEntity> finalOrderFilter = new FilterImpl<OrderFinalEntity>();
		finalOrderFilter.addExpression(Restrictions.isNull(OrderFinalEntity.ATTR_SENT_KPI_TO_SAP_BW_TS));
		//Set up DaoProvider for OrderFinalEntity
		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<OrderFinalEntity> orderFinalDao = daoProvider.getOrderFinalEntityDao();
		//JPAQuery the IDs of OrderFinalEntities
		JPAQuery<OrderFinalEntity> sub = orderFinalDao.createJPAQuery();
		sub.setFilter(finalOrderFilter);
		sub.addSelect(SelectExpression.simpleSelect(OrderFinalEntity.ATTR_ORDER_FINAL_ID));
		sub.setDistinct(true);

		@SuppressWarnings("unchecked")
		List<Long> finalOrderEntityIds = orderFinalDao.find(sub);
		if (finalOrderEntityIds == null) {
			return;
		}
		LOG.info(finalOrderEntityIds.size()
				+ " order final entities are eligible to be sent to SAP BW as Logistic Report!");
		// Every Entity is processed as a different transaction.
		for (Long finalOrderEntityId : finalOrderEntityIds) {
			orderService._sendSapLogisticReport(finalOrderEntityId);
		}
	}

	/**
	 * Creates an SapLogisticReport based on the OrderFinalEntity, then sends it to SAP.
	 * @param finalOrderEntityId Id of the OrderFinalEntity which will the report will be based upon.
	 * @return
	 */
	// The sent flag(ts) update will happen only after successful sending
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _sendSapLogisticReport(Long finalOrderEntityId) {
		if (finalOrderEntityId == null) {
			LOG.error("Trying to generate SAP Logistic Report from null ID.");
			return;
		}

		// I. get DTO base on Entity ID
		SapLogisticReportDTO orderFinalDto = sapOutgoingDataService.getLogisticReportDocument(finalOrderEntityId);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Processing dto: " + orderFinalDto.toString());
		}

		// II. Send DTO to SAP
		Boolean sendingSuccessful = false;
		try {
			LogonToSap.sendLogisticReport(getConfigBean(), orderFinalDto);
			sendingSuccessful = true;
		}
		catch (Exception e) {
			LOG.error("Sap Logistic Report could not be sent, finalOrderEntityId:" + finalOrderEntityId);
			return;
		}

		// III. Set timestamp of successfully sent Order.
		if (sendingSuccessful) {
			OrderFinalEntity reportedEntity = getDaoProvider().getOrderFinalEntityDao().findById(finalOrderEntityId);
			reportedEntity.setSentKpiToSapBwTs(new Date());
		}
	}

	/**
	 * Creates a goodsMovementEntity from the load, and sends it to SAP.
	 *
	 * The method stores the entity in the tlgnc_goods_movement table, is SAP was not reachable ... timestamp is null.
	 * @return
	 */
	public void createAndSaveGoodsMovementEntity(LoadEntity load) {
		goodsMovementService.createGoodsMovementEntity(load, null, true, false, false);
	}

	/**
	 * @param actualDeliveryDate
	 * @return the expression which defines which orders need to be send to SAP (any interface).
	 */
	private Expression getSendToSapExpression(Date actualDeliveryDate) {
		return Restrictions.and(Restrictions.eq(OrderEntity.ATTR_RELEASE_SAP_FLAG, Boolean.TRUE), Restrictions
						.or(Restrictions.isNull(OrderEntity.ATTR_ITRACK_STATUS), Restrictions
								.eq(OrderEntity.ATTR_ITRACK_STATUS + "." + ItrackStatusBsEntity.ATTR_ITRACK_STATUS_CD,
										ITrackStatus.OK.getStatusCode())),
		// Order status = delivered, actual delivery dat > now and salesdoc should be send after delivered
				Restrictions.and(
						Restrictions.eq(OrderEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_DELIVERED),
						Restrictions.or(
								Restrictions.eq(OrderEntity.ATTR_FORCE_TO_SAP_FLAG, Boolean.TRUE),
								Restrictions.eq(OrderEntity.ATTR_SAREA + "." + SareaEntity.ATTR_FORCE_TO_SAP_FLAG, Boolean.TRUE),
								Restrictions.lt(OrderEntity.ATTR_ACTUAL_DELIVERY_DAT, actualDeliveryDate))));
	}

	/**
	 * Makes a filter expression for orders having repair or any of return distribution dest cd.
	 * Checks also on sap_contract_id, which must be null. This will guarantee that the returned
	 * orders are not included in the set filtered by "ATTR_SAP_CONTRACT condition".
	 * @return Expression
	 */
	private Expression getDistributionDestExpression() {
		String[] retTypes = { Constant.DISTRIBUTION_DEST_INBOUND_RETURN,
				Constant.DISTRIBUTION_DEST_INTERUNIT_DIST_RETURN, Constant.DISTRIBUTION_DEST_INTERUNIT_PROD_RETURN,
				Constant.DISTRIBUTION_DEST_OUTBOUND_RETURN, Constant.DISTRIBUTION_DEST_REPAIR };

		Expression returnOrRepair = Restrictions.in(OrderEntity.ATTR_DISTRIBUTION_DEST_CD, Arrays.asList(retTypes));
		Expression sapContract = Restrictions.isNull(OrderEntity.ATTR_SAP_CONTRACT_ID);
		return Restrictions.and(returnOrRepair, sapContract);
	}

	/**
	 * This method is responsible to decide whether a delivery/shipment/transfer document
	 * can be sent to SAP or not and an order can be set to finished or not, depending on
	 * the extra minutes delay of the orders sales area.
	 * @param now current time
	 * @param order current order
	 * @return
	 */
	private boolean isOrderPastDelay(Date now, OrderEntity order) {
		Calendar delayedDeliveryTime = Calendar.getInstance();
		delayedDeliveryTime.setTime(order.getActualDeliveryDat());
		Long delaySendMinutes = order.getSarea().getDelaySendToSapMi();

		if (delaySendMinutes != null) {
			delayedDeliveryTime.add(Calendar.MINUTE, delaySendMinutes.intValue());
		}

		if (order.getNoPdaOrRailEventsFlag()) {
			if (delayedDeliveryTime.getTime().before(now)) {
				return true;
			}
		}
		else {
			return true;
		}

		return false;
	}

	private void sendOrdersToSap() {
		final Date now = new Date();
		final Expression sendToSapExpression = getSendToSapExpression(now);

		List<Long> failedOrders = new ArrayList<Long>();
		List<Long> delayedOrders = new ArrayList<Long>();

		// Orders which need to be send to the salesdoc interface.
		final ReadOnlyDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();

		Filter<OrderEntity> filter = orderDao.createFilter();
		final Filter<OrderEntity> filterReturnOrder = orderDao.createFilter();

		//TODO there is an ugly solution here!!! (LOGON-6324)
		/*
		2 different filters are needed: one for filtering by sap_contract -> filter, and one for filtering
		repair/return orders -> filterReturnOrder. The hitlist is 2 disjunct sets of orders
		depending on sap_contract_id is set (for repair/rail orders) or not set (all others).

		LOGICALLY, it would be good to filter by or(ATTR_SAP_CONTRACT condition, DistributionDest=repair/return condition).

		But, TECHNICALLY it's wrong, as hibernate generates a query with joining tlgnc_sap_contract table
		to tlgnc_order (where sap_contract_id equals), regardless of sap_contract_id is null, something like this:

		select * from tlgnc_order o, tlgnc_sap_contract sc
		where o.sap_contract_id=sc.sap_contract_id
		and (sc.contract_type=... or o.distribution_dest_cd=...)

		=> drops return/repair orders from hits, as there sap_contract_id is null.
		*/

		filter.addExpression(sendToSapExpression);
		filterReturnOrder.addExpression(sendToSapExpression);
		// Salesdoc has not been send yet
		filter.addExpression(Restrictions.isNull(OrderEntity.ATTR_SENT_SALESDOC_TO_SAP_TS));
		filterReturnOrder.addExpression(Restrictions.isNull(OrderEntity.ATTR_SENT_SALESDOC_TO_SAP_TS));
		// Contract is not STPO ("ATTR_SAP_CONTRACT condition")
		filter.addExpression(Restrictions.eq(OrderEntity.ATTR_SAP_CONTRACT + "."
				+ SapContractEntity.ATTR_SAP_CONTRACT_TYPE + "."
				+ SapContractTypeBsEntity.ATTR_CONTRACT_TYPE_IS_STPO_FLAG, Boolean.FALSE));
		// it is a rail/repair order ("DistributionDest=repair/return condition")
		filterReturnOrder.addExpression(getDistributionDestExpression());

		final List<OrderEntity> salesDocOrders = orderDao.find(filter);
		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("%d orders with sap contract will be sent to the salesdoc interface",
					salesDocOrders.size()));
		}

		final List<OrderEntity> salesDocOrdersReturnOrder = orderDao.find(filterReturnOrder);
		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("%d return/repair orders will be sent to the salesdoc interface",
					salesDocOrdersReturnOrder.size()));
		}

		salesDocOrders.addAll(salesDocOrdersReturnOrder);

		for (OrderEntity order : salesDocOrders) {
			try {
				if ((order.getForceToSapFlag() || order.getSarea().getForceToSapFlag() || isOrderPastDelay(now, order)) && isValidForSending(order)) {
					orderService._sendSapSalesDocument(order.getOrderId(), null);
				}
				else {
					if (LOG.isInfoEnabled()) {
						LOG.info(String.format("Delayed sending of order to salesdoc interface id=%d, order number=%d",
								order.getOrderId(), order.getOrderNumber()));
					}
					delayedOrders.add(order.getOrderId());
				}
			}
			catch (Exception e) {
				LOG.error(String.format("Failed to send order to salesdoc interface id=%d, order number=%d",
						order.getOrderId(), order.getOrderNumber()));
				ExceptionUtil.errException(LOG, e, null);
				failedOrders.add(order.getOrderId());
			}
		}

		if (!failedOrders.isEmpty()) {
			LOG.error(String.format("The following orders were not sent to the salesdoc interface: %s.",
					Arrays.toString(failedOrders.toArray())));
			if (LOG.isInfoEnabled()) {
				if (!delayedOrders.isEmpty()) {
					LOG.info(String.format(
							"The following orders sending to the salesdoc interface were postponed: %s.",
							Arrays.toString(delayedOrders.toArray())));
				}
			}
		}
		else if (!salesDocOrders.isEmpty()) {
			if (LOG.isInfoEnabled()) {
				if (delayedOrders.isEmpty()) {
					LOG.info(String.format("All %d orders successfully sent to the salesdoc interface",
							salesDocOrders.size()));
				}
				else {
					LOG.info(String.format(
							"%d orders successfully sent to the salesdoc interface. The following orders sending to the salesdoc interface were postponed: %s",
							(salesDocOrders.size() - delayedOrders.size()), Arrays.toString(failedOrders.toArray())));
				}
			}
		}

		// Orders which need to be send to the transferdoc interface.
		filter = orderDao.createFilter();
		filter.addExpression(sendToSapExpression);
		// Salesdoc/transferdoc has not been send yet
		filter.addExpression(Restrictions.isNull(OrderEntity.ATTR_SENT_SALESDOC_TO_SAP_TS));
		// Contract is STPO
		filter.addExpression(Restrictions.eq(OrderEntity.ATTR_SAP_CONTRACT + "."
				+ SapContractEntity.ATTR_SAP_CONTRACT_TYPE + "."
				+ SapContractTypeBsEntity.ATTR_CONTRACT_TYPE_IS_STPO_FLAG, Boolean.TRUE));
		final List<OrderEntity> transferDocOrders = orderDao.find(filter);

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("%d orders will be send to the transferdoc interface.", transferDocOrders.size()));
		}

		failedOrders.clear();
		delayedOrders.clear();

		for (OrderEntity order : transferDocOrders) {
			try {
				if ((order.getForceToSapFlag() || order.getSarea().getForceToSapFlag() || isOrderPastDelay(now, order)) && isValidForSending(order)) {
					orderService._sendSapTransferDocuments(order.getOrderId(), null);
				}
				else {
					if (LOG.isInfoEnabled()) {
						LOG.info(String.format(
								"Delayed sending of order to transferdoc interface id=%d, order number=%d",
								order.getOrderId(), order.getOrderNumber()));
					}
					delayedOrders.add(order.getOrderId());
				}
			}
			catch (Exception e) {
				LOG.error("Failed to send order to transferdoc interface id=" + order.getOrderId() + ", order number="
						+ order.getOrderNumber() + ".");
				ExceptionUtil.errException(LOG, e, null);
				failedOrders.add(order.getOrderId());
			}
		}

		if (!failedOrders.isEmpty()) {
			LOG.error(String.format("The following orders were not sent to the transferdoc interface: %s.",
					Arrays.toString(failedOrders.toArray())));
			if (LOG.isInfoEnabled()) {
				if (!delayedOrders.isEmpty()) {
					LOG.info(String.format(
							"The following orders sending to the transferdoc interface were postponed: %s.",
							Arrays.toString(delayedOrders.toArray())));
				}
			}
		}
		else if (!transferDocOrders.isEmpty()) {
			if (LOG.isInfoEnabled()) {
				if (delayedOrders.isEmpty()) {
					LOG.info(String.format("All %d orders successfully sent to the transferdoc interface",
							transferDocOrders.size()));
				}
				else {
					LOG.info(String.format(
							"%d orders successfully sent to the transferdoc interface. The following orders sending to the transferdoc interface were postponed: %s",
							(transferDocOrders.size() - delayedOrders.size()), Arrays.toString(failedOrders.toArray())));
				}
			}
		}

		// Shipment doc generation
		final Filter<ShipmentdocAssignmentEntity> shipmentdocAssignmentFilter = getDaoProvider().getShipmentdocAssignmentEntityDao().createFilter();
		final List<ShipmentdocAssignmentEntity> docs = getDaoProvider().getShipmentdocAssignmentEntityDao().find(
				shipmentdocAssignmentFilter);

		List<Long> assignmentIds = new ArrayList<Long>();

		for (ShipmentdocAssignmentEntity doc : docs) {
			assignmentIds.add(doc.getAssignmentId());
		}

		if (assignmentIds.isEmpty()) {
			if (LOG.isInfoEnabled()) {
				LOG.info("No assingments found for shipment processing.");
			}
			return;
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("%d assignments will be send to the shipmentdoc interface: %s",
					assignmentIds.size(), Arrays.toString(assignmentIds.toArray())));
		}

		for (Long assignmentId : assignmentIds) {

			final AssignmentEntity assignment = orderService.fetchAssignment(assignmentId);

			final List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
			final List<AssignmentPartEntity> orderParts = AssignmentUtils.collectOrderParts(assignmentParts);

			try {

				boolean skipShipmentAssignment = false;
				final Collection<Long> delayedShipmentOrders = new ArrayList<Long>();

				for (AssignmentPartEntity orderPart : orderParts) {
					final OrderEntity order = orderPart.getOrder();

					if (!isOrderPastDelay(now, order) && !order.getForceToSapFlag() && !order.getSarea().getForceToSapFlag()) {
						skipShipmentAssignment = true;
						delayedShipmentOrders.add(order.getOrderId());
					}
				}

				if (skipShipmentAssignment) {
					if (LOG.isInfoEnabled()) {
						LOG.info(String.format(
								"The sending of the following assignment to the shipmentdoc interface were postponed: Assignment: [%d] because of orders: [%s].",
								assignment.getAssignmentId(), delayedShipmentOrders));
					}
					continue; // Move on to next assignment
				}

				orderService.processShipmentDoc(assignmentId);

				for (AssignmentPartEntity orderPart : orderParts) {
					OrderEntity order = orderPart.getOrder();
					orderService._storeBizevent(Constant.BIZEVENT_TYPE_SHIPMENTDOC_SUCCESS, order);
				}

			}
			catch (Exception e) {

				LOG.error(String.format(
						"Failed to send assignment to shipmentdoc interface id=%d, assignment number=%d",
						assignment.getAssignmentId(), assignment.getAssignmentNumber()));
				ExceptionUtil.errException(LOG, e, null);

				//BizeventTypeBsEntity findBizeventTypeBsEntity = referentialDataService.findBsEntity(BizeventTypeBsEntity.class, Constant.BIZEVENT_TYPE_SHIPMENTDOC_FAIL);

				for (AssignmentPartEntity orderPart : orderParts) {
					OrderEntity order = orderPart.getOrder();
					orderService._storeBizevent(Constant.BIZEVENT_TYPE_SHIPMENTDOC_FAIL, order);
				}
			}
		}
	}

	/**
	 * Orders without a contract should not be sent to SAP
	 * <p>
	 * AND
	 * <p>
	 * Pickup orders have to have the actualDeliveryDat set and in Delivered/Finished status for sending
	 */
	protected static boolean isValidForSending(OrderEntity order) {
		Boolean isValidForSending;

		SapContractEntity contract = order.getSapContract();
		if (contract != null) {
			isValidForSending = isValidPickupOrderInCaseOfPickup(order.getDeliveryType().getDeliveryTypeCd(), //
					order.getOrderStatus().getOrderStatusCd(), //
					contract.getIncotermsCd(), //
					order.getActualDeliveryDat());
		}
		else {
			isValidForSending = false;
		}

		return isValidForSending;
	}

	/**
	 *  actualDeliveryDat of the order must be already set if order status is delivered or later for pickup orders
	 *  and road direct orders with EXW incoterms to send them to SAP
	 *
	 * @return true 	if actualDeliveryDat has been set and order_status is greater or equal delivered
	 * 		   false	if actualDeliveryDat has not set or orderStatus is lower then delivered
	 */
	protected static boolean isValidPickupOrderInCaseOfPickup(Long deliveryTypeCd, Long orderStatusCd,
			String incotermsCd, Date actualDeliveryDat) {
		boolean result = true;

		if (LogonUtil.isPickupOrder(deliveryTypeCd) //
				|| LogonUtil.isShipPickupOrder(deliveryTypeCd) //
				|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd) //
				|| LogonUtil.isScheculedPickupOrder(deliveryTypeCd, incotermsCd)) {

			Boolean isActualDeliveryDateSet = actualDeliveryDat != null;
			Boolean isOrderStatusDeliveredOrFinished = orderStatusCd == Constant.ORDER_STATUS_DELIVERED
					|| orderStatusCd == Constant.ORDER_STATUS_FINISHED;

			result = isActualDeliveryDateSet && isOrderStatusDeliveredOrFinished;
		}

		return result;
	}

	/**
	 * 1.) Generate shipment document
	 * 2.) Send shipment document
	 * 3.) Set orders shipmentdoc timestamp
	 *
	 *  If the process fails the transaction will be reverted
	 */
	public void processShipmentDoc(Long assignmentId) {
		AssignmentEntity assignment = fetchAssignment(assignmentId);
		try {
			ShipmentDocumentDTO shipmentDocument = sapOutgoingDataService.getShipmentDocument(assignment);
			sapOutgoingDataService.sendShipmentDocument(shipmentDocument);
		}
		catch (LogonException e) {
			if (e.isReason(Reason.RES_ERROR_SAP_SHIPPINGPOINT_MISCONFIGURED)) {
				LOG.error(e.getMessage());
			}
			else {
				throw e;
			}
		}
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
		List<AssignmentPartEntity> orderParts = AssignmentUtils.collectOrderParts(assignmentParts);
		for (AssignmentPartEntity orderPart : orderParts) {
			OrderEntity order = orderPart.getOrder();
			order.setSentShipmentdocToSapTs(new Date());
		}
	}

	/**
	 * Load assignments and initialize important mapped entities
	 * <b>Should only be called via the orderService field</b> because only this we can be
	 * sure the transaction handling works properly
	 * @param assignmentIds - list of assignment ids
	 * @return the fetched entity list
	 */
	public AssignmentEntity fetchAssignment(Long assignmentId) {
		ReadWriteDao<AssignmentEntity> assignmentEntityDao = getDaoProvider().getAssignmentEntityDao();
		Filter<AssignmentEntity> assignmentFilter = assignmentEntityDao.createFilter();
		assignmentFilter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ASSIGNMENT_ID, assignmentId));
		AssignmentEntity assignment = assignmentEntityDao.findSingle(assignmentFilter);
		assignment.initialize();
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
		List<AssignmentPartEntity> orderParts = AssignmentUtils.collectOrderParts(assignmentParts);
		for (AssignmentPartEntity assignmentPartEntity : orderParts) {
			OrderEntity order = assignmentPartEntity.getOrder();
			order.initialize();
		}
		return assignment;
	}

	private void finishOrders() {
		Date now = new Date();
		// Subquery: All OrderIDs, which can be finished
		Filter<OrderReadyToFinishEntity> readyOrderFilter = new FilterImpl<OrderReadyToFinishEntity>();
		readyOrderFilter.addExpression(Restrictions.or(
				Restrictions.eq(OrderEntity.ATTR_FORCE_TO_SAP_FLAG, Boolean.TRUE),
				Restrictions.lt(OrderReadyToFinishEntity.ATTR_ACTUAL_DELIVERY_DAT, now)));
		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<OrderReadyToFinishEntity> readyOrderDao = daoProvider.getOrderReadyToFinishEntityDao();
		JPAQuery<OrderReadyToFinishEntity> sub = readyOrderDao.createJPAQuery();
		sub.setFilter(readyOrderFilter);
		sub.addSelect(SelectExpression.simpleSelect(OrderReadyToFinishEntity.ATTR_ORDER_ID));
		sub.setDistinct(true);

		@SuppressWarnings("unchecked")
		List<Long> orderIds = readyOrderDao.find(sub);
		if (orderIds == null || orderIds.isEmpty()) {
			LOG.info("No orders found to finish");
			return;
		}

		LOG.info(orderIds.size() + " orders found to finish");

		Filter<OrderEntity> orderFilter = new FilterImpl<OrderEntity>();
		orderFilter.addExpression(DbUtils.createSplittedInRestrictionOfIds(OrderEntity.ATTR_ORDER_ID, orderIds));

		List<OrderEntity> orders = new ArrayList<OrderEntity>();
		ReadOnlyDao<OrderEntity> orderDao = daoProvider.getOrderEntityDao();
		orders.addAll(orderDao.find(orderFilter));

		StringBuilder failedOrders = null;
		for (OrderEntity order : orders) {
			Long orderId = order.getOrderId();
			try {
				orderService._setToFinished(orderId);
			}
			catch (Exception exception) {
				LOG.error("Finishing order failed for order id=" + orderId + ", order number=" + order.getOrderNumber()
						+ ".");
				ExceptionUtil.errException(LOG, exception, null);
				if (failedOrders == null) {
					failedOrders = new StringBuilder();
				}
				else {
					failedOrders.append(", ");
				}
				failedOrders.append(orderId);
			}
		}
		if (failedOrders != null) {
			LOG.error("The following orders failed to finish: " + failedOrders + ".");
		}
		else if (!orders.isEmpty()) {
			LOG.info("All " + orders.size() + " orders successfully finished");
		}
	}

	private void sendCreditNotes() {
		ReadWriteDao<CreditNoteEntity> creditNoteEntityDao = getDaoProvider().getCreditNoteEntityDao();
		Filter<CreditNoteEntity> filter = creditNoteEntityDao.createFilter();
		filter.addExpression(Restrictions.isNull(CreditNoteEntity.ATTR_SENT_SALESDOC_TO_SAP_TS));
		filter.addExpression(Restrictions.eq(CreditNoteEntity.ATTR_ORDER_ITEM + "."
				+ OrderItemEntity.ATTR_RETURN_QUANT_HANDLING_CD, Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_CREATED));
		filter.addExpression(Restrictions.neq(CreditNoteEntity.ATTR_ORDER_ITEM + "." + OrderItemEntity.ATTR_ORDER + "."
				+ OrderEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_DELETED));
		List<CreditNoteEntity> creditNotes = creditNoteEntityDao.find(filter);

		LOG.info(creditNotes.size() + " credit notes found to send to SAP");
		StringBuilder failedCreditNotes = null;
		HashMap<Long, List<Long>> creditNoteIdByOrderId = organizeNotesToSend(creditNotes);
		for (Long orderId : creditNoteIdByOrderId.keySet()) {
			try {
				transmitToSap(creditNoteIdByOrderId.get(orderId));
			}
			catch (Exception e) {
				LOG.error("Sending credit notes failed for credit notes" + creditNoteIdByOrderId.get(orderId)
						+ ", order number=" + orderId + ".");
				ExceptionUtil.errException(LOG, e, null);
				if (failedCreditNotes == null) {
					failedCreditNotes = new StringBuilder();
				}
				else {
					failedCreditNotes.append(", ");
				}
				failedCreditNotes.append(creditNoteIdByOrderId.get(orderId));
			}
		}
		if (failedCreditNotes != null) {
			LOG.error("The following credit note failed to send to SAP: " + failedCreditNotes + ".");
		}
		else if (!creditNotes.isEmpty()) {
			LOG.info("All " + creditNotes.size() + " credit notes successfully sent to SAP");
		}
	}

	private HashMap<Long, List<Long>> organizeNotesToSend(List<CreditNoteEntity> notes) {
		HashMap<Long, List<Long>> sendNotes = new HashMap<Long, List<Long>>();
		for (CreditNoteEntity note : notes) {
			OrderItemEntity orderItem = note.getOrderItem();
			OrderEntity order = orderItem.getOrder();
			Long orderId = order.getOrderId();
			List<Long> value = sendNotes.get(orderId);
			if (value != null) {
				value.add(note.getCreditNoteId());
			} else {
				value = new ArrayList<Long>();
				value.add(note.getCreditNoteId());
				sendNotes.put(orderId, value);
			}
		}
		return sendNotes;
	}

	/**
	 * Loops through the material (bag/bulk) items of the order, and
	 * updates loaded/delivered fields. The updates applied are depending on if the
	 * material is bag/bulk.
	 *
	 * @param order
	 */
	private void copyLoadToOrderItems(OrderEntity order) {
		List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
		for (OrderItemEntity orderItemEntity : orderItems) {
			if (orderItemEntity.getLoad() != null) {
				updateOrderItemLoadedFields(orderItemEntity);
				updateOrderItemDeliveredFields(orderItemEntity);
			}
		}
	}

	private void updateOrderItemLoadedFields(OrderItemEntity oie) {
		if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(oie)) {
			if (oie.getLoad().getNetQuantity() != null) {
				oie.setLoadedQuantity(oie.getLoad().getNetQuantity());
				oie.setLoadedUom(oie.getLoad().getNetQuantityUom());
				updateOrderItemNumeratorAndDenominator(oie, Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR);
			}
		}
		else {
			// For bag case loaded quantity and UoM is already filled in, but we need to
			// update the conversion factors somewhere. One such place could be here.
			updateOrderItemNumeratorAndDenominator(oie, Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR);
		}
	}

	private void updateOrderItemDeliveredFields(OrderItemEntity oie) {
		if (oie.getLoad().getNetQuantity() != null) {
			if (LogonPackageHandlingUtil.isBag(oie)) {
				//by default it is the same as the loaded quantity
				final BigDecimal loadedQuantity = oie.getLoadedQuantity();
				final BigDecimal returnedQuantity = oie.getReturnedQuantity();
				final UomBsEntity loadedUom = oie.getLoadedUom();

				/* If a returned quantity is specified, it must be subtracted.
				 * For this, the returned quantity must be converted to the UOM of the
				 * loaded quantity.
				 */
				final BigDecimal deliveredQuantity;
				if (returnedQuantity != null) {
					final UomBsEntity returnedUom = oie.getReturnedUom();
					final BigDecimal returnedQtyInLoadedUom;
					final UomConversionResult conversion;

					try {
						conversion = unitOfMeasurementConverterService.convert(returnedQuantity, returnedUom,
								loadedUom, null, null, null, false);
					}
					catch (LogonConversionException e) {
						final String msg = "Could not convert UOM " + returnedUom + " of returned quantity to UOM "
								+ loadedUom + " of the loaded " + "quantity (" + e.getMessage() + ")";
						LOG.error(msg);
						throw new LogonException(msg, e);
					}

					returnedQtyInLoadedUom = conversion.getQuantity();

					deliveredQuantity = loadedQuantity.subtract(returnedQtyInLoadedUom);
				}
				else {
					deliveredQuantity = loadedQuantity;
				}

				oie.setDeliveredQuantity(deliveredQuantity);
				oie.setDeliveredUom(loadedUom);

				//if it can be converted to base uom, base uom will be set, and quantity according
				SapMaterialEntity sapMaterial = oie.getSapMaterial();
				if (sapMaterial != null && sapMaterial.getBaseUom() != null) {
					final BigDecimal deliveredQuantityInBaseUom = convertToBaseUom(sapMaterial,
							oie.getDeliveredQuantity(), oie.getDeliveredUom());

					if (deliveredQuantityInBaseUom != null) {
						oie.setDeliveredQuantity(deliveredQuantityInBaseUom);
						oie.setDeliveredUom(sapMaterial.getBaseUom());
					}
				}
			}
			else {
				oie.setDeliveredQuantity(oie.getLoad().getNetQuantity());
				oie.setDeliveredUom(oie.getLoad().getNetQuantityUom());
			}
		}
		oie.setDeliveredLoadContainer(oie.getLoad().getContainer());
		oie.setDeliveredLoadTrailer(oie.getLoad().getTrailer());
		oie.setDeliveredLoadTruck(oie.getLoad().getTruck());
		oie.setDeliveredLoadWagon(oie.getLoad().getWagon());
	}

	/**
	 * @param sapMaterial
	 * @param quantity
	 * @param uom
	 * @return
	 */
	private BigDecimal convertToBaseUom(SapMaterialEntity sapMaterial, BigDecimal quantity, UomBsEntity uom) {
		BigDecimal result = null;

		ReadOnlyDao<SapMaterialAltUomEntity> sapMatAltUomDao = getDaoProvider().getSapMaterialAltUomEntityDao();
		result = UOMConverter.convertAltUomToMaterialBaseUom(uom, quantity, sapMaterial, sapMatAltUomDao);

		if (result == null) {
			result = UOMConverter.convertUomToAnotherUomUsingCommonDimension(uom, sapMaterial.getBaseUom(), quantity,
					false);
		}

		if (result == null) {
			LOG.error("Could not convert the loaded quantity to base uom, by default loaded values are set to delivered values."
					+ " Sap material no="
					+ (sapMaterial != null ? sapMaterial.getSapMaterialNo() : "null")
					+ ","
					+ " loaded uom="
					+ (uom != null ? uom.getUomCd() : "null")
					+ ","
					+ " loaded quantity="
					+ (quantity != null ? quantity.toString() : "null")
					+ ","
					+ " base uom="
					+ ((sapMaterial != null && sapMaterial.getBaseUom() != null) ? sapMaterial.getBaseUom().getUomCd() : "null"));
		}
		return result;
	}

	/**
	 * Enter the unloading site. This can be the ship-to or the shipping point [LF].
	 * <br/>
	 * Mandatory event fields:
	 * <ul>
	 * <li>assignment</li>
	 * <li>order (for internal assignments)</li>
	 * </ul>
	 * Set timestampIsAccurate to true if the event's timestap (for rail orders) is known to be
	 * accurate and should not be replaced by the planned time in any case.
	 */
	public void enterUnloadingSite(BizeventEntity event, boolean timestampIsAccurate) {
		AssignmentEntity assignment = event.getAssignment();
		OrderEntity order = event.getOrder();
		if ((assignment == null) || (AssignmentUtils.isInternal(assignment) && (order == null))) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		List<LoadEntity> loads = AssignmentUtils.getLoadsFromAssignment(assignment);

		for (LoadEntity load : loads) {
			if (load != null && load.getNetQuantity() == null) {
				throw new LogonException(Reason.RES_ERROR_LOADING_NOT_YET_UNLOADING);
			}
		}

		if (order != null) {
			if (LogonStatusUtils.isArrivedAtUnloadingPointOrBeyond(order.getOrderStatus())) {
				// event arriving too late (after delivery), ignore
				return;
			}
		}
		else if (!AssignmentUtils.isInternal(assignment)) {
			for (AssignmentPartEntity part : assignment.getAssignmentParts()) {
				if (AssignmentUtils.isUnloadPart(part) && part.getExtOrder() != null) {
					if (LogonStatusUtils.isArrivedAtUnloadingPointOrBeyond(part.getExtOrder().getOrderStatus())) {
						return;
					}
					break;
				}
			}
		}

		BizEventUtil.processCoordinates(event);

		HashSet<Long> loadIds = new HashSet<Long>();
		List<OrderItemEntity> orderItems = order != null ? LogonPackageHandlingUtil.getMaterialItems(order) : new ArrayList<OrderItemEntity>();
		for (OrderItemEntity oie : orderItems) {
			if (oie.getLoad() != null) {
				loadIds.add(oie.getLoad().getLoadId());
			}
		}

		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_UNLOADING);

		AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
		partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_TRANSMITTED_ACK);

		List<AssignmentPartEntity> assignmentParts = AssignmentUtils.getSortedAssignmentParts(assignment);

		AssignmentPartEntity previousPart = null;
		boolean deliver = false;
		for (AssignmentPartEntity ap : assignmentParts) {
			if (AssignmentUtils.isPreLoadPart(ap)) {
				if (ap.getRealStart() == null) {
					ap.setRealStart(event.getEventTs());
				}
			}
			else if (AssignmentUtils.isLoadPart(ap)) {
				if ((ap.getLoad() != null) && loadIds.contains(ap.getLoad().getLoadId())) {
					ap.setAssignPartStatus(partStatus);
				}
				if (ap.getRealStart() == null) {
					ap.setRealStart(event.getEventTs());
				}
			}
			else if (AssignmentUtils.isUnloadPart(ap)) {
				if ((ap.getOrder() == null) || ap.getOrder().getOrderId().equals(order.getOrderId())) {
					if (!timestampIsAccurate && (assignment.getWagon() != null)
							&& event.getEventTs().before(ap.getPlannedStart())) {
						ap.setRealStart(ap.getPlannedStart());
					}
					else {
						ap.setRealStart(event.getEventTs());
					}
					ap.setAssignPartStatus(partStatus);
					previousPart.setAssignPartStatus(partStatus);
					if (previousPart.getRealStart() == null) {
						previousPart.setRealStart(ap.getRealStart());
						if (ap.getOrder() != null) {
							ap.getOrder().setNoPdaOrRailEventsFlag(Boolean.TRUE);
						}
					}
					WagonEntity we = event.getWagon();
					if (we != null) {
						RailwayStationEntity rse = null;
						if (ap.getShipto() != null) {
							rse = ap.getShipto().getShiptoSapBp().getRailwayStation();
						}
						else if (ap.getShippingpoint() != null) {
							rse = ap.getShippingpoint().getRailwayStation();
						}
						if (rse != null) {
							we.setLastKnownRailwayStation(rse);
							we.setArrivalLastKnownStationDat(ap.getRealStart());
							dispoService.setWagonTrack(new String[] { we.getWagonNo() }, null);
						}

						if (order != null
								&& Arrays.asList(Constant.DISTRIBUTION_DEST_RETURN).contains(
										order.getDistributionDest().getDistributionDestCd())) {
							// TODO (emoke Dec 1, 2010):this must remain here because there are still loads in the db which are related to empty wagons, but in the future there won't be anymore
							//in case we do not do this, rail dispo might show invalid load, since it is not unloaded
							//in the new orders services do NOT have a load anymore, so this part should be removed at some point
							//for return orders there is no process which reports the wagon empty, not as for normal and REP orders, so this is needed here for old ones
							for (OrderItemEntity oie : orderItems) {
								if (LogonPackageHandlingUtil.isService(oie) && oie.getLoad() != null
										&& oie.getLoad().getUnloadingTs() == null) {
									oie.getLoad().setUnloadingTs(event.getEventTs());
								}
							}
							deliver = true;
						}
					}
					if (ap.getShipto() != null) {
						if (ap.getShipto().getShiptoType().getShiptoTypeCd().longValue() == Constant.SHIPTO_TYPE_CONTAINER_TERMINAL
								&& (event.getWagon() != null || event.getTruck() != null)) {
							//Container terminal: separate containers from wagon, close rail order and create return order
							Filter<ContainerEntity> containerFilter = new FilterImpl<ContainerEntity>();
							if (we != null) {
								containerFilter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_WAGON_ID,
										event.getWagon().getWagonId()));
								deliver = true;
							}
							else {
								containerFilter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRUCK_ID,
										event.getTruck().getTruckId()));
							}
							Iterator<ContainerEntity> j = getDaoProvider().getContainerEntityDao().find(containerFilter).iterator();
							while (j.hasNext()) {
								ContainerEntity ce = j.next();
								ce.setArrivalLastKnownSpDat(event.getEventTs());
								ce.setLastKnownShippingpoint(ap.getShipto().getMatChainShippingpoint());
								ce.setCurrentWagon(null); // automatic composition change for wagon only, for truck it comes as a different event or is done at setUloaded
							}
						}
					}
					break;
				}
			}
			previousPart = ap;
		}

		if (order != null) {
			changeOrderStatus(order, new Long(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT));
			if (deliver) {
				for (OrderItemEntity orderItem : orderItems) {
					updateOrderItemDeliveredFields(orderItem);
				}
				setDelivered(event, true);

			}
			if (LogonUtil.isRailRepairOrder(order.getDeliveryType().getDeliveryTypeCd(),
					order.getDistributionDest().getDistributionDestCd())) {
				Filter<OrderRepEntity> orderRepFilter = new FilterImpl<OrderRepEntity>();
				orderRepFilter.addExpression(Restrictions.eq(OrderRepEntity.ATTR_ORDER, order));
				final OrderRepEntity orderRep = getDaoProvider().getOrderRepEntityDao().findSingle(orderRepFilter);

				if (orderRep == null) {
					throw new LogonException("A 'REP' Order does not have a corresponding OrderRep entity, "
							+ "cannot refresh last/next maintenance time on the Wagon!",
							Reason.RES_ORDER_INSUFFICIENT_DATA);
				}

				orderRep.setEnterPlantDat(event.getEventTs());
			}
		}
		else if (!AssignmentUtils.isInternal(assignment)) {
			for (AssignmentPartEntity ap : assignmentParts) {
				if (ap.getExtOrder() != null) {
					changeExternalOrderStatus(ap.getExtOrder(), Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);
					break;
				}
			}
		}
	}

	/**
	 * mandatory event fields:
	 * 	- assignment
	 *  - order
	 */
	public void startUnload(BizeventEntity event) {
		if ((event.getAssignment() == null)
				|| ((event.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_INTERNAL) && (event.getOrder() == null))) {

			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		if (((event.getCoordLat() == null) || (event.getCoordLon() == null)) && (event.getShipto() != null)) {
			if (event.getShipto().getShiptoType().getShiptoTypeCd().longValue() == Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED) {
				event.setCoordLat(event.getOrder().getLnfCoordLat());
				event.setCoordLon(event.getOrder().getLnfCoordLon());
			}
			else {
				event.setCoordLat(event.getShipto().getCoordLat());
				event.setCoordLon(event.getShipto().getCoordLon());
			}
		}
		else {
			checkShiptoCoords(event);
		}

		if (event.getOrder() != null) {
			long os = event.getOrder().getOrderStatus().getOrderStatusCd().longValue();
			if ((os != Constant.ORDER_STATUS_FINISHED) && (os != Constant.ORDER_STATUS_DELETED)) {
				if ((os != Constant.ORDER_STATUS_UNLOADING) && (os != Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT)) {
					enterUnloadingSite(event, false);
				}
				event.getAssignment().setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_UNLOADING);
				changeOrderStatus(event.getOrder(), new Long(Constant.ORDER_STATUS_UNLOADING));
			}
		}
		else if (event.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_EXTERNAL) {
			enterUnloadingSite(event, false);
			List<AssignmentPartEntity> assignmentParts = event.getAssignment().getAssignmentParts();
			for (AssignmentPartEntity assignmentPart : assignmentParts) {
				if (assignmentPart.getExtOrder() != null) {
					changeExternalOrderStatus(assignmentPart.getExtOrder(), new Long(Constant.ORDER_STATUS_UNLOADING));
					break;
				}
			}
		}
	}

	/**
	 * checks if the shipto has the fixed flag set and tries to take over the coordinates from
	 * the given event. If the takeover takes places, the fixed flag is set.
	 * @param event
	 */
	private void checkShiptoCoords(BizeventEntity event) {
		ShiptoEntity st = event.getShipto();
		if ((st != null) && (event.getCoordLat() != null) && (event.getCoordLon() != null)
				&& (!st.getCoordFixFlag().booleanValue())) {
			st.setCoordLon(event.getCoordLon());
			st.setCoordLat(event.getCoordLat());
			st.setCoordFixFlag(Boolean.TRUE);
		}
	}

	/**
	 * set the status of the return assignment part to "done"
	 * also sets the timestamp
	 * 	- assignment
	 * Shippingpoint/shipto/coords/homebase falg should be set if known
	 */
	public void setReturned(BizeventEntity event) {
		AssignmentEntity assignment = event.getAssignment();
		if (assignment == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		if (((event.getCoordLat() == null) || (event.getCoordLon() == null)) && (event.getShippingpoint() != null)) {
			event.setCoordLat(event.getShippingpoint().getCoordLat());
			event.setCoordLon(event.getShippingpoint().getCoordLon());
		}
		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_FINISHED);
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
		for (AssignmentPartEntity ap : assignmentParts) {
			if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_FINISHED) {
				ap.setRealStart(event.getEventTs());
			}
		}
	}

	/**
	 * Sets the return shipping point for the prev assignment and removes an eventual
	 * pre_load assignmet part for this assignment
	 * The caller has also to pass the duration of the trip from the shipto to the new shipping point.
	 * @param prevAssignmentId  id of the previous assignment
	 * @param thisAssignmentId id of this assignment
	 * @param newDuration new duration of the trip in minutes
	 */
	public void setPrevAssignmentReturnShippingpoint(Long prevAssignmentId, Long thisAssignmentId, long newDuration) {
		ReadWriteDao<AssignmentPartEntity> apDao = getDaoProvider().getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> f = new FilterImpl<AssignmentPartEntity>();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, prevAssignmentId));
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_FROM));
		AssignmentPartEntity prevapfrom = apDao.findSingle(f);
		f = new FilterImpl<AssignmentPartEntity>();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, prevAssignmentId));
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_FINISHED));
		AssignmentPartEntity prevapfinished = apDao.findSingle(f);
		f = new FilterImpl<AssignmentPartEntity>();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, thisAssignmentId));
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD));
		AssignmentPartEntity thisappre = apDao.findSingle(f);
		if (thisappre != null) {
			apDao.remove(thisappre);
		}
		f = new FilterImpl<AssignmentPartEntity>();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, thisAssignmentId));
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_LOAD));
		f.addExpression(Order.asc(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));
		AssignmentPartEntity thisapload = apDao.find(f).get(0);

		AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
		partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_NEW);

		if (prevapfrom == null) {
			prevapfrom = new AssignmentPartEntity();
			AssignmentPartTypeBsEntity apt = new AssignmentPartTypeBsEntity();
			apt.setAssignmentPartTypeCd(Constant.ASSIGNMENT_PART_TYPE_FROM);
			prevapfrom.setAssignmentPartType(apt);
			prevapfrom.setAssignment(prevapfinished.getAssignment());
			prevapfrom.setAssignmentPartSeqNo(prevapfinished.getAssignmentPartSeqNo().longValue() + 1);
			prevapfinished.setAssignmentPartSeqNo(prevapfinished.getAssignmentPartSeqNo().longValue() + 2);
			prevapfrom.setAssignPartStatus(partStatus);
			prevapfrom.setCoordLat(prevapfinished.getCoordLat());
			prevapfrom.setCoordLon(prevapfinished.getCoordLon());
			prevapfrom.setPlannedStart(prevapfinished.getPlannedStart());
			prevapfrom.setShippingpoint(prevapfinished.getShippingpoint());
			prevapfrom.setLnfShiptoFlag(prevapfinished.getLnfShiptoFlag());
			prevapfrom.setShipto(prevapfinished.getShipto());
			prevapfrom.setHomebaseFlag(Boolean.FALSE);
			apDao.persist(prevapfrom);
		}

		prevapfinished.setShippingpoint(thisapload.getShippingpoint());
		prevapfinished.setShipto(thisapload.getShipto());
		prevapfinished.setLnfShiptoFlag(thisapload.getLnfShiptoFlag());
		prevapfinished.setCoordLat(thisapload.getCoordLat());
		prevapfinished.setCoordLon(thisapload.getCoordLon());
		prevapfinished.setHomebaseFlag(Boolean.FALSE);

		prevapfrom.setAssignPartStatus(partStatus);
		prevapfinished.setAssignPartStatus(partStatus);

		newDuration *= getDurationCorrectionFactor(
				(prevapfinished.getShippingpoint() == null) ? null : prevapfinished.getShippingpoint().getShippingpointId(),
				(prevapfrom.getShipto() == null) ? null : prevapfrom.getShipto().getShiptoId(),
				prevapfinished.getOrder());

		long oldduration = prevapfinished.getPlannedStart().getTime() - prevapfrom.getPlannedStart().getTime();
		prevapfinished.setPlannedStart(new Date(prevapfinished.getPlannedStart().getTime() - oldduration + newDuration
				* 60000));
		prevapfinished.getAssignment().setAssignTrStatus(
				referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
						Constant.ASSIGN_STATUS_TR_NOT_TRANSMITTED));
	}

	/**
	 * Returns the count of order items for the load, which are not related to this specific order, but
	 * were merged into the same load from other orders.
	 * @param load
	 * @param order
	 * @return
	 */
	private long countOtherOrderItemsForLoad(LoadEntity load, OrderEntity order) {
		ReadWriteDao<OrderItemEntity> itemdao = getDaoProvider().getOrderItemEntityDao();
		Filter<OrderItemEntity> itemsOfLoadFilter = itemdao.createFilter();
		itemsOfLoadFilter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOAD_ID,
				load.getLoadId()));
		itemsOfLoadFilter.addExpression(Restrictions.neq(OrderItemEntity.ATTR_ORDER + "." + OrderEntity.ATTR_ORDER_ID,
				order.getOrderId()));
		return itemdao.count(itemsOfLoadFilter);
	}

	/**
	 * Removes an order from an assignment and adjusts the affected assignment parts accordingly
	 *
	 * @param assignmentPart assignmentPart Unload type, which has order reference
	 */
	private OrderEntity removeOrderFromAssignment(AssignmentPartEntity assignmentPart) {
		if ((assignmentPart == null)
				|| (assignmentPart.getAssignmentPartType().getAssignmentPartTypeCd() != Constant.ASSIGNMENT_PART_TYPE_UNLOAD)
				|| (assignmentPart.getOrder() == null)) {

			throw new IllegalArgumentException("assignmentPart must be of type unload and contain an order");
		}

		AssignmentEntity assignment = assignmentPart.getAssignment();
		OrderEntity order = assignmentPart.getOrder();

		if (assignment.getWagon() != null) {
			dispoService.transferEtranToWagon(assignment.getWagon(), order);
		}

		// Get the order's loads and set the order status
		Set<LoadEntity> loadsToRemove = new HashSet<LoadEntity>();
		long orderStatus = order.getOrderStatus().getOrderStatusCd().longValue();
		if (orderStatus == Constant.ORDER_STATUS_DELIVERED || orderStatus == Constant.ORDER_STATUS_FINISHED) {
			LOG.info("The order has already been delivered");
			return null;
		}
		/**
		 * In the case of tour prescheduled orders the order can be in status transmitted to carrier or even below.
		 */
		if (assignment.getAssignStatus().getAssignStatusCd().longValue() == AssignmentStatusUtil.ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER.getAssignStatusCd()) {
			changeOrderStatus(order, new Long(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER));
		}
		else {
			changeOrderStatus(order, new Long(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK));
		}

		order.setReschedulingFlag(false);
		order.setITrackAvailableFlag(Boolean.FALSE);
		order.setItrackStatus(null);

		List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
		for (OrderItemEntity oi : orderItems) {
			if (oi.getLoad() != null) {

				// Loaded quantity should be reset
				oi.setLoadedQuantity(null);
				oi.setLoadedUom(null);
				oi.setLrUomDenominator(null);
				oi.setLrUomNumerator(null);

				//count items included in this load, but not related to this order
				long countOtherMergedItemsInLoad = countOtherOrderItemsForLoad(oi.getLoad(), oi.getOrder());
				LoadEntity load = oi.getLoad();
				oi.setLoad(null);
				if (countOtherMergedItemsInLoad != 0) {
					// There are order items in this load which are not part of the canceled order, keep the load
					continue;
				}
				else if (((load.getLoadOrderId() == null) && ((load.getLoadingTs() == null)
						|| (load.getNetQuantity() == null) || (load.getNetQuantity().compareTo(BigDecimal.ZERO) == 0)))) {
					loadsToRemove.add(load);
				}
				else {
					// misuse the former location of the LDS delivery note number as a temporary
					// storage location for the LDS delivery note number now located on the order
					// in will be propagated to the order taking over this load (if applicable)
					// see LOGON-7348
					load.setWsDeliveryNoteNo(order.getLdsDeliveryNoteNo());
				}
			}

			updateOrderItemNumeratorAndDenominator(oi, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);
		}

		order.setLdsDeliveryNoteNo(null);

		List<AssignmentPartEntity> assignmentPartList = new ArrayList<AssignmentPartEntity>(
				assignment.getAssignmentParts());
		Collections.sort(assignmentPartList, new Comparator<AssignmentPartEntity>() {
			public int compare(AssignmentPartEntity o1, AssignmentPartEntity o2) {
				return o1.getAssignmentPartSeqNo().compareTo(o2.getAssignmentPartSeqNo());
			}
		});

		// Iterate over the assignment parts and alter/remove those parts affected
		int timeAdjustMin = 0;
		boolean noMoreUnloads = true;
		Date nextPlannedStart = null;
		AssignmentPartEntity ap = null;
		AssignmentPartEntity prevAp = null;
		AssignmentPartEntity apBefore = null;

		for (int i = 0; i < assignmentPartList.size(); i++) {
			AssignmentPartEntity prevPrevAp = prevAp;
			prevAp = ap;
			ap = assignmentPartList.get(i);
			boolean remove = false;
			switch (ap.getAssignmentPartType().getAssignmentPartTypeCd().intValue()) {
			case (int) Constant.ASSIGNMENT_PART_TYPE_LOAD:
				if (loadsToRemove.contains(ap.getLoad())) {
					remove = true;
					timeAdjustMin += -1
							* (int) ((assignmentPartList.get(i + 1).getPlannedStart().getTime() - ap.getPlannedStart().getTime()) / (60l * 1000l));
				}
				break;
			case (int) Constant.ASSIGNMENT_PART_TYPE_UNLOAD:
				if (ap.getAssignmentPartId().equals(assignmentPart.getAssignmentPartId())) {
					remove = true;
					nextPlannedStart = prevAp.getPlannedStart();
					apBefore = prevPrevAp;
					if (!assignment.getAssignmentParts().remove(prevAp)) {

						throw new LogonException();
					}

				}
				else {
					// The assignment is not to be canceled completely, there's more unloads left
					noMoreUnloads = false;
				}
				break;
			default:
				break;
			}
			if (remove) {
				if (!assignment.getAssignmentParts().remove(ap)) {

					throw new LogonException();
				}
			}
			else {
				if (ap.getAssignmentPartSeqNo().longValue() > assignmentPart.getAssignmentPartSeqNo().longValue()) {
					// The planned start is not known, set to null
					ap.setPlannedStart(nextPlannedStart);
					if (apBefore != null) {
						ap.setCoordLat(apBefore.getCoordLat());
						ap.setCoordLon(apBefore.getCoordLon());
						ap.setHomebaseFlag(apBefore.getHomebaseFlag());
						ap.setLnfShiptoFlag(apBefore.getLnfShiptoFlag());
						ap.setShippingpoint(apBefore.getShippingpoint());
						ap.setShipto(apBefore.getShipto());
						ap.setSiteCity(apBefore.getSiteCity());
						ap.setSiteContactPersonTxt(apBefore.getSiteContactPersonTxt());
						ap.setSiteCountry(apBefore.getSiteCountry());
						ap.setSiteLoadUnload(apBefore.getSiteLoadUnload());
						ap.setSiteName(apBefore.getSiteName());
						ap.setSitePostalCode(apBefore.getSitePostalCode());
						ap.setSiteStreetHouseNo(apBefore.getSiteStreetHouseNo());
						apBefore = null;
					}
				}
				else if (timeAdjustMin > 0) {
					// This is a part before the to-get-deleted unload part, adjust the start time based on the removed load part(s)
					Calendar c = Calendar.getInstance();
					c.setTime(ap.getPlannedStart());
					c.add(Calendar.MINUTE, timeAdjustMin);
					ap.setPlannedStart(c.getTime());
				}
			}
		}

		for (LoadEntity load : loadsToRemove) {
			goodsMovementService.removeLoadReferenceFromGoodsMovements(load);
			getDaoProvider().getLoadEntityDao().remove(load);
		}

		if (noMoreUnloads) {
			// This was the last order; complete assignment is canceled
			assignment.getAssignmentParts().clear();
			assignment.setActiveFlag(Boolean.FALSE);
			assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_CANCELLED);
			if (assignment.getAssignTrStatus().getAssignTrStatusCd().longValue() == Constant.ASSIGN_STATUS_TR_TRANSMITTED) {
				transmitDeallocateMessage(assignment, order);
			}
		}

		//returns the removed order
		return order;
	}

	/**
	 * Handles all the deallocate message sending for the assignment and specifically the order.
	 *
	 * @param assignment
	 * @param order
	 */
	private void transmitDeallocateMessage(AssignmentEntity assignment, OrderEntity order) {
		TruckEntity truck = assignment.getTruck();
		if (truck == null) {
			return;
		}
		if (order == null || !order.getNoPdaOrRailEventsFlag()) {
			asyncMopEventOutService.sendCancelTourToPDA(truck, assignment);
		}
		// send SMS to driver (if sms flag is enabled)
		final String smsSendTemplateCd = "order.deallocate.sms.message";
		Locale locale = LangUtil.getOrderLocale(order);
		String message = pdfGeneratorService.formatText(smsSendTemplateCd, locale,
				new Object[] { order.getOrderNumber().toString() });
		sendSmsToTruckDriver(truck, message);
	}

	/**
	* Removes the prescheduling data stored for the order
	* <p>
	* and removes the Order from the AssignmentPart
	* <p>
	* and cancel the assignment on the WeighSystem
	*/
	private void discardOrderPreschedule(OrderEntity order) {
		removePrescheduleDataForOrder(order);
		AssignmentPartEntity unloadPart = getAssignmentPartUnloadPrescheduled(order);
		if (unloadPart == null) {
			return;
		}
		AssignmentEntity assignment = unloadPart.getAssignment();
		removeOrderFromAssignment(unloadPart);
		if (assignment.getAssignStatus().getAssignStatusCd().longValue() != Constant.ASSIGNMENT_STATUS_CANCELLED) {
			throw new LogonException(
					"The preschedule assignment should have been deleted together with the planning [assignmentId="
							+ assignment.getAssignmentId() + "]", Reason.RES_ERROR_IMPL);
		}
		cancelOnWeighSystem(unloadPart.getAssignment(), order.getShippingpoint());
	}

	/** deletes the OrderPrescheduleEntity of the Order */
	private void removePrescheduleDataForOrder(OrderEntity order) {
		OrderPrescheduleEntity ope = findOrderPrescheduleForOrder(order);
		ReadWriteDao<OrderPrescheduleEntity> dao = getDaoProvider().getOrderPrescheduleEntityDao();
		if (ope != null) {
			dao.remove(ope);
		}
	}

	/**
	 * Removes a set orders from an assignment
	 *  @param assignment assignment from which to remove orders
	 * @param orderNumbers list of order numbers (as strings) to remove. If null, all orders will be removed
	 */
	public List<OrderEntity> removeAssignment(AssignmentEntity assignment, Collection<String> orderNumbers,
			boolean includeRailTransmitted) {
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
		boolean sendCancelTourToPDA = true;
		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);

		if (orders.size() >= 1 && orders.get(0).getNoPdaOrRailEventsFlag()) {
			sendCancelTourToPDA = false;
		}
		if (assignment.getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_INTERNAL) {
			List<AssignmentPartEntity> assignmentPartList = new ArrayList<AssignmentPartEntity>();
			int remainingNotYetLoadedOrdersCount = 0;
			for (AssignmentPartEntity ap : assignmentParts) {
				if ((ap.getOrder() != null)
						&& ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
					OrderEntity order = ap.getOrder();
					if ((orderNumbers == null) || (orderNumbers.contains(ap.getOrder().getOrderNumber().toString()))) {
						Long deliveryTypeCd = (order == null ? null : order.getDeliveryType().getDeliveryTypeCd().longValue());
						if ((order == null)
								|| includeRailTransmitted // for rail orders: honor includeRailTransmitted
								|| (deliveryTypeCd == Constant.DELIVERY_TYPE_BIMODAL_ROAD)
								|| (deliveryTypeCd == Constant.DELIVERY_TYPE_ROAD_DIRECT)
								|| (deliveryTypeCd == Constant.DELIVERY_TYPE_PICKUP)
								|| (deliveryTypeCd == Constant.DELIVERY_TYPE_SHIP_PICKUP)
								|| (deliveryTypeCd == Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP)
								|| (order.getOrderStatus().getOrderStatusCd().longValue() <= Constant.ORDER_STATUS_LOADED)) {
							assignmentPartList.add(ap);
						}
					}
					else {
						final OrderStatusBsEntity orderStatus = order.getOrderStatus();
						if (LogonStatusUtils.isBeforeLoaded(orderStatus)) {
							remainingNotYetLoadedOrdersCount++;
						}
					}
				}
			}

			List<OrderEntity> removedOrderEntities = new ArrayList<OrderEntity>();
			boolean cancelOnWeighSystem = false;
			for (Iterator<AssignmentPartEntity> it = assignmentPartList.iterator(); it.hasNext();) {
				AssignmentPartEntity ap = it.next();

				/**
				 * Once true -> remains true.
				 */
				cancelOnWeighSystem |= ((ap.getOrder().getOrderStatus().getOrderStatusCd().longValue() != Constant.ORDER_STATUS_DISPATCHED) || includeRailTransmitted);

				OrderEntity removedOrder = removeOrderFromAssignment(ap);
				// once the orders is de-allocated, we don't need the preschedule data anymore. The new assignment will create new preschedule info
				if (removedOrder != null) {
					removePrescheduleDataForOrder(removedOrder);
					removedOrderEntities.add(removedOrder);
				}
			}

			if (cancelOnWeighSystem && removedOrderEntities.size() != 0) {
				cancelOnWeighSystem(assignment, removedOrderEntities.get(0).getShippingpoint());
				// transmit the remaining assignment to the weighing system unless all orders have been removed
				if (remainingNotYetLoadedOrdersCount != 0) {
					transmitToWeighSystem(assignment);
				}
			}

			return removedOrderEntities;
		}

		for (AssignmentPartEntity ap : assignmentParts) {
			ExtOrderEntity extOrder = ap.getExtOrder();
			if (extOrder != null) {
				// 28-Jan-2014/toan: LOGON-9086 check the status before changing order status backward
				OrderStatusBsEntity orderStatus = extOrder.getOrderStatus();
				Long orderStatusCd = orderStatus.getOrderStatusCd();
				if (orderStatusCd < Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) {
					changeExternalOrderStatus(ap.getExtOrder(), new Long(
							Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK));
					break;
				}
			}
		}
		assignment.getAssignmentParts().clear();
		assignment.setActiveFlag(Boolean.FALSE);
		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_CANCELLED);
		if (sendCancelTourToPDA && assignment.getAssignTrStatus().getAssignTrStatusCd().longValue() == Constant.ASSIGN_STATUS_TR_TRANSMITTED) {
			asyncMopEventOutService.sendCancelTourToPDA(assignment.getTruck(), assignment);
		}
		// for external orders no normal order exists.
		return null;
	}

	/**
	 *
	 *
	 * @param assignment assignment from which to remove orders
	 */
	public void putOrdersToShippingState(AssignmentEntity assignment) {
		if (assignment.getAssignmentType().getAssignmentTypeCd().longValue() != Constant.ASSIGNMENT_TYPE_INTERNAL) {
			return;
		}
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
		for (AssignmentPartEntity ap : assignmentParts) {
			OrderEntity order = ap.getOrder();
			if (order != null
					&& ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
				if (order.getOrderStatus().getOrderStatusCd() == Constant.ORDER_STATUS_DELIVERED) {
					//fixme other conditions should be applied here or before or both?
					changeOrderStatus(order, new Long(Constant.ORDER_STATUS_SHIPPING));
				}
				else {
					LOG.error("orders can be put back to shipping state only from delivered state. orderId: "
							+ order.getOrderId() + "; current state: " + order.getOrderStatus().getOrderStatusCd());
					throw new LogonException("orders can be put back to shipping state only from delivered state",
							Reason.RES_ERROR_IMPL);
				}
			}
		}
	}

	/**
	 * set the status of the order as transmitted to the driver
	 * mandatory event fields:
	 * 	- assignment
	 *  - truck
	 */
	public void transmitToDriver(BizeventEntity event, long weighSystemSpCd, long weighSystemLpCd,
			boolean allowTransmitAssignmentToWeightSystem) throws LogonException {
		final TruckEntity truck = event.getTruck();
		final AssignmentEntity assignment = event.getAssignment();
		if ((assignment == null) || (truck == null)) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		if (AssignmentUtils.isOnHold(assignment)) {
			throw new LogonException(Reason.RES_ORDER_ON_HOLD);
		}

		boolean allLoadsLoaded = true;
		boolean isLdsOfflineMode = false;

		List<AssignmentPartEntity> assignmentPartList = assignment.getAssignmentParts();
		Collections.sort(assignmentPartList, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return ((AssignmentPartEntity) o1).getAssignmentPartSeqNo().compareTo(
						((AssignmentPartEntity) o2).getAssignmentPartSeqNo());
			}
		});
		OrderEntity lastOrder = null;
		AssignStatusBsEntity assignmentStatus = assignment.getAssignStatus();
		Long assignmentStatusCd = assignmentStatus.getAssignStatusCd();

		AssignmentPartEntity previousAssignmentPart = null;
		AssignmentPartTypeBsEntity previousAssignmentPartType = null;
		Long previousAssignmentPartTypeCd = null;

		for (AssignmentPartEntity currentAssignmentPart : assignmentPartList) {
			// Check whether all 'loading' assignment parts are loaded
			// Set real start time if assignment is 'new'
			AssignmentPartTypeBsEntity currentAssignmentPartType = currentAssignmentPart.getAssignmentPartType();
			Long currentAssignmentPartTypeCd = currentAssignmentPartType.getAssignmentPartTypeCd();
			LoadEntity currentLoad = currentAssignmentPart.getLoad();

			if (currentAssignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
				if (currentLoad != null && currentLoad.getLoadingTs() != null) {
					if (assignmentStatusCd == Constant.ASSIGNMENT_STATUS_NEW) {
						currentAssignmentPart.setRealStart(event.getEventTs());
					}
					isLdsOfflineMode = isLdsOfflineMode || currentLoad.getLdsOfflineMode().booleanValue();
					allLoadsLoaded = allLoadsLoaded && (currentLoad.getNetQuantity() != null)
							&& (currentLoad.getNetQuantity().doubleValue() > 0);
				}
				else {
					allLoadsLoaded = false;
				}
			} // Set real start time of first 'driving-to' if all loads have been loaded
			else if (currentAssignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_TO) {
				if (allLoadsLoaded && previousAssignmentPartTypeCd != null
						&& previousAssignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
					currentAssignmentPart.setRealStart(event.getEventTs());
					AssignmentEntity currentAssignment = currentAssignmentPart.getAssignment();
					AssignmentTypeBsEntity currentAssignmentType = currentAssignment.getAssignmentType();
					Long currentAssignmentTypeCd = currentAssignmentType.getAssignmentTypeCd();

					if (currentAssignmentTypeCd == Constant.ASSIGNMENT_TYPE_INTERNAL) {
						/**
						 * The order is immediately set to status loaded therefore send cancel to
						 * LDS as it will/and should never be loaded anymore.
						 */
						Collection<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(currentAssignment);
						OrderEntity firstOrder = orders.iterator().next();
						orderChangeService.cancelOnWeighSystem(currentAssignment.getAssignmentId(),
								firstOrder.getShippingpoint().getShippingpointId());
					}
				}
			} //Retrieve order from assignment part 'unloading'  Update order status
			else if (currentAssignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
				if (currentAssignmentPart.getOrder() != null) {
					OrderEntity order = currentAssignmentPart.getOrder();
					OrderStatusBsEntity orderStatus = order.getOrderStatus();
					Long orderStatusCd = orderStatus.getOrderStatusCd();
					lastOrder = order;

					boolean orderLoaded = (countNotLoadedItems(null, order, false) == 0);
					if (orderLoaded) {
						if (orderStatusCd < Constant.ORDER_STATUS_SHIPPING) {
							changeOrderStatus(order, new Long(
									allLoadsLoaded ? Constant.ORDER_STATUS_SHIPPING : Constant.ORDER_STATUS_LOADING));
						}
					}
					else if (orderStatusCd == Constant.ORDER_STATUS_DISPATCHED) {
						if (LogonUtil.isPlanningTruck(event.getTruck())) {
							storePreschedulerData(currentAssignmentPart, truck);
						}
						changeOrderStatus(order, new Long(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER));
						order.setReschedulingFlag(false);
					}
				}
				else if (currentAssignmentPart.getExtOrder() != null) {
					// 28-Jan-2015/toan: LOGON-9086 check the status before changing order status back to transmitted
					ExtOrderEntity extOrder = currentAssignmentPart.getExtOrder();
					OrderStatusBsEntity orderStatus = extOrder.getOrderStatus();
					Long orderStatusCd = orderStatus.getOrderStatusCd();
					if (orderStatusCd < Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) {
						changeExternalOrderStatus(currentAssignmentPart.getExtOrder(), new Long(
								Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER));
					}
				}
			}

			previousAssignmentPart = currentAssignmentPart;
			previousAssignmentPartType = previousAssignmentPart.getAssignmentPartType();
			previousAssignmentPartTypeCd = previousAssignmentPartType.getAssignmentPartTypeCd();
		}

		// Transmit assignment to weighing system
		if (isLdsOfflineMode || (!allLoadsLoaded && allowTransmitAssignmentToWeightSystem)) {
			transmitToWeighSystem(assignment);
		}
		boolean isITrackAssignment = false;
		if (lastOrder != null) {
			isITrackAssignment = AssignmentUtils.isITrackAssignment(assignment);
			lastOrder.setITrackAvailableFlag(isITrackAssignment);
		}

		if (allLoadsLoaded && assignmentStatusCd <= Constant.ASSIGNMENT_STATUS_SHIPPING) {
			assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_SHIPPING);
			if (!assignment.getTruck().getPdaAvailableFlag() && !isITrackAssignment) {
				closeAfterLoad(assignment, event.getEventTs());
			}
		}

		AssignTrStatusBsEntity transmittedStatus = referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
				Constant.ASSIGN_STATUS_TR_TRANSMITTED);
		assignment.setAssignTrStatus(transmittedStatus);
		asyncMopEventOutService.sendTourToPDA(truck, assignment);

		//TODO planning trucks do not have a driver, therefore perhaps not even necessary to check this flag.
		if (!LogonUtil.isPlanningTruck(truck) && lastOrder != null) {
			// send SMS to driver (only last order)
			String message = generateSmsMessageToTruckDriver(lastOrder);
			sendSmsToTruckDriver(truck, message);
		}
	}

	private String generateSmsMessageToTruckDriver(OrderEntity order) {
		Locale locale = LangUtil.getOrderLocale(order);
		String shippingpointName = order.getShippingpoint().getShippingpointName();
		String startDeliveryTime = DateTimeFormat.asHourMinute(order.getDeliveryFromDat());
		String startDeliveryDate = DateTimeFormat.asDayMonth(order.getDeliveryFromDat());
		String endDeliveryTime = DateTimeFormat.asHourMinute(order.getDeliveryToDat());
		String endDeliveryDate = DateTimeFormat.asDayMonth(order.getDeliveryToDat());
		String shiptoName = order.getShiptoSapBp().getName1();
		String shiptoAddress = order.getShiptoSapBp().getStreet();
		String shiptoPostalCode = order.getShiptoSapBp().getPostalCode();
		String shiptoCity = order.getShiptoSapBp().getCity();
		String orderValidationCode = order.getOrderValidationCode();
		// extra param for the ChoiceFormatter
		int orderValidationCodeNotSet = StringUtils.isNotEmpty(orderValidationCode) ? 0 : 1;

		String smsSendTemplateCd = "order.released.sms.message";

		// SMS template: "ORDER <LOGON order number> / Start: <Shipping point name> /
		//			 	Time: <Start delivery time planned>(<Finished delivery date planned>) - <Finished delivery time planned>
		// 			 	(<Finished delivery date planned>) / Ship-to: <Ship-to name>, <Ship-to address>, <Ship-to Postal Code>, <Ship-to City>".
		return pdfGeneratorService.formatText(smsSendTemplateCd, locale, new Object[] {
				order.getOrderNumber().toString(), shippingpointName, startDeliveryTime, startDeliveryDate,
				endDeliveryTime, endDeliveryDate, shiptoName, shiptoAddress, shiptoPostalCode, shiptoCity,
				orderValidationCodeNotSet, orderValidationCode });
	}

	/**
	 * Send an SMS to truck driver, with the given message.
	 *
	 * @param truck
	 * 			The truck of the driver
	 * @return
	 * 		<code>true</code> if the SMS is sent successful, otherwise <code>false</code>
	 */
	private boolean sendSmsToTruckDriver(TruckEntity truck, String message) {
		if (truck == null) {
			return false;
		}
		if (truck.getSmsSendFlag() == null || !truck.getSmsSendFlag()) {
			return false;
		}

		DriverEntity driver = truckAdminService.fetchDriver(truck.getTruckId());
		if (driver == null) {
			LOG.error("Couldn't load the Driver details for truck: " + truck.getTruckId());
			return false;
		}

		String number = driver.getUser().getPerson().getMobileNr();
		if (!NumberUtils.validateMobileNr(number)) {
			LOG.error("The drivers (driver_id: '" + driver.getDriverId() + "') mobile number is not valid: " + number);
			return false;
		}

		if (StringUtils.isEmpty(number)) {
			LOG.error("Cannot send SMS to driver: " + driver.getDriverId()
					+ ", because the Mobile number is empty or null!");
			return false;
		}

		if (!faxAndSmsService.callSendSms(number, message)) {
			LOG.error("Couldn't send the SMS to driver" + driver.getDriverId() + " with mobile number: " + number);
			return false;
		}

		return true;
	}

	private void storePreschedulerData(AssignmentPartEntity ape, TruckEntity currentTruck) {
		OrderPrescheduleEntity ope = findOrderPrescheduleForOrder(ape.getOrder());
		if (ope != null && ope.getTruck().getTruckId().equals(currentTruck.getTruckId())) {
			return;
		}
		ope = new OrderPrescheduleEntity();
		OrderEntity order = ape.getOrder();
		Long orderId = order.getOrderId();
		CarrierEntity carrier = order.getCommCarrier();

		ope.setTruck(currentTruck);
		ope.setOrder(order);

		ReadOnlyDao<DispoPlanningEntity> DispoPlanningEntityDao = getDaoProvider().getDispoPlanningEntityDao();
		Filter<DispoPlanningEntity> f = new FilterImpl<DispoPlanningEntity>();
		f.addExpression(Restrictions.eq(DispoPlanningEntity.ATTR_ORDER_ID, orderId));
		DispoPlanningEntity drp = DispoPlanningEntityDao.findSingle(f);

		if (drp != null) {
			BigDecimal freightCost = drp.getFreightCost();
			ope.setFreightCost(freightCost);
			ope.setFreightCostCurrency(carrier.getFreightRevenueCurrency());
		}

		Date loadStart = null;
		Date toStart = null;
		Date unloadStart = null;
		Date fromStart = null;

		List<AssignmentPartEntity> assignmentParts = ape.getAssignment().getAssignmentParts();
		for (AssignmentPartEntity a : assignmentParts) {
			if (a.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_LOAD)) {
				// FIXME (emoke Jan 10, 2012):store the order's loading time!
				loadStart = a.getPlannedStart();
			}
			else if (a.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_TO)) {
				//toStart - loadStart => loadduration
				toStart = a.getPlannedStart();
			}
			else if (a.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_UNLOAD)) {
				// FIXME (emoke Jan 10, 2012):store the concrete order's unloading time!
				// unloadStart - toStart => transport duration
				unloadStart = a.getPlannedStart();
			}
			else if (a.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_FROM)) {
				//fromStart - unloadStart => unloadDuration
				fromStart = a.getPlannedStart();
			}
		}

		ope.setPlannedStart(loadStart);

		int loadDuration = DateTimeUtils.getDifferenceInMinutes(loadStart, toStart);
		ope.setLoadDuration(new Long(loadDuration));

		int transportDuration = DateTimeUtils.getDifferenceInMinutes(toStart, unloadStart);
		ope.setTransportDuration(new Long(transportDuration));

		// FROM part is optional, so fromStart may be null
		if (fromStart != null) {
			int unloadDuration = DateTimeUtils.getDifferenceInMinutes(unloadStart, fromStart);
			ope.setUnloadDuration(new Long(unloadDuration));
		}

		ReadWriteDao<OrderPrescheduleEntity> dao = getDaoProvider().getOrderPrescheduleEntityDao();
		dao.merge(ope);
	}

	/**
	 * Calls the transmit order to parallel for the given load.
	 * @param loadId
	 */
	public void transmitToWeighSystemFromTerminal(Long assignmentId, Long shippingpointId) {
		if (assignmentId == null || shippingpointId == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		AssignmentEntity assignment = getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		ShippingpointEntity sp = getDaoProvider().getShippingpointEntityDao().findById(shippingpointId);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT);

		event.setAssignment(assignment);
		event.setTruck(assignment.getTruck());
		event.setShippingpoint(sp);
		event.setCoordLat(event.getShippingpoint().getCoordLat());
		event.setCoordLon(event.getShippingpoint().getCoordLon());
		event.setEventTs(new Date());

		monitoringTablesService.processBizevent(event);

		transmitToWeighSystem(assignment);
	}

	private void resendBimodalRoadOrders(OrderEntity order) {
		ReadWriteDao<AssignmentPartEntity> assignmentPartEntityDao = getDaoProvider().getAssignmentPartEntityDao();
		ReadWriteDao<OrderItemEntity> orderItemEntityDao = getDaoProvider().getOrderItemEntityDao();
		Set<Long> sentAssignments = new HashSet<Long>();
		List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
		for (OrderItemEntity orderItem : orderItems) {
			Filter<OrderItemEntity> filter1 = orderItemEntityDao.createFilter();
			filter1.addExpression(Restrictions.eq(OrderItemEntity.ATTR_BIMODAL_RAIL_ORDER_ITEM, orderItem));
			List<OrderItemEntity> truckOrderItemList = orderItemEntityDao.find(filter1);
			for (OrderItemEntity truckOrderItem : truckOrderItemList) {
				OrderEntity truckOrder = truckOrderItem.getOrder();
				Filter<AssignmentPartEntity> filter2 = assignmentPartEntityDao.createFilter();
				filter2.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "."
						+ AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
				filter2.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER, truckOrder));
				AssignmentPartEntity ap = assignmentPartEntityDao.findSingle(filter2);
				if (ap == null) {
					continue;
				}
				AssignmentEntity assignment = ap.getAssignment();
				if (sentAssignments.contains(assignment.getAssignmentId())) {
					continue;
				}
				AssignStatusBsEntity assignStatus = assignment.getAssignStatus();
				long assignStatusCd = assignStatus.getAssignStatusCd();
				AssignTrStatusBsEntity assignTrStatus = assignment.getAssignTrStatus();
				long assignTrStatusCd = assignTrStatus.getAssignTrStatusCd();
				if (assignStatusCd == Constant.ASSIGNMENT_STATUS_CANCELLED
						|| assignStatusCd == Constant.ASSIGNMENT_STATUS_FINISHED
						|| assignTrStatusCd != Constant.ASSIGN_STATUS_TR_TRANSMITTED) {
					continue;
				}
				// resend the tour
				asyncMopEventOutService.sendTourToPDA(assignment.getTruck(), assignment);
				sentAssignments.add(assignment.getAssignmentId());
			}
		}
	}

	/**
	 * set the status of the order as transmitted to the driver
	 * mandatory event fields:
	 * 	- assignment
	 *  - wagon
	 *  - railway station
	 */
	public void transmitToRailCompany(BizeventEntity event) throws LogonException {
		if ((event.getAssignment() == null) || (event.getWagon() == null) || (event.getRailwayStation() == null)) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		AssignmentEntity assignment = event.getAssignment();

		List<AssignmentPartEntity> assignmentParts = getAssignmentPartsInSeqOrder(assignment.getAssignmentId());
		Date[] dates = null;
		OrderEntity order = null;
		for (AssignmentPartEntity ape : assignmentParts) {
			if (ape.getOrder() != null) {
				order = ape.getOrder();
				changeOrderStatus(order, new Long(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL));
				order.setActualDepRailwayStation(event.getRailwayStation());

				dates = getDepartureArrivalDate(event.getRailwayStation().getRailwayStationId(),
						order.getShiptoSapBp().getRailwayStation().getRailwayStationId(), event.getEventTs());
				if (dates != null) {
					order.setActualDeliveryDat(dates[1]);
					copyLoadToOrderItems(order);
					try {
						resendBimodalRoadOrders(order);
					}
					catch (Exception ex) {
						LOG.error("An error occurred sending tour to PDA.", ex);
					}
					generateAndSendPDFWithoutExplicitLoadingpoint(order, true, true, false);
					if (order.getCommCarrier().getTransmitToCarrierFlag().booleanValue()) {
						edifactExportService.transmitToSbb(order, assignment.getWagon());
					}
				}
			}
		}

		if ((dates == null) || (order == null)) {
			throw new LogonException(Reason.RES_ORDER_PROCESSING_ERROR);
		}

		for (AssignmentPartEntity ape : assignmentParts) {
			switch (ape.getAssignmentPartType().getAssignmentPartTypeCd().intValue()) {
			case (int) Constant.ASSIGNMENT_PART_TYPE_LOAD: //fallthrough
			case (int) Constant.ASSIGNMENT_PART_TYPE_TO:
				ape.setPlannedStart(dates[0]);
				break;
			case (int) Constant.ASSIGNMENT_PART_TYPE_UNLOAD: //fallthrough
			case (int) Constant.ASSIGNMENT_PART_TYPE_FINISHED:
				ape.setPlannedStart(dates[1]);
				break;
			default:
				//other types not allowed in rail
				throw new LogonException(Reason.RES_ORDER_PROCESSING_ERROR);
			}
		}

		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_SHIPPING);
		assignment.setAssignTrStatus(referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
				Constant.ASSIGN_STATUS_TR_TRANSMITTED));
		order.setDeliveredWagon(event.getWagon());
		if (order.getDistributionDest().getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_REPAIR)) {
			ReadWriteDao<OrderRepEntity> orderRepEntityDao = getDaoProvider().getOrderRepEntityDao();
			Filter<OrderRepEntity> orderRepFilter = orderRepEntityDao.createFilter();
			orderRepFilter.addExpression(Restrictions.eq(OrderRepEntity.ATTR_ORDER_ID, order.getOrderId()));
			OrderRepEntity orderRep = orderRepEntityDao.findSingle(orderRepFilter);
			if (orderRep != null && orderRep.getEmailSendFlag().booleanValue()) {
				orderRep.initialize();
				try {
					sendOrderRepEmail(order, orderRep, null, false, true);
				}
				catch (Exception ex) {
					LOG.error("An error occurred sending rail repair mail.", ex);
				}
			}
		}
	}

	/**
	 * Helper to get the distinctive orders given a load
	 * @param load
	 * @return
	 */
	public Set<OrderEntity> getOrders(LoadEntity load) {
		List<OrderItemEntity> orderItems = getOrderItems(load);
		Set<OrderEntity> orders = new HashSet<OrderEntity>();
		for (OrderItemEntity orderItem : orderItems) {
			if (!orders.contains(orderItem.getOrder())) {
				orders.add(orderItem.getOrder());
			}
		}
		return orders;
	}

	/**
	 * Helper to get the order items for the load.
	 */
	public List<OrderItemEntity> getOrderItems(LoadEntity load) {
		if (load == null) {
			return null;
		}
		ReadWriteDao<OrderItemEntity> orderItemEntityDao = getDaoProvider().getOrderItemEntityDao();
		Filter<OrderItemEntity> filter = orderItemEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD, load));
		List<OrderItemEntity> orderItemList = orderItemEntityDao.find(filter);
		return orderItemList;
	}

	/**
	 * Decide whether to transmit the assignment based on the following rules:<br/>
	 * [1] If at least one order item in the assignment (there can be multiple order items in
	 *     multiple orders) has a specific loading point configured, then the assignment is
	 *     transmitted if and only if at least one of the loading points of the order items has
	 *     weighing type 'LDS'.<br/>
	 * [2] If none of the order items in the assignment has a specific loading point configured,
	 *     then the assignment is transmitted if and only if at least one of the loading points of
	 *     the shipping point has weighing type 'LDS'.
	 * @param assignment The assignment to (potentially) transmit.
	 */
	public void transmitToWeighSystem(AssignmentEntity assignment) {

		ShippingpointEntity shippingpoint = null;

		boolean loadingpointAvailable = false;
		boolean ldsEnabledForLoadingpoint = false;

		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);

		for (OrderEntity order : orders) {
			if (shippingpoint == null) {
				shippingpoint = order.getShippingpoint();
			}
			else if (!shippingpoint.getShippingpointId().equals(order.getShippingpoint().getShippingpointId())) {
				throw new LogonException("Multiple shipping points used for same assignment",
						Reason.RES_ERROR_UNSPECIFIED);
			}
			for (OrderItemEntity orderItem : order.getOrderItems()) {
				LoadingpointEntity loadingpoint = orderItem.getLoadingpoint();
				if (loadingpoint != null) {
					loadingpointAvailable = true;
					if (loadingpoint.getWeighSystemLp().getWeighSystemLpCd().equals(Constant.WEIGH_SYSTEM_LP_WS)) {
						ldsEnabledForLoadingpoint = true;
						break;
					}
				}
			}
		}

		if (shippingpoint == null) {
			return;
		}

		// [1]
		if (loadingpointAvailable) {
			if (ldsEnabledForLoadingpoint) {
				ldsService.sendLoadOrderNotification(assignment, shippingpoint);
			}
			else {
				return;
			}
		}
		// [2]
		else {
			ReadOnlyDao<LoadingpointEntity> dao = getDaoProvider().getLoadingpointEntityDao();

			Filter<LoadingpointEntity> f = dao.createFilter();
			f.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_SHIPPINGPOINT_ID,
					shippingpoint.getShippingpointId()));
			f.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_WEIGH_SYSTEM_LP_CD, Constant.WEIGH_SYSTEM_LP_WS));
			f.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));

			JPAQuery<LoadingpointEntity> queryForLoadingpoint = dao.createJPAQuery();
			queryForLoadingpoint.setFilter(f);
			queryForLoadingpoint.addSelect(SelectExpression.simpleSelect(LoadingpointEntity.ATTR_LOADINGPOINT_ID));

			@SuppressWarnings("unchecked")
			List<Long> loadingpointIds = dao.findNRecs(0, 1, queryForLoadingpoint);

			if (!loadingpointIds.isEmpty()) {
				ldsService.sendLoadOrderNotification(assignment, shippingpoint);
			}
		}
	}

	/**
	 * Acks the assignment by a driver.
	 *
	 * mandatory event fields:
	 * 	- assignment
	 */
	public void driverAck(BizeventEntity event) throws LogonException {
		AssignmentEntity assignment = event.getAssignment();
		if (event.getAssignment() == null) {
			LOG.error("Input bizevent doesn't have any assignment.");
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
		for (AssignmentPartEntity assignmentPartEntity : assignmentParts) {
			switch (assignmentPartEntity.getAssignmentPartType().getAssignmentPartTypeCd().intValue()) {
			case (int) Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD:
			case (int) Constant.ASSIGNMENT_PART_TYPE_LOAD:
			case (int) Constant.ASSIGNMENT_PART_TYPE_UNLOAD:
			case (int) Constant.ASSIGNMENT_PART_TYPE_FROM:
				driverAck(assignmentPartEntity);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * set the status of the assignment part as driver acknowledged
	 */
	public void driverAck(AssignmentPartEntity ap) {
		if (ap == null) {
			LOG.error("Input assignment part is null.");
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
		partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_TRANSMITTED_ACK);

		if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
			final OrderEntity order = ap.getOrder();
			if (order != null
					&& order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) {
				changeOrderStatus(order, new Long(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK));
			}

			AssignmentPartEntity prevAp = null;
			for (AssignmentPartEntity curr : ap.getAssignment().getAssignmentParts()) {
				if ((curr.getAssignmentPartSeqNo().longValue() < ap.getAssignmentPartSeqNo().longValue())
						&& ((prevAp == null) || (prevAp.getAssignmentPartSeqNo().longValue() < curr.getAssignmentPartSeqNo().longValue()))) {
					prevAp = curr;
				}
			}
			if (prevAp != null) {
				prevAp.setAssignPartStatus(partStatus);
			}
		}
		else if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD
				&& ap.getLoad() != null) {
			final Collection<OrderEntity> orders = getOrders(ap.getLoad());
			for (OrderEntity order : orders) {
				if (order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) {
					changeOrderStatus(order, new Long(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK));
				}
			}
		}

		ap.setAssignPartStatus(partStatus);
	}

	/**
	 * set the status of an assignment part to acknowledged by driver
	 * (for "pre" and "from" parts, according to event type)
	 * mandatory event fields:
	 * 	- assignment
	 *  - order
	 *  - truck
	 */
	public void driverAckAssignmentPart(BizeventEntity event) {
		if ((event.getAssignment() == null) || (event.getOrder() == null) || (event.getTruck() == null)) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		long partType = Constant.ASSIGNMENT_PART_TYPE_FROM;
		if (event.getBizeventType().getBizeventTypeCd().longValue() == Constant.BIZEVENT_TYPE_PRE_LOAD_TRANSMITTED_TO_TRUCK_ACK) {
			partType = Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD;
		}

		AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
		partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_TRANSMITTED_ACK);

		for (AssignmentPartEntity part : event.getAssignment().getAssignmentParts()) {
			if (part.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == partType) {
				part.setAssignPartStatus(partStatus);

				break;
			}
		}
	}

	/**
	 * sets the real timestamp of the assignment part "pre" and the assignemnt status
	 * to pre_loading
	 * 	- assignment
	 *  - truck
	 */
	public void leaveStartpoint(BizeventEntity event) {
		if ((event.getAssignment() == null) || (event.getTruck() == null)) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
		partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_TRANSMITTED_ACK);

		for (AssignmentPartEntity part : event.getAssignment().getAssignmentParts()) {
			if (part.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD) {
				part.setAssignPartStatus(partStatus);
				part.setRealStart(event.getEventTs());
				event.getAssignment().setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_PRE_LOAD);
				break;
			}
		}
	}

	/**
	 * Called when truck enters the geofence radius of the loading site, which can be the shipping
	 * point (standard), but also the ship-to (landfill).
	 * <br/>
	 * Mandatory event fields:
	 * <ul>
	 * <li>assignment</li>
	 * </ul>
	 */
	public void enterLoadingSite(BizeventEntity event) {
		AssignmentEntity assignment = event.getAssignment();
		if (assignment == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		boolean transmitTourToPda = false;
		Date orderDataSentDate = null;
		if (assignment.getTruck() != null) {
			ReadWriteDao<BizeventEntity> bizeventEntityDao = getDaoProvider().getBizeventEntityDao();
			Filter<BizeventEntity> filter = bizeventEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(BizeventEntity.ATTR_BIZEVENT_TYPE_CD,
					Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK));
			filter.addExpression(Restrictions.eq(BizeventEntity.ATTR_ASSIGNMENT, assignment));
			filter.addExpression(Order.desc(BizeventEntity.ATTR_EVENT_TS));
			List<BizeventEntity> transmitToDriverEvents = bizeventEntityDao.find(filter);
			if (!transmitToDriverEvents.isEmpty()) {
				BizeventEntity transmitToDriverEvent = (BizeventEntity) transmitToDriverEvents.get(0);
				orderDataSentDate = transmitToDriverEvent.getEventTs();
				// The bizevent is created first, then transmitToDriver is called which modifies the order.
				// We assume this all takes less than 3 seconds and we assume no one is so fast that (s)he can modify the order within this time.
				orderDataSentDate = DateUtils.addSeconds(orderDataSentDate, 3);
			}
		}

		for (AssignmentPartEntity ap : assignment.getAssignmentParts()) {
			OrderEntity order = ap.getOrder();
			ShiptoEntity loadingShipto = event.getShipto();
			if ((order != null && !LogonUtil.isOrderWithReversedDirection(order))
					|| isReverseOrderHavingShipto(order, loadingShipto)) {
				if (event.getOrder() == null) {
					event.setOrder(order);
				}
				changeOrderStatus(order, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);

				// If the order is a container order, always send an update to PDA (containing the most recent list of containers)
				if ((order.getShippingpoint() != null)
						&& (order.getShippingpoint().getShipppointType().getShipppointTypeCd().longValue() == Constant.SHIPPINGPOINT_TYPE_CONTAINER)) {
					transmitTourToPda = true;
					continue;
				}

				List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
				for (OrderItemEntity orderItem : orderItems) {
					// If loading from a wagon, always send an update to PDA (containing the most recent list of wagons)
					if (orderItem.getBimodalRailOrderItem() != null) {
						transmitTourToPda = true;
						break;
					}
					// Check if order item was modified since sending the tour.
					if ((orderDataSentDate != null) && (orderItem.getCtlModTs().after(orderDataSentDate))) {
						transmitTourToPda = true;
						break;
					}
				}
				// Check if the order was modified since sending the tour.
				if ((orderDataSentDate != null) && (order.getCtlModTs().after(orderDataSentDate))) {
					transmitTourToPda = true;
				}
			}
		}

		BizEventUtil.processCoordinates(event);
		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_LOADING);

		if ((event.getTruck() != null) && (transmitTourToPda)) {
			// send the latest info about the tour (loadingpoints, containers, etc)
			asyncMopEventOutService.sendTourToPDA(event.getTruck(), assignment);
		}

		if ((event.getTruck() != null) || (event.getWagon() != null)) {
			ReadWriteDao<AssignmentEntity> assignmentEntityDao = getDaoProvider().getAssignmentEntityDao();
			//check if older unclosed assignments exist and close them
			Filter<AssignmentEntity> f = assignmentEntityDao.createFilter();
			f.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ASSIGN_STATUS_CD,
					Constant.ASSIGNMENT_STATUS_RETURNING));
			f.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			if (event.getTruck() != null) {
				f.addExpression(Restrictions.eq(AssignmentEntity.ATTR_TRUCK, event.getTruck()));
			}
			else if (event.getWagon() != null) {
				f.addExpression(Restrictions.eq(AssignmentEntity.ATTR_WAGON, event.getWagon()));
			}
			List<AssignmentEntity> previousAssignments = assignmentEntityDao.find(f);
			for (AssignmentEntity ass : previousAssignments) {
				AssignmentPartEntity returning = null;
				AssignmentPartEntity finished = null;
				for (AssignmentPartEntity ap : ass.getAssignmentParts()) {
					long assignmentPartTypeCd = ap.getAssignmentPartType().getAssignmentPartTypeCd();
					if (assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_FROM) {
						returning = ap;
					}
					if (assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_FINISHED) {
						finished = ap;
					}
				}
				if ((returning != null) && (finished != null)) {
					// if returning started before the new event
					if ((returning.getRealStart() != null) && returning.getRealStart().before(event.getEventTs())) {
						finished.setRealStart(event.getEventTs());
						ass.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_FINISHED);
					}
					else {
						LOG.error("inconsistency between assignments " + ass.getAssignmentId() + " and "
								+ assignment.getAssignmentId() + " found");
					}
				}
			}
		}
	}

	/**
	 * Checks if the order belongs to the given shipto.
	 *
	 * @param order
	 * @param shipto
	 * @return boolean - order belongs to shipto
	 */
	private boolean isReverseOrderHavingShipto(OrderEntity order, ShiptoEntity shipto) {
		boolean result = false;
		SapBpEntity ordersShiptoSapBp = order != null ? order.getShiptoSapBp() : null;
		if (shipto != null && ordersShiptoSapBp != null && LogonUtil.isOrderWithReversedDirection(order)) {
			Long shiptoSapBpIdInOrder = ordersShiptoSapBp.getSapBpId();
			SapBpEntity loadingShiptoSapBp = shipto.getShiptoSapBp();
			Long shiptoSapBpIdOfShipto = loadingShiptoSapBp.getSapBpId();
			result = shiptoSapBpIdInOrder.equals(shiptoSapBpIdOfShipto);
		}
		return result;
	}

	/**
	 * changes the delivery time for the assignment
	 */
	public void setAssignmentDeliveryTime(Long assignmentId, Long orderId, Date deliveryTime, Long externalOrderId) {
		ReadWriteDao<AssignmentPartEntity> apDao = getDaoProvider().getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> f = apDao.createFilter();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, assignmentId));
		if (externalOrderId != null) {
			f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_EXT_ORDER_ID, externalOrderId));
			f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
					Constant.ASSIGNMENT_PART_TYPE_UNLOAD));
		}
		else {
			f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, orderId));
		}
		AssignmentPartEntity ap = apDao.findSingle(f);

		long diff = ap.getPlannedStart().getTime() - deliveryTime.getTime();

		List<AssignmentPartEntity> assignmentParts = getAssignmentPartsInSeqOrder(assignmentId);
		for (AssignmentPartEntity assignmentPart : assignmentParts) {
			long plannedStartTime = assignmentPart.getPlannedStart().getTime();
			assignmentPart.setPlannedStart(new Date(plannedStartTime - diff));
		}
	}

	/**
	 * Sets the position of a pre_load assignment part of this assignment to correspond to the
	 * finished assingment part of the prev assignment. Creates the pre assignment part if it
	 * doesn't exist.
	 * The caller has also to pass the duration of the pre_load assignment part.
	 * @param prevAssignmentId  id of the previous assignment
	 * @param thisAssignmentId id of this assignment
	 * @param newDuration new duration of the trip in minutes (uncorrected)
	 */
	public void setAssignmentPrePosition(Long prevAssignmentId, Long thisAssignmentId, long newDuration) {
		ReadWriteDao<AssignmentPartEntity> apDao = getDaoProvider().getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> f = apDao.createFilter();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, prevAssignmentId));
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_FINISHED));
		AssignmentPartEntity prevapfinished = apDao.findSingle(f);
		f = apDao.createFilter();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, thisAssignmentId));
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD));
		AssignmentPartEntity thisappre = apDao.findSingle(f);
		f = apDao.createFilter();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, thisAssignmentId));
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_LOAD));
		f.addExpression(Order.asc(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));
		AssignmentPartEntity thisapload = apDao.find(f).get(0);

		AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
		partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_NEW);

		if (thisappre == null) {
			f = apDao.createFilter();
			f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, thisAssignmentId));
			f.addExpression(Order.desc(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));
			List<AssignmentPartEntity> l = apDao.find(f);
			long offset = l.size() + 2;
			Iterator<AssignmentPartEntity> i = l.iterator();
			AssignmentPartEntity ap = i.next();
			long maxSeq = ap.getAssignmentPartSeqNo().longValue();
			ap.setAssignmentPartSeqNo(maxSeq + offset);
			while (i.hasNext()) {
				offset--;
				ap = i.next();
				ap.setAssignmentPartSeqNo(maxSeq + offset);
			}

			thisappre = new AssignmentPartEntity();
			AssignmentPartTypeBsEntity apt = new AssignmentPartTypeBsEntity();
			apt.setAssignmentPartTypeCd(Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD);
			thisappre.setAssignmentPartType(apt);
			thisappre.setAssignment(thisapload.getAssignment());
			thisappre.setAssignmentPartSeqNo(maxSeq + 1);
			thisappre.setAssignPartStatus(partStatus);
			thisappre.setHomebaseFlag(prevapfinished.getHomebaseFlag());
			thisappre.setLnfShiptoFlag(prevapfinished.getLnfShiptoFlag());
			apDao.persist(thisappre);
		}

		thisappre.setShippingpoint(prevapfinished.getShippingpoint());
		thisappre.setShipto(prevapfinished.getShipto());
		thisappre.setLnfShiptoFlag(prevapfinished.getLnfShiptoFlag());
		thisappre.setCoordLat(prevapfinished.getCoordLat());
		thisappre.setCoordLon(prevapfinished.getCoordLon());
		thisappre.setHomebaseFlag(prevapfinished.getHomebaseFlag());

		thisappre.setAssignPartStatus(partStatus);

		newDuration *= getDurationCorrectionFactor(
				(thisapload.getShippingpoint() == null) ? null : thisapload.getShippingpoint().getShippingpointId(),
				(thisappre.getShipto() == null) ? null : thisappre.getShipto().getShiptoId(), thisapload.getOrder());

		thisappre.setPlannedStart(new Date(thisapload.getPlannedStart().getTime() - newDuration * 60000));
		thisappre.getAssignment().setAssignTrStatus(
				referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
						Constant.ASSIGN_STATUS_TR_NOT_TRANSMITTED));
	}

	/**
	 * called when driver scans loading point RFID tag with his PDA
	 * mandatory event fields:
	 * 	- order
	 *  - loadingPoint
	 */
	public void pdaLogin(BizeventEntity event, LoadingpointEntity loadingPoint) {
		if ((event.getOrder() == null) || (loadingPoint == null)) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		BizEventUtil.processCoordinates(event);

		if (event.getAssignment() != null) {
			event.getAssignment().setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_LOADING);
			for (AssignmentPartEntity ap : event.getAssignment().getAssignmentParts()) {
				if (ap.getOrder() != null) {
					if (ap.getOrder().getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK) {
						changeOrderStatus(ap.getOrder(), Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
					}
				}
			}
		}
		else if (event.getOrder().getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK) {
			changeOrderStatus(event.getOrder(), Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
		}

		if (!(loadingPoint.getWeighSystemLp().getWeighSystemLpCd().equals(Constant.WEIGH_SYSTEM_LP_WS) && loadingPoint.getShippingpoint().getWeighSystemSp().getWeighSystemSpCd().equals(
				Constant.WEIGH_SYSTEM_SP_PARALLEL_TRUCK))) {
			LOG.error("pdaLogin: not a parallel loadingpoint (assignmentId = "
					+ event.getAssignment().getAssignmentId() + ")");
			throw new LogonException(Reason.RES_ORDER_PROCESSING_ERROR);
		}

		List<OrderItemEntity> orderItems = (List<OrderItemEntity>) LogonPackageHandlingUtil.getMaterialItems(event.getOrder());
		OrderItemEntity orderItem = LogonUtil.getBulkDumpOrPowderOrderItem(orderItems);
		if (orderItem == null) {
			LOG.error("No valid order item found for order " + event.getOrder().getOrderId());

			throw new LogonException("No valid order item found for order", Reason.RES_ORDER_PROCESSING_ERROR);
		}

		if (event.getWagon() == null) {

			TruckEntity truck = event.getTruck();
			TrailerEntity trailer = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), false);
			ContainerEntity container = truckAdminService.fetchContainerForTruckTrailerComposition(truck, trailer);

			/*
			 * Check the composition against the material package. At this point the composition has to be whole, and valid.
			 */

			PackageTypeBsEntity logonPackageType = orderItem.getLoad().getSapMaterial().getMaterialPackage().getLogonPackageType();
			if (logonPackageType != null) {
				boolean validComposition = LogonUtil.isTruckCompositionValidForMaterialPackage(
						logonPackageType.getPackageTypeCd(), truck, trailer, container);
				if (!validComposition) {
					throw new LogonException("Invalid truck composition", Reason.RES_ERROR_INVALID_TRUCK_COMPOSITION);
				}
			}
		}

		AssignmentEntity assignment = event.getAssignment();
		if (assignment == null) {
			AssignmentPartEntity assignmentPartUnload = getAssignmentPartUnload(event.getOrder());

			if (assignmentPartUnload != null) {
				assignment = assignmentPartUnload.getAssignment();
			}
		}

		if (assignment != null) {
			ldsService.sendPdaLogin(assignment, loadingPoint.getShippingpoint(), loadingPoint);
		}
	}

	/**
	 * Sets the loading start timestamp to the load and its assignment
	 * Called when a start load event comes from an external source
	 * <br/>
	 * Mandatory fields:
	 * <ul>
	 * <li>load</li>
	 * </ul>
	 */
	public void startLoad(BizeventEntity event, LoadEntity load, LoadingpointEntity lp, BigDecimal taraMeasureT,
			EventSource source) {
		// For external orders, only set real start time of load assignment part.
		if (event.getAssignment() != null && AssignmentUtils.isExternal(event.getAssignment())) {
			event.getAssignment().setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_LOADING);
			for (AssignmentPartEntity ap : event.getAssignment().getAssignmentParts()) {
				if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
					ap.setRealStart(event.getEventTs());
				}
			}

			return;
		}
		else if (load == null) {
			// For internal orders, the load is mandatory.
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		if (load.getSapMaterial() != null && LogonUtil.isAggPlanningMaterial(load.getSapMaterial())) {
			LOG.error("agg planning material cannot be loaded");
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		// For trucks, make sure the truck/trailer/composition is valid.
		TruckEntity truck = event.getTruck();
		if (truck != null) {
			// Load trailer and container for truck
			TrailerEntity trailer = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), false);
			ContainerEntity container = truckAdminService.fetchContainerForTruckTrailerComposition(truck, trailer);

			/*
			 * Check the composition against the material package. At this point the composition
			 * has to be whole, and valid except the case container terminal, where
			 * scancontainer comes right after startload.
			 */
			if (event.getShippingpoint() != null
					&& !event.getShippingpoint().getShipppointType().getShipppointTypeCd().equals(
							Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_RAIL)
					&& !event.getShippingpoint().getShipppointType().getShipppointTypeCd().equals(
							Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
				PackageTypeBsEntity logonPackageType = load.getSapMaterial().getMaterialPackage().getLogonPackageType();
				if (logonPackageType != null) {
					boolean validComposition = LogonUtil.isTruckCompositionValidForMaterialPackage(
							logonPackageType.getPackageTypeCd(), truck, trailer, container);
					if (!validComposition) {
						throw new LogonException("Invalid truck composition",
								Reason.RES_ERROR_INVALID_TRUCK_COMPOSITION);
					}
				}
			}

			// It is not allowed to load a planning truck.
			if (LogonUtil.isPlanningTruck(truck)) {
				throw new LogonException("Invalid truck composition", Reason.RES_ERROR_INVALID_TRUCK_COMPOSITION);
			}
		}
		boolean isLdsOfflineMode = load.getLdsOfflineMode();
		Boolean isReverseDirectedOrder = false;
		Collection<OrderEntity> orderList = getOrders(load);
		final OrderEntity firstOrder;
		if (orderList != null && orderList.size() > 0) {
			firstOrder = orderList.iterator().next();
			isReverseDirectedOrder = LogonUtil.isOrderWithReversedDirection(firstOrder);

			if (LogonUtil.isLandfill(firstOrder)) {
				validateLandfillOrderBeforeLoading(orderList, firstOrder, source);
			}
		}
		else {
			firstOrder = null;
		}

		boolean loadingAtCustomerSite = isReverseDirectedOrder && firstOrder != null
				&& LogonStatusUtils.isBeforeArrivedAtUnloadingPoint(firstOrder.getOrderStatus())
				&& source == EventSource.MOP;

		if (load.getSapMaterial() != null && LogonUtil.isLandfillPlanningMaterial(load.getSapMaterial())
				&& !loadingAtCustomerSite) {
			LOG.error("landfill planning material cannot be measured at crh site");
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		// Cancel the load order on the weighing system, since it was actually loaded by Logon or PDA.
		if (source != EventSource.LDS && firstOrder != null && event.getAssignment() != null && !(loadingAtCustomerSite || isLdsOfflineMode)) {
			cancelOnWeighSystem(event.getAssignment(), firstOrder.getShippingpoint());
		}
		writeThroughCompositionDataToLoad(load, event.getTruck(), event.getContainer());

		boolean resendTourToPda = false;
		LoadingpointEntity loadLp = load.getLoadingpoint();
		List<OrderItemEntity> orderItemList = getOrderItems(load);
		Set<OrderEntity> orders = new HashSet<OrderEntity>();
		for (OrderItemEntity orderItem : orderItemList) {
			if (orderItem.getBimodalRailOrderItem() != null) {
				// Order is being loaded from a wagon, set the wagon's order to unloading.
				OrderEntity railOrder = orderItem.getBimodalRailOrderItem().getOrder();
				if ((railOrder.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL)
						|| (railOrder.getOrderStatus().getOrderStatusCd().longValue() < Constant.ORDER_STATUS_UNLOADING)) {
					changeOrderStatus(railOrder, Constant.ORDER_STATUS_UNLOADING);
				}
				orderItem.getLoad().setLoadedFromLoad(orderItem.getBimodalRailOrderItem().getLoad());
			}
			if (!orders.contains(orderItem.getOrder())) {
				// per order
				if ((event.getWagon() != null)
						&& (orderItem.getOrder().getOrderStatus().getOrderStatusCd().longValue() < Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT)) {
					// Wagon order might not have been dispatched (manually)
					changeOrderStatus(orderItem.getOrder(), Constant.ORDER_STATUS_DISPATCHED);
				}
				if (!isReverseDirectedOrder || loadingAtCustomerSite) {
					changeOrderStatus(orderItem.getOrder(), Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
					changeOrderStatus(orderItem.getOrder(), Constant.ORDER_STATUS_LOADING);
				}
				else if (source != EventSource.MOP) {
					changeOrderStatus(orderItem.getOrder(), Constant.ORDER_STATUS_UNLOADING);
				}
				orders.add(orderItem.getOrder());
			}

			// The LP was not known, but now it is, or it changed the order data will be re-sent to PDA
			LoadingpointEntity itemLp = orderItem.getLoadingpoint();
			if ((lp != null) && ((loadLp == null) || !lp.getLoadingpointId().equals(loadLp.getLoadingpointId()))) {
				/*
				 * If the load does not have a loadingpoint, but the order item corresponds with
				 * the actual LP, the lp is already known to the pda, otherwise always resend!
				 */
				if (loadLp != null || itemLp == null || !lp.getLoadingpointId().equals(itemLp.getLoadingpointId())) {
					resendTourToPda = true;
				}
				load.setLoadingpoint(lp);
			}
		}

		BizEventUtil.processCoordinates(event);

		AssignmentPartEntity loadPart = getAssignmentPartLoad(load);
		if (loadPart != null) {
			AssignmentEntity assignment = loadPart.getAssignment();

			if (!isReverseDirectedOrder) {
				//drive to part - delete the drive-to's real starts!
				List<AssignmentPartEntity> toPartList = getAssignmentPartTo(assignment.getAssignmentId());
				for (AssignmentPartEntity currentAssignmentPart : toPartList) {
					currentAssignmentPart.setRealStart(null);
				}
			}

			if (!isReverseDirectedOrder
					|| (isReverseDirectedOrder && (loadingAtCustomerSite || isLdsOfflineMode))) {
				loadPart.setRealStart(event.getEventTs());
			}

			// Send update to PDA
			if (assignment.getTruck() != null && resendTourToPda) {
				asyncMopEventOutService.sendTourToPDA(assignment.getTruck(), assignment);
			}

			if ((source != EventSource.MOP) && (assignment.getTruck() != null) && (load.getLoadingTs() == null)) {
				LOG.debug("Sending LOADING STARTED message to PDA related to load" + load.getLoadId());
				asyncMopEventOutService.sendLoadingStarted(assignment.getTruck(), loadPart);
			}

			if (isReverseDirectedOrder && !(loadingAtCustomerSite || isLdsOfflineMode)) {
				assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_UNLOADING);
			}
			else {
				assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_LOADING);
			}
			AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
			partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_TRANSMITTED_ACK);
			loadPart.setAssignPartStatus(partStatus);
		}

		if (source == EventSource.LDS && load.getTareWeightT() != null && load.getWagon() != null) {
			//in case it is coming from an lds, do not let overwrite the tare quantity, but keep the original see ticket LOGON-6570
			taraMeasureT = load.getTareWeightT();
		}

		if (load.getTareWeightT() == null && isReverseDirectedOrder) {
			taraMeasureT = truck.getTareWeightT();
		}
		boolean isAccurateQty = !(loadingAtCustomerSite || isLdsOfflineMode);
		updateMeasureData(load, taraMeasureT, null, null, event.getEventTs(), isAccurateQty);

		event.setDescription(((load.getTareWeightT() != null) ? load.getTareWeightT().toPlainString() : "") + " "
				+ Constant.UOM_TONS_TXT);

		load.setLoadingTs(event.getEventTs());
	}

	private void validateLandfillOrderBeforeLoading(Collection<OrderEntity> orderList, OrderEntity firstOrder,
			EventSource source) {
		if (orderList.size() > 1) {
			throw new LogonException("a landfill load can only have 1 order connected",
					Reason.RES_ERROR_LANDFILL_MULTIPLE_ORDERS);
		}
		if (firstOrder.getOrderItems() == null || firstOrder.getOrderItems().size() != 1) {
			throw new LogonException("a landfill order can only have 1 orderItem",
					Reason.RES_ERROR_LANDFILL_MULTIPLE_ORDERITEMS);
		}
		if (source != EventSource.MOP
				&& LogonStatusUtils.isArrivedAtUnloadingPointOrBeyond(firstOrder.getOrderStatus())) {
			throw new LogonException(
					"not allowed startLoad call in this order status (arrived at unloading point or beyond)",
					Reason.RES_ERROR_REVERSE_NOT_ALLOWED_STARTLOAD);
		}
	}

	/**
	 * Sets the current composition to the loadEntity. The composition contains truck+trailer+container. Trailer and container
	 * are retrieved from DB according to the current composition.
	 * @param load
	 * @param truck
	 * @param container
	 */
	public void writeThroughCompositionDataToLoad(LoadEntity load, TruckEntity truck, ContainerEntity container) {
		// Set the load's trailer/container according to the currently known composition
		if (truck != null) {
			TrailerEntity currentTrailer = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), false);
			load.setTrailer(currentTrailer);

			ContainerEntity currentContainer = truckAdminService.fetchContainerForTruckTrailerComposition(truck,
					currentTrailer);
			load.setContainer(currentContainer);
		}
		else if (container != null) {
			load.setContainer(container);
		}
	}

	/**
	 * Returns the count of such order items for the order, which are not yet loaded (loading_ts is null).
	 * In case load parameter is not null, that load will not be considered in the count (suppose that is under
	 * loading,and that is the last one).
	 * @param load
	 * @param order
	 * @param strict consider just items that are already laoded
	 * @return
	 */
	protected long countNotLoadedItems(LoadEntity load, OrderEntity order, boolean strict) {
		if (strict) {
			//NORMALLY THIS QUERY WOULD LIKE THE FOLLOWING, BUT THERE IS AN EXCEPTION IF WE USE THAT - THEREFORE USING DIRECTJPA DAO UNTIL THIS ISSUE IS SOLVED
			//THE FILTER COULD ALSO BE USED LIKE THIS: notLoadedItemsFilter.addExpression(Restrictions.neq("x." + OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOAD_ID, load.getLoadId()));
			//(adding x. in front of the alias, as far the Entity is named as x in daosupport)
			//				[junit] Caused by: java.lang.NullPointerException
			//				[junit]	 at org.hibernate.hql.ast.tree.FromClause.findIntendedAliasedFromElementBasedOnCrazyJPARequirements(FromClause.java:120)

			//				ReadWriteDao<OrderItemEntity> itemdao = getDaoProvider().getOrderItemEntityDao();
			//				Filter notLoadedItemsFilter = new FilterImpl();
			//				if (load != null) {
			//					notLoadedItemsFilter.addExpression(Restrictions.neq(OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOAD_ID, load.getLoadId()));
			//				}
			//				notLoadedItemsFilter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_ORDER + "." + OrderEntity.ATTR_ORDER_ID, order.getOrderId()));
			//				notLoadedItemsFilter.addExpression(
			//						Restrictions.or(
			//							Restrictions.isNull(OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOADING_TS),
			//							Restrictions.isNull(OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_NET_QUANTITY),

			if (load != null) {
				String query = "SELECT COUNT(x) FROM " + OrderItemEntity.class.getSimpleName() + " x where " + " x."
						+ OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOAD_ID + " <> ?1 and " + " x."
						+ OrderItemEntity.ATTR_ORDER + "." + OrderEntity.ATTR_ORDER_ID + " = ?2 " + "  and (x."
						+ OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOADING_TS + " is null or " + " x."
						+ OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_NET_QUANTITY + " is null )";
				Long res = ((Long) getDaoProvider().getDirectJpaDao().findSingle(query, load.getLoadId(),
						order.getOrderId()));
				if (res == null) {
					LOG.error("Error when querying for not loaded loads for order " + order.getOrderId());
					return 0L;
				}
				return res.longValue();
			}
			String query = "SELECT COUNT(x) FROM " + OrderItemEntity.class.getSimpleName() + " x where " + " x."
					+ OrderItemEntity.ATTR_ORDER + "." + OrderEntity.ATTR_ORDER_ID + " = ?1 " + "  and (x."
					+ OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOADING_TS + " is null or " + " x."
					+ OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_NET_QUANTITY + " is null or " + " x."
					+ OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_NET_QUANTITY + " = ?2 )";
			Long res = ((Long) getDaoProvider().getDirectJpaDao().findSingle(query, order.getOrderId(),
					new BigDecimal(0)));
			if (res == null) {
				LOG.error("Error when querying for not loaded loads for order " + order.getOrderId());
				return 0L;
			}
			return res.longValue();

		}
		ReadWriteDao<OrderItemEntity> itemdao = getDaoProvider().getOrderItemEntityDao();
		Filter<OrderItemEntity> notLoadedItemsFilter = itemdao.createFilter();
		if (load != null) {
			notLoadedItemsFilter.addExpression(Restrictions.neq(OrderItemEntity.ATTR_LOAD + "."
					+ LoadEntity.ATTR_LOAD_ID, load.getLoadId()));
		}
		notLoadedItemsFilter.addExpression(Restrictions.eq(
				OrderItemEntity.ATTR_ORDER + "." + OrderEntity.ATTR_ORDER_ID, order.getOrderId()));
		notLoadedItemsFilter.addExpression(Restrictions.isNull(OrderItemEntity.ATTR_LOAD + "."
				+ LoadEntity.ATTR_LOADING_TS));

		return itemdao.count(notLoadedItemsFilter);
	}

	public BigDecimal getWagonRemainingQuantityT(LoadEntity wagonLoad) {
		ReadWriteDao<LoadEntity> loadEntityDao = getDaoProvider().getLoadEntityDao();
		BigDecimal remainingQuantityT = null;
		Filter<LoadEntity> filter = loadEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_LOADED_FROM_LOAD, wagonLoad));
		List<LoadEntity> loadedFromList = loadEntityDao.find(filter);
		BigDecimal loadedFromQuantity = BigDecimal.ZERO;
		for (LoadEntity loadedFromLoad : loadedFromList) {
			if (loadedFromLoad.getNetQuantity() != null) {
				BigDecimal quantity = UOMConverter.getUomConvertedValue(loadedFromLoad.getNetQuantityUom(),
						loadedFromLoad.getNetQuantity(), false);
				if (quantity != null) {
					loadedFromQuantity = loadedFromQuantity.add(quantity);
				}
			}
		}
		if (wagonLoad.getNetQuantity() != null) {
			BigDecimal wagonQuantity = UOMConverter.getUomConvertedValue(wagonLoad.getNetQuantityUom(),
					wagonLoad.getNetQuantity(), false);
			if (wagonQuantity != null) {
				remainingQuantityT = wagonQuantity.subtract(loadedFromQuantity);
			}
		}

		return remainingQuantityT;
	}

	/**
	 * Sets the loading start timestap to the load and its assignment
	 * Called when a start load event comes from an external source
	 * <br/>
	 * Mandatory fields:
	 * <ul>
	 * <li>load</li>
	 * </ul>
	 * In case of bag orders, the orderItem.loadedQuantity must be set before calling this method.
	 * In case of bulk orders, the orderItem.loadedQuantity will be set by this method
	 * @param validateComposition set to true if the truck/trailer composition needs to be
	 *  validated. For e.g. createCompletedCustomerOrder requests, this should be set to false
	 *  because after the fact, no more validation is required.
	 * @param lock A lock for the configurable sequence counter entities required in this endLoad.
	 *  In the future, this must not be null. Currently though, it being null means that the caller
	 *  has not been refactored for locking on the required configurable sequence counter entities.
	 * @return true if the endload is accepted, false if the load was aborted (weight=0)
	 */
	public boolean endLoadAndSendGoodsMovements(BizeventEntity event, LoadEntity load, LoadingpointEntity loadingpoint,
			BigDecimal tareWeightT, BigDecimal grossWeightT, BigDecimal netQuantity, boolean isAccurate,
			boolean validateComposition, EventSource source, ConfSeqLock lock) {
		return endLoad(event, load, loadingpoint, tareWeightT, grossWeightT, netQuantity, isAccurate,
				validateComposition, SEND_GOODS_MOVEMENTS, source, lock);
	}

	public boolean 	endLoad(BizeventEntity event, LoadEntity load, LoadingpointEntity loadingpoint,
			BigDecimal tareWeightT, BigDecimal grossWeightT, BigDecimal netQuantity, boolean isAccurate,
			boolean validateComposition, boolean isGoodsMovementSendingRequired, EventSource source, ConfSeqLock lock) {
		AssignmentEntity assignment = event.getAssignment();

		if ((assignment != null) && !AssignmentUtils.isInternal(assignment)) {
			for (AssignmentPartEntity ap : assignment.getAssignmentParts()) {
				if ((ap.getExtOrder() != null) && (ap.getExtOrder().getLoadedQuantity() == null)) {
					ap.getExtOrder().setLoadedAndDeliveredUom(
							referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS));
					ap.getExtOrder().setLoadedQuantity(netQuantity);
					break;
				}
			}
			setShipping(assignment, event.getEventTs());

			return true;
		}
		else if (load == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		if (load.getSapMaterial() != null && LogonUtil.isAggPlanningMaterial(load.getSapMaterial())) {
			LOG.error("agg planning material cannot be loaded");
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		List<OrderItemEntity> orderItems = getOrderItems(load);

		Boolean isReverseDirectedOrder = false;
		boolean isLandfillOrder = false;
		boolean isReturnOrderPallet = false;
		OrderEntity firstOrder = null;
		boolean isLdsOfflineMode = load.getLdsOfflineMode();
		if (orderItems != null && orderItems.size() > 0) {
			firstOrder = orderItems.get(0).getOrder();
			isLandfillOrder = LogonUtil.isLandfill(firstOrder);
			isReturnOrderPallet = LogonUtil.isReturnDeliveredOrder(firstOrder);
			isReverseDirectedOrder = LogonUtil.isOrderWithReversedDirection(firstOrder);
		}
		boolean loadingAtCustomerSite = isReverseDirectedOrder && firstOrder != null
				&& LogonStatusUtils.isBeforeArrivedAtUnloadingPoint(firstOrder.getOrderStatus())
				&& source == EventSource.MOP;

		if (load.getSapMaterial() != null && LogonUtil.isLandfillPlanningMaterial(load.getSapMaterial())
				&& !loadingAtCustomerSite) {
			LOG.error("landfill planning material cannot be measured at crh site");
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		AssignmentPartEntity loadAp = null;
		int unloadOrderItemCount = 0;
		boolean allLoadsLoaded = true;

		/*
		 * Check the composition against the material package. At this point the composition has to be whole to carry the material
		 */
		PackageTypeBsEntity logonPackageType = load.getSapMaterial().getMaterialPackage().getLogonPackageType();
		TruckEntity truck = event.getTruck();
		if (validateComposition && truck != null && logonPackageType != null) {
			TrailerEntity trailer = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), false);
			ContainerEntity container = truckAdminService.fetchContainerForTruckTrailerComposition(truck, trailer);

			boolean validComposition = LogonUtil.isTruckCompositionValidForMaterialPackage(
					logonPackageType.getPackageTypeCd(), truck, trailer, container);
			if (!validComposition) {
				throw new LogonException("Invalid truck composition", Reason.RES_ERROR_INVALID_TRUCK_COMPOSITION);
			}
		}

		if (assignment != null) {
			for (AssignmentPartEntity ap : assignment.getAssignmentParts()) {
				if (AssignmentUtils.isLoadPart(ap)) {
					if (ap.getLoad().getLoadId().equals(load.getLoadId())) {
						loadAp = ap;
					}
					else if ((ap.getLoad().getLoadingTs() == null) || (ap.getLoad().getNetQuantity() == null)) {
						allLoadsLoaded = false;
					}
				}
				else if (AssignmentUtils.isUnloadPart(ap)) {
					List<OrderItemEntity> materialItems = LogonPackageHandlingUtil.getMaterialItems(ap.getOrder());
					unloadOrderItemCount += materialItems.size();
				}
			}

			if (loadAp == null) {
				throw new LogonException("No single assignment part found for load " + load.getLoadId(),
						Reason.RES_ORDER_PROCESSING_ERROR);
			}
		}

		OrderItemEntity bulkOrderItem = LogonUtil.getBulkDumpOrPowderOrderItem(orderItems);

		if (((source == EventSource.LDS) && (load.getTareWeightT() != null))
				&& (!((load.getNetQuantity() == null) && (load.getContainer() != null) && (load.getWagon() != null)))) {
			// In case it is coming from an lds, do not let overwrite the tare quantity, but keep the original see ticket 44356
			// However, this is not the case if the event comes in case of loading multiple containers for a wagon, in that case we do want to update the load with the measured tare from endLoad.
			tareWeightT = load.getTareWeightT();
		}
		if (loadingAtCustomerSite || isLdsOfflineMode) {
			if (truck == null) {
				LOG.error("truck shouldn't be null if call is coming from mop");
				throw new LogonException("truck shouldn't be null if call is coming from mop", Reason.RES_ERROR_IMPL);
			}
			tareWeightT = truck.getTareWeightT();
			if (isLandfillOrder || isLdsOfflineMode) {
				grossWeightT = truck.getMaxGrossWeightT();
				UomBsEntity uomTons = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
				load.setNetQuantityUom(uomTons);
				isAccurate = false;
			}
			else if (isReturnOrderPallet) {
				UomBsEntity uomPieces = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_PCE);
				load.setNetQuantityUom(uomPieces);
				isAccurate = true;
			}
		}

		BizEventUtil.processCoordinates(event);

		if (load.getLoadedFromLoad() != null) {
			LoadEntity loadedFrom = load.getLoadedFromLoad();
			// If a container is loaded, there are 2 loads involved: the
			// container's load that delivered it to the container terminal
			// and the load of the current assignment. In that case, the
			// first load must be set to unloaded (in the container case,
			// this is not done upon delivery).
			if ((load.getContainer() != null) && (loadedFrom.getContainer() != null)
					&& (load.getContainer().getContainerId().equals(loadedFrom.getContainer().getContainerId()))) {
				if (loadedFrom.getUnloadingTs() == null) {
					loadedFrom.setUnloadingTs(event.getEventTs());
				}
			}

			if ((bulkOrderItem != null) && (bulkOrderItem.getBimodalRailOrderItem() != null)) {
				if (getWagonRemainingQuantityT(loadedFrom).compareTo(netQuantity) < 0) {

					throw new LogonException(Reason.RES_ERROR_INVALID_MEASUREMENT);
				}
			}
		}

		boolean resendTourToPda = false;
		LoadingpointEntity loadingpointOfLoad = load.getLoadingpoint();
		// The LP was not known, but now it is, or it changed the order data will be re-sent to PDA
		if ((loadingpoint != null)
				&& ((loadingpointOfLoad == null) || !loadingpoint.getLoadingpointId().equals(
						loadingpointOfLoad.getLoadingpointId()))) {
			load.setLoadingpoint(loadingpoint);
			resendTourToPda = true;
		}

		updateMeasureData(load, tareWeightT, grossWeightT, netQuantity, event.getEventTs(), isAccurate);
		// set the alternative sequnce numbers for special (cash sales, export incoice etc... orders)
		updateCustomSequenceNumbers(load, lock);

		if (isGoodsMovementSendingRequired) {
			createAndSaveGoodsMovementEntity(load);
		}

		event.setDescription(((load.getGrossWeightT() != null) ? load.getGrossWeightT().toPlainString() : "") + " "
				+ Constant.UOM_TONS_TXT + (isLdsOfflineMode ? " offline" : ""));

		/*
		 * The weighing system can send a tare quantity higher than the one stored in the DB.
		 * This is only possible if the difference is the returned quantity from a previous
		 * order, which is still loaded on the truck.
		 *
		 * => Check if we have returned quantity, and adjust the net/gross quantity accordingly.
		 */

		Boolean isLoadAttachedToShipOrBlocktrainPickup = load.getSimpleVehicle() != null
				&& (load.getSimpleVehicle().getSimpleVehicleId().equals(Constant.SIMPLE_VEHICLE_BLOCKTRAIN_PICKUP_CD) || load.getSimpleVehicle().getSimpleVehicleId().equals(
						Constant.SIMPLE_VEHICLE_SHIP_PICKUP_CD));

		if (LogonPackageHandlingUtil.isBulk(load) && !isLoadAttachedToShipOrBlocktrainPickup) {
			Filter<OrderEntity> oldOrderFilter = new FilterImpl<OrderEntity>();
			BigDecimal returnQuantity = null;
			BigDecimal dbTare = null;
			WagonEntity wagon = event.getWagon();
			if ((wagon == null) && (loadAp != null)) {
				wagon = loadAp.getAssignment().getWagon();
			}
			if ((truck == null) && (loadAp != null)) {
				truck = loadAp.getAssignment().getTruck();
			}
			if (truck != null) {
				if (truck.getTruckTransportCat().getTruckTransportCatCd().longValue() == Constant.TKATEGORIE_SILO) {
					dbTare = truck.getTareWeightT();
				}
				else if (truck.getTruckTransportCat().getTruckTransportCatCd().longValue() == Constant.TKATEGORIE_TRAILER) {
					TrailerEntity trailer = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), false);
					if ((trailer != null)
							&& (trailer.getTrailerTrspCat().getTrailerTrspCatCd().longValue() == Constant.TRAILER_TKATEGORIE_BULK_POWDER)) {
						dbTare = truck.getTareWeightT().add(trailer.getTareWeightT());
					}
				}
				if (dbTare != null) {
					BigDecimal rc = load.getTareWeightT().subtract(dbTare);
					if (rc.compareTo(new BigDecimal(getConfigBean().getAppDefaultWeighingTruckLimitCreditNoteT())) > 0) {
						returnQuantity = rc;
						oldOrderFilter.addExpression(Restrictions.eq(OrderEntity.ATTR_DELIVERED_TRUCK_ID,
								truck.getTruckId()));
					}
				}
			}
			else if (wagon != null) {
				if (wagon.getWagonType().getWagonTrspCat().getWagonTrspCatCd().longValue() == Constant.WAGON_TKATEGORIE_SILO) {
					BigDecimal limit = wagon.getLimitCreditNoteT();
					if (limit == null) {
						limit = wagon.getWagonType().getLimitCreditNoteT();
					}
					if (limit == null) {
						limit = new BigDecimal(getConfigBean().getAppDefaultWeighingWagonLimitCreditNoteT());
					}
					dbTare = wagon.getTareWeightT();
					BigDecimal rc = load.getTareWeightT().subtract(dbTare);

					if (rc.compareTo(limit) > 0) {
						returnQuantity = rc;
						oldOrderFilter.addExpression(Restrictions.eq(OrderEntity.ATTR_DELIVERED_WAGON_ID,
								wagon.getWagonId()));
					}
				}
			}
			if (returnQuantity != null) {
				/*
				 * modify load's tare weight to the master data tare
				 *
				 * (1) load net quantity = WS net quantity + returned quantity
				 * (2) load tare weight = WS tare weight - returned quantity
				 *
				 * This part will not be reached for the case of a "cancel loading" request
				 * ("rc" will be negative => no return quantity).
				 *
				 * TODO 19-Nov-2012/romek: "cancel loading" request does not call "endLoad"
				 * anymore, so consider removing the comment above.
				 *
				 * a goods movement document may have to be sent here, but no action for now,
				 * since goods movement CR is ongoing (November 2012).
				 */
				UomBsEntity uomTons = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);

				final UomConversionResult ucr;
				try {
					ucr = unitOfMeasurementConverterService.convert(load.getNetQuantity(), load.getNetQuantityUom(),
							uomTons, load.getSapMaterial(), null, null, false);
				}
				catch (LogonConversionException e) {
					final String msg = "Could not convert net quantity UOM " + load.getNetQuantityUom()
							+ " to tons for material " + load.getSapMaterial() + "(" + e.getMessage() + ")";
					LOG.error(msg);
					throw new LogonException(msg, e);

				}
				try {
					load.setNetQuantity(unitOfMeasurementConverterService.convert(
							ucr.getQuantity().add(load.getTareWeightT().subtract(dbTare)), ucr.getNumerator(),
							ucr.getDenominator(), false));
				}
				catch (LogonConversionException e) {
					final String msg = "There was a problem converting "
							+ "back from tons using the conversion result " + ucr + "(" + e.getMessage() + ")";
					LOG.error(msg);
					throw new LogonException(msg, e);
				}

				load.setTareWeightT(dbTare);

				// find previous load
				List<Long> currentOrders = new ArrayList<Long>();
				for (OrderItemEntity orderItem : orderItems) {
					if (!currentOrders.contains(orderItem.getOrder().getOrderId())) {
						currentOrders.add(orderItem.getOrder().getOrderId());
					}
				}
				if (!currentOrders.isEmpty()) {
					oldOrderFilter.addExpression(Restrictions.not(Restrictions.in(OrderEntity.ATTR_ORDER_ID,
							currentOrders)));
				}
				String[] retTypes = { Constant.DISTRIBUTION_DEST_INBOUND, Constant.DISTRIBUTION_DEST_INTERUNIT_DIST,
						Constant.DISTRIBUTION_DEST_INTERUNIT_PROD, Constant.DISTRIBUTION_DEST_OUTBOUND };
				oldOrderFilter.addExpression(Restrictions.in(OrderEntity.ATTR_DISTRIBUTION_DEST_CD,
						Arrays.asList(retTypes)));
				oldOrderFilter.addExpression(Restrictions.isNotNull(OrderEntity.ATTR_ACTUAL_DELIVERY_DAT));
				oldOrderFilter.addExpression(Order.desc(OrderEntity.ATTR_ACTUAL_DELIVERY_DAT));
				// FIXME Nov 9, 2015/gabor: you cannot move the 'dao' declaration/definition line
				// to the beginning where the filter is created because then the unit tests fail.
				// Now was the test written so that this block is not reached or has it simply
				// worked so far by accident?
				ReadWriteDao<OrderEntity> orderEntityDao = getDaoProvider().getOrderEntityDao();
				List<OrderEntity> oldOrders = orderEntityDao.find(oldOrderFilter);
				if (oldOrders.size() > 0) {
					OrderEntity oldOrder = (OrderEntity) oldOrders.get(0);
					List<OrderItemEntity> oldorderItems = LogonPackageHandlingUtil.getMaterialItems(oldOrder);
					for (OrderItemEntity item : oldorderItems) {
						if ((item.getReturnedQuantity() == null)
								|| (item.getReturnedQuantity().equals(BigDecimal.ZERO))) {
							item.setReturnedQuantity(returnQuantity);
							item.setReturnQuantHandling(referentialDataService.findBsEntity(
									ReturnQuantHandlingBsEntity.class,
									Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_NEEDED));
							item.setReturnedUom(referentialDataService.findBsEntity(UomBsEntity.class,
									Constant.UOM_TONS));
							item.setReturnOrderReason(referentialDataService.findBsEntity(
									ReturnOrderReasonBsEntity.class,
									Constant.RETURN_ORDER_REASON_RAIL_UNLOADING_PROBLEM));
						}
					}
				}
			}
		}

		Set<OrderEntity> orders = new HashSet<OrderEntity>();
		for (OrderItemEntity orderItem : orderItems) {
			// We need to execute this for both bulk and bag loads.
			updateOrderItemLoadedFields(orderItem);

			OrderEntity order = orderItem.getOrder();
			if (!orders.contains(order)) {
				orders.add(order);
			}
		}

		/*
		 *  Only paper-based delivery note to be generated and sent out per post already on event
		 *  endLoad (except for pickup).
		 *  This exists only for backwards compatibility. New methods must not use endLoad with
		 *  lock == null and take care of PDF generation themselves.
		 */
		if (lock == null) {
			generateAndSendPDF(isReverseDirectedOrder, orders);
		}

		if (loadAp != null) {
			// Set shipping, this must be done after updateMeasureData, as updateMeasureData might set the status to 'loaded'
			// for truck events only
			AssignmentEntity loadedAssignment = loadAp.getAssignment();
			SimpleVehicleEntity loadedSimpleVehicle = loadedAssignment.getSimpleVehicle();
			TruckEntity loadedTruck = loadedAssignment.getTruck();
			Boolean isVehicleAShipOrBlocktrainPickup = loadedSimpleVehicle != null
					&& (loadedSimpleVehicle.getSimpleVehicleId() == Constant.SIMPLE_VEHICLE_BLOCKTRAIN_PICKUP_CD || loadedSimpleVehicle.getSimpleVehicleId() == Constant.SIMPLE_VEHICLE_SHIP_PICKUP_CD);

			if (loadedTruck != null || isVehicleAShipOrBlocktrainPickup) {
				if (allLoadsLoaded) {

					// Looking for an order, which is bulk and belongs to a custorder
					// There can only exist exactly one order per load
					// TODO (pascal Oct 15, 2012): activate when ready
					// Missing: Creation of SDOs for Performance Split recalc
					if (orders.size() == 1) {
						OrderEntity bulkOrder = orders.iterator().next();
						// If for this one order, a custorder exists,
						CustorderEntity custorder = bulkOrder.getCustorder();

						// Only do the recalculation in case the exact quantity flag is on.
						if (custorder != null
								&& ((CustorderItemEntity) custorder.getCustorderItems().get(0)).getExactCustorderQtyFlag()) {
							// Do the split recalc
							customerOrderService.splitBulkCustorderEntity(custorder);

							// DB probs?
							customerOrderService.mergeCustomerOrder(custorder);
							if (LOG.isDebugEnabled()) {
								LOG.debug("Custorder Recalc proceeded!");
							}
						}
					}

					setShipping(loadedAssignment, event.getEventTs());

					if (unloadOrderItemCount > 1) {
						// PDA cannot distuingish which laod.loadedQuantity
						// belongs to which unloadOrderItem in case of multiple
						// unloadOrderItems, resend the tour with this info.
						resendTourToPda = true;
					}
				}

				if (source != EventSource.LDS && firstOrder != null && assignment != null && !(loadingAtCustomerSite || isLdsOfflineMode)) {

					// Cancel the load order on the weighing system, since it was actually loaded by Logon or PDA.
					cancelOnWeighSystem(assignment, firstOrder.getShippingpoint());
				}

				// Send update to PDA
				if ((resendTourToPda) && (loadedTruck != null)) {
					asyncMopEventOutService.sendTourToPDA(loadedTruck, loadedAssignment);
				}

				Boolean isTruckPdaAvailable = (loadedTruck != null && loadedTruck.getPdaAvailableFlag());
				final boolean isITrackAssignment = AssignmentUtils.isITrackAssignment(loadedAssignment);
				firstOrder.setITrackAvailableFlag(isITrackAssignment);

				if ((allLoadsLoaded)) {
					// close the order in case there is no PDA on the truck, or the order is flagged NOT to send to the PDA
					if ((!isTruckPdaAvailable && !isITrackAssignment) || firstOrder.getNoPdaOrRailEventsFlag()) {
						closeAfterLoad(loadedAssignment, event.getEventTs());
						detachCurrentTruckAndTrailerFromContainer(event.getContainer());
						List<AssignmentPartEntity> assignmentPartList = loadedAssignment.getAssignmentParts();
						for (AssignmentPartEntity assignmentPart : assignmentPartList) {
							AssignPartStatusBsEntity assignmentPartStatus = new AssignPartStatusBsEntity();
							assignmentPartStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_TRANSMITTED_ACK);
							assignmentPart.setAssignPartStatus(assignmentPartStatus);
						}
					}
				}

				// Notify PDA
				if ((source != EventSource.MOP) && (loadAp.getAssignment().getTruck() != null)) {
					LOG.info("Sending LOADING ENDED message to PDA related to load" + load.getLoadId());

					SapMaterialEntity sapMaterial = load.getSapMaterial();
					UomBsEntity netQuantityUom = load.getNetQuantityUom();

					asyncMopEventOutService.sendLoadingEnded(loadAp.getAssignment().getTruck(), loadAp, tareWeightT,
							grossWeightT, netQuantity, netQuantityUom, sapMaterial);
				}
			}
			else {
				AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
				partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_TRANSMITTED_ACK);
				loadAp.setAssignPartStatus(partStatus);
			}
		}

		if (firstOrder != null) {
			Long deliveryTypeCd = firstOrder.getDeliveryType().getDeliveryTypeCd();
			if (LogonUtil.isPickupOrder(deliveryTypeCd) || LogonUtil.isShipPickupOrder(deliveryTypeCd)
					|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd)) {
				firstOrder.setActualDeliveryDat(new Date());
			}
		}

		return true;
	}

	/**
	 * @param isReverseDirectedOrder
	 * @param orders
	 */
	private void generateAndSendPDF(Boolean isReverseDirectedOrder, Set<OrderEntity> orders) {
		for (OrderEntity order : orders) {
			OrderStatusBsEntity orderStatus = order.getOrderStatus();
			long orderStatusCd = orderStatus.getOrderStatusCd();

			if ((!isReverseDirectedOrder && orderStatusCd == Constant.ORDER_STATUS_LOADED)
					|| (isReverseDirectedOrder && orderStatusCd == Constant.ORDER_STATUS_DELIVERED)) {
				DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
				Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
				if (LogonUtil.isPickupOrder(deliveryTypeCd) || LogonUtil.isShipPickupOrder(deliveryTypeCd)
						|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd)) {
					// In pickup case, the order is set to delivered right away (non-PDA), so this DN is the final DN.
					generateAndSendPDFWithoutExplicitLoadingpoint(order, true, true, true);
				}
				else {
					generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false, true);
				}
			}
		}
	}

	private void closeAfterLoad(AssignmentEntity assignment, Date fallbackRealStartForLoadPart) {
		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_FINISHED);
		List<AssignmentPartEntity> assignmentParts = getAssignmentPartsInSeqOrder(assignment.getAssignmentId());

		AssignmentPartEntity ape = null;
		if (!assignmentParts.isEmpty()) {
			ape = assignmentParts.get(0);
		}

		for (AssignmentPartEntity assignmentPart : assignmentParts) {
			AssignmentPartEntity prevApe = ape;
			ape = assignmentPart;

			if (ape.getRealStart() == null && !AssignmentUtils.isPreLoadPart(ape)) {
				Date prevPartRealStart = prevApe.getRealStart();
				Date assignmentPartPlannedStart = ape.getPlannedStart();
				Date prevPartPlannedStart = prevApe.getPlannedStart();

				if (prevPartRealStart == null || assignmentPartPlannedStart == null || prevPartPlannedStart == null) {
					if (AssignmentUtils.isLoadPart(ape)) {
						ape.setRealStart(fallbackRealStartForLoadPart);
					}
				}
				else {
					long prevPartRealStartTime = prevPartRealStart.getTime();
					long assignmentPartPlannedStartTime = assignmentPartPlannedStart.getTime();
					long prevPartPlannedStartTime = prevPartPlannedStart.getTime();

					ape.setRealStart(new Date(prevPartRealStartTime + assignmentPartPlannedStartTime
							- prevPartPlannedStartTime));
				}
			}

			if (AssignmentUtils.isUnloadPart(ape)) {
				OrderEntity order = ape.getOrder();
				if (order != null) {
					changeOrderStatus(order, Constant.ORDER_STATUS_DELIVERED);
					order.setNoPdaOrRailEventsFlag(true);
					order.setCarrierTruckDriverNote(assignment.getCarrierTruckDriverNote());
					order.setCarrierDispatchingNote(assignment.getCarrierDispatchingNote());
					if (order.getDeliveryFromDat().after(ape.getRealStart())) {
						order.setActualDeliveryDat(order.getDeliveryFromDat());
					}
					else {
						order.setActualDeliveryDat(ape.getRealStart());
					}
					copyVehicleDataToOrder(assignment, order);
					copyLoadToOrderItems(order);
					updateServiceItemDeliveredFields(order, assignment);
					List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(ape.getOrder());
					for (OrderItemEntity oie : orderItems) {
						if (oie.getLoad() == null) {
							continue;
						}
						ShiptoEntity st = dispoService.fetchShipto(order.getShiptoSapBp().getSapBpId());
						if (st.getShiptoType().getShiptoTypeCd().longValue() == Constant.SHIPTO_TYPE_CONTAINER_TERMINAL) {
							if (assignment.getTruck() != null) {
								// Make this check just to be sure, rail orders ariving at container terminal should never call this
								// as they are set to state delivered directly at arrivan (with setAtShipto)
								//in case of truck container terminal, unloading_ts should NOT be set, as far this is the requirement in order to that the container is availble for loading
								//unloading_ts will be set when another load copies over the load values from this load
								//at that point that other load will set the loaded_from_load reference to this load, and set the unloading_ts of this one as well
								setAtContainerTerminal(assignment.getTruck(), new Date(), oie.getLoad(),
										st.getMatChainShippingpoint());
							}
						}
						else {
							oie.getLoad().setUnloadingTs(ape.getRealStart());
						}
					}
				}
			}
		}
	}

	private void copyVehicleDataToOrder(AssignmentEntity assignment, OrderEntity order) {
		order.setDeliveredTruck(assignment.getTruck());
		order.setDeliveredSimpleVehicle(assignment.getSimpleVehicle());
		if (assignment.getTruck() != null) {
			TruckEntity truck = assignment.getTruck();
			Long truckId = truck.getTruckId();

			DriverEntity driver = truckAdminService.fetchDriver(truckId);
			order.setDeliveredDriver(driver);

			TrailerEntity trailer = truckAdminService.fetchTrailerForTruck(truckId, false);
			order.setDeliveredTrailer(trailer);
		}
	}

	/**
	 * Old method calling the extended one with default null as parameter
	 *
	 * Since no explicit loadingpoint is provided (where printing should happen),
	 * the result of printing is not interesting for the callers.
	 */
	public void generateAndSendPDFWithoutExplicitLoadingpoint(OrderEntity order, boolean faxFlag, boolean emailFlag,
			boolean paperFlag) {
		generateAndSendDeliveryNotes(order, faxFlag, emailFlag, paperFlag, NO_LOADINGPOINT);
	}

	/**
	 * Generates and sends Collective and Single Delivery Note for the given order
	 *
	 * In practice, the collective DN should be generated only once, when the last order of customer order is also loaded.
	 * However, this condition is checked every time.
	 *
	 * @param order
	 * @faxFlag true if fax needs to be sent
	 * @emailFlag true if email needs to be sent
	 * @paperFlag true if paper needs to be sent
	 * @loadingpoint used for printer determination if order has no load entity associated (e.g. return pickup)
	 * @return whether all necessary DNs were printed to paper
	 * @throws LogonException, only business exceptions can fly out (technical ones not)
	 *
	 * Notice that this return value has nothing to do with success or validity,
	 * because we have several use cases when paper printing will skipped
	 * due to legal business reasons.
	 *
	 */
	public boolean generateAndSendDeliveryNotes(OrderEntity order, boolean faxFlag, boolean emailFlag,
			boolean paperFlag, LoadingpointEntity loadingPoint) {
		try {
			Long orderId = order.getOrderId();
			PdfGeneratingResult generationWasSuccessful = pdfGeneratorService.generatePDF(orderId);
			if (!generationWasSuccessful.isSuccess()) {
				return false;
			}
			return sendDeliveryNotes(order, faxFlag, emailFlag, paperFlag, loadingPoint);
		}
		catch (LogonException logonException) {
			LOG.error("Business error occurred, rethrowing for the UI", logonException);
			throw logonException;
		}
		catch (Exception ex) {
			LOG.error("Technical error occurred, only logging in order to don't stop other business processes", ex);
			return false;
		}
	}

	private boolean isOneTimeCustomer(boolean csEnableOneTimeCustFlag, String sapContractTypeCd) {
		boolean isGenericContract = Constant.SAP_CONTRACT_TYPE_GENERIC_CONTRACT.equals(sapContractTypeCd);
		return csEnableOneTimeCustFlag && isGenericContract;
	}

	public boolean isCashSales(SapContractEntity contractEntity, SareaEntity sareaEntity) {
		if (sareaEntity == null || contractEntity == null) {
			return false;
		}
		String sapContractTypeCd = contractEntity.getSapContractType().getSapContractTypeCd();
		Long sareaId = sareaEntity.getSareaId();

		boolean isOneTimeCustomer = isOneTimeCustomer(sareaEntity.getCsEnableOneTimeCustFlag(), sapContractTypeCd);
		if (isOneTimeCustomer) {
			return true;
		}

		MapExtraDnTypeBsEntity entity = getMapExtraDN(sapContractTypeCd, sareaId);
		if (entity == null) {
			return false;
		}

		return entity.getPrintCashSales();
	}

	public boolean isExportInvoice(SapContractEntity contractEntity, SareaEntity sareaEntity) {
		if (sareaEntity == null || contractEntity == null) {
			return false;
		}
		String sapContractTypeCd = contractEntity.getSapContractType().getSapContractTypeCd();
		Long sareaId = sareaEntity.getSareaId();
		MapExtraDnTypeBsEntity entity = getMapExtraDN(sapContractTypeCd, sareaId);

		if (entity == null) {
			return false;
		}

		return entity.getPrintExportInvoice();
	}

	public boolean isCashSalesOrExportInvoice(SapContractEntity contractEntity, SareaEntity sareaEntity) {
		if (sareaEntity == null || contractEntity == null) {
			return false;
		}
		String sapContractTypeCd = contractEntity.getSapContractType().getSapContractTypeCd();
		Long sareaId = sareaEntity.getSareaId();

		boolean isOneTimeCustomer = isOneTimeCustomer(sareaEntity.getCsEnableOneTimeCustFlag(), sapContractTypeCd);
		if (isOneTimeCustomer) {
			return true;
		}

		MapExtraDnTypeBsEntity entity = getMapExtraDN(sapContractTypeCd, sareaId);
		if (entity == null) {
			return false;
		}

		return entity.getPrintCashSales() || entity.getPrintExportInvoice();
	}

	/**
	 * This method returns an entity for the given contract and sarea pair.
	 * That entity will tell whether an additional Cash sales invoice or an Export invoice needs to be printed.
	 */
	public MapExtraDnTypeBsEntity getMapExtraDN(String sapContractTypeCd, Long sareaId) {
		Filter<MapExtraDnTypeBsEntity> filter = new FilterImpl<MapExtraDnTypeBsEntity>();
		filter.addExpression(Restrictions.eq(MapExtraDnTypeBsEntity.ATTR_SAP_CONTRACT_TYPE_CD, sapContractTypeCd));
		filter.addExpression(Restrictions.eq(MapExtraDnTypeBsEntity.ATTR_SAREA_ID, sareaId));
		List<MapExtraDnTypeBsEntity> entities = referentialDataService.findBsEntityFiltered(
				MapExtraDnTypeBsEntity.class, filter, 1, 0);
		if (entities.size() > 0) {
			return entities.get(0);
		}
		return null;
	}

	/**
	 * Updates the load measure data.
	 * @return
	 */
	public void resetMeasureData(LoadEntity load, BigDecimal tareQuantity, BigDecimal grossQuantity,
			BigDecimal netQuantity, Date timestamp) {
		updateLoad(load, tareQuantity, netQuantity, grossQuantity);
		Collection<OrderItemEntity> orderItems = getOrderItems(load);
		if (orderItems != null && !orderItems.isEmpty()) {
			for (OrderItemEntity oie : orderItems) {
				updateOrderItemLoadedFields(oie);
				updateOrderItemDeliveredFields(oie);
			}
		}
	}

	protected void updateCustomSequenceNumbers(LoadEntity load, ConfSeqLock lock) {
		if (lock == null) {
			oldUpdateCustomSequenceNumbers(load);
		}

		final ReadWriteDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();

		final Collection<OrderEntity> orders = getOrders(load);
		for (final OrderEntity order : orders) {
			final Long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
			final String sequenceNumber = order.getOrderNumberFromSeqUsage();
			final SapContractEntity contract = order.getSapContract();

			if (orderStatusCd.equals(Constant.ORDER_STATUS_LOADED) && StringUtils.isEmpty(sequenceNumber)
					&& contract != null) {
				final ConfigurableSequenceResult csr = configurableSequencesService.next(contract.getSapContractId(),
						order.getShippingpoint().getShippingpointId(), lock);

				if (csr != null) {
					order.setOrderNumberSeqUsage(csr.getUsage());
					order.setOrderNumberFromSeqUsage(csr.getSequenceNumber());

					// Required because the the goods movement creation triggered in endLoad uses
					// a view which accesses the order number from sequence usage.
					dao.merge(order);
				}
			}
		}
	}

	/**
	 * @param load
	 */
	private void oldUpdateCustomSequenceNumbers(LoadEntity load) {
		Collection<OrderEntity> orders = getOrders(load);
		for (OrderEntity order : orders) {
			if (!order.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_LOADED)) {
				continue;
			}
			// generate the sequence number in special cases
			SapContractEntity sapContract = order.getSapContract();
			String customSequenceNumber = order.getOrderNumberFromSeqUsage();
			if (sapContract == null || !StringUtils.isEmpty(customSequenceNumber)) {
				continue;
			}
			String sapContractTypeCd = sapContract.getSapContractType().getSapContractTypeCd();
			MapExtraDnTypeBsEntity mapExtraDN = getMapExtraDN(sapContractTypeCd, sapContract.getSarea().getSareaId());
			Long confSeqCountersUsageCd = Constant.DEFAULT_SEQ_USAGE_CD;

			// cash sales or export invoice, set custom cash sales or export invoice sequence
			if (mapExtraDN != null && mapExtraDN.getPrintExportInvoice()) {
				confSeqCountersUsageCd = Constant.EXPORT_INVOICE_SEQ_USAGE_CD;
			}
			else if (mapExtraDN != null && mapExtraDN.getPrintCashSales()) {
				confSeqCountersUsageCd = Constant.CASH_SALES_SEQ_USAGE_CD;
			}
			SeqUsageBsEntity seqUsageEntity = getSeqUsageBsEntity(confSeqCountersUsageCd);
			if (seqUsageEntity == null) {
				if (confSeqCountersUsageCd != Constant.DEFAULT_SEQ_USAGE_CD) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("No SEQ_USAGE_CD " + confSeqCountersUsageCd + ", fallback to "
								+ Constant.DEFAULT_SEQ_USAGE_CD);
					}
					confSeqCountersUsageCd = Constant.DEFAULT_SEQ_USAGE_CD;
					seqUsageEntity = getSeqUsageBsEntity(confSeqCountersUsageCd);
				}
				if (seqUsageEntity == null) {
					LOG.error(MessageFormat.format("Cannot find conf seq counter usage entity for cd: {0}",
							confSeqCountersUsageCd));
					return;
				}
			}
			if (order.getShippingpoint() == null) {
				LOG.error(MessageFormat.format(
						"Cannot generate custom sequence number for order: {0}, no shippingpoint defined.",
						order.getOrderNumber()));
				return;
			}
			PlantBsEntity plant = order.getShippingpoint().getPlant();
			ReadWriteDao<PlantCompcodeEntity> plantCompcodeEntityDao = getDaoProvider().getPlantCompcodeEntityDao();
			Filter<PlantCompcodeEntity> filter = new FilterImpl<PlantCompcodeEntity>();
			filter.addExpression(Restrictions.eq(PlantCompcodeEntity.ATTR_PLANT, plant));
			PlantCompcodeEntity plantCompcodeEntity = plantCompcodeEntityDao.findSingle(filter);
			if (plantCompcodeEntity == null) {
				LOG.error(MessageFormat.format(
						"Cannot generate custom sequence number for order: {0}, no PlantCompcodeEntity found for plant: {1}.",
						order.getOrderNumber(), plant.getPlantCd()));
				return;
			}

			customSequenceNumber = configurableSequencesService.next(
					plantCompcodeEntity.getCompanycode().getCompanycodeCd(), plant.getPlantCd(),
					seqUsageEntity.getSeqUsageCd());

			if (customSequenceNumber == null && confSeqCountersUsageCd != Constant.DEFAULT_SEQ_USAGE_CD) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("No TLGNC_CONF_SEQ_COUNTER " + confSeqCountersUsageCd + ", fallback to "
							+ Constant.DEFAULT_SEQ_USAGE_CD);
				}
				confSeqCountersUsageCd = Constant.DEFAULT_SEQ_USAGE_CD;
				seqUsageEntity = getSeqUsageBsEntity(confSeqCountersUsageCd);
				if (seqUsageEntity == null) {
					LOG.error(MessageFormat.format("Cannot find conf seq counter usage entity for cd: {0}",
							confSeqCountersUsageCd));
					return;
				}
				customSequenceNumber = configurableSequencesService.next(
						plantCompcodeEntity.getCompanycode().getCompanycodeCd(), plant.getPlantCd(),
						seqUsageEntity.getSeqUsageCd());
			}

			order.setOrderNumberSeqUsage(seqUsageEntity);
			order.setOrderNumberFromSeqUsage(customSequenceNumber);

			// Required because the the goods movement creation triggered in endLoad uses
			// a view which accesses the order number from sequence usage.
			ReadWriteDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();
			dao.merge(order);
		}
	}

	private SeqUsageBsEntity getSeqUsageBsEntity(Long confSeqCountersUsageCd) {
		Filter<SeqUsageBsEntity> seqUsageEntityfilter = new FilterImpl<SeqUsageBsEntity>();
		seqUsageEntityfilter.addExpression(Restrictions.eq(SeqUsageBsEntity.ATTR_SEQ_USAGE_CD, confSeqCountersUsageCd));
		SeqUsageBsEntity seqUsageEntity = RefDataUtil.findSeqUsageBsEntity(em, seqUsageEntityfilter);
		return seqUsageEntity;
	}

	/**
	 * Simply checks the loaded UOM for the items, they must match the load's uom.
	 * Merges the changes.
	 * @return
	 */
	public void checkLoadedUomMergeLoad(@SuppressWarnings("rawtypes") Collection orderItems) {
		if (orderItems == null || orderItems.isEmpty()) {
			return;
		}
		LoadEntity load = null;
		UomBsEntity uom = null;
		for (Iterator<?> iterator = orderItems.iterator(); iterator.hasNext();) {
			OrderItemEntity oie = (OrderItemEntity) iterator.next();
			if (load != null && !oie.getLoad().equals(load)) {
				//it must be the same load by the way for ALL
				throw new LogonException();
			}
			load = oie.getLoad();
			if (oie.getLoadedQuantity() != null) {
				if (uom != null && !oie.getLoadedUom().getUomCd().equals(uom.getUomCd())) {
					//it must be the same uom by the way for ALL
					throw new LogonException();
				}
				uom = oie.getLoadedUom();
				getDaoProvider().getOrderItemEntityDao().merge(oie);
			}
		}
		load.setNetQuantityUom(uom);
		getDaoProvider().getLoadEntityDao().merge(load);
	}

	/**
	 * Updates the netto measure field of the given TLGNC_LOAD entry, changes the status to ARRIVED AT LOADINGPOINT or
	 * LOADED depending on the data provided.
	 * The real start of assignment parts TO are going to be set in case the status is set to loaded.
	 * @param load
	 * @param tareWeightT the weight of the transport unit before loading
	 * @param grossWeightT calculated from the taraMeasureT and netMeasure
	 * @param netQuantity Alternatively, instead of taraMeasure & grossMeasure. TaraMeasure will be taken from
	 * 						Truck defaults, Gross weight will be calculated as tara + net weight.
	 * @param timestamp ts to be set as loading ts, and real start of assignment part to
	 * @param isAccurate TODO 14-Nov-2012/romek: Write description here if needed.
	 */
	private void updateMeasureData(LoadEntity load, BigDecimal tareWeightT, BigDecimal grossWeightT,
			BigDecimal netQuantity, Date timestamp, boolean isAccurate) {
		if (load == null) {
			///no entry was present in the load table for any reason, which should not be like that after the dispatching
			LOG.error("No load entry was present");
			throw new LogonException("auftragbearbeitung.errors.savefailed", Reason.RES_ORDER_PROCESSING_ERROR);
		}

		load.setAccurateQuantitiesFlag(isAccurate);

		// TODO 16-Nov-2012/romek: Do we still need this?
		if (grossWeightT != null && tareWeightT != null
				&& (netQuantity == null || (netQuantity.compareTo(BigDecimal.ZERO) <= 0))
				&& (grossWeightT.compareTo(tareWeightT) > 0)
				&& load.getNetQuantityUom().getUomCd().equals(Constant.UOM_TONS)) {
			//in case gross was set, calculate the netto
			netQuantity = grossWeightT.subtract(tareWeightT);
		}

		TrailerEntity trailer = load.getTrailer();
		ContainerEntity container = load.getContainer();
		SimpleVehicleEntity vehicle = load.getSimpleVehicle();

		if ((tareWeightT == null || tareWeightT.longValue() <= 0) && (grossWeightT != null || netQuantity != null)) {
			//in case tara measure is left but brutto is not, fill  it in with tara lkw
			//at this point the trailer and container composition is already set to the load, no need to check the current composition
			TruckEntity truck = load.getTruck();
			WagonEntity wagon = load.getWagon();
			tareWeightT = LogonUtil.sumTareWeights(trailer, container, truck, wagon, vehicle);
		}

		Collection<OrderEntity> orders = getOrders(load);
		for (OrderEntity order : orders) {
			//UPDATE ALL RELATED ORDERS STATUS DEPENDING ON THE FILLED IN WEIGHTS

			/* cosmin (18.11.2009): this is wrong (or at least not right). This status change is handled by startLoad
			//the netQuantity and the tareQuantity is editable until the unloading_ts of tlgnc_load is set,
			//but the order status changes do not have to happen in case the fields were already filled in once
			if (tareQuantity != null){
				changeOrderStatus(order, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
			}
			*/
			//the netQuantity and the tareQuantity is editable until the unloading_ts of tlgnc_load is not set,
			//but the order status changes do not have to happen in case the fields were already filled in once
			//TODO (cosmin 18.11.2009): This should probably be moved to endLoad
			if (netQuantity != null) {
				//update only if all items have been loaded already
				Long count = countNotLoadedItems(load, order, true);
				if (count == 0) {
					if (LogonStatusUtils.isBeforeLoaded(order.getOrderStatus())) {
						changeOrderStatus(order, Constant.ORDER_STATUS_LOADED);
					}
				}
			}
		}

		AssignmentPartEntity ap = getAssignmentPartLoad(load);
		if (ap != null) {
			if (tareWeightT != null && load.getTareWeightT() == null) {
				if (ap.getRealStart() == null) {
					ap.setRealStart(timestamp);
				}
				// FIXME (cosmin Oct 2, 2009):implement this for wagon
				if (load.getTruck() != null) {
					List<AssignmentPartEntity> unloadedEntries = getUnloadedAssignmnentPartsForTruck(
							load.getTruck().getTruckId(), ap.getAssignment().getAssignmentId());
					updateUnloadedLoads(unloadedEntries);
				}
			}
		}

		if (netQuantity != null && load.getNetQuantity() == null) {
			load.setLoadingTs(timestamp);
		}

		updateLoad(load, tareWeightT, netQuantity, grossWeightT);
	}

	/**
	 * @param load
	 * @param tareQuantity
	 * @param netQuantity
	 */
	private void updateLoad(LoadEntity load, BigDecimal tareWeightT, BigDecimal netQuantity, BigDecimal grossWeightT) {
		load.setNetQuantity(netQuantity);
		load.setTareWeightT(tareWeightT);
		updateGrossWeightT(load, grossWeightT);
	}

	/**
	 * LOGON-6762: ignore payer if autopayer flags are ON
	 */
	public void cleanUpPayerIfInvalid(OrderEntity oe) {
		if (OrderUtils.isPayerIgnored(oe.getSapContract())) {
			oe.setPayerSapBp(null);
		}
	}

	/**
	 * Updates the unloading timestamp of load entries related to the assignment parts sent in unloadedEntries.
	 * Called when a later assignment is under loading, and it founds that the truck was loaded carrying these load
	 * entries, but when the truck was unloaded, unloading_ts was just missed to be filled in.
	 * @param unloadedAssignmentParts list of the assignment parts, whose loads has to be updated. ONLY with ASSIGNMENT_PART_TYPE_CD
	 * LOAD.
	 */
	private void updateUnloadedLoads(List<AssignmentPartEntity> unloadedAssignmentParts) {
		Date now = new Date();
		if (unloadedAssignmentParts == null) {
			return;
		}
		for (AssignmentPartEntity unloadedPart : unloadedAssignmentParts) {
			LoadEntity load = unloadedPart.getLoad();
			load.setUnloadingTs(now);
		}
	}

	/**
	 * Queries the list of assignment part entries related to other assignments then the parameteres assignmentId,
	 *  where the given truck is present, and the related load has loading_ts, but does not have unloading_ts.
	 * @param truckId
	 * @param loadId ignore the assignment parts with this id
	 * @return
	 */
	public List<AssignmentPartEntity> getUnloadedAssignmnentPartsForTruck(Long truckId, Long assignmentId) {
		if (truckId == null || assignmentId == null
				|| truckId.equals(Long.valueOf(Constant.ARTIFICIAL_PICKUP_BAG_TRUCK_ID))
				|| truckId.equals(Long.valueOf(Constant.ARTIFICIAL_PICKUP_BULK_DMP_TRUCK_ID))
				|| truckId.equals(Long.valueOf(Constant.ARTIFICIAL_PICKUP_BULK_PWDR_TRUCK_ID))) {
			return new ArrayList<AssignmentPartEntity>();
		}
		TruckEntity truck = truckAdminService.fetchTruck(truckId);
		Authorizer.disableNextAccessRestriction();

		String query = "SELECT x FROM " + AssignmentPartEntity.class.getSimpleName() + " x where " + " x."
				+ AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_TRUCK + " = ?1 and " + " x."
				+ AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ASSIGNMENT_ID + " <> ?2 and"
				+ " x." + AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOADING_TS + " is not null and "
				+ " x." + AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_UNLOADING_TS + " is null";
		DirectJpaDao directJpaDao = getDaoProvider().getDirectJpaDao();
		@SuppressWarnings("unchecked")
		List<AssignmentPartEntity> list = (List<AssignmentPartEntity>) directJpaDao.find(query, truck, assignmentId);
		return list;
	}

	private void checkRoute(OrderEntity order) {
		if (order.getOrderCreationType() != null
		 && orderTakingWithoutRouteInformation.contains(order.getOrderCreationType().getOrderCreationTypeCd())
				|| order.getTransportDurPlanGoogleMin() == null) {
			LOG.debug("Transport duration and/or distance was not provided.");
			return;
		}
		ReadWriteDao<ShiptoEntity> shiptoEntityDao = getDaoProvider().getShiptoEntityDao();
		Filter<ShiptoEntity> f = shiptoEntityDao.createFilter();
		f.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, order.getShiptoSapBp().getSapBpId()));
		Authorizer.disableNextAccessRestriction();
		ShiptoEntity shipto = shiptoEntityDao.findSingle(f);
		SapTrspModeBsEntity sapTrspMode = order.getDeliveryType() != null ? order.getDeliveryType().getSapTrspMode() : null;
		TruckRouteEntity route = findTruckRoute(shipto, order.getShippingpoint(), sapTrspMode);
		if (route == null) {
			route = new TruckRouteEntity();
			route.setStartShippingpoint(order.getShippingpoint());
			route.setEndShipto(shipto);
			//TODO: BigDecimal ??
			route.setTimeCorrectionFactor(new BigDecimal(getConfigBean().getAppDefaultTruckRouteTimeCorrectionFactor()));
			route.setDistanceCorrectionFactor(BigDecimal.ONE);
			if (sapTrspMode == null) {
				String transportModeCd = transportModeCodeWithDefault(sapTrspMode);
				sapTrspMode = referentialDataService.findBsEntity(SapTrspModeBsEntity.class, transportModeCd);
			}
			route.setSapTrspMode(sapTrspMode);
			getDaoProvider().getTruckRouteEntityDao().persist(route);
		}
		Long uncorrectedDistance = order.getTransportDistanceM();
		if (order.getTransportDistanceM() != null && route.getDistanceCorrectionFactor() != null
				&& route.getDistanceCorrectionFactor().compareTo(BigDecimal.ZERO) != 0) {
			// in the order we previously saved a distance multiplied by the correction factor
			// we have to divide it back before updating the route
			uncorrectedDistance = Math.round(uncorrectedDistance.doubleValue()
					/ route.getDistanceCorrectionFactor().doubleValue());
		}
		route.setDistanceCalculatedM(uncorrectedDistance);
		route.setTimeCalculatedMin(order.getTransportDurPlanGoogleMin());
		route.setLastCalculatedDat(new Date());
	}

	/**
	 * Saves modifications of an existing order
	 * @param OrderdEntity to save
	 */
	public void saveOrder(OrderEntity order) {
		if (order.getOrderId() == null) {
			LOG.error("cant merge order");
			return;
		}
		mergeOrder(order);
		checkRoute(order);
	}

	public void resetCarrierNotes(OrderEntity order) {
		String noteDelivery = "";
		String noteTruckDriver = "";
		String noteDispatching = "";
		if ((order.getNoteDelivery() != null) && (order.getNoteDelivery().length() > 0)) {
			noteDelivery = order.getNoteDelivery();
		}
		if ((order.getNoteTruckDriver() != null) && (order.getNoteTruckDriver().length() > 0)) {
			noteTruckDriver = order.getNoteTruckDriver();
		}
		if ((order.getNoteDispatching() != null) && (order.getNoteDispatching().length() > 0)) {
			noteDispatching = order.getNoteDispatching();
		}
		if (noteDelivery.length() > 250) {
			noteDelivery = noteDelivery.substring(0, 250);
		}
		if (noteTruckDriver.length() > 250) {
			noteTruckDriver = noteTruckDriver.substring(0, 250);
		}
		if (noteDispatching.length() > 250) {
			noteDispatching = noteDispatching.substring(0, 250);
		}
		order.setCarrierDeliveryNote(noteDelivery);
		order.setCarrierTruckDriverNote(noteTruckDriver);
		order.setCarrierDispatchingNote(noteDispatching);
	}

	/**
	 * Calculates the freight cost based on the distance, weight of the items and the rates
	 * defined per sales area, material packaging type, shipping point and shipto country.
	 * The resulting cost, if applicable, will be set on the order object, including the
	 * currency it is stored in.
	 * @param order
	 * @param orderItems
	 */
	private void calculateFreightCost(OrderEntity order, List<OrderItemEntity> orderItems) {
		// only required for road orders
		Long deliveryType = order.getDeliveryType().getDeliveryTypeCd();
		if (Constant.DELIVERY_TYPE_BIMODAL_RAIL == deliveryType || Constant.DELIVERY_TYPE_PICKUP == deliveryType
				|| Constant.DELIVERY_TYPE_SHIP_PICKUP == deliveryType
				|| Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP == deliveryType
				|| Constant.DELIVERY_TYPE_RAIL_DIRECT == deliveryType) {
			return;
		}

		// if any of the following is null, cost cannot be calculated
		if (order.getTransportDistanceM() == null || order.getSarea() == null || order.getShiptoSapBp() == null) {
			return;
		}

		BigDecimal distance = BigDecimal.valueOf(order.getTransportDistanceM() / 1000.0);
		Long sareaId = order.getSarea().getSareaId();
		Long shippingPointId = order.getShippingpoint() != null ? order.getShippingpoint().getShippingpointId() : null;
		String country = order.getShiptoSapBp().getCountry().getSapCountryCd();

		// If the commercial carrier is already set, take its currency as base.
		// Otherwise, the currency of the first applicable rate will be taken as base.
		CurrencyBsEntity currencyBase = null;
		if (order.getCommCarrier() != null) {
			currencyBase = order.getCommCarrier().getFreightRevenueCurrency();
		}

		BigDecimal costPerOrder = BigDecimal.ZERO;

		for (OrderItemEntity orderItem : orderItems) {
			String packageCd = orderItem.getSapMaterial().getMaterialPackage().getMaterialPackageCd();
			UomBsEntity uom = orderItem.getOrderedUom();

			// only applicable to bag or bulk
			if (Constant.MATERIAL_PACKAGE_BAG.equals(packageCd)
					|| Constant.MATERIAL_PACKAGE_BULK_POWDER.equals(packageCd)) {

				ReadOnlyDao<FreightCostEntity> fcDao = getDaoProvider().getFreightCostEntityDao();
				Filter<FreightCostEntity> f = new FilterImpl<FreightCostEntity>();
				// mandatory
				f.addExpression(Restrictions.eq(FreightCostEntity.ATTR_SAREA_ID, sareaId));
				// mandatory
				f.addExpression(Restrictions.eq(FreightCostEntity.ATTR_MATERIAL_PACKAGE_CD, packageCd));
				// optional
				if (shippingPointId != null) {
					f.addExpression(Restrictions.or(Restrictions.isNull(FreightCostEntity.ATTR_SHIPPINGPOINT_ID),
							Restrictions.eq(FreightCostEntity.ATTR_SHIPPINGPOINT_ID, shippingPointId)));
				}
				else {
					f.addExpression(Restrictions.isNull(FreightCostEntity.ATTR_SHIPPINGPOINT_ID));
				}
				// optional
				f.addExpression(Restrictions.or(Restrictions.isNull(FreightCostEntity.ATTR_COUNTRY_CD),
						Restrictions.eq(FreightCostEntity.ATTR_COUNTRY_CD, country)));
				// ordering implements the prioritization of shipping point over country
				f.addExpression(Order.asc(FreightCostEntity.ATTR_DISTANCE_KM));
				f.addExpression(Order.asc(FreightCostEntity.ATTR_SHIPPINGPOINT_ID));
				f.addExpression(Order.asc(FreightCostEntity.ATTR_COUNTRY_CD));
				List<FreightCostEntity> rates = fcDao.findNRecs(0, 0, f);

				if (rates.size() == 0) {
					/*
					LOG.error("No applicable rates have been defined for " +
							"sales area \'" + sareaId + "\' and material package code \'" + packageCd + "\'!");
					*/
					return;
				}

				if (currencyBase == null) {
					currencyBase = rates.get(0).getCurrency();
				}

				BigDecimal costPerTon;
				// handle initial distance-independent cost
				if (rates.get(0).getDistanceKm() == 0) {
					costPerTon = LogonUtil.getCurrencyConvertedValue(rates.get(0).getCurrency(), currencyBase,
							rates.get(0).getRate(), getDaoProvider().getCurrentExchangeRateEntityDao());
					if (costPerTon == null) {
						return;
					}
				}
				else {
					costPerTon = BigDecimal.ZERO;
				}
				// handle distance-dependent cost
				BigDecimal prevDistance = BigDecimal.ZERO;
				for (FreightCostEntity fce : rates) {
					BigDecimal curDistance = BigDecimal.valueOf(fce.getDistanceKm());

					// the ordering above implements the correct prioritization
					// skip rates for a distance already used
					if (curDistance.compareTo(prevDistance) <= 0) {
						continue;
					}

					BigDecimal rate = LogonUtil.getCurrencyConvertedValue(fce.getCurrency(), currencyBase,
							fce.getRate(), getDaoProvider().getCurrentExchangeRateEntityDao());
					if (rate == null) {
						return;
					}

					if (distance.compareTo(curDistance) <= 0) {
						costPerTon = costPerTon.add(rate.multiply(distance.subtract(prevDistance)));
						break;
					}
					costPerTon = costPerTon.add(rate.multiply(curDistance.subtract(prevDistance)));
					prevDistance = curDistance;
				}

				BigDecimal quantity = UOMConverter.getUomConvertedValue(uom, orderItem.getOrderedQuantity(), true);
				if (quantity == null) {
					return;
				}
				BigDecimal costPerItem = quantity.multiply(costPerTon);
				costPerOrder = costPerOrder.add(costPerItem);
			}
		}

		order.setFreightCost(costPerOrder);
		order.setFreightCostCurrency(currencyBase);
	}

	/**
	 * In the rail/bimodal rail container special case container order has to be splitted into separate order items
	 * within the same rail single delivery order. For bimodal road order this rule does not hold. The cause of this
	 * special handling is that there are wagons with 2 container carrying capacity, and so such a rail order can
	 * be dispatched to a single wagon.
	 */
	private void handleSpecialUomSplit(OrderEntity order) {
		List<OrderItemEntity> orderItems = order.getOrderItems();
		OrderItemEntity firstItem = orderItems.get(0);

		int itemMultiplier = 0;
		if (firstItem.getOrderedUom() != null && Constant.UOM_CONTAINER.equals(firstItem.getOrderedUom().getUomCd())
				&& LogonUtil.isRailOrder(order.getDeliveryType().getDeliveryTypeCd())) {
			itemMultiplier = firstItem.getOrderedQuantity().intValue() - 1;
			firstItem.setOrderedQuantity(BigDecimal.valueOf(1));
		}

		try {
			for (int i = 0; i < itemMultiplier; i++) {
				OrderItemEntity firstItemClone = (OrderItemEntity) BeanUtils.cloneBean(firstItem);
				orderItems.add(firstItemClone);
			}

			for (int i = 0; i < orderItems.size(); i++) {
				final OrderItemEntity orderItemEntity = orderItems.get(i);
				orderItemEntity.setOrder(order);
				orderItemEntity.setOrderItemNo((i + 1) * 10L);
			}

		}
		catch (Exception e) {
			throw new IllegalStateException("Unable to save Order", e);
		}
		return;
	}

	/**
	 */
	private OrderEntity createRailReturnOrder(OrderEntity origOrder, WagonEntity wagon, ShiptoEntity origShipto,
			Date date) {
		if ((origShipto == null)
				|| ((origShipto.getRailHoldBackReOrderFlag() != null) && (origShipto.getRailHoldBackReOrderFlag().booleanValue()))
				|| (origOrder.getRailHoldBackReOrderFlag() != null && origOrder.getRailHoldBackReOrderFlag().booleanValue())
				|| (Arrays.asList(Constant.DISTRIBUTION_DEST_RETURN).contains(origOrder.getDistributionDest().getDistributionDestCd()))) {
			LOG.debug("SKIPPING AUTOMATIC RETURN ORDER CREATION FOR WAGON " + wagon.getWagonNo());
			LOG.debug("ORIGSHIPTO railHoldReOrderFlag: "
					+ (origShipto.getRailHoldBackReOrderFlag() != null ? origShipto.getRailHoldBackReOrderFlag() : "null"));
			LOG.debug("ORDER railHoldReOrderFlag: "
					+ (origOrder.getRailHoldBackReOrderFlag() != null ? origOrder.getRailHoldBackReOrderFlag() : "null (orderNumber:  "
							+ origOrder.getOrderNumber() + ")"));
			LOG.debug("SKIPPING AUTOMATIC RETURN ORDER CREATION FOR WAGON " + wagon.getWagonNo());
			return null;
		}

		final ShippingpointEntity railChainShippingpoint = origShipto.getRailChainShippingpoint();

		if (railChainShippingpoint == null) {
			LOG.info("Shipto " + origShipto.getShiptoSapBp().getName1() + "(" + origShipto.getShiptoSapBp().getSapId()
					+ ") has no rail_chain_shippingpoint");
			return null;
		}

		if ((wagon.getLastKnownRailwayStation() == null)
				|| (wagon.getReturnShiptoSapBp() == null)
				|| (wagon.getReturnShiptoSapBp().getRailwayStation() == null)
				|| wagon.getLastKnownRailwayStation().getRailwayStationId().equals(
						wagon.getReturnShiptoSapBp().getRailwayStation().getRailwayStationId())) {
			LOG.info("Rainlway stations undefined or same railway station");
			return null;
		}

		DistributionDestBsEntity dDest = origOrder.getDistributionDest();

		if (dDest.getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_INBOUND)) {
			dDest = referentialDataService.findBsEntity(DistributionDestBsEntity.class,
					Constant.DISTRIBUTION_DEST_INBOUND_RETURN);
		}
		else if (dDest.getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_OUTBOUND)) {
			dDest = referentialDataService.findBsEntity(DistributionDestBsEntity.class,
					Constant.DISTRIBUTION_DEST_OUTBOUND_RETURN);
		}
		else if (dDest.getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_INTERUNIT_DIST)) {
			dDest = referentialDataService.findBsEntity(DistributionDestBsEntity.class,
					Constant.DISTRIBUTION_DEST_INTERUNIT_DIST_RETURN);
		}
		else if (dDest.getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_INTERUNIT_PROD)) {
			dDest = referentialDataService.findBsEntity(DistributionDestBsEntity.class,
					Constant.DISTRIBUTION_DEST_INTERUNIT_PROD_RETURN);
		}
		else if (dDest.getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_REPAIR)) {
			dDest = referentialDataService.findBsEntity(DistributionDestBsEntity.class,
					Constant.DISTRIBUTION_DEST_INTERUNIT_DIST_RETURN);
		}

		final OrderEntity order = new OrderEntity(); // rail return order
		final OrderItemEntity item = new OrderItemEntity();

		order.setArchivingStatus(referentialDataService.findBsEntity(ArchivingStatusBsEntity.class,
				Constant.ARCHIVING_STATUS_NEW));
		/* marius/2012-10-24: The updateOrderItemNumeratorAndDenominator() method called below
		 * requires the shipto sap bp to be set in the order.
		 * The rest of the order's fields are set below. Needed to fix LOGON-5705.
		 */
		order.setShiptoSapBp(wagon.getReturnShiptoSapBp());
		item.setOrder(order);
		ReadWriteDao<SapMaterialEntity> sapMaterialEntityDao = getDaoProvider().getSapMaterialEntityDao();
		Filter<SapMaterialEntity> filter = sapMaterialEntityDao.createFilter();
		String sapMaterialNo = OrderSpecialUtils.getEmptySapMaterialNo(dDest.getDistributionDestCd(), null, wagon);
		filter.addExpression(Restrictions.eq(SapMaterialEntity.ATTR_SAP_MATERIAL_NO, sapMaterialNo));
		Authorizer.disableNextAccessRestriction();
		SapMaterialEntity sapMat = sapMaterialEntityDao.findSingle(filter);
		item.setSapMaterial(sapMat);
		item.setOrderedQuantity(new BigDecimal(1));
		item.setOrderedUom(item.getSapMaterial().getBaseUom());
		updateOrderItemNumeratorAndDenominator(item, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);
		item.setDeliveredQuantity(new BigDecimal(1));
		item.setDeliveredUom(item.getSapMaterial().getBaseUom());
		item.setOrderItemNo(new Long(10));
		if (!LogonPackageHandlingUtil.isService(sapMat)) {
			item.setProductionPlant(origOrder.getShippingpoint().getPlant());
		}
		else {
			PlantBsEntity plant = referentialDataService.findBsEntity(PlantBsEntity.class,
					Constant.PROD_PLANT_SIGGENTHAL);
			item.setProductionPlant(plant);
		}
		List<OrderItemEntity> orderItems = origOrder.getOrderItems();
		OrderItemEntity firstOrderItem = orderItems.get(0);
		CurrencyBsEntity currency = firstOrderItem.getCurrency();
		item.setCurrency(currency);

		final ReadOnlyDao<RailRepSoldtoShiptoEntity> railRepDao = getDaoProvider().getRailRepSoldtoShiptoEntityDao();
		final Filter<RailRepSoldtoShiptoEntity> railRepFilter = railRepDao.createFilter();

		railRepFilter.addExpression(Restrictions.eq(RailRepSoldtoShiptoEntity.ATTR_RAIL_RETURN_SAREA_FLAG, Boolean.TRUE));
		railRepFilter.addExpression(Restrictions.eq(RailRepSoldtoShiptoEntity.ATTR_SHIPTO_SAP_BP_ID,
				wagon.getReturnShiptoSapBp().getSapBpId()));
		railRepFilter.addExpression(Restrictions.neq(RailRepSoldtoShiptoEntity.ATTR_SAP_BP_ID,
				wagon.getReturnShiptoSapBp().getSapBpId()));

		RailRepSoldtoShiptoEntity railRepSoldShip = null;
		try {
			Authorizer.disableNextAccessRestriction();
			railRepSoldShip = railRepDao.findSingle(railRepFilter);
		}
		catch (NonUniqueResultException e) {
			LOG.info("Multiple Soldtos on Ship to SAP Business partner ID: "
					+ wagon.getReturnShiptoSapBp().getSapBpId(), e);
			return null;
		}
		if (railRepSoldShip == null) {
			return null;
		}

		Date[] dates = getDepartureArrivalDate(wagon.getLastKnownRailwayStation().getRailwayStationId(),
				wagon.getReturnShiptoSapBp().getRailwayStation().getRailwayStationId(), date);

		if (dates == null) {
			LOG.info("Cannot create return order for order " + origOrder.getOrderId()
					+ " because there is no timetable entry");
			return null;
		}

		order.setActualDeliveryDat(dates[1]);
		order.setActualDepRailwayStation(wagon.getLastKnownRailwayStation());
		order.setCommCarrier(origOrder.getCommCarrier());
		order.setExecCarrier(origOrder.getExecCarrier());
		order.setDeliveryFromDat(DateTimeUtils.cleanTimeWithDefaultTimeZone(dates[0]));
		order.setDeliveryToDat(dates[1]);
		order.setDeliveryType(referentialDataService.findBsEntity(DeliveryTypeBsEntity.class,
				Constant.DELIVERY_TYPE_RAIL_DIRECT));
		order.setDelivnoteEmailFlag(Boolean.FALSE);
		order.setDelivnoteFaxFlag(Boolean.FALSE);
		order.setDelivnoteSinglePaperFlag(Boolean.FALSE);
		order.setForceToSapFlag(Boolean.FALSE);

		// TODO (pascal Nov 10, 2012): Remove after Release 2.1.0.6 (Nov 26)
		// LOGON-5630: DeliveryNoteCollFlag is no longer saved on individual Order but on CustOrder
		//order.setDelivnoteCollPaperFlag(Boolean.FALSE);

		order.setOnHoldOrderAndLockedFlag(Boolean.FALSE);
		order.setDistributionDest(dDest);
		order.setFreeDeliveryFlag(Boolean.FALSE);
		order.setSapSuppressMinChargeFlag(Boolean.FALSE);
		order.setReleaseSapFlag(Boolean.TRUE);
		order.setNoPdaOrRailEventsFlag(Boolean.FALSE);
		order.setOrderConfEmailFlag(Boolean.FALSE);
		order.setOrderConfFaxFlag(Boolean.FALSE);
		order.setOrderConfSmsFlag(Boolean.FALSE);
		order.setLoadConfEmailFlag(Boolean.FALSE);
		order.setLoadConfFaxFlag(Boolean.FALSE);
		order.setLoadConfSmsFlag(Boolean.FALSE);
		order.setOrderCreationType(referentialDataService.findBsEntity(OrderCreationTypeBsEntity.class,
				Constant.ORDER_CREATION_TYPE_CREATED_BY_LOGON));
		order.setOrderNumber(fetchSequenceNextVal("SLGNC_ORDER_NUMBER"));
		order.setOrderStatus(referentialDataService.findBsEntity(OrderStatusBsEntity.class,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER));
		order.setPool(origOrder.getPool());
		order.setArchivingStatus(referentialDataService.findBsEntity(ArchivingStatusBsEntity.class,
				Constant.ARCHIVING_STATUS_NEW));
		order.setReschedulingFlag(Boolean.FALSE);
		order.setSarea(getDaoProvider().getSareaEntityDao().findById(railRepSoldShip.getSareaId()));
		order.setShippingpoint(railChainShippingpoint);
		// marius/2012-10-24: Moving this up, since it is needed when updating the
		// order item numerator/denominator. See LOGON-5705.
		//order.setShiptoSapBp(wagon.getReturnShiptoSapBp());
		Authorizer.disableNextAccessRestriction();
		order.setSoldtoSapBp(getDaoProvider().getSapBpEntityDao().findById(railRepSoldShip.getSapBpId()));
		order.setBilltoSapBp(order.getSoldtoSapBp());
		order.setOrderItems(new LinkedList<OrderItemEntity>());
		order.getOrderItems().add(item);
		item.setPlannedLoadingTimeMin(0L);
		order.setPlannedUnloadingTimeMin(0L);

		getDaoProvider().getOrderEntityDao().persist(order);

		BizeventEntity event = new BizeventEntity();

		event.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class,
				Constant.BIZEVENT_TYPE_NEW));
		event.setEventTs(date);
		event.setOrder(order);
		event.setShippingpoint(order.getShippingpoint());
		event.setCoordLat(order.getShippingpoint().getCoordLat());
		event.setCoordLon(order.getShippingpoint().getCoordLon());
		event.setRailwayStation(order.getActualDepRailwayStation());
		event.setWagon(wagon);
		monitoringTablesService.processBizevent(event);

		return order;
	}

	/**
	 */
	private OrderEntity createRailMaintenanceOrder(WagonEntity wagon, ShippingpointEntity shippingpoint, Date date,
			PoolEntity pool, CarrierEntity carrier, List<WagonMaintEntity> maint) {
		final ShiptoSareaEntity repairShiptoSarea;
		try {
			repairShiptoSarea = getRepairMaintenanceShipto(maint);
		}
		catch (NonUniqueResultException e) {
			LOG.info("Repair shipto could not be determined [wagon: " + wagon.getWagonNo() + ", shipppingpoint "
					+ shippingpoint.getShippingpointName() + "]");
			return null;
		}

		final SareaEntity sarea = repairShiptoSarea.getSarea();
		final ShiptoEntity repairShipto = repairShiptoSarea.getShipto();

		if (repairShipto == null) {
			LOG.info("Repair shipto could not be determined [wagon: " + wagon.getWagonNo() + ", shipppingpoint "
					+ shippingpoint.getShippingpointName() + "]");
			return null;
		}

		final SapBpEntity sapBp = repairShipto.getShiptoSapBp();
		RailwayStationEntity railwayStation = shippingpoint.getRailwayStation();
		RailwayStationEntity arrRailwayStation = sapBp.getRailwayStation();

		if (arrRailwayStation == null) {
			LOG.info("Arrival railway station could not be determined [wagon: " + wagon.getWagonNo()
					+ ", repair shipto railway station:" + sapBp.getRailwayStation().getRailwayStationNo() + "]");
			return null;
		}

		Date[] dates = getDepartureArrivalDate(railwayStation.getRailwayStationId(),
				arrRailwayStation.getRailwayStationId(), date);

		if (dates == null) {
			LOG.info("Cannot create maintenance order for wagon " + wagon.getWagonNo()
					+ " because there is no timetable entry");
			return null;
		}

		String sapMaterialNo = OrderSpecialUtils.getRepairRailOrderMaterialNo(wagon, maint);

		OrderItemEntity item = new OrderItemEntity();
		ReadWriteDao<SapMaterialEntity> sapMaterialEntityDao = getDaoProvider().getSapMaterialEntityDao();
		Filter<SapMaterialEntity> filter1 = sapMaterialEntityDao.createFilter();
		filter1.addExpression(Restrictions.eq(SapMaterialEntity.ATTR_SAP_MATERIAL_NO, sapMaterialNo));

		Authorizer.disableNextAccessRestriction();
		SapMaterialEntity sapMat = sapMaterialEntityDao.findSingle(filter1);
		item.setSapMaterial(sapMat);
		item.setOrderedQuantity(new BigDecimal(1));
		item.setOrderedUom(item.getSapMaterial().getBaseUom());
		item.setDeliveredQuantity(new BigDecimal(1));
		item.setDeliveredUom(item.getSapMaterial().getBaseUom());
		item.setOrderItemNo(new Long(10));
		if (!LogonPackageHandlingUtil.isService(sapMat)) {
			item.setProductionPlant(shippingpoint.getPlant());
		}
		else {
			PlantBsEntity plant = referentialDataService.findBsEntity(PlantBsEntity.class,
					Constant.PROD_PLANT_SIGGENTHAL);
			item.setProductionPlant(plant);
		}
		item.setCurrency(referentialDataService.findBsEntity(CurrencyBsEntity.class, Constant.CURRENCY_CODE_CHF));

		Filter<RailRepSoldtoShiptoEntity> soldShipFilter = new FilterImpl<RailRepSoldtoShiptoEntity>();
		soldShipFilter.addExpression(Restrictions.eq(RailRepSoldtoShiptoEntity.ATTR_SHIPTO_SAP_BP_ID,
				sapBp.getSapBpId()));
		final RailRepSoldtoShiptoEntity soldShip;
		try {
			Authorizer.disableNextAccessRestriction();
			soldShip = getDaoProvider().getRailRepSoldtoShiptoEntityDao().findSingle(soldShipFilter);
		}
		catch (NonUniqueResultException e) {
			LOG.info(
					"Multiple Soldtos on sarea: " + sarea.getSareaId() + ", SAP Business partner ID: "
							+ sapBp.getSapBpId(), e);
			return null;
		}

		if (soldShip == null) {
			return null;
		}

		SapBpEntity soldtoSapBp = getDaoProvider().getSapBpEntityDao().findById(soldShip.getSapBpId());

		OrderEntity order = new OrderEntity(); // rail maintenance order

		order.setActualDeliveryDat(dates[1]);
		order.setActualDepRailwayStation(railwayStation);
		order.setOnHoldOrderAndLockedFlag(Boolean.FALSE);
		order.setCommCarrier(carrier);
		order.setExecCarrier(carrier);
		order.setDeliveryFromDat(DateTimeUtils.cleanTimeWithDefaultTimeZone(dates[0]));
		order.setDeliveryToDat(dates[1]);
		order.setDeliveryType(referentialDataService.findBsEntity(DeliveryTypeBsEntity.class,
				Constant.DELIVERY_TYPE_RAIL_DIRECT));
		order.setDelivnoteEmailFlag(Boolean.FALSE);
		order.setDelivnoteFaxFlag(Boolean.FALSE);
		order.setDelivnoteSinglePaperFlag(Boolean.FALSE);
		order.setForceToSapFlag(Boolean.FALSE);

		// TODO (pascal Nov 10, 2012): Remove after Release 2.1.0.6 (Nov 26)
		// LOGON-5630: DeliveryNoteCollFlag is no longer saved on individual Order but on CustOrder
		// order.setDelivnoteCollPaperFlag(Boolean.FALSE);

		order.setDistributionDest(referentialDataService.findBsEntity(DistributionDestBsEntity.class,
				Constant.DISTRIBUTION_DEST_REPAIR));
		order.setFreeDeliveryFlag(Boolean.FALSE);
		order.setSapSuppressMinChargeFlag(Boolean.FALSE);
		order.setReleaseSapFlag(Boolean.TRUE);
		order.setNoPdaOrRailEventsFlag(Boolean.FALSE);
		order.setOrderConfEmailFlag(Boolean.FALSE);
		order.setOrderConfFaxFlag(Boolean.FALSE);
		order.setOrderConfSmsFlag(Boolean.FALSE);
		order.setLoadConfEmailFlag(Boolean.FALSE);
		order.setLoadConfFaxFlag(Boolean.FALSE);
		order.setLoadConfSmsFlag(Boolean.FALSE);
		order.setOrderCreationType(referentialDataService.findBsEntity(OrderCreationTypeBsEntity.class,
				Constant.ORDER_CREATION_TYPE_CREATED_BY_LOGON));
		order.setOrderNumber(fetchSequenceNextVal("SLGNC_ORDER_NUMBER"));
		order.setOrderStatus(referentialDataService.findBsEntity(OrderStatusBsEntity.class,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER));
		order.setPool(pool);
		order.setReschedulingFlag(Boolean.FALSE);
		order.setSarea(sarea);
		order.setArchivingStatus(referentialDataService.findBsEntity(ArchivingStatusBsEntity.class,
				Constant.ARCHIVING_STATUS_NEW));
		order.setShippingpoint(shippingpoint);
		order.setShiptoSapBp(sapBp);
		order.setSoldtoSapBp(soldtoSapBp);
		order.setBilltoSapBp(order.getSoldtoSapBp());
		order.setOrderItems(new LinkedList<OrderItemEntity>());
		order.getOrderItems().add(item);
		item.setOrder(order);
		item.setPlannedLoadingTimeMin(0L);
		order.setPlannedUnloadingTimeMin(0L);

		updateOrderItemNumeratorAndDenominator(item, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);
		getDaoProvider().getOrderEntityDao().persist(order);

		BizeventEntity event = new BizeventEntity();

		event.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class,
				Constant.BIZEVENT_TYPE_NEW));
		event.setEventTs(date);
		event.setOrder(order);
		event.setShippingpoint(order.getShippingpoint());
		event.setCoordLat(order.getShippingpoint().getCoordLat());
		event.setCoordLon(order.getShippingpoint().getCoordLon());
		event.setRailwayStation(order.getActualDepRailwayStation());
		event.setWagon(wagon);

		monitoringTablesService.processBizevent(event);

		OrderRepEntity or = new OrderRepEntity();
		or.setOrder(order);
		or.setEnterPlantDat(dates[1]);
		or.setEmailSendFlag(Boolean.TRUE);
		or.setEmailToAdresses(repairShipto.getOrderConfEmailAddresses());
		or.setEmailCcAdresses(wagon.getWagonKeeper().getEmailAddresses());
		String remarks = repairShipto.getNoteDelivery();
		if (remarks == null) {
			remarks = "";
		}
		ReadWriteDao<ShiptoSareaRestrEntity> shiptoSareaRestrEntityDao = getDaoProvider().getShiptoSareaRestrEntityDao();
		Filter<ShiptoSareaRestrEntity> filter2 = shiptoSareaRestrEntityDao.createFilter();
		filter2.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SAP_MATERIAL_ID,
				item.getSapMaterial().getSapMaterialId()));
		filter2.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA + '.'
				+ ShiptoSareaEntity.ATTR_SHIPTO, repairShipto));
		filter2.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA + '.'
				+ ShiptoSareaEntity.ATTR_SAREA, sarea));
		Authorizer.disableNextAccessRestriction();
		ShiptoSareaRestrEntity restr = getDaoProvider().getShiptoSareaRestrEntityDao().findSingle(filter2);

		if ((restr != null) && (restr.getNoteDelivery() != null)) {
			remarks += restr.getNoteDelivery();
		}
		or.setInvoicePaidFlag(Boolean.FALSE);
		or.setOfferAcceptFlag(Boolean.FALSE);
		or.setOfferDesiredFlag(Boolean.FALSE);
		or.setDocumentationAcceptFlag(Boolean.FALSE);
		or.setOrderRepAttachments(new ArrayList<OrderRepAttEntity>());

		List<OrderRepMaintTypeEntity> ormts = new ArrayList<OrderRepMaintTypeEntity>();
		Iterator<WagonMaintEntity> it = maint.iterator();
		while (it.hasNext()) {
			WagonMaintEntity wme = it.next();
			OrderRepMaintTypeEntity ormt = new OrderRepMaintTypeEntity();
			ormt.setOrderRep(or);
			ormt.setMaintType(wme.getMaintType());
			ormts.add(ormt);
		}
		String repairComments = StringUtils.defaultString(wagon.getWagonNoteMaintenance());
		remarks += String.format("%n") + String.format("%n") + repairComments + String.format("%n");

		or.setOrderRepMaintTypes(ormts);
		or.setEmailAdditionalRemarks(remarks);

		// add predefined damage codes
		Filter<WagonDmgcodeTypeEntity> dmgCodeFilter = new FilterImpl<WagonDmgcodeTypeEntity>();
		dmgCodeFilter.addExpression(Restrictions.eq(WagonDmgcodeTypeEntity.ATTR_WAGON, wagon));
		List<WagonDmgcodeTypeEntity> dmgTypes = getDaoProvider().getWagonDmgcodeTypeEntityDao().find(dmgCodeFilter);
		List<OrderRepDmgcodeTypeEntity> orderRepDmgCodes = getOrderRepDmgEntitiesFromWagonDmgEntities(dmgTypes);
		for (Object object : orderRepDmgCodes) {
			OrderRepDmgcodeTypeEntity entity = (OrderRepDmgcodeTypeEntity) object;
			entity.setOrderRep(or);
		}
		or.setDamageCdList(orderRepDmgCodes);

		// add predefined attachments
		Filter<WagonRepAttEntity> attachmentFilter = new FilterImpl<WagonRepAttEntity>();
		attachmentFilter.addExpression(Restrictions.eq(WagonRepAttEntity.ATTR_WAGON, wagon));
		List<WagonRepAttEntity> wagonAttEntities = getDaoProvider().getWagonRepAttEntityDao().find(attachmentFilter);
		List<OrderRepAttEntity> orderAttEntities = getOrderRepAttEntitiesFromWagonRepAttEntities(wagonAttEntities);
		for (Object object : orderAttEntities) {
			OrderRepAttEntity entity = (OrderRepAttEntity) object;
			entity.setOrderRep(or);
		}
		or.setOrderRepAttachments(orderAttEntities);

		getDaoProvider().getOrderRepEntityDao().persist(or);

		return order;
	}

	/**
	 * Transforms a list of {@link WagonDmgcodeTypeEntity} into a list of {@link OrderRepDmgcodeTypeEntity}.
	 * @param or The {@link OrderRepEntity} to set. Must not be null
	 * @param wagonDmgTypes the list of {@link WagonDmgcodeTypeEntity} to transfrom. Can be null.
	 * @return a new {@link ArrayList} of {@link OrderRepDmgcodeTypeEntity}. If param <code>wagonDmgTypes</code>
	 * is null than an empty list.
	 */
	public List<OrderRepDmgcodeTypeEntity> getOrderRepDmgEntitiesFromWagonDmgEntities(
			List<WagonDmgcodeTypeEntity> wagonDmgTypes) {
		List<OrderRepDmgcodeTypeEntity> orderRepDmgCodes = new ArrayList<OrderRepDmgcodeTypeEntity>();
		if (wagonDmgTypes != null) {
			for (WagonDmgcodeTypeEntity wagonDmgcodeEntity : wagonDmgTypes) {
				OrderRepDmgcodeTypeEntity orderRepDmgcodeEntity = new OrderRepDmgcodeTypeEntity();
				orderRepDmgcodeEntity.setCtlActCd(wagonDmgcodeEntity.getCtlActCd());
				orderRepDmgcodeEntity.setDmgcodeType(wagonDmgcodeEntity.getDmgcodeType());
				orderRepDmgCodes.add(orderRepDmgcodeEntity);
			}
		}
		return orderRepDmgCodes;
	}

	/**
	 * Transforms a list of {@link WagonRepAttEntity} into a list of {@link OrderRepAttEntity}.
	 * @param or The {@link OrderRepEntity} to set. Must not be null
	 * @param wagonAttEntities the list of {@link WagonRepAttEntity} to transfrom. Can be null.
	 * @return a new {@link ArrayList} of {@link OrderRepAttEntity}. If param <code>wagonAttEntities</code>
	 * is null than an empty list.
	 */
	public List<OrderRepAttEntity> getOrderRepAttEntitiesFromWagonRepAttEntities(
			List<WagonRepAttEntity> wagonAttEntities) {
		List<OrderRepAttEntity> orderAttEntities = new ArrayList<OrderRepAttEntity>();
		if (wagonAttEntities != null) {
			for (WagonRepAttEntity wagonAttEntity : wagonAttEntities) {
				OrderRepAttEntity orderAttEntity = new OrderRepAttEntity();
				orderAttEntity.setAttachmentName(wagonAttEntity.getAttachmentName());
				orderAttEntity.setAttachmentType(wagonAttEntity.getAttachmentType());
				orderAttEntity.setData(wagonAttEntity.getData());
				orderAttEntity.setDataMimeType(wagonAttEntity.getDataMimeType());
				orderAttEntities.add(orderAttEntity);
			}
		}
		return orderAttEntities;
	}

	/**
	 * Returns a list of all those repair or maintenance types, which should already be executed
	 * according to maintenance schedule.
	 * Access restrictions are skipped!
	 * @param wagon
	 * @param date
	 * @return
	 */
	private List<WagonMaintEntity> getDueRepairMaintenaceTypes(WagonEntity wagon, Date date) {
		final ReadOnlyDao<WagonMaintEntity> dao = getDaoProvider().getWagonMaintEntityDao();
		final Filter<WagonMaintEntity> f = dao.createFilter();
		f.addExpression(Restrictions.eq(WagonMaintEntity.ATTR_WAGON_ID, wagon.getWagonId()));
		f.addExpression(Restrictions.le(WagonMaintEntity.ATTR_NEXT_DAT, date));
		f.addExpression(Order.desc(WagonMaintEntity.ATTR_NEXT_DAT));
		Authorizer.disableNextAccessRestriction();
		return getDaoProvider().getWagonMaintEntityDao().find(f);
	}

	/**
	 * Returns the common SapBp of the maintenance entries in case it is straightforward.
	 * In case it could not be determined, returns null.
	 * @param maint
	 * @return
	 */
	private ShiptoSareaEntity getRepairMaintenanceShipto(List<WagonMaintEntity> maint) {
		SapBpEntity commonShiptoSapBp = null;
		for (WagonMaintEntity maintEnt : maint) {
			if (maintEnt.getShiptoSapBp() == null) {
				continue;
			}
			if (commonShiptoSapBp == null) {
				commonShiptoSapBp = maintEnt.getShiptoSapBp();
			}
			if (!maintEnt.getShiptoSapBp().getSapBpId().equals(commonShiptoSapBp.getSapBpId())) {
				commonShiptoSapBp = null;
				break;
			}
		}
		if (commonShiptoSapBp == null) {
			return null;
		}

		final ReadOnlyDao<ShiptoSareaEntity> shiptoDao = getDaoProvider().getShiptoSareaEntityDao();
		final Filter<ShiptoSareaEntity> f = shiptoDao.createFilter();
		f.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID,
				commonShiptoSapBp.getSapBpId()));
		f.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SAREA + "." + SareaEntity.ATTR_RAIL_RETURN_SAREA_FLAG,
				Boolean.TRUE));
		Authorizer.disableNextAccessRestriction();
		ShiptoSareaEntity repairShiptoSarea = shiptoDao.findSingle(f);

		return repairShiptoSarea;
	}

	/**
	 * Creates two orders. Bimodal road and rail.
	 * @param bimodalRoadOrder
	 * @param item
	 * @param railPartOrder
	 * @param serviceItems
	 */
	public void createBimodalOrder(List<OrderEntity> bimodalRoadOrders, OrderEntity railPartOrder) {
		Long railOrderId = railPartOrder.getOrderId();
		railPartOrder = em.find(OrderEntity.class, railOrderId);
		final OrderItemEntity bimodalRailPartItem = createOrderBimodalRailPart(railPartOrder);
		BizeventTypeBsEntity newBizEvent = referentialDataService.findBsEntity(BizeventTypeBsEntity.class,
				Constant.BIZEVENT_TYPE_NEW);
		for (OrderEntity bimodalRoadOrder : bimodalRoadOrders) {
			List<OrderItemEntity> roadOrderItems = bimodalRoadOrder.getOrderItems();
			OrderItemEntity roadOrderItem = roadOrderItems.get(0);
			Long roadOrderItemId = roadOrderItem.getOrderItemId();
			roadOrderItem = em.find(OrderItemEntity.class, roadOrderItemId);

			BizeventEntity bizevent = new BizeventEntity();
			bizevent.setBizeventType(newBizEvent);
			bizevent.setOrder(bimodalRoadOrder);
			bizevent.setEventTs(new Date());

			roadOrderItem.setBimodalRailOrderItem(bimodalRailPartItem);
			monitoringTablesService.processBizevent(bizevent);
		}
	}

	/**
	 * Helper method for the createMultiOrder method
	 * This is basically a simplified version (quantity is always 1 and the UoM is Bahnwagen) of the normal order saving
	 *
	 * @param OrderBulkprdEntity to persist
	 */
	private OrderItemEntity createOrderBimodalRailPart(OrderEntity order) {
		// FIXME Nov 3, 2015/gabor: someone, please, fix this side-effect hell!
		order.setNoPdaOrRailEventsFlag(false);
		order.setReschedulingFlag(false);
		List<SapContractItemEntity> sapContractItems = new ArrayList<SapContractItemEntity>();
		List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
		for (int i = 0; i < orderItems.size(); i++) {
			OrderItemEntity item = (OrderItemEntity) orderItems.get(i);
			item.setOrderItemNo((i + 1) * 10L);
			updateOrderItemNumeratorAndDenominator(item, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);
			if (item.getSapContractItem() != null) {
				sapContractItems.add(item.getSapContractItem());
			}
		}
		final DeliverySettingSalesAreaMap salesAreaMap = initSalesAreaMapForOrderValidation(order, true);
		Map<Long, QuantityRestrictionCalculation> quantityRestrCalcMap = quantityRestrictionService.calculateOpenQuantityForContractItems(
				sapContractItems, Collections.<Long> emptyList());
		validateOrder(order, salesAreaMap, quantityRestrCalcMap, false);
		// // FIXME Nov 3, 2015/gabor: any reason why we return the first element of the list? is it ordered by some criteria?
		return orderItems.get(0);
	}

	private TruckRouteEntity findTruckRoute(ShiptoEntity shipto, ShippingpointEntity shippingpoint,
			SapTrspModeBsEntity transportMode) {
		Filter<TruckRouteEntity> truckRouteFilter = new FilterImpl<TruckRouteEntity>();
		truckRouteFilter.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_START_SHIPPINGPOINT_ID,
				shippingpoint.getShippingpointId()));
		truckRouteFilter.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_END_SHIPTO_ID, shipto.getShiptoId()));
		String transportModeCd = transportModeCodeWithDefault(transportMode);
		truckRouteFilter.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_SAP_TRSP_MODE_CD, transportModeCd));
		return getDaoProvider().getTruckRouteEntityDao().findSingle(truckRouteFilter);
	}

	private String transportModeCodeWithDefault(SapTrspModeBsEntity transportMode) {
		String transportModeCd = transportMode != null ? transportMode.getSapTrspModeCd() : Constant.TRANSPORTATION_MODE_TRUCK;
		if (transportMode == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("transportModeCodeWithDefault: No transportMode provided, using fallback to "
						+ transportModeCd);
			}
		}
		return transportModeCd;
	}

	/**
	 * Retrieves the truck route given the parameters
	 */
	public TruckRouteEntity findTruckRoute(SapBpEntity shiptoSapBp, ShippingpointEntity shippingpoint,
			SapTrspModeBsEntity transportMode) {
		ReadWriteDao<ShiptoEntity> shiptoEntityDao = getDaoProvider().getShiptoEntityDao();
		Filter<ShiptoEntity> f = shiptoEntityDao.createFilter();
		f.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBp.getSapBpId()));
		ShiptoEntity shipto = shiptoEntityDao.findSingle(f);
		return findTruckRoute(shipto, shippingpoint, transportMode);
	}

	/**
	 * Retrieves the truck route given the parameters
	 */
	public TruckRouteEntity findTruckRoute(SapBpEntity shiptoSapBp, ShippingpointEntity shippingpoint,
			DeliveryTypeBsEntity deliveryType) {
		SapTrspModeBsEntity transportMode = deliveryType != null ? deliveryType.getSapTrspMode() : null;
		return findTruckRoute(shiptoSapBp, shippingpoint, transportMode);
	}

	public void sendDeliveryNoteByEmail(OrderEntity order, OutputdocEntity doc) {
		if (StringUtils.isEmpty(order.getOrderConfEmailAdresses())) {
			LOG.error("No email addresses for order " + order.getOrderId());
			return;
		}
		Locale locale = LangUtil.getOrderLocale(order);
		String subject = pdfGeneratorService.formatText("order.delivery.email.subject", locale,
				new Object[] { order.getOrderNumber().toString() });
		String message = pdfGeneratorService.formatText("order.delivery.email.text", locale,
				new Object[] { order.getOrderNumber().toString() });
		final String[] to = StringUtils.split(order.getOrderConfEmailAdresses(), ',');
		MimeBodyPart mimeBodyPartAttachment = emailService.createMimeBodyPartAttachment(doc.getData(),
				doc.getDataMimeType(), doc.getOutputdocName());
		emailService.send(to, null, null, subject, message, new MailPropertiesConfiguration(order),
				mimeBodyPartAttachment);
	}

	public void sendDeliveryNoteByFax(OrderEntity order, OutputdocEntity doc) {
		if (StringUtils.isEmpty(order.getOrderConfFaxNumber())) {
			LOG.error("No fax number specified for order " + order.getOrderId());
			return;
		}
		if (!faxAndSmsService.callSendFaxFile(order.getOrderConfFaxNumber(), doc)) {
			throw new LogonException("Sending fax failed", Reason.RES_ERROR_UNSPECIFIED);
		}
	}

	/**
	 * @param loadingpoint used primarily for printer determination
	 * @param order used secondary for printer determination (if no loadingpoint specified)
	 * @param doc going to be printed
	 * @return whether printing happened or not
	 *
	 * Notice that this return value has nothing to do with success or validity,
	 * because we have several use cases when paper printing will skipped
	 * due to legal business reasons.
	 */
	public boolean sendDeliveryNoteToPrinter(OrderEntity order, OutputdocEntity doc, LoadingpointEntity loadingpoint)
			throws PrintException {
		// If the caller did not specify the loading point we find it using the order items
		if (loadingpoint == null) {
			loadingpoint = OrderUtils.getLoadingpointFromOrder(order);
		}

		if (loadingpoint == null) {
			/**
			 * Only warning, because there are valid use cases for this situation,
			 * e.g. bimodal road part, landfill order first loading at customer side.
			 *
			 * Currently we can not decide at this point (due to the lack of exact and permanent
			 * business knowhow), whether it is valid or not.
			 */
			return false;
		}

		DelivNotePrintTypeBsEntity delivNotePrintType = loadingpoint.getDelivNotePrintType();
		Long delivNotePrintTypeCd = delivNotePrintType.getDelivNotePrintTypeCd();
		boolean logonIsPrinting = delivNotePrintTypeCd.equals(Constant.DELIV_NOTE_PRINT_TYPE_LOGON);
		if (!logonIsPrinting) {
			/**
			 * Valid case, but no printing needed.
			 */
			LOG.debug("LOGON is not responsible for printing, but some other system (SAP, Weighingsystem)!");
			return false;
		}

		PrinterBsEntity printer = loadingpoint.getPrinter();
		if (printer == null) {
			/**
			 * Valid case, customer has no printer specified.
			 */
			String message = "LOGON responsible for printing, but the loadingpoint has no printer specified: "
					+ loadingpoint.getLoadingpointName() + " (" + loadingpoint.getLoadingpointId() + ")";
			LOG.warn(message);
			return false;
		}

		URI printerURI = null;
		try {
			String printerUriOfPrinter = printer.getPrinterUri();
			printerURI = new URI(printerUriOfPrinter);
		}
		catch (URISyntaxException uriSyntaxException) {
			String message = "Invalid printer specified for the loadingpoint: " + loadingpoint.getLoadingpointName()
					+ " (" + loadingpoint.getLoadingpointId() + ")";
			LOG.error(message);
			throw new LogonPrintException(message,
					Reason.RES_ERROR_DELIVERY_NOTE_PRINTING_INVALID_PRINTER_FOR_LOADINGPOINT);
		}
		String printerName = printer.getPrinterName();
		Long delivNoteCopiesLogon = loadingpoint.getDelivNoteCopiesLogon();
		/* Number of copies is null by almost every loading point in the test DB,
		 * which caused here NullPointerException before.
		 * Now the default is one copy of each delivery note.
		 */
		int intValue = (delivNoteCopiesLogon == null || delivNoteCopiesLogon < 1) ? 1 : delivNoteCopiesLogon.intValue();
		faxAndSmsService.sendToPrinter(printerURI, printerName, doc, intValue);
		return true;
	}

	/**
	 * Sends delivery notes to the channels defined by flags
	 *
	 * The single delivery note has to be previously generated,
	 * the collective delivery note is generated on the fly when needed.
	 *
	 * @param orderId The ID of the order for which the delivery note should be send.
	 * @faxFlag true if fax needs to be sent
	 * @emailFlag true if email needs to be sent
	 * @paperFlag true if paper needs to be sent
	 * @return whether all necessary DNs were printed to paper
	 *
	 * Notice that this return value has nothing to do with success or validity,
	 * because we have several use cases when paper printing will skipped
	 * due to legal business reasons.
	 *
	 */
	private boolean sendDeliveryNotes(OrderEntity order, boolean faxFlag, boolean emailFlag, boolean paperFlag,
			LoadingpointEntity loadingPoint) {
		Long orderId = order.getOrderId();
		List<OrderOutputdocEntity> outputDocuments = loadDeliveryNoteList(orderId);
		boolean empty = outputDocuments.isEmpty();
		Long orderNumber = order.getOrderNumber();
		if (empty) {
			LOG.error("No tlgnc_order_outputdoc entry found for order id " + orderId + ", order number: " + orderNumber
					+ ". Could not send delivery note.");
			return false;
		}

		OrderOutputdocEntity firstOrderOutputDoc = outputDocuments.get(0);

		Boolean deliveryNoteFaxFlag = order.getDelivnoteFaxFlag();
		OutputdocEntity pdfOutputdoc = firstOrderOutputDoc.getPdfOutputdoc();
		if (faxFlag && deliveryNoteFaxFlag) {
			if (pdfOutputdoc == null) {
				LOG.error("Cannot send fax, no PDF delivery note for order id " + orderId + ", order number: "
						+ orderNumber + ".");
			}
			else {
				try {
					sendDeliveryNoteByFax(order, pdfOutputdoc);
				}
				catch (Exception ex) {
					LOG.error("Fax sending failed for order id " + orderId, ex);
				}
			}
		}
		Boolean deliveryNoteEmailFlag = order.getDelivnoteEmailFlag();
		if (emailFlag && deliveryNoteEmailFlag) {
			if (pdfOutputdoc == null) {
				LOG.error("Cannot send email, no PDF delivery note for order id " + orderId + ", order number: "
						+ orderNumber + ".");
			}
			else {
				try {
					sendDeliveryNoteByEmail(order, pdfOutputdoc);
				}
				catch (Exception ex) {
					LOG.error("Email sending failed for order id " + orderId, ex);
				}
			}
		}
		boolean allNecessaryDNsPrintedToPaper = true;
		Boolean delivnoteSinglePaperFlag = order.getDelivnoteSinglePaperFlag();
		if (paperFlag && delivnoteSinglePaperFlag) {
			OutputdocEntity psOutputdoc = firstOrderOutputDoc.getPsOutputdoc();
			if (psOutputdoc == null) {
				LOG.error("Cannot send single DN to printer, no PS delivery note for order id " + orderId
						+ ", order number: " + orderNumber + ".");
				allNecessaryDNsPrintedToPaper = false;
			}
			else {
				allNecessaryDNsPrintedToPaper &= printDeliveryNote(order, loadingPoint, psOutputdoc);
			}

			CustorderEntity customerOrder = order.getCustorder();
			if (shouldGenerateCollectiveDeliveryNote(customerOrder)) {
				Long customerOrderId = customerOrder.getCustorderId();
				if (generateCollectiveDeliveryNote(customerOrderId)) {
					allNecessaryDNsPrintedToPaper &= printCollectiveDeliveryNote(order, loadingPoint);
				}
				else {
					allNecessaryDNsPrintedToPaper = false;
				}
			}
		}
		return allNecessaryDNsPrintedToPaper;
	}

	/**
	 * @param ID of the customer order
	 * @return success of generation
	 * @throws LogonException, only business exceptions can fly out (technical ones not)
	 */
	public boolean generateCollectiveDeliveryNote(Long customerOrderId) {
		try {
			return pdfGeneratorService.generateCollPdf(customerOrderId);
		}
		catch (LogonException logonException) {
			throw logonException;
		}
		catch (Error error) {
			LOG.error("Unknown error occured during collective DN generation", error);
			return false;
		}
		catch (Exception exception) {
			LOG.error("Unknown exception occured during collective DN generation", exception);
			return false;
		}
	}

	/**
	 *
	 * @param customer order
	 * @return If the last order of the customer order has been loaded, and the related flags are set.
	 */
	public boolean shouldGenerateCollectiveDeliveryNote(CustorderEntity customerOrder) {
		if (customerOrder == null || customerOrder.getDelivnoteCollPaperFlag() == false) {
			return false;
		}
		List<OrderEntity> siblingOrders = customerOrder.getOrders();
		// Check if all Orders are loaded. If one is not, no coll.del.note is created on endload
		for (OrderEntity siblingOrder : siblingOrders) {
			OrderStatusBsEntity orderStatus = siblingOrder.getOrderStatus();
			boolean loadedOrBeyond = LogonStatusUtils.isLoadedOrBeyond(orderStatus);
			if (!loadedOrBeyond) {
				return false;
			}
		}
		LOG.info("Last Order of Custorder has been loaded. Printing CollectiveDeliveryNote");
		return true;
	}

	/**
	 * If collective DN is already generated, then prints it out.
	 *
	 * @param order
	 * @param loadingPoint
	 * @return whether collective delivery note was printed to paper
	 * @throws LogonException, only business exceptions can fly out (technical ones not)
	 */
	public boolean printCollectiveDeliveryNote(OrderEntity order, LoadingpointEntity loadingPoint) {
		try {
			CustorderEntity customerOrder = order.getCustorder();
			CustorderOutputdocEntity customerOutputDoc = getCustOrderOutputDocEntry(customerOrder);
			Long customerOrderNumber = customerOrder.getCustorderNumber();
			Long customerOrderId = customerOrder.getCustorderId();
			if (customerOutputDoc == null) {
				LOG.error("No tlgnc_custorder_outputdoc entry found for custorder id " + customerOrderId
						+ ", custorder number: " + customerOrderNumber + ". Could not send collective delivery note.");
				return false;
			}
			OutputdocEntity psOutputdocOfDocEntry = customerOutputDoc.getPsOutputdoc();
			if (psOutputdocOfDocEntry == null) {
				LOG.error("Cannot send to printer, no PS collective delivery note for custorder id " + customerOrderId
						+ ", custorder number: " + customerOrderNumber + ".");
				return false;
			}
			return sendDeliveryNoteToPrinter(order, psOutputdocOfDocEntry, loadingPoint);
		}
		catch (PrintException printException) {
			Long orderNumber = order.getOrderNumber();
			Long orderId = order.getOrderId();
			String errorMessage = "Printing collective delivery note failed for order id " + orderId
					+ ", order number: " + orderNumber + ".";
			LOG.error(errorMessage, printException);
			return false;
		}
		catch (LogonException logonException) {
			throw logonException;
		}
		catch (Error error) {
			LOG.error("Unknown error occured during collective DN printing", error);
			return false;
		}
		catch (Exception exception) {
			LOG.error("Unknown exception occured during collective DN printing", exception);
			return false;
		}
	}

	/**
	 *
	 * @param order
	 * @param loadingPoint
	 * @param psOutputdoc
	 * @return whether single delivery note was printed to paper
	 * @throws LogonException, only business exceptions can fly out (technical ones not)
	 */
	public boolean printDeliveryNote(OrderEntity order, LoadingpointEntity loadingPoint, OutputdocEntity psOutputdoc) {
		try {
			return sendDeliveryNoteToPrinter(order, psOutputdoc, loadingPoint);
		}
		catch (PrintException printException) {
			Long orderId = order.getOrderId();
			Long orderNumber = order.getOrderNumber();
			String errorMessage = "Printing delivery note failed for order id " + orderId + ", order number: "
					+ orderNumber + ".";
			LOG.error(errorMessage, printException);
			return false;
		}
		catch (LogonException logonException) {
			throw logonException;
		}
		catch (Error error) {
			LOG.error("Unknown error occured during single DN printing", error);
			return false;
		}
		catch (Exception exception) {
			LOG.error("Unknown exception occured during single DN printing", exception);
			return false;
		}
	}

	/**
	 * Loads a list of delivery notes for a given order ordered by creation timestamp => latest is the first in the list
	 * @param orderId
	 * @adnlog.exclude return
	 */
	public List<OrderOutputdocEntity> loadDeliveryNoteList(Long orderId) {
		final ReadOnlyDao<OrderOutputdocEntity> orderDocDao = getDaoProvider().getOrderOutputdocEntityDao();
		final Filter<OrderOutputdocEntity> filter = orderDocDao.createFilter();
		filter.addExpression(Restrictions.eq(OrderOutputdocEntity.ATTR_ORDER_ID, orderId));
		filter.addExpression(Order.desc(OrderOutputdocEntity.ATTR_CTL_CRE_TS));
		return orderDocDao.find(filter);
	}

	private CustorderOutputdocEntity getCustOrderOutputDocEntry(CustorderEntity custOrder) {
		ReadWriteDao<CustorderOutputdocEntity> orderOutDocDao = getDaoProvider().getCustorderOutputdocEntityDao();
		Filter<CustorderOutputdocEntity> filter = orderOutDocDao.createFilter();
		filter.addExpression(Restrictions.eq(CustorderOutputdocEntity.ATTR_CUSTORDER_ID, custOrder.getCustorderId()));
		filter.addExpression(Order.desc(CustorderOutputdocEntity.ATTR_CTL_CRE_TS));
		List<CustorderOutputdocEntity> docList = orderOutDocDao.find(filter);
		if ((docList == null) || (docList.isEmpty())) {
			return null;
		}
		return docList.get(0);
	}

	/**
	 * Simply sets the Daily Order Status for a Ship-to
	 *
	 * @param shipto
	 * @param statusCd
	 */
	public ShiptoEntity changeDailyOrderStatus(ShiptoEntity shipto, Long statusCd) {
		ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
		shipto.setDailyOrderStatus(referentialDataService.findBsEntity(DailyOrderStatusBsEntity.class, statusCd));
		shipto.setDailyOrderLastContactDat(new Date());
		// TODO gergely analyze this
		if (shipto.getContactPerson() != null && shipto.getContactPerson().getPersonId() == null) {
			shipto.setContactPerson(null);
		}
		return shiptoDao.merge(shipto);
	}

	/**
	 * OrderStatus is set to transmitted to carrier
	 *
	 * @param event mandatory fields
	 * 				-order
	 */
	public void transmitToCarrier(BizeventEntity event) {
		if (event.getOrder() == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		else if (event.getOrder().getCommCarrier() == null && event.getOrder().getExecCarrier() == null) {
			LOG.warn("order couldn't be transmitted to carrier, no commercial/executing carrier defined."
					+ " order number: " + event.getOrder().getOrderNumber());
			return;
		}
		changeOrderStatus(event.getOrder(), Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
	}

	/**
	 * OrderStatus is set according to state transition rules
	 *
	 * @param event mandatory fields
	 * 				-order
	 */
	public void rejectByCarrier(BizeventEntity event) {
		OrderEntity order = event.getOrder();

		if (order == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		Long newOrderStatusCd;
		order.setCarrierDeclineNote(event.getDescription());
		if (order.getExecCarrier() != null) {
			setExecutingCarrier(order, null);
			newOrderStatusCd = Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK;
			checkAssignmentExistence(newOrderStatusCd,order.getOrderId());
			changeOrderStatus(order, newOrderStatusCd);
		}
		else {
			setCommercialCarrier(order, null);
			if (order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER
					|| order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK) {
				newOrderStatusCd = Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK;
				checkAssignmentExistence(newOrderStatusCd, order.getOrderId());
				changeOrderStatus(order, newOrderStatusCd);
			}
		}
	}

	/**
	 * Check, and throw exception in case in the given order status
	 * active assignment should not exist.
	 * @param orderStatusCd
	 */
	private void checkAssignmentExistence(Long orderStatusCd, Long orderId){
		if (assignmentShouldNotExist.contains(orderStatusCd)) {
			long countAssignments = orderStore.getCountAssignmentForOrderId(orderId);
			if (countAssignments > 0) {
				LOG.error("Active assignment should not exist");
				throw new LogonException(Reason.RES_ORDER_WRONG_STATUS);
			}
		}
	}
	/**
	 * Returns the order preschedule row for the given order.
	 * @param order
	 * @return
	 */
	public OrderPrescheduleEntity findOrderPrescheduleForOrder(OrderEntity order) {
		if (order == null) {
			return null;
		}
		ReadWriteDao<OrderPrescheduleEntity> dao = getDaoProvider().getOrderPrescheduleEntityDao();
		Filter<OrderPrescheduleEntity> f = dao.createFilter();
		f.addExpression(Restrictions.eq(OrderPrescheduleEntity.ATTR_ORDER_ID, order.getOrderId()));
		return dao.findSingle(f);
	}

	/**
	 * OrderStatus is set to transmitted to pool
	 *
	 * @param event mandatory fields
	 * 				-order
	 */
	public void transmitToPool(BizeventEntity event) {
		OrderEntity order = event.getOrder();
		if (order == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		else if (order.getPool() == null) {
			LOG.warn("order couldn't be transmitted to pool, no pool defined. order number: " + order.getOrderNumber());
			return;
		}
		changeOrderStatus(order, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);
	}

	/**
	 * OrderStatus is set to new
	 *
	 * @param event mandatory fields
	 * 				-order
	 */
	public void rejectByPool(BizeventEntity event) {
		OrderEntity order = event.getOrder();
		if (order == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		if (order.getPool() == null) {
			return;
		}
		removeOrderFromPool(order);
	}

	/**
	 * Handles removing pool attribute of the order. This also means that those attributes which depend on
	 * the pool are removed, and cleanup of relevant data is done.
	 *
	 * @param order
	 */
	public void removeOrderFromPool(final OrderEntity order) {
		/**
		 * Prescheduling data belongs to the carrier, which is now removed anyway. Data belonging
		 * to the carrier and the order has to be removed.
		 */
		discardOrderPreschedule(order);
		order.setCommCarrier(null);
		order.setExecCarrier(null);
		order.setPool(null);
		changeOrderStatus(order, Constant.ORDER_STATUS_NEW);
		resetCarrierNotes(order);
	}

	/**
	 * Sets the order status to accepted by carrier, and stores in a bizevent audit the current
	 * pool.
	 *
	 * mandatory fields:
	 * 	- order
	 */
	public void carrierAcceptOrder(BizeventEntity event) {
		OrderEntity order = event.getOrder();
		if (order == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		changeOrderStatus(order, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);
	}

	/**
	 * Sets the order status to accepted by pool
	 *
	 * mandatory fields:
	 * 	- order
	 */
	public void poolAcceptOrder(BizeventEntity event) {
		OrderEntity order = event.getOrder();
		if (order == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		changeOrderStatus(order, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK);
	}

	/**
	 * Add the delivery signature to order
	 *
	 * mandatory fields:
	 * 	- order
	 */
	public void setDeliveryNoteSignature(BizeventEntity event, SignatureEntity signature, String signatureName,
			String comment) {
		// load the AssignmentPart where the ExtOrder is not null.
		List<AssignmentPartEntity> assignmentParts = event.getAssignment().getAssignmentParts();
		ExtOrderEntity extOrder = null;
		for (AssignmentPartEntity part : assignmentParts) {
			extOrder = part.getExtOrder();
			if (extOrder != null) {
				break;
			}
		}
		OrderEntity order = event.getOrder();
		if (order == null && extOrder == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		boolean isExternalOrder = order == null;

		Long orderStatus = null;
		if (isExternalOrder) {
			orderStatus = extOrder.getOrderStatus().getOrderStatusCd().longValue();
		}
		else {
			orderStatus = order.getOrderStatus().getOrderStatusCd().longValue();
		}

		if (Constant.ORDER_STATUS_SHIPPING == orderStatus) {
			enterUnloadingSite(event, false);
		}

		if (isExternalOrder) {
			extOrder.setCustDeliveryNoteComment(comment);
		}
		else {
			order.setDeliverynoteSignature(signature);
			order.setContactPersonSignatureTxt(signatureName);
			order.setCustDeliveryNoteComment(comment);
		}
	}

	public void setSilo(BizeventEntity event, ShiptoSiloVerificationWrapper siloVerificationWrapper) {
		if ((event.getAssignment() != null)
				&& (event.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_INTERNAL)
				&& (event.getOrder() == null)) {

			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		if (((event.getCoordLat() == null) || (event.getCoordLon() == null)) && (event.getShipto() != null)) {
			if (event.getShipto().getShiptoType().getShiptoTypeCd().longValue() == Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED) {
				event.setCoordLat(event.getOrder().getLnfCoordLat());
				event.setCoordLon(event.getOrder().getLnfCoordLon());
			}
			else {
				event.setCoordLat(event.getShipto().getCoordLat());
				event.setCoordLon(event.getShipto().getCoordLon());
			}
		}

		final ShiptoSiloEntity silo;
		final ShiptoSiloVerificationWrapper.SiloVerificationType siloVerificationType;
		final SiloVerifTypeBsEntity siloVerificationTypeEntity;

		if (siloVerificationWrapper != null && siloVerificationWrapper.getShiptoSilo() != null) {
			silo = siloVerificationWrapper.getShiptoSilo();
			siloVerificationType = siloVerificationWrapper.getSiloVerificationType();
			if (siloVerificationType != null) {
				siloVerificationTypeEntity = referentialDataService.findBsEntity(SiloVerifTypeBsEntity.class,
						siloVerificationType.getVerificationType());
			}
			else {
				siloVerificationTypeEntity = referentialDataService.findBsEntity(SiloVerifTypeBsEntity.class,
						ShiptoSiloVerificationWrapper.SiloVerificationType.NOT_VERIFIED.getVerificationType());
			}
		}
		else {
			silo = null;
			siloVerificationTypeEntity = null;
		}

		if (event.getOrder() != null) {
			List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(event.getOrder());
			for (OrderItemEntity orderItem : orderItems) {
				orderItem.setShiptoSilo(silo);
				orderItem.setShiptoSiloVerifType(siloVerificationTypeEntity);
			}
		}
	}

	/**
	 * Creates a TrafficJamEntity, triggered by the PDA.
	 * Mandatory fields:
	 * 	- truck
	 */
	public void startTrafficJam(BizeventEntity event) {
		if (event.getTruck() == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		ReadWriteDao<TrafficJamEntity> tjDao = getDaoProvider().getTrafficJamEntityDao();

		// Find previous traffic jams and close them with duration 0
		Filter<TrafficJamEntity> f = tjDao.createFilter();
		f.addExpression(Restrictions.eq(TrafficJamEntity.ATTR_TRUCK_ID, event.getTruck().getTruckId()));
		f.addExpression(Restrictions.le(TrafficJamEntity.ATTR_START_DAT, event.getEventTs()));
		f.addExpression(Restrictions.eq(TrafficJamEntity.ATTR_END_DAT, Constant.INFINITE_DATE));
		List<TrafficJamEntity> trafficJams = tjDao.findNRecs(0, 100, f);
		for (TrafficJamEntity jam : trafficJams) {
			jam.setEndDat(jam.getStartDat());
		}
		TrafficJamEntity newJam = new TrafficJamEntity();
		newJam.setTruck(event.getTruck());
		newJam.setStartDat(event.getEventTs());
		newJam.setEndDat(Constant.INFINITE_DATE);
		tjDao.persist(newJam);
	}

	/**
	 * Ends a TrafficJamEntity, triggered by the PDA.
	 * Mandatory fields:
	 * 	- truck
	 */
	public void endTrafficJam(BizeventEntity event) {
		if (event.getTruck() == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		ReadWriteDao<TrafficJamEntity> tjDao = getDaoProvider().getTrafficJamEntityDao();

		// Find previous open jams and close them with duration 0
		Filter<TrafficJamEntity> f = new FilterImpl<TrafficJamEntity>();
		f.addExpression(Restrictions.eq(TrafficJamEntity.ATTR_TRUCK_ID, event.getTruck().getTruckId()));
		f.addExpression(Restrictions.le(TrafficJamEntity.ATTR_START_DAT, event.getEventTs()));
		f.addExpression(Restrictions.eq(TrafficJamEntity.ATTR_END_DAT, Constant.INFINITE_DATE));

		f.addExpression(Order.asc(TrafficJamEntity.ATTR_START_DAT));

		// close all previous breaks with duration 0
		List<TrafficJamEntity> trafficJams = tjDao.findNRecs(0, 100, f);
		TrafficJamEntity lastJam = null;
		for (TrafficJamEntity jam : trafficJams) {
			jam.setEndDat(jam.getStartDat());
			lastJam = jam;
		}
		// set duration for last jam
		if (lastJam != null) {
			lastJam.setEndDat(event.getEventTs());
		}
	}

	/**
	 * Convenience variant
	 * @see OrderServiceBean#getDurationCorrectionFactor(Long,Long,SapTrspModeBsEntity)
	 */
	public double getDurationCorrectionFactor(Long shippingpointId, Long shiptoId, OrderEntity order) {
		DeliveryTypeBsEntity deliveryType = order != null ? order.getDeliveryType() : null;
		SapTrspModeBsEntity transportMode = deliveryType != null ? deliveryType.getSapTrspMode() : null;
		return getDurationCorrectionFactor(shippingpointId, shiptoId, transportMode);
	}

	/**
	 * Convenience variant
	 * @see OrderServiceBean#getDurationCorrectionFactor(Long,Long,SapTrspModeBsEntity)
	 */
	public double getDurationCorrectionFactor(Long shippingpointId, Long shiptoId, Long deliveryType) {
		SapTrspModeBsEntity transportMode = null;
		if (deliveryType != null) {
			// this is a convenience variant, we have to convert the input back to the object
			DeliveryTypeBsEntity delTypeBsEntity = referentialDataService.findBsEntity(DeliveryTypeBsEntity.class,
					deliveryType);
			transportMode = delTypeBsEntity.getSapTrspMode();
		}
		return getDurationCorrectionFactor(shippingpointId, shiptoId, transportMode);
	}

	/**
	 * Returns the duration correction factor for a trip from shippingpointId to shiptoId
	 * If no specific correction factor for that route exists, the default correction factor from
	 * configuration is returned.
	 * null values are allowed for parameters (cause function to return default)
	 */
	public double getDurationCorrectionFactor(Long shippingpointId, Long shiptoId, SapTrspModeBsEntity transportMode) {
		double defaultTruckRouteTimeCorrectionFactor = getConfigBean().getAppDefaultTruckRouteTimeCorrectionFactor();
		if (shippingpointId == null || shiptoId == null) {
			return defaultTruckRouteTimeCorrectionFactor;
		}
		ReadWriteDao<TruckRouteEntity> trd = getDaoProvider().getTruckRouteEntityDao();
		Filter<TruckRouteEntity> f = trd.createFilter();
		f.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_START_SHIPPINGPOINT_ID, shippingpointId));
		f.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_END_SHIPTO_ID, shiptoId));
		String transportModeCd = transportModeCodeWithDefault(transportMode);
		f.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_SAP_TRSP_MODE_CD, transportModeCd));
		TruckRouteEntity route = trd.findSingle(f);
		if (route != null && route.getTimeCorrectionFactor() != null) {
			return route.getTimeCorrectionFactor().doubleValue();
		}
		return defaultTruckRouteTimeCorrectionFactor;
	}

	public TruckRouteEntity getTruckRoute(Long shippingpointId, Long shiptoId, Long deliveryTypeCd) {
		if (shippingpointId == null || shiptoId == null) {
			return null;
		}
		ReadWriteDao<TruckRouteEntity> trd = getDaoProvider().getTruckRouteEntityDao();
		Filter<TruckRouteEntity> f = trd.createFilter();
		f.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_START_SHIPPINGPOINT_ID, shippingpointId));
		f.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_END_SHIPTO_ID, shiptoId));

		SapTrspModeBsEntity transportMode = null;
		if (deliveryTypeCd != null) {
			// this is a convenience variant, we have to convert the input back to the object
			DeliveryTypeBsEntity delTypeBsEntity = referentialDataService.findBsEntity(DeliveryTypeBsEntity.class,
					deliveryTypeCd);
			transportMode = delTypeBsEntity.getSapTrspMode();
		}
		String transportModeCd = transportModeCodeWithDefault(transportMode);
		f.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_SAP_TRSP_MODE_CD, transportModeCd));
		return trd.findSingle(f);
	}

	/**
	 * fetch a fetchMatShippingpoint
	 * @param sapMaterialId
	 * @param shippingpointId
	 * @return MatShippingpointEntity
	 */
	public MatShippingpointEntity fetchMatShippingpoint(Long sapMaterialId, Long shippingpointId) {
		ReadWriteDao<MatShippingpointEntity> dao = getDaoProvider().getMatShippingpointEntityDao();
		Filter<MatShippingpointEntity> f = dao.createFilter();
		f.addExpression(Restrictions.eq(MatShippingpointEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
		f.addExpression(Restrictions.eq(MatShippingpointEntity.ATTR_SHIPPINGPOINT_ID, shippingpointId));
		MatShippingpointEntity entity = dao.findSingle(f);
		return entity;
	}

	/**
	 * merges the mergedAssingmet into the targetAssignment, by moving all its load, drive-to and uload
	 * assignment parts into the other assignment.The parts of the mergerd assingment will be inserted directly
	 * after the parts associated with the order deliveryAfterOrderId in the target assignment.
	 * If deliveryAfterOrderId is null, The parts of mergedAssignment should be inserted before the parts
	 * from all other assignments.
	 */
	public AssignmentEntity mergeAssignments(Long sourceAssignmentId, Long targetAssignmentId, Long deliveryAfterOrderId) {
		if (sourceAssignmentId.longValue() == targetAssignmentId.longValue()) {
			throw new LogonException("Cannot merge the assignment with itself", Reason.RES_ERROR_IMPL);
		}
		HashSet<LoadEntity> loadsToRemove = new HashSet<LoadEntity>();

		AssignmentEntity targetAssignment = getDaoProvider().getAssignmentEntityDao().findById(targetAssignmentId);
		AssignmentEntity sourceAssignment = getDaoProvider().getAssignmentEntityDao().findById(sourceAssignmentId);
		final List<OrderEntity> sourceOrders = AssignmentUtils.getOrdersFromAssignment(sourceAssignment);
		final List<OrderEntity> targetOrders = AssignmentUtils.getOrdersFromAssignment(targetAssignment);

		final TruckEntity sourceTruck = sourceAssignment.getTruck();
		final TruckEntity targetTruck = targetAssignment.getTruck();

		/**
		 * As LDS cannot handle tours there is no chance to send here an update for the source
		 * assignment -> send a deleteLoadOrder as LDS should not try to load the order
		 * according to the original plan, where that order was the only one present in the assignment!
		 */
		if (AssignmentUtils.isInternal(targetAssignment)) {
			cancelAssignmentOnWeighSystem(targetAssignmentId, targetOrders);
		}

		/**
		 * The assignment is going to be inactivated -> it will never ever be loaded by
		 * anyone -> send cancel to lds.
		 */
		if (AssignmentUtils.isInternal(sourceAssignment)) {
			cancelAssignmentOnWeighSystem(sourceAssignmentId, sourceOrders);
		}

		/* For trucks: move source assignment to target trucks if the assignments
		 * are currently not scheduled on the same truck.
		 * For simple vehicles or rail, this is currently not done.
		 */
		if (sourceTruck != null && targetTruck != null && !sourceTruck.getTruckId().equals(targetTruck.getTruckId())) {
			moveAssignment(targetTruck.getTruckId(), sourceAssignmentId);
		}

		getDaoProvider().getDirectJpaDao().flush();

		List<AssignmentPartEntity> sourcePartList = getAssignmentPartsInSeqOrder(sourceAssignmentId);
		Iterator<AssignmentPartEntity> sourceParts = sourcePartList.iterator();

		List<AssignmentPartEntity> targetPartList = getAssignmentPartsInSeqOrder(targetAssignmentId);
		Iterator<AssignmentPartEntity> targetParts = targetPartList.iterator();

		int indexOfLastTargetPart = targetPartList.size() - 1;
		//use new sequence numbers that are after the last sequence number in the assingment
		AssignmentPartEntity lastTargetPart = targetPartList.get(indexOfLastTargetPart);
		long currSeqno = lastTargetPart.getAssignmentPartSeqNo() + 1;

		//find load assignment part before which we insert the mergerd assgnment's load assignmentParts
		AssignmentPartEntity currTargetPart = null;
		AssignmentPartEntity nextTargetPart = targetParts.next();

		boolean foundOrderLoad = false;
		final int numberOfChambers = getNumberOfChambersForTruck(targetTruck);
		final boolean hasChambers = numberOfChambers > 1;
		boolean[] chamberMap = new boolean[numberOfChambers + 1];

		while (targetParts.hasNext()) {
			currTargetPart = nextTargetPart;
			nextTargetPart = targetParts.next();
			if (AssignmentUtils.isLoadPart(currTargetPart)) {
				if (deliveryAfterOrderId == null) {
					break;
				}
				// If deliveryAfterOrderId != null iterate until a load references deliveryAfterOrderId
				// then again until it doesn't anymore
				LoadEntity currentTargetLoad = currTargetPart.getLoad();
				Long currentTargetLoadId = currentTargetLoad.getLoadId();

				if (existsOrderItem(currentTargetLoadId, deliveryAfterOrderId)) {
					foundOrderLoad = true;
					if (hasChambers) {
						Long chamberNumber = currentTargetLoad.getChamberNumber();
						int indexOfChamberNumber = chamberNumber.intValue();
						chamberMap[indexOfChamberNumber] = true;
					}
				}
				else if (foundOrderLoad) {
					break;
				}
			}
			else if (!AssignmentUtils.isPreLoadPart(currTargetPart)) {
				break;
			}
		}

		// now merge the load assignment parts
		AssignmentPartEntity currentSourcePart = null;
		AssignmentPartEntity nextSourcePart = sourceParts.next();
		long targetOffset = 0;
		while (sourceParts.hasNext()) {
			currentSourcePart = nextSourcePart;
			nextSourcePart = sourceParts.next();
			if (AssignmentUtils.isLoadPart(currentSourcePart)) {
				LoadEntity sourceLoad = currentSourcePart.getLoad();
				LoadEntity targetLoad = findLoad(targetAssignmentId, sourceLoad);

				if (targetLoad != null && LogonPackageHandlingUtil.isBag(sourceLoad)
						&& !LogonPackageHandlingUtil.isPallet(sourceLoad.getSapMaterial())) {
					//Matching load found: don't merge the load assignment part into the target assignment
					//but assign the target's load to the source assignments corresponding order items
					if (sourceLoad.getLoadingTs() != null) {
						// If source assignment's load is already active, then copy its attributes to the target's load
						if (targetLoad.getLoadingTs() != null) {
							// if the target's load is not active itself, which would be an error
							LOG.error("Load state mismatch when merging assignments. Load ids: "
									+ targetLoad.getLoadId() + ", " + sourceLoad.getLoadId());
							return null; // both assignments have active laods: can't merge
						}
						if (hasChambers) {
							chamberMap[sourceLoad.getChamberNumber().intValue()] = true;
						}
						targetLoad = mergeLoad(targetLoad, sourceLoad);
					}
					List<OrderItemEntity> orderItemList = getOrderItems(sourceLoad);
					for (OrderItemEntity sourceOrderItem : orderItemList) {
						sourceOrderItem.setLoad(targetLoad);
					}
					// now add the source load entity to the list which entities should be removed at the end of the method to avoid constraint violations
					loadsToRemove.add(sourceLoad);
					currentSourcePart.setLoad(null);
				}
				else {
					// no matching load found: merge the loading assignment part
					currentSourcePart.setAssignmentPartSeqNo(currSeqno);
					currentSourcePart.setAssignment(targetAssignment);
					long diff = nextSourcePart.getPlannedStart().getTime()
							- currentSourcePart.getPlannedStart().getTime();
					currentSourcePart.setPlannedStart(new Date(currTargetPart.getPlannedStart().getTime()
							+ targetOffset));
					if (hasChambers) {
						for (int i = 1; i < chamberMap.length; i++) {
							if (!chamberMap[i]) {
								chamberMap[i] = true;
								currentSourcePart.getLoad().setChamberNumber(new Long(i));
								break;
							}
						}
					}
					targetAssignment.getAssignmentParts().add(currentSourcePart);
					targetOffset += diff;
					currSeqno++;
				}

			}
			else if (!AssignmentUtils.isPreLoadPart(currentSourcePart)) {
				break;
			}
		}

		currTargetPart.setAssignmentPartSeqNo(currSeqno);
		currSeqno++;
		currTargetPart.setPlannedStart(new Date(currTargetPart.getPlannedStart().getTime() + targetOffset));
		if (hasChambers && currTargetPart.getLoad() != null) {
			for (int i = 1; i < chamberMap.length; i++) {
				if (!chamberMap[i]) {
					chamberMap[i] = true;
					currTargetPart.getLoad().setChamberNumber(new Long(i));
					break;
				}
			}
		}

		//now find the assignment part before which to insert the unload assignemnt parts and adjust the
		//planned start and seqnos inbetween
		while (targetParts.hasNext()) {
			currTargetPart = nextTargetPart;
			nextTargetPart = targetParts.next();

			if (hasChambers && AssignmentUtils.isLoadPart(currTargetPart)) {
				for (int i = 1; i < chamberMap.length; i++) {
					if (!chamberMap[i]) {
						chamberMap[i] = true;
						currTargetPart.getLoad().setChamberNumber(new Long(i));
						break;
					}
				}
			}

			if (AssignmentUtils.isDrivingFromPart(currTargetPart)) {
				break;
			}

			if (AssignmentUtils.isDrivingToPart(currTargetPart)) {
				if ((deliveryAfterOrderId == null)
						|| ((nextTargetPart.getOrder() != null) && nextTargetPart.getOrder().getOrderId().equals(
								deliveryAfterOrderId))) {
					break;
				}
			}

			currTargetPart.setPlannedStart(new Date(currTargetPart.getPlannedStart().getTime() + targetOffset));
			currTargetPart.setAssignmentPartSeqNo(currSeqno);
			currSeqno++;
		}

		if (AssignmentUtils.isFinishedPart(currTargetPart)) {
			currTargetPart.setPlannedStart(new Date(currTargetPart.getPlannedStart().getTime() + targetOffset));
			currTargetPart.setAssignmentPartSeqNo(currSeqno);
			currSeqno++;

			currTargetPart = nextTargetPart;
			nextTargetPart = null;
		}

		//now merge the to and uload assignment parts
		//the duration of the to parts at beginning an at the end og the merge are set to 0, as they are unknown
		//the fronntend has to set them using google maps (or manual entry)

		/*
		 * FIXME (marius Nov 21, 2012) [LOGON-5971]:
		 * The while loop below doesn't work if the sourceAssignment has only a "finished" part
		 * after the "unload" part (i.e., if it has no "from" part).
		 *
		 * Explanation:
		 * If initially sourceParts.hasNext() is true, the loop is entered, and at the end
		 * of the loop currentSourcePart and nextSourcePart are advanced (see below).
		 * If at this point currentSourcePart points to the "unload" part and nextSourcePart points
		 * to the "finished" part, the loop condition no longer holds, and the "unload" part
		 * will be skipped.
		 *
		 * If there is a "from" part, then it works because currentSourcePart will point to the
		 * "unload" part and nextSourcePart will point to the "from" part, and the loop
		 * condition still holds because there is a "finished" part after the from part.
		 */
		boolean first = true;
		while (sourceParts.hasNext()) {
			if (AssignmentUtils.isDrivingToPart(currentSourcePart)) {
				currentSourcePart.setAssignment(targetAssignment);
				currentSourcePart.setAssignmentPartSeqNo(currSeqno);
				currSeqno++;
				long diff = 0;
				if (first) {
					currentSourcePart.setPlannedStart(new Date(currTargetPart.getPlannedStart().getTime()
							+ targetOffset));
					currentSourcePart.setTransportDistanceM(null);
					currentSourcePart.setHomebaseFlag(currTargetPart.getHomebaseFlag());
					currentSourcePart.setLnfShiptoFlag(currTargetPart.getLnfShiptoFlag());
					currentSourcePart.setCoordLat(currTargetPart.getCoordLat());
					currentSourcePart.setCoordLon(currTargetPart.getCoordLon());
					currentSourcePart.setShippingpoint(currTargetPart.getShippingpoint());
					currentSourcePart.setShipto(currTargetPart.getShipto());
				}
				else {
					diff = nextSourcePart.getPlannedStart().getTime() - currentSourcePart.getPlannedStart().getTime();
					currentSourcePart.setPlannedStart(new Date(currTargetPart.getPlannedStart().getTime()
							+ targetOffset));
				}
				targetAssignment.getAssignmentParts().add(currentSourcePart);
				targetOffset += diff;
			}
			else if (AssignmentUtils.isUnloadPart(currentSourcePart)) {
				currentSourcePart.setAssignment(targetAssignment);
				currentSourcePart.setAssignmentPartSeqNo(currSeqno);
				currSeqno++;
				first = false;
				long diff = nextSourcePart.getPlannedStart().getTime() - currentSourcePart.getPlannedStart().getTime();
				currentSourcePart.setPlannedStart(new Date(currTargetPart.getPlannedStart().getTime() + targetOffset));
				targetAssignment.getAssignmentParts().add(currentSourcePart);
				targetOffset += diff;
			}
			else {
				break;
			}
			currentSourcePart = nextSourcePart;
			nextSourcePart = sourceParts.next();
		}

		//now adjust seqno and start times for the remaining target assignment parts
		currTargetPart.setAssignmentPartSeqNo(currSeqno);
		currSeqno++;
		long diff = nextTargetPart.getPlannedStart().getTime() - currTargetPart.getPlannedStart().getTime();
		currTargetPart.setPlannedStart(new Date(currTargetPart.getPlannedStart().getTime() + targetOffset));
		if (AssignmentUtils.isDrivingToPart(currTargetPart) || AssignmentUtils.isDrivingFromPart(currTargetPart)) {
			currTargetPart.setTransportDistanceM(null);
		}
		currTargetPart.setHomebaseFlag(currentSourcePart.getHomebaseFlag());
		currTargetPart.setCoordLat(currentSourcePart.getCoordLat());
		currTargetPart.setCoordLon(currentSourcePart.getCoordLon());
		currTargetPart.setShippingpoint(currentSourcePart.getShippingpoint());
		currTargetPart.setShipto(currentSourcePart.getShipto());

		if (nextTargetPart != null) {
			targetOffset -= diff;
			targetOffset += TimeUnit.MINUTES.toMillis(Constant.TMP_TRANSPORT_DURATION_MIN);
			while (targetParts.hasNext()) {
				currTargetPart = nextTargetPart;
				nextTargetPart = targetParts.next();
				currTargetPart.setAssignmentPartSeqNo(currSeqno);
				currSeqno++;
				currTargetPart.setPlannedStart(new Date(currTargetPart.getPlannedStart().getTime() + targetOffset));
			}
			nextTargetPart.setAssignmentPartSeqNo(currSeqno);
			currSeqno++;
			nextTargetPart.setPlannedStart(new Date(nextTargetPart.getPlannedStart().getTime() + targetOffset));
		}

		sourceAssignment.setActiveFlag(Boolean.FALSE);

		/* LOGON-6183:
		 * Delete any planning truck info in the target assignment. Otherwise we end up with
		 * an assignment which has a planning truck set, but this planning truck was only used
		 * for one of the orders of the source assignment.
		 * This can lead to weird things depending on the order in which the orders are
		 * deallocated from the assignment; details are in the ticket.
		 */
		LOG.debug("Removing any pre-planning information in the assignment and assignment parts");
		targetAssignment.setPlanningTruck(null);
		for (AssignmentPartEntity ape : targetAssignment.getAssignmentParts()) {
			ape.setPrePlannedStart(null);
		}
		// We also have to set it in the parts of the sourceAssignment, since they have not
		// yet been added to the assignmentPartList of the targetAssignment. Hibernate will
		// do that somehow automatically.
		for (AssignmentPartEntity ape : sourceAssignment.getAssignmentParts()) {
			ape.setPrePlannedStart(null);
		}

		targetAssignment.setAssignTrStatus(referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
				Constant.ASSIGN_STATUS_TR_NOT_TRANSMITTED));
		for (LoadEntity loadToRemove : loadsToRemove) {
			getDaoProvider().getLoadEntityDao().remove(loadToRemove);
		}

		return targetAssignment;
	}

	/**
	 * It gives back a LoadEntity instance if the assignmentId, the sap_material, net_quantity_uom and production_plant is matching
	 * @param assignmentId
	 * @param sourceLoad
	 * @return loadEntity or null
	 */
	private LoadEntity findLoad(Long assignmentId, LoadEntity sourceLoad) {
		@SuppressWarnings("unchecked")
		List<AssignmentPartEntity> matchingLoadParts = getDaoProvider().getDirectJpaDao().find(
				"SELECT a FROM " + AssignmentPartEntity.class.getSimpleName() + " a " + "WHERE a."
						+ AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ASSIGNMENT_ID + " = ?1"
						+ " AND a." + AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_SAP_MATERIAL + " = ?2"
						+ " AND a." + AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_NET_QUANTITY_UOM + " = ?3"
						+ " AND a." + AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_PRODUCTION_PLANT + " = ?4",
				assignmentId, sourceLoad.getSapMaterial(), sourceLoad.getNetQuantityUom(),
				sourceLoad.getProductionPlant());
		if (matchingLoadParts == null || matchingLoadParts.size() == 0) {
			return null;
		}
		LoadEntity load = matchingLoadParts.get(0).getLoad();
		return load;
	}

	/**
	 * It sets the sourceLoad's following fields into targetLoad
	 * targetLoad.setChamberNumber(sourceLoad.getChamberNumber());
	 *	-	loadedFromLoad
	 *	-	loadingTs
	 *	-	netQuantityUom
	 *	-	netQuantity
	 *	-	loadOrderId
	 *	-	tareWeightT
	 *
	 * @param targetLoad
	 * @param sourceLoad
	 * @return LoadEntity
	 */
	private LoadEntity mergeLoad(LoadEntity targetLoad, LoadEntity sourceLoad) {
		targetLoad.setChamberNumber(sourceLoad.getChamberNumber());
		targetLoad.setLoadedFromLoad(sourceLoad.getLoadedFromLoad());
		targetLoad.setLoadingTs(sourceLoad.getLoadingTs());
		targetLoad.setNetQuantityUom(sourceLoad.getNetQuantityUom());
		targetLoad.setNetQuantity(sourceLoad.getNetQuantity());
		targetLoad.setLoadOrderId(sourceLoad.getLoadOrderId());
		targetLoad.setTareWeightT(sourceLoad.getTareWeightT());
		updateGrossWeightT(targetLoad, null);
		return targetLoad;
	}

	/**
	 * It gives back an assignmentPart's list in seq. order regarding the given assignmentId
	 * @param assignmentId
	 * @return List<AssignmentPartEntity>
	 */
	protected List<AssignmentPartEntity> getAssignmentPartsInSeqOrder(Long assignmentId) {
		ReadWriteDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> assignmentPartFilter = dao.createFilter();
		assignmentPartFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, assignmentId));
		assignmentPartFilter.addExpression(Order.asc(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));
		List<AssignmentPartEntity> partList = dao.find(assignmentPartFilter);
		return partList;
	}

	/**
	 * It gives back the number of chambers for the given truck. For silo trucks 1 or 2, 0 for other truck types.
	 * @param truck
	 * @return number of chambers.
	 */
	private int getNumberOfChambersForTruck(final TruckEntity truck) {
		final int numberOfChambers;
		if (truck == null) {
			// Non-truck assignment
			numberOfChambers = 0;
		}
		else if (truck.getTruckTransportCat().getTruckTransportCatCd().longValue() == Constant.TKATEGORIE_SILO) {
			numberOfChambers = truck.getNrChambers().intValue();
		}
		else if (truck.getTruckTransportCat().getTruckTransportCatCd().longValue() == Constant.TKATEGORIE_TRAILER) {
			TrailerEntity trailer = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), false);
			if (trailer != null) {
				numberOfChambers = trailer.getNrChambers().intValue();
			}
			else {
				numberOfChambers = 0;
			}
		}
		else {
			numberOfChambers = 0;
		}
		return numberOfChambers;
	}

	/**
	 * It sends a cancel message to the LDS with the given assignemntId and the shippingpoint of the first order from the list
	 * @param assignmentId
	 * @param orders
	 */
	private void cancelAssignmentOnWeighSystem(Long assignmentId, final List<OrderEntity> orders) {
		OrderEntity firstOrder = orders.get(0);
		ShippingpointEntity shippingpoint = firstOrder.getShippingpoint();
		Long shippingpointId = shippingpoint.getShippingpointId();
		orderChangeService.cancelOnWeighSystem(assignmentId, shippingpointId);
	}

	/**
	 * Returns true if orderItem is exists for the given loadId and orderId
	 * @param loadId
	 * @param orderId
	 * @return boolean
	 */
	private boolean existsOrderItem(Long loadId, long orderId) {
		ReadWriteDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
		Filter<OrderItemEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD_ID, loadId));
		filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_ORDER_ID, orderId));
		return dao.count(filter) > 0;
	}

	/**
	 * Updates the distance attribute of the {@link AssignmentPartEntity} having the given id
	 * @param assignmentPartId
	 * @param distance
	 */
	public void correctAssignmentPartDistance(Long assignmentPartId, Long distance) {
		ReadWriteDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_ID, assignmentPartId));
		AssignmentPartEntity ap = dao.findSingle(filter);
		if (ap == null) {
			LOG.debug("AssignmentPartEntity with id: [" + assignmentPartId + "] cannot be found!");
			return;
		}
		ap.setTransportDistanceM(distance);
	}

	/**
	 * corrects the duration of assignment part withs the givenh seqno for the given
	 * assignemnt to the given duration (multiplied with correction factor, if available.
	 * @param assiggnmentId
	 * @param seqNo
	 * @param durationMin new duration in minutes
	 */
	public List<AssignmentPartEntity> correctAssignmentPartDuration(Long assiggnmentId, Long seqNo, long durationMin) {
		ReadWriteDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		Filter<AssignmentPartEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, assiggnmentId));
		filter.addExpression(Restrictions.ge(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO, seqNo));
		filter.addExpression(Order.asc(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));
		List<AssignmentPartEntity> assignmentParts = dao.find(filter);
		if (assignmentParts == null) {
			return null;
		}

		Iterator<AssignmentPartEntity> parts = assignmentParts.iterator();
		AssignmentPartEntity referencedPart = parts.hasNext() ? parts.next() : null;
		AssignmentPartEntity nextPart = parts.hasNext() ? parts.next() : null;
		if (referencedPart != null && nextPart != null) {
			long diff = nextPart.getPlannedStart().getTime() - referencedPart.getPlannedStart().getTime() - durationMin
					* 60000;
			nextPart.setPlannedStart(new Date(nextPart.getPlannedStart().getTime() - diff));
			while (parts.hasNext()) {
				nextPart = parts.next();
				nextPart.setPlannedStart(new Date(nextPart.getPlannedStart().getTime() - diff));
			}
		}
		return assignmentParts;
	}

	/**
	 * Sets Stellzeit of the given assingment id to the next possible stellzeit according to
	 * Railway timetable
	 * @param assiggnmentId
	 */
	public void adjustStellzeit(Long assignmentId) {
		AssignmentEntity ass = getDaoProvider().getAssignmentEntityDao().findById(assignmentId.longValue());

		if ((ass == null)
				|| (ass.getAssignTrStatus().getAssignTrStatusCd().longValue() == Constant.ASSIGN_STATUS_TR_TRANSMITTED)) {
			return;
		}

		OrderEntity order = null;

		Iterator<AssignmentPartEntity> i = ass.getAssignmentParts().iterator();
		while (i.hasNext()) {
			AssignmentPartEntity ape = (AssignmentPartEntity) i.next();
			if (ape.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
				if ((order != null) || (ape.getOrder() == null)) {
					return;
				}
				long dt = ape.getOrder().getDeliveryType().getDeliveryTypeCd().longValue();
				if ((dt != Constant.DELIVERY_TYPE_BIMODAL_RAIL) && (dt != Constant.DELIVERY_TYPE_RAIL_DIRECT)) {
					return;
				}
				order = ape.getOrder();
			}
		}

		if (order == null) {
			return;
		}

		Date[] dates = getDepartureArrivalDate(order.getShippingpoint().getRailwayStation().getRailwayStationId(),
				order.getShiptoSapBp().getRailwayStation().getRailwayStationId(), new Date());

		if (dates == null) {
			return;
		}

		i = ass.getAssignmentParts().iterator();
		while (i.hasNext()) {
			AssignmentPartEntity ape = (AssignmentPartEntity) i.next();
			int cd = ape.getAssignmentPartType().getAssignmentPartTypeCd().intValue();
			switch (cd) {
			case (int) Constant.ASSIGNMENT_PART_TYPE_LOAD: // fallthrough
			case (int) Constant.ASSIGNMENT_PART_TYPE_TO:
				ape.setPlannedStart(dates[0]);
				break;
			case (int) Constant.ASSIGNMENT_PART_TYPE_UNLOAD: // fallthrough
			case (int) Constant.ASSIGNMENT_PART_TYPE_FINISHED:
				ape.setPlannedStart(dates[1]);
				break;
			default:
				break; // shouldn't happen

			}
		}
	}

	/**
	 * Returns a list of cointainers which are possible match for the order item in a meaning
	 * that it contains the same material, and the last known shippingpoint is the order item's
	 * shippingpoint. Used by pda and shipping terminal also.
	 * @param orderItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AssignmentPartEntity> getContainers(OrderItemEntity orderItem) {
		//NORMALLY THIS QUERY WOULD LIKE THE FOLLOWING, BUT THERE IS AN EXCEPTION IF WE USE THAT - THEREFORE USING DIRECTJPA DAO UNTIL THIS ISSUE IS SOLVED
		//THE FILTER COULD ALSO BE USED LIKE THIS: notLoadedItemsFilter.addExpression(Restrictions.neq("x." + OrderItemEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOAD_ID, load.getLoadId()));
		//(adding x. in front of the alias, as far the Entity is named as x in daosupport)
		//			[junit] Caused by: java.lang.NullPointerException
		//			[junit]	 at org.hibernate.hql.ast.tree.FromClause.findIntendedAliasedFromElementBasedOnCrazyJPARequirements(FromClause.java:120)

		/*			List containers = new ArrayList();
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_CONTAINER + "." + ContainerEntity.ATTR_LAST_KNOWN_SHIPPINGPOINT, orderItem.getOrder().getShippingpoint()));
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_PRODUCTION_PLANT, orderItem.getProductionPlant()));
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_SAP_MATERIAL, orderItem.getSapMaterial()));
		filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_CONTAINER + "." + ContainerEntity.ATTR_CURRENT_WAGON));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_CONTAINER + "." + ContainerEntity.ATTR_CURRENT_TRUCK));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_CONTAINER + "." + ContainerEntity.ATTR_CURRENT_TRAILER));
		List loads = getDaoProvider().getLoadEntityDao().find(filter);
		// This is a list with < 20 elements, so this should be ok (instead of a view)
		for (Iterator it = loads.iterator();it.hasNext();) {
			LoadEntity load = (LoadEntity)it.next();
			if (!containers.contains(load.getContainer())) {
				containers.add(load.getContainer());
			}
		} */
		//			on the plus side, the direct JPA query allows to skip the looping on the result list
		String query = "SELECT x." + LoadEntity.ATTR_CONTAINER + " FROM " + LoadEntity.class.getSimpleName()
				+ " x where " + " x." + LoadEntity.ATTR_CONTAINER + "." + ContainerEntity.ATTR_LAST_KNOWN_SHIPPINGPOINT
				+ " = ?1 and " + " x." + LoadEntity.ATTR_PRODUCTION_PLANT + " = ?2 and" + " x."
				+ LoadEntity.ATTR_SAP_MATERIAL + " = ?3 and" + " x." + LoadEntity.ATTR_LOADING_TS + " is not null and "
				+ " x." + LoadEntity.ATTR_UNLOADING_TS + " is null and " + " x." + LoadEntity.ATTR_CONTAINER + "."
				+ ContainerEntity.ATTR_CURRENT_WAGON + " is null and " + " x." + LoadEntity.ATTR_CONTAINER + "."
				+ ContainerEntity.ATTR_CURRENT_TRUCK + " is null and " + " x." + LoadEntity.ATTR_CONTAINER + "."
				+ ContainerEntity.ATTR_CURRENT_TRAILER + " is null";
		return getDaoProvider().getDirectJpaDao().find(query, orderItem.getOrder().getShippingpoint(),
				orderItem.getProductionPlant(), orderItem.getSapMaterial());
	}

	/**
	 * Updates the truck load from the container load. The assignment part is expected to be a load assignment
	 * part. Used by pda, and terminal screen for container terminal type shipping point.
	 * @param bizevent
	 * @param part
	 * @param container
	 * @return
	 */
	public LoadEntity updateTruckLoadFromContainerLoad(BizeventEntity bizevent, AssignmentPartEntity part,
			ContainerEntity container) {
		ReadWriteDao<LoadEntity> loadEntityDao = getDaoProvider().getLoadEntityDao();
		Filter<LoadEntity> filter = loadEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_CONTAINER, container));
		filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
		LoadEntity containerLoad = loadEntityDao.findSingle(filter);
		if (containerLoad == null) {
			throw new LogonException("Unknown material in container " + container.getContainerName(),
					Reason.RES_ERROR_UNKNOWN_MATERIAL);
		}
		LoadEntity truckLoad = part.getLoad();
		if ((!truckLoad.getSapMaterial().getSapMaterialId().equals(containerLoad.getSapMaterial().getSapMaterialId()))
				|| (!truckLoad.getProductionPlant().getPlantCd().equals(containerLoad.getProductionPlant().getPlantCd()))) {
			throw new LogonException("Material mismatch for in container " + container.getContainerName(),
					Reason.RES_ERROR_MATERIAL_MISMATCH);
		}
		// Copy container's load info into the truck's load
		truckLoad.setLoadedFromLoad(containerLoad);
		truckLoad.setContainer(container);
		truckLoad.setNetQuantityUom(containerLoad.getNetQuantityUom());
		truckLoad.setNetQuantity(containerLoad.getNetQuantity());
		truckLoad.setAccurateQuantitiesFlag(containerLoad.getAccurateQuantitiesFlag());
		truckLoad.setWsTareQuitWarnFlag(containerLoad.getWsTareQuitWarnFlag());
		truckLoad.setActualChromatReductionMonth(containerLoad.getActualChromatReductionMonth());
		truckLoad.setWsSiloName(containerLoad.getWsSiloName());
		truckLoad.setWsSiloNo(containerLoad.getWsSiloNo());
		truckLoad.setWsSampleNo(containerLoad.getWsSampleNo());
		updateGrossWeightT(truckLoad, null);

		OrderEntity order = null;
		for (AssignmentPartEntity ap : part.getAssignment().getAssignmentParts()) {
			if (ap.getOrder() != null) {
				if (order != null) {
					// more than one order found in assignment
					throw new LogonException("Multiple orders found in truck's assignment",
							Reason.RES_ERROR_UNSPECIFIED);
				}
				order = ap.getOrder();
			}
		}
		if (order == null) {
			throw new LogonException("No order found in truck's assignment", Reason.RES_ERROR_UNSPECIFIED);
		}
		List<OrderItemEntity> orderItems = (List<OrderItemEntity>) LogonPackageHandlingUtil.getMaterialItems(order);
		OrderItemEntity orderItem = LogonUtil.getBulkDumpOrPowderOrderItem(orderItems);
		if (orderItem == null) {
			throw new LogonException("Cannot determine order item for order", Reason.RES_ERROR_UNSPECIFIED);
		}
		if (!orderItem.getSapMaterial().getSapMaterialId().equals(containerLoad.getSapMaterial().getSapMaterialId())) {
			throw new LogonException("Material mismatch for in container " + container.getContainerName(),
					Reason.RES_ERROR_MATERIAL_MISMATCH);
		}

		// scanContainer success, update truck composition
		truckAdminService.updateTruck(bizevent.getTruck(), bizevent.getTrailer(), container, true);

		PackageTypeBsEntity logonPackageType = truckLoad.getSapMaterial().getMaterialPackage().getLogonPackageType();
		if (logonPackageType != null) {
			boolean validComposition = LogonUtil.isTruckCompositionValidForMaterialPackage(
					logonPackageType.getPackageTypeCd(), bizevent.getTruck(), bizevent.getTrailer(), container);
			if (!validComposition) {
				throw new LogonException("Invalid truck composition", Reason.RES_ERROR_INVALID_TRUCK_COMPOSITION);
			}
		}
		return containerLoad;
	}

	/**
	 * formats and sends email if all needed data is specified in OrderRepEntity
	 * @param order OrderEntity
	 * @param orderRep OrderEntityRep
	 * @param locale
	 */
	public void sendOrderRepEmail(OrderEntity order, OrderRepEntity orderRep, Locale locale) {
		sendOrderRepEmail(order, orderRep, locale, false);
	}

	/**
	 * formats and sends email to railadmin if all needed data is specified in OrderRepEntity
	 * @param order OrderEntity
	 * @param orderRep OrderEntityRep
	 * @param locale
	 */
	public void sendOrderRepEmailToRailAdmin(OrderEntity order, OrderRepEntity orderRep, Locale locale) {
		sendOrderRepEmail(order, orderRep, locale, true);
	}

	public void sendOrderRepEmail(OrderEntity order, OrderRepEntity orderRep, Locale locale, boolean toRailAdmin) {
		sendOrderRepEmail(order, orderRep, locale, toRailAdmin, false);
	}

	/**
	 * formats and sends email if all needed data is specified in OrderRepEntity.<br />
	 * If to AND cc AND bcc are null, values from orderRep will be used
	 * @param order OrderEntity
	 * @param orderRep OrderEntityRep
	 * @param locale
	 * @param toRailAdmin
	 * @param ignoreExceptions Tell to the email sender service to not propagate the exceptions to us
	 */
	public void sendOrderRepEmail(OrderEntity order, OrderRepEntity orderRep, Locale locale, boolean toRailAdmin,
			boolean ignoreExceptions) {
		locale = (Locale) ObjectUtils.defaultIfNull(locale, LangUtil.getOrderLocale(order));

		final WagonEntity wagon = order.getDeliveredWagon();
		final String wagonNo;
		final String wagonUicClassCd;
		if (wagon != null) {
			wagonNo = wagon.getWagonNo();
			wagonUicClassCd = wagon.getWagonType().getWagonUicClassCd();
		}
		else {
			wagonNo = "-";
			wagonUicClassCd = "-";
		}

		final OrderItemEntity oi = order.getOrderItems().iterator().next();
		final String loadedQuantity;
		if (oi.getLoadedQuantity() != null) {
			if (oi.getLoadedUom() != null) {
				final UomTxEntity tx = referentialDataService.findTxEntity(UomTxEntity.class, locale,
						oi.getLoadedUom().getUomCd());
				loadedQuantity = oi.getLoadedQuantity().toString() + " " + tx.getTxt();
			}
			else {
				loadedQuantity = oi.getLoadedQuantity().toString();
			}
		}
		else {
			loadedQuantity = "-";
		}

		// get material description
		final SapMaterialDescriptionEntity matDesc = orderDetailService.loadMaterialDesc(
				oi.getSapMaterial().getSapMaterialId(), locale.getLanguage());

		// transport
		final String depRailwayStationName;
		final String depRailwayStationNo;
		if (order.getActualDepRailwayStation() != null) {
			depRailwayStationName = order.getActualDepRailwayStation().getRailwayStationName();
			depRailwayStationNo = order.getActualDepRailwayStation().getRailwayStationNo();
		}
		else {
			depRailwayStationName = "-";
			depRailwayStationNo = "-";
		}

		final RailwayStationEntity arrRailwayStation = order.getShiptoSapBp().getRailwayStation();
		final String arrRailwayStationName;
		final String arrRailwayStationNo;
		if (arrRailwayStation != null) {
			arrRailwayStationName = arrRailwayStation.getRailwayStationName();
			arrRailwayStationNo = arrRailwayStation.getRailwayStationNo();
		}
		else {
			arrRailwayStationName = "-";
			arrRailwayStationNo = "-";
		}

		final String repMaintTypes;
		if ((orderRep.getOrderRepMaintTypes() != null) && (!orderRep.getOrderRepMaintTypes().isEmpty())) {
			MaintTypeTxEntity tx;
			final Collection<String> sb = new LinkedList<String>();
			for (OrderRepMaintTypeEntity oRep : orderRep.getOrderRepMaintTypes()) {
				tx = referentialDataService.findTxEntity(MaintTypeTxEntity.class, locale, oRep.getMaintType());
				sb.add(String.format(" %s - %s%n", oRep.getMaintType().getMaintTypeCd(), tx.getTxt()));
			}
			repMaintTypes = StringUtils.join(sb, null);
		}
		else {
			repMaintTypes = "-";
		}

		final String damageCodes;
		if ((orderRep.getDamageCdList() != null) && (!orderRep.getDamageCdList().isEmpty())) {
			DamagecodeTxEntity tx;
			final Collection<String> sb = new LinkedList<String>();
			for (OrderRepDmgcodeTypeEntity oRep : orderRep.getDamageCdList()) {
				tx = referentialDataService.findTxEntity(DamagecodeTxEntity.class, locale, oRep.getDmgcodeType());
				sb.add(String.format(" %s - %s%n", tx.getDamagecodeTxt(), tx.getTxt()));
			}
			damageCodes = StringUtils.join(sb, null);
		}
		else {
			damageCodes = "-";
		}

		// # 0=Repair maintenance types, 1=Damage codes
		final String repair = pdfGeneratorService.formatText("special.rail.order.email.rep", locale, new Object[] {
				repMaintTypes, damageCodes });

		final String offerRequired;
		if (BooleanUtils.isTrue(orderRep.getOfferDesiredFlag())) {
			offerRequired = pdfGeneratorService.formatText("special.rail.order.email.offer", locale, new Object[] {});
		}
		else {
			offerRequired = "";
		}

		final String contactPerson;
		final String orderCreationUser;
		final Long orderCreationUserId = order.getCtlCreUid();

		if (orderCreationUserId != Constant.SYSTEM_USER_ID) {

			contactPerson = StringUtils.defaultString(order.getContactPersonTxt(), "-");

			UserEntity user = userStore.findById(orderCreationUserId);
			PersonEntity person = user.getPerson();

			String personFirstName = StringUtils.defaultString(person.getFirstname());
			String personName = StringUtils.defaultString(person.getName());

			orderCreationUser = StringUtils.join(new String[] { personFirstName, personName }, ' ').trim();

		}
		else {
			contactPerson = "-";
			orderCreationUser = "-";
		}

		final SorgTxEntity sorgTx = referentialDataService.findTxEntity(SorgTxEntity.class, locale,
				order.getSarea().getSorg());

		// # 0=wagon number, 1=order number
		final String displayWagonNo = WagonNumberUtils.toDisplayNo(wagonNo);
		final String subject = pdfGeneratorService.formatText("special.rail.order.email.subj", locale, new Object[] {
				displayWagonNo, order.getOrderNumber().toString() });

		// # 0=Order number, 1=Wagon No, 2=Wagon UIC class cd
		// # 3=Loaded qty, 4=Material, 5=ShipTo name, 6=Dep. railway station name, 7=Dep. railway station number,
		// # 8=Arr. railway station name, 9=Dep. railway station number,
		// # 10=Repair Maintenance, 11=Remarks, 12=offer required, 13=Contact person, 14=Order Creation User,
		// # 15=Address name, 16=Adress country city 17=phone,
		final String message = pdfGeneratorService.formatText(
				"special.rail.order.email",
				locale,
				new Object[] {
						order.getOrderNumber().toString(),
						displayWagonNo,
						wagonUicClassCd,
						loadedQuantity,
						matDesc.getTxt(),
						StringUtils.join(new String[] { StringUtils.defaultString(order.getShiptoSapBp().getName1()),
								StringUtils.defaultString(order.getShiptoSapBp().getName2()) }, ' '),
						depRailwayStationName, depRailwayStationNo, arrRailwayStationName, arrRailwayStationNo, repair,
						StringUtils.defaultString(orderRep.getEmailAdditionalRemarks(), "-"), offerRequired,
						contactPerson, orderCreationUser, StringUtils.defaultString(sorgTx.getAddressName()),
						StringUtils.defaultString(order.getSarea().getSorg().getAddressCountrycity()),
						StringUtils.defaultString(sorgTx.getLinePhone()) });

		final String[] to;
		final String[] cc;
		final String[] bcc;
		if (toRailAdmin) {
			to = new String[] { getConfigBean().getMailRailadmin() };
			cc = new String[] {};
			bcc = new String[] {};
		}
		else {
			to = StringUtils.split(orderRep.getEmailToAdresses(), ',');
			cc = StringUtils.split(orderRep.getEmailCcAdresses(), ',');
			bcc = StringUtils.split(orderRep.getEmailBccAdresses(), ',');
		}

		if ((to.length > 0) || (cc.length > 0)) {

			final Collection<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();
			if (!orderRep.getOrderRepAttachments().isEmpty()) {
				MimeBodyPart part;
				for (OrderRepAttEntity att : orderRep.getOrderRepAttachments()) {
					if (att.getAttachmentType().getAttachmentTypeCd().equals(Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL)) {
						part = emailService.createMimeBodyPartAttachment(att.getData(), att.getDataMimeType(),
								att.getAttachmentName());
						attachments.add(part);
					}
				}
			}

			final MailPropertiesConfiguration cfg = new MailPropertiesConfiguration(
					MailPropertiesConfiguration.ConfigurationType.REP_ORDER, order != null ? order.getSarea() : null);

			// EmailServiceBean will ignore null attachments
			MimeBodyPart[] attachmentsArray = null;
			if (!attachments.isEmpty()) {
				attachmentsArray = attachments.toArray(new MimeBodyPart[attachments.size()]);
			}

			if (ignoreExceptions) {
				emailService.sendButIgnoreExceptions(to, cc, bcc, subject, message, cfg, attachmentsArray);
			}
			else {
				emailService.send(to, cc, bcc, subject, message, cfg, attachmentsArray);
			}

			orderRep.setEmailLastSentDat(new Date());
		}
	}

	/**
	 *
	 * Delegetates the merge to OrderEntityDao after ensuring that the Entity is valid
	 */
	public OrderEntity mergeOrder(OrderEntity order) {
		ReadWriteDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();
		calculateFreightCost(order, order.getOrderItems());
		checkOrderPrereq(order);
		return dao.merge(order);
	}

	/**
	 * Delegetates the persist to OrderEntityDao after ensuring that the Entity is valid
	 */
	private OrderEntity persistOrder(OrderEntity order) {
		ReadWriteDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();
		checkOrderPrereq(order);
		return dao.persist(order);
	}

	private void checkOrderPrereq(OrderEntity order) {
		// --- ADN53172: Ignore the checkbox values for mail/fax/sms if the corresponding values are empty ---
		if (StringUtils.isEmpty(order.getOrderConfEmailAdresses())) {
			order.setOrderConfEmailFlag(false);
			order.setLoadConfEmailFlag(false);
			order.setDelivnoteEmailFlag(false);
		}
		if (StringUtils.isEmpty(order.getOrderConfFaxNumber())) {
			order.setOrderConfFaxFlag(false);
			order.setLoadConfFaxFlag(false);
			order.setDelivnoteFaxFlag(false);
		}
		if (StringUtils.isEmpty(order.getOrderConfSmsNumber())) {
			order.setOrderConfSmsFlag(false);
			order.setLoadConfSmsFlag(false);
		}

		// Generate order validation code for FENICS FBNL. Even though this is
		// only necessary for orders that will be send to FENICS, it will be
		// generated for all orders. For example: a Fenics LP could be created
		// after the order was created.
		OrderUtils.generateOrderValidationCode(order);
	}

	/**
	 * Updates the load assignment part's shippingpoint to the bag stock shippingpoint, and stores the distance and
	 * duration parameters on the order, as far it changes together with the location of the shippingpoint.
	 * @param order
	 */
	public void setLoadingAtCarrierStockShippingpoint(Long orderId, int trDistance, int trDuration) {
		ReadWriteDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();
		OrderEntity order = dao.findById(orderId);

		order.setTransportDistanceM(Long.valueOf(trDistance));
		order.setTransportDurationMin(Long.valueOf(trDuration));
		order.setTransportDurPlanGoogleMin(Long.valueOf(trDuration));
		// FIXME (emoke Nov 30, 2011): Split carrier to executional and commercial carrier! executional is not handled.
		CarrierEntity carrier = order.getCommCarrier();
		LoadingpointEntity lp = getSingleLoadingpointForShippingpoint(carrier.getBagStockShippingpoint());
		List<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
		for (OrderItemEntity item : orderItems) {
			if ((item.getLoad() != null) && (item.getLoad().getLoadingpoint() != null)) {
				item.getLoad().setLoadingpoint(lp);
			}
		}
		AssignmentPartEntity upart = orderChangeService.findUnloadPartForOrder(order);
		AssignmentEntity assignment = findAssignmentById(upart.getAssignment().getAssignmentId());
		for (AssignmentPartEntity part : assignment.getAssignmentParts()) {
			if (part.getShippingpoint() != null
					&& part.getShippingpoint().getShippingpointId().equals(
							order.getShippingpoint().getShippingpointId())) {
				//replace the order shippingpoint with the bag stock shippingpoint!
				part.setShippingpoint(carrier.getBagStockShippingpoint());
			}
		}
	}

	/**
	 * Returns the bag stock loadingpoint for the carrier. The rule is if there is one loadingpoint,
	 * then it will be used, otherwise null is returned.
	 * @param carrier
	 * @return
	 */
	public LoadingpointEntity getSingleLoadingpointForShippingpoint(ShippingpointEntity shippingpoint) {
		ReadWriteDao<LoadingpointEntity> dao1 = getDaoProvider().getLoadingpointEntityDao();
		Filter<LoadingpointEntity> filter = dao1.createFilter();
		filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_SHIPPINGPOINT_ID,
				shippingpoint.getShippingpointId()));
		List<LoadingpointEntity> loadingPoints = dao1.find(filter);
		if (loadingPoints.isEmpty() || loadingPoints.size() > 1) {
			return null;
		}
		LoadingpointEntity lp = loadingPoints.get(0);
		return lp;
	}

	/**
	 * Returns the bag stock loadingpoint for the carrier. The rule is if there is one loadingpoint,
	 * then it will be used, otherwise null is returned.
	 * @param carrier
	 * @return
	 */
	public LoadingpointEntity getSingleLoadingpointForShippingpointWithRFId(ShippingpointEntity shippingpoint) {
		ReadWriteDao<LoadingpointEntity> dao1 = getDaoProvider().getLoadingpointEntityDao();
		Filter<LoadingpointEntity> filter = dao1.createFilter();
		filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_SHIPPINGPOINT_ID,
				shippingpoint.getShippingpointId()));
		List<LoadingpointEntity> loadingPoints = dao1.find(filter);
		if (loadingPoints.isEmpty() || loadingPoints.size() > 1) {
			return null;
		}
		LoadingpointEntity loadingPoint = loadingPoints.get(0);
		RfidobjectEntity rfidObject = loadingPoint.getRfidobject();
		if (rfidObject != null) {
			rfidObject.getRfids().size();
		}
		return loadingPoint;
	}

	/**
	 * 1.) Deletes the orders from deleteOrderIdList
	 * 2.) Sets the state of the referenced {@link CustorderEntity} to Closed
	 * @param deleteOrderIdList - orders to be deleted
	 * @param custorderId - reference for the {@link CustorderEntity} we would like to close
	 */
	public CustorderLifecycleReturnCode cascadeCloseCustomerOrder(Map deleteOrderMap, Long custorderId) {
		// TODO (balakirev Aug 31, 2012): do we need to check if custorder closable first???
		final CustorderEntity custorder = fetchCustorderById(custorderId);
		final Long stateBefore = custorder.getCustorderStatus().getCustorderStatusCd();
		if (stateBefore.equals(Constant.CUSTORDER_STATUS_FINISHED)) {
			return CustorderLifecycleReturnCode.ERR_OPEN_TO_FINISHED_NOT_ALL_SDOS_CLOSED;
		}
		for (OrderEntity order : ((Map<Long, OrderEntity>) deleteOrderMap).values()) {
			deleteOrder(order.getOrderId(), "Customer Order has been manually closed");
		}
		final Long stateAfter = custorder.getCustorderStatus().getCustorderStatusCd();
		if (stateAfter.equals(Constant.CUSTORDER_STATUS_FINISHED)) {
			return CustorderLifecycleReturnCode.SUCCESS;
		}
		return setCustorderToFinishedManually(custorder);
	}

	public OrderEntity deleteWebOrder(Long orderId, String deletionReason) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter<OrderEntity> f = orderDao.createFilter();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_ID, orderId));
		OrderEntity order = orderDao.findSingle(f);
		if (!OrderUtils.isWebOrderDeletable(order.getOrderStatus().getOrderStatusCd(),
				order.getDeliveryType().getDeliveryTypeCd())) {
			throw new LogonException(Reason.RES_ORDER_WRONG_STATUS);
		}
		return startOrderDelete(order, deletionReason);
	}

	public OrderEntity deleteOrder(Long orderId, String deletionReason) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter<OrderEntity> f = new FilterImpl<OrderEntity>();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_ID, orderId));
		OrderEntity order = orderDao.findSingle(f);
		return startOrderDelete(order, deletionReason);
	}

	private OrderEntity startOrderDelete(OrderEntity order, String deletionReason) {
		LOG.info("Start deleting order: " + order.getOrderId());

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DELETED);
		//initialize ebent_ts to "now"/systimestamp"
		event.setEventTs(new Date());
		event.setOrder(order);
		event.setDescription(deletionReason);

		monitoringTablesService.processBizevent(event);

		Long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		if (orderStatusCd == Constant.ORDER_STATUS_FINISHED || orderStatusCd.equals(Constant.ORDER_STATUS_DELIVERED)) {
			//only special delete is possible
			order = deleteClosedOrder(order.getOrderId());
			if (kafkaPublisherService != null) {
				kafkaPublisherService.publishCancelledOrder(order);
			}
			return order;
		}

		deleteOrder(event);

		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		order = orderDao.findById(order.getOrderId());
		if (kafkaPublisherService != null) {
			kafkaPublisherService.publishCancelledOrder(order);
		}
		return order;
	}

	/**
	 * Update the hold back return order flag of the order.
	 * @param helpForm
	 * @param holdBackReOrderFlag
	 */
	public void setOrderRailHoldBackReOrderFlag(Long orderId, Boolean railHoldBackReOrderFlag) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		OrderEntity order = orderDao.findById(orderId);
		if (railHoldBackReOrderFlag != null && railHoldBackReOrderFlag.booleanValue()) {
			order.setRailHoldBackReOrderFlag(Boolean.TRUE);
		}
		else {
			order.setRailHoldBackReOrderFlag(null);
		}
		mergeOrder(order);
	}

	public CustorderEntity fetchCustorderById(Long custorderId) {
		ReadWriteDao<CustorderEntity> custorderDao = getDaoProvider().getCustorderEntityDao();
		return custorderDao.findById(custorderId);
	}

	/**
	 * Must be called after a change in the status of the given Single-Delivery Order to check if
	 * the order is associated with a Customer Order, and if yes, whether the status change should trigger
	 * changing the Customer Order status to finished. If yes, it does the change.
	 *
	 * <p>This method should only be called with an OrderEntity that has the latest CustomerEntity
	 * information, otherwise you will end up with inconsistencies in the DB! (Because the customer order
	 * rarely changes, the worst case scenario is when an open customer order is seen as finished and
	 * therefore the automatic state change will not happen.)</p>
	 *
	 * @param order
	 */
	private void setCustorderToFinishedAutomatically(OrderEntity order) {
		CustorderEntity custorder = order.getCustorder();
		if (custorder == null) {
			return;
		}

		final Long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		if (orderStatusCd != Constant.ORDER_STATUS_DELIVERED && orderStatusCd != Constant.ORDER_STATUS_FINISHED
				&& orderStatusCd != Constant.ORDER_STATUS_DELETED) {
			return;
		}

		final CustorderLifecycleReturnCode returnCode = isCustorderClosable(custorder, false);
		if (returnCode.isSuccess()) {
			custorder = setNewStatusForCustorder(custorder, Constant.CUSTORDER_STATUS_FINISHED);
			order.setCustorder(custorder);
		}
	}

	/**
	 * Checks if the Customer Order can be set to finished, and makes that change. This method is called
	 * by user request.
	 *
	 * @param custorderId
	 * @return an error code defined in CustorderLifecycleReturnCode
	 */
	public CustorderLifecycleReturnCode setCustorderToFinishedManually(CustorderEntity custorder) {
		final CustorderLifecycleReturnCode returnCode = isCustorderClosable(custorder, true);
		if (returnCode.isSuccess()) {
			setNewStatusForCustorder(custorder, Constant.CUSTORDER_STATUS_FINISHED);
		}
		return returnCode;
	}

	/**
	 * Common check that has to be done before setting a customer order to finished either manually or
	 * automatically.
	 *  1) only has Single-Delivery Orders in the state finished, delivered, or deleted,
	 *
	 * @param custorder
	 * @param manually true if the transition would be manual, false if automatic
	 * @return an error code defined in CustorderLifecycleReturnCode
	 */
	private CustorderLifecycleReturnCode isCustorderClosable(CustorderEntity custorder, boolean manually) {
		/* LOGON-5691 (marius/2012-10-24): Only open and new orders can be deleted. */
		final Long custorderStatus = custorder.getCustorderStatus().getCustorderStatusCd();
		if (custorderStatus != Constant.CUSTORDER_STATUS_OPEN && custorderStatus != Constant.CUSTORDER_STATUS_NEW) {
			return CustorderLifecycleReturnCode.ERR_OPEN_TO_FINISHED_NOT_OPEN;
		}

		/*
		 * 1) Check if we can find at least one SDO that is not "closed".
		 */
		final ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		final Filter<OrderEntity> orderFilter = orderDao.createFilter();
		orderFilter.addExpression(Restrictions.eq(OrderEntity.ATTR_CUSTORDER_ID, custorder.getCustorderId()));
		orderFilter.addExpression(Restrictions.not(Restrictions.in(OrderEntity.ATTR_ORDER_STATUS_CD, Arrays.asList(
				Constant.ORDER_STATUS_FINISHED, Constant.ORDER_STATUS_DELIVERED, Constant.ORDER_STATUS_DELETED))));

		final long foundOrdersNum = orderDao.count(orderFilter);
		if (foundOrdersNum > 0) {
			return CustorderLifecycleReturnCode.ERR_OPEN_TO_FINISHED_NOT_ALL_SDOS_CLOSED;
		}

		return CustorderLifecycleReturnCode.SUCCESS;
	}

	/**
	 * Set the customer order state to be finished. The caller has to make sure this method is not called
	 * before certain pre-conditions match.
	 * @param custorder - entity on which we would like to update the status
	 * @param newCustorderStatusCd - numerical representation of the desired new status code. <b>Please use Constant.CUSTORDER_STATUS_XXXX</b> to set this.
	 */
	private CustorderEntity setNewStatusForCustorder(CustorderEntity custorder, long newCustorderStatusCd) {
		CustorderStatusBsEntity newCustorderStatus = referentialDataService.findBsEntity(CustorderStatusBsEntity.class,
				newCustorderStatusCd);
		custorder.setCustorderStatus(newCustorderStatus);
		ReadWriteDao<CustorderEntity> custorderEntityDao = getDaoProvider().getCustorderEntityDao();
		custorder = custorderEntityDao.merge(custorder);
		return custorder;
	}

	/**
	 * Throws exception in case the combination of executing and commercial carrier
	 * is not valid.
	 *
	 * TODO: Later on partner functions are to be checked around or in this method. WARNING: when implementing this partner function change: make sure
	 * that they are not checked if the call comes from the weighing system!
	 *
	 * General rules:
	 * 1. if the commercial carrier is not set, setting the executing carrier can set the empty commercial carrier attribute, only in the following cases
	 * 1a. The carrier does not need prescheduling process
	 * 1b. The carrier is executing and commercial carrier also
	 * 2. it is not intended to overwrite an already set executing carrier. the flow is: executing carrier needs to reject first and then the executing carrier can be set
	 * 3. it is a must that the carrier set is marked as executing carrier
	 * 4. should not just remove the commercial carrier attribute if executing carrier is set, it should only happen with rejections
	 *
	 * If all these requirements are met, the executing carrier (and in case 1 also the commercial carrier) are going to be set
	 */
	public void setExecutingCarrier(OrderEntity order, CarrierEntity execCarrier) throws LogonException {
		try {
			if (execCarrier != null && order.getExecCarrier() != null
					&& !execCarrier.getCarrierId().equals(order.getExecCarrier().getCarrierId())) {
				throw new LogonException("Scheduled and actual truck belong to different carriers.",
						Reason.RES_ERROR_INVALID_CARRIER);
			}
			if (execCarrier != null && execCarrier.getSapVendor().getVnIsExecCarrier().equals(Boolean.FALSE)) {
				throw new LogonException("The carrier specified as executing carrier is not an executing carrier.",
						Reason.RES_ERROR_INVALID_CARRIER);
			}
			if (execCarrier != null && order.getCommCarrier() == null) {
				throw new LogonException("There is no commercial carrier specified, cannot set executing carrier.",
						Reason.RES_ERROR_INVALID_CARRIER);
			}
			order.setExecCarrier(execCarrier);
		}
		catch (LogonException e) {
			LOG.error("Executing carrier setting failed. Executing carrier to set="
					+ (execCarrier != null ? execCarrier.getCarrierId() : "null") + "; requires prescheduling="
					+ (execCarrier != null ? execCarrier.getCarrierRequiresPlanningFlag() : "null")
					+ ". [commercial carrier"
					+ (order.getCommCarrier() != null ? order.getCommCarrier().getCarrierId() : "null")
					+ "; executing carrier="
					+ (order.getExecCarrier() != null ? order.getExecCarrier().getCarrierId() : "null") + "; order id="
					+ order.getOrderId() + "; order number=" + order.getOrderNumber() + "]");
			throw e;
		}
	}

	/**
	 * Throws exception in case the commercial carrier specified is not a possible commercial carrier.
	 *
	 * TODO: Later on partner functions are to be checked around or in this method. WARNING: when implementing this partner function change: make sure
	 * that they are not checked if the call comes from the weighing system!
	 *
	 * General rules:
	 * 1. there should be no such case that the executing carrier is set, but the commercial is unknown
	 * 2. the commercial carrier can be overwritten if it's the case of LRQ 112.6 Auto Changing Carriers in Scheduling Screen (checked outside of this method).
	 * 3. only carriers marked as commercial carrier should be possible to set
	 *
	 * If all these requirements are met, the commercial carrier of the order is set.
	 */
	public void setCommercialCarrier(OrderEntity order, CarrierEntity commCarrier) throws LogonException {
		try {
			if (commCarrier != null && commCarrier.getSapVendor().getVnIsCommCarrier().equals(Boolean.FALSE)) {
				throw new LogonException("The carrier specified as commercial carrier is not a commercial carrier",
						Reason.RES_ERROR_INVALID_CARRIER);
			}
			if (commCarrier == null && order.getExecCarrier() != null) {
				throw new LogonException("Cannot delete the commercial carrier if the executing carrier is set.",
						Reason.RES_ERROR_INVALID_CARRIER);
			}
			order.setCommCarrier(commCarrier);

			if (commCarrier == null) {
				return;
			}

			BigDecimal freightCost = order.getFreightCost();
			CurrencyBsEntity fromCurrency = order.getFreightCostCurrency();
			CurrencyBsEntity toCurrency = commCarrier.getFreightRevenueCurrency();
			if (fromCurrency != null && toCurrency != null) {
				freightCost = LogonUtil.getCurrencyConvertedValue(fromCurrency, toCurrency, freightCost,
						getDaoProvider().getCurrentExchangeRateEntityDao());
				if (freightCost != null) {
					order.setFreightCost(freightCost);
					order.setFreightCostCurrency(toCurrency);
				}
			}
		}
		catch (LogonException e) {
			LOG.error("Commercial carrier setting failed. Commercial carrier to set="
					+ (commCarrier != null ? commCarrier.getCarrierId() : "null") + "; requires prescheduling="
					+ (commCarrier != null ? commCarrier.getCarrierRequiresPlanningFlag() : "null")
					+ ". [commercial carrier="
					+ (order.getCommCarrier() != null ? order.getCommCarrier().getCarrierId() : "null")
					+ "; executing carrier="
					+ (order.getExecCarrier() != null ? order.getExecCarrier().getCarrierId() : "null") + "; order id="
					+ order.getOrderId() + "; order number=" + order.getOrderNumber() + "]");
			throw e;
		}
	}

	protected ConfigBean getConfigBean() {
		return ConfigInitializer.inst.getBean();
	}

	/**
	 * See description {{@link #postProcessOrder(OrderEntity)} and additionally initializes sequence numbers.
	 * In certain cases there is a 'test' run for creating an order, which should NOT use up sequence values, but should
	 * do all the initialization the postprocess does.
	 *
	 * @param order
	 */
	private void finalInitOrder(OrderEntity order, final DeliverySettingSalesAreaMap salesAreaMap) {
		postProcessOrder(order, salesAreaMap);
		initSequences(order);
		resetCarrierNotes(order);
		notificationService.sendOrderConfirmation(order);
	}

	/**
	 * This method is supposed to do all that postprocessing of a single delivery order which
	 * is supposed to be done on each order. Certain operations like filling in mandatory but left empty
	 * attributes with default values is also supposed to be handled here.
	 *
	 * The method is currently under constant development, as order taking sources are rapidly changing, and in the beginning
	 * it was not clear what to move here what not.
	 *
	 * The current objective what to move here: those initializations, which are not shown in the order taking sources at all, and which
	 * are initialized the same from each order taking source (Call center/web order/LDS/pickup terminal.
	 * For example distribution destination is an attribute, which was initialized exactly the same way from all order taking source
	 * and which was never shown on the UI -> no one is interested in it when taking the order, but it needs to be set correctly.
	 *
	 * Certain code parts are moved here from the actions, and as it turns out that the action implementations for these parts is
	 * equivalent, we can step by step remove, or add if statements (see checking order creation type cd)
	 *
	 * For example: guess is that duration/distance fields is handled the very same way from the call center than from the web order,
	 * but currently (08.10.2012) only the web order is doing initialization for those, as no one had the time to check this equivalence
	 * in the call center. As soon as there will be enough time our job is to
	 * 1. analyse possible differences regarding these fields (which basically will almost for sure turn out to be not at all existing)
	 * 2. add to the if statement the creation type code for that part, and clean up the action
	 * like that we can get rid of that huge amount of code duplication in the different order taking sources.
	 *
	 * Example: autoadd material - for bag orders pallet order items are added automatically if master data is correctly set up
	 *
	 */
	public void postProcessOrder(OrderEntity order, DeliverySettingSalesAreaMap salesAreaMap) {
		final ShiptoEntity shipto = salesAreaMap.getShiptoSarea().getShipto();
		final CustorderEntity custorder = order.getCustorder();

		checkOrderPrereq(order);
		checkRoute(order);

		initFromCustorderFields(order);

		DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
		final Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();

		// initialize with false the Force to SAP flag that is taken into consideration when update_orders cron job occure
		order.setForceToSapFlag(Boolean.FALSE);
		if (LogonUtil.isPickupOrder(deliveryTypeCd) || LogonUtil.isReturnPickupOrder(deliveryTypeCd)) {
			//in case the order initialize the pool and carrier fields to the artificial pickup pool/carrier
			ReadWriteDao<PoolEntity> poolDao = getDaoProvider().getPoolEntityDao();
			PoolEntity pool = poolDao.findById(Constant.ARTIFICIAL_PICKUP_POOL_ID);
			ReadWriteDao<CarrierEntity> carrierDao = getDaoProvider().getCarrierEntityDao();
			CarrierEntity carrier = carrierDao.findById(Constant.ARTIFICIAL_PICKUP_CARRIER_ID);
			order.setPool(pool);
			order.setCommCarrier(carrier);
			order.setExecCarrier(carrier);
		}

		if (LogonUtil.isShipPickupOrder(deliveryTypeCd)) {
			//in case the order initialize the pool and carrier fields to the artificial pickup pool/carrier
			ReadWriteDao<PoolEntity> poolDao = getDaoProvider().getPoolEntityDao();
			PoolEntity pool = poolDao.findById(Constant.ARTIFICIAL_SHIP_PICKUP_POOL_ID);
			ReadWriteDao<CarrierEntity> carrierDao = getDaoProvider().getCarrierEntityDao();
			CarrierEntity carrier = carrierDao.findById(Constant.ARTIFICIAL_PICKUP_CARRIER_ID);
			order.setPool(pool);
			order.setCommCarrier(carrier);
			order.setExecCarrier(carrier);
			order.setTransportDurPlanGoogleMin(0L);
		}

		if (LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd)) {
			//in case the order initialize the pool and carrier fields to the artificial pickup pool/carrier
			ReadWriteDao<PoolEntity> poolDao = getDaoProvider().getPoolEntityDao();
			PoolEntity pool = poolDao.findById(Constant.ARTIFICIAL_BLOCKTRAIN_PICKUP_POOL_ID);
			ReadWriteDao<CarrierEntity> carrierDao = getDaoProvider().getCarrierEntityDao();
			CarrierEntity carrier = carrierDao.findById(Constant.ARTIFICIAL_PICKUP_CARRIER_ID);
			order.setPool(pool);
			order.setCommCarrier(carrier);
			order.setExecCarrier(carrier);
			order.setTransportDurPlanGoogleMin(0L);
		}

		if (order.getDistributionDest() == null) {
			ShiptoSareaEntity shiptoSalesArea = fetchShiptoSarea(order.getShiptoSapBp(), order.getSarea());
			DistributionDestBsEntity distributionDest = shiptoSalesArea.getDistributionDest();
			order.setDistributionDest(distributionDest);
		}

		//LRQ 095.5 Load confirmation SMS/Fax/Email see LOGON-7898 spec.
		//explicitly set the flags to false for the non supported cases! these cases are NOT supposed to receive SMS/email/fax currently
		if (order.getDistributionDest() == null
				|| (!order.getDistributionDest().getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_INBOUND)
						&& !order.getDistributionDest().getDistributionDestCd().equals(
								Constant.DISTRIBUTION_DEST_OUTBOUND)
						&& !order.getDistributionDest().getDistributionDestCd().equals(
								Constant.DISTRIBUTION_DEST_INTERUNIT_PROD) && !order.getDistributionDest().getDistributionDestCd().equals(
						Constant.DISTRIBUTION_DEST_INTERUNIT_DIST))
				|| order.getDeliveryType() == null
				|| (!order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_BIMODAL_ROAD)
						&& !order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_ROAD_DIRECT)
						&& !order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_RAIL_DIRECT) && !order.getDeliveryType().getDeliveryTypeCd().equals(
						Constant.DELIVERY_TYPE_PICKUP)) || LogonUtil.isLandfill(order)) {
			order.setLoadConfEmailFlag(Boolean.FALSE);
			order.setLoadConfSmsFlag(Boolean.FALSE);
			order.setLoadConfFaxFlag(Boolean.FALSE);
		}

		long orderCreationType = order.getOrderCreationType().getOrderCreationTypeCd().longValue();

		if (orderCreationType == Constant.ORDER_CREATION_TYPE_WEB_ORDER) {

			if (LogonUtil.isRailOrder(order.getDeliveryType().getDeliveryTypeCd())) {
				//for rail orders currently this is OK to do, even from other order taking sources it would be just fine, need to check
				order.setExecCarrier(order.getExecCarrier());

				order.setNoteRailwayDisp(shipto.getNoteRailwayDisp());
			}

			initializeDistanceAndDurationForRoadOrder(order, shipto);

			if (LogonUtil.isBimodalRailPartOrder(order.getDeliveryType().getDeliveryTypeCd())) {
				//FIXME (emoke Sep 21, 2012): Investigate for bimodal rail order in web order! LOGON-5424
				order.setTransportDurationMin(0L);
				order.setTransportDurPlanGoogleMin(0L);
				order.setTransportDistanceM(0L);
				order.setCommercialDistanceM(0L);
			}
		}

		if (orderTakingWithoutRouteInformation.contains(orderCreationType)){
			initializeDistanceAndDurationForRoadOrder(order, shipto);
		}

		if (orderCreationType == Constant.ORDER_CREATION_TYPE_PICKUP) {
			order.setNoteRailwayDisp(null);
			order.setNoteDelivery(custorder != null ? custorder.getNoteDelivery() : null);
		}
		if (LogonUtil.isAnyTypeOfPickupOrder(deliveryTypeCd)) {
			order.setTransportDurationMin(0L);
			order.setTransportDurPlanGoogleMin(0L);
			order.setTransportDistanceM(0L);
			order.setCommercialDistanceM(0L);
		}
		//LOGON-5445 currently disabling until unclear if it is needed or unnecessary
		/*if (order.getTransportDistanceM() == null || order.getTransportDistanceM() < 0) {
			throw new CustorderValidationException(CustorderError.INVALID_DISTANCE, null);
		}*/

		if (!LogonPackageHandlingUtil.isServiceOnlyCustomerOrder(custorder)) {
			final OrderItemEntity item = LogonPackageHandlingUtil.getMaterialItems(order).iterator().next();
			final OrderItemDeliverySetting itemDeliverySetting = new OrderItemDeliverySetting(order, item);
			final DeliveryOptionConfigurationDTO validOptionConfiguration = DeliverySettingFilterLogic.getDeliveryOptionConfiguration(
					itemDeliverySetting, salesAreaMap);
			/**
			 * If not specified in advance initialize the order's vehicle types according to the shipto setting.
			 */
			if ((LogonUtil.isTruckOrder(deliveryTypeCd) || LogonUtil.isPickupOrder(deliveryTypeCd)
					|| LogonUtil.isReturnPickupOrder(deliveryTypeCd) || LogonUtil.isShipPickupOrder(deliveryTypeCd) || LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd))
					&& order.getOrderVehicleTypes() == null) {

				List<OrderTruckTypeEntity> orderVehicleTypes = new ArrayList<OrderTruckTypeEntity>();

				final List<TruckWagonTypeWrapper> validVehicleTypes = validOptionConfiguration.getVehicleTypeWrapperList();
				for (TruckWagonTypeWrapper wrapper : validVehicleTypes) {
					OrderTruckTypeEntity truckTypeEntity = new OrderTruckTypeEntity();
					truckTypeEntity.setEvaluationPriority(wrapper.getPriority());
					TruckTypeBsEntity findTruckTypeBsEntity = referentialDataService.findBsEntity(
							TruckTypeBsEntity.class, wrapper.getTypeCd());
					truckTypeEntity.setTruckType(findTruckTypeBsEntity);
					truckTypeEntity.setOrder(order);
					orderVehicleTypes.add(truckTypeEntity);
				}
				order.setOrderVehicleTypes(orderVehicleTypes);
			}
		}

		// If the status code is not set on the order
		if (order.getOrderStatus() == null) {
			OrderStatusBsEntity orderStatus = referentialDataService.findBsEntity(OrderStatusBsEntity.class,
					Constant.ORDER_STATUS_NEW);
			order.setOrderStatus(orderStatus);
		}

		if (order.getSapSuppressMinChargeFlag() == null) {
			order.setSapSuppressMinChargeFlag(Boolean.FALSE);
		}

		if (order.getNoPdaOrRailEventsFlag() == null) {
			order.setNoPdaOrRailEventsFlag(false);
		}
		if (order.getReschedulingFlag() == null) {
			order.setReschedulingFlag(false);
		}
		if (order.getArchivingStatus() == null) {
			ArchivingStatusBsEntity archivingStatusBsEntity = referentialDataService.findBsEntity(ArchivingStatusBsEntity.class,
					Constant.ARCHIVING_STATUS_NEW);
			order.setArchivingStatus(archivingStatusBsEntity);
		}

		handleSpecialUomSplit(order);

		/**
		 * In this step all order item related initialization should happen. See description.
		 */
		finalInitNewOrderItems(order, salesAreaMap.getShiptoSarea(), order.getOrderItems());

		/**
		 * From this point on no changes should be made on the order items and
		 * the order item list, the order itself, as the operations below expect
		 * the complete order item list properly initialized. If further changes needs to be
		 * done before this point, mind adding it before finalInit, or in finalInit.
		 */
		calculateFreightCost(order, order.getOrderItems());
	}

	/**
	 * Initializes fields which are basically supposed to come from customer order initialization
	 * @link {@link CustorderServiceBean#postProcessCustorder}
	 * Similar step happens in the case of splitting where BeanUtils.cloneProperties takes place.
	 *
	 * WARNING: ALL FIELDS HERE HAS TO BE SPECIFIED MANUALLY FOR SPECIAL ORDERS WHICH DO NOT HAVE CUSTOMER
	 * ORDER REFERENCE AT ALL. See CreateRailReturnOrder and CreateRailMaintenanceOrder.
	 *
	 * In most
	 * @param order
	 */
	private void initFromCustorderFields(OrderEntity order) {
		final CustorderEntity custorder = order.getCustorder();
		if (order.getPlannedUnloadingTimeMin() == null) {
			order.setPlannedUnloadingTimeMin(custorder.getPlannedUnloadingTimeMin());
		}
		// LOGON-6338
		if (order.getPayerSapBp() == null) {
			order.setPayerSapBp(custorder.getPayerSapBp());
		}
		if (order.getBilltoSapBp() == null) {
			order.setBilltoSapBp(custorder.getBilltoSapBp());
		}
	}

	/**
	 * Initializes those new order items which can be added on order detail, or by
	 * LDS.
	 * Currently 24.10.2012 only new service items can be added to the order.
	 * @param fetchShiptoSarea
	 * @param order
	 */
	public void initLaterAddedOrderItems(OrderEntity order) {
		/**
		 * This call triggers a flush on the order -> currently there is no other possibility
		 * then to detach the new order items until the order is flushed, and so prevent
		 * constraint violation.
		 */
		List<OrderItemEntity> newOrderItems = detachNewOrderItems(order);

		//LOGON-5572 update the delivered quantity in case the order is above loaded state
		for (OrderItemEntity newOrderItem : newOrderItems) {
			if (LogonPackageHandlingUtil.isService(newOrderItem)) {
				if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())
						&& !order.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_DELETED)) {
					// initialize loaded quantity/UoM if needed
					if (newOrderItem.getLoadedQuantity() == null) {
						newOrderItem.setLoadedQuantity(newOrderItem.getOrderedQuantity());
						newOrderItem.setLoadedUom(newOrderItem.getOrderedUom());
					}

					// initialize delivered quantity/UoM if needed
					if (newOrderItem.getDeliveredQuantity() == null) {
						// for normal items this would be wrong, as there is a load, and we would need to copy from the loaded to delivered,
						// currently not possible to add anything else than an order item
						newOrderItem.setDeliveredQuantity(newOrderItem.getLoadedQuantity());
						newOrderItem.setDeliveredUom(newOrderItem.getLoadedUom());
					}
				}
				else {
					// initialize loaded quantity/UoM if needed
					if (newOrderItem.getLoadedQuantity() == null) {
						newOrderItem.setLoadedQuantity(newOrderItem.getOrderedQuantity());
						newOrderItem.setLoadedUom(newOrderItem.getOrderedUom());
					}
				}
			}
		}

		//this call triggers the em.flush() -> results in an insert without the not yet initialized non-null attributes which are supposed to be set in finalInitNewOrderItems
		final ShiptoSareaEntity fetchShiptoSarea = fetchShiptoSarea(order.getShiptoSapBp(), order.getSarea());

		//newOrderItems contains the really new order items and also the planning material items, after replacing with a new material
		finalInitNewOrderItems(order, fetchShiptoSarea, newOrderItems);

		for (OrderItemEntity item : newOrderItems) {
			item.setOrder(order);
			order.getOrderItems().add(item);
			item.setOrderItemNo(Long.valueOf((order.getOrderItems().indexOf(item) + 1) * 10));
		}
	}

	/**
	 * Initializes those order items which has/had planning material, and it could
	 * be replaced.
	 * @param fetchShiptoSarea
	 * @param order
	 */
	public void initPlanningMaterialOrderItems(OrderEntity order) {
		List<OrderItemEntity> planningOrderItems = new ArrayList<OrderItemEntity>();
		for (OrderItemEntity orderItem : order.getOrderItems()) {
			if (!LogonPackageHandlingUtil.isService(orderItem.getSapMaterial())) {
				planningOrderItems.add(orderItem);
			}
		}

		//this call triggers the em.flush() -> results in an insert without the not yet initialized non-null attributes which are supposed to be set in finalInitNewOrderItems
		final ShiptoSareaEntity fetchShiptoSarea = fetchShiptoSarea(order.getShiptoSapBp(), order.getSarea());
		if (!planningOrderItems.isEmpty()) {
			finalInitNewOrderItems(order, fetchShiptoSarea, planningOrderItems);
		}

		// After successful resolution we do a pricing call
		if (isPricingCallRequired(order)) {
			pricingCallService.sendPricingCall(order);
		}

	}

	/**
	 * Adjusts the quantities depending on the order status. Certain parameters are only passed to be able to
	 * guarantee that the outcome of this operation leaves the order in a consistent state.
	 *
	 * Adjusting means adding the specified amount to the ordered/delivered quantity depending on the order status.
	 *
	 * @param orderItem
	 * @param quantity
	 */
	public void adjustQuantities(final OrderItemEntity orderItem, final SapMaterialEntity sapMaterial,
			final SapContractItemEntity sapContractItem, final BigDecimal quantity, final UomBsEntity quantityUom) {

		if (!orderItem.getSapMaterial().getSapMaterialId().equals(sapMaterial.getSapMaterialId())) {
			throw new LogonException("Sap material does not correspond, cannot adjust quantities",
					Reason.RES_ERROR_IMPL);
		}

		final SapContractItemEntity orderContractItem = orderItem.getSapContractItem();

		/**
		 * If the contract item number on the order does not correspond or is not set -> do not allow summing!
		 */
		if ((orderContractItem != null && sapContractItem == null)
				|| (orderContractItem == null && sapContractItem != null)
				|| (orderContractItem != null && sapContractItem != null && !orderContractItem.getSapContractItemId().equals(
						sapContractItem.getSapContractItemId()))) {

			throw new LogonException(
					String.format(
							"The order already has a service item for received material (ifcNo: %s) , but for another contract item number, or whithout contract item number (contract item number in order: %s; contract item number received: %s)",
							sapMaterial.getSapMaterialIfcNo(),
							(orderContractItem != null ? orderContractItem.getContractItemNo() : "null"),
							(sapContractItem != null ? sapContractItem.getContractItemNo() : "null")),
					Reason.RES_ORDER_PROCESSING_ERROR);
		}

		final String quantityUomCd = quantityUom.getUomCd();

		/**
		 * Above loaded status adjust the delivered fields.
		 */
		OrderEntity order = orderItem.getOrder();
		if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())
				&& !order.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_DELETED)) {

			/**
			 * Currently only services can be added above status. Cause: it would need additional logic to adapt the load, and the
			 * loaded quantity. See also order detail changing loaded quantity how complicated that can be, as returns have to be handled etc.
			 * In the case of services there is no return handling.
			 */
			if (!LogonPackageHandlingUtil.isService(orderItem)) {
				throw new LogonException("Adjust quantitites called above status loaded for other than service item",
						Reason.RES_ERROR_IMPL);
			}

			final UomBsEntity deliveredUom = orderItem.getDeliveredUom();
			final String deliveredUomCd = deliveredUom.getUomCd();

			/**
			 * No, conversion is not supported here, at least it was not specified so. If the order is above loaded status -> leave the ordered
			 * quantity untouched, but adapt the delivered quantity.
			 */
			if (!StringUtils.equals(deliveredUomCd, quantityUomCd)) {
				throw new LogonException(
						String.format(
								"OrderItem already exist with delivered uom %s, and it differs from the received material's (ifcNo: %s) quantity uom, which is: %s.",
								deliveredUomCd, orderItem.getSapMaterial().getSapMaterialIfcNo(), quantityUomCd),
						Reason.RES_ORDER_PROCESSING_ERROR);
			}
			orderItem.setDeliveredQuantity(orderItem.getDeliveredQuantity().add(quantity));
		}
		else {

			/**
			 * No, conversion is not supported here, at least it was not specified so. If the order is below loaded status -> adapt the loaded quantity.
			 */
			final UomBsEntity orderedUom = orderItem.getOrderedUom();
			final String orderedUomCd = orderedUom.getUomCd();

			if (!StringUtils.equals(orderedUomCd, quantityUomCd)) {
				throw new LogonException(
						String.format(
								"OrderItem already exist with ordered uom %s, and it differs from the received material's (ifcNo: %s) quantity uom, which is: %s.",
								orderedUomCd, orderItem.getSapMaterial().getSapMaterialIfcNo(), quantityUomCd),
						Reason.RES_ORDER_PROCESSING_ERROR);
			}
			orderItem.setOrderedQuantity(orderItem.getOrderedQuantity().add(quantity));
		}
	}

	/**
	 * Detaching the new order items before doing any query which might trigger a flush.
	 * This is a workaround. See also Changelog BUSINESS2635.
	 */
	private List<OrderItemEntity> detachNewOrderItems(OrderEntity order) {
		List<OrderItemEntity> newOrderItems = new ArrayList<OrderItemEntity>();
		List<OrderItemEntity> orderItems = order.getOrderItems();
		Iterator<OrderItemEntity> iterator = orderItems.iterator();
		while (iterator.hasNext()) {
			OrderItemEntity item = iterator.next();
			if (item.getOrderItemId() == null) {
				newOrderItems.add(item);
				iterator.remove();
			}
		}
		return newOrderItems;
	}

	/**
	 * In this step all order item related initialization should happen. Only applies to NEW order items.
	 *
	 * @param order
	 * @param shiptoSalesArea
	 * @param orderItems TODO
	 */
	private void finalInitNewOrderItems(OrderEntity order, ShiptoSareaEntity shiptoSalesArea,
			List<OrderItemEntity> orderItems) {
		final ShiptoEntity shipto = shiptoSalesArea.getShipto();
		final boolean isTruckOrder = (order != null && order.getDeliveryType() != null) ? LogonUtil.isTruckOrder(order.getDeliveryType().getDeliveryTypeCd()) : false;
		SiloVerifTypeBsEntity siloNotVerified = null;
		for (OrderItemEntity orderItem : orderItems) {
			updateOrderItemNumeratorAndDenominator(orderItem, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);

			if (LogonPackageHandlingUtil.isService(orderItem.getSapMaterial())) {
				updateOrderItemNumeratorAndDenominator(orderItem, Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR);
			}

			if (orderItem.getSapContractItem() != null) {
				boolean iscashSalesOrExportInvoice = isCashSalesOrExportInvoice(order.getSapContract(),
						order.getSarea());
				OrderUtils.initItemInSapContract(orderItem, sapMaterialAlternativeUnitOfMeasurementStore,
						iscashSalesOrExportInvoice);
			}
			else {
				OrderUtils.initItemNotInContract(order, orderItem);
			}

			// init ship to silo verification type to 'not verified' in case of truck delivery if shipto silo is defined
			if (isTruckOrder && orderItem.getShiptoSilo() != null) {
				if (siloNotVerified == null) { // init it only once
					siloNotVerified = referentialDataService.findBsEntity(SiloVerifTypeBsEntity.class,
							Constant.SILO_VERIFICATION_TYPE_NOT_VERIFIED);
				}
				orderItem.setShiptoSiloVerifType(siloNotVerified);
			}
		}
		initProductionPlantForServiceItems(order, orderItems);

		// Determining loading/unloading times
		ConfigBean configBean = getConfigBean();
		DefaultFallbackLogic defaultFallbackLogic = new DefaultFallbackLogic(configBean);

		defaultFallbackLogic.setPlannedLoadingTimeForOrderItem(shipto, orderItems);
	}

	/**
	 * This is a workaround for the problem described in LOGON-4797 where it is described that the production plant
	 * attribute should actually be left empty for service items, but currently making it nullable is out of scope.
	 *
	 * Due to lack of time the current decision (24.10.2012) is to simply initialize the service item's
	 * production plant attribute to the value of the first material item's production plant.
	 *
	 * See also {CustorderServiceBean#initProductionPlantForServiceItems}
	 *
	 * See also LOGON-5272
	 * @param order TODO
	 * @param orderItems
	 */
	private void initProductionPlantForServiceItems(final OrderEntity order, final List<OrderItemEntity> orderItems) {
		final List<OrderItemEntity> serviceItems = LogonPackageHandlingUtil.getServiceItems(orderItems);
		if (serviceItems.isEmpty()) {
			return;
		}

		List<OrderItemEntity> tmpOrderItems = null;
		if (LogonUtil.isServiceOnlyOrder(order)) {
			tmpOrderItems = LogonPackageHandlingUtil.getServiceItems(order.getOrderItems());

			if (tmpOrderItems.isEmpty()) {
				tmpOrderItems = LogonPackageHandlingUtil.getServiceItems(orderItems);
			}
		}
		else {
			tmpOrderItems = LogonPackageHandlingUtil.getMaterialItems(order.getOrderItems());

			if (tmpOrderItems.isEmpty()) {
				tmpOrderItems = LogonPackageHandlingUtil.getMaterialItems(orderItems);
			}
		}

		if (tmpOrderItems == null || tmpOrderItems.isEmpty()) {
			return;
		}

		final OrderItemEntity item = tmpOrderItems.get(0);
		final PlantBsEntity plant = item.getProductionPlant();

		for (OrderItemEntity serviceItem : serviceItems) {
			if (serviceItem.getProductionPlant() == null) {
				serviceItem.setProductionPlant(plant);
			}
		}
	}

	private void initSequences(OrderEntity order) {
		if (order.getOrderNumber() == null) {
			order.setOrderNumber(fetchSequenceNextVal("SLGNC_ORDER_NUMBER"));
		}
	}

	/**
	 * Initializes the distance/duration related fields of the order, but only in case they are not set.
	 * From order taking sources these data are coming from google, but for LDS it does not. LOGON-5445
	 * @param order
	 * @param shipto
	 */
	private void initializeDistanceAndDurationForRoadOrder(OrderEntity order, final ShiptoEntity shipto) {
		// TODO (orsika Aug 24, 2013):check if calculateAndSaveDistanceAndDuration/4 or /1 can be used
		//instead of this separate implementation. This implementation is running when
		//order is created by LDS or if it is a web order.
		if (!LogonUtil.isTruckOrder(order.getDeliveryType().getDeliveryTypeCd())) {
			return;
		}
		//copy the value what google calculated and multiply
		if (order.getTransportDurPlanGoogleMin() != null) {

			final double durationCorrectionFactor = getDurationCorrectionFactor(
					order.getShippingpoint().getShippingpointId(), shipto.getShiptoId(), order);
			order.setTransportDurationMin(Math.round(order.getTransportDurPlanGoogleMin().doubleValue()
					* durationCorrectionFactor));
		}

		if (order.getTransportDurationMin() == null && order.getShippingpoint() != null) {
			//if still null
			TruckRouteEntity route = findTruckRoute(order.getShiptoSapBp(), order.getShippingpoint(),
					order.getDeliveryType());
			if (route != null) {
				order.setTransportDurationMin(route.getTimeCalculatedMin());
			}
		}

		if (order.getTransportDistanceM() == null && order.getShippingpoint() != null) {
			//if still null
			TruckRouteEntity route = findTruckRoute(order.getShiptoSapBp(), order.getShippingpoint(),
					order.getDeliveryType());
			if (route != null) {
				Long correctedDistance = route.getDistanceCalculatedM();
				if (route.getDistanceCorrectionFactor() != null && correctedDistance != null) {
					correctedDistance = correctedDistance * route.getDistanceCorrectionFactor().longValue();
				}
				order.setTransportDistanceM(correctedDistance);
				order.setCommercialDistanceM(correctedDistance);
			}
		}

		// FIXME 08-Nov-2012/stich: [LOGON-5868] This is a temporary workaround. Provide a better solution, such as air distance * correction factor
		if (order.getTransportDurationMin() == null) {
			order.setTransportDurationMin(Constant.TMP_TRANSPORT_DURATION_MIN);
		}
	}

	/**
	 * Calculates the distance and duration related data between the shipping point (/lnf coordinates)
	 * and the ship-to. If there is no valid calculation, default
	 * (fallback) values are used. The calculated values are set to the order.
	 * Order of computing the values:
	 * 1. trust in default values (input parameter) - mainly given from the javascript google api
	 * 2. checking for non-expired data in truck route table
	 * 3. calling google server side api
	 * 4. fallback values
	 */
	@Override
	public OrderEntity calculateAndSaveDistanceAndDuration(OrderEntity order, Long uncorrectedDurationMin,
			Long transportDurationMin, Long uncorrectedTransportDistanceM, Long transportDistanceM) {
		//not calculating for pickup, as in a later phase in save new order, they are set to 0 - would be unnecessary
		if (LogonUtil.isAnyTypeOfPickupOrder(order.getDeliveryType().getDeliveryTypeCd())) {
			return order;
		}
		if (transportDurationMin == null || transportDurationMin <= 0 || transportDistanceM == null
				|| transportDistanceM <= 1) {
			//no valid default data coming (from client side google api) -> truck route table, google server side api
			GoogleMapsDistanceMatrixResultBean distanceMatrixResult = calculateDistanceAndDuration(order);

			if (distanceMatrixResult != null) {
				transportDurationMin = distanceMatrixResult.getDurationMin();
				uncorrectedDurationMin = distanceMatrixResult.getRawGoogleDurationMin();

				transportDistanceM = distanceMatrixResult.getCorrectedDistance();
			}
		}
		//		else {
		// TODO (orsika Aug 24, 2013):if valid js data is coming from javascript, the appropriate record
		//in TruckRoute table should be updated/created. this is only an optimization point, the behavior
		//is ok this way also
		//		}

		String message = null;
		if (transportDurationMin == null || transportDurationMin <= 0) {
			message = "transportDurationMin is null or <= 0. fallback to 1.";
			LOG.info(message);
			transportDurationMin = 1L;//fallback value
		}

		// TODO (orsika Aug 23, 2013): not sure this condition is valid (inheritage)
		if ((LogonUtil.isTruckOrder(order.getDeliveryType().getDeliveryTypeCd()) && transportDistanceM == null)
				|| (transportDistanceM != null && transportDistanceM <= 0)) {
			message = "transportDistanceM is null or <= 0. fallback to 1.";
			LOG.info(message);
			transportDistanceM = 1L;//fallback value
		}

		order.setTransportDurPlanGoogleMin(uncorrectedDurationMin);
		order.setTransportDurationMin(transportDurationMin);
		order.setTransportDistanceM(transportDistanceM);
		order.setCommercialDistanceM(transportDistanceM);

		return order;
	}

	/**
	 * Calculates the distance and duration related data between the shipping point (/lnf coordinates)
	 * and the ship-to. If there is no valid calculation, default
	 * (fallback) values are used. The calculated values are set to the order.
	 * Order of computing the values:
	 * 1. checking for non-expired data in truck route table
	 * 2. calling google server side api
	 * 3. fallback values
	 */
	public OrderEntity calculateAndSaveDistanceAndDuration(OrderEntity order) {
		return calculateAndSaveDistanceAndDuration(order, null, null, null, null);
	}

	/**
	 * Calling GoogleMapsService for calculating duration and distance values between
	 * shipping point and ship-to. It takes values from TruckRoute table, if there is.
	 * Handles cases when no route found.
	 *
	 * @param order
	 * @return
	 */
	private GoogleMapsDistanceMatrixResultBean calculateDistanceAndDuration(OrderEntity order) {
		final GoogleMapsService googleMapsService = getServiceFor(GoogleMapsService.class);

		Long deliveryTypeCd = order.getDeliveryType().getDeliveryTypeCd();
		final ShippingpointEntity origin = order.getShippingpoint();

		GoogleMapsDistanceMatrixResultBean distanceMatrixResult = null;
		//only in case of location not fixed orders:
		Double lnfCoordLat = order.getLnfCoordLat();
		Double lnfCoordLon = order.getLnfCoordLon();
		if (lnfCoordLat != null && lnfCoordLon != null && lnfCoordLat != 0 && lnfCoordLon != 0) {
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(origin, lnfCoordLat, lnfCoordLon);
		}
		//otherwise: googleMapsService fetches also truckroute table
		else {
			SapBpEntity shiptoSapBp = order.getShiptoSapBp();
			final String shiptoSapId = shiptoSapBp.getSapId();
			final ShiptoEntity destination = dispokarteService.fetchShiptoBySapId(shiptoSapId);
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(origin, destination, deliveryTypeCd);
		}

		final String status = distanceMatrixResult.getStatus();
		if (Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK.equals(status)) {
			Long durationMin = distanceMatrixResult.getDurationMin();
			if (durationMin == null) {
				String message = "Unable to calculate TransportDurationMin, "
						+ "GoogleMapsDistanceMatrixResultBean.getDurationSec() is null.";
				LOG.warn(message);
				throw new LogonInvalidDurationException();
			}
		}
		else {
			String message = "Unable to calculate TransportDurationMin, " + "status code is not OK. Status: " + status;
			LOG.warn(message);
			throw new LogonInvalidDurationException();
		}
		return distanceMatrixResult;
	}

	/**
	 *
	 * Delivery time window validation for "truck" type web-order creation.
	 */
	private void checkLeadTimeDeliveryAddWarning(OrderEntity order) throws ValidationException {
		final List<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();

		CustorderError checkLeadTimeError = deliveryDateService.checkTruckLeadTime(order);

		if (checkLeadTimeError != null) {
			if (checkLeadTimeError.equals(CustorderError.LEADTIME_WEB_ORDER_SD_EARLY)) {
				long applySdLeadtimeChkFromMi = deliveryDateService.getApplySdLeadtimeChkFromMi(order);
				final String addInfo = DateTimeUtils.formatHoursMinutes(DateTimeUtils.getHoursMinutes(applySdLeadtimeChkFromMi));
				String[] additionalInfo = { addInfo };
				validationErrorReasons.add(new ValidationErrorReason(checkLeadTimeError, order, additionalInfo));
			}
			else if (checkLeadTimeError.equals(CustorderError.LEADTIME_WEB_ORDER_SD_LATER)) {
				long applySdLeadtimeChkFromMi = deliveryDateService.getApplySdLeadtimeChkFromMi(order);
				final String addInfo1 = DateTimeUtils.formatHoursMinutes(DateTimeUtils.getHoursMinutes(applySdLeadtimeChkFromMi));

				long allowSdWebOrderUntilMi = deliveryDateService.getAllowSdWebOrderUntilMi(order);
				final String addInfo2 = DateTimeUtils.formatHoursMinutes(DateTimeUtils.getHoursMinutes(allowSdWebOrderUntilMi));
				String[] additionalInfo = { addInfo1, addInfo2 };

				validationErrorReasons.add(new ValidationErrorReason(checkLeadTimeError, order, additionalInfo));
			}
			else if (checkLeadTimeError.equals(CustorderError.LEADTIME_WEB_ORDER_ND_EARLY)) {
				long applyNdLeadtimeChkFromMi = deliveryDateService.getApplyNdLeadtimeChkUntilMi(order);
				final String addInfo1 = DateTimeUtils.formatHoursMinutes(DateTimeUtils.getHoursMinutes(applyNdLeadtimeChkFromMi));

				long allowNdWebOrderUntilMi = deliveryDateService.getAllowNdWebOrderUntilMi(order);
				final String addInfo2 = DateTimeUtils.formatHoursMinutes(DateTimeUtils.getHoursMinutes(allowNdWebOrderUntilMi));
				String[] additionalInfo = { addInfo1, addInfo2 };

				validationErrorReasons.add(new ValidationErrorReason(checkLeadTimeError, order, additionalInfo));
			}
			else if (checkLeadTimeError.equals(CustorderError.LEADTIME_WEB_ORDER_WEEKEND)) {
				String[] additionalInfo = {};
				validationErrorReasons.add(new ValidationErrorReason(checkLeadTimeError, order, additionalInfo));
			}

			if (!validationErrorReasons.isEmpty()) {
				throw new ValidationException(validationErrorReasons);
			}
		}
	}

	public DeliverySettingSalesAreaMap initSalesAreaMapForOrderValidation(OrderEntity order,
			boolean filterFutureContracts) {
		DeliverySettingSalesAreaMap salesAreaMap = null;

		final SapBpEntity shiptoSapBpEntity = order.getShiptoSapBp();
		final SapBpEntity soldtoSapBp = order.getSoldtoSapBp();
		final SareaEntity sarea = order.getSarea();

		final ShiptoSareaEntity shiptoSarea = fetchShiptoSarea(shiptoSapBpEntity, sarea);

		final Set<Long> sapMaterialIds = getSapMaterialIdsFromOrder(order);

		DeliverySettingShiptoMap shiptoMap = deliverySettingService.initDeliverySettingShiptoMapForDeliverySettingValidation(
				soldtoSapBp, Locale.ENGLISH, shiptoSarea, sapMaterialIds, filterFutureContracts);

		salesAreaMap = shiptoMap.getSalesAreaMapForSareaId(sarea.getSareaId());
		return salesAreaMap;
	}

	private Set<Long> getSapMaterialIdsFromOrder(OrderEntity order) {
		Set<Long> sapMaterialIds = new HashSet<Long>();
		for (OrderItemEntity orderItem : order.getOrderItems()) {
			sapMaterialIds.add(orderItem.getSapMaterial().getSapMaterialId());
		}
		return sapMaterialIds;
	}

	/**
	 * @param shiptoSapBpEntity
	 * @param sarea
	 * @return
	 */
	public ShiptoSareaEntity fetchShiptoSarea(final SapBpEntity shiptoSapBpEntity, final SareaEntity sarea) {
		return shiptoSareaStore.findShiptoSareaWithActiveShipto(shiptoSapBpEntity, sarea);
	}

	/**
	 * Validate order item mandatory fields.
	 *
	 * @param order
	 * @param salesAreaMap
	 * @throws ValidationException
	 */
	public void validateOrder(OrderEntity order, final DeliverySettingSalesAreaMap salesAreaMap) {
		validateOrder(order, salesAreaMap, null, false);
	}

	/**
	 * Validate order item mandatory fields.
	 *
	 * @param order
	 * @param salesAreaMap
	 * @param quantityRestrCalcMap
	 * @param skipLeadTimeCheck
	 * @throws ValidationException
	 */
	public void validateOrder(OrderEntity order, final DeliverySettingSalesAreaMap salesAreaMap,
			Map<Long, QuantityRestrictionCalculation> quantityRestrCalcMap, boolean skipLeadTimeCheck) {
		List<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();

		try {
			validateOrderItems(order, salesAreaMap);
		}
		catch (ValidationException e) {
			validationErrorReasons.addAll(e.getValidationErrorReasons());
		}

		//this validation checks the leadtime, that should be validated when user updates by LOGON-9116
		//in case the user do not have the right to override leadtime during Update action, otherwise it works as-is
		if (order.getOrderId() == null
				&& order.getOrderCreationType().getOrderCreationTypeCd().longValue() == Constant.ORDER_CREATION_TYPE_WEB_ORDER
				&& !skipLeadTimeCheck) {
			try {
				validateLeadTimeCheck(order);
			}
			catch (ValidationException e) {
				validationErrorReasons.addAll(e.getValidationErrorReasons());
			}
		}

		// --- For rail orders, auto assignment for pool and carrier is mandatory ---
		if (LogonUtil.isRailOrder(order.getDeliveryType().getDeliveryTypeCd())) {
			if (order.getPool() == null) {
				String[] additionalInfo = {};
				validationErrorReasons.add(new ValidationErrorReason(CustorderError.RAIL_ORDER_POOL_EMPTY, order,
						additionalInfo));
			}
			if (order.getCommCarrier() == null) {
				String[] additionalInfo = {};
				validationErrorReasons.add(new ValidationErrorReason(CustorderError.RAIL_ORDER_COMM_CARRIER_EMPTY,
						order, additionalInfo));
			}
		}
		// --- For simple vehicle orders pool and carrier is mandatory ---
		else if (LogonUtil.isSimpleVehicleOrder(order.getDeliveryType().getDeliveryTypeCd())) {
			if (order.getPool() == null || order.getCommCarrier() == null) {
				String[] additionalInfo = {};
				validationErrorReasons.add(new ValidationErrorReason(
						CustorderError.SIMPLE_VEHICLE_ORDER_POOL_COMM_CARRIER, order, additionalInfo));
			}
		}

		/*
		 * The following check should only run if the consistency of the order data is assured,
		 * that is, if no other validation errors occurred.
		 */

		final List<QuantityRestrictionError> quantityErrors = quantityRestrictionService.validateQuantities(order,
				quantityRestrCalcMap);

		if ((quantityErrors != null) && !quantityErrors.isEmpty()) {

			final QuantityRestrictionError qrError = quantityErrors.get(0);
			final SapMaterialDescriptionEntity materialDesc = dispokarteService.fetchSapMaterialDescription(
					qrError.getOrderItem().getSapMaterial().getSapMaterialId(), Locale.ENGLISH.getLanguage());
			final String uomTxt = referentialDataService.findTxEntity(UomTxEntity.class, Locale.ENGLISH,
					qrError.getTargetQuantityUomCd()).getTxt();
			String[] additionalInfo = { qrError.getOpenQuantity() + " " + uomTxt, materialDesc.getTxt() };

			LOG.error(String.format("Validation for order %s failed: Open: %s %s, Required: %s %s",
					order.getOrderNumber(), qrError.getOpenQuantity(), qrError.getTargetQuantityUomCd(),
					qrError.getSumOrderedQuantity(), qrError.getTargetQuantityUomCd()));

			validationErrorReasons.add(new ValidationErrorReason(CustorderError.OPEN_QUANTITY_CHECK_FAILURE, order,
					additionalInfo));
		}

		if (!validationErrorReasons.isEmpty()) {
			throw new ValidationException(validationErrorReasons);
		}
	}

	/**
	 * These checks should be made even the order has already been existed as user have right to chnage the order delivery from or/and to date
	 * @param order
	 *
	 * @return
	 */
	public void validateLeadTimeCheck(OrderEntity order) throws ValidationException {
		LinkedList<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();

		final ShiptoSareaEntity shiptoSarea = fetchShiptoSarea(order.getShiptoSapBp(), order.getSarea());

		/**
		 * Web order specific validation. If this turns out to be common for all, then the if statement can be removed.
		 */
		if (order.getDeliveryFromDat().after(order.getDeliveryToDat())) {
			String[] additionalInfo = {};
			validationErrorReasons.add(new ValidationErrorReason(CustorderError.DELIVERY_TO_BEFORE_DELIVERY_FROM,
					order, additionalInfo));
		}

		if (order.getDeliveryToDat().before(new Date())) {
			String[] additionalInfo = {};
			validationErrorReasons.add(new ValidationErrorReason(CustorderError.DELIVERY_TO_IN_PAST, order,
					additionalInfo));
		}

		if (LogonUtil.isBimodalRailPartOrder(order.getDeliveryType().getDeliveryTypeCd())) {

			try {
				TimeZone userTimeZone = getUserTimeZone();
				ShiptoEntity shipTo = shiptoSarea.getShipto();
				CustorderEntity customerOrder = order.getCustorder();
				ConfigBean configBean = getConfigBean();
				railSchedulingService.applyLeadTimeCheck(shipTo, customerOrder, configBean, userTimeZone);
			}
			catch (ValidationException e) {
				validationErrorReasons.addAll(e.getValidationErrorReasons());
			}
		}

		if (LogonUtil.isTruckOrder(order.getDeliveryType().getDeliveryTypeCd())) {
			try {
				// run validation only if control flag allows it
				if (Boolean.TRUE.equals(shiptoSarea.getSdltAndNdltActive())) {
					checkLeadTimeDeliveryAddWarning(order);
				}
			}
			catch (ValidationException e) {
				validationErrorReasons.addAll(e.getValidationErrorReasons());
			}

			// run validation only if control flag allows it
			if (Boolean.TRUE.equals(shiptoSarea.getDbltActiveFlag())) {
				LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean(shiptoSarea, order
						.getSarea());
				DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
				DistanceValidationInfoBean distanceValidationInfoBean =
						deliveryDistanceValidationUtils.distanceBaseLeadTimeCheckOrderTaking(leadTimeValidationBaseBean, order
						);
				List<ValidationErrorReason> distanceBasedErrorReasons = distanceValidationInfoBean
						.getDistanceBasedErrorReasons();
				validationErrorReasons.addAll(distanceBasedErrorReasons);
			}
		}

		if (!validationErrorReasons.isEmpty()) {
			throw new ValidationException(validationErrorReasons);
		}
	}

	/**
	 *
	 * @return the TimeZone of the current user
	 */
	private TimeZone getUserTimeZone() {
		Long userId = getUserId();
		UserEntity user = userService.fetchUserEntity(userId);
		TimezoneBsEntity userTimeZoneBs = user.getTimezone();
		TimeZone userTimeZone;
		if (userTimeZoneBs != null) {
			String timezoneCode = userTimeZoneBs.getTimezoneCd();
			userTimeZone = new TimeZone(timezoneCode, Locale.getDefault());
		}
		else {
			userTimeZone = TimeZone.getAppTimeZone();
		}
		return userTimeZone;
	}

	/**
	 * Validate order item mandatory fields.
	 *
	 * @param order
	 * @param salesAreaMap
	 */
	private void validateOrderItems(OrderEntity order, final DeliverySettingSalesAreaMap salesAreaMap)
			throws ValidationException {

		LinkedList<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();

		/**
		 * Phase 1: Checks regarding the combination of order items: like cannot mix bag and bulk orders, or
		 * cannot mix free of charge orders
		 */
		LogonPackageHandlingUtil.checkLogonPackageCombination(order.getOrderItems());
		Date orderDeliveryFromDat = order.getDeliveryFromDat();
		Date orderDeliveryToDat = order.getDeliveryToDat();

		for (OrderItemEntity orderItem : order.getOrderItems()) {

			SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
			SapContractItemEntity sapContractItem = orderItem.getSapContractItem();

			if (sapContractItem != null) {
				SapContractEntity sapContract = sapContractItem.getSapContract();
				if (sapContract != null) {
					Date contractValidFromDat = sapContract.getValidFromDat();
					Date contractValidToDat = sapContract.getValidUntilDat();
					if (contractValidFromDat.compareTo(orderDeliveryFromDat) > 0
							|| (DateTimeComparator.getDateOnlyInstance().compare(contractValidToDat, orderDeliveryToDat) < 0)) {
						String[] additionalInfo = { sapMaterial.getSapMaterialNo() };
						validationErrorReasons.add(new ValidationErrorReason(
								CustorderError.CONTRACT_VALID_DATE_INVALID, orderItem, additionalInfo));
					}
				}
			}

			if (orderItem.getCurrency() == null) {
				String[] additionalInfo = { sapMaterial.getSapMaterialNo() };
				validationErrorReasons.add(new ValidationErrorReason(CustorderError.CURRENCY_MISSING, orderItem,
						additionalInfo));

			}

			if (orderItem.getOrderedQuantity() == null) {
				String[] additionalInfo = { sapMaterial.getSapMaterialNo() };
				validationErrorReasons.add(new ValidationErrorReason(CustorderError.QUANTITY_MISSING, orderItem,
						additionalInfo));
			}

			// TODO (emoke Oct 9, 2012):Service items can have this field empty later on, but not now see LOGON-4581
			if (!LogonUtil.isPlanningMaterial(sapMaterial) && orderItem.getProductionPlant() == null) {
				String[] additionalInfo = { sapMaterial.getSapMaterialNo() };
				validationErrorReasons.add(new ValidationErrorReason(CustorderError.PRODUCTION_PLANT_MISSING,
						orderItem, additionalInfo));
			}

			if (sapContractItem != null
					&& !sapContractItem.getSapContract().getSapContractId().equals(
							order.getSapContract().getSapContractId())) {
				String[] additionalInfo = { sapMaterial.getSapMaterialNo(), order.getSapContract().getSapContractNo() };
				validationErrorReasons.add(new ValidationErrorReason(CustorderError.ITEM_NOT_IN_CONTRACT, orderItem,
						additionalInfo));
			}

			/**
			 * For this material selection all materials which come from contracts are present, and all are resolved to generic materials.
			 * The order items has to be present among these settings, otherwise it should not be present at all, and that signs that the
			 * order item is invalid.
			 *
			 * The validation is 2 phase:
			 * Phase 1: Checking the qualification of the material + contract item presence in the order item
			 * Phase 2: Checking the validity of the delivery setting: the material has to be available at the shippingpoint, must eb available for
			 * road/rail etc. rules, the uom has to be active etc.
			 * If the delivery setting's attributes what is formed from the order item + order attributes (see OrderItemDeliverySetting) are all available
			 * in the valid option values for each attribute, the order item is valid.
			 */
			final OrderItemDeliverySetting itemDeliverySetting = new OrderItemDeliverySetting(order, orderItem);
			final DeliverySettingDTO deliverySetting = salesAreaMap.getDeliverySettingByCompositeIdentifier(itemDeliverySetting.getCompositeIdentifier());

			/**
			 * This here is supposed to check all meaningful attributes of the sales area, and fill out the
			 * sales area map's invalid map. This will be later on used when validating the order item's delivery setting.
			 */

			salesAreaMap.validateSapBpSareaRelatedData(order.getDeliveryType().getApplyBpBlockCheck());
			validationErrorReasons.addAll(InvalidMapSupport.getCustorderExceptionFromInvalidMap(
					salesAreaMap.getInvalidMap(), orderItem));

			/*
			 * Phase 1: Check the material + contract item pair (if there is a contract item at all - for services there might be no contract item, but overall they can be present in an order)
			 */
			if (deliverySetting == null) {
				/**
				 * In case in the sales area map this delivery setting is not present,
				 * that means that the product is not available for the contract item
				 * the order item specifies ==> it is invalid! This should not happen from any of
				 * the order taking sources. For most of the order taking sources this is guaranteed that on the UI
				 * nothing is selectable what is not valid. But in case it does, this validation has to
				 * make sure that it does not happen. For the order taking sources loading systems nothing
				 * is guaranteed, as in a request anything can appear in any combination.
				 */
				final ValidationErrorReason reason;
				if (sapContractItem != null) {
					String contractItemNo = null;
					contractItemNo = sapContractItem.getContractItemNo().toString();
					String[] additionalInfo = { sapMaterial.getSapMaterialNo(), contractItemNo };
					reason = new ValidationErrorReason(CustorderError.CONTRACT_INVALID, orderItem, additionalInfo);
				}
				else {
					String[] additionalInfo = { sapMaterial.getSapMaterialNo() };
					reason = new ValidationErrorReason(CustorderError.CONTRACT_INVALID, orderItem, additionalInfo);
				}

				validationErrorReasons.add(reason);
				continue;
			}

			/*
			 * Phase 2
			 * Now check the delivery setting's validity. This can very well cause save failure. On all order taking scrrens the options are filtered for the
			 * first item. The second item has poor pre-validation if it is valid against the order level settings.
			 */
			final DeliveryOptionConfigurationDTO validOptionConfiguration = DeliverySettingFilterLogic.getDeliveryOptionConfiguration(
					itemDeliverySetting, salesAreaMap);

			HashMap<Object, List<ShiptoSalesAreaError>> invalidMap = new HashMap<Object, List<ShiptoSalesAreaError>>();

			// FIXME: (28.03.2013/lajos): @fecnek: as discussed please clarify bimodail rail case (LOGON-6492)
			if (isRailDirectGoingToRepair(order, itemDeliverySetting.getSapMaterialId())) {
				salesAreaMap.validateDeliverySettingWithoutSupplyPlant(itemDeliverySetting, validOptionConfiguration,
						invalidMap);
			}
			else {
				salesAreaMap.validateDeliverySetting(itemDeliverySetting, validOptionConfiguration, invalidMap);
			}

			validationErrorReasons.addAll(InvalidMapSupport.getCustorderExceptionFromInvalidMap(invalidMap, orderItem));

			if (!validationErrorReasons.isEmpty()) {
				throw new ValidationException(validationErrorReasons);
			}
		}

		if (!validationErrorReasons.isEmpty()) {
			throw new ValidationException(validationErrorReasons);
		}
	}

	private boolean isRailDirectGoingToRepair(final OrderEntity order, final long sapMaterialId) {
		if (LogonUtil.isRailDirectOrder(order.getDeliveryType().getDeliveryTypeCd())
				&& LogonUtil.isRepairDistributionDest(order.getDistributionDest().getDistributionDestCd())) {

			final ShippingpointEntity shippingPoint = order.getShippingpoint();
			final boolean isShippingPointKnownInSap = shippingPoint.getKnownAlsoInSapFlag();

			final PlantBsEntity plant = shippingPoint.getPlant();
			final boolean isLogonOnlyPlant = orderDetailService.isLogonOnlySupplyPlant(plant, sapMaterialId);

			return isLogonOnlyPlant || !isShippingPointKnownInSap;
		}
		return false;
	}

	public InvoiceBean getInvoiceBean(OrderItemEntity orderItem) {
		BigDecimal netQuantity = BigDecimal.ZERO;

		CurrencyBsEntity currencyBase = orderItem.getOrder().getSapContract().getCurrency();
		if (currencyBase == null) {
			return null;
		}
		String currency = currencyBase.getCurrencyCd();

		UomBsEntity netUom = null;
		LoadEntity load = orderItem.getLoad();
		if (load == null || load.getLoadingTs() == null) {
			// case 1: before load
			if (orderItem.getOrderedUomNumerator() != null && orderItem.getOrderedUomDenominator() != null) {
				//if available, then use the calculated measure in the base UOM of the material - cause the ordered uom can be a fuzzy uom
				final BigDecimal orderedUomNumerator = new BigDecimal(orderItem.getOrderedUomNumerator());
				final BigDecimal orderedUomDenominator = new BigDecimal(orderItem.getOrderedUomDenominator());
				netQuantity = orderItem.getOrderedQuantity().multiply(orderedUomNumerator).divide(orderedUomDenominator);
				netUom = orderItem.getSapMaterial().getBaseUom();
			}
			else {
				netQuantity = orderItem.getOrderedQuantity() != null ? orderItem.getOrderedQuantity() : netQuantity;
				netUom = orderItem.getOrderedUom();
			}
		}
		else {
			// case 2: after load
			netQuantity = load.getNetQuantity() != null ? load.getNetQuantity() : netQuantity;
			BigDecimal returnedQuantity = orderItem.getReturnedQuantity() != null ? orderItem.getReturnedQuantity() : BigDecimal.ZERO;
			netUom = load.getNetQuantityUom();
			// if returned quantity not equal with net quantity: conversion
			UomBsEntity returnedUom = orderItem.getReturnedUom();
			if (returnedUom != null && !returnedUom.getUomCd().equals(netUom.getUomCd())) {
				BigDecimal returnedBase = UOMConverter.convertAltUomToMaterialBaseUom(returnedUom, returnedQuantity,
						orderItem.getSapMaterial(), getDaoProvider().getSapMaterialAltUomEntityDao());
				if (returnedBase == null) {
					// not alt. UoM
					returnedBase = returnedQuantity;
				}
				else {
					returnedUom = orderItem.getSapMaterial().getBaseUom();
				}

				// convert to loaded UoM, after converting it to base
				BigDecimal base = UOMConverter.convertAltUomToMaterialBaseUom(netUom, netQuantity,
						orderItem.getSapMaterial(), getDaoProvider().getSapMaterialAltUomEntityDao());
				if (base == null) {
					// net UoM is NOT alternative
					base = UOMConverter.convertUomToAnotherUomUsingCommonDimension(netUom,
							orderItem.getSapMaterial().getBaseUom(), netQuantity, false);
				}
				netQuantity = base;
				netUom = orderItem.getSapMaterial().getBaseUom();
				returnedQuantity = UOMConverter.convertUomToAnotherUomUsingCommonDimension(returnedUom, netUom,
						returnedBase, false);
			}
			netQuantity = netQuantity.subtract(returnedQuantity);
		}

		Long orderItemId = orderItem.getOrderItemId();

		PricingCallEntity pricingCallEntity = pricingCallService.findPricingCallEntityByOrderItemId(orderItemId);

		if (pricingCallEntity == null) {
			return getInvoiceBeanWhenBasedOnContract(orderItem, netUom, netQuantity, currency);
		}
		return getInvoiceBeanWhenBasedOnPricingCall(orderItem, netUom, netQuantity, currency, pricingCallEntity);
	}

	private InvoiceBean getInvoiceBeanWhenBasedOnContract(OrderItemEntity orderItem, UomBsEntity netUom,
			BigDecimal netQuantity, String currency) {
		final BigDecimal netValue = orderItem.getNetValue();
		final BigDecimal targetQuantity = orderItem.getTargetQuantity();
		final BigDecimal vatPercentage = orderItem.getVatPercentage();
		final UomBsEntity targetQuantityUom = orderItem.getTargetQuantityUom();
		final SapMaterialEntity sapMaterial = orderItem.getSapMaterial();

		if (netUom == null || vatPercentage == null || netValue == null || targetQuantity == null
				|| targetQuantityUom == null || BigDecimal.ZERO.compareTo(targetQuantity) == 0) {
			return null;
		}

		if (!netUom.getUomCd().equals(targetQuantityUom.getUomCd())) {
			// conversion needed
			BigDecimal base = UOMConverter.convertAltUomToMaterialBaseUom(netUom, netQuantity, sapMaterial,
					getDaoProvider().getSapMaterialAltUomEntityDao());
			if (base == null) {
				// net UoM is NOT alternative
				base = UOMConverter.convertUomToAnotherUomUsingCommonDimension(netUom, sapMaterial.getBaseUom(),
						netQuantity, false);
			}
			netUom = sapMaterial.getBaseUom();
			if (orderItem.getTargetUomNumerator() != null && orderItem.getTargetUomDenominator() != null
					&& orderItem.getTargetUomNumerator().compareTo(0L) != 0) {
				// target quantity is alt. UoM
				netQuantity = base.multiply(BigDecimal.valueOf(orderItem.getTargetUomDenominator())).divide(
						BigDecimal.valueOf(orderItem.getTargetUomNumerator()));
			}
			else {
				// target quantity is NOT alt. UoM
				netQuantity = UOMConverter.convertUomToAnotherUomUsingCommonDimension(netUom, targetQuantityUom,
						netQuantity, false);
			}
			if (netQuantity == null) {
				return null;
			}
		}

		final BigDecimal netPrice = netValue.multiply(netQuantity).divide(targetQuantity, Constant.MONEY_SCALE,
				Constant.MONEY_ROUNDING_MODE);
		final BigDecimal vatValue = netPrice.multiply(vatPercentage).divide(new BigDecimal(100), Constant.MONEY_SCALE,
				Constant.MONEY_ROUNDING_MODE);
		final BigDecimal grossPrice = netPrice.add(vatValue);
		final BigDecimal netPricePerBaseUom = calculateNetPricePerBaseUoM(sapMaterial, netValue, targetQuantity,
				targetQuantityUom);
		final BigDecimal totalNetPrice = netPricePerBaseUom.multiply(netQuantity);
		final BigDecimal netPricePerBaseUomInEur = calculateNetPricePerBaseUomInEur(orderItem, netPricePerBaseUom);

		InvoiceBean invoiceBean = new InvoiceBean(currency, netPrice, vatValue, vatPercentage, grossPrice,
				netPricePerBaseUom, netUom, totalNetPrice, netPricePerBaseUomInEur);

		return invoiceBean;
	}

	private InvoiceBean getInvoiceBeanWhenBasedOnPricingCall(OrderItemEntity orderItem, UomBsEntity netUom,
			BigDecimal netQuantity, String currency, PricingCallEntity pricingCall) {
		final BigDecimal netValue = pricingCall.getNetValue();
		final BigDecimal targetQuantity = pricingCall.getTargetQuantity();
		final String targetQuantityUomCd = pricingCall.getTargetQuantityUomCd();
		final SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
		final BigDecimal vatPercentage = orderItem.getVatPercentage();

		if (netUom == null || vatPercentage == null || netValue == null || targetQuantity == null
				|| targetQuantityUomCd == null || BigDecimal.ZERO.compareTo(targetQuantity) == 0) {
			return null;
		}
		final UomBsEntity targetQuantityUom = referentialDataService.findBsEntity(UomBsEntity.class,
				targetQuantityUomCd);
		if (targetQuantityUom == null) {
			return null;
		}

		if (!netUom.getUomCd().equals(targetQuantityUom.getUomCd())) {
			// conversion needed
			BigDecimal base = UOMConverter.convertAltUomToMaterialBaseUom(netUom, netQuantity, sapMaterial,
					getDaoProvider().getSapMaterialAltUomEntityDao());
			if (base == null) {
				// net UoM is NOT alternative
				base = UOMConverter.convertUomToAnotherUomUsingCommonDimension(netUom, sapMaterial.getBaseUom(),
						netQuantity, false);
			}
			netUom = sapMaterial.getBaseUom();
			if (orderItem.getTargetUomNumerator() != null && orderItem.getTargetUomDenominator() != null
					&& orderItem.getTargetUomNumerator().compareTo(0L) != 0) {
				// target quantity is alt. UoM
				netQuantity = base.multiply(BigDecimal.valueOf(orderItem.getTargetUomDenominator())).divide(
						BigDecimal.valueOf(orderItem.getTargetUomNumerator()));
			}
			else {
				// target quantity is NOT alt. UoM
				netQuantity = UOMConverter.convertUomToAnotherUomUsingCommonDimension(netUom, targetQuantityUom,
						netQuantity, false);
			}
			if (netQuantity == null) {
				return null;
			}
		}

		final BigDecimal netPrice = netValue.multiply(netQuantity).divide(targetQuantity, Constant.MONEY_SCALE,
				Constant.MONEY_ROUNDING_MODE);
		final BigDecimal vatValue = pricingCall.getVatAmount();
		final BigDecimal grossPrice = netPrice.add(vatValue);
		final BigDecimal netPricePerBaseUom = calculateNetPricePerBaseUoM(sapMaterial, netValue, targetQuantity,
				targetQuantityUom);
		final BigDecimal totalNetPrice = netPricePerBaseUom.multiply(netQuantity);
		final BigDecimal netPricePerBaseUomInEur = calculateNetPricePerBaseUomInEur(orderItem, netPricePerBaseUom);

		InvoiceBean invoiceBean = new InvoiceBean(currency, netPrice, vatValue, vatPercentage, grossPrice,
				netPricePerBaseUom, netUom, totalNetPrice, netPricePerBaseUomInEur);

		return invoiceBean;
	}

	/**
	 * Calculates the net price per base uom in EUR for th export invoice document.
	 * LOGON-6647
	 * LRQ 307.0 Export Invoice Changes for Eastern Europe
	 */
	private BigDecimal calculateNetPricePerBaseUomInEur(OrderItemEntity orderItem, BigDecimal netPricePerBaseUom) {
		BigDecimal netPricePerBaseUomInEur = null;

		final SapContractEntity sapContract = orderItem.getSapContractItem().getSapContract();
		final String contractCurrencyCd = sapContract.getCurrency().getCurrencyCd();
		final String toCurrencyCd = Constant.CURRENCY_CODE_EUR;

		if (contractCurrencyCd.equals(toCurrencyCd)) {
			return null;
		}

		// Get the contract currency exchange rate
		final BigDecimal contractCurrencyExchangeRate = fetchContractCurrencyExchRate(contractCurrencyCd);

		// Get the exchange rate for EUR
		final BigDecimal toCurrencyExchangeRate = fetchContractCurrencyExchRate(toCurrencyCd);

		// Sanity check
		if (contractCurrencyExchangeRate.equals(BigDecimal.ZERO) || toCurrencyExchangeRate.equals(BigDecimal.ZERO)) {
			return null;
		}

		// Currently the EUR is the base currency the other currency exchange rates are stored compared to EUR.
		if (toCurrencyExchangeRate.equals(Constant.BASE_CURRENCY_EXCHANGE_RATE)) {
			netPricePerBaseUomInEur = netPricePerBaseUom.divide(contractCurrencyExchangeRate, Constant.MONEY_SCALE,
					Constant.MONEY_ROUNDING_MODE);
		}
		else if (contractCurrencyExchangeRate.equals(Constant.BASE_CURRENCY_EXCHANGE_RATE)) {
			netPricePerBaseUomInEur = netPricePerBaseUom.multiply(toCurrencyExchangeRate);
		}
		else {
			throw new LogonException("Could not convert the net price per base UoM from '" + contractCurrencyCd
					+ "' currency to '" + "' currency.", Reason.RES_ERROR_IMPL);
		}

		return netPricePerBaseUomInEur;
	}

	/**
	 * Returns the valid exchange rate of the currency.
	 *
	 * @param contractCurrencyCd
	 * @return the exchange rate of the most recent CurrencyExchRateEntity for teh given currency code.
	 * If there is no valid CurrencyExchRateEntity, returns BigDecimal.ZERO.
	 */
	private BigDecimal fetchContractCurrencyExchRate(String contractCurrencyCd) {
		final ReadOnlyDao<CurrencyExchRateEntity> currencyExchRateEntityDao = getDaoProvider().getCurrencyExchRateEntityDao();
		final Filter<CurrencyExchRateEntity> filter = currencyExchRateEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(CurrencyExchRateEntity.ATTR_CURRENCY_CD, contractCurrencyCd));
		filter.addExpression(Restrictions.le(CurrencyExchRateEntity.ATTR_VALID_FROM_DAT, new Date())); // FIXME (paksyd Aug 24, 2013): Is this timezone friendly?
		filter.addExpression(Order.asc(CurrencyExchRateEntity.ATTR_CURRENCY_CD));
		filter.addExpression(Order.desc(CurrencyExchRateEntity.ATTR_VALID_FROM_DAT));

		final List<CurrencyExchRateEntity> currencyExchRateEntities = currencyExchRateEntityDao.find(filter);
		if (currencyExchRateEntities.isEmpty()) {
			LOG.warn("Could not find valid CurrencyExchRateEntity for currency code: " + contractCurrencyCd);
			return BigDecimal.ZERO;
		}
		final CurrencyExchRateEntity contractCurrencyExchRateEntity = currencyExchRateEntities.get(0);
		final BigDecimal exchangeRate = contractCurrencyExchRateEntity.getExchangeRate();
		return exchangeRate;
	}

	/**
	 * 	Calculates the net price per base Unit of Measure for an order item.
	 * See [LOGON-6636] "LRQ 012.3 Cash Sales Invoice Form" specification:
	 * 20130719_SDS_Logon_block5_LRQ_012.3_Cash_Sales_Invoice_Form_V1_5_20130719.pdf
	 * 2.6.1.2 "Price Type in Material Lines" section for the calculation logic.
	 * @param sapMaterial - orderItems material for which we calculate the net price per base uom
	 * @param netValue - orderItems netValue
	 * @param targetQty - orderItems target quantity
	 * @param targetQuantityUom - orderItems target quantity uom
	 * @return - converted net price per base uom
	 */
	private BigDecimal calculateNetPricePerBaseUoM(SapMaterialEntity sapMaterial, BigDecimal netValue,
			BigDecimal targetQty, UomBsEntity targetQuantityUom) {

		BigDecimal netPricePerBaseUom = BigDecimal.ZERO;

		final String contractItemTargetQuantityUomCd = targetQuantityUom.getUomCd();

		final UomBsEntity sapMaterialBaseUom = sapMaterial.getBaseUom();
		final String sapMaterialBaseUomCd = sapMaterialBaseUom.getUomCd();

		if (contractItemTargetQuantityUomCd.equals(sapMaterialBaseUomCd)) {
			netPricePerBaseUom = netValue.divide(targetQty, Constant.MONEY_SCALE, Constant.MONEY_ROUNDING_MODE);
		}
		else {
			boolean allowFuzzyQty = sapMaterialBaseUom.getFuzzyQuantityFlag()
					|| targetQuantityUom.getFuzzyQuantityFlag();

			try {
				final UomConversionResult uomConversionResult = unitOfMeasurementConverterService.convert(targetQty,
						targetQuantityUom, sapMaterialBaseUom, sapMaterial, null, null, allowFuzzyQty);

				final BigDecimal convertedQuantity = uomConversionResult.getQuantity();
				netPricePerBaseUom = netValue.divide(convertedQuantity, Constant.MONEY_SCALE,
						Constant.MONEY_ROUNDING_MODE);

			}
			catch (LogonConversionException e) {
				throw new LogonException("Could not convert ", e);
			}
		}

		return netPricePerBaseUom;
	}

	public void addAutoaddMaterial(OrderEntity order) {
		AutoaddMaterialHelper autoAddMaterialHelper = new AutoaddMaterialHelper();
		try {
			autoAddMaterialHelper.autoAddItems(order, bestellungService);
		}
		catch (AutoaddMaterialHelper.AutoaddMaterialException e) {
			SapMaterialDescEntity fetchSapMaterialDesc = bestellungService.fetchSapMaterialDesc(
					e.getAutoaddSapMaterialId(), Locale.ENGLISH);
			LOG.error("An autoadd material for the material "
					+ fetchSapMaterialDesc.getTxt()
					+ " ("
					+ fetchSapMaterialDesc.getSapMaterialNo()
					+ ")"
					+ " should be added to this order but does not exist in the contract or is not maintained for the sales area or not generic material. Check material masterdata in SAP and LOGON."
					+ e.getDetails());
		}
	}

	/**
	 * Creates a new assignment for an order onto a simple vehicle,
	 * or merges a new assignment into an existing one.
	 * This method creates a new assignment for a simple vehicle for the given order.
	 * If there is already an existing one, it is merged with the new one,
	 * so that there is always at most one active assignment for a simple
	 * vehicle. The assignment is always for internal orders; landfill materials
	 * are not supported.
	 * @param order A simple vehicle order (i.e. delivery type "ship" or "block train"
	 * for which no assignment has been made yet.
	 * @param vehicle A simple vehicle that is not yet shipping, i.e., it does must
	 * not have any active assignments in state "shipping".
	 */
	public Long assignOrderToSimpleVehicle(OrderEntity order, SimpleVehicleEntity vehicle) {
		DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
		Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
		final boolean isShipOrBlocktrainPickup = LogonUtil.isShipPickupOrder(deliveryTypeCd)
				|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd);
		final AssignmentEntity newAssignment;

		if (!isShipOrBlocktrainPickup) {
			/* First find any existing active assignment. There can always
			 * only be at most one active assignment on a simple vehicle, so
			 * if there is an existing one, we will have to merge it with the
			 * newly created one.
			 */
			AssignmentEntity existingAssignment = simpleVehicleSchedulingService.findActiveAssignmentForVehicle(vehicle);

			newAssignment = createNewSimpleVehicleAssignment(order, vehicle);

			if (existingAssignment != null) {
				// If loading has already begun, it is no longer possible to assign another order.
				final Long existingStatus;
				existingStatus = existingAssignment.getAssignStatus().getAssignStatusCd();

				if (existingStatus.equals(Constant.ASSIGNMENT_STATUS_SHIPPING)) {
					throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_ASSG_ALREADY_LOADING);
				}

				/* Merge the existing assignment into the new one,
				 * because that will place the assignment parts of the existing
				 * assignment before the respective parts of the new assignment,
				 * meaning effectively that the existing assignment will be
				 * unloaded first, which was probably the intention.
				 */
				mergeAssignments(existingAssignment.getAssignmentId(), newAssignment.getAssignmentId(), null);
			}
		}
		else {
			newAssignment = createNewSimpleVehicleAssignment(order, vehicle);
		}
		order.setExecCarrier(vehicle.getCarrier());

		// create dispatched biz. event
		final BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DISPATCHED);
		event.setOrder(order);
		event.setAssignment(newAssignment);
		event.setEventTs(new Date());
		monitoringTablesService.processBizevent(event);

		changeOrderStatus(order, Constant.ORDER_STATUS_DISPATCHED);

		return newAssignment.getAssignmentId();
	}

	/**
	 * Creates and persists a new assignment for an order on a simple vehicle.
	 * When you use this method, please make sure that afterwards you merge
	 * the new assignment with an existing one (if there is one), so that
	 * there always is at most one active assignment on the simple vehicle.
	 * @param order A simple vehicle order (i.e. delivery type "ship" or "block train"
	 * for which no assignment has been made yet.
	 * @param vehicle A simple vehicle that is not yet shipping, i.e., it does must
	 * not have any active assignments in state "shipping".
	 * @return the ID of the newly created assignment
	 */
	private AssignmentEntity createNewSimpleVehicleAssignment(OrderEntity order, SimpleVehicleEntity vehicle) {
		AssignmentEntity newAssignment = new AssignmentEntity();
		newAssignment.setManuallyScheduled(Boolean.TRUE);
		newAssignment.setSimpleVehicle(vehicle);
		newAssignment.setActiveFlag(Boolean.TRUE);
		newAssignment.setAssignmentType(referentialDataService.findBsEntity(AssignmentTypeBsEntity.class,
				Constant.ASSIGNMENT_TYPE_INTERNAL));
		newAssignment.setAssignmentDat(order.getDeliveryFromDat()); // TODO (marius Nov 14, 2012): Is this correct?
		newAssignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_NEW);
		newAssignment.setAssignTrStatus(referentialDataService.findBsEntity(AssignTrStatusBsEntity.class,
				Constant.ASSIGN_STATUS_TR_NOT_TRANSMITTED));
		newAssignment.setCarrierDispatchingNote(order.getCarrierDispatchingNote());
		newAssignment.setCarrierTruckDriverNote(order.getCarrierTruckDriverNote());
		newAssignment.setAssignmentNumber(fetchSequenceNextVal("SLGNC_ASSIGNMENT_NUMBER"));

		// TODO (marius Nov 14, 2012): How do we set the transport duration for simple vehicle assignments?
		final int transportDuration = Constant.TMP_TRANSPORT_DURATION_MIN.intValue();
		final Date deliveryStart = getDeliveryStartTime(order);

		/* marius Nov 14, 2012: Very ugly solution to pass a DAO to the
		 * assignment part maker. However, as I see it, there is something fundamentally
		 * incompatible here: I want a helper class with its own mutable state, in this case
		 * an assignment maker that keeps track of the assignment part sequence number and start
		 * times internally, so that we don't have to worry about this. However, because of
		 * limitations (or features???) of GDI-JPA, the created load entity of each "load"
		 * part must be persisted explicitly (it won't be persisted automatically
		 * if the assignment is persisted). This means that my helper class must have access
		 * to the persistence layer, i.e., to a DAO.
		 *
		 * The nicer solution would be if persisting the assignment would
		 * propagate and also persist the load, just like the assignment parts
		 * are persisted. Unfortunately, GDI-JPA supports propagation only for
		 * 1:N relations, not for 1:0..1.
		 */
		final AssignmentUtils.AssignmentPartMaker partMaker = AssignmentUtils.createPartMaker(newAssignment, deliveryStart,
				referentialDataService, getDaoProvider().getLoadEntityDao());

		final Collection<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
		for (OrderItemEntity item : orderItems) {
			partMaker.addSimpleVehicleLoadPart(vehicle, item, order.getShippingpoint(),
					fetchSequenceNextVal("SLGNC_LOAD_NUMBER"));
		}

		partMaker.addSimpleVehicleToPart(order.getShippingpoint(), transportDuration);
		partMaker.addSimpleVehicleUnloadPart(order, findShiptoBySapBpId(order));
		partMaker.addSimpleVehicleFromPart(order, findShiptoBySapBpId(order), transportDuration);
		partMaker.addSimpleVehicleFinishedPart(order.getShippingpoint());

		// Finally persist the new assignment.
		newAssignment = getDaoProvider().getAssignmentEntityDao().persist(newAssignment);
		return newAssignment;
	}

	/**
	 * Computes the start time of delivery for an order, based on the order parameters.
	 * This can be used to set the start time of the first assignment part
	 * when creating the assignment for an order.
	 * @return The latest time at which delivery should start.
	 */
	private Date getDeliveryStartTime(OrderEntity order) {
		final ShiptoEntity shipto = dispokarteService.fetchShiptoBySapId(order.getShiptoSapBp().getSapId());
		final TimeZone timeZone = timeZoneService.loadForShipTo(shipto, Locale.ENGLISH);
		return SchedulingUtils.calculateLatestDeliveryStart(order, getConfigBean(), shipto, null, timeZone);
	}

	/**
	 * Updates the gross weight of the load by either taking the pre-calculated value from
	 * the weighing system, or if that is not available, we calculate it by ourselves from
	 * the tare weight and net weight.
	 *
	 * <p>NOTE 1: the changes are not persisted by this method, has to be done by the caller!</p>
	 *
	 * <p>NOTE 2: if the <code>grossWeighT</code> parameter is unspecified, the gross weight of
	 * the load will be set to <code>null</code> if one of the following is true:
	 *
	 * <ul>
	 *     <li>there is no tare weight or net quantity,</li>
	 *     <li>net quantity is not convertible to tons,</li>
	 * </ul></p>
	 *
	 * @param load
	 * @param grossWeightT pre-calculated gross weight coming from the weighing system
	 */
	public void updateGrossWeightT(LoadEntity load, BigDecimal grossWeightT) {
		if (grossWeightT != null) {
			load.setGrossWeightT(grossWeightT);
			return;
		}
		BigDecimal tareWeightT = load.getTareWeightT();
		BigDecimal netQuantity = load.getNetQuantity();

		if ((tareWeightT != null) && (netQuantity != null)) {
			UomBsEntity netQuantityUom = load.getNetQuantityUom();
			String netQuantityUomCd = netQuantityUom.getUomCd();
			SapMaterialEntity sapMaterial = load.getSapMaterial(); // Not nullable!
			BigDecimal netQuantityT;

			if (netQuantityUomCd.equals(Constant.UOM_TONS)) {
				netQuantityT = netQuantity;
			}
			else {
				UomBsEntity uomTons = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
				try {
					UomConversionResult ucr = unitOfMeasurementConverterService.convert(netQuantity, netQuantityUom,
							uomTons, sapMaterial, null, null, false);
					netQuantityT = ucr.getQuantity();
				}
				catch (LogonConversionException e) {
					LOG.warn("Cannot calculate gross weight due to conversion error: " + "net quantity in UoM "
							+ netQuantityUomCd + " cannot be converted to tons  (sapMaterialId = "
							+ sapMaterial.getSapMaterialId() + "): " + e.getMessage());
					netQuantityT = null;
				}
			}

			if (netQuantityT != null) {
				grossWeightT = tareWeightT.add(netQuantityT);
			}
		}

		load.setGrossWeightT(grossWeightT);
	}

	/**
	 * Utility to get the updated gross also from the web tier, where the LoadEntity is passed by
	 * copy and not by reference.
	 * @param load
	 * @param grossWeightT pre-calculated gross weight coming from the weighing system
	 * @return The calculated gross weight in tons
	 */
	public BigDecimal getUpdatedGrossWeightT(LoadEntity load, BigDecimal grossWeightT) {
		updateGrossWeightT(load, grossWeightT);
		return load.getGrossWeightT();
	}

	/**
	 * Returns true if the a pricing call has to be performed before order taking.
	 *
	 * From LRQ_012.0:
	 *
	 * <table border="1">
	 * <tr>
	 * 		<td>
	 * 			-
	 * 		</td>
	 * 		<td>
	 * 			Title
	 * 		</td>
	 * 		<td>
	 * 			Definition
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td>
	 * 			0
	 * 		</td>
	 * 		<td>
	 * 			Cash Sales contract
	 * 		</td>
	 * 		<td>
	 *
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td>
	 * 			1
	 * 		</td>
	 * 		<td>
	 * 			One time Ship-to
	 * 		</td>
	 * 		<td>
	 * 			Always use the pricing call if the Ship-To is a One-Time-Shipto.<br /><br />
				<b>Identification:</b><br />
				* BP used in Partner function Ship-to has the flag tlgns_Sap_bp.TREAT_AS_ONETIME_FLAG set to true.<br />
				<b>Origin SAP:</b><br />
				* Technical flag. Set in SAP import to true in case the BP is received with 'Z011' (one time customer) or 'Z004' (one time shipto) in field master_customer_basic_data / KTOKD.
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td>
	 * 			2
	 * 		</td>
	 * 		<td>
	 * 			Generic Material
	 * 		</td>
	 * 		<td>
	 * 			Cash Sales for Generic materials is not supported.
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td>
	 * 			3
	 * 		</td>
	 * 		<td>
	 * 			Contract of Sold-to equals to Ship-to
	 * 		</td>
	 * 		<td>
	 * 			Always use the pricing call if the contract originates from a contract where Sold-to equals Ship-to. Such a contract can then be used for any Ship-To for that Sold-To.<br /><br />
				<b>Identification:</b><br />
				* Table: Tlgns_sap_contract.soldto_sap_bp_id = shipto_sap_bp_id
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td>
	 * 			4
	 * 		</td>
	 * 		<td>
	 * 			Contract price information missing
	 * 		</td>
	 * 		<td>
	 * 			If one of the contract item properties is missing/null do a pricing lookup:
	 *			=> TARGET_QUANTITY
	 * 			=> TARGET_QUANTITY_UOM_CD
	 * 			=> VAT_PERCENTAGE
	 * 			=> NET_VALUE
	 * 		</td>
	 * </tr>
	 * </table>
	 *
	 * @return
	 */
	public boolean isPricingCallRequired(OrderEntity order) {
		SapContractEntity sapContract = order.getSapContract();
		if (sapContract == null) {
			return false;
		}

		SapBpEntity shiptoSapBp = sapContract.getShiptoSapBp();
		SapBpEntity soldtoSapBp = sapContract.getSoldtoSapBp();

		if (OrderUtils.isPayerIgnored(sapContract)) {
			return false;
		}

		SareaEntity sarea = order.getSarea();

		if (!sarea.getEnablePricingCallFlag()) {
			return false;
		}

		if (isCashSales(sapContract, sarea)) {
			return false;
		}

		if (LogonUtil.isLandfill(order)) {
			return false;
		}

		DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
		Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();

		if (LogonUtil.isServiceOnlyOrder(deliveryTypeCd)) {
			return false;
		}

		if (LogonUtil.isReturnPalletOnly(deliveryTypeCd)) {
			return false;
		}

		boolean result = false;
		List<OrderItemEntity> orderItems = order.getOrderItems();
		for (OrderItemEntity orderItem : orderItems) {
			// If there is at least one order for which we nee the pricing call
			if (isPricingCallRequiredForOrderItem(orderItem, sarea)) {
				result = true;
				break;
			}
		}

		// If not missing contract item attributes were found
		if (shiptoSapBp.getTreatAsOnetimeFlag() || soldtoSapBp.getSapBpId().equals(shiptoSapBp.getSapBpId())) {
			result = true;
		}

		return result;
	}

	/**
	 * @see #isPricingCallRequired(OrderEntity)
	 * @param orderItem - item for which we decide if we need a pricing call
	 * @return true when pricing call is needed
	 */
	public boolean isPricingCallRequiredForOrderItem(OrderItemEntity orderItem, SareaEntity sarea) {
		SapContractItemEntity sapContractItem = orderItem.getSapContractItem();
		if (sapContractItem == null) {
			return false;
		}
		SapMaterialEntity sapMaterial = sapContractItem.getSapMaterial();
		if (LogonUtil.isPlanningMaterial(sapMaterial)) {
			return false;
		}
		if (LogonPackageHandlingUtil.isService(sapMaterial)) {
			return false;
		}
		if (sapContractItem.getTransportCostIncludedFlag()) {
			return true;
		}
		SapMaterialSareaEntity sapMaterialSarea = fetchSapMaterialSarea(sapMaterial, sarea);
		if (sapMaterialSarea.getIsGenericFlag()) {
			return false;
		}
		if (sapContractItem.getTargetQuantity() == null || sapContractItem.getTargetQuantityUom() == null
				|| sapContractItem.getVatPercentage() == null || sapContractItem.getNetValue() == null) {
			return true;
		}
		return false;
	}

	/**
	 * Looks up a {@code SapMaterialSareaEntity} using the sapmaterialid of the sapMaterial and the
	 * DISTR_CHAN and DIVISION of the sarea.
	 * @param sapMaterial
	 * @param sarea
	 * @return entity which corresponds to the DISTR_CHAN and DIVISION
	 */
	public SapMaterialSareaEntity fetchSapMaterialSarea(SapMaterialEntity sapMaterial, SareaEntity sarea) {
		ReadOnlyDao<SapMaterialSareaEntity> sapMaterialSareaDao = getDaoProvider().getSapMaterialSareaEntityDao();
		Filter<SapMaterialSareaEntity> sapMaterialSareaFilter = sapMaterialSareaDao.createFilter();
		sapMaterialSareaFilter.addExpression(Restrictions.eq(SapMaterialSareaEntity.ATTR_SAP_MATERIAL_ID,
				sapMaterial.getSapMaterialId()));
		SapMaterialUtil.addSareaRestriction(sapMaterialSareaFilter, sarea);
		Authorizer.disableNextAccessRestriction();
		SapMaterialSareaEntity sapMaterialSarea = sapMaterialSareaDao.findSingle(sapMaterialSareaFilter);
		return sapMaterialSarea;
	}

	public OrderDeliveryMinMaxDateDTO fetchOrderDeliveryTimeIntervalEntity(Long custorderId, Long orderId) {
		return orderStore.findMinMaxOrderDeliveryFromToDate(custorderId, orderId);
	}

	/**
	 * Sets the default return reason for returned pallets, where the returned quantity is not null, but
	 * the return reason is null
	 * @param orders
	 */
	public void setDefaultReturnReasonForPallets(List<OrderEntity> orders) {
		if (orders == null || orders.isEmpty()) {
			return;
		}

		ReturnOrderReasonBsEntity defaultReturnReason = referentialDataService.findBsEntity(ReturnOrderReasonBsEntity.class,SalesDocumentDTO.ORDER_REASON_VOLUNTARY_RETURN);

		for (OrderEntity order : orders) {
			for (OrderItemEntity orderItem : order.getOrderItems()) {
				if (LogonPackageHandlingUtil.isPallet(orderItem) &&
						orderItem.getReturnedQuantity() != null &&
						orderItem.getReturnOrderReason() == null) {
					orderItem.setReturnOrderReason(defaultReturnReason);
				}
			}
		}
	}

	/**
	 * @param truck
	 */
	private void detachCurrentTruckAndTrailerFromContainer(ContainerEntity container ) {
		if(container != null) {
			truckAdminService.releaseContainer(container, false);
		}
	}

}


