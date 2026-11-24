/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.io.Serializable;
import java.util.Collections;

import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.dto.DeliverySetting;
import com.crh.logon.order.web.utils.comparator.BilltoComparator;
import com.crh.logon.order.web.utils.comparator.CarrierComparator;
import com.crh.logon.order.web.utils.comparator.LoadingPointComparator;
import com.crh.logon.order.web.utils.comparator.PlantComparator;
import com.crh.logon.order.web.utils.comparator.UomComparator;


/**
 * @author AdNovum Informatik AG
 */
public class DeliveryOptionConfigurationWrapper implements Serializable {

	
	public static DeliveryOptionConfigurationDTO getDeliveryOptionConfiguration(DeliverySettingSalesAreaMap mapBean, final DeliverySetting setting) {
		final DeliveryOptionConfigurationDTO deliveryOptionConfiguration = DeliverySettingFilterLogic.getDeliveryOptionConfiguration(setting, mapBean);
		sortDeliveryOptionConfiguration(deliveryOptionConfiguration);
		return deliveryOptionConfiguration;
	}
	
	public static void sortDeliveryOptionConfiguration(DeliveryOptionConfigurationDTO optionConfiguration) {
		
		Collections.sort(optionConfiguration.getShippingpoinList(), new ShippingpointComparator());
		Collections.sort(optionConfiguration.getBilltoList(), new BilltoComparator());
		Collections.sort(optionConfiguration.getCommercialCarrierList(), new CarrierComparator());
		Collections.sort(optionConfiguration.getLoadingpointList(), new LoadingPointComparator());
		Collections.sort(optionConfiguration.getProdPlantList(), new PlantComparator());
		Collections.sort(optionConfiguration.getSupplyPlantList(), new PlantComparator());
		Collections.sort(optionConfiguration.getUomList(), new UomComparator());
	}
	
}
