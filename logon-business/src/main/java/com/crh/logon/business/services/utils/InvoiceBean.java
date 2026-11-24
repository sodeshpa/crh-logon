/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;

import com.crh.logon.persistence.entities.refdata.UomBsEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * This class is used to store price informations for Cash-Sales 
 * and Export Invoice Delivery Notes.
 */
public class InvoiceBean implements Serializable {
	
	private String currency;
	private BigDecimal netPrice;
	private BigDecimal vatValue;
	private BigDecimal vatRate;
	private BigDecimal grossPrice;
	private BigDecimal netPricePerBaseUoM;
	private UomBsEntity baseUoM;
	private BigDecimal totalNetPrice;
	private BigDecimal netPricePerBaseUomInEur;

	/**
	 * This constructor is used on creating the TOTAL price row for cash sales.
	 * (Does provide net price per base UoM and a base UoM entity.)
	 * @param currency
	 * @param netPrice
	 * @param vatValue
	 * @param vatRate
	 * @param grossPrice
	 */
	public InvoiceBean(String currency, BigDecimal netPrice, BigDecimal vatValue,
			BigDecimal vatRate, BigDecimal grossPrice) {
		
		this(currency, netPrice, vatValue, vatRate, grossPrice, null, null, null, null);
	}
	/**
	 * This constructor is used on creating the price information 
	 * below material lines.
	 * (Does net price per base UoM and a base UoM entity.)
	 * @param currency
	 * @param netPrice
	 * @param vatValue
	 * @param vatRate
	 * @param grossPrice
	 * @param netPricePerBaseUoM
	 * @param baseUoM
	 */
	public InvoiceBean(String currency, BigDecimal netPrice, BigDecimal vatValue,
			BigDecimal vatRate, BigDecimal grossPrice, BigDecimal netPricePerBaseUoM, 
			UomBsEntity baseUoM, BigDecimal totalNetPrice, BigDecimal netPricePerBaseUomInEur) {
		
		this.currency = currency;
		this.netPrice = netPrice;
		this.vatValue = vatValue;
		this.vatRate = vatRate;
		this.grossPrice = grossPrice;
		this.netPricePerBaseUoM = netPricePerBaseUoM;
		this.baseUoM = baseUoM;
		this.totalNetPrice = totalNetPrice;
		this.netPricePerBaseUomInEur = netPricePerBaseUomInEur;
	}
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public BigDecimal getNetPrice() {
		return netPrice;
	}
	
	public void setNetPrice(BigDecimal netPrice) {
		this.netPrice = netPrice;
	}
	
	public BigDecimal getVatValue() {
		return vatValue;
	}
	
	public void setVatValue(BigDecimal vatValue) {
		this.vatValue = vatValue;
	}

	public BigDecimal getVatRate() {
		return vatRate;
	}

	public void setVatRate(BigDecimal vatRate) {
		this.vatRate = vatRate;
	}
	
	public BigDecimal getGrossPrice() {
		return grossPrice;
	}
	
	public void setGrossPrice(BigDecimal grossPrice) {
		this.grossPrice = grossPrice;
	}

	public BigDecimal getNetPricePerBaseUoM() {
		return netPricePerBaseUoM;
	}

	public void setNetPricePerBaseUoM(BigDecimal netPricePerBaseUoM) {
		this.netPricePerBaseUoM = netPricePerBaseUoM;
	}

	public UomBsEntity getBaseUoM() {
		return baseUoM;
	}

	public void setBaseUoM(UomBsEntity baseUoM) {
		this.baseUoM = baseUoM;
	}
	public BigDecimal getTotalNetPrice() {
		return totalNetPrice;
	}
	public void setTotalNetPrice(BigDecimal totalNetPrice) {
		this.totalNetPrice = totalNetPrice;
	}
	public BigDecimal getNetPricePerBaseUomInEur() {
		return netPricePerBaseUomInEur;
	}
	public void setNetPricePerBaseUomInEur(BigDecimal netPricePerBaseUomInEur) {
		this.netPricePerBaseUomInEur = netPricePerBaseUomInEur;
	}
}
