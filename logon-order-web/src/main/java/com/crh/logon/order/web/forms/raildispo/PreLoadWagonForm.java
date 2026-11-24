/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.raildispo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crh.logon.order.web.dto.WagonPreLoadDTO;
import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;

/**
 * Struts form for the Wagon Pre-loading popup on Rail Scheduling.
 * 
 * @author AdNovum Informatik AG
 * 
 * @struts.form name="order_raildispo_PreLoadWagonForm"
 */
public class PreLoadWagonForm extends ActionForm {

	private static final long serialVersionUID = 1L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PreLoadWagonForm.class);
	
	/** The wagon which the user will pre-load. */
	private WagonEntity wagon;
	/** The plant of the wagon. */
	private PlantBsEntity plant;
	/** The loading point of the wagon. */
	private LoadingpointEntity loadingpoint;
	/** This holds the user entered input for one preload. */
	private List<WagonPreLoadDTO> preLoads;
	private Boolean isBag;
	private Boolean isBulk;
	
	// Objects to choose from
	
	/** The list of materials to choose from. */
	private List<SapMaterialDescEntity> materials;
	
	/** Needed to determine the packed types (BAG/BULK), only needed server side. */
	private transient Map<Long, SapMaterialEntity> sapMaterialMap;
	/** The map which hold he list of plants for the materials (sapMatId -> List<SapMaterialPlantDescEntity>). */
	private Map<Long, List<SapMaterialPlantDescEntity>> sapMatPlantDescMap;
	private UomTxEntity tonUomTx;
	
	public PreLoadWagonForm() {
		init();
	}
	
	public void init() {
		wagon = null;
		plant = null;
		loadingpoint = null;
		isBag = null;
		isBulk = null;
		
		preLoads = new ArrayList<WagonPreLoadDTO>();
		
		materials = new ArrayList<SapMaterialDescEntity>();
		sapMaterialMap = new HashMap<Long, SapMaterialEntity>();
		sapMatPlantDescMap = new HashMap<Long, List<SapMaterialPlantDescEntity>>();
		tonUomTx = null;
	}

	public WagonEntity getWagon() {
		return wagon;
	}

	public void setWagon(WagonEntity wagon) {
		this.wagon = wagon;
	}

	public PlantBsEntity getPlant() {
		return plant;
	}

	public void setPlant(PlantBsEntity plant) {
		this.plant = plant;
	}

	public LoadingpointEntity getLoadingpoint() {
		return loadingpoint;
	}

	public void setLoadingpoint(LoadingpointEntity loadingpoint) {
		this.loadingpoint = loadingpoint;
	}

	public List<WagonPreLoadDTO> getPreLoads() {
		return preLoads;
	}

	public List<SapMaterialDescEntity> getMaterials() {
		return materials;
	}

	public void setMaterials(List<SapMaterialDescEntity> materials) {
		this.materials = materials;
	}

	public Boolean getIsBag() {
		return isBag;
	}

	public void setIsBag(Boolean isBag) {
		this.isBag = isBag;
	}

	public Boolean getIsBulk() {
		return isBulk;
	}

	public void setIsBulk(Boolean isBulk) {
		this.isBulk = isBulk;
	}

	public UomTxEntity getTonUomTx() {
		return tonUomTx;
	}

	public void setTonUomTx(UomTxEntity tonUomTx) {
		this.tonUomTx = tonUomTx;
	}
	
	private static BigDecimal getBigDecimalFromString(String parameter) {
		BigDecimal bigDecimal;
		
		if (parameter != null) {
			try {
				bigDecimal = new BigDecimal(parameter);
			} catch (NumberFormatException ex) {
				LOG.warn("Failed to parse parameter: " + parameter);
				bigDecimal = null; 
			}
		}
		else {
			bigDecimal = null;
		}
		
		return bigDecimal;
	} 

	/**
	 * Indexed property getter for WagonPreLoadBean.getSapMaterialId().
	 */
	public Long getSapMaterialIdIndexed(int index) {
		Long result = null;

		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				result = preLoad.getSapMaterialId();
				break;
			}
			idx++;
		}
		return result;
	}
	
	/**
	 * Indexed property setter for WagonPreLoadBean.setSapMaterialId().
	 */
	public void setSapMaterialIdIndexed(int index, Long sapMaterialId) {
		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				preLoad.setSapMaterialId(sapMaterialId);
				break;
			}
			idx++;
		}
	}
	
	/**
	 * Indexed property getter for WagonPreLoadBean.getProdPlantCd().
	 */
	public String getProdPlantCdIndexed(int index) {
		String result = null;

		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				result = preLoad.getProdPlantCd();
				break;
			}
			idx++;
		}
		return result;
	}
	
	/**
	 * Indexed property setter for WagonPreLoadBean.setProdPlantCd().
	 */
	public void setProdPlantCdIndexed(int index, String prodPlantCd) {
		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				preLoad.setProdPlantCd(prodPlantCd);
				break;
			}
			idx++;
		}
	}
	
	/**
	 * Indexed property getter for WagonPreLoadBean.getTareWeightT().
	 */
	public String getTareWeightTIndexed(int index) {
		String result = null;

		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				final BigDecimal tareWeightT = preLoad.getTareWeightT();
				if (tareWeightT != null) {
					result = tareWeightT.toPlainString();
				}
				break;
			}
			idx++;
		}
		return result;
	}
	
	/**
	 * Indexed property setter for WagonPreLoadBean.setTareWeightT().
	 */
	public void setTareWeightTIndexed(int index, String tareWeightTString) {
		final BigDecimal tareWeightT = getBigDecimalFromString(tareWeightTString);
		
		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				preLoad.setTareWeightT(tareWeightT);
				break;
			}
			idx++;
		}
	}
	
	/**
	 * Indexed property getter for WagonPreLoadBean.getGrossWeightT().
	 */
	public String getGrossWeightTIndexed(int index) {
		String result = null;

		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				final BigDecimal grossWeightT = preLoad.getGrossWeightT();
				if (grossWeightT != null) {
					result = grossWeightT.toPlainString();
				}
				break;
			}
			idx++;
		}
		return result;
	}
	
	/**
	 * Indexed property setter for WagonPreLoadBean.setGrossWeightT().
	 */
	public void setGrossWeightTIndexed(int index, String grossWeightTString) {
		
		final BigDecimal grossWeightT = getBigDecimalFromString(grossWeightTString);
		
		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				preLoad.setGrossWeightT(grossWeightT);
				break;
			}
			idx++;
		}
	}
	
	public Map<Long, List<SapMaterialPlantDescEntity>> getSapMatPlantDescMap() {
		return sapMatPlantDescMap;
	}

	public void setSapMatPlantDescMap(Map<Long, List<SapMaterialPlantDescEntity>> sapMatPlantDescMap) {
		this.sapMatPlantDescMap = sapMatPlantDescMap;
	}

	/**
	 * Indexed property getter for WagonPreLoadBean.getNetQuantity().
	 */
	public String getNetQuantityIndexed(int index) {
		String result = null;

		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				final BigDecimal netQuantity = preLoad.getNetQuantity();
				if (netQuantity != null) {
					result = netQuantity.toPlainString();
				}
				break;
			}
			idx++;
		}
		return result;
	}
	
	/**
	 * Indexed property setter for WagonPreLoadBean.setNetQuantity().
	 */
	public void setNetQuantityIndexed(int index, String netQuantityString) {
		
		final BigDecimal netQuantity = getBigDecimalFromString(netQuantityString);
		
		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				preLoad.setNetQuantity(netQuantity);
				break;
			}
			idx++;
		}
	}
	
	/**
	 * Indexed property getter for WagonPreLoadBean.getNetQuantityUomCd().
	 */
	public String getNetQuantityUomCdIndexed(int index) {
		String result = null;

		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				result = preLoad.getNetQuantityUomCd();
				break;
			}
			idx++;
		}
		return result;
	}
	
	/**
	 * Indexed property setter for WagonPreLoadBean.setNetQuantityUomCd().
	 */
	public void setNetQuantityUomCdIndexed(int index, String netQuantityUomCd) {
		int idx = 0;
		for (WagonPreLoadDTO preLoad : preLoads) {
			if (idx == index) {
				preLoad.setNetQuantityUomCd(netQuantityUomCd);
				break;
			}
			idx++;
		}
	}

	public Map<Long, SapMaterialEntity> getSapMaterialMap() {
		return sapMaterialMap;
	}

	public void setSapMaterialMap(Map<Long, SapMaterialEntity> sapMaterialMap) {
		this.sapMaterialMap = sapMaterialMap;
	}
	
}
