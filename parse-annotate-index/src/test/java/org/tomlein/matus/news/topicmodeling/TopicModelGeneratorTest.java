package org.tomlein.matus.news.topicmodeling;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.tomlein.matus.news.nlp.DocumentTermsVector;
import org.tomlein.matus.news.nlp.EventCluster;
import org.tomlein.matus.news.nlp.GlobalTermsVector;
import org.tomlein.matus.news.nlp.NLPProcessing;
import org.tomlein.matus.news.topicmodeling.TopicModelGenerator;

public class TopicModelGeneratorTest {

	@Test
	public void test() {
		GlobalTermsVector globalTerms = new GlobalTermsVector(false);

		String text1 = "Dropbox is my filesystem. Every file that I need to have available across devices and that doesn’t require the rich text and search capabilities of Evernote goes into my Dropbox account: screenshots that I share with coworkers; PDF copies of my receipts and invoices; articles written in Editorial are stored in Dropbox. Even my photo backup workflow relies on Dropbox as an archival system that’s always in the cloud, readily available and easily shareable. With the Packrat feature, a $39 yearly add-on, I get access to the full history of my deleted files and file revisions, which have saved me on several occasions in the past.";
		DocumentTermsVector documentTerms1 = NLPProcessing.createTermsVector(globalTerms, text1, 1);
		String text2 = "The official Dropbox app for iOS is good, but it’s not great for power users and it hasn’t been substantially enhanced for iOS 7 yet. That’s what Italian developers Matteo Lallone and Gianluca Divisi (together, Tapwings) want to fix with Boxie, a $1.99 third-party Dropbox client for iPhone packed with advanced features and navigation options. I’ve been testing Boxie for the past month, and I think that it’s off to a solid start.";
		DocumentTermsVector documentTerms2 = NLPProcessing.createTermsVector(globalTerms, text2, 2);
		
		EventCluster cluster = new EventCluster(-1);
		cluster.insertArticle(documentTerms1);
		cluster.insertArticle(documentTerms2);
		
		EventTopicModel eventTopicModel = TopicModelGenerator.createTopicModel(cluster.getClusterFilePath(), 5, 100);
		
		File file = new File(cluster.getClusterFilePath());
		file.delete();
		
		assertTrue(eventTopicModel.getTopics().size() == 5);
		for (Topic topic : eventTopicModel.getTopics()) {
			if (topic.getArticleProbabilities().size() == 2)
				return;
		}
		
		assertTrue(false);
	}

}
