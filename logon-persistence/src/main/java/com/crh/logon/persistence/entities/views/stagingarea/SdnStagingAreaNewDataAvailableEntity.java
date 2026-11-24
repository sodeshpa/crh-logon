package com.crh.logon.persistence.entities.views.stagingarea;

import javax.persistence.Table;
import javax.persistence.Entity;


@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNS_STAGINGAREA_SDN_NEW")
public class SdnStagingAreaNewDataAvailableEntity extends StagingAreaNewDataAvailableBaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNS_STAGINGAREA_SDN_NEW";

}
