/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.pages;

import java.util.Properties;

import org.openqa.selenium.WebDriver;

/**
 * @author AdNovum Informatik AG
 */
public class ShiptoSaleAreaTabServiceMaterialTemplate extends ShiptoSaleAreaTab {

	/**
	 * @param driver
	 */
	public ShiptoSaleAreaTabServiceMaterialTemplate(WebDriver driver) {
		super(driver);
	}

	@Override
	public void verifyCorrectPage() {
		super.verifyCorrectPage();
	}

	/**
	 * Create new service material only template in master data with some information in properties file
	 * @param properties the service material only template information to create
	 */
	public void createNewServiceMaterialOnlyTemplate(Properties properties) {
		this.searchMaterial(properties.getProperty("service.material.materialName"),
				properties.getProperty("service.material.materialContact"));
		this.selectExtJsCombo(ExtJsCombo.PRODUCTION_PLANT,
				properties.getProperty("service.material.productionPlant"));
		this.selectExtJsCombo(ExtJsCombo.DELIVERY_TYPE,
				properties.getProperty("service.material.deliveryType"));
		this.selectExtJsComboWithStringValue(ExtJsCombo.UOM,
				properties.getProperty("service.material.uom"));
		this.selectExtJsCombo(ExtJsCombo.BILL_TO, properties.getProperty("service.material.billTo"));
		this.selectExtJsCombo(ExtJsCombo.PAYER, properties.getProperty("service.material.payer"));
		this.inputTextField(
				Template_Element.QUANLITY.getHtmlTagId() + this.getOrderTemplateNumberInList(),
				properties.getProperty("service.material.quanlity"));
		this.saveSetting();
	}
}
