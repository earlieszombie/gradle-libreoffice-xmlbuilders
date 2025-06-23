package org.boldi.libreoffice.utils.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Optional

@SuppressWarnings('unused')
abstract class ManifestBuilderTaskConfig extends XmlBuildersBaseTaskConfig {

    abstract DirectoryProperty getLicensesDir()
    @Optional
    abstract DirectoryProperty getDescriptionsDir()

}
