require "codeclimate-test-reporter"
CodeClimate::TestReporter.configure do |config|
  config.path_prefix = "WatchDogServer"
  config.git_dir = "../"
end
CodeClimate::TestReporter.start
require 'rack/test'

ENV['RACK_ENV'] = 'test'

module RSpecMixin
  include Rack::Test::Methods
end

RSpec.configure { |c| c.include RSpecMixin }
