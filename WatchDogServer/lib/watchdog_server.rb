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

  def mongo
    MongoClient.new("localhost", 27017)
  end

  ## The API
  before  do
    @db ||= mongo.db('watchdog')
  end

  after do
    #@db.connection.close
    #@db = nil
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
      halt 400, "Wrong JSON object #{request.body.read}"
    end

    if user['unq'].nil?
      halt 400, 'Missing field: unq from request'
    end

    stored_user = get_user_by_unq(user['unq'])

    if stored_user.nil?
      rnd = (0...100).map { ('a'..'z').to_a[rand(26)] }.join
      sha = Digest::SHA1.hexdigest rnd

      user['id'] = sha
      users.save(user)
      stored_user = get_user_by_id(sha)
    end

    status 201
    body stored_user['id']
  end

  # Delete a user
  delete '/user/:id' do

  end

  # Get user intervals
  get '/user/:id/intervals' do

  end

  # Create new intervals
  post '/user/:id/intervals' do
    begin
      ivals = JSON.parse(request.body.read)
    rescue
      halt 400, "Wrong JSON object #{request.body.read}"
    end

    unless ivals.kind_of?(Array)
      halt 400, 'Wrong request, body is not a JSON array'
    end

    if ivals.size > 1000
      halt 400, 'Request too long (> 1000 intervals)'
    end

    negative_intervals = ivals.find{|x| (x['te'].to_i - x['ts'].to_i) < 0}

    unless negative_intervals.nil?
      halt 400, 'Request contains negative intervals'
    end

    user_id = params[:id]
    user = get_user_by_id(user_id)

    if user.nil?
      halt 404, "User does not exist"
    end

    ivals.each do |i|
      intervals.save(i)
    end

    status 201
    body ivals.size.to_s
  end

  def users
    @db.collection('users')
  end

  def intervals
    @db.collection('intervals')
  end

  def get_user_by_id(id)
    users.find_one({'id' => id})
  end

  def get_user_by_unq(unq)
    users.find_one({'unq' => unq})
  end

end