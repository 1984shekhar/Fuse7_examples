# Run as plain springboot jar
mvn clean package -DskipTests
java -jar target/camel-amqp-springboot-1.0-SNAPSHOT.jar
