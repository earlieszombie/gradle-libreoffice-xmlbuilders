package org.boldi.libreoffice.utils.tasks

import org.gradle.api.provider.Property

@SuppressWarnings('unused')
abstract class Log4J2XmlBuilderTaskConfig extends XmlBuildersBaseTaskConfig {

    abstract Property<String> getComponentRootUrl()
    abstract Property<String> getLogsRootDirPath()

}
