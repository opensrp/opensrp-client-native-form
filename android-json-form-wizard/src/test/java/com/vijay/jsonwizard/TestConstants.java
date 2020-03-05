package com.vijay.jsonwizard;

/**
 * Created by onaio on 23/08/2017.
 */

public class TestConstants {
    public static final String TYPE_STRING = "string";
    public static final String TYPE_NUMERIC = "numeric";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_ARRAY = "array";

    public static final String PAOT_TEST_FORM="{\"count\":\"1\",\"encounter_type\":\"PAOT\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Potential Area of Transmission\",\"display_back_button\":\"true\",\"fields\":[{\"key\":\"paotStatus\",\"label\":\"Status\",\"type\":\"native_radio\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"options\":[{\"key\":\"Active\",\"text\":\"Active\"},{\"key\":\"Inactive\",\"text\":\"Inactive\"},{\"key\":\"Not_Eligible\",\"text\":\"Not Eligible\"}],\"v_required\":{\"value\":true,\"err\":\"Please specify status of area of transmission\"}},{\"key\":\"paotComments\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Comments\"},{\"key\":\"lastUpdatedDate\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"date_picker\",\"hint\":\"Last Updated\",\"max_date\":\"today\",\"v_required\":{\"value\":true,\"err\":\"Please specify the last updated date\"}},{\"key\":\"business_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"hidden\",\"value\":\"Complete\"}]}}";
}
