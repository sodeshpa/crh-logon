/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.common.builders;

import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapTrspModeBsEntity;
import com.crh.logon.constants.Constant;

public class DeliveryTypeBsEntityBuilder {

	private Long deliveryTypeCd;

	private SapTrspModeBsEntity sapTrspMode;

	private DeliveryTypeBsEntity dependsOnDeliveryType;

	private DeliveryTypeBsEntityBuilder() {
	}

	public static DeliveryTypeBsEntityBuilder get() {
		return new DeliveryTypeBsEntityBuilder();
	}

	public void reset() {
		deliveryTypeCd = null;
		sapTrspMode = null;
		dependsOnDeliveryType = null;
	}

	public DeliveryTypeBsEntity build() {
		DeliveryTypeBsEntity deliveryType = new DeliveryTypeBsEntity();
		deliveryType.setDeliveryTypeCd(deliveryTypeCd);
		deliveryType.setSapTrspMode(sapTrspMode);
		deliveryType.setDependsOnDeliveryType(dependsOnDeliveryType);
		return deliveryType;
	}

	/**
	{@linkplain Constant#DELIVERY_TYPE_ROAD_DIRECT}
	{@linkplain Constant#DELIVERY_TYPE_RAIL_DIRECT}
	{@linkplain Constant#DELIVERY_TYPE_BIMODAL_RAIL}
	{@linkplain Constant#DELIVERY_TYPE_BIMODAL_ROAD}
	{@linkplain Constant#DELIVERY_TYPE_PICKUP}
	{@linkplain Constant#DELIVERY_TYPE_BLOCK_TRAIN}
	{@linkplain Constant#DELIVERY_TYPE_SHIP}
	{@linkplain Constant#DELIVERY_TYPE_SERVICE}
	{@linkplain Constant#DELIVERY_TYPE_RETURN_PICKUP}
	{@linkplain Constant#DELIVERY_TYPE_RETURN_DELIVERED}
	{@linkplain Constant#DELIVERY_TYPE_SHIP_PICKUP}
	{@linkplain Constant#DELIVERY_TYPE_BLOCKTRAIN_PICKUP}
	*/
	public DeliveryTypeBsEntityBuilder deliveryTypeCd(Long deliveryTypeCd) {
		this.deliveryTypeCd = deliveryTypeCd;
		return this;
	}

	public DeliveryTypeBsEntityBuilder sapTrspMode(SapTrspModeBsEntity sapTrspMode) {
		this.sapTrspMode = sapTrspMode;
		return this;
	}

	public DeliveryTypeBsEntityBuilder dependsOnDeliveryType(
			DeliveryTypeBsEntity dependsOnDeliveryType) {
		this.dependsOnDeliveryType = dependsOnDeliveryType;
		return this;
	}

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_ROAD_DIRECT = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_ROAD_DIRECT);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_RAIL_DIRECT = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_RAIL_DIRECT);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_BIMODAL_RAIL = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_BIMODAL_RAIL);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_BIMODAL_ROAD = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_BIMODAL_ROAD);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_PICKUP = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_PICKUP);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_BLOCK_TRAIN = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_BLOCK_TRAIN);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_SHIP = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_SHIP);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_SERVICE = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_SERVICE);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_RETURN_PICKUP = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_RETURN_PICKUP);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_RETURN_DELIVERED = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_RETURN_DELIVERED);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_SHIP_PICKUP = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_SHIP_PICKUP);

	public static DeliveryTypeBsEntityBuilder DELIVERY_TYPE_BLOCKTRAIN_PICKUP = DeliveryTypeBsEntityBuilder.get().deliveryTypeCd(
			Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP);

}
