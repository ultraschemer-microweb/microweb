# 1. MicroWeb

Microweb is HTTP Rest/Web framework, which brings MVC concepts to Vert.x, and focus in being very lightweight, reactive and with intgrated user management support, with OpenId and permissions control, provided by KeyCloak.

The __definitive__ use case for Microweb is the development of __distributed microservices__ with strict user management control.

Microweb has these characteristics:

* Stateless enforcing architecture, to support clusters transparently
* Strong separation between View, Controller and Business Domain code layers, but with no hard enforcement over this
* No default view templating support
* Default Exception mapping and handling
* Standard Object-Oriented/Relational mapping, using Hibernate
* Default configuration handling
* Default entity logging on register creation and updating
* Default simple user management
* Default abstractions for Filter and Request Controllers.
* Distributed register lockers, to define distributed critical sections
* Default entity search routines
* Logic Big Integer sequence implementation
* Intrinsic support to OpenId and Resource Permission, using KeyCloak

The main advantages of microweb, currently, are:

* Highly performant on limited environments: in Windows, 32Bit JVM, the entire system (without Keycloak) will use around 30MB of RAM Memory.
* Completely asynchronous, but supporting limited syncronicity.
* Full exposure of Vert.x features, including support to Reactive Programming.
* Not very opinionated, apart the rigid separation of Controller and Domain layers.

The main current limitation of microweb is that it's exclusive to PostgreSQL and Oracle databases. It can be ported to other databases, if demanded.

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
  implementation "com.ultraschemer.microweb:microweb:0.3.0"
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
    <version>0.3.0</version>
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

Since this project does not aim to be _very_ opinionated, the user can choose any Templating library to generate the views in his/her projects. By the way, in the code samples in this documentation, we use [FreeMarker](https://freemarker.apache.org/).

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

Microweb uses, necessarily, a Relational Database, which can be PostgreSQL or Oracle. PosgreSQL is advised, since it is the database used to develop the framework. MySQL is not supported (_yet_).

To generate this database, a set of migrations is defined, using [Python 3](https://www.python.org/) and [Alembic](https://alembic.sqlalchemy.org/en/latest/).

If you want to go directly to action, __you can skip the next major document section, 5. Project Samples__. The next subsections explain, conceptually, the Migration technology choice rationale, the Database Structure and how this structure impacts the Entity Package in Microweb projects.

## 4.1. The Rationale over the Microweb Database structure

If a specific database, generated by pre-defined migrations is used by Microweb, it means that Microweb isn't a database agnostic framework, and this database must contain specific tables and relationships.

This is the correct assumption. The main database must be explicitly defined, linked and populated with specific relations and data to serve Microweb.

### 4.1.2. Microweb, Microservices and SOA Architecture

For programmers with no experience in SOA Architecture and Microservices, Microweb dependency on a predefined database can be puzzling, if we say Microweb is not _very_ opinionated and reusable.

The main point is that Microweb projects are __to be used as self-contained microservices in a bigger whole of components__. This predefined default database can be shared with other microservices, but it can be used solely by the developed Microweb service. The reuse and independence is in __architectural__ level, if the whole project is developed using SOA Architecture and microservices.

If you want to use a Microweb service to develop a monolitic project, the database generated by Microweb basic migrations must be the __start__ point of the entire project, as other Frameworks, like __Laravel__ or __Django__ require.

If you have a previous project and you yet want to use Microweb in this project, the framework use must be evaluated case by case. Contact Microweb main developer if you want consulting services on this subject.

### 4.1.3. Migrations - and why Python? And why Alembic?

Why to use a Python based Migration library to provide migrations to Microweb, if do very good and well tested and used Java-based migration tools exist? 

Because Microweb is a Java based framework, not a Java Centric project.

Alembic has been chosen because it provided the best ways to be customized following the defined database architecture used by Microweb.

If you don't want to use Alembic, you can create the first database version, export it entirely as a SQL script, and then import this database script to another migration framework. The most known Java-based migration frameworks ([Liquibase](https://www.liquibase.org/) and [Flyway](https://flywaydb.org/)) both support this approach. You can just create the first database version and then forget Alembic forever.

But this choice brings some constraints: the basic table and relation abstractions Microweb relies are already implemented in the Alembic given structure. You'll need to reimplement them in the Migration tool you choose.

## 4.2. Microweb database concepts and principles

The standard database structure is considered one of the most important features Microweb provides, and this structure and its abstractions are explained here. Before a deep view in the database structure, let's understand the basic architectural principles that guide Microweb Entity layer and database structure.

### 4.2.1. Microweb relational database principles and architecture

__TODO__


# 5. Project Samples

Two project samples are used to show how Microweb works. The first project is a simple user management project, without OpenID support. The second project is the same project, but with OpenId support, and permissions control.

## Simple user manager system, without OpenID support

__TODO__

## simple user manager system, with OpenID support

__TODO__

### MVC version

__TODO__

### REST api version

__TODO__