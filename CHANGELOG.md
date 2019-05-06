# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

- Allow multiple installations side by side for opensrp apps that use the select image function
    - Starting Opensrp Client Native Form library Snapshot Version *1.0.5-SNAPSHOT* and Release Version *1.0.3* from Any OpenSRP implementations utilizing the library should now specify the File Provider authority in the manifest as
     `android:authorities="${applicationId}.fileprovider"`
     
     
### [1.3.0] - 11-29-2018
--------------------
#### Changed
- The checkbox UI XML to use Linear layouts with layout widths instead of Relative layouts
- New way to demo notable features in native form sample App - Multiple buttons with mnemonic feature name titles

#### Fixed
- Checkbox widget all options select/deselect on moving to the next step and back caused by same view Id assignment
- Improved optimization during json form render for special views (constrained, calculated and relevance views)

#### Added
- The specify extra information popup. This enables you to define multiple widgets on the specify extra info popup. Examples of how to add it are in the sample json file
- Add support for setting custom title in form exit alert dialog confirmation
- Add support for setting custom message in form exit alert dialog confirmation
    - By overriding the `JsonFormActivity` class in your installation you get access to these methods e.g. `setConfirmCloseTitle(message)` and `getConfirmCloseTitle()`
 
- Add checkbox multi select Skip Logic
    - It is now possible to implement complex skip logic using the relevance attribute for a multi select checkbox. Check out the README document for more details on usage

- Add checkbox multi select Skip Logic
- Add checkbox multi select exclusive selector logic 
- Integrate rules engine for rule support using yaml configs
- Support for skip logic using complex rules 
- Support for constraint logic using complex rules for number selector widget
- Add helper class for use in rules engine - added method to get difference in days for 2 dates to be used in condition or action , example syntax `calculation = (1 + helper.getDifferenceDays('2018-12-18'))` 
- Add skip logic based on date duration

### [1.5.0-SNAPSHOT] - 02-29-2019
--------------------
#### Added
- Added the support for saving the widget OpenMRS attributes and the value options for widgets with secondary values | popups e.g the native radio button, the anc radio button, the spinner and the check boxes.
  - This will make a single secondary value option to look like below:-
    ```json{
            "key": "respiratory_exam_abnormal",
                "type": "check_box",
                "values": [
                  "rapid_breathing:Rapid breathing:true",
                  "rales:Rales:true",
                  "other:Other (specify):true"
                ],
                "openmrs_attributes": {
                  "openmrs_entity_parent": "",
                  "openmrs_entity": "",
                  "openmrs_entity_id": ""
                },
                "value_openmrs_attributes": [
                  {
                    "key": "respiratory_exam_abnormal",
                    "openmrs_entity_parent": "",
                    "openmrs_entity": "TACHYPNEA",
                    "openmrs_entity_id": "125061"
                  },
                  {
                    "key": "respiratory_exam_abnormal",
                    "openmrs_entity_parent": "",
                    "openmrs_entity": "Respiratory Crackles",
                    "openmrs_entity_id": "127640"
                  },
                  {
                    "key": "respiratory_exam_abnormal",
                    "openmrs_entity_parent": "",
                    "openmrs_entity": "",
                    "openmrs_entity_id": ""
                  }
                ]
              }

### [1.5.3-SNAPSHOT] - 03-13-2019
--------------------
#### Fixed
- Rules engine not returning negative values for calculation

#### Changed 
- Layout for labels used by radio buttons and checkboxes
- Text appearance on templates to bold

### [1.5.4-SNAPSHOT] - 03-14-2019
--------------------
#### Added
- Added the French translations from Transifex 

### [1.5.5-SNAPSHOT] - 03-18-2019
--------------------
#### Added
- Added a rules engine helper class to transform the GA from a string and give back the GA in numeric values
- Added an example of how to use the tree location widget

#### Changed
- Updated the `attachJson` function scope for the `DatePickerFactory.class` & `EdixTextFactory` to protected so as to enable extending

### [1.5.6-SNAPSHOT] - 03-20-2019
--------------------
#### Added
- Added a value set function to the hidden text factory. This is to enable the setting of this values from the previous ones incase calculations fail

#### Changed
- Updated the scope for the `moveToNextWizardStep` to protected to allow for extension

### [1.5.7-SNAPSHOT] - 03-26-2019
--------------------
#### Changed
- Updated the native radio button to display the secondary values picked before step change
- Update the `JsonFormActivity` to allow implementation of the `getConfirmCloseMessage, setConfirmCloseMessage, getConfirmCloseTitle, setConfirmCloseTitle`
- Moved validation for Edittext to OnTextChanged from OnFocusChange event

### [1.6.1-SNAPSHOT] - 04-26-2019
--------------------
#### Changed
- Added RepeatingGroup widget

### [1.6.2-SNAPSHOT] - 05-02-2019
--------------------
#### Added
- Added a calculation logic to write values on the main form values from the popup
    - This only works with the rules engine logic. 
    - You need to add the src json object on the calculation json object.  
    - Then set the `key` (this is the key of main widget launching the popup)
    - Then set the `option_key` (this is the option key where the popup is located )
    - Then set the `stepName` (this is the form step name)
    - ```json
{
        "key": "toaster26_hidden",
        "openmrs_entity_parent": "",
        "openmrs_entity": "",
        "openmrs_entity_id": "",
        "type": "hidden",
        "calculation": {
          "rules-engine": {
            "ex-rules": {
              "rules-file": "sample-calculation-rules.yml"
            }
          },
          "src": {
            "key": "cervical_exam",
            "option_key": "1",
            "stepName": "step1"
          }
        }
      }

