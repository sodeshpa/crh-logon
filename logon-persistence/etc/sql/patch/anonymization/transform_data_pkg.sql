SET serveroutput ON;
CREATE OR REPLACE PACKAGE TRANSFORM_DATA
AS
    c_length_of_random         CONSTANT NUMBER := 10;
    PROCEDURE TRANSFORM(
            in_table_name        IN VARCHAR2,
            in_value_column_name IN VARCHAR2,
            in_id_column_name    IN VARCHAR2);
    PROCEDURE get_rows_to_transform(
            in_table_name        IN VARCHAR2,
            in_value_column_name IN VARCHAR2,
            in_id_column_name    IN VARCHAR2,
            ret_cursor OUT SYS_REFCURSOR);
    FUNCTION get_value_including_id(
            in_id        IN NUMBER,
            in_new_value IN VARCHAR2)
        RETURN VARCHAR2;
    FUNCTION get_random_value(
            in_old_value IN VARCHAR2)
        RETURN VARCHAR2;
    FUNCTION get_table_prefix(
            in_table_name IN VARCHAR2)
        RETURN VARCHAR2;
    FUNCTION get_max_length_of_col(
            in_table_name        IN VARCHAR2,
            in_value_column_name IN VARCHAR2)
        RETURN NUMBER;
END TRANSFORM_DATA;
/
SHOW ERRORS;
CREATE OR REPLACE PACKAGE BODY TRANSFORM_DATA
AS
    e_transform_failed          EXCEPTION;
    e_transformed_to_non_unique EXCEPTION;
PROCEDURE TRANSFORM(
        in_table_name        IN VARCHAR2,
        in_value_column_name IN VARCHAR2,
        in_id_column_name    IN VARCHAR2)
IS
    cnt_simple_replace        NUMBER:=0;
    cnt_simple_replace_and_id NUMBER:=0;
    cnt_random                NUMBER:=0;
    open_cursor SYS_REFCURSOR;
    q1                  VARCHAR2(1000);
    q2                  VARCHAR2(1000);
    q3                  VARCHAR2(1000);
    v_id                NUMBER;
    v_old_value         VARCHAR2(1000);
    v_new_value         VARCHAR2(1000);
    v_old_count         NUMBER;
    v_max_length_of_col NUMBER;
    v_prefix            VARCHAR2(1000);
BEGIN
    --Retrieve a mapping: original value -> transformed value (transform: replacing the predefined set of characters
    --in the string).
    v_max_length_of_col:=get_max_length_of_col(in_table_name,in_value_column_name);
    v_prefix           :=get_table_prefix(in_table_name);
    get_rows_to_transform(in_table_name,in_value_column_name,in_id_column_name,ret_cursor => open_cursor);
    LOOP
        BEGIN
            q1          :=NULL;
            q2          :=NULL;
            q3          :=NULL;
            v_id        :=NULL;
            v_old_value :=NULL;
            v_new_value :=NULL;
            FETCH open_cursor INTO v_id,v_old_value,v_new_value,v_old_count;
            EXIT
        WHEN open_cursor%NOTFOUND;
            BEGIN
                IF (v_old_value=v_new_value) THEN
                    --if the original value is the same as the transformed value (by replacing the predefined set
                    --of characters it was not possible to mask the value successfully)
                    RAISE e_transform_failed;
                END IF;
                BEGIN
                    --escape ' character
                    v_new_value    :=regexp_replace(v_new_value, '''', '''''');
                    IF (v_old_count >1) THEN
                        --if the original value was unique, but the new vale is not unique anymore -> attempt to
                        --make sure the new value is unique
                        RAISE e_transformed_to_non_unique;
                    END IF;
                    q1 := 'UPDATE ' || in_table_name || ' SET ' || in_value_column_name || '=''' || v_new_value || ''' WHERE ' || in_id_column_name || '=' || v_id;
                    EXECUTE IMMEDIATE q1;
                    cnt_simple_replace:=cnt_simple_replace+1;
                EXCEPTION
                WHEN e_transformed_to_non_unique THEN
                    --add the negated unique id at the end of the string to make it unique
                    v_new_value   :=get_value_including_id(v_id, v_new_value);
                    IF (v_old_value=v_new_value) THEN
                        RAISE e_transform_failed;
                    END IF;
                    IF (LENGTH(v_new_value) + LENGTH(v_prefix) < v_max_length_of_col) THEN
                        v_new_value                           :=v_prefix || v_new_value;
                    END IF;
                    q2 := 'UPDATE ' || in_table_name || ' SET ' || in_value_column_name || '=''' || v_new_value || ''' WHERE ' || in_id_column_name || '=' || v_id;
                    EXECUTE IMMEDIATE q2;
                    cnt_simple_replace_and_id:=cnt_simple_replace_and_id+1;
                END;
            EXCEPTION
            WHEN e_transform_failed THEN
                GOTO set_random_value;
            WHEN OTHERS THEN
                <<set_random_value>>
                BEGIN
                    v_new_value                               :=get_random_value(v_old_value);
                    IF (LENGTH(v_new_value) + LENGTH(v_prefix) < v_max_length_of_col) THEN
                        v_new_value                           :=v_prefix || v_new_value;
                    END IF;
                    q3 := 'UPDATE ' || in_table_name || ' SET ' || in_value_column_name || '=''' || v_new_value || ''' WHERE ' || in_id_column_name || '=' || v_id;
                    EXECUTE IMMEDIATE q3;
                    cnt_random:=cnt_random+1;
                END;
            END;
        EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Anonymization of [' || in_table_name || ']/[' || in_value_column_name || ']/' || '[v_id=' || v_id || '] failed, the content is unchanged.');
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
            DBMS_OUTPUT.PUT_LINE('Query 1 was:' || q1 );
            DBMS_OUTPUT.PUT_LINE('Query 2 was:' || q2 );
            DBMS_OUTPUT.PUT_LINE('Query 3 was:' || q3 );
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
            CLOSE open_cursor;
            RAISE;
        END;
    END LOOP;
    CLOSE open_cursor;
    DBMS_OUTPUT.PUT_LINE('Transform statistics [' || in_table_name || ']/[' || in_value_column_name || ']: ' || cnt_simple_replace || '/'|| cnt_simple_replace_and_id || '/' || cnt_random);
END TRANSFORM;
PROCEDURE get_rows_to_transform(
        in_table_name        IN VARCHAR2,
        in_value_column_name IN VARCHAR2,
        in_id_column_name    IN VARCHAR2,
        ret_cursor OUT SYS_REFCURSOR)
IS
    q VARCHAR2(10000);
BEGIN
    q:=
    'SELECT ID, OLD_VALUE, NEW_VALUE, COUNT (distinct(OLD_VALUE)) OVER( PARTITION BY (NEW_VALUE))as OLD_COUNT FROM (
SELECT ID, OLD_VALUE, 
REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE(REGEXP_REPLACE( OLD_VALUE, 
''[a|i]'', ''e'' ,1,0,''''),
''[A|I]'', ''E'' ,1,0,''''),
''[N]'', ''D'' ,1,0,''''),
''[k|p]'', ''h'' ,1,0,''''),
''[H|L]'', ''T'' ,1,0,''''),
''[S|R]'', ''C'' ,1,0,''''),
''[M|Z]'', ''N'' ,1,0,''''),
''[K|P|]'', ''F'' ,1,0,''''), 
''[o|u]'', ''a'',1,0,''''),
''[O|U]'', ''A'',1,0,''''),
''[n,r]'', ''l'' ,1,0,''''),
''[v,t]'', ''z'' ,1,0,''''),
''[f,b]'', ''r'',1,0,''''),
''[F,B]'', ''R'',1,0,''''),
''[3,4]'', ''5'',1,0,''''),
''[1,9]'', ''3'',1,0,''''),
''[5,6]'', ''7'',1,0,''''),
''[2]'', ''8'',1,0,''''),
''[c|d]'', ''t'',1,0,''''),
''[g|m]'', ''s'',1,0,'''')
AS NEW_VALUE FROM  
(SELECT '
    || in_id_column_name || ' AS ID, ' || in_value_column_name || ' AS OLD_VALUE FROM ' || in_table_name || ' WHERE ' || in_value_column_name || ' IS NOT NULL ORDER BY CTL_CRE_TS ASC))' ;
    --DBMS_OUTPUT.PUT_LINE('Executing query ''' || q || '''');
    OPEN ret_cursor FOR q;
END get_rows_to_transform;
/**
* Function replaces the last characters of the input in_new_value
* with the negated in_id given.
* Example: in_id:1215, in_new_value: Mark Sample
* Return: Mark S-1215
*/
FUNCTION get_value_including_id(
        in_id        IN NUMBER,
        in_new_value IN VARCHAR2)
    RETURN VARCHAR2
IS
    v_new_value   VARCHAR2(1000);
    v_negative_id VARCHAR2(1000);
BEGIN
    v_negative_id:=TO_CHAR(0-in_id);
    --if the original value is longer than the negated id
    IF (LENGTH(in_new_value)> LENGTH(v_negative_id)) THEN
        v_new_value        :=SUBSTR(in_new_value, 0,LENGTH(in_new_value) - LENGTH(v_negative_id)) || v_negative_id;
    ELSE
        v_new_value:=v_negative_id;
    END IF;
    RETURN v_new_value;
END get_value_including_id;
/**
* Function to retrieve a random string.
*
* If the string contains only numbers then returns a c_length_of_random long
* negative number as string.
* Otherwise returns a random string with the same length as in_old_value
* keeping the original spaces where they are in the in_old_value.
*/
FUNCTION get_random_value(
        in_old_value IN VARCHAR2)
    RETURN VARCHAR2
IS
    v_contains_only_number BOOLEAN;
    v_new_value            VARCHAR2(1000);
    v_escaped_old_value    VARCHAR2(1000);
    q                      VARCHAR2 (1000);
BEGIN
    v_escaped_old_value   :=regexp_replace(in_old_value, '''', '''''');
    v_contains_only_number:=replace( translate( v_escaped_old_value, '0123456789', '0000000000' ), '0', '' ) IS NULL;
    IF (v_contains_only_number) THEN
        --use a negative number with length c_length_of_random
        v_new_value:=0-TRUNC(DBMS_RANDOM.value(POWER(10,c_length_of_random-2),POWER(10,c_length_of_random-1)));
    ELSE
        --create a random string from in_old_value keeping the spaces where they were originally
        q:='select LISTAGG(random_string,'' '') WITHIN GROUP (ORDER BY rownum) from (select 1 as grp,rownum,dbms_random.string(''a'', case nvl(word,''x'') when ''x'' then 10 else length(word) end) as random_string from (select regexp_substr('''|| v_escaped_old_value || ''',''[^ ]+'', 1, level) as word from dual connect by regexp_substr(''' || v_escaped_old_value || ''', ''[^ ]+'', 1, level) is not null)) group by grp';
        --DBMS_OUTPUT.PUT_LINE('Executing query ''' || q || '''');
        EXECUTE IMMEDIATE q INTO v_new_value;
    END IF;
    RETURN v_new_value;
END get_random_value;
/**
* Function returns an intended lowercase prefix based on the in_table_name.
* Assumption is that the table name is in the form ulgn01.TLGN*_.
* Example: in_table_name is ulgn01.TLGNU_USER, then returns user
*/
FUNCTION get_table_prefix(
        in_table_name IN VARCHAR2)
    RETURN VARCHAR2
IS
    v_prefix VARCHAR2(1000);
BEGIN
    --remove ulgn01.TLGN*_ and keep as prefix
    v_prefix :=LOWER(SUBSTR(in_table_name, 14)) || ' ';
    RETURN v_prefix;
END get_table_prefix;
/**
* Function returns the maximum length of the in_table_name/in_column_name;
*/
FUNCTION get_max_length_of_col(
        in_table_name        IN VARCHAR2,
        in_value_column_name IN VARCHAR2)
    RETURN NUMBER
IS
    v_max_length_of_col NUMBER;
    q                   VARCHAR2(1000);
BEGIN
	--remove ulgn01. prefix
    q:='select CHAR_LENGTH from user_tab_columns where table_name=UPPER(SUBSTR(''' || in_table_name || ''',8,LENGTH(''' || in_table_name || '''))) and column_name=UPPER(''' || in_value_column_name || ''')';
    --DBMS_OUTPUT.PUT_LINE('Executing query ''' || q || '''');
    EXECUTE IMMEDIATE q INTO v_max_length_of_col;
    RETURN v_max_length_of_col;
END get_max_length_of_col;
END TRANSFORM_DATA;
/
SHOW ERRORS;