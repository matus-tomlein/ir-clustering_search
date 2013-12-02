package org.tomlein.matus.news.searchindex;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.tomlein.matus.news.searchindex.Article;
import org.tomlein.matus.news.searchindex.ElasticSearchClient;

public class ElasticSearchClientTest {

	@Test
	public void test() {
		ElasticSearchClient client = new ElasticSearchClient();
		List<Article> articles = client.search("government shutdown snl");
		
		for (Article article : articles) {
			System.out.println(article.title);
		}
		client.close();
	}

}
