FROM bellsoft/liberica-openjdk-debian:21

ARG JAR_FILE=build/libs/*SNAPSHOT.jar

COPY ${JAR_FILE} project.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=prod", "-jar", "project.jar"]