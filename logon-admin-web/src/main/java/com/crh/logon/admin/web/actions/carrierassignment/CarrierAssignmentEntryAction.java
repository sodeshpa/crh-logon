/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.carrierassignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.carrierassignment.CarrierAssignmentForm;
import com.crh.logon.business.services.interfaces.CarrierAssignmentService;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.persistence.entities.views.PoolShiptoMatSoldtoEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 *
 * @struts.action name="admin_CarrierAssignmentForm"
 *  path="/carrierassignment/CarrierAssignmentEntry" scope="session"
 *  validate="false" input="/carrierassignment/CarrierAssignment.def"
 *
 * @struts.action-forward name="success" path="/carrierassignment/CarrierAssignment.def"
 * 
 * This screen cannot be accessed, it's disabled because of poor performance.
 * See also tlgnu_role_func.sql.
 * The same functionality can be used in the Dispokarte.
 */
public class CarrierAssignmentEntryAction extends ActionSupport {

	 private static final JcanLogger LOG =
	        JcanLoggerFactory.getLogger(CarrierAssignmentEntryAction.class);

		public ActionForward execute(
			ActionMapping mapping, ActionForm form,
			HttpServletRequest request,	HttpServletResponse response)
			throws IOException, ServletException
		{
			// [[
			ActionForward result = null;
			Object[][] params = {{"mapping", mapping},
								{"form", form},
								{"request", request},
								{"response", response}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "execute", params);
			}
			// ]]
			try {
				Authorization.authorize(Authorization.MASTER_DATA_CARRIER_ASSIGNMT_SHIPTO_PROD);
				setSelectedTab("masterdata", request);
				setSelectedSub1Tab("carrier", request);
				
				CarrierAssignmentForm helpForm = (CarrierAssignmentForm) form;
				helpForm.clean();				
				String userId = "" + getUser().getUserId();
								
				CarrierAssignmentService caServ = getServiceLocator().getCarrierAssignmentService();
				List poolList = caServ.fetchPoolEntityList();  
				
				List poolIdList = new ArrayList();
				ListIterator poolIt = poolList.listIterator();
				
				while ( poolIt.hasNext() ) {
					PoolEntity poolEntity = (PoolEntity) poolIt.next();
					poolIdList.add(poolEntity.getPoolId());
				}
				
				helpForm.setUserPoolIdList(poolIdList);
				
				if (poolIdList.isEmpty()) {
					return result = mapping.findForward("success");
				}
				
				// load table data ( pool-kunde-shipto-produkt combinations )
				List poolShiptoMaterialList = caServ.fetchPoolShiptoMatSoldtoList(poolIdList, request.getLocale().getLanguage());
				helpForm.setPoolShiptoMaterialList(poolShiptoMaterialList);
				
				// load possible carriers
		 		List poolCarrierList = caServ.fetchCarriersForPoolIdList(poolIdList);
		 		
		 		Map poolCarrierMap = new HashMap();
		 		
		 		//	 build a map of poolId -> ArrayList( poolCarrierCarrierEntities belonging to the given poolId )
		 		if ( poolCarrierList != null) {
		 			ListIterator listIt = poolCarrierList.listIterator();
		 			while (listIt.hasNext()) {
		 				PoolCarrierCarrierEntity pcc = (PoolCarrierCarrierEntity) listIt.next();
		 				if ( !poolCarrierMap.containsKey( pcc.getPoolId())) {
		 					poolCarrierMap.put(pcc.getPoolId(), new ArrayList());
		 				}
		 				((ArrayList) poolCarrierMap.get(pcc.getPoolId())).add(pcc);
		 			}
		 		}
		 		
				helpForm.setPoolCarrierMap(poolCarrierMap);
				
				
				if ( poolShiptoMaterialList == null ) {
					return result = mapping.findForward("success");
				}
				
				// fill filtering dropdowns
				ListIterator resultIt = poolShiptoMaterialList.listIterator();
				
				// the TreeMaps are used to sort the entries by name (key: displayName, value: id)
				// later these will be converted into LinkedHashMaps (key: id, value: displayName)
				TreeMap poolTreeMap = new TreeMap();
				TreeMap soldtoTreeMap = new TreeMap();
				TreeMap shiptoTreeMap = new TreeMap();
				TreeMap productTreeMap = new TreeMap();
				
				Map originalSelectedCarrierMap = new HashMap();
				Map originalFlagMap = new HashMap();
				
				while (resultIt.hasNext()) {
					PoolShiptoMatSoldtoEntity entity = (PoolShiptoMatSoldtoEntity) resultIt.next();
					
					// populate filter dropdowns
					if ( !poolTreeMap.containsKey(entity.getPoolName()) ) {
						poolTreeMap.put(entity.getPoolName(), entity.getPoolId());
					}
					if ( !soldtoTreeMap.containsKey(entity.getSoldtoName()) ) {
						soldtoTreeMap.put(entity.getSoldtoName(), entity.getSoldtoId());
					}
					if ( !shiptoTreeMap.containsKey(entity.getShiptoName()) ) {
						shiptoTreeMap.put(entity.getShiptoName(), entity.getShiptoNr());
					}
					if ( !productTreeMap.containsKey(entity.getMaterialDescription()) ) {
						productTreeMap.put(entity.getMaterialDescription(), entity.getMaterialNr());
					}
					
					// store the original values for the selected carrier and the auto forward checkbox
					originalSelectedCarrierMap.put(entity.getShiptoSareaRestrId(), entity.getCommCarrierId());
					originalFlagMap.put(entity.getShiptoSareaRestrId(), entity.getAutoCarrierAssignmentFlag());
				}
				
				helpForm.setPoolMap(mapConversion( new LinkedHashMap(), poolTreeMap));
				helpForm.setSoldtoMap(mapConversion( new LinkedHashMap(), soldtoTreeMap));
				helpForm.setShiptoMap(mapConversion( new LinkedHashMap(), shiptoTreeMap));
				helpForm.setProductMap(mapConversion( new LinkedHashMap(), productTreeMap));
				
				helpForm.setOriginalCheckBoxValueMap(originalFlagMap);
				helpForm.setOriginalSelectedCarrierMap(originalSelectedCarrierMap);
				return result = mapping.findForward("success");
			}
			finally {
			// [[
				if (logDebugEnabled) {
					LOG.exiting(result);
				}
			// ]]
			}
		}
		
		private LinkedHashMap mapConversion(LinkedHashMap destMap, TreeMap sourceMap) {
			while ( !sourceMap.isEmpty() ) {
				Object key = sourceMap.firstKey();
				destMap.put(sourceMap.get(key), key);
				sourceMap.remove(key);
			}
			return destMap;
		}
}
