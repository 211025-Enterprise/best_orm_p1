#Tech Stack
- [ ] Java 8
- [ ] Junit
- [ ] Mockito
- [ ] Apache maven
- [ ] Jackson library(for JSON marshalling/unmarshalling)
- [ ] Java EE servlet API
- [ ] PostGreSQL deployed on AWS RDS
- [ ] Git SCM (on github)

#Functional requirements
- [ ] CRUD operations are supported for one or more domain objects via the web application's exposed endpoints
- [ ] JDBC logic is abstracted away by the custom ORM
- [ ] Programmatic persistence of entities (basic CRUD support) using custom ORM
- [ ] File-based or programmatic configuration of entities

#Non-Functional requirements
- [ ] 80% line coverage of all service layer classes
- [ ] Generated Jacoco reports that display coverage metrics
- [ ] Usage of the java.util.Stream API within your project
- [ ] Custom ORM source code should be included within the web application as a Maven dependency

#Bonus Features
-[ ] Custom ORM supports basic transaction management (begin, commit, savepoint, rollback)
-[ ] Custom ORM supports connection pooling
-[ ] Session-based caching to minimize calls to the database for already retrieved data
-[ ] Deployment of web application to AWS EC2 (use of AWS Elastic Beanstalk is permitted)