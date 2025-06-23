package org.boldi.libreoffice.utils

import org.boldi.libreoffice.utils.tasks.*

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

abstract class XmlBuildersExtension {

    abstract Property<String> getProjectName()

    final ManifestBuilderTaskConfig manifestBuilderTaskConfig
    final DescriptionBuilderTaskConfig descriptionBuilderTaskConfig
    final XcuRegistryBuilderTaskConfig xcuRegistryBuilderTaskConfig
    final Log4J2XmlBuilderTaskConfig log4J2XmlBuilderTaskConfig

    @Inject
    XmlBuildersExtension(ObjectFactory objects) {
        manifestBuilderTaskConfig = objects.newInstance(ManifestBuilderTaskConfig)
        descriptionBuilderTaskConfig = objects.newInstance(DescriptionBuilderTaskConfig)
        xcuRegistryBuilderTaskConfig = objects.newInstance(XcuRegistryBuilderTaskConfig)
        log4J2XmlBuilderTaskConfig = objects.newInstance(Log4J2XmlBuilderTaskConfig)
    }

    ManifestBuilderTaskConfig getManifestBuilderTaskConfig() {
        return manifestBuilderTaskConfig
    }

    DescriptionBuilderTaskConfig getDescriptionBuilderTaskConfig() {
        return descriptionBuilderTaskConfig
    }

    XcuRegistryBuilderTaskConfig getXcuRegistryBuilderTaskConfig() {
        return xcuRegistryBuilderTaskConfig
    }

    Log4J2XmlBuilderTaskConfig getLog4J2XmlBuilderTaskConfig() {
        return log4J2XmlBuilderTaskConfig
    }

    void buildManifestXml(Action<ManifestBuilderTaskConfig> action) {
        action.execute(manifestBuilderTaskConfig)
    }

    void buildDescriptionXml(Action<DescriptionBuilderTaskConfig> action) {
        action.execute(descriptionBuilderTaskConfig)
    }

    void buildXcuRegistry(Action<XcuRegistryBuilderTaskConfig> action) {
        action.execute(xcuRegistryBuilderTaskConfig)
    }

    void buildLog4J2Xml(Action<Log4J2XmlBuilderTaskConfig> action) {
        action.execute(log4J2XmlBuilderTaskConfig)
    }

}
