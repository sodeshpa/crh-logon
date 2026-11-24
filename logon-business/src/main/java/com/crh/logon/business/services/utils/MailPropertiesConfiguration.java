/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
	
public final class MailPropertiesConfiguration implements Serializable {
	
	public enum ConfigurationType {
		DEFAULT, REP_ORDER
	}

	private final ConfigurationType configurationType;
	private final SareaEntity sarea;
	
	public MailPropertiesConfiguration() {
		this((SareaEntity) null);
	}

	public MailPropertiesConfiguration(OrderEntity order) {
		this((SareaEntity) (order != null ? order.getSarea() : null));
	}
	
	public MailPropertiesConfiguration(SareaEntity sarea) {	
		this(ConfigurationType.DEFAULT, sarea);
	}
	
	public MailPropertiesConfiguration(ConfigurationType configurationType) {		
		this(configurationType, null);
	}	

	public MailPropertiesConfiguration(ConfigurationType configurationType, SareaEntity sarea) {		
		this.configurationType = configurationType;
		this.sarea = sarea;
	}

	public ConfigurationType getConfigurationType() {
		return configurationType;
	}
	
	public SareaEntity getSarea() {
		return sarea;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("configurationType", configurationType);
		builder.append("sarea", sarea);
		return builder.toString();
	}	
}
