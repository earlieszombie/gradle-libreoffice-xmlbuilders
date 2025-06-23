package org.boldi.libreoffice.utils

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class XmlBuildersPluginTest extends Specification {

    def "plugin registers tasks"() {
        given:
            def project = ProjectBuilder.builder().build()

        when:
            project.plugins.apply("org.boldi.libreoffice.utils.xmlbuilders")
            project.evaluate()

        then:
            project.tasks.named("buildManifestXml") != null
            project.tasks.named("buildDescriptionXml") != null
            project.tasks.named("buildXcuRegistry") != null
            project.tasks.named("buildLog4J2Xml") != null
    }
}
