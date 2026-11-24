COLUMN patch_file_name NEW_VALUE patch_file_name NOPRINT
column patch_file_name new_value patch_file_name noprint;
select '''logon_create_db_01_user''' as patch_file_name from dual;
@patch_spool_on.sql
COLUMN patch_file_name CLEAR

PROMPT************************************
PROMPT Create Role RL_LGN_OWNER          *
PROMPT************************************
--
CREATE ROLE RL_LGN_OWNER NOT IDENTIFIED;
-- Since LOGON 2.1 we now systematically drop the roles together with the users. I.e., this info message is not needed anymore.
-- prompt If error occurs, accept it: Roles are not dropped on purpose, i.e. next creation will fail.
GRANT ALTER SESSION TO RL_LGN_OWNER;
GRANT CREATE SESSION TO RL_LGN_OWNER;
GRANT CREATE MATERIALIZED VIEW TO RL_LGN_OWNER;
GRANT CREATE PROCEDURE TO RL_LGN_OWNER;
GRANT CREATE SEQUENCE TO RL_LGN_OWNER;
GRANT CREATE SYNONYM TO RL_LGN_OWNER;
GRANT CREATE TABLE TO RL_LGN_OWNER;
GRANT CREATE TRIGGER TO RL_LGN_OWNER;
GRANT CREATE TYPE TO RL_LGN_OWNER;
GRANT CREATE VIEW TO RL_LGN_OWNER;
GRANT QUERY REWRITE TO RL_LGN_OWNER;

PROMPT************************************
PROMPT Create Role RL_LGN_DB_ANALIZER    *
PROMPT************************************

--------------------------------------------------------------
-- This role is intended to use for DB performance analyzis
-- on live systems
--------------------------------------------------------------

CREATE ROLE RL_LGN_DB_ANALIZER NOT IDENTIFIED;
GRANT SELECT ANY DICTIONARY TO RL_LGN_DB_ANALIZER;
GRANT SELECT_CATALOG_ROLE TO RL_LGN_DB_ANALIZER;
GRANT SELECT ANY SEQUENCE TO RL_LGN_DB_ANALIZER;
GRANT SELECT ANY TABLE TO RL_LGN_DB_ANALIZER;

--------------------------------------------------------------
-- There should be an 'adnovum' user created by Crh
-- for creating backups, restore points, etc.
-- This user should have ADMIN rights.
--
-- After verifing that it exists and has the proper access rights,
-- you should run the following
-- in order to use it for DB performance analizer tasks:
-- GRANT RL_LGN_DB_ANALIZER TO adnovum;
-- GRANT RL_LGN_LOGIN TO adnovum;
--------------------------------------------------------------


PROMPT************************************
PROMPT Create Role RL_LGN_USER           *
PROMPT************************************
--
CREATE ROLE RL_LGN_USER NOT IDENTIFIED;
-- Since LOGON 2.1 we now systematically drop the roles together with the users. I.e., this info message is not needed anymore.
-- prompt If error occurs, accept it: Roles are not dropped on purpose, i.e. next creation will fail.
GRANT ALTER SESSION TO RL_LGN_USER;
GRANT CREATE SESSION TO RL_LGN_USER;

PROMPT************************************
PROMPT Create Role RL_LGN_ETL           *
PROMPT************************************
--
CREATE ROLE RL_LGN_ETL NOT IDENTIFIED;
-- Since LOGON 2.1 we now systematically drop the roles together with the users. I.e., this info message is not needed anymore.
-- prompt If error occurs, accept it: Roles are not dropped on purpose, i.e. next creation will fail.

PROMPT************************************
PROMPT Create Role RL_LGN_LOGIN          *
PROMPT************************************
--
CREATE ROLE RL_LGN_LOGIN NOT IDENTIFIED;
-- Since LOGON 2.1 we now systematically drop the roles together with the users. I.e., this info message is not needed anymore.
-- prompt If error occurs, accept it: Roles are not dropped on purpose, i.e. next creation will fail.
GRANT ALTER SESSION TO RL_LGN_LOGIN;
GRANT CREATE SESSION TO RL_LGN_LOGIN;

PROMPT************************************
PROMPT Create User ULGN01                *
PROMPT************************************
--
CREATE USER ULGN01
PROFILE DEFAULT IDENTIFIED BY ulgn01
DEFAULT TABLESPACE DATA_LGN_A
TEMPORARY TABLESPACE TEMP
QUOTA UNLIMITED ON DATA_LGN_A
QUOTA UNLIMITED ON INDEX_LGN_A
ACCOUNT UNLOCK;

GRANT RL_LGN_OWNER TO ULGN01;

/
GRANT SELECT ON "SYS"."ALL_TAB_COLUMNS" TO "ULGN01" WITH GRANT OPTION
/
GRANT SELECT ON "SYS"."ALL_COL_COMMENTS" TO "ULGN01" WITH GRANT OPTION
/
GRANT SELECT ON "SYS"."ALL_TAB_COMMENTS" TO "ULGN01" WITH GRANT OPTION
/

-- Since LOGON 2.1 we should have this data_blob_lgn_a on all our DBs as well, which is simply as it should be
-- I.e., this info message is not needed anymore.
-- prompt If error occurs @AdNovum: Ask DBA to add blob tablespace similar to @Crh, but no problem other than that.
ALTER USER ULGN01  QUOTA UNLIMITED ON DATA_BLOB_LGN_A;

PROMPT************************************
PROMPT Create User ULGNSAP               *
PROMPT************************************
--
CREATE USER ULGNSAP
PROFILE DEFAULT IDENTIFIED BY ulgnsap
DEFAULT TABLESPACE DATA_LGN_A
TEMPORARY TABLESPACE TEMP
QUOTA UNLIMITED ON DATA_LGN_A
QUOTA UNLIMITED ON INDEX_LGN_A
ACCOUNT UNLOCK;

GRANT RL_LGN_OWNER TO ULGNSAP;

-- Since LOGON 2.1 we should have this data_blob_lgn_a on all our DBs as well, which is simply as it should be
-- I.e., this info message is not needed anymore.
-- prompt If error occurs @AdNovum: Ask DBA to add blob tablespace similar to @Crh, but no problem other than that.
ALTER USER ULGNSAP QUOTA UNLIMITED ON DATA_BLOB_LGN_A;

PROMPT************************************
PROMPT Create User ULGN02                *
PROMPT************************************
--
CREATE USER ULGN02
PROFILE DEFAULT IDENTIFIED BY ulgn02
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
ACCOUNT UNLOCK;

GRANT RL_LGN_USER TO ULGN02;
GRANT RL_LGN_ETL TO ULGN02;

PROMPT ************************************
PROMPT Create User ULGN03                 *
PROMPT ************************************
--
CREATE USER ULGN03
PROFILE DEFAULT IDENTIFIED BY ulgn03
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
ACCOUNT UNLOCK;

GRANT RL_LGN_LOGIN TO ULGN03;

-- No migration planned for now (Sept 2012). And nobody knows how it would be. Therefore, disabled.
-- PROMPT ***********************************************************
-- PROMPT Create Role and Grants for ULGNMIG (Migration 1.0 -> 1.1) *
-- PROMPT ***********************************************************
-- PROMPT mig_grants.sql skipped, not needed for 2.0 or 2.1 creation.
-- @mig_grants.sql

@patch_spool_off.sql
