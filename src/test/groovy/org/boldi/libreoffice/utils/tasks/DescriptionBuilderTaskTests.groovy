package org.boldi.libreoffice.utils.tasks

import groovy.xml.XmlSlurper
import org.boldi.libreoffice.utils.XmlBuildersExtension
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.TempDir

class DescriptionBuilderTaskTests extends Specification {
    @TempDir
    File tempDir
    Project project
    DescriptionBuilderTask task

    private Project createTestProject() {
        def project = ProjectBuilder.builder()
                .withProjectDir(tempDir)
                .build()
        project.extensions.create('xmlBuildersExt', XmlBuildersExtension)
        return project
    }

    def setup() {

        project = createTestProject()
        def taskProvider = project.tasks.register('buildDescriptionXml', DescriptionBuilderTask)
        task = taskProvider.get()

        def extension = project.extensions.getByType(XmlBuildersExtension)
        extension.buildDescriptionXml { config ->
            config.outputFile.set(project.file("${tempDir}/description.xml"))
        }

        task.outputFile.set(extension.descriptionBuilderTaskConfig.outputFile)
    }

    def cleanup() {
        tempDir.deleteDir()
    }

    def "should create a description.xml file"() {
        given:
            task.projectName.set("test-project-name")
            task.versionGitTag.set("1.0.0-a087d1fcce")
            task.componentServiceId.set("com.example.libreoffice.calc.addin.test-project-name")
            task.loMinimalVersion.set("24.2")
            task.loName.set("LibreOffice 25.2")
            task.displayName.set("test-project-name calc add-in")
            task.publisher.set("John Doe")
        when:
            task.generate()
        then: "the description file is created"
            def outputFile = new File(tempDir, "description.xml")
            assert outputFile.exists()
        and: "it references the correct version"
            def content = outputFile.text
            assert content.contains("<version value='1.0.0-a087d1fcce' />") : "version value entry not found in the add-in description!"

    }

    def "should not throw exception when the project name is empty or missing"() {
        given:
            // task.projectName.set("")
            task.versionGitTag.set("1.0.0-a087d1fcce")
            task.componentServiceId.set("com.example.libreoffice.calc.addin.test-project-name")
            task.loMinimalVersion.set("24.2")
            task.loName.set("LibreOffice 25.2")
            task.displayName.set("test-project-name calc add-in")
            task.publisher.set("John Doe")
        when:
            task.generate()
        then:
            noExceptionThrown()
    }

    def "should throw an exception when the version is empty or missing"() {
        given:
            task.versionGitTag.set("")
            task.componentServiceId.set("com.example.libreoffice.calc.addin.test-project-name")
            task.loMinimalVersion.set("24.2")
            task.loName.set("LibreOffice 25.2")
            task.displayName.set("test-project-name calc add-in")
            task.publisher.set("John Doe")
        when:
            task.generate()
        then:
            thrown(Exception)
    }

    def "should throw an exception when the component service id is empty or missing"() {
        given:
            task.versionGitTag.set("1.0.0-deadbeef")
            task.componentServiceId.set("")
            task.loMinimalVersion.set("24.2")
            task.loName.set("LibreOffice 25.2")
            task.displayName.set("test-project-name calc add-in")
            task.publisher.set("John Doe")
        when:
            task.generate()
        then:
            thrown(Exception)
    }

}
