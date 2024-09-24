FROM openjdk:11

EXPOSE 8080

ADD target/kor-0.0.1-SNAPSHOT.jar kor-0.0.1-SNAPSHOT.jar

ENTRYPOINT [ "java","-jar","kor-0.0.1-SNAPSHOT.jar"]
