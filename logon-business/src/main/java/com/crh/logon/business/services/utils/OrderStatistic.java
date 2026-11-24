/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 * Contains information about number of orders and weight of orders.
 */
public class OrderStatistic implements Serializable{
    //statistic members

	//total weight of completed orders in tons (order is in status fulfilled)
    private double completedWeightOfOrders;
    
    //total weight of all orders in tons
    private double totalWeightOfOrders;
    
    //number of completed orders (order is in status fulfilled) 
    private long completedNumberOfOrders;
    
    //number of orders
    private long totalNumberOfOrders;
    
    //list containing OrderStatistic.MaterialStatisticEntries
    private List<MaterialStatisticEntry> materialAndWeight = new ArrayList<OrderStatistic.MaterialStatisticEntry>();
    
    private Map<String, MaterialStatisticEntry> mergedMap = new HashMap<String, MaterialStatisticEntry>();
    
    public OrderStatistic(){
    	this.completedWeightOfOrders = 0d;
        this.totalWeightOfOrders = 0d;
        this.completedNumberOfOrders = 0l;
        this.totalNumberOfOrders = 0l;
    }
    
	public double getCompletedWeightOfOrders() {
		return completedWeightOfOrders;
	}
	public void setCompletedWeightOfOrders(double completedWeightOfOrders) {
		this.completedWeightOfOrders = completedWeightOfOrders;
	}
	public double getTotalWeightOfOrders() {
		return totalWeightOfOrders;
	}
	public void setTotalWeightOfOrders(double totalWeightOfOrders) {
		this.totalWeightOfOrders = totalWeightOfOrders;
	}
	public long getCompletedNumberOfOrders() {
		return completedNumberOfOrders;
	}
	public void setCompletedNumberOfOrders(long completedNumberOfOrders) {
		this.completedNumberOfOrders = completedNumberOfOrders;
	}
	public long getTotalNumberOfOrders() {
		return totalNumberOfOrders;
	}
	public void setTotalNumberOfOrders(long totalNumberOfOrders) {
		this.totalNumberOfOrders = totalNumberOfOrders;
	}
	public double getWeightPercent() {
		if (completedWeightOfOrders == 0.0 || totalWeightOfOrders == 0.0){
			return 0.0;
		}
		else {
			return (completedWeightOfOrders / totalWeightOfOrders) * 100;
		}
	}
	public double getNumberPercent() {
		if (completedNumberOfOrders == 0L || totalNumberOfOrders == 0L){
			return 0.0;
		}
		else {
			return ((double)completedNumberOfOrders / (double)totalNumberOfOrders) * 100;
		}
	}

	public class MaterialStatisticEntry implements Serializable, Comparable<MaterialStatisticEntry>{

		//id of the material (SAP_MATERIAL_ID)
		long id;
		
		//name of the material (MATERIAL_TXT)
		String name;
		
		//production plant info
		String productionPlantCd;
		String productionPlantName;
		
		//weight ordered from the material in tons
		double weight;
		
		public MaterialStatisticEntry(long id, String name, double weight, String productionPlantCd, String productionPlantName){
			this.id = id;
			this.name = name;
			this.productionPlantCd = productionPlantCd;
			this.productionPlantName = productionPlantName;
			this.weight = weight;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public double getWeight() {
			return weight;
		}
		
		public MaterialStatisticEntry (MaterialStatisticEntry add1, MaterialStatisticEntry add2){
			if (add1.id == add2.id && add1.name.equals(add2.name) && add1.productionPlantCd.equals(add2.productionPlantCd) && add1.productionPlantName.equals(add2.productionPlantName)){				
				this.id = add1.id;
				this.name = add1.name;
				this.weight = add1.weight + add2.weight;
				this.productionPlantCd = add1.productionPlantCd;
				this.productionPlantName = add1.productionPlantName;
			}
			else{
				throw new LogonException();
			}
		}

		public String getProductionPlantCd() {
			return productionPlantCd;
		}

		public String getProductionPlantName() {
			return productionPlantName;
		}

		@Override
		/**
		 * compares the weight
		 */
		public int compareTo(MaterialStatisticEntry o) {
			if (this.getWeight() > o.getWeight()) {
				return 1;
			} else if (this.getWeight() < o.getWeight()) {
				return -1;
			}
			return 0;
		}
	}

	/**
	 * The statistics shows the statistic in TONS. In case the ordered UOM was LKW this is something, what is only a 
	 * supposed weight in tons, not a concrete value, but it must also be added to the statistics.
	 * @param materialAndWeight - this is a concrete value in TONS, calculated to TONS
	 * @param materialAndSupposedWeight - this comes only from an order where the weight can only be supposed
	 */
	public void setMaterialAndWeight(Map<String, MaterialStatisticEntry> materialAndWeight, Map<String, MaterialStatisticEntry> materialAndSupposedWeight) {
		List<MaterialStatisticEntry> all  = new ArrayList<OrderStatistic.MaterialStatisticEntry>();
		//the two must be merged
		if (materialAndWeight.entrySet() != null && materialAndSupposedWeight.entrySet() != null 
				&& !materialAndWeight.entrySet().isEmpty() && !materialAndSupposedWeight.entrySet().isEmpty()){
			Iterator it = materialAndWeight.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry type = (Map.Entry) it.next();
				String compositeId = (String) type.getKey();
				MaterialStatisticEntry entry = (MaterialStatisticEntry) materialAndSupposedWeight.get(compositeId);
				if (entry != null){					
					MaterialStatisticEntry sum = new MaterialStatisticEntry(entry, (MaterialStatisticEntry)type.getValue());
					mergedMap.put(compositeId, sum);
					materialAndSupposedWeight.remove(compositeId);
				}
				else{
					mergedMap.put(compositeId, (MaterialStatisticEntry) type.getValue());
				}
				it.remove();
			}
			// add those which have only supposed values
			mergedMap.putAll(materialAndSupposedWeight);
		}
		else if (materialAndWeight.entrySet() != null && !materialAndWeight.entrySet().isEmpty()){
			mergedMap.putAll(materialAndWeight);
		}
		else if (materialAndSupposedWeight.entrySet() != null && !materialAndSupposedWeight.entrySet().isEmpty()){
			mergedMap.putAll(materialAndSupposedWeight);
		}
		all.addAll(mergedMap.values());
		this.materialAndWeight = all;
	}

	public Collection getMaterialAndWeight() {
		return materialAndWeight;
	}

	public void orderMaterialAndWeightDesc() {
		Collections.sort(this.materialAndWeight);
		Collections.reverse(this.materialAndWeight);
	}

}
