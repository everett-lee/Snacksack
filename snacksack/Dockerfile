FROM amazoncorretto:19-alpine3.15-jdk
ARG JAR_FILE=snacksack*.jar
ARG JAVA_OPTIONS=""
COPY build/libs/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar","${JAVA_OPTIONS}"]

