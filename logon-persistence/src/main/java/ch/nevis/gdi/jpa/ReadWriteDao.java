/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa;

import javax.persistence.EntityManager;


/**
 * @author AdNovum Informatik AG
 */
public interface ReadWriteDao<T extends BaseEntity> extends ReadOnlyDao<T> {
    
	/**
	 * Make an instance managed and persistent.<br>
	 * <b>NOTE</b> This call <b>always</b> executes a {@link EntityManager#flush()},
	 * too, for historical reasons: developers sometimes read from views right after
	 * persisting an entity, expecting the results to be reflected by the view.
	 * @param entity
	 * @throws EntityExistsException if the entity already exists.
	 * (The EntityExistsException may be thrown when the persist
	 * operation is invoked, or the EntityExistsException or
	 * another PersistenceException may be thrown at flush or
	 * commit time.)
	 * @throws IllegalArgumentException if not an entity
	 */
	T persist(T entity);

	/**
	 * Make an instance managed and persistent.<br>
	 * <b>NOTE</b> If <code>doFlush</code> is <code>false</code>, the results of this call are
	 * only flushed when {@link EntityManager#flush()} is called next time, typically only before
	 * the transaction ends. They are, therefore, not immediately reflected by any view.
	 * @param entity
	 * @param doFlush
	 * @throws EntityExistsException if the entity already exists.
	 * (The EntityExistsException may be thrown when the persist
	 * operation is invoked, or the EntityExistsException or
	 * another PersistenceException may be thrown at flush or
	 * commit time.)
	 * @throws IllegalArgumentException if not an entity
	 */
	T persist(T entity, boolean doFlush);
	
	/**
	 * Merge the state of the given entity into the current persistence context.<br>
	 * <b>NOTE</b> This call <b>always</b> executes a {@link EntityManager#flush()},
	 * too, for historical reasons: developers sometimes read from views right after
	 * merging an entity, expecting the results to be reflected by the view.
	 * @param entity
	 * @return the instance that the state was merged to
	 * @throws IllegalArgumentException if instance is not an entity or is a removed entity
	 */
	T merge(T entity);
	
	/**
	 * Merge the state of the given entity into the current persistence context.<br>
	 * <b>NOTE</b> If <code>doFlush</code> is <code>false</code>, the results of this call are
	 * only flushed when {@link EntityManager#flush()} is called next time, typically only before
	 * the transaction ends. They are, therefore, not immediately reflected by any view.
	 * @param entity
	 * @param doFlush
	 * @return the instance that the state was merged to
	 * @throws IllegalArgumentException if instance is not an entity or is a removed entity
	 */
	T merge(T entity, boolean doFlush);
	
	/**
	 * Remove the entity instance.<br>
	 * <b>NOTE</b> This call <b>always</b> executes a {@link EntityManager#flush()},
	 * too, for historical reasons: developers sometimes read from views right after
	 * removing an entity, expecting the results to be reflected by the view.
	 * @param entity
	 * @throws IllegalArgumentException if not an entity or if a detached entity
	 */
	void remove(final T entity);
	
	/**
	 * Remove the entity instance.<br>
	 * <b>NOTE</b> If <code>doFlush</code> is <code>false</code>, the results of this call are
	 * only flushed when {@link EntityManager#flush()} is called next time, typically only before
	 * the transaction ends. They are, therefore, not immediately reflected by any view.
	 * @param entity
	 * @param doFlush
	 * @throws IllegalArgumentException if not an entity or if a detached entity
	 */
	void remove(final T entity, boolean doFlush);
	
}
