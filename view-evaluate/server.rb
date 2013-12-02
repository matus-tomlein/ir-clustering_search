require 'sinatra'
require 'sinatra/base'
require 'json'
require 'haml'

require_relative 'clustering_search'
require_relative 'elasticsearch_client'
require_relative 'environment'

class Server < Sinatra::Base
  def initialize
    super
  end

  set :port, 80 if Environment.use_port_80
  set :environment, :production if Environment.use_port_80

  get "/api/clustering_search.json" do
    clustering_search = ClusteringSearch.new params[:q], params[:title_types].split(';'), params[:content_types].split(';'), params[:algorithm]
    content_type :json
    return clustering_search.clusters.to_json
  end

  get "/search" do
    haml :search
  end
end

Server.run!
