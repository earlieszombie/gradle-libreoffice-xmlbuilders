package org.boldi.libreoffice.utils

import spock.lang.Specification
import spock.lang.TempDir
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

import java.nio.file.Path

class XmlBuildersFunctionalTests extends Specification {
    @TempDir
    Path testProjectDir
    File testProjectBuildDir

    private void createBuildFileFromResource(String resourceFileName) {
        def resourceStream = this.getClass().getClassLoader().getResourceAsStream("testProject/${resourceFileName}")
        assert resourceStream != null : "Resource file ${resourceFileName} not found"
        def buildFile = testProjectDir.resolve("build.gradle").toFile()
        buildFile.text = resourceStream.text
    }
    
    def setup() {
        // Creating the test project "build" directory
        testProjectBuildDir = new File(testProjectDir.toFile(), "build")
        testProjectBuildDir.mkdirs()
        // creating the test project settings.gradle file (to define the project name)
        def settingsFile = testProjectDir.resolve("settings.gradle").toFile()
        settingsFile.text ="""
            rootProject.name = 'test-project-name'
        """
        createBuildFileFromResource("build.gradle")

    }

    def "can run :buildManifestXml task"() {
        given:
            // matches the build.gradle descriptionsDir folder setup
            File descriptionsDir = new File(testProjectDir.toFile(), "descriptions")
            descriptionsDir.mkdirs()
            new File(descriptionsDir, "package-description_en-GB.txt").text = "LibreOffice Calc Add-in description (English)"
            new File(descriptionsDir, "package-description_fr-FR.txt").text = "Description de l'extension pour LibreOffice (Français)"
            // matches the build.gradle licensesDir folder setup
            File licensesDir = new File(testProjectDir.toFile(), "licenses")
            licensesDir.mkdirs()
            new File(licensesDir, "license_en-GB.txt").text = "GNU GENERAL PUBLIC LICENSE etc..."
            new File(licensesDir, "license_fr-FR.txt").text = "GNU LICENCE GENERALE PUBLIQUE etc..."
        when:
            def result = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(':buildManifestXml')
                .withPluginClasspath()
                .build()
        then:
            result.task(":buildManifestXml").outcome == TaskOutcome.SUCCESS
        and: "Check the manifest.xml file exists"
            def outputFilePath = "${testProjectBuildDir}/manifest.xml"
            def outputFile = new File(outputFilePath)
            assert outputFile.exists() : "The manifest.xml file was not created."
        and: "Verify the content of the file"
            def content = outputFile.text
            println("\nManifest Content:\n$content")
            assert content.contains('manifest:full-path="test-project-name.jar"') : "uno-component;type=Java entry not found in the manifest!"
            assert content.contains('manifest:full-path="test-project-name_IDL_types.jar"') : "uno-typelibrary;type=Java entry not found in the manifest!"
            assert content.contains('manifest:full-path="descriptions/package-description_fr-FR.txt"') : "application/vnd.sun.star.package-bundle-description (fr-FR) entry not found in the manifest!"
            assert content.contains('manifest:full-path="descriptions/package-description_en-GB.txt"') : "application/vnd.sun.star.package-bundle-description (en-GB) entry not found in the manifest!"
            assert content.contains('manifest:full-path="licenses/license_fr-FR.txt"') : "application/vnd.sun.star.package-bundle-license (fr-FR) entry not found in the manifest!"
            assert content.contains('manifest:full-path="licenses/license_en-GB.txt"') : "application/vnd.sun.star.package-bundle-license (en-GB) entry not found in the manifest!"
            assert content.contains('manifest:full-path="types.rdb"') : "uno-typelibrary;type=RDB entry not found in the manifest!"
            assert content.contains('manifest:full-path="CalcAddIns.xcu"') : "configuration-data entry not found in the manifest!"
    }

    def "can run :buildDescriptionXml task"() {
        given:
            // matches the build.gradle licensesDir folder setup
            File licensesDir = new File(testProjectDir.toFile(), "licenses")
            licensesDir.mkdirs()
            new File(licensesDir, "license_en-GB.txt").text = "GNU GENERAL PUBLIC LICENSE etc..."
            new File(licensesDir, "license_fr-FR.txt").text = "GNU LICENCE GENERALE PUBLIQUE etc..."
        when:
            def result = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(':buildDescriptionXml')
                .withPluginClasspath()
                .build()
        then:
            result.task(":buildDescriptionXml").outcome == TaskOutcome.SUCCESS
        and: "Check the description.xml output file exists"
            def outputFilePath = "${testProjectBuildDir}/description.xml"
            def outputFile = new File(outputFilePath)
            assert outputFile.exists() : "The description.xml file was not created."
        and: "Verify the content of the file"
            def content = outputFile.text                           // Read file content
            println("\nDescription Content:\n$content")
            assert content.contains('<version value=\'1.0.0-a087d1fcce\' />') : "version value entry not found in the description file!"
            assert content.contains('<identifier value=\'com.example.libreoffice.calc.test-project-name\' />') : "identifier value entry not found in the description file!"
            assert content.contains('<lo:LibreOffice-minimal-version value=\'24.2\' name=\'LibreOffice 24.2\' />') : "dependencies 'lo' entries not found in the description file!"
            assert content.contains('<license-text xlink:href=\'licenses/license_en-GB.txt\' lang=\'en-GB\' />') : "license text entry not found in the description file!"
            assert content.contains('<name lang=\'en\'>My LibreOffice Calc Add-in</name>') : "name entry not found in the description file!"
            assert content.contains('<name lang=\'en\'>John Doe</name>') : "publisher entry not found in the description file!"
    }
    
    def "can run :buildXcuRegistry task"() {
        given:
            // Copy CalcAddIns.yaml from testProjectDir root
            def yamlResourceStream = this.getClass().getClassLoader().getResourceAsStream("testProject/CalcAddIns.yaml")
            assert yamlResourceStream != null : "CalcAddIns.yaml not found in test resources"
            def yamlFile = new File("${testProjectDir}/CalcAddIns.yaml")
            yamlFile.withOutputStream { out ->
                out << yamlResourceStream
            }
        when:
            def result = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(':buildXcuRegistry')
                .withPluginClasspath()
                .build()
        then:
            result.task(":buildXcuRegistry").outcome == TaskOutcome.SUCCESS
        and: "Check the content of the description.xml file"
            def outputFilePath = "${testProjectBuildDir}/CalcAddIns.xcu"
            def outputFile = new File(outputFilePath)
            assert outputFile.exists() : "The CalcAddIns.xcu file was not created."
        and: "Verify the content of the file"
            def content = outputFile.text
            println("\nCalcAddIns.xcu Content:\n$content")
            assert content.contains('<node oor:name="com.example.libreoffice.calc.test-project-name" oor:op="replace">') : "componentServiceId entry not found in the registry file!"
            assert content.contains('<node oor:name="FNDISPLAYNAME" oor:op="replace">') : "calc add-in function name entry not found in the registry file!"
            assert content.contains('<value xml:lang="en">FNDISPLAYNAME</value>') : "calc add-in function display name (en) entry not found in the registry file!"
            assert content.contains('<value xml:lang="en">param display name (en)</value>') : "calc add-in function param display name (en) qentry not found in the registry file!"
            assert content.contains('<value xml:lang="en">param description (en)</value>') : "calc add-in function (en) description entry not found in the registry file!"
    }

    def "can run :buildLog4J2Xml task"() {
        given:
        when:
            def result = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withArguments(':buildLog4J2Xml')
                .withPluginClasspath()
                .build()
        then:
            result.task(":buildLog4J2Xml").outcome == TaskOutcome.SUCCESS
        and: "Check the content of the log4j2.xml file"
            def outputFilePath = "${testProjectBuildDir}/log4j2.xml"
            def outputFile = new File(outputFilePath)
            assert outputFile.exists() : "The log4j2.xml file was not created."
        and: "Verify the content of the file"
            def content = outputFile.text
            println("\nlog4j2.xml Content:\n$content")

    }

}
