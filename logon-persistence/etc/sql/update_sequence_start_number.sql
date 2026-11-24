set serveroutput on;

create or replace 
procedure update_sequence_start_number (
  table_name in varchar2 default 'tlgnc_trsp_zone_vertex', 
  column_id in varchar2 default 'trsp_zone_vertex_id', 
  sequence_name in varchar2 default 'slgnc_trsp_zone_vertex'
)
is
  l_max_id number;
  l_max_seq number;
begin
    -- Get the maximum ID
    execute immediate 'select max(' || column_id || ') from ' || table_name into l_max_id;    
    dbms_output.put_line('max id value: ' || l_max_id);

    -- To avoid: ORA-08002: sequence MySequence.CURRVAL is not yet defined in this session
    execute immediate 'select ' || sequence_name || '.nextval from dual' into l_max_seq ;

    -- Get the current sequence value;
    execute immediate 'select ' || sequence_name || '.currval from dual' into l_max_seq;
    
    dbms_output.put_line('current sequence number: ' || l_max_seq);
    
    IF (l_max_seq < l_max_id) then
      -- Alter the sequence to increment by the difference
      execute immediate 'alter sequence ' || sequence_name || ' increment by ' || ( l_max_id - l_max_seq );
     
      -- run nextval to update sequence number to l_max_id
      execute immediate 'select ' || sequence_name || '.nextval from dual' into l_max_seq ;
      
      -- Change the sequence back to normal
      execute immediate 'alter sequence ' || sequence_name || ' increment by 1';
    end IF;
  commit;
end;
/

show errors;
