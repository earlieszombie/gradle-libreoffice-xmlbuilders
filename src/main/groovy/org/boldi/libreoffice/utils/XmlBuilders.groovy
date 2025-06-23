package org.boldi.libreoffice.utils

import org.boldi.libreoffice.utils.tasks.*

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

@SuppressWarnings('unused')
@SuppressWarnings('UnusedProperty')
class XmlBuilders implements Plugin<Project> {

    void apply(Project project) {

        def extension = project.extensions.create('xmlBuildersExt', XmlBuildersExtension)

        project.tasks.register('buildManifestXml', ManifestBuilderTask) { task ->
            task.projectName.set(extension.manifestBuilderTaskConfig.projectName)
            task.outputFile.set(extension.manifestBuilderTaskConfig.outputFile)
            task.licensesDir.set(extension.manifestBuilderTaskConfig.licensesDir)
            task.descriptionsDir.set(extension.manifestBuilderTaskConfig.descriptionsDir)
        }

        project.tasks.register('buildDescriptionXml', DescriptionBuilderTask) { task ->
            task.projectName.set(extension.descriptionBuilderTaskConfig.projectName)
            task.outputFile.set(extension.descriptionBuilderTaskConfig.outputFile)
            task.licensesDir.set(extension.descriptionBuilderTaskConfig.licensesDir)
            task.versionGitTag.set(extension.descriptionBuilderTaskConfig.versionGitTag)
            task.loMinimalVersion.set(extension.descriptionBuilderTaskConfig.loMinimalVersion)
            task.loName.set(extension.descriptionBuilderTaskConfig.loName)
            task.componentServiceId.set(extension.descriptionBuilderTaskConfig.componentServiceId)
            task.displayName.set(extension.descriptionBuilderTaskConfig.displayName)
            task.publisher.set(extension.descriptionBuilderTaskConfig.publisher)
        }

        project.tasks.register('buildXcuRegistry', XcuRegistryBuilderTask) { task ->
            task.outputFile.set(extension.xcuRegistryBuilderTaskConfig.outputFile)
            task.inputFile.set(extension.xcuRegistryBuilderTaskConfig.inputFile)
            task.componentServiceId.set(extension.xcuRegistryBuilderTaskConfig.componentServiceId)
        }

        project.tasks.register('buildLog4J2Xml', Log4J2XmlBuilderTask) { task ->
            task.projectName.set(extension.log4J2XmlBuilderTaskConfig.projectName)
            task.outputFile.set(extension.log4J2XmlBuilderTaskConfig.outputFile)
            task.componentRootUrl.set(extension.log4J2XmlBuilderTaskConfig.componentRootUrl)
            task.logsRootDirPath.set(extension.log4J2XmlBuilderTaskConfig.logsRootDirPath)
        }
    }

}
