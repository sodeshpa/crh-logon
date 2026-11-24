package com.crh.logon.business.stores.system;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.admin.UserEntity;

@Local
public interface UserStoreInterface extends GenericReadOnlyInterface<UserEntity> {

}