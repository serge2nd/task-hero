## Task Hero

A simple task management app corresponding to a job vacancy test assignment.

### Requirements
- Docker recent version (with Compose V2)
- JDK >=17

### Run
Clone the project, go to the project dir and execute:
```shell
$ docker compose up -d
$ ./gradlew clean bootJar
$ java -Duser.language=en -Duser.timezone=UTC -Dspring.profiles.active=dev -jar build/libs/task-hero.jar
```
Then go to `http://localhost:8080//swagger-ui.html`
