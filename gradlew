#!/bin/sh
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JAVA_EXE="${JAVA_HOME:+$JAVA_HOME/bin/}java"
if [ -z "$JAVA_HOME" ]; then JAVA_EXE=java; fi
exec "$JAVA_EXE" -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
