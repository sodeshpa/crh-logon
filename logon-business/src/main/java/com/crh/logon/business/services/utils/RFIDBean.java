/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.RfidobjectSiloAttachEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@AdnTrace
public class RFIDBean implements Serializable {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RFIDBean.class);
	
	public static final String SESSION_KEY = "RFID_form_bean";
    
    public static final RfidobjectTypeBsEntity RFIDOBJECT_TYPE_PDA_FORKLIFT = new RfidobjectTypeBsEntity();
    public static final RfidobjectTypeBsEntity RFIDOBJECT_TYPE_LOADINGPOINT_SILO = new RfidobjectTypeBsEntity();
    public static final RfidobjectTypeBsEntity RFIDOBJECT_TYPE_PARALLEL_TERMINAL = new RfidobjectTypeBsEntity();
    public static final RfidobjectTypeBsEntity RFIDOBJECT_TYPE_PDA_USER = new RfidobjectTypeBsEntity();
    public static final RfidobjectTypeBsEntity RFIDOBJECT_TYPE_SHIPTO_SILO = new RfidobjectTypeBsEntity();
    public static final RfidobjectTypeBsEntity RFIDOBJECT_TYPE_TRAILER = new RfidobjectTypeBsEntity();
    public static final RfidobjectTypeBsEntity RFIDOBJECT_TYPE_CONTAINER = new RfidobjectTypeBsEntity();
    public static final RfidobjectTypeBsEntity RFIDOBJECT_TYPE_TRUCK = new RfidobjectTypeBsEntity();
    public static final RfidobjectTypeBsEntity RFIDOBJECT_TYPE_WAGON = new RfidobjectTypeBsEntity();
    
    static{
        RFIDOBJECT_TYPE_PDA_FORKLIFT.setRfidobjectTypeCd(Constant.RFIDOBJECT_TYPE_FORKLIFT);
        RFIDOBJECT_TYPE_LOADINGPOINT_SILO.setRfidobjectTypeCd(Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO);
        RFIDOBJECT_TYPE_PARALLEL_TERMINAL.setRfidobjectTypeCd(Constant.RFIDOBJECT_TYPE_PARALLEL_TERMINAL);
        RFIDOBJECT_TYPE_PDA_USER.setRfidobjectTypeCd(Constant.RFIDOBJECT_TYPE_PDA_USER);
        RFIDOBJECT_TYPE_SHIPTO_SILO.setRfidobjectTypeCd(Constant.RFIDOBJECT_TYPE_SHIPTO_SILO);
        RFIDOBJECT_TYPE_TRAILER.setRfidobjectTypeCd(Constant.RFIDOBJECT_TYPE_TRAILER);
        RFIDOBJECT_TYPE_TRUCK.setRfidobjectTypeCd(Constant.RFIDOBJECT_TYPE_TRUCK);
        RFIDOBJECT_TYPE_WAGON.setRfidobjectTypeCd(Constant.RFIDOBJECT_TYPE_WAGON);
        RFIDOBJECT_TYPE_CONTAINER.setRfidobjectTypeCd(Constant.RFIDOBJECT_TYPE_CONTAINER);
    }
    
	/**
	 * Finds and returns the rfidobject according to the context provided by the bizevent, and the attributeValue, which is in fact
	 * a string what is the main component of the rfid object label. Since the labels are unique *within a context* the labels are
	 * only unique containing this context too. 
	 * 
	 * If the rfidobject could not be found it will return null.
	 * 
	 * trucks - globally unique they have only the truck plateno as a base generation attribute
	 * trailers - globally unique they have only the trailer plateno as a base generation attribute
	 * shipto silos - unique in the context of shiptos, the generation attribute is the silo name, but has the context prefix sap_id
	 * loadingpoints - unique in context of shippingpoints thus besides the generation attribute loadinpoint name they have the context prefix plant_cd+sap_shippingpoint_no
	 * 
	 * @param bizEvent
	 * @param attributeValue
	 * @param rfidObjectTypeCd
	 * @param service
	 * @return
	 */
	public static RfidobjectEntity findRfidObject( BizeventEntity bizEvent, String attributeValue, Long rfidObjectTypeCd, RFIDServiceLocalInterface service) {
		// TODO (emoke Jun 3, 2009):its not clear in which cases will we use at all bizevent and when it is not needed currently truck trailer and such won't need that
		//maybe rfids with global context won't need the bizevent at all, then the constructors can be modified so. until then just send null for event
		if (rfidObjectTypeCd.equals(Long.valueOf(Constant.RFIDOBJECT_TYPE_TRUCK))) {
			TruckRFIDBean bean = (TruckRFIDBean) TruckRFIDBean(attributeValue);
			return bean.findSingleRFIDObject(service);
		}
		else if (rfidObjectTypeCd.equals(Long.valueOf(Constant.RFIDOBJECT_TYPE_TRAILER))) {
			TrailerRFIDBean bean = (TrailerRFIDBean) TrailerRFIDBean(attributeValue);
			return bean.findSingleRFIDObject(service);
		}
		else if (rfidObjectTypeCd.equals(Long.valueOf(Constant.RFIDOBJECT_TYPE_SHIPTO_SILO))) {
			if (bizEvent == null || bizEvent.getShipto() == null) {
				//order is the minimum context of looking up things
				return null;
			}
			AbstractRFIDBean bean = ShiptoSiloRFIDBean(bizEvent, attributeValue);
			return bean.findSingleRFIDObject(service);
		}
		else if (rfidObjectTypeCd.equals(Long.valueOf(Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO))) {
			if (bizEvent == null || bizEvent.getShippingpoint() == null) {
				//order is the minimum context of looking up things
				return null;
			}
			LoadingPointRFIDBean bean = (LoadingPointRFIDBean) LoadingpointRFIDBean(bizEvent,
					attributeValue);
			return bean.findSingleRFIDObject(service);
		}
		else if (rfidObjectTypeCd.equals(Long.valueOf(Constant.RFIDOBJECT_TYPE_PDA_USER))) {
			UserRFIDBean bean = (UserRFIDBean) UserRFIDBean(attributeValue);
			return bean.findSingleRFIDObject(service);
		}
		else if (rfidObjectTypeCd.equals(Long.valueOf(Constant.RFIDOBJECT_TYPE_WAGON))) {
			WagonRFIDBean bean = (WagonRFIDBean) WagonRFIDBean(attributeValue);
			return bean.findSingleRFIDObject(service);
		}
		else if (rfidObjectTypeCd.equals(Long.valueOf(Constant.RFIDOBJECT_TYPE_CONTAINER))) {
			ContainerRFIDBean bean = (ContainerRFIDBean) ContainerRFIDBean(attributeValue);
			return bean.findSingleRFIDObject(service);
		}
		return null;
	}
	


	public static AbstractRFIDBean ShiptoSiloRFIDBean(ShiptoSiloEntity silo) {
		if ( ! silo.getPortableSiloFlag()) {
			return new RFIDBean().new CustomerSiloRFIDBean(silo);
		}
		else if (silo.getPortableSiloFlag()){
			return new RFIDBean().new PortableSiloRFIDBean(silo);
		}
		else{
			throw new LogonException();
		}
	}
	
	private static AbstractRFIDBean ShiptoSiloRFIDBean(BizeventEntity bizEvent, String attributeValue) {		
		return new RFIDBean().new CustomerSiloRFIDBean(bizEvent, attributeValue);
	}
	
	public static AbstractRFIDBean TruckRFIDBean(TruckEntity truck) {
		return new RFIDBean().new TruckRFIDBean(truck);
	}
	
	private static AbstractRFIDBean TruckRFIDBean(String attributeValue) {		
		return new RFIDBean().new TruckRFIDBean(attributeValue);
	}
	
	public static AbstractRFIDBean WagonRFIDBean(WagonEntity wagon) {
		return new RFIDBean().new WagonRFIDBean(wagon);
	}
	
	private static AbstractRFIDBean WagonRFIDBean(String attributeValue) {		
		return new RFIDBean().new WagonRFIDBean(attributeValue);
	}
	
	public static AbstractRFIDBean TrailerRFIDBean(TrailerEntity trailer) {
		return new RFIDBean().new TrailerRFIDBean(trailer);
	}
	
	private static AbstractRFIDBean TrailerRFIDBean(String attributeValue) {
		return new RFIDBean().new TrailerRFIDBean(attributeValue);		
	}
	
	public static AbstractRFIDBean ContainerRFIDBean(ContainerEntity container) {
		return new RFIDBean().new ContainerRFIDBean(container);
	}
	
	private static AbstractRFIDBean ContainerRFIDBean(String attributeValue) {
		return new RFIDBean().new ContainerRFIDBean(attributeValue);		
	}
	
	public static AbstractRFIDBean LoadingpointRFIDBean(LoadingpointEntity loadingPoint) {
		return new RFIDBean().new LoadingPointRFIDBean(loadingPoint);
	}
	
	private static AbstractRFIDBean LoadingpointRFIDBean(BizeventEntity bizEvent, String attributeValue) {
		return new RFIDBean().new LoadingPointRFIDBean(bizEvent, attributeValue);		
	}

	public static AbstractRFIDBean UserRFIDBean(UserEntity user) {
		return new RFIDBean().new UserRFIDBean(user);
	}

	private static AbstractRFIDBean UserRFIDBean(String attributeValue) {
		return new RFIDBean().new UserRFIDBean(attributeValue);
	}

	private static HashMap<String, String> textConverts = new HashMap<String, String>();
	static {
		textConverts.put("\\s", "");
		textConverts.put("[^\\p{L}\\p{Nd}_]+", "");
		textConverts.put("[ÄÀ]", "A");
		textConverts.put("Ö", "O");
		textConverts.put("Ü", "U");
		textConverts.put("[ÉÊÈ]", "E");
		textConverts.put("Ç", "C");
		textConverts.put("Ï", "I");
	}

	public static String dfltConvertToASCII(String str) {
		String result = str.toUpperCase();
		Set<String> convertKeys = textConverts.keySet();
		for (String key : convertKeys) {
			String replacement = textConverts.get(key);
			result = result.replaceAll(key, replacement);
		}
		return result;
	}

	 /**
	 * Abstract classes for different type rfid objects. 
	 * There are rfidobjects which are in a fix context, the label for these will
	 * not ever change, but will, or will not contain the context id as a prefix in their
	 * object label. See type hierarcy. - FixContextTypeBean
	 * 
	 * There are rfidobjects, which does not have a context, the label for these WONT ever change,
	 * but WONT EVER have a context id as a prefix in their object label. See type hierarchy. - GlobalContextTypeBean
	 * 
	 * There are rfidobjects, for which the context may change and may not exist at all. These
	 * objects if currently have a context, will have the context id as a prefix in their object label,
	 * and if they are detached from that context object, they will loose this prefix. - ChangingContextTypeBean
	 * 
	 */
	public abstract class AbstractRFIDBean implements Serializable {
		
		RfidobjectEntity rfidObject;
		Object contextObject;
	
		protected abstract String getGenerationAttributeValue();
			
		public RfidobjectEntity getRfidObject() {
			return this.rfidObject;
		}
		
		public abstract Object getContextObject();

		protected final void setContextObject(Object object) {
			contextObject = object;
		}

		public final void setRfidObject(RfidobjectEntity object) {
			rfidObject = object;
		}

		/**
		 * This method will generate, validate and set the label of the RFID object of the bean.
		 * If it is null, nothing happens. 
		 * 
		 * @throws LogonException if an RFID object with the same type code AND label in already in the DB
		 */
		public void generateAndValidateAndSetRFIDObjectLabel(RFIDServiceLocalInterface service) {
			generateRFIDObjectLabel(service, true);
		}

		public void generateRFIDObjectLabel(RFIDServiceLocalInterface service, boolean validate) {
			if (rfidObject == null) {
				throw new LogonException();
			}
			String objectLabel = getRFIDSearchString();
			// TODO (emoke Jun 3, 2009):perhaps should check if it is a new rfidobject and does not have tags in this case there would be no need to check the labels

			if (validate) {
				findCollidingRFIDs(service);
			}

			rfidObject.setObjectLabel(objectLabel);
		}

		/**
		 * 
		 * @return an rfid object from the DB with the same label AND type code as 'this' (null, if not found)
		 */
		protected final RfidobjectEntity findSingleRFIDObject(RFIDServiceLocalInterface service) {
			String rfidSearchString = getRFIDSearchString();
			RfidobjectTypeBsEntity rfidobjectType = getRfidObject().getRfidobjectType();
			Long rfidobjectTypeCode = rfidobjectType.getRfidobjectTypeCd();
			RfidobjectEntity rfid = service.findSingleRFID(rfidSearchString,
					rfidobjectTypeCode);
			return rfid;
		}

		protected String convertToASCII(String str) {
			return RFIDBean.dfltConvertToASCII(str);
		}
		
		protected abstract void findCollidingRFIDs(RFIDServiceLocalInterface service);
		
		/**
		 * Returns the generated rfid object label for a given string.
		 * 
		 * This generated object label will be checked when saving to the db - in case the function was called from saving to db -
		 *  if it is unique in the given object context:
		 * for type truck the same searchstring can only be related to one truck. (But the same searchstring can be given to
		 * another type rfidobject).
		 * 
		 * Generation details:
		 * Replaces the whitespace and non word characters in the string. (Cannot contain whitespace and non ascii characters)
		 * @param generationAttributeValue - the returned search string will be generated from this string
		 * @param typeCd TODO
		 * @return
		 */
		public abstract String getRFIDSearchString();
	}

	public class TruckRFIDBean extends GlobalContextTypeBean {
		
		protected TruckRFIDBean(TruckEntity truck) {
			this.rfidObject = truck.getRfidobject();
			this.setContextObject(truck);
		}
		
		protected TruckRFIDBean(String attributeValue) {
			
			//pretend to be a truck with plateNr attribute attributevalue, and get the searchstring for that
			TruckEntity truck = new TruckEntity();
			truck.setTruckPlateNo(attributeValue);
			
			RfidobjectEntity rfidObject = new RfidobjectEntity();
			rfidObject.setRfidobjectType(RFIDOBJECT_TYPE_TRUCK);
			truck.setRfidobject(rfidObject);
			
			this.rfidObject = rfidObject;
			this.setContextObject(truck);
		}
		
		/**
		 * @return truck plate number
		 */
		@Override
		protected String getGenerationAttributeValue() {
			TruckEntity entity = (TruckEntity) contextObject;
			String generationAttribute = entity.getTruckPlateNo(); 
			return generationAttribute;
		}

		@Override
		public TruckEntity getContextObject() {
			return (TruckEntity) this.contextObject;
		}

	}
	
	public class TrailerRFIDBean extends GlobalContextTypeBean {
		
		protected TrailerRFIDBean(TrailerEntity trailer) {
			this.rfidObject = trailer.getRfidobject();
			this.setContextObject(trailer);
			
		}
		
		protected TrailerRFIDBean(String attributeValue) {
			//pretend to be a trailer with plateNr attribute attributevalue, and get the searchstring for that
			TrailerEntity trailer = new TrailerEntity();
			trailer.setTrailerPlateNo(attributeValue);
			
			RfidobjectEntity rfidObject = new RfidobjectEntity();
			rfidObject.setRfidobjectType(RFIDOBJECT_TYPE_TRAILER);
			trailer.setRfidobject(rfidObject);
			
			this.rfidObject = rfidObject;
			this.setContextObject(trailer);
		}

		@Override
		protected String getGenerationAttributeValue() {
			TrailerEntity entity = (TrailerEntity) contextObject;
			String generationAttribute = entity.getTrailerPlateNo(); 
			return generationAttribute;
		}

		@Override
		public TrailerEntity getContextObject() {
			return (TrailerEntity) this.contextObject;
		}

	}
	
	public class ContainerRFIDBean extends GlobalContextTypeBean {
		
		protected ContainerRFIDBean(ContainerEntity container) {
			
			this.rfidObject = container.getRfidobject();
			this.setContextObject(container);
			
		}
		
		protected ContainerRFIDBean(String attributeValue) {
			//pretend to be a trailer with plateNr attribute attributevalue, and get the searchstring for that
			ContainerEntity container = new ContainerEntity();
			container.setContainerNo(attributeValue);
			
			RfidobjectEntity rfidObject = new RfidobjectEntity();
			rfidObject.setRfidobjectType(RFIDOBJECT_TYPE_CONTAINER);
			container.setRfidobject(rfidObject);
			
			this.rfidObject = rfidObject;
			this.setContextObject(container);
		}

		@Override
		protected String getGenerationAttributeValue() {
			ContainerEntity entity = (ContainerEntity) contextObject;
			String generationAttribute = entity.getContainerNo(); 
			return generationAttribute;
		}

		@Override
		public ContainerEntity getContextObject() {
			return (ContainerEntity) this.contextObject;
		}

	}
	
	public class CustomerSiloRFIDBean extends FixContextTypeBean {
		protected CustomerSiloRFIDBean(ShiptoSiloEntity silo) {
			
			/**
			 * In case of silos there can be customer silos with the same name, thus the name
			 * will contain the id of the shipto as well, to make the generated search string unique.
			 * In case a baustellen silo is going to be attached to a shipto, it will also have 
			 * the shipto id in it's searchstring, if it is going to be detached it will loose the 
			 * shipto_sap_id prefix.
			 */
			if (silo.getShipto() == null) {
				LOG.error("Customer silo cannot exist unattached from a shipto");
				throw new LogonException();
			}
						
			this.rfidObject = silo.getRfidobject();
			this.setContextObject(silo);
		
		}
		@Override
		protected String getGenerationAttributeValue() {
			ShiptoSiloEntity silo = (ShiptoSiloEntity) contextObject;			
			String generationAttribute = silo.getSiloName();
			return generationAttribute;
		}

		@Override
		protected String getGenerationPrefixValue() {
			ShiptoSiloEntity silo = (ShiptoSiloEntity) contextObject;
			SapBpEntity shiptoSapBp = silo.getShipto().getShiptoSapBp();
			if (shiptoSapBp == null) {
				LOG.error("It must be assured that the shipto is persisted first before attaching it to a shipto");
				throw new LogonException(Reason.RES_STRUTS_VALIDATION);	
			}
			String id = shiptoSapBp.getSapId();
			return id;
		}
		
		@Override
		public ShiptoSiloEntity getContextObject() {
			return (ShiptoSiloEntity) this.contextObject;
		}

		protected CustomerSiloRFIDBean(BizeventEntity bizEvent, String attributeValue) {
			//pretend to be a silo with name attribute attributevalue, and have the context object and get the searchstring for that			
			ShiptoEntity shipto = bizEvent.getShipto();
			
			//in case a bizevent takes place customer and portable silo is in a fixed context, thus search for only one
			ShiptoSiloEntity silo = new ShiptoSiloEntity();
			silo.setPortableSiloFlag(false);
			silo.setSiloName(attributeValue);
			
			RfidobjectEntity rfidObject = new RfidobjectEntity();
			rfidObject.setRfidobjectType(RFIDOBJECT_TYPE_SHIPTO_SILO);
			silo.setRfidobject(rfidObject);
			
			ShiptoEntity shipTo = new ShiptoEntity();
			SapBpEntity shiptoSapBp = shipto.getShiptoSapBp();
			shipTo.setShiptoSapBp(shiptoSapBp);
			
			silo.setShipto(shipTo);
			
			this.rfidObject = rfidObject;
			this.setContextObject(silo);
		}
		
	}
	
	public class PortableSiloRFIDBean extends ChangingContextTypeBean{
		
		protected PortableSiloRFIDBean(ShiptoSiloEntity silo) {
			
			/**
			 * In case of silos there can be customer silos with the same name, thus the name
			 * will contain the id of the shipto as well, to make the generated search string unique.
			 * In case a baustellen silo is going to be attached to a shipto, it will also have 
			 * the shipto id in it's searchstring, if it is going to be detached it will loose the 
			 * shipto's shipto_sapbp_id prefix.
			 */
						
			this.rfidObject = silo.getRfidobject();
			this.setContextObject(silo);
			
		}
		
		@Override
		protected String getGenerationAttributeValue() {
			ShiptoSiloEntity silo = (ShiptoSiloEntity) contextObject;
			String generationAttribute = silo.getSiloName();
			return generationAttribute;
		}

		@Override
		protected String getGenerationPrefixValue() {
			ShiptoSiloEntity silo = (ShiptoSiloEntity) contextObject;
			String generationPrefixAttribute = null;
			if (silo.getShipto() != null) {
				SapBpEntity shiptoSapBp = silo.getShipto().getShiptoSapBp();
				if (shiptoSapBp == null) {
					LOG.error("It must be assured that the shipto is persisted first before attaching it to a shipto");
					throw new LogonException(Reason.RES_STRUTS_VALIDATION);	
				}
				String id = shiptoSapBp.getSapId();
				generationPrefixAttribute = id;
			}
			return generationPrefixAttribute;
		}
		
		@Override
		public ShiptoSiloEntity getContextObject() {
			return (ShiptoSiloEntity) this.contextObject;
		}

		@Override
		protected void findCollidingRFIDs(RFIDServiceLocalInterface service) {
			List list = service.queryRFIDSiloView(getRFIDSearchString(), getLabelWithoutPrefix(),
					rfidObject.getRfidobjectType().getRfidobjectTypeCd());

			if (list == null || list.isEmpty()) {
				//if there is no such, not even this one, OK
				return;
			}

			//if the object already exist naturally it can remain there
			RfidobjectSiloAttachEntity rfid = (RfidobjectSiloAttachEntity) list.get(0);
			if (rfid.getRfidobjectId().equals(rfidObject.getRfidobjectId())) {
				return;
			}
			throw new LogonException(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE);
		}
	
	} 
	
	public class LoadingPointRFIDBean extends FixContextTypeBean {
		
		/**
		 * In case of loadingpoint there can be loadingpoints with the same name, but the searchstring has to be 
		 * unique though. Thus adding the shippingpoint_sap_no and plant_cd combination as prefix for the searchstring.
		 */
		protected LoadingPointRFIDBean(LoadingpointEntity loadingPoint) {
			this.rfidObject = loadingPoint.getRfidobject();
			this.setContextObject(loadingPoint);
		}
		
		public LoadingPointRFIDBean(BizeventEntity bizEvent, String attributeValue) {
			ShippingpointEntity shippingpoint = bizEvent.getShippingpoint();
			
			LoadingpointEntity loadingpoint = new LoadingpointEntity();
			loadingpoint.setLoadingpointObjectLabel(attributeValue);
			
			RfidobjectEntity rfidObject = new RfidobjectEntity();
			rfidObject.setRfidobjectType(RFIDOBJECT_TYPE_LOADINGPOINT_SILO);
			loadingpoint.setRfidobject(rfidObject);
			loadingpoint.setShippingpoint(shippingpoint);
			
			this.rfidObject = rfidObject;
			this.setContextObject(loadingpoint);
			
		}

		@Override
		protected String getGenerationAttributeValue() {
			LoadingpointEntity loadingPoint = (LoadingpointEntity) contextObject;
			String generationAttribute = loadingPoint.getLoadingpointObjectLabel();
			return generationAttribute;
		}

		@Override
		protected String getGenerationPrefixValue() {
			String result = null;
			ShippingpointEntity shippingPoint = ((LoadingpointEntity) contextObject).getShippingpoint();
			
			String plantCd = shippingPoint.getPlant().getPlantCd();
			String id = shippingPoint.getSapShippingpointNo();

			// concatenate plantCd and shippingpointNo
			result = plantCd + '_' + id;
			
			return result;
		}
		
		@Override
		public LoadingpointEntity getContextObject() {
			return (LoadingpointEntity) this.contextObject;
		}
	}

	public class UserRFIDBean extends GlobalContextTypeBean {

		/**
		 * In case of loadingpoint there can be loadingpoints with the same name, but the searchstring has to be 
		 * unique though. Thus adding the shippingpoint_sap_no and plant_cd combination as prefix for the searchstring.
		 */
		protected UserRFIDBean(UserEntity user) {
			this.rfidObject = user.getPdaUserRfidobject();
			this.setContextObject(user);
		}

		public UserRFIDBean(String attributeValue) {
			UserEntity user = new UserEntity();
			user.setPdaUserNo(Long.valueOf(attributeValue));
			RfidobjectEntity rfidObject = new RfidobjectEntity();
			rfidObject.setRfidobjectType(RFIDOBJECT_TYPE_PDA_USER);
			user.setPdaUserRfidobject(rfidObject);
			this.rfidObject = rfidObject;
			this.setContextObject(user);
		}

		@Override
		protected String getGenerationAttributeValue() {
			UserEntity user = (UserEntity) contextObject;
			String generationAttribute = user.getPdaUserNo().toString();
			return generationAttribute;
		}

		@Override
		public UserEntity getContextObject() {
			return (UserEntity) contextObject;
		}
	}
	
	public class WagonRFIDBean extends GlobalContextTypeBean {
		
		protected WagonRFIDBean(WagonEntity wagon) {
			this.rfidObject = wagon.getRfidobject();
			this.setContextObject(wagon);
		}
		
		protected WagonRFIDBean(String attributeValue) {
			WagonEntity wagon = new WagonEntity();
			wagon.setWagonNo(attributeValue);
			
			RfidobjectEntity rfidObject = new RfidobjectEntity();
			rfidObject.setRfidobjectType(RFIDOBJECT_TYPE_WAGON);
			wagon.setRfidobject(rfidObject);
			
			this.rfidObject = rfidObject;
			this.setContextObject(wagon);
		}
		
		@Override
		protected String getGenerationAttributeValue() {
			WagonEntity entity = (WagonEntity) contextObject;
			String generationAttribute = entity.getWagonNo(); 
			return generationAttribute;
		}

		@Override
		public WagonEntity getContextObject() {
			return (WagonEntity) this.contextObject;
		}
	}

	/**
	 * Abstract classes for different type rfid objects. 
	 * There are rfidobjects which are in a fix context, the label for these will
	 * not ever change, but will, or will not contain the context id as a prefix in their
	 * object label. See type hierarcy. - FixContextTypeBean
	 * 
	 * There are rfidobjects, which does not have a context, the label for these WONT ever change,
	 * but WONT EVER have a context id as a prefix in their object label. See type hierarchy. - GlobalContextTypeBean
	 * 
	 * There are rfidobjects, for which the context may change and may not exist at all. These
	 * objects if currently have a context, will have the context id as a prefix in their object label,
	 * and if they are detached from that context object, they will loose this prefix. - ChangingContextTypeBean
	 * 
	 * @author AdNovum Informatik AG
	 *
	 * To change this generated comment go to 
	 * Window>Preferences>Java>Code Generation>Code Template
	 */
	abstract class FixContextTypeBean extends AbstractRFIDBean {
		
		protected abstract String getGenerationPrefixValue();

		
		public String getRFIDSearchString() {
			String generationAttributeValue = getGenerationAttributeValue();
			String generationPrefixValue = getGenerationPrefixValue();
			if (StringUtils.empty(generationAttributeValue)) {
				throw new LogonException(Reason.RES_STRUTS_VALIDATION);
			}
			if (generationPrefixValue == null) {
				throw new LogonException();
			}
			String result = convertToASCII(generationAttributeValue);
			
			result = generationPrefixValue + "_" + result;

			String upper = result.toUpperCase();
			return result = upper;
		}
		
		@Override
		protected void findCollidingRFIDs(RFIDServiceLocalInterface service) {
			RfidobjectEntity rfid = findSingleRFIDObject(service);

			if (rfid == null || rfid.getRfidobjectId().equals(rfidObject.getRfidobjectId())) {
				//if the object already exist naturally it can remain there
				return;
			}
			throw new LogonException(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE);
		}
	}
		
	/**
	 * There are rfidobjects, which does not have a context, the label for these WONT ever change,
	 * but WONT EVER have a context id as a prefix in their object label. See type hierarchy.
	 */
	abstract class GlobalContextTypeBean extends AbstractRFIDBean {

		public String getRFIDSearchString() {
			String generationAttributeValue = getGenerationAttributeValue();
			if (StringUtils.empty(generationAttributeValue)) {
				throw new LogonException(Reason.RES_STRUTS_VALIDATION);
			}
			return convertToASCII(generationAttributeValue);
		}
		
		/**
		 * @throws LogonException if there is already another RFID object in the DB:
		 *  - with the same label AND type code
		 *  - but with different ID
		 */
		@Override
		protected void findCollidingRFIDs(RFIDServiceLocalInterface service) {
			RfidobjectEntity existingRFID = findSingleRFIDObject(service);

			Long currentId = rfidObject.getRfidobjectId();
			if (existingRFID == null || existingRFID.getRfidobjectId().equals(currentId)) {
				//if the object already exist naturally it can remain there
				return;
			}
			
			Long existingId = existingRFID.getRfidobjectId();
			RfidobjectTypeBsEntity rfidObjectType = existingRFID.getRfidobjectType();
			Long rfidobjectTypeCode = rfidObjectType.getRfidobjectTypeCd();
			String existingLabel = existingRFID.getObjectLabel();
			String currentLabel = getRFIDSearchString();
			String message = "Colliding RFIDs found"
					+ ", existing id: " + existingId
					+ ", current id: " + currentId
					+ ", type code: " + rfidobjectTypeCode
					+ ", (labels: " + existingLabel + " and " + currentLabel + ")";
			throw new LogonException(message, Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE);
		}
	}
	
	/**
	 * This rfidTypeBean has changing context. This means the object related to the
	 * rfidObject may or may not be assigned to a given context.
	 * (Such as a baustellen silo can be assigned to a certain shipto, but it may be
	 * without an assignment).
	 * If the object to which the rfidObject is attached is attached to a context object
	 * the label will have the id_ prefix, otherwise won't.
	 */
	abstract class ChangingContextTypeBean extends AbstractRFIDBean{

		public final String getRFIDSearchString() {
			String result = getLabelWithoutPrefix();
			String generationPrefixValue = getGenerationPrefixValue();

			if (generationPrefixValue != null) {
				result = generationPrefixValue + "_" + result;
			}

			String upper = result.toUpperCase();
			return result = upper;
		}

		/**
		 * This is only the attribute without prefix, but already the generated one.
		 * @return
		 */
		protected String getLabelWithoutPrefix() {
			String result;
			String generationAttributeValue = getGenerationAttributeValue();
			if (StringUtils.empty(generationAttributeValue)) {
				throw new LogonException(Reason.RES_STRUTS_VALIDATION);
			}

			result = convertToASCII(generationAttributeValue);

			return result;
		}
		
		protected abstract String getGenerationPrefixValue();
		/**
		 * This may be different for all changing context rfidobject.
		 * @return
		 */
	}
	
	public static final String getErrorMessage(Long typeCd){
		if (typeCd == Constant.RFIDOBJECT_TYPE_TRUCK) {
			return "res.rfidobject.label.not.unique.truck";
		}
		else if (typeCd == Constant.RFIDOBJECT_TYPE_TRAILER) {
			return "res.rfidobject.label.not.unique.trailer";
		}
		else if (typeCd == Constant.RFIDOBJECT_TYPE_SHIPTO_SILO) {
			return "res.rfidobject.label.not.unique.shiptosilo";
		}
		else if (typeCd == Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO) {
			return "res.rfidobject.label.not.unique.loadingpoint";
		}
		else if (typeCd == Constant.RFIDOBJECT_TYPE_PARALLEL_TERMINAL) {
			return "res.rfidobject.label.not.unique.loadingpoint";
		}
		else if (typeCd == Constant.RFIDOBJECT_TYPE_WAGON) {
			return "res.rfidobject.label.not.unique.wagon";
		}
		else if (typeCd == Constant.RFIDOBJECT_TYPE_CONTAINER) {
			return "res.rfidobject.label.not.unique.container";
		}
		else if (typeCd == Constant.RFIDOBJECT_TYPE_PDA_USER) {
			return "res.rfidobject.label.not.unique.userpda";
		}

		return null;
	}
}
