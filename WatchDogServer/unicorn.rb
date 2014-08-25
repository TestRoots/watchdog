env = ENV['RACK_ENV'] || 'production'

# 6 workers in production, 1 for development
worker_processes (env == 'production' ? 6 : 1)

timeout 15

preload_app true

if env == 'production'
  listen "/tmp/unicorn.watchdog.socket"
else
  puts "Starting WatchDog in development mode (127.0.0.1:3000) ..."
  listen "127.0.0.1:3000" 
end
