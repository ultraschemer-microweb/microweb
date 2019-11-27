#!/usr/bin/env bash

if [ "${ORCL_HOST}" == "" ]; then
  ORCL_HOST=127.0.0.1
fi

if [ "${ORCL_PORT}" == "" ]; then
  ORCL_PORT=1521
fi

if [ "${ORCL_SID}" == "" ]; then
  ORCL_SID='orcl'
fi

if [ "${ORCL_USER}" == "" ]; then
  ORCL_USER='microweb'
fi

if [ "${ORCL_PASSWD}" == "" ]; then
  ORCL_PASSWD='microweb'
fi

sqlplus -S "${ORCL_USER}/${ORCL_PASSWD}@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=${ORCL_HOST})(PORT=${ORCL_PORT}))(CONNECT_DATA=(SID=${ORCL_SID})))" <<SQL_COMMANDS
BEGIN
  -- Remova all tables:
  FOR i IN (SELECT ut.table_name
              FROM USER_TABLES ut) LOOP
    EXECUTE IMMEDIATE 'drop table '|| i.table_name ||' CASCADE CONSTRAINTS ';
  END LOOP;  
END;
/
SQL_COMMANDS
