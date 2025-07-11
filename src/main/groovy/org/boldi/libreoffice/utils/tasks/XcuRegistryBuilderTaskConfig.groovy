package org.boldi.libreoffice.utils.tasks

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property


@SuppressWarnings('unused')
abstract class XcuRegistryBuilderTaskConfig {

    abstract RegularFileProperty getOutputFile()
    abstract RegularFileProperty getInputFile()
    abstract Property<String> getComponentServiceId()

}

