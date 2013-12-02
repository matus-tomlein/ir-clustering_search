require_relative 'elasticsearch_client'

require 'json'

class ClusteringSearch
  attr_reader :clusters

  def initialize(query, title_types, content_types, algorithm)
    @clusters = []
    search query, title_types, content_types, algorithm
  end

  def search(query, title_types, content_types, algorithm)
    client = ElasticsearchClient.new
    search_results = client.search_with_clusters query, title_types, content_types, algorithm

    articles = {}
    search_results['hits']['hits'].each do |hit|
      articles[hit['_id']] = hit
    end

    search_results['clusters'].each do |cluster|
      cluster_articles = []
      cluster['documents'].each do |article_id|
        cluster_articles << articles[article_id]
      end
      @clusters << {
        'score' => cluster['score'],
        'label' => cluster['label'],
        'documents' => cluster_articles
      }
    end
  end
end
