#!/bin/bash -xe

#
# Access configurations:
#
DATABASE=microweb
USERNAME=microweb
PASSWORD=microweb

#
# Main procedures:
#
SCRIPTPATH=$0
CURRDIR=$(dirname "${SCRIPTPATH}")

echo "Generating ~/.pgpass file..."

echo "localhost:5432:${DATABASE}:${USERNAME}:${PASSWORD}" > ~/.pgpass
chmod 0600 ~/.pgpass

echo "Generating database."
echo
echo -n "Constructing schema: "
for fname in $(ls Schema); do
  PGPASSFILE=${HOME}/.pgpass psql -f "${CURRDIR}/Schema/${fname}" -h localhost -p 5432 ${USERNAME} ${DATABASE}
  echo -n "."
done
echo
echo
echo -n "Running seeds: "
for fname in $(ls Seeds); do
  PGPASSFILE=${HOME}/.pgpass psql -f "${CURRDIR}/Seeds/${fname}" -h localhost -p 5432 ${USERNAME} ${DATABASE}
  echo -n "."
done
echo
echo

