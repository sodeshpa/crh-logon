/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.forms.dispokarte.AnsprechpartnerEditForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoPersonEntity;
import com.crh.logon.web.utils.Authorization;

import flexjson.JSONSerializer;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispokarte_AnsprechpartnerEditForm" 
 *  path="/dispokarte/Ansprechpartner" scope="session"
 *  validate="true" input="DispokarteAnsprechpartnerTile.def"
 *
 * @struts.action-forward name="list" path="DispokarteAnsprechpartnerTile.def"
 * @struts.action-forward name="edit" path="DispokarteAnsprechpartnerEditTile.def"
  */
@AdnTrace
public class AnsprechpartnerAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(AnsprechpartnerAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
		// Get the struts form(s).
		AnsprechpartnerEditForm helpForm = (AnsprechpartnerEditForm) form;
		String referrer = helpForm.getReferrer();
		DispokarteForm dispoForm = null;
		BestellungForm orderForm = null;
		ShiptoEntity shipTo = null;
		boolean isOrderTaking = false;
		boolean forceSave = false;

		Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);

		// --- If called from Dispokarte... ---
		if (AnsprechpartnerEditForm.REFERRER_DISPOKARTE.equals(referrer)) {
			HttpSession session = request.getSession();
			dispoForm = (DispokarteForm) session.getAttribute(DispokarteForm.FORM_NAME);
			shipTo = dispoForm.getShipTo();
		}
		// --- ...or from Order taking ---
		else {
			Authorization.authorize(Authorization.ORDER);
			orderForm = BestellungForm.getSessionInstance(request);
			shipTo = orderForm.getShipTo();
			isOrderTaking = true;
		}

		String forwardName = "list";
		DispokarteServiceLocalInterface localDispokarteService = getServiceLocator().getLocalDispokarteService();

		List<ShiptoPersonEntity> shipToPersons = shipTo.getShiptoPersonList();
		if (request.getParameter("search_ansprechPartner") != null) {
			String searchFirstName = helpForm.getSearchFirstName();
			String searchName = helpForm.getSearchName();
			if (!StringUtils.isEmpty(searchFirstName)
					|| !StringUtils.isEmpty(searchName)) {
				List<PersonEntity> searchResult = localDispokarteService.fetchPerson(
								searchName, searchFirstName);
				helpForm.setSearchResult(searchResult);
			}
			isOrderTaking = false;
			forwardName = "edit";
		}
		else {
			String idParameter = request.getParameter("id");
			if (request.getParameter("add") != null) {
				int index = Integer.valueOf(idParameter);
				List<PersonEntity> searchResult = helpForm.getSearchResult();
				PersonEntity addPerson = searchResult.get(index);
				ShiptoPersonEntity newContactPerson = new ShiptoPersonEntity();
				newContactPerson.setPerson(addPerson);
				newContactPerson.setShipto(shipTo);

				boolean contactPersonExists = false;
				for (ShiptoPersonEntity cp : shipToPersons) {
					PersonEntity person = cp.getPerson();
					Long personId = person.getPersonId();
					Long addPersonId = addPerson.getPersonId();
					if (personId != null && addPersonId != null
							&& personId.equals(addPersonId)) {
						contactPersonExists = true;
						break;
					}
				}

				if (!contactPersonExists) {
					shipToPersons.add(newContactPerson);
					forceSave = true;
				}
			}
			else if (request.getParameter("delete") != null) {
				int index = new Integer(idParameter).intValue();
				PersonEntity person = shipToPersons.get(index).getPerson();
				shipToPersons.remove(index);
				if (person.getPersonId() != null
						&& shipTo.getContactPerson() != null
						&& shipTo.getContactPerson().getPersonId() == person.getPersonId().longValue()) {
					shipTo.setContactPerson(null);
				}
				forceSave = true;
			}
			else if (request.getParameter("save") != null) {
				if (helpForm.isNewContact()) {
					shipToPersons.add(helpForm.getContactperson());
				}
				else {
					shipToPersons.remove(helpForm.getId());
					shipToPersons.add(helpForm.getId(), helpForm.getContactperson());
				}
				forceSave = true;

			}
		}

		// --- Used only by Order Taking ---
		if (isOrderTaking && forceSave) {
			LOG.debug("Saving contact person list data");
			for (ShiptoPersonEntity shiptoPerson : shipToPersons) {
				PersonEntity person = shiptoPerson.getPerson();
				if (person.getPersonId() == null) {
					person.setActiveFlag(true);
					shiptoPerson.setPerson(localDispokarteService.persistPerson(person));
					shiptoPerson.setShipto(shipTo);
				}
				else {
					person = localDispokarteService.mergePerson(person);
				}
				if (person.getPersonId() != null
						&& orderForm.getSelectedContactPersonId() == person.getPersonId().longValue()) {
					shipTo.setContactPerson(person);
				}
			}

			// --- Immediate saving of new contact person data (in contrast to the "lazy" approach of Dispokarte) ---
			shipTo = localDispokarteService.mergeShipto(shipTo, null);
			TimeZone shipToTimeZone = createTimeZoneFor(shipTo);
			orderForm.setShipTo(shipTo, shipToTimeZone);

			// --- Re-serializing the contact person list ---
			Map<String, String> json = orderForm.getJson();
			json.put(
					"contactPersonList",
					new JSONSerializer().exclude("*.class").exclude("*.shipto").serialize(
							shipToPersons));
		}

		return mapping.findForward(forwardName);
	}
}
