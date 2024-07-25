# UI Surveyor plugin for IDEA

![Build](https://github.com/TarCV/surveyor-idea/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/21218-ui-surveyor.svg)](https://plugins.jetbrains.com/plugin/21218-ui-surveyor)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/21218-ui-surveyor.svg)](https://plugins.jetbrains.com/plugin/21218-ui-surveyor)

## Description
<!-- Plugin description -->
UI Surveyor plugin provides features helping designing selectors for mobile automated tests:
They provide the following features:
* **_Evaluating_ selectors against XML UI snapshots** <br />
  * UIAutomator UISelectors and BySelectors for Android
    ![Search by UISelector](https://github.com/TarCV/surveyor-idea/raw/main/docs/Search.png)
  * (Experimental) WebDriverAgent/Appium class chain and predicate queries for iOS
    ![Search by class chain](https://github.com/TarCV/surveyor-idea/raw/main/docs/SearchPredicate.png)
* **Syntax highlighting and autocomplete for element selectors (UIAutomator selectors in Java IDEs only)** <br />
![Autocomplete & Highlighting](https://github.com/TarCV/surveyor-idea/raw/main/docs/Autocomplete.png)
* **Improved structure navigation for XML UI snapshots** <br />
  **Note:** When using with an old IDE or Kotlin plugin, navigation bar display might be broken - XML tag names
  displayed without any UI element identifiers or texts (e.g. just `node` instead of `Button "ZERO"`). This is a known
  issue that can be fixed by updating your Kotlin plugin and/or IDE (please see [KTIJ-27484](https://youtrack.jetbrains.com/issue/KTIJ-27484/) for details).<br />
  ![Structure navigation](https://github.com/TarCV/surveyor-idea/raw/main/docs/StructureNavigation.png)
  ![Structure navigation](https://github.com/TarCV/surveyor-idea/raw/main/docs/StructurePredicate.png)

If for some reason `Locate Element` tool window is not enabled, you can open it from the View menu: <br />
![View &gt; Tool windows &gt; Locate Element](https://github.com/TarCV/surveyor-idea/raw/main/docs/MenuLocation.png)

All trademarks are the property of their respective owners. All company, product and service names
used in this plugin description are for identification purposes only. Use of these names or brands does not imply endorsement.
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "surveyor-idea"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/TarCV/surveyor-idea/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
