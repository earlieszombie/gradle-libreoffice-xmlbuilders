package org.boldi.libreoffice.utils.tasks

import groovy.xml.MarkupBuilder
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputDirectory

@SuppressWarnings('unused')
abstract class DescriptionBuilderTask extends XmlBuildersBaseTask {

    @InputDirectory
    abstract DirectoryProperty getLicensesDir()
    @Input
    abstract Property<String> getVersionGitTag()
    @Input
    abstract Property<String> getComponentServiceId()
    @Input
    abstract Property<String> getLoMinimalVersion()
    @Input
    abstract Property<String> getLoName()
    @Input
    abstract Property<String> getDisplayName()
    @Input
    abstract Property<String> getPublisher()

    @TaskAction
    def generate() {

        def sw = new StringWriter()
        def xml = new MarkupBuilder(sw)

        String versionGitTagValue = versionGitTag.getOrNull()
        if (versionGitTagValue== null || versionGitTagValue.trim().isEmpty()) {
            throw new GradleException("""
                versionGitTag is not set for the DescriptionBuilderTask.
                Please set it using:
                    descriptionBuilder {
                        versionGitTag.set("1.0.0-deadbeef")
                    }
            """.stripIndent())
        }

        String componentServiceIdValue = componentServiceId.getOrNull()
        if (componentServiceIdValue== null || componentServiceIdValue.trim().isEmpty()) {
            throw new GradleException("""
                componentServiceId is not set for the DescriptionBuilderTask.
                Please set it using:
                    descriptionBuilder {
                        componentServiceId.set("com.example.libreoffice.calc.my-component")
                    }
            """.stripIndent())
        }

        Closure<MarkupBuilder> describeTo = { MarkupBuilder builder ->
            builder.version(value: versionGitTagValue)
            builder.identifier(value: componentServiceIdValue)
            builder.dependencies('xmlns:lo': 'http://libreoffice.org/extensions/description/2011') {
                'lo:LibreOffice-minimal-version'(value: loMinimalVersion.get(), name: loName.get())
            }
            builder.registration() {
                'simple-license'('accept-by': 'admin', 'suppress-on-update': true) {
                    if (licensesDir.present) {
                        def licenseFiles = licensesDir.get().asFile
                        licenseFiles.listFiles().each { file ->
                            'license-text'('xlink:href': "licenses/${file.name}", lang: file.name.replaceFirst(~/\.[^\.]+$/, '').takeRight(5))
                        }
                    }
                }
            }
            if (displayName) {
                builder.'display-name'() {
                    name(lang: 'en', (String) displayName.get())
                }
            }
            if (publisher) {
                builder.publisher() {
                    name(lang: 'en', (String) publisher.get())
                }
            }
            return builder
        }

        xml.description('xmlns': 'http://openoffice.org/extensions/description/2006',
                'xmlns:xlink': 'http://www.w3.org/1999/xlink') {
            describeTo(xml)
        }

        writeToOutputFile(sw)

    }

}
