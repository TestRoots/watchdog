require 'digest/sha1'

require 'sinatra'
require 'mongo'
require 'sinatra/contrib'

class WatchDogServer < Sinatra::Base
  include Mongo

  # Configuration file support
  register Sinatra::ConfigFile
  config_file '../config.yaml'

  # Application-wide configuration
  configure :development, :production do

    # Make sure our MongoDB has the appropriate collections/indexes
    COLLECTIONS = %w(users intervals)

    IDXS = {
        :users      => %w(unq),
        :intervals  => %w(ts te)
    }

    db = MongoClient.new(settings.mongo_host, settings.mongo_port).db(settings.mongo_db)

    # Trigger creation of collections
    COLLECTIONS.each{|c| db.collection(c)}

    # Ensure that the necessary indexes exist
    IDXS.each do |k,v|

      col = db.collection(k.to_s)
      idx_name = v.join('_1_') + '_1'
      idx_exists =  col.index_information.find {|k,v| k == idx_name}

      if idx_exists.nil?
        idx_fields = v.reduce({}){|acc, x| acc.merge({x => 1})}

        col.create_index(idx_fields, :background => true)
        STDERR.puts "Creating index on #{col}(#{v})"
      end
    end

    # Do not support static files
    set :static, false

    # Enable request logging
    enable :logging
  end

  def mongo
    MongoClient.new(settings.mongo_host, settings.mongo_port)
  end

  ## The API
  before  do
    @db ||= mongo.db(settings.mongo_db)
  end

  after do
    @db.connection.close
    @db = nil
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