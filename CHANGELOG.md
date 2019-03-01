# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

- Allow multiple installations side by side for opensrp apps that use the select image function
  - Starting Opensrp Client Native Form library Snapshot Version *1.0.5-SNAPSHOT* and Release Version *1.0.3* from Any opensrp implementations utilizing the library should now specify the File Provider authority in the manifest as
     `android:authorities="${applicationId}.fileprovider"`
     
     
[1.3.0] - 11-29-2018
--------------------
##### Changed
- The checkbox UI XML to use Linear layouts with layout widths instead of Relative layouts
- New way to demo notable features in native form sample App - Multiple buttons with mnemonic feature name titles

##### Fixed
- Checkbox widget all options select/deselect on moving to the next step and back caused by same view Id assignment
- Improved optimization during json form render for special views (constrained, calculated and relevance views)

#### Added
- The specify extra information popup. This enables you to define multiple widgets on the specify extra info popup. Examples of how to add it are in the sample json file


#### Added
 
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



[1.5.0] - 02-29-2019
--------------------
#### Added
 - Added the support for saving the widget OpenMRS attributes and the value options for widgets with options e.g the native radio button, the anc radio button, the spinner and the check boxes. 
 