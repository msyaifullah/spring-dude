############################
# STEP 1 build executable packages
############################
FROM maven:3.9-eclipse-temurin-17 AS builder
COPY earth/src /home/app/earth/src
COPY earth/pom.xml /home/app/earth/pom.xml
COPY venus/src /home/app/venus/src
COPY venus/pom.xml /home/app/venus/pom.xml
COPY pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean package

#############################
## STEP 2 build a small image
#############################
FROM eclipse-temurin:17-jre
COPY --from=builder /home/app/venus/target/*.jar spring-boot-application.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","spring-boot-application.jar"]