/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.tablebrowser;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


/**
 * @author AdNovum Informatik AG
 */
@Embeddable
public class ColumnMetaKey implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name = "COLUMN_META_ID")
	private long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "TABLE_META_ID")
	private TableMeta tableMeta;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TableMeta getTableMeta() {
		return tableMeta;
	}

	public void setTableMeta(TableMeta tableMeta) {
		this.tableMeta = tableMeta;
	}

	
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof ColumnMetaKey)) return false;
        boolean ret = false;
        ColumnMetaKey x = (ColumnMetaKey) o;
        if ( true
            && (this.tableMeta == null ? (x.tableMeta == null ? true : false) : (this.tableMeta.equals(x.tableMeta)))
            && this.id == x.id
        )
        {
            ret = true;
        }
        return ret;
    }

    public int hashCode() {
        int ret = 17; // some odd prime
        ret = 37*ret + (tableMeta == null ? 0 : tableMeta.hashCode());
        ret = 37*ret + (int)id;
        return ret;
    }
    
	

}
