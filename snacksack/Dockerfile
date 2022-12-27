FROM amazoncorretto:19-alpine3.15-jdk
ARG JAR_FILE=snacksack*.jar
ARG CUSTOM_OPTIONS=""
ENV ENV_CUSTOM_OPTIONS=${CUSTOM_OPTIONS}
COPY build/libs/${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
#CMD "java ${ENV_CUSTOM_OPTIONS} -jar ./app.jar"
CMD java ${ENV_CUSTOM_OPTIONS} -jar /app.jar
