package org.tomlein.matus.news.nlp;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.tomlein.matus.news.nlp.Term;
import org.tomlein.matus.news.nlp.Wordnet;

public class WordnetTest {

	@Test
	public void test() {
		List<String> results = Wordnet.getSynset(new Term("USA", "NN"));
		
	}

}
