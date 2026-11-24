/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.Locale;

import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.ULocale;

/**
 * @author AdNovum Informatik AG
 */
public class LangUtil {

    private static final Locale DEFAULT_LOCALE = new Locale("de");
    
    public static Locale getOrderLocale(OrderEntity order) {
        if ((order == null)
                || (order.getShiptoSapBp() == null)
                || (order.getShiptoSapBp().getLang() == null)) {
            return DEFAULT_LOCALE;
        }
        return new Locale(order.getShiptoSapBp().getLang().getLangCd());
    }
    
    public static Locale getShiptoLocale(ShiptoEntity shipto) {
        if ((shipto == null)
                || (shipto.getShiptoSapBp() == null)
                || (shipto.getShiptoSapBp().getLang() == null)) {
            return DEFAULT_LOCALE;
        }
        return new Locale(shipto.getShiptoSapBp().getLang().getLangCd());
    }
    
    /**
     * Represents the given number as text on a language based on the given locale.
     * Note: This method uses the ICU4J library to represent numbers as text.
     * 
     * @param number A number to represent as text.
     * @param locale A locale to determine the language.
     * 
     * @return null if either number or locale is null. Otherwise returns the 
     * text representation of the number on the appropriate language.
     */
	public static String getNumberAsText(BigDecimal number, Locale locale) {
		String numberAsText = null;

		if (number == null || locale == null) {
			return numberAsText;
		}

		final ULocale to = new ULocale(locale.getLanguage(), locale.getCountry());

		final RuleBasedNumberFormat rbnf = new RuleBasedNumberFormat(to,
				RuleBasedNumberFormat.SPELLOUT);

		numberAsText = rbnf.format(number);

		return numberAsText;
	}
}
