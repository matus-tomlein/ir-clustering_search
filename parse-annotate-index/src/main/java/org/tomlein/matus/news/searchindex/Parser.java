package org.tomlein.matus.news.searchindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tomlein.matus.news.helpers.Helper;
import org.tomlein.matus.news.nlp.NLPProcessing;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class Parser {

	/**
	* Reads a file that contains links and ids of articles, parses them, annotates them and inserts them into Elasticsearch.
	*/
	public void indexArticlesFromFile(String filePath) {
		ElasticSearchClient client = new ElasticSearchClient();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(" ");
				int pageId = Integer.parseInt(parts[0]);
				long linkId = Long.parseLong(parts[1]);
				String url = parts[2];
				annotateAndIndexArticle(pageId, linkId, url, client);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	* Parses, annotates and indexes a single article.
	*/
	void annotateAndIndexArticle(int pageId, long linkId, String url, ElasticSearchClient client) {
		String filePath = String.format(
				"/Volumes/Library HD/Data/news/all_news_articles/%d/%d.html",
				pageId, linkId);

		File f = new File(filePath);
		if (!f.exists())
			return;

		System.out.println(url);

		try {
			Document doc = Jsoup.parse(f, "UTF-8", "http://example.com/");

			String content = getHtmlContent(doc.outerHtml());
			if (content == "")
				return;

			List<String> lemmas = new ArrayList<String>();
			List<String> organizations = new ArrayList<String>();
			List<String> dates = new ArrayList<String>();
			List<String> locations = new ArrayList<String>();
			List<String> persons = new ArrayList<String>();

			NLPProcessing.getLemmasAndEntities(content, linkId, lemmas, persons, organizations, dates, locations);

			client.insertAnnotatedArticle(url, doc.title(), content, linkId, pageId,
					StringUtils.join(lemmas, " "),
					StringUtils.join(organizations, " "),
					StringUtils.join(locations, " "),
					StringUtils.join(persons, " "),
					StringUtils.join(dates, " "));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	* Parses article content from HTML using Boilerpipe.
	*/
	String getHtmlContent(String html) {
		try {
			return ArticleExtractor.getInstance().getText(html);
		} catch (BoilerpipeProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
