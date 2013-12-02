require 'json'
require 'rspec'

class Environment
  @@loaded = false
  @@environment = {}

  def self.load
    return if @@loaded
    @@loaded = true

    @@environment = JSON.parse(File.read('environment.json').force_encoding('utf-8'))
  end

  def self.news_folder
    @@environment['news_folder']
  end

  def self.crawled_data_folder
    @@environment['crawled_data_folder']
  end

  def self.use_port_80
    @@environment['use_port_80']
  end

  def self.can_include_stanford_nlp?
    @@environment['can_include_stanford_nlp']
  end
end

Environment.load

describe Environment do
  it "should load correctly" do
    Environment.news_folder[0].should == '/'
    Environment.crawled_data_folder[0].should == '/'
  end
end
