/*
 * Author            : AdNovum Informatik AG
 * Version Number    : \$Revision$
 * Date of last edit : \$Date$
 */

package com.crh.logon.cust.web.utils;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author hbm2java
 */
 public final class Adresse implements java.io.Serializable {

    // Fields
    private java.lang.Long adresseId;
    private java.lang.String ctlCreUid;
    private java.util.Date ctlCreDat;
    private java.lang.String ctlModUid;
    private java.util.Date ctlModDat;
    private java.lang.String strasse;
    private java.lang.String hausNr;
    private java.lang.String ort;
    private java.lang.String plz;
    private java.lang.Integer tcn;

    // Fieldnames
    public static final String ATTR_ADRESSEID = "adresseId";
    public static final String ATTR_CTLCREUID = "ctlCreUid";
    public static final String ATTR_CTLCREDAT = "ctlCreDat";
    public static final String ATTR_CTLMODUID = "ctlModUid";
    public static final String ATTR_CTLMODDAT = "ctlModDat";
    public static final String ATTR_STRASSE = "strasse";
    public static final String ATTR_HAUSNR = "hausNr";
    public static final String ATTR_ORT = "ort";
    public static final String ATTR_PLZ = "plz";
    public static final String ATTR_TCN = "tcn";

    // Field length on DB (extracted from sql-type from hibernate mapping)
    public static final long DBSIZE_CTLCREUID = 30;
    public static final long DBSIZE_CTLMODUID = 30;
    public static final long DBSIZE_STRASSE = 40;
    public static final long DBSIZE_HAUSNR = 5;
    public static final long DBSIZE_ORT = 40;
    public static final long DBSIZE_PLZ = 6;

    // Constructors
    /** default constructor */
    public Adresse() { /* empty code block */ }

    /** full constructor */
    /** TODO correct javadoc **/
    public Adresse(
     java.lang.String ctlCreUid
      , java.util.Date ctlCreDat
      , java.lang.String ctlModUid
      , java.util.Date ctlModDat
      , java.lang.String strasse
      , java.lang.String hausNr
      , java.lang.String ort
      , java.lang.String plz
      , java.lang.Integer tcn
    ) {
        this.ctlCreUid = ctlCreUid;
        this.ctlCreDat = ctlCreDat;
        this.ctlModUid = ctlModUid;
        this.ctlModDat = ctlModDat;
        this.strasse = strasse;
        this.hausNr = hausNr;
        this.ort = ort;
        this.plz = plz;
        this.tcn = tcn;
    }

    /** minimal constructor */
    public Adresse(
     java.lang.String ctlCreUid
      , java.util.Date ctlCreDat
    ) {
        this.ctlCreUid = ctlCreUid;
        this.ctlCreDat = ctlCreDat;
    }

    /** Property accessors */
    /** TODO javadoc without warnings **/
    /**
     * getAdresseId()
     * Primary Key
     */
    public java.lang.Long getAdresseId() {
        return this.adresseId;
    }

    public String getWebAdresseId() {
        if (this.adresseId == null) {
            return "";
        }
        return this.adresseId+"";
    }



    protected void setAdresseId(java.lang.Long adresseId) {
        this.adresseId = adresseId;
    }
    /**
     * getCtlCreUid()
     * Technical Field. The user that created the db record
     *
     */
    public java.lang.String getCtlCreUid() {
        return this.ctlCreUid;
    }

    public String getWebCtlCreUid() {
        if (this.ctlCreUid == null) {
            return "";
        }
        return this.ctlCreUid+"";
    }



    public void setCtlCreUid(java.lang.String ctlCreUid) {
        if (ctlCreUid != null) {
            this.ctlCreUid = ctlCreUid.trim();
        }
    }
    /**
     * getCtlCreDat()
     * Technical Field. A timestamp that indicates when a db record has been inserted.
     *
     */
    public java.util.Date getCtlCreDat() {
        return this.ctlCreDat;
    }

    public String getWebCtlCreDat() {
        if (this.ctlCreDat == null) {
            return "";
        }
        return this.ctlCreDat+"";
    }



    public void setCtlCreDat(java.util.Date ctlCreDat) {
        this.ctlCreDat = ctlCreDat;
    }
    /**
     * getCtlModUid()
     * Technical Field. The user that modified the db record
     *
     */
    public java.lang.String getCtlModUid() {
        return this.ctlModUid;
    }

    public String getWebCtlModUid() {
        if (this.ctlModUid == null) {
            return "";
        }
        return this.ctlModUid+"";
    }



    public void setCtlModUid(java.lang.String ctlModUid) {
        if (ctlModUid != null) {
            this.ctlModUid = ctlModUid.trim();
        }
    }
    /**
     * getCtlModDat()
     * Technical Field. A timestamp that indicates when a db record has been modified.
     *
     */
    public java.util.Date getCtlModDat() {
        return this.ctlModDat;
    }

    public String getWebCtlModDat() {
        if (this.ctlModDat == null) {
            return "";
        }
        return this.ctlModDat+"";
    }



    public void setCtlModDat(java.util.Date ctlModDat) {
        this.ctlModDat = ctlModDat;
    }
    /**
     * getStrasse()
     * Strasse
     *
     */
    public java.lang.String getStrasse() {
        return this.strasse;
    }

    public String getWebStrasse() {
        if (this.strasse == null) {
            return "";
        }
        return this.strasse+"";
    }



    public void setStrasse(java.lang.String strasse) {
        if (strasse != null) {
            this.strasse = strasse.trim();
        }
    }
    /**
     * getHausNr()
     * Hausnummer
     *
     */
    public java.lang.String getHausNr() {
        return this.hausNr;
    }

    public String getWebHausNr() {
        if (this.hausNr == null) {
            return "";
        }
        return this.hausNr+"";
    }



    public void setHausNr(java.lang.String hausNr) {
        if (hausNr != null) {
            this.hausNr = hausNr.trim();
        }
    }
    /**
     * getOrt()
     * Ort
     *
     */
    public java.lang.String getOrt() {
        return this.ort;
    }

    public String getWebOrt() {
        if (this.ort == null) {
            return "";
        }
        return this.ort+"";
    }



    public void setOrt(java.lang.String ort) {
        if (ort != null) {
            this.ort = ort.trim();
        }
    }
    /**
     * getPlz()
     * PLZ
     *
     */
    public java.lang.String getPlz() {
        return this.plz;
    }

    public String getWebPlz() {
        if (this.plz == null) {
            return "";
        }
        return this.plz+"";
    }



    public void setPlz(java.lang.String plz) {
        if (plz != null) {
            this.plz = plz.trim();
        }
    }
    public java.lang.Integer getTcn() {
        return this.tcn;
    }




    public void setTcn(java.lang.Integer tcn) {
        this.tcn = tcn;
    }

    public String toString() {
        ToStringBuilder tbs = new ToStringBuilder(this);

        // prevent hibernate from lazy loading collections here

       tbs.append(super.toString());

        if ( !((Object)adresseId instanceof Collection) && !((Object)adresseId instanceof Map) ) {
            tbs.append("adresseId", getAdresseId());
        }
        if ( !((Object)ctlCreUid instanceof Collection) && !((Object)ctlCreUid instanceof Map) ) {
            tbs.append("ctlCreUid", getCtlCreUid());
        }
        if ( !((Object)ctlCreDat instanceof Collection) && !((Object)ctlCreDat instanceof Map) ) {
            tbs.append("ctlCreDat", getCtlCreDat());
        }
        if ( !((Object)ctlModUid instanceof Collection) && !((Object)ctlModUid instanceof Map) ) {
            tbs.append("ctlModUid", getCtlModUid());
        }
        if ( !((Object)ctlModDat instanceof Collection) && !((Object)ctlModDat instanceof Map) ) {
            tbs.append("ctlModDat", getCtlModDat());
        }
        if ( !((Object)strasse instanceof Collection) && !((Object)strasse instanceof Map) ) {
            tbs.append("strasse", getStrasse());
        }
        if ( !((Object)hausNr instanceof Collection) && !((Object)hausNr instanceof Map) ) {
            tbs.append("hausNr", getHausNr());
        }
        if ( !((Object)ort instanceof Collection) && !((Object)ort instanceof Map) ) {
            tbs.append("ort", getOrt());
        }
        if ( !((Object)plz instanceof Collection) && !((Object)plz instanceof Map) ) {
            tbs.append("plz", getPlz());
        }
        if ( !((Object)tcn instanceof Collection) && !((Object)tcn instanceof Map) ) {
            tbs.append("tcn", getTcn());
        }
        return tbs.toString();
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Adresse)) return false;
        boolean ret = false;
        Adresse x = (Adresse)o;
        if ( true
            && (this.ctlCreUid == null ? (x.ctlCreUid == null ? true : false) : (this.ctlCreUid.equals(x.ctlCreUid)))
            && (this.ctlCreDat == null ? (x.ctlCreDat == null ? true : false) : (this.ctlCreDat.equals(x.ctlCreDat)))
            && (this.ctlModUid == null ? (x.ctlModUid == null ? true : false) : (this.ctlModUid.equals(x.ctlModUid)))
            && (this.ctlModDat == null ? (x.ctlModDat == null ? true : false) : (this.ctlModDat.equals(x.ctlModDat)))
            && (this.strasse == null ? (x.strasse == null ? true : false) : (this.strasse.equals(x.strasse)))
            && (this.hausNr == null ? (x.hausNr == null ? true : false) : (this.hausNr.equals(x.hausNr)))
            && (this.ort == null ? (x.ort == null ? true : false) : (this.ort.equals(x.ort)))
            && (this.plz == null ? (x.plz == null ? true : false) : (this.plz.equals(x.plz)))
         )
        {
            ret = true;
        }
        return ret;
    }

    /*
     * hashCode()
     * TODO: review implementation (taken from Effective Java Book)
     */
    public int hashCode() {
           int ret = 17; // some odd prime
        ret = 37*ret + (ctlCreUid == null ? 0 : ctlCreUid.hashCode());
        ret = 37*ret + (ctlCreDat == null ? 0 : ctlCreDat.hashCode());
        ret = 37*ret + (ctlModUid == null ? 0 : ctlModUid.hashCode());
        ret = 37*ret + (ctlModDat == null ? 0 : ctlModDat.hashCode());
        ret = 37*ret + (strasse == null ? 0 : strasse.hashCode());
        ret = 37*ret + (hausNr == null ? 0 : hausNr.hashCode());
        ret = 37*ret + (ort == null ? 0 : ort.hashCode());
        ret = 37*ret + (plz == null ? 0 : plz.hashCode());
          return ret;
     }

}
