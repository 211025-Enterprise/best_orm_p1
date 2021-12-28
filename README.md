#Tech Stack
- [x] Java 8
- [x] Junit
- [x] Mockito
- [x] Apache maven
- [x] Jackson library(for JSON marshalling/unmarshalling)
- [x] Java EE servlet API
- [x] PostGreSQL deployed on AWS RDS
- [x] Git SCM (on github)

#Functional requirements
- [x] CRUD operations are supported for one or more domain objects via the web application's exposed endpoints
- [x] JDBC logic is abstracted away by the custom ORM
- [x] Programmatic persistence of entities (basic CRUD support) using custom ORM
- [x] File-based or programmatic configuration of entities

#Non-Functional requirements
- [ ] 80% line coverage of all service layer classes
- [ ] Generated Jacoco reports that display coverage metrics
- [x] Usage of the java.util.Stream API within your project
- [x] Custom ORM source code should be included within the web application as a Maven dependency

#Bonus Features
-[ ] Custom ORM supports basic transaction management (begin, commit, savepoint, rollback)
-[ ] Custom ORM supports connection pooling
-[ ] Session-based caching to minimize calls to the database for already retrieved data
-[ ] Deployment of web application to AWS EC2 (use of AWS Elastic Beanstalk is permitted)

#Getting Started
git clone https://github.com/211025-Enterprise/best_orm_p1.git

git clone https://github.com/211025-Enterprise/Levan_Webapp_p1.git (for webapp)

3 annotations Used
@Pkey for primary key constraint
@NoNull for not null constraint and 
@Unique for a unique constraint

#How to use
Make a model class preferably with public values and public getter/setters, toString ovverride needed for print statements put the annotations on top of each class field to make sure it's added correctly to the sql queries each class can be turned into a table Generic servlet service to service each servlet for each class

