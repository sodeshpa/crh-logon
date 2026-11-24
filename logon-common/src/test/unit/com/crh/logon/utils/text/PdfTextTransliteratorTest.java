/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.text;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class PdfTextTransliteratorTest {

	@Test
	public void nil() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("cs"));

		String output = transliterator.transliterate(null);
		assertThat(output, is(nullValue()));
	}

	@Test
	public void empty() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("cs"));

		String output = transliterator.transliterate("");
		assertThat(output, is(equalTo("")));
	}

	@Test
	public void english() {
		Transliterator transliterator = new PdfTextTransliterator(Locale.ENGLISH);

		String input = "aáčeéiíoóöőřšuúüűž AÁČEÉIÍOÓÖŐŘŠUÚÜŰŽ";
		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo("aácheéiíoóöõrzhshuúüûzh AÁChEÉIÍOÓÖÕRzhShUÚÜÛZh")));
	}

	@Test
	public void french() {
		Transliterator transliterator = new PdfTextTransliterator(Locale.ENGLISH);

		String input = "ŒœŸ";
		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo("OEoeY")));
	}

	@Test
	public void czech() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("cs"));

		// https://en.wikipedia.org/wiki/Czech_alphabet
		String input = "A, Á, B, C, Č, D, Ď, E, É, Ě, F, G, H, Ch, I, Í, J, K, L, M, N, Ň, O, Ó, "
				+ "P, Q, R, Ř, S, Š, T, Ť, U, Ú, Ů, V, W, X, Y, Ý, Z, Ž , a, á, b, c, č, d, ď, e, "
				+ "é, ě, f, g, h, ch, i, í, j, k, l, m, n, ň, o, ó, p, q, r, ř, s, š, t, ť, u, ú, "
				+ "ů, v, w, x, y, ý, z, ž";

		String expected = "A, Á, B, C, Ch, D, D, E, É, E, F, G, H, Ch, I, Í, J, K, L, M, N, N, O, Ó, "
				+ "P, Q, R, Rzh, S, Sh, T, T, U, Ú, U, V, W, X, Y, Ý, Z, Zh , a, á, b, c, ch, d, d, e, "
				+ "é, e, f, g, h, ch, i, í, j, k, l, m, n, n, o, ó, p, q, r, rzh, s, sh, t, t, u, ú, "
				+ "u, v, w, x, y, ý, z, zh";

		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo(expected)));
	}

	@Test
	public void slovak() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("sk"));

		// https://en.wikipedia.org/wiki/Slovak_alphabet
		String input = "A, Á, Ä, B, C, Č, D, Ď, DZ, DŽ, E, É, F, G, H, CH, I, Í, J, K, L, Ĺ, Ľ, M, "
				+ "N, Ň, O, Ó, Ô, P, Q, R, Ŕ, S, Š, T, Ť, U, Ú, V, W, X, Y, Ý, Z, Ž, a, á, ä, b, c, č, "
				+ "d, ď, dz, dž, e, é, f, g, h, ch, i, í, j, k, l, ĺ, ľ, m, n, ň, o, ó, ô, p, q, r, ŕ, "
				+ "s, š, t, ť, u, ú, v, w, x, y, ý, z, ž";

		String expected = "A, Á, Ä, B, C, Ch, D, D, DZ, DZh, E, É, F, G, H, CH, I, Í, J, K, L, L, L, M, "
				+ "N, N, O, Ó, Ô, P, Q, R, R, S, Sh, T, T, U, Ú, V, W, X, Y, Ý, Z, Zh, a, á, ä, b, c, ch, "
				+ "d, d, dz, dzh, e, é, f, g, h, ch, i, í, j, k, l, l, l, m, n, n, o, ó, ô, p, q, r, r, "
				+ "s, sh, t, t, u, ú, v, w, x, y, ý, z, zh";

		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo(expected)));
	}

	@Test
	public void hungarian() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("hu"));

		// https://en.wikipedia.org/wiki/Hungarian_alphabet
		String input = "A, Á, B, C, Cs, D, Dz, Dzs, E, É, F, G, Gy, H, I, Í, J, K, L, Ly, M, N, Ny, "
				+ "O, Ó, Ö, Ő, P, Q, R, S, Sz, T, Ty, U, Ú, Ü, Ű, V, W, X, Y, Z, Zs, a, á, b, c, cs, "
				+ "d, dz, dzs, e, é, f, g, gy, h, i, í, j, k, l, ly, m, n, ny, o, ó, ö, ő, p, q, r, "
				+ "s, sz, t, ty, u, ú, ü, ű, v, w, x, y, z, zs";

		String expected = "A, Á, B, C, Cs, D, Dz, Dzs, E, É, F, G, Gy, H, I, Í, J, K, L, Ly, M, N, Ny, "
				+ "O, Ó, Ö, Õ, P, Q, R, S, Sz, T, Ty, U, Ú, Ü, Û, V, W, X, Y, Z, Zs, a, á, b, c, cs, "
				+ "d, dz, dzs, e, é, f, g, gy, h, i, í, j, k, l, ly, m, n, ny, o, ó, ö, õ, p, q, r, "
				+ "s, sz, t, ty, u, ú, ü, û, v, w, x, y, z, zs";

		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo(expected)));
	}

	@Test
	public void romanian() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("ro"));

		// https://en.wikipedia.org/wiki/Romanian_alphabet
		String input = "A, Ă, Â, B, C, D, E, F, G, H, I, Î, J, K, L, M, N, O, P, Q, R, S, Ș, T, Ț, "
				+ "U, V, W, X, Y, Z, a, ă, â, b, c, d, e, f, g, h, i, î, j, k, l, m, n, o, p, q, r, "
				+ "s, ș, t, ț, u, v, w, x, y, z";

		String expected = "A, A, Â, B, C, D, E, F, G, H, I, Î, J, K, L, M, N, O, P, Q, R, S, S, T, T, "
				+ "U, V, W, X, Y, Z, a, a, â, b, c, d, e, f, g, h, i, î, j, k, l, m, n, o, p, q, r, "
				+ "s, s, t, t, u, v, w, x, y, z";

		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo(expected)));
	}

	@Test
	public void polish() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("pl"));

		// https://en.wikipedia.org/wiki/Polish_alphabet
		String input = "A, Ą, B, C, Ć, D, E, Ę, F, G, H, I, J, K, L, Ł, M, N, Ń, O, Ó, P, R, S, "
				+ "Ś, T, U, W, Y, Z, Ź, a, ą, b, c, ć, d, e, ę, f, g, h, i, j, k, l, ł, m, n, ń, "
				+ "o, ó, p, r, s, ś, t, u, w, y, z, ź";

		String expected = "A, A, B, C, C, D, E, E, F, G, H, I, J, K, L, L, M, N, N, O, Ó, P, R, S, "
				+ "S, T, U, W, Y, Z, Z, a, a, b, c, c, d, e, e, f, g, h, i, j, k, l, l, m, n, n, "
				+ "o, ó, p, r, s, s, t, u, w, y, z, z";

		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo(expected)));
	}

	@Test
	public void azerbaijani() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("az"));

		// https://en.wikipedia.org/wiki/Azerbaijani_alphabet
		String input = "Aa, Bb, Cc, Çç, Dd, Ee, Əə, Ff, Gg, Ğğ, Hh, Xx, Iı, İi, Jj, Kk, Qq, "
				+ "Ll, Mm, Nn, Oo, Öö, Pp, Rr, Ss, Şş, Tt, Uu, Üü, Vv, Yy, Zz";

		String expected = "Aa, Bb, Cc, Cc, Dd, Ee, Ää, Ff, Gg, Gg, Hh, Xx, Ii, Ii, Jj, Kk, Qq, "
				+ "Ll, Mm, Nn, Oo, Öö, Pp, Rr, Ss, Ss, Tt, Uu, Üü, Vv, Yy, Zz";

		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo(expected)));
	}

	@Test
	public void croatian() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("hr"));

		// https://en.wikipedia.org/wiki/Gaj%27s_Latin_alphabet
		// https://en.wikipedia.org/wiki/Croatian_language
		String input = "A, B, C, Č, Ć, D, DŽ, Đ, E, F, G, H, I, J, K, L, LJ, M, N, NJ, O, P, R, "
				+ "S, Š, T, U, V, Z, Ž, a, b, c, č, ć, d, dž, đ, e, f, g, h, i, j, k, l, lj, m, "
				+ "n, nj, o, p, r, s, š, t, u, v, z, ž";

		String expected = "A, B, C, Ch, C, D, DZh, D, E, F, G, H, I, J, K, L, LJ, M, N, NJ, O, P, R, "
				+ "S, Sh, T, U, V, Z, Zh, a, b, c, ch, c, d, dzh, d, e, f, g, h, i, j, k, l, lj, m, "
				+ "n, nj, o, p, r, s, sh, t, u, v, z, zh";

		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo(expected)));
	}

	@Test
	public void serbian() {
		Transliterator transliterator = new PdfTextTransliterator(new Locale("sr"));

		// https://en.wikipedia.org/wiki/Gaj%27s_Latin_alphabet
		// https://en.wikipedia.org/wiki/Serbian_language
		String input = "A, B, C, Č, Ć, D, DŽ, Đ, E, F, G, H, I, J, K, L, LJ, M, N, NJ, O, P, R, "
				+ "S, Š, T, U, V, Z, Ž, a, b, c, č, ć, d, dž, đ, e, f, g, h, i, j, k, l, lj, m, "
				+ "n, nj, o, p, r, s, š, t, u, v, z, ž";

		String expected = "A, B, C, Ch, C, D, DZh, D, E, F, G, H, I, J, K, L, LJ, M, N, NJ, O, P, R, "
				+ "S, Sh, T, U, V, Z, Zh, a, b, c, ch, c, d, dzh, d, e, f, g, h, i, j, k, l, lj, m, "
				+ "n, nj, o, p, r, s, sh, t, u, v, z, zh";

		String output = transliterator.transliterate(input);
		assertThat(output, is(equalTo(expected)));
	}
}
