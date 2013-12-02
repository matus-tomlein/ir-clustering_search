require 'elasticsearch'

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

  def search(query)
    @client.search index: 'news', type: 'article', q: query
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

  def search_tech_lemmas(query)
    @client.search index: 'news', type: 'tech_article', body: {
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

  def article(id)
    begin
      return @client.get index: 'news', type: 'article', id: id
    rescue
    end
    nil
  end

  def annotated_article(id)
    begin
      return @client.get index: 'news', type: 'annotated_article', id: id
    rescue
    end
    nil
  end

  def tech_article(id)
    begin
      return @client.get index: 'news', type: 'tech_article', id: id
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

  def index_tech_article(article)
    @client.index index: 'news', type: 'tech_article', id: article[:article_id], body: {
      title: article[:title],
      published: article[:published],
      url: article[:url],
      feedUrl: article[:feed_url],
      content: article[:content],
      author: article[:author],
      lemmas: article[:lemmas],
      persons: article[:persons],
      organizations: article[:organizations],
      locations: article[:locations],
      dates: article[:dates]
    }
  end

  def recent_tech_articles
    @client.search index: 'news', type: 'tech_article', body: {
      sort: [
        published: { order: 'desc' }
      ],
      size: 10
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
end
