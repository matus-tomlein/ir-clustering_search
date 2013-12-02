package org.tomlein.matus.news.topicmodeling;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.tomlein.matus.news.nlp.EventCluster;

import java.util.Set;

public class EventTopicModelTest {

	@Test
	public void testRecommendNewTopics() {
		EventTopicModel model = createTopicModel();
		
		ArrayList<Long> readArticles = new ArrayList<Long>();
		readArticles.add(1L);
		List<Topic> newTopics = model.recommendNewTopics(readArticles);
		
		assertTrue(newTopics.size() == 2);
		assertTrue(newTopics.get(0) == model.get(3));
		assertTrue(newTopics.get(1) == model.get(2));
	}
	
	@Test
	public void testRecommendArticles() {
		EventTopicModel model = createTopicModel();
		
		ArrayList<Long> readArticles = new ArrayList<Long>();
		readArticles.add(1L);
		List<Topic> newTopics = model.recommendNewTopics(readArticles);
		
		Long[] recommendedArticles = model.recommendArticlesForTopics(newTopics, readArticles).toArray(new Long[0]);
		assertTrue(recommendedArticles.length == 2);
		assertTrue(recommendedArticles[0] == 3L);
		assertTrue(recommendedArticles[1] == 2L);
	}
	
	EventTopicModel createTopicModel() {
		EventTopicModel model = new EventTopicModel(4);
		model.setArticleTopicProbability(1, 0, 0.3);
		model.setArticleTopicProbability(2, 0, 0.5);
		model.setArticleTopicProbability(1, 1, 0.1);
		model.setArticleTopicProbability(2, 1, 0.8);
		model.setArticleTopicProbability(3, 1, 0.2);
		model.setArticleTopicProbability(2, 2, 0.5);
		model.setArticleTopicProbability(3, 2, 0.6);
		model.setArticleTopicProbability(2, 3, 0.5);
		model.setArticleTopicProbability(3, 3, 0.8);
		return model;
	}

	@Test
	public void testRealData() {
		EventCluster cluster = new EventCluster(-2);
		EventTopicModel model = TopicModelGenerator.createTopicModel(cluster.getClusterFilePath(), 50, 200);
		
		List<Long> readArticles = new ArrayList<Long>();
		readArticles.add(2759055549L);
		List<Topic> recommendedTopics = model.recommendNewTopics(readArticles);
		Set<Long> recommendedArticles = model.recommendArticlesForTopics(recommendedTopics, readArticles);
		assertTrue(recommendedArticles.size() > 0);
	}
}
