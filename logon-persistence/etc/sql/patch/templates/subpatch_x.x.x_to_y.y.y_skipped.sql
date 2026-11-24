/**
 * This dummy file is only created in order to avoid errors when executing
 * @&sub_patch_script_name
 * in __ORIGINAL_PATCHFILE__ file.
 */

spool /temp/logon_subpatch___FROM_VERSION___to___TO_VERSION___skipped.log

begin
	dbms_output.put_line('Skipping: subpatch___FROM_VERSION___to___TO_VERSION__.sql');
end;
/
