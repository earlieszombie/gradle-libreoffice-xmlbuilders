package org.boldi.libreoffice.utils.tasks

import org.boldi.libreoffice.utils.XmlBuildersExtension
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import org.gradle.api.Project
import groovy.xml.XmlSlurper
import spock.lang.TempDir

class XcuRegistryBuilderTaskTests extends Specification {
    @TempDir
    File tempDir
    Project project
    XcuRegistryBuilderTask task

    private Project createTestProject() {
        def project = ProjectBuilder.builder()
                .withProjectDir(tempDir)
                .build()
        def extension = project.objects.newInstance(XmlBuildersExtension)
        project.extensions.add('xmlBuildersExt', extension)
        project.objects
        return project
    }

    def findXmlNode(path, name) {
        path."**".find { it.@"oor:name" == name }
    }
    def findXmlNodeProperty(path, nodeName, propName) {
        def node = path."**".find { it.@"oor:name" == nodeName }
        node.prop.find { it.@"oor:name" == propName }
    }

    def setup() {

        project = createTestProject()
        def taskProvider = project.tasks.register('buildXcuRegistry', XcuRegistryBuilderTask)
        task = taskProvider.get()

        def extension = project.extensions.getByType(XmlBuildersExtension)
        extension.buildXcuRegistry { config ->
            config.inputFile.set(project.file("${tempDir}/CalcAddIns.yaml"))
            config.outputFile.set(project.file("${tempDir}/CalcAddIns.xcu"))
            config.componentServiceId.set("com.example.libreoffice.component.test")
        }
        task.outputFile.set(extension.xcuRegistryBuilderTaskConfig.outputFile)
        task.inputFile.set(extension.xcuRegistryBuilderTaskConfig.inputFile)
        task.componentServiceId.set(extension.xcuRegistryBuilderTaskConfig.componentServiceId)
    }

    def cleanup() {
        tempDir.deleteDir()
    }

    def "should generate valid XCU file from YAML input"() {
        given: "A valid YAML input"
            def resourceStream = getClass().getResourceAsStream('/SimpleFunction.yaml')
            def inputFile = new File(tempDir, "CalcAddIns.yaml")
            inputFile.text = resourceStream.text
        when:
            task.generate()
        then:
            def outputFile = new File(tempDir, "CalcAddIns.xcu")
            assert outputFile.exists()
            def xml = new XmlSlurper().parse(outputFile)
        and: "verify basic XML structure"
            xml.name() == "component-data"
            xml.@"oor:name" == "CalcAddIns"
            xml.@"oor:package" == "org.openoffice.Office"
        and: "verify function details"
            def function = xml."**".find { it.@"oor:name" == "SimpleFunction" }
            function != null
        and: "verify category"
            def category = function."prop".find { it.@"oor:name" == "Category" }
            category.value.text() == "Add-In"
        and: "verify display name"
            def displayName = function."prop".find { it.@"oor:name" == "DisplayName" }
            displayName."value".find { it.@"xml:lang" == "en" }.text() == "SimpleFunction"
            displayName."value".find { it.@"xml:lang" == "fr" }.text() == "FonctionSimple"
        and: "verify description"
            def description = function."prop".find { it.@"oor:name" == "Description" }
            description."value".find { it.@"xml:lang" == "en" }.text() == "Description (en) of the SimpleFunction"
            description."value".find { it.@"xml:lang" == "fr" }.text() == "Description (fr) de la FonctionSimple"
        and: "verify compatibility name"
            def compatibilityName = function."prop".find { it.@"oor:name" == "CompatibilityName" }
            compatibilityName."value".find { it.@"xml:lang" == "en" }.text() == "SIMPLE_FUNCTION"
        and: "verify parameters"
            def parameter = function."node".find { it.@"oor:name" == "Parameters" }
            parameter."node".find { it.@"oor:name" == "param1" } != null
    }

    def "should throw exception when input file is missing"() {
        given:
            task.inputFile // inputFile is now "unset"
        when:
            task.generate()
        then:
            thrown(Exception)
    }

    def "should throw exception when input file is empty"() {
        given:
            def inputFile = new File(tempDir, "CalcAddIns.yaml")
            inputFile.text = ""
        when:
            task.generate()
        then:
            thrown(Exception)
    }

    def "should handle YAML with multiple functions"() {
        given:
            def resourceStream = getClass().getResourceAsStream('/MultipleFunctions.yaml')
            def inputFile = new File(tempDir, "CalcAddIns.yaml")
            inputFile.text = resourceStream.text
        when:
            task.generate()
        then:
            def outputFile = new File(tempDir, "CalcAddIns.xcu")
            assert outputFile.exists()
            def xml = new XmlSlurper().parse(outputFile)
            def functions = xml."**".findAll { it.@"oor:name" in ["Function1", "Function2"] }
            functions.size() == 2
    }

    def "should correctly handle underscored parameters name"() {
        given: "a yaml file with underscored parameters name"
            def resourceStream = getClass().getResourceAsStream('/CamelCaseTestParam.yaml')
            def inputFile = new File(tempDir, "CalcAddIns.yaml")
            inputFile.text = resourceStream.text
        when: "Task is executed"
            task.generate()
            def outputFile = new File(tempDir, "CalcAddIns.xcu")
            outputFile.exists()
            def xml = new XmlSlurper().parse(outputFile)
        then: "We can find our xml parameter node in camel case"
            def testParamNode = findXmlNode(xml, 'TestParam')
            assert testParamNode != null
        and: "Retrieve their properties using the camel case parameter name"
            def displayName = findXmlNodeProperty(xml, "TestParam", "DisplayName")
            assert displayName."value".find { it.@"xml:lang" == "en" }.text() == "test parameter display name"
    }

}