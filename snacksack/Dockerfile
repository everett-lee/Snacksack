FROM amazoncorretto:19-alpine3.15-jdk
ARG JAR_FILE=snacksack*.jar
COPY build/libs/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

