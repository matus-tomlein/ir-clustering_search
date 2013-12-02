require_relative 'clustering_search'
require_relative 'similarity'

def calculate_average_intra_cluster_similarity(clustering_search)
  similarities = []
  clustering_search.clusters.each do |cluster|
    next if cluster['label'] == 'Other Topics'
    similarities << calculate_intra_cluster_similarity(cluster)
  end
  return 0 if similarities.size == 0
  similarities.inject{ |sum, el| sum + el.to_f }.to_f / similarities.size
end

def calculate_intra_cluster_similarity(cluster)
  similarities = []
  cluster['documents'].each do |article1|
    cluster['documents'].each do |article2|
      sim = Similarity.cosine_similarity(article1['_source']['lemmas'].split, article2['_source']['lemmas'].split).to_f
      similarities << if sim.nan?
        0.0
      else
        sim
      end
    end
  end
  return 0 if similarities.size == 0
  similarities.inject{ |sum, el| sum + el.to_f }.to_f / similarities.size
end

def run_clustering_experiment
  queries = ['government shutdown', 'barack obama', 'car', 'car accident', 'football', 'hockey', 'new york', 'terrorist attack']
  keys = [
    [['title'], ['lemmas']],
    [['title'], ['persons']],
    [['title'], ['locations']],
    [['title'], ['persons', 'locations']], 
    [['title'], ['persons', 'locations', 'organizations']]
  ]

  results = {}

  queries.each do |query|
    keys.each do |key|
      clustering_search = ClusteringSearch.new query, key.first, key.last, 'lingo'
      sim = calculate_average_intra_cluster_similarity clustering_search
      puts "#{query} #{key.to_json} #{sim}"
      (results[query] ||= {})[key.last.map(&:capitalize).join(' ')] = sim
    end
  end

  puts "RESULTS"
  puts
  puts results.to_json
  puts
end

run_clustering_experiment
