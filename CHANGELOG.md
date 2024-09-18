<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# surveyor-idea Changelog

## [Unreleased]

### Added
- Updated to UiAutomator 2.3 and added support for new view attributes

### Fixed
- Updated dependencies

## [2.0.0] - 2024-07-25

### Added

- Class chain and predicate query support

### Fixed

- Double-click in Structure panel should now work for navigating inside a snapshot
- Mod+Enter keyboard shortcut previously sometimes changed the current selector
- Added a note on a minor incompatibility with old IDE/Kotlin plugin versions
- Updated dependencies

## [1.0.2] - 2023-12-10

### Fixed

- Keyboard shortcut for locating an element is now Cmd+Enter on Mac. Previously it seems to require double Ctrl+Enter
- Toolwindow buttons might have been disabled under very rare conditions
- Remove "UI Surveyor" prefixes from main menu items where it looked redundant

## [1.0.1] - 2023-05-25

### Fixed

- Module files for selector highlighting should not be created now (previously,
.idea/UISurveyor/UISurveyor_Highlighting.iml was created, which is now safe to delete).
- Added underscores to the name of the module for selector highlighting (\_\_UISurveyor\_Highlighting), so this utility
module is not confused with normal project modules. Duplicates with the old name are automatically removed when
the plugin tool window is opened. But only as long as they still contain just one library - 'uiautomator'.
- IDEs should now suggest this plugin for .UIX files

## [1.0.0] - 2023-03-08

### Added

- UISelector and BySelector evaluation
- Selector autocomplete & highlighting as Java code
- Structure navigation for UI snapshots

[Unreleased]: https://github.com/TarCV/surveyor-idea/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/TarCV/surveyor-idea/compare/v1.0.2...v2.0.0
[1.0.2]: https://github.com/TarCV/surveyor-idea/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/TarCV/surveyor-idea/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/TarCV/surveyor-idea/commits/v1.0.0
