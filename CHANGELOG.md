# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

- Allow multiple installations side by side for opensrp apps that use the select image function
  - Starting Opensrp Client Native Form library Snapshot Version *1.0.4-SNAPSHOT* and Release Version *1.0.3* from Any opensrp implementations utilizing the library should now specify the File Provider authority in the manifest as
     `android:authorities="${applicationId}.fileprovider"`
