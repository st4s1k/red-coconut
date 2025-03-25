FROM openjdk:21-jdk
COPY target/red-coconut-0.0.1-SNAPSHOT.jar red-coconut.jar
ENTRYPOINT ["java","-jar","/red-coconut.jar"]
