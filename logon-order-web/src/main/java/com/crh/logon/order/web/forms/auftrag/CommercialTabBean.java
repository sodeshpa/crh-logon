/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class CommercialTabBean implements Serializable{

	private static final long serialVersionUID = 1L;
	//private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CommercialTabBean.class);

	private String currencyCd;
	private String netpriceQuantity;
	private String grossPrice;
    private String grossPricePt;
    private String grossPricePer;
    private String discounts;
    private String discountsPt;
    private String discountsPer;
    private String netPrice;
    private String netPricePt;    
    private String netPricePer;
    private String rebates;
    private String rebatesPt;
    private String rebatesPer;
    private String cashDiscount;
    private String cashDiscountPt;
    private String cashDiscountPer;
    private String netSalesEarining;
    private String netSalesEariningPt;
    private String netSalesEariningPer;
    private String prodCost;
    private String prodCostPt;
    private String prodCostPer;
    private String iudCost;
    private String iudCostPt;
    private String iudCostPer;
    private String handlingCost;
    private String handlingCostPt;
    private String handlingCostPer;
    private String outCost;
    private String outCostPt;
    private String outCostPer;
    private String totalDistCost;
    private String totalDistCostPt;
    private String totalDistCostPer;
    private String totalCost;
    private String totalCostPt;
    private String totalCostPer;
    private String contribMargin;
    private String contribMarginPt;
    private String contribMarginPer;	



    public String getNetpriceQuantity() {
    	return netpriceQuantity;
    }
    public void setNetpriceQuantity(String netpriceQuantity) {
    	this.netpriceQuantity = netpriceQuantity;
    }
    public String getCurrencyCd() {
		return currencyCd;
	}
	public void setCurrencyCd(String currencyCd) {
		this.currencyCd = currencyCd;
	}
	public String getGrossPrice() {
		return grossPrice;
	}
	public void setGrossPrice(String grossPrice) {
		this.grossPrice = grossPrice;
	}
	public String getGrossPricePt() {
		return grossPricePt;
	}
	public void setGrossPricePt(String grossPricePt) {
		this.grossPricePt = grossPricePt;
	}
	public String getGrossPricePer() {
		return grossPricePer;
	}
	public void setGrossPricePer(String grossPricePer) {
		this.grossPricePer = grossPricePer;
	}
	public String getDiscounts() {
		return discounts;
	}
	public void setDiscounts(String discounts) {
		this.discounts = discounts;
	}
	public String getDiscountsPt() {
		return discountsPt;
	}
	public void setDiscountsPt(String discountsPt) {
		this.discountsPt = discountsPt;
	}
	public String getDiscountsPer() {
		return discountsPer;
	}
	public void setDiscountsPer(String discountsPer) {
		this.discountsPer = discountsPer;
	}
	public String getNetPrice() {
		return netPrice;
	}
	public void setNetPrice(String netPrice) {
		this.netPrice = netPrice;
	}
	public String getNetPricePt() {
		return netPricePt;
	}
	public void setNetPricePt(String netPricePt) {
		this.netPricePt = netPricePt;
	}
	public String getNetPricePer() {
		return netPricePer;
	}
	public void setNetPricePer(String netPricePer) {
		this.netPricePer = netPricePer;
	}
	public String getRebates() {
		return rebates;
	}
	public void setRebates(String rebates) {
		this.rebates = rebates;
	}
	public String getRebatesPt() {
		return rebatesPt;
	}
	public void setRebatesPt(String rebatesPt) {
		this.rebatesPt = rebatesPt;
	}
	public String getRebatesPer() {
		return rebatesPer;
	}
	public void setRebatesPer(String rebatesPer) {
		this.rebatesPer = rebatesPer;
	}
	public String getCashDiscount() {
		return cashDiscount;
	}
	public void setCashDiscount(String cashDiscount) {
		this.cashDiscount = cashDiscount;
	}
	public String getCashDiscountPt() {
		return cashDiscountPt;
	}
	public void setCashDiscountPt(String cashDiscountPt) {
		this.cashDiscountPt = cashDiscountPt;
	}
	public String getCashDiscountPer() {
		return cashDiscountPer;
	}
	public void setCashDiscountPer(String cashDiscountPer) {
		this.cashDiscountPer = cashDiscountPer;
	}
	public String getNetSalesEarining() {
		return netSalesEarining;
	}
	public void setNetSalesEarining(String netSalesEarining) {
		this.netSalesEarining = netSalesEarining;
	}
	public String getNetSalesEariningPt() {
		return netSalesEariningPt;
	}
	public void setNetSalesEariningPt(String netSalesEariningPt) {
		this.netSalesEariningPt = netSalesEariningPt;
	}
	public String getNetSalesEariningPer() {
		return netSalesEariningPer;
	}
	public void setNetSalesEariningPer(String netSalesEariningPer) {
		this.netSalesEariningPer = netSalesEariningPer;
	}
	public String getProdCost() {
		return prodCost;
	}
	public void setProdCost(String prodCost) {
		this.prodCost = prodCost;
	}
	public String getProdCostPt() {
		return prodCostPt;
	}
	public void setProdCostPt(String prodCostPt) {
		this.prodCostPt = prodCostPt;
	}
	public String getProdCostPer() {
		return prodCostPer;
	}
	public void setProdCostPer(String prodCostPer) {
		this.prodCostPer = prodCostPer;
	}
	public String getIudCost() {
		return iudCost;
	}
	public void setIudCost(String iudCost) {
		this.iudCost = iudCost;
	}
	public String getIudCostPt() {
		return iudCostPt;
	}
	public void setIudCostPt(String iudCostPt) {
		this.iudCostPt = iudCostPt;
	}
	public String getIudCostPer() {
		return iudCostPer;
	}
	public void setIudCostPer(String iudCostPer) {
		this.iudCostPer = iudCostPer;
	}
	public String getHandlingCost() {
		return handlingCost;
	}
	public void setHandlingCost(String handlingCost) {
		this.handlingCost = handlingCost;
	}
	public String getHandlingCostPt() {
		return handlingCostPt;
	}
	public void setHandlingCostPt(String handlingCostPt) {
		this.handlingCostPt = handlingCostPt;
	}
	public String getHandlingCostPer() {
		return handlingCostPer;
	}
	public void setHandlingCostPer(String handlingCostPer) {
		this.handlingCostPer = handlingCostPer;
	}
	public String getOutCost() {
		return outCost;
	}
	public void setOutCost(String outCost) {
		this.outCost = outCost;
	}
	public String getOutCostPt() {
		return outCostPt;
	}
	public void setOutCostPt(String outCostPt) {
		this.outCostPt = outCostPt;
	}
	public String getOutCostPer() {
		return outCostPer;
	}
	public void setOutCostPer(String outCostPer) {
		this.outCostPer = outCostPer;
	}
	public String getTotalDistCost() {
		return totalDistCost;
	}
	public void setTotalDistCost(String totalDistCost) {
		this.totalDistCost = totalDistCost;
	}
	public String getTotalDistCostPt() {
		return totalDistCostPt;
	}
	public void setTotalDistCostPt(String totalDistCostPt) {
		this.totalDistCostPt = totalDistCostPt;
	}
	public String getTotalDistCostPer() {
		return totalDistCostPer;
	}
	public void setTotalDistCostPer(String totalDistCostPer) {
		this.totalDistCostPer = totalDistCostPer;
	}
	public String getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(String totalCost) {
		this.totalCost = totalCost;
	}
	public String getTotalCostPt() {
		return totalCostPt;
	}
	public void setTotalCostPt(String totalCostPt) {
		this.totalCostPt = totalCostPt;
	}
	public String getTotalCostPer() {
		return totalCostPer;
	}
	public void setTotalCostPer(String totalCostPer) {
		this.totalCostPer = totalCostPer;
	}
	public String getContribMargin() {
		return contribMargin;
	}
	public void setContribMargin(String contribMargin) {
		this.contribMargin = contribMargin;
	}
	public String getContribMarginPt() {
		return contribMarginPt;
	}
	public void setContribMarginPt(String contribMarginPt) {
		this.contribMarginPt = contribMarginPt;
	}
	public String getContribMarginPer() {
		return contribMarginPer;
	}
	public void setContribMarginPer(String contribMarginPer) {
		this.contribMarginPer = contribMarginPer;
	}
}

