@echo off
set "MAVEN_PROJECTBASEDIR=%~dp0"
java -cp "%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
