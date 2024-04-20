FROM openjdk:17

ADD target/TaskAssignmentApp.jar TaskAssignmentApp.jar

EXPOSE 8080

CMD ["java", "-jar", "TaskAssignmentApp.jar"]