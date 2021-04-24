

To build this project use

    mvn clean install

To run this project

    mvn camel:run


    - install dependencies in karaf :
	
 	features:install pax-jms-artemis
	features:install pax-jms-pool-pooledjms
        features:install camel-jms
  
    - install the bundle

       install -s mvn:com.mycompany/karafArtemis/1.0.0-SNAPSHOT
  
