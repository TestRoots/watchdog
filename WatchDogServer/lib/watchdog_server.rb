require 'digest/sha1'

require 'sinatra'
require 'mongo'
require 'sinatra/contrib'
require 'json'
require 'net/smtp'

class WatchDogServer < Sinatra::Base
  include Mongo

  def mongo
    MongoClient.new("localhost", 27017)
  end

  ## The API
  before  do
    @db ||= mongo.db('watchdog')
  end

  after do
    @db.connection.close
    @db = nil
  end


  # Do not support static files
  set :static, false

  # Enable request logging
  enable :logging

  get '/' do
    'Woof Woof'
  end

  # Get info about stored user
  get '/user/:id' do
    # TODO (MMB) check userid
    stored_user = get_user_by_id(params[:'id'])

    if stored_user.nil?
      halt 404, "User does not exist"
    else
      status 200
      body stored_user.to_json
    end
  end

  # Create a new user and return unique SHA1
  post '/user' do
    begin
      user = JSON.parse(request.body.read)
    rescue Exception => e
      puts e
      halt 400, "Wrong JSON object #{request.body.read}"
    end

    rnd = (0...100).map { ('a'..'z').to_a[rand(26)] }.join
    sha = Digest::SHA1.hexdigest rnd

    user['id'] = sha
    users.save(user)
    stored_user = get_user_by_id(sha)

    unless user['email'].nil? or user['email'].empty?
      send_registration_email(user['email'], sha)
    end

    status 201
    body stored_user['id']
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

    # TODO (MMB) check userid
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

  private

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

  def send_registration_email(email, user_id)
    text = sprintf(USER_REGISTERED, Time.now, user_id)

    Net::SMTP.start('localhost', 25, 'testroots.org') do |smtp|
      begin
        smtp.send_message(text, 'info@testroots.org', email)
      rescue Exception => e
        logger.error "Failed to send email to #{email}: #{e.message}"
        logger.error e.backtrace.join("\n")
      end
    end
  end

  USER_REGISTERED = <<-END_EMAIL
Subject: Your Watchdog user id
Date: %s

Dear Watchdog user,

You recently registered with the Watchdog server.

Your user id is: %s

You can use this user id to link another Watchdog installation to your account.

Thank you for using Watchdog!
The Watchdog team
  END_EMAIL

end
