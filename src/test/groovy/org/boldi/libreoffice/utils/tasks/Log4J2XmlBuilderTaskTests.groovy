package org.boldi.libreoffice.utils.tasks

import groovy.xml.XmlSlurper
import org.boldi.libreoffice.utils.XmlBuildersExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.TempDir

class Log4J2XmlBuilderTaskTests extends Specification {
    @TempDir
    File tempDir
    Project project
    Log4J2XmlBuilderTask task

    private Project createTestProject() {
        def project = ProjectBuilder.builder()
                .withProjectDir(tempDir)
                .build()
        def extension = project.objects.newInstance(XmlBuildersExtension)
        project.extensions.add('xmlBuildersExt', extension)
        project.objects
        return project
    }

    def setup() {

        project = createTestProject()
        def taskProvider = project.tasks.register('buildLog4J2Xml', Log4J2XmlBuilderTask)
        task = taskProvider.get()

        def extension = project.extensions.getByType(XmlBuildersExtension)
        extension.buildLog4J2Xml { config ->
            config.outputFile.set(project.file("${tempDir}/log4j2.xml"))
            config.logsRootDirPath.set("${System.getenv("HOME")}.var/logs")
            config.componentRootUrl.set("com.example.libreoffice.calc")
        }

        task.outputFile.set(extension.log4J2XmlBuilderTaskConfig.outputFile)
        task.logsRootDirPath.set(extension.log4J2XmlBuilderTaskConfig.logsRootDirPath)
        task.componentRootUrl.set(extension.log4J2XmlBuilderTaskConfig.componentRootUrl)
    }

    def cleanup() {
        tempDir.deleteDir()
    }

    def "should reference the project name as APP_NAME"() {
        given:
            task.projectName.set("test-project-name")
        when:
            task.generate()
        then: "the log4j2.xml file is created"
            def outputFile = new File(tempDir, "log4j2.xml")
            assert outputFile.exists()
            def xml = new XmlSlurper().parse(outputFile)
        and: "it references the correct APP_NAME"
            def content = outputFile.text
            println("\nlog4j2.xml Content:\n$content")
            def appName = xml.Properties.Property.find { prop -> prop.@name == 'APP_NAME' }.text()
            assert appName == task.projectName.get()
    }

}