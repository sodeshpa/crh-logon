/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.railcaroptimization;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class RailCarTypeInfoBean implements Serializable {

	//part about status of the wagon types
	Map <Long, Integer> loadedMap = new HashMap();
	Map <Long, Integer> incomingMap = new HashMap();
	Map <Long, Integer> emptyMap = new HashMap();
	Map <Long, Integer> availableMap = new HashMap();

	//part about orders and the priority they represent
	Map typePriorityMap = new HashMap();
	
	List wagonTypePositionList = new LinkedList();
	/**
	 * Simple form bean which has maps containing the wagon types and the count of them 
	 * for the 4 different columns shown on the rail car optimization tab.
	 * @param wagon
	 * @param isReturning
	 */
	public RailCarTypeInfoBean(List optimizationData) {
		
		 initWagonStatusRelatedInfo(optimizationData); 

	}

	/**
	 * @param optimizationData
	 */
	private void initWagonStatusRelatedInfo(List optimizationData) {
		Iterator it = optimizationData.iterator();
			while (it.hasNext()) {
				Object[] object =  (Object[])it.next();

				Long wagonTypeCd = (object[0] != null) ? ((BigDecimal) object[0]).longValue() : 0;
				String gr = (object[3] != null) ? (object[3]).toString() : "";				
				Integer count = (object[4] != null) ? ((BigDecimal)object[4]).intValue() : 0;

				Integer availableCount = 0;

				if (gr.equals("loaded")){
					loadedMap.put(wagonTypeCd, count);
				}
				if (gr.equals("not loaded")){
					emptyMap.put(wagonTypeCd, count);
				}
				if (gr.equals("incoming")){
					incomingMap.put(wagonTypeCd, count);
				}

				Object countAvailable = availableMap.get(wagonTypeCd);
				if ( countAvailable != null ) {
					Integer newCountAvailable = (Integer) countAvailable + count;
					availableMap.put(wagonTypeCd,newCountAvailable);
				}
				else {
					availableMap.put(wagonTypeCd,count);
				}

				if ( !wagonTypePositionList.contains(wagonTypeCd) ) {					
					wagonTypePositionList.add(wagonTypeCd);
				}
			}
	}

	public Map getLoadedMap() {
		return loadedMap;
	}

	public Map getIncomingMap() {
		return incomingMap;
	}

	public Map getEmptyMap() {
		return emptyMap;
	}

	public Map getAvailableMap() {
		return availableMap;
	}

	/**
	 * @return the wagonTypePositionList
	 */
	public List getWagonTypePositionList() {
		return wagonTypePositionList;
	}

	/**
	 * @param typePriorityList the typePriorityList to set
	 */
	public void setTypePriorityMap(List typePriorityList) {
		Iterator it = typePriorityList.iterator();
		
		Map map = new HashMap();
		while (it.hasNext()) {
			Object [] o = (Object []) it.next();
			Long typeCd = (Long)(o)[0];
			BigDecimal factor = (BigDecimal)(o)[1];
			map.put(typeCd, factor);
		}
		this.typePriorityMap = map;
	}

	/**
	 * @return the typePriorityMap
	 */
	public Map getTypePriorityMap() {
		return typePriorityMap;
	}
	
}
