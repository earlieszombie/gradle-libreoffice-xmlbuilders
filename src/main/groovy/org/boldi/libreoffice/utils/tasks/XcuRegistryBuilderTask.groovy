package org.boldi.libreoffice.utils.tasks

import groovy.xml.MarkupBuilder
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.Yaml

abstract class XcuRegistryBuilderTask extends XmlBuildersBaseTask {

    @Input
    abstract Property<String> getComponentServiceId()
    @InputFile
    abstract RegularFileProperty getInputFile()

    @TaskAction
    def generate() {

        def xcu = new Yaml().load(inputFile.get().asFile.text)
        def sw = new StringWriter()
        def xml = new MarkupBuilder(sw)

        xml.setDoubleQuotes(true)

        Closure<String> underScoredToCamelCase = { String underscored ->
            return underscored.capitalize().replaceAll(/_\w/) {
                (String) it[1].toUpperCase()
            }
        }

        Closure<MarkupBuilder> fnNodeMarkup = { MarkupBuilder mb, LinkedHashMap fn ->
            mb.setDoubleQuotes(true)
            mb.node("oor:name": fn.display_name.en, "oor:op": "replace") {
                mb.prop("oor:name": "Category") {
                    mb.value("Add-In")
                }
                mb.prop("oor:name": "DisplayName") {
                    fn.display_name.each { k, v ->
                        mb.value("xml:lang": k, v)
                    }
                }
                mb.prop("oor:name": "Description") {
                    fn.description.each { k, v ->
                        mb.value("xml:lang": k, v)
                    }
                }
                mb.prop("oor:name": "CompatibilityName") {
                    mb.value("xml:lang": "en", fn.compatibility_name.en)
                }
                fn.parameters.each { param ->
                    mb.node("oor:name": "Parameters") {
                        mb.node("oor:name": underScoredToCamelCase(param.name), "oor:op": "replace") {
                            param.properties.each { k, v ->
                                mb.prop("oor:name": underScoredToCamelCase(k.toString())) {
                                    mb.value("xml:lang": "en", v.en)
                                }
                            }
                        }
                    }
                }
            }
            return mb
        }

        xml."oor:component-data"("xmlns:oor": "http://openoffice.org/2001/registry",
                "xmlns:xs": "http://www.w3.org/2001/XMLSchema",
                "oor:name": "CalcAddIns",
                "oor:package": "org.openoffice.Office") {
            node("oor:name": "AddInInfo") {
                node("oor:name": componentServiceId.get(), "oor:op": "replace") {
                    node("oor:name": "AddInFunctions") {
                        xcu.add_in_functions.each { fn ->
                            fnNodeMarkup(xml, fn)
                        }
                    }
                }
            }

        }

        writeToOutputFile(sw)

    }

}
