/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.List;

import com.crh.logon.business.services.impl.OrderDetailServiceBean;
import com.crh.logon.persistence.entities.common.OrderTpSapVendorEntity;

/**
 * Utility class for {@link OrderDetailServiceBean}
 */
public class OrderDetailsUtil {
	
	/**
	 * Creates a List of {@link BindingsDTO} from the list of {@link OrderTpSapVendorEntity}
	 * @param orderTpSapVendorEntities list of {@link OrderTpSapVendorEntity}
	 * @return List of {@link BindingsDTO}
	 */
	public static List<BindingsDTO> createBindingsList(List<OrderTpSapVendorEntity> orderTpSapVendorEntities) {
		List<BindingsDTO> result = new ArrayList<BindingsDTO>(orderTpSapVendorEntities.size());
		
		for (OrderTpSapVendorEntity e: orderTpSapVendorEntities) {
			BindingsDTO b = BindingsDTO.createBindingsDTO(e);
			result.add(b);
		}
		
		return result;
	}

	/**
	 * Finds an item in the list of BindingsDTO by id
	 * @param tpSapVendorId
	 * @param existingBindings
	 * @return the index or -1 if not found
	 */
	public static int findItemById(Long tpSapVendorId, List<BindingsDTO> existingBindings) {
		for (int i = 0; i < existingBindings.size(); i++) {
			BindingsDTO dto = existingBindings.get(i);
			if (dto.getId().equals(tpSapVendorId)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Checks if the tpSapVendorId is already in the existingBindings array
	 * @param tpSapVendorId id to find in the BindingsDTO
	 * @param existingBindings the list of BindingsDTOs
	 * @return true, if the Binding was found
	 */
	public static boolean isBindingExists(Long tpSapVendorId, List<BindingsDTO> existingBindings) {
		for (int i = 0; i < existingBindings.size(); i++) {
			BindingsDTO dto = existingBindings.get(i);
			if (dto.getId().equals(tpSapVendorId)) {
				return true;
			}
		}
		return false;
	}

}
