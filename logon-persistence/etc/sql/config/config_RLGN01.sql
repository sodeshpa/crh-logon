-- 20.11.2009:
-- File and Directory are obsolete!
-- The idea was to make it possible to create a fresh DB fully automated, by executing just one command
-- - Useful in Nightly Build?
-- - Useful to setup a fresh DB in dev?
--
-- Problem: Passwords should not be stored in such a file, at least not for the productive installation at Crh.
-- Therefore, this whole idea has kind of lost its signifance and is currently not pursued further.

-- Defines the set of configurable values for the following database:
-- REFERENCE ADN, RLGN01, default schema

DEFINE INST           = RLGN01
DEFINE OWNER          = ulgn01
DEFINE OWNER_PWD      = ulgn01
DEFINE USER           = ulgn02
DEFINE USER_PWD       = ulgn02
DEFINE SAPOWNER       = ulgnsap
DEFINE SAPOWNER_PWD   = ulgnsap
