require 'elasticsearch'
require 'json'

require_relative 'environment'

module Elasticsearch
  module API
    module Actions
      def search_with_clusters(arguments)
        arguments[:index] = '_all' if ! arguments[:index] && arguments[:type]

        method = 'POST'
        path   = Utils.__pathify( Utils.__listify(arguments[:index]), Utils.__listify(arguments[:type]), '_search_with_clusters' )

        title_types = arguments[:title_types].map { |word| "_source.#{word}" }
        content_types = arguments[:content_types].map { |word| "_source.#{word}" }
        body   = {
          search_request: {
            query: {
              match: {
                _all: arguments[:q]
              }
            },
            size: 100
          },
          query_hint: arguments[:q],
          field_mapping: {
            title: title_types,
            content: content_types
          },
          algorithm: arguments[:algorithm]
        }
        perform_request(method, path, {}, body).body
      end
    end
  end
end

class ElasticsearchClient
  def initialize
    @client = Elasticsearch::Client.new
  end

  def search_lemmas(query)
    @client.search index: 'news', type: 'annotated_article', body: {
      query: {
        match: {
          lemmas: query
        }
      }
    }
  end

  def search_with_clusters(query, title_types, content_types, algorithm)
    @client.search_with_clusters index: 'news', type: 'annotated_article', q: query,
      title_types: title_types, content_types: content_types,
      algorithm: algorithm
  end

  def annotated_article(id)
    begin
      return @client.get index: 'news', type: 'annotated_article', id: id
    rescue
    end
    nil
  end

  def index_annotated_article(article)
    @client.index index: 'news', type: 'annotated_article', id: article[:article_id], body: {
      title: article[:title],
      urlText: article[:url_text],
      createdAt: article[:created_at],
      pageId: article[:page_id],
      updateId: article[:update_id],
      url: article[:url],
      content: article[:content],
      lemmas: article[:lemmas],
      persons: article[:persons],
      organizations: article[:organizations],
      locations: article[:locations],
      dates: article[:dates]
    }
  end

  def remove_duplicate_articles
    result = @client.search index: 'news',
      type: 'annotated_article',
      scroll: '10m',
      body: { query: { match_all: {} } }

    articles = {}
    last_count = 0

    loop do
      scroll = @client.scroll scroll: '5m', scroll_id: result['_scroll_id']
      break if scroll['hits'].count == 0
      scroll['hits']['hits'].each do |article|
        (articles[article['_source']['title']] ||= []) << article['_id']
      end
      puts articles.count
      break if articles.count == last_count
      last_count = articles.count
    end

    puts articles.count

    articles.each do |title, ids|
      ids = ids.uniq
      next if ids.count == 1
      ids.shift
      ids.each do |id|
        @client.delete index: 'news', type: 'annotated_article', id: id
      end
    end
  end

  def dump_annotated_articles
    result = @client.search index: 'news',
      type: 'annotated_article',
      scroll: '10m',
      body: { query: { match_all: {} } }

    dumped_articles = []

    loop do
      scroll = @client.scroll scroll: '5m', scroll_id: result['_scroll_id']
      break if scroll['hits'].count == 0
      scroll['hits']['hits'].each do |article|
        next if dumped_articles.include? article['_id']
        dumped_articles << article['_id']
        puts "Dumping #{article['_id']}"
        article['_source']['id'] = article['_id']
        File.open("#{Environment.news_folder}annotated_articles_dump/#{article['_id']}.json",'w') do |f|
           f.write article['_source'].to_json
        end
      end
    end
  end
end
