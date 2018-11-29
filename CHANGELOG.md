# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

- Allow multiple installations side by side for opensrp apps that use the select image function
  - Starting Opensrp Client Native Form library Snapshot Version *1.0.5-SNAPSHOT* and Release Version *1.0.3* from Any opensrp implementations utilizing the library should now specify the File Provider authority in the manifest as
     `android:authorities="${applicationId}.fileprovider"`

###Added
 
 - Add support for setting custom title in form exit alert dialog confirmation
 - Add support for setting custom message in form exit alert dialog confirmation
    - By overriding the `JsonFormActivity` class in your installation you get access to these methods e.g. `setConfirmCloseTitle(message)` and `getConfirmCloseTitle()`
 
- Add checkbox multi select Skip Logic
  - It is now possible to implement complex skip logic using the relevance attribute for a multi select checkbox. Check out the README document for more details on usage

- Add checkbox multi select Skip Logic
- Add checkbox multi select exclusive selector logic 

[1.3.0] - 11-29-2018
----------------------
###Added
- Integrate rules engine for rule support using yaml configs
- Support for skip logic using complex rules
- Support for calculations for fields using complex rules