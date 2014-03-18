require 'spec_helper.rb'
require 'watchdog_server'
require 'json'

def app
  WatchDogServer
end

def test_user
  user = Hash.new
  user['email']       = 'foo@bar.gr'
  user['name']        = 'Foo Bar'
  user['org']         = 'Baz B.V.'
  user['org_website'] = 'http://baz.nl'
  user['prize']       = false
  user
end

describe 'The WatchDog Server' do

  before(:each) do
    WatchDogServer.mongo.database
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

  it 'should return the existing user when a user is re-added' do
    post '/user', test_user.to_json
  end

end