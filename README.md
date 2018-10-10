[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-native-form.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-native-form) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-native-form/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-native-form?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/73c9d3b1fd9140fda8397ebe518825bc)](https://www.codacy.com/app/OpenSRP/opensrp-client-native-form?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-native-form&amp;utm_campaign=Badge_Grade)

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

OpenSRP Client Native Form Module/app provides the ability to easily create Android forms using JSON forms. 

Thanks to this [Android Native JSON Form Library](https://github.com/vijayrawatsan/android-json-form-wizard) from which OpenSRP Client Native Form has been customised to fit OpenSRP Requirements

> **NOTE**
>
> **JSON Form** is written using **JSON (syntax)** which can be found [here](http://json.org/).


# Features

1. It enables definition of Android forms in JSON
2. It enables one to define metadata for OpenMRS forms
3. It enables one to define validations for form inputs in JSON
   * [Regular Expression](https://en.wikipedia.org/wiki/Regular_expression) validation rules
   * Number validation rules
   * Alphabetic & alphanumeric validation rules in JSON
4. It enables one to define form input constraints using JSON
   * Min value
   * Max value
5. It enables one to define OpenMRS mappings in JSON
6. It enables one to define skip logic for fields based on values entered in other fields

# App Walkthrough

1. When you open the app, the following page is displayed

![Main Page Screenshot](https://user-images.githubusercontent.com/31766075/30383189-ca377ca6-98a9-11e7-8c23-9538214a975f.png)
![Main Page Screenshot -> Menu Open](https://user-images.githubusercontent.com/31766075/30383181-c49c8732-98a9-11e7-9f95-f56fc0cb7931.png)

This page has a menu at the top-right which opens a sample Patient Registration form written in JSON Form.

#### 2. Sample Form

Below is a sample Android Form created using the `OpenSRP Native JSON Form`:

![Sample Form Screenshot](https://user-images.githubusercontent.com/31766075/30383177-c4285aa6-98a9-11e7-84de-5550c0d1d159.png)
![Part 2](https://user-images.githubusercontent.com/31766075/30435414-6321428a-9972-11e7-84c5-d22e841faf1c.png)
![Part 3](https://user-images.githubusercontent.com/31766075/30435413-631caed2-9972-11e7-8d86-adc81936f27d.png)
![Part 4](https://user-images.githubusercontent.com/31766075/30435415-632578be-9972-11e7-9e67-c14cf250a2ab.png)

This form has been generated from the `JSON Form` below: [Click here to Skip](#usage)

#### Sample JSON Form

`child_enrollment.json`

```
{  
    "count":"1",
    "encounter_type":"Birth Registration",
    "mother":{  
        "encounter_type":"New Woman Registration"
    },
    "entity_id":"",
    "relational_id":"",
    "metadata":{  
        "start":{  
            "openmrs_entity_parent":"",
            "openmrs_entity":"concept",
            "openmrs_data_type":"start",
            "openmrs_entity_id":"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        "end":{  
            "openmrs_entity_parent":"",
            "openmrs_entity":"concept",
            "openmrs_data_type":"end",
            "openmrs_entity_id":"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        "today":{  
            "openmrs_entity_parent":"",
            "openmrs_entity":"encounter",
            "openmrs_entity_id":"encounter_date"
        },
        "deviceid":{  
            "openmrs_entity_parent":"",
            "openmrs_entity":"concept",
            "openmrs_data_type":"deviceid",
            "openmrs_entity_id":"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        "subscriberid":{  
            "openmrs_entity_parent":"",
            "openmrs_entity":"concept",
            "openmrs_data_type":"subscriberid",
            "openmrs_entity_id":"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        "simserial":{  
            "openmrs_entity_parent":"",
            "openmrs_entity":"concept",
            "openmrs_data_type":"simserial",
            "openmrs_entity_id":"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        "phonenumber":{  
            "openmrs_entity_parent":"",
            "openmrs_entity":"concept",
            "openmrs_data_type":"phonenumber",
            "openmrs_entity_id":"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        "encounter_location":"",
        "look_up":{  
            "entity_id":"",
            "value":""
        }
    },
    "step1":{  
        "title":"Birth Registration",
        "fields":[  
            {  
                "key":"Child_Photo",
                "openmrs_entity_parent":"",
                "openmrs_entity":"",
                "openmrs_entity_id":"",
                "type":"choose_image",
                "uploadButtonText":"Take a photo of the child"
            },
            {  
                "key":"gps",
                "openmrs_entity_parent":"usual_residence",
                "openmrs_entity":"person_address",
                "openmrs_entity_id":"geopoint",
                "openmrs_data_type":"text",
                "type":"gps"
            },
            {  
                "key":"Home_Facility",
                "openmrs_entity_parent":"",
                "openmrs_entity":"",
                "openmrs_entity_id":"",
                "openmrs_data_type":"text",
                "type":"tree",
                "hint":"Child's home health facility *",
                "tree":[  

                ],
                "v_required":{  
                    "value":true,
                    "err":"Please enter the child's home facility"
                }
            },
            {  
                "key":"ZEIR_ID",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_identifier",
                "openmrs_entity_id":"ZEIR_ID",
                "type":"barcode",
                "barcode_type":"qrcode",
                "hint":"Child's ZEIR ID *",
                "scanButtonText":"Scan QR Code",
                "value":"0",
                "v_numeric":{  
                    "value":"true",
                    "err":"Please enter a valid ID"
                },
                "v_required":{  
                    "value":"true",
                    "err":"Please enter the Child's ZEIR ID"
                }
            },
            {  
                "key":"Child_Register_Card_Number",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"Child_Register_Card_Number",
                "type":"edit_text",
                "hint":"Child's register card number"
            },
            {  
                "key":"Child_Birth_Certificate",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"Child_Birth_Certificate",
                "type":"edit_text",
                "hint":"Child's birth certificate number"
            },
            {  
                "key":"First_Name",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person",
                "openmrs_entity_id":"first_name",
                "type":"edit_text",
                "hint":"First name",
                "edit_type":"name",
                "v_regex":{  
                    "value":"[A-Za-z\\s\.\-]*",
                    "err":"Please enter a valid name"
                }
            },
            {  
                "key":"Last_Name",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person",
                "openmrs_entity_id":"last_name",
                "type":"edit_text",
                "hint":"Last name *",
                "edit_type":"name",
                "v_required":{  
                    "value":"true",
                    "err":"Please enter the last name"
                },
                "v_regex":{  
                    "value":"[A-Za-z\\s\.\-]*",
                    "err":"Please enter a valid name"
                }
            },
            {  
                "key":"Sex",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person",
                "openmrs_entity_id":"gender",
                "type":"spinner",
                "hint":"Sex *",
                "values":[  
                    "Male",
                    "Female"
                ],
                "v_required":{  
                    "value":"true",
                    "err":"Please enter the sex"
                }
            },
            {  
                "key":"Date_Birth",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person",
                "openmrs_entity_id":"birthdate",
                "type":"date_picker",
                "hint":"Child's DOB *",
                "expanded":false,
                "duration":{  
                    "label":"Age"
                },
                "min_date":"today-5y",
                "max_date":"today",
                "v_required":{  
                    "value":"true",
                    "err":"Please enter the date of birth"
                }
            },
            {  
                "key":"First_Health_Facility_Contact",
                "openmrs_entity_parent":"",
                "openmrs_entity":"concept",
                "openmrs_entity_id":"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "openmrs_data_type":"text",
                "type":"date_picker",
                "hint":"Date first seen *",
                "expanded":false,
                "min_date":"today-5y",
                "max_date":"today",
                "v_required":{  
                    "value":"true",
                    "err":"Enter the date that the child was first seen at a health facility for immunization services"
                },
                "constraints":[  
                    {  
                        "type":"date",
                        "ex":"greaterThanEqualTo(., step1:Date_Birth)",
                        "err":"Date first seen can't occur before date of birth"
                    }
                ]
            },
            {  
                "key":"Birth_Weight",
                "openmrs_entity_parent":"",
                "openmrs_entity":"concept",
                "openmrs_entity_id":"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "openmrs_data_type":"text",
                "type":"edit_text",
                "hint":"Birth weight (kg) *",
                "v_min":{  
                    "value":"0.1",
                    "err":"Weight must be greater than 0"
                },
                "v_numeric":{  
                    "value":"true",
                    "err":"Enter a valid weight"
                },
                "v_required":{  
                    "value":"true",
                    "err":"Enter the child's birth weight"
                }
            },
            {  
                "key":"Mother_Guardian_First_Name",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person",
                "openmrs_entity_id":"first_name",
                "entity_id":"mother",
                "type":"edit_text",
                "hint":"Mother/guardian first name *",
                "edit_type":"name",
                "look_up":"true",
                "v_required":{  
                    "value":"true",
                    "err":"Please enter the mother/guardian's first name"
                },
                "v_regex":{  
                    "value":"[A-Za-z\\s\.\-]*",
                    "err":"Please enter a valid name"
                }
            },
            {  
                "key":"Mother_Guardian_Last_Name",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person",
                "openmrs_entity_id":"last_name",
                "entity_id":"mother",
                "type":"edit_text",
                "hint":"Mother/guardian last name *",
                "edit_type":"name",
                "look_up":"true",
                "v_required":{  
                    "value":"true",
                    "err":"Please enter the mother/guardian's last name"
                },
                "v_regex":{  
                    "value":"[A-Za-z\\s\.\-]*",
                    "err":"Please enter a valid name"
                }
            },
            {  
                "key":"Mother_Guardian_Date_Birth",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person",
                "openmrs_entity_id":"birthdate",
                "entity_id":"mother",
                "type":"date_picker",
                "hint":"Mother/guardian DOB",
                "look_up":"true",
                "expanded":false,
                "duration":{  
                    "label":"Age"
                },
                "min_date":"01-01-1900",
                "max_date":"today-10y"
            },
            {  
                "key":"Mother_Guardian_NRC",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"NRC_Number",
                "entity_id":"mother",
                "type":"edit_text",
                "hint":"Mother/guardian NRC number",
                "v_regex":{  
                    "value":"([0-9]{6}/[0-9]{2}/[0-9])|\s*",
                    "err":"Number must take the format of ######/##/#"
                }
            },
            {  
                "key":"Mother_Guardian_Number",
                "openmrs_entity_parent":"",
                "openmrs_entity":"concept",
                "openmrs_entity_id":"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "type":"edit_text",
                "hint":"Mother/guardian phone number",
                "v_numeric":{  
                    "value":"true",
                    "err":"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length"
                },
                "v_regex":{  
                    "value":"(09[5-7][0-9]{7})|\s*",
                    "err":"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length"
                }
            },
            {  
                "key":"Father_Guardian_Name",
                "openmrs_entity_parent":"",
                "openmrs_entity":"concept",
                "openmrs_entity_id":"1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "openmrs_data_type":"text",
                "type":"edit_text",
                "hint":"Father/guardian full name",
                "edit_type":"name",
                "v_regex":{  
                    "value":"[A-Za-z\\s\.\-]*",
                    "err":"Please enter a valid name"
                }
            },
            {  
                "key":"Father_Guardian_NRC",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"Father_NRC_Number",
                "type":"edit_text",
                "hint":"Father/guardian NRC number",
                "v_regex":{  
                    "value":"([0-9]{6}/[0-9]{2}/[0-9])|\s*",
                    "err":"Number must take the format of ######/##/#"
                }
            },
            {  
                "key":"Place_Birth",
                "openmrs_entity_parent":"",
                "openmrs_entity":"concept",
                "openmrs_entity_id":"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "openmrs_data_type":"select one",
                "type":"spinner",
                "hint":"Place of birth *",
                "values":[  
                    "Health facility",
                    "Home"
                ],
                "openmrs_choice_ids":{  
                    "Health facility":"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    "Home":"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                "v_required":{  
                    "value":true,
                    "err":"Please enter the place of birth"
                }
            },
            {  
                "key":"Birth_Facility_Name",
                "openmrs_entity_parent":"",
                "openmrs_entity":"concept",
                "openmrs_entity_id":"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "openmrs_data_type":"text",
                "type":"tree",
                "hint":"Which health facility was the child born in? *",
                "tree":[  

                ],
                "v_required":{  
                    "value":true,
                    "err":"Please enter the birth facility name"
                },
                "relevance":{  
                    "step1:Place_Birth":{  
                        "type":"string",
                        "ex":"equalTo(., \"Health facility\")"
                    }
                }
            },
            {  
                "key":"Birth_Facility_Name_Other",
                "openmrs_entity_parent":"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "openmrs_entity":"concept",
                "openmrs_entity_id":"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "type":"edit_text",
                "hint":"Other health facility *",
                "edit_type":"name",
                "v_required":{  
                    "value":true,
                    "err":"Please specify the health facility the child was born in"
                },
                "relevance":{  
                    "step1:Birth_Facility_Name":{  
                        "type":"string",
                        "ex":"equalTo(., \"[\"Other\"]\")"
                    }
                }
            },
            {  
                "key":"Residential_Area",
                "openmrs_entity_parent":"usual_residence",
                "openmrs_entity":"person_address",
                "openmrs_entity_id":"address3",
                "openmrs_data_type":"text",
                "type":"tree",
                "hint":"Child's residential area *",
                "tree":[  

                ],
                "v_required":{  
                    "value":true,
                    "err":"Please enter the child's residential area"
                }
            },
            {  
                "key":"Residential_Area_Other",
                "openmrs_entity_parent":"usual_residence",
                "openmrs_entity":"person_address",
                "openmrs_entity_id":"address5",
                "type":"edit_text",
                "hint":"Other residential area *",
                "edit_type":"name",
                "v_required":{  
                    "value":true,
                    "err":"Please specify the residential area"
                },
                "relevance":{  
                    "step1:Residential_Area":{  
                        "type":"string",
                        "ex":"equalTo(., \"[\"Other\"]\")"
                    }
                }
            },
            {  
                "key":"Residential_Address",
                "openmrs_entity_parent":"usual_residence",
                "openmrs_entity":"person_address",
                "openmrs_entity_id":"address2",
                "type":"edit_text",
                "hint":"Home address *",
                "edit_type":"name",
                "v_required":{  
                    "value":true,
                    "err":"Please enter the home address"
                }
            },
            {  
                "key":"Physical_Landmark",
                "openmrs_entity_parent":"usual_residence",
                "openmrs_entity":"person_address",
                "openmrs_entity_id":"address1",
                "type":"edit_text",
                "hint":"Landmark",
                "edit_type":"name"
            },
            {  
                "key":"CHW_Name",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"CHW_Name",
                "type":"edit_text",
                "hint":"CHW name",
                "edit_type":"name",
                "v_regex":{  
                    "value":"[A-Za-z\\s\.\-]*",
                    "err":"Please enter a valid name"
                }
            },
            {  
                "key":"CHW_Phone_Number",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"CHW_Phone_Number",
                "type":"edit_text",
                "hint":"CHW phone number",
                "v_numeric":{  
                    "value":"true",
                    "err":"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length"
                },
                "v_regex":{  
                    "value":"(09[5-7][0-9]{7})|\s*",
                    "err":"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length"
                }
            },
            {  
                "key":"PMTCT_Status",
                "openmrs_entity_parent":"",
                "openmrs_entity":"concept",
                "openmrs_entity_id":"1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "type":"spinner",
                "hint":"HIV exposure",
                "values":[  
                    "CE",
                    "MSU",
                    "CNE"
                ],
                "openmrs_choice_ids":{  
                    "CE":"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    "MSU":"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    "CNE":"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                }
            },
            {  
                "key":"native_radio",
                "openmrs_entity_parent":"",
                "openmrs_entity":"",
                "openmrs_entity_id":"",
                "type":"native_radio",
                "label":"Highest Level of School",
                "label_text_size":"20sp",
                "label_text_color":"#FF9800",
                "options":[  
                    {  
                        "key":"primary_school",
                        "text":"Primary school",
                        "text_color":"#000000"
                    },
                    {  
                        "key":"high_school",
                        "text":"High School",
                        "text_size":"30sp"
                    },
                    {  
                        "key":"higher_education",
                        "text":"College/University",
                        "text_color":"#358CB7"
                    }
                ],
                "value":"primary_school"
            },
            {  
                "key":"delivery_complications",
                "openmrs_entity_parent":"",
                "openmrs_entity":"concept",
                "openmrs_entity_id":"161641AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "openmrs_data_type":"select one",
                "type":"check_box",
                "label":"Any delivery complications?",
                "label_text_size":"18sp",
                "label_text_color":"#FF9800",
                "hint":"Any delivery complications?",
                "options":[  
                    {  
                        "key":"None",
                        "text":"None",
                        "value":false,
                        "openmrs_choice_id":"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {  
                        "key":"Severe bleeding/Hemorrhage",
                        "text":"Severe bleeding/Hemorrhage",
                        "value":false,
                        "openmrs_choice_id":"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        "text_color":"#000000"
                    },
                    {  
                        "key":"Placenta previa",
                        "text":"Placenta previa",
                        "value":false,
                        "openmrs_choice_id":"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        "text_size":"30sp"
                    },
                    {  
                        "key":"Cord prolapse",
                        "text":"Cord prolapse",
                        "value":false,
                        "openmrs_choice_id":"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        "text_size":"10sp"
                    },
                    {  
                        "key":"Prolonged/obstructed labour",
                        "text":"Prolonged/obstructed labour",
                        "value":false,
                        "openmrs_choice_id":"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {  
                        "key":"Abnormal presentation",
                        "text":"Abnormal presentation",
                        "value":false,
                        "openmrs_choice_id":"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        "text_color":"#FF9800"
                    },
                    {  
                        "key":"Perineal tear (2, 3 or 4th degree)",
                        "text":"Perineal tear (2, 3 or 4th degree)",
                        "value":false,
                        "openmrs_choice_id":"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {  
                        "key":"Other",
                        "text":"Other",
                        "value":false,
                        "openmrs_choice_id":"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    }
                ],
                "v_required":{  
                    "value":"false"
                }
            },
            {  
                "key":"toaster_notes",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"toaster_notes",
                "type":"toaster_notes",
                "text":"This is an information note",
                "text_color":"#1199F9",
                "toaster_type":"info"
            },
            {  
                "key":"toaster_notes",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"toaster_notes",
                "type":"toaster_notes",
                "text":"This is an information note",
                "toaster_type":"info"
            },
            {  
                "key":"toaster_notes",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"toaster_notes",
                "type":"toaster_notes",
                "text":"This is an positive note",
                "text_color":"#3E7E2E",
                "toaster_type":"positive"
            },
            {  
                "key":"toaster_notes",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"toaster_notes",
                "type":"toaster_notes",
                "text":"This is an warning note",
                "text_color":"#FFC100",
                "toaster_type":"warning"
            },
            {  
                "key":"toaster_notes",
                "openmrs_entity_parent":"",
                "openmrs_entity":"person_attribute",
                "openmrs_entity_id":"toaster_notes",
                "type":"toaster_notes",
                "text":"This is an danger note",
                "text_color":"#CF0800",
                "toaster_type":"problem"
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
    "count":1,
    "encounter_type":"Birth Registration",
    "mother":{  

    },
    "entity_id":"",
    "relational_id":"",
    "metadata":{  

    },
    "step1":{  
        "title":"Birth Registration",
        "fields":[  
            {  
                "type":"edit_text",
                "openmrs_entity_id":"",
                "hint":"First name",
                "key":"First_Name",
                "v_regex":{  
                    "value":"[A-Za-z]*",
                    "err":"Please enter a valid name"
                }
            },
            {  

            }
        ]
    },

}

```


The android implementation code can be found [here](#android-implementation-code)


#### JSON FORM ATTRIBUTES

Attribute | Type | Meaning
--------- | ---- | ---
count | Integer | Number of steps or forms
encounter_type | String | Type of Encounter in OpenMRS
mother | JSON Object | Object detailing another encounter `created` during the first step `step1`
*entity_id | String | Unique identifier of the OpenMRS Entity
*relational_id | String | Unique identifer for the relationship in OpenMRS
metadata | JSON Object | Object detailing the metadata such as **start, end, simserial, deviceid** objects found [here](#customisations-from-android-native-json-form)
step1 | JSON Object | Object detailing a single form to be displayed as a `fragment`, `activity`. Upcoming steps i.e. `step2`, `step3` will only be displayed after `step1` is completed


**STEP/SINGLE FORM ATTRIBUTES**

Attribute | Type | Meaning
--------- | ---- | -------
title | String | The title of the form
fields | Array(of form fields) | This is a list of input fields in the form


**INPUT FIELD ATTRIBUTES**

Attribute | Type | Meaning
--------- | ---- | -------
type | String | The type of input field: Available - `edit_text, choose_image, check_box, spinner, radio, label, barcode, date_picker, tree`
*openmrs_entity_id | String | The unique identifier of the OpenMRS Entity
hint | String | This is the string displayed incase the input field is blank
*key | String | This is the field name in the data models
value | String | Value of this form field. **Optional** during creation but generated after the form is filled. Default Value is `""`
look_up | String | **(Optional)**Either `"true"` or `"false"` indicating whether it is a lookup field. Default value is `"false"`
read_only | Boolean | Indicates whether the value of the field cannot be edited. Default value of `read_only` is `false`
hidden | Boolean | **(Optional)**The field is not visible. Default value is `false`


> * For attributes with an **asterisk** above, go to [OpenMRS documentation](https://smartregister.atlassian.net/wiki/spaces/Documentation/overview) for more information


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


## Android Implementation Code


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


## Customisations From Android Native JSON Form

Some customisations were done on the parent library to fit OpenSRP requirements. Below are the customisations:

1. Input field mappings to OpenMRS entity fields i.e. [OpenMRS Keys](#openmrs-keys)

2. Default metadata (`metadata` field) for each JSON FORM i.e.
   * Objects
      * `start` - Object with the time the form is openned for editing
         * `value` - Date time value in the format `yyyy-MM-dd HH:mm:ss`
      * `end` - Time the form is completed
         * `value` - Date time value in the format `yyyy-MM-dd HH:mm:ss`
      * `today` - Day the form was completed
         * `value` - Date value in the format `dd-MM-yyyy`
      * `deviceid` - Unique identifier of the provider device
      * `subscriberid` - Unique Subscriber ID based on Service Provider eg. `IMSI` will be returned for a `GSM Phone`
      * `simserial` - SIM card Serial Number, if one is available
      * `phonenumber` - Phone number for SIM 1 eg. the `MSISDN` for a `GSM Phone`
      * `encounter_location` - Health Facility Zone where the encounter happened
      * `look_up` - JSON Object describing any lookup data selected in a form lookup field. It basically has:
         * `entity_id` - The unique entity name eg. `mother`
         * `value` - The unique entity identifier eg. `9898-sd23D-f523a`

> ### OpenMRS Keys
> 
> Each metadata field objects above have the following properties:
>    * `key` 
>    * `openmrs_entity_parent`
>    * `openmrs_entity`
>    * `openmrs_data_type` 
>    * `openmrs_entity_id`

3. Mandatory fields for each JSON Form i.e.
   * `encounter_type` - This can be either of the [encounter types below](#encounter-types) or `blank` if the encounter type is not supported by OpenMRS
   * `entity_id` - Unique identifier

### Encounter Types

Below are the most common encounter types:

Encounter Type | Description
-------------- | -----------
Birth Registration | Child enrollment
AEFI | Adverse Effect of vaccines, supplements or other services given by providers
HIA2 Monthly Report | Monthly health reports written by providers
Out of Catchment Service | Health service provision to a patient outside the patient's registered location eg. During `relative visits`, `temporary relocation`
Death | A deceased child/patient is reported


4. Validations & Constraints for input fields i.e.
   * `v_regex` - This is used for validating input using [Regular Expression](https://en.wikipedia.org/wiki/Regular_expression) 
   * `v_min` - This validation ensures input entered is not below minimum value stated
   * `v_max` - Ensure that the input entered is not above the maximum value stated

5. Different types of comparisons were added for the **Skip Logic**

This enables a field to be shown only if the condition set in `'ex'` is `True`

Skip Logic is defined using the `"relevance"` value

#### Sample Skip Logic

```
"relevance": {
          "step1:Place_Birth": {
            "type": "string",
            "ex": "equalTo(., \"Health facility\")"
          }
        }
```
The field above will only be shown if the value for field whose key is `Place_Birth`, which is the **Patient's Place of Birth (Location)**, is equals to `"Health Facility"`

```
      {
        "key": "isDateOfBirthUnknown",
        "openmrs_entity_parent": "",
        "openmrs_entity": "",
        "openmrs_entity_id": "",
        "type": "check_box",
        "label": "",
        "options": [
          {
            "key": "isDateOfBirthUnknown",
            "text": "DOB unknown?",
            "value": "false"
          }
        ]
      }
```
```
        "relevance": {
          "step1:isDateOfBirthUnknown": {
            "type": "string",
            "ex": "equalTo(., \"true\")"
          }
        }
```
The above is an example showing an example with a checkbox field

The following are the available comparators:
   * Equal To - `equalTo`
   * Greater Than - `greaterThan`
   * Greater Than or Equal To - `greaterThanEqualTo`
   * Less Than - `lessThan`
   * Less Than or Equal To - `lessThanEqualTo`
   * Not Equal To - `notEqualTo`
   * Regex Comparison - `regex`

The comparators work on the following variable types:
   * String - `string`
   * Numeric `numeric` - Integer, double, float ...
   * Date `date` - In the format `dd-MM-yyyy`
   * Array `array` - Arrays are said to be similar if:
      * Have the same number of items
      * Have same items in equal number, irrespective of index

The syntax for the comparison expression `ex`:

`equalTo(., "Health facility")`

 is as follows:

`comparator($value1, $value2)`

`$value1` is supposed to be a dot `.` so that the value `$value1` is that of the referenced field --> `Place_Birth` in `step1`.

The field reference/identifier uses the field's `key` attribute


###Skip logic for Checkbox group widget

Check boxes can also be selected multiple times . The corresponding skip logic can also thus become complex. In the example below, *(present in the sample app)*,
the CHW Phone Number Widget will only show based on these various conditions:
1. Severe Bleeding is checked
2. Both Perineal Tear and Placenta Previa are checked
3. Both Cord Prolapse and Abnormal Presentation checked or just Prolonged Obstructed Labour is checked

The implementation introduces a new field `ex-checkbox` which contains the complex checkbox expression, the example for the above 3 conditions is as below. 
The relevant keys of the multi-select checkbox are specified as arrays wrapped with an object key of either `and` or `or` or `both` to be used for simple boolean logic processing. 

``` 
,
        "relevance": {
          "step1:delivery_complications": { 
            "ex-checkbox": [
              {
                "or": ["severe_bleeding"]
              },
              {
                "and": ["perineal_tear","placenta_previa"]
              },
              {
                "and": [
                  "cord_prolapse",
                  "abnormal_presentation"
                ],
                "or": [
                  "prolonged_obstructed_labour"
                ]
              }
            ]
          }

```

```
   {
        "key": "delivery_complications",
        "openmrs_entity_parent": "",
        "openmrs_entity": "concept",
        "openmrs_entity_id": "161641AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "openmrs_data_type": "select one",
        "type": "check_box",
        "label": "Any delivery complications?",
        "label_text_size": "18sp",
        "label_text_color": "#FF9800",
        "hint": "Any delivery complications?",
        "exclusive": ["none"],
        "options": [
          {
            "key": "none",
            "text": "None",
            "value": false,
            "openmrs_choice_id": "160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
          },
          {
            "key": "severe_bleeding",
            "text": "Severe bleeding/Hemorrhage",
            "value": false,
            "openmrs_choice_id": "160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "text_color": "#000000"
          },
          {
            "key": "placenta_previa",
            "text": "Placenta previa",
            "value": false,
            "openmrs_choice_id": "160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "text_size": "30sp"
          },
          {
            "key": "cord_prolapse",
            "text": "Cord prolapse",
            "value": false,
            "openmrs_choice_id": "160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "text_size": "10sp"
          },
          {
            "key": "prolonged_obstructed_labour",
            "text": "Prolonged/obstructed labour",
            "value": false,
            "openmrs_choice_id": "160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
          },
          {
            "key": "abnormal_presentation",
            "text": "Abnormal presentation",
            "value": false,
            "openmrs_choice_id": "160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "text_color": "#FF9800"
          },
          {
            "key": "perineal_tear",
            "text": "Perineal tear (2, 3 or 4th degree)",
            "value": false,
            "openmrs_choice_id": "160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
          },
          {
            "key": "Other",
            "text": "Other",
            "value": false,
            "openmrs_choice_id": "160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
          }
        ],
        "v_required": {
          "value": "false"
        }
      }
``` 
The above relevance example shows a multiple select checkbox example and its used in the sample demo app for the CHW_Phone_Number widget. 

 ### Checkbox Exclusive Select field
 The checkbox implementation also introduces a new field `exclusive` in which you specify the array value of a key or set of keys e.g. `exclusive: ["none"]` which if selected excludes/clears all other entries.
 e.g. in the above example the exclusive key array contains the key `"none"` , thus selecting the item with key none on the multi-select checkbox clears all others.

6. More input field types:
 
 ### Extra input field types

 Field Type | Name | Description
 ---------- | ---- | -----------
 TreeView | `tree` | A stepped Selection View/Widget for nested selections eg. `Happy Kids Clinic` can be found in **Zambia Ministry of Health > Northern Highlands > Fort Jameson**. They all have to be expanded to get to it
 Barcode | `barcode` | A text input field with a **SCAN QR CODE** button. It enables one to scan QR code and prints the scan result in the text input field
 Date Picker | `date_picker` | This is a date picker view
 GPS Location Picker | `gps` | This retrieves the current user location from the GPS. The value is retrievable as a `latitude longitude`(LatLng Combination separated by a space) 

