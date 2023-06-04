FROM amazoncorretto:19-alpine3.15-jdk as build
COPY . .
RUN ./gradlew build --exclude-task test

FROM amazoncorretto:19-alpine3.15-jdk
ARG JAR_FILE=snacksack*.jar
COPY --from=build build/libs/${JAR_FILE} app.jar
ARG CUSTOM_OPTIONS=""
ENV ENV_CUSTOM_OPTIONS=${CUSTOM_OPTIONS}
#ENTRYPOINT ["java","-jar","/app.jar"]
#CMD "java ${ENV_CUSTOM_OPTIONS} -jar ./app.jar"
CMD java ${ENV_CUSTOM_OPTIONS} -jar /app.jar
