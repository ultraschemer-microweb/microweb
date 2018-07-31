#!/usr/bin/env bash
psql --command "CREATE USER example WITH PASSWORD 'example';"
createdb -O example example
psql --command "GRANT ALL PRIVILEGES ON DATABASE example TO example"
psql --command 'CREATE EXTENSION "uuid-ossp"' example
psql --command 'CREATE EXTENSION "pgcrypto"' example