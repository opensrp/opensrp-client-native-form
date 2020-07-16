package com.vijay.jsonwizard.activities;

import android.os.Build;

import com.vijay.jsonwizard.application.TestApplication;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1}, application = TestApplication.class)
public abstract class BaseActivityTest {
    public static final String DUMMY_TEST_STRING = "DUMMY TEST STRING";
    public static final String DUMMY_JSON_FORM_STRING = "{\r\n  \"count\": \"1\",\r\n  \"encounter_type\": \"Birth Registration\",\r\n  \"mother\": {\r\n    \"encounter_type\": \"New Woman Registration\"\r\n  },\r\n  \"entity_id\": \"\",\r\n  \"relational_id\": \"\",\r\n  \"metadata\": {\r\n    \"start\": {\r\n      \"openmrs_entity_parent\": \"\",\r\n      \"openmrs_entity\": \"concept\",\r\n      \"openmrs_data_type\": \"start\",\r\n      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n    }\r\n  },\r\n  \"step1\": {\r\n    \"title\": \"Birth Registration\",\r\n    \"fields\": [\r\n      {\r\n        \"key\": \"Child_Photo\",\r\n        \"openmrs_entity_parent\": \"\",\r\n        \"openmrs_entity\": \"\",\r\n        \"openmrs_entity_id\": \"\",\r\n        \"type\": \"choose_image\",\r\n        \"uploadButtonText\": \"Take a photo of the child\"\r\n      },\r\n      {\r\n        \"key\": \"gps\",\r\n        \"openmrs_entity_parent\": \"usual_residence\",\r\n        \"openmrs_entity\": \"person_address\",\r\n        \"openmrs_entity_id\": \"geopoint\",\r\n        \"openmrs_data_type\": \"text\",\r\n        \"type\": \"gps\"\r\n      },\r\n      {\r\n        \"key\": \"Home_Facility\",\r\n        \"openmrs_entity_parent\": \"\",\r\n        \"openmrs_entity\": \"\",\r\n        \"openmrs_entity_id\": \"\",\r\n        \"openmrs_data_type\": \"text\",\r\n        \"type\": \"tree\",\r\n        \"hint\": \"Child's home health facility *\",\r\n        \"tree\": [],\r\n        \"v_required\": {\r\n          \"value\": true,\r\n          \"err\": \"Please enter the child's home facility\"\r\n        }\r\n      }\r\n    ]\r\n  }\r\n}";
}
