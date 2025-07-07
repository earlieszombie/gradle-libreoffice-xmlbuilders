# LibreOffice XML Builders Gradle Plugin

This Gradle plugin provides utility functions and helpers to streamline the process of building and manipulating XML files
involved in producing LibreOffice Calc Add-ins in Java.
The plugin is designed to support automation tasks associated with LibreOffice Calc Add-ins development.

## Features

- **YAML-to-XML Conversion:** Read YAML definitions and produce XML output suitable for LibreOffice Calc Add-ins.
- **Seamless Gradle Integration:** Easily incorporate XML building utilities in Java and Groovy-based Gradle projects.
- **Testing with Spock:** Includes basic unit and functional test support using the Spock framework.

## Getting Started

### Apply the Plugin

Add the plugin to your `build.gradle`:
```groovy
plugins {
    id 'org.boldi.libreoffice.utils.xmlbuilders' version '1.0.0'
}
```

### Usage Overview

Once applied, the plugin exposes DSLs and tasks for:

- Converting YAML configuration into LibreOffice-compatible XML fragments
- Automating routine XML edits for extension development

Refer to project documentation or Gradle task help for available commands and configuration options.

### Dependencies

This project utilizes:

- [SnakeYAML](https://bitbucket.org/asomov/snakeyaml) for YAML parsing
- [Spock Framework](https://spockframework.org/) for testing

All dependencies are automatically managed through Gradle.

### Testing

The plugin includes both standard and functional tests. After modifying the code, run:
```sh
./gradlew check
```