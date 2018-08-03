#!/usr/bin/env bash
psql --command "CREATE USER microweb WITH PASSWORD 'microweb';"
createdb -O microweb microweb
psql --command "GRANT ALL PRIVILEGES ON DATABASE microweb TO microweb"
psql --command 'CREATE EXTENSION "uuid-ossp"' microweb
psql --command 'CREATE EXTENSION "pgcrypto"' microweb