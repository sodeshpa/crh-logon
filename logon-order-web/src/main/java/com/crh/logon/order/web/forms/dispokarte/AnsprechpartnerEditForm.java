/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispokarte;

import java.util.List;

import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.ShiptoPersonEntity;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_dispokarte_AnsprechpartnerEditForm"
 */
@AdnTrace
public class AnsprechpartnerEditForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AnsprechpartnerEditForm.class);

	public static final String REFERRER_DISPOKARTE = "dispokarte";
	public static final String REFERRER_ORDER_TAKING = "order";

	// input from the user
	private int id;
	private String action;
	private String searchName;
	private String searchFirstName;
	private String searchFunction;
	private String referrer;
	private List<PersonEntity> searchResult;
	private boolean newContact;
	// db state
	private ShiptoPersonEntity contactperson;

	/**
	 * Default constructor.
	 */
	public AnsprechpartnerEditForm() {
		init();
	}

	/**
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the order.web form.
	 */
	private void init() {
		id = -1;
		contactperson = new ShiptoPersonEntity();
		contactperson.setPerson(new PersonEntity());
		searchResult = null;
		newContact = false;
		searchFirstName = null;
		searchFunction = null;
		searchName = null;
	}

	public void clear() {
		init();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ShiptoPersonEntity getContactperson() {
		return contactperson;
	}

	public void setContactperson(ShiptoPersonEntity contactperson) {
		this.contactperson = contactperson;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getSearchFirstName() {
		return searchFirstName;
	}

	public void setSearchFirstName(String searchFirstName) {
		this.searchFirstName = searchFirstName;
	}

	public String getSearchFunction() {
		return searchFunction;
	}

	public void setSearchFunction(String searchFunction) {
		this.searchFunction = searchFunction;
	}

	public List<PersonEntity> getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(List<PersonEntity> searchResult) {
		this.searchResult = searchResult;
	}

	public boolean isNewContact() {
		return newContact;
	}

	public void setNewContact(boolean newContact) {
		this.newContact = newContact;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	
}
