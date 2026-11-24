/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.store;

import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.business.services.pdftools.layout.PdfLayoutGenerationTool;
import com.crh.logon.business.services.pdftools.layout.colldelnote.OrderItemTotalDTO;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectiveDeliveryNotePdfDataProvider extends DeliveryNotePdfDataProvider {

	private static final Map<String, Object> GLOBAL_KEYS;

	static {
		Map<String, Object> globalKeys = new HashMap<String, Object>();

		globalKeys.put("DeliveryNoteLabel", new LabelData(DeliveryNoteLabel.COLL_ORDER_NUMBER));
		globalKeys.put("VersionLabel", new LabelData(DeliveryNoteLabel.VERSION));
		globalKeys.put("TotalWaitingTime", new NoteData(DeliveryNoteData.WAITING_TIME));

		globalKeys.put("TableDeliveryNoteLabel", new LabelData(DeliveryNoteLabel.ORDERS_TABLE_DELNOTE));
		globalKeys.put("TableSapNoLabael", new LabelData(DeliveryNoteLabel.ORDERS_TABLE_SAPNR));
		globalKeys.put("TableMaterialLabel", new LabelData(DeliveryNoteLabel.ORDERS_TABLE_MATERIAL));
		globalKeys.put("TableTruckLabel", new LabelData(DeliveryNoteLabel.ORDERS_TABLE_TRUCK));
		globalKeys.put("TableLoadingTimeLabel", new LabelData(DeliveryNoteLabel.ORDERS_TABLE_LOAD));
		globalKeys.put("TableDeliveryTimeLabel", new LabelData(DeliveryNoteLabel.ORDERS_TABLE_DELIVERY));
		globalKeys.put("TableNetWLabel", new LabelData(DeliveryNoteLabel.ORDERS_TABLE_NET));

		globalKeys.put("TablePhysicalMatLabel", new LabelData(DeliveryNoteLabel.PHYSICAL_MAT));
		globalKeys.put("TableTotal", new LabelData(DeliveryNoteLabel.TOTAL));
		globalKeys.put("TableServicesMat", new LabelData(DeliveryNoteLabel.SERVICES));
		globalKeys.put("TableWaitingTime", new LabelData(DeliveryNoteLabel.WAIT_TIME_TOTAL));

		GLOBAL_KEYS = Collections.unmodifiableMap(globalKeys);
	}

	public CollectiveDeliveryNotePdfDataProvider(
			DeliveryNotePdfDataStore store, GenericBlobServiceLocalInterface blobService, SareaEntity sarea) {
		super(store, blobService, sarea);

		keys.putAll(GLOBAL_KEYS);
	}

	@Override
	protected void initDeliveryNoteDependentSettings() {
		initOrderItemSettings();
		initTotalOrderItemSettings();

		keys.put("IsPayerIdenticalToBillto",
				 store.getData(DeliveryNoteData.PAYER_SAP)
					  .equals(store.getData(DeliveryNoteData.BILLTO_SAP)));
	}

	private void initOrderItemSettings() {
		List<OrderItemDTO> orderItems = store.getDTOList(OrderItemDTO.class);
		for (int i = 0; i < orderItems.size(); i++) {
			OrderItemDTO dto = orderItems.get(i);
			keys.put(String.format("DeliveryOrderItem[%s].deliveryNoteNo", i), dto.getOrderNr());
			keys.put(String.format("DeliveryOrderItem[%s].SapMaterialNo", i), dto.getSapNr());
			keys.put(String.format("DeliveryOrderItem[%s].BrandSalesOrMatDescription", i), dto.getMaterialName());
			keys.put(String.format("DeliveryOrderItem[%s].VehicleIdent", i), dto.getTruckNr());
			keys.put(String.format("DeliveryOrderItem[%s].LoadingTime", i), dto.getLoadingTime());
			keys.put(String.format("DeliveryOrderItem[%s].DeliveryTime", i), dto.getDeliveryTime());
			keys.put(String.format("DeliveryOrderItem[%s].DeliveredQtyAndUoM", i),
					 toWeightString(OrderItemDTO.getPkgDependantWeight(dto),
									OrderItemDTO.getPkgDependantUom(dto)));
		}
	}

	private void initTotalOrderItemSettings() {
		int totalPhysicalDeliveryCnt = 0;

		List<OrderItemTotalDTO> totalItemDTOs = store.getDTOList(OrderItemTotalDTO.class);
		for (int i = 0; i < totalItemDTOs.size(); i++) {
			OrderItemTotalDTO dto = totalItemDTOs.get(i);

			String prefix;
			if (dto.isPhysical()) {
				prefix = "totalPhysicalMaterial";
				totalPhysicalDeliveryCnt++;
			}
			else {
				prefix = "totalServiceMaterial";
			}

			keys.put(String.format("%s[%s].SapMaterialNo", prefix, i), dto.getSapNr());
			keys.put(String.format("%s[%s].BrandSalesOrMatDescription", prefix, i), dto.getMaterialName());
			keys.put(String.format("%s[%s].SumQtyDelivered", prefix, i),
					 toWeightString(dto.getNetWeight(), dto.getUom()));
		}

		keys.put("totalPhysicalMaterialCountDeliveries", String.valueOf(totalPhysicalDeliveryCnt));

		if (StringUtils.notEmpty(store.getData(DeliveryNoteData.NET_TOTAL_WEIGHT))) {
			keys.put("totalPhysicalMaterialSumQty",
					 toWeightString(store.getData(DeliveryNoteData.NET_TOTAL_WEIGHT),
									store.getData(DeliveryNoteData.NET_TOTAL_UOM)));
		}

		if (StringUtils.notEmpty(store.getData(DeliveryNoteData.WAITING_TIME))) {
			keys.put("TotalWaitingTime", store.getData(DeliveryNoteData.WAITING_TIME));
		}
	}

	private String toWeightString(BigDecimal weight, String uom) {
		return toWeightString(
				weight != null ? PdfLayoutGenerationTool.WEIGHT_FORMAT.format(weight) : null,
				uom);
	}

	private String toWeightString(String weight, String uom) {
		if (StringUtils.empty(weight)) {
			return null;
		}

		StringBuilder buf = new StringBuilder();
		buf.append(weight);

		if (StringUtils.notEmpty(uom)) {
			buf.append(" ");
			buf.append(StringUtils.cutStringWithDots(uom, 9));
		}

		return buf.toString();
	}
}
