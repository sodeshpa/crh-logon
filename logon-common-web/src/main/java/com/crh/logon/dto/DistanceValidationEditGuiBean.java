package com.crh.logon.dto;

import java.text.ParseException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.views.SareaDisplayEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

public class DistanceValidationEditGuiBean {

		private Boolean dbltActiveFlag;
		private String dbltDistanceL1;
		private String dbltDistanceL2;
		private String h1EarlyOrderLimit = "";
		private String h2EarliestDeliveryFrom = "";
		private String dbltOffsetD1;
		private String dbltOffsetD2;
		private String dbltOffsetD3;
		private String dbltOffsetD4;
		private String dbltOffsetD5;
		private String dbltOffsetD6;


		protected DistanceValidationEditGuiBean() {
		}

		public DistanceValidationEditGuiBean(SareaDisplayEntity sarea) {
			this.dbltActiveFlag = null; 	// null means no dbltActiveFlag field exist at all in sarea
			this.dbltDistanceL1 = sarea.getDbltDistanceL1() != null ? sarea.getDbltDistanceL1().toString() : null;
			this.dbltDistanceL2 = sarea.getDbltDistanceL2() != null ? sarea.getDbltDistanceL2().toString() : null;

			if (sarea.getH1EarlyOrderLimit() != null) {
				this.h1EarlyOrderLimit = DateTimeUtils.formatHoursMinutes(
						DateTimeUtils.getHoursMinutes(sarea.getH1EarlyOrderLimit()));
			}

			if (sarea.getH2EarliestDeliveryFrom() != null) {
				this.h2EarliestDeliveryFrom = DateTimeUtils.formatHoursMinutes(
						DateTimeUtils.getHoursMinutes(sarea.getH2EarliestDeliveryFrom()));
			}

			this.dbltOffsetD1 = sarea.getDbltOffsetD1() != null ? sarea.getDbltOffsetD1().toString() : null;
			this.dbltOffsetD2 = sarea.getDbltOffsetD2() != null ? sarea.getDbltOffsetD2().toString() : null;
			this.dbltOffsetD3 = sarea.getDbltOffsetD3() != null ? sarea.getDbltOffsetD3().toString() : null;
			this.dbltOffsetD4 = sarea.getDbltOffsetD4() != null ? sarea.getDbltOffsetD4().toString() : null;
			this.dbltOffsetD5 = sarea.getDbltOffsetD5() != null ? sarea.getDbltOffsetD5().toString() : null;
			this.dbltOffsetD6 = sarea.getDbltOffsetD6() != null ? sarea.getDbltOffsetD6().toString() : null;
		}

	public DistanceValidationEditGuiBean(SareaEntity sarea) {
		this.dbltActiveFlag = null; 	// null means no dbltActiveFlag field exist at all in sarea
		this.dbltDistanceL1 = sarea.getDbltDistanceL1() != null ? sarea.getDbltDistanceL1().toString() : null;
		this.dbltDistanceL2 = sarea.getDbltDistanceL2() != null ? sarea.getDbltDistanceL2().toString() : null;

		if (sarea.getH1EarlyOrderLimit() != null) {
			this.h1EarlyOrderLimit = DateTimeUtils.formatHoursMinutes(
					DateTimeUtils.getHoursMinutes(sarea.getH1EarlyOrderLimit()));
		}

		if (sarea.getH2EarliestDeliveryFrom() != null) {
			this.h2EarliestDeliveryFrom = DateTimeUtils.formatHoursMinutes(
					DateTimeUtils.getHoursMinutes(sarea.getH2EarliestDeliveryFrom()));
		}

		this.dbltOffsetD1 = sarea.getDbltOffsetD1() != null ? sarea.getDbltOffsetD1().toString() : null;
		this.dbltOffsetD2 = sarea.getDbltOffsetD2() != null ? sarea.getDbltOffsetD2().toString() : null;
		this.dbltOffsetD3 = sarea.getDbltOffsetD3() != null ? sarea.getDbltOffsetD3().toString() : null;
		this.dbltOffsetD4 = sarea.getDbltOffsetD4() != null ? sarea.getDbltOffsetD4().toString() : null;
		this.dbltOffsetD5 = sarea.getDbltOffsetD5() != null ? sarea.getDbltOffsetD5().toString() : null;
		this.dbltOffsetD6 = sarea.getDbltOffsetD6() != null ? sarea.getDbltOffsetD6().toString() : null;
	}

		/**
		 * Use values from shiptoSarea.
		 *
		 * @param shiptoSarea
		 */
		public DistanceValidationEditGuiBean(ShiptoSareaEntity shiptoSarea) {

			// these 2 fields exist only in shipto-sarea
			this.dbltActiveFlag = shiptoSarea.getDbltActiveFlag();

			this.dbltDistanceL1 = shiptoSarea.getDbltDistanceL1() != null ? shiptoSarea.getDbltDistanceL1().toString() : null;
			this.dbltDistanceL2 = shiptoSarea.getDbltDistanceL2() != null ? shiptoSarea.getDbltDistanceL2().toString() : null;

			if (shiptoSarea.getH1EarlyOrderLimit() != null) {
				this.h1EarlyOrderLimit = DateTimeUtils.formatHoursMinutes(
						DateTimeUtils.getHoursMinutes(shiptoSarea.getH1EarlyOrderLimit()));
			}

			if (shiptoSarea.getH2EarliestDeliveryFrom() != null) {
				this.h2EarliestDeliveryFrom = DateTimeUtils.formatHoursMinutes(
						DateTimeUtils.getHoursMinutes(shiptoSarea.getH2EarliestDeliveryFrom()));
			}

			this.dbltOffsetD1 = shiptoSarea.getDbltOffsetD1() != null ? shiptoSarea.getDbltOffsetD1().toString() : null;
			this.dbltOffsetD2 = shiptoSarea.getDbltOffsetD2() != null ? shiptoSarea.getDbltOffsetD2().toString() : null;
			this.dbltOffsetD3 = shiptoSarea.getDbltOffsetD3() != null ? shiptoSarea.getDbltOffsetD3().toString() : null;
			this.dbltOffsetD4 = shiptoSarea.getDbltOffsetD4() != null ? shiptoSarea.getDbltOffsetD4().toString() : null;
			this.dbltOffsetD5 = shiptoSarea.getDbltOffsetD5() != null ? shiptoSarea.getDbltOffsetD5().toString() : null;
			this.dbltOffsetD6 = shiptoSarea.getDbltOffsetD6() != null ? shiptoSarea.getDbltOffsetD6().toString() : null;
		}
		
		public boolean transmitValuesToShiptoSareaEntity(ShiptoSareaEntity shiptoSarea, HttpServletRequest request) {

			try {

				Long dbltDistanceL1 = StringUtils.isEmpty(this.dbltDistanceL1) ? null : Long.valueOf(this.dbltDistanceL1);
				Long dbltDistanceL2 = StringUtils.isEmpty(this.dbltDistanceL2) ? null : Long.valueOf(this.dbltDistanceL2);
				Long dbltOffsetD1 = StringUtils.isEmpty(this.dbltOffsetD1) ? null : Long.valueOf(this.dbltOffsetD1);
				Long dbltOffsetD2 = StringUtils.isEmpty(this.dbltOffsetD2) ? null : Long.valueOf(this.dbltOffsetD2);
				Long dbltOffsetD3 = StringUtils.isEmpty(this.dbltOffsetD3) ? null : Long.valueOf(this.dbltOffsetD3);
				Long dbltOffsetD4 = StringUtils.isEmpty(this.dbltOffsetD4) ? null : Long.valueOf(this.dbltOffsetD4);
				Long dbltOffsetD5 = StringUtils.isEmpty(this.dbltOffsetD5) ? null : Long.valueOf(this.dbltOffsetD5);
				Long dbltOffsetD6 = StringUtils.isEmpty(this.dbltOffsetD6) ? null : Long.valueOf(this.dbltOffsetD6);
				shiptoSarea.setDbltDistanceL1(dbltDistanceL1);
				shiptoSarea.setDbltDistanceL2(dbltDistanceL2);
				shiptoSarea.setDbltOffsetD1(dbltOffsetD1);
				shiptoSarea.setDbltOffsetD2(dbltOffsetD2);
				shiptoSarea.setDbltOffsetD3(dbltOffsetD3);
				shiptoSarea.setDbltOffsetD4(dbltOffsetD4);
				shiptoSarea.setDbltOffsetD5(dbltOffsetD5);
				shiptoSarea.setDbltOffsetD6(dbltOffsetD6);

				if (StringUtils.isEmpty(h1EarlyOrderLimit)) {
					shiptoSarea.setH1EarlyOrderLimit(null);
				}
				else {
					Long minAfterMidnight = null;
					try {
						Date parseHoursMinutes = DateTimeUtils.parseHoursMinutes(h1EarlyOrderLimit);
						minAfterMidnight = DateTimeUtils.getMinAfterMidnight(parseHoursMinutes);
					}
					catch (ParseException e) {
						minAfterMidnight = null;
					}
					shiptoSarea.setH1EarlyOrderLimit(minAfterMidnight);
				}

				if (StringUtils.isEmpty(h2EarliestDeliveryFrom)) {
					shiptoSarea.setH2EarliestDeliveryFrom(null);
				}
				else {
					Long minAfterMidnight = null;
					try {
						Date parseHoursMinutes = DateTimeUtils.parseHoursMinutes(h2EarliestDeliveryFrom);
						minAfterMidnight = DateTimeUtils.getMinAfterMidnight(parseHoursMinutes);
					}
					catch (ParseException e) {
						minAfterMidnight = null;
					}
					shiptoSarea.setH2EarliestDeliveryFrom(minAfterMidnight);
				}

				shiptoSarea.setDbltActiveFlag(this.dbltActiveFlag);
			}
			catch (NumberFormatException e) {
				MessageUtils.addActionWarning(request, "salesarea.errors.badNumberFormat");
				return false;
			}

			return true;
		}

	public void transmitValuesToSareaEntity(SareaDisplayEntity sarea) {

			Long dbltDistanceL1 = StringUtils.isEmpty(this.dbltDistanceL1) ? null : Long.valueOf(this.dbltDistanceL1);
			Long dbltDistanceL2 = StringUtils.isEmpty(this.dbltDistanceL2) ? null : Long.valueOf(this.dbltDistanceL2);
			Long dbltOffsetD1 = StringUtils.isEmpty(this.dbltOffsetD1) ? null : Long.valueOf(this.dbltOffsetD1);
			Long dbltOffsetD2 = StringUtils.isEmpty(this.dbltOffsetD2) ? null : Long.valueOf(this.dbltOffsetD2);
			Long dbltOffsetD3 = StringUtils.isEmpty(this.dbltOffsetD3) ? null : Long.valueOf(this.dbltOffsetD3);
			Long dbltOffsetD4 = StringUtils.isEmpty(this.dbltOffsetD4) ? null : Long.valueOf(this.dbltOffsetD4);
			Long dbltOffsetD5 = StringUtils.isEmpty(this.dbltOffsetD5) ? null : Long.valueOf(this.dbltOffsetD5);
			Long dbltOffsetD6 = StringUtils.isEmpty(this.dbltOffsetD6) ? null : Long.valueOf(this.dbltOffsetD6);
			sarea.setDbltDistanceL1(dbltDistanceL1);
			sarea.setDbltDistanceL2(dbltDistanceL2);
			sarea.setDbltOffsetD1(dbltOffsetD1);
			sarea.setDbltOffsetD2(dbltOffsetD2);
			sarea.setDbltOffsetD3(dbltOffsetD3);
			sarea.setDbltOffsetD4(dbltOffsetD4);
			sarea.setDbltOffsetD5(dbltOffsetD5);
			sarea.setDbltOffsetD6(dbltOffsetD6);

			if (StringUtils.isEmpty(h1EarlyOrderLimit)) {
				sarea.setH1EarlyOrderLimit(null);
			}
			else {
				Long minAfterMidnight = null;
				try {
					Date parseHoursMinutes = DateTimeUtils.parseHoursMinutes(h1EarlyOrderLimit);
					minAfterMidnight = DateTimeUtils.getMinAfterMidnight(parseHoursMinutes);
				}
				catch (ParseException e) {
					minAfterMidnight = null;
				}
				sarea.setH1EarlyOrderLimit(minAfterMidnight);
			}

			if (StringUtils.isEmpty(h2EarliestDeliveryFrom)) {
				sarea.setH2EarliestDeliveryFrom(null);
			}
			else {
				Long minAfterMidnight = null;
				try {
					Date parseHoursMinutes = DateTimeUtils.parseHoursMinutes(h2EarliestDeliveryFrom);
					minAfterMidnight = DateTimeUtils.getMinAfterMidnight(parseHoursMinutes);
				}
				catch (ParseException e) {
					minAfterMidnight = null;
				}
				sarea.setH2EarliestDeliveryFrom(minAfterMidnight);
			}

	}


	public Boolean getDbltActiveFlag() {
		return dbltActiveFlag;
	}

	public void setDbltActiveFlag(Boolean dbltActiveFlag) {
		this.dbltActiveFlag = dbltActiveFlag;
	}

	public String getDbltDistanceL1() {
		return dbltDistanceL1;
	}

	public void setDbltDistanceL1(String dbltDistanceL1) {
		this.dbltDistanceL1 = dbltDistanceL1;
	}

	public String getDbltDistanceL2() {
		return dbltDistanceL2;
	}

	public void setDbltDistanceL2(String dbltDistanceL2) {
		this.dbltDistanceL2 = dbltDistanceL2;
	}

	public String getH1EarlyOrderLimit() {
		return h1EarlyOrderLimit;
	}

	public void setH1EarlyOrderLimit(String h1EarlyOrderLimit) {
		this.h1EarlyOrderLimit = h1EarlyOrderLimit;
	}

	public String getH2EarliestDeliveryFrom() {
		return h2EarliestDeliveryFrom;
	}

	public void setH2EarliestDeliveryFrom(String h2EarliestDeliveryFrom) {
		this.h2EarliestDeliveryFrom = h2EarliestDeliveryFrom;
	}

	public String getDbltOffsetD1() {
		return dbltOffsetD1;
	}

	public void setDbltOffsetD1(String dbltOffsetD1) {
		this.dbltOffsetD1 = dbltOffsetD1;
	}

	public String getDbltOffsetD2() {
		return dbltOffsetD2;
	}

	public void setDbltOffsetD2(String dbltOffsetD2) {
		this.dbltOffsetD2 = dbltOffsetD2;
	}

	public String getDbltOffsetD3() {
		return dbltOffsetD3;
	}

	public void setDbltOffsetD3(String dbltOffsetD3) {
		this.dbltOffsetD3 = dbltOffsetD3;
	}

	public String getDbltOffsetD4() {
		return dbltOffsetD4;
	}

	public void setDbltOffsetD4(String dbltOffsetD4) {
		this.dbltOffsetD4 = dbltOffsetD4;
	}

	public String getDbltOffsetD5() {
		return dbltOffsetD5;
	}

	public void setDbltOffsetD5(String dbltOffsetD5) {
		this.dbltOffsetD5 = dbltOffsetD5;
	}

	public String getDbltOffsetD6() {
		return dbltOffsetD6;
	}

	public void setDbltOffsetD6(String dbltOffsetD6) {
		this.dbltOffsetD6 = dbltOffsetD6;
	}

	public String toString() {
			ToStringBuilder tsb = new ToStringBuilder(this);
			return tsb.toString();
		}

	}
