/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.railexport;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.crh.logon.order.web.forms.raildispo.DispoForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.crh.logon.business.services.beans.RailDispoExportWagonRow;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ShippingPointServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.business.services.utils.ContainerWrapper;
import com.crh.logon.business.services.utils.LoadWrapper;
import com.crh.logon.business.services.utils.RailAssignmentWrapper;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.WagonWrapper;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoAssignPartListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoRailAssListEntity;
import com.crh.logon.persistence.entities.views.DispoWContainerListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonLoadListEntity;
import com.crh.logon.utils.LogonConversionException;
import javax.ejb.EJB;

/**
 * @author AdNovum Informatik AG
 */
public class RailDataProviderTest {

	private static final boolean RETURNING = true;
	private static final boolean NOT_RETURNING = false;

	@EJB private UserServiceLocalInterface userService;
	@EJB private DispoServiceLocalInterface dispoService;
	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private LoadingServiceLocalInterface loadingService;
	@EJB private ShippingPointServiceLocalInterface shippingPointService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringService;
	private TimeZone userTimeZone;
	private DispoForm dispoForm;
	private Locale userLocale;

	@Before
	public void setup() throws LogonConversionException {

		userTimeZone = mock(TimeZone.class);
		unitOfMeasurementConverterService = mock(UomConverterServiceLocalInterface.class);
		when(
				unitOfMeasurementConverterService.convert(org.mockito.Matchers.<BigDecimal> anyObject(),
						Matchers.anyString(), Matchers.anyString(), Matchers.anyLong(),
						Matchers.anyLong(), Matchers.anyLong(), Matchers.anyBoolean()))
				.thenAnswer(new Answer<UomConversionResult>() {
					@Override
					public UomConversionResult answer(InvocationOnMock invocation) throws Throwable {

						Object[] args = invocation.getArguments();

						BigDecimal quantity = (BigDecimal) args[0];
						Long numerator = 1L;
						Long denominator = 1L;

						UomConversionResult ucr = new UomConversionResult(quantity, numerator, denominator);
						return ucr;

					}
				});
	}

	/**
	The two containers, each 2 tons of tare, each with a different material, on a wagon with tare of 20 tons.

	The tare to be printed is the weight of the empty wagon plus the weight of both empty containers. 
	The net is the net of each material. 
	The gross is the tare plus the net of both materials

	Wagon	 				Container	 Material	 			Tare/t	Net/t	 	Gross/t
	3185 4552 175-5	IAG-C651	 Portland cement	24	 		20	 		62
	3185 4552 175-5	F-C19	 	Roman cement	 	24	 		18	 		62
	 */
	@Test
	public void testContainerCase() {

		String wagonNo = UUID.randomUUID().toString();

		List<WagonWrapper> wagons = new ArrayList<WagonWrapper>();

		DispoWagonListEntity wagon = new DispoWagonListEntity();
		wagon.setTareWeightT(new BigDecimal("20"));
		wagon.setWagonNo(wagonNo);
		wagon.setWagonTrspCatCd(Constant.WAGON_TKATEGORIE_CONTAINER);

		DispoWContainerListEntity container1 = new DispoWContainerListEntity();
		container1.setTareWeightT(new BigDecimal("2"));
		container1.setNetQuantity(20D);
		container1.setNetQuantityUomCd(Constant.UOM_TONS);
		ContainerWrapper containerWrapper1 = new ContainerWrapper(container1, unitOfMeasurementConverterService, userTimeZone);

		DispoWContainerListEntity container2 = new DispoWContainerListEntity();
		container2.setTareWeightT(new BigDecimal("2"));
		container2.setNetQuantity(18D);
		container2.setNetQuantityUomCd(Constant.UOM_TONS);
		ContainerWrapper containerWrapper2 = new ContainerWrapper(container2, unitOfMeasurementConverterService, userTimeZone);

		WagonWrapper wagonWrapper = new WagonWrapper(wagon , NOT_RETURNING, unitOfMeasurementConverterService, userTimeZone);
		wagonWrapper.setDeterminedStatus(Constant.WAGON_STATUS_AT_SHIPPINGPOINT);
		wagonWrapper.addContainer(containerWrapper1);
		wagonWrapper.addContainer(containerWrapper2);
		wagons.add(wagonWrapper);

		RailDataProvider provider = new RailDataProvider(userService, dispoService, unitOfMeasurementConverterService, 
				 										 loadingService, shippingPointService, monitoringService,
				 										 userTimeZone, dispoForm, userLocale);
		List<RailDispoExportWagonRow> convertedWagons = provider.convertToWagonRows(wagons);

		Assert.assertEquals("Wagon should generate two rows for the two containers", 2, convertedWagons.size());

		RailDispoExportWagonRow row1 = convertedWagons.get(0);
		RailDispoExportWagonRow row2 = convertedWagons.get(1);

		// Tare
		Assert.assertEquals(new BigDecimal("24"), row1.getLoadTare());
		Assert.assertEquals(new BigDecimal("24"), row2.getLoadTare());
		// Net
		Assert.assertEquals(new BigDecimal("20"), row1.getLoadNet());
		Assert.assertEquals(new BigDecimal("18"), row2.getLoadNet());
		// Gross
		Assert.assertEquals(new BigDecimal("62"), row1.getLoadGross());
		Assert.assertEquals(new BigDecimal("62"), row2.getLoadGross());

	}

	/**
	The items with a different material, on a wagon with tare of 20 tons.

	The tare to be printed is the weight of the empty wagon. 
	The net is the net of each material. 
	The gross is the tare plus the net of both materials

	Wagon	 				Container	 Material	 			Tare/t	Net/t	 	Gross/t
	3185 4552 175-5	-				Portland cement	20	 		6	 		43
	3185 4552 175-5	-				Roman cement	 	20	 		17	 		43
	 */
	@Test
	public void testLoadCase() {

		String wagonNo = UUID.randomUUID().toString();

		List<WagonWrapper> wagons = new ArrayList<WagonWrapper>();

		DispoWagonListEntity wagon = new DispoWagonListEntity();
		wagon.setTareWeightT(new BigDecimal("20"));
		wagon.setWagonNo(wagonNo);
		wagon.setWagonTrspCatCd(Constant.WAGON_TKATEGORIE_BAG);

		List<LoadWrapper> loads = new ArrayList<LoadWrapper>();

		DispoWagonLoadListEntity load1 = new DispoWagonLoadListEntity();
		load1.setNetQuantity(new BigDecimal("6"));
		load1.setNetQuantityUomCd(Constant.UOM_TONS);
		LoadWrapper loadWrapper1 = new LoadWrapper(load1, unitOfMeasurementConverterService);

		DispoWagonLoadListEntity load2 = new DispoWagonLoadListEntity();
		load2.setNetQuantity(new BigDecimal("17"));
		load2.setNetQuantityUomCd(Constant.UOM_TONS);
		LoadWrapper loadWrapper2 = new LoadWrapper(load2, unitOfMeasurementConverterService);

		loads.add(loadWrapper1);
		loads.add(loadWrapper2);

		WagonWrapper wagonWrapper = new WagonWrapper(wagon , NOT_RETURNING, unitOfMeasurementConverterService, userTimeZone);
		wagonWrapper.setDeterminedStatus(Constant.WAGON_STATUS_AT_SHIPPINGPOINT);
		wagonWrapper.setLoads(loads);
		wagons.add(wagonWrapper);

		RailDataProvider provider = new RailDataProvider(userService, dispoService, unitOfMeasurementConverterService, 
				 										 loadingService, shippingPointService, monitoringService,
				 										 userTimeZone, dispoForm, userLocale);
		List<RailDispoExportWagonRow> convertedWagons = provider.convertToWagonRows(wagons);

		Assert.assertEquals("Wagon should generate two rows for the two items", 2, convertedWagons.size());

		RailDispoExportWagonRow row1 = convertedWagons.get(0);
		RailDispoExportWagonRow row2 = convertedWagons.get(1);

		// Tare
		Assert.assertEquals(new BigDecimal("20"), row1.getLoadTare());
		Assert.assertEquals(new BigDecimal("20"), row2.getLoadTare());
		// Net
		Assert.assertEquals(new BigDecimal("6"), row1.getLoadNet());
		Assert.assertEquals(new BigDecimal("17"), row2.getLoadNet());
		// Gross
		Assert.assertEquals(new BigDecimal("43"), row1.getLoadGross());
		Assert.assertEquals(new BigDecimal("43"), row2.getLoadGross());

	}

	/**
	The items with a different material, on a wagon with tare of 20 tons.

	The tare to be printed is the weight of the empty wagon. 
	The net is the net of each material. 
	The gross is the tare plus the net of both materials

	Wagon	 				Container	 Material	 			Tare/t	Net/t	 	Gross/t
	3185 4552 175-5	-				Portland cement	20	 		5	 		43
	3185 4552 175-5	-				Roman cement	 	20	 		18	 		43
	 */
	@Test
	public void testWagonCase() {

		String wagonNo = UUID.randomUUID().toString();

		List<WagonWrapper> wagons = new ArrayList<WagonWrapper>();

		DispoWagonListEntity wagon = new DispoWagonListEntity();
		wagon.setTareWeightT(new BigDecimal("20"));
		wagon.setWagonNo(wagonNo);
		wagon.setWagonTrspCatCd(Constant.WAGON_TKATEGORIE_BAG);

		DispoOrderItemListEntity orderItem1 = new DispoOrderItemListEntity();
		orderItem1.setLoadedQuantity(new BigDecimal("5"));
		orderItem1.setMaterialPackageCd(Constant.MATERIAL_PACKAGE_BAG);

		DispoOrderItemListEntity orderItem2 = new DispoOrderItemListEntity();
		orderItem2.setLoadedQuantity(new BigDecimal("18"));
		orderItem2.setMaterialPackageCd(Constant.MATERIAL_PACKAGE_BAG);

		DispoRailAssListEntity assignment = new DispoRailAssListEntity();
		RailAssignmentWrapper railAssignment = new RailAssignmentWrapper(assignment , userTimeZone);
		DispoAssignPartListEntity unloadAssignmentPart = new DispoAssignPartListEntity();
		unloadAssignmentPart.setAssignmentPartTypeCd(Constant.ASSIGNMENT_PART_TYPE_UNLOAD);
		railAssignment.addPart(unloadAssignmentPart);
		railAssignment.addItem(orderItem1);
		railAssignment.addItem(orderItem2);

		WagonWrapper wagonWrapper = new WagonWrapper(wagon , NOT_RETURNING, unitOfMeasurementConverterService, userTimeZone);
		wagonWrapper.setDeterminedStatus(Constant.WAGON_STATUS_AT_SHIPPINGPOINT);
		wagonWrapper.setAssignment(railAssignment);
		wagonWrapper.initPossibleMaterialList();
		wagons.add(wagonWrapper);

		RailDataProvider provider = new RailDataProvider(userService, dispoService, unitOfMeasurementConverterService, 
				 										 loadingService, shippingPointService, monitoringService,
				 										 userTimeZone, dispoForm, userLocale);
		List<RailDispoExportWagonRow> convertedWagons = provider.convertToWagonRows(wagons);

		Assert.assertEquals("Wagon should generate two rows for the two items", 2, convertedWagons.size());

		RailDispoExportWagonRow row1 = convertedWagons.get(0);
		RailDispoExportWagonRow row2 = convertedWagons.get(1);

		// Tare
		Assert.assertEquals(new BigDecimal("20"), row1.getLoadTare());
		Assert.assertEquals(new BigDecimal("20"), row2.getLoadTare());
		// Net
		Assert.assertEquals(new BigDecimal("5"), row1.getLoadNet());
		Assert.assertEquals(new BigDecimal("18"), row2.getLoadNet());
		// Gross
		Assert.assertEquals(new BigDecimal("43"), row1.getLoadGross());
		Assert.assertEquals(new BigDecimal("43"), row2.getLoadGross());

	}

}
