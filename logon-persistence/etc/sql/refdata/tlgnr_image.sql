-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_image.sql ----------'

prompt INSERTING into TLGNC_IMAGE - depends on tlgnr_mime_type.sql and files in IMAGE_DIR

-- Procedure ulgn01.logon_insert_image created by refdata_prepare.sql
-- tlgnc_image.sql to be called as the same user as procedure owner, i.e. ulgn01.
-- Parameters: logon_insert_image(filename, image_name, mime_type)

set serveroutput on
EXECUTE logon_insert_image('crh-logo.png',            'pdf_crh_logo',         3);
EXECUTE logon_insert_image('pdf_aenor_logo.png',         'pdf_aenor_logo',          3);
EXECUTE logon_insert_image('pdf_benor_logo.png',         'pdf_benor_logo',          3);
EXECUTE logon_insert_image('pdf_ce_logo_600dpi.png',     'pdf_ce_logo',             3);
EXECUTE logon_insert_image('pdf_general_info_banner.jpg','pdf_general_info_banner', 2);
EXECUTE logon_insert_image('pdf_georoc_logo.png',        'pdf_georoc_logo',         3);
EXECUTE logon_insert_image('pdf_hydro_logo.jpg',         'pdf_hydro_logo',          2);
EXECUTE logon_insert_image('pdf_hydro_logo_en.jpg',      'pdf_hydro_logo_en',       2);
EXECUTE logon_insert_image('pdf_hydro_logo_fr.jpg',      'pdf_hydro_logo_fr',       2);
EXECUTE logon_insert_image('pdf_hydro_logo_it.jpg',      'pdf_hydro_logo_it',       2);
EXECUTE logon_insert_image('pdf_komobv_logo.jpg',        'pdf_komobv_logo',         2);
EXECUTE logon_insert_image('pdf_mpanrw_logo_300dpi.png', 'pdf_mpanrw_logo',         3);
EXECUTE logon_insert_image('pdf_mpastu_logo_300dpi.png', 'pdf_mpastu_logo',         3);
EXECUTE logon_insert_image('pdf_nf_logo.png',            'pdf_nf_logo',             3);
EXECUTE logon_insert_image('pdf_scert_logo_300dpi.png',  'pdf_scert_logo',          3);
EXECUTE logon_insert_image('pdf_u_logo_300dpi.png',      'pdf_u_logo',              3);
EXECUTE logon_insert_image('pdf_vdz_logo_600dpi.png',    'pdf_vdz_logo',            3);
EXECUTE logon_insert_image('livemap_container_loaded.png',   			 'livemap_container_loaded.png',    3);
EXECUTE logon_insert_image('livemap_container_partially_loaded.png',   	 'ivemap_container_partially_loaded.png',    3);
EXECUTE logon_insert_image('livemap_container_unloaded.png',   			 'livemap_container_unloaded.png',    3);
EXECUTE logon_insert_image('livemap_ordermap_plant.png',   				 'livemap_ordermap_plant.png',    3);
EXECUTE logon_insert_image('livemap_ordermap_shipping_point.png',   	 'livemap_ordermap_shipping_point.png',    3);
EXECUTE logon_insert_image('livemap_event.png',                          'livemap_event.png',    3);
EXECUTE logon_insert_image('livemap_non_geo_event.png',                  'livemap_non_geo_event.png',    3);
EXECUTE logon_insert_image('livemap_ordermap_train_station.png',   		 'livemap_ordermap_train_station.png',    3);
EXECUTE logon_insert_image('livemap_railcar_loaded.png',   				 'livemap_railcar_loaded.png',    3);
EXECUTE logon_insert_image('livemap_railcar_partially_loaded.png',   	 'livemap_railcar_partially_loaded.png',    3);
EXECUTE logon_insert_image('livemap_railcar_unloaded.png',  			 'livemap_railcar_unloaded.png',    3);
EXECUTE logon_insert_image('livemap_route.png',                          'livemap_route.png',    3);
EXECUTE logon_insert_image('livemap_shipto.png',                         'livemap_shipto.png',   3);
EXECUTE logon_insert_image('livemap_silo_loaded.png',   				 'livemap_silo_loaded.png',    3);
EXECUTE logon_insert_image('livemap_silo_partially_loaded.png',    		 'livemap_silo_partially_loaded.png',    3);
EXECUTE logon_insert_image('livemap_silo_unloaded.png',   				 'livemap_silo_unloaded.png',    3);
EXECUTE logon_insert_image('livemap_trailer_loaded.png',    			 'livemap_trailer_loaded.png',    3);
EXECUTE logon_insert_image('livemap_trailer_partially_loaded.png',    	 'livemap_trailer_partially_loaded.png',    3);
EXECUTE logon_insert_image('livemap_trailer_unloaded.png',   			 'livemap_trailer_unloaded.png',    3);
EXECUTE logon_insert_image('livemap_truck_loaded_en_route.png',   		 'livemap_truck_loaded_en_route.png',    3);
EXECUTE logon_insert_image('livemap_truck_loaded_inside_geofence.png',   'livemap_truck_loaded_inside_geofence.png',    3);
EXECUTE logon_insert_image('livemap_truck_loaded_traffic_jam.png',   	 'livemap_truck_loaded_traffic_jam.png',    3);
EXECUTE logon_insert_image('livemap_truck_partially_loaded_en_route.png',   	 'livemap_truck_partially_loaded_en_route.png',    3);
EXECUTE logon_insert_image('livemap_truck_partially_loaded_inside_geofence.png', 'livemap_truck_partially_loaded_inside_geofence.png',    3);
EXECUTE logon_insert_image('livemap_truck_partially_loaded_traffic_jam.png',   	 'livemap_truck_partially_loaded_traffic_jam.png',    3);
EXECUTE logon_insert_image('livemap_truck_unloaded_en_route.png',    	 'livemap_truck_unloaded_en_route.png',    3);
EXECUTE logon_insert_image('livemap_truck_unloaded_inside_geofence.png', 'livemap_truck_unloaded_inside_geofence.png',    3);
EXECUTE logon_insert_image('livemap_truck_unloaded_traffic_jam.png',     'livemap_truck_unloaded_traffic_jam.png',    3);
EXECUTE logon_insert_image('crh-logo.png',                            'truck_revenue_planning_report', 3);
EXECUTE logon_insert_image('main_logo_logon_small.png',                            'main_logo_logon_small', 3);
EXECUTE logon_insert_image('crh-logo.png',                            'main_logo_company', 3);
EXECUTE logon_insert_image('main_logo_company_small.png',                            'main_logo_company_small', 3);

-- uma 6.11.2009: This is not a real 'image', but a testpage for printing.
-- It is a little abuse, but it effectively solves the problem of distributing
-- the document into an empty DB @ Crh....
-- If more initial outputdoc or signatures must be loaded the same way:
-- -> Better create individual functions similar to 'logon_insert_image'.
-- But for just 1 document this workaround is ok.
execute logon_insert_image('printing_testpage.ps',     'printing_testpage.ps',    5 /*application/postscript*/);
insert into tlgnc_outputdoc (outputdoc_id, outputdoc_name, lang_cd, data, data_mime_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn, outputdoc_type_cd)
( select 999, i.image_name, 'en', i.data, i.data_mime_type_cd, 1, systimestamp, 1, systimestamp, 1, 1000
  from tlgnc_image i where image_name = 'printing_testpage.ps'
);
commit;
