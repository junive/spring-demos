# demo1

- Import the folder into Intellij or Eclipse
- Change the configuration on src/main/resources/application.properties
- And run the app !

On ubuntu Server :
- Create src/main/resources/application-prod.properties
- Run `nohup java -Dserver.port=8080 -Dspring.profiles.active=prod -jar demo1.jar &`
  ( nohup and & symbol will run console and java in background )