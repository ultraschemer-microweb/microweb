# 1. MicroWeb

Microweb is HTTP Rest/Web framework, which brings MVC concepts to Vert.x, and focus in being very lightweight, reactive and with integrated user management support, supporting OpenId and permissions control, provided by KeyCloak.

The __definitive__ use case for Microweb is the development of __distributed microservices with strict user management control__.

Microweb has these characteristics:

* Stateless enforcing architecture, to support clusters transparently.
* No external configuration, apart database connection. No configuration through XML or annotations, availability of dynamic configuration and multiple application instance, in the same process.
* Strong separation between View, Controller and Business Domain code layers, but no hard enforcement over this.
* No default view templating support.
* Default Exception mapping and handling.
* Standard Object-Oriented/Relational mapping, using Hibernate.
* Default entity logging on register creation and updating.
* Default simple user management.
* Default abstractions for Filter and Request Controllers.
* Distributed register lockers, to define distributed critical sections.
* Default entity search routines.
* Logic Big Integer sequence implementation.
* Intrinsic support to OpenId and Resource Permission, using KeyCloak.

The main advantages of microweb, currently, are:

* Highly performant on limited environments: in Windows, 32Bit JVM, the entire system (without Keycloak) will use around 30MB of RAM Memory.
* Completely asynchronous, but supporting limited syncronicity.
* Full exposure of Vert.x features, including support to Reactive Programming.
* Not very opinionated, apart the rigid separation of Controller and Domain layers.
* Extremely narrowly focused: created for SOA Distributed Architecture with REST microservices, supporting Reactive programmming and User and Permissions control.

The main current limitation of microweb is that it's exclusive to PostgreSQL database. It can be ported to other databases, if demanded. Oracle version exists, but it's not released nor production ready.

# 2. Adding Microweb to you project

Microweb can be imported in Maven and Gradle Java Projects:

Gradle:
```
repositories {
  maven {
    url "https://ultraschemer.com/opensource/maven-repos/releases"
  }
}

dependencies {
  implementation "com.ultraschemer.microweb:microweb:0.5.0"
}
```

Maven:
```
<repositories>
  <repository>
    <url>https://ultraschemer.com/opensource/maven-repos/release</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>com.ultraschemer.microweb</groupId>
    <artifactId>microweb</artifactId>
    <version>0.5.0</version>
  </dependency>
</dependencies>
```

It's necessary to generate the database and configure its access too, as you can see in the sections below.

# 3. Microweb Concepts

Microweb aims to be a simple library to use. However, it doesn't aim to be a simple library. Microweb uses lots of concepts, and to know these conpects is __really__ necessary to permit a __good__ use of the library.

This section of Microweb documentation describes in details Microweb architectural concepts. Understanding these concepts, Microweb is much more usable.

## 3.1. Asynchronous and Reactive Programming - and Vert.x

__Microweb__ is built on top of __[Vert.x](https://vertx.io/)__.

When you're using Microweb, if you use it as a simple MVC or REST library, Microweb has no Vert.x flavor. It's easy to forget Microweb is built on top of Vert.x, but the library is not made to encapsulate and hide Vert.x.

You can use Microweb freely, as an MVC library, ignoring completely its Vert.x foundations. But Microweb is __Vert.x__. It means Microweb is __asynchronous__, __concurrent__, __multithreaded__ and __reactive__. To understand these topics is very important to understand exactly how this framework works.

As has been said above, you can abstract Vert.x and its specificities, but you must follow the next rule:

* __All the processing at REST Controllers must be made in, at most, 2 seconds__ - otherwise your program performance will degrade and maybe it can't work correctly in the long run.

It is strongly advised a previous research about the next topics, to understand all Microweb details correctly:
* [Vert.x](https://vertx.io)
* [Asynchronous programming](https://vertx.io/docs/guide-for-java-devs/)
* [Reactive programming](https://en.wikipedia.org/wiki/Reactive_programming) - and [Reactive.X](http://reactivex.io/), for extra information.
* Multithreading and Multitasking
* Parallellism and Concurrency
* Clustering

The last three topics are of general knowledge in Computer Sciences and Engineering. It's advised to search about them in Google, or look for good Computer Engineering books.

## 3.2. The Model/View/Controller architecture and its variants

Microweb can be used as a REST server library or as a full MVC library, to create Web Applications. Microweb, in its core, isn't a full MVC library, since it has no support to a standard view implementation. But, when its architecture is evaluated, it's visible the full support to MVC architecture, since, to generate views, similarly to other MVC frameworks, it's only necessary a Templating Library attached to Microweb.

Since this project does not aim to be _very_ opinionated, the user can choose any Templating library to generate the views in his/her projects. By the way, in the code samples in this documentation, we use [FreeMarker](https://freemarker.apache.org/), which is packaged, by default, with Microweb.

It's even possible to use Microweb as an MVC library without a Templating Library, but the HTML text of views would need to be completely generated on fly, and programmatically.

A critique about the MVC architecture is presented below, and the separation between the __domain__ and __entity__ layers, in Microweb projects is rationalized.

## 3.3. MVC, MVP, MVVM, the _Controller_ and the _Model_ problem

MVC, MVP and MVVM are architectures developed to implement __graphical user interfaces__. While MVC and MVP are more generic, MVVM is an architecture developed by Microsoft specifically for web interface development. This section of this documentation considers MVP and MVVM as MVC variants, and the discussion can be applied to MVP and MVVM without losing generalization.

__MVC__ means __Model__, __View__ and __Controller__, each one being a software layer having one specific responsibility. __MVC__ was first defined as a pattern in the infamous book known as [__Gang of Four__](https://www.amazon.com/Design-Patterns-Object-Oriented-Addison-Wesley-Professional-ebook/dp/B000SEIBB8).

The __View__ layer has a very clear role, but the __Controller__ and __Model__ layers (or which names they have in MVP or MVVM) have some obscure interpretations.

__MVC__ pattern has been championed by lots of frameworks, most notably by Apple, in its [__Cocoa__](https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/CocoaFundamentals/WhatIsCocoa/WhatIsCocoa.html) Framework. 

__MVP__ pattern is from Google, and has some minor differences from __MVC__.

Both Google and Apple actively promote that the __Business Rules__ and __Algoritms__ must be held at the __Model__ layer, and that data is just part of the __Model__, in the same level of importance than the __Algorithms__.

For Microsoft, the __Model__ layer is used to store and manipulate project __Data__. The Business Rules and Algorithms must be held at the __Controller__ layer.

Independently of the origin of these interpretations, the vast majority of MVC Frameworks (with the notable exception of [Ruby-on-Rails](https://rubyonrails.org/)) doesn't give any advice over where the Business Rules must be held. Rails, being the exception, advises the Business Rules must be held at the Model layer, as Google with MVP and Apple with MVC advise.

The problem of the idea of maintain the Business Rules and Algorithms in the Model layer, or in the Controller layer, is that this promotes some bad programming behavior from programmers not very used to Objected Oriented software architecture.

Maintaining business rules at the __Model__ level creates the __fat models__, as defended by Ruby-on-Rails and on Cocoa development, while maintaining business rules at the __Controller__ level creates __fat controllers__.

Both __fat controllers__ and __fat models__ are __evil__. They are less reusable, and they break one of the most important programming principles: the __separation of concerns__. Fat models mix data and algorithm, which is a feature of Object-Oriented programming, but, in a layered system, to these fat models be really useful, the encapsulation of data must be disrespected, because the Model Layer almost always are directly modelled on database entities, which should be exclusive to each model class, and Fat Models really __need__ to disrespect this rule of thumb - thus turning these Fat Models akin to spaghetti code. Fat controllers are even more evil, because they're not reusable at all apart of the MVC (MVP/MVVM) structure it's originated.

So, some frameworks solve this problem maintaining the Model as the area to store and make little manipulation to data (see the [Repository Pattern](https://martinfowler.com/eaaCatalog/repository.html)) and defining a "Services" layer, which receives all algorithms. [Laravel](https://laravel.com/) follows this approach.

However, multiplying layers doesn't solve the problem at all. The only way to solve encapsulation and separation of concerns problems is __to use encapsulation and separation of concerns__. Microweb doesn't enforce these, but follow the next principles:

* The __Model__ layer is not named, but an __Entity__ layer is defined, and all data mapping is defined in this layer. The base technology used in the Model layer is Hibernate and a strict tree of Classes that defines Object-Relational entity mappings. This layer can be completely ignored by the remainder of the system - although the basic configurations require the correct Model layer configuration. After these configurations are finished, the Entity layer can be forget, and other approachs can be used. On the other hand, the Model layer is completely independent of the remainder of Microweb, and can even be used by other frameworks.
* The __Controller__ layer is __explicit__, and is only used strictly to register REST routes. Data validation and process flow are to be made in the Controller Layer, as MVC/MVP/MVVM dictate.
* The algorithms and business rules are mainly __procedural algorithms__ which can be held anywere, and it's advised to define a specific package to receive them. By default, in all samples presented here, these algorithms and business rules are held in a __Domain__ package.
* No additional layers are required and the project structure is completely under responsibility of the implementer. It's even possible to use Microweb without respecting Separation of Concerns or MVC layers, but the responsibility over such architecture is solely yours.


## 3.4. The relevant MVC layers on Microweb

As said in the section above, Microweb __is__ an MVC Framework, but it doesn't enforce this architecture. However, all samples presented here use MVC. The layers are defined as next:

* __View__: this layer is defined as a set of FreeMarker templates, all rendered in a standard way. This rendering way doesn't belong to the Framework, but is a useful example of how to implement the __View__ layer. 
* __Model__: this layer is not enforced, but can be seen as the set of two packages, the __Domain__  and the __Entity__ packages, if you prefere the __fat model__ aproach, or can be seen as the __Entity__ package alone, if you prefer the __thin model__ and __fat controller__ approach.
* __Controller__: This is the only enforced layer, but the __Domain__ package can be considered part of the __Controller__ layer if you prefer the __fat controller__ and __thin model__ approach.

Beyond these layers/packages, data exchanged between layers can be validated, enabling a kind of contract programming between them. This communication uses specific beans, declared in properly defined packages.

# 4. Database Model and External Dependencies

Microweb uses, necessarily, a Relational Database, which, currently, can only be PostgreSQL. MySQL is not supported (_yet_).

To generate this database, a set of migrations is defined, using [Python 3](https://www.python.org/) and [Alembic](https://alembic.sqlalchemy.org/en/latest/).

If you want to go directly to action, __you can skip to the next major document section, 5. Project Samples__. The next subsections explain, conceptually, the Migration technology choice rationale, the Database Structure and how this structure impacts the Entity Package in Microweb projects.

## 4.1. The Rationale over the Microweb Database structure

If a specific database, generated by pre-defined migrations, is used by Microweb, it means that Microweb isn't a database agnostic framework, and this database must contain specific tables and relationships.

This is the correct assumption. The main database must be explicitly defined, linked and populated with specific relations and data to serve Microweb.

### 4.1.2. Microweb, Microservices and SOA Architecture

For programmers with no experience in SOA Architecture and Microservices, Microweb dependency on a predefined database can be puzzling, if we say Microweb is not _very_ opinionated and reusable.

The main point is that Microweb projects are __to be used as self-contained microservices in a bigger whole of components__. This predefined default database can be shared with other microservices, but it can be used solely by the developed Microweb service. The reuse and independence is in __architectural__ level, if the whole project is developed using SOA Architecture and microservices.

If you want to use a Microweb service to develop a monolitic project, the database generated by Microweb basic migrations must be the __start__ point of the entire project, as other Frameworks, like __Laravel__ or __Django__ require. This approach is not commonplace in Java, since all Java Major frameworks (Spring/Spring-Boot, J2EE, Struts) are database agnostic.

If you have a previous project and you yet want to use Microweb in this project, the framework use must be evaluated case by case. The basic scenario is to set up an entire new database to be used by Microweb, and isolate such database behind the new developed __microservice__. 

Contact Microweb main developer if you want consulting services on this subject.

### 4.1.3. Migrations - and why Python? And why Alembic?

Why to use a Python based Migration library to provide migrations to Microweb, if do very good and well tested and used Java-based migration tools exist? 

Because Microweb is a Java based framework, not a Java Centric project.

Alembic has been chosen because it provided the best ways to be customized following the defined database architecture used by Microweb.

If you don't want to use Alembic, you can create the first database version, export it entirely as a SQL script, and then import this database script to another migration framework. The most known Java-based migration frameworks ([Liquibase](https://www.liquibase.org/) and [Flyway](https://flywaydb.org/)) both support this approach. You can just create the first database version and then forget Alembic forever.

But this choice brings some constraints: the basic table and relation abstractions Microweb relies are already implemented in the Alembic given structure. You'll need to reimplement them in the Migration tool you choose.

## 4.2. Microweb database concepts and principles

The standard database structure is considered one of the most important features Microweb provides, and this structure and its abstractions are explained here. Before a deep view in the database structure, let's understand the basic architectural principles that guide Microweb Entity layer and database structure.

### 4.2.1. Microweb relational database principles and architecture

Microweb database structure emphasizes safety and clarity. The next conventions are used:

1. All microweb relations (tables) have a unique random identifier, which can't be identified as a member of a sequence. A GUID is used for the primary key.
2. No multiple field primary key is supported, to ensure simple relationship definitions between tables. It must be considered the fact that any multiple unique-constraint is correspondent to a primary key, since the relationship between unique constraints and primary keys, in a table, is a bijection.
3. Data tables and relationship tables are indistiguishible. Microweb treat all of them simply as data, and the meaning of the tables are defined by the interpretation of these relations in the Business Layer.
4. All tables must be named with ANSI names, so all names are considered case insensitive - and defined in case sensitive databases (as PostgreSQL) __in lowercase__.
5. Tables can have names with multiple words - these words are separated by __underline (_)__ characters.
6. Relationship tables, by definition, carry the names of the related tables, containing or not, on each table, a explanation prefix, separated by underlines, and these names are united by __double underlines (_)__.
7. Tables with names equais to SQL reserved words are just ended with an underline character.

The conventions above are __internal__ to Microweb, and the developer doesn't need to use them. They're explained above just to show how table names are defined in Microweb, and the default Microweb database can be seen as an example.

The individual table structure, in the other hand, has a standard physical structure, requiring:

1. The existence of a table called __entity_history__, which contains the log of all register creations and updates in database.
2. The primary key field must be named __id__.
3. Tables can log their creation date with a field called __created_at__, which is a full timestamp, with time zone enabled.
4. Tables can log their update dates with a field called __updated_at__, which is a full timestamp, with time zone enabled.
5. The filling of __id__, __created_at__ and __updated_at__ fields are dealt by Microweb in standard ways. Se more details below.
7. The filling of __entity_history__ logs, from all registers from all tables, are also dealt by Microweb.
8. The __delete__ database operation is considered harmful and must be avoided almost always. An object/register state control must be ensured.

This standard structure exists to fulfill specific Microweb Object-Relational data definitions, which are explained next.

### 4.2.2. Microweb Object-Relational Mapping concepts

Microweb framework, in the Business Layer relies on [Hibernate ORM](http://hibernate.org/), but additions to Hibernate are minimal. Hibernate is a huge project, and no addition to the library were considered relevant.

However, Microwed defines four types of Entities. These entities are defined in base of a specific undestanding of the concept of Classes and Objects and its correspondent relationship with the concepts of Relation and Registers.

It's a consolidated concept, in Object-Relational-Mapping that tables can be mapped to classes, and registers to objects. Microweb follows this concept but add the next additional concepts, to help to understand the lifecycle of persisted objects:

1. An object with persistent lifecycle has history. This history can be logged. If an object/register can have its history logged, then it is a Loggable Entity, represented by the mapped java class __Loggable__.
2. Each register, independent of its internal data and unique keys, has a single identity. This identity is defined by a random unique identification, stored in the field __id__. This kind of register is an __Identifiable__ Entity. Every __Identifiable__ is __Loggable__.
3. A persistent register can have the initial period of its lifecycle (i.e, its birth) registered and known. The date of birth is stored in the field __created_at__. By convention, these objects are assigned and after that, they're read only. This kind of register is a __Createable__. Every __Createable__ is a specialization of __Identifiable__.
4. A persistent register can have full lifecycle and each update will be logged. The last update date and time is saved at the __updated_at__ field. This kind of object/field is __Timeable__, and every __Timeable__ object is a __Createable__ object. __Timeable__ objects and entities, once created, evolve through time.
5. Register removal (deletion) is strictly avoided, and objects which need to be deleted must be of type __Timeable__ and support some kind of status control, indicating soft-delete operations.

Considering all concepts above (in the __Section 4__), we can dive into practical details of Microweb and the best way to make such dive is through a practical sample. Migration examples can be seen in both __5.1__ and __5.2__ sections.

# 5. Project Samples

Two project samples are used to show how Microweb works. The first project is a simple user management project, without OpenID support. The second project is the same project, but with OpenId support, and permissions control.

Both projects are written in Java, which is the _lingua franca_ of JVM Plataform (which is called ___Java___ Platform).

## 5.1. Simple user manager system, without OpenID support

This sections present a simple Microweb project, with full MVC architecture, with data model, business rules, migrations, route-registering, controllers and interface generation with HTML templates.

A simple user management is defined, and no OpenId, nor Permission Control is supported.

This is the simplest form of use of Microweb.

### 5.1.1. Project Objectives and Technical Requirements

Any project must have a defined scope and objective. The objective of this project is just to provide a central user repository, managed by Microweb.

The next features will be provided:

1. Web management interface
   1. Unlogged Home Page with Login
   2. Logged Home Page
   3. User Addition Form
   4. User Edition Form
   5. User Listing
   6. User Removal
2. REST interface enabling all services above to a public API
3. Attribution of user roles
4. Simple permission control based on user roles
6. Each user can store text documents and images on his/her accounts.
7. Data management interface must be available to users.
8. REST interfaces for user data management must be available to users.
9. Read-only comments can be attached to documents and images.

After defining the project objectives, project conditions are defined:

1. The project must be developed in Java Programming language, to achieve the biggest public using the Java Platform.
2. The basic project management system is Gradle.
3. The default migration tool to be used is Alembic, which is Microweb default migration tool.
4. The database to be used is PostgreSQL, which is Microweb default database.
5. User interface will be generated in the Backend, using FreeMarker, which is already packaged with Microweb.

Now, we can start to develop the system, starting by the project creation, and then the database definition.

### 5.1.2. Creating the project

To create the project, reserve a directory in your system, and create a new folder to store project code:

```sh
$ mkdir microweb-sample
$ cd microweb-sample
$ gradle init

Select type of project to generate:
  1: basic
  2: application
  3: library
  4: Gradle plugin
Enter selection (default: basic) [1..4] 2

Select implementation language:
  1: C++
  2: Groovy
  3: Java
  4: Kotlin
  5: Swift
Enter selection (default: Java) [1..5] 3

Select build script DSL:
  1: Groovy
  2: Kotlin
Enter selection (default: Groovy) [1..2] 1

Select test framework:
  1: JUnit 4
  2: TestNG
  3: Spock
  4: JUnit Jupiter
Enter selection (default: JUnit 4) [1..4] 1

Project name (default: microweb-sample):
Source package (default: microweb.sample):

> Task :init
Get more help with your project: https://docs.gradle.org/6.../userguide/tutorial_java_projects.html

BUILD SUCCESSFUL in 32s
2 actionable tasks: 2 executed

```

Then, add a Wrapper, to your project, appending the next snippet to your `build.gradle` file:
```groovy
wrapper {
    gradleVersion = '6.5'
}
```

Then, update the wrapper, and build the project:
```sh
$ gradle wrapper
```

Add __Microweb__ to your project, as a dependency, changing, in `build.gradle`, this snippet:
```groovy
...
...

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    // This dependency is used by the application.
    implementation 'com.google.guava:guava:28.1-jre'

    // Use JUnit test framework
    testImplementation 'junit:junit:4.12'
}

...
...
```
to this:
```groovy
...
...

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    maven {
        url "https://ultraschemer.com/opensource/maven-repos/releases"
    }
}

dependencies {
    implementation 'com.ultraschemer.microweb:microweb:0.5.0'

    // Use JUnit test framework
    testImplementation 'junit:junit:4.12'
}

...
...
```

Currently, Microweb doesn't support Guava versions above 20.0. Since Guava is a Microweb dependency, you can just remove the original Guava dependency defined in the original generated `build.gradle` file, as shown above.

Now, build the project:
```sh
$ ./gradlew build
```
### 5.1.3. Database definition and migrations
After creating the Java Project, with Gradle support, migrations must be attached to this project.

In the project root directory, execute:
```sh
$ git clone https://github.com/ultraschemer/microweb.git
$ mv microweb/database-sql ./database-sql
$ rm -Rf microweb
```

Now you have the same database scripts and migrations originally provided by Microweb to your project.

#### 5.1.3.1. Creating the database and generating it.
Before to continue this tutorial, lets consider you have a PostgreSQL database instance available at your local machine, on default port. Your administrator user is the default `postgres` user. (_obs.: PostgreSQL is the standard microweb database. Support to other databases are being provided. Details on PostgreSQL database administration are beyond the scope of this README._)

> To execute the instructions available in this section, you need a local installation of PostgreSQL CLI client programs (`psql`), a Python 3 installation (`python3`) and a local installation of PostrgreSQL C client libraries (`libpq`, `libpq-dev`, depending on your operating system).

To create the database, open your __sh__ shell, if you are on Linux or MacOS, and, from the project root directory, open the file `database-sql/database-initialization-postgresql.sh` and
change its contents from:

```bash
#!/usr/bin/env bash
psql --command "CREATE USER microweb WITH PASSWORD 'microweb';"
createdb -O microweb microweb
psql --command "GRANT ALL PRIVILEGES ON DATABASE microweb TO microweb"
psql --command 'CREATE EXTENSION "uuid-ossp"' microweb
psql --command 'CREATE EXTENSION "pgcrypto"' microweb
```

to

```bash
#!/usr/bin/env bash
psql --command "CREATE USER microwebsample WITH PASSWORD 'microwebsample';"
createdb -O microwebsample microwebsample
psql --command "GRANT ALL PRIVILEGES ON DATABASE microwebsample TO microwebsample"
psql --command 'CREATE EXTENSION "uuid-ossp"' microwebsample
psql --command 'CREATE EXTENSION "pgcrypto"' microwebsample
```
I.e, change every time the word `microweb` appears to `microwebsample`.

Then, open the file `database-sql\database-finalization-postgresql.sh` and change its contents from:

```bash
#!/usr/bin/env bash
psql --command "DROP DATABASE microweb"
psql --command "DROP USER microweb"
```

to

```bash
#!/usr/bin/env bash
psql --command "DROP DATABASE microwebsample"
psql --command "DROP USER microwebsample"
```

the create the database, ensure the script will be run by a Postgresql user with administrative powers:

```sh
$ cd database-sql
$ PGUSER=postgres; export PGUSER
$ PGPASSWORD=postgres; export PGPASSWORD
$ ./database-initialization-postgresql.sh
CREATE ROLE
GRANT
CREATE EXTENSION
CREATE EXTENSION
```

Change the variables `PGUSER` and `PGDATABASE` to reflect your PostgreSQL superuser. You can set other PostgreSQL environment variables, as shown [here](https://www.postgresql.org/docs/9.3/libpq-envars.html), to support your database configuration.

To delete the database, just run the `database-finalization-postgresql.sh` script:

```sh
$ cd database-sql
$ PGUSER=postgres; export PGUSER
$ PGPASSWORD=postgres; export PGPASSWORD
$ ./database-finalization-postgresql.sh
DROP DATABASE
DROP ROLE
```

Once you created the database, you'll have a `microwebsample` database, accessible by a user called `microwebsample` which password is `microwebsample`.

#### 5.1.3.2. Configuring and running the migrations

Once the database is created, it's necessary to run the migrations. To do so, go to project root and execute:

```sh
$ cd database-sql/python-migrations
$ python3 -m venv venv # Create python virtual environment, just to run migrations, here
$ source venv/bin/activate # Load the python virtual environment
(venv) $ pip install -r requirements.txt
...
...
Lots of output
...
...
```

Then, yet with the Python3 Virtual Environment enabled, create the migration configuration file:

```sh
$ cp alembic.ini.sample alembic.ini
```

Edit the configuration file, so Alembic can access the database you created before, changing, on it, the snippet:

```python
# PostgreSQL:
sqlalchemy.url = postgresql://microweb:microweb@localhost/microweb
```

to:

```python
# PostgreSQL:
sqlalchemy.url = postgresql://microwebsample:microwebsample@localhost/microwebsample
```

Save the changes, return to shell and, yet in Virtual Environment, execute:

```sh
(venv) $ alembic upgrade head
INFO  [alembic.runtime.migration] Context impl PostgresqlImpl.
INFO  [alembic.runtime.migration] Will assume transactional DDL.
INFO  [alembic.runtime.migration] Running upgrade  -> 58a0c70dd98a, Create initial database.
(venv) $
```

Now you have the initial microweb database. You can choose to continue using Alembic, or change the migration tool. Since we see no profit changing the migration tool at this moment, we'll continue this tutorial with Alembic.

#### 5.1.3.3. Understanding the default database schema
Once you created the database, you can connect to it and you'll see this schema:

![database schema](microweb-initial-schema.png)

This is the initial Microweb database, and a thoroughly explanation about it will be given now, to clarify each entity use in the system, how they're related and how simple they are.

* `alembic_version`: Table used by __Alembic__ to control migration use.
* `entity_history`: Table used internally, by __Microweb__ to store the entire update history, on all __Loggable__ tables. Since to __Microweb__ register deletion is totally unadvised for Loggable entities, then no logging __delete__ support is provided. The orientation is __do not delete loggable registers from database, implement _soft_ delete__.
* `configuration`: A key-value table, with internal configurations needed by Microweb be started up. Configurations are read only, and can be edited only externally to the system (at least this is their purpose and idea).
* `runtime`: A key-value table with variables which can be used globally by the system, and that can be changed over time.
* `lock_control`: A table with lock names. These locks are used by microweb to create and control distributed critical sections, to avoid the use of external queue tools. These distributed locks are dangerous and of limited use, so don't try to replace queue and messaging management systems with these distributed locks. Their use case is to provide some exclusive block control in clustered and distributed systems.
* `user_`: __The main and central table in Microweb__. The `user_` table (with the trailing underline, to avoid clashes with the default `user` tables available in RDBMS systems, as PostgreSQL and Oracle) contains necessary user data for his/her identification. These data are mapped to be compatible with __KeyCloak__ user format and this table is synchronized with KeyCloak when OpenID support is used in a __Microweb__ project.
* `person`: A __user__ is not necessarily a __person__. This table represents the person linked to a user. This is an optional table, __Microweb__ doesn't use this table actively, and it's here for future evolution of the platform. 
* `email_address`: A table to store users' emails.
* `user__email_address`: The relation of users and their e-mail addresses.
* `phone_number`: A table to store users' phone numbers. This table, currently, is not used by __Microweb__. It's available for future __Microweb__ development.
* `user__phone_number`: The relation of users and their phone numbers.
* `access_token`: When OpenID is not used, the user access is controlled internally by Microweb. The access keys (used in Http Bearer authorization headers) is stored at this table.
* `role`: Table used to register user roles in the system. When OpenID is enabled, this table is synchronized with User Roles present at KeyCloak.
* `user__role`: __TODO__

__<span style="color: red">Never ever edit these tables. They're reserved to internal microweb use.</span>__ 

If you want to change the behavior of Microweb tables, create other tables to extend them. If the standard Microweb table names clash with other table names on your system, it's possible to prefix all table names, editing the Migration scripts and creating a custom microweb version to support your needs.

In the future, an alternative version of Microweb, with all table names prefixed can be provided.

#### 5.1.3.4. Creating the migration and needed tables

Once the default Microweb database is understoood, it's the moment to create a migration, and create the table to store __documents__ and __images__ as requested in the project objectives (__Section 5.1.1__).

Go to the `database-sql/python-migrations` directory, load Python3 environment and create a new Alembic migration. Read the full [Alembic Tutorial](https://alembic.sqlalchemy.org/en/latest/tutorial.html), to understand this section better.

```sh
$ cd database-sql/python-migrations
$ source venv/bin/activate
(venv) $ alembic revision -m "Creating document and image relations."
Generating  ...\f020117fa010_creating_document_and_image_relations.py ...  done
$ 
```

Now, let's edit the created file (in the example above, it has the name `f020117fa010_creating_document_and_image_relations.py` and is stored at `database-sql/python-migrations/microweb/versuibs` directory.

The entities to be created are the next:

* `image`: A table to store the binary image data. To store binary image data in database is considered a __bad practice__. It's, generally, better to store them in filesystems (as S3, Hadoop, etc), but this project is a self contained example, so, to maintain simplicity, images will be stored in database. These images will be read only, and won't be updated at all. With this properties, `image` is a __Createable__ entity.

### 5.1.4. Creating the interfaces

__TODO__

## 5.2. Simple user manager system, with OpenID support

__TODO__
