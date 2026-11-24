/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.utils.time.DateTimeParser;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;

/**
 * LKW popup called from Dispatching screen by clicking on the truck's name.
 * Possible to modify several data of the truck.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_dispo_LKWPopupForm"
 */
public class LKWPopupForm extends ActionForm {

    private static final long serialVersionUID = -8910104051337644556L;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(LKWPopupForm.class);
    
    private TruckEntity truck;
    private TrailerEntity trailer;
    private TruckTypeTxEntity type;
    private Long truckPriority;
	private ContainerEntity container;
    private TruckTransportCatTxEntity trspCat;
    private TrailerTrspCatTxEntity trailerTrspCat;
    private List truckUnavailabilities;
    private String inputAction;
    private String compositionTare;
    private String compositionGross;
    private String compositionNrChambers;
    
    private String inputEvent;
    private String inputDateFrom;
    private String inputTimeFrom;
    private String inputDateUntil;
    private String inputTimeUntil;
    
    private long inputTruckUnavailabilityId;
    private long[] truckPriorities;
    
	/**
     * Default constructor.
     */
    public LKWPopupForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "LKWPopupForm");
		}
		// ]]
		try {
	        init();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    /**
     * Initializes the form. This has the effect of clearing all the input
     * fields on the order.web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			type = null;
			truck = null;
			trailer = null;
			container = null;
			trailerTrspCat = null;
			trspCat = null;
			compositionTare = null;
			compositionGross = null;
			truckUnavailabilities = new ArrayList();
			inputEvent = "";
			inputDateFrom = "";
			inputDateUntil = "";
			inputTimeFrom = "";
			inputTimeUntil = "";
			truckPriority = null;
			setTruckPriorities(null);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			init();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    /**
     * Override struts method.
     * Note: this is not associated with or invoked by the reset button in the
     * ordermgr.web form, but rather is called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			inputEvent = "";
			inputDateFrom = "";
			inputDateUntil = "";
			inputTimeFrom = "";
			inputTimeUntil = "";
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

	public TruckEntity getTruck() {
		return truck;
	}

	public void setTruck(TruckEntity truck) {
		this.truck = truck;
	}

	public List getTruckUnavailabilities() {
		return truckUnavailabilities;
	}

	public void setTruckUnavailabilities(List truckUnavailabilities) {
		this.truckUnavailabilities = truckUnavailabilities;
	}

	public String getInputAction() {
		return inputAction;
	}

	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}

	public Date getTimeStart() throws ParseException, NumberFormatException {
		Calendar c = Calendar.getInstance();
		c.setTime(DateTimeParser.parseLogonDayMonthYear(Locale.ENGLISH, inputDateFrom));
		
		Integer timeFromHour = Integer.valueOf(inputTimeFrom.substring(0, 2));
		c.add(Calendar.HOUR_OF_DAY, timeFromHour);
		
		Integer timeFromMinute = Integer.valueOf(inputTimeFrom.substring(3));
		c.add(Calendar.MINUTE, timeFromMinute);
		return c.getTime();
	}
	
	public Date getTimeUntil() throws ParseException, NumberFormatException {
		Calendar c = Calendar.getInstance();	
		c.setTime(DateTimeParser.parseLogonDayMonthYear(Locale.ENGLISH, inputDateUntil));

		Integer timeUntilHour = Integer.valueOf(inputTimeUntil.substring(0, 2));
		c.add(Calendar.HOUR_OF_DAY, timeUntilHour);
		
		Integer timeUntilMinute = Integer.valueOf(inputTimeUntil.substring(3));
		c.add(Calendar.MINUTE, timeUntilMinute);
		return c.getTime();
	}

	public long getInputTruckUnavailabilityId() {
		return inputTruckUnavailabilityId;
	}

	public void setInputTruckUnavailabilityId(long inputTruckUnavailabilityId) {
		this.inputTruckUnavailabilityId = inputTruckUnavailabilityId;
	}

	public TruckTypeTxEntity getType() {
		return type;
	}

	public void setType(TruckTypeTxEntity type) {
		this.type = type;
	}

	public TrailerEntity getTrailer() {
		return trailer;
	}

	public void setTrailer(TrailerEntity trailer) {
		this.trailer = trailer;
	}

	public TruckTransportCatTxEntity getTrspCat() {
		return trspCat;
	}

	public void setTrspCat(TruckTransportCatTxEntity trspCat) {
		this.trspCat = trspCat;
	}

	public TrailerTrspCatTxEntity getTrailerTrspCat() {
		return trailerTrspCat;
	}

	public void setTrailerTrspCat(TrailerTrspCatTxEntity trailerTrspCat) {
		this.trailerTrspCat = trailerTrspCat;
	}

	public ContainerEntity getContainer() {
		return container;
	}

	public void setContainer(ContainerEntity container) {
		this.container = container;
	}

	public String getCompositionTare() {
		return compositionTare;
	}

	public void setCompositionTare(String compositionTare) {
		this.compositionTare = compositionTare;
	}
	
    public String getCompositionGross() {
		return compositionGross;
	}

	public void setCompositionGross(String compositionGross) {
		this.compositionGross = compositionGross;
	}

	public String getInputEvent() {
		return inputEvent;
	}

	public void setInputEvent(String inputEvent) {
		this.inputEvent = inputEvent;
	}
    
	public String getInputDateFrom() {
		return inputDateFrom;
	}

	public void setInputDateFrom(String inputDateFrom) {
		this.inputDateFrom = inputDateFrom;
	}

	public String getInputTimeFrom() {
		return inputTimeFrom;
	}

	public void setInputTimeFrom(String inputTimeFrom) {
		this.inputTimeFrom = inputTimeFrom;
	}

	public String getInputDateUntil() {
		return inputDateUntil;
	}

	public void setInputDateUntil(String inputDateUntil) {
		this.inputDateUntil = inputDateUntil;
	}

	public String getInputTimeUntil() {
		return inputTimeUntil;
	}

	public void setInputTimeUntil(String inputTimeUntil) {
		this.inputTimeUntil = inputTimeUntil;
	}

	public String getCompositionNrChambers() {
		return compositionNrChambers;
	}

	public void setCompositionNrChambers(String compositionNrChambers) {
		this.compositionNrChambers = compositionNrChambers;
	}
	
    public Long getTruckPriority() {
		return truckPriority;
	}

	public void setTruckPriority(Long truckPriority) {
		this.truckPriority = truckPriority;
	}

	public long[] getTruckPriorities() {
		return truckPriorities;
	}

	public void setTruckPriorities(long[] truckPriorities) {
		this.truckPriorities = truckPriorities;
	}
	
}