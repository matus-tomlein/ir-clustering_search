package org.tomlein.matus.news.nlp;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.tomlein.matus.news.nlp.GlobalTermsVector;


public class GlobalTermsVectorTest {

	@Test
	public void testSaveAndLoad() {
		String fileName = "terms_vector_test.ser";
		GlobalTermsVector vector1 = new GlobalTermsVector(fileName);
		vector1.addDocument(34);
		vector1.addDocument(543);
		vector1.addTerm("pig", 34);
		vector1.addTerm("dog", 34);
		vector1.addTerm("cat", 543);
		vector1.addTerm("lion", 543);
		vector1.addTerm("turtle", 543);
		vector1.saveTermsToFile();

		GlobalTermsVector vector2 = new GlobalTermsVector(fileName);

		File file = new File(fileName);
		file.delete();
		
		assertTrue(vector1.termsAndDocumentsMap().equals(vector2.termsAndDocumentsMap()));
		assertTrue(vector1.documents().equals(vector2.documents()));
		assertTrue(vector1.terms().contains("cat"));
	}

}
