package org.tomlein.matus.news.nlp;
import static org.junit.Assert.*;

import org.junit.Test;
import org.tomlein.matus.news.nlp.Stopwords;


public class StopwordsTest {

	@Test
	public void test() {
		assertTrue(Stopwords.isStopword("and"));
		assertTrue(Stopwords.isStopword("And"));
		assertTrue(Stopwords.isStopword("while"));
		assertFalse(Stopwords.isStopword("tourist"));
		assertFalse(Stopwords.isStopword("Bratislava"));
		assertFalse(Stopwords.isStopword("school"));
		assertFalse(Stopwords.isStopword("car"));
		assertFalse(Stopwords.isStopword("insurance"));
	}

}
