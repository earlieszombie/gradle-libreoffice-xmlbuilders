package org.boldi.libreoffice.utils.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

@SuppressWarnings('unused')
abstract class DescriptionBuilderTaskConfig extends XmlBuildersBaseTaskConfig {

    abstract Property<String> getVersionGitTag()
    abstract Property<String> getComponentServiceId()
    abstract Property<String> getLoMinimalVersion()
    abstract Property<String> getLoName()
    abstract Property<String> getDisplayName()
    abstract Property<String> getPublisher()
    abstract DirectoryProperty getLicensesDir()

}
