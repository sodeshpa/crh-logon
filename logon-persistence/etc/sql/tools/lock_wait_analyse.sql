/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

/*
 * Use this helper script for checking the current locks and wait chains.
 * Run it from SQL*Plus as sysdba.
 * You will be promted for the output filename.
 *
 */

spool &spool_file
prompt
col myts noprint new_value v_myts
select to_char(systimestamp,'DD.MM.YYYY HH24:SS:FF') myts from dual;
prompt Start timestamp: &v_myts
prompt
prompt ############################## Active Sessions ##############################
prompt
set lines 210 pages 100
set heading on
column session_id heading 'Session ID' format a13 tru
column lmode heading 'Lock Held' format a17 tru
column request heading 'Lock Requested' format a15 tru
column serial# format 99999 tru
column username  format a10  heading "Username" tru
column program heading Program format a15 tru
column machine heading Host format a30 tru
column tab format a30 heading "Table Name" tru
column ctime heading "Seconds" tru
select	coalesce(S.USERNAME,'Internal') username,
	coalesce(S.PROGRAM,'None') program,
  coalesce(S.MACHINE,'None') machine,
	L.SID||','||S.SERIAL# session_id,
	U1.NAME||'.'||substr(T1.NAME,1,20) tab,
	decode(L.LMODE,1,'No Lock',
		2,'Row Share',
		3,'Row Exclusive',
		4,'Share',
		5,'Share Row Exclusive',
		6,'Exclusive',null) lmode,
	decode(L.REQUEST,1,'No Lock',
		2,'Row Share',
		3,'Row Exclusive',
		4,'Share',
		5,'Share Row Exclusive',
		6,'Exclusive',null) request,
		l.ctime
from	V$LOCK L,
	V$SESSION S,
	SYS.USER$ U1,
	SYS.OBJ$ T1
where	L.SID = S.SID
and	T1.OBJ# = decode(L.ID2,0,L.ID1,L.ID2)
and	U1.USER# = T1.OWNER#
and	S.TYPE != 'BACKGROUND'
order by 1,2,5;

prompt
prompt ############################## Wait Chains ##############################
prompt
set pages 1000
set lines 120
set heading off
column w_proc format a50 tru
column instance format a20 tru
column inst format a28 tru
column wait_event format a50 tru
column p1 format a16 tru
column p2 format a16 tru
column p3 format a15 tru
column Seconds format a50 tru
column sincelw format a50 tru
column blocker_proc format a50 tru
column waiters format a50 tru
column chain_signature format a100 wra
column blocker_chain format a100 wra

SELECT *
FROM (SELECT 'Current Process: '||osid W_PROC, 'SID '||i.instance_name INSTANCE,
'INST #: '||instance INST,'Blocking Process: '||decode(blocker_osid,null,'<none>',blocker_osid)||
' from Instance '||blocker_instance BLOCKER_PROC,'Number of waiters: '||num_waiters waiters,
'Wait Event: ' ||wait_event_text wait_event, 'P1: '||p1 p1, 'P2: '||p2 p2, 'P3: '||p3 p3,
'Seconds in Wait: '||in_wait_secs Seconds, 'Seconds Since Last Wait: '||time_since_last_wait_secs sincelw,
'Wait Chain: '||chain_id ||': '||chain_signature chain_signature,'Blocking Wait Chain: '||decode(blocker_chain_id,null,
'<none>',blocker_chain_id) blocker_chain
FROM v$wait_chains wc,
v$instance i
WHERE wc.instance = i.instance_number (+)
AND ( num_waiters > 0
OR ( blocker_osid IS NOT NULL
AND in_wait_secs > 10 ) )
ORDER BY chain_id,
num_waiters DESC)
WHERE ROWNUM < 101;

col mytsend noprint new_value v_mytsend
select to_char(systimestamp,'DD.MM.YYYY HH24:SS:FF') mytsend from dual;
prompt End timestamp: &v_mytsend

spool off;
