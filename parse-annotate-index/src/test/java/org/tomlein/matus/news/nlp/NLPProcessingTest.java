package org.tomlein.matus.news.nlp;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.tomlein.matus.news.nlp.DocumentTermsVector;
import org.tomlein.matus.news.nlp.GlobalTermsVector;
import org.tomlein.matus.news.nlp.NLPProcessing;
import org.tomlein.matus.news.nlp.Term;

public class NLPProcessingTest {
	
	@Test
	public void simpleTest() {
		GlobalTermsVector globalTerms = new GlobalTermsVector(false);
		String text = "Dogs, go to heaven.";
		DocumentTermsVector documentTerms = NLPProcessing.createTermsVector(globalTerms, text, 1);
		
		assertTrue(globalTerms.terms().size() == 2);
		assertTrue(documentTerms.terms().size() == 2);
		
		Set<String> terms = globalTerms.terms();
		assertTrue(terms.contains("dog"));
		assertTrue(terms.contains("heaven"));
		
		Set<Byte> tags = documentTerms.tags();
		assertTrue(tags.size() == 1);
		assertTrue(tags.contains(Term.posTagNN));
	}

	@Test
	public void namedEntityRecognitionTest() {
		GlobalTermsVector globalTerms = new GlobalTermsVector();
		String text = "The concern for Harkin and King, among others, is that the party could sacrifice too much negotiating power by signing off on Collins� plan.";
		DocumentTermsVector documentTerms = NLPProcessing.createTermsVector(globalTerms, text, 1);
		
		// King will not be recognized by Stanford NLP
		List<String> persons = documentTerms.getEntitiesWithType(Term.PERSON);
		assertTrue(persons.size() == 2);
		assertTrue(persons.contains("Harkin"));
		assertTrue(persons.contains("Collins"));
		
		text = "Grasshopper, the reusable prototype rocket from SpaceX, reached a record high � quite literally � after it rose to 744 meters (2,441 feet) during an October 7 test flight, before returning to land back on its launchpad unscathed.";
		documentTerms = NLPProcessing.createTermsVector(globalTerms, text, 1);
		
		persons = documentTerms.getEntitiesWithType(Term.PERSON);
		assertTrue(persons.size() == 1);
		assertTrue(persons.contains("Grasshopper"));
		List<String> organizations = documentTerms.getEntitiesWithType(Term.ORGANIZATION);
		assertTrue(organizations.size() == 1);
		assertTrue(organizations.contains("SpaceX"));
		List<String> dates = documentTerms.getEntitiesWithType(Term.DATE);
		assertTrue(dates.size() == 2);
		assertTrue(dates.contains("October"));
	}

}
