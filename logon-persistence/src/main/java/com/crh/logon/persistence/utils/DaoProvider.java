package com.crh.logon.persistence.utils;

import javax.persistence.EntityManager;

import ch.nevis.gdi.jpa.Authorizer;
import ch.nevis.gdi.jpa.DirectJpaDao;
import ch.nevis.gdi.jpa.DirectJpaDaoImpl;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadOnlyDaoImpl;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.ReadWriteDaoImpl;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.admin.UserPoolAccEntity;
import com.crh.logon.persistence.entities.admin.UserPurchorgAccEntity;
import com.crh.logon.persistence.entities.common.*;
import com.crh.logon.persistence.entities.sap.MatpkgMapCondEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.views.*;
import com.crh.logon.persistence.entities.views.stagingarea.MnnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.SdnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.TgdiaTestTypesEntity;
import com.crh.logon.persistence.entities.admin.ExtLoginPwdHistoryEntity;
import com.crh.logon.persistence.entities.admin.FuncEntity;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.admin.RoleFuncEntity;
import com.crh.logon.persistence.entities.admin.UserActivityEntity;
import com.crh.logon.persistence.entities.admin.UserCompanycodeAccEntity;
import com.crh.logon.persistence.entities.admin.UserRegionAccEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.admin.UserPlantAccEntity;
import com.crh.logon.persistence.entities.admin.UserRoleEntity;
import com.crh.logon.persistence.entities.admin.UserSapVendorAccEntity;
import com.crh.logon.persistence.entities.admin.UserSareaAccEntity;
import com.crh.logon.persistence.entities.admin.UserSgroupAccEntity;
import com.crh.logon.persistence.entities.admin.UserShippingpointAccEntity;
import com.crh.logon.persistence.entities.admin.UserShiptoAccEntity;
import com.crh.logon.persistence.entities.admin.UserSoldtoBpAccEntity;
import com.crh.logon.persistence.entities.frozen.OrderFinalEntity;
import com.crh.logon.persistence.entities.frozen.OrderItemFinalEntity;
import com.crh.logon.persistence.entities.monitoring.ContainerPositionEntity;
import com.crh.logon.persistence.entities.monitoring.TrailerPositionEntity;
import com.crh.logon.persistence.entities.monitoring.TruckPositionEntity;
import com.crh.logon.persistence.entities.monitoring.WagonPositionEntity;
import com.crh.logon.persistence.entities.refdata.SapTpSapVendorPfBsEntity;
import com.crh.logon.persistence.entities.sap.MatpkgMapRuleEntity;
import com.crh.logon.persistence.entities.sap.SapBpCompanycodeEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpPfBilltoEntity;
import com.crh.logon.persistence.entities.sap.SapBpPfPayerEntity;
import com.crh.logon.persistence.entities.sap.SapBpPfShiptoEntity;
import com.crh.logon.persistence.entities.sap.SapBpPfSoldtoEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMatPlantNormEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialSareaEntity;
import com.crh.logon.persistence.entities.sap.SapNetpriceOrderItemEntity;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.sap.SapVendorPorgEntity;
import com.crh.logon.persistence.entities.sap.SapVnPfCommCarrierEntity;
import com.crh.logon.persistence.entities.sap.SapVnPfExecCarrierEntity;
import com.crh.logon.persistence.entities.tablebrowser.ColumnMeta;
import com.crh.logon.persistence.entities.tablebrowser.TableMeta;
import com.crh.logon.persistence.entities.views.stagingarea.BpnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.MnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.VehnStagingAreaNewDataAvailableEntity;
import com.crh.logon.persistence.entities.views.stagingarea.VnStagingAreaNewDataAvailableEntity;

/**
 * @author AdNovum Informatik AG
 */
public class DaoProvider {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DaoProvider.class);

	protected EntityManager em;

	protected Authorizer authorizer;

	private DirectJpaDao directJpaDao;

	private ReadWriteDao<TgdiaTestTypesEntity> tgdiaTestTypesEntityDao;

	private ReadWriteDao<ExtLoginPwdHistoryEntity> extLoginPwdHistoryEntityDao;

	private ReadWriteDao<FuncEntity> funcEntityDao;

	private ReadWriteDao<PreferencesEntity> preferencesEntityDao;

	private ReadWriteDao<RoleEntity> roleEntityDao;

	private ReadWriteDao<RoleFuncEntity> roleFuncEntityDao;

	private ReadWriteDao<UserActivityEntity> userActivityEntityDao;

	private ReadWriteDao<UserCompanycodeAccEntity> userCompanycodeAccEntityDao;

	private ReadWriteDao<UserRegionAccEntity> userRegionAccEntityDao;

	private ReadWriteDao<UserEntity> userEntityDao;

	private ReadWriteDao<UserPlantAccEntity> userPlantAccEntityDao;

	private ReadWriteDao<UserPoolAccEntity> userPoolAccEntityDao;

	private ReadWriteDao<UserPurchorgAccEntity> userPurchorgAccEntityDao;

	private ReadWriteDao<UserRoleEntity> userRoleEntityDao;

	private ReadWriteDao<UserSapVendorAccEntity> userSapVendorAccEntityDao;

	private ReadWriteDao<UserSareaAccEntity> userSareaAccEntityDao;

	private ReadWriteDao<UserSgroupAccEntity> userSgroupAccEntityDao;

	private ReadWriteDao<UserShippingpointAccEntity> userShippingpointAccEntityDao;

	private ReadWriteDao<UserShiptoAccEntity> userShiptoAccEntityDao;

	private ReadWriteDao<UserSoldtoBpAccEntity> userSoldtoBpAccEntityDao;

	private ReadWriteDao<ArchivingLogEntity> archivingLogEntityDao;

	private ReadWriteDao<AssignmentEntity> assignmentEntityDao;

	private ReadWriteDao<AssignmentPartEntity> assignmentPartEntityDao;

	private ReadWriteDao<AuditEntity> auditEntityDao;

	private ReadWriteDao<BizeventAuditEntity> bizeventAuditEntityDao;

	private ReadWriteDao<BizeventEntity> bizeventEntityDao;

	private ReadWriteDao<CarrierEntity> carrierEntityDao;

	private ReadWriteDao<ConfSeqCounterEntity> confSeqCounterEntityDao;

	private ReadWriteDao<ConfigLoginModuleEntity> configLoginModuleEntityDao;

	private ReadWriteDao<ContainerEntity> containerEntityDao;

	private ReadWriteDao<ContainerHistoryEntity> containerHistoryEntityDao;

	private ReadWriteDao<CreditNoteEntity> creditNoteEntityDao;

	private ReadWriteDao<CronstatusEntity> cronstatusEntityDao;

	private ReadWriteDao<CurrencyExchRateEntity> currencyExchRateEntityDao;

	private ReadWriteDao<CustorderEntity> custorderEntityDao;

	private ReadWriteDao<CustorderItemEntity> custorderItemEntityDao;

	private ReadWriteDao<CustorderOutputdocEntity> custorderOutputdocEntityDao;

	private ReadWriteDao<DailyReportEntity> dailyReportEntityDao;

	private ReadWriteDao<DbVersionLogEntity> dbVersionLogEntityDao;

	private ReadWriteDao<DriverBreakEntity> driverBreakEntityDao;

	private ReadWriteDao<DriverEntity> driverEntityDao;

	private ReadWriteDao<DriverWaitingreasonEntity> driverWaitingreasonEntityDao;

	private ReadWriteDao<ExtMaterialEntity> extMaterialEntityDao;

	private ReadWriteDao<ExtOrderEntity> extOrderEntityDao;

	private ReadWriteDao<ExtSiteEntity> extSiteEntityDao;

	private ReadWriteDao<FireqMaterialMixdesignEntity> fireqMaterialMixdesignEntityDao;

	private ReadWriteDao<FreightCostEntity> freightCostEntityDao;

	private ReadWriteDao<FuelingEntity> fuelingEntityDao;

	private ReadWriteDao<GoodsMovementEntity> goodsMovementEntityDao;

	private ReadWriteDao<ImageEntity> imageEntityDao;

	private ReadWriteDao<IudCostEntity> iudCostEntityDao;

	private ReadWriteDao<LdpointMatRestrEntity> ldpointMatRestrEntityDao;

	private ReadWriteDao<LoadEntity> loadEntityDao;

	private ReadWriteDao<LoadingpointEntity> loadingpointEntityDao;

	private ReadWriteDao<LogEntity> logEntityDao;

	private ReadWriteDao<MaintRhythmDependEntity> maintRhythmDependEntityDao;

	private ReadWriteDao<MaintRhythmEntity> maintRhythmEntityDao;

	private ReadWriteDao<MatCompatEntity> matCompatEntityDao;

	private ReadWriteDao<MatCompatMatEntity> matCompatMatEntityDao;

	private ReadWriteDao<MatPlantEntity> matPlantEntityDao;

	private ReadWriteDao<MatPlantTxEntity> matPlantTxEntityDao;

	private ReadWriteDao<MatShippingpointEntity> matShippingpointEntityDao;

	private ReadWriteDao<MopeventDataInEntity> mopeventDataInEntityDao;

	private ReadWriteDao<MopeventDataOutEntity> mopeventDataOutEntityDao;

	private ReadWriteDao<MopeventHeaderInEntity> mopeventHeaderInEntityDao;

	private ReadWriteDao<MopeventHeaderOutEntity> mopeventHeaderOutEntityDao;

	private ReadWriteDao<OrderEntity> orderEntityDao;

	private ReadWriteDao<OrderItemContractItemEntity> orderItemContractItemEntityDao;

	private ReadWriteDao<ContractItemOrderItemListEntity> contractItemOrderItemEntityDao;

	private ReadWriteDao<OrderItemEntity> orderItemEntityDao;

	private ReadWriteDao<OrderOutputdocEntity> orderOutputdocEntityDao;

	private ReadWriteDao<OrderPrescheduleEntity> orderPrescheduleEntityDao;

	private ReadWriteDao<OrderRepAttEntity> orderRepAttEntityDao;

	private ReadWriteDao<OrderRepDmgcodeTypeEntity> orderRepDmgcodeTypeEntityDao;

	private ReadWriteDao<OrderRepEntity> orderRepEntityDao;

	private ReadWriteDao<OrderRepMaintTypeEntity> orderRepMaintTypeEntityDao;

	private ReadWriteDao<OrderTpSapVendorEntity> orderTpSapVendorEntityDao;

	private ReadWriteDao<OrderTruckTypeEntity> orderTruckTypeEntityDao;

	private ReadWriteDao<OutputdocEntity> outputdocEntityDao;

	private ReadWriteDao<PdaDeviceEntity> pdaDeviceEntityDao;

	private ReadWriteDao<PdaMessageEntity> pdaMessageEntityDao;

	private ReadWriteDao<PdaSessionEntity> pdaSessionEntityDao;

	private ReadWriteDao<PersonEntity> personEntityDao;

	private ReadWriteDao<PickupLoadingpointEntity> pickupLoadingpointEntityDao;

	private ReadWriteDao<PlantCompcodeEntity> plantCompcodeEntityDao;

	private ReadWriteDao<PlantSareaEntity> plantSareaEntityDao;

	private ReadWriteDao<PoolCarrierEntity> poolCarrierEntityDao;

	private ReadWriteDao<RegionPoolEntity> regionPoolEntityDao;

	private ReadWriteDao<PoolEntity> poolEntityDao;

	private ReadWriteDao<PoolTruckEntity> poolTruckEntityDao;

	private ReadWriteDao<PoolTxEntity> poolTxEntityDao;

	private ReadWriteDao<PricingCallEntity> pricingCallEntityDao;

	private ReadWriteDao<RailDistanceEntity> railDistanceEntityDao;

	private ReadWriteDao<RailDistanceViewEntity> railDistanceViewEntityDao;

	private ReadWriteDao<RailRouteEntity> railRouteEntityDao;

	private ReadWriteDao<RailTariffEntity> railTariffEntityDao;

	private ReadWriteDao<RailTimetableEntity> railTimetableEntityDao;

	private ReadWriteDao<RailwayStationCommEntity> railwayStationCommEntityDao;

	private ReadWriteDao<RailwayStationEntity> railwayStationEntityDao;

	private ReadWriteDao<RfidobjectEntity> rfidobjectEntityDao;

	private ReadWriteDao<RfidtagEntity> rfidtagEntityDao;

	private ReadWriteDao<SareaEntity> sareaEntityDao;

	private ReadWriteDao<SapTpSapVendorPfBsEntity> sapTpSapVendorPfBsEntityDao;

	private ReadWriteDao<ShippingpointEntity> shippingpointEntityDao;

	private ReadWriteDao<ShippingpointLoadtargetEntity> shippingpointLoadtargetEntityDao;

	private ReadWriteDao<ShiptoBilltoEntity> shiptoBilltoEntityDao;

	private ReadWriteDao<ShiptoEntity> shiptoEntityDao;

	private ReadWriteDao<ShiptoPersonEntity> shiptoPersonEntityDao;

	private ReadWriteDao<ShiptoSareaEntity> shiptoSareaEntityDao;

	private ReadWriteDao<ShiptoSareaRestrBtrulEntity> shiptoSareaRestrBtrulEntityDao;

	private ReadWriteDao<ShiptoSareaRestrEntity> shiptoSareaRestrEntityDao;

	private ReadWriteDao<ShiptoSiloEntity> shiptoSiloEntityDao;

	private ReadWriteDao<ShiptoTruckTypeEntity> shiptoTruckTypeEntityDao;

	private ReadWriteDao<ShiptoWagonTypeEntity> shiptoWagonTypeEntityDao;

	private ReadWriteDao<ShsareaRestrTruckTypeEntity> shsareaRestrTruckTypeEntityDao;

	private ReadWriteDao<SignatureEntity> signatureEntityDao;

	private ReadWriteDao<SimpleVehicleEntity> simpleVehicleEntityDao;

	private ReadWriteDao<TpSapVendorEntity> tpSapVendorEntityDao;

	private ReadWriteDao<TrackLoadingpointEntity> trackLoadingpointEntityDao;

	private ReadWriteDao<TrafficJamEntity> trafficJamEntityDao;

	private ReadWriteDao<TrailerEntity> trailerEntityDao;

	private ReadWriteDao<TrspZonePolygonEntity> trspZonePolygonEntityDao;

	private ReadWriteDao<TrspZoneVertexEntity> trspZoneVertexEntityDao;

	private ReadWriteDao<TruckEntity> truckEntityDao;

	private ReadWriteDao<TruckRouteEntity> truckRouteEntityDao;

	private ReadWriteDao<TruckTrailerHistoryEntity> truckTrailerHistoryEntityDao;

	private ReadWriteDao<TruckUnavailabilityEntity> truckUnavailabilityEntityDao;

	private ReadWriteDao<WagonDmgcodeTypeEntity> wagonDmgcodeTypeEntityDao;

	private ReadWriteDao<WagonEntity> wagonEntityDao;

	private ReadWriteDao<WagonEventEntity> wagonEventEntityDao;

	private ReadWriteDao<WagonKeeperEntity> wagonKeeperEntityDao;

	private ReadWriteDao<WagonMaintEntity> wagonMaintEntityDao;

	private ReadWriteDao<WagonRepAttEntity> wagonRepAttEntityDao;

	private ReadWriteDao<OrderFinalEntity> orderFinalEntityDao;

	private ReadWriteDao<OrderItemFinalEntity> orderItemFinalEntityDao;

	private ReadWriteDao<ContainerPositionEntity> containerPositionEntityDao;

	private ReadWriteDao<TrailerPositionEntity> trailerPositionEntityDao;

	private ReadWriteDao<TruckPositionEntity> truckPositionEntityDao;

	private ReadWriteDao<WagonPositionEntity> wagonPositionEntityDao;

	private ReadWriteDao<MatpkgMapCondEntity> matpkgMapCondEntityDao;

	private ReadWriteDao<MatpkgMapRuleEntity> matpkgMapRuleEntityDao;

	private ReadWriteDao<SapBpCompanycodeEntity> sapBpCompanycodeEntityDao;

	private ReadWriteDao<SapBpEntity> sapBpEntityDao;

	private ReadWriteDao<SapBpPfBilltoEntity> sapBpPfBilltoEntityDao;

	private ReadWriteDao<SapBpPfPayerEntity> sapBpPfPayerEntityDao;

	private ReadWriteDao<SapBpPfShiptoEntity> sapBpPfShiptoEntityDao;

	private ReadWriteDao<SapBpPfSoldtoEntity> sapBpPfSoldtoEntityDao;

	private ReadWriteDao<SapBpSareaEntity> sapBpSareaEntityDao;

	private ReadWriteDao<SapContractEntity> sapContractEntityDao;

	private ReadWriteDao<SapContractItemEntity> sapContractItemEntityDao;

	private ReadWriteDao<SapMatPlantNormEntity> sapMatPlantNormEntityDao;

	private ReadWriteDao<SapMaterialAltUomEntity> sapMaterialAltUomEntityDao;

	private ReadWriteDao<SapMaterialDescriptionEntity> sapMaterialDescriptionEntityDao;

	private ReadWriteDao<SapMaterialEntity> sapMaterialEntityDao;

	private ReadWriteDao<SapMaterialPlantEntity> sapMaterialPlantEntityDao;

	private ReadWriteDao<SapMaterialSareaEntity> sapMaterialSareaEntityDao;

	private ReadWriteDao<SapNetpriceOrderItemEntity> sapNetpriceOrderItemEntityDao;

	private ReadWriteDao<SapVehicleEntity> sapVehicleEntityDao;

	private ReadWriteDao<SapVendorEntity> sapVendorEntityDao;

	private ReadWriteDao<SapVendorPorgEntity> sapVendorPorgEntityDao;

	private ReadWriteDao<SapVnPfCommCarrierEntity> sapVnPfCommCarrierEntityDao;

	private ReadWriteDao<SapVnPfExecCarrierEntity> sapVnPfExecCarrierEntityDao;

	private ReadWriteDao<ColumnMeta> columnMetaDao;

	private ReadWriteDao<TableMeta> tableMetaDao;

	private ReadWriteDao<DemandPlanEntity> demandPlanEntityDao;

	private ReadOnlyDao<ArchivingMetadataEntity> archivingMetadataEntityDao;

	private ReadOnlyDao<ArchivingOrderLogEntity> archivingOrderLogEntityDao;

	private ReadOnlyDao<ArchivingPdfEntity> archivingPdfEntityDao;

	private ReadOnlyDao<BagTerminalEntity> bagTerminalEntityDao;

	private ReadOnlyDao<BimodalRailOrderEntity> bimodalRailOrderEntityDao;

	private ReadOnlyDao<BizeventAuditDisplayEntity> bizeventAuditDisplayEntityDao;

	private ReadOnlyDao<BizeventMapEntity> bizeventMapEntityDao;

	private ReadOnlyDao<BizeventOrderLogEntity> bizeventOrderLogEntityDao;

	private ReadOnlyDao<BizeventTruckLogEntity> bizeventTruckLogEntityDao;

	private ReadOnlyDao<BpSareaSofficeSgroupEntity> bpSareaSofficeSgroupEntityDao;

	private ReadOnlyDao<CarrierSvendorEntity> carrierSvendorEntityDao;

	private ReadOnlyDao<CarrierVendorEntity> carrierVendorEntityDao;

	private ReadOnlyDao<ChartLoadScheduledEntity> chartLoadScheduledEntityDao;

	private ReadOnlyDao<ChartLoadUnscheduledEntity> chartLoadUnscheduledEntityDao;

	private ReadOnlyDao<ColumnInfoEntity> columnInfoEntityDao;

	private ReadOnlyDao<CommExecCarrierPfEntity> commExecCarrierPfEntityDao;

	private ReadOnlyDao<CommExecCarrPfActiveEntity> commExecCarrActiveEntityDao;

	private ReadOnlyDao<CommonSareaMapEntity> commonSareaMapEntityDao;

	private ReadOnlyDao<CompCodeSareaEntity> compCodeSareaEntityDao;

	private ReadOnlyDao<ContainerMonitoringEntity> containerMonitoringEntityDao;

	private ReadOnlyDao<ContractItemQuantityEntity> contractItemQuantityEntityDao;

	private ReadOnlyDao<CreditNoteProposalsEntity> creditNoteProposalsEntityDao;

	private ReadOnlyDao<CurrentExchangeRateEntity> currentExchangeRateEntityDao;

	private ReadOnlyDao<CustordItemSumQtyExtEntity> custordItemSumQtyExtEntityDao;

	private ReadOnlyDao<CustordOnetimeDataEntity> custordOnetimeDataEntityDao;

	private ReadOnlyDao<CustorderCreateEntity> custorderCreateEntityDao;

	private ReadOnlyDao<CustorderFinishedEntity> custorderFinishedEntityDao;

	private ReadOnlyDao<CustorderItemSumQtyEntity> custorderItemSumQtyEntityDao;

	private ReadOnlyDao<CustorderViewEntity> custorderViewEntityDao;

	private ReadOnlyDao<DamagecodeSearchEntity> damagecodeSearchEntityDao;

	private ReadOnlyDao<DispatchingCarrierEntity> dispatchingCarrierEntityDao;

	private ReadOnlyDao<DispoAssignPartListEntity> dispoAssignPartListEntityDao;

	private ReadOnlyDao<DispoAssignPoolCarrEntity> dispoAssignPoolCarrEntityDao;

	private ReadOnlyDao<DispoAssignTruckEntity> dispoAssignTruckEntityDao;

	private ReadOnlyDao<DispoAssignmentListEntity> dispoAssignmentListEntityDao;

	private ReadOnlyDao<DispoDayLoadsEntity> dispoDayLoadsEntityDao;

	private ReadOnlyDao<DispoDriverBreakListEntity> dispoDriverBreakListEntityDao;

	private ReadOnlyDao<DispoExtOrderListEntity> dispoExtOrderListEntityDao;

	private ReadOnlyDao<DispoHomePlantEntity> dispoHomePlantEntityDao;

	private ReadOnlyDao<DispoLoadtargetEntity> dispoLoadtargetEntityDao;

	private ReadOnlyDao<DispoMaxWagonPosEntity> dispoMaxWagonPosEntityDao;

	private ReadOnlyDao<DispoMessageListEntity> dispoMessageListEntityDao;

	private ReadOnlyDao<DispoOrderItemListEntity> dispoOrderItemListEntityDao;

	private ReadOnlyDao<DispoOrderListEntity> dispoOrderListEntityDao;

	private ReadOnlyDao<DispoPlanningEntity> dispoPlanningEntityDao;

	private ReadOnlyDao<DispoPlantTrackListEntity> dispoPlantTrackListEntityDao;

	private ReadOnlyDao<DispoRailAssListEntity> dispoRailAssListEntityDao;

	private ReadOnlyDao<DispoShiptoSoldtoEntity> dispoShiptoSoldtoEntityDao;

	private ReadOnlyDao<DispoTruckListEntity> dispoTruckListEntityDao;

	private ReadOnlyDao<DispoTruckListV2Entity> dispoTruckListV2EntityDao;

	private ReadOnlyDao<DispoTruckChangeListEntity> dispoTruckChangeListEntityDao;

	private ReadOnlyDao<DispoTruckLoadListEntity> dispoTruckLoadListEntityDao;

	private ReadOnlyDao<DispoTruckLoadListV2Entity> dispoTruckLoadListV2EntityDao;

	private ReadOnlyDao<DispoTruckTypeListEntity> dispoTruckTypeListEntityDao;

	private ReadOnlyDao<DispoWContainerListEntity> dispoWContainerListEntityDao;

	private ReadOnlyDao<DispoWagonApartListEntity> dispoWagonApartListEntityDao;

	private ReadOnlyDao<DispoWagonListEntity> dispoWagonListEntityDao;

	private ReadOnlyDao<DispoWagonLoadListEntity> dispoWagonLoadListEntityDao;

	private ReadOnlyDao<DispoWagonTrspCatEntity> dispoWagonTrspCatEntityDao;

	private ReadOnlyDao<DispoWagonTypeListEntity> dispoWagonTypeListEntityDao;

	private ReadOnlyDao<DriverPersonEntity> driverPersonEntityDao;

	private ReadOnlyDao<DriverWithoutSessionEntity> driverWithoutSessionEntityDao;

	private ReadOnlyDao<EditbpSoldshipSareaEntity> editbpSoldshipSareaEntityDao;

	private ReadOnlyDao<ExtOrderSearchEntity> extOrderSearchEntityDao;

	private ReadOnlyDao<FreeTrucksEntity> freeTrucksEntityDao;

	private ReadOnlyDao<FvlSapBpEntity> fvlSapBpEntityDao;

	private ReadOnlyDao<GoodsMovementHelperEntity> goodsMovementHelperEntityDao;

	private ReadOnlyDao<LdsServerDropdownEntity> ldsServerDropdownEntityDao;

	private ReadOnlyDao<LocationStationsInUseEntity> locationStationsInUseEntityDao;

	private ReadOnlyDao<ManPoolAssignmentListEntity> manPoolAssignmentListEntityDao;

	private ReadOnlyDao<MatPlantMissingEntity> matPlantMissingEntityDao;

	private ReadOnlyDao<MaterialCompatEntity> materialCompatEntityDao;

	private ReadOnlyDao<MaterialEnhancementsEntity> materialEnhancementsEntityDao;

	private ReadOnlyDao<OrdChartNotAssignedEntity> ordChartNotAssignedEntityDao;

	private ReadOnlyDao<OrdItemAssignmtLoadEntity> ordItemAssignmtLoadEntityDao;

	private ReadOnlyDao<OrdLoadSchedInfoEntity> ordLoadSchedInfoEntityDao;

	private ReadOnlyDao<OrdUnloadSchedInfoEntity> ordUnloadSchedInfoEntityDao;

	private ReadOnlyDao<OrderCreateEntity> orderCreateEntityDao;

	private ReadOnlyDao<OrderFiltersEntity> orderFiltersEntityDao;

	private ReadOnlyDao<OrderFinishedEntity> orderFinishedEntityDao;

	private ReadOnlyDao<OrderItemMarginEntity> orderItemMarginEntityDao;

	private ReadOnlyDao<OrderItemOrderedQtyEntity> orderItemOrderedQtyEntityDao;

	private ReadOnlyDao<OrderItemQuantityEntity> orderItemQuantityEntityDao;

	private ReadOnlyDao<OrderMatPointsEntity> orderMatPointsEntityDao;

	private ReadOnlyDao<OrderMonitoringEntity> orderMonitoringEntityDao;

	private ReadOnlyDao<OrderShippNotificationEntity> orderShippNotificationEntityDao;

	private ReadOnlyDao<OrderReadyToFinishEntity> orderReadyToFinishEntityDao;

	private ReadOnlyDao<OrderSearchEntity> orderSearchEntityDao;

	private ReadOnlyDao<OrderSearchExportEntity> orderSearchExportEntityDao;

	private ReadOnlyDao<OutputdocAccessEntity> outputdocAccessEntityDao;

	private ReadOnlyDao<OrderSearchStatEntity> orderSearchStatEntityDao;

	private ReadOnlyDao<PdaDeviceCarrierEntity> pdaDeviceCarrierEntityDao;

	private ReadOnlyDao<PickupTerminalEntity> pickupTerminalEntityDao;

	private ReadOnlyDao<PlantRailwayStationEntity> plantRailwayStationEntityDao;

	private ReadOnlyDao<PlantSareaCompcodeEntity> plantSareaCompcodeEntityDao;

	private ReadOnlyDao<PlantShippingpointEntity> plantShippingpointEntityDao;

	private ReadOnlyDao<PoolAllCarrierEntity> poolAllCarrierEntityDao;

	private ReadOnlyDao<PoolCarrierCarrierCntEntity> poolCarrierCarrierCntEntityDao;

	private ReadOnlyDao<PoolCarrierCarrierResEntity> poolCarrierCarrierResEntityDao;

	private ReadOnlyDao<PoolCarrierCarrierEntity> poolCarrierCarrierEntityDao;

	private ReadOnlyDao<PoolCommCarrierEntity> poolCommCarrierEntityDao;

	private ReadOnlyDao<PoolShiptoMatSoldtoEntity> poolShiptoMatSoldtoEntityDao;

	private ReadOnlyDao<ProdPlantNameEntity> prodPlantNameEntityDao;

	private ReadOnlyDao<RailChartEntity> railChartEntityDao;

	private ReadOnlyDao<RailPlantEntity> railPlantEntityDao;

	private ReadOnlyDao<RailPlantSpEntity> railPlantSpEntityDao;

	private ReadOnlyDao<RailRepSoldtoShiptoEntity> railRepSoldtoShiptoEntityDao;

	private ReadOnlyDao<RailRepairEntity> railRepairEntityDao;

	private ReadOnlyDao<RailSpecialAssignmentEntity> railSpecialAssignmentEntityDao;

	private ReadOnlyDao<RailcarOptLoadedCarsEntity> railcarOptLoadedCarsEntityDao;

	private ReadOnlyDao<RailcarOptOpenOrdersEntity> railcarOptOpenOrdersEntityDao;

	private ReadOnlyDao<RepExternalOrderEntity> repExternalOrderEntityDao;

	private ReadOnlyDao<RepGoodsmovementEntity> repGoodsmovementEntityDao;

	private ReadOnlyDao<RepLoadShippingStatsEntity> repLoadShippingStatsEntityDao;

	private ReadOnlyDao<RepOPerRouteCarrierEntity> repOPerRouteCarrierEntityDao;

	private ReadOnlyDao<RepOrderListCarrierEntity> repOrderListCarrierEntityDao;

	private ReadOnlyDao<RepOrderListCommEntity> repOrderListCommEntityDao;

	private ReadOnlyDao<RepOrderListCustEntity> repOrderListCustEntityDao;

	private ReadOnlyDao<RepOrderListEntity> repOrderListEntityDao;

	private ReadOnlyDao<RepOrderListCrhEntity> repOrderListCrhEntityDao;

	private ReadOnlyDao<RepOrderPerCarrierEntity> repOrderPerCarrierEntityDao;

	private ReadOnlyDao<RepOrderPerCustEntity> repOrderPerCustEntityDao;

	private ReadOnlyDao<RepOrderPerPlantMatEntity> repOrderPerPlantMatEntityDao;

	private ReadOnlyDao<RepOrderPerPoolEntity> repOrderPerPoolEntityDao;

	private ReadOnlyDao<RepOrderPerRouteEntity> repOrderPerRouteEntityDao;

	private ReadOnlyDao<RepOrderPerSpMatpkgEntity> repOrderPerSpMatpkgEntityDao;

	private ReadOnlyDao<RepOrderWinSpedEntity> repOrderWinSpedEntityDao;

	private ReadOnlyDao<RepSalesPerCustEntity> repSalesPerCustEntityDao;

	private ReadOnlyDao<RepSalesPerCustMatEntity> repSalesPerCustMatEntityDao;

	private ReadOnlyDao<RepSalesPerMatEntity> repSalesPerMatEntityDao;

	private ReadOnlyDao<RepSapBwExportEntity> repSapBwExportEntityDao;

	private ReadOnlyDao<RepSapimportErrorBpnEntity> repSapimportErrorBpnEntityDao;

	private ReadOnlyDao<RepSapimportErrorMnEntity> repSapimportErrorMnEntityDao;

	private ReadOnlyDao<RepSapimportErrorVnEntity> repSapimportErrorVnEntityDao;

	private ReadOnlyDao<RepTransPerPlantDtEntity> repTransPerPlantDtEntityDao;

	private ReadOnlyDao<RepTransPerPlantEntity> repTransPerPlantEntityDao;

	private ReadOnlyDao<RepTransplusEntity> repTransplusEntityDao;

	private ReadOnlyDao<RequireFreezingListEntity> requireFreezingListEntityDao;

	private ReadOnlyDao<ReturnStationsInUseEntity> returnStationsInUseEntityDao;

	private ReadOnlyDao<RfidobjectSiloAttachEntity> rfidobjectSiloAttachEntityDao;

	private ReadOnlyDao<RfidtagDetailEntity> rfidtagDetailEntityDao;

	private ReadOnlyDao<SapBpStagingAreaEntity> sapBpStagingAreaEntityDao;

	private ReadOnlyDao<SapContractDispEntity> sapContractDispEntityDao;

	private ReadOnlyDao<SapContractItemDispEntity> sapContractItemDispEntityDao;

	private ReadOnlyDao<SapFinancialDataEntity> sapFinancialDataEntityDao;

	private ReadOnlyDao<SapMatDescPackEntity> sapMatDescPackEntityDao;

	private ReadOnlyDao<SapMatMixdesignEntity> sapMatMixdesignEntityDao;

	private ReadOnlyDao<SapMatPlantNormDescEntity> sapMatPlantNormDescEntityDao;

	private ReadOnlyDao<SapMatStagingAreaEntity> sapMatStagingAreaEntityDao;

	private ReadOnlyDao<SapMaterialDescEntity> sapMaterialDescEntityDao;

	private ReadOnlyDao<SapMaterialPlantDescEntity> sapMaterialPlantDescEntityDao;

	private ReadOnlyDao<SapMaterialPlantSareaEntity> sapMaterialPlantSareaEntityDao;

	private ReadOnlyDao<SapNetpOrderItemSumEntity> sapNetpOrderItemSumEntityDao;

	private ReadOnlyDao<SapSoldBillSareaSrchEntity> sapSoldBillSareaSrchEntityDao;

	private ReadOnlyDao<SapSoldPaySareaSrchEntity> sapSoldPaySareaSrchEntityDao;

	private ReadOnlyDao<SapSoldtoSrchEntity> sapSoldtoSrchEntityDao;

	private ReadOnlyDao<SapVendorStagingAreaEntity> sapVendorStagingAreaEntityDao;

	private ReadOnlyDao<SapbpRailwayStationEntity> sapbpRailwayStationEntityDao;

	private ReadOnlyDao<SareaDisplayEntity> sareaDisplayEntityDao;

	private ReadOnlyDao<SdoOnetimeDataEntity> sdoOnetimeDataEntityDao;

	private ReadOnlyDao<SentContractItemQtyEntity> sentContractItemQtyEntityDao;

	private ReadOnlyDao<ContractItemQtyEntity> contractItemQtyEntityDao;

	private ReadOnlyDao<ShipmentdocAssignmentEntity> shipmentdocAssignmentEntityDao;

	private ReadOnlyDao<ShippBimodalRoadRailEntity> shippBimodalRoadRailEntityDao;

	private ReadOnlyDao<ShippingStatEntity> shippingStatEntityDao;

	private ReadOnlyDao<ShippingpointMaterialEntity> shippingpointMaterialEntityDao;

	private ReadOnlyDao<ShiptoSapBpEntity> shiptoSapBpEntityDao;

	private ReadOnlyDao<ShiptoSareaDetailEntity> shiptoSareaDetailEntityDao;

	private ReadOnlyDao<ShiptoSareaRestrSearEntity> shiptoSareaRestrSearEntityDao;

	private ReadOnlyDao<ShiptoSoldtoLnfDataEntity> shiptoSoldtoLnfDataEntityDao;

	private ReadOnlyDao<ShiptoWagonPrioFactorEntity> shiptoWagonPrioFactorEntityDao;

	private ReadOnlyDao<SimpleVehicleAssgEntity> simpleVehicleAssgEntityDao;

	private ReadOnlyDao<SimpvehLoadOrderItemEntity> simpvehLoadOrderItemEntityDao;

	private ReadOnlyDao<SimpvehLoadingpointEntity> simpvehLoadingpointEntityDao;

	private ReadOnlyDao<SimpvehOrdItemMatEntity> simpvehOrdItemMatEntityDao;

	private ReadOnlyDao<SimpvehOrdVhcEntity> simpvehOrdVhcEntityDao;

	private ReadOnlyDao<SimpvehUnalcOrdEntity> simpvehUnalcOrdEntityDao;

	private ReadOnlyDao<SoldshipBestFlexSrchEntity> soldshipBestFlexSrchEntityDao;

	private ReadOnlyDao<SoldshipFlexSrchEntity> soldshipFlexSrchEntityDao;

	private ReadOnlyDao<SoldshipSareaFlexSrchEntity> soldshipSareaFlexSrchEntityDao;

	private ReadOnlyDao<TableInfoEntity> tableInfoEntityDao;

	private ReadOnlyDao<TestSpDropdownEntity> testSpDropdownEntityDao;

	private ReadOnlyDao<TimezoneEntity> timezoneEntityDao;

	private ReadOnlyDao<TruckAssignmentEntity> truckAssignmentEntityDao;

	private ReadOnlyDao<TruckChartAssignedEntity> truckChartAssignedEntityDao;

	private ReadOnlyDao<TruckLastKnownCoordEntity> truckLastKnownCoordEntityDao;

	private ReadOnlyDao<TruckRevenuePlanMatEntity> truckRevenuePlanMatEntityDao;

	private ReadOnlyDao<TruckRevenuePlanningEntity> truckRevenuePlanningEntityDao;

	private ReadOnlyDao<TruckRouteSearchEntity> truckRouteSearchEntityDao;

	private ReadOnlyDao<UserActivityLogEntity> userActivityLogEntityDao;

	private ReadOnlyDao<UserAdminEntity> userAdminEntityDao;

	private ReadOnlyDao<UserRoleSelectionEntity> userRoleSelectionEntityDao;

	private ReadOnlyDao<UtilLastLoginEntity> utilLastLoginEntityDao;

	private ReadOnlyDao<UtilMatplantMissingEntity> utilMatplantMissingEntityDao;

	private ReadOnlyDao<UtilSapmatdescMissingEntity> utilSapmatdescMissingEntityDao;

	private ReadOnlyDao<VehicleEntity> vehicleEntityDao;

	private ReadOnlyDao<VgdiaTestTypesEntity> vgdiaTestTypesEntityDao;

	private ReadOnlyDao<WagonDetailAssignmentEntity> wagonDetailAssignmentEntityDao;

	private ReadOnlyDao<WagonDetailLoadListEntity> wagonDetailLoadListEntityDao;

	private ReadOnlyDao<WagonSearchEntity> wagonSearchEntityDao;

	private ReadOnlyDao<WebbestSoldshipSareaEntity> webbestSoldshipSareaEntityDao;

	private ReadOnlyDao<WeborderShipToSoldToEntity> weborderShipToSoldToEntityDao;

	private ReadOnlyDao<IncotermDelivTypeEntity> incotermDelivTypeEntityDao;

	private ReadOnlyDao<BpnStagingAreaNewDataAvailableEntity> bpnStagingAreaNewDataAvailableEntityDao;

	private ReadOnlyDao<SdnStagingAreaNewDataAvailableEntity> sdnStagingAreaNewDataAvailableEntityDao;

	private ReadOnlyDao<MnnStagingAreaNewDataAvailableEntity> mnnStagingAreaNewDataAvailableEntityDao;

	private ReadOnlyDao<MnStagingAreaNewDataAvailableEntity> mnStagingAreaNewDataAvailableEntityDao;

	private ReadOnlyDao<VehnStagingAreaNewDataAvailableEntity> vehnStagingAreaNewDataAvailableEntityDao;

	private ReadOnlyDao<VnStagingAreaNewDataAvailableEntity> vnStagingAreaNewDataAvailableEntityDao;

	private ReadOnlyDao<PlanningTruckEntity> planningTruckEntityDao;

	private ReadOnlyDao<SapMaterialSareaInfoEntity> sapMaterialSareaInfoEntityDao;

	private ReadOnlyDao<SapMatSareaContractEntity> sapMatSareaContractEntityDao;

	private ReadOnlyDao<TpSapVendorSearchEntity> tpSapVendorSearchEntityDao;

	private ReadOnlyDao<UserTypeViewEntity> userTypeViewEntityDao;

	private ReadOnlyDao<DemandPlanShipSoldEntity> demandPlanShipSoldEntityDao;

	private ReadOnlyDao<DemandPlanPlanEntity> demandPlanPlanEntityDao;

	private ReadOnlyDao<DemandPlanMatEntity> demandPlanMatEntityDao;

	private ReadOnlyDao<DemandPlanDiffEntity> demandPlanDiffEntityDao;

	private ReadOnlyDao<UnloadsForTargetEntity> unloadsForTargetEntityDao;

	private ReadOnlyDao<ItrackShiptoSapBpEntity> itrackShiptoSapBpEntityDao;

	private ReadOnlyDao<ItrackSoldtoSapBpEntity> itrackSoldtoSapBpEntityDao;

	private ReadOnlyDao<ItrackReportEntity> itrackReportEntityDao;

	private ReadOnlyDao<OcGetOrdersViewEntity> ocGetOrdersViewEntityDao;


	public DaoProvider(EntityManager em) {
		// [[
		Object[][] params = { { "em", em } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "DaoProvider", params);
		}
		// ]]
		try {
			if (em == null) {
				throw new RuntimeException("em must not be null!");
			}
			this.em = em;
			this.authorizer = null;
			init();
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public DaoProvider(EntityManager em, Authorizer authorizer) {
		// [[
		Object[][] params = { { "em", em },
				{ "authorizer", authorizer } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "DaoProvider", params);
		}
		// ]]
		try {
			if (em == null) {
				throw new RuntimeException("em must not be null!");
			}
			this.em = em;
			this.authorizer = authorizer;
			init();
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void refresh(EntityManager entityManager) {
		if (!em.equals(entityManager)) {
			LOG.debug("container provided new EntityManager! refreshing daoprovider...");
			em = entityManager;
			init();
		}
	}

	private void init() {
		directJpaDao = null;
		tgdiaTestTypesEntityDao = null;
		extLoginPwdHistoryEntityDao = null;
		funcEntityDao = null;
		preferencesEntityDao = null;
		roleEntityDao = null;
		roleFuncEntityDao = null;
		userActivityEntityDao = null;
		userCompanycodeAccEntityDao = null;
		userRegionAccEntityDao = null;
		userEntityDao = null;
		userPlantAccEntityDao = null;
		userPoolAccEntityDao = null;
		userPurchorgAccEntityDao = null;
		userRoleEntityDao = null;
		userSapVendorAccEntityDao = null;
		userSareaAccEntityDao = null;
		userSgroupAccEntityDao = null;
		userShippingpointAccEntityDao = null;
		userShiptoAccEntityDao = null;
		userSoldtoBpAccEntityDao = null;
		archivingLogEntityDao = null;
		assignmentEntityDao = null;
		assignmentPartEntityDao = null;
		auditEntityDao = null;
		bizeventAuditEntityDao = null;
		bizeventEntityDao = null;
		carrierEntityDao = null;
		confSeqCounterEntityDao = null;
		configLoginModuleEntityDao = null;
		containerEntityDao = null;
		containerHistoryEntityDao = null;
		creditNoteEntityDao = null;
		cronstatusEntityDao = null;
		currencyExchRateEntityDao = null;
		custorderEntityDao = null;
		custorderItemEntityDao = null;
		custorderOutputdocEntityDao = null;
		dailyReportEntityDao = null;
		dbVersionLogEntityDao = null;
		driverBreakEntityDao = null;
		driverEntityDao = null;
		driverWaitingreasonEntityDao = null;
		extMaterialEntityDao = null;
		extOrderEntityDao = null;
		extSiteEntityDao = null;
		fireqMaterialMixdesignEntityDao = null;
		freightCostEntityDao = null;
		fuelingEntityDao = null;
		goodsMovementEntityDao = null;
		imageEntityDao = null;
		iudCostEntityDao = null;
		ldpointMatRestrEntityDao = null;
		loadEntityDao = null;
		loadingpointEntityDao = null;
		logEntityDao = null;
		maintRhythmDependEntityDao = null;
		maintRhythmEntityDao = null;
		matCompatEntityDao = null;
		matCompatMatEntityDao = null;
		matPlantEntityDao = null;
		matPlantTxEntityDao = null;
		matShippingpointEntityDao = null;
		mopeventDataInEntityDao = null;
		mopeventDataOutEntityDao = null;
		mopeventHeaderInEntityDao = null;
		mopeventHeaderOutEntityDao = null;
		orderEntityDao = null;
		orderItemContractItemEntityDao = null;
		contractItemOrderItemEntityDao = null;
		orderItemEntityDao = null;
		orderOutputdocEntityDao = null;
		orderPrescheduleEntityDao = null;
		orderRepAttEntityDao = null;
		orderRepDmgcodeTypeEntityDao = null;
		orderRepEntityDao = null;
		orderRepMaintTypeEntityDao = null;
		orderTruckTypeEntityDao = null;
		outputdocEntityDao = null;
		pdaDeviceEntityDao = null;
		pdaMessageEntityDao = null;
		pdaSessionEntityDao = null;
		personEntityDao = null;
		pickupLoadingpointEntityDao = null;
		plantCompcodeEntityDao = null;
		plantSareaEntityDao = null;
		poolCarrierEntityDao = null;
		regionPoolEntityDao = null;
		poolEntityDao = null;
		poolTruckEntityDao = null;
		poolTxEntityDao = null;
		poolCommCarrierEntityDao = null;
		pricingCallEntityDao = null;
		railDistanceEntityDao = null;
		railDistanceViewEntityDao = null;
		railRouteEntityDao = null;
		railTariffEntityDao = null;
		railTimetableEntityDao = null;
		railwayStationCommEntityDao = null;
		railwayStationEntityDao = null;
		rfidobjectEntityDao = null;
		rfidtagEntityDao = null;
		sareaEntityDao = null;
		shippingpointEntityDao = null;
		shippingpointLoadtargetEntityDao = null;
		shiptoBilltoEntityDao = null;
		shiptoEntityDao = null;
		shiptoPersonEntityDao = null;
		shiptoSareaEntityDao = null;
		shiptoSareaRestrBtrulEntityDao = null;
		shiptoSareaRestrEntityDao = null;
		shiptoSiloEntityDao = null;
		shiptoTruckTypeEntityDao = null;
		shiptoWagonTypeEntityDao = null;
		shsareaRestrTruckTypeEntityDao = null;
		signatureEntityDao = null;
		simpleVehicleEntityDao = null;
		trackLoadingpointEntityDao = null;
		trafficJamEntityDao = null;
		trailerEntityDao = null;
		trspZonePolygonEntityDao = null;
		trspZoneVertexEntityDao = null;
		truckEntityDao = null;
		truckRouteEntityDao = null;
		truckTrailerHistoryEntityDao = null;
		truckUnavailabilityEntityDao = null;
		wagonDmgcodeTypeEntityDao = null;
		wagonEntityDao = null;
		wagonEventEntityDao = null;
		wagonKeeperEntityDao = null;
		wagonMaintEntityDao = null;
		wagonRepAttEntityDao = null;
		orderFinalEntityDao = null;
		orderItemFinalEntityDao = null;
		containerPositionEntityDao = null;
		trailerPositionEntityDao = null;
		truckPositionEntityDao = null;
		wagonPositionEntityDao = null;
		matpkgMapCondEntityDao = null;
		matpkgMapRuleEntityDao = null;
		sapBpCompanycodeEntityDao = null;
		sapBpEntityDao = null;
		sapBpPfBilltoEntityDao = null;
		sapBpPfPayerEntityDao = null;
		sapBpPfShiptoEntityDao = null;
		sapBpPfSoldtoEntityDao = null;
		sapBpSareaEntityDao = null;
		sapContractEntityDao = null;
		sapContractItemEntityDao = null;
		sapMatPlantNormEntityDao = null;
		sapMaterialAltUomEntityDao = null;
		sapMaterialDescriptionEntityDao = null;
		sapMaterialEntityDao = null;
		sapMaterialPlantEntityDao = null;
		sapMaterialSareaEntityDao = null;
		sapNetpriceOrderItemEntityDao = null;
		sapVehicleEntityDao = null;
		sapVendorEntityDao = null;
		sapVendorPorgEntityDao = null;
		sapVnPfCommCarrierEntityDao = null;
		sapVnPfExecCarrierEntityDao = null;
		columnMetaDao = null;
		tableMetaDao = null;
		demandPlanEntityDao = null;
		archivingMetadataEntityDao = null;
		archivingOrderLogEntityDao = null;
		archivingPdfEntityDao = null;
		bagTerminalEntityDao = null;
		bimodalRailOrderEntityDao = null;
		bizeventAuditDisplayEntityDao = null;
		bizeventMapEntityDao = null;
		bizeventOrderLogEntityDao = null;
		bizeventTruckLogEntityDao = null;
		bpSareaSofficeSgroupEntityDao = null;
		carrierSvendorEntityDao = null;
		carrierVendorEntityDao = null;
		chartLoadScheduledEntityDao = null;
		chartLoadUnscheduledEntityDao = null;
		columnInfoEntityDao = null;
		commExecCarrierPfEntityDao = null;
		commExecCarrActiveEntityDao = null;
		commonSareaMapEntityDao = null;
		compCodeSareaEntityDao = null;
		containerMonitoringEntityDao = null;
		contractItemQuantityEntityDao = null;
		creditNoteProposalsEntityDao = null;
		currentExchangeRateEntityDao = null;
		custordItemSumQtyExtEntityDao = null;
		custordOnetimeDataEntityDao = null;
		custorderCreateEntityDao = null;
		custorderFinishedEntityDao = null;
		custorderItemSumQtyEntityDao = null;
		custorderViewEntityDao = null;
		damagecodeSearchEntityDao = null;
		dispatchingCarrierEntityDao = null;
		dispoAssignPartListEntityDao = null;
		dispoAssignPoolCarrEntityDao = null;
		dispoAssignTruckEntityDao = null;
		dispoAssignmentListEntityDao = null;
		dispoDayLoadsEntityDao = null;
		dispoDriverBreakListEntityDao = null;
		dispoExtOrderListEntityDao = null;
		dispoHomePlantEntityDao = null;
		dispoLoadtargetEntityDao = null;
		dispoMaxWagonPosEntityDao = null;
		dispoMessageListEntityDao = null;
		dispoOrderItemListEntityDao = null;
		dispoOrderListEntityDao = null;
		dispoPlanningEntityDao = null;
		dispoPlantTrackListEntityDao = null;
		dispoRailAssListEntityDao = null;
		dispoShiptoSoldtoEntityDao = null;
		dispoTruckListEntityDao = null;
		dispoTruckListV2EntityDao = null;
		dispoTruckChangeListEntityDao = null;
		dispoTruckLoadListEntityDao = null;
		dispoTruckLoadListV2EntityDao = null;
		dispoTruckTypeListEntityDao = null;
		dispoWContainerListEntityDao = null;
		dispoWagonApartListEntityDao = null;
		dispoWagonListEntityDao = null;
		dispoWagonLoadListEntityDao = null;
		dispoWagonTrspCatEntityDao = null;
		dispoWagonTypeListEntityDao = null;
		driverPersonEntityDao = null;
		driverWithoutSessionEntityDao = null;
		editbpSoldshipSareaEntityDao = null;
		extOrderSearchEntityDao = null;
		freeTrucksEntityDao = null;
		fvlSapBpEntityDao = null;
		goodsMovementHelperEntityDao = null;
		ldsServerDropdownEntityDao = null;
		locationStationsInUseEntityDao = null;
		manPoolAssignmentListEntityDao = null;
		matPlantMissingEntityDao = null;
		materialCompatEntityDao = null;
		materialEnhancementsEntityDao = null;
		ordChartNotAssignedEntityDao = null;
		ordItemAssignmtLoadEntityDao = null;
		ordLoadSchedInfoEntityDao = null;
		ordUnloadSchedInfoEntityDao = null;
		orderCreateEntityDao = null;
		orderFiltersEntityDao = null;
		orderFinishedEntityDao = null;
		orderItemMarginEntityDao = null;
		orderItemOrderedQtyEntityDao = null;
		orderItemQuantityEntityDao = null;
		orderMatPointsEntityDao = null;
		orderMonitoringEntityDao = null;
		orderShippNotificationEntityDao = null;
		orderReadyToFinishEntityDao = null;
		orderSearchEntityDao = null;
		orderSearchExportEntityDao = null;
		outputdocAccessEntityDao = null;
		orderSearchStatEntityDao = null;
		pdaDeviceCarrierEntityDao = null;
		pickupTerminalEntityDao = null;
		plantRailwayStationEntityDao = null;
		plantSareaCompcodeEntityDao = null;
		plantShippingpointEntityDao = null;
		poolAllCarrierEntityDao = null;
		poolCarrierCarrierCntEntityDao = null;
		poolCarrierCarrierResEntityDao = null;
		poolCarrierCarrierEntityDao = null;
		poolShiptoMatSoldtoEntityDao = null;
		prodPlantNameEntityDao = null;
		railChartEntityDao = null;
		railPlantEntityDao = null;
		railPlantSpEntityDao = null;
		railRepSoldtoShiptoEntityDao = null;
		railRepairEntityDao = null;
		railSpecialAssignmentEntityDao = null;
		railcarOptLoadedCarsEntityDao = null;
		railcarOptOpenOrdersEntityDao = null;
		repExternalOrderEntityDao = null;
		repGoodsmovementEntityDao = null;
		repLoadShippingStatsEntityDao = null;
		repOPerRouteCarrierEntityDao = null;
		repOrderListCarrierEntityDao = null;
		repOrderListCommEntityDao = null;
		repOrderListCustEntityDao = null;
		repOrderListEntityDao = null;
		repOrderListCrhEntityDao = null;
		repOrderPerCarrierEntityDao = null;
		repOrderPerCustEntityDao = null;
		repOrderPerPlantMatEntityDao = null;
		repOrderPerPoolEntityDao = null;
		repOrderPerRouteEntityDao = null;
		repOrderPerSpMatpkgEntityDao = null;
		repOrderWinSpedEntityDao = null;
		repSalesPerCustEntityDao = null;
		repSalesPerCustMatEntityDao = null;
		repSalesPerMatEntityDao = null;
		repSapBwExportEntityDao = null;
		repSapimportErrorBpnEntityDao = null;
		repSapimportErrorMnEntityDao = null;
		repSapimportErrorVnEntityDao = null;
		repTransPerPlantDtEntityDao = null;
		repTransPerPlantEntityDao = null;
		repTransplusEntityDao = null;
		requireFreezingListEntityDao = null;
		returnStationsInUseEntityDao = null;
		rfidobjectSiloAttachEntityDao = null;
		rfidtagDetailEntityDao = null;
		sapBpStagingAreaEntityDao = null;
		sapContractDispEntityDao = null;
		sapContractItemDispEntityDao = null;
		sapFinancialDataEntityDao = null;
		sapMatDescPackEntityDao = null;
		sapMatMixdesignEntityDao = null;
		sapMatPlantNormDescEntityDao = null;
		sapMatStagingAreaEntityDao = null;
		sapMaterialDescEntityDao = null;
		sapMaterialPlantDescEntityDao = null;
		sapMaterialPlantSareaEntityDao = null;
		sapNetpOrderItemSumEntityDao = null;
		sapSoldBillSareaSrchEntityDao = null;
		sapSoldPaySareaSrchEntityDao = null;
		sapSoldtoSrchEntityDao = null;
		sapVendorStagingAreaEntityDao = null;
		sapbpRailwayStationEntityDao = null;
		sareaDisplayEntityDao = null;
		sdoOnetimeDataEntityDao = null;
		sentContractItemQtyEntityDao = null;
		contractItemQtyEntityDao = null;
		shipmentdocAssignmentEntityDao = null;
		shippBimodalRoadRailEntityDao = null;
		shippingStatEntityDao = null;
		shippingpointMaterialEntityDao = null;
		shiptoSapBpEntityDao = null;
		shiptoSareaDetailEntityDao = null;
		shiptoSareaRestrSearEntityDao = null;
		shiptoSoldtoLnfDataEntityDao = null;
		shiptoWagonPrioFactorEntityDao = null;
		simpleVehicleAssgEntityDao = null;
		simpvehLoadOrderItemEntityDao = null;
		simpvehLoadingpointEntityDao = null;
		simpvehOrdItemMatEntityDao = null;
		simpvehOrdVhcEntityDao = null;
		simpvehUnalcOrdEntityDao = null;
		soldshipBestFlexSrchEntityDao = null;
		soldshipFlexSrchEntityDao = null;
		soldshipSareaFlexSrchEntityDao = null;
		tableInfoEntityDao = null;
		testSpDropdownEntityDao = null;
		timezoneEntityDao = null;
		truckAssignmentEntityDao = null;
		truckChartAssignedEntityDao = null;
		truckLastKnownCoordEntityDao = null;
		truckRevenuePlanMatEntityDao = null;
		truckRevenuePlanningEntityDao = null;
		truckRouteSearchEntityDao = null;
		userActivityLogEntityDao = null;
		userAdminEntityDao = null;
		userRoleSelectionEntityDao = null;
		utilLastLoginEntityDao = null;
		utilMatplantMissingEntityDao = null;
		utilSapmatdescMissingEntityDao = null;
		vehicleEntityDao = null;
		vgdiaTestTypesEntityDao = null;
		wagonDetailAssignmentEntityDao = null;
		wagonDetailLoadListEntityDao = null;
		wagonSearchEntityDao = null;
		webbestSoldshipSareaEntityDao = null;
		weborderShipToSoldToEntityDao = null;
		bpnStagingAreaNewDataAvailableEntityDao = null;
		sdnStagingAreaNewDataAvailableEntityDao = null;
		mnnStagingAreaNewDataAvailableEntityDao = null;
		mnStagingAreaNewDataAvailableEntityDao = null;
		vehnStagingAreaNewDataAvailableEntityDao = null;
		vnStagingAreaNewDataAvailableEntityDao = null;
		incotermDelivTypeEntityDao = null;
		planningTruckEntityDao = null;
		sapMaterialSareaInfoEntityDao = null;
		sapMatSareaContractEntityDao = null;
		orderTpSapVendorEntityDao = null;
		tpSapVendorEntityDao = null;
		sapTpSapVendorPfBsEntityDao = null;
		tpSapVendorSearchEntityDao = null;
		userTypeViewEntityDao = null;
		demandPlanShipSoldEntityDao = null;
		demandPlanPlanEntityDao = null;
		demandPlanMatEntityDao = null;
		demandPlanDiffEntityDao = null;
		unloadsForTargetEntityDao = null;
		itrackShiptoSapBpEntityDao = null;
		itrackSoldtoSapBpEntityDao = null;
		itrackReportEntityDao = null;


	}

	public DirectJpaDao getDirectJpaDao() {
		if (directJpaDao == null) {
			directJpaDao = new DirectJpaDaoImpl(em);
		}
		return directJpaDao;
	}

	public ReadWriteDao<OrderTpSapVendorEntity> getOrderTpSapVendorEntityDao() {
		if (orderTpSapVendorEntityDao == null) {
			orderTpSapVendorEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderTpSapVendorEntity.class);
		}
		return orderTpSapVendorEntityDao;
	}

	public ReadWriteDao<TpSapVendorEntity> getTpSapVendorEntityDao() {
		if (tpSapVendorEntityDao == null) {
			tpSapVendorEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TpSapVendorEntity.class);
		}
		return tpSapVendorEntityDao;
	}

	public ReadWriteDao<SapTpSapVendorPfBsEntity> getSapTpSapVendorPfBsEntityDao() {
		if (sapTpSapVendorPfBsEntityDao == null) {
			sapTpSapVendorPfBsEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapTpSapVendorPfBsEntity.class);
		}
		return sapTpSapVendorPfBsEntityDao;
	}

	public ReadOnlyDao<TpSapVendorSearchEntity> getTpSapVendorSearchEntityDao() {
		if (tpSapVendorSearchEntityDao == null) {
			tpSapVendorSearchEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TpSapVendorSearchEntity.class);
		}
		return tpSapVendorSearchEntityDao;
	}


	public ReadWriteDao<TgdiaTestTypesEntity> getTgdiaTestTypesEntityDao() {
		if (tgdiaTestTypesEntityDao == null) {
			tgdiaTestTypesEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TgdiaTestTypesEntity.class);
		}
		return tgdiaTestTypesEntityDao;
	}

	public ReadWriteDao<ExtLoginPwdHistoryEntity> getExtLoginPwdHistoryEntityDao() {
		if (extLoginPwdHistoryEntityDao == null) {
			extLoginPwdHistoryEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ExtLoginPwdHistoryEntity.class);
		}
		return extLoginPwdHistoryEntityDao;
	}

	public ReadWriteDao<FuncEntity> getFuncEntityDao() {
		if (funcEntityDao == null) {
			funcEntityDao = new ReadWriteDaoImpl<>(em, authorizer, FuncEntity.class);
		}
		return funcEntityDao;
	}

	public ReadWriteDao<PreferencesEntity> getPreferencesEntityDao() {
		if (preferencesEntityDao == null) {
			preferencesEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PreferencesEntity.class);
		}
		return preferencesEntityDao;
	}

	public ReadWriteDao<RoleEntity> getRoleEntityDao() {
		if (roleEntityDao == null) {
			roleEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RoleEntity.class);
		}
		return roleEntityDao;
	}

	public ReadWriteDao<RoleFuncEntity> getRoleFuncEntityDao() {
		if (roleFuncEntityDao == null) {
			roleFuncEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RoleFuncEntity.class);
		}
		return roleFuncEntityDao;
	}

	public ReadWriteDao<UserActivityEntity> getUserActivityEntityDao() {
		if (userActivityEntityDao == null) {
			userActivityEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserActivityEntity.class);
		}
		return userActivityEntityDao;
	}

	public ReadWriteDao<UserCompanycodeAccEntity> getUserCompanycodeAccEntityDao() {
		if (userCompanycodeAccEntityDao == null) {
			userCompanycodeAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserCompanycodeAccEntity.class);
		}
		return userCompanycodeAccEntityDao;
	}

	public ReadWriteDao<UserRegionAccEntity> getUserRegionAccEntityDao() {
		if (userRegionAccEntityDao == null) {
			userRegionAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserRegionAccEntity.class);
		}
		return userRegionAccEntityDao;
	}

	public ReadWriteDao<UserEntity> getUserEntityDao() {
		if (userEntityDao == null) {
			userEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserEntity.class);
		}
		return userEntityDao;
	}

	public ReadWriteDao<UserPlantAccEntity> getUserPlantAccEntityDao() {
		if (userPlantAccEntityDao == null) {
			userPlantAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserPlantAccEntity.class);
		}
		return userPlantAccEntityDao;
	}

	public ReadWriteDao<UserPoolAccEntity> getUserPoolAccEntityDao() {
		if (userPoolAccEntityDao == null) {
			userPoolAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserPoolAccEntity.class);
		}
		return userPoolAccEntityDao;
	}

	public ReadWriteDao<UserPurchorgAccEntity> getUserPurchorgAccEntityDao() {
		if (userPurchorgAccEntityDao == null) {
			userPurchorgAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserPurchorgAccEntity.class);
		}
		return userPurchorgAccEntityDao;
	}

	public ReadWriteDao<UserRoleEntity> getUserRoleEntityDao() {
		if (userRoleEntityDao == null) {
			userRoleEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserRoleEntity.class);
		}
		return userRoleEntityDao;
	}

	public ReadWriteDao<UserSapVendorAccEntity> getUserSapVendorAccEntityDao() {
		if (userSapVendorAccEntityDao == null) {
			userSapVendorAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserSapVendorAccEntity.class);
		}
		return userSapVendorAccEntityDao;
	}

	public ReadWriteDao<UserSareaAccEntity> getUserSareaAccEntityDao() {
		if (userSareaAccEntityDao == null) {
			userSareaAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserSareaAccEntity.class);
		}
		return userSareaAccEntityDao;
	}

	public ReadWriteDao<UserSgroupAccEntity> getUserSgroupAccEntityDao() {
		if (userSgroupAccEntityDao == null) {
			userSgroupAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserSgroupAccEntity.class);
		}
		return userSgroupAccEntityDao;
	}

	public ReadWriteDao<UserShippingpointAccEntity> getUserShippingpointAccEntityDao() {
		if (userShippingpointAccEntityDao == null) {
			userShippingpointAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserShippingpointAccEntity.class);
		}
		return userShippingpointAccEntityDao;
	}

	public ReadWriteDao<UserShiptoAccEntity> getUserShiptoAccEntityDao() {
		if (userShiptoAccEntityDao == null) {
			userShiptoAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserShiptoAccEntity.class);
		}
		return userShiptoAccEntityDao;
	}

	public ReadWriteDao<UserSoldtoBpAccEntity> getUserSoldtoBpAccEntityDao() {
		if (userSoldtoBpAccEntityDao == null) {
			userSoldtoBpAccEntityDao = new ReadWriteDaoImpl<>(em, authorizer, UserSoldtoBpAccEntity.class);
		}
		return userSoldtoBpAccEntityDao;
	}

	public ReadWriteDao<ArchivingLogEntity> getArchivingLogEntityDao() {
		if (archivingLogEntityDao == null) {
			archivingLogEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ArchivingLogEntity.class);
		}
		return archivingLogEntityDao;
	}

	public ReadWriteDao<AssignmentEntity> getAssignmentEntityDao() {
		if (assignmentEntityDao == null) {
			assignmentEntityDao = new ReadWriteDaoImpl<>(em, authorizer, AssignmentEntity.class);
		}
		return assignmentEntityDao;
	}

	public ReadWriteDao<AssignmentPartEntity> getAssignmentPartEntityDao() {
		if (assignmentPartEntityDao == null) {
			assignmentPartEntityDao = new ReadWriteDaoImpl<>(em, authorizer, AssignmentPartEntity.class);
		}
		return assignmentPartEntityDao;
	}

	public ReadWriteDao<AuditEntity> getAuditEntityDao() {
		if (auditEntityDao == null) {
			auditEntityDao = new ReadWriteDaoImpl<>(em, authorizer, AuditEntity.class);
		}
		return auditEntityDao;
	}

	public ReadWriteDao<BizeventAuditEntity> getBizeventAuditEntityDao() {
		if (bizeventAuditEntityDao == null) {
			bizeventAuditEntityDao = new ReadWriteDaoImpl<>(em, authorizer, BizeventAuditEntity.class);
		}
		return bizeventAuditEntityDao;
	}

	public ReadWriteDao<BizeventEntity> getBizeventEntityDao() {
		if (bizeventEntityDao == null) {
			bizeventEntityDao = new ReadWriteDaoImpl<>(em, authorizer, BizeventEntity.class);
		}
		return bizeventEntityDao;
	}

	public ReadWriteDao<CarrierEntity> getCarrierEntityDao() {
		if (carrierEntityDao == null) {
			carrierEntityDao = new ReadWriteDaoImpl<>(em, authorizer, CarrierEntity.class);
		}
		return carrierEntityDao;
	}

	public ReadWriteDao<ConfSeqCounterEntity> getConfSeqCounterEntityDao() {
		if (confSeqCounterEntityDao == null) {
			confSeqCounterEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ConfSeqCounterEntity.class);
		}
		return confSeqCounterEntityDao;
	}

	public ReadWriteDao<ConfigLoginModuleEntity> getConfigLoginModuleEntityDao() {
		if (configLoginModuleEntityDao == null) {
			configLoginModuleEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ConfigLoginModuleEntity.class);
		}
		return configLoginModuleEntityDao;
	}

	public ReadWriteDao<ContainerEntity> getContainerEntityDao() {
		if (containerEntityDao == null) {
			containerEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ContainerEntity.class);
		}
		return containerEntityDao;
	}

	public ReadWriteDao<ContainerHistoryEntity> getContainerHistoryEntityDao() {
		if (containerHistoryEntityDao == null) {
			containerHistoryEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ContainerHistoryEntity.class);
		}
		return containerHistoryEntityDao;
	}

	public ReadWriteDao<CreditNoteEntity> getCreditNoteEntityDao() {
		if (creditNoteEntityDao == null) {
			creditNoteEntityDao = new ReadWriteDaoImpl<>(em, authorizer, CreditNoteEntity.class);
		}
		return creditNoteEntityDao;
	}

	public ReadWriteDao<CronstatusEntity> getCronstatusEntityDao() {
		if (cronstatusEntityDao == null) {
			cronstatusEntityDao = new ReadWriteDaoImpl<>(em, authorizer, CronstatusEntity.class);
		}
		return cronstatusEntityDao;
	}

	public ReadWriteDao<CurrencyExchRateEntity> getCurrencyExchRateEntityDao() {
		if (currencyExchRateEntityDao == null) {
			currencyExchRateEntityDao = new ReadWriteDaoImpl<>(em, authorizer, CurrencyExchRateEntity.class);
		}
		return currencyExchRateEntityDao;
	}

	public ReadWriteDao<CustorderEntity> getCustorderEntityDao() {
		if (custorderEntityDao == null) {
			custorderEntityDao = new ReadWriteDaoImpl<>(em, authorizer, CustorderEntity.class);
		}
		return custorderEntityDao;
	}

	public ReadWriteDao<CustorderItemEntity> getCustorderItemEntityDao() {
		if (custorderItemEntityDao == null) {
			custorderItemEntityDao = new ReadWriteDaoImpl<>(em, authorizer, CustorderItemEntity.class);
		}
		return custorderItemEntityDao;
	}

	public ReadWriteDao<CustorderOutputdocEntity> getCustorderOutputdocEntityDao() {
		if (custorderOutputdocEntityDao == null) {
			custorderOutputdocEntityDao = new ReadWriteDaoImpl<>(em, authorizer, CustorderOutputdocEntity.class);
		}
		return custorderOutputdocEntityDao;
	}

	public ReadWriteDao<DailyReportEntity> getDailyReportEntityDao() {
		if (dailyReportEntityDao == null) {
			dailyReportEntityDao = new ReadWriteDaoImpl<>(em, authorizer, DailyReportEntity.class);
		}
		return dailyReportEntityDao;
	}

	public ReadWriteDao<DbVersionLogEntity> getDbVersionLogEntityDao() {
		if (dbVersionLogEntityDao == null) {
			dbVersionLogEntityDao = new ReadWriteDaoImpl<>(em, authorizer, DbVersionLogEntity.class);
		}
		return dbVersionLogEntityDao;
	}

	public ReadWriteDao<DriverBreakEntity> getDriverBreakEntityDao() {
		if (driverBreakEntityDao == null) {
			driverBreakEntityDao = new ReadWriteDaoImpl<>(em, authorizer, DriverBreakEntity.class);
		}
		return driverBreakEntityDao;
	}

	public ReadWriteDao<DriverEntity> getDriverEntityDao() {
		if (driverEntityDao == null) {
			driverEntityDao = new ReadWriteDaoImpl<>(em, authorizer, DriverEntity.class);
		}
		return driverEntityDao;
	}

	public ReadWriteDao<DriverWaitingreasonEntity> getDriverWaitingreasonEntityDao() {
		if (driverWaitingreasonEntityDao == null) {
			driverWaitingreasonEntityDao = new ReadWriteDaoImpl<>(em, authorizer, DriverWaitingreasonEntity.class);
		}
		return driverWaitingreasonEntityDao;
	}

	public ReadWriteDao<ExtMaterialEntity> getExtMaterialEntityDao() {
		if (extMaterialEntityDao == null) {
			extMaterialEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ExtMaterialEntity.class);
		}
		return extMaterialEntityDao;
	}

	public ReadWriteDao<ExtOrderEntity> getExtOrderEntityDao() {
		if (extOrderEntityDao == null) {
			extOrderEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ExtOrderEntity.class);
		}
		return extOrderEntityDao;
	}

	public ReadWriteDao<ExtSiteEntity> getExtSiteEntityDao() {
		if (extSiteEntityDao == null) {
			extSiteEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ExtSiteEntity.class);
		}
		return extSiteEntityDao;
	}

	public ReadWriteDao<FireqMaterialMixdesignEntity> getFireqMaterialMixdesignEntityDao() {
		if (fireqMaterialMixdesignEntityDao == null) {
			fireqMaterialMixdesignEntityDao = new ReadWriteDaoImpl<>(em, authorizer, FireqMaterialMixdesignEntity.class);
		}
		return fireqMaterialMixdesignEntityDao;
	}

	public ReadWriteDao<FreightCostEntity> getFreightCostEntityDao() {
		if (freightCostEntityDao == null) {
			freightCostEntityDao = new ReadWriteDaoImpl<>(em, authorizer, FreightCostEntity.class);
		}
		return freightCostEntityDao;
	}

	public ReadWriteDao<FuelingEntity> getFuelingEntityDao() {
		if (fuelingEntityDao == null) {
			fuelingEntityDao = new ReadWriteDaoImpl<>(em, authorizer, FuelingEntity.class);
		}
		return fuelingEntityDao;
	}

	public ReadWriteDao<GoodsMovementEntity> getGoodsMovementEntityDao() {
		if (goodsMovementEntityDao == null) {
			goodsMovementEntityDao = new ReadWriteDaoImpl<>(em, authorizer, GoodsMovementEntity.class);
		}
		return goodsMovementEntityDao;
	}

	public ReadWriteDao<ImageEntity> getImageEntityDao() {
		if (imageEntityDao == null) {
			imageEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ImageEntity.class);
		}
		return imageEntityDao;
	}

	public ReadWriteDao<IudCostEntity> getIudCostEntityDao() {
		if (iudCostEntityDao == null) {
			iudCostEntityDao = new ReadWriteDaoImpl<>(em, authorizer, IudCostEntity.class);
		}
		return iudCostEntityDao;
	}

	public ReadWriteDao<LdpointMatRestrEntity> getLdpointMatRestrEntityDao() {
		if (ldpointMatRestrEntityDao == null) {
			ldpointMatRestrEntityDao = new ReadWriteDaoImpl<>(em, authorizer, LdpointMatRestrEntity.class);
		}
		return ldpointMatRestrEntityDao;
	}

	public ReadWriteDao<LoadEntity> getLoadEntityDao() {
		if (loadEntityDao == null) {
			loadEntityDao = new ReadWriteDaoImpl<>(em, authorizer, LoadEntity.class);
		}
		return loadEntityDao;
	}

	public ReadWriteDao<LoadingpointEntity> getLoadingpointEntityDao() {
		if (loadingpointEntityDao == null) {
			loadingpointEntityDao = new ReadWriteDaoImpl<>(em, authorizer, LoadingpointEntity.class);
		}
		return loadingpointEntityDao;
	}

	public ReadWriteDao<LogEntity> getLogEntityDao() {
		if (logEntityDao == null) {
			logEntityDao = new ReadWriteDaoImpl<>(em, authorizer, LogEntity.class);
		}
		return logEntityDao;
	}

	public ReadWriteDao<MaintRhythmDependEntity> getMaintRhythmDependEntityDao() {
		if (maintRhythmDependEntityDao == null) {
			maintRhythmDependEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MaintRhythmDependEntity.class);
		}
		return maintRhythmDependEntityDao;
	}

	public ReadWriteDao<MaintRhythmEntity> getMaintRhythmEntityDao() {
		if (maintRhythmEntityDao == null) {
			maintRhythmEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MaintRhythmEntity.class);
		}
		return maintRhythmEntityDao;
	}

	public ReadWriteDao<MatCompatEntity> getMatCompatEntityDao() {
		if (matCompatEntityDao == null) {
			matCompatEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MatCompatEntity.class);
		}
		return matCompatEntityDao;
	}

	public ReadWriteDao<MatCompatMatEntity> getMatCompatMatEntityDao() {
		if (matCompatMatEntityDao == null) {
			matCompatMatEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MatCompatMatEntity.class);
		}
		return matCompatMatEntityDao;
	}

	public ReadWriteDao<MatPlantEntity> getMatPlantEntityDao() {
		if (matPlantEntityDao == null) {
			matPlantEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MatPlantEntity.class);
		}
		return matPlantEntityDao;
	}

	public ReadWriteDao<MatPlantTxEntity> getMatPlantTxEntityDao() {
		if (matPlantTxEntityDao == null) {
			matPlantTxEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MatPlantTxEntity.class);
		}
		return matPlantTxEntityDao;
	}

	public ReadWriteDao<MatShippingpointEntity> getMatShippingpointEntityDao() {
		if (matShippingpointEntityDao == null) {
			matShippingpointEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MatShippingpointEntity.class);
		}
		return matShippingpointEntityDao;
	}

	public ReadWriteDao<MopeventDataInEntity> getMopeventDataInEntityDao() {
		if (mopeventDataInEntityDao == null) {
			mopeventDataInEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MopeventDataInEntity.class);
		}
		return mopeventDataInEntityDao;
	}

	public ReadWriteDao<MopeventDataOutEntity> getMopeventDataOutEntityDao() {
		if (mopeventDataOutEntityDao == null) {
			mopeventDataOutEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MopeventDataOutEntity.class);
		}
		return mopeventDataOutEntityDao;
	}

	public ReadWriteDao<MopeventHeaderInEntity> getMopeventHeaderInEntityDao() {
		if (mopeventHeaderInEntityDao == null) {
			mopeventHeaderInEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MopeventHeaderInEntity.class);
		}
		return mopeventHeaderInEntityDao;
	}

	public ReadWriteDao<MopeventHeaderOutEntity> getMopeventHeaderOutEntityDao() {
		if (mopeventHeaderOutEntityDao == null) {
			mopeventHeaderOutEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MopeventHeaderOutEntity.class);
		}
		return mopeventHeaderOutEntityDao;
	}

	public ReadWriteDao<OrderEntity> getOrderEntityDao() {
		if (orderEntityDao == null) {
			orderEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderEntity.class);
		}
		return orderEntityDao;
	}

	public ReadWriteDao<OrderItemContractItemEntity> getOrderItemContractItemEntityDao() {
		if (orderItemContractItemEntityDao == null) {
			orderItemContractItemEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderItemContractItemEntity.class);
		}
		return orderItemContractItemEntityDao;
	}

	public ReadWriteDao<ContractItemOrderItemListEntity> getContractItemOrderItemListEntityDao() {
		if (contractItemOrderItemEntityDao == null) {
			contractItemOrderItemEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ContractItemOrderItemListEntity.class);
		}
		return contractItemOrderItemEntityDao;
	}

	public ReadWriteDao<OrderItemEntity> getOrderItemEntityDao() {
		if (orderItemEntityDao == null) {
			orderItemEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderItemEntity.class);
		}
		return orderItemEntityDao;
	}

	public ReadWriteDao<OrderOutputdocEntity> getOrderOutputdocEntityDao() {
		if (orderOutputdocEntityDao == null) {
			orderOutputdocEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderOutputdocEntity.class);
		}
		return orderOutputdocEntityDao;
	}

	public ReadWriteDao<OrderPrescheduleEntity> getOrderPrescheduleEntityDao() {
		if (orderPrescheduleEntityDao == null) {
			orderPrescheduleEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderPrescheduleEntity.class);
		}
		return orderPrescheduleEntityDao;
	}

	public ReadWriteDao<OrderRepAttEntity> getOrderRepAttEntityDao() {
		if (orderRepAttEntityDao == null) {
			orderRepAttEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderRepAttEntity.class);
		}
		return orderRepAttEntityDao;
	}

	public ReadWriteDao<OrderRepDmgcodeTypeEntity> getOrderRepDmgcodeTypeEntityDao() {
		if (orderRepDmgcodeTypeEntityDao == null) {
			orderRepDmgcodeTypeEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderRepDmgcodeTypeEntity.class);
		}
		return orderRepDmgcodeTypeEntityDao;
	}

	public ReadWriteDao<OrderRepEntity> getOrderRepEntityDao() {
		if (orderRepEntityDao == null) {
			orderRepEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderRepEntity.class);
		}
		return orderRepEntityDao;
	}

	public ReadWriteDao<OrderRepMaintTypeEntity> getOrderRepMaintTypeEntityDao() {
		if (orderRepMaintTypeEntityDao == null) {
			orderRepMaintTypeEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderRepMaintTypeEntity.class);
		}
		return orderRepMaintTypeEntityDao;
	}

	public ReadWriteDao<OrderTruckTypeEntity> getOrderTruckTypeEntityDao() {
		if (orderTruckTypeEntityDao == null) {
			orderTruckTypeEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderTruckTypeEntity.class);
		}
		return orderTruckTypeEntityDao;
	}

	public ReadWriteDao<OutputdocEntity> getOutputdocEntityDao() {
		if (outputdocEntityDao == null) {
			outputdocEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OutputdocEntity.class);
		}
		return outputdocEntityDao;
	}

	public ReadWriteDao<PdaDeviceEntity> getPdaDeviceEntityDao() {
		if (pdaDeviceEntityDao == null) {
			pdaDeviceEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PdaDeviceEntity.class);
		}
		return pdaDeviceEntityDao;
	}

	public ReadWriteDao<PdaMessageEntity> getPdaMessageEntityDao() {
		if (pdaMessageEntityDao == null) {
			pdaMessageEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PdaMessageEntity.class);
		}
		return pdaMessageEntityDao;
	}

	public ReadWriteDao<PdaSessionEntity> getPdaSessionEntityDao() {
		if (pdaSessionEntityDao == null) {
			pdaSessionEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PdaSessionEntity.class);
		}
		return pdaSessionEntityDao;
	}

	public ReadWriteDao<PersonEntity> getPersonEntityDao() {
		if (personEntityDao == null) {
			personEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PersonEntity.class);
		}
		return personEntityDao;
	}

	public ReadWriteDao<PickupLoadingpointEntity> getPickupLoadingpointEntityDao() {
		if (pickupLoadingpointEntityDao == null) {
			pickupLoadingpointEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PickupLoadingpointEntity.class);
		}
		return pickupLoadingpointEntityDao;
	}

	public ReadWriteDao<PlantCompcodeEntity> getPlantCompcodeEntityDao() {
		if (plantCompcodeEntityDao == null) {
			plantCompcodeEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PlantCompcodeEntity.class);
		}
		return plantCompcodeEntityDao;
	}

	public ReadWriteDao<PlantSareaEntity> getPlantSareaEntityDao() {
		if (plantSareaEntityDao == null) {
			plantSareaEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PlantSareaEntity.class);
		}
		return plantSareaEntityDao;
	}

	public ReadWriteDao<PoolCarrierEntity> getPoolCarrierEntityDao() {
		if (poolCarrierEntityDao == null) {
			poolCarrierEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PoolCarrierEntity.class);
		}
		return poolCarrierEntityDao;
	}

	public ReadWriteDao<RegionPoolEntity> getRegionPoolEntityDao() {
		if (regionPoolEntityDao == null) {
			regionPoolEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RegionPoolEntity.class);
		}
		return regionPoolEntityDao;
	}

	public ReadOnlyDao<PoolCommCarrierEntity> getPoolCommCarrierEntityDao() {
		if (poolCommCarrierEntityDao == null) {
			poolCommCarrierEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PoolCommCarrierEntity.class);
		}
		return poolCommCarrierEntityDao;
	}

	public ReadWriteDao<PoolEntity> getPoolEntityDao() {
		if (poolEntityDao == null) {
			poolEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PoolEntity.class);
		}
		return poolEntityDao;
	}

	public ReadWriteDao<PoolTruckEntity> getPoolTruckEntityDao() {
		if (poolTruckEntityDao == null) {
			poolTruckEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PoolTruckEntity.class);
		}
		return poolTruckEntityDao;
	}

	public ReadWriteDao<PoolTxEntity> getPoolTxEntityDao() {
		if (poolTxEntityDao == null) {
			poolTxEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PoolTxEntity.class);
		}
		return poolTxEntityDao;
	}

	public ReadWriteDao<PricingCallEntity> getPricingCallEntityDao() {
		if (pricingCallEntityDao == null) {
			pricingCallEntityDao = new ReadWriteDaoImpl<>(em, authorizer, PricingCallEntity.class);
		}
		return pricingCallEntityDao;
	}

	public ReadWriteDao<RailDistanceEntity> getRailDistanceEntityDao() {
		if (railDistanceEntityDao == null) {
			railDistanceEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RailDistanceEntity.class);
		}
		return railDistanceEntityDao;
	}

	public ReadWriteDao<RailDistanceViewEntity> getRailDistanceViewEntityDao() {
		if (railDistanceViewEntityDao == null) {
			railDistanceViewEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RailDistanceViewEntity.class);
		}
		return railDistanceViewEntityDao;
	}

	public ReadWriteDao<RailRouteEntity> getRailRouteEntityDao() {
		if (railRouteEntityDao == null) {
			railRouteEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RailRouteEntity.class);
		}
		return railRouteEntityDao;
	}

	public ReadWriteDao<RailTariffEntity> getRailTariffEntityDao() {
		if (railTariffEntityDao == null) {
			railTariffEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RailTariffEntity.class);
		}
		return railTariffEntityDao;
	}

	public ReadWriteDao<RailTimetableEntity> getRailTimetableEntityDao() {
		if (railTimetableEntityDao == null) {
			railTimetableEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RailTimetableEntity.class);
		}
		return railTimetableEntityDao;
	}

	public ReadWriteDao<RailwayStationCommEntity> getRailwayStationCommEntityDao() {
		if (railwayStationCommEntityDao == null) {
			railwayStationCommEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RailwayStationCommEntity.class);
		}
		return railwayStationCommEntityDao;
	}

	public ReadWriteDao<RailwayStationEntity> getRailwayStationEntityDao() {
		if (railwayStationEntityDao == null) {
			railwayStationEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RailwayStationEntity.class);
		}
		return railwayStationEntityDao;
	}

	public ReadWriteDao<RfidobjectEntity> getRfidobjectEntityDao() {
		if (rfidobjectEntityDao == null) {
			rfidobjectEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RfidobjectEntity.class);
		}
		return rfidobjectEntityDao;
	}

	public ReadWriteDao<RfidtagEntity> getRfidtagEntityDao() {
		if (rfidtagEntityDao == null) {
			rfidtagEntityDao = new ReadWriteDaoImpl<>(em, authorizer, RfidtagEntity.class);
		}
		return rfidtagEntityDao;
	}

	public ReadWriteDao<SareaEntity> getSareaEntityDao() {
		if (sareaEntityDao == null) {
			sareaEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SareaEntity.class);
		}
		return sareaEntityDao;
	}

	public ReadWriteDao<ShippingpointEntity> getShippingpointEntityDao() {
		if (shippingpointEntityDao == null) {
			shippingpointEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShippingpointEntity.class);
		}
		return shippingpointEntityDao;
	}

	public ReadWriteDao<ShippingpointLoadtargetEntity> getShippingpointLoadtargetEntityDao() {
		if (shippingpointLoadtargetEntityDao == null) {
			shippingpointLoadtargetEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShippingpointLoadtargetEntity.class);
		}
		return shippingpointLoadtargetEntityDao;
	}

	public ReadWriteDao<ShiptoBilltoEntity> getShiptoBilltoEntityDao() {
		if (shiptoBilltoEntityDao == null) {
			shiptoBilltoEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShiptoBilltoEntity.class);
		}
		return shiptoBilltoEntityDao;
	}

	public ReadWriteDao<ShiptoEntity> getShiptoEntityDao() {
		if (shiptoEntityDao == null) {
			shiptoEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShiptoEntity.class);
		}
		return shiptoEntityDao;
	}

	public ReadWriteDao<ShiptoPersonEntity> getShiptoPersonEntityDao() {
		if (shiptoPersonEntityDao == null) {
			shiptoPersonEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShiptoPersonEntity.class);
		}
		return shiptoPersonEntityDao;
	}

	public ReadWriteDao<ShiptoSareaEntity> getShiptoSareaEntityDao() {
		if (shiptoSareaEntityDao == null) {
			shiptoSareaEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShiptoSareaEntity.class);
		}
		return shiptoSareaEntityDao;
	}

	public ReadWriteDao<ShiptoSareaRestrBtrulEntity> getShiptoSareaRestrBtrulEntityDao() {
		if (shiptoSareaRestrBtrulEntityDao == null) {
			shiptoSareaRestrBtrulEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShiptoSareaRestrBtrulEntity.class);
		}
		return shiptoSareaRestrBtrulEntityDao;
	}

	public ReadWriteDao<ShiptoSareaRestrEntity> getShiptoSareaRestrEntityDao() {
		if (shiptoSareaRestrEntityDao == null) {
			shiptoSareaRestrEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShiptoSareaRestrEntity.class);
		}
		return shiptoSareaRestrEntityDao;
	}

	public ReadWriteDao<ShiptoSiloEntity> getShiptoSiloEntityDao() {
		if (shiptoSiloEntityDao == null) {
			shiptoSiloEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShiptoSiloEntity.class);
		}
		return shiptoSiloEntityDao;
	}

	public ReadWriteDao<ShiptoTruckTypeEntity> getShiptoTruckTypeEntityDao() {
		if (shiptoTruckTypeEntityDao == null) {
			shiptoTruckTypeEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShiptoTruckTypeEntity.class);
		}
		return shiptoTruckTypeEntityDao;
	}

	public ReadWriteDao<ShiptoWagonTypeEntity> getShiptoWagonTypeEntityDao() {
		if (shiptoWagonTypeEntityDao == null) {
			shiptoWagonTypeEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShiptoWagonTypeEntity.class);
		}
		return shiptoWagonTypeEntityDao;
	}

	public ReadWriteDao<ShsareaRestrTruckTypeEntity> getShsareaRestrTruckTypeEntityDao() {
		if (shsareaRestrTruckTypeEntityDao == null) {
			shsareaRestrTruckTypeEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ShsareaRestrTruckTypeEntity.class);
		}
		return shsareaRestrTruckTypeEntityDao;
	}

	public ReadWriteDao<SignatureEntity> getSignatureEntityDao() {
		if (signatureEntityDao == null) {
			signatureEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SignatureEntity.class);
		}
		return signatureEntityDao;
	}

	public ReadWriteDao<SimpleVehicleEntity> getSimpleVehicleEntityDao() {
		if (simpleVehicleEntityDao == null) {
			simpleVehicleEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SimpleVehicleEntity.class);
		}
		return simpleVehicleEntityDao;
	}

	public ReadWriteDao<TrackLoadingpointEntity> getTrackLoadingpointEntityDao() {
		if (trackLoadingpointEntityDao == null) {
			trackLoadingpointEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TrackLoadingpointEntity.class);
		}
		return trackLoadingpointEntityDao;
	}

	public ReadWriteDao<TrafficJamEntity> getTrafficJamEntityDao() {
		if (trafficJamEntityDao == null) {
			trafficJamEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TrafficJamEntity.class);
		}
		return trafficJamEntityDao;
	}

	public ReadWriteDao<TrailerEntity> getTrailerEntityDao() {
		if (trailerEntityDao == null) {
			trailerEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TrailerEntity.class);
		}
		return trailerEntityDao;
	}

	public ReadWriteDao<TrspZonePolygonEntity> getTrspZonePolygonEntityDao() {
		if (trspZonePolygonEntityDao == null) {
			trspZonePolygonEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TrspZonePolygonEntity.class);
		}
		return trspZonePolygonEntityDao;
	}

	public ReadWriteDao<TrspZoneVertexEntity> getTrspZoneVertexEntityDao() {
		if (trspZoneVertexEntityDao == null) {
			trspZoneVertexEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TrspZoneVertexEntity.class);
		}
		return trspZoneVertexEntityDao;
	}

	public ReadWriteDao<TruckEntity> getTruckEntityDao() {
		if (truckEntityDao == null) {
			truckEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TruckEntity.class);
		}
		return truckEntityDao;
	}

	public ReadWriteDao<TruckRouteEntity> getTruckRouteEntityDao() {
		if (truckRouteEntityDao == null) {
			truckRouteEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TruckRouteEntity.class);
		}
		return truckRouteEntityDao;
	}

	public ReadWriteDao<TruckTrailerHistoryEntity> getTruckTrailerHistoryEntityDao() {
		if (truckTrailerHistoryEntityDao == null) {
			truckTrailerHistoryEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TruckTrailerHistoryEntity.class);
		}
		return truckTrailerHistoryEntityDao;
	}

	public ReadWriteDao<TruckUnavailabilityEntity> getTruckUnavailabilityEntityDao() {
		if (truckUnavailabilityEntityDao == null) {
			truckUnavailabilityEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TruckUnavailabilityEntity.class);
		}
		return truckUnavailabilityEntityDao;
	}

	public ReadWriteDao<WagonDmgcodeTypeEntity> getWagonDmgcodeTypeEntityDao() {
		if (wagonDmgcodeTypeEntityDao == null) {
			wagonDmgcodeTypeEntityDao = new ReadWriteDaoImpl<>(em, authorizer, WagonDmgcodeTypeEntity.class);
		}
		return wagonDmgcodeTypeEntityDao;
	}

	public ReadWriteDao<WagonEntity> getWagonEntityDao() {
		if (wagonEntityDao == null) {
			wagonEntityDao = new ReadWriteDaoImpl<>(em, authorizer, WagonEntity.class);
		}
		return wagonEntityDao;
	}

	public ReadWriteDao<WagonEventEntity> getWagonEventEntityDao() {
		if (wagonEventEntityDao == null) {
			wagonEventEntityDao = new ReadWriteDaoImpl<>(em, authorizer, WagonEventEntity.class);
		}
		return wagonEventEntityDao;
	}

	public ReadWriteDao<WagonKeeperEntity> getWagonKeeperEntityDao() {
		if (wagonKeeperEntityDao == null) {
			wagonKeeperEntityDao = new ReadWriteDaoImpl<>(em, authorizer, WagonKeeperEntity.class);
		}
		return wagonKeeperEntityDao;
	}

	public ReadWriteDao<WagonMaintEntity> getWagonMaintEntityDao() {
		if (wagonMaintEntityDao == null) {
			wagonMaintEntityDao = new ReadWriteDaoImpl<>(em, authorizer, WagonMaintEntity.class);
		}
		return wagonMaintEntityDao;
	}

	public ReadWriteDao<WagonRepAttEntity> getWagonRepAttEntityDao() {
		if (wagonRepAttEntityDao == null) {
			wagonRepAttEntityDao = new ReadWriteDaoImpl<>(em, authorizer, WagonRepAttEntity.class);
		}
		return wagonRepAttEntityDao;
	}

	public ReadWriteDao<OrderFinalEntity> getOrderFinalEntityDao() {
		if (orderFinalEntityDao == null) {
			orderFinalEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderFinalEntity.class);
		}
		return orderFinalEntityDao;
	}

	public ReadWriteDao<OrderItemFinalEntity> getOrderItemFinalEntityDao() {
		if (orderItemFinalEntityDao == null) {
			orderItemFinalEntityDao = new ReadWriteDaoImpl<>(em, authorizer, OrderItemFinalEntity.class);
		}
		return orderItemFinalEntityDao;
	}

	public ReadWriteDao<ContainerPositionEntity> getContainerPositionEntityDao() {
		if (containerPositionEntityDao == null) {
			containerPositionEntityDao = new ReadWriteDaoImpl<>(em, authorizer, ContainerPositionEntity.class);
		}
		return containerPositionEntityDao;
	}

	public ReadWriteDao<TrailerPositionEntity> getTrailerPositionEntityDao() {
		if (trailerPositionEntityDao == null) {
			trailerPositionEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TrailerPositionEntity.class);
		}
		return trailerPositionEntityDao;
	}

	public ReadWriteDao<TruckPositionEntity> getTruckPositionEntityDao() {
		if (truckPositionEntityDao == null) {
			truckPositionEntityDao = new ReadWriteDaoImpl<>(em, authorizer, TruckPositionEntity.class);
		}
		return truckPositionEntityDao;
	}

	public ReadWriteDao<WagonPositionEntity> getWagonPositionEntityDao() {
		if (wagonPositionEntityDao == null) {
			wagonPositionEntityDao = new ReadWriteDaoImpl<>(em, authorizer, WagonPositionEntity.class);
		}
		return wagonPositionEntityDao;
	}

	public ReadWriteDao<MatpkgMapCondEntity> getMatpkgMapCondEntityDao() {
		if (matpkgMapCondEntityDao == null) {
			matpkgMapCondEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MatpkgMapCondEntity.class);
		}
		return matpkgMapCondEntityDao;
	}

	public ReadWriteDao<MatpkgMapRuleEntity> getMatpkgMapRuleEntityDao() {
		if (matpkgMapRuleEntityDao == null) {
			matpkgMapRuleEntityDao = new ReadWriteDaoImpl<>(em, authorizer, MatpkgMapRuleEntity.class);
		}
		return matpkgMapRuleEntityDao;
	}

	public ReadWriteDao<SapBpCompanycodeEntity> getSapBpCompanycodeEntityDao() {
		if (sapBpCompanycodeEntityDao == null) {
			sapBpCompanycodeEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapBpCompanycodeEntity.class);
		}
		return sapBpCompanycodeEntityDao;
	}

	public ReadWriteDao<SapBpEntity> getSapBpEntityDao() {
		if (sapBpEntityDao == null) {
			sapBpEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapBpEntity.class);
		}
		return sapBpEntityDao;
	}

	public ReadWriteDao<SapBpPfBilltoEntity> getSapBpPfBilltoEntityDao() {
		if (sapBpPfBilltoEntityDao == null) {
			sapBpPfBilltoEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapBpPfBilltoEntity.class);
		}
		return sapBpPfBilltoEntityDao;
	}

	public ReadWriteDao<SapBpPfPayerEntity> getSapBpPfPayerEntityDao() {
		if (sapBpPfPayerEntityDao == null) {
			sapBpPfPayerEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapBpPfPayerEntity.class);
		}
		return sapBpPfPayerEntityDao;
	}

	public ReadWriteDao<SapBpPfShiptoEntity> getSapBpPfShiptoEntityDao() {
		if (sapBpPfShiptoEntityDao == null) {
			sapBpPfShiptoEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapBpPfShiptoEntity.class);
		}
		return sapBpPfShiptoEntityDao;
	}

	public ReadWriteDao<SapBpPfSoldtoEntity> getSapBpPfSoldtoEntityDao() {
		if (sapBpPfSoldtoEntityDao == null) {
			sapBpPfSoldtoEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapBpPfSoldtoEntity.class);
		}
		return sapBpPfSoldtoEntityDao;
	}

	public ReadWriteDao<SapBpSareaEntity> getSapBpSareaEntityDao() {
		if (sapBpSareaEntityDao == null) {
			sapBpSareaEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapBpSareaEntity.class);
		}
		return sapBpSareaEntityDao;
	}

	public ReadWriteDao<SapContractEntity> getSapContractEntityDao() {
		if (sapContractEntityDao == null) {
			sapContractEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapContractEntity.class);
		}
		return sapContractEntityDao;
	}

	public ReadWriteDao<SapContractItemEntity> getSapContractItemEntityDao() {
		if (sapContractItemEntityDao == null) {
			sapContractItemEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapContractItemEntity.class);
		}
		return sapContractItemEntityDao;
	}

	public ReadWriteDao<SapMatPlantNormEntity> getSapMatPlantNormEntityDao() {
		if (sapMatPlantNormEntityDao == null) {
			sapMatPlantNormEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapMatPlantNormEntity.class);
		}
		return sapMatPlantNormEntityDao;
	}

	public ReadWriteDao<SapMaterialAltUomEntity> getSapMaterialAltUomEntityDao() {
		if (sapMaterialAltUomEntityDao == null) {
			sapMaterialAltUomEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapMaterialAltUomEntity.class);
		}
		return sapMaterialAltUomEntityDao;
	}

	public ReadWriteDao<SapMaterialDescriptionEntity> getSapMaterialDescriptionEntityDao() {
		if (sapMaterialDescriptionEntityDao == null) {
			sapMaterialDescriptionEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapMaterialDescriptionEntity.class);
		}
		return sapMaterialDescriptionEntityDao;
	}

	public ReadWriteDao<SapMaterialEntity> getSapMaterialEntityDao() {
		if (sapMaterialEntityDao == null) {
			sapMaterialEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapMaterialEntity.class);
		}
		return sapMaterialEntityDao;
	}

	public ReadWriteDao<SapMaterialPlantEntity> getSapMaterialPlantEntityDao() {
		if (sapMaterialPlantEntityDao == null) {
			sapMaterialPlantEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapMaterialPlantEntity.class);
		}
		return sapMaterialPlantEntityDao;
	}

	public ReadWriteDao<SapMaterialSareaEntity> getSapMaterialSareaEntityDao() {
		if (sapMaterialSareaEntityDao == null) {
			sapMaterialSareaEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapMaterialSareaEntity.class);
		}
		return sapMaterialSareaEntityDao;
	}

	public ReadWriteDao<SapNetpriceOrderItemEntity> getSapNetpriceOrderItemEntityDao() {
		if (sapNetpriceOrderItemEntityDao == null) {
			sapNetpriceOrderItemEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapNetpriceOrderItemEntity.class);
		}
		return sapNetpriceOrderItemEntityDao;
	}

	public ReadWriteDao<SapVehicleEntity> getSapVehicleEntityDao() {
		if (sapVehicleEntityDao == null) {
			sapVehicleEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapVehicleEntity.class);
		}
		return sapVehicleEntityDao;
	}

	public ReadWriteDao<SapVendorEntity> getSapVendorEntityDao() {
		if (sapVendorEntityDao == null) {
			sapVendorEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapVendorEntity.class);
		}
		return sapVendorEntityDao;
	}

	public ReadWriteDao<SapVendorPorgEntity> getSapVendorPorgEntityDao() {
		if (sapVendorPorgEntityDao == null) {
			sapVendorPorgEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapVendorPorgEntity.class);
		}
		return sapVendorPorgEntityDao;
	}

	public ReadWriteDao<SapVnPfCommCarrierEntity> getSapVnPfCommCarrierEntityDao() {
		if (sapVnPfCommCarrierEntityDao == null) {
			sapVnPfCommCarrierEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapVnPfCommCarrierEntity.class);
		}
		return sapVnPfCommCarrierEntityDao;
	}

	public ReadWriteDao<SapVnPfExecCarrierEntity> getSapVnPfExecCarrierEntityDao() {
		if (sapVnPfExecCarrierEntityDao == null) {
			sapVnPfExecCarrierEntityDao = new ReadWriteDaoImpl<>(em, authorizer, SapVnPfExecCarrierEntity.class);
		}
		return sapVnPfExecCarrierEntityDao;
	}

	public ReadWriteDao<ColumnMeta> getColumnMetaDao() {
		if (columnMetaDao == null) {
			columnMetaDao = new ReadWriteDaoImpl<>(em, authorizer, ColumnMeta.class);
		}
		return columnMetaDao;
	}

	public ReadWriteDao<TableMeta> getTableMetaDao() {
		if (tableMetaDao == null) {
			tableMetaDao = new ReadWriteDaoImpl<>(em, authorizer, TableMeta.class);
		}
		return tableMetaDao;
	}

	public ReadWriteDao<DemandPlanEntity> getDemandPlanEntityDao() {
		if (demandPlanEntityDao == null) {
			demandPlanEntityDao = new ReadWriteDaoImpl<>(em, authorizer, DemandPlanEntity.class);
		}
		return demandPlanEntityDao;
	}

	public ReadOnlyDao<ArchivingMetadataEntity> getArchivingMetadataEntityDao() {
		if (archivingMetadataEntityDao == null) {
			archivingMetadataEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ArchivingMetadataEntity.class);
		}
		return archivingMetadataEntityDao;
	}

	public ReadOnlyDao<ArchivingOrderLogEntity> getArchivingOrderLogEntityDao() {
		if (archivingOrderLogEntityDao == null) {
			archivingOrderLogEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ArchivingOrderLogEntity.class);
		}
		return archivingOrderLogEntityDao;
	}

	public ReadOnlyDao<ArchivingPdfEntity> getArchivingPdfEntityDao() {
		if (archivingPdfEntityDao == null) {
			archivingPdfEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ArchivingPdfEntity.class);
		}
		return archivingPdfEntityDao;
	}

	public ReadOnlyDao<BagTerminalEntity> getBagTerminalEntityDao() {
		if (bagTerminalEntityDao == null) {
			bagTerminalEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, BagTerminalEntity.class);
		}
		return bagTerminalEntityDao;
	}

	public ReadOnlyDao<BimodalRailOrderEntity> getBimodalRailOrderEntityDao() {
		if (bimodalRailOrderEntityDao == null) {
			bimodalRailOrderEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, BimodalRailOrderEntity.class);
		}
		return bimodalRailOrderEntityDao;
	}

	public ReadOnlyDao<BizeventAuditDisplayEntity> getBizeventAuditDisplayEntityDao() {
		if (bizeventAuditDisplayEntityDao == null) {
			bizeventAuditDisplayEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, BizeventAuditDisplayEntity.class);
		}
		return bizeventAuditDisplayEntityDao;
	}

	public ReadOnlyDao<BizeventMapEntity> getBizeventMapEntityDao() {
		if (bizeventMapEntityDao == null) {
			bizeventMapEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, BizeventMapEntity.class);
		}
		return bizeventMapEntityDao;
	}

	public ReadOnlyDao<BizeventOrderLogEntity> getBizeventOrderLogEntityDao() {
		if (bizeventOrderLogEntityDao == null) {
			bizeventOrderLogEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, BizeventOrderLogEntity.class);
		}
		return bizeventOrderLogEntityDao;
	}

	public ReadOnlyDao<BizeventTruckLogEntity> getBizeventTruckLogEntityDao() {
		if (bizeventTruckLogEntityDao == null) {
			bizeventTruckLogEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, BizeventTruckLogEntity.class);
		}
		return bizeventTruckLogEntityDao;
	}

	public ReadOnlyDao<BpSareaSofficeSgroupEntity> getBpSareaSofficeSgroupEntityDao() {
		if (bpSareaSofficeSgroupEntityDao == null) {
			bpSareaSofficeSgroupEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, BpSareaSofficeSgroupEntity.class);
		}
		return bpSareaSofficeSgroupEntityDao;
	}

	public ReadOnlyDao<CarrierSvendorEntity> getCarrierSvendorEntityDao() {
		if (carrierSvendorEntityDao == null) {
			carrierSvendorEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CarrierSvendorEntity.class);
		}
		return carrierSvendorEntityDao;
	}

	public ReadOnlyDao<CarrierVendorEntity> getCarrierVendorEntityDao() {
		if (carrierVendorEntityDao == null) {
			carrierVendorEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CarrierVendorEntity.class);
		}
		return carrierVendorEntityDao;
	}

	public ReadOnlyDao<ChartLoadScheduledEntity> getChartLoadScheduledEntityDao() {
		if (chartLoadScheduledEntityDao == null) {
			chartLoadScheduledEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ChartLoadScheduledEntity.class);
		}
		return chartLoadScheduledEntityDao;
	}

	public ReadOnlyDao<ChartLoadUnscheduledEntity> getChartLoadUnscheduledEntityDao() {
		if (chartLoadUnscheduledEntityDao == null) {
			chartLoadUnscheduledEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ChartLoadUnscheduledEntity.class);
		}
		return chartLoadUnscheduledEntityDao;
	}

	public ReadOnlyDao<ColumnInfoEntity> getColumnInfoEntityDao() {
		if (columnInfoEntityDao == null) {
			columnInfoEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ColumnInfoEntity.class);
		}
		return columnInfoEntityDao;
	}

	public ReadOnlyDao<CommExecCarrierPfEntity> getCommExecCarrierPfEntityDao() {
		if (commExecCarrierPfEntityDao == null) {
			commExecCarrierPfEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CommExecCarrierPfEntity.class);
		}
		return commExecCarrierPfEntityDao;
	}

	public ReadOnlyDao<CommExecCarrPfActiveEntity> getCommExecCarrPfActiveEntityDao() {
		if (commExecCarrActiveEntityDao == null) {
			commExecCarrActiveEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CommExecCarrPfActiveEntity.class);
		}
		return commExecCarrActiveEntityDao;
	}

	public ReadOnlyDao<CommonSareaMapEntity> getCommonSareaMapEntityDao() {
		if (commonSareaMapEntityDao == null) {
			commonSareaMapEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CommonSareaMapEntity.class);
		}
		return commonSareaMapEntityDao;
	}

	public ReadOnlyDao<CompCodeSareaEntity> getCompCodeSareaEntityDao() {
		if (compCodeSareaEntityDao == null) {
			compCodeSareaEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CompCodeSareaEntity.class);
		}
		return compCodeSareaEntityDao;
	}

	public ReadOnlyDao<ContainerMonitoringEntity> getContainerMonitoringEntityDao() {
		if (containerMonitoringEntityDao == null) {
			containerMonitoringEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ContainerMonitoringEntity.class);
		}
		return containerMonitoringEntityDao;
	}

	public ReadOnlyDao<ContractItemQuantityEntity> getContractItemQuantityEntityDao() {
		if (contractItemQuantityEntityDao == null) {
			contractItemQuantityEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ContractItemQuantityEntity.class);
		}
		return contractItemQuantityEntityDao;
	}

	public ReadOnlyDao<CreditNoteProposalsEntity> getCreditNoteProposalsEntityDao() {
		if (creditNoteProposalsEntityDao == null) {
			creditNoteProposalsEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CreditNoteProposalsEntity.class);
		}
		return creditNoteProposalsEntityDao;
	}

	public ReadOnlyDao<CurrentExchangeRateEntity> getCurrentExchangeRateEntityDao() {
		if (currentExchangeRateEntityDao == null) {
			currentExchangeRateEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CurrentExchangeRateEntity.class);
		}
		return currentExchangeRateEntityDao;
	}

	public ReadOnlyDao<CustordItemSumQtyExtEntity> getCustordItemSumQtyExtEntityDao() {
		if (custordItemSumQtyExtEntityDao == null) {
			custordItemSumQtyExtEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CustordItemSumQtyExtEntity.class);
		}
		return custordItemSumQtyExtEntityDao;
	}

	public ReadOnlyDao<CustordOnetimeDataEntity> getCustordOnetimeDataEntityDao() {
		if (custordOnetimeDataEntityDao == null) {
			custordOnetimeDataEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CustordOnetimeDataEntity.class);
		}
		return custordOnetimeDataEntityDao;
	}

	public ReadOnlyDao<CustorderCreateEntity> getCustorderCreateEntityDao() {
		if (custorderCreateEntityDao == null) {
			custorderCreateEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CustorderCreateEntity.class);
		}
		return custorderCreateEntityDao;
	}

	public ReadOnlyDao<CustorderFinishedEntity> getCustorderFinishedEntityDao() {
		if (custorderFinishedEntityDao == null) {
			custorderFinishedEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CustorderFinishedEntity.class);
		}
		return custorderFinishedEntityDao;
	}

	public ReadOnlyDao<CustorderItemSumQtyEntity> getCustorderItemSumQtyEntityDao() {
		if (custorderItemSumQtyEntityDao == null) {
			custorderItemSumQtyEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CustorderItemSumQtyEntity.class);
		}
		return custorderItemSumQtyEntityDao;
	}

	public ReadOnlyDao<CustorderViewEntity> getCustorderViewEntityDao() {
		if (custorderViewEntityDao == null) {
			custorderViewEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, CustorderViewEntity.class);
		}
		return custorderViewEntityDao;
	}

	public ReadOnlyDao<DamagecodeSearchEntity> getDamagecodeSearchEntityDao() {
		if (damagecodeSearchEntityDao == null) {
			damagecodeSearchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DamagecodeSearchEntity.class);
		}
		return damagecodeSearchEntityDao;
	}

	public ReadOnlyDao<DispatchingCarrierEntity> getDispatchingCarrierEntityDao() {
		if (dispatchingCarrierEntityDao == null) {
			dispatchingCarrierEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispatchingCarrierEntity.class);
		}
		return dispatchingCarrierEntityDao;
	}

	public ReadOnlyDao<DispoAssignPartListEntity> getDispoAssignPartListEntityDao() {
		if (dispoAssignPartListEntityDao == null) {
			dispoAssignPartListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoAssignPartListEntity.class);
		}
		return dispoAssignPartListEntityDao;
	}

	public ReadOnlyDao<DispoAssignPoolCarrEntity> getDispoAssignPoolCarrEntityDao() {
		if (dispoAssignPoolCarrEntityDao == null) {
			dispoAssignPoolCarrEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoAssignPoolCarrEntity.class);
		}
		return dispoAssignPoolCarrEntityDao;
	}

	public ReadOnlyDao<DispoAssignTruckEntity> getDispoAssignTruckEntityDao() {
		if (dispoAssignTruckEntityDao == null) {
			dispoAssignTruckEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoAssignTruckEntity.class);
		}
		return dispoAssignTruckEntityDao;
	}

	public ReadOnlyDao<DispoAssignmentListEntity> getDispoAssignmentListEntityDao() {
		if (dispoAssignmentListEntityDao == null) {
			dispoAssignmentListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoAssignmentListEntity.class);
		}
		return dispoAssignmentListEntityDao;
	}

	public ReadOnlyDao<DispoDayLoadsEntity> getDispoDayLoadsEntityDao() {
		if (dispoDayLoadsEntityDao == null) {
			dispoDayLoadsEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoDayLoadsEntity.class);
		}
		return dispoDayLoadsEntityDao;
	}

	public ReadOnlyDao<DispoDriverBreakListEntity> getDispoDriverBreakListEntityDao() {
		if (dispoDriverBreakListEntityDao == null) {
			dispoDriverBreakListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoDriverBreakListEntity.class);
		}
		return dispoDriverBreakListEntityDao;
	}

	public ReadOnlyDao<DispoExtOrderListEntity> getDispoExtOrderListEntityDao() {
		if (dispoExtOrderListEntityDao == null) {
			dispoExtOrderListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoExtOrderListEntity.class);
		}
		return dispoExtOrderListEntityDao;
	}

	public ReadOnlyDao<DispoHomePlantEntity> getDispoHomePlantEntityDao() {
		if (dispoHomePlantEntityDao == null) {
			dispoHomePlantEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoHomePlantEntity.class);
		}
		return dispoHomePlantEntityDao;
	}

	public ReadOnlyDao<DispoLoadtargetEntity> getDispoLoadtargetEntityDao() {
		if (dispoLoadtargetEntityDao == null) {
			dispoLoadtargetEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoLoadtargetEntity.class);
		}
		return dispoLoadtargetEntityDao;
	}

	public ReadOnlyDao<DispoMaxWagonPosEntity> getDispoMaxWagonPosEntityDao() {
		if (dispoMaxWagonPosEntityDao == null) {
			dispoMaxWagonPosEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoMaxWagonPosEntity.class);
		}
		return dispoMaxWagonPosEntityDao;
	}

	public ReadOnlyDao<DispoMessageListEntity> getDispoMessageListEntityDao() {
		if (dispoMessageListEntityDao == null) {
			dispoMessageListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoMessageListEntity.class);
		}
		return dispoMessageListEntityDao;
	}

	public ReadOnlyDao<DispoOrderItemListEntity> getDispoOrderItemListEntityDao() {
		if (dispoOrderItemListEntityDao == null) {
			dispoOrderItemListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoOrderItemListEntity.class);
		}
		return dispoOrderItemListEntityDao;
	}

	public ReadOnlyDao<DispoOrderListEntity> getDispoOrderListEntityDao() {
		if (dispoOrderListEntityDao == null) {
			dispoOrderListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoOrderListEntity.class);
		}
		return dispoOrderListEntityDao;
	}

	public ReadOnlyDao<DispoPlanningEntity> getDispoPlanningEntityDao() {
		if (dispoPlanningEntityDao == null) {
			dispoPlanningEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoPlanningEntity.class);
		}
		return dispoPlanningEntityDao;
	}

	public ReadOnlyDao<DispoPlantTrackListEntity> getDispoPlantTrackListEntityDao() {
		if (dispoPlantTrackListEntityDao == null) {
			dispoPlantTrackListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoPlantTrackListEntity.class);
		}
		return dispoPlantTrackListEntityDao;
	}

	public ReadOnlyDao<DispoRailAssListEntity> getDispoRailAssListEntityDao() {
		if (dispoRailAssListEntityDao == null) {
			dispoRailAssListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoRailAssListEntity.class);
		}
		return dispoRailAssListEntityDao;
	}

	public ReadOnlyDao<DispoShiptoSoldtoEntity> getDispoShiptoSoldtoEntityDao() {
		if (dispoShiptoSoldtoEntityDao == null) {
			dispoShiptoSoldtoEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoShiptoSoldtoEntity.class);
		}
		return dispoShiptoSoldtoEntityDao;
	}

	@Deprecated
	public ReadOnlyDao<DispoTruckListEntity> getDispoTruckListEntityDao() {
		if (dispoTruckListEntityDao == null) {
			dispoTruckListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoTruckListEntity.class);
		}
		return dispoTruckListEntityDao;
	}

	public ReadOnlyDao<DispoTruckListV2Entity> getDispoTruckListV2EntityDao() {
		if (dispoTruckListV2EntityDao == null) {
			dispoTruckListV2EntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoTruckListV2Entity.class);
		}
		return dispoTruckListV2EntityDao;
	}

	public ReadOnlyDao<DispoTruckChangeListEntity> getDispoTruckChangeListEntityDao() {
		if (dispoTruckChangeListEntityDao == null) {
			dispoTruckChangeListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoTruckChangeListEntity.class);
		}
		return dispoTruckChangeListEntityDao;
	}

	@Deprecated
	public ReadOnlyDao<DispoTruckLoadListEntity> getDispoTruckLoadListEntityDao() {
		if (dispoTruckLoadListEntityDao == null) {
			dispoTruckLoadListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoTruckLoadListEntity.class);
		}
		return dispoTruckLoadListEntityDao;
	}

	public ReadOnlyDao<DispoTruckLoadListV2Entity> getDispoTruckLoadListV2EntityDao() {
		if (dispoTruckLoadListV2EntityDao == null) {
			dispoTruckLoadListV2EntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoTruckLoadListV2Entity.class);
		}
		return dispoTruckLoadListV2EntityDao;
	}

	public ReadOnlyDao<DispoTruckTypeListEntity> getDispoTruckTypeListEntityDao() {
		if (dispoTruckTypeListEntityDao == null) {
			dispoTruckTypeListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoTruckTypeListEntity.class);
		}
		return dispoTruckTypeListEntityDao;
	}

	public ReadOnlyDao<DispoWContainerListEntity> getDispoWContainerListEntityDao() {
		if (dispoWContainerListEntityDao == null) {
			dispoWContainerListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoWContainerListEntity.class);
		}
		return dispoWContainerListEntityDao;
	}

	public ReadOnlyDao<DispoWagonApartListEntity> getDispoWagonApartListEntityDao() {
		if (dispoWagonApartListEntityDao == null) {
			dispoWagonApartListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoWagonApartListEntity.class);
		}
		return dispoWagonApartListEntityDao;
	}

	public ReadOnlyDao<DispoWagonListEntity> getDispoWagonListEntityDao() {
		if (dispoWagonListEntityDao == null) {
			dispoWagonListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoWagonListEntity.class);
		}
		return dispoWagonListEntityDao;
	}

	public ReadOnlyDao<DispoWagonLoadListEntity> getDispoWagonLoadListEntityDao() {
		if (dispoWagonLoadListEntityDao == null) {
			dispoWagonLoadListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoWagonLoadListEntity.class);
		}
		return dispoWagonLoadListEntityDao;
	}

	public ReadOnlyDao<DispoWagonTrspCatEntity> getDispoWagonTrspCatEntityDao() {
		if (dispoWagonTrspCatEntityDao == null) {
			dispoWagonTrspCatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoWagonTrspCatEntity.class);
		}
		return dispoWagonTrspCatEntityDao;
	}

	public ReadOnlyDao<DispoWagonTypeListEntity> getDispoWagonTypeListEntityDao() {
		if (dispoWagonTypeListEntityDao == null) {
			dispoWagonTypeListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DispoWagonTypeListEntity.class);
		}
		return dispoWagonTypeListEntityDao;
	}

	public ReadOnlyDao<DriverPersonEntity> getDriverPersonEntityDao() {
		if (driverPersonEntityDao == null) {
			driverPersonEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DriverPersonEntity.class);
		}
		return driverPersonEntityDao;
	}

	public ReadOnlyDao<DriverWithoutSessionEntity> getDriverWithoutSessionEntityDao() {
		if (driverWithoutSessionEntityDao == null) {
			driverWithoutSessionEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DriverWithoutSessionEntity.class);
		}
		return driverWithoutSessionEntityDao;
	}

	public ReadOnlyDao<EditbpSoldshipSareaEntity> getEditbpSoldshipSareaEntityDao() {
		if (editbpSoldshipSareaEntityDao == null) {
			editbpSoldshipSareaEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, EditbpSoldshipSareaEntity.class);
		}
		return editbpSoldshipSareaEntityDao;
	}

	public ReadOnlyDao<ExtOrderSearchEntity> getExtOrderSearchEntityDao() {
		if (extOrderSearchEntityDao == null) {
			extOrderSearchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ExtOrderSearchEntity.class);
		}
		return extOrderSearchEntityDao;
	}

	public ReadOnlyDao<FreeTrucksEntity> getFreeTrucksEntityDao() {
		if (freeTrucksEntityDao == null) {
			freeTrucksEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, FreeTrucksEntity.class);
		}
		return freeTrucksEntityDao;
	}

	public ReadOnlyDao<FvlSapBpEntity> getFvlSapBpEntityDao() {
		if (fvlSapBpEntityDao == null) {
			fvlSapBpEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, FvlSapBpEntity.class);
		}
		return fvlSapBpEntityDao;
	}

	public ReadOnlyDao<GoodsMovementHelperEntity> getGoodsMovementHelperEntityDao() {
		if (goodsMovementHelperEntityDao == null) {
			goodsMovementHelperEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, GoodsMovementHelperEntity.class);
		}
		return goodsMovementHelperEntityDao;
	}

	public ReadOnlyDao<LdsServerDropdownEntity> getLdsServerDropdownEntityDao() {
		if (ldsServerDropdownEntityDao == null) {
			ldsServerDropdownEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, LdsServerDropdownEntity.class);
		}
		return ldsServerDropdownEntityDao;
	}

	public ReadOnlyDao<LocationStationsInUseEntity> getLocationStationsInUseEntityDao() {
		if (locationStationsInUseEntityDao == null) {
			locationStationsInUseEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, LocationStationsInUseEntity.class);
		}
		return locationStationsInUseEntityDao;
	}

	public ReadOnlyDao<ManPoolAssignmentListEntity> getManPoolAssignmentListEntityDao() {
		if (manPoolAssignmentListEntityDao == null) {
			manPoolAssignmentListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ManPoolAssignmentListEntity.class);
		}
		return manPoolAssignmentListEntityDao;
	}

	public ReadOnlyDao<MatPlantMissingEntity> getMatPlantMissingEntityDao() {
		if (matPlantMissingEntityDao == null) {
			matPlantMissingEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, MatPlantMissingEntity.class);
		}
		return matPlantMissingEntityDao;
	}

	public ReadOnlyDao<MaterialCompatEntity> getMaterialCompatEntityDao() {
		if (materialCompatEntityDao == null) {
			materialCompatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, MaterialCompatEntity.class);
		}
		return materialCompatEntityDao;
	}

	public ReadOnlyDao<MaterialEnhancementsEntity> getMaterialEnhancementsEntityDao() {
		if (materialEnhancementsEntityDao == null) {
			materialEnhancementsEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, MaterialEnhancementsEntity.class);
		}
		return materialEnhancementsEntityDao;
	}

	public ReadOnlyDao<OrdChartNotAssignedEntity> getOrdChartNotAssignedEntityDao() {
		if (ordChartNotAssignedEntityDao == null) {
			ordChartNotAssignedEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrdChartNotAssignedEntity.class);
		}
		return ordChartNotAssignedEntityDao;
	}

	public ReadOnlyDao<OrdItemAssignmtLoadEntity> getOrdItemAssignmtLoadEntityDao() {
		if (ordItemAssignmtLoadEntityDao == null) {
			ordItemAssignmtLoadEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrdItemAssignmtLoadEntity.class);
		}
		return ordItemAssignmtLoadEntityDao;
	}

	public ReadOnlyDao<OrdLoadSchedInfoEntity> getOrdLoadSchedInfoEntityDao() {
		if (ordLoadSchedInfoEntityDao == null) {
			ordLoadSchedInfoEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrdLoadSchedInfoEntity.class);
		}
		return ordLoadSchedInfoEntityDao;
	}

	public ReadOnlyDao<OrdUnloadSchedInfoEntity> getOrdUnloadSchedInfoEntityDao() {
		if (ordUnloadSchedInfoEntityDao == null) {
			ordUnloadSchedInfoEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrdUnloadSchedInfoEntity.class);
		}
		return ordUnloadSchedInfoEntityDao;
	}

	public ReadOnlyDao<OrderCreateEntity> getOrderCreateEntityDao() {
		if (orderCreateEntityDao == null) {
			orderCreateEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderCreateEntity.class);
		}
		return orderCreateEntityDao;
	}

	public ReadOnlyDao<OrderFiltersEntity> getOrderFiltersEntityDao() {
		if (orderFiltersEntityDao == null) {
			orderFiltersEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderFiltersEntity.class);
		}
		return orderFiltersEntityDao;
	}

	public ReadOnlyDao<OrderFinishedEntity> getOrderFinishedEntityDao() {
		if (orderFinishedEntityDao == null) {
			orderFinishedEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderFinishedEntity.class);
		}
		return orderFinishedEntityDao;
	}

	public ReadOnlyDao<OrderItemMarginEntity> getOrderItemMarginEntityDao() {
		if (orderItemMarginEntityDao == null) {
			orderItemMarginEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderItemMarginEntity.class);
		}
		return orderItemMarginEntityDao;
	}

	public ReadOnlyDao<OrderItemOrderedQtyEntity> getOrderItemOrderedQtyEntityDao() {
		if (orderItemOrderedQtyEntityDao == null) {
			orderItemOrderedQtyEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderItemOrderedQtyEntity.class);
		}
		return orderItemOrderedQtyEntityDao;
	}

	public ReadOnlyDao<OrderItemQuantityEntity> getOrderItemQuantityEntityDao() {
		if (orderItemQuantityEntityDao == null) {
			orderItemQuantityEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderItemQuantityEntity.class);
		}
		return orderItemQuantityEntityDao;
	}

	public ReadOnlyDao<OrderMatPointsEntity> getOrderMatPointsEntityDao() {
		if (orderMatPointsEntityDao == null) {
			orderMatPointsEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderMatPointsEntity.class);
		}
		return orderMatPointsEntityDao;
	}

	public ReadOnlyDao<OrderMonitoringEntity> getOrderMonitoringEntityDao() {
		if (orderMonitoringEntityDao == null) {
			orderMonitoringEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderMonitoringEntity.class);
		}
		return orderMonitoringEntityDao;
	}

	public ReadOnlyDao<OrderShippNotificationEntity> getOrderShippNotificationEntityDao() {
		if (orderShippNotificationEntityDao == null) {
			orderShippNotificationEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderShippNotificationEntity.class);
		}
		return orderShippNotificationEntityDao;
	}

	public ReadOnlyDao<OrderReadyToFinishEntity> getOrderReadyToFinishEntityDao() {
		if (orderReadyToFinishEntityDao == null) {
			orderReadyToFinishEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderReadyToFinishEntity.class);
		}
		return orderReadyToFinishEntityDao;
	}

	public ReadOnlyDao<OrderSearchEntity> getOrderSearchEntityDao() {
		if (orderSearchEntityDao == null) {
			orderSearchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderSearchEntity.class);
		}
		return orderSearchEntityDao;
	}

	public ReadOnlyDao<OrderSearchExportEntity> getOrderSearchExportEntityDao() {
		if (orderSearchExportEntityDao == null) {
			orderSearchExportEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderSearchExportEntity.class);
		}
		return orderSearchExportEntityDao;
	}

	public ReadOnlyDao<OutputdocAccessEntity> getOutputdocAccessEntityDao() {
		if (outputdocAccessEntityDao == null) {
			outputdocAccessEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OutputdocAccessEntity.class);
		}
		return outputdocAccessEntityDao;
	}

	public ReadOnlyDao<OrderSearchStatEntity> getOrderSearchStatEntityDao() {
		if (orderSearchStatEntityDao == null) {
			orderSearchStatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OrderSearchStatEntity.class);
		}
		return orderSearchStatEntityDao;
	}

	public ReadOnlyDao<PdaDeviceCarrierEntity> getPdaDeviceCarrierEntityDao() {
		if (pdaDeviceCarrierEntityDao == null) {
			pdaDeviceCarrierEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PdaDeviceCarrierEntity.class);
		}
		return pdaDeviceCarrierEntityDao;
	}

	public ReadOnlyDao<PickupTerminalEntity> getPickupTerminalEntityDao() {
		if (pickupTerminalEntityDao == null) {
			pickupTerminalEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PickupTerminalEntity.class);
		}
		return pickupTerminalEntityDao;
	}

	public ReadOnlyDao<PlantRailwayStationEntity> getPlantRailwayStationEntityDao() {
		if (plantRailwayStationEntityDao == null) {
			plantRailwayStationEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PlantRailwayStationEntity.class);
		}
		return plantRailwayStationEntityDao;
	}

	public ReadOnlyDao<PlantSareaCompcodeEntity> getPlantSareaCompcodeEntityDao() {
		if (plantSareaCompcodeEntityDao == null) {
			plantSareaCompcodeEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PlantSareaCompcodeEntity.class);
		}
		return plantSareaCompcodeEntityDao;
	}

	public ReadOnlyDao<PlantShippingpointEntity> getPlantShippingpointEntityDao() {
		if (plantShippingpointEntityDao == null) {
			plantShippingpointEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PlantShippingpointEntity.class);
		}
		return plantShippingpointEntityDao;
	}

	public ReadOnlyDao<PoolAllCarrierEntity> getPoolAllCarrierEntityDao() {
		if (poolAllCarrierEntityDao == null) {
			poolAllCarrierEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PoolAllCarrierEntity.class);
		}
		return poolAllCarrierEntityDao;
	}

	@Deprecated
	public ReadOnlyDao<PoolCarrierCarrierCntEntity> getPoolCarrierCarrierCntEntityDao() {
		if (poolCarrierCarrierCntEntityDao == null) {
			poolCarrierCarrierCntEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PoolCarrierCarrierCntEntity.class);
		}
		return poolCarrierCarrierCntEntityDao;
	}

	public ReadOnlyDao<PoolCarrierCarrierResEntity> getPoolCarrierCarrierResEntityDao() {
		if (poolCarrierCarrierResEntityDao == null) {
			poolCarrierCarrierResEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PoolCarrierCarrierResEntity.class);
		}
		return poolCarrierCarrierResEntityDao;
	}

	public ReadOnlyDao<PoolCarrierCarrierEntity> getPoolCarrierCarrierEntityDao() {
		if (poolCarrierCarrierEntityDao == null) {
			poolCarrierCarrierEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PoolCarrierCarrierEntity.class);
		}
		return poolCarrierCarrierEntityDao;
	}

	public ReadOnlyDao<PoolShiptoMatSoldtoEntity> getPoolShiptoMatSoldtoEntityDao() {
		if (poolShiptoMatSoldtoEntityDao == null) {
			poolShiptoMatSoldtoEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PoolShiptoMatSoldtoEntity.class);
		}
		return poolShiptoMatSoldtoEntityDao;
	}

	public ReadOnlyDao<ProdPlantNameEntity> getProdPlantNameEntityDao() {
		if (prodPlantNameEntityDao == null) {
			prodPlantNameEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ProdPlantNameEntity.class);
		}
		return prodPlantNameEntityDao;
	}

	public ReadOnlyDao<RailChartEntity> getRailChartEntityDao() {
		if (railChartEntityDao == null) {
			railChartEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RailChartEntity.class);
		}
		return railChartEntityDao;
	}

	public ReadOnlyDao<RailPlantEntity> getRailPlantEntityDao() {
		if (railPlantEntityDao == null) {
			railPlantEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RailPlantEntity.class);
		}
		return railPlantEntityDao;
	}

	public ReadOnlyDao<RailPlantSpEntity> getRailPlantSpEntityDao() {
		if (railPlantSpEntityDao == null) {
			railPlantSpEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RailPlantSpEntity.class);
		}
		return railPlantSpEntityDao;
	}

	public ReadOnlyDao<RailRepSoldtoShiptoEntity> getRailRepSoldtoShiptoEntityDao() {
		if (railRepSoldtoShiptoEntityDao == null) {
			railRepSoldtoShiptoEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RailRepSoldtoShiptoEntity.class);
		}
		return railRepSoldtoShiptoEntityDao;
	}

	public ReadOnlyDao<RailRepairEntity> getRailRepairEntityDao() {
		if (railRepairEntityDao == null) {
			railRepairEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RailRepairEntity.class);
		}
		return railRepairEntityDao;
	}

	public ReadOnlyDao<RailSpecialAssignmentEntity> getRailSpecialAssignmentEntityDao() {
		if (railSpecialAssignmentEntityDao == null) {
			railSpecialAssignmentEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RailSpecialAssignmentEntity.class);
		}
		return railSpecialAssignmentEntityDao;
	}

	public ReadOnlyDao<RailcarOptLoadedCarsEntity> getRailcarOptLoadedCarsEntityDao() {
		if (railcarOptLoadedCarsEntityDao == null) {
			railcarOptLoadedCarsEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RailcarOptLoadedCarsEntity.class);
		}
		return railcarOptLoadedCarsEntityDao;
	}

	public ReadOnlyDao<RailcarOptOpenOrdersEntity> getRailcarOptOpenOrdersEntityDao() {
		if (railcarOptOpenOrdersEntityDao == null) {
			railcarOptOpenOrdersEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RailcarOptOpenOrdersEntity.class);
		}
		return railcarOptOpenOrdersEntityDao;
	}

	public ReadOnlyDao<RepExternalOrderEntity> getRepExternalOrderEntityDao() {
		if (repExternalOrderEntityDao == null) {
			repExternalOrderEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepExternalOrderEntity.class);
		}
		return repExternalOrderEntityDao;
	}

	public ReadOnlyDao<RepGoodsmovementEntity> getRepGoodsmovementEntityDao() {
		if (repGoodsmovementEntityDao == null) {
			repGoodsmovementEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepGoodsmovementEntity.class);
		}
		return repGoodsmovementEntityDao;
	}

	public ReadOnlyDao<RepLoadShippingStatsEntity> getRepLoadShippingStatsEntityDao() {
		if (repLoadShippingStatsEntityDao == null) {
			repLoadShippingStatsEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepLoadShippingStatsEntity.class);
		}
		return repLoadShippingStatsEntityDao;
	}

	public ReadOnlyDao<RepOPerRouteCarrierEntity> getRepOPerRouteCarrierEntityDao() {
		if (repOPerRouteCarrierEntityDao == null) {
			repOPerRouteCarrierEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOPerRouteCarrierEntity.class);
		}
		return repOPerRouteCarrierEntityDao;
	}

	public ReadOnlyDao<RepOrderListCarrierEntity> getRepOrderListCarrierEntityDao() {
		if (repOrderListCarrierEntityDao == null) {
			repOrderListCarrierEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderListCarrierEntity.class);
		}
		return repOrderListCarrierEntityDao;
	}

	public ReadOnlyDao<RepOrderListCommEntity> getRepOrderListCommEntityDao() {
		if (repOrderListCommEntityDao == null) {
			repOrderListCommEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderListCommEntity.class);
		}
		return repOrderListCommEntityDao;
	}

	public ReadOnlyDao<RepOrderListCustEntity> getRepOrderListCustEntityDao() {
		if (repOrderListCustEntityDao == null) {
			repOrderListCustEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderListCustEntity.class);
		}
		return repOrderListCustEntityDao;
	}

	public ReadOnlyDao<RepOrderListEntity> getRepOrderListEntityDao() {
		if (repOrderListEntityDao == null) {
			repOrderListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderListEntity.class);
		}
		return repOrderListEntityDao;
	}

	public ReadOnlyDao<RepOrderListCrhEntity> getRepOrderListCrhEntityDao() {
		if (repOrderListCrhEntityDao == null) {
			repOrderListCrhEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderListCrhEntity.class);
		}
		return repOrderListCrhEntityDao;
	}

	public ReadOnlyDao<RepOrderPerCarrierEntity> getRepOrderPerCarrierEntityDao() {
		if (repOrderPerCarrierEntityDao == null) {
			repOrderPerCarrierEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderPerCarrierEntity.class);
		}
		return repOrderPerCarrierEntityDao;
	}

	public ReadOnlyDao<RepOrderPerCustEntity> getRepOrderPerCustEntityDao() {
		if (repOrderPerCustEntityDao == null) {
			repOrderPerCustEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderPerCustEntity.class);
		}
		return repOrderPerCustEntityDao;
	}

	public ReadOnlyDao<RepOrderPerPlantMatEntity> getRepOrderPerPlantMatEntityDao() {
		if (repOrderPerPlantMatEntityDao == null) {
			repOrderPerPlantMatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderPerPlantMatEntity.class);
		}
		return repOrderPerPlantMatEntityDao;
	}

	public ReadOnlyDao<RepOrderPerPoolEntity> getRepOrderPerPoolEntityDao() {
		if (repOrderPerPoolEntityDao == null) {
			repOrderPerPoolEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderPerPoolEntity.class);
		}
		return repOrderPerPoolEntityDao;
	}

	public ReadOnlyDao<RepOrderPerRouteEntity> getRepOrderPerRouteEntityDao() {
		if (repOrderPerRouteEntityDao == null) {
			repOrderPerRouteEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderPerRouteEntity.class);
		}
		return repOrderPerRouteEntityDao;
	}

	public ReadOnlyDao<RepOrderPerSpMatpkgEntity> getRepOrderPerSpMatpkgEntityDao() {
		if (repOrderPerSpMatpkgEntityDao == null) {
			repOrderPerSpMatpkgEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderPerSpMatpkgEntity.class);
		}
		return repOrderPerSpMatpkgEntityDao;
	}

	public ReadOnlyDao<RepOrderWinSpedEntity> getRepOrderWinSpedEntityDao() {
		if (repOrderWinSpedEntityDao == null) {
			repOrderWinSpedEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepOrderWinSpedEntity.class);
		}
		return repOrderWinSpedEntityDao;
	}

	public ReadOnlyDao<RepSalesPerCustEntity> getRepSalesPerCustEntityDao() {
		if (repSalesPerCustEntityDao == null) {
			repSalesPerCustEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepSalesPerCustEntity.class);
		}
		return repSalesPerCustEntityDao;
	}

	public ReadOnlyDao<RepSalesPerCustMatEntity> getRepSalesPerCustMatEntityDao() {
		if (repSalesPerCustMatEntityDao == null) {
			repSalesPerCustMatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepSalesPerCustMatEntity.class);
		}
		return repSalesPerCustMatEntityDao;
	}

	public ReadOnlyDao<RepSalesPerMatEntity> getRepSalesPerMatEntityDao() {
		if (repSalesPerMatEntityDao == null) {
			repSalesPerMatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepSalesPerMatEntity.class);
		}
		return repSalesPerMatEntityDao;
	}

	public ReadOnlyDao<RepSapBwExportEntity> getRepSapBwExportEntityDao() {
		if (repSapBwExportEntityDao == null) {
			repSapBwExportEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepSapBwExportEntity.class);
		}
		return repSapBwExportEntityDao;
	}

	public ReadOnlyDao<RepSapimportErrorBpnEntity> getRepSapimportErrorBpnEntityDao() {
		if (repSapimportErrorBpnEntityDao == null) {
			repSapimportErrorBpnEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepSapimportErrorBpnEntity.class);
		}
		return repSapimportErrorBpnEntityDao;
	}

	public ReadOnlyDao<RepSapimportErrorMnEntity> getRepSapimportErrorMnEntityDao() {
		if (repSapimportErrorMnEntityDao == null) {
			repSapimportErrorMnEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepSapimportErrorMnEntity.class);
		}
		return repSapimportErrorMnEntityDao;
	}

	public ReadOnlyDao<RepSapimportErrorVnEntity> getRepSapimportErrorVnEntityDao() {
		if (repSapimportErrorVnEntityDao == null) {
			repSapimportErrorVnEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepSapimportErrorVnEntity.class);
		}
		return repSapimportErrorVnEntityDao;
	}

	public ReadOnlyDao<RepTransPerPlantDtEntity> getRepTransPerPlantDtEntityDao() {
		if (repTransPerPlantDtEntityDao == null) {
			repTransPerPlantDtEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepTransPerPlantDtEntity.class);
		}
		return repTransPerPlantDtEntityDao;
	}

	public ReadOnlyDao<RepTransPerPlantEntity> getRepTransPerPlantEntityDao() {
		if (repTransPerPlantEntityDao == null) {
			repTransPerPlantEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepTransPerPlantEntity.class);
		}
		return repTransPerPlantEntityDao;
	}

	public ReadOnlyDao<RepTransplusEntity> getRepTransplusEntityDao() {
		if (repTransplusEntityDao == null) {
			repTransplusEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RepTransplusEntity.class);
		}
		return repTransplusEntityDao;
	}

	public ReadOnlyDao<RequireFreezingListEntity> getRequireFreezingListEntityDao() {
		if (requireFreezingListEntityDao == null) {
			requireFreezingListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RequireFreezingListEntity.class);
		}
		return requireFreezingListEntityDao;
	}

	public ReadOnlyDao<ReturnStationsInUseEntity> getReturnStationsInUseEntityDao() {
		if (returnStationsInUseEntityDao == null) {
			returnStationsInUseEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ReturnStationsInUseEntity.class);
		}
		return returnStationsInUseEntityDao;
	}

	public ReadOnlyDao<RfidobjectSiloAttachEntity> getRfidobjectSiloAttachEntityDao() {
		if (rfidobjectSiloAttachEntityDao == null) {
			rfidobjectSiloAttachEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RfidobjectSiloAttachEntity.class);
		}
		return rfidobjectSiloAttachEntityDao;
	}

	public ReadOnlyDao<RfidtagDetailEntity> getRfidtagDetailEntityDao() {
		if (rfidtagDetailEntityDao == null) {
			rfidtagDetailEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, RfidtagDetailEntity.class);
		}
		return rfidtagDetailEntityDao;
	}

	public ReadOnlyDao<SapBpStagingAreaEntity> getSapBpStagingAreaEntityDao() {
		if (sapBpStagingAreaEntityDao == null) {
			sapBpStagingAreaEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapBpStagingAreaEntity.class);
		}
		return sapBpStagingAreaEntityDao;
	}

	public ReadOnlyDao<SapContractDispEntity> getSapContractDispEntityDao() {
		if (sapContractDispEntityDao == null) {
			sapContractDispEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapContractDispEntity.class);
		}
		return sapContractDispEntityDao;
	}

	public ReadOnlyDao<SapContractItemDispEntity> getSapContractItemDispEntityDao() {
		if (sapContractItemDispEntityDao == null) {
			sapContractItemDispEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapContractItemDispEntity.class);
		}
		return sapContractItemDispEntityDao;
	}

	public ReadOnlyDao<SapFinancialDataEntity> getSapFinancialDataEntityDao() {
		if (sapFinancialDataEntityDao == null) {
			sapFinancialDataEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapFinancialDataEntity.class);
		}
		return sapFinancialDataEntityDao;
	}

	public ReadOnlyDao<SapMatDescPackEntity> getSapMatDescPackEntityDao() {
		if (sapMatDescPackEntityDao == null) {
			sapMatDescPackEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapMatDescPackEntity.class);
		}
		return sapMatDescPackEntityDao;
	}

	public ReadOnlyDao<SapMatMixdesignEntity> getSapMatMixdesignEntityDao() {
		if (sapMatMixdesignEntityDao == null) {
			sapMatMixdesignEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapMatMixdesignEntity.class);
		}
		return sapMatMixdesignEntityDao;
	}

	public ReadOnlyDao<SapMatPlantNormDescEntity> getSapMatPlantNormDescEntityDao() {
		if (sapMatPlantNormDescEntityDao == null) {
			sapMatPlantNormDescEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapMatPlantNormDescEntity.class);
		}
		return sapMatPlantNormDescEntityDao;
	}

	public ReadOnlyDao<SapMatStagingAreaEntity> getSapMatStagingAreaEntityDao() {
		if (sapMatStagingAreaEntityDao == null) {
			sapMatStagingAreaEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapMatStagingAreaEntity.class);
		}
		return sapMatStagingAreaEntityDao;
	}

	public ReadOnlyDao<SapMaterialDescEntity> getSapMaterialDescEntityDao() {
		if (sapMaterialDescEntityDao == null) {
			sapMaterialDescEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapMaterialDescEntity.class);
		}
		return sapMaterialDescEntityDao;
	}

	public ReadOnlyDao<SapMaterialPlantDescEntity> getSapMaterialPlantDescEntityDao() {
		if (sapMaterialPlantDescEntityDao == null) {
			sapMaterialPlantDescEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapMaterialPlantDescEntity.class);
		}
		return sapMaterialPlantDescEntityDao;
	}

	public ReadOnlyDao<SapMaterialPlantSareaEntity> getSapMaterialPlantSareaEntityDao() {
		if (sapMaterialPlantSareaEntityDao == null) {
			sapMaterialPlantSareaEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapMaterialPlantSareaEntity.class);
		}
		return sapMaterialPlantSareaEntityDao;
	}

	public ReadOnlyDao<SapNetpOrderItemSumEntity> getSapNetpOrderItemSumEntityDao() {
		if (sapNetpOrderItemSumEntityDao == null) {
			sapNetpOrderItemSumEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapNetpOrderItemSumEntity.class);
		}
		return sapNetpOrderItemSumEntityDao;
	}

	public ReadOnlyDao<SapSoldBillSareaSrchEntity> getSapSoldBillSareaSrchEntityDao() {
		if (sapSoldBillSareaSrchEntityDao == null) {
			sapSoldBillSareaSrchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapSoldBillSareaSrchEntity.class);
		}
		return sapSoldBillSareaSrchEntityDao;
	}

	public ReadOnlyDao<SapSoldPaySareaSrchEntity> getSapSoldPaySareaSrchEntityDao() {
		if (sapSoldPaySareaSrchEntityDao == null) {
			sapSoldPaySareaSrchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapSoldPaySareaSrchEntity.class);
		}
		return sapSoldPaySareaSrchEntityDao;
	}

	public ReadOnlyDao<SapSoldtoSrchEntity> getSapSoldtoSrchEntityDao() {
		if (sapSoldtoSrchEntityDao == null) {
			sapSoldtoSrchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapSoldtoSrchEntity.class);
		}
		return sapSoldtoSrchEntityDao;
	}

	public ReadOnlyDao<SapVendorStagingAreaEntity> getSapVendorStagingAreaEntityDao() {
		if (sapVendorStagingAreaEntityDao == null) {
			sapVendorStagingAreaEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapVendorStagingAreaEntity.class);
		}
		return sapVendorStagingAreaEntityDao;
	}

	public ReadOnlyDao<SapbpRailwayStationEntity> getSapbpRailwayStationEntityDao() {
		if (sapbpRailwayStationEntityDao == null) {
			sapbpRailwayStationEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapbpRailwayStationEntity.class);
		}
		return sapbpRailwayStationEntityDao;
	}

	public ReadOnlyDao<SareaDisplayEntity> getSareaDisplayEntityDao() {
		if (sareaDisplayEntityDao == null) {
			sareaDisplayEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SareaDisplayEntity.class);
		}
		return sareaDisplayEntityDao;
	}

	public ReadOnlyDao<SdoOnetimeDataEntity> getSdoOnetimeDataEntityDao() {
		if (sdoOnetimeDataEntityDao == null) {
			sdoOnetimeDataEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SdoOnetimeDataEntity.class);
		}
		return sdoOnetimeDataEntityDao;
	}

	public ReadOnlyDao<SentContractItemQtyEntity> getSentContractItemQtyEntityDao() {
		if (sentContractItemQtyEntityDao == null) {
			sentContractItemQtyEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SentContractItemQtyEntity.class);
		}
		return sentContractItemQtyEntityDao;
	}

	public ReadOnlyDao<ContractItemQtyEntity> getContractItemQtyEntityDao() {
		if (contractItemQtyEntityDao == null) {
			contractItemQtyEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ContractItemQtyEntity.class);
		}
		return contractItemQtyEntityDao;
	}

	public ReadOnlyDao<ShipmentdocAssignmentEntity> getShipmentdocAssignmentEntityDao() {
		if (shipmentdocAssignmentEntityDao == null) {
			shipmentdocAssignmentEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ShipmentdocAssignmentEntity.class);
		}
		return shipmentdocAssignmentEntityDao;
	}

	public ReadOnlyDao<ShippBimodalRoadRailEntity> getShippBimodalRoadRailEntityDao() {
		if (shippBimodalRoadRailEntityDao == null) {
			shippBimodalRoadRailEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ShippBimodalRoadRailEntity.class);
		}
		return shippBimodalRoadRailEntityDao;
	}

	public ReadOnlyDao<ShippingStatEntity> getShippingStatEntityDao() {
		if (shippingStatEntityDao == null) {
			shippingStatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ShippingStatEntity.class);
		}
		return shippingStatEntityDao;
	}

	public ReadOnlyDao<ShippingpointMaterialEntity> getShippingpointMaterialEntityDao() {
		if (shippingpointMaterialEntityDao == null) {
			shippingpointMaterialEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ShippingpointMaterialEntity.class);
		}
		return shippingpointMaterialEntityDao;
	}

	public ReadOnlyDao<ShiptoSapBpEntity> getShiptoSapBpEntityDao() {
		if (shiptoSapBpEntityDao == null) {
			shiptoSapBpEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ShiptoSapBpEntity.class);
		}
		return shiptoSapBpEntityDao;
	}

	public ReadOnlyDao<ShiptoSareaDetailEntity> getShiptoSareaDetailEntityDao() {
		if (shiptoSareaDetailEntityDao == null) {
			shiptoSareaDetailEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ShiptoSareaDetailEntity.class);
		}
		return shiptoSareaDetailEntityDao;
	}

	public ReadOnlyDao<ShiptoSareaRestrSearEntity> getShiptoSareaRestrSearEntityDao() {
		if (shiptoSareaRestrSearEntityDao == null) {
			shiptoSareaRestrSearEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ShiptoSareaRestrSearEntity.class);
		}
		return shiptoSareaRestrSearEntityDao;
	}

	public ReadOnlyDao<ShiptoSoldtoLnfDataEntity> getShiptoSoldtoLnfDataEntityDao() {
		if (shiptoSoldtoLnfDataEntityDao == null) {
			shiptoSoldtoLnfDataEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ShiptoSoldtoLnfDataEntity.class);
		}
		return shiptoSoldtoLnfDataEntityDao;
	}

	public ReadOnlyDao<ShiptoWagonPrioFactorEntity> getShiptoWagonPrioFactorEntityDao() {
		if (shiptoWagonPrioFactorEntityDao == null) {
			shiptoWagonPrioFactorEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ShiptoWagonPrioFactorEntity.class);
		}
		return shiptoWagonPrioFactorEntityDao;
	}

	public ReadOnlyDao<SimpleVehicleAssgEntity> getSimpleVehicleAssgEntityDao() {
		if (simpleVehicleAssgEntityDao == null) {
			simpleVehicleAssgEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SimpleVehicleAssgEntity.class);
		}
		return simpleVehicleAssgEntityDao;
	}

	public ReadOnlyDao<SimpvehLoadOrderItemEntity> getSimpvehLoadOrderItemEntityDao() {
		if (simpvehLoadOrderItemEntityDao == null) {
			simpvehLoadOrderItemEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SimpvehLoadOrderItemEntity.class);
		}
		return simpvehLoadOrderItemEntityDao;
	}

	public ReadOnlyDao<SimpvehLoadingpointEntity> getSimpvehLoadingpointEntityDao() {
		if (simpvehLoadingpointEntityDao == null) {
			simpvehLoadingpointEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SimpvehLoadingpointEntity.class);
		}
		return simpvehLoadingpointEntityDao;
	}

	public ReadOnlyDao<SimpvehOrdItemMatEntity> getSimpvehOrdItemMatEntityDao() {
		if (simpvehOrdItemMatEntityDao == null) {
			simpvehOrdItemMatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SimpvehOrdItemMatEntity.class);
		}
		return simpvehOrdItemMatEntityDao;
	}

	public ReadOnlyDao<SimpvehOrdVhcEntity> getSimpvehOrdVhcEntityDao() {
		if (simpvehOrdVhcEntityDao == null) {
			simpvehOrdVhcEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SimpvehOrdVhcEntity.class);
		}
		return simpvehOrdVhcEntityDao;
	}

	public ReadOnlyDao<SimpvehUnalcOrdEntity> getSimpvehUnalcOrdEntityDao() {
		if (simpvehUnalcOrdEntityDao == null) {
			simpvehUnalcOrdEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SimpvehUnalcOrdEntity.class);
		}
		return simpvehUnalcOrdEntityDao;
	}

	public ReadOnlyDao<SoldshipBestFlexSrchEntity> getSoldshipBestFlexSrchEntityDao() {
		if (soldshipBestFlexSrchEntityDao == null) {
			soldshipBestFlexSrchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SoldshipBestFlexSrchEntity.class);
		}
		return soldshipBestFlexSrchEntityDao;
	}

	public ReadOnlyDao<SoldshipFlexSrchEntity> getSoldshipFlexSrchEntityDao() {
		if (soldshipFlexSrchEntityDao == null) {
			soldshipFlexSrchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SoldshipFlexSrchEntity.class);
		}
		return soldshipFlexSrchEntityDao;
	}

	public ReadOnlyDao<SoldshipSareaFlexSrchEntity> getSoldshipSareaFlexSrchEntityDao() {
		if (soldshipSareaFlexSrchEntityDao == null) {
			soldshipSareaFlexSrchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SoldshipSareaFlexSrchEntity.class);
		}
		return soldshipSareaFlexSrchEntityDao;
	}

	public ReadOnlyDao<TableInfoEntity> getTableInfoEntityDao() {
		if (tableInfoEntityDao == null) {
			tableInfoEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, TableInfoEntity.class);
		}
		return tableInfoEntityDao;
	}

	public ReadOnlyDao<TestSpDropdownEntity> getTestSpDropdownEntityDao() {
		if (testSpDropdownEntityDao == null) {
			testSpDropdownEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, TestSpDropdownEntity.class);
		}
		return testSpDropdownEntityDao;
	}

	public ReadOnlyDao<TimezoneEntity> getTimezoneEntityDao() {
		if (timezoneEntityDao == null) {
			timezoneEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, TimezoneEntity.class);
		}
		return timezoneEntityDao;
	}

	public ReadOnlyDao<TruckAssignmentEntity> getTruckAssignmentEntityDao() {
		if (truckAssignmentEntityDao == null) {
			truckAssignmentEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, TruckAssignmentEntity.class);
		}
		return truckAssignmentEntityDao;
	}

	public ReadOnlyDao<TruckChartAssignedEntity> getTruckChartAssignedEntityDao() {
		if (truckChartAssignedEntityDao == null) {
			truckChartAssignedEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, TruckChartAssignedEntity.class);
		}
		return truckChartAssignedEntityDao;
	}

	public ReadOnlyDao<TruckLastKnownCoordEntity> getTruckLastKnownCoordEntityDao() {
		if (truckLastKnownCoordEntityDao == null) {
			truckLastKnownCoordEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, TruckLastKnownCoordEntity.class);
		}
		return truckLastKnownCoordEntityDao;
	}

	public ReadOnlyDao<TruckRevenuePlanMatEntity> getTruckRevenuePlanMatEntityDao() {
		if (truckRevenuePlanMatEntityDao == null) {
			truckRevenuePlanMatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, TruckRevenuePlanMatEntity.class);
		}
		return truckRevenuePlanMatEntityDao;
	}

	public ReadOnlyDao<TruckRevenuePlanningEntity> getTruckRevenuePlanningEntityDao() {
		if (truckRevenuePlanningEntityDao == null) {
			truckRevenuePlanningEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, TruckRevenuePlanningEntity.class);
		}
		return truckRevenuePlanningEntityDao;
	}

	public ReadOnlyDao<TruckRouteSearchEntity> getTruckRouteSearchEntityDao() {
		if (truckRouteSearchEntityDao == null) {
			truckRouteSearchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, TruckRouteSearchEntity.class);
		}
		return truckRouteSearchEntityDao;
	}

	public ReadOnlyDao<UserActivityLogEntity> getUserActivityLogEntityDao() {
		if (userActivityLogEntityDao == null) {
			userActivityLogEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, UserActivityLogEntity.class);
		}
		return userActivityLogEntityDao;
	}

	public ReadOnlyDao<UserAdminEntity> getUserAdminEntityDao() {
		if (userAdminEntityDao == null) {
			userAdminEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, UserAdminEntity.class);
		}
		return userAdminEntityDao;
	}

	public ReadOnlyDao<UserRoleSelectionEntity> getUserRoleSelectionEntityDao() {
		if (userRoleSelectionEntityDao == null) {
			userRoleSelectionEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, UserRoleSelectionEntity.class);
		}
		return userRoleSelectionEntityDao;
	}

	public ReadOnlyDao<UtilLastLoginEntity> getUtilLastLoginEntityDao() {
		if (utilLastLoginEntityDao == null) {
			utilLastLoginEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, UtilLastLoginEntity.class);
		}
		return utilLastLoginEntityDao;
	}

	public ReadOnlyDao<UtilMatplantMissingEntity> getUtilMatplantMissingEntityDao() {
		if (utilMatplantMissingEntityDao == null) {
			utilMatplantMissingEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, UtilMatplantMissingEntity.class);
		}
		return utilMatplantMissingEntityDao;
	}

	public ReadOnlyDao<UtilSapmatdescMissingEntity> getUtilSapmatdescMissingEntityDao() {
		if (utilSapmatdescMissingEntityDao == null) {
			utilSapmatdescMissingEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, UtilSapmatdescMissingEntity.class);
		}
		return utilSapmatdescMissingEntityDao;
	}

	public ReadOnlyDao<VehicleEntity> getVehicleEntityDao() {
		if (vehicleEntityDao == null) {
			vehicleEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, VehicleEntity.class);
		}
		return vehicleEntityDao;
	}

	public ReadOnlyDao<VgdiaTestTypesEntity> getVgdiaTestTypesEntityDao() {
		if (vgdiaTestTypesEntityDao == null) {
			vgdiaTestTypesEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, VgdiaTestTypesEntity.class);
		}
		return vgdiaTestTypesEntityDao;
	}

	public ReadOnlyDao<WagonDetailAssignmentEntity> getWagonDetailAssignmentEntityDao() {
		if (wagonDetailAssignmentEntityDao == null) {
			wagonDetailAssignmentEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, WagonDetailAssignmentEntity.class);
		}
		return wagonDetailAssignmentEntityDao;
	}

	public ReadOnlyDao<WagonDetailLoadListEntity> getWagonDetailLoadListEntityDao() {
		if (wagonDetailLoadListEntityDao == null) {
			wagonDetailLoadListEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, WagonDetailLoadListEntity.class);
		}
		return wagonDetailLoadListEntityDao;
	}

	public ReadOnlyDao<WagonSearchEntity> getWagonSearchEntityDao() {
		if (wagonSearchEntityDao == null) {
			wagonSearchEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, WagonSearchEntity.class);
		}
		return wagonSearchEntityDao;
	}

	/**
	 * Keeping for DB backward compatibility
	 *
	 * @return
	 */
	public ReadOnlyDao<WebbestSoldshipSareaEntity> getWebbestSoldshipSareaEntityDao() {
		if (webbestSoldshipSareaEntityDao == null) {
			webbestSoldshipSareaEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, WebbestSoldshipSareaEntity.class);
		}
		return webbestSoldshipSareaEntityDao;
	}

	public ReadOnlyDao<WeborderShipToSoldToEntity> getWeborderShipToSoldToEntityDao() {
		if (weborderShipToSoldToEntityDao == null) {
			weborderShipToSoldToEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, WeborderShipToSoldToEntity.class);
		}
		return weborderShipToSoldToEntityDao;
	}

	public ReadOnlyDao<BpnStagingAreaNewDataAvailableEntity> getBpnStagingAreaNewDataAvailableEntityDao() {
		if (bpnStagingAreaNewDataAvailableEntityDao == null) {
			bpnStagingAreaNewDataAvailableEntityDao =
					new ReadOnlyDaoImpl<>(em, authorizer, BpnStagingAreaNewDataAvailableEntity.class);
		}
		return bpnStagingAreaNewDataAvailableEntityDao;
	}

	public ReadOnlyDao<SdnStagingAreaNewDataAvailableEntity> getSdnStagingAreaNewDataAvailableEntityDao() {
		if (sdnStagingAreaNewDataAvailableEntityDao == null) {
			sdnStagingAreaNewDataAvailableEntityDao =
					new ReadOnlyDaoImpl<>(em, authorizer, SdnStagingAreaNewDataAvailableEntity.class);
		}
		return sdnStagingAreaNewDataAvailableEntityDao;
	}

	public ReadOnlyDao<VehnStagingAreaNewDataAvailableEntity> getVehnStagingAreaNewDataAvailableEntityDao() {
		if (vehnStagingAreaNewDataAvailableEntityDao == null) {
			vehnStagingAreaNewDataAvailableEntityDao =
					new ReadOnlyDaoImpl<>(em, authorizer, VehnStagingAreaNewDataAvailableEntity.class);
		}
		return vehnStagingAreaNewDataAvailableEntityDao;
	}

	public ReadOnlyDao<MnnStagingAreaNewDataAvailableEntity> getMnnStagingAreaNewDataAvailableEntityDao() {
		if (mnnStagingAreaNewDataAvailableEntityDao == null) {
			mnnStagingAreaNewDataAvailableEntityDao =
					new ReadOnlyDaoImpl<>(em, authorizer, MnnStagingAreaNewDataAvailableEntity.class);
		}
		return mnnStagingAreaNewDataAvailableEntityDao;
	}

	public ReadOnlyDao<MnStagingAreaNewDataAvailableEntity> getMnStagingAreaNewDataAvailableEntityDao() {
		if (mnStagingAreaNewDataAvailableEntityDao == null) {
			mnStagingAreaNewDataAvailableEntityDao =
					new ReadOnlyDaoImpl<>(em, authorizer, MnStagingAreaNewDataAvailableEntity.class);
		}
		return mnStagingAreaNewDataAvailableEntityDao;
	}

	public ReadOnlyDao<VnStagingAreaNewDataAvailableEntity> getVnStagingAreaNewDataAvailableEntityDao() {
		if (vnStagingAreaNewDataAvailableEntityDao == null) {
			vnStagingAreaNewDataAvailableEntityDao =
					new ReadOnlyDaoImpl<>(em, authorizer, VnStagingAreaNewDataAvailableEntity.class);
		}
		return vnStagingAreaNewDataAvailableEntityDao;
	}

	public ReadOnlyDao<IncotermDelivTypeEntity> getIncotermDelivTypeEntityDao() {
		if (incotermDelivTypeEntityDao == null) {
			incotermDelivTypeEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, IncotermDelivTypeEntity.class);
		}
		return incotermDelivTypeEntityDao;
	}

	public ReadOnlyDao<PlanningTruckEntity> getPlanningTruckEntityDao() {
		if (planningTruckEntityDao == null) {
			planningTruckEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, PlanningTruckEntity.class);
		}
		return planningTruckEntityDao;
	}

	public ReadOnlyDao<SapMaterialSareaInfoEntity> getSapMaterialSareaInfoEntityDao() {
		if (sapMaterialSareaInfoEntityDao == null) {
			sapMaterialSareaInfoEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapMaterialSareaInfoEntity.class);
		}
		return sapMaterialSareaInfoEntityDao;
	}

	public ReadOnlyDao<SapMatSareaContractEntity> getSapMatSareaContractEntityDao() {
		if (sapMatSareaContractEntityDao == null) {
			sapMatSareaContractEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, SapMatSareaContractEntity.class);
		}
		return sapMatSareaContractEntityDao;
	}

	public ReadOnlyDao<UserTypeViewEntity> getUserTypeViewEntityDao() {
		if (userTypeViewEntityDao == null) {
			userTypeViewEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, UserTypeViewEntity.class);
		}
		return userTypeViewEntityDao;
	}

	public ReadOnlyDao<DemandPlanShipSoldEntity> getDemandPlanShipSoldEntityDao() {
		if (demandPlanShipSoldEntityDao == null) {
			demandPlanShipSoldEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DemandPlanShipSoldEntity.class);
		}
		return demandPlanShipSoldEntityDao;
	}

	public ReadOnlyDao<DemandPlanPlanEntity> getDemandPlanPlanEntityDao() {
		if (demandPlanPlanEntityDao == null) {
			demandPlanPlanEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DemandPlanPlanEntity.class);
		}
		return demandPlanPlanEntityDao;
	}

	public ReadOnlyDao<DemandPlanMatEntity> getDemandPlanMatEntityDao() {
		if (demandPlanMatEntityDao == null) {
			demandPlanMatEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DemandPlanMatEntity.class);
		}
		return demandPlanMatEntityDao;
	}

	public ReadOnlyDao<DemandPlanDiffEntity> getDemandPlanDiffEntityDao() {
		if (demandPlanDiffEntityDao == null) {
			demandPlanDiffEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, DemandPlanDiffEntity.class);
		}
		return demandPlanDiffEntityDao;
	}

	public ReadOnlyDao<UnloadsForTargetEntity> getUnloadsForTargetEntityDao() {
		if (unloadsForTargetEntityDao == null) {
			unloadsForTargetEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, UnloadsForTargetEntity.class);
		}
		return unloadsForTargetEntityDao;
	}


	public ReadOnlyDao<ItrackShiptoSapBpEntity> getItrackShiptoSapBpEntityDao() {
		if (itrackShiptoSapBpEntityDao == null) {
			itrackShiptoSapBpEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ItrackShiptoSapBpEntity.class);
		}
		return itrackShiptoSapBpEntityDao;
	}

	public ReadOnlyDao<ItrackSoldtoSapBpEntity> getItrackSoldtoSapBpEntityDao() {
		if (itrackSoldtoSapBpEntityDao == null) {
			itrackSoldtoSapBpEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ItrackSoldtoSapBpEntity.class);
		}
		return itrackSoldtoSapBpEntityDao;
	}

	public ReadOnlyDao<ItrackReportEntity> getItrackReportEntityDao() {
		if (itrackReportEntityDao == null) {
			itrackReportEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, ItrackReportEntity.class);
		}
		return itrackReportEntityDao;
	}

	public ReadOnlyDao<OcGetOrdersViewEntity> getOcGetOrdersViewEntityDao() {
		if (ocGetOrdersViewEntityDao == null) {
			ocGetOrdersViewEntityDao = new ReadOnlyDaoImpl<>(em, authorizer, OcGetOrdersViewEntity.class);
		}
		return ocGetOrdersViewEntityDao;
	}
}
