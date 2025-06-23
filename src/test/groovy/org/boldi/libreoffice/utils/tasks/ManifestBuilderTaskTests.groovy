package org.boldi.libreoffice.utils.tasks

import groovy.xml.XmlSlurper
import org.boldi.libreoffice.utils.XmlBuildersExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.TempDir

class ManifestBuilderTaskTests extends Specification {
    @TempDir
    File tempDir
    Project project
    ManifestBuilderTask task

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
        def taskProvider = project.tasks.register('buildManifestXml', ManifestBuilderTask)
        task = taskProvider.get()

        def extension = project.extensions.getByType(XmlBuildersExtension)
        extension.buildManifestXml { config ->
            config.outputFile.set(project.file("${tempDir}/manifest.xml"))
        }

        task.outputFile.set(extension.manifestBuilderTaskConfig.outputFile)
    }

    def cleanup() {
        tempDir.deleteDir()
    }

    def "should reference the correct jar file when project name is set"() {
        given:
            task.projectName.set("test-project-name")
        when:
            task.generate()
        then: "the manifest file is created"
            def outputFile = new File(tempDir, "manifest.xml")
            assert outputFile.exists()
            def xml = new XmlSlurper().parse(outputFile)
        and: "it references the correct jar file"
            def content = outputFile.text
            // println("Manifest Content:\n$content")
            assert content.contains('manifest:full-path="test-project-name.jar"') : "uno-component;type=Java entry not found in the manifest!"

    }

    def "should throw exception when the project name is empty or missing"() {
        given:
            task.projectName.set("")
        when:
            task.generate()
        then:
            thrown(Exception)
    }

    def "should not throw exception when the descriptionDir is missing"() {
        given:
            task.projectName.set("test-project-name")
            task.descriptionsDir // not set
        when:
            task.generate()
        then:
            noExceptionThrown()
    }

    def "should not throw exception when the descriptionDir has no files present"() {
        given:
            def emptyDescriptionsDir = new File(tempDir, "descriptions")
            emptyDescriptionsDir.mkdirs()
            task.projectName.set("set-project-name")
            task.descriptionsDir.set(project.file(emptyDescriptionsDir))
        when:
            task.generate()
        then:
            noExceptionThrown()
    }

    def "should not throw exception when the licensesDir is missing"() {
        given:
            task.projectName.set("test-project-name")
            task.licensesDir // not set
        when:
            task.generate()
        then:
            noExceptionThrown()
    }

    def "should not throw exception when the licensesDir has no files present"() {
        given:
            def emptyLicensesDir = new File(tempDir, "licenses")
            emptyLicensesDir.mkdirs()
            task.projectName.set("set-project-name")
            task.descriptionsDir.set(project.file(emptyLicensesDir))
        when:
            task.generate()
        then:
            noExceptionThrown()
    }
}
