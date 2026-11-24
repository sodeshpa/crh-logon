/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import com.crh.logon.business.services.interfaces.SalesAreaService;
import com.crh.logon.business.services.interfaces.SapContractTestDataService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.nevis.gdi.jpa.Authorizer;

import com.crh.logon.arquillian.ArquillianHelper;
import com.crh.logon.arquillian.ArquillianTestData;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;

/**
 * @author AdNovum Informatik AG
 */
@RunWith(Arquillian.class)
public class PlanningContractItemIntegrationTest extends
		BaseServiceIntegrationTest<DispokarteServiceBean> {

	private static final String CONTRACT_SHOULD_NOT_HAVE_INVALID_AGGREGATE_PLANNING_CONTRACT_ITEM = "Contract should not have invalid aggregate planning contract item: %d";
	private static final String CONTRACT_SHOULD_HAVE_VALID_AGGREGATE_PLANNING_CONTRACT_ITEM = "Contract should have valid aggregate planning contract item: %d";
	private static final String CONTRACT_SHOULD_NOT_HAVE_INVALID_LANDFILL_PLANNING_CONTRACT_ITEM = "Contract should not have invalid landfill planning contract item: %d";
	private static final String CONTRACT_SHOULD_HAVE_VALID_LANDFILL_PLANNING_CONTRACT_ITEM = "Contract should have valid landfill planning contract item: %d";
	private static final String CONTRACT_SHOULD_NOT_HAVE_VALID_AGGREGATE_PLANNING_CONTRACT_ITEM = "Contract should not have valid aggregate planning contract item: %d";
	private static final String CONTRACT_SHOULD_HAVE_INVALID_AGGREGATE_PLANNING_CONTRACT_ITEM = "Contract should have invalid aggregate planning contract item: %d";
	private static final String CONTRACT_SHOULD_NOT_HAVE_VALID_LANDFILL_PLANNING_CONTRACT_ITEM = "Contract should not have valid landfill planning contract item: %d";
	private static final String CONTRACT_SHOULD_HAVE_INVALID_LANDFILL_PLANNING_CONTRACT_ITEM = "Contract should have invalid landfill planning contract item: %d";
	private static final String FLAG_IS_ON_AS_STRING = "1";
	private static final String MISSING_TEST_DATA = "Test data is missing and has to be (re)imported!";

	static final String TEST_SALES_AREA_DISPLAY_LONG_NAME = "AD03-20-30";

	private static final String INCORRECT_ALLOW_PLANNING_MATERIAL_FLAG_OF_SALES_AREA = "Allow planning material flag of sales area is incorrect: "
			+ TEST_SALES_AREA_DISPLAY_LONG_NAME;


	public enum PlanningMaterialSupportType {
		NOT_SUPPORTED, DISABLED, ENABLED
	}

	public enum PlanningContractItemType {
		VALID_LANDFILL_PLANNING, INVALID_LANDFILL_PLANNING, VALID_AGGREGATE_PLANNING, INVALID_AGGREGATE_PLANNING, NON_PLANNING,
	}

	static final Authorizer DO_NOT_AUTHORIZE = null;

	@EJB
	SalesAreaService salesAreaService;

	@Inject
	SapContractTestDataService sapContractTestDataService;
	
	@Deployment
	@TargetsContainer("jbossas_remote")
	public static WebArchive createDeployment() {
		PlanningContractItemIntegrationTest instance = new PlanningContractItemIntegrationTest();
		ArquillianTestData testData = instance.prepareTestData();
		WebArchive webArchive = ArquillianHelper.createWebArchive(testData);
		return webArchive;
	}

	@Test
	public void testPlanningContractItemMaintanence() {
		setPlanningMaterialSupport(Constant.OFF);
		validateSalesArea(PlanningMaterialSupportType.NOT_SUPPORTED);
		setPlanningMaterialSupport(Constant.ON);
		validateSalesArea(PlanningMaterialSupportType.ENABLED);
		setPlanningMaterialSupport(Constant.OFF);
		validateSalesArea(PlanningMaterialSupportType.DISABLED);
		sapContractTestDataService.deleteContractsFromSalesArea(TEST_SALES_AREA_DISPLAY_LONG_NAME);
	}

	private void validateSalesArea(PlanningMaterialSupportType planningMaterialSupportType) {
		SareaEntity testSalesArea = salesAreaService.findSalesAreaByDisplayName(TEST_SALES_AREA_DISPLAY_LONG_NAME);
		validateAllowPlanningMaterialFlag(planningMaterialSupportType, testSalesArea);

		List<SapContractEntity> contracts = sapContractTestDataService.getTestContractsBySalesArea(TEST_SALES_AREA_DISPLAY_LONG_NAME);

		boolean hasAtLeastTwoContracts = contracts.size() > 1;
		Assert.assertTrue(MISSING_TEST_DATA, hasAtLeastTwoContracts);

		for (SapContractEntity contract : contracts) {
			validateContract(planningMaterialSupportType, contract);
		}
	}

	public void validateContract(PlanningMaterialSupportType planningMaterialSupportType,
			SapContractEntity contract) {
		List<SapContractItemEntity> contractItems = contract.getSapContractItems();
		List<PlanningContractItemType> contractItemTypes = new ArrayList<PlanningContractItemType>();
		for (SapContractItemEntity contractItem : contractItems) {
			contractItemTypes.add(getContractItemType(contractItem));
		}
		Long sapContractId = contract.getSapContractId();
		switch (planningMaterialSupportType) {
		case NOT_SUPPORTED:
			Assert.assertFalse(
					String.format(CONTRACT_SHOULD_NOT_HAVE_INVALID_AGGREGATE_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.INVALID_AGGREGATE_PLANNING));
			Assert.assertFalse(
					String.format(CONTRACT_SHOULD_NOT_HAVE_VALID_AGGREGATE_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.VALID_AGGREGATE_PLANNING));
			Assert.assertFalse(
					String.format(CONTRACT_SHOULD_NOT_HAVE_INVALID_LANDFILL_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.INVALID_LANDFILL_PLANNING));
			Assert.assertFalse(
					String.format(CONTRACT_SHOULD_NOT_HAVE_VALID_LANDFILL_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.VALID_LANDFILL_PLANNING));
			break;
		case ENABLED:
			Assert.assertFalse(
					String.format(CONTRACT_SHOULD_NOT_HAVE_INVALID_AGGREGATE_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.INVALID_AGGREGATE_PLANNING));
			Assert.assertTrue(String.format(CONTRACT_SHOULD_HAVE_VALID_AGGREGATE_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.VALID_AGGREGATE_PLANNING));
			Assert.assertFalse(
					String.format(CONTRACT_SHOULD_NOT_HAVE_INVALID_LANDFILL_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.INVALID_LANDFILL_PLANNING));
			Assert.assertTrue(String.format(CONTRACT_SHOULD_HAVE_VALID_LANDFILL_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.VALID_LANDFILL_PLANNING));
			break;
		case DISABLED:
			Assert.assertFalse(
					String.format(CONTRACT_SHOULD_NOT_HAVE_VALID_AGGREGATE_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.VALID_AGGREGATE_PLANNING));
			Assert.assertTrue(String.format(CONTRACT_SHOULD_HAVE_INVALID_AGGREGATE_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.INVALID_AGGREGATE_PLANNING));
			Assert.assertFalse(
					String.format(CONTRACT_SHOULD_NOT_HAVE_VALID_LANDFILL_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.VALID_LANDFILL_PLANNING));
			Assert.assertTrue(String.format(CONTRACT_SHOULD_HAVE_INVALID_LANDFILL_PLANNING_CONTRACT_ITEM, sapContractId),
					contractItemTypes.contains(PlanningContractItemType.INVALID_LANDFILL_PLANNING));
			break;
		default:
				throw new IllegalArgumentException(String.format("Received value [%s] where this was not anticipated", planningMaterialSupportType));
		}
	}

	private PlanningContractItemType getContractItemType(SapContractItemEntity contractItem) {
		Long contractItemNumber = contractItem.getContractItemNo();
		if (contractItemNumber.equals(Constant.PLANNING_AGGREGATE_CONTRACT_ITEM_NUMBER)
				&& !isMarkedForDeletion(contractItem)) {
			return PlanningContractItemType.VALID_AGGREGATE_PLANNING;
		} else if (contractItemNumber.equals(Constant.PLANNING_AGGREGATE_CONTRACT_ITEM_NUMBER)
				&& isMarkedForDeletion(contractItem)) {
			return PlanningContractItemType.INVALID_AGGREGATE_PLANNING;
		}
		else if (contractItemNumber.equals(Constant.PLANNING_LANDFILL_CONTRACT_ITEM_NUMBER)
				&& !isMarkedForDeletion(contractItem)) {
			return PlanningContractItemType.VALID_LANDFILL_PLANNING;
		}
		else if (contractItemNumber.equals(Constant.PLANNING_LANDFILL_CONTRACT_ITEM_NUMBER)
				&& isMarkedForDeletion(contractItem)) {
			return PlanningContractItemType.INVALID_LANDFILL_PLANNING;
		}
		else {
			return PlanningContractItemType.NON_PLANNING;
		}
	}

	public void validateAllowPlanningMaterialFlag(
			PlanningMaterialSupportType planningMaterialSupportType, SareaEntity testSalesArea) {
		Boolean allowPlanningMaterialFlag = testSalesArea.getAllowPlanningMaterialFlag();
		switch (planningMaterialSupportType) {
		case NOT_SUPPORTED:
		case DISABLED:
			Assert.assertEquals(INCORRECT_ALLOW_PLANNING_MATERIAL_FLAG_OF_SALES_AREA,
					Constant.OFF, allowPlanningMaterialFlag);
			break;
		case ENABLED:
			Assert.assertEquals(INCORRECT_ALLOW_PLANNING_MATERIAL_FLAG_OF_SALES_AREA,
					Constant.ON, allowPlanningMaterialFlag);
			break;
		default:
			throw new IllegalArgumentException(String.format("Received value [%s] where this was not anticipated", planningMaterialSupportType));
		}
	}

	private boolean isMarkedForDeletion(SapContractItemEntity contractItem) {
		String markedForDeletion = contractItem.getMarkedForDeletion();
		return markedForDeletion != null && markedForDeletion.equals(FLAG_IS_ON_AS_STRING);
	}

	private void setPlanningMaterialSupport(boolean value) {
		SareaEntity testSalesArea = salesAreaService.findSalesAreaByDisplayName(TEST_SALES_AREA_DISPLAY_LONG_NAME);
		testSalesArea = 
				sapContractTestDataService.setAllowPlanningMaterialFlag(testSalesArea, value);
		Long salesAreaId = testSalesArea.getSareaId();
		salesAreaService.maintainPlanningMaterialContractItems(salesAreaId);
	}

}
