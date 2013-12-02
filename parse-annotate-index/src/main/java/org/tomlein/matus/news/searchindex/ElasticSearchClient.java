package org.tomlein.matus.news.searchindex;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.tomlein.matus.news.helpers.Helper;


public class ElasticSearchClient {
	private Client client = null;
	private float minScore = 0.3f;

	public ElasticSearchClient() {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "elasticsearch_matus").build();

		client = new TransportClient(settings)
			.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
	}

	public void close() {
		client.close();
	}

	public void insertAnnotatedArticle(String url, String title,
			String content, long linkId, int pageId, String lemmas,
			String organizations, String locations, String persons, String dates) {

		Map<String, Object> json = new HashMap<String, Object>();
		json.put("url", url);
		json.put("title", title);
		json.put("content", content);
		json.put("pageId", pageId);
		json.put("organizations", organizations);
		json.put("locations", locations);
		json.put("persons", persons);
		json.put("lemmas", lemmas);
		json.put("dates", dates);

		client.prepareIndex("news", "annotated_article", Long.toString(linkId))
			.setSource(json)
			.execute()
			.actionGet();
	}

}
