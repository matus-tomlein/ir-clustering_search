package org.tomlein.matus.news.nlp;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;


public class NLPProcessing {

	static StanfordCoreNLP pipeline = null;

	static {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		pipeline = new StanfordCoreNLP(props);
	}

	/*
	* Gets lists of lemmas and entities from the given text.
	*/
	public static void getLemmasAndEntities(String text, long linkId, List<String> lemmas, List<String> persons, List<String> organizations, List<String> dates, List<String> locations) {
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence: sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {

				// this is the text of the token
				String lemma = token.get(LemmaAnnotation.class);
				// this is the NER label of the token
				String namedEntity = token.get(NamedEntityTagAnnotation.class);

				// skip stop words
				if (Stopwords.isStopword(lemma))
					continue;

				lemmas.add(lemma);

				if (namedEntity.equals("PERSON"))
					persons.add(lemma);
				else if (namedEntity.equals("ORGANIZATION"))
					organizations.add(lemma);
				else if (namedEntity.equals("LOCATION"))
					locations.add(lemma);
				else if (namedEntity.equals("DATE"))
					dates.add(lemma);
			}
		}
	}
}
