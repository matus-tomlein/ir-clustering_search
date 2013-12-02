package org.tomlein.matus.news.nlp;
import static org.junit.Assert.*;

import org.junit.Test;
import org.tomlein.matus.news.nlp.DocumentTermsVector;
import org.tomlein.matus.news.nlp.GlobalTermsVector;
import org.tomlein.matus.news.nlp.NLPProcessing;
import org.tomlein.matus.news.nlp.TermsVectorComparison;
import org.tomlein.matus.news.searchindex.Article;
import org.tomlein.matus.news.searchindex.ElasticSearchClient;


public class TermsVectorComparisonTest {

	@Test
	public void testCosineSimilarity() {
		String text1 = "Dogs love cats.";
		String text2 = "Dogs hate cats.";
		String text3 = "Dogs hate cats and lions.";
		
		GlobalTermsVector globalTerms = new GlobalTermsVector();
		DocumentTermsVector vector1 = NLPProcessing.createTermsVector(globalTerms, text1, 1);
		DocumentTermsVector vector2 = NLPProcessing.createTermsVector(globalTerms, text2, 2);
		DocumentTermsVector vector3 = NLPProcessing.createTermsVector(globalTerms, text3, 3);
		
		testCosineSimilarityResults(TermsVectorComparison.cosineSimilarity(vector1, vector3),
				TermsVectorComparison.cosineSimilarity(vector2, vector3));
		testCosineSimilarityResults(TermsVectorComparison.cosineSimilarityOfWeightedTerms(vector1, vector3, globalTerms),
				TermsVectorComparison.cosineSimilarityOfWeightedTerms(vector2, vector3, globalTerms));
	}
	
	void testCosineSimilarityResults(double result1and3, double result2and3) {
		System.out.println(result1and3);
		System.out.println(result2and3);
		assertTrue(result2and3 < 1);
		assertTrue(result1and3 < result2and3);
	}

	@Test
	public void testCompareArticles() {
		ElasticSearchClient client = new ElasticSearchClient();
		Article libertyArticleAbc = client.getArticle(469668925L);
		Article libertyArticleExaminer = client.getArticle(2056836992L);
		Article shutdownProtest = client.getArticle(3463371245L);
		
		GlobalTermsVector globalTerms = new GlobalTermsVector();
		DocumentTermsVector libertyArticleAbcVector = NLPProcessing.createTermsVector(globalTerms, libertyArticleAbc.getContent(), 1);
		DocumentTermsVector libertyArticleExaminerVector = NLPProcessing.createTermsVector(globalTerms, libertyArticleExaminer.getContent(), 2);
		DocumentTermsVector shutdownProtestVector = NLPProcessing.createTermsVector(globalTerms, shutdownProtest.getContent(), 3);
		
		double libertyArticlesSimilarity = TermsVectorComparison.cosineSimilarity(libertyArticleAbcVector, libertyArticleExaminerVector);
		testCosineSimilarityArticlesResults(libertyArticlesSimilarity,
			TermsVectorComparison.cosineSimilarity(libertyArticleAbcVector, shutdownProtestVector));
		testCosineSimilarityArticlesResults(libertyArticlesSimilarity,
			TermsVectorComparison.cosineSimilarity(libertyArticleExaminerVector, shutdownProtestVector));
		

		libertyArticlesSimilarity = TermsVectorComparison.cosineSimilarityOfWeightedTerms(libertyArticleAbcVector, libertyArticleExaminerVector, globalTerms);
		testCosineSimilarityArticlesResults(libertyArticlesSimilarity,
			TermsVectorComparison.cosineSimilarityOfWeightedTerms(libertyArticleAbcVector, shutdownProtestVector, globalTerms));
		testCosineSimilarityArticlesResults(libertyArticlesSimilarity,
			TermsVectorComparison.cosineSimilarityOfWeightedTerms(libertyArticleExaminerVector, shutdownProtestVector, globalTerms));
	}

	void testCosineSimilarityArticlesResults(double libertyArticlesSimilarity, double nonLibertySimilarity) {
		System.out.println(libertyArticlesSimilarity);
		System.out.println(nonLibertySimilarity);
		assertTrue(libertyArticlesSimilarity > nonLibertySimilarity);
	}
	
}
