#!/usr/bin/env ruby

require 'parallel'
require 'rest_client'
require 'json'

def test_user
  user = Hash.new
  user['email']       = 'foo@bar.gr'
  user['name']        = 'Foo Bar'
  user['org']         = 'Baz B.V.'
  user['org_website'] = 'http://baz.nl'
  user['prize']       = false
  user
end

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

def interval

  int = rand(1..2**32 - 50)

  [{
    :doc => {
    "pn" => "foobar",
    "fn" => "debug.rel",
    "sloc" => rand(1 .. 50),
    "dt" => "un"
    },

    :it => "re",
    :ts => int,
    :te => int + rand(1..50),
    :ss => rand(1..2**32 - 1),
    :wdv => "1.0-SNAPSHOT"
  }]
end

# Configuration params
username = "foobar"
passwd = "passwd"
urlbase = "http://#{username}:#{passwd}@watchdog.testroots.org"
num_intervals = 10000

#create test user
@user_id = RestClient.post(urlbase + '/user', test_user.to_json)
puts "user_id=#{@user_id}"

#create test project
@pid = RestClient.post(urlbase + '/project', test_project(@user_id).to_json)
puts "pid=#{@pid}"

loader = Proc.new do |req_id|
    resp = RestClient.post(urlbase + "/user/#{@user_id}/#{@pid}/intervals",
                           interval.to_json)
    puts "Req id: #{req_id}, resp: #{resp}"
end

start = Time.now.to_i

Parallel.map((1..num_intervals), :in_threads => 30) do |req|
  loader.call(req)
end

delta = Time.now.to_i - start
puts "#{num_intervals} intervals in #{delta} secs #{(num_intervals/delta).to_f}
intervals per sec"

