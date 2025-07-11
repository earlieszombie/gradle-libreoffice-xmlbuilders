package org.boldi.libreoffice.utils.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.Input
import org.gradle.util.GradleVersion

@SuppressWarnings('unused')
abstract class XmlBuildersBaseTask extends DefaultTask {
    @OutputFile
    abstract RegularFileProperty getOutputFile()
    @Input
    @Optional
    abstract Property<String> getProjectName()

    def commentedGradleVersion() {
        "<!-- Generated by Gradle Task '${this.class.superclass.simpleName}' using Gradle v${GradleVersion.current().version}} -->\n"
    }

    def writeToOutputFile(StringWriter stringWriter) {
        def targetFile = outputFile.get().asFile
        targetFile.write(Constants.XML_SIGNATURE)
        targetFile << commentedGradleVersion()
        targetFile << stringWriter.toString()
    }
}
