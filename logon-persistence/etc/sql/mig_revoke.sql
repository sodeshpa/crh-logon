-- These grants had to be given to the owner 'ulgn01' directly because they
-- are needed during the migration, inside a PL/SQL block with executed immediate.
-- (When executing a procedure, grants given only to the role are not active)
-- Do not forget to revoke them again in the end...
revoke select on sys.dba_sequences from ulgn01;
revoke create sequence from ulgn01;

-- grants on sequences: No need to revoke, simply revoke or even drop role

revoke rl_lgn_mig from ulgn01;
-- drop role rl_lgn_mig;
