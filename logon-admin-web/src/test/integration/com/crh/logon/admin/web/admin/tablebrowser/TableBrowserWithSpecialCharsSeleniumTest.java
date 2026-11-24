
package com.crh.logon.admin.web.admin.tablebrowser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.admin.tablebrowser.pages.TableBrowserPage;
import com.crh.logon.admin.web.admin.tablebrowser.pages.TlgncCarrierPage;
import com.crh.logon.admin.web.admin.tablebrowser.pages.TlgncExtMaterialPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;

/**
 * This functional test case exercises the application's ability to handle foreign
 * characters outside of the IO-8859-1 range. In other words, we ensure that we 
 * can enter, save, and retrieve the UTF-8 encoded characters correctly.
 * <p>
 * The handling of special characters is not special to the <em>TableBrowser</em>,
 * however, the <em>TableBrowser</em> does provide a decent basis for testing since
 * we we can update most fields of any table we like and the updates and retrievals
 * go through the same mechanism as any other application page (JSP, Java, DB). In
 * this way, we can at least check whether the overall settings and all links in the
 * chain are functioning as expected.
 * <p>
 * Each test will create an entry in the TLGNC_EXT_MATERIAL table and then delete it.
 * There was not reason why the TLGNC_EXT_MATERIAL table was chosen. We based the code
 * off the GenericTableBrowserSeleniumTest and this used TLGNC_EXT_MATERIAL. But really,
 * any table where you can save and retrieve VARCHAR data would suffice I guess.
 * <p>
 * <b>NOTE:</b> All special characters are escaped as Unicode hex points. The reason 
 * is that this avoids problems with the compiler encoding.
 * 
 * @ticket LOGON-6918
 * @author AdNovum Informatik AG
 */
public final class TableBrowserWithSpecialCharsSeleniumTest extends LogonSeleniumTestCaseBase {

	@Before
	public void login() throws Exception {
		loginToLogon(TableBrowserPage.ENTRY_PATH, "logonadmin_de", "logonadmin_de");
	}

	@After
	public void logout() throws Exception {
		logoutOfLogon();
	}


	@Test
	public void cyrillicCharacters() {
		// http://www.unicode.org/charts/PDF/U0400.pdf
		// the cyrillic alphabet contains around 256 characters which is much larger then
		// the comment field size, so we do this in 2 iterations.

		String special;

		// Ѐ Ё Ђ Ѓ Є Ѕ І Ї Ј Љ Њ Ћ Ќ Ѝ Ў Џ А Б В Г Д Е Ж З И Й К Л М Н О П Р С Т У Ф Х Ц Ч Ш Щ Ъ Ы Ь Э Ю Я а б в г д е ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я ѐ ё ђ ѓ є ѕ і ї ј љ њ ћ ќ ѝ ў џ Ѡ ѡ Ѣ ѣ Ѥ ѥ Ѧ ѧ Ѩ ѩ Ѫ ѫ Ѭ ѭ Ѯ ѯ Ѱ ѱ Ѳ ѳ Ѵ ѵ Ѷ ѷ Ѹ ѹ Ѻ ѻ Ѽ ѽ
		special = "\u0400\u0401\u0402\u0403\u0404\u0405\u0406\u0407\u0408\u0409\u040a\u040b\u040c\u040d\u040e\u040f\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f\u0450\u0451\u0452\u0453\u0454\u0455\u0456\u0457\u0458\u0459\u045a\u045b\u045c\u045d\u045e\u045f\u0460\u0461\u0462\u0463\u0464\u0465\u0466\u0467\u0468\u0469\u046a\u046b\u046c\u046d\u046e\u046f\u0470\u0471\u0472\u0473\u0474\u0475\u0476\u0477\u0478\u0479\u047a\u047b\u047c\u047d";
		testSpecialCharacters(special);

		// Ѿ ѿ Ҁ ҁ ҂ ҃ ҄ ҅ ҆ ҇ ҈ ҉ Ҋ ҋ Ҍ ҍ Ҏ ҏ Ґ ґ Ғ ғ Ҕ ҕ Җ җ Ҙ ҙ Қ қ Ҝ ҝ Ҟ ҟ Ҡ ҡ Ң ң Ҥ ҥ Ҧ ҧ Ҩ ҩ Ҫ ҫ Ҭ ҭ Ү ү Ұ ұ Ҳ ҳ Ҵ ҵ Ҷ ҷ Ҹ ҹ Һ һ Ҽ ҽ Ҿ ҿ Ӏ Ӂ ӂ Ӄ ӄ Ӆ ӆ Ӈ ӈ Ӊ ӊ Ӌ ӌ Ӎ ӎ ӏ Ӑ ӑ Ӓ ӓ Ӕ ӕ Ӗ ӗ Ә ә Ӛ ӛ Ӝ ӝ Ӟ ӟ Ӡ ӡ Ӣ ӣ Ӥ ӥ Ӧ ӧ Ө ө Ӫ ӫ Ӭ ӭ Ӯ ӯ Ӱ ӱ Ӳ ӳ Ӵ ӵ Ӷ ӷ Ӹ ӹ Ӻ ӻ Ӽ ӽ Ӿ
		special = "\u047e\u047f\u0480\u0481\u0482\u0483\u0484\u0485\u0486\u0487\u0488\u0489\u048a\u048b\u048c\u048d\u048e\u048f\u0490\u0491\u0492\u0493\u0494\u0495\u0496\u0497\u0498\u0499\u049a\u049b\u049c\u049d\u049e\u049f\u04a0\u04a1\u04a2\u04a3\u04a4\u04a5\u04a6\u04a7\u04a8\u04a9\u04aa\u04ab\u04ac\u04ad\u04ae\u04af\u04b0\u04b1\u04b2\u04b3\u04b4\u04b5\u04b6\u04b7\u04b8\u04b9\u04ba\u04bb\u04bc\u04bd\u04be\u04bf\u04c0\u04c1\u04c2\u04c3\u04c4\u04c5\u04c6\u04c7\u04c8\u04c9\u04ca\u04cb\u04cc\u04cd\u04ce\u04cf\u04d0\u04d1\u04d2\u04d3\u04d4\u04d5\u04d6\u04d7\u04d8\u04d9\u04da\u04db\u04dc\u04dd\u04de\u04df\u04e0\u04e1\u04e2\u04e3\u04e4\u04e5\u04e6\u04e7\u04e8\u04e9\u04ea\u04eb\u04ec\u04ed\u04ee\u04ef\u04f0\u04f1\u04f2\u04f3\u04f4\u04f5\u04f6\u04f7\u04f8\u04f9\u04fa\u04fb\u04fc\u04fd\u04fe";
		testSpecialCharacters(special);
	}

	@Test
	public void cyrillicSupplementCharacters() {
		// http://www.unicode.org/charts/PDF/U0500.pdf		
		// Ԁ ԁ Ԃ ԃ Ԅ ԅ Ԇ ԇ Ԉ ԉ Ԋ ԋ Ԍ ԍ Ԏ ԏ Ԑ ԑ Ԓ ԓ Ԕ ԕ Ԗ ԗ Ԙ ԙ Ԛ ԛ Ԝ ԝ Ԟ ԟ Ԡ ԡ Ԣ ԣ Ԥ ԥ
		String special = "\u0500\u0501\u0502\u0503\u0504\u0505\u0506\u0507\u0508\u0509\u050a\u050b\u050c\u050d\u050e\u050f\u0510\u0511\u0512\u0513\u0514\u0515\u0516\u0517\u0518\u0519\u051a\u051b\u051c\u051d\u051e\u051f\u0520\u0521\u0522\u0523\u0524\u0525";
		testSpecialCharacters(special);
	}

	@Test
	public void croatianSpecialCharacters() {
		// Čč Ćć Đđ Šš Žž
		String special = "\u010C\u010D \u0106\u0107 \u0110\u0111 \u0160\u0161 \u017D\u017E";		
		testSpecialCharacters(special);
	}

	@Test
	public void serbianSpecialCharacters() {
		// Čč Ćć Đđ Šš Žž
		String special = "\u010C\u010D \u0106\u0107 \u0110\u0111 \u0160\u0161 \u017D\u017E";
		testSpecialCharacters(special);
	}

	@Test
	public void czechSpecialCharacters() {
		// Ěě Šš Čč Řř Žž Ýý Áá Íí Éé Úú Ůů Ďď Ťť Ňň Óó
		String special = "\u011A\u011B \u0160\u0161 \u010C\u010D \u0158\u0159 \u017D\u017E \u00DD\u00FD \u00C1\u00E1 \u00CD\u00ED \u00C9\u00E9 \u00DA\u00FA \u016E\u016F \u010E\u010F \u0164\u0165 \u0147\u0148 \u00D3\u00F3";
		testSpecialCharacters(special);
	}

	@Test
	public void slovakSpecialCharacters() {
		// Ľľ Šš Čč Ťť Žž Ýý Áá Íí Éé Óó Úú Ää Ôô Ňň Ďď Ĺĺ Ŕŕ
		String special = "\u013D\u013E \u0160\u0161 \u010C\u010D \u0164\u0165 \u017D\u017E \u00DD\u00FD \u00C1\u00E1 \u00CD\u00ED \u00C9\u00E9 \u00D3\u00F3 \u00DA\u00FA \u00C4\u00E4 \u00D4\u00F4 \u0147\u0148 \u010E\u010F \u0139\u013A \u0154\u0155";
		testSpecialCharacters(special);
	}

	@Test
	public void hungarianSpecialCharacters() {
		// Á É Í Ó Ö Ő Ú Ü Ű
		String special = "\u00C1 \u00C9 \u00CD \u00D3 \u00D6 \u0150 \u00DA \u00DC \u0170";
		testSpecialCharacters(special);
	}

	@Test
	public void romanianSpecialCharactres() {
		// Ăă Ââ Îî Șș Țț
		String special = "\u0102\u0103 \u00C2\u00E2 \u00CE\u00EE \u0218\u0219 \u021A\u021B";
		testSpecialCharacters(special);
	}


	private void testSpecialCharacters(String special) {
		String name = "TB_EXT_MAT_SPECIAL_" + Long.toString(System.currentTimeMillis());
		String comment = String.format("Selenium table browser test with special chars %s", special);

		TableBrowserPage tableBrowserPage = LogonPageFactory.initElements(seleniumDriver, TableBrowserPage.class);
		
		TlgncExtMaterialPage tlgncExtMaterialPage = tableBrowserPage.viewTablePage(TlgncExtMaterialPage.class);
		
		String materialWhereClause = "MATERIAL_NAME = '" + name + "'";
		
		tlgncExtMaterialPage.filterWithWhereClause(materialWhereClause);
		assertThat(tlgncExtMaterialPage.countNumberOfRows(), is(equalTo(0)));
		
		// add new external material
		tlgncExtMaterialPage.clickAddRowButton();
		String carrierReferenceWhereClauseFilter = TlgncCarrierPage.CarrierColumn.DISPLAY_NAME.getColumName() + " like '%carrier%'";
		
		tlgncExtMaterialPage.selectReference(TlgncCarrierPage.CarrierColumn.CARRIER_ID, carrierReferenceWhereClauseFilter);
		
		tlgncExtMaterialPage.inputValueForColum(TlgncExtMaterialPage.ExtMaterialColumn.MATERIAL_NAME, name);
		tlgncExtMaterialPage.inputValueForColum(TlgncExtMaterialPage.ExtMaterialColumn.MATERIAL_COMMENT, comment);
		tlgncExtMaterialPage.clickSaveButton();
		
		// filter again
		tlgncExtMaterialPage.filterWithWhereClause(materialWhereClause);
		
		// count again
		assertThat(tlgncExtMaterialPage.countNumberOfRows(), is(equalTo(1)));
		assertThat(tlgncExtMaterialPage.getValueAtColumn(TlgncExtMaterialPage.ExtMaterialColumn.MATERIAL_COMMENT), is(equalTo(comment)));
		
		// delete
		tlgncExtMaterialPage.deleteFirstRow();
		
		// count again
		assertThat(tlgncExtMaterialPage.countNumberOfRows(), is(equalTo(0)));
	}
}