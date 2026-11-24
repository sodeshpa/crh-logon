-- $Revision$
-- $Date$
--

prompt '--------- initialdata/tlgnc_outputdoc_type.sql ----------'

prompt INSERTING into TLGNC_OUTPUTDOC_TYPE
insert into tlgnr_outputdoc_type_bs(outputdoc_type_cd, typename) values(1000, 'Delivery Note ');
insert into tlgnr_outputdoc_type_bs(outputdoc_type_cd, typename) values(1001, 'Export Invoice ');
insert into tlgnr_outputdoc_type_bs(outputdoc_type_cd, typename) values(1002, 'Collective Delivery Note ');
insert into tlgnr_outputdoc_type_bs(outputdoc_type_cd, typename) values(1003, 'Cash Sales ');
insert into tlgnr_outputdoc_type_bs(outputdoc_type_cd, typename)  values(-1, 'Unidentified output document type');

commit;