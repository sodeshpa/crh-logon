prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1314: Performance scheduling
prompt ************************************************************************

prompt *** Add the min_start and max_end date fields to the tlgnc_assignment table
alter table tlgnc_assignment add (
min_start                    date                 null
);
 
      alter table tlgnc_assignment add (
max_end                    date                 null
);
 
prompt *** Fill out the new fields min_start and max_end commit after 1000 entries each

DECLARE
  CURSOR assignment_cursor is select assignment_id from tlgnc_assignment where active_flag = 1;
  v_min_start date;
  v_max_end date;
  v_count number;
BEGIN
  v_count:=0;
  FOR cur_ass IN assignment_cursor
  LOOP
  select least(ap1.planned_start, coalesce(ap1.real_start, ap1.planned_start)) into v_min_start from tlgnc_assignment_part ap1
   where ap1.assignment_id = cur_ass.assignment_id and
     ap1.assignment_part_seq_no =
       (select min(ap2.assignment_part_seq_no) from tlgnc_assignment_part ap2
        where ap2.assignment_id = cur_ass.assignment_id) ;
        --DBMS_OUTPUT.PUT_LINE(v_min_start || ' ' || cur_ass.assignment_id);
  select greatest(ap.planned_start, coalesce(ap.real_start, ap.planned_start)) into v_max_end from tlgnc_assignment_part ap
   where ap.assignment_id = cur_ass.assignment_id and
     ap.assignment_part_seq_no =
       (select max(ap1.assignment_part_seq_no) from tlgnc_assignment_part ap1
        where ap1.assignment_id = cur_ass.assignment_id) ;
        --DBMS_OUTPUT.PUT_LINE(v_max_end || ' ' || cur_ass.assignment_id);     
    update tlgnc_assignment set min_start=v_min_start, max_end=v_max_end where assignment_id=cur_ass.assignment_id;
    v_count:=v_count + 1;
    if mod(v_count,1000) = 0 then
        commit;
    end if;
  END LOOP;
  commit;
END;
/

prompt ************************************************************************
prompt *** END CRHLOGON-1314: Performance scheduling
prompt ************************************************************************