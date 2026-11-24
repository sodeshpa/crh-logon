/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.comparator;

import java.util.Comparator;

import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;

/**
 * @author AdNovum Informatik AG
 */
// TODO (emoke Sep 7, 2012):REname to dtocomp
public class DeliverySettingWrapperComparator implements Comparator<DeliverySettingDTO> {


	@Override
	public int compare(DeliverySettingDTO o1, DeliverySettingDTO o2) {
		SapContractItemEntity sapContractItem1 = o1.getSapContractItem();
		SapContractItemEntity sapContractItem2 = o2.getSapContractItem();
		Long restrId = (o1.getShiptoSareaRestr() != null && o1.getShiptoSareaRestr().getShiptoSareaRestrId() != null) ? o1.getShiptoSareaRestr().getShiptoSareaRestrId() : Long.MAX_VALUE;
		Long restrId2 = (o2.getShiptoSareaRestr() != null && o2.getShiptoSareaRestr().getShiptoSareaRestrId() != null ) ? o2.getShiptoSareaRestr().getShiptoSareaRestrId() : Long.MAX_VALUE;
		
		int compareToFirstStep = comapareSapContractNo(sapContractItem1, sapContractItem2);
		if (compareToFirstStep != 0) {
			return compareToFirstStep;
		}
		else {
			Long contractItemNo1 = sapContractItem1 != null ? sapContractItem1.getContractItemNo() : null;
			Long contractItemNo2 = sapContractItem2 != null ? sapContractItem2.getContractItemNo() : null;
			int compareToSecondStep = compareIfOneElementIsNull(contractItemNo1, contractItemNo2);
			if (compareToSecondStep != 0) {
				return compareToSecondStep;
			}
			else {
				String matT1 = o1.getTxt();
				String matT2 = o2.getTxt();
				int compareToThirdStep = matT1.compareTo(matT2);
				if (compareToThirdStep != 0) {
					return compareToThirdStep;
				}
				else {
					return restrId.compareTo(restrId2);
				}
			}
		}
	}

	private int comapareSapContractNo(SapContractItemEntity sapContractItem1,
			SapContractItemEntity sapContractItem2) {
		SapContractEntity sapContract1 = sapContractItem1 != null ? sapContractItem1.getSapContract() : null;
		SapContractEntity sapContract2 = sapContractItem2 != null ? sapContractItem2.getSapContract() : null;
		String sapContractNo1 = sapContract1 != null ? sapContract1.getSapContractNo() : null;
		String sapContractNo2 = sapContract2 != null ? sapContract2.getSapContractNo() : null;
		
		int compareToFirstStep = compareIfOneElementIsNull(sapContractNo1, sapContractNo2);
		return compareToFirstStep;
	}

	/**
	 * @param o1
	 * @param sapContractNo12
	 * @return
	 */
	private int compareIfOneElementIsNull(Comparable o1, Object o2) {
		if(o1 == null && o2 != null){
			return 1;
		}else 
			if(o1 != null && o2 == null){
			return -1;
		}else 
			if(o1 == null && o2 == null){
			return 0;
		}else{
			return o1.compareTo(o2);
		}
	}
}
