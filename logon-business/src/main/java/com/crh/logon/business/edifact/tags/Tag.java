/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.crh.logon.business.edifact.parser.MsgContext;
import com.crh.logon.business.edifact.parser.Segment;
import com.crh.logon.business.services.utils.EdifactException;
import org.apache.commons.beanutils.BeanUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


// (set[^\(]*\(\R?\s*)DataElementMeta(.*\R\s*[^\s]*)\s=\s([^;]*);
// $1String$2.setValue($3);

// DataElementMeta(\s*get.*\R\s*[^;]*);
// String$1.getValue();

/**
 * @author AdNovum Informatik AG
 */
public abstract class Tag {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(Tag.class);
	private String name;
	private String description;
	private List<Tag> tags;
		
	public Tag (String name, String description) {
		
		this.name = name;
		this.description = description;
	}
	
	public String getDescription() {
		
		return description;
	}
	
	public String getName () {
		
		return name;
	}
	
	@Override
	public String toString() {
		
		return name;
	}
	
	protected abstract DataElementMeta[] getDataElementMetas();
	
	public void parse(Segment segment, MsgContext ctxt) throws EdifactException {
		
		for (DataElementMeta dem: getDataElementMetas()) {
			dem.parse(segment);
		}
		
		if (LOG.isDebugEnabled()) {
			try {
				LOG.debug("parse " + BeanUtils.describe(this));
			} catch (Exception e) {
				LOG.debug("Error describing bean", e);
			}
		}
	}
	
	public List<Tag> getTags() {
		
		if (tags == null) {
			tags = new ArrayList<Tag>();
		}
		
		return tags;
	}
	
	public void write(Writer w) throws IOException {
		
		Segment s = new Segment();
		s.setTagName(getName());
		for (DataElementMeta dem: getDataElementMetas()) {
			dem.applyValue(s);
		}
		s.write(w);
		if (tags != null) {
			for (Tag t: tags) {
				t.write(w);
			}
		}
	}
}
