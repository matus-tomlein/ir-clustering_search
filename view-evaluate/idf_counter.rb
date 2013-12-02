require 'singleton'
require 'rspec'

require_relative 'elasticsearch_client'
require_relative 'stopwords'
require_relative 'environment'

class IDFCounterBase
  @@file_path = Environment.news_folder + "idf.marshal"

  attr_reader :num_documents

  def initialize(load_from_file = true)
    if load_from_file
      @terms, @num_documents = if File.exists?(@@file_path)
                 File.open(@@file_path) do|file|
                   Marshal.load(file)
                 end
               else
                 [Hash.new(0), 0]
               end
    else
      @terms, @num_documents = Hash.new(0), 0
    end
  end

  def add_terms(terms)
    terms.uniq.each do |term|
      next if Stopwords.is_stopword? term
      next if term.scan(/\w/).count == 0

      @terms[term] += 1
    end
    @num_documents += 1
  end

  def get_term_idf(term)
    @terms[term].to_i
  end

  def save_to_file
    File.open(@@file_path,'w') do |file|
      Marshal.dump([@terms, @num_documents], file)
    end
  end

  def get_weighted_terms(terms)
    term_counts = Hash.new(0)
    terms.each { |term| term_counts[term] += 1 }

    term_idfs = {}

    term_counts.each do |term, count|
      idf = get_term_idf term
      wtf = 1 + Math.log(count)
      if idf > 0
        idf = Math.log(@num_documents.to_f / idf)
      end
      term_idfs[term] = idf * wtf
    end

    term_idfs
  end
end

class IDFCounter < IDFCounterBase
  include Singleton
end

describe IDFCounter do
  it "should rank more frequent terms higher" do
    weights = IDFCounter.instance.get_weighted_terms ['dog', 'dog', 'cat']
    weights['dog'].should > weights['cat']
  end
end
