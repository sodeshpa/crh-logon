/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoDayLoadsEntity;
import com.crh.logon.persistence.entities.views.DispoLoadtargetEntity;

/**
 * @author AdNovum Informatik AG
 */
public class ShippingpointLoadtargetManager {
	
	private Set<DispoLoadtargetPrioWrapper> tonTargets;
	private Set<DispoLoadtargetPrioWrapper> containerTargets;
	private Set<DispoLoadtargetPrioWrapper> wagonTargets;
	private String plantCd;
	
	public ShippingpointLoadtargetManager(List<DispoLoadtargetEntity> targets) {
	
		tonTargets = new TreeSet<DispoLoadtargetPrioWrapper>();
		containerTargets = new TreeSet<DispoLoadtargetPrioWrapper>();
		wagonTargets = new TreeSet<DispoLoadtargetPrioWrapper>();
		plantCd = null;
		
		for (DispoLoadtargetEntity dl : targets) {
			plantCd = dl.getPlantCd();
			if (dl.getLoadUomCd().equals(Constant.UOM_TONS)) {
				tonTargets.add(new DispoLoadtargetPrioWrapper(dl.getTargetLoadCount(), dl));
			}
			else if (dl.getLoadUomCd().equals(Constant.UOM_CONTAINER)) {
				containerTargets.add(new DispoLoadtargetPrioWrapper(dl.getTargetLoadCount(), dl));
			}
			else if (dl.getLoadUomCd().equals(Constant.UOM_WAGON)) {
				wagonTargets.add(new DispoLoadtargetPrioWrapper(dl.getTargetLoadCount(), dl));
			}
		}
	}
	
	
	public void removeLoaded(List<DispoDayLoadsEntity> loaded) {
		for (DispoDayLoadsEntity ddl : loaded) {
			Iterator<DispoLoadtargetPrioWrapper> tonTargetsIt = tonTargets.iterator();
			while (tonTargetsIt.hasNext()) {
				DispoLoadtargetPrioWrapper slpw = tonTargetsIt.next();
				DispoLoadtargetEntity slpwTarget = slpw.getTarget();
				if (slpwTarget.getSapMaterialId().equals(ddl.getSapMaterialId()) &&
						slpwTarget.getProductionPlantCd().equals(ddl.getProductionPlantCd())) {
					if (slpwTarget.getLoadQuantity().subtract(ddl.getNetQuantity()).abs().doubleValue() <= 1) {
						tonTargetsIt.remove();
						slpw.decreaseCount();
						if (slpw.getCount().intValue() > 0) {
							tonTargets.add(slpw);
						}
						return;
					}
				}
			}
			Set targets = wagonTargets;
			if (ddl.getContainerId() != null) {
				targets = containerTargets;
			}
			tonTargetsIt = targets.iterator();
			while (tonTargetsIt.hasNext()){
				DispoLoadtargetPrioWrapper slpw = (DispoLoadtargetPrioWrapper)tonTargetsIt.next();
				if (slpw.getTarget().getSapMaterialId().equals(ddl.getSapMaterialId()) &&
						slpw.getTarget().getProductionPlantCd().equals(ddl.getProductionPlantCd())) {
					tonTargetsIt.remove();
					slpw.decreaseCount();
					if (slpw.getCount().intValue() > 0) {
						targets.add(slpw);
					}
					return;
				}
			}
		}
	}
	
	public Collection<DispoLoadtargetPrioWrapper> getTargets() {
		TreeSet<DispoLoadtargetPrioWrapper> res = new TreeSet<DispoLoadtargetPrioWrapper>();
		res.addAll(tonTargets);
		res.addAll(containerTargets);
		res.addAll(wagonTargets);
		return res;
	}


	public String getPlantCd() {
		return plantCd;
	}
	
}
