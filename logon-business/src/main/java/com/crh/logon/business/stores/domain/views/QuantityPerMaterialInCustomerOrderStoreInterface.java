package com.crh.logon.business.stores.domain.views;
import java.util.List;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.views.CustordItemSumQtyExtEntity;

@Local
public interface QuantityPerMaterialInCustomerOrderStoreInterface extends GenericReadOnlyInterface<CustordItemSumQtyExtEntity> {
	
	/**
	 * Returns a list for each customer order item describing how much is
	 * loaded, delivered, etc. similar categorization per customer order item.
	 * See the description of {@link CustordItemSumQtyExtEntity}
	 * 
	 * @param custorderId
	 * @param language
	 * @return
	 */
	public List<CustordItemSumQtyExtEntity> findByCustorderId(Long custorderId,  String language);
	
}
