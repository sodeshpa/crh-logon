
-- List of ADDITIONAL exchange rates, only used for TEST systems.
-- Logon does not support currencies other than CHF and EUR in release 1.0/1.1 in 2009.
-- Created to make it easier to modify the rate for the NEXT period for the next few years (no UI exists to update exchange rates, just tablebrowser

prompt INSERTING into TLGNC_CURRENCY_EXCH_RATE

-- 2009
insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'USD', to_date('20090101', 'YYYYMMDD'),   1.4334, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'GBP', to_date('20090101', 'YYYYMMDD'),   1.8752, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'HUF', to_date('20090101', 'YYYYMMDD'), 266.3500, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'RSD', to_date('20090101', 'YYYYMMDD'), 115.6500, 1, systimestamp, 1, systimestamp, 0);

-- 2010 
insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'USD', to_date('20100101', 'YYYYMMDD'),   1.4500, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'GBP', to_date('20100101', 'YYYYMMDD'),   1.8000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'HUF', to_date('20100101', 'YYYYMMDD'), 266.0000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'RSD', to_date('20100101', 'YYYYMMDD'), 115.6500, 1, systimestamp, 1, systimestamp, 0);

--2011
insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'USD', to_date('20110101', 'YYYYMMDD'),   1.4500, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'GBP', to_date('20110101', 'YYYYMMDD'),   1.8000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'HUF', to_date('20110101', 'YYYYMMDD'), 266.0000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'RSD', to_date('20110101', 'YYYYMMDD'), 115.6500, 1, systimestamp, 1, systimestamp, 0);

--2012
insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'USD', to_date('20120101', 'YYYYMMDD'),   1.4500, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'GBP', to_date('20120101', 'YYYYMMDD'),   1.8000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'HUF', to_date('20120101', 'YYYYMMDD'), 266.0000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'RSD', to_date('20120101', 'YYYYMMDD'), 115.6500, 1, systimestamp, 1, systimestamp, 0);

--2013
insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'USD', to_date('20130101', 'YYYYMMDD'),   1.4500, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'GBP', to_date('20130101', 'YYYYMMDD'),   1.8000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'HUF', to_date('20130101', 'YYYYMMDD'), 266.0000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'RSD', to_date('20130101', 'YYYYMMDD'), 115.6500, 1, systimestamp, 1, systimestamp, 0);

--2014
insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'USD', to_date('20140101', 'YYYYMMDD'),   1.4500, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'GBP', to_date('20140101', 'YYYYMMDD'),   1.8000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'HUF', to_date('20140101', 'YYYYMMDD'), 266.0000, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_currency_exch_rate (currency_exch_rate_id, currency_cd, valid_from_dat, exchange_rate, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (
  slgnc_currency_exch_rate.nextval, 'RSD', to_date('20140101', 'YYYYMMDD'), 115.6500, 1, systimestamp, 1, systimestamp, 0);

-- http://www.fazfinance.net/d/Markt.faz?type=cur , 26.8.2009:
-- EUR/CHF:   1.5178
-- EUR/USD:   1.4334
-- EUR/GBP:   1.8752
-- EUR/HUF: 266.3500

commit;
