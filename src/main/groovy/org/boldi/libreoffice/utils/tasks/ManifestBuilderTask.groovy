package org.boldi.libreoffice.utils.tasks

import groovy.xml.MarkupBuilder
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

abstract class ManifestBuilderTask extends XmlBuildersBaseTask {
    @InputDirectory
    abstract DirectoryProperty getLicensesDir()
    @InputDirectory
    abstract DirectoryProperty getDescriptionsDir()

    static final UNO_TYPE_LIBRARY_RDB = 'application/vnd.sun.star.uno-typelibrary;type=RDB'
    static final UNO_TYPE_LIBRARY_JAVA = 'application/vnd.sun.star.uno-typelibrary;type=Java'
    static final UNO_COMPONENT_JAVA = 'application/vnd.sun.star.uno-component;type=Java'
    static final UNO_PACKAGE_BUNDLE_DESCRIPTION = 'application/vnd.sun.star.package-bundle-description'
    static final UNO_PACKAGE_BUNDLE_LICENSE = 'application/vnd.sun.star.package-bundle-license'
    static final UNO_CONFIGURATION_DATA = 'application/vnd.sun.star.configuration-data'

    @TaskAction
    def generate() {

        def sw = new StringWriter()
        def xml = new MarkupBuilder(sw)
        xml.setDoubleQuotes(true)

        String projectNameValue = projectName.getOrNull()
        if (projectNameValue == null || projectNameValue.trim().isEmpty()) {
            throw new GradleException("""
                Project name is not set for ManifestBuilderTask.
                Please set it using:
                    buildManifestXml {
                        projectName.set(rootProject.name)
                    }
            """.stripIndent())
        }

        Closure<MarkupBuilder> xmlFileEntriesTo = { MarkupBuilder mub ->
            mub.setDoubleQuotes(true)
            mub.'manifest:file-entry'('manifest:media-type': UNO_COMPONENT_JAVA, 'manifest:full-path': "${projectName.get()}.jar")
            mub.'manifest:file-entry'('manifest:media-type': UNO_TYPE_LIBRARY_JAVA, 'manifest:full-path': "${projectName.get()}_IDL_types.jar")
            mub.'manifest:file-entry'('manifest:media-type': UNO_TYPE_LIBRARY_RDB, 'manifest:full-path': 'types.rdb')

            if (descriptionsDir.present) {
                def descriptionFiles = descriptionsDir.get().asFile
                descriptionFiles.listFiles().each { file ->
                    mub.'manifest:file-entry'('manifest:media-type': UNO_PACKAGE_BUNDLE_DESCRIPTION, 'manifest:full-path': "${descriptionFiles.name}/${file.name}")
                }
            }
            if (licensesDir.present) {
                def licenseFiles = licensesDir.get().asFile
                licenseFiles.listFiles().each { file ->
                    mub.'manifest:file-entry'('manifest:media-type': UNO_PACKAGE_BUNDLE_LICENSE, 'manifest:full-path': "${licenseFiles.name}/${file.name}")
                }
            }
            mub.'manifest:file-entry'('manifest:media-type': UNO_CONFIGURATION_DATA, 'manifest:full-path': 'CalcAddIns.xcu')
            return mub
        }
        xml.'manifest:manifest'('xmlns:manifest': 'http://openoffice.org/2001/manifest') {
            xmlFileEntriesTo(xml)
        }

        writeToOutputFile(sw)

    }
}
