--------------------------------------------------------------------------------
--
-- PROJECT      : $Project: LOGON$
--
-- AUTHOR       : $Author: tomi $
-- CREATION DATE: $Date$
-- DESCRIPTION  : Create logging package and table.
--
--

/**
 * Helper table for delivnote_loop_commit
 */
execute drop_table_if_exists('tlgnt_outputdocs_to_delete');
create global temporary table tlgnt_outputdocs_to_delete(
  order_outputdoc_id number,
  pdf_outputdoc_id number,
  ps_outputdoc_id number
) on commit delete rows;


--------------------------------------------------------------------------------
-- create new package (declaration)
--------------------------------------------------------------------------------
CREATE OR REPLACE PACKAGE LOGON_LOG AS

  --Loglevel for the PL/SQL import packages. DEBUG, INFO or ERROR. Logging info to be found in tlgnc_log.
  g_min_loglevel VARCHAR2(6) := NULL;
  --Used to mark log messages or imported records as belonging together (one procedure call)
  g_run_number NUMBER(12)  := NULL;

  c_cleanup_name           constant VARCHAR(30) := 'sapimport_clean_log';
  c_cleanup_mop_name       constant VARCHAR(30) := 'mop_cleanup';
  c_cleanup_biz_name       constant VARCHAR(30) := 'biz_cleanup';
  c_cleanup_delivnote_name constant VARCHAR(30) := 'delivnote_cleanup';
  c_level_info             constant VARCHAR(10) := 'INFO';
  c_level_debug            constant VARCHAR(10) := 'DEBUG';
  c_level_error            constant VARCHAR(10) := 'ERROR';

  PROCEDURE start_import(var_import_name IN VARCHAR2);
  PROCEDURE finish_import(var_import_name IN VARCHAR2);
  
  PROCEDURE log(var_severity IN VARCHAR2, var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2);

  PROCEDURE log_cleanup_loop_commit (i_delete_after_days IN NUMBER, i_delete_level in VARCHAR2);

  PROCEDURE mop_out_data_loop_commit    (delete_mop_out_before_ts IN timestamp);
  PROCEDURE mop_out_head_loop_commit    (delete_mop_out_before_ts IN timestamp);
  PROCEDURE mop_in_data_loop_commit     (delete_mop_in_before_ts IN timestamp);
  PROCEDURE mop_in_bizevent_loop_commit (delete_mop_in_before_ts IN timestamp);
  PROCEDURE mop_in_head_loop_commit     (delete_mop_in_before_ts IN timestamp);
  PROCEDURE bizevent_loop_commit        (delete_bizevent_before_ts IN timestamp);
  PROCEDURE delivnote_loop_commit       (i_delete_delivnote_before_ts IN timestamp);

  PROCEDURE log_cleanup (i_min_loglevel IN VARCHAR2, i_delete_debug_after_days IN NUMBER, i_delete_info_after_days IN NUMBER, i_delete_error_after_days IN NUMBER, i_delete_bizevent_after_days IN NUMBER);

  PROCEDURE mop_cleanup (i_min_loglevel IN VARCHAR2, i_delete_mop_in_after_days IN NUMBER, i_delete_mop_out_after_days IN NUMBER, i_delete_bizevent_after_days IN NUMBER);

  PROCEDURE biz_cleanup (i_min_loglevel IN VARCHAR2, i_delete_bizevent_after_days IN NUMBER);
  PROCEDURE delivnote_cleanup (i_min_loglevel IN VARCHAR2, i_delete_delivnote_after_days IN NUMBER);

  PROCEDURE debug(var_source IN VARCHAR2, var_text IN VARCHAR2);
  PROCEDURE debug(var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2);

  PROCEDURE info (var_source IN VARCHAR2, var_text IN VARCHAR2);
  PROCEDURE info (var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2);

  PROCEDURE error(var_source IN VARCHAR2, var_text IN VARCHAR2);
  PROCEDURE error(var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2);

END LOGON_LOG;
/

SHOW ERRORS;

CREATE OR REPLACE PACKAGE BODY LOGON_LOG AS

  ------------------------------------------------------------------------------
  -- at the beginning every new import call has to update TLGNC_CRONSTATUS.LAST_START_TS and get his own run (call) number
  ------------------------------------------------------------------------------
  PROCEDURE start_import(var_import_name IN VARCHAR2) IS
      PRAGMA AUTONOMOUS_TRANSACTION;
  BEGIN

      --get last run (call) number
      SELECT last_run_number
      INTO g_run_number
      FROM tlgnc_cronstatus
      WHERE cronstatus_name = var_import_name;

      --increament lest run to be current run
      g_run_number := g_run_number + 1;

  END;

  ------------------------------------------------------------------------------
  -- at the end every import call has to update TLGNC_CRONSTATUS.LAST_END_TS and LAST_RUN_NUMBER(call)
  ------------------------------------------------------------------------------
  PROCEDURE finish_import(var_import_name IN VARCHAR2) IS
      PRAGMA AUTONOMOUS_TRANSACTION;
  BEGIN

      --time of the start of the import
      UPDATE tlgnc_cronstatus
        SET last_run_number = g_run_number,
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
      WHERE cronstatus_name = var_import_name;

      COMMIT;

  END;

  ------------------------------------------------------------------------------
  -- general log method
  ------------------------------------------------------------------------------
  PROCEDURE log(var_severity IN VARCHAR2, var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2) IS
      PRAGMA AUTONOMOUS_TRANSACTION;
      var_num_minlevel NUMBER(1);
      var_num_severity NUMBER(1);

    BEGIN

      -- logging level should be set correctly
      IF (g_min_loglevel IS NOT NULL) THEN

          SELECT
            decode(g_min_loglevel, c_level_debug, 1, c_level_info,  2, c_level_error, 3)
            INTO var_num_minlevel
            FROM DUAL;
          SELECT
            decode(var_severity, c_level_debug, 1, c_level_info,  2, c_level_error, 3)
            INTO var_num_severity
            FROM DUAL;

          IF var_num_severity >= var_num_minlevel THEN
            INSERT INTO tlgnc_log (log_id, log_severity, log_source, log_sqlcode, log_sqlerrm, log_text, run_number, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
              VALUES (SLGNC_LOG.NEXTVAL, var_severity, var_source, var_sqlcode, var_sqlerrm, var_text, g_run_number, 1, systimestamp, 1, systimestamp, 0);
            COMMIT;
          END IF;

      ELSE
          INSERT INTO tlgnc_log (log_id, log_severity, log_source, log_sqlcode, log_sqlerrm, log_text, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
             VALUES (SLGNC_LOG.NEXTVAL, c_level_error, 'LOGON_LOG', NULL, NULL, 'Logging level is not set!', 1, systimestamp, 1, systimestamp, 0);
          COMMIT;
      END IF;

  END log;

  ------------------------------------------------------------------------------
  -- helper procedure for log_cleanup, to clean up a single level but commit in clices of 10000.
  -- - i_delete_after_days : Delete records which are older than X days. (Check for 0=never to be done by CALLER!)
  -- - i_delete_level      : loglevel to delete, one of these values: c_level_debug, c_level_info, c_level_error.
  --
  -- As a result of ticket ADN 52004, commit in loops. This should be done for all kinds of bulk deletions.
  -- Reused from http://www.dbasupport.com/oracle/ora9i/DeleteHistoricalData02.shtml
  PROCEDURE log_cleanup_loop_commit (
    i_delete_after_days in NUMBER,
    i_delete_level      in VARCHAR2)
  IS
    PRAGMA AUTONOMOUS_TRANSACTION;
    rec_read       number(10)    := 0;
    rec_rest       number(10)    := 0;
    cursor rec_sql is select log_id, rowid from tlgnc_log where log_severity = i_delete_level and ctl_cre_ts < systimestamp - i_delete_after_days;
  BEGIN
    for rec_sql_run in rec_sql loop
      rec_read := rec_read + 1 ;
      rec_rest := rec_rest + 1 ;
      delete from tlgnc_log lg where lg.rowid = rec_sql_run.rowid ;
      if rec_rest > 10000 THEN
        COMMIT;
        rec_rest := 1;
        LOGON_LOG.DEBUG('LOG: log_cleanup', '    Deleting ' || i_delete_level || ', commit at ' || rec_read);
      END IF;
    end loop;
    commit;
    LOGON_LOG.DEBUG('LOG: log_cleanup', '    Deleting ' || i_delete_level || ', commit at ' || rec_read);
    LOGON_LOG.INFO('LOG: log_cleanup', '    Deleted ' || rec_read || ' records from ''tlgnc_log'' table with log severity ' || i_delete_level );
  END log_cleanup_loop_commit;

  ------------------------------------------------------------------------------
  -- helper procedure for mop_cleanup, to clean up a set of MOPEVENT OUT DATA and commit in clices of 10000.
  -- - delete_mop_out_before_ts : Delete records which refer to a mopevent header older than this timestamp
  --
  -- As a result of ticket ADN 52004, commit in loops. This should be done for all kinds of bulk deletions.
  -- Reused from http://www.dbasupport.com/oracle/ora9i/DeleteHistoricalData02.shtml
  PROCEDURE mop_out_data_loop_commit (
    delete_mop_out_before_ts in timestamp)
  IS
    PRAGMA AUTONOMOUS_TRANSACTION;
    rec_read       number(10)    := 0;
    rec_rest       number(10)    := 0;
    -- header and data do NOT have the identical ctl_cre_ts. Therefore, need to join data+header to delete data, cannot just use data.ctl_cre_ts
    cursor rec_sql is
      select mho.mopevent_header_out_id, mdo.mopevent_data_out_id /*, mho.ctl_cre_ts, mdo.ctl_cre_ts */
      from tlgnc_mopevent_header_out mho
      inner join tlgnc_mopevent_data_out mdo on (mho.mopevent_header_out_id = mdo.mopevent_header_out_id)
      where mho.ctl_cre_ts < delete_mop_out_before_ts;
  BEGIN
    for rec_sql_run in rec_sql loop
      rec_read := rec_read + 1 ;
      rec_rest := rec_rest + 1 ;
      delete from tlgnc_mopevent_data_out d where d.mopevent_data_out_id = rec_sql_run.mopevent_data_out_id;
      if rec_rest > 10000 THEN
        COMMIT;
        rec_rest := 1;
        LOGON_LOG.DEBUG('LOG: mop_OUT_data_loop_commit', '    Deleting, commit at ' || rec_read);
      END IF;
    end loop;
    commit;
    LOGON_LOG.DEBUG('LOG: mop_OUT_data_loop_commit', '    Deleting, commit at ' || rec_read);
    LOGON_LOG.INFO('LOG: mop_OUT_data_loop_commit', '    Deleted ' || rec_read || ' records from ''tlgnc_mopevent_data_out'' table.');
  END mop_out_data_loop_commit;

  ------------------------------------------------------------------------------
  -- helper procedure for mop_cleanup, to clean up a set of MOPEVENT OUT DATA and commit in clices of 10000.
  -- - delete_mop_out_before_ts : Delete records which are older than this timestamp
  --
  -- Precondition: mop_out_DATA_loop_commit must have been called before, for the right set of data!!
  --
  -- As a result of ticket ADN 52004, commit in loops. This should be done for all kinds of bulk deletions.
  -- Reused from http://www.dbasupport.com/oracle/ora9i/DeleteHistoricalData02.shtml
  PROCEDURE mop_out_head_loop_commit (
    delete_mop_out_before_ts in timestamp)
  IS
    PRAGMA AUTONOMOUS_TRANSACTION;
    rec_read       number(10)    := 0;
    rec_rest       number(10)    := 0;
    cursor rec_sql is
      select mho.mopevent_header_out_id /*, mhi.ctl_cre_ts */
      from tlgnc_mopevent_header_out mho
      where mho.ctl_cre_ts < delete_mop_out_before_ts;
  BEGIN
    for rec_sql_run in rec_sql loop
      rec_read := rec_read + 1 ;
      rec_rest := rec_rest + 1 ;
      delete from tlgnc_mopevent_header_out mho where mho.mopevent_header_out_id = rec_sql_run.mopevent_header_out_id;
      if rec_rest > 10000 THEN
        COMMIT;
        rec_rest := 1;
        LOGON_LOG.DEBUG('LOG: mop_OUT_head_loop_commit', '    Deleting, commit at ' || rec_read);
      END IF;
    end loop;
    commit;
    LOGON_LOG.DEBUG('LOG: mop_OUT_head_loop_commit', '    Deleting, commit at ' || rec_read);
    LOGON_LOG.INFO('LOG: mop_OUT_head_loop_commit', '    Deleted ' || rec_read || ' records from ''tlgnc_mopevent_header_out'' table.');
  END mop_out_head_loop_commit;

  ------------------------------------------------------------------------------
  -- helper procedure for mop_cleanup, to clean up a set of MOPEVENT IN DATA and commit in clices of 10000.
  -- - delete_mop_in_before_ts : Delete records which refer to a mopevent header older than this timestamp
  --
  -- As a result of ticket ADN 52004, commit in loops. This should be done for all kinds of bulk deletions.
  -- Reused from http://www.dbasupport.com/oracle/ora9i/DeleteHistoricalData02.shtml
  PROCEDURE mop_in_data_loop_commit (
    delete_mop_in_before_ts in timestamp)
  IS
    PRAGMA AUTONOMOUS_TRANSACTION;
    rec_read       number(10)    := 0;
    rec_rest       number(10)    := 0;
    -- header and data do NOT have the identical ctl_cre_ts. Therefore, need to join data+header to delete data, cannot just use data.ctl_cre_ts
    cursor rec_sql is
      select mhi.mopevent_header_in_id, mdi.mopevent_data_in_id /*, mhi.ctl_cre_ts, mdi.ctl_cre_ts */
      from tlgnc_mopevent_header_in mhi
      inner join tlgnc_mopevent_data_in mdi on (mhi.mopevent_header_in_id = mdi.mopevent_header_in_id)
      where mhi.ctl_cre_ts < delete_mop_in_before_ts;
  BEGIN
    for rec_sql_run in rec_sql loop
      rec_read := rec_read + 1 ;
      rec_rest := rec_rest + 1 ;
      delete from tlgnc_mopevent_data_in d where d.mopevent_data_in_id = rec_sql_run.mopevent_data_in_id;
      if rec_rest > 10000 THEN
        COMMIT;
        rec_rest := 1;
        LOGON_LOG.DEBUG('LOG: mop_IN_data_loop_commit', '    Deleting, commit at ' || rec_read);
      END IF;
    end loop;
    commit;
    LOGON_LOG.DEBUG('LOG: mop_IN_data_loop_commit', '    Deleting, commit at ' || rec_read);
    LOGON_LOG.INFO('LOG: mop_IN_data_loop_commit', '    Deleted ' || rec_read || ' records from ''tlgnc_mopevent_data_in'' table.');
  END mop_in_data_loop_commit;

  ------------------------------------------------------------------------------
  -- helper procedure for mop_cleanup, to remove reference BIZEVENT -> MOPEVENT IN HEADER and commit in clices of 10000.
  -- - delete_mop_in_before_ts : UPDATE records which refer to a mopevent header older than this timestamp
  --
  -- Precondition 1: mop_in_DATA_loop_commit must have been called before, for the right set of data!!
  -- Precondition 2: bizevents referring to this mopevent must have been set to original_mopevent_id = null before.
  --
  -- As a result of ticket ADN 52004, commit in loops. This should be done for all kinds of bulk deletions.
  -- Reused from http://www.dbasupport.com/oracle/ora9i/DeleteHistoricalData02.shtml
  PROCEDURE mop_in_bizevent_loop_commit (
    delete_mop_in_before_ts in timestamp)
  IS
    PRAGMA AUTONOMOUS_TRANSACTION;
    rec_read       number(10)    := 0;
    rec_rest       number(10)    := 0;
    cursor rec_sql is
      select mhi.mopevent_header_in_id, biz.bizevent_id /* , mhi.ctl_cre_ts, biz.ctl_cre_ts */
      from tlgnc_mopevent_header_in mhi
      inner join tlgnc_bizevent biz on (mhi.mopevent_header_in_id = biz.original_mopevent_id)
      where mhi.ctl_cre_ts < delete_mop_in_before_ts;
  BEGIN
    for rec_sql_run in rec_sql loop
      rec_read := rec_read + 1 ;
      rec_rest := rec_rest + 1 ;
      update tlgnc_bizevent biz set original_mopevent_id=null where biz.bizevent_id = rec_sql_run.bizevent_id;
      if rec_rest > 10000 THEN
        COMMIT;
        rec_rest := 1;
        LOGON_LOG.DEBUG('LOG: mop_IN_bizevent_loop_commit', '    Update biz.original_mopevent_id to null, commit at ' || rec_read);
      END IF;
    end loop;
    commit;
    LOGON_LOG.DEBUG('LOG: mop_IN_bizevent_loop_commit', '    Update biz.original_mopevent_id to null, commit at ' || rec_read);
    LOGON_LOG.INFO('LOG: mop_IN_bizevent_loop_commit', '    Updated original_mopevent_id to null for ' || rec_read || ' records in ''tlgnc_bizevent'' table.');
  END mop_in_bizevent_loop_commit;

  ------------------------------------------------------------------------------
  -- helper procedure for mop_cleanup, to clean up a set of MOPEVENT IN HEADER and commit in clices of 10000.
  -- - delete_mop_in_before_ts : Delete records which are older than this timestamp
  --
  -- Precondition 1: mop_in_DATA_loop_commit must have been called before, for the right set of data!!
  -- Precondition 2: bizevents referring to this mopevent must have been set to original_mopevent_id = null before.
  --
  -- As a result of ticket ADN 52004, commit in loops. This should be done for all kinds of bulk deletions.
  -- Reused from http://www.dbasupport.com/oracle/ora9i/DeleteHistoricalData02.shtml
  PROCEDURE mop_in_head_loop_commit (
    delete_mop_in_before_ts in timestamp)
  IS
    PRAGMA AUTONOMOUS_TRANSACTION;
    rec_read       number(10)    := 0;
    rec_rest       number(10)    := 0;
    cursor rec_sql is
      select mhi.mopevent_header_in_id /*, mhi.ctl_cre_ts */
      from tlgnc_mopevent_header_in mhi
      where mhi.ctl_cre_ts < delete_mop_in_before_ts;
  BEGIN
    for rec_sql_run in rec_sql loop
      rec_read := rec_read + 1 ;
      rec_rest := rec_rest + 1 ;
      delete from tlgnc_mopevent_header_in mhi where mhi.mopevent_header_in_id = rec_sql_run.mopevent_header_in_id;
      if rec_rest > 10000 THEN
        COMMIT;
        rec_rest := 1;
        LOGON_LOG.DEBUG('LOG: mop_IN_head_loop_commit', '    Deleting, commit at ' || rec_read);
      END IF;
    end loop;
    commit;
    LOGON_LOG.DEBUG('LOG: mop_IN_head_loop_commit', '    Deleting, commit at ' || rec_read);
    LOGON_LOG.INFO('LOG: mop_IN_head_loop_commit', '    Deleted ' || rec_read || ' records from ''tlgnc_mopevent_header_in'' table.');
  END mop_in_head_loop_commit;

  ------------------------------------------------------------------------------
  -- helper procedure for biz_cleanup, to remove bizevents type 118 (position) of all orders and commit in clices of 10000.
  -- - delete_bizevent_before_ts : Delete all bizevents type 118 (position) of all orders with delivery date older than this timestamp
  --
  -- As a result of ticket ADN 52004, commit in loops. This should be done for all kinds of bulk deletions.
  -- Reused from http://www.dbasupport.com/oracle/ora9i/DeleteHistoricalData02.shtml
  PROCEDURE bizevent_loop_commit (
    delete_bizevent_before_ts in timestamp)
  IS
    PRAGMA AUTONOMOUS_TRANSACTION;
    rec_read       number(10)    := 0;
    rec_rest       number(10)    := 0;
    cursor rec_order is
      select biz.bizevent_id
      from tlgnc_order ord
      inner join tlgnc_bizevent biz on (ord.order_id = biz.order_id)
      where biz.bizevent_type_cd = 118 and ord.actual_delivery_dat < delete_bizevent_before_ts;
    cursor rec_assignment is
      select biz.bizevent_id
      from tlgnc_order ord
      inner join tlgnc_assignment_part ap on (ord.order_id = ap.order_id)
      inner join tlgnc_bizevent biz on (ap.assignment_id = biz.assignment_id)
      where biz.bizevent_type_cd = 118 and ord.actual_delivery_dat < delete_bizevent_before_ts;
  BEGIN
    FOR rec_order_run IN rec_order LOOP
      rec_read := rec_read + 1 ;
      rec_rest := rec_rest + 1 ;
      delete from tlgnc_bizevent biz where biz.bizevent_id = rec_order_run.bizevent_id;
      -- LOGON_LOG.DEBUG('LOG: bizevent_loop_commit', '    Deleting order -> bizevent, bizevent_id = ' || rec_order_run.bizevent_id);
      if rec_rest >= 10000 THEN
        COMMIT;
        rec_rest := 0;
        LOGON_LOG.DEBUG('LOG: bizevent_loop_commit', '    Deleting order -> bizevent, commit at ' || rec_read);
      END IF;
    END LOOP;
    COMMIT;
    rec_rest := 0;
    LOGON_LOG.DEBUG('LOG: bizevent_loop_commit', '    Deleting order -> bizevent, commit at ' || rec_read);

    FOR rec_assignment_run IN rec_assignment LOOP
      rec_read := rec_read + 1 ;
      rec_rest := rec_rest + 1 ;
      delete from tlgnc_bizevent biz where biz.bizevent_id = rec_assignment_run.bizevent_id;
      -- LOGON_LOG.DEBUG('LOG: bizevent_loop_commit', '    Deleting order -> assignment -> bizevent, bizevent_id = ' || rec_assignment_run.bizevent_id);
      if rec_rest >= 10000 THEN
        COMMIT;
        rec_rest := 0;
        LOGON_LOG.DEBUG('LOG: bizevent_loop_commit', '    Deleting order -> assignment -> bizevent, commit at ' || rec_read);
      END IF;
    END LOOP;
    COMMIT;
    LOGON_LOG.DEBUG('LOG: bizevent_loop_commit', '    Deleting order -> assignment -> bizevent, commit at ' || rec_read);
    LOGON_LOG.INFO('LOG: bizevent_loop_commit', '    Deleted ' || rec_read || ' records from ''tlgnc_bizevent'' table.');
  END bizevent_loop_commit;

  ------------------------------------------------------------------------------
  -- Helper procedure for delivnote_cleanup, to remove delivery notes except the most actual one of all orders.
  -- - delete_delivnote_before_ts : Delete all delivery notes except the most actual one of all orders with delivery date older than this timestamp
  --
  -- As a result of ticket ADN 52004, commit in loops. This should be done for all kinds of bulk deletions.
  procedure delivnote_loop_commit(
    i_delete_delivnote_before_ts in timestamp)
  is
    pragma autonomous_transaction;
    l_order_outputdocs_count number;
    l_outputdocs_count number;
  begin
    delete from tlgnt_outputdocs_to_delete;
    insert into tlgnt_outputdocs_to_delete
    select
      order_outputdoc.order_outputdoc_id,
      order_outputdoc.pdf_outputdoc_id,
      order_outputdoc.ps_outputdoc_id
    from tlgnc_order logon_order
      join tlgnc_order_outputdoc order_outputdoc
      on (order_outputdoc.order_id = logon_order.order_id)
    where
      logon_order.actual_delivery_dat < i_delete_delivnote_before_ts and
      order_outputdoc.ctl_cre_ts != (
        select
          max(max_order_outputdoc.ctl_cre_ts)
        from
          tlgnc_order_outputdoc max_order_outputdoc
        where
          max_order_outputdoc.order_id = order_outputdoc.order_id
        group by
          max_order_outputdoc.order_id
      );

    delete from
      tlgnc_order_outputdoc order_outputdoc
    where order_outputdoc.order_outputdoc_id in (
      select distinct order_outputdoc_id
        from tlgnt_outputdocs_to_delete
    );
    logon_log.info(
      'LOG: delivnote_loop_commit',
      '    Deleted ' || sql%rowcount || ' records from ''tlgnc_order_outputdoc'' table.'
    );

    delete from
      tlgnc_outputdoc outputdoc
    where outputdoc.outputdoc_id in (
      select distinct pdf_outputdoc_id
        from tlgnt_outputdocs_to_delete
      union
      select distinct ps_outputdoc_id
        from tlgnt_outputdocs_to_delete
    );
    logon_log.info(
      'LOG: delivnote_loop_commit',
      '    Deleted ' || sql%rowcount || ' records from ''tlgnc_outputdoc'' table.');

    logon_log.debug('LOG: delivnote_loop_commit', '    Deleting finished');
    commit;
  end delivnote_loop_commit;


  ------------------------------------------------------------------------------
  -- tlgnc_log table clean up.
  -- - i_min_loglevel :
  --     loglevel to trace call ('DEBUG', 'INFO', 'ERROR'). DEBUG traces sliced commits.
  -- - i_delete_debug/info/error_after_days :
  --     after how many days entries in tlgnc_log are automatically deleted.
  --     If 0: keep forever, do not delete.
  -- - i_delete_bizevent_after_days :
  --     delete all bizevents type 118 (position) of all orders with delivery date older than this number of days.
  --     If 0: do nothing (useful for manual calls from SQL developer)
  ------------------------------------------------------------------------------
  PROCEDURE log_cleanup(i_min_loglevel IN VARCHAR2, i_delete_debug_after_days IN NUMBER, i_delete_info_after_days IN NUMBER, i_delete_error_after_days IN NUMBER, i_delete_bizevent_after_days IN NUMBER) IS
      PRAGMA AUTONOMOUS_TRANSACTION;
  BEGIN

      --set the time of the start of the import
      start_import(c_cleanup_name);

      --set logging parameters
      g_min_loglevel := i_min_loglevel;

      LOGON_LOG.INFO('LOG: log_cleanup', 'START deleting data from ''tlgnc_log'' table...');

      IF (i_delete_debug_after_days <> 0) THEN
          logon_log.log_cleanup_loop_commit (i_delete_debug_after_days, c_level_debug);
      END IF;

      IF (i_delete_info_after_days <> 0) THEN
          logon_log.log_cleanup_loop_commit (i_delete_info_after_days, c_level_info);
      END IF;

      IF (i_delete_error_after_days <> 0) THEN
          logon_log.log_cleanup_loop_commit (i_delete_error_after_days, c_level_error);
      END IF;

      LOGON_LOG.INFO('LOG: log_cleanup', 'FINISH! Data has been deleted from ''tlgnc_log'' table.');

      /* TODO:
         A) Activate automatica mop_cleanup as part of log_cleanup => Sufficient for 1.x release!
         B) But better: implemented as its own cleanup cron job    => Logon 2.0 maybe */
      logon_log.mop_cleanup (i_min_loglevel, 30, 30, i_delete_bizevent_after_days); /* TODO: could pass those as parameter, application get them from tlgnr_config. */

      finish_import(c_cleanup_name);
  END log_cleanup;

  ------------------------------------------------------------------------------
  -- CLEANUP Job: tlgnc_mopevent_..._IN/OUT tables and dependencies
  -- - i_min_loglevel :
  --     loglevel to trace call ('DEBUG', 'INFO', 'ERROR'). DEBUG traces sliced commits.
  -- - i_delete_mop_IN/OUT_after_dayd :
  --     after how many days entries in tlgnc_mopevent_..._IN/OUT are automatically deleted.
  --     If 0: do nothing (useful for manual calls from SQL developer)
  -- - i_delete_bizevent_after_days :
  --     delete all bizevents type 118 (position) of all orders with delivery date older than this number of days.
  --     If 0: do nothing (useful for manual calls from SQL developer)
  ------------------------------------------------------------------------------
  PROCEDURE mop_cleanup(i_min_loglevel IN VARCHAR2, i_delete_mop_in_after_days IN NUMBER, i_delete_mop_out_after_days IN NUMBER, i_delete_bizevent_after_days IN NUMBER) IS
      PRAGMA AUTONOMOUS_TRANSACTION;

      /* bizevent, mop data + mop header depend on each other, therefore base them all on the SAME timestamp. */
      delete_mop_in_before_ts         timestamp     := systimestamp - i_delete_mop_in_after_days;
      delete_mop_out_before_ts        timestamp     := systimestamp - i_delete_mop_out_after_days;
  BEGIN

      --set the time of the start of the import
      /* TODO: Only when called as an independent cronjob!! start_import(c_cleanup_mop_name); */

      --set logging parameters
      g_min_loglevel := i_min_loglevel;

      LOGON_LOG.INFO('LOG: mop_cleanup', 'START deleting MOPEVENT IN/OUT data ...');

      IF (i_delete_mop_in_after_days <> 0) THEN
          /* Need to delete the data first, then the header (FK D->H)*/
          logon_log.mop_in_data_loop_commit (delete_mop_in_before_ts);
          logon_log.mop_in_bizevent_loop_commit (delete_mop_in_before_ts);
          logon_log.mop_in_head_loop_commit (delete_mop_in_before_ts); /* FIXME: update bizevent first! */
      END IF;

      IF (i_delete_mop_out_after_days <> 0) THEN
          /* Need to delete the data first, then the header (FK D->H)*/
          logon_log.mop_out_data_loop_commit (delete_mop_out_before_ts);
          logon_log.mop_out_head_loop_commit (delete_mop_out_before_ts);
      END IF;

      LOGON_LOG.INFO('LOG: mop_cleanup', 'FINISH! Data has been deleted from MOPEVENT IN/OUT data.');

      logon_log.biz_cleanup (i_min_loglevel, i_delete_bizevent_after_days);

      /* TODO: Only when called as an independent cronjob!! finish_import(c_cleanup_mop_name); */
  END mop_cleanup;

  ------------------------------------------------------------------------------
  -- CLEANUP Job: tlgnc_bizevent table, bizevent_type_cd = 118 (position)
  -- - i_min_loglevel :
  --     loglevel to trace call ('DEBUG', 'INFO', 'ERROR'). DEBUG traces sliced commits.
  -- - i_delete_bizevent_after_days :
  --     delete all bizevents type 118 (position) of all orders with delivery date older than this number of days.
  --     If 0: do nothing (useful for manual calls from SQL developer)
  ------------------------------------------------------------------------------
  PROCEDURE biz_cleanup(i_min_loglevel IN VARCHAR2, i_delete_bizevent_after_days IN NUMBER) IS
      PRAGMA AUTONOMOUS_TRANSACTION;

      delete_bizevent_before_ts       timestamp     := systimestamp - i_delete_bizevent_after_days;
  BEGIN

      --set the time of the start of the import
      /* TODO: Only when called as an independent cronjob!! start_import(c_cleanup_biz_name); */

      --set logging parameters
      g_min_loglevel := i_min_loglevel;

      LOGON_LOG.INFO('LOG: biz_cleanup', 'START deleting BIZEVENT data ...');

      IF (i_delete_bizevent_after_days <> 0) THEN
          logon_log.bizevent_loop_commit (delete_bizevent_before_ts);
      END IF;

      LOGON_LOG.INFO('LOG: biz_cleanup', 'FINISH! Data has been deleted from BIZEVENT.');

      /* TODO: Only when called as an independent cronjob!! finish_import(c_cleanup_biz_name); */
  END biz_cleanup;

  ------------------------------------------------------------------------------
  -- CLEANUP Job: tlgnc_outputdoc, tlgnc_order_outputdoc tables
  -- - i_min_loglevel :
  --     loglevel to trace call ('DEBUG', 'INFO', 'ERROR'). DEBUG traces sliced commits.
  -- - i_delete_delivnote_after_days :
  --     delete all delivery notes except the most actual one of all orders with delivery date older than this number of days.
  --     If 0: do nothing (useful for manual calls from SQL developer)
  ------------------------------------------------------------------------------
  PROCEDURE delivnote_cleanup(i_min_loglevel IN VARCHAR2, i_delete_delivnote_after_days IN NUMBER) IS
      PRAGMA AUTONOMOUS_TRANSACTION;

      delete_delivnote_before_ts      timestamp     := systimestamp - i_delete_delivnote_after_days;
  BEGIN

      --set the time of the start of the import
      start_import(c_cleanup_delivnote_name);

      --set logging parameters
      g_min_loglevel := i_min_loglevel;

      LOGON_LOG.INFO('LOG: delivnote_cleanup', 'START deleting OUTPUTDOC data ...');

      IF (i_delete_delivnote_after_days <> 0) THEN
          logon_log.delivnote_loop_commit (delete_delivnote_before_ts);
      END IF;

      LOGON_LOG.INFO('LOG: delivnote_cleanup', 'FINISH! Data has been deleted from OUTPUTDOC.');

      finish_import(c_cleanup_delivnote_name);
  END delivnote_cleanup;

  ------------------------------------------------------------------------------
  -- debug
  ------------------------------------------------------------------------------
  PROCEDURE debug(var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2) AS
     backtrace VARCHAR2(8000) := '';
  BEGIN
	 IF DBMS_UTILITY.FORMAT_ERROR_BACKTRACE IS NOT NULL THEN
	   backtrace := ' Backtrace: ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE;
	 END IF;
     LOGON_LOG.log(c_level_debug, var_source, var_sqlcode, var_sqlerrm, var_text || backtrace);
  END debug;

  PROCEDURE debug(var_source IN VARCHAR2, var_text IN VARCHAR2) AS
  BEGIN
     LOGON_LOG.debug(var_source, NULL, NULL, var_text);
  END debug;

  ------------------------------------------------------------------------------
  -- info
  ------------------------------------------------------------------------------
  PROCEDURE info(var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2) AS
     backtrace VARCHAR2(8000) := '';
  BEGIN
	 IF DBMS_UTILITY.FORMAT_ERROR_BACKTRACE IS NOT NULL THEN
	   backtrace := ' Backtrace: ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE;
	 END IF;
     LOGON_LOG.log(c_level_info, var_source, var_sqlcode, var_sqlerrm, var_text || backtrace);
  END info;

  PROCEDURE info(var_source IN VARCHAR2, var_text IN VARCHAR2) AS
  BEGIN
     LOGON_LOG.info(var_source, NULL, NULL, var_text);
  END info;

  ------------------------------------------------------------------------------
  -- error
  ------------------------------------------------------------------------------
  PROCEDURE error(var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2) AS
     backtrace VARCHAR2(8000) := '';
  BEGIN
	 IF DBMS_UTILITY.FORMAT_ERROR_BACKTRACE IS NOT NULL THEN
	   backtrace := ' Backtrace: ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE;
	 END IF;
     LOGON_LOG.log(c_level_error, var_source, var_sqlcode, var_sqlerrm, var_text || backtrace);
  END error;

  PROCEDURE error(var_source IN VARCHAR2, var_text IN VARCHAR2) AS
  BEGIN
     LOGON_LOG.error(var_source, NULL, NULL, var_text);
  END error;

END  LOGON_LOG;
/

SHOW ERRORS;

--------------------------------------------------------------------------------
-- END OF THE SCRIPT
--------------------------------------------------------------------------------
