
package org.boldi.libreoffice.utils.tasks

import groovy.xml.MarkupBuilder
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@SuppressWarnings('unused')
abstract class Log4J2XmlBuilderTask extends XmlBuildersBaseTask {
    @Input
    abstract Property<String> getComponentRootUrl()
    @Input
    abstract Property<String> getLogsRootDirPath()

    @TaskAction
    def generate() {

        def sw = new StringWriter()
        def xml = new MarkupBuilder(sw)
        xml.setDoubleQuotes(true)

        Closure<MarkupBuilder> writeTo = { MarkupBuilder builder ->
            builder.Properties() {
                builder.Property(name: 'LOG_PATTERN', "%d{yyyy-MM-dd'T'HH:mm:ss.SSSZ} [%t] %c{1.} %p - %m%n")
                builder.Property(name: 'APP_LOG_ROOT', "${logsRootDirPath.get()}/${componentRootUrl.get()}")
                builder.Property(name: 'APP_NAME', projectName.get())
            }
            builder.Appenders() {
                builder.RollingFile(
                        name: 'debugLog'
                        , filename: '${APP_LOG_ROOT}/${APP_NAME}-debug.log'
                        , filepattern: '${APP_LOG_ROOT}/${APP_NAME}-debug-%d{yyyy-MM-dd}-%i.log'
                ) {
                    builder.LevelRangeFilter(minLevel: 'DEBUG', maxLevel: 'DEBUG', onMatch: 'ACCEPT', onMismatch: 'DENY')
                    builder.PatternLayout(pattern: '${LOG_PATTERN}')
                    builder.Policies() {
                        builder.SizeBasedTriggeringPolicy(size: "19500KB")
                    }
                    builder.DefaultRolloverStrategy(max: "10")
                }
                builder.RollingFile(
                        name: 'errorLog'
                        , fileName: '${APP_LOG_ROOT}/${APP_NAME}-error.log'
                        , filePattern: '${APP_LOG_ROOT}/${APP_NAME}-error-%d{yyyy-MM-dd}-%i.log'
                ) {
                    builder.LevelRangeFilter(minLevel: 'ERROR', maxLevel: 'ERROR', onMatch: 'ACCEPT', onMismatch: 'DENY')
                    builder.PatternLayout(pattern: '${LOG_PATTERN}')
                    builder.Policies() {
                        builder.SizeBasedTriggeringPolicy(size: "19500KB")
                    }
                    builder.DefaultRolloverStrategy(max: "10")
                }
                builder.RollingFile(
                        name: 'infoLog'
                        , fileName: '${APP_LOG_ROOT}/${APP_NAME}-info.log'
                        , filePattern: '${APP_LOG_ROOT}/${APP_NAME}-info-%d{yyyy-MM-dd}-%i.log'
                ) {
                    builder.LevelRangeFilter(minLevel: 'INFO', maxLevel: 'INFO', onMatch: 'ACCEPT', onMismatch: 'DENY')
                    builder.PatternLayout(pattern: '${LOG_PATTERN}')
                    builder.Policies() {
                        builder.SizeBasedTriggeringPolicy(size: "19500KB")
                    }
                    builder.DefaultRolloverStrategy(max: "10")
                }
                builder.RollingFile(
                        name: 'thirdPartyLog'
                        , fileName: '${APP_LOG_ROOT}/${APP_NAME}-thirdparty.log'
                        , filePattern: '${APP_LOG_ROOT}/${APP_NAME}-thirdparty-%d{yyyy-MM-dd}-%i.log'
                ) {
                    builder.LevelRangeFilter(minLevel: 'INFO', maxLevel: 'INFO', onMatch: 'ACCEPT', onMismatch: 'DENY')
                    builder.PatternLayout(pattern: '${LOG_PATTERN}')
                    builder.Policies() {
                        builder.SizeBasedTriggeringPolicy(size: "19500KB")
                    }
                    builder.DefaultRolloverStrategy(max: "10")
                }
            }
            builder.Loggers() {
                builder.Logger(name: '${APP_NAME}', level: 'debug', additivity: 'false') {
                    builder.AppenderRef(ref: 'infoLog')
                    builder.AppenderRef(ref: 'debugLog')
                    builder.AppenderRef(ref: 'errorLog')
                }
                builder.Root(level: 'INFO') {
                    builder.AppenderRef(ref: 'thirdPartyLog')
                }
            }
            return builder
        }

        xml.Configuration('status': 'WARN', 'monitorInterval': '30') {
            writeTo(xml)
        }

        writeToOutputFile(sw)

    }

}
