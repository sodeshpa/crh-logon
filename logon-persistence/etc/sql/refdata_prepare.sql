prompt CREATING logon_insert_image PROCEDURE

-- Procedure used to insert images into tlgnc_image table.
-- File is called during objs.sql.
-- The procedure could also be merged into tlgnc_image, and be dropped after
-- all files have been loaded, the procedure is only used during schema creation.
--
-- Parameters:
--   p_image - image name with extention
--   p_image_name - image name in the table
--   p_mime_type_cd - mime type code (1-image/gif, 2-image/jpeg,3-image/png, 4-application/pdf)
--
-- The following preconditions are needed. In a proper installation, executed by credbposttask.sql
--   create the directory as sys: CREATE OR REPLACE directory IMAGES_DIR AS '/u00/app/adnoracle/admin/DCHLGN01/scripts/images';
--   grant read,write on directory IMAGES_DIR to rl_lgn_owner
--
CREATE OR REPLACE PROCEDURE logon_insert_image(p_image VARCHAR2,   p_image_name VARCHAR2,   p_mime_type_cd NUMBER) IS

src_file bfile := bfilename('IMAGES_DIR',   p_image);
dst_file BLOB;
lgh_file binary_integer;

n_image_id NUMBER(12);

BEGIN
  -- requires 'set serveroutput on' as well.
  DBMS_OUTPUT.enable(1000);
  DBMS_OUTPUT.PUT_LINE('Inserting ' || p_image || ' image...');

  SELECT slgnc_image.nextval
  INTO n_image_id
  FROM dual;

  -- insert a NULL record to lock
  INSERT
  INTO tlgnc_image(image_id,   image_name,   data,   data_mime_type_cd)

  VALUES(n_image_id,   p_image_name,   empty_blob(),   p_mime_type_cd) returning data
  INTO dst_file;

  -- lock record
  SELECT data
  INTO dst_file
  FROM tlgnc_image
  WHERE image_id = n_image_id FOR
  UPDATE;

  -- open the file
  dbms_lob.fileopen(src_file,   dbms_lob.file_readonly);

  -- determine length
  lgh_file := dbms_lob.getlength(src_file);

  -- read the file
  dbms_lob.loadfromfile(dst_file,   src_file,   lgh_file);

  -- update the blob field
  UPDATE tlgnc_image
  SET data = dst_file
  WHERE image_id = n_image_id;

  COMMIT;

  DBMS_OUTPUT.PUT_LINE('Image ' || p_image || ' successfully inserted!');

  -- close file
  dbms_lob.fileclose(src_file);

EXCEPTION
WHEN others THEN
  DBMS_OUTPUT.PUT_LINE('ERROR inserting ' || p_image || ' image: ' || SQLCODE || sqlerrm);

END logon_insert_image;
/
