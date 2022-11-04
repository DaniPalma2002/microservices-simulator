# Business Logic Consistency Models

## Technology Requirements

- [Maven 3.6.3](https://archive.apache.org/dist/maven/maven-3/3.6.3/) 

- [Java 11+](https://www.oracle.com/java/technologies/downloads/#java11)

- [PSQL 12](https://www.postgresql.org/download/) 

- [JMeter 5.5](https://jmeter.apache.org/download_jmeter.cgi)


## Setting up the database
* Start db
```
sudo service postgresql start
sudo su -l postgres
dropdb blcmdb
createdb blcmdb
```
* Create user to access db
```
psql blcmdb
CREATE USER your-username WITH SUPERUSER LOGIN PASSWORD 'yourpassword';
\q
exit
```
* Rename `backend/src/main/resources/application-dev.properties.example` to `application-dev.properties` and fill the placeholder fields.


## Running the application

```
cd backend
mvn clean spring-boot:run
```

## Running JMeter tests

```
cd backend/jmeter/tournament/thesis-cases/
jmeter -n -t TEST.jmx
```

* **TEST** is replaced with actual name of a test
* The cases described in section VII of the paper are, presented in the same:
  * [5a-updateStudentName-addParticipant-processUpdateNameEvent.jmx](backend/jmeter/tournament/thesis-cases/5a-updateStudentName-addParticipant-processUpdateNameEvent.jmx)
  * [5b-addParticipant-updateStudentName-processUpdateNameEvent.jmx](backend/jmeter/tournament/thesis-cases/5b-addParticipant-updateStudentName-processUpdateNameEvent.jmx)
  * [5c-updateStudentName1-addParticipant-updateStudentName2-processUpdateNameEvent.jmx](backend/jmeter/tournament/thesis-cases/5c-updateStudentName1-addParticipant-updateStudentName2-processUpdateNameEvent.jmx)
  * [5d-addParticipant1-updateStudentName-processUpdateNameEvent1-addParticipant2-processUpdateNameEvent2.jmx](backend/jmeter/tournament/thesis-cases/5d-addParticipant1-updateStudentName-processUpdateNameEvent1-addParticipant2-processUpdateNameEvent2.jmx)
  * [8-5-update-tournament-concurrent-intention-pass.jmx](backend/jmeter/tournament/thesis-cases/8-5-update-tournament-concurrent-intention-pass.jmx)

* The folder contains additional test cases not described in the paper.
## Viewing JMeter tests strucure

```
cd backend/jmeter/tournament/thesis-cases/
jmeter
```
* The command launches JMeter GUI. By clicking `File > Open` and selecting a test file it is possible to observe the test structure.
* Tests can also be run using the GUI, by clicking on the `Start` button.