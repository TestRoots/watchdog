To run the server, first set up the correct environment.

Make sure mongo is running:

```bash
./mongo_process_is_running.sh
```
If it is not running, run something along the lines of

```bash
sudo service mongod start
```

Now make sure that the correct database is running with a new user.
Since you probably do not have it, run the following commands:

```bash
mongo
```
Once inside the mongoDB server, execute the following commands:

```bash
use watchdog-dev
db.createUser({user: "foo", pwd: "bar", roles: []})
exit
```

Then make sure to configure the Ruby server to take the development profile

```bash
export RACK_ENV=development
```

Then run the server with

```bash
./start_server.sh
```

At this point the server is running on localhost:3000.
Go to localhost:3000/ to verify the server returns "Woof Woof" and localhost:3000/client returns the client version ("X.X.X")
