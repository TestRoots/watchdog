require 'spec_helper.rb'
require 'watchdog_server'
require 'json'

def app
  WatchDogServer
end

def test_user
  user = Hash.new
  user['email']                 = 'foo@bar.gr'
  user['name']                  = 'Foo Bar'
  user['org']                   = 'Baz B.V.'
  user['org_website']           = 'http://baz.nl'
  user['prize']                 = false
  user['programmingExperience'] = '1-2 years'
  user
end

def empty_user
  user = Hash.new
  user['programmingExperience'] = nil
  user
end

existing_user = nil
existing_project = nil

def test_project(user_id)
  project = Hash.new
  project['name']        = 'Foo Bar Proj'
  project['role']        = 'Foo Barer'
  project['belongToASingleSofware'] = true
  project['usesJunit'] = true
  project['usesOtherFrameworks'] = false
  project['productionPercentage'] = 50
  project['useJunitOnlyForUnitTesting'] = false
  project['followTestDrivenDesign'] = false
  project['userId'] = user_id
  project
end

def test_interval(from, to)
  interval = Hash.new
  interval['ts'] = from
  interval['te'] = to
  interval
end

def test_event(timestamp)
  event = Hash.new
  event['ts'] = timestamp
  event
end

describe 'The WatchDog Server' do

  before(:each) do
    mongo = WatchDogServer.new.helpers.mongo
    mongo.close
  end

  it 'should woof' do
    get '/'
    expect(last_response).to be_ok
    expect(last_response.body).to eq('Woof Woof')
  end

  it 'should get client version' do
    get '/client'
    expect(last_response).to be_ok
    expect(last_response.body).to eq('"1.6.0"')
  end

  it 'should create a user when the details are correct' do
    post '/user', test_user.to_json

    last_response.status.should eql(201)
    expect(last_response.body).to match(/^[0-9a-z]{40}$/)
    existing_user = last_response.body
  end

  it 'should return 400 on bad JSON request to /user' do
    post '/user', 'foobar'
    last_response.status.should eql(400)
  end

  it 'should create a project when the details are correct' do
    post '/project', test_project(existing_user).to_json

    last_response.status.should eql(201)
    expect(last_response.body).to match(/^[0-9a-z]{40}$/)
    existing_project = last_response.body
  end

  it 'should return 400 on bad JSON request to /project' do
    post '/project', 'foobar'
    last_response.status.should eql(400)
  end

  it 'should not create a project when the user does not exist' do
    post '/project', test_project(nil).to_json

    last_response.status.should eql(404)
  end

  it 'should return 400 on bad JSON to /users/:id/intervals' do
    post '/user/foobar/foobarproject/intervals', 'foobar'
    last_response.status.should eql(400)
  end

  it 'should return 400 on non JSON array being sent to /users/:id/intervals' do
    post '/user/foobar/foobarproject/intervals', '{"foo":"bar"}'
    last_response.status.should eql(400)
  end

  it 'should return 400 when negative intervals exist' do
    intervals = (1..10).map{|x| test_interval(x + 1, x)}

    post '/user/foobar/foobarproj/intervals', intervals.to_json
    last_response.status.should eql(400)
  end

  it 'should return 400 on bad JSON to /users/:id/:pid/events' do
    post '/user/foobar/foobarproject/events', 'foobar'
    last_response.status.should eql(400)
  end

  it 'should return 400 on non JSON array being sent to /users/:id/:pid/events' do
    post '/user/foobar/foobarproject/events', '{"foo":"bar"}'
    last_response.status.should eql(400)
  end

  it 'should return 404 for non-existing user' do
    get '/user/noexistingfoobar'
    last_response.status.should eql(404)
  end

  it 'should return 200 for existing user' do
    get '/user/' + existing_user
    last_response.status.should eql(200)
  end

 it 'should return 200 for existing project' do
    get '/project/' + existing_project
    last_response.status.should eql(200)
  end

  it 'should return 404 when posting intervals for non-existing user' do
    intervals = (1..10).map{|x| test_interval(x, x + 1)}

    post '/user/foobar/foobarprojects/intervals', intervals.to_json
    last_response.status.should eql(404)
  end

  it 'should return 404 when posting intervals for non-existing project' do
    intervals = (1..10).map{|x| test_interval(x, x + 1)}

    post '/user/' + existing_user + '/intervals', intervals.to_json
    last_response.status.should eql(404)
  end

  it 'should return 404 when posting events for non-existing user' do
    events = (1..10).map{|x| test_event(x)}

    post '/user/noexistingfoobar/foobarprojects/events', events.to_json
    last_response.status.should eql(404)
  end

  it 'should return 404 when posting events for non-existing project' do
    events = (1..10).map{|x| test_event(x)}

    post '/user/' + existing_user + '/noexistingfoobarproject/events', events.to_json
    last_response.status.should eql(404)
  end
  
  it 'should return 404 when trying to register a user with missing programming experience' do
    post '/user', empty_user.to_json
	last_response.status.should eql(404)
  end  

  it 'should return the number of stored intervals on successful insert' do
    intervals = (1..10).map{|x| test_interval(x, x + 1)}
    user = test_user
    post '/user', user.to_json
    user_id = last_response.body
    project = test_project(user_id)
    post '/project', project.to_json
    project_id = last_response.body

    post "/user/#{user_id}/#{project_id}/intervals", intervals.to_json
    last_response.status.should eql(201)
    expect(last_response.body).to eq('10')
  end

  it 'should return the number of stored events on successful insert' do
    events = (1..10).map{|x| test_event(x)}
    user = test_user
    post '/user', user.to_json
    user_id = last_response.body
    project = test_project(user_id)
    post '/project', project.to_json
    project_id = last_response.body

    post "/user/#{user_id}/#{project_id}/events", events.to_json
    last_response.status.should eql(201)
    expect(last_response.body).to eq('10')
  end

end
