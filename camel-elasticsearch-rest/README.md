POST http://localhost:9200/school/posts/103

Body: RAW TYPE: JSON
{
  "Name": "Dev Thakur",
  "Class":"KG",
  "Section":"P3"
}

GET http://localhost:9200/school/posts/_search

DEBUG:
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=y -jar camel-ose-springboot-xml-1.0.0-SNAPSHOT.jar



