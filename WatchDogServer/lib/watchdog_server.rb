require 'digest/sha1'

require 'sinatra'
require 'mongo'
require 'sinatra/contrib'

class WatchDogServer < Sinatra::Base
  include Mongo

  # Configuration file support
  register Sinatra::ConfigFile
  config_file 'config.yaml'

  # Do not support static files
  set :static, false

  def self.mongo
    MongoClient.new("localhost", 27017).db('watchdog')
  end

  before '/user' do
    Thread.current[:db] ||= mongo
  end

  after '/user' do
    #Thread.current[:db].close
  end

  get '/' do
    'Woof Woof'
  end

  # Get info about stored user
  get '/user/:id' do

  end

  # Create a new user and return unique SHA1
  post '/user' do
    begin
      user = JSON.parse(request.body.read)
    rescue
      return [400, {}, "Wrong JSON object #{request.body.read}"]
    end

    Thread.current[:db].find_one('')

    rnd = (0...100).map { ('a'..'z').to_a[rand(26)] }.join
    sha = Digest::SHA1.hexdigest rnd

    Thread.current[:db].collection('users').save(user)
  end

  # Delete a user
  delete '/user/:id' do

  end

  # Get user intervals
  get '/user/:id/intervals' do

  end

  # Create new intervals
  post '/user/:id/intervals' do

  end

end