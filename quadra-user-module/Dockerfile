FROM azul/zulu-openjdk:21.0.10

WORKDIR /app

EXPOSE 18081

COPY target/quadra-user-inrastructure/quadra-user-inrastructure-1.0.0-SNAPSHOT.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
