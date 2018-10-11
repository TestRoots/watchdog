require 'digest/sha1'

require 'sinatra'
require 'mongo'
require 'sinatra/contrib'
require 'json'
require 'net/smtp'
require 'logger'
require 'geocoder'
require 'yaml'

class WatchDogServer < Sinatra::Base
  logger = Logger.new('logfile.log')
  include Mongo

  def mongo
    @serverconfig ||= YAML.load_file('config.yaml')[settings.environment.to_s]
    Mongo::Client.new(
      ["#{@serverconfig['mongo_host']}:27017"],
      :database => @serverconfig['mongo_db'],
      user: @serverconfig['mongo_username'],
      password: @serverconfig['mongo_password']
    )
  end

  # Setup database connection
  before  do
    mongo
    @db = mongo.database
  end

  after do
    mongo.close
    @db = nil
  end

  # Do not support static files
  set :static, false

  # Enable request logging
  enable :logging

  # Enable post payloads up to 4MB
  Rack::Utils.key_space_limit = 4914304
  logger.info("key_space_limit=#{Rack::Utils.key_space_limit}")

  Geocoder.configure(:timeout => 3, :lookup => :google)

  get '/' do
    'Woof Woof'
  end

  # Get info about stored user
  get '/client' do
    client_version = "3.1.0"

    status 200
    body client_version.to_json
  end

  # Get info about stored user
  get '/user/:id' do
    get_entity_info('get_user_by_id', params[:'id'])
  end

  # Get info about stored project
  get '/project/:id' do
    get_entity_info('get_project_by_id', params[:'id'])
  end


  # Return info about stored entity
  def get_entity_info(info_function, id)
    stored_entity = self.send(info_function, id)

    if stored_entity.nil?
      halt 404, "Entity does not exist"
    else
      status 200
      body stored_entity['id'].to_json
    end
  end

  # Create a new user and return unique SHA1
  post '/user' do
    user = create_json_object(request)

	if user['programmingExperience'].nil? or user['programmingExperience'].empty?
	  halt 404, "Missing programming experience in user registration"
	end

    sha = create_40_char_SHA
    logger.info user

    user['id'] = sha

    begin
      user['country'] = request.location.country
    rescue
      user['country'] = 'NA'
      logger.warn user
    end
    begin
      user['city'] = request.location.city
    rescue
      user['city'] = 'NA'
    end
    begin
      user['postCode'] = request.location.postal_code
    rescue
      user['postCode'] = 'NA'
    end

    add_ip_timestamp(user, request)

    users.insert_one(user)
    stored_user = get_user_by_id(sha)

    unless user['email'].nil? or user['email'].empty?
      send_registration_email(USER_REGISTERED, user['email'], sha, nil)
    end

    status 201
    body stored_user['id']
  end

  # Create a new project and return unique SHA1
  post '/project' do
    project = create_json_object(request)
    logger.info project

    associated_user = get_user_by_id(project['userId'])
    if associated_user.nil?
      halt 404, "The user who registers the project does not exist on the server. Create a new user first."
    end

    sha = create_40_char_SHA()

    project['id'] = sha

    add_ip_timestamp(project, request)

    projects.insert_one(project)

    unless associated_user['email'].nil? or associated_user['email'].empty?
      send_registration_email(PROJECT_REGISTERED, associated_user['email'], sha, project['name'])
    end

    status 201
    body sha
  end

  # Create new intervals
  post '/user/:uid/:pid/intervals' do
    ivals = create_json_object(request)

    unless ivals.kind_of?(Array)
      halt 400, 'Wrong request, body is not a JSON array'
    end

    if ivals.size > 100000
      halt 413, 'Request too long (> 100000 intervals)'
    end

    negative_intervals = ivals.find{|x| (x['te'].to_i - x['ts'].to_i) < 0}

    unless negative_intervals.nil?
      halt 400, 'Request contains negative intervals'
    end

    user_id = params[:uid]
    user = get_user_by_id(user_id)

    if user.nil?
      halt 404, "User does not exist"
    end

    project_id = params[:pid]
    project = get_project_by_id(project_id)

    if project.nil?
      halt 404, "Project does not exist"
    end

    ivals.each do |i|
      begin
        i['userId'] = user_id
        i['projectId'] = project_id
        add_ip_timestamp(i, request)
        intervals.insert_one(i)
      rescue IndexError => e
        log.error "IndexError occurred. Interval: #{i}"
        log.error e.backtrace
      rescue StandardError => e
        log.error "Unexpected error: #{e.message}"
        log.error e.backtrace
      end
    end

    status 201
    body ivals.size.to_s
  end

  # Create new events
  post '/user/:uid/:pid/events' do
    evs = create_json_object(request)

    unless evs.kind_of?(Array)
      halt 400, 'Wrong request, body is not a JSON array'
    end

    if evs.size > 100000
      halt 413, 'Request too long (> 100000 events)'
    end

    user_id = params[:uid]
    user = get_user_by_id(user_id)

    if user.nil?
      halt 404, "User does not exist"
    end

    project_id = params[:pid]
    project = get_project_by_id(project_id)

    if project.nil?
      halt 404, "Project does not exist"
    end

    evs.each do |i|
      begin
        i['userId'] = user_id
        i['projectId'] = project_id
        add_ip_timestamp(i, request)
        events.insert_one(i)
      rescue IndexError => e
        log.error "IndexError occurred. Event: #{i}"
        log.error e.backtrace
      rescue StandardError => e
        log.error "Unexpected error: #{e.message}"
        log.error e.backtrace
      end
    end

    status 201
    body evs.size.to_s
  end

  private

  def users
    @db[:users]
  end

  def projects
    @db[:projects]
  end

  def intervals
    @db[:intervals]
  end

  def events
    @db[:events]
  end

  def get_user_by_id(id)
    users.find(id: id).limit(1).first
  end

  def get_project_by_id(id)
    projects.find(id: id).limit(1).first
  end

  # creates a json object from a http request
  def create_json_object(request)
     begin
      object = JSON.parse(request.body.read)
    rescue StandardError => e
      logger.error e
      halt 400, "Wrong JSON object #{request.body.read}"
    end
    return object
  end

  def add_ip_timestamp(object, request)
    object['ip'] = request.ip
    object['regDate'] = Time.now
  end

  # creates a 40 character long SHA hash
  def create_40_char_SHA()
    rnd = (0...100).map { ('a'..'z').to_a[rand(26)] }.join
    return Digest::SHA1.hexdigest rnd
  end

  # sends a registration mail
  def send_registration_email(mailtext, email, id, projectname)
    text = sprintf(mailtext, Time.now.rfc2822, id, projectname, id)

    Net::SMTP.start('localhost', 25, 'testroots.org') do |smtp|
      begin
        smtp.send_message(text, 'info@testroots.org', email)
      rescue StandardError => e
        logger.error "Failed to send email to #{email}: #{e.message}"
        logger.error e.backtrace.join("\n")
      end
    end
   rescue Errno::ECONNREFUSED => e
        logger.error "NET::SMTP.start failure"
        logger.error e.backtrace.join("\n")
  end


  USER_REGISTERED = <<-END_EMAIL
Subject: Your new WatchDog user id
Date: %s

Dear WatchDog user,

You recently registered with WatchDog.

Your user id is: %s

You can use this id to link other WatchDog installations to your user. Remember
that you only start sending us data once you also register a possibly anonymous
project. We will send you one other email once you do so.

Thank you for contributing to science -- with WatchDog!

Moritz, Georgios, Annibale, Igor and Andy
--
The TestRoots team
http://www.testroots.org - http://www.tudelft.nl
  END_EMAIL


  PROJECT_REGISTERED = <<-END_EMAIL
Subject: Your new WatchDog project id
Date: %s

Dear WatchDog user,

You recently registered a new project with WatchDog.

Your new project id is: %s
Your project name is: %s

You can use this id for other workspaces where you work on the same project.
If your colleagues work on the same project, please ask them to create new
project ids, but with the same project name.

You will be able to access your custom-generated project report under:
http://www.testroots.org/reports/project/%s.html

Please give us a time frame of 24 hours to create your report.

Thank you for contributing to science -- with WatchDog!

Moritz, Georgios, Annibale, Igor and Andy
--
The TestRoots team
http://www.testroots.org - http://www.tudelft.nl
  END_EMAIL

end
