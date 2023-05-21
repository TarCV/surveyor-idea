<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# surveyor-idea Changelog

## [Unreleased]

### Fixed
- Highlighter module files should not be created on a disk now (previously .idea/UISurveyor/UISurveyor_Highlighting.iml
were created).
- Added underscores to the highlighter module (new name is __UISurveyor_Highlighting) to avoid confusion with normal
project modules. Old module will be automatically removed when the plugin toolbar is opened, as long as the module still
contains just one library - 'uiautomator'.

## [1.0.0] - 2023-03-08

### Added
- UISelector and BySelector evaluation
- Selector autocomplete & highlighting as Java code
- Structure navigation for UI snapshots

[Unreleased]: https://github.com/TarCV/surveyor-idea/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/TarCV/surveyor-idea/commits/v1.0.0
