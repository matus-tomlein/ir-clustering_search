package org.tomlein.matus.news.searchindex;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.tomlein.matus.news.searchindex.Parser;

public class HtmlParsingTest {

	@Test
	public void test() {
		assertTrue(Parser.getTextFromHtmlFile("articles/42504980.html").contains("Audi"));
	}

}
