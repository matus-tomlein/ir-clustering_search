package org.tomlein.matus.news.topicmodeling;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.tomlein.matus.news.nlp.EventCluster;

public class ArticleTopicsComparisonTest {
	
	EventTopicModel topicModel = null;
	ArticleTopicsComparison articleComparison = null;
	
	@Before
	public void setUp() throws Exception {
		EventCluster cluster = new EventCluster(-2);
		topicModel = TopicModelGenerator.createTopicModel(cluster.getClusterFilePath(), 50, 200);
		articleComparison = new ArticleTopicsComparison(topicModel);
	}

	@Test
	public void testArticleComparison() {
		assertTrue(
				articleComparison.compareArticles(2160987082L, 2759055549L)
				>
				articleComparison.compareArticles(2160987082L, 2269878572L)
				);
	}
	
	@Test
	public void testArticleDifference() {
		assertTrue(
				articleComparison.getArticleDifference(2160987082L, 2759055549L)
				<
				articleComparison.getArticleDifference(2160987082L, 2269878572L)
				);
	}
	
	@Test
	public void testNovelArticleRecommendation() {
		List<Long> readArticles = new ArrayList<Long>();
		readArticles.add(2160987082L);
		
		Set<Long> recommendedArticles = articleComparison.recommendNovelArticles(readArticles);
		assertTrue(true);
	}

}
