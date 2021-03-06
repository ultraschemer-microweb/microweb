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
* Intrinsic support to OpenId and Resource Permission, using KeyCloak.

And three internal functionalities, which can be used, but are unadvised:

* Distributed register lockers, to define distributed critical sections.
* Default entity search routines.
* Logic Big Integer sequence implementation.

The main advantages of microweb, currently, are:

* Highly performant on limited environments: in Windows, 32Bit JVM, the entire system (without Keycloak) will use around 60MB of RAM Memory.
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
  implementation "com.ultraschemer.microweb:microweb:0.6.16"
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
    <version>0.6.16</version>
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

The problem of the idea of maintaining the Business Rules and Algorithms in the Model layer, or in the Controller layer, is that this promotes some bad programming behavior from programmers not very used to Objected Oriented software architecture.

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

If you want to use a Microweb service to develop a monolitic project, the database generated by Microweb basic migrations must be the __start__ point of the entire project, as other Frameworks, like __Laravel__ or __Django__ require. This approach is not commonplace in Java, since all Java Major frameworks (Spring/Spring-Boot, J2EE, Struts, Tapestry, etc) are __completely__ database agnostic.

If you have a previous project and you yet want to use Microweb in this project, the framework use must be evaluated case by case. The basic scenario is to set up an entire new database to be used by Microweb, and isolate such database behind the new developed __microservice__. 

Contact Microweb main developer if you want consulting services on this subject.

### 4.1.3. Migrations - and why Python? And why Alembic?

Why to use a Python based Migration library to provide migrations to Microweb, if do very good and well tested and used Java-based migration tools exist? 

Because Microweb is a Java based framework, not a Java Centric project.

Alembic has been chosen because it provided the best ways to be customized following the defined database architecture used by Microweb.

If you don't want to use Alembic, you can create the first database version, export it entirely to a SQL script, and then import this database script to another migration framework. The most known Java-based migration frameworks ([Liquibase](https://www.liquibase.org/) and [Flyway](https://flywaydb.org/)) both support this approach. You can just create the first database version and then forget Alembic forever.

But this choice brings some constraints: the basic table and relation abstractions Microweb relies are already implemented in the Alembic given structure. You'll need to reimplement them in the Migration tool you choose.

## 4.2. Microweb database concepts and principles

The standard database structure is considered one of the most important features Microweb provides, and this structure and its abstractions are explained here. Before a deep view in the database structure, let's understand the basic architectural principles that guide Microweb Entity layer and database structure.

### 4.2.1. Microweb relational database principles and architecture

Microweb database structure emphasizes safety and clarity. The next conventions are used:

1. All microweb relations (tables) have a unique random identifier, which can't be identified as a member of a sequence. A GUID is used for the primary key.
2. No multiple field primary key is supported, to ensure simple relationship definitions between tables. It must be considered the fact that any multiple unique-constraint is correspondent to a primary key, since the relationship between unique constraints and primary keys, in a table, is a bijection.
3. Data tables and relationship tables are indistinguishible. Microweb treat all of them simply as data, and the meaning of the tables are defined by the interpretation of these relations in the Business Layer.
4. All tables must be named with ANSI names, so all names are considered case insensitive - and defined in case sensitive databases (as PostgreSQL) __in lowercase__.
5. Tables can have names with multiple words - these words are separated by __underline (_)__ characters.
6. Relationship tables, by definition, carry the names of the related tables, containing or not, on each table, a explanation prefix, separated by underlines, and these names are united by __double underlines (\_\_)__.
7. Tables with names equais to SQL reserved words are just ended with an underline character to avoid clashes.

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

It's a consolidated concept, in Object-Relational-Mapping, that tables can be mapped to classes, and registers to objects. Microweb follows this concept but add the next additional concepts, to help to understand the lifecycle of persisted objects:

1. An object with persistent lifecycle has history. This history can be logged. If an object/register can have its history logged, then it is a Loggable Entity, represented by the mapped java class __Loggable__.
2. Each register, independent of its internal data and unique keys, has a single identity. This identity is defined by a random unique identification, stored in the field __id__. This kind of register is an __Identifiable__ Entity. Every __Identifiable__ is __Loggable__.
3. A persistent register can have the initial period of its lifecycle (i.e, its birth) registered and known. The date of birth is stored in the field __created_at__. By convention, these objects are assigned and after that, they're read only. This kind of register is a __Createable__. Every __Createable__ is a specialization of __Identifiable__.
4. A persistent register can have full lifecycle and each update will be logged. The last update date and time is saved at the __updated_at__ field. This kind of object/field is __Timeable__, and every __Timeable__ object is a __Createable__ object. __Timeable__ objects and entities, once created, evolve through time.
5. Register removal (deletion) is strictly avoided, and objects which need to be deleted must be of type __Timeable__ and support some kind of status control, indicating soft-delete operations.

Considering all concepts above (in the __Section 4__), we can dive into practical details of Microweb and the best way to make such dive is through a practical sample. Migration examples can be seen in both __5.1__ and __5.2__ sections.

# 5. Microweb Use Cases

Two project samples are used to show how Microweb works. The first project is a simple user management project, without OpenID support. The second project is the same project, but with OpenId support permissions control and an external service, using other technology than the Java Platform.

Both projects are written mainly in Java, which is the _lingua franca_ of JVM Plataform (which is called ___Java___ Platform).

The first project presents the first two use cases for Microweb:

* Use Case __MVC Web Application Platform__: This is the very basic use case for Microweb. If you want to use Microweb as a MVC framework, it fits suitably, but it doesn't shine. Other frameworks in Java (Spring, JEE, Struts), or other languages (Laravel, Ruby-on-Rails, Django) are more stable and flexible to use for this purpose - but Microweb is suitable to be used as a fairly complete and simple MVC Framework.
* Use Case __HTTP REST Api with Custom Resource Control__: Microweb brings facilities for this use case, and to use such features can really justify Microweb use, but this feature is very simple and can be easily replicated with other frameworks, most notably the non-Java complex ones (like Laravel, Rails or Django).

The second project reuses the first two use cases and go deeper on Microweb really useful use cases:

* Use Case __HTTP Web Application and API with complex Resource Control and OpenID support__: Microweb is __deeply__ integrated with __KeyCloak__, in __run-time__. KeyCloak offers strong support to other Java technologies, like __Spring__ and __JEE__, but in a different fashion than Microweb. Microweb is made to be used alongside __KeyCloak__ in __run-time__, being KeyCloak just another associated __microservice__, differently from KeyCloak own approach with JEE and Spring. No intentions to say which approach is best, since this kind of analysis is subjective.
* Use Case __OpenID enabled HTTP Web Application and API over heterogeneous SOA infra-structure__: This is ___the Microweb main use case___. Microweb can be used as a central proxy and service registrar for application services, under a SOA architecture, using HTTP REST as the standard network integration protocol. All resources can be registered, filtered and their permissions can be controlled by Microweb web microservices or clusters. Everything is stateless, and user and permission control is assured by the pair Microweb-KeyCloak.

_Obs.: It's possible to use HTTP services as distributed objects, making calls among them, __even recursively__. But it's necessary to ensure the implementation of some architectural structures (like all Network and REST calls being called and handle asynchronously) to create a SOA general architecture. This topic is complex and far beyond the objective of this README._

Let's start creating the first project.

## 5.1. Simple user manager system, without OpenID support

This section presents a simple Microweb project, with full MVC architecture, with data model, business rules, migrations, route-registering, controllers and interface generation with HTML templates.

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
6. Each user can store images on his/her accounts.
7. Data management interface must be available to users.
8. REST interfaces for user data management must be available to users.

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
    // The entire Microweb framework:
    implementation 'com.ultraschemer.microweb:microweb:0.6.16'

    // Database driver:
    implementation group: 'org.postgresql', name: 'postgresql', version: '42.2.4'

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

To create the database, open your __sh__ shell, if you are on Linux or MacOS, and, from the project root directory, open the file `database-sql/database-initialization-postgresql.sh` and change its contents from:

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

To create the database, ensure the script will be run by a Postgresql user with administrative powers:

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

__<span style="color: red">Never ever edit these tables. They're reserved for internal microweb use.</span>__ 

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
* `document`: A table to store __text__ documents from users. Differently from images, a document can be changed and updated, so it's an entity evolving through time. These caracteristcs place `document` as a specialization of __Timeable__.
* `user__image`: This is the relationship between images and users, linking users to their known images. This relation, in principle, is not editable, so it should be a __Createable__. An image can be known by many users, but it has only one original owner - so we can set an `alias` field to this relationship, so the user can rename the image, without changing its global name. This turns this entity a __Timeable__.
* `user__document`: This is the relationship between documents and users, linking users to their known documents. As with `user__image`, this is a __Timeable__ entity. A document can be known by many users, but it has only one original owner.

Edit the generated migration file, and put these contents on the `upgrade` and `downgrade` functions, in the generated migration file:

```python
def upgrade():
    # Create the table 'image' only with data fields. The boilerplate fields (like id, created_at, etc)
    # will be created later
    op.create_table('image',
                    # This is the image name - just a helper. No uniqueness ensured:
                    sa.Column('name', sa.String(1024), nullable=False),
                    # The image data. Since it's saved in database, and it's binary data, we store it as
                    # Base64 information. This is a non-optimized option, just to ensure simplicity in this
                    # sample:
                    sa.Column('base64data', sa.Text(), nullable=False))

    # There are other three set-type methods of op: set_identifiable, se_createable and set timeable.
    # They configure suitably the tables on which they're applied, so they can be mapped as Tdentiable, Createable
    #  and Timeable entities correctly.

    # The 'image' relation store read-only registers. These registers are of type Createable. So, set the table as
    # a 'createable' entity - and this function will generate all boilerplate fields to fulfill the needs of a
    # 'createable' entity.
    op.set_createable('image')

    # Table references in Microweb are built in a default fashion, using the method 'op.set_reference'.
    # Let's create a reference to a user, which will be the image owner. Is it possible to have an image
    # without a owner? Yes, it is. A system image, for example. So, this reference is nullable:
    op.set_reference(
        # this is the table which owns the reference, i.e, the table which has the foreign key:
        'image',
        # This is the Foreign key field name, present on 'image' table, which has the foreign key:
        'owner_user_id',
        # This is the table receiving the reference, i.e, the table pointed by the foreign key, on it's 'id' field:
        'user_',
        # This user_id reference field is nullable, so we set the 'rtn' field, to reach the 'n' field.
        # The 'rtn' field is the reference table field name, which, by default equals to 'id'. This single lettered
        # parameter names is to remember the user that these fields are optional, non-default, and must be used only
        # in very well thought structures:
        'id',
        # This user_id reference field is nullable, so the 'n' (from nullable) parameter is set:
        True)

    # Create the relationship table between user and image - the table need to have, at least a column:
    op.create_table('user__image',
                    # This is a pure relationship table, but alembic/database limits the creation of empty tables,
                    # so we can create a dummy field. To avoid creating dummy fields, and customizing the relationship
                    # table in a useful way, we create a relationship table between a user and an image, assigning an
                    # alias field - so the image can appear to the user with a different name from its owner:
                    sa.Column('alias', sa.String(1024), nullable=True))
    # The relationship, originally was imagined as a Createable, but since it has an alias, which can be updated, then
    # it's a Timeable:
    op.set_timeable('user__image')

    # Set the references:

    # Use the default optional parameters - since this field is not nullable, and the reference
    # primary key is default, too:
    op.set_reference('user__image', 'image_id', 'image')
    op.set_reference('user__image', 'user_id', 'user_')

    # Since the references are set in the relationship table, ensure unicity - the unique key has a name, to ensure
    # it is accessible in the future, for updates or changes:
    op.create_unique_constraint('ui_65e8sZZ5_uidx', 'user__image', ['image_id', 'user_id'])

    # Create the text document entity:
    op.create_table('document',
                    sa.Column('name', sa.String(1024), nullable=False),
                    sa.Column('contents', sa.Text(), nullable=False),
                    # A status for document. 'regular' is de default document status, and its default format too.
                    # Other status can be set in the future.
                    sa.Column('status', sa.String(16), nullable=False, server_default='regular'))

    # Documents are quintessentially Timeable:
    op.set_timeable('document')

    # As an 'image', a 'document' has an optional owner:
    op.set_reference('document', 'owner_user_id', 'user_', 'id', True)

    # The document/user relationship is similar to the image/user relationship:
    op.create_table('user__document', sa.Column('alias', sa.String(1024), nullable=True))
    op.set_timeable('user__document')

    op.set_reference('user__document', 'document_id', 'document')
    op.set_reference('user__document', 'user_id', 'user_')


def downgrade():
    op.drop_table('user__document')
    op.drop_table('document')
    op.drop_table('user__image')
    op.drop_table('image')
```

Read the comments in the file above, to understand how Alembic is used by Microweb. You can remove such comments, if you decide to do so.

You'll see there are some Microweb specific functions and methods in the Migration. This is the cause we chosen Alembic to create Microweb migrations: because framework extensions are __very, very easy__ to implement on it. You can see all Alembic extensions provided by Microweb reading the file `database-sql/python-migrations/microweb/env.py`, customized specifically for Microweb. You can add your own customizations there, too.

Now, go to the command line, and run the Alembic migration, to update the database:

```sh
$ cd database-sql/python-migrations
# Load the python virtual environment. If the environment is already loaded, ignore this:
$ source venv/bin/activate 
(venv) $ alembic upgrade head
```

If everything is OK, you can see the new tables in database:

![new tables](new-tables.png)

A table for text documents has been added only as an extra _migration/database mapping_ example. This table will be mapped but no Business Rules will be defined for it.

### 5.1.4. Database Mapping and Hibernate
Microweb uses [Hibernate ](http://hibernate.org/) as its ORM. Hibernate is a very known Java ORM and Framework, and lots of tutorials about it exist online, so we won't bother to present this tool. We assume you know how to use Hibernate correctly, to read and understand this tutorial section.

After you created the database, and customized it, it's necessary to map the created tables, and to configure Hibernate.

Let's start configuring Hibernate, generating a configuration file, which will be placed at the project `resources` folder.

#### 5.1.4.1. Creating the hibernate.cfg.xml file and the Entity Mappings
Microweb has a default hibernate.cfg.xml file, which can be viewed [here](https://github.com/ultraschemer/microweb/blob/master/src/main/resources/_hibernate.cfg.xml).

Just copy this file and save it at `src/main/resources/hibernate.cfg.xml` file.

Edit it to:

```xml
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="connection.driver_class">org.postgresql.Driver</property>
        
        <!-- Set database access variables, here, to reflect your system: -->
        <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/microwebsample</property>
        <property name="hibernate.connection.username">microwebsample</property>
        <property name="hibernate.connection.password">microwebsample</property>
        <!--  -->

        <property name="hibernate.connection.pool_size">16</property>
        <property name="show_sql">true</property>
        <property name="dialect">org.hibernate.dialect.PostgreSQL93Dialect</property>

        <!-- The entity mappings: -->
        <mapping class="com.ultraschemer.microweb.entity.AccessToken"/>
        <mapping class="com.ultraschemer.microweb.entity.Configuration"/>
        <mapping class="com.ultraschemer.microweb.entity.EmailAddress"/>
        <mapping class="com.ultraschemer.microweb.entity.Person"/>
        <mapping class="com.ultraschemer.microweb.entity.PhoneNumber"/>
        <mapping class="com.ultraschemer.microweb.entity.Role"/>
        <mapping class="com.ultraschemer.microweb.entity.User"/>
        <mapping class="com.ultraschemer.microweb.entity.User_EmailAddress"/>
        <mapping class="com.ultraschemer.microweb.entity.User_PhoneNumber"/>
        <mapping class="com.ultraschemer.microweb.entity.User_Role"/>
        <mapping class="com.ultraschemer.microweb.entity.LockControl"/>
        <mapping class="com.ultraschemer.microweb.entity.Runtime"/>

        <!-- Project custom entity mappings: -->
        <mapping class="microweb.sample.entity.Image"/> 
        <mapping class="microweb.sample.entity.Document"/> 
        <mapping class="microweb.sample.entity.User_Image"/> 
        <mapping class="microweb.sample.entity.User_Document"/> 
    </session-factory>
</hibernate-configuration>
```

As you can see, above, all default Microweb tables have their default mappings already defined, and then four additional mappings are added to list.

These mappings are the tables created in the migrations, above.

The source code for these entity mappings are shown below:

File: `src/main/java/microweb/sample/entity/Image.java`

```java
package microweb.sample.entity;

import com.ultraschemer.microweb.persistence.Createable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "image")
public class Image extends Createable {
    @Column(name = "name")
    private String name;

    @Column(name = "base64data")
    private String base64data;

    @Column(name="owner_user_id")
    private UUID ownerUserId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase64data() {
        return base64data;
    }

    public void setBase64data(String base64data) {
        this.base64data = base64data;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
```

File: `src/main/java/microweb/sample/entity/Document.java`

```java

```

File: `src/main/java/microweb/sample/entity/User_Image.java`

```java
package microweb.sample.entity;

import com.ultraschemer.microweb.persistence.Timeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "document")
public class Document extends Timeable {
    @Column(name = "name")
    private String name;

    @Column(name = "contents")
    private String contents;

    @Column(name = "status")
    private String status;

    @Column(name="owner_user_id")
    private UUID ownerUserId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
```

File: `src/main/java/microweb/sample/entity/User_Document.java`

```java
package microweb.sample.entity;

import com.ultraschemer.microweb.persistence.Timeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "user__document")
public class User_Document extends Timeable {
    @Column(name = "alias")
    private String alias;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "user_id")
    private UUID userId;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
```

Some points must be considered, in the mappings above:

1. No boilerplate for __Loggable__, __Identifiable__, __Createable__ and __Timeable__ entities must be written: Just use inheritance from these classes, in the entity mappings, as it's shown in the classes __Image__, __Document__, __User_Image__ and __User_Document__, above.
2. All foreign keys must be explicitly declared, and they must be of __java.util.UUID__ type. Hibernate will deal with them transparently.
3. Table and column name mappings are enforced, since table naming conventions and Java classes name conventions differ between them.
4. All mappings are isolated in its own package, named __microweb.sample.entity__, in this project. Use this kind of package isolation to ensure __separation of concerns__, and to isolate the Data Model from the other layers of the system.

### 5.1.5. The Controller Layer and Routing Mapping

Database mapping is a __huge__ part of Microweb, not in complexity, but in importance. But, after understanding how the mapping works, how the migrations work and how to apply the concepts of __Timeable__ and __Createable__ (and concepts of __Identifiable__ and __Loggable__, by analogy), the next step is to start to structure the application, per se.

As can be seen in this sample, no system entry-point has been defined, and the main class, named __microweb.sample.App__ is just an empty class, with a _"Hello World"_ implementation.

Then, let's structure the application, creating the most relevant class in Microweb, the WebAppVerticle.

### 5.1.5.1. The web application entry-point

Let's change the project main class from its current state (_"Hello World"_) to a Web App verticle.

change the file `src/main/java/microweb/sample` from:

```java
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package microweb.sample;

public class App {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
    }
}

```

to:

```java
package microweb.sample;

import com.ultraschemer.microweb.controller.AuthorizationFilter;
import com.ultraschemer.microweb.controller.LoginController;
import com.ultraschemer.microweb.controller.LogoffController;
import com.ultraschemer.microweb.domain.RoleManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.WebAppVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;

// 1. Specialize WebAppVerticle:
public class App extends WebAppVerticle {
    static {
        // 2. Initialize default entity util here:
        EntityUtil.initialize();
    }

    @Override
    public void initialization() throws Exception {
        // 3. Verify the default user and the default role:
        UserManagement.initializeRoot();

        // 4. Initialize additional roles (if not using KeyCloak):
        RoleManagement.initializeDefault();

        // 5. Register authorization filter:
        registerFilter(new AuthorizationFilter());

        // 6. Register controllers:
        registerController(HttpMethod.POST, "/v0/login", new LoginController());
        registerController(HttpMethod.GET, "/v0/logoff", new LogoffController());
    }

    public static void main(String[] args) {
        // 7. Create an Application Vertx instance. Vert.x is, to Microweb, 
        // the entire application component container:
        Vertx vertx = Vertx.vertx();

        // 8. Deploy the WebAppVerticle:
        vertx.deployVerticle(new App());
    }
}

```

Then, remove all pre-created tests - file `src/test/java/microweb/sample`, changing from
```java
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package microweb.sample;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test public void testAppHasAGreeting() {
        App classUnderTest = new App();
        assertNotNull("app should have a greeting", classUnderTest.getGreeting());
    }
}
```

to:

```java
package microweb.sample;

public class AppTest {
}
```

With these modifications, a new Web REST application is built, with only two operations: login and logoff.

Let's run it, and, then, understand each point of such program.

Build and run the project - run from the project root directory:
```sh
$ ./gradlew build
$ ./gradlew run

...
...
Lots of output
...
...

--Thread[vert.x-eventloop-thread-0]--> [2020-06-26 20:31:11,017] DEBUG io.netty.buffer.ByteBufUtil.<clinit>(ByteBufUtil.java:93) 
-Dio.netty.threadLocalDirectBufferSize: 0

--Thread[vert.x-eventloop-thread-0]--> [2020-06-26 20:31:11,018] DEBUG io.netty.buffer.ByteBufUtil.<clinit>(ByteBufUtil.java:96) 
-Dio.netty.maxThreadLocalCharBufferSize: 16384

HTTP Server started on port 48080
```

Now the server is running at port 48080.

#### 5.1.5.2. Testing the default endpoints

Once the server is running, it's possible to test the two loaded routes.

Microweb, in the configuration presented in this sample, creates a standard user called __root__, whose password is __rootpasswordchangemenow__.

The examples, here, are performed using [curl](https://linux.die.net/man/1/curl), but you can use the REST client you prefer.

Performing login:
```sh
$ curl http://localhost:48080/v0/login -H 'Content-type: application/json' \
--data '{"name":"root","password":"rootpasswordchangemenow"}' -v

* STATE: INIT => CONNECT handle 0x600084ea8; line 1617 (connection #-5000)
* Added connection 0. The cache now contains 1 members
* STATE: CONNECT => WAITRESOLVE handle 0x600084ea8; line 1658 (connection #0)
*   Trying ::1:48080...
* STATE: WAITRESOLVE => WAITCONNECT handle 0x600084ea8; line 1737 (connection #0)
* Connected to localhost (::1) port 48080 (#0)
* STATE: WAITCONNECT => SENDPROTOCONNECT handle 0x600084ea8; line 1791 (connection #0)
* Marked for [keep alive]: HTTP default
* STATE: SENDPROTOCONNECT => DO handle 0x600084ea8; line 1811 (connection #0)
> POST /v0/login HTTP/1.1
> Host: localhost:48080
> User-Agent: curl/7.69.0
> Accept: */*
> Content-type: application/json
> Content-Length: 52
>
* upload completely sent off: 52 out of 52 bytes
* STATE: DO => DO_DONE handle 0x600084ea8; line 1882 (connection #0)
* STATE: DO_DONE => PERFORM handle 0x600084ea8; line 2003 (connection #0)
* Mark bundle as not supporting multiuse
* HTTP 1.1 or later with persistent connection
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8
< content-length: 49
<
* STATE: PERFORM => DONE handle 0x600084ea8; line 2193 (connection #0)
* multi_done
* Connection #0 to host localhost left intact
* Expire cleared (transfer 0x600084ea8)
{"accessToken":"p>?9H780ZLb%>$Ni/Qam","ttl":3600}
```

It's possible to see the login entry-point is REST. Microweb is made essentially to create REST APIs based on Json, so it's not a surprise the default __login__ endpoint be REST, Json based.

With the returned access token, it's possible to perform the only access restricted endpoint, logoff:

```sh
$ curl http://localhost:48080/v0/logoff -H 'Content-type: application/json' \
-H 'Authorization: Bearer p>?9H780ZLb%>$Ni/Qam' -v

* STATE: INIT => CONNECT handle 0x600084ef8; line 1617 (connection #-5000)
* Added connection 0. The cache now contains 1 members
* STATE: CONNECT => WAITRESOLVE handle 0x600084ef8; line 1658 (connection #0)
*   Trying ::1:48080...
* STATE: WAITRESOLVE => WAITCONNECT handle 0x600084ef8; line 1737 (connection #0)
* Connected to localhost (::1) port 48080 (#0)
* STATE: WAITCONNECT => SENDPROTOCONNECT handle 0x600084ef8; line 1791 (connection #0)
* Marked for [keep alive]: HTTP default
* STATE: SENDPROTOCONNECT => DO handle 0x600084ef8; line 1811 (connection #0)
> GET /v0/logoff HTTP/1.1
> Host: localhost:48080
> User-Agent: curl/7.69.0
> Accept: */*
> Content-type: application/json
> Authorization: Bearer p>?9H780ZLb%>$Ni/Qam
>
* STATE: DO => DO_DONE handle 0x600084ef8; line 1882 (connection #0)
* STATE: DO_DONE => PERFORM handle 0x600084ef8; line 2003 (connection #0)
* Mark bundle as not supporting multiuse
* HTTP 1.1 or later with persistent connection
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8
< content-length: 125
<
* STATE: PERFORM => DONE handle 0x600084ef8; line 2193 (connection #0)
* multi_done
* Connection #0 to host localhost left intact
* Expire cleared (transfer 0x600084ef8)
{"message":"User logoff has been successful.","code":"154840d6-edd4-4636-a2fb-a2c34080abd3","httpStatus":200,"stackTrace":""}
```

As you can see above, the access token is used in the __Authorization__ header, as the __Bearer__.

After logoff, if you try to perform this operation again, Microweb will unauthorize the call:

```sh
$  curl http://localhost:48080/v0/logoff -H "Content-type: application/json" \
-H "Authorization: Bearer p>?9H780ZLb%>$Ni/Qam" -v

* STATE: INIT => CONNECT handle 0x600084ef8; line 1617 (connection #-5000)
* Added connection 0. The cache now contains 1 members
* STATE: CONNECT => WAITRESOLVE handle 0x600084ef8; line 1658 (connection #0)
*   Trying ::1:48080...
* STATE: WAITRESOLVE => WAITCONNECT handle 0x600084ef8; line 1737 (connection #0)
* Connected to localhost (::1) port 48080 (#0)
* STATE: WAITCONNECT => SENDPROTOCONNECT handle 0x600084ef8; line 1791 (connection #0)
* Marked for [keep alive]: HTTP default
* STATE: SENDPROTOCONNECT => DO handle 0x600084ef8; line 1811 (connection #0)
> GET /v0/logoff HTTP/1.1
> Host: localhost:48080
> User-Agent: curl/7.69.0
> Accept: */*
> Content-type: application/json
> Authorization: Bearer p>?9H780ZLb%>$Ni/Qam
>
* STATE: DO => DO_DONE handle 0x600084ef8; line 1882 (connection #0)
* STATE: DO_DONE => PERFORM handle 0x600084ef8; line 2003 (connection #0)
* Mark bundle as not supporting multiuse
* HTTP 1.1 or later with persistent connection
< HTTP/1.1 401 Unauthorized
< Content-Type: application/json; encoding=utf-8
< content-length: 1895
<
* STATE: PERFORM => DONE handle 0x600084ef8; line 2193 (connection #0)
* multi_done
* Connection #0 to host localhost left intact
* Expire cleared (transfer 0x600084ef8)
{"message":"Acesso invÃ¡lido - nÃ£o autorizado a continuar.","code":"ERROR:f74b2759-4163-4b0f-a913-929d606af26a","httpStatus":401,"stackTrace":"..."}
```
_Observation: the unauthorization message is written in Portuguese. This is a minor bug, which will be corrected in future Microweb releases._

#### 5.1.5.3. Understanding all points in the App main class

The main App class implementation (class __com.microweb.sample.App__), as shown above, has eight commented points. These points are placed specially to be explained, and to show the architecture of a Microweb microserver.

Each comment point is presented below:

1. __Specialize WebAppVerticle__: Microweb provides a Standard Web Application class. This class is __WebAppVerticle__, which is a __Verticle__ (See Vert.x documentation about [Verticles](https://vertx.io/docs/vertx-core/java/#_verticles)). You can create how many Web Application Verticles you want, but all these Verticles will share a single database instance. Below this design choice is better explained.
2. __Initialize default entity util here__: `EntityUtil` is the Hibernate Database Session Factory __Singleton__ Microweb uses to configure and load __all of WebAppVerticles__ in a program or application. The WebAppVerticles aren't singletons, but the Database Instance, for the entire system, __really is__. And why? Because Microweb has been developed to __centralize users and permissions control in distributed systems__ and users' data rely in a __central database__, having no meaning to permit multiple databases in WebAppVerticles configuration and loading context. Furthermore, business rules are completely independent from Microweb architecture and structure, so you can create another database connections elsewhere, these connections being Singletons or not, and use these connections in the manner suitable to you, but __outside__ Microweb architectural structure. Always initialize `EntityUtil`, in a static scope, in your WebAppVerticle. You can call `EntityUtil.initialize()` multiple times, that only in the first effective call the database connection singleton will be configured. In all other attempts, the initialization will be skipped, since it has already been performed. There are optional parametes to `EntityUtil.initialize()`, so you don't need to be bound to the standard Hibernate configuration file.
3. __Verify the default user and the default role__: This sample doesn't use KeyCloak nor OpenId to centrally manage user data, so it's needed to create, at least, a single user to the system. If you provide alternative ways to create your users, this step can be skipped. This call create a user called __root__, whose password is __rootpasswordchangemenow__. It's strongly advised to no skip this step, if you are not using OpenID.
4. __Initialize additional roles (if not using KeyCloak)__: The existence of a user registered in database is not enough to run a WebAppVerticle. Default roles are needed to do so. This call generate such roles. These roles can be ignored in the majority of time, but, due design choices made in Microweb, these roles are necessary to start WebAppVerticles up. These roles can be used, later, to implement a custom permission control in the system.
5. __Register initialization filter__: Microweb supports registration of HTTP routes and filters. Without support of OpenID, the authorization is implemented as a filter, which must be registered __before__ the registration of all other filters and routes.
6. __Register controllers__: Microweb supports registration of Route controllers, which support all HTTP Methods supported by Vert.x Web, and Vert.x routing paths. No path Regexen are supported currently, but this support can be easily added in the future. Each route is vinculated to a Controller. In this sample, both registered routes (for Login and for Logoff) are linked to Standard Controllers provided by Microweb to deal with Login and Logoff calls. Later we'll see how to implement other types of Login/Logoff handling.
7. __Create the Application Vert.x instance__: Now we're seeing the application main function, creating a Vert.x instance to load the created WebAppVerticle.
8. __Deploy the WebAppVerticle__: Created the application Vert.x instance, it's time to deploy the verticle and start to run the application _per se_. Vert.x is the container used by Microweb, much alike [Spring Application Context container](https://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/beans.html), or J2EE Enterprise Java Beans container. But, Microweb doesn't use Inversion-of-Control, Dependency injection, or Entity Factory to provide software componentry. Microweb uses __Vert.x__ message passing to modularize systems internally - and Microweb integrates with external services through REST. All these techniques will be presented later.

Now, all steps to create a Microweb application have been presented. It's time to implement customized controllers, business rules and user interfaces.

### 5.1.6. Implementing custom Features: Controllers, Business Rules and Views

The best way to understand Microweb is to implement an entire feature, and them implement others in analog ways.

Let's create a static webroot folder, and structure a web application on it.

#### 5.1.6.1. Static webroot folder and static data

Every web application needs some static files, and with this sample, it's no different. Since Microweb is Vert.x, let's create a static folder, put basic HTML and CSS there

```Java
// Set this in the initialization() method of microweb.sample.App class:

// ...
// previous code already present
// ...

// 4. Initialize additional roles (if not using KeyCloak):
RoleManagement.initializeDefault();

// This is added to serve static files to project - All static files are
// to be stored at src/main/java/resources/webroot directory, which will be
// packed with the application Jar file
getRouter().route("/static/*").handler(StaticHandler.create());

// ...
// previous code already present
// ...
```

Create the `src/main/java/resources/webroot` directory, and create an `index.html` file in it, with these contents:

__File__ `src/main/java/resources/webroot/index.html`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    This is Microweb sample file!
</body>
</html>
```

And create its default CSS file:

__File__ `src/main/java/resources/webroot/index.css`

```css
body {
    font-family: Roboto, OpenSans, Arial, Helvetica, 'sans-serif'
}
```

If you start the application (using Gradle, as shown previously), you'll be able to load the home page at __http://localhost:48080/static__, address, as show below:

![static homepage](static-home-page.png)

At this moment, the implementation of `microweb.sample.App` class must be equals to:

```Java
package microweb.sample;

import com.ultraschemer.microweb.controller.AuthorizationFilter;
import com.ultraschemer.microweb.controller.LoginController;
import com.ultraschemer.microweb.controller.LogoffController;
import com.ultraschemer.microweb.domain.RoleManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.WebAppVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.StaticHandler;

// 1. Specialize WebAppVerticle:
public class App extends WebAppVerticle {
    static {
        // 2. Initialize default entity util here:
        EntityUtil.initialize();
    }

    @Override
    public void initialization() throws Exception {
        // 3. Verify the default user and the default role:
        UserManagement.initializeRoot();

        // 4. Initialize additional roles (if not using KeyCloak):
        RoleManagement.initializeDefault();

        // This is added to serve static files to project - All static files are
        // to be stored at src/main/java/resources/webroot directory, which will be
        // packed with the application Jar file
        getRouter().route("/static/*").handler(StaticHandler.create());

        // 5. Register authorization filter:
        registerFilter(new AuthorizationFilter());

        // 6. Register controllers:
        registerController(HttpMethod.POST, "/v0/login", new LoginController());
        registerController(HttpMethod.GET, "/v0/logoff", new LogoffController());
    }

    public static void main(String[] args) {
        // 7. Create the Application Vertx instance:
        Vertx vertx = Vertx.vertx();

        // 8. Deploy the WebAppVerticle:
        vertx.deployVerticle(new App());
    }
}
```

Now, we can start to implement a System login form.

#### 5.1.6.2. Home path, Home page with HTML GUI

The default User Login is a REST API. This is not suitable to human users. Let's create a standard GUI login interface and an application real homepage (not the static one, shown above). In the `initialization()` method, of `App` class, put this snippet after the login and logoff controllers registration:

```java
// Set this in the end of initialization() method of microweb.sample.App class:

// ...
// previous code already present
// ...

// Controllers used to manage User Login:
// L.1: Login default presentation:
registerController(HttpMethod.GET, "/v0/gui-user-login", new GuiUserLoginViewController());
// L.2: Login submission handling:
registerController(HttpMethod.POST, "/v0/gui-user-login", new GuiUserLoginProcessController());

// L.3:  Default system home page handling:
registerController(HttpMethod.GET, "/v0", new DefaultHomePageController());
registerController(HttpMethod.GET, "/", new DefaultHomePageController());
```

At this point it's important to reinforce a specific Vert.x routing registration characteristic: router registration is order sensitive. Vert.x evaluate routes sequencially, so if you register two controllers with conflicting routing paths, Vert.x will chose, __always__, the first registered controller. So, be careful with the order of your registrations, registering more specific routes __before__ more general ones.

Now, create the three controller classes, leave them empty. Put all of them in the package `microweb.sample.controller`, to enforce Separation of Concerns:

__File__ `src/main/java/microweb/sample/controller/GuiUserLoginViewController.java`

```java
package microweb.sample.controller;

import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class GuiUserLoginViewController extends SimpleController {
    public GuiUserLoginViewController() {
        super(500, "85c2c7d9-eab9-4b6e-9ebd-271966722124");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        throw new Exception("Unimplemented");
    }
}
```
__File__ `src/main/java/microweb/sample/controller/GuiUserLoginProcessController.java`

```java
package microweb.sample.controller;

import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class GuiUserLoginProcessController extends SimpleController {
    public GuiUserLoginProcessController() {
        super(500, "7f65217b-a95e-4b0c-8161-5ab116b49dea");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        throw new Exception("Unimplemented");
    }
}
```
__File__ `src/main/java/microweb/sample/controller/DefaultHomePageController.java`

```java
package microweb.sample.controller;

import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class DefaultHomePageController extends SimpleController {
    public DefaultHomePageController() {
        super(500, "a37c914b-f737-4a73-a226-7bd86baac8c3");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        throw new Exception("Unimplemented");
    }
}
```

Let's run the project and test it.

If you try to load some page, you'll get an Unauthorized error:

![unauthorized error](unauthorized-error.png)

It means that the `AuthorizationFilter` is working correctly. But, to use the system, we must have, at least, a home-page which doesn't need authorization to be loaded.

Then, let's specialize the Authorization filter, and release from authorization some pages.

Create a new `AuthorizationFilter` class file, in the `microweb.sample.controller` package:

__File__ `src/main/java/microweb/sample/controller/AuthorizationFilter.java`:

```java
package microweb.sample.controller;

public class AuthorizationFilter extends com.ultraschemer.microweb.controller.AuthorizationFilter {
    public AuthorizationFilter() {
        super();
        this.addUnfilteredPath("/");
        this.addUnfilteredPath("/v0");
    }
}
```

And let's edit the `App` class implementation, to use this new `AuthorizationFilter`:

__File__ `src/main/java/microweb/sample/App.java`:
```java
package microweb.sample;

import com.ultraschemer.microweb.controller.LoginController;
import com.ultraschemer.microweb.controller.LogoffController;
import com.ultraschemer.microweb.domain.RoleManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.WebAppVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.StaticHandler;

// The Authorization filter import has been changed here:
import microweb.sample.controller.AuthorizationFilter;

import microweb.sample.controller.DefaultHomePageController;
import microweb.sample.controller.GuiUserLoginProcessController;
import microweb.sample.controller.GuiUserLoginViewController;

// 1. Specialize WebAppVerticle:
public class App extends WebAppVerticle {
    static {
        // 2. Initialize default entity util here:
        EntityUtil.initialize();
    }

    @Override
    public void initialization() throws Exception {
        // 3. Verify the default user and the default role:
        UserManagement.initializeRoot();

        // 4. Initialize additional roles (if not using KeyCloak):
        RoleManagement.initializeDefault();

        // This is added to serve static files to project - All static files are
        // to be stored at src/main/java/resources/webroot directory, which will be
        // packed with the application Jar file
        getRouter().route("/static/*").handler(StaticHandler.create());

        // 5. Register authorization filter:
        registerFilter(new AuthorizationFilter());

        // 6. Register controllers:
        registerController(HttpMethod.POST, "/v0/login", new LoginController());
        registerController(HttpMethod.GET, "/v0/logoff", new LogoffController());

        // Controllers used to manage User Login:
        // L.1: Login default presentation:
        registerController(HttpMethod.GET, "/v0/gui-user-login", new GuiUserLoginViewController());
        // L.2: Login submission handling:
        registerController(HttpMethod.POST, "/v0/gui-user-login", new GuiUserLoginProcessController());

        // L.3:  Default system home page handling:
        registerController(HttpMethod.GET, "/v0", new DefaultHomePageController());
        registerController(HttpMethod.GET, "/", new DefaultHomePageController());
    }

    public static void main(String[] args) {
        // 7. Create the Application Vertx instance:
        Vertx vertx = Vertx.vertx();

        // 8. Deploy the WebAppVerticle:
        vertx.deployVerticle(new App());
    }
}
```

And, let's restart the service, after these modifications. Now, the __"/"__ and __"/v0"__ paths must be freely available, but yet returning an error, because they're not implemented:

![unimplemented](unimplemented.png)

Now, let's start editing the `DefaultHomePageController`, to provide a default home page for our project.

Change the original implementation of such class to:

__File__ `src/main/java/microweb/sample/controller/DefaultHomePageController.java`:
```Java
package microweb.sample.controller;

import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.view.FtlHelper;

public class DefaultHomePageController extends SimpleController {
    // C.1: Create a static instance of the default home page template variable, to store and cache it:
    private static Template homePageTemplate = null;

    // C.2: Initialize the template defined above, suitably:
    static {
        try {
            homePageTemplate = FtlHelper.getConfiguration().getTemplate("homePage.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    // C.3: Define the default controller constructor:
    public DefaultHomePageController() {
        super(500, "a37c914b-f737-4a73-a226-7bd86baac8c3");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        // C.4: In the controller evaluation routine, render the template:
        routingContext
                 .response()
                 .putHeader("Content-type", "text/html")
                 .end(FtlHelper.processToString(homePageTemplate, null));
    }
}
```

The code above depends on a helper class to configure the [FreeMarker](https://freemarker.apache.org/) template library, which is already packaged with Microweb.

Create the next class, to load configurations for FreeMarker:

__File__ `src/main/java/microweb/sample/view/FtlHelper.java`:
```java
package microweb.sample.view;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.StringWriter;

public abstract class FtlHelper {
    private static final Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);

    // Perform the entire configuration here, similarly to what is presented in
    // FreeMarker tutorial, at https://freemarker.apache.org/docs/pgui_quickstart_createconfiguration.html:
    static {
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setFallbackOnNullLoopVariable(false);

        //
        // Templates will be loaded from Classpath, to turn the project self-contained and packable
        //
        configuration.setClassForTemplateLoading(FtlHelper.class, "/views");
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    // Just a helper to process templates to string easily:
    public static String processToString(Template tpl, Object dataModel) throws Exception {
        StringWriter writer = new StringWriter();
        tpl.process(dataModel, writer);
        return writer.toString();
    }
}
```

Then, create the next template file, in the resources path:
__File__ `src/main/java/resources/views/homePage.ftl`:
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    This is Microweb generated Home Page!
</body>
</html>
```

After all of these, we have a full Controller->View structure defined for the application homepage, and it can be loaded correctly, after recompiling the project and reloading the home-page:

![loaded home page](home-page.png)

Now let's explain all points commented in the code of `DefaultHomePageController`, above.

* __C.1: Create a static instance of the default home page template variable, to store and cache it:__ It's necessary to define the FreeMarker templates to be used as View for this Controller. FreeMarker template instantiation is considered resource consuming, so creating such templates statically optimizes the code and this is considered the correct fashion to use these templates.
* __C.2: Initialize the template defined above, suitably:__ Once declared, the template must be instantiated. Instantiate them statically, too. These templates __must__ be initialized. Any error here jeopardize the code, and must be solved. A not loaded template due errors is completely useless.
* __C.3: Define the default controller constructor:__ The controller constructor overrides the superclass constructor. The first variable of __super__ constructor is the HTTP Status error to be presented if any error processing the controller occurs, and it's not handled. Here it will return a __500 Internal Server Error__ status code, in the case of a unknown unhandled exception happening. The second variable is random UUID, to identify the Controller where the unhandled exception ocurred. We'll present better about standard exception handling when implementing business rules.
* __C.4: In the controller evaluation routine, render the template:__ The template rendering uses a simple helper provided by `FltHelper` class, but the rendering code can be considered straightforward.

And let's explain how the new AuthorizationFilter works. By default, AuthorizationFilter forbids the execution of any route, if an authorization code isn't provided, with the very exception of __"/v0/login"__ endpoint, regardless of calling method.

This condition is far from suitable for a working REST/WEB microservice. So, we can specialize AuthorizationFilter (which is a kind of controller) and add new unrestricted endpoints. This is what we do, with the new `AuthorizationFilter` we implement above. Each call of `addUnfilteredPath`, which is a __protected__ method, releases the path from Authorization Filter restriction.

#### 5.1.6.2. User login and logoff, with HTML GUI

Now we created a working home-page to the application, we can create a login form to enter in the system. The home-page will continue clean, and static, only with a link to the Login form:

__File__ `src/main/java/microweb/sample/controller/AuthorizationFilter.java`:

```java
package microweb.sample.controller;

public class AuthorizationFilter extends com.ultraschemer.microweb.controller.AuthorizationFilter {
    public AuthorizationFilter() {
        super();
        this.addUnfilteredPath("/");
        this.addUnfilteredPath("/v0");

        // Add this, to release the login form to any unauthenticated user:
        this.addUnfilteredPath("/v0/gui-user-login");
    }
}

```

__File__ `src/main/resources/views/homePage.ftl`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    <p>This is Microweb generated Home Page!</p>
    <p> Login <a href="/v0/gui-user-login">here</a>.</p>
</body>
</html>
```

__File__ `src/main/java/microweb/sample/controller/GuiUserLoginViewController.java`

```java
package microweb.sample.controller;

import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.view.FtlHelper;

import java.util.HashMap;
import java.util.Map;

public class GuiUserLoginViewController extends SimpleController {
    private static Template loginFormTemplate;

    static {
        try {
            loginFormTemplate = FtlHelper.getConfiguration().getTemplate("loginForm.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    public GuiUserLoginViewController() {
        super(500, "85c2c7d9-eab9-4b6e-9ebd-271966722124");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        Map<String, Object> loginMessageData = new HashMap<>();
        loginMessageData.put("error", false);
        routingContext
                .response()
                .putHeader("Content-type", "text/html")
                .end(FtlHelper.processToString(loginFormTemplate, loginMessageData));
    }
}
```

__File__ `src/main/resources/views/loginForm.ftl`
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    <p>Perform Login to Microweb Sample:</p>
    <form method="post">
        <#if error >
            <p><strong>${errorMessage}</strong></p>
        </#if>
        <p>Name: <input type="text" name="name"/></p>
        <p>Password: <input type="password" name="password"/></p>
        <p><input type="submit" name="log in"/></p>
    </form>
</body>
</html>
```

The files above define the form to perform login, but we need a controller to process Login, _per se_, and the internal home page, after login.

The controller to evaluate login is next:

__File__ `src/main/java/microweb/sample/controller/GuiUserLoginProcessController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.bean.AuthenticationData;
import com.ultraschemer.microweb.domain.bean.AuthorizationData;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.bean.UserLoginData;
import microweb.sample.view.FtlHelper;

import java.util.HashMap;
import java.util.Map;

public class GuiUserLoginProcessController extends SimpleController {
    private static Template homePageTemplate = null;
    private static Template loginFormTemplate = null;

    static {
        try {
            homePageTemplate = FtlHelper.getConfiguration().getTemplate("homePage.ftl");
            loginFormTemplate = FtlHelper.getConfiguration().getTemplate("loginForm.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    public GuiUserLoginProcessController() {
        super(500, "7f65217b-a95e-4b0c-8161-5ab116b49dea");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = routingContext.request();

        try {
            // D.1: Validate input data:
            UserLoginData userLoginData =
                    new UserLoginData(request.getFormAttribute("name"), request.getFormAttribute("password"));
            Validator.ensure(userLoginData);

            // D.2: Transform data and call business rule to perform login:
            AuthenticationData authenticationData = new AuthenticationData();
            authenticationData.setName(userLoginData.getName());
            authenticationData.setPassword(userLoginData.getPassword());

            // D.3: Business call:
            AuthorizationData authorizationData = AuthManagement.authenticate(authenticationData);

            // D.4: Success - evaluate returned values:
            response
                    // Set authorization cookie:
                    .putHeader("Set-Cookie", "Microweb-Access-Token=" + authorizationData.getAccessToken())
                    // Just add an informative cookie, with token TTL (this cookie isn't used to enforce anything):
                    .putHeader("Set-Cookie", "Access-Token-TTL=" + authorizationData.getTtl())
                    // Render template:
                    .putHeader("Content-type", "text/html")
                    .setStatusCode(200)
                    .end(FtlHelper.processToString(homePageTemplate, null));
        } catch(StandardException e) {
            // D.5: Business call failure, return to login form, but with error message:
            Map<String, Object> loginMessageData = new HashMap<>();
            loginMessageData.put("errorMessage", e.getLocalizedMessage());
            loginMessageData.put("error", true);
            response.putHeader("Content-type", "text/html")
                    .setStatusCode(401)
                    .end(FtlHelper.processToString(loginFormTemplate, loginMessageData));
        }
    }
}
```

And the same controller performs a validation, using this bean:
__File__ `src/main/java/microweb/sample/domain/bean/UserLoginData.java`:
```java
package microweb.sample.domain.bean;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.io.Serializable;

public class UserLoginData implements Serializable {
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String password;

    public UserLoginData(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

Data input validation is quintessentially a __controller__ responsibility, and Input validation errors, by default, raise a __400 Bad Request__ status.

Microweb packs a library called __OVal__, used to perform data validation.

The __OVal__ library packaged with Microweb is somewhat old, available at [this link](https://sourceforge.net/projects/oval/). To update Microweb to a more modern version of Oval is planned, but, at this moment, it's not a priority.

Let's follow the comments pointed at `GuiUserLoginProcessController` implementation, above:

* __D.1: Validate input data__: In a well structured controller, the input data must be suitably validated. Here, the controller instantiates a object enriched with some __OVal__ validation annotations, and then, on this object, is called a method called `Validator.ensure`. The `Validator` class verifies given object data and confront them with the __OVal__ validation annotations, thus validating the object. If a validation error occurs, a exception is raised. This exception is of type `ValidationException`, which is a specialization of `StandardException`. It can be seen that validation occurs inside a `try { ... } catch (...) { ... }` block, which is the correct way to perform validations in Microweb. Validation exceptions can receive specific exception handling, implemented by the developer.
* __D.2: Transform data and call business rule to perform login__: After data is validated, it must be used by some Business Rule routine. If you're reusing some previous implemented business rule, then data must be transformed in a way this predefined business rule can use it. In this case, the `UserLoginData` is transformed to `AuthenticationData`, so the method `AuthManagement.authenticate` can be used here.
* __D.3: Business call__: At this moment, a business call, from __Domain__ layer is called. No business logic can be in the controller, everything is in the business class.
* __D.4: Success - evaluate returned values__: Microweb assumes a successful business call will return, with a value or not. If the business call returns, then it's successful and its return data must be formatted to user. In this case, the authorization token is saved as a Cookie, and the user is sent to a logged home page.
* __D.5: Business call failure, return to login form, but with error message__: In the case of a business call failure, or any other failure in the controller scope, a specific error message must be formatted to the user. Here, the login form is shown again to the user, receiving the error message. It can be seen that a variable called `loginMessageData` is created to feed login form template. This variable is the FreeMarker template data model, akin to the .Net MVC View-Model, if you're used to this Framework.

If you build the project, you'll see a simple usable form login for the application.

The home-page must show if the user is logged or not. Change the  `executeEvaluation()` method from `microweb.sample.controller.DefaultHomePageController` class to:

__File__ `src/main/java/microweb/sample/controller/DefaultHomePageController.java`, method `DefaultHomePageController.executeEvaluation`:

```java
    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        // Define homepage data model:
        Map<String, Object> homepageDataRoot = new HashMap<>();

        // Load user cookie:
        HttpServerRequest request = routingContext.request();
        Cookie authorizationToken = request.getCookie("Microweb-Access-Token");

        // Populate Homepage data model:
        if(authorizationToken != null) {
            // Get user data:
            User u = AuthManagement.authorize(authorizationToken.getValue());
            homepageDataRoot.put("logged", true);
            homepageDataRoot.put("user", u);
        } else {
            homepageDataRoot.put("logged", false);
        }

        // C.4: In the controller evaluation routine, render the template:
        routingContext
                 .response()
                 .putHeader("Content-type", "text/html")
                 .end(FtlHelper.processToString(homePageTemplate, homepageDataRoot));
    }
```

Change the `executeEvaluation()` method from `microweb.sample.controller.GuiUserLoginProcessController` to:

__File__ `src/main/java/microweb/sample/controller/GuiUserLoginProcessController.java`, method `GuiUserLoginProcessController.executeEvaluation` :

```java
@Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = routingContext.request();

        try {
            // D.1: Validate input data:
            UserLoginData userLoginData =
                    new UserLoginData(request.getFormAttribute("name"), request.getFormAttribute("password"));
            Validator.ensure(userLoginData);

            // D.2: Transform data and call business rule to perform login:
            AuthenticationData authenticationData = new AuthenticationData();
            authenticationData.setName(userLoginData.getName());
            authenticationData.setPassword(userLoginData.getPassword());

            // D.3: Business call:
            AuthorizationData authorizationData = AuthManagement.authenticate(authenticationData);

            // Populate view:
            Map<String, Object> homepageDataRoot = new HashMap<>();
            User u = AuthManagement.authorize(authorizationData.getAccessToken());
            homepageDataRoot.put("logged", true);
            homepageDataRoot.put("user", u);

            // D.4: Success - evaluate returned values:
            response
                    // Set authorization cookie:
                    .putHeader("Set-Cookie", "Microweb-Access-Token=" + authorizationData.getAccessToken())
                    // Render template:
                    .putHeader("Content-type", "text/html")
                    .setStatusCode(200)
                    .end(FtlHelper.processToString(homePageTemplate, homepageDataRoot));
        } catch(StandardException e) {
            // D.5: Business call failure, return to login form, but with error message:
            Map<String, Object> loginMessageData = new HashMap<>();
            loginMessageData.put("errorMessage", e.getLocalizedMessage());
            loginMessageData.put("error", true);
            response.putHeader("Content-type", "text/html")
                    .setStatusCode(401)
                    .end(FtlHelper.processToString(loginFormTemplate, loginMessageData));
        }
    }
```

And change the home page template to:

__File__ `src/main/resources/views/homePage.ftl`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    <p>This is Microweb generated Home Page!</p>
    <#if logged>
        <p>Welcome <strong>${user.name}</strong>!</p>
        <p>Logoff <a href="/v0/gui-user-logoff">here</a>.</p>
    <#else>
        <p>Login <a href="/v0/gui-user-login">here</a>.</p>
    </#if>
</body>
</html>
```

Now you can see which user is logged, and you have a link to a logoff call. This logoff call won't work, since its controller isn't implemented. Let's implement the user logoff.

We start defining the logoff route in the `App.initialization()` method:

__File__ `src/main/java/microweb/sample/App.java`, __method__ `App.initialization()`:
```java
    // Add the next line before the registration of "/" and "/v0" routes,   
    // in App.initialization() method:
    registerController(HttpMethod.GET, "/v0/gui-user-logoff", new GuiUserLogoffProcessController());
```

This route is restricted by `AuthorizationFilter`, so it's only accessible by logged authorized users.

Now, create class `GuiUserLogoffProcessController`, as shown below:

__File__ `src/main/java/microweb/sample/controller/GuiUserLogoffProcessController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.view.FtlHelper;

import java.util.HashMap;
import java.util.Map;

public class GuiUserLogoffProcessController extends SimpleController {
    private static Template homePageTemplate = null;

    static {
        try {
            homePageTemplate = FtlHelper.getConfiguration().getTemplate("homePage.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    public GuiUserLogoffProcessController() {
        super(500, "eb474551-42d5-4452-be4f-4875d525b993");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        String token = routingContext.getCookie("Microweb-Access-Token").getValue();

        // Perform logoff here:
        AuthManagement.unauthorize(token);
        Map<String, Object> homepageDataRoot = new HashMap<>();
        homepageDataRoot.put("logged", false);

        // Render home page again:
        routingContext
                .response()
                .putHeader("Content-type", "text/html")
                .end(FtlHelper.processToString(homePageTemplate, homepageDataRoot));

    }
}
```

Now a complete cycle of login and logoff can be performed in this simple system.

#### 5.1.6.3. A customized business rule

So far, we learned how to create system database, Entity mapping, define Views and Controllers, and define end-points for REST Routes. But nothing about how to define business rules have been told. This section is to close such gap.

If we return to the project scope (Section __5.1.1. Project Objectives and Technical Requirements__), it's told, there:
> Each user can store images on his/her accounts.
So, let's implement such feature in the system, to give an example of how a Business Rule should be implemented in Microweb.

It has been told, too, Microweb doesn't enforce __fat__ or __thin__ controllers, nor __fat__ or __thin__ models. The only advice to be given when implementing business rules is to isolate them in its own package, and this package must be __completely independent__ from Microweb archiecture and structure. If this package will be called model, business, controller or anything else is outside the scope of Microweb and this tutorial.

To use a neutral name, we'll create a package called `microweb.sample.domain` and implement all business rules there. Any external dependency needed from Microweb structure will be injected, so we can assure the independence of such package from Microweb structure.

To simplify the development, and since we don't need an alternative database for our Data Models, we'll use the Global Database Connection facility (which is just a simple wrapper over Hibernate SessionFactory) from Microweb, to persist data. This facility is called EntityUtil, but it won't be injected entirely, just Hibernate SessionFactory will be injected. The injection of such factory ensures an independent business layer.

Another approach is to use `EntityUtil` directly - but this approach turn the Business Rules dependent to Microweb Structure. You can use such approach if you want to use the business rules you're developing only with Microweb - since Microweb will become a dependency to your business layer package. 

As a rule of thumb, business rules can be developed procedurally, since the algorithm, in business layer is more important than the structure. A little Object-Orientation will be used, just to store de injected dependencies.

You can read the code from `com.ultraschemer.microweb.domain.AuthManagement` class as an example of a purely procedural Business Layer implementation, which uses `EntityUtil` directly, if you want a more coupled approach of your system with Microweb.

_Obs.: The Data Models we defined use the classes __Timeable__, __Createable__, __Identifiable__ and __Loggable__. They're integral to Microweb but they're apart from the Framework Runtime infra-structure. It's planned to turn these database modelling facilities a external library, and to make such library compatible with a series of databases, so this dependency is acceptable in this example. If you __really__ want Business Layer independence from Microweb, just use Hibernate or JPA plain mappings, and avoid using Microweb customized database mappings._

Let's start, creating a default Business Layer class, to receive Microweb dependencies:

__File__: `src/main/java/microweb/sample/domain/StandardDomain.java`:
```java
package microweb.sample.domain;

import io.vertx.core.Vertx;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

// E.1: Abstract class to create business rules:
public abstract class StandardDomain {
    // E.2: Private dependencies, which shouldn't be updated, once assigned:
    private SessionFactory factory;
    private Vertx vertx;

    // E.3: Facility to call sessions and transactions:
    public Session openTransactionSession() {
        Session s = factory.openSession();
        s.beginTransaction();

        return s;
    }

    // E.4: Dependencies getters:
    protected SessionFactory getSessionFactory() {
        return factory;
    }

    // E.4: Dependencies getters:
    protected Vertx getVertx() {
        return vertx;
    }

    // E.5: Business class default constructor:
    public StandardDomain(SessionFactory factory, Vertx vertx) {
        this.factory = factory;
        this.vertx = vertx;
    }
}
```

The class above will be used to all Business classes, and they'll receive a Vertx and a SessionFactory instance to perform Asynchoronous operations and Database operations, respectively.

Now, let's structure our first business class - that dealing with image storing and retrieving. The most important features of such class is to define a CRUD for Images, and to link them to users, under the owner resposibility:

__File__ `src/main/java/microweb/sample/domain/ImageManagement.java`:
```java
package microweb.sample.domain;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import io.vertx.core.Vertx;
import microweb.sample.entity.Image;
import org.hibernate.SessionFactory;

import java.util.UUID;

public class ImageManagement extends StandardDomain {
    public ImageManagement(SessionFactory factory, Vertx vertx) {
        super(factory, vertx);
    }

    public UUID save(User user, String imageFileName, String imageName) {
        // Implement image saving routines here.
        return null;
    }

    public Image read(User user, UUID imageId) {
        // Implement image reading routines here.
        return null;
    }

    public static byte[] base64StrToBin(String base64Str) {
        // Helper to implement conversion of image Base64 String to binary
        return null;
    }

    public void linkToUser(User owner, UUID imageId, UUID userId) throws StandardException {
        // Implement user->image linking here.
    }
}
```

To use the business class above, the user need to know other users, and maybe create other users. Fortunately, these business rules are all provided by Microweb in the box. It's important to remember the most important Microweb use-case: user management and resource permission, so it's natural Microweb provides such faciltiies.

The first business function to define is `ImageManagement.save`, to populate our database with images.

__File__ `src/main/java/microweb/sample/domain/ImageManagement.java`, method `ImageManagement.save()`:
```java
    public void save(ImageRegistrationData imageRegistrationData, BiConsumer<UUID, StandardException> resultHandler) {
        // A.1: Assure the input parameter is respecting its contract:
        try {
            Validator.ensure(imageRegistrationData, 500);
        } catch(StandardException e) {
            resultHandler.accept(null, e);
        }

        // A.2: Isolate the entire operation in a new thread, since it can be time and resource consuming:
        new Thread(() -> {
            try {
                // A.3: Read file:
                File file = new File(imageFileName);
                byte[] fEncoded = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));

                // A.4: Convert it to String:
                String base64contents = new String(fEncoded, StandardCharsets.US_ASCII);

                try(Session session = this.openTransactionSession()) {
                    // A.5: Save image in database
                    Image img = new Image();
                    img.setBase64data(base64contents);
                    img.setName(imageRegistrationData.getName());
                    img.setOwnerUserId(imageRegistrationData.getUserId());
                    session.persist(img);

                    // A.6: Commit saved data:
                    session.getTransaction().commit();

                    // A.7: Finish operation:
                    resultHandler.accept(img.getId(), null);
                }
            } catch(Exception e) {
                resultHandler.accept(null, new ImageManagementSaveException("Unable to persist image to user: ", e));
            }
        }).start();
    }
```

It can be seen, above, that the initial method signature change from a synchronous one to asynchronous. File reading and Base64 conversion can be a slow operation, so an asynchronous approach has been chosen.

Other observation is that the input parameters changed from a list of _quasi_-primitive values to a Bean, with validation annotations. This change ocurred because it's easier to implement contract-validation in this way. Formerly, Microweb supported parameter contracts, enforced with AspectJ, but this approach has ben abandoned, since the explicit activation of `Validator.ensure` seemed easier to understand and easier to maintain.

The class `ImageRegistrationData` is:

__File__ `src/main/java/microweb/sample/domain/bean/ImageRegistrationData.java`:
```java
package microweb.sample.domain.bean;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class ImageRegistrationData implements Serializable {
    @NotNull
    UUID userId;

    @NotNull
    @NotEmpty
    String imageFileName;

    @NotNull
    @NotEmpty
    String name;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

At this point, it's important to note that it's used a new Thread to release the processing costs of Image conversion to string and saving from main Microweb structure. This approach is acceptable because the two most sensitive components of Microweb Infra-Structure (Vert.X and Hibernate) are strongly thread-safe. Otherwise, locks and semaphores would be needed.

_Obs.: No unit testing has been described in this README, but to develop Business Classes without proper unit-testing coverage is a __bad practice__. You'll receive the link of this sample, and some unit tests can be present on it, because they're being used, but not described here, since this isn't a tutorial about unit testing._

_Obs.: To create Threads on fly isn't the best way to perform asynchronous operations in a Vert.X enabled environment, because it doesn't control Thread generation, and can provoke processing overloading. Vert.X has facilities to overcome this. But this isn't a Vert.X tutorial, so this approach isn't presented here, and hence outside the scope of this tutorial._

Let's understand, point by point, all the details of the implemented Business Rule. The other implemented business rules won't be explained in so much detail, but they can be understood by analogy:

* __A.1: Assure the input parameter is respecting its contract__: We defined a class to receive the image data to be saved. This class has been annotated using OVal, and a Contract has been defined to it. The validation, at this point, raises an HTTP 500 error, since it's not validation user input data, but internal data exchange in the system. It's considered standard, by Microweb, to raise HTTP 500 errors in Business Rules validations.
* __A.2: Isolate the entire operation in a new thread, since it can be time and resource consuming__: The image to be saved is a binary file, which can be really big. To respect the timing constraints of Vert.X Web, and consequently, of Microweb, it has been chosen to isolate the entire business rule in a parallel thread, and process input data asynchronously.
* __A.3: Read file__: The input image is being provided as a File. It can be a temporary or a definitive file, but a file, anyway. This approach is chosen because Vert.X Web register file uploads as registered files in filesystem. No genericity is lost.
* __A.4: Convert it to String__: It has been chosen, to maintain the sample simple, to store images in database as Base64 Strings. This is a __very bad approach__, since there are lots of efficient distributed filesystems to be used to store binary data. But, in this sample, it's acceptable.
* __A.5: Save image in database__: Once the `Image` object is populated, it is persisted, using Hibernate. To enable Microweb logging facilities, always insert and update data using the `Session.persist` method, from Hibernate. Block updates using HQL skip Microweb logging facilities.
* __A.6: Commit saved data__: Necessary step to ensure data persistence.
* __A.7: Finish operation__: Call the function consumer, to asynchronously finish the operation.

Let's finish the implementation of Image business rules, implementing all business methods. Explanations about each method can be found in the code.

The finished and complete implementation of `ImageManagement` is:

__File__ `src/main/java/microweb/sample/domain/ImageManagement.java`:
```java
package microweb.sample.domain;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.Identifiable;
import com.ultraschemer.microweb.validation.Validator;
import io.vertx.core.Vertx;
import microweb.sample.domain.bean.ImageListingData;
import microweb.sample.domain.bean.ImageRegistrationData;
import microweb.sample.domain.error.*;
import microweb.sample.entity.Image;
import microweb.sample.entity.User_Image;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.PersistenceException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ImageManagement extends StandardDomain {
    public ImageManagement(SessionFactory factory, Vertx vertx) {
        super(factory, vertx);
    }

    public void save(ImageRegistrationData imageRegistrationData, BiConsumer<UUID, StandardException> resultHandler) {
        // A.1: Assure the input parameter is respecting its contract:
        try {
            Validator.ensure(imageRegistrationData, 500);
        } catch(StandardException e) {
            resultHandler.accept(null, e);
        }

        // A.2: Isolate the entire operation in a new thread, since it can be time and resource consuming:
        new Thread(() -> {
            try {
                // A.3: Read file:
                File file = new File(imageRegistrationData.getImageFileName());
                byte[] fEncoded = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));

                // A.4: Convert it to String:
                String base64contents = new String(fEncoded, StandardCharsets.US_ASCII);

                try(Session session = openTransactionSession()) {
                    // A.5: Save image in database
                    Image img = new Image();
                    img.setBase64data(base64contents);
                    img.setName(imageRegistrationData.getName());
                    img.setOwnerUserId(imageRegistrationData.getUserId());
                    session.persist(img);

                    // A.6: Commit saved data:
                    session.getTransaction().commit();

                    // A.7: Finish operation:
                    resultHandler.accept(img.getId(), null);
                }
            } catch(Exception e) {
                resultHandler.accept(null, new ImageManagementSaveException("Unable to persist image to user." , e));
            }
        }).start();
    }

    // List images from user:
    public List<ImageListingData> list(User user) throws StandardException {
        try(Session session = openTransactionSession()) {
            List<ImageListingData> res = new LinkedList<>();

            List<User_Image> accessibleImageList =
                    session.createQuery("from User_Image where userId = :uid", User_Image.class).list();
            HashMap<UUID, User_Image> userImageMap = new HashMap<>();
            accessibleImageList.forEach((i) -> userImageMap.put(i.getImageId(), i));

            List<Image> allImages;
            if(accessibleImageList.size() > 0) {
                allImages = session.createQuery("from Image where id in :iid or ownerUserId = :oid order by ownerUserId", Image.class)
                        .setParameterList("iid", accessibleImageList.stream().map(User_Image::getImageId).collect(Collectors.toList()))
                        .setParameter("oid", user.getId())
                        .list();
            } else {
                allImages = session.createQuery("from Image where ownerUserId = :oid order by ownerUserId", Image.class)
                        .setParameter("oid", user.getId())
                        .list();
            }

            UUID ownerUserId = null;
            String ownerUserName = null;
            for(Image i: allImages) {
                if(!i.getOwnerUserId().equals(ownerUserId)) {
                    User u = session.createQuery("from User where id = :uid", User.class)
                            .setParameter("uid", i.getOwnerUserId())
                            .getSingleResult();
                    ownerUserId = u.getId();
                    ownerUserName = u.getName();
                }

                ImageListingData imageListingData = new ImageListingData();
                imageListingData.setId(i.getId());
                imageListingData.setName(i.getName());
                imageListingData.setOwnerId(ownerUserId);
                imageListingData.setOwnerName(ownerUserName);
                imageListingData.setCreatedAt(i.getCreatedAt());
                if(userImageMap.containsKey(i.getId())) {
                    User_Image ui = userImageMap.get(i.getId());
                    imageListingData.setAlias(ui.getAlias());
                }
                res.add(imageListingData);
            }

            // Set return data in creation order:
            res.sort(Comparator.comparing(ImageListingData::getCreatedAt));

            return res;
        } catch(PersistenceException pe) {
            throw new ImageManagementListingException("Unable to list images.", pe);
        }
    }

    // Read, synchronously the Image object:
    public Image read(User user, UUID imageId) throws StandardException {
        // Implement image reading routines here.
        try (Session session = openTransactionSession()){
            Image image = session.createQuery("from Image where id = :iid", Image.class)
                    .setParameter("iid", imageId)
                    .getSingleResult();

            if(!image.getOwnerUserId().equals(imageId)) {
                // Verify if the user has access to such image:
                List<User_Image> userImageList = session.createQuery("from User_Image where imageId = :iid and userId = :uid",
                        User_Image.class).setParameter("iid", imageId).setParameter("uid", user.getId())
                        .list();
                if(userImageList.size() == 0) {
                    throw new ImageManagementReadNotPermittedException("User has no permission to read such image.");
                }
            }
            return image;
        } catch(Exception e) {
            throw new ImageManagementReadException("Unable to read image.", e);
        }
    }

    // Link a user to an image, so that user can see the image on his/her user interface.
    public void linkToUser(User owner, UUID imageId, UUID userId, String imageAlias) throws StandardException {
        try (Session session = openTransactionSession()){
            Image img = session.createQuery("from Image where id = :iid and ownerUserId = :oid", Image.class)
                    .setParameter("iid", imageId)
                    .setParameter("oid", owner.getId())
                    .getSingleResult();
            User_Image userImage = new User_Image();
            userImage.setUserId(userId);
            userImage.setImageId(img.getId());
            userImage.setAlias(imageAlias);
            session.persist(userImage);
            session.getTransaction().commit();
        } catch(Exception e) {
            throw new ImageManagementImageUserLinkingException(e.getLocalizedMessage(), e);
        }
    }

    // Read and decode image data asynchronously.
    public void readAndDecode(User user, UUID imageId, TriConsumer<Image, byte[], StandardException> resultHandler) {
        new Thread(() -> {
            try {
                Image img = read(user, imageId);
                byte []imgByteRepresentation = Base64.getDecoder().decode(img.getBase64data());
                resultHandler.accept(img, imgByteRepresentation, null);
            } catch(StandardException e) {
                resultHandler.accept(null, null, e);
            } catch(Exception e) {
                resultHandler.accept(null, null, new ImageManagementReadException(e.getLocalizedMessage(), e));
            }
        }).start();
    }
}
```

The newly created exceptions for this Business class are:

__File__ `src/main/java/microweb/sample/domain/error/ImageManagementSaveException.java`:
```java
package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementSaveException extends StandardException {
    public ImageManagementSaveException(String message, Exception cause) {
        super("9880d7d9-9496-4324-a605-f8d19ac3788d", 500, message, cause);
    }
}
```

__File__ `src/main/java/microweb/sample/domain/error/ImageManagementReadException.java`:
```java
package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementReadException extends StandardException {
    public ImageManagementReadException(String message, Exception cause) {
        super("efa3115f-c742-410c-a908-5eb2bda6de24", 500, message, cause);
    }
}
```

__File__ `src/main/java/microweb/sample/domain/error/ImageManagementReadNotPermittedException.java`:
```java
package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementReadNotPermittedException extends StandardException {
    public ImageManagementReadNotPermittedException(String message) {
        super("b2d710c4-95d8-4f43-9f13-bf1810f36f36", 500, message);
    }
}
```

__File__ `src/main/java/microweb/sample/domain/error/ImageManagementImageUserLinkingException.java`:
```java
package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementImageUserLinkingException extends StandardException {
    public ImageManagementImageUserLinkingException(String message, Exception cause) {
        super("a2baffbb-149c-4f99-8b1c-85e500798f7c", 500, message, cause);
    }
}

```

__File__ `src/main/java/microweb/sample/domain/error/ImageManagementListingException.java`:
```java
package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementListingException extends StandardException {
    public ImageManagementListingException(String message, Throwable cause) {
        super("b751fabe-7134-4b80-95f3-94cd5ed01b07", 500, message, cause);
    }
}
```

And the listing image bean is:
__File__ `src/main/java/microweb/sample/domain/error/ImageManagementListingException.java`:
```java
package microweb.sample.domain.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class ImageListingData implements Serializable {
    private UUID id;
    private String name;
    private String alias;
    private UUID ownerId;
    private String ownerName;
    private Date createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
```

As a final observation, you can see the methods `ImageManagement.linkToUser`, `ImageManagement.list` and `ImageManagement.read`, which are implemented in __synchronous__ fashion. The conversion of string data to binary, from method `ImageManagement.read`, can be specially slow, so an asynchronous version of it, already performing the byte conversion, is given.

Then, we'll, now, create the interface for user management and image management, using this new defined Business rule and the current existent user management business rules Microweb provides.

#### 5.1.6.4. Image management user interface: Linking Business Rules to View, through Controllers

Since this is a tutorial about Microweb and this section is about how to integrate business layers and views, we'll develop the _simplest_ useful view to add, list, and present images in such system, finishing the entire basic Microweb use tutorial. After that, it will be implemented the REST api versions of these utilities, and it'll be defined a simple permissions control.

In the application homepage, we'll add a form to upload images, and below this form, the list of all uploaded images. Clicking on one element of such list, a new page, with image details, will be present. The image details includes image extra-data, a link to download image, and a form to add users which can see the image, and a suitable alias to that image.

From homepage, a link to a user management page will be present too. This management page permits the creation of new users, and the current users listing.

All business rules, in the case of error, throw an exception. No treatment will be given to them, and they return JSON data to the user. In the end of the project, we'll specialize `SimpleController` class to format better these exceptions.

Let's adjust the homepage template, and create the Image and Users' page templates:

__File__ `src/main/resources/views/homePage.ftl`:
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    <p>This is Microweb generated Home Page!</p>
    <#if logged>
        <p>Welcome <strong>${user.name}</strong>!</p>
        <p>Logoff <a href="/v0/gui-user-logoff">here</a> | Manage <a href="/v0/gui-user-management">users</a></p>
        <hr/>
        <p>Add image here:</p>
        <form action="/v0/gui-image" method="post" enctype="multipart/form-data">
            <table style="width: 100%">
                <tr>
                    <td style="width: 30%">Name:</input></td>
                    <td>File:</td>
                </tr>
                <tr>
                    <td style="width: 30%"><input name="fileName" style="width: 100%" type="text"></input></td>
                    <td style="padding-left: 10px"><input name="fileData" style="width: 100%" type="file"></input></td>
                </tr>
            </table>
            <input type="submit" value="Send"></input>
        </form>
        <hr/>
        <#if (images?size > 0) >
        <p><strong>Your images here:</strong></p>
        <table style="width: 100%">
        <tr>
            <td>Name:</td>
            <td>Owner:</td>
            <td>Alias:</td>
            <td>Download:</td>
            <td>Send to:</td>
        </tr>
        <#list images as image>
            <tr>
                <td>${image.name}</td>
                <td>${image.ownerName}</td>
                <td><#if image.alias??>${image.alias}</#if></td>
                <td><a href="/v0/gui-image/${image.id}/raw"><strong>&#8595;</strong></a></td>
                <td>
                    <#if (user.name == image.ownerName)>
                        <form method="post" action="/v0/gui-image/${image.id}/assign">
                            Alias:
                            <input type="text" name="alias"/>
                            User:
                            <select name="userId">
                                <#list users as u>
                                    <#if (u.name != user.name)>
                                        <option value="${u.id}">${u.name}</option>
                                    </#if>
                                </#list>
                            </select>
                            <input type="submit" value="&#8594;"/>
                        </form>
                    </#if>
                </td>
            </tr>
        </#list>
        </table>
        </#if>
    <#else>
        <p>Login <a href="/v0/gui-user-login">here</a>.</p>
    </#if>
</body>
</html>
```

And let's improve the DefaultHomePageController class, so we can redirect calls to it, after some business rule operation:
__File__ `src/main/java/microweb/sample/controller/DefaultHomePageController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.ImageManagement;
import microweb.sample.domain.bean.ImageListingData;
import microweb.sample.view.FtlHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultHomePageController extends SimpleController {
    // C.1: Create a static instance of the default home page template variable, to store and cache it:
    private static Template homePageTemplate = null;

    // C.2: Initialize the template defined above, suitably:
    static {
        try {
            homePageTemplate = FtlHelper.getConfiguration().getTemplate("homePage.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    // C.3: Define the default controller constructor:
    public DefaultHomePageController() {
        super(500, "a37c914b-f737-4a73-a226-7bd86baac8c3");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        // Define homepage data model:
        Map<String, Object> homepageDataRoot = new HashMap<>();

        // Load user cookie:
        HttpServerRequest request = routingContext.request();
        Cookie authorizationToken = request.getCookie("Microweb-Access-Token");

        // Populate Homepage data model:
        if(authorizationToken != null) {
            // Get user data:
            User u = AuthManagement.authorize(authorizationToken.getValue());

            // Load images, if they are available for this user:
            ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), routingContext.vertx());
            List<ImageListingData> imageListingData = imageManagement.list(u);

            // Load users, to assign images to them:
            List<UserData> users = UserManagement.loadUsers(1000, 0);
            users.sort(Comparator.comparing(UserData::getName));

            homepageDataRoot.put("logged", true);
            homepageDataRoot.put("user", u);
            homepageDataRoot.put("images", imageListingData);
            homepageDataRoot.put("users", users);
        } else {
            homepageDataRoot.put("logged", false);
        }

        // C.4: In the controller evaluation routine, render the template:
        routingContext
                .response()
                .putHeader("Content-type", "text/html")
                .putHeader("Cache-Control", "no-cache")
                .end(FtlHelper.processToString(homePageTemplate, homepageDataRoot));
    }
}
```
It can be seen that some formatting actions, like ordering results, limiting the number of users loaded, have been performed in the controller. Such controller isn't considering pagination, nor user search, but the reader can add them later, as an exercise.

The business rule class ImageManagement is instantiated directly in the controller, and its dependencies (Hibernate Session Factory and Vertx) are injected.

Then, after the business call, view data (`homepageDataRoot`) is populated, and the Template View is called.

One detail is important: no cache-control is enabled for this controller call, because the contents of this page is dynamic, and cache can be harmful in this circunstance.

Analysing `homePage.ftl` Template, we see it calls, now, four new different routes:

* `/v0/gui-user-management`, through `GET` method
* `/v0/gui-image/${image.id}/raw`, through `GET` method
* `/v0/gui-image/${image.id}/assign`, through `POST` method
* `/v0/gui-image`, through `POST` method

These new routes must be registered in the App. Other routes, regarding Image business rules, are registered too, and the App class turns:

__File__ `src/main/java/microweb/sample/App.java`:
```java
package microweb.sample;

import com.ultraschemer.microweb.controller.LoginController;
import com.ultraschemer.microweb.controller.LogoffController;
import com.ultraschemer.microweb.domain.RoleManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.WebAppVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.StaticHandler;
import microweb.sample.controller.*;

// 1. Specialize WebAppVerticle:
public class App extends WebAppVerticle {
    static {
        // 2. Initialize default entity util here:
        EntityUtil.initialize();
    }

    @Override
    public void initialization() throws Exception {
        // 3. Verify the default user and the default role:
        UserManagement.initializeRoot();

        // 4. Initialize additional roles (if not using KeyCloak):
        RoleManagement.initializeDefault();

        // This is added to serve static files to project - All static files are
        // to be stored at src/main/java/resources/webroot directory, which will be
        // packed with the application Jar file
        getRouter().route("/static/*").handler(StaticHandler.create());

        // 5. Register authorization filter:
        registerFilter(new AuthorizationFilter());

        // 6. Register controllers:
        registerController(HttpMethod.POST, "/v0/login", new LoginController());
        registerController(HttpMethod.GET, "/v0/logoff", new LogoffController());

        // Controllers used to manage User Login:
        // L.1: Login default presentation:
        registerController(HttpMethod.GET, "/v0/gui-user-login", new GuiUserLoginViewController());
        // L.2: Login submission handling:
        registerController(HttpMethod.POST, "/v0/gui-user-login", new GuiUserLoginProcessController());

        registerController(HttpMethod.GET, "/v0/gui-user-logoff", new GuiUserLogoffProcessController());

        // Image manipulation:
        registerController(HttpMethod.POST, "/v0/gui-image/:id/assign", new GuiImageAssignController());
        registerController(HttpMethod.GET, "/v0/gui-image/:id/raw", new GuiImageRawDataController());
        registerController(HttpMethod.POST, "/v0/gui-image", new GuiImageCreationController());

        // User management:
        registerController(HttpMethod.GET, "/v0/gui-user-management", new GuiUserManagementController());
        registerController(HttpMethod.POST, "/v0/gui-user/:id/role", new GuiAssignRoleController());
        registerController(HttpMethod.POST, "/v0/gui-user", new GuiCreateUserController());

        // L.3:  Default system home page handling:
        registerController(HttpMethod.GET, "/v0", new DefaultHomePageController());
        registerController(HttpMethod.GET, "/", new DefaultHomePageController());
    }

    public static void main(String[] args) {
        // 7. Create the Application Vertx instance:
        Vertx vertx = Vertx.vertx();

        // 8. Deploy the WebAppVerticle:
        vertx.deployVerticle(new App());
    }
}

```

These new routes are added to `App` class, too:

* `/v0/gui-user/:id/role`, through `POST` method
* `/v0/gui-user`, through `POST` method

These routes are used by the User Management interface, and will be explained later.

The first route needing to be implemented is the `/v0/gui-user-management`, to manage users. Until now, this is a single-user system. Adding user management, we can turn it multi-user, as any Web Application. This route is associated to `GuiUserManagementController` Controller class, as can be seen in `App` implementation, above. This controller implementation is shown below:

__File__ `src/main/java/microweb/sample/controller/GuiUserManagementController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.domain.RoleManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.view.FtlHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiUserManagementController extends SimpleController {
    private static Template userManagementTemplate;

    static {
        try {
            userManagementTemplate = FtlHelper.getConfiguration().getTemplate("userManagementPage.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    public GuiUserManagementController() {
        super(500, "18478c45-624d-41d1-b284-8aec7520914e");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        List<UserData> users = UserManagement.loadUsers(1000, 0);
        users.sort(Comparator.comparing(UserData::getName));
        Map<String, Object> dataRoot = new HashMap<>();
        dataRoot.put("user", routingContext.get("user"));
        dataRoot.put("users", users);
        dataRoot.put("roles", RoleManagement.loadAllRoles());
        routingContext
                .response()
                .putHeader("Content-type", "text/html")
                .putHeader("Cache-Control", "no-cache")
                .end(FtlHelper.processToString(userManagementTemplate, dataRoot));
    }
}
```

It's possible to see that this controller is minimal. It loads a list of users (limited to 1000 - pagination can be added by the reader, later), load the current logged user (which can be obtained by a call of `routingContext.get("user")`, in a logged call, which is called after `AuthorizationFilter` - i.e., any logged call), the possible user roles, and then it renders the user management template, no caching it.

Since this controller is not rendering cacheable data, we can assume it is used as a redirection from other calls.

Let's examine the user management template:

__File__ `src/main/resources/views/userManagementPage.ftl`:
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    <p>User Management</p>
    <p>Welcome <strong>${user.name}</strong>!</p>
    <p>Return to <a href="/v0">home</a></p>
    <hr/>
    <form method="post" action="/v0/gui-user">
        <table>
            <tr>
                <td>Name:</td>
                <td style="padding-left: 10px"><input type="text" name="name"/></td>
            </tr>
            <tr>
                <td>Password:</td>
                <td style="padding-left: 10px"><input type="password" name="password"/></td></tr>
            <tr>
                <td>Password confirmation:</td>
                <td style="padding-left: 10px"><input type="password" name="passConfirmation"/></td>
            </tr>
            <tr>
                <td>Given name:</td>
                <td style="padding-left: 10px"><input type="text" name="givenName"/></td>
            </tr>
            <tr>
                <td>Family name:</td>
                <td style="padding-left: 10px"><input type="text" name="familyName"/></td>
            </tr>
            <tr>
                <td>Role:</td>
                <td style="padding-left: 10px">
                    <select name="role">
                        <#list roles as r>
                        <option value="${r.name}">${r.name}</option>
                        </#list>
                    </select>
                </td>
            </tr>
        </table>
        <input type="submit" value="Create"/>
    </form>
    <hr/>
    <table style="width: 100%">
        <tr>
            <td>Name:</td>
            <td>Roles:</td>
            <td>Add Role:</td>
        </tr>
        <#list users as u>
            <tr>
                <td>${u.name}</td>
                <td>
                    <#list u.roles as r>
                        <strong style="color: gray">[</strong>${r.name}<strong style="color: gray">]</strong>&nbsp;
                    </#list>
                </td>
                <td>
                    <form action="/v0/gui-user/${u.id}/role" method="post">
                        <input type="hidden" name="userId" value="${u.id}"/>
                        <select name="role">
                            <#list roles as r>
                                <option value="${r.name}">${r.name}</option>
                            </#list>
                        </select>
                        <input type="submit" value="&#8594;"/>
                    </form>
                </td>
            </tr>
        </#list>
    </table>
</body>
</html>
```

The template above shows a very simple page, with a form to add users, and a list of users, with an associated form to assign roles to them.

Two of the previous defined routes are used in this page (`POST /v0/gui-user` and `POST /v0/gui-user/${u.id}/role`). The controllers linked to these routes, respectively, are:

__File__ `src/main/java/microweb/sample/controller/GuiCreateUserController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.controller.bean.CreateUserData;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class GuiCreateUserController extends SimpleController {
    public GuiCreateUserController() {
        super(500, "63ccb2ee-3c99-4b20-91d4-bb521f4945dd");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();

        // Get form data:
        CreateUserData userData = new CreateUserData();
        userData.setName(request.getFormAttribute("name").toLowerCase());
        userData.setAlias(userData.getName());
        userData.setPassword(request.getFormAttribute("password"));
        userData.setPasswordConfirmation(request.getFormAttribute("passConfirmation"));
        userData.setGivenName(request.getFormAttribute("givenName"));
        userData.setFamilyName(request.getFormAttribute("familyName"));

        Validator.ensure(userData);

        UserManagement.registerSimpleUser(userData, request.getFormAttribute("role"));

        // Redirect to users management interface:
        response.putHeader("Content-type", "text/html")
                .putHeader("Location", "/v0/gui-user-management")
                .setStatusCode(303)
                .end();
    }
}
```

__File__ `src/main/java/microweb/sample/controller/GuiAssignRoleController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

public class GuiAssignRoleController extends SimpleController {
    public GuiAssignRoleController() {
        super(500, "0d75c820-7650-41cd-be63-01c91bf2e4ea");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();

        UUID userId =  UUID.fromString(request.getFormAttribute("userId"));
        String role = request.getFormAttribute("role");

        // Set user role:
        UserManagement.setRoleToUser(userId, role);

        // Redirect to users management interface:
        response.putHeader("Content-type", "text/html")
                .putHeader("Location", "/v0/gui-user-management")
                .setStatusCode(303)
                .end();
    }
}
```

Both routes link the user management view to previously implemented User Management business rules, which belongs to standard Microweb libraries. Obviously the programmer can create his/her own customized business rules.

Both controllers, after calling business rules, redirect back to the user management router, using an __HTTP 303 Redirection__ message.

As can be seen, the linking between views and business rules (disregarding if they're __Model__ or __Controller__ in MVC) is made in Controller classes, and both views and domain classes and implementations are strongly aparted from each other.

Now, we can take a look at Image management controllers, which are linked to Image Management business rules and `userManagementPage.ftl` Template:

__File__ `src/main/java/microweb/sample/controller/GuiImageAssignController.java`, associated to `POST /v0/gui-image/:id/assign`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.ImageManagement;

import java.util.UUID;

public class GuiImageAssignController extends SimpleController {
    public GuiImageAssignController() {
        super(500, "c8fd6b8c-b0ec-4331-bf84-20382e616bf5");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();
        UUID imageId = UUID.fromString(request.getParam("id"));
        UUID userId = UUID.fromString(request.getFormAttribute("userId"));
        String alias = request.getFormAttribute("alias");


        ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());
        imageManagement.linkToUser(context.get("user"), imageId, userId, alias);

        // Redirect home:
        response.putHeader("Content-type", "text/html")
                .putHeader("Location", "/v0")
                .setStatusCode(303)
                .end();
    }
}
```

__File__ `src/main/java/microweb/sample/controller/GuiImageRawDataController.java` associated to `GET /v0/gui-image/:id/raw`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.ImageManagement;
import microweb.sample.entity.Image;

import java.util.UUID;

public class GuiImageRawDataController extends SimpleController {
    public GuiImageRawDataController() {
        super(500, "303c2bc7-cd1e-4570-b0c3-5a25053a8d1b");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();

        //
        // Load binary image and return it to caller:
        //

        // Create the business rule class, injecting the structural dependencies (Hibernate and Vertx):
        ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());

        // Load and decode image asynchronously, to maintain system performance:
        imageManagement.readAndDecode(context.get("user"), UUID.fromString(request.getParam("id")),
                (Image image, byte[]contents, StandardException error) ->
                {   // Call asyncEvaluation, so Microweb ensures the HTTP call will be finished suitably:
                    asyncEvaluation(500, "", context, () -> {
                        if(error!= null) {
                            throw error;
                        }

                        // Format image return data:
                        String [] imageNameParts = image.getName().split("\\.");
                        String imageExtension = imageNameParts[imageNameParts.length-1];
                        if(imageExtension.equals("jpg")) {
                            imageExtension = "jpeg";
                        }
                        Buffer b = Buffer.buffer(contents);

                        // Return it to caller:
                        response.putHeader("Content-Type", "image/" + imageExtension).end(b);
                    });
                });
    }
}

```

__File__ `src/main/java/microweb/sample/controller/GuiImageCreationController.java` associated to `POST /v0/gui-image`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.ImageManagement;
import microweb.sample.domain.bean.ImageRegistrationData;
import microweb.sample.view.FtlHelper;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.util.Set;
import java.util.UUID;

public class GuiImageCreationController extends SimpleController {
    private static Template homePageTemplate = null;

    static {
        try {
            homePageTemplate = FtlHelper.getConfiguration().getTemplate("homePage.ftl");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public GuiImageCreationController() {
        super(500, "98473a7b-3fd6-4ef3-b1b7-b53210f7a75b");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        Set<FileUpload> uploads = context.fileUploads();

        if(uploads.size()>1) {
            // Delete all received files:
            for(FileUpload f : uploads) {
                new File(f.uploadedFileName()).delete();
            }

            throw new ValidationException("Only one file is expected.");
        }

        // Process upload:
        for (FileUpload f : uploads) {
            // Verify file extension:
            if(!f.fileName().toLowerCase().matches("^.*\\.(jpg|jpeg|png|bmp|tiff|svg|ico|gif|webp)$")) {
                // Delete file:
                new File(f.uploadedFileName()).delete();
                throw new ValidationException("Unexpected format for an image. Use files with these extensions: jpg, jpeg, png, bmp, tiff, svg, ico, gif or webp.");
            }

            //
            // Save the file in database, asynchronously:
            //

            // Create the business rule class, injecting the structural dependencies (Hibernate and Vertx):
            ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());

            // Create the input bean:
            User u = context.get("user");
            ImageRegistrationData imageRegistrationData = new ImageRegistrationData();
            imageRegistrationData.setUserId(u.getId());
            imageRegistrationData.setImageFileName(f.uploadedFileName());

            // Replaces the file name by given name, adding the extension:
            String [] fileNameParts = f.fileName().split("\\.");
            imageRegistrationData.setName(context.request().getFormAttribute("fileName") +
                    "." + fileNameParts[fileNameParts.length-1]);

            // Save file:
            imageManagement.save(imageRegistrationData, (UUID uuid, StandardException e) -> {
                // Process results using default method asyncEvaluation, which treats any error
                // and ensures HTTP evaluation finalization:
                asyncEvaluation(500, "d7056121-3bf2-4f72-92f4-7b0435954572", context, () -> {
                    // Delete processed file:
                    new File(f.uploadedFileName()).delete();

                    // Raises exception, in the case of error - Microweb will deal with it suitably:
                    if(e != null) {
                        throw e;
                    }

                    // Redirect home:
                    response.putHeader("Content-type", "text/html")
                            .putHeader("Location", "/v0")
                            .setStatusCode(303)
                            .end();
                });
            });
        }
    }
}
```

It can be seen above, there are two asynchronous controllers (`GuiImageRawDataController`, `GuiImageCreationController`) and one simple synchronous controller (`GuiImageAssignController`). It can be seen in the asynchronous controllers that the logic after the asynchronous call is made is enclosed in the method `SimpleController.asyncEvaluation`. This method deals correctly with any error which could happen in controller processing. If this method is not used, the programmer must ensure all errors will be evaluated correctly, otherwise the controller can hang up. Synchronous calls Microweb already deals with exceptions, ensuring response closing.

Now we can define REST APIs to manage images and users, as all business rules and user interface is complete.

#### 5.1.6.4. Exposing a REST API

In the sections before, we defined a set of usable business rules for our sample. Let's turn them usable through an API.

_Obs.: This presented API isn't rigorously REST, nor HATEOAS. As told before, this is a Microweb tutorial, not a Tutorial about the applications of Microweb._

Let's assume we have two kinds or entities:

* User
* Image

We need to define CRUD REST operations for them, and some actions to be performed over these entities to create a well defined API on this sample system.

The list of REST operations to be defined are below:

* Create a user: `POST /v0/user`
* Read a user: `GET /v0/user/:id`
* Change password: `PATCH /v0/user/:id/password`
* Register an Image: `POST /v0/image`
* Link Image to User: `PUT /v0/image/:id/link`

User Inactivation involves to take specific actions on his/her data and permissions, so user removal won't be implemented, because it implies to change business rules from Microweb. Image removal has the same kind of constraints, so any deletion operation won't be implemented in this sample.

All the operations above already have the suitable business calls to be called, so we only need to implement the controllers.

Listing operations should enable some kind of search query. To do so, you can use what you already learned about Microweb and use some query language as [GraphQL](https://graphql.org/) or [FIQL](https://tools.ietf.org/html/draft-nottingham-atompub-fiql-00) to implement such searches. Both languages have tools which support conversion from them to SQL, so to teach how to implement these queries is beyond the scope of this tutorial. Furthermore, Microweb has its own small query language, from class `com.ultraschemer.microweb.persistence.search.Searcher` (___TODO:___ _document all microweb classes using Javadoc_).

We register all defined API calls in the class `App`, as show below:

__File__ `src/main/java/microweb/sample/App.java`, method `App.initialization`:
```java
    @Override
    public void initialization() throws Exception {
        // 3. Verify the default user and the default role:
        UserManagement.initializeRoot();

        // 4. Initialize additional roles (if not using KeyCloak):
        RoleManagement.initializeDefault();

        // This is added to serve static files to project - All static files are
        // to be stored at src/main/java/resources/webroot directory, which will be
        // packed with the application Jar file
        getRouter().route("/static/*").handler(StaticHandler.create());

        // 5. Register authorization filter:
        registerFilter(new AuthorizationFilter());

        // 6. Register controllers:
        registerController(HttpMethod.POST, "/v0/login", new LoginController());
        registerController(HttpMethod.GET, "/v0/logoff", new LogoffController());

        // Controllers used to manage User Login:
        // L.1: Login default presentation:
        registerController(HttpMethod.GET, "/v0/gui-user-login", new GuiUserLoginViewController());
        // L.2: Login submission handling:
        registerController(HttpMethod.POST, "/v0/gui-user-login", new GuiUserLoginProcessController());

        registerController(HttpMethod.GET, "/v0/gui-user-logoff", new GuiUserLogoffProcessController());

        // Image manipulation:
        registerController(HttpMethod.POST, "/v0/gui-image/:id/assign", new GuiImageAssignController());
        registerController(HttpMethod.GET, "/v0/gui-image/:id/raw", new GuiImageRawDataController());
        registerController(HttpMethod.POST, "/v0/gui-image", new GuiImageCreationController());

        // User management:
        registerController(HttpMethod.GET, "/v0/gui-user-management", new GuiUserManagementController());
        registerController(HttpMethod.POST, "/v0/gui-user/:id/role", new GuiAssignRoleController());
        registerController(HttpMethod.POST, "/v0/gui-user", new GuiCreateUserController());

        // REST API calls:
        registerController(HttpMethod.POST, "/v0/user", new UserCreationController());
        registerController(HttpMethod.GET, "/v0/user", new UserController());
        registerController(HttpMethod.GET, "/v0/user/:userIdOrName", new OtherUsersController());
        registerController(HttpMethod.PATCH, "/v0/user/:id/password", new UserPasswordUpdateController());
        registerController(HttpMethod.POST, "/v0/image", new ImageCreateController());
        registerController(HttpMethod.PUT, "/b0/image/:id/link", new ImageUserLinkController());

        // L.3:  Default system home page handling:
        registerController(HttpMethod.GET, "/v0", new DefaultHomePageController());
        registerController(HttpMethod.GET, "/", new DefaultHomePageController());
    }    
```

There are some predefined controllers on Microweb, which are present at `com.ultraschemer.microweb.controller` package, that can be reused. Just read their code, and take them as examples for Controller implementation, or just link them to some REST route you think convenient.

Since we're reusing already implemented User Management controllers, all of them suitable for REST calls, the new implemented controllers are only for Image management, below:

__File__ `src/main/java/microweb/sample/controller/ImageCreateController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.controller.bean.ImageCreationData;
import microweb.sample.domain.ImageManagement;

import javax.xml.bind.ValidationException;

public class ImageCreateController extends SimpleController {
    public ImageCreateController() {
        super(500, "cb989df8-acf1-46a5-bf9b-75879ebb4abe");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        new Thread(() -> {
            asyncEvaluation(500, "d0b8fdc8-4614-4122-ac50-c779108baec7", context, () -> {
                ImageCreationData imageCreationData = Json.decodeValue(context.getBodyAsString(), ImageCreationData.class);
                Validator.ensure(imageCreationData);

                if(!imageCreationData.getName().toLowerCase().matches("^.*\\.(jpg|jpeg|png|bmp|tiff|svg|ico|gif|webp)$")) {
                    throw new ValidationException("Unexpected format for an image. Use files with these extensions: jpg, jpeg, png, bmp, tiff, svg, ico, gif or webp.");
                }

                    // Create the business rule class, injecting the structural dependencies (Hibernate and Vertx):
                ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());

                // Create the input bean:
                User u = context.get("user");
                imageManagement.saveBase64ImageRepresentation(imageCreationData.getBase64FileRepresentation(),
                        imageCreationData.getName(), u.getId());
            });
        }).start();
    }
}
```

__File__ `src/main/java/microweb/sample/controller/ImageUserLinkController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.controller.bean.ImageUserLinkData;
import microweb.sample.domain.ImageManagement;

public class ImageUserLinkController extends SimpleController {
    public ImageUserLinkController() {
        super(500, "c57ff7a7-a255-4bf9-b6e3-dd923f2bffa9");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        ImageUserLinkData linkData = Json.decodeValue(context.getBodyAsString(), ImageUserLinkData.class);
        Validator.ensure(linkData);

        ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());
        imageManagement.linkToUser(context.get("user"), linkData.getImageId(), linkData.getUserId(), linkData.getAlias());

        response.setStatusCode(204).end();
    }
}
```

The necessary data beans:

__File__ `src/main/java/microweb/sample/controller/bean/ImageCreationData.java`:
```java
package microweb.sample.controller.bean;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.io.Serializable;

public class ImageCreationData implements Serializable {
    @NotNull
    @NotEmpty
    String base64FileRepresentation;

    @NotNull
    @NotEmpty
    String name;

    public String getBase64FileRepresentation() {
        return base64FileRepresentation;
    }

    public void setBase64FileRepresentation(String base64FileRepresentation) {
        this.base64FileRepresentation = base64FileRepresentation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

```

__File__ `src/main/java/microweb/sample/controller/bean/ImageUserLinkData.java`:
```java
package microweb.sample.controller.bean;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class ImageUserLinkData implements Serializable {
    @NotNull
    UUID userId;

    @NotNull
    UUID imageId;

    @NotNull
    @NotEmpty
    String alias;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

```

And a new exception:

__File__ `src/main/java/microweb/sample/domain/error/ImageManagementSaveBase64RepresentationError.java`:
```java
package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementSaveBase64RepresentationError extends StandardException {
    public ImageManagementSaveBase64RepresentationError(String message, Throwable cause) {
        super("ca1b849b-b598-46c9-9e2a-26e7c79ab888", 500, message, cause);
    }
}
```

It's necessary, too, to add the next Business Method to class `ImageManagement`:

__File__ `src/main/java/microweb/sample/domain/ImageManagement.java`, method `ImageManagement.saveBase64ImageRepresentation`:
```java
    // Save Base64Image representation
    public void saveBase64ImageRepresentation(String base64contents, String name, UUID userId) throws StandardException {
        try(Session session = openTransactionSession()) {
            // A.5: Save image in database
            Image img = new Image();
            img.setBase64data(base64contents);
            img.setName(name);
            img.setOwnerUserId(userId);
            session.persist(img);

            // A.6: Commit saved data:
            session.getTransaction().commit();
        } catch(Exception e) {
            throw new ImageManagementSaveBase64RepresentationError("Unable to save image data", e);
        }
    }
```

It can be seen that in the API above, no Image Listing REST route exists. To create a full REST representation of Image objects, this route must exist. To implement this route is let as an exercise to the reader.

At this point we have a full implementation of a MVC web application, with REST API, developed using Microweb. This is the most basic use case of this technology. From now, we go to the specific use cases.

#### 5.1.6.5. A simple customised resource permission control

Basic implementation of MVC web application is interesting, using Microweb, but no new contribution over existent MVC libraries exist. Further, an application as constructed above is useless, since it's a multiuser application, with no role enforcing and no permission control.

To enforce roles and implement permission control in Microweb is fairly simple, even if we're not using KeyCloak/OpenId integrations. To make such implementation, we'll create a Filter, and register it after the AuthorizationFilter and before all other controllers.

This filter will just evaluate the resource paths of each call, and, in according to the logged user role, release the route processing, or just return a __403 Forbidden__ message return.

All responses will be in JSON format. General returning format can be implemented extending the `SimpleController` class, and this will be shown only after all major features of Microweb been presented.

The permission filter registration is implemented like this:

__File__ `src/main/java/microweb/sample/App.java`, method `App.initialization`:
```java
        // ...
        // ...

        // 5. Register authorization filter:
        registerFilter(new AuthorizationFilter());

        // Register permission filter here:
        registerFilter(new PermissionControlFilter());

        // 6. Register controllers:
        registerController(HttpMethod.POST, "/v0/login", new LoginController());
        registerController(HttpMethod.GET, "/v0/logoff", new LogoffController());

        // ...
        // ...
```

And this is the implementation of permission filter:

__File__ `src/main/java/microweb/sample/controller/PermissionControlFilter.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.PermissionManagement;

public class PermissionControlFilter extends SimpleController {
    public PermissionControlFilter() {
        super(500, "dfa4afdd-9314-48a4-aa17-30dde0dbeda0");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();
        if(PermissionManagement.evaluatePermission(context.get("user"), request.path(), request.method().toString())) {
            // Continue processing
            context.next();
        } else {
            // Reached a restricted path - which is forbidden.
            response.setStatusCode(403)
                    .putHeader("Content-type", "text/html")
                    .end("<html><body><h1>Forbidden</h1></body></html>");
        }
    }
}
```

And the implementation of Permission business rules:

__File__ `src/main/java/microweb/sample/domain/PermissionManagement.java`:
```java
package microweb.sample.domain;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.entity.Role;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.utils.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionManagement {
    /**
     * Simple permission evaluation. Just verify if the path and method are inside a specific group. If so, return True.
     * False, otherwise.
     * @param u The user being evaluated
     * @param path The path being evaluated
     * @return True, if the user has permission to access path. False, otherwise.
     */
    public static boolean evaluatePermission(User u, String path, String method) {
        try {
            // If user is given by controller, than authorization has been evaluated.
            if(u != null) {
                // In the case of a valid authorization, it's necessary to evaluate permissions:
                List<Role> roleList = UserManagement.loadRolesFromUser(u.getId());
                Set<String> roleSet = roleList.stream().map(Role::getName).collect(Collectors.toSet());
                Set<String> restrictRoleSet = new HashSet<>();
                restrictRoleSet.add("root");
                restrictRoleSet.add("user-manager");
                roleSet.retainAll(restrictRoleSet);

                if (roleSet.size() > 0) {
                    // All routes are permitted
                    return true;
                }

                // Block all restricted paths:
                return  !Resource.resourceIsEquivalentToPath("GET /v0/gui-user-management#", path, method) &&
                        !Resource.resourceIsEquivalentToPath("POST /v0/gui-user/:id/role#", path, method) &&
                        !Resource.resourceIsEquivalentToPath("POST /v0/gui-user#", path, method) &&
                        !Resource.resourceIsEquivalentToPath("POST /v0/user#", path, method) &&
                        !Resource.resourceIsEquivalentToPath("GET /v0/user/:userIdOrName#", path, method);
            } else {
                // Any route without required authorization is automatically permitted:
                return true;
            }
        } catch(Exception e) {
            return false;
        }
    }
}
```

Now, we have a very simple multiuser image sharing system, with a simple permission control, and a simple REST API. It can be seen that `PermissionManagement` business class uses some utilitis from Microweb to evaluate resources. These functions are used internally by Microweb to evaluate resource control, but here are used only to evaluate path/URI equivalence, permitting to implement a simple permission control.

With this filter implemented, all main features of Microweb, regarding MVC applications implementation is finished. Now, it's time to present the most relevant features of this framework - it's KeyCloak integration, and how to use Microweb in a heterogeneous network of REST/SOA microservices.

This complete project can be used as base of new Microweb projects, and it's source code can be [found here](https://github.com/ultraschemer/microweb-sample).

## 5.2. Simple user manager system, with OpenID support

We have developed a simple Web Application so far. It's multiuser and it has a simple permissioning control. But it's far from a full fledged multiuser system, with complete permissions and resource management. And this Web Application is, yet, locked to a single platform (the Java Platform), and locked to a single type of database (PostgreSQL).

The database problem can easily be addressed, just accepting the User Management and Permissions management will be addressed by Microweb structure on PostgreSQL, and that business rules will receive connections from other types of databases for anything else. If you already developed a complex system, you'll realize you already have dealt with these constraints before. (_other approach is just map Microweb to other database, but let's assume you don't want to implement this_)

The previous project sample (the entire 5.1 section) address the permissions problem with a very simple filter, which is evaluated after the basic authorization filter. So, using Microweb and filters, it's possible to implement a complete Permissions Resource control system.

But, unfortunately, we shouldn't implement this.

Resource permission and access control is a really complex topic. If you look at this subject at [WhatIs.com](https://searchsecurity.techtarget.com/definition/access-control), [Wikipedia](https://en.wikipedia.org/wiki/Access_control) or at [IDG CSO](https://www.csoonline.com/article/3251714/what-is-access-control-a-key-component-of-data-security.html), you can follow series of hyperlinks, about the topic, going deeper and deeper in the subject. Resource control isn't a subject to be treated by amateurs.

But, luckily, lots of providers of Resource Permission Platforms exist; from Oracle, to Microsoft, to Amazon AWS, to Google. Just take a look up about this subject [here](https://www.google.com/search?q=access+control+software+service+provider).

Analysing the sample provided in Section 5.1 and analysing Microweb database, we can see that Microweb security and access control are based on ___resources___ and ___roles___. Resource and Role based access control is a specific subject in Access Control (see [RBAC, here](https://auth0.com/docs/authorization/concepts/rbac)), and a fairly complex one. Furthermore, Microweb is built upon an HTTP/REST/Web foundation. It's built to offer suitable user controls for complex scenarios. In such scenarios, the widespread technology for Authentication, Authorization and User Management is [OpenId](https://openid.net/what-is-openid/).

Then, the challenge is to provide a technology to support standards, like OpenID, to offer user, resource and role control, and yet to control services and resources in a heterogeneous environment, based on HTTP/REST technologies, in SOA Architecture.

Obviously, a framework to provide all these features should be a huge and complex one.

But, fortunately, it doesn't.

There are lots of providers of OpenID platforms, they being services or libraries. Some of them have RBAC features. Then, we just need to choose one to support, and associate Microweb to it. This has been Microweb choice, and the chosen technology has been [KeyCloak](https://www.keycloak.org/).

Microweb, being a platform for development of Microservices, has chosen to use the entire KeyCloak stack as a loosely coupled service on runtime.

Obviously, if you use KeyCloak as a service alongside Microweb, the Microweb memory limit of 60MB for the entire stack won't apply, anymore, since KeyCloak alone requires more computing resources than 60MB of RAM. But, in a clustered environment, these benefits worth the extra costs.

KeyCloak is [Open Source](https://github.com/keycloak/keycloak), with a business permissive license, it's [very well documented](https://www.keycloak.org/documentation), it supports OpenID and User Federation, and it has lots of more features.

KeyCloak, as is, has strong support to Spring and Java EE, and [lots of technologies](https://www.keycloak.org/docs/latest/securing_apps/#supported-platforms). __But it doesn't support Vert.X__. Moreover, KeyCloak doesn't enforce no particular architecture to secure REST applications in heterogeneous environments. And server side permission control must be implemented with Java Technologies or directly on Apache HTTP Server configuration.

Microweb main idea is to provide customizeable KeyCloak user and resource management services, on HTTP/Rest Services, on heterogenous environments, following the architecture presented in this figure:

__Figure 1: Microweb OpenId enabled heterogeneous architecture:__
![Heterogenous-Architecture](Microweb-Proposed-Architecture.png)

Microweb+Keycloak suit you if:
* You want to follow the architecture presented in the figure above to create a heterogenous SOA enabled environment, and leverage services developed using multiple programming languages and platforms, using resource management and user control offered by KeyCloak. 
* You want to use Vert.X on your Java Backend Services using KeyCloak resource control services too.
* You have lots of non-Java code, which can be offered as REST services, to users of your system, under KeyCloak user, resources and permissions control.

The sample presented in this section shows how to create such service architecture to attend all three points above. Let's start from the sample we created before, changing it to support OpenID, and loading external services with two different approachs, to show how to use Microweb as a proxy to HTTP and REST services developed in other technologies than Java, or Microweb itself.

### 5.2.1. Instantiating KeyCloak and preparing your application to OpenId

To enable OpenID support on the web application, we must instantiate KeyCloak, prepare a suitable realm, and register users and resources.

To follow this section, please, read a tutorial about OpenID Connect, or understand the protocol, reading some documentation about it [here](https://auth0.com/docs/protocols/oidc) and learn about securing your application with KeyCloak [here](https://www.keycloak.org/docs/latest/securing_apps/).

Lots of tutorials about OpenID Connect can be found using Google or on YouTube.

You must understand, also, the basics about KeyCloak use, which documentation can be found [here](https://www.keycloak.org/docs/latest/getting_started/).

OpenID and OpenID Connect are extense topics, and the official information can be found [here](https://openid.net/connect/).

After you inform yourself about OpenID, OpenID Connect and KeyCloak installation and management, create a default installation on your own computer, in any place you want, with any database support you think suitable to you (in this sample we use the default internal KeyCloak database, since nothing more complex than this is needed). Microweb and KeyCloak doesn't share data, they synchronize user information and roles as they're required.

This sample will use only the authentication/authorization services provided by OpenID, so it's very similar to a [OAuth2](https://oauth.net/2/) enabled application. Full use of OpenID is not covered in this tutorial.

#### 5.2.1.1. Enabling a DNS entry for your application

Start KeyCloak on its default port (8080):

```sh
<KeyCloak installation dir>/keycloak-10.0.1/bin $ ./standalone.sh # On windows run standalone.bat

...
...
Lots of output
...
...

00:00:00,000 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0025: Keycloak 10.0.1 (WildFly Core 11.1.1.Final) started in 23287ms - Started 591 of 889 services (606 services are lazy, passive or on-demand)
```

_Obs.: Currently Microweb supports KeyCloak 10.0.1._

Let's assume our services are offered at the DNS entry __www.sample.microweb__, so the application site should be accessible at __http://www.sample.microweb__. We'll use this address to configure our application.

Verify your physical address Ip (from your machine), and assign the address __www.sample.microweb__ to it, in the `/etc/hosts` file of your system (on windows it is `c:\Windows\System32\drivers\etc\hosts`), appending to the current file:

```
#
# Previous assigned address above
#
000.000.000.000 www.sample.microweb
```

Where `000.000.000.000` should be the physical Ip address of your machine. Please, do not use the loopback adapter address (127.0.0.1) on this assignment, since it won't represent an accurate example of how to use OpenID.

After this addition, ping the address __www.sample.microweb__ and the address of your machine should answer.

Now, your App address will be __http://www.sample.microweb__.

#### 5.2.1.2. Replacing default Microweb configurations

Let's open the `configuration` table on __microwebsample__ database we previously created, and you'll see:

```sql
select name, value from configuration;

name                                             |value                                                              |
-------------------------------------------------|-------------------------------------------------------------------|
Java backend port                                |48080                                                              |
backend oauth wellknown                          |http://localhost/auth/realms/<realm>/.well-known/uma2-configuration|
keycloak master oauth wellknown                  |http://localhost/auth/realms/master/.well-known/uma2-configuration |
keycloak admin resource                          |http://localhost/auth/admin/realms/<realm>                         |
keycloak master admin name                       |<admin>                                                            |
keycloak master admin password                   |<password>                                                         |
keycloak admin realm                             |<realm>                                                            |
keycloak client application                      |<client-name>                                                      |
keycloak client application available permissions|#user,#role                                                        |
```

We must edit this configuration to reflect what we want to our system. Each configuration is explained in details below:

* __`Java Backend port`:__ This is the application default port. As you could see from previous sample, the application always start at port 48080. This starting service port is configured by this configuration variable.
* __`backend oauth wellknown`:__ This is where Microweb finds the OpenID endpoints from your KeyCloak installation. Microweb requires a configured Realm on KeyCloak, and this server must be waiting at a specific address. This address must be at the same DNS entry of your application. Let's call the KeyCloak realm the application will use as __microweb__, and the application DNS entry, as it has been told before is equal to __www.sample.microweb__. We'll need a reverse-proxy to make KeyCloak available at this address - and to avoid root-user reserved ports clash, we'll configure in such reverse proxy the port __9080__. So this variable should contain the string: `http://www.sample.microweb:9080/auth/realms/microweb/.well-known/uma2-configuration`.
* __`keycloak master oauth wellknown`:__ This variable should point to web path of KeyCloak master application. Just change it to point to __www.sample.microweb__, port __9080__. So this variable should contain the string: `http://www.sample.microweb:9080/auth/realms/master/.well-known/uma2-configuration`.
* __`keycloak admin resource`:__ This is the REST address used by Microweb to manage users, permissions and resources on KeyCloak. Just adjust the realm and the address, changing this variable to `http://www.sample.microweb:9080/auth/admin/realms/microweb`
* __`keycloak master admin name`:__ The root admin name, of the Master Realm. We'll create a user called `microwebadmin` to perform this role. You can set here the user name of the root user, of your KeyCloak installation __master__ realm.
* __`keycloak master admin password`:__ This is the password of the user presented in the variable above. Just assign `microwebpassword` string value to it.
* __`keycloak admin realm`:__ This is the realm where your application is registered. Assign the value `microweb` to it.
* __`keycloak client application`:__ Your application name. To differentiate it from the realm name, use `microwebsampleapp` on it. 
* __`keycloak client application available permissions`:__ This configuration is used internally by Microweb. Ignore it.

After these modifications, your `configuration` table should contain these values:

```sql
select name, value from configuration;
name                                             |value                                                                              |
-------------------------------------------------|-----------------------------------------------------------------------------------|
Java backend port                                |48080                                                                              |
backend oauth wellknown                          |http://www.sample.microweb:9080/auth/realms/microweb/.well-known/uma2-configuration|
keycloak master oauth wellknown                  |http://www.sample.microweb:9080/auth/realms/master/.well-known/uma2-configuration  |
keycloak admin resource                          |http://www.sample.microweb:9080/auth/admin/realms/microweb                         |
keycloak master admin name                       |microwebadmin                                                                      |
keycloak master admin password                   |microwebpassword                                                                   |
keycloak admin realm                             |microweb                                                                           |
keycloak client application                      |microwebsampleapp                                                                  |
keycloak client application available permissions|#user,#role                                                                        |
```

Then we can configure KeyCloak. If you have an already configured KeyCloak installation, just replace the values given above by that correspondent to your installation, and skip the next steps directly to section __5.2.2. Reseting the database__.

#### 5.2.1.3. Configuring the fresh KeyCloak installation

If you're using a fresh new KeyCloak installation, then you must configure such installation to reflect your `configuration` table. If you already have a KeyCloak installation, your `configuration` table that must reflect your KeyCloak data. We're assuming now you're loading a new KeyCloak install.

Since it must be already running, access it from your `localhost` address, and you'll see this screen:

![KeyCloak initial screen](keycloak-initial-screen.png)

In the form asking to create an initial admin user, fill with:

* __Username__: `microwebadmin`
* __Password__: `microwebpassword`
* __Password confirmation__: `microwebpassword`

And click the __Create__ button, below the form. After that, you'll see this screen:

![KeyCloak master admin created](keycloak-master-admin-created.png)

Click on __Administration Console__ link, and login with the user __microwebadmin__. You'll be redirected to the __Master__ realm administration screen, and, then, you'll already have your `keycloak master admin user`, `keycloak master admin password`, and the `Master` realm.

Now, we must create a new realm (`microweb`), and, in this realm, a new application (`microwebsampleapp`).

Chose the __Add realm__ button, below the __Master__ realm name in the left-bar:

![Calling Add Realm](calling-add-realm.png)

Then, create the `microweb` realm, already enabled:

![Adding realm form](adding-realm-form.png)

Then, in the new created realm, let's add a new client.

> In KeyCloak _jargon_ (and I think in Oauth2 and OpenID jargon, as well), an application is called __client__, then, our `microwebsampleapp` application, on KeyCloak managemente console, will be a client.

![Calling client creation form](calling-client-create.png)

Which redirects to:

![]()

In the form above, just fill __Client ID__ field with `microwebsampleapp`, entirely lowercase, and click __Save__. Ignore the __Root URL__ field at the moment. Be sure you're creating the application in the `microweb` realm. Maintain the client protocol as openid-connect (_Obs.: Microweb doesn't support SAML_).


![Creating microweb sample application client](creating-microwebsampleapp-client.png)

You'll redirected to the next form, with the new application settings:

![Microweb Sample App Settings](microweb-sample-app-settings.png)

In this form, set:

* __Name__: `Microweb Sample App`
* __Description:__: `Application Sample for Microweb Framework`
* __Enabled__: __On__
* __Consent Required__: __On__. This option will force all users to be `logged` and `identified` users, since `microwebsampleapp` is an external application to KeyCloak, and KeyCloak is requiring users to consent to `microwebsampleapp` access actively.
* __Client Protocol__:  __openid-connect__
* __Access type__: __confidential__. Very important configuration - it'll force active user authentication on our Microweb Sample application.
* __Standard Flow Enabled__: __On__
* __Direct Access Grants Enabled__: __On__
* __Services Accounts Enabled__: __On__
* __Authorization Enabled__: __On__. This option is specially relevant, because without it, no Resource Permission Control is possible.
* __Valid Redirect URIs__: `/*`. This setting is really important, because it says any HTTP address prepended by the given string will be accepted as an authentication redirection. Reading about OpenID and [OAuth2](https://oauth.net/2/) specifications provide more information. In this case we're using a relative path, so the Referer address will be required to authentication. Since we didn't enable CORS, no cross authentication is possible, anyway. 

You can maintain all other options as default.

If you followed all steps correctly, you'll see a tab called __Authorization__ on Microwebsampleapp settings page:

![Microweb Sample App authorization tab](microwebsampleapp-authorization-tab.png)

This __authorization__ tab is the most relevant for Permissions control, and Microweb requires a specific use of the resources managed under this tab.

Click on it and you'll see the __Authorization Settings__ page. Just let it with defaults, and click on __Resources__ tab:

![Microweb Sample App calling resource management](microwebsampleapp-authorization-tab.png)

You'll see the __Resources management page__:

![Microweb Sampe App resources management page](microwebsampleapp-resources-management-page.png)

Before continue, we must understand how Microweb deals with resources, otherwise, this section will be completely senseless.

#### 5.2.1.4. Microweb permission control abstractions

One characteristic of OpenID authorization is that when a user is validated, the Authorization system returns to him/her a list of resources he/she has authorized access. Using this feature, Microweb evaluate any REST/Http Call on it, and evaluate the permission.

This approach has some big disadvantages (which I didn't know, yet, how to deal with):

* Every REST Call on Microweb represents an Authorization Call on KeyCloak, which represents performance penalty. I didn't evaluated how strong is this penalty.
* Since OpenID authorization, by default, returns only the list of names of authorized resources, resorce naming must be strictly defined, to be useful.
* Resource additional features provided by KeyCloak, like the "URI" resource field, under these circunstances become redundant.

But, it also has some advantages:

* Resource configuration and management can be made directly on KeyCloak, on runtime, with __imediate__ results.
* No conflict between different resource management uses is enforced.
* Complex permission and resource control provided by KeyCloak is readily available as a Microweb feature.

Then, we realized that the benefits given are far beyond the penalties imposed, and these penalties, in the future, can be solved, with some architectural evolution of Microweb.

Microweb has the next approach on resource management, when KeyCloak is involved:

* If the resource is not registered on KeyCloak, then it's automatically forbidden.
* The resource name must follow specific criteria to be considered registered.
* If the criteria has been fulfilled, then permission evaluation is performed.
* Permission returned by KeyCloak is considered final.

Microweb doesn't perform resource name conflict resolution, and if one given resource matches more than one resource name, no conflict resolution is made, and the first resource evaluated will be considered the only one. __So, never create two conflicting resource names, because Microweb will consider correct the first name matching the real resource and all other names will be ignored. Since OpenID authorization calls return the _allowed_ resources to the calling user, in the case of naming conflict, if one, and only one of these resources, has an _allowed_ permission, then the resource will be considered allowed to that user, no matter how many _"forbiddens"_ that resource has to that user on the other registered names__. No solution for this problem is planned, because no __cascading permission control__ has been planned for Microweb.

From the points above, we can conclude Microweb uses the __resource name__ to evaluate its registration on KeyCloak, and then, to evaluate its permission.

Microweb only consider URI paths and Methods to evaluate resources. So, for Microweb, resources are:

* A full route endpoint, composed by a Method name (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`), and a URI path, without the query string. In this case, all paths must be finished by a __sharp signal (`#`)__. So, `GET /v0/user#` is an example of a resource).
* A URI path, identified by a Regular Expression. So, `^\/v0\/user.*$` is an example of resource, different from `\/user\/v[123]management`, which is another resource.

All these resorces are evaluated on HTTP Request calls. So, if a Microweb Microservice receives this request:

```
POST /v0/user
Content-Type: application/json
Entity-Body:
{
    name: 'paul',
    password: 'abc123'
}
```

And a resource is registered on KeyCloak with `POST /v0/user#` as name, then that route resource is considered registered. The same if a resource with `^.*\/user$` as a name exists. If both exist, then we have a resource name clash, and Microweb has no way to deal with it. If one of the clashed resource names has an _allowed_ permission, than the call will be considered _allowed_ to that user.

A resource with `POST /v0/users` as a name, will be treated as a regular expression, and since no route will match this resource, it will means nothing and will be completely ignored by Microweb.

Again, __never create two resources with conflicting names__, because Microweb doesn't make naming conflicting resolution, and this feature is not expected to be implemented __at all__. If two permissions with conflicting names exist, one giving user permission, and another forbidding it, __Microweb will consider the allowing permission, ignoring the forbidding permission__.

Given this explanation, let's create the necessary resources to our sample application.

#### 5.1.2.5. Naming the application resources

Considering the resource naming rules presented above, the rule of thumb to naming Microweb resources, under KeyCloak, are:
1. Create a resource name for each __route__ or __endpoint__ registered in your Microweb Application project.
2. Create generic resource names using regular expressions only for boundary cases.

When we open the current `microweb.sample.App.initialize` method, we can see all routes registered in the system. Some of the routes (`GET /#`, `GET /v0#`, `POST /v0/login#`, `GET /v0/gui-user-login`, `POST /v0/gui-user-login`) don't need authorization, so we can simply ignore them. All the other need. Let's just list all of them, using Microweb KeyCloak resource naming rules (`METHOD path#`), and saving all of them as resources in KeyCloak:

* `GET /v0/logoff#` 
* `GET /v0/gui-user-logoff#` 
* `POST /v0/gui-image/:id/assign#` 
* `POST /v0/gui-image/:id/raw#` 
* `POST /v0/gui-image#` 
* `GET /v0/gui-user-management#` 
* `POST /v0/gui-user/:id/role#` 
* `POST /v0/gui-user#` 
* `POST /v0/user#` 
* `GET /v0/user#` 
* `GET /v0/user/:userIdOrName#` 
* `PATCH /v0/user/:id/passowrd#` 
* `POST /v0/image#` 
* `PUT /v0/image/:id/link#` 

as can be viewed below:

![Microweb Resource Registration](microwebsampleapp-resource-registration.png)

_Obs.: Save the resource __Name__ and __Display Name__ with the same value, just to maintain this sample simple. For Microweb, just the resource __name__ is important. Other resource features are not used._

As can be viewed in resource names above, resource path parameters are prepended with colons (`:`) both in Microweb (due Vert.X routing naming rules) and in KeyCloak (due Microweb routing names being inherited from Vert.X). 

Hence, both resources are considered equivalent:

* `POST /v0/gui-image/:id/assign#`
* `POST /v0/gui-image/:imageId/assign#`

And if both names are used, they will clash. So, be careful with path parameters, when registering a resource.

After all resources are registered, we need to assign permissions to them.

KeyCloak uses OpenID conventions to permissioning, and every resource to be permitted need to be associated to __Scopes__, and these scopes are assigned to __Roles__, and such roles, in their turn, are assigned to __Users__. The __Roles__ receive permissions to access certain __Scopes__ and all __Resources__ associated to them. You can understand these relations better reading KeyCloak [documentation](https://www.keycloak.org/docs/latest/authorization_services/). This kind of authorization presented in this tutorial is Role-based (RBAC). Other types of authorization are possible, but to describe them is beyond the scope of this documentation.

So, let's create the scopes we need, and then, assign resources to them. On __Microwebsampleapp - Authorization__, select the __Authorization Scopes__ tab, as shown below:

![Microweb authorization scopes](microwebsample-authorization-scopes.png)

Create four authorization scopes, with the next names:

* `user`
* `user-manager`
* `user-api`
* `user-manager-api`

Now, we need to edit each resource, assigning them to the policies above.

Go back to the list of resources, and assign them to the scopes, using this distribution:

* Scope `user` receives the resources:
  * `GET /v0/logoff#`
  * `GET /v0/gui-user-logoff#`
  * `POST /v0/gui-image/:id/assign#`
  * `GET /v0/gui-image/:id/raw#`
  * `POST /v0/gui-image#`
* Scope `user-manager` receives the resources:
  * `GET /v0/gui-user-management#`
  * `POST /v0/gui-user/:id/role#`
  * `POST /v0/gui-user#`
* Scope `api` receives the resources:
  * `GET /v0/logoff#`
  * `GET /v0/user#`
  * `POST /v0/image#`
  * `PUT /v0/image/:id/link#`
  * `PATCH /v0/user/:id/password#`
* Scope `user-manager-api` receives the resources:
  * `POST /v0/user#`
  * `GET /v0/user/:userIdOrName#`

You can see, above, that the resource `GET /v0/logoff#` is present in both `user` and `api` scopes. There is no problem if a resource belongs to more than one scope.

To create Role-Based Access permissions, we need roles. Let's create three:

* The first one is the `user` role.
* The second one is the `user-admin` role.
* The third one is the `root` role, which is a combination of the two, above.

Go to the __Microwebsampleapp - Roles__ tab, and add the three roles above:

![Adding role](microwebsampleapp-add-role.png)

In the case of `root` role, it is the combination of `user` and `user-admin` roles:

![Adding root role](microwebsampleapp-adding-root-role.png)


Now we can create permissions on these scopes. We'll use __Role-Based Access Control (RBAC)__, which is easily provided by KeyCloak. Just go back to the ___Authorization Scopes__ tab, and create the permissions, based on roles:

![Creating a scope based permission](microwebappsample-scope-based-permission-creation.png)

Create two permissions.

The first one is for default users, with access to `user` and `api` scopes:
* Name: `user`
* Description: `User default access`
* Scopes: `user` and `api`
* Apply policy: Create a role based policy, granting access to role `user`.
* Decision Strategy: `Unanimous`.

The other values you can let the default:

![Creating permission](microwebsampleapp-create-permission.png)

The other permission is for an Administrator:
* Name: `administrator`
* Description: `User manager permission`
* Scopes: `user-manager` and `user-manager-api`
* Apply policy: Create a role based policy, granting access to the role `user-admin`.
* Decision Strategy: `Unanimous`.

_Obs.: This step is confuse and a may seem a little complicated. This is because KeyCloak implementation of RBAC is not exactly the simplest one. Read KeyCloak documentation to get used to its concepts._

The last step is to create a default user group, granting to all users in this group the role `user`, created above. This will ensure that any new user will have access to the correct application resources.

Go to the Groups management interface, and create a __default__ user group, assigning the role `user` to all of its members:

![Creating group](microwebsampleapp-create-group.png)

Assigning the default role:

![Assigning default role](microwebsampleapp-assign-default-group-role.png)

In theory, all permission configuration has been implemented. Now, it's time to adapt the Microweb Sample App project to support KeyCloak permissions.

### 5.2.2. Reseting the database

Now that we configured KeyCloak, we need to cleanup the database, because the simple user management provided by Microweb isn't compatible with KeyCloak user management. We need to clean the entire database.

The best way is simply to downgrade the database, using the __Alembic__ migrations, but this will cleanup the __Configuration__ table, too. If you downgrade the database to base, you'll need to repopulate the __Configuration__ table. If you don't want to do this, just run the SQL script below, which will reset the project entirely:

```sql
delete from user__image;
delete from image;
delete from access_token;
delete from user__role;
delete from role;
delete from user_;
```

Since the other tables aren't used, they're irrelevant on this point of project.

### 5.2.3. Adjusting KeyCloak integration on App class

The way Microweb integrate with KeyCloak is different from the way we used the framework until now. Each controller must be integrated to KeyCloak authorization calls __independently__. Filters aren't used.

The `SimpleController` class has some overridable methods, and a `SimpleController` specification exists, to integrate route authorization to KeyCloak. This specification is called `CentralUserRepositoryAuthorizedController`, and we'll use it to control authorization to each of our controllers. If a controller has no restricted authorization, than we'll maintain them inheriting from `SimpleController`.

The first action we need to perform is to remove all Microweb internal user management calls from `App.initialization` method, as can be seen below:

__File__ `src/main/java/microweb/sample/App.java`, method `App.initialization()`:
```java
    @Override
    public void initialization() throws Exception {
        getRouter().route("/static/*").handler(StaticHandler.create());

        // Register controllers:

        // User access controllers:
        registerController(HttpMethod.GET, "/v0/gui-user-login", new GuiUserLoginViewController());
        registerController(HttpMethod.POST, "/v0/gui-user-login", new GuiUserLoginProcessController());
        registerController(HttpMethod.GET, "/v0/gui-user-logoff", new GuiUserLogoffProcessController());

        // Image manipulation:
        registerController(HttpMethod.POST, "/v0/gui-image/:id/assign", new GuiImageAssignController());
        registerController(HttpMethod.GET, "/v0/gui-image/:id/raw", new GuiImageRawDataController());
        registerController(HttpMethod.POST, "/v0/gui-image", new GuiImageCreationController());

        // User management:
        registerController(HttpMethod.GET, "/v0/gui-user-management", new GuiUserManagementController());
        registerController(HttpMethod.POST, "/v0/gui-user/:id/role", new GuiAssignRoleController());
        registerController(HttpMethod.POST, "/v0/gui-user", new GuiCreateUserController());

        // REST API calls:
        registerController(HttpMethod.POST, "/v0/user", new UserCreationController());
        registerController(HttpMethod.GET, "/v0/user", new UserController());
        registerController(HttpMethod.GET, "/v0/user/:userIdOrName", new OtherUsersController());
        registerController(HttpMethod.PATCH, "/v0/user/:id/password", new UserPasswordUpdateController());
        registerController(HttpMethod.POST, "/v0/image", new ImageCreateController());
        registerController(HttpMethod.PUT, "/b0/image/:id/link", new ImageUserLinkController());

        // Default system home page handling:
        registerController(HttpMethod.GET, "/v0", new DefaultHomePageController());
        registerController(HttpMethod.GET, "/", new DefaultHomePageController());
    }
```

It can be seen, above, that `AuthorizationFilter` has been removed, as the `PermissionControlFilter`. These class can even be removed from the project. The calls to `UserManagement.initializeRoot()` and to `RoleManagement.initializeDefault()` are removed too. Root user management and role management will be performed by KeyCloak. `Login` and `Logoff` REST controllers have been completely removed. Now, these actions are controlled by KeyCloak.

Now, all controllers which authorization will be performed by KeyCloak must specialize `com.ultraschemer.microweb.vertx.CentralUserRepositoryAuthorizedController`, as the sample below:

__File__ `src/main/java/microweb/sample/controller/GuiUserLogoffProcessController.java`:
```java
package microweb.sample.controller;

// ...
// Other imports
//
import com.ultraschemer.microweb.vertx.CentralUserRepositoryAuthorizedController;
//
// Other imports
// ...

public class GuiUserLogoffProcessController extends CentralUserRepositoryAuthorizedController {
    // Class internal implementation (it doesn't change)
    // ...
    // ...
}
```

The controllers which must be changed in this way are listed below, all of them in the package `microweb.sample.controller`:

* `GuiUserLogoffProcessController`
* `GuiImageAssignController`
* `GuiCreateUserController`
* `GuiImageRawDataController`
* `GuiImageCreationController`
* `GuiUserManagementController`
* `GuiAssignRoleController`
* `UserCreationController`
* `UserController`
* `OtherUsersController`
* `UserPasswordUpdateController`
* `ImageCreateController`
* `ImageUserLinkController`

After changing the controllers to specialize the `CentralUserRepositoryAuthorizedController` controller class, we realize we don't have, anymore, a way to perform system login.

Login, now, is controlled by KeyCloak. Then we need a mor detailed explanation about how to implement login, and then, how to reimplement logoff.

### 5.2.4. Re-creating Login and Logoff calls

When integrated with KeyCloak, Microweb must follow OpenID rules, and OpenID login process is strictly requires a 2-step login procedure, with the two parties involved in the login/sign-in process.

The first party is the `user`. The user must authenticate him/herself, to ensure he/she is who he/she says, as the first step.

The second party is the application, the `client`. The application must ensure it is a valid application, and that the user given it permission to access him/her resources and data.

Do not confuse this two steps with __[two factor authentication](https://auth0.com/learn/two-factor-authentication/)__, which is a step further in security.

This README is not about __two factor authentication__, so an explanation about it won't be given here.

The first detail we must pay attention is that every OpenID client has two important data:

* The Client ID
* The Client Secret

Client secret must be held by the client application implementation, but it __never can be sent to source code running in the client, it must be held in server side__, to avoid its leakage.

The ClientID is free to be loaded in any place, to identify the client application.

The login form is controlled by KeyCloak, and all user data is exchanged directly by the user and the OpenID provider (in our case, KeyCloak). So, when requesting login, the client application must redirect the user to a KeyCloak controlled login form, and pass to KeyCloak a finish login redirection address. KeyCloak will redirect the user back to the client application, and the client, server side, will authenticate itself on KeyCloak.

_Obs.: This flow can seem difficult. If you're thinking it complex, please, read OpenID documentation and look for some OpenID or OAuth2 tutorial to understand it better._

So, the login link, in the home view, just need to redirect application to KeyCloak, then receiving back the flow, for client authentication.

But, to KeyCloak be completely integrated to Microweb application, and to be reachable to other parts of the sample system, it must be accessible by the application in a safe way. So, let's configure Microweb to access KeyCloak correctly.

#### 5.2.4.1 Exposing KeyCloak as a Microweb Microwervice

Until now, KeyCloak and Microweb Sample App are different and independent systems. Let's integrate them in a single system.

The best way to perform this integration is to use a Reverse Proxy, linking all routes of KeyCloak and Microweb application in a single address. This kind of integration turns all KeyCloak calls and Microweb calls in elements of a single heterogeneous system, as can be seen in the __Figure 1__, above.

This is necessary, again, because DNS entries and IP address are __limited resources__, and there is __[CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)__, which restrict HTTP calls to specific addresses when they're called from a specific source.

_Obs.: __CORS__ can be disabled in production systems, and for public APIs, this can be necessary. But, in almost all other situations, apart public APIs, __CORS__ is one of the most important security measures in distributed systems. Complex and detailed control of __CORS__ is extremely necessary in safe complex systems, and this is a topic beyond this README. As a rule of thumb, maintain all HTTP/REST calls in the same DNS entry or HTTP Address, to permit advanced __CORS__ configuration._

Microweb packs with itself a proxy API, provided by [little proxy](https://github.com/adamfisk/LittleProxy) library. This proxy has been customized to work as a reverse proxy for the HTTP protocol. HTTPS protocol is not available, because Microweb internal proxy is for internal network use. HTTPS support must be provided by external http servers, like [Nginx](http://nginx.org/) or [Apache](http://httpd.apache.org/), over the entire Microweb services you're developing.

As defined in the Microweb Sample App `Configuration` table, the entire application will listen at port __9080__, so we'll configure the internal proxy on this port.

_Obs.: The internal proxy should listen in port __80__. On Windows and Mac OS X, this is simple and direct. But in Linux, only root users' programs can listen at port __80__. To make the entire system to listen in another port doesn't reduce this sample genericity, so it can be developed in Windows, Linux or Mac OS equally._

Change the `App.initialization()` method to:

__File__ `src/main/java/microweb/sample/App.java`, method `App.initialization`:
```java
    @Override
    public void initialization() throws Exception {
        // ..
        // Previous code is maintained
        // ..
        // ..

        //
        // Append this to the end of method:
        //
        RegisteredReverseProxy proxy = new RegisteredReverseProxy(9080);
        proxy.registerPath("^\\/auth.*$", "localhost:8080");
        proxy.registerPath("^\\/v0.*$", "localhost:48080" );
        proxy.registerPath("^\\/$", "localhost:48080");
        proxy.run();
    }
```

The code above will redirect any uri with starting with `/auth` to a server located at localhost, but listening at __8080__ port, which is our instance of KeyCloak. The uri `/`, and any uri starting with `/v0` will be redirected to Microweb sample app, itself.

_Obs.: Make sure your KeyCloak instance is prepared to receive connections from reverse proxies, as show in [this documentation](https://www.keycloak.org/docs/latest/server_installation/index.html#_setting-up-a-load-balancer-or-proxy). In this example, it's not necessary to prepare KeyCloak to HTTPS enabled reverse proxies._

Just implement it and restart Microweb sample application. The reverse proxy must proceed correctly, you can call KeyCloak interface from the address `http://www.sample.microweb:9080/auth`, while the application will be accessible from `http://www.sample.microweb:9080/` - so both applications, although in different microservices, can be seen as a single Http Web Application:

![Working reverse proxy](microweb-sample-reverse-proxy-unifying-microservices.png)

Once this configuration is done, let's verify if KeyCloak is ___really___ configured as a Microweb microservice. To test it, we just need to access the "well known" OpenId endpoint:

__URL__ `http://www.sample.microweb:9080/auth/realms/microweb/.well-known/uma2-configuration`:
![Open Id URL Consistency](microwebsampleapp-openid-wellknown-urls-consistency.png)

The OpenID configuration URLs and the application URLs are consistent - so KeyCloak is correctly registered as a Microweb microservice.

#### 5.1.4.2. Implementing Login and Logoff to support OpenId

Now KeyCloak and OpenId are configured correctly, we need to reimplement Login and Logoff calls, to support double authentication.

Login must have two steps. The first step is to present to the user the KeyCloak login form.

This is simple, we just need to create a link at the Home Page template, as shown below:

__File__ `src/main/resources/views/homePage.ftl`:
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    <p>This is Microweb generated Home Page!</p>
    <#if logged>
    <!--
        ...
        ...
        Code for the logged user - maintain what has been presented before
         -->
    <#else>
        <p>Login <a href="/auth/realms/microweb/protocol/openid-connect/auth?scope=openid&response_type=code&client_id=microwebsampleapp&redirect_uri=http:%2F%2Fwww.sample.microweb:9080%2Fv0%2Ffinish-login&state=000001">here</a>.</p>
    </#if>
</body>
</html>
```

As can be seen, login form is not needed anymore. It can just be removed.

The new login link redirects the user to KeyCloak Microweb realm login. The user logs in the realm, and he/she can access all clients registered on that realm, depending on his/her permissions.

Some observations must be noted:

1. The login URL is from KeyCloak, and if it must be customized, it must be customized on KeyCloak. Such customization is beyond the scope of this document.
2. The login procedure performed by the user, on KeyCloak, represents the first step of the required two-factor authentication.
3. The `redirect_uri` parameter in the link above must fulfull the redirection criterion of Microweb realm. In this sample, it's hard-coded, but it can be obtained from a configuration, from Javascript redirection or form, or anything correspondent. The redirection URL must be an absolute url (not a relative one).
4. To explain the details about the login URL is beyond the scope of this tutorial. Just read KeyCloak, OpenID or OAuth2 documentation about it.

Now we need to implement the login finish redirection route. This route will implement the second step for two-factor authentication, validating the application with its secret.

Before implementing this route and necessary business rules, let's create two new configurations. Just add them to the `configuration` table, as show in the SQL script below:

```sql
insert into configuration (name, value) values ('keycloak client redirect uri', 'http://www.sample.microweb:9080/v0/finish-login');
insert into configuration (name, value) values ('keycloak client application secret', '<your KeyCloak client secret>');
insert into configuration (name, value) values ('server backend resource', 'http://www.sample.microweb:9080');
```

You can find your application secret looking for the `microwebsampleapp` credentials, as show below. Your client authentication option must be __Client Id and Secret__:

![Client Id and Secret](microwebsampleapp-find-secret.png)

Then register the new route at `App` class.

__File__ `/src/main/java/microweb/sample/App.java`, method `App.initialization`:
```java
// On imports, add:
import com.ultraschemer.microweb.controller.FinishConsentController;

    @Override
    public void initialization() throws Exception {
        getRouter().route("/static/*").handler(StaticHandler.create());

        // Register controllers:

        // Finish login authentication:
        registerController(HttpMethod.GET, "/v0/finish-login", new FinishLoginController());

        // Default finish consent call:
        registerController(HttpMethod.GET, "/v0/finish-consent", new FinishConsentController());

        //
        // The remainder of the method is maintained as it was before.
        //
        // ...
        // ...
    }
```

And implement this controller:

__File__ `/src/main/java/microweb/sample/controller/FinishLoginController.java`:
```java
package microweb.sample.controller;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.PermissionManagement;

public class FinishLoginController extends SimpleController {
    public FinishLoginController() {
        super(500, "44154235-fd79-4487-9092-56e9e280e2d5");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();
        PermissionManagement.finishLogin(request.getParam("state"),
                request.getParam("session_state"),
                request.getParam("code"), (JsonObject res, StandardException se) -> {
                    try {
                        // Vert.X has a bug which prevents setting multiple cookies simultaneously, so we create a simple page
                        // to set the cookies and, then, redirect itself to home page:
                        response.setStatusCode(200)
                                .putHeader("Content-type", "text/html")
                                .end("<html><head>" +
                                        "<title>Microweb login</title>" +
                                        "<head>" +
                                        "<body>Logging in..." +
                                        "<script language=\"javascript\">" +
                                        "document.cookie = \"Microweb-Access-Token=" + "" + "; path=/;\";" +
                                        "document.cookie = \"Microweb-User-Id=" + "" + "; path=/;\";" +
                                        "document.cookie = \"Microweb-Refresh-Token=" + "" + "; path=/;\";" +
                                        "document.cookie = \"Microweb-User-Name=" + "" + "; path=/;\";" +
                                        "window.location.replace(\"/v0\");" +
                                        "</script>" +
                                        "</body>" +
                                        "</html>");
                    } catch (Throwable e) {
                        response.setStatusCode(401)
                                .putHeader("Content-type", "text/html")
                                .end("<html><body>Authorization error:<br/>" +
                                        Throwables.getStackTraceAsString(e) + "</body></html>");
                    }
                });
    }
}
```

Since we won't use custom made permission management, but we'll use KeyCloak, then we can rewrite `PermissionManagement` business class to reflect our new reality:

__File__ `src/main/java/microweb/sample/controller/PermissionManagement.java`:
```java
package microweb.sample.domain;

import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;
import com.ultraschemer.microweb.domain.Configuration;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import io.vertx.core.json.JsonObject;
import microweb.sample.domain.error.FinishAuthenticationConsentException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Objects;
import java.util.function.BiConsumer;

public class PermissionManagement {
    private static OkHttpClient client = new OkHttpClient();

    public static void finishLogin(String state, String sessionState, String code,
                                   BiConsumer<JsonObject, StandardException> callResult) {
        // Since this method uses an internal REST call recursively, call it asynchronously, to avoid to block
        // Vert.X event queue (A Vert.X future can be used here):
        new Thread(() -> {
            try {
                Request clientRequest = new Request.Builder()
                        .url(Configuration.read("server backend resource") +
                                "/v0/finish-consent?" +
                                "state=" + state + "&" +
                                "session_state=" + sessionState + "&" +
                                "code=" + code + "&" +
                                "redirect_uri=" + Configuration.read("keycloak client redirect uri") + "&" +
                                "client_secret=" + Configuration.read("keycloak client application secret") + "&" +
                                "client_id=" + Configuration.read("keycloak client application"))
                        .build();

                try (Response response = client.newCall(clientRequest).execute()) {
                    if (response.code() <= 299) {
                        JsonObject res = new JsonObject(Objects.requireNonNull(response.body()).string());

                        // Locate user from returned data, evaluating the permission of an ALWAYS permitted resource:
                        User u = CentralUserRepositoryManagement.evaluateResourcePermission("GET", "/v0/logoff",
                                "Bearer " + res.getString("access_token"));
                        res.put("Microweb-User-Id", u.getId().toString());
                        res.put("Microweb-User-Name", u.getName());
                        res.put("Microweb-Central-Control-User-Id", u.getCentralControlId().toString());

                        callResult.accept(res, null);
                    } else {
                        callResult.accept(null, new FinishAuthenticationConsentException("Unable to finish client authentication consent: " +
                                Objects.requireNonNull(response.body()).string()));
                    }
                } catch (Exception e) {
                    callResult.accept(null, new FinishAuthenticationConsentException("Unable to finish client authentication consent.", e));
                }
            } catch (Exception e) {
                callResult.accept(null, new FinishAuthenticationConsentException("Unable to finish client authentication consent", e));
            }
        }).start();
    }

    public static void logoff(String refreshToken, String accessToken, BiConsumer<JsonObject, StandardException> callResult) {
        new Thread(() -> {
            try {
                FormBody body = new FormBody.Builder()
                        .add("client_id", Configuration.read("keycloak client application"))
                        .add("client_secret", Configuration.read("keycloak client application secret"))
                        .add("refresh_token", refreshToken)
                        .build();
                Request clientRequest = new Request.Builder()
                        .url(CentralUserRepositoryManagement.wellKnown().getString("end_session_endpoint"))
                        .post(body)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();
                try (Response response = client.newCall(clientRequest).execute()) {
                    if (response.code() <= 299) {
                        if(response.code()!=204) {
                            JsonObject res = new JsonObject(Objects.requireNonNull(response.body()).string());
                            callResult.accept(res, null);
                        } else {
                            callResult.accept(new JsonObject(),null);
                        }
                    } else {
                        callResult.accept(null, new FinishAuthenticationConsentException("Unable to finish client authentication consent: " +
                                Objects.requireNonNull(response.body()).string()));
                    }
                } catch(Exception e) {
                    callResult.accept(null, new LogoffException("Unable to perform logoff.", e));
                }
            } catch(Throwable t) {
                callResult.accept(null, new LogoffException("Unable to perform logoff.", t));
            }
        }).start();
    }
}
```

The Business Class above has a tricky asynchronous implementation because it calls another Microweb Sample route using HTTP Rest (`GET /v0/finish-consent`). __Only asynchronous operations can perform this type of self-referencing call. If a synchronous business call perform such self-referencing call, Vert.X event queue will hang up.__

We implemented a Logoff business routine too, which needs the next exception class:

__File__ `src/main/java/microweb/sample/domain/error/LogoffException.java`:
```java
package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class LogoffException extends StandardException {
    public LogoffException(String message, Throwable cause) {
        super("31ae70ca-35fc-4ba6-9b61-aafcf0078bb5", 500, message, cause);
    }
}
```

Now we have a complete login process, with two-factor evaluation, but we have a problem in the `DefaultHomePageController` class: it calls Microweb `AuthManagement.authorize` business call, which is incompatible to KeyCloak OpenId Microweb implementation. We must replace its call by other suitable calls, to load the application home page correctly.

The correction is shown below:

__File__ `src/main/java/microweb/sample/controller/DefaultHomePageController.java`, method `executeEvaluation`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.ImageManagement;
import microweb.sample.domain.bean.ImageListingData;
import microweb.sample.view.FtlHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultHomePageController extends SimpleController {
    // C.1: Create a static instance of the default home page template variable, to store and cache it:
    private static Template homePageTemplate = null;

    // C.2: Initialize the template defined above, suitably:
    static {
        try {
            homePageTemplate = FtlHelper.getConfiguration().getTemplate("homePage.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    // C.3: Define the default controller constructor:
    public DefaultHomePageController() {
        super(500, "a37c914b-f737-4a73-a226-7bd86baac8c3");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        // Define homepage data model:
        Map<String, Object> homepageDataRoot = new HashMap<>();

        // Load user cookie:
        HttpServerRequest request = routingContext.request();
        Cookie authorizationToken = request.getCookie("Microweb-Access-Token");
        Cookie userId = request.getCookie("Microweb-User-Id");

        // Populate Homepage data model:
        if(authorizationToken != null) {
            // Get user data:
            UserData u = UserManagement.loadUser(userId.getValue());

            // Load images, if they are available for this user:
            ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), routingContext.vertx());
            List<ImageListingData> imageListingData = imageManagement.list(u.getId());

            // Load users, to assign images to them:
            List<UserData> users = UserManagement.loadUsers(1000, 0);
            users.sort(Comparator.comparing(UserData::getName));

            homepageDataRoot.put("logged", true);
            homepageDataRoot.put("user", u);
            homepageDataRoot.put("images", imageListingData);
            homepageDataRoot.put("users", users);
        } else {
            homepageDataRoot.put("logged", false);
        }

        // C.4: In the controller evaluation routine, render the template:
        routingContext
                .response()
                .putHeader("Content-type", "text/html")
                .putHeader("Cache-Control", "no-cache")
                .end(FtlHelper.processToString(homePageTemplate, homepageDataRoot));
    }
}
```

We need to adjust logoff controller too, with this new code:

__File__ `src/main/java/microweb/sample/controller/GuiUserLogoffProcessController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.CentralUserRepositoryAuthorizedController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.PermissionManagement;

public class GuiUserLogoffProcessController extends CentralUserRepositoryAuthorizedController {
    public GuiUserLogoffProcessController() {
        super(500, "eb474551-42d5-4452-be4f-4875d525b993");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        String token = context.getCookie("Microweb-Access-Token").getValue();
        String refreshToken = context.getCookie("Microweb-Refresh-Token").getValue();

        PermissionManagement.logoff(refreshToken, token, (JsonObject j, StandardException se) -> {
            asyncEvaluation(500, "8ada30f6-e400-4994-9c2e-cd41df80439f", context, () -> {
                // Delete all cookies:
                response.setStatusCode(200)
                        .putHeader("Content-type", "text/html")
                        .end("<html><head>" +
                                "<title>Microweb login</title>" +
                                "<head>" +
                                "<body>Logging in..." +
                                "<script language=\"javascript\">" +
                                "document.cookie = \"Microweb-Access-Token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "document.cookie = \"Microweb-User-Id=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "document.cookie = \"Microweb-Central-Control-User-Id=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "document.cookie = \"Microweb-Refresh-Token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "document.cookie = \"Microweb-User-Name=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "window.location.replace(\"/v0\");" +
                                "</script>" +
                                "</body>" +
                                "</html>");
            });
        });
    }
}
```

Other business classes and methods are also problematic. The refresh of access key must be implemented. The assignment of roles to users can't be made using internal Microweb business calls anymore. It must be made by KeyCloak - so you need to call KeyCloak to timplement this operation. It means that the business call called by `GuiAssignRoleController`, which is `UserManagement.setRoleToUser`, can't be used anymore. It must be replaced by a suitable call to KeyCloak REST API. If you analyse the class `CentralUserRepositoryManagement` you'll se how to perform such calls, and reimplement `GuiAssignRoleController` with the correct business call implementation (these will need to be implemented from scratch, since `CentralUserRepositoryManagement` doesn't have any routine to assign roles to users) is let as an exercise to the reader. Other user data updates also need to be altered to use `CentralUserRepositoryManagement` features, or call KeyCloak directly. An example of this is the password change API calls.

The `ImageManagement` business class must be corrected too:

__File__ `src/main/java/microweb/sample/domain/ImageManagement.java`, method `list()`:
```java
    public List<ImageListingData> list(UUID userId) throws StandardException {
        try(Session session = openTransactionSession()) {
            User user = session.createQuery("from User where id = :uid", User.class)
                    .setParameter("uid", userId).getSingleResult();

            List<ImageListingData> res = new LinkedList<>();

            List<User_Image> accessibleImageList =
                    session.createQuery("from User_Image where userId = :uid", User_Image.class)
                            .setParameter("uid", user.getId())
                            .list();
            HashMap<UUID, User_Image> userImageMap = new HashMap<>();
            accessibleImageList.forEach((i) -> userImageMap.put(i.getImageId(), i));

            List<Image> allImages;
            if(accessibleImageList.size() > 0) {
                allImages = session.createQuery("from Image where id in :iid or ownerUserId = :oid order by ownerUserId", Image.class)
                        .setParameterList("iid", accessibleImageList.stream().map(User_Image::getImageId).collect(Collectors.toList()))
                        .setParameter("oid", user.getId())
                        .list();
            } else {
                allImages = session.createQuery("from Image where ownerUserId = :oid order by ownerUserId", Image.class)
                        .setParameter("oid", user.getId())
                        .list();
            }

            UUID ownerUserId = null;
            String ownerUserName = null;
            for(Image i: allImages) {
                if(!i.getOwnerUserId().equals(ownerUserId)) {
                    User u = session.createQuery("from User where id = :uid", User.class)
                            .setParameter("uid", i.getOwnerUserId())
                            .getSingleResult();
                    ownerUserId = u.getId();
                    ownerUserName = u.getName();
                }

                ImageListingData imageListingData = new ImageListingData();
                imageListingData.setId(i.getId());
                imageListingData.setName(i.getName());
                imageListingData.setOwnerId(ownerUserId);
                imageListingData.setOwnerName(ownerUserName);
                imageListingData.setCreatedAt(i.getCreatedAt());
                if(userImageMap.containsKey(i.getId())) {
                    User_Image ui = userImageMap.get(i.getId());
                    imageListingData.setAlias(ui.getAlias());
                }
                res.add(imageListingData);
            }

            // Set return data in creation order:
            res.sort(Comparator.comparing(ImageListingData::getCreatedAt));

            return res;
        } catch(PersistenceException pe) {
            throw new ImageManagementListingException("Unable to list images.", pe);
        }
    }
```

To avoid problems with user management calls, let's change the `userManagementPage.flt` template to hide user role attribution calls:

__File__ ``:
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    <p>User Management</p>
    <p>Welcome <strong>${user.name}</strong>!</p>
    <p>Return to <a href="/v0">home</a></p>
    <hr/>
    <form method="post" action="/v0/gui-user">
        <table>
            <tr>
                <td>Name:</td>
                <td style="padding-left: 10px"><input type="text" name="name"/></td>
            </tr>
            <tr>
                <td>Password:</td>
                <td style="padding-left: 10px"><input type="password" name="password"/></td></tr>
            <tr>
                <td>Password confirmation:</td>
                <td style="padding-left: 10px"><input type="password" name="passConfirmation"/></td>
            </tr>
            <tr>
                <td>Given name:</td>
                <td style="padding-left: 10px"><input type="text" name="givenName"/></td>
            </tr>
            <tr>
                <td>Family name:</td>
                <td style="padding-left: 10px"><input type="text" name="familyName"/></td>
            </tr>
            <tr>
                <td>Role:</td>
                <td style="padding-left: 10px">
                    <select name="role">
                        <#list roles as r>
                        <option value="${r.name}">${r.name}</option>
                        </#list>
                    </select>
                </td>
            </tr>
        </table>
        <input type="submit" value="Create"/>
    </form>
    <hr/>
    <table style="width: 100%">
        <tr>
            <td>Name:</td>
            <td>Roles:</td>
        </tr>
        <#list users as u>
            <tr>
                <td>${u.name}</td>
                <td>
                    <#list u.roles as r>
                        <strong style="color: gray">[</strong>${r.name}<strong style="color: gray">]</strong>&nbsp;
                    </#list>
                </td>
            </tr>
        </#list>
    </table>
</body>
</html>
```

And let change the user addition controllers, to use `CentralUserRepositoryManagement` instead of `UserManagement` business class:

__File__ `src/main/java/microweb/sample/controller/GuiCreateUserController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.controller.bean.CreateUserData;
import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;

public class GuiCreateUserController extends SimpleController {
    public GuiCreateUserController() {
        super(500, "63ccb2ee-3c99-4b20-91d4-bb521f4945dd");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();

        if(request.getHeader("Content-Type").trim().toLowerCase().startsWith("application/json")) {
            CreateUserData userData = Json.decodeValue(context.getBodyAsString(), CreateUserData.class);
            Validator.ensure(userData);

            CentralUserRepositoryManagement.registerUser(context.get("user"), userData, Collections.singletonList(request.getFormAttribute("role")));

            response.setStatusCode(204).end();
        } else {
            // Get form data:
            CreateUserData userData = new CreateUserData();
            userData.setName(request.getFormAttribute("name").toLowerCase());
            userData.setAlias(userData.getName());
            userData.setPassword(request.getFormAttribute("password"));
            userData.setPasswordConfirmation(request.getFormAttribute("passConfirmation"));
            userData.setGivenName(request.getFormAttribute("givenName"));
            userData.setFamilyName(request.getFormAttribute("familyName"));

            Validator.ensure(userData);

            CentralUserRepositoryManagement.registerUser(context.get("user"), userData, Collections.singletonList(request.getFormAttribute("role")));

            // Redirect to users management interface:
            response.putHeader("Content-type", "text/html")
                    .putHeader("Location", "/v0/gui-user-management")
                    .setStatusCode(303)
                    .end();
        }
    }
}    
```

__File__ `src/main/java/microweb/sample/App.java`, method `initialization`:
```java
    // Change this:
    // registerController(HttpMethod.POST, "/v0/user", new UserCreationController());
    // To this:
    registerController(HttpMethod.POST, "/v0/user", new GuiCreateUserController());
```

_Obs.: Microweb synchronizes `user_` and `role` tables as users execute REST calls on system. To load roles and users from Microweb local database doesn't ensure the most recent data will be retrieved. You can create a process to update regularly these information or create a process to force users to log in the system after their registration._

After these changes, the sample application has been adapted to use OpenID and import all KeyCloak features, including user federation, integration with Social Networks, and permission control.

Go to KeyCloak, create users, assign roles to them, and test the application with these users.

### 5.2.5. Using Microweb as middleware to internal microservices

The last feature to be present in this README is the use of Microweb as Middleware for internal REST services.

Why to use this framework in this fashion? Because not all technologies are best implemented in all available platforms and programming languages. REST architecture permit the creation of loose-coupled services which can know each other if we have the correct middleware, and registration services.

Registration services depend on system architecture, and this problem is far beyond the scope of Microweb and this README. But Microweb permit the registration of external REST services as routes, and these routes will be subject to the same OpenID permission control as any other REST service implemented as a Microweb controller.

Since these services are REST, they're restricted to these five methods:

* `GET`
* `POST`
* `PATCH`
* `PUT`
* `DELETE`

As an extra restriction, to register services programatically, the entity-body also need to be representable by a UTF-8 string (so all `multipart/form-data` requests aren't supported).

Obviously, these restrictions are not suitable to all needs.


To support extra needs, the reverse proxy packed with Microweb can be used as middleware to other services, as well, and these are subject to Permission Control and filtering as any other service implemented directly using Microweb and Vert.X routing. When the reverse proxy is used, no restriction on HTTP methods exist, and full flexibility is available.

These features are powerful, but they're simple, not needing extense explanation as the MVC library provided by the Framework. We'll start understanding the REST Routing based reverse proxy, in section __5.2.5.1__, below, and then, we proceed to the complete reverse proxy, in section __5.2.5.2__, after.

#### 5.2.5.1. Implementing authorized REST proxies programatically

What if we need a technology we find an implementation in another platform than Java, but yet suitable to our needs? It's simple, we just create a REST microservice with that technology, and then register such service in a Microweb application using one of these two Controller classes:

1. __`SimpleServerProxyController`__: This class is correspondent to the `SimpleController` class, but while `SimpleController`, when specialized, permit the creation of a Java Controller, `SimpleServerProxyController` redirects any call to it to an external REST server, receiving the same URI, method, EntityBody and Headers from the original call. If a SimpleServerProxyController receives a `GET /abc/def?a=b` call, it will redirect this exact call (the same URI, the same method, the same query-string) to an external service registered in this `simpleServerProxyController` instance. Since this class is correspondent to `SimpleController`, it is not subject to OpenID permission evaluation. 
2. __`CentralAuthorizedServerProxyController`__: This class is correspondent to the `CentralUserRepositoryAuthorizedController` class, so its calls are subject to OpenID permission evaluation. All other features are exactly equals to that available by `SimpleServerProxyController`.

Both classes are available in the `com.ultraschemer.microweb.proxy` package.

One interesting feature of __all routes__ registered in Microweb Sample is that they receive a __versioning prefix__ (`/v0`), with the very exception of the path `/`. It means that if we have any service path which doesn't start with `/v0/`, or doesn't match with the pattern `^\/v[0-9]+\/.*$` or it isn't `/`, it isn't implemented as an internal Microweb Sample REST service path.

This feature opens lots of possibilities of use of external microservices in the system.

Microweb standard database is PostgreSQL. There is a project called __[PostgREST](http://postgrest.org)__ which enable a full REST interface for a PostgreSQL database. Do you remember when it was said REST doesn't have default searching facilities? PostgREST implements very powerful REST search for any database implemented on PostgreSQL. We can just reuse PostgREST in our project, and provide search faciltiies to our users.

But PostgREST is implemented in __Haskell__.

It's no problem for Microweb. Since PostgREST is REST, we can register it as a Microweb microservice, and put it under KeyCloak versioning control, accessible to any client of Microweb Sample client.

Let's start assuming we just want to provide an Image Search to our users, so we'll configure PostgREST, and expose only one entity search: `image`. It'll be read only, by PostgREST configuration, and restricted to users with `user-manager-api` permissions. We don't want a user can search images from other users (_Obs.: we can implement a filter to restrict such searches, but this implementation is not objective of this README_).

##### 5.2.5.1.1. Installing and configuring PostgREST

[Install PostgREST](http://postgrest.org/en/v7.0.0/tutorials/tut0.html) on your system, and let it available in __PATH__, sou you can call the executable in the command line.

Prepare this configuration file:

__File__ `microwebsample.conf`:
```ini
db-uri       = "postgres://microwebsample:microwebsample@localhost:5432/microwebsample"
db-schema    = "public"

# Anonymous access = full access - service available only internally, controlled by Security Groups
db-anon-role = "microwebsample" 

# Limit the number of rows
max-rows = 1024

# Access and servers:
server-host = "*"
server-port = 9580
```

Then run the service:

```sh
$ postgrest microwebsample.conf
Listening on port 9580
Attempting to connect to the database...
Connection successful
```

I've added some images to database, then I can access them directly, using PostgREST:

```sh
$ curl 'http://localhost:9580/image?name=fts.delicate.png&select=id,name'
[{"id":"717b2f6c-44ba-4275-b686-cc5c6ffa185a","name":"delicate.png"}]

$ curl "http://localhost:9580/image?select=id,name"
[{"id":"717b2f6c-44ba-4275-b686-cc5c6ffa185a","name":"delicate.png"},
 {"id":"04766886-f9be-4af4-83d2-d253a7bf869b","name":"fdsa.png"},
 {"id":"420a9b69-a7bc-4dd7-93bd-9524333798c1","name":"oura.jpg"}]
```

##### 5.2.5.1.2. Registering PostgREST `image` search as Microweb route

We can add the route `GET /image` in the project, specializing the class `CentralAuthorizedServerProxyController` and creating a proxy redirector class:

__File__ `src/main/java/microweb/sample/controller/PostgRESTRedirectionController.java`:
```java
package microweb.sample.controller;

import com.ultraschemer.microweb.domain.Configuration;
import com.ultraschemer.microweb.proxy.CentralAuthorizedServerProxyController;

public class PostgRESTRedirectionController extends CentralAuthorizedServerProxyController {
    public PostgRESTRedirectionController() {
        super(500, "150e7454-2754-4270-bf0a-a70958cf17ea");
    }

    @Override
    protected String getServerAddress() throws Throwable {
        String postgrestAddress = Configuration.read("PostgREST address");
        if(postgrestAddress.equals("")) {
            return "http://localhost:9580";
        }
        return postgrestAddress;
    }
}
```

And registering the image search route in the `App.initialization` method:

__File__ `src/main/java/microweb/sample/App.java`, method `initialization`
```java
    @Override
    public void initialization() throws Exception {
        getRouter().route("/static/*").handler(StaticHandler.create());

        // Register controllers:

        // Finish login authentication:
        registerController(HttpMethod.GET, "/v0/finish-login", new FinishLoginController());

        // Default finish consent call:
        registerController(HttpMethod.GET, "/v0/finish-consent", new FinishConsentController());

        // User access controllers:
        registerController(HttpMethod.GET, "/v0/gui-user-login", new GuiUserLoginViewController());
        registerController(HttpMethod.POST, "/v0/gui-user-login", new GuiUserLoginProcessController());
        registerController(HttpMethod.GET, "/v0/gui-user-logoff", new GuiUserLogoffProcessController());

        // Image manipulation:
        registerController(HttpMethod.POST, "/v0/gui-image/:id/assign", new GuiImageAssignController());
        registerController(HttpMethod.GET, "/v0/gui-image/:id/raw", new GuiImageRawDataController());
        registerController(HttpMethod.POST, "/v0/gui-image", new GuiImageCreationController());

        // User management:
        registerController(HttpMethod.GET, "/v0/gui-user-management", new GuiUserManagementController());
        registerController(HttpMethod.POST, "/v0/gui-user/:id/role", new GuiAssignRoleController());
        registerController(HttpMethod.POST, "/v0/gui-user", new GuiCreateUserController());

        // REST API calls:
        registerController(HttpMethod.POST, "/v0/user", new GuiCreateUserController());
        registerController(HttpMethod.GET, "/v0/user", new UserController());
        registerController(HttpMethod.GET, "/v0/user/:userIdOrName", new OtherUsersController());
        registerController(HttpMethod.PATCH, "/v0/user/:id/password", new UserPasswordUpdateController());
        registerController(HttpMethod.POST, "/v0/image", new ImageCreateController());
        registerController(HttpMethod.PUT, "/v0/image/:id/link", new ImageUserLinkController());

        // Default system home page handling:
        registerController(HttpMethod.GET, "/v0", new DefaultHomePageController());

        //
        // Register calls to external microservices HERE:
        //
        registerController(HttpMethod.GET, "/image", new PostgRESTRedirectionController());

        // At last step, register the route "GET /", which is the most generic one:
        registerController(HttpMethod.GET, "/", new DefaultHomePageController());

        RegisteredReverseProxy proxy = new RegisteredReverseProxy(9080);
        proxy.registerPath("^\\/auth.*$", "localhost:8080");
        proxy.registerPath("^\\/v0.*$", "localhost:48080" );
        proxy.registerPath("^\\/$", "localhost:48080");
        proxy.run();
    }
```

_Obs.: If you want to add a route without permission control, you can use the `SimpleServerProxyController` as the superclass of your specialized proxy class. If you're using simple user management, as in the sample project from section `5.1`, you'll also use the `SimpleServerProxyController` class as the superclass of your proxy redirector. The class specialization procedure is essentially equals as that shown above._

The redirection can be made to any HTTP path supported by our PostgREST instance, since methods are `GET`, `POST`, `PUT`, `DELETE` and `PATCH`, and the entity body can be represented by a UTF-8 string.

If a configuration called `PostgREST address` doesn't exist in table `configuration`, then the proxy redirector will call `http://localhost:9580`, which works as a default address for PostgREST.

For a client accessing Microweb Sample application, the `GET /image` resource works exactly equals any other resource, being developed by Microweb or not.

Now you need to go to KeyCloak, enter as __microwebadmin__ user, create a resource called `GET /image#` and assign it to the scope `user-manager-api`, as planned before.

#### 5.2.5.2. Implementing complete reverse proxies

Previously we saw how to register in Microweb HTTP REST services implemented elsewhere, specializing the proxy controllers (`SimpleServerProxyController` and `CentralAuthorizedServerProxyController` classes), but they have severe limitations:

* Only supoort methods `GET`, `POST`, `PUT`, `PATCH` and `DELETE`.
* Entity body in all calls must be representable by a UTF-8 valid string.
* Each service must be registered individually.

And what if we need to register a service that receives uploaded images or video? Obviously, we can convert these data to text-compatible representation (like Base64 or ASCII85), but this approach is not exacly space and processing efficient.

To support this situations, Microweb packs one more class, which is a specialization of the already packed reverse proxy: `CentralAuthorizedRegisteredReverseProxy`, that extends `RegisteredReverseProxy`.

Both classes, above, work exactly equals to each other, but `CentralAuthorizedRegisteredReverseProxy` implements the request permission filter `RegisteredReverseProxy` provides, and this implementation delegate to KeyCloak the permission evaluation needed to each resource redirected and filtered by its instances.

Using `RegisteredReverseProxy` we already shown how to attach any HTTP service to a Microweb microservice. It has been done with KeyCloak itself, which has been completely integrated to Microweb Sample project through the routes at the `^\/auth.*$` paths. With `CentralAuthorizedRegisteredReverseProxy`, we go beyond, and all paths registered on its instance will be filtered and sent to KeyCloak for permission evaluation.

Now, let's change the reverse proxy implementation in the `App.initializationmethod` to support the permission filtered reverse proxy:

__File__ `src/main/java/microweb/sample/App.java`, method `initialization`
```java
        @Override
    public void initialization() throws Exception {
        getRouter().route("/static/*").handler(StaticHandler.create());

        // Register controllers:

        // Finish login authentication:
        registerController(HttpMethod.GET, "/v0/finish-login", new FinishLoginController());

        // Default finish consent call:
        registerController(HttpMethod.GET, "/v0/finish-consent", new FinishConsentController());

        // User access controllers:
        registerController(HttpMethod.GET, "/v0/gui-user-login", new GuiUserLoginViewController());
        registerController(HttpMethod.POST, "/v0/gui-user-login", new GuiUserLoginProcessController());
        registerController(HttpMethod.GET, "/v0/gui-user-logoff", new GuiUserLogoffProcessController());

        // Image manipulation:
        registerController(HttpMethod.POST, "/v0/gui-image/:id/assign", new GuiImageAssignController());
        registerController(HttpMethod.GET, "/v0/gui-image/:id/raw", new GuiImageRawDataController());
        registerController(HttpMethod.POST, "/v0/gui-image", new GuiImageCreationController());

        // User management:
        registerController(HttpMethod.GET, "/v0/gui-user-management", new GuiUserManagementController());
        registerController(HttpMethod.POST, "/v0/gui-user/:id/role", new GuiAssignRoleController());
        registerController(HttpMethod.POST, "/v0/gui-user", new GuiCreateUserController());

        // REST API calls:
        registerController(HttpMethod.POST, "/v0/user", new GuiCreateUserController());
        registerController(HttpMethod.GET, "/v0/user", new UserController());
        registerController(HttpMethod.GET, "/v0/user/:userIdOrName", new OtherUsersController());
        registerController(HttpMethod.PATCH, "/v0/user/:id/password", new UserPasswordUpdateController());
        registerController(HttpMethod.POST, "/v0/image", new ImageCreateController());
        registerController(HttpMethod.PUT, "/v0/image/:id/link", new ImageUserLinkController());

        // Default system home page handling:
        registerController(HttpMethod.GET, "/v0", new DefaultHomePageController());

        // Remove this path, because it will be handled by the reverse proxy:
        // Register calls to external microservices:
        // registerController(HttpMethod.GET, "/image", new PostgRESTRedirectionController());

        // At last step, register the route "GET /", which is the most generic one:
        registerController(HttpMethod.GET, "/", new DefaultHomePageController());

        CentralAuthorizedRegisteredReverseProxy proxy = new CentralAuthorizedRegisteredReverseProxy(9080);

        // KeyCloak and Microweb own paths aren't evaluated by the reverse proxy,
        // since they have their own Permission control:
        proxy.registerPath("^\\/auth.*$", "localhost:8080");
        proxy.registerPath("^\\/v0.*$", "localhost:48080");

        // Add any generic search path to PostgREST, with the exception of "/", and enable Permission filtering on them:
        proxy.registerPath("^\\/.+$", "localhost:9580", true);

        // "/", being the most generic address, continue to be handled by this application:
        proxy.registerPath("^\\/$", "localhost:48080");
        proxy.run();
    }
```

With these changes, all calls to PostgREST will be mediated by the Reverse Proxy, but not by the custom Proxy Controllers. Any HTTP call can be controlled and have its permissions evaluated in this way.

Now, you can customize the sample application to your needs, or correct all of its bugs, or incomplete features, as an exercise.

The most important Microweb features have been presented.

The OpenID sample can be found [here](https://github.com/ultraschemer/microweb-sample-openid).

# 6. Conclusions and Next Steps

This README and Tutorial has the objective to present Microweb as an MVC framework, with OpenID permission control enabled and as a framework to register REST and HTTP services developed in multiple technologies, under SOA architecture.

Microweb doesn't aim to be the best implementation of these architectures, nor to be the ultimate MVC, OpenID or SOA middleware. It aims to provide Vert.X integration with OpenID, through KeyCloak, and to be the base of REST services in heterogenous environments.

This project is being published to be useful. Any help to improve the framework is welcome, and if Microweb is useful to you, let us know.

Thank you if you reached this point of the text.

Any bug or problem, just open a ticket in the project issue tracker.
