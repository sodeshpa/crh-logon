/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.ZoneMapServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.ZoneMapServiceLocalInterface;
import com.crh.logon.persistence.entities.common.TrspZonePolygonEntity;
import com.crh.logon.persistence.entities.common.TrspZoneVertexEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class ZoneMapServiceBean extends ZoneMapServiceDispatcher 
	implements ZoneMapServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ZoneMapServiceBean.class);

	/**
	 * Loads all polygons 
	 * @adnlog.exclude return
	 */
	public List<TrspZonePolygonEntity> loadTrspZonePolygons() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTrspZonePolygons");
		}
		// ]]
		try {
			final ReadOnlyDao<TrspZonePolygonEntity> trspDao = getDaoProvider().getTrspZonePolygonEntityDao();
			final Filter<TrspZonePolygonEntity> filter = trspDao.createFilter();
			filter.addExpression(Order.asc(TrspZonePolygonEntity.ATTR_TRSP_ZONE_CD));
			return trspDao.find(filter);
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
	 * Loads coordinates of all polygons 
	 * @adnlog.exclude return
	 */
	public List<TrspZoneVertexEntity> loadTrspZoneVertexs() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTrspZoneVertexs");
		}
		// ]]
		try {
			final ReadOnlyDao<TrspZoneVertexEntity> trspDao = getDaoProvider().getTrspZoneVertexEntityDao();
			final Filter<TrspZoneVertexEntity> filter = trspDao.createFilter();
			filter.addExpression(Order.asc(TrspZoneVertexEntity.ATTR_TRSP_ZONE_POLYGON_ID));
			filter.addExpression(Order.asc(TrspZoneVertexEntity.ATTR_SEQ_NO));
			return trspDao.find(filter);
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
	 * Loads coordinates of all polygons 
	 * @param updateList list to update
	 * @param removeList list to remove
	 * @adnlog.exclude return
	 */
	public List<TrspZonePolygonEntity> updateTrspZonePolygons(List<TrspZonePolygonEntity> updateList, List<TrspZonePolygonEntity> removeList, Map<String, List<TrspZoneVertexEntity>> trspZoneVertexMap) {
		// [[
		Object[][] params = {{"updateList", updateList},
							{"removeList", removeList},
							{"trspZoneVertexMap", trspZoneVertexMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTrspZonePolygons", params);
		}
		// ]]
		try {
			
			List<TrspZonePolygonEntity> updatedPolygons = new ArrayList<TrspZonePolygonEntity>();
			ReadWriteDao<TrspZonePolygonEntity> trspDao = getDaoProvider().getTrspZonePolygonEntityDao();
			ReadWriteDao<TrspZoneVertexEntity> vertexDao = getDaoProvider().getTrspZoneVertexEntityDao();
			
			//remove polygons and their coordinates
			for (TrspZonePolygonEntity polygon : removeList) {
				polygon = trspDao.merge(polygon);
				
				//get init vertex list
				final Filter<TrspZoneVertexEntity>  filter = vertexDao.createFilter();
				filter.addExpression(Restrictions.eq(TrspZoneVertexEntity.ATTR_TRSP_ZONE_POLYGON_ID, polygon.getTrspZonePolygonId()));
				final List<TrspZoneVertexEntity>  initVertexList = vertexDao.find(filter);
				
				//remove all points
				for (TrspZoneVertexEntity vertex : initVertexList) {
					vertexDao.remove(vertex);
				}
				
				//remove polygon
				trspDao.remove(polygon);
			}
			
			
			//add or update polygons and their coordinates
			for (TrspZonePolygonEntity polygon : updateList) {
				//get changed vertex list
				final List<TrspZoneVertexEntity> vertexList = trspZoneVertexMap.get(polygon.getTrspZonePolygonId().toString());
				
				if (polygon.getTrspZonePolygonId() < 0) {
					polygon.setTrspZonePolygonId(null);
					TrspZonePolygonEntity savedPolygon = trspDao.persist(polygon);
					updatedPolygons.add(savedPolygon);
					
					//add new points
					for (TrspZoneVertexEntity vertex : vertexList) {
						vertex.setTrspZonePolygon(savedPolygon);
						vertexDao.persist(vertex);
					}
				}
				else {
					updatedPolygons.add(trspDao.merge(polygon));
					
					//get init vertex list
					final Filter<TrspZoneVertexEntity> filter = vertexDao.createFilter();
					filter.addExpression(Restrictions.eq(TrspZoneVertexEntity.ATTR_TRSP_ZONE_POLYGON_ID, polygon.getTrspZonePolygonId()));
					final List<TrspZoneVertexEntity> initVertexList = vertexDao.find(filter);
					
					// removed or added new point
					if (initVertexList.size() != vertexList.size()) {
						//remove all old points
						for (TrspZoneVertexEntity vertex : initVertexList) {
							vertexDao.remove(vertex);
						}
						
						//add new points
						for (TrspZoneVertexEntity vertex : vertexList) {
							vertex.setTrspZonePolygon(polygon);
							vertexDao.persist(vertex);
						}
					} 
					else { 
						//points maybe changed
						for (int i = 0; i < initVertexList.size(); i++) {
							TrspZoneVertexEntity initVertex = (TrspZoneVertexEntity) initVertexList.get(i);
							TrspZoneVertexEntity vertex = (TrspZoneVertexEntity) vertexList.get(i);
							initVertex.setCoordLat(vertex.getCoordLat());
							initVertex.setCoordLon(vertex.getCoordLon());
							vertexDao.merge(initVertex);
						}
					}
				}
			}
			
			return  updatedPolygons;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}
