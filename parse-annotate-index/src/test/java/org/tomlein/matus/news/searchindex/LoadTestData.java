package org.tomlein.matus.news.searchindex;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.tomlein.matus.news.nlp.GlobalTermsVector;
import org.tomlein.matus.news.nlp.NLPProcessing;
import org.tomlein.matus.news.searchindex.Article;
import org.tomlein.matus.news.searchindex.ElasticSearchClient;
import org.tomlein.matus.news.searchindex.Parser;

public class LoadTestData {

	@Test
	public void loadTestDataToElasticSearch() {
		if (true)
			return;
		Parser parser = new Parser();
		parser.readAndDownloadLinksFromJson("articles/links.json");
		
		ElasticSearchClient client = new ElasticSearchClient();
		assertTrue(client.containsArticle(1663333099));
		client.close();
	}

	@Test
	public void updateGlobalVector() {
		GlobalTermsVector globalTerms = new GlobalTermsVector();
		ElasticSearchClient client = new ElasticSearchClient();
		
		List<HashMap<String,Object>> results = Parser.getLinksFromJson("articles/links.json");
		for (HashMap<String,Object> link : results.subList(0, Math.min(1500, results.size()))) {
			long linkId = Long.valueOf(link.get("LinkId").toString());

			Article article = client.getArticle(linkId);
			if (article == null)
				continue;
			System.out.println(article.url);

			NLPProcessing.createTermsVector(globalTerms, article.content, linkId);
		}

		globalTerms.saveTermsToFile();
	}
}
