require 'spec_helper.rb'
require 'watchdog_server'
require 'json'

def app
  WatchDogServer
end

def test_user(unq = nil)
  user = Hash.new
  user['email']       = 'foo@bar.gr'
  user['name']        = 'Foo Bar'
  user['org']         = 'Baz B.V.'
  user['org_website'] = 'http://baz.nl'
  user['prize']       = false
  user['unq'] = if unq.nil? then (0...10).map{('a'..'z').to_a[rand(26)]}.join else unq end
  user
end

def test_interval(from, to)
  interval = Hash.new
  interval['ts'] = from
  interval['te'] = to
  interval
end

describe 'The WatchDog Server' do

  before(:each) do
    mongo = WatchDogServer.new.helpers.mongo
    mongo.drop_database('watchdog')
    mongo.close
  end

  it 'should woof' do
    get '/'
    expect(last_response).to be_ok
    expect(last_response.body).to eq('Woof Woof')
  end

  it 'should create users when the details are correct' do
    post '/user', test_user.to_json

    expect(last_response).to be_ok
  end

  it 'should return 400 on bad JSON request to /users' do
    post '/user', 'foobar'
    last_response.status.should eql(400)
  end

  #it 'should return the existing user when a user is re-added' do
  #  post '/user', test_user.to_json
  #end

  it 'should return 400 on bad JSON to /users/:id/intervals' do
    post '/user/foobar/intervals', 'foobar'
    last_response.status.should eql(400)
  end

  it 'should return 400 on non JSON array being sent to /users/:id/intervals' do
    post '/user/foobar/intervals', '{"foo":"bar"}'
    last_response.status.should eql(400)
  end

  it 'should return 400 when > 1000 intervals are sent at once' do
    intervals = (1..1001).map{|x| test_interval(x, x + 1)}

    post '/user/foobar/intervals', intervals.to_json
    last_response.status.should eql(400)
  end

  it 'should return 400 when negative intervals exist' do
    intervals = (1..10).map{|x| test_interval(x + 1, x)}

    post '/user/foobar/intervals', intervals.to_json
    last_response.status.should eql(400)
  end

  it 'should return 404 when posting intervals for non-existing user' do
    intervals = (1..10).map{|x| test_interval(x, x + 1)}

    post '/user/foobar/intervals', intervals.to_json
    last_response.status.should eql(404)
  end

  it 'should return then number of stored intervals on successful insert' do
    intervals = (1..10).map{|x| test_interval(x, x + 1)}
    user = test_user('foobar')
    post '/user', user.to_json
    user_id = last_response.body

    post "/user/#{user_id}/intervals", intervals.to_json
    last_response.status.should eql(201)
    expect(last_response.body).to eq('10')

  end

end