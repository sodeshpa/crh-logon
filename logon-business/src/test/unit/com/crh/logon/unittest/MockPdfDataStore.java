/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.unittest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.pdftool.labelprovider.DTOMarker;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;

/**
 * @author AdNovum Informatik AG
 */
public class MockPdfDataStore extends AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> {
	
	Map<Class, List<DTOMarker>> dtoMap = new HashMap<Class, List<DTOMarker>>();

	/**
	 * @param locale
	 */
	public MockPdfDataStore(Locale locale) {
		super(locale);
	}

	/* (non-Javadoc)
	 * @see AbstractPdfDataStore#initializeLabels()
	 */
	@Override
	protected void initializeLabels() {
		for (DeliveryNoteLabel label : DeliveryNoteLabel.values()) {
			addLabel(label, Message.lookup(locale, label.getMessageId()));
		}
	}

	/* (non-Javadoc)
	 * @see AbstractPdfDataStore#getDTOList(java.lang.Class)
	 */
	@Override
	public <E extends DTOMarker> List<E> getDTOList(Class<E> clazz) {
		if (!dtoMap.containsKey(clazz)) {
			dtoMap.put(clazz, new ArrayList<DTOMarker>());
		}
		return (List<E>) dtoMap.get(clazz);
	}

}
