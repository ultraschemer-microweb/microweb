#!/bin/bash -e

#
# Main procedures:
#
SCRIPTPATH=$0
CURRDIR=$(dirname "${SCRIPTPATH}")

echo "Generating ~/.pgpass file..."

echo "localhost:5432:starringnews:starringnews:starringnews" > ~/.pgpass
chmod 0600 ~/.pgpass

echo "Generating database."
echo
echo -n "Constructing schema: "
for fname in $(ls Schema); do
  psql -f "${CURRDIR}/Schema/${fname}" -h localhost -p 5432 starringnews starringnews
  echo -n "."
done
echo
echo
echo -n "Running seeds: "
for fname in $(ls Seeds); do
  psql -f "${CURRDIR}/Seeds/${fname}" -h localhost -p 5432 starringnews starringnews
  echo -n "."
done
echo
echo

