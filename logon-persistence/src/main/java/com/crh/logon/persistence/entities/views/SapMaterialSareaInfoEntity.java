package com.crh.logon.persistence.entities.views;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import ch.nevis.gdi.jpa.BaseEntity;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNS_SAP_MATERIAL_SAREA_INFO")
public class SapMaterialSareaInfoEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNS_SAP_MATERIAL_SAREA_INFO";


    @Id
    @Column(name = "SAP_MATERIAL_SAREA_ID")
    private Long sapMaterialSareaId;
    
    @Column(name = "SAP_MATERIAL_NO")
    private String sapMaterialNo;

    @Column(name = "SAP_MATERIAL_ID")
    private Long sapMaterialId;

    @Column(name = "DISTRIBUTION_CHANNEL")
    private String distributionChannel;

    @Column(name = "MARKED_FOR_DELETION")
    private String markedForDeletion;
    
    @Column(name = "LOGON_PACKAGE_TYPE_CD")
    private Long logonPackageTypeCd;

    @Column(name = "SORG_CD")
    private String sorgCd;

    // Fieldnames
    public static final String ATTR_SORG_CD = "sorgCd";
    public static final String ATTR_SAP_MATERIAL_SAREA_ID = "sapMaterialSareaId";
    public static final String ATTR_SAP_MATERIAL_ID = "sapMaterialId";
    public static final String ATTR_SAP_MATERIAL_NO = "sapMaterialNo";
    public static final String ATTR_DISTRIBUTION_CHANNEL = "distributionChannel";
    public static final String ATTR_MARKED_FOR_DELETION = "markedForDeletion";
    public static final String ATTR_LOGON_PACKAGE_TYPE_CD = "logonPackageTypeCd";

    // Field length on DB
    public static final long DBSIZE_SAP_MATERIAL_SAREA_ID = 12;
    public static final long DBSIZE_SAP_MATERIAL_ID = 12;
    public static final long DBSIZE_DISTRIBUTION_CHANNEL = 2;
    public static final long DBSIZE_MARKED_FOR_DELETION = 1;

    /**
     * SAP_MATERIAL_SAREA_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setSapMaterialSareaId(Long sapMaterialSareaId) {
        this.sapMaterialSareaId = sapMaterialSareaId;
    }

    /**
     * SAP_MATERIAL_SAREA_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getSapMaterialSareaId() {
        return sapMaterialSareaId;
    }

    /**
     * SAP_MATERIAL_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setSapMaterialId(Long sapMaterialId) {
        this.sapMaterialId = sapMaterialId;
    }

    /**
     * SAP_MATERIAL_NO is NOT nullable
     * Field length on DB: 18
     * SAP material number/identifier, leading zeros are trimmed. SAP: material_general_data (MARA) / MATNR, trimmed.
     */
    public void setSapMaterialNo(String sapMaterialNo) {
        this.sapMaterialNo = sapMaterialNo;
    }

    /**
     * SAP_MATERIAL_NO is NOT nullable
     * Field length on DB: 18
     * SAP material number/identifier, leading zeros are trimmed. SAP: material_general_data (MARA) / MATNR, trimmed.
     */
    public String getSapMaterialNo() {
        return sapMaterialNo;
    }
    /**
     * SAP_MATERIAL_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getSapMaterialId() {
        return sapMaterialId;
    }
    /**
     * DISTRIBUTION_CHANNEL is NOT nullable
     * Field length on DB: 2
     * The Distribution Channel for which this material-sales-area is defined for, e.g. 10=Domestic, 20=Export. SAP: material_sales_data (E1MVKEM) / VTWEG
     */
    public void setDistributionChannel(String distributionChannel) {
        this.distributionChannel = distributionChannel;
    }

    /**
     * DISTRIBUTION_CHANNEL is NOT nullable
     * Field length on DB: 2
     * The Distribution Channel for which this material-sales-area is defined for, e.g. 10=Domestic, 20=Export. SAP: material_sales_data (E1MVKEM) / VTWEG
     */
    public String getDistributionChannel() {
        return distributionChannel;
    }

    /**
     * MARKED_FOR_DELETION is NOT nullable
     * Field length on DB: 1
     * Deletion code in SAP. SAP: material_sales_data (E1MVKEM) / LVORM. 0=active, 1=deleted. Note that Logon normally has active_flag, with inversed logic. We stick to SAP standards in the tlgns_sap_... tables.
     */
    public void setMarkedForDeletion(String markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    /**
     * MARKED_FOR_DELETION is NOT nullable
     * Field length on DB: 1
     * Deletion code in SAP. SAP: material_sales_data (E1MVKEM) / LVORM. 0=active, 1=deleted. Note that Logon normally has active_flag, with inversed logic. We stick to SAP standards in the tlgns_sap_... tables.
     */
    public String getMarkedForDeletion() {
        return markedForDeletion;
    }
    /**
     * PACKAGE_TYPE_CD is NOT nullable
     * Field length on DB: 2
     */
    public void setLogonPackageTypeCd(Long logonPackageTypeCd) {
        this.logonPackageTypeCd = logonPackageTypeCd;
    }

    /**
     * PACKAGE_TYPE_CD is NOT nullable
     * Field length on DB: 2
     */
    public Long getLogonPackageTypeCd() {
        return logonPackageTypeCd;
    }

    /**
     * SORG_CD is NOT nullable
     * Field length on DB: 4
     */
    public void setSorgCd(String sorgCd) {
        this.sorgCd = sorgCd;
    }

    /**
     * SORG_CD is NOT nullable
     * Field length on DB: 4
     */
    public String getSorgCd() {
        return sorgCd;
    }
    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("sapMaterialSareaId", sapMaterialSareaId);
            tsb.append("sapMaterialId", sapMaterialId);
            tsb.append("sapMaterialNo", sapMaterialNo);
            tsb.append("distributionChannel", distributionChannel);
            tsb.append("markedForDeletion", markedForDeletion);
            tsb.append("logonPackageTypeCd", logonPackageTypeCd);

        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("sapMaterialSareaId", sapMaterialSareaId); // ID
        }
        return tsb.toString();
    }
}
