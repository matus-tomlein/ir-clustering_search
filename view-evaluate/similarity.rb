require 'rspec'

require_relative 'idf_counter'

class Similarity
  def self.cosine_similarity(terms1, terms2)
    weighted_terms1 = IDFCounter.instance.get_weighted_terms terms1
    weighted_terms2 = IDFCounter.instance.get_weighted_terms terms2

    cosine_similarity_weighted_terms weighted_terms1, weighted_terms2
  end

  def self.cosine_similarity_weighted_terms(weighted_terms1, weighted_terms2)
    score = 0.0

    weighted_terms2.each do |term, weight|
      score += weighted_terms1[term] * weight if weighted_terms1.has_key? term
    end

    score / (weighted_terms1.count * weighted_terms2.count)
  end
end

describe Similarity do
  it "should give a high score for the same terms" do
    Similarity.cosine_similarity(['dog', 'cat', 'fox'], ['dog', 'fox', 'cat']).should > 0.9
  end

  it "should give a low score for different terms" do
    Similarity.cosine_similarity(['dog', 'cat', 'fox'], ['human', 'alien', 'door']).should < 0.1
  end

  it "should give a higher score for more similar terms" do
    Similarity.cosine_similarity(['dog', 'cat', 'fox'], ['dog', 'alien', 'fox']).should >
      Similarity.cosine_similarity(['dog', 'cat', 'fox'], ['dog', 'alien', 'door'])
  end
end
