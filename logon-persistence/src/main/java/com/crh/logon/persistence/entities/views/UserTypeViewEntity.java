package com.crh.logon.persistence.entities.views;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

@SuppressWarnings("serial")
@Entity
@Table(name = "VLGNR_USER_TYPE")
public class UserTypeViewEntity implements BaseEntity {
		
		private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
		private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
		public static final String TABLE_NAME = "VLGNR_USER_TYPE";
	
    	// Fieldnames
    	public static final String ATTR_USER_TYPE_CD = "userTypeCd";
        public static final String ATTR_FUNCTION_ID = "functionId";
        public static final String ATTR_LANG_CD = "langCd";
        public static final String ATTR_TXT = "txt";
		
	 	@Id
	    @Column(name = "USER_TYPE_CD")
	    private Long userTypeCd;
	    
	    @Column(name="FUNCTION_ID")
	    private Long functionId;
	    
	    @Id
		@Column(name = "LANG_CD")
		private String langCd;
	    
	    @Column(name = "TXT")
	    private String txt;
	    
	    /**
	     * USER_TYPE_CD is NOT nullable
	     * Field length on DB: 2
	     */
		public Long getUserTypeCd() {
			return userTypeCd;
		}
		
	    /**
	     * USER_TYPE_CD is NOT nullable
	     * Field length on DB: 2
	     */
		public void setUserTypeCd(Long userTypeCd) {
			this.userTypeCd = userTypeCd;
		}
		
	    /**
	     * FUNCTION_ID is NOT nullable
	     * Field length on DB: 12
	     * The function needed to create/edit the given usertype
	     */
		public Long getFunctionId() {
			return functionId;
		}
		
	    /**
	     * FUNCTION_ID is NOT nullable
	     * Field length on DB: 12
	     * The function needed to create/edit the given usertype
	     */
		public void setFunctionId(Long functionId) {
			this.functionId = functionId;
		}
		
	    /**
	     * LANG_CD is NOT nullable
	     * Field length on DB: 2
	     * Part of PK. FK to TLGNR_LANG_BS.
	     */
		public String getLangCd() {
			return langCd;
		}

	    /**
	     * LANG_CD is NOT nullable
	     * Field length on DB: 2
	     * Part of PK. FK to TLGNR_LANG_BS.
	     */
		public void setLangCd(String langCd) {
			this.langCd = langCd;
		}
		
	    /**
	     * TXT is NOT nullable
	     * Field length on DB: 30
	     * Description of the status in the language LANG_CD.
	     */
		public String getTxt() {
			return txt;
		}
		
	    /**
	     * TXT is NOT nullable
	     * Field length on DB: 30
	     * Description of the status in the language LANG_CD.
	     */
		public void setTxt(String txt) {
			this.txt = txt;
		}
		
	    public String toString() {
	        ToStringBuilder tsb = new ToStringBuilder(this);
	        tsb.append(super.toString());
	        if (LOG_ENTITIES.isDebugEnabled()) {
	            tsb.append("userTypeCd", userTypeCd);
	            tsb.append("functionId", functionId);
	            tsb.append("langCd", langCd);
	            tsb.append("txt", "txt");
	        }
	        else if (LOG_ENTITIES.isInfoEnabled()) {
	            tsb.append("userTypeCd", userTypeCd);
	            tsb.append("functionId", functionId);
	            tsb.append("langCd", langCd);
	        }
	        return tsb.toString();
	    }
}
