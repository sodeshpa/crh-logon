-------------------------------------------------------------------------------
-- getRolesString - Used in User Admin
-------------------------------------------------------------------------------
-- Retrieves the role names (from TLGNU_ROLE) assigned for a certain user,
-- separated with comma.
-- Query simply does a filtering of TLGNU_USER_ROLE table with the user id
-- received as input parameter, and formats the output
--
create or replace function getRolesString(id tlgnu_user.user_id%type) return varchar2 as
  type t_roles	is table of tlgnu_role.role_name%type;
  l_roles  t_roles; l_res varchar2(4000);
begin 
  select role_name 
bulk collect
  into l_roles
from tlgnu_role rol, tlgnu_user_role userrol, tlgnu_user usertb where rol.role_id=userrol.role_id and
userrol.user_id=usertb.user_id and usertb.user_id=id;

  for i in 1..l_roles.count loop
    exit when length(l_res) + length(l_roles(i)) + 2 > 4000;
    l_res := l_res || l_roles(i) || ', ';
  end loop;
  l_res := substr(l_res,1, length(l_res)-2);
  return l_res;
END getRolesString;
/
show errors;

-------------------------------------------------------------------------------
-- findFirstFreePDAUserNo - Used in User Admin / PDA User Number 
-------------------------------------------------------------------------------
-- Example: select findFirstFreePDAUserNo(1000, 9999) from dual;
-- Finds the first number between 1000 and 9999 which is not used as
-- pda_user_no in the table tlgnu_user.
--
-- Why not doing it with a Sequence?
-- This value is a proposal to the administrator when doing "add user". In most
-- cases he will replace it with the 5 or 7 digit Crh employee number.
-- In those cases we don't want to waste a sequence number.
-- Note: There is no "locking" of the number, if two administrators would
-- call this screen in parallel, they get the same proposed value, and one
-- of them will get an error (number already exists)
--
create or replace function findFirstFreePDAUserNo(
  i_start_number in number,
  i_end_number in number)
return number is
  v_lastexisting number := i_start_number - 1;
begin
  for curr_user in (
    select * from tlgnu_user
      where pda_user_no between i_start_number and i_end_number order by pda_user_no
  )
  loop
    if curr_user.pda_user_no - v_lastexisting > 1 then
      return v_lastexisting + 1;
    end if;
    v_lastexisting := curr_user.pda_user_no;
  end loop;
  if v_lastexisting < i_end_number then
    return v_lastexisting + 1;
  end if;
  return null;
end;
/
show errors;

/**
 * Calculates the substratcion of two dates in the given unit of time.
 * Possible units of time: 'ss' (seconds), 'mi' (minutes), 'hh' (hours)
 */
create or replace function date_difference(
  i_unit_of_time in varchar2,
  i_first_date in date,
  i_second_date in date)
return number as
  l_result    number;
begin
  select
    (i_second_date-i_first_date) * decode(upper(i_unit_of_time), 'SS', 24*60*60, 'MI', 24*60, 'HH', 24, NULL)
  into
    l_result
  from
    dual;
  return round(l_result);
end;
/
show errors;

/**
 * Returns the current (red or yellow) warning timeline as date for the given parameters
 */
create or replace function get_current_timeline(
  i_transport_duration_min in number,
  i_planned_loading_time_min in number,
  i_delivery_to_date in date,
  i_orderstatus_code in number,
  i_color in varchar2
) return date deterministic as 
  l_total_threshold number;
  l_current_timeline date;
  c_order_status_arrived constant number := 13;
  c_order_status_shipping constant number := 12;
  c_order_status_loading constant number := 10;
  c_order_status_rail_ready constant number := 18;
begin
  /* Do not show any warning after "Arrived at delivery point" */
  if (i_orderstatus_code > c_order_status_arrived and 
  	  i_orderstatus_code != c_order_status_rail_ready) then
    return sysdate;
  end if;
  select /*+ RESULT_CACHE */ sum(value) into l_total_threshold
  from
    tlgnr_config
  where
    i_orderstatus_code <= cast(rtrim(ltrim(key, 'app.default.monitoring.threshold.'), concat('.', i_color)) as number) and
    key like concat('app.default.monitoring.threshold.%',concat('.', i_color));
  l_current_timeline := i_delivery_to_date;
  /* Calculate in the delivery time if not yet ended */
  if (i_orderstatus_code <= c_order_status_shipping) then
    l_current_timeline := l_current_timeline - numtodsinterval(coalesce(i_transport_duration_min,0),'minute');
  end if;
  /* Calculate in the loading time if not yet ended */
  if (i_orderstatus_code <= c_order_status_loading) then
    l_current_timeline := l_current_timeline - numtodsinterval(coalesce(i_planned_loading_time_min,0),'minute');
  end if;
  l_current_timeline := l_current_timeline - numtodsinterval(coalesce(l_total_threshold,0),'minute');
  return l_current_timeline;
end get_current_timeline;
/
show errors;

/**
 * Used in VLGNC_ORDER_MONITORING
 * 
 * Returns the minutes to the (red or yellow) warning timeline backwards.
 * 
 * Positive if we already passed the timeline,
 * negative if there is still some minutes left.
 */
create or replace function get_minutes_to_timeline(
  i_transport_duration_min in number,
  i_planned_loading_time_min in number,
  i_delivery_to_date in date,
  i_orderstatus_code in number,
  i_color in varchar2
) return number deterministic as
begin
  return date_difference(
    'mi',
    get_current_timeline(
      i_transport_duration_min,
      i_planned_loading_time_min,
      i_delivery_to_date,
      i_orderstatus_code,
      i_color
    ),
    sysdate
  );
end get_minutes_to_timeline;
/
show errors;

/**
 * Used in VLGNC_ORDER_MONITORING
 * 
 * Decides which flag to show up by calculating and checking the 
 * minutes back to the red and yellow warning timelines. 
 */
create or replace function get_warning_status(
  i_transport_duration_min in number,
  i_planned_loading_time_min in number,
  i_delivery_to_date in date,
  i_orderstatus_code in number,
  i_order_is_on_hold in number
) return number deterministic as
  c_warning_status_on_hold constant number := 3;
  c_warning_status_red constant number := 2;
  c_warning_status_yellow constant number := 1;
  c_warning_status_nothing constant number := 0;
  c_yes constant number := 1;
begin
  if i_order_is_on_hold = c_yes then
    return c_warning_status_on_hold;
  elsif 0 < get_minutes_to_timeline(i_transport_duration_min, i_planned_loading_time_min, i_delivery_to_date, i_orderstatus_code, 'red') then
    return c_warning_status_red;
  elsif 0 < get_minutes_to_timeline(i_transport_duration_min, i_planned_loading_time_min, i_delivery_to_date, i_orderstatus_code, 'yellow') then
    return c_warning_status_yellow;
  else
    return c_warning_status_nothing;
  end if;
end get_warning_status;
/
show errors;

-- checks in tlgnr_config and tlgnc_config_login_module if an instance is defined
-- returns 1 if the instance exists in either config table, 0 otherwise.
create or replace function is_existing_instance(instance_name varchar2) return number as
  n_exists number;
begin
  select count(distinct instance)
  into n_exists
  from
  (
    select instance from tlgnr_config
    union all
    select instance from tlgnc_config_login_module
  )
  where instance = instance_name;
  return n_exists;
end is_existing_instance;
/

create or replace procedure drop_table_if_exists(i_table_name varchar) as
  table_or_view_not_exist exception;
  pragma exception_init(table_or_view_not_exist, -942);
begin 
  execute immediate 'drop table ' || i_table_name;
  dbms_output.put_line('Table dropped: "' || i_table_name || '"');
exception
  when table_or_view_not_exist then
    dbms_output.put_line('Table does not exist, so not dropping now: "' || i_table_name || '"');
  when others then
    dbms_output.put_line('Could not drop existing table: "' || i_table_name || '"');
    raise;
end;
/

show errors;

create or replace TYPE FILTER_TRUCK_IDS IS TABLE OF NUMBER(12,0);
/

show errors;

create or replace PROCEDURE FETCH_TRUCK_CHANGES_FOR_DISPO 
(
  P_FILTER_TRUCK_IDS IN FILTER_TRUCK_IDS
, P_FROM_DATE IN DATE  
, P_TO_DATE IN DATE  
, P_LAST_ACCESS_TIME IN TIMESTAMP
, O_CHANGED_TRUCK_ID_LIST OUT SYS_REFCURSOR  
) AS 
BEGIN
  OPEN O_CHANGED_TRUCK_ID_LIST FOR 
  select distinct truck.truck_id  
  from tlgnc_truck truck
  left join TLGNC_TRUCK_UNAVAILABILITY unav on truck.truck_id = unav.truck_id and ((unav.start_dat is null and unav.end_dat is null) or (unav.start_dat < P_TO_DATE and unav.end_dat > P_FROM_DATE))
  left join TLGNC_TRAFFIC_JAM traffjam on truck.truck_id = traffjam.truck_id and (traffjam.start_dat is null or (traffjam.start_dat < P_TO_DATE and traffjam.start_dat > P_FROM_DATE ))
  left join tlgnc_driver driver ON truck.truck_id = driver.current_truck_id
  left join tlgnc_driver_break drvbreak ON driver.driver_id = drvbreak.driver_id and (drvbreak.start_dat is null or (drvbreak.start_dat < P_TO_DATE and drvbreak.start_dat > P_FROM_DATE ))
  left join tlgnc_pda_message msg ON driver.user_id = msg.pda_user_id and (msg.CTL_CRE_TS is null or (msg.CTL_CRE_TS < P_TO_DATE and msg.CTL_CRE_TS > P_FROM_DATE ))
  WHERE truck.active_flag = 1 
  and greatest( nvl(truck.ctl_mod_ts, DATE'1970-01-01'),
                nvl(unav.ctl_mod_ts,DATE'1970-01-01'),        
                nvl(traffjam.ctl_mod_ts,DATE'1970-01-01'),
                nvl(driver.ctl_mod_ts,DATE'1970-01-01'),        
                nvl(drvbreak.ctl_mod_ts,DATE'1970-01-01'),        
                nvl(msg.ctl_mod_ts,DATE'1970-01-01')) > P_LAST_ACCESS_TIME
  and truck.truck_id in (select column_value from table(P_FILTER_TRUCK_IDS));

END FETCH_TRUCK_CHANGES_FOR_DISPO;
/

show errors;