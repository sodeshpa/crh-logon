
package com.crh.logon.persistence.entities.views;

import java.util.Date;

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
@Table(name = "VLGNS_SAP_MAT_SAREA_CONTRACT")
public class SapMatSareaContractEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNS_SAP_MAT_SAREA_CONTRACT";

    @Id
    @Column(name = "COMPOSITE_ID")
    private String compositeId;
    
    
    @Column(name = "SOLDTO_SAP_BP_ID")
    private Long soldtoSapBpId;

    @Column(name = "SHIPTO_SAP_BP_ID")
    private Long shiptoSapBpId;


    @Column(name = "SAP_CONTRACT_ITEM_ID")
    private Long sapContractItemId;

    @Column(name = "SAP_CONTRACT_ID")
    private Long sapContractId;

    @Column(name = "SAP_MATERIAL_ID")
    private Long sapMaterialId;
    
    @Column(name = "SAP_MATERIAL_NO")
    private String sapMaterialNo;
    
    @Column(name = "CONTRACT_SAP_MATERIAL_ID")
    private Long contractSapMaterialId;

    @Column(name = "MATERIAL_GROUP_4")
    private String materialGroup4;
    
    @Column(name = "CONTRACT_HIERARCHY_LEVEL")
    private Long contractHierarchyLevel;

    @Column(name = "VALID_FROM_DAT")
    private Date validFromDat;
    
    @Column(name = "IS_GENERIC_FLAG")
    private Boolean isGenericFlag;
    
    @Column(name = "ITEM_GROUPING_TYPE")
    private Long itemGroupingType;
    
    @Column(name = "IS_AP_GROUP_LEADER")
    private Boolean isApGroupLeader;
    
    @Column(name = "AP_GROUP_LEADER_ID")
    private Long apGroupLeaderId;
    
    @Column(name = "LOGON_PACKAGE_TYPE_CD")
    private Long logonPackageTypeCd;
    
    // Fieldnames
    public static final String ATTR_COMPOSITE_ID = "compositeId";
    public static final String ATTR_SOLDTO_SAP_BP_ID = "soldtoSapBpId";
    public static final String ATTR_SHIPTO_SAP_BP_ID = "shiptoSapBpId";
    public static final String ATTR_SAP_MATERIAL_NO = "sapMaterialNo";
    public static final String ATTR_SAP_CONTRACT_ITEM_ID = "sapContractItemId";
    public static final String ATTR_SAP_CONTRACT_ID = "sapContractId";
    public static final String ATTR_CONTRACT_SAP_MATERIAL_ID = "contactSapMaterialId";
    public static final String ATTR_SAP_MATERIAL_ID = "sapMaterialId";
    public static final String ATTR_MATERIAL_GROUP_4 = "materialGroup4";
    public static final String ATTR_IS_GENERIC_FLAG = "isGenericFlag";
    public static final String ATTR_ITEM_GROUPING_TYPE = "itemGroupingType";
    public static final String ATTR_AP_GROUP_LEADER = "apGroupLeaderId";
    public static final String ATTR_LOGON_PACKAGE_TYPE_CD = "logonPackageTypeCd";

    // Field length on DB
    public static final long DBSIZE_COMPOSITE_ID = 43;
    public static final long DBSIZE_SAP_CONTRACT_ITEM_ID = 12;
    public static final long DBSIZE_SAP_CONTRACT_ID = 12;
    public static final long DBSIZE_CONTRACT_ITEM_NO = 6;
    public static final long DBSIZE_SAP_MATERIAL_ID = 12;
    public static final long DBSIZE_MATERIAL_GROUP_4 = 3;

    /**
     * COMPOSITE_ID is nullable
     * Field length on DB: 43
     */
    public void setCompositeId(String compositeId) {
        this.compositeId = compositeId;
    }

    /**
     * COMPOSITE_ID is nullable
     * Field length on DB: 43
     */
    public String getCompositeId() {
        return compositeId;
    }


    /**
     * SAP_CONTRACT_ITEM_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setSapContractItemId(Long sapContractItemId) {
        this.sapContractItemId = sapContractItemId;
    }

    /**
     * SAP_CONTRACT_ITEM_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getSapContractItemId() {
        return sapContractItemId;
    }

    /**
     * SAP_CONTRACT_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setSapContractId(Long sapContractId) {
        this.sapContractId = sapContractId;
    }

    /**
     * SAP_CONTRACT_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getSapContractId() {
        return sapContractId;
    }

    /**
     * SAP_MATERIAL_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setSapMaterialId(Long sapMaterialId) {
        this.sapMaterialId = sapMaterialId;
    }

    /**
     * SAP_MATERIAL_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getSapMaterialId() {
        return sapMaterialId;
    }
    
    /**
     * CONTRACT_SAP_MATERIAL_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setContractSapMaterialId(Long contractSapMaterialId) {
        this.contractSapMaterialId = contractSapMaterialId;
    }

    /**
     * SAP_MATERIAL_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getContractSapMaterialId() {
        return contractSapMaterialId;
    }

    /**
     * MATERIAL_GROUP_4 is nullable
     * Field length on DB: 3
     */
    public void setMaterialGroup4(String materialGroup4) {
        this.materialGroup4 = materialGroup4;
    }

    /**
     * MATERIAL_GROUP_4 is nullable
     * Field length on DB: 3
     */
    public String getMaterialGroup4() {
        return materialGroup4;
    }

    /**
     * CONTRACT_HIERARCHY_LEVEL is NOT nullable
     * Field length on DB: 1
     * Contract hierarchy level. Depending on this value and the settings on the sales area it is determined if a contract is valid for order taking. Possible values: 1 = Specific contracts, 2 = Frame Contract, 3 = One Time Customer Contracts. Any other value then those above will be interpreted as Level 3 contracts.
     */
    public void setContractHierarchyLevel(Long contractHierarchyLevel) {
        this.contractHierarchyLevel = contractHierarchyLevel;
    }

    /**
     * CONTRACT_HIERARCHY_LEVEL is NOT nullable
     * Field length on DB: 1
     * Contract hierarchy level. Depending on this value and the settings on the sales area it is determined if a contract is valid for order taking. Possible values: 1 = Specific contracts, 2 = Frame Contract, 3 = One Time Customer Contracts. Any other value then those above will be interpreted as Level 3 contracts.
     */
    public Long getContractHierarchyLevel() {
        return contractHierarchyLevel;
    }
    
    /**
     * VALID_FROM_DAT is NOT nullable
     * 
     * Valid-from date as received from SAP. 1.1.1970=no start date / since ever. (Origin: VBAK.GUEBG / E1EDK03.datum.iddat.019)
     */
    public void setValidFromDat(Date validFromDat) {
        this.validFromDat = validFromDat;
    }

    /**
     * VALID_FROM_DAT is NOT nullable
     * 
     * Valid-from date as received from SAP. 1.1.1970=no start date / since ever. (Origin: VBAK.GUEBG / E1EDK03.datum.iddat.019)
     */
    public Date getValidFromDat() {
        return validFromDat;
    }
    /**
     * IS_GENERIC_FLAG is NOT nullable
     * 
     * Technical flag for easier filtering. Is set if the material group4 defines the material as a generic material. 1=Generic Material, 0=Real Material
     */
    public void setIsGenericFlag(Boolean isGenericFlag) {
        this.isGenericFlag = isGenericFlag;
    }
    
    /**
     * IS_GENERIC_FLAG is NOT nullable
     * 
     * Technical flag for easier filtering. Is set if the material group4 defines the material as a generic material. 1=Generic Material, 0=Real Material
     */
    public Boolean getIsGenericFlag() {
        return isGenericFlag;
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
     * ITEM_GROUPING_TYPE is NOT nullable
     * Field length on DB: 2
     * Grouping type applied for the contract items during sap import, meaning a different contract_item generation method. Should not change during the lifecycle of a contract. Values: 0 (unknown) - for contracts already in the database at the time of introducing this field, and no possibility to achieve which type was applied (but known: quantity restricted or advance payment); 1 (no grouping) - no group leader for the contract items; 2 (quantity restricted) - every contract item has its own group leader, whose quantity is a limitation: it cannot be exceeded in order taking; 3 (advance payment) - 1 group leader for many payments (sap contract items), with a limitation: sum of quantities cannot be exceeded in order taking
     */
    public void setItemGroupingType(Long itemGroupingType) {
        this.itemGroupingType = itemGroupingType;
    }

    /**
     * ITEM_GROUPING_TYPE is NOT nullable
     * Field length on DB: 2
     * Grouping type applied for the contract items during sap import, meaning a different contract_item generation method. Should not change during the lifecycle of a contract. Values: 0 (unknown) - for contracts already in the database at the time of introducing this field, and no possibility to achieve which type was applied (but known: quantity restricted or advance payment); 1 (no grouping) - no group leader for the contract items; 2 (quantity restricted) - every contract item has its own group leader, whose quantity is a limitation: it cannot be exceeded in order taking; 3 (advance payment) - 1 group leader for many payments (sap contract items), with a limitation: sum of quantities cannot be exceeded in order taking
     */
    public Long getItemGroupingType() {
        return itemGroupingType;
    }
    
    public void setApGroupLeaderId(Long apGroupLeaderId) {
        this.apGroupLeaderId = apGroupLeaderId;
    }

    public Long getApGroupLeaderId() {
        return apGroupLeaderId;
    }
    
    /**
     * IS_AP_GROUP_LEADER is NOT nullable
     * 
     * Defines whether this contract item represents an Advance Payment Group Leader - a virtual contract item from which other contract items are associated to for easier lookup and group associations
     */
    public void setIsApGroupLeader(Boolean isApGroupLeader) {
        this.isApGroupLeader = isApGroupLeader;
    }
    /**
     * IS_AP_GROUP_LEADER is NOT nullable
     * 
     * Defines whether this contract item represents an Advance Payment Group Leader - a virtual contract item from which other contract items are associated to for easier lookup and group associations
     */
    public Boolean getIsApGroupLeader() {
        return isApGroupLeader;
    }
    
    public void setLogonPackageTypeCd(Long logonPackageTypeCd) {
        this.logonPackageTypeCd = logonPackageTypeCd;
    }

    public Long getLogonPackageTypeCd() {
        return logonPackageTypeCd;
    }
    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("compositeId", compositeId);
            tsb.append("sapContractItemId", sapContractItemId);
            tsb.append("sapContractId", sapContractId);
            tsb.append("sapMaterialId", sapMaterialId);
            tsb.append("contractSapMaterialId", contractSapMaterialId);
            tsb.append("sapMaterialNo", sapMaterialNo);
            tsb.append("materialGroup4", materialGroup4);
            tsb.append("contractHierarchyLevel", contractHierarchyLevel);
            tsb.append("validFromDat", validFromDat);
            tsb.append("isGenericFlag", isGenericFlag);
            tsb.append("itemGroupingType", itemGroupingType);
            tsb.append("isApGroupLeader", isApGroupLeader);
            tsb.append("logonPackageTypeCd", logonPackageTypeCd);
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("compositeId", compositeId); // ID
        }
        return tsb.toString();
    }
    
}
