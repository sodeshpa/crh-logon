/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.common.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import ch.nevis.gdi.jpa.BaseEntity;

import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.utils.DaoProvider;

public abstract class GenericReadOnlyProvider<T extends BaseEntity> implements GenericReadOnlyInterface<T> {

	@PersistenceContext(unitName="logon")
	protected EntityManager em;

	
	private DaoProvider daoProvider;

	public T findById(Long id) {
		Class<T> entityClass = getClassOfEntity();
		T result = em.find(entityClass , id);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<T> fetchAll() {
		Class<T> entityClass = getClassOfEntity();
		Query q = em.createQuery("SELECT e FROM " + entityClass.getName() + " e");
		List<T> list = (List<T>) q.getResultList();
		return list;
	}

	/**
	 * @adnlog.notrace
	 */
	public DaoProvider getDaoProvider() {
		if (daoProvider == null) {
			daoProvider = new DaoProvider(em, new Authorizer());
		}
		else {
			daoProvider.refresh(em);
		}
		return daoProvider;
	}

	private Class<T> getClassOfEntity() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		Type[] parameterTypes = genericSuperclass.getActualTypeArguments();
		Class<T> entityClass = (Class<T>) parameterTypes[0];
		return entityClass;
	}

}