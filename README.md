[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-native-form.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-native-form) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-native-form/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-native-form?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/b8bfc908e9084550b1a1363205ab83b0)](https://www.codacy.com/app/opensrp/opensrp-client-native-form?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-native-form&amp;utm_campaign=Badge_Grade)

[![Dristhi](https://raw.githubusercontent.com/OpenSRP/opensrp-client/master/opensrp-app/res/drawable-mdpi/login_logo.png)](https://smartregister.atlassian.net/wiki/dashboard.action)

# Table of Contents

* [Introduction](#introduction)
* [Features](#features)
* [App Walkthrough](#app-walkthrough)
   * [Sample Android Form](#2-sample-form)
   * [Sample JSON Form](#sample-json-form)
   * [Json Form Attributes](#json-form-attributes)
* [Usage](#usage)
* [Developer Documentation](#developer-documentation)
   * [Pre-requisites](#pre-requisites)
   * [Installation Devices](#installation-devices)
   * [How to install](#how-to-install)
   * [Customisations From Android Native JSON Form](#customisations-from-android-native-json-form)


# Introduction

OpenSRP Client Native Form Module/app provides the ability to easily create Android forms using JSON forms. This module has been customised from [Android Native JSON Form](https://github.com/vijayrawatsan/android-json-form-wizard) to fit OpenSRP Requirements

> **NOTE**
>
> **JSON Form** is written using **JSON (syntax)** which can be found [here](https://www.w3schools.com/js/js_json_syntax.asp).


# Features

1. It enables definition of Android forms in JSON
2. It enables one to define default meta-data for OpenMRS forms
3. It enables one to define validations for form inputs in JSON
   * [Regular Expression](https://en.wikipedia.org/wiki/Regular_expression) validation rules
   * Number validation rules
   * Alphabetic & alphanumeric validation rules in JSON
4. It eanbles one to define form input constraints using JSON
   * Min value
   * Max value
5. It enables one to define OpenMRS mappings in JSON eg. 

# App Walkthrough

1. When you open the app, the following page is displayed

![Main Page Screenshot](https://user-images.githubusercontent.com/31766075/30383189-ca377ca6-98a9-11e7-8c23-9538214a975f.png)
![Main Page Screenshot -> Menu Open](https://user-images.githubusercontent.com/31766075/30383181-c49c8732-98a9-11e7-9f95-f56fc0cb7931.png)

This page has a menu at the top which opens a sample Patient Registeration form written in JSON Form.

#### 2. Sample Form

Below is a sample Android Form created using the `OpenSRP Native JSON Form`:

![Sample Form Screenshot](https://user-images.githubusercontent.com/31766075/30383177-c4285aa6-98a9-11e7-84de-5550c0d1d159.png)

This form has been generated from the `JSON Form` below: [Click here to Skip](#usage)

#### Sample JSON Form

`child_enrollment.json`

```
{
  "count": "1",
  "encounter_type": "Birth Registration",
  "mother": {
    "encounter_type": "New Woman Registration"
  },
  "entity_id": "",
  "relational_id": "",
  "metadata": {
    "start": {
      "openmrs_entity_parent": "",
      "openmrs_entity": "concept",
      "openmrs_data_type": "start",
      "openmrs_entity_id": "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    },
    "end": {
      "openmrs_entity_parent": "",
      "openmrs_entity": "concept",
      "openmrs_data_type": "end",
      "openmrs_entity_id": "163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    },
    "today": {
      "openmrs_entity_parent": "",
      "openmrs_entity": "encounter",
      "openmrs_entity_id": "encounter_date"
    },
    "deviceid": {
      "openmrs_entity_parent": "",
      "openmrs_entity": "concept",
      "openmrs_data_type": "deviceid",
      "openmrs_entity_id": "163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    },
    "subscriberid": {
      "openmrs_entity_parent": "",
      "openmrs_entity": "concept",
      "openmrs_data_type": "subscriberid",
      "openmrs_entity_id": "163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    },
    "simserial": {
      "openmrs_entity_parent": "",
      "openmrs_entity": "concept",
      "openmrs_data_type": "simserial",
      "openmrs_entity_id": "163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    },
    "phonenumber": {
      "openmrs_entity_parent": "",
      "openmrs_entity": "concept",
      "openmrs_data_type": "phonenumber",
      "openmrs_entity_id": "163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    },
    "encounter_location": "",
    "look_up": {
        "entity_id":"",
        "value":""
    }
  },
  "step1": {
    "title": "Birth Registration",
    "fields": [
      {
        "key": "Child_Photo",
        "openmrs_entity_parent": "",
        "openmrs_entity": "",
        "openmrs_entity_id": "",
        "type": "choose_image",
        "uploadButtonText": "Take a photo of the child"
      },
      {
        "key": "Home_Facility",
        "openmrs_entity_parent": "",
        "openmrs_entity": "",
        "openmrs_entity_id": "",
        "openmrs_data_type": "text",
        "type": "tree",
        "hint": "Child's home health facility *",
        "tree": [],
        "v_required": {
          "value": true,
          "err": "Please enter the child's home facility"
        }
      },
      {
        "key": "ZEIR_ID",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person_identifier",
        "openmrs_entity_id": "ZEIR_ID",
        "type": "barcode",
        "barcode_type": "qrcode",
        "hint": "Child's ZEIR ID *",
        "scanButtonText": "Scan QR Code",
        "value": "0",
        "v_numeric": {
          "value": "true",
          "err": "Please enter a valid ID"
        },
        "v_required": {
          "value": "true",
          "err": "Please enter the Child's ZEIR ID"
        }
      },
      {
        "key": "Child_Register_Card_Number",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person_attribute",
        "openmrs_entity_id": "Child_Register_Card_Number",
        "type": "edit_text",
        "hint": "Child's register card number"
      },
      {
        "key": "Child_Birth_Certificate",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person_attribute",
        "openmrs_entity_id": "Child_Birth_Certificate",
        "type": "edit_text",
        "hint": "Child's birth certificate number"
      },
      {
        "key": "First_Name",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person",
        "openmrs_entity_id": "first_name",
        "type": "edit_text",
        "hint": "First name",
        "edit_type": "name",
        "v_regex": {
          "value": "[A-Za-z\\s\.\-]*",
          "err": "Please enter a valid name"
        }
      },
      {
        "key": "Last_Name",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person",
        "openmrs_entity_id": "last_name",
        "type": "edit_text",
        "hint": "Last name *",
        "edit_type": "name",
        "v_required": {
          "value": "true",
          "err": "Please enter the last name"
        },
        "v_regex": {
          "value": "[A-Za-z\\s\.\-]*",
          "err": "Please enter a valid name"
        }
      },
      {
        "key": "Sex",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person",
        "openmrs_entity_id": "gender",
        "type": "spinner",
        "hint": "Sex *",
        "values": [
          "Male",
          "Female"
        ],
        "v_required": {
          "value": "true",
          "err": "Please enter the sex"
        }
      },
      {
        "key": "Date_Birth",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person",
        "openmrs_entity_id": "birthdate",
        "type": "date_picker",
        "hint": "Child's DOB *",
        "expanded": false,
        "duration": {
          "label": "Age"
        },
        "min_date": "today-5y",
        "max_date": "today",
        "v_required": {
          "value": "true",
          "err": "Please enter the date of birth"
        }
      },
      {
        "key": "First_Health_Facility_Contact",
        "openmrs_entity_parent": "",
        "openmrs_entity": "concept",
        "openmrs_entity_id": "163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "openmrs_data_type": "text",
        "type": "date_picker",
        "hint": "Date first seen *",
        "expanded": false,
        "min_date": "today-5y",
        "max_date": "today",
        "v_required": {
          "value": "true",
          "err": "Enter the date that the child was first seen at a health facility for immunization services"
        },
        "constraints": [
          {
            "type": "date",
            "ex": "greaterThanEqualTo(., step1:Date_Birth)",
            "err": "Date first seen can't occur before date of birth"
          }
        ]
      },
      {
        "key": "Birth_Weight",
        "openmrs_entity_parent": "",
        "openmrs_entity": "concept",
        "openmrs_entity_id": "5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "openmrs_data_type": "text",
        "type": "edit_text",
        "hint": "Birth weight (kg) *",
        "v_min": {
          "value": "0.1",
          "err": "Weight must be greater than 0"
        },
        "v_numeric": {
          "value": "true",
          "err": "Enter a valid weight"
        },
        "v_required": {
          "value": "true",
          "err": "Enter the child's birth weight"
        }
      },
      {
        "key": "Mother_Guardian_First_Name",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person",
        "openmrs_entity_id": "first_name",
        "entity_id": "mother",
        "type": "edit_text",
        "hint": "Mother/guardian first name *",
        "edit_type": "name",
        "look_up": "true",
        "v_required": {
          "value": "true",
          "err": "Please enter the mother/guardian's first name"
        },
        "v_regex": {
          "value": "[A-Za-z\\s\.\-]*",
          "err": "Please enter a valid name"
        }
      },
      {
        "key": "Mother_Guardian_Last_Name",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person",
        "openmrs_entity_id": "last_name",
        "entity_id": "mother",
        "type": "edit_text",
        "hint": "Mother/guardian last name *",
        "edit_type": "name",
        "look_up": "true",
        "v_required": {
          "value": "true",
          "err": "Please enter the mother/guardian's last name"
        },
        "v_regex": {
          "value": "[A-Za-z\\s\.\-]*",
          "err": "Please enter a valid name"
        }
      },
      {
        "key": "Mother_Guardian_Date_Birth",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person",
        "openmrs_entity_id": "birthdate",
        "entity_id": "mother",
        "type": "date_picker",
        "hint": "Mother/guardian DOB",
        "look_up": "true",
        "expanded": false,
        "duration": {
          "label": "Age"
        },
        "min_date": "01-01-1900",
        "max_date": "today-10y"
      },
      {
        "key": "Mother_Guardian_NRC",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person_attribute",
        "openmrs_entity_id": "NRC_Number",
        "entity_id": "mother",
        "type": "edit_text",
        "hint": "Mother/guardian NRC number",
        "v_regex": {
          "value": "([0-9]{6}/[0-9]{2}/[0-9])|\s*",
          "err": "Number must take the format of ######/##/#"
        }
      },
      {
        "key": "Mother_Guardian_Number",
        "openmrs_entity_parent": "",
        "openmrs_entity": "concept",
        "openmrs_entity_id": "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "type": "edit_text",
        "hint": "Mother/guardian phone number",
        "v_numeric": {
          "value": "true",
          "err": "Number must begin with 095, 096, or 097 and must be a total of 10 digits in length"
        },
        "v_regex": {
          "value": "(09[5-7][0-9]{7})|\s*",
          "err": "Number must begin with 095, 096, or 097 and must be a total of 10 digits in length"
        }
      },
      {
        "key": "Father_Guardian_Name",
        "openmrs_entity_parent": "",
        "openmrs_entity": "concept",
        "openmrs_entity_id": "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "openmrs_data_type": "text",
        "type": "edit_text",
        "hint": "Father/guardian full name",
        "edit_type": "name",
        "v_regex": {
          "value": "[A-Za-z\\s\.\-]*",
          "err": "Please enter a valid name"
        }
      },
      {
        "key": "Father_Guardian_NRC",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person_attribute",
        "openmrs_entity_id": "Father_NRC_Number",
        "type": "edit_text",
        "hint": "Father/guardian NRC number",
        "v_regex": {
          "value": "([0-9]{6}/[0-9]{2}/[0-9])|\s*",
          "err": "Number must take the format of ######/##/#"
        }
      },
      {
        "key": "Place_Birth",
        "openmrs_entity_parent": "",
        "openmrs_entity": "concept",
        "openmrs_entity_id": "1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "openmrs_data_type": "select one",
        "type": "spinner",
        "hint": "Place of birth *",
        "values": [
          "Health facility",
          "Home"
        ],
        "openmrs_choice_ids": {
          "Health facility": "1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
          "Home": "1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        "v_required": {
          "value": true,
          "err": "Please enter the place of birth"
        }
      },
      {
        "key": "Birth_Facility_Name",
        "openmrs_entity_parent": "",
        "openmrs_entity": "concept",
        "openmrs_entity_id": "163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "openmrs_data_type": "text",
        "type": "tree",
        "hint": "Which health facility was the child born in? *",
        "tree": [],
        "v_required": {
          "value": true,
          "err": "Please enter the birth facility name"
        },
        "relevance": {
          "step1:Place_Birth": {
            "type": "string",
            "ex": "equalTo(., \"Health facility\")"
          }
        }
      },
      {
        "key": "Birth_Facility_Name_Other",
        "openmrs_entity_parent": "163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "openmrs_entity": "concept",
        "openmrs_entity_id": "160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "type": "edit_text",
        "hint": "Other health facility *",
        "edit_type": "name",
        "v_required": {
          "value": true,
          "err": "Please specify the health facility the child was born in"
        },
        "relevance": {
          "step1:Birth_Facility_Name": {
            "type": "string",
            "ex": "equalTo(., \"[\"Other\"]\")"
          }
        }
      },
      {
        "key": "Residential_Area",
        "openmrs_entity_parent": "usual_residence",
        "openmrs_entity": "person_address",
        "openmrs_entity_id": "address3",
        "openmrs_data_type": "text",
        "type": "tree",
        "hint": "Child's residential area *",
        "tree": [],
        "v_required": {
          "value": true,
          "err": "Please enter the child's residential area"
        }
      },
      {
        "key": "Residential_Area_Other",
        "openmrs_entity_parent": "usual_residence",
        "openmrs_entity": "person_address",
        "openmrs_entity_id": "address5",
        "type": "edit_text",
        "hint": "Other residential area *",
        "edit_type": "name",
        "v_required": {
          "value": true,
          "err": "Please specify the residential area"
        },
        "relevance": {
          "step1:Residential_Area": {
            "type": "string",
            "ex": "equalTo(., \"[\"Other\"]\")"
          }
        }
      },
      {
        "key": "Residential_Address",
        "openmrs_entity_parent": "usual_residence",
        "openmrs_entity": "person_address",
        "openmrs_entity_id": "address2",
        "type": "edit_text",
        "hint": "Home address *",
        "edit_type": "name",
        "v_required": {
          "value": true,
          "err": "Please enter the home address"
        }
      },
      {
        "key": "Physical_Landmark",
        "openmrs_entity_parent": "usual_residence",
        "openmrs_entity": "person_address",
        "openmrs_entity_id": "address1",
        "type": "edit_text",
        "hint": "Landmark",
        "edit_type": "name"
      },
      {
        "key": "CHW_Name",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person_attribute",
        "openmrs_entity_id": "CHW_Name",
        "type": "edit_text",
        "hint": "CHW name",
        "edit_type": "name",
        "v_regex": {
          "value": "[A-Za-z\\s\.\-]*",
          "err": "Please enter a valid name"
        }
      },
      {
        "key": "CHW_Phone_Number",
        "openmrs_entity_parent": "",
        "openmrs_entity": "person_attribute",
        "openmrs_entity_id": "CHW_Phone_Number",
        "type": "edit_text",
        "hint": "CHW phone number",
        "v_numeric": {
          "value": "true",
          "err": "Number must begin with 095, 096, or 097 and must be a total of 10 digits in length"
        },
        "v_regex": {
          "value": "(09[5-7][0-9]{7})|\s*",
          "err": "Number must begin with 095, 096, or 097 and must be a total of 10 digits in length"
        }
      },
      {
        "key": "PMTCT_Status",
        "openmrs_entity_parent": "",
        "openmrs_entity": "concept",
        "openmrs_entity_id": "1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "type": "spinner",
        "hint": "HIV exposure",
        "values": [
          "CE",
          "MSU",
          "CNE"
        ],
        "openmrs_choice_ids": {
          "CE": "703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
          "MSU": "1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
          "CNE": "664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        }
      }
    ]
  }
}

```


# Usage

Here are a few instructions on how to write the JSON Form:


```
//Start of your JSON FORM
{
    "count": 1, 
    "encounter_type": "Birth Registration",
    "mother" : {},
    "entity_id": "",
    "relational_id" : "",
    "metadata": {},
    "step1": {
        "title": "Birth Registration",
        "fields": [
            {
                "type": "edit_text",
                "openmrs_entity_id": "",
                "hint": "First name",
                "key": "First_Name",
                "v_regex" : {
                        "value": "[A-Za-z]*",
                        "err": "Please enter a valid name"
                    }
                },
            {}
        ]
        },
}

```


Android Implementation Code:

```
//Other imports here ...
import com.vijay.jsonwizard.activities.JsonFormActivity;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_GET_JSON = 1234;

    //.. Initialisation methods here

    public void startForm(int jsonFormActivityRequestCode,
                              String formName, String entityId) throws Exception {

        //Inject OpenMRS MetaData into the form here...

        Intent intent = new Intent(this, JsonFormActivity.class);
        intent.putExtra("json", form.toString());
        Log.d(getClass().getName(), "form is " + form.toString());
        startActivityForResult(intent, jsonFormActivityRequestCode);

    }

    public JSONObject getFormJson(String formIdentity) {

        try {
            InputStream inputStream = getApplicationContext().getAssets()
                    .open("json.form/" + formIdentity + ".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));
            String jsonString;
            StringBuilder stringBuilder = new StringBuilder();
            while ((jsonString = reader.readLine()) != null) {
                stringBuilder.append(jsonString);
            }
            inputStream.close();

            return new JSONObject(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    //Handle the result returned...

```

#### JSON FORM ATTRIBUTES

Attribute | Type | Meaning
--------- | ---- | ---
count | Integer | Number of steps or encounters
encounter_type | String | Type of Encounter in OpenMRS
mother | JSON Object | Object detailing the mother's details eg. **first_name, second_name**
*entity_id | String | Unique identifier of the OpenSRP Entity
*relational_id | String | Unique identifer for the relationship in OpenSRP
metadata | JSON Object | Object detailing the metadata such as **start, end, simserial, deviceid** objects
step1 | JSON Object | Object detailing a single form to be displayed in fragment, activity. Upcoming steps i.e. `step2, step3` will only be displayed after step1 is completed


**STEP/SINGLE FORM ATTRIBUTES**

Attribute | Type | Meaning
--------- | ---- | -------
title | String | The title of the form
fields | Array(of fields) | This is a list of input fields in the form


**INPUT FIELD ATTRIBUTES**

Attribute | Type | Meaning
--------- | ---- | -------
type | String | The type of input field: Available - `edit_text, choose_image, check_box, spinner, radio, label`
*openmrs_entity_id | String | The unique identifier of the OpenMRS Entity
hint | String | This is the String displayed incase the input field is blank
*key | String | This is the field name in the data models
v_regex | JSON Validation Object | This is an object declaring the validation rule in regex under `name` & validation error message to be show `err`


> * Attributes with an **asterisk**. Contact developer team or [OpenMRS documentation](https://smartregister.atlassian.net/wiki/spaces/Documentation/overview) for more information


# Developer Documentation

This section will provide a brief description how to build and install the application from the repository source code.

## Pre-requisites

1. Make sure you have Java 1.7 to 1.8 installed
2. Make sure you have Android Studio installed or [download it from here](https://developer.android.com/studio/index.html)


## Installation Devices

1. Use a physical Android device to run the app
2. Use the Android Emulator that comes with the Android Studio installation (Slow & not advisable)
3. Use Genymotion Android Emulator
    * Go [here](https://www.genymotion.com/) and register for genymotion account if none. Free accounts have limitations which are not counter-productive
    * Download your OS Version of VirtualBox at [here](https://www.virtualbox.org/wiki/Downloads)
    * Install VirtualBox
    * Download Genymotion & Install it
    * Sign in to the genymotion app
    * Create a new Genymotion Virtual Device 
        * **Preferrable & Stable Choice** - API 22(Android 5.1.0), Screen size of around 800 X 1280, 1024 MB Memory --> eg. Google Nexus 7, Google Nexus 5

## How to install

1. Import the project into Android Studio by: **Import a gradle project** option
   _All the plugins required are explicitly stated therefore can work with any Android Studio version - Just enable it to download any packages not available offline_
1. Open Genymotion and Run the Virtual Device created previously.
1. Run the app on Android Studio and chose the Genymotion Emulator as the ` Deployment Target`

## Customisations From Android Native JSON Form

Some customisations were done on the parent library to fit OpenMRS requirements. Below are the customisations:

1. Input field mappings to OpenMRS entity fields i.e. 
   * `openmrs_entity_id` 
   * `openmrs_entity`
   * `openmrs_entity_parent`
   * `openmrs_data_type` ...

2. Default metadata (`metadata` field) for each JSON FORM i.e.
   * Objects
      * `start`
      * `end`
      * `today`
      * `deviceid`
      * `subscriberid`
      * `simserial`
      * `phonenumber`
      * `encounter_location`
      * `look_up` 

3. Default fields for each JSON Form i.e.
   * `encounter_type`
   * `mother`
   * `entity_id`
   * `relational_id`

4. Validations & Constraints for input fields i.e.
   * `v_regex` - For validating input using [Regular Expression](https://en.wikipedia.org/wiki/Regular_expression) 
   * `v_min` - Ensure input entered is not below minimum value provided
   * `v_max` - Ensure that the input entered is not above the maximum value provided
