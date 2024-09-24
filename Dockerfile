FROM openjdk:11
 
EXPOSE 8080
 
ADD target/s3-policies-0.0.1-SNAPSHOT.jar s3-policies-0.0.1-SNAPSHOT.jar
 
ENTRYPOINT [ "java","-jar","s3-policies-0.0.1-SNAPSHOT.jar"]