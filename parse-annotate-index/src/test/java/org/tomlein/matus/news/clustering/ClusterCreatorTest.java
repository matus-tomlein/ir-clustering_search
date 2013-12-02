package org.tomlein.matus.news.clustering;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.tomlein.matus.news.nlp.DocumentTermsVector;
import org.tomlein.matus.news.nlp.GlobalTermsVector;
import org.tomlein.matus.news.nlp.NLPProcessing;
import org.tomlein.matus.news.searchindex.Article;
import org.tomlein.matus.news.searchindex.ElasticSearchClient;

public class ClusterCreatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		GlobalTermsVector globalTerms = new GlobalTermsVector();
		ClusterCreator creator = new ClusterCreator(globalTerms);
		
//		3942671462 - Government Shutdown Talks Move To Spending Levels As Mitch McConnell Holds The Line
//		4093011835 - House Republicans Changed The Rules So A Majority Vote Couldn't Stop The Government Shutdown
//		808668670 - Official Apple 'Spaceship' Campus Model Makes Its Debut
		
		ElasticSearchClient client = new ElasticSearchClient();
		Article governmentShutdown1 = client.getArticle(3942671462L);
		Article governmentShutdown2 = client.getArticle(4093011835L);
		Article appleCampus = client.getArticle(808668670L);
		
		DocumentTermsVector governmentShutdown1Vector = NLPProcessing.createTermsVector(globalTerms, governmentShutdown1.getContent(), governmentShutdown1.getLinkId());
		DocumentTermsVector governmentShutdown2Vector = NLPProcessing.createTermsVector(globalTerms, governmentShutdown2.getContent(), governmentShutdown2.getLinkId());
		DocumentTermsVector appleCampusVector = NLPProcessing.createTermsVector(globalTerms, appleCampus.getContent(), appleCampus.getLinkId());
		
		Cluster governmentCluster = creator.addArticleToCluster(governmentShutdown1Vector);
		Cluster appleCampusCluster = creator.addArticleToCluster(appleCampusVector);
		
		assertTrue(governmentCluster != appleCampusCluster);
		assertTrue(governmentCluster == creator.addArticleToCluster(governmentShutdown2Vector));
	}

}
