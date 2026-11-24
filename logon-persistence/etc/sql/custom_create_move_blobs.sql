-- Manual overrides / corrections / extensions to generated SQL which cannot (yet) be done in the mapping files
--
-- Unlike "custom_overrides.sql", this file is ONLY meant to be called during
-- DB creation from scratch. Any modification which should be done also during
-- patch => MOVE INTO THE PATCH FILE DIRECTLY, or check if it's save to put
-- into custom_overrides.sql.

prompt '----- custom_create_move_blobs.sql -----'

prompt *** Moving BLOB to seperate tablespace DATA_BLOB_LGN_A
-- WARNING: These operation will make any existing indexes UNUSABLE for non-empty tables!
--
-- Motivation:
-- Crh HSEE (IT in Spain) wants to have BLOB data in a seperate tablespace.
-- Jose Enrique Martin Maestro sent us these 3 statements, for the blobs which
-- existed in December 2011.
-- Add additional ones when adding new blobs.
ALTER TABLE ULGN01.TLGNC_OUTPUTDOC MOVE LOB (DATA) STORE AS (TABLESPACE DATA_BLOB_LGN_A);
ALTER TABLE ULGN01.TLGNC_SIGNATURE MOVE LOB (DATA) STORE AS (TABLESPACE DATA_BLOB_LGN_A);
ALTER TABLE ULGN01.TLGNC_IMAGE     MOVE LOB (DATA) STORE AS (TABLESPACE DATA_BLOB_LGN_A);
