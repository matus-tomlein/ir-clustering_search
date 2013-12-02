package org.tomlein.matus.news.nlp;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.tomlein.matus.news.searchindex.Article;
import org.tomlein.matus.news.searchindex.ElasticSearchClient;

public class EventClusterTest {

	@Test
	public void test() {
		GlobalTermsVector globalTerms = new GlobalTermsVector(false);
		String text = "Now directory 1 and directory 2 currently don't exist. I want Java to create them automatically if they aren't already there. Actually Java should set up the whole file path if not already existing.";
		DocumentTermsVector documentTerms = NLPProcessing.createTermsVector(globalTerms, text, 1);
		
		EventCluster cluster = new EventCluster(-1);
		cluster.insertArticle(documentTerms);
		
		File file = new File(cluster.getClusterFilePath());
		assertTrue(file.exists());
		assertTrue(file.getTotalSpace() > 0);
		file.delete();
	}

	@Test
	public void loadTestCluster() {
		ElasticSearchClient client = new ElasticSearchClient();
		List<Article> articles = client.search("Shutdown Protests Take Over D.C. Memorials");
		
		GlobalTermsVector globalTerms = new GlobalTermsVector();
		EventCluster cluster = new EventCluster(-2);
		
		File file = new File(cluster.getClusterFilePath());
		if (file.exists())
			file.delete();
		
		for (Article article : articles) {
			System.out.println(article.getTitle());
			DocumentTermsVector documentTerms = NLPProcessing.createTermsVector(globalTerms, article.getContent(), article.getLinkId());
			cluster.insertArticle(documentTerms);
		}
		
		file = new File(cluster.getClusterFilePath());
		assertTrue(file.exists());
		assertTrue(file.getTotalSpace() > 0);
	}
}
