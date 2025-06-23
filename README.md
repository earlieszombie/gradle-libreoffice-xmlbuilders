## LibreOffice XmlBuilders Gradle Plugin

- integration with LibreOffice Java Add-in (once published locally):
```groovy
buildscript {
  repositories {
    mavenLocal()
    mavenCentral()
  }
  dependencies {
    classpath "org.boldi.libreoffice.utils:libreoffice-xmlbuilders:1.0.0"
  }
}

apply plugin: 'org.boldi.libreoffice.utils.xmlbuilders'

xmlBuildersExt {
    greeting = "Hi from my gradle extension!"
}

tasks.named('hello') {
    doLast {
        println "This runs after the hello task..."
    }
}
```
