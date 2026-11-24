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
 public final class Person implements java.io.Serializable {

    // Fields
    private java.lang.Long personId;
    private java.lang.String ctlCreUid;
    private java.util.Date ctlCreDat;
    private java.lang.String ctlModUid;
    private java.util.Date ctlModDat;
    private java.lang.String name;
    private java.lang.String vorname;
    private java.lang.Integer tcn;
    private Adresse adresse;

    // Fieldnames
    public static final String ATTR_PERSONID = "personId";
    public static final String ATTR_CTLCREUID = "ctlCreUid";
    public static final String ATTR_CTLCREDAT = "ctlCreDat";
    public static final String ATTR_CTLMODUID = "ctlModUid";
    public static final String ATTR_CTLMODDAT = "ctlModDat";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_VORNAME = "vorname";
    public static final String ATTR_TCN = "tcn";
    public static final String ATTR_ADRESSE = "adresse";

    // Field length on DB (extracted from sql-type from hibernate mapping)
    public static final long DBSIZE_CTLCREUID = 30;
    public static final long DBSIZE_CTLMODUID = 30;
    public static final long DBSIZE_NAME = 40;
    public static final long DBSIZE_VORNAME = 40;

    // Constructors
    /** default constructor */
    public Person() { /* empty code block */ }

    /** full constructor */
    /** TODO correct javadoc **/
    public Person(
     java.lang.String ctlCreUid
      , java.util.Date ctlCreDat
      , java.lang.String ctlModUid
      , java.util.Date ctlModDat
      , java.lang.String name
      , java.lang.String vorname
      , java.lang.Integer tcn
      , Adresse adresse
    ) {
        this.ctlCreUid = ctlCreUid;
        this.ctlCreDat = ctlCreDat;
        this.ctlModUid = ctlModUid;
        this.ctlModDat = ctlModDat;
        this.name = name;
        this.vorname = vorname;
        this.tcn = tcn;
        this.adresse = adresse;
    }

    /** minimal constructor */
    public Person(
     java.lang.String ctlCreUid
      , java.util.Date ctlCreDat
      , java.lang.String name
      , java.lang.String vorname
      , Adresse adresse
    ) {
        this.ctlCreUid = ctlCreUid;
        this.ctlCreDat = ctlCreDat;
        this.name = name;
        this.vorname = vorname;
        this.adresse = adresse;
    }

    /** Property accessors */
    /** TODO javadoc without warnings **/
    /**
     * getPersonId()
     * Primary Key
     */
    public java.lang.Long getPersonId() {
        return this.personId;
    }

    public String getWebPersonId() {
        if (this.personId == null) {
            return "";
        }
        return this.personId+"";
    }



    protected void setPersonId(java.lang.Long personId) {
        this.personId = personId;
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
     * getName()
     * Name
     *
     */
    public java.lang.String getName() {
        return this.name;
    }

    public String getWebName() {
        if (this.name == null) {
            return "";
        }
        return this.name+"";
    }



    public void setName(java.lang.String name) {
        if (name != null) {
            this.name = name.trim();
        }
    }
    /**
     * getVorname()
     * Vorname
     *
     */
    public java.lang.String getVorname() {
        return this.vorname;
    }

    public String getWebVorname() {
        if (this.vorname == null) {
            return "";
        }
        return this.vorname+"";
    }



    public void setVorname(java.lang.String vorname) {
        if (vorname != null) {
            this.vorname = vorname.trim();
        }
    }
    public java.lang.Integer getTcn() {
        return this.tcn;
    }




    public void setTcn(java.lang.Integer tcn) {
        this.tcn = tcn;
    }
    /**
     * getAdresse()
     * FK Adresse
     *
     */
    public Adresse getAdresse() {
        return this.adresse;
    }

    public String getWebAdresse() {
        if (this.adresse == null) {
            return "";
        }
        return this.adresse+"";
    }



    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public String toString() {
        ToStringBuilder tbs = new ToStringBuilder(this);

        // prevent hibernate from lazy loading collections here

       tbs.append(super.toString());

        if ( !((Object)personId instanceof Collection) && !((Object)personId instanceof Map) ) {
            tbs.append("personId", getPersonId());
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
        if ( !((Object)name instanceof Collection) && !((Object)name instanceof Map) ) {
            tbs.append("name", getName());
        }
        if ( !((Object)vorname instanceof Collection) && !((Object)vorname instanceof Map) ) {
            tbs.append("vorname", getVorname());
        }
        if ( !((Object)tcn instanceof Collection) && !((Object)tcn instanceof Map) ) {
            tbs.append("tcn", getTcn());
        }
        if ( !((Object)adresse instanceof Collection) && !((Object)adresse instanceof Map) ) {
            tbs.append("adresse", getAdresse());
        }
        return tbs.toString();
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Person)) return false;
        boolean ret = false;
        Person x = (Person)o;
        if ( true
            && (this.ctlCreUid == null ? (x.ctlCreUid == null ? true : false) : (this.ctlCreUid.equals(x.ctlCreUid)))
            && (this.ctlCreDat == null ? (x.ctlCreDat == null ? true : false) : (this.ctlCreDat.equals(x.ctlCreDat)))
            && (this.ctlModUid == null ? (x.ctlModUid == null ? true : false) : (this.ctlModUid.equals(x.ctlModUid)))
            && (this.ctlModDat == null ? (x.ctlModDat == null ? true : false) : (this.ctlModDat.equals(x.ctlModDat)))
            && (this.name == null ? (x.name == null ? true : false) : (this.name.equals(x.name)))
            && (this.vorname == null ? (x.vorname == null ? true : false) : (this.vorname.equals(x.vorname)))
            && (this.adresse == null ? (x.adresse == null ? true : false) : (this.adresse.equals(x.adresse)))
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
        ret = 37*ret + (name == null ? 0 : name.hashCode());
        ret = 37*ret + (vorname == null ? 0 : vorname.hashCode());
        ret = 37*ret + (adresse == null ? 0 : adresse.hashCode());
          return ret;
     }

}
