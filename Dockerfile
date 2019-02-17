FROM openjdk:8-alpine

COPY target/uberjar/ireadit.jar /ireadit/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/ireadit/app.jar"]
