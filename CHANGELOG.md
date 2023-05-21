<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# surveyor-idea Changelog

## [Unreleased]

### Fixed
- Module files for selector highlighting should not be created now (previously,
.idea/UISurveyor/UISurveyor_Highlighting.iml was created, which is now safe to delete).
- Added underscores to the name of the module for selector highlighting (__UISurveyor_Highlighting), so this utility
module is not confused with normal project modules. Duplicates with the old name are automatically removed when
the plugin tool window is opened. But only as long as they still contain just one library - 'uiautomator'.

## [1.0.0] - 2023-03-08

### Added
- UISelector and BySelector evaluation
- Selector autocomplete & highlighting as Java code
- Structure navigation for UI snapshots

[Unreleased]: https://github.com/TarCV/surveyor-idea/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/TarCV/surveyor-idea/commits/v1.0.0
