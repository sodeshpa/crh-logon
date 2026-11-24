package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TruckModificationDTO implements Serializable {

	private static final long serialVersionUID = -5032247513649029770L;
	
	private List<IndexedObject> newTrucks;
	private transient List<Long> removedTruckIds;
	private transient Map<Long, IndexedObject> indexedStaleTruckIds;
	
	public TruckModificationDTO(List<Long> staleTruckIds) {
		this.newTrucks = new ArrayList<IndexedObject>();
		this.indexedStaleTruckIds = new HashMap<Long, IndexedObject>();
		int staleIndex = 0;
		if(staleTruckIds != null) {
			this.removedTruckIds = new ArrayList<Long>(staleTruckIds);
			for (Long staleTruckId : staleTruckIds) {
				this.indexedStaleTruckIds.put(staleTruckId, new IndexedObject(staleTruckId, staleIndex++));
			}					
		} else {
			this.removedTruckIds = new ArrayList<Long>();
		}
	}
	
	public List<IndexedObject> getRemovedTrucks() {
		List<IndexedObject> removedTrucks = new ArrayList<IndexedObject>();
		for (Long removedTruckId : removedTruckIds) {
			IndexedObject indexedStaleTruckId = indexedStaleTruckIds.get(removedTruckId);
			if(indexedStaleTruckId != null) {
				removedTrucks.add(indexedStaleTruckId);
			}
		}
		Collections.sort(removedTrucks);
		return removedTrucks;
	}
	
	public List<IndexedObject> getNewTrucks() {
		return newTrucks;
	}
	
	public void addChangedTruck(Long truckId, IndexedObject changedTruck) {
		if(!removedTruckIds.contains(truckId)) {
			removedTruckIds.add(truckId);
		}
		newTrucks.add(changedTruck);
	}
	
	public boolean removeStaleTruckId(Long truckId) {
		return removedTruckIds.remove(truckId);			
	}
	
	public void retainTrucks(List<Long> retainTruckIds) {
		if (indexedStaleTruckIds.isEmpty()) {
			List<IndexedObject> retainTrucks = new ArrayList<IndexedObject>();
			for (IndexedObject indexedObject : newTrucks) {
				TruckWrapper newTruck = (TruckWrapper) indexedObject.getObject();
				if (newTruck == null) {
					continue;
				}
				Long truckId = newTruck.getTruck().getTruckId();
				if (retainTruckIds.contains(truckId)) {
					retainTrucks.add(indexedObject);
				}
			}
			this.newTrucks = retainTrucks;
			return;
		}
		List<Long> removeableTruckIds = new ArrayList<Long>();
		Iterator<IndexedObject> iterator = newTrucks.iterator();
		while (iterator.hasNext()) {
			IndexedObject indexedObject = (IndexedObject) iterator.next();
			TruckWrapper truck = (TruckWrapper) indexedObject.getObject();
			if (truck == null) {
				continue;
			}
			Long truckId = truck.getTruck().getTruckId();
			if (!retainTruckIds.contains(truckId)) {
				if (indexedStaleTruckIds.get(truckId) == null) {
					iterator.remove();
				}
				else {
					removeableTruckIds.add(truckId);
				}
			}
		}

		for (Long removeableTruckId : removeableTruckIds) {
			removeNewTruck(removeableTruckId);
		}
	}

	private void removeNewTruck(Long truckId) {
		IndexedObject indexedObject = indexedStaleTruckIds.get(truckId);
		if(indexedObject != null) {
			removeFromIndexedObjectList(newTrucks, indexedObject);
			if(!removedTruckIds.contains(truckId)) {
				removedTruckIds.add(truckId);
			}
		}
	}
	
	private boolean removeFromIndexedObjectList(List<IndexedObject> indexedObjectList, IndexedObject indexedObject) {
		boolean removed = false;
		if(indexedObjectList != null) {
			removed = indexedObjectList.remove(indexedObject);
			if(removed) {
				reindexIndexedObjectListElements(indexedObjectList, indexedObject.getIndex());				
			}
		}
		return removed;
	}
	
	private void reindexIndexedObjectListElements(List<IndexedObject> indexedObjectList, int fromIndex) {
		if(indexedObjectList == null || fromIndex < 0 || fromIndex > indexedObjectList.size() - 1) {
			return;
		}
		for (int i = fromIndex; i < indexedObjectList.size(); i++) {
			IndexedObject indexedObject = indexedObjectList.get(fromIndex);
			if(indexedObject != null) {
				int index = indexedObject.getIndex();
				if(index > fromIndex) {
					int newIndex = index--;
					indexedObject.setIndex(newIndex);				
				}
			}
		}
	}
	
}
