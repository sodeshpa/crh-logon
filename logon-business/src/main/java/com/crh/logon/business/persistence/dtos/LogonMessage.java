/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.persistence.dtos;

import java.io.Serializable;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class LogonMessage implements Serializable {
    private Long userId;
    private List messageBeans; // list of LogonEventOutBean
    private boolean accessRestrictions;
    private boolean disableNextAccessRestrictions;
    
    public LogonMessage(List messageBeans, Long userId, boolean accessRestrictions, boolean disableNextAccessRestrictions) {
        this.messageBeans = messageBeans;
        this.userId = userId;
        this.accessRestrictions = accessRestrictions;
        this.disableNextAccessRestrictions = disableNextAccessRestrictions;
    }
    public List getMessageBeans() {
        return messageBeans;
    }
    public Long getUserId() {
        return userId;
    }
    public boolean getAccessRestrictions() {
        return accessRestrictions;
    }
    public boolean getDisableNextAccessRestrictions() {
        return disableNextAccessRestrictions;
    }
}
