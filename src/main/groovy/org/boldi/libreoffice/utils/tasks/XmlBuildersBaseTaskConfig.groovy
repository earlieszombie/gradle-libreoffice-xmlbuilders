package org.boldi.libreoffice.utils.tasks


import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

@SuppressWarnings('unused')
abstract class XmlBuildersBaseTaskConfig {

    abstract RegularFileProperty getOutputFile()
    abstract Property<String> getProjectName()

}
