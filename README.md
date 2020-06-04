# MicroWeb
Microweb is HTTP Rest/Web framework, which brings MVC concepts to Vert.x, and focus in being very lightweight, reactive and with user maangement support, with OpenId and permissions control, provided by KeyCloak. 

Microweb has these characteristics:

* Stateless enforcing architecture, to support clusters transparently
* Strong separation between View, Controller and Business Domain code layers
* No default view templating support
* Default Exception mapping and handling
* Standard Object-Oriented/Relational mapping, using Hibernate
* Default configuration handling
* Default entity logging on register creation and updating
* Default simple user management
* Default abstractions for Filter and Request Controllers.
* Distributed register lockers, to define distributed critical sections
* Default entity search routines
* Logic Big Integer sequences
* Intrinsic support to OpenId and Resource Permission, using KeyCloak

The main advantages of microweb, currently, are:

* Highly performant on limited environments: in Windows, 32Bit JVM, the entire system will use around 30MB of Memory.
* Completely asynchronous, but supporting limited syncronicity.
* Full exposure of Vert.x features, including support to Reactive Programming.
* Not very opinionated, apart the rigid separation of Controller and Domain layers.

The main current limitation of microweb is that it's exclusive to PostgreSQL and Oracle database. it can be ported to other databases, if demanded.

# Adding Microweb to you project

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

# Microweb Concepts

Microweb aims to be a simple library to use. However, it doesn't aim to be a simple library. Microweb uses lots of concepts, and to know these conpects is __really__ necessary to permit a good use of the library.

This section of Microweb documentation describes in details Microweb architectural concepts. Understanding these concepts, Microweb is much more usable.

## Asynchronous and Reactive Programming - and Vert.x

__Microweb__ is built on top of __[Vert.x](https://vertx.io/)__.

When you're using Microweb, if you use it as a simple MVC or REST library, Microweb has no Vert.x flavor. It's easy to forget Microweb is built on top of Vert.x, but the library is not made to encapsulate and hide Vert.x.

You can use Microweb freely, as an MVC library, ignoring completely its Vert.x foundations. But Microweb is __Vert.x__. It means Microweb is __asynchronous__, __concurrent__, __multithreaded__ and __reactive__. To understand these topics is very important to understand exactly how this framework works.

As has been said above, you can abstract Vert.x and its specificities, but you must follow the next rule:

* __All the processing at REST Controllers must be made in, at most, 2 seconds__ - otherwise your program performance will degrade and maybe it can't work correctly in the long run.

It is strongly advised a previous research about, to understand all Microweb details correctly:
* [Vert.x](https://vertx.io)
* [Asynchronous programming](https://vertx.io/docs/guide-for-java-devs/)
* [Reactive programming](https://en.wikipedia.org/wiki/Reactive_programming) - and [Reactive.X](http://reactivex.io/), for extra information.
* Multithreading and Multitasking
* Parallellism and Concurrency
* Clustering

The last three topics are of general knowledge in Computer Sciences and Engineering. It's advised to search about them in Google, or look for good Computer Engineering books.

## The Model/View/Controller architecture and its variants

Microweb can be used as a REST server library or as a full MVC library, to create Web Applications. Microweb, in its core, isn't a full MVC library, since it has no support to a standard view implementation. But, when its architecture is evaluated, it's visible the full support to MVC architecture, since, to generate views, similarly to other MVC frameworks, it's only necessary a Templating Library attached to Microweb.

Since this project does not aim to be opinionated, the user can choose any Templating library to generate the views in his/her projects. By the way, in the code samples in this documentation, we use [FreeMarker](https://freemarker.apache.org/).

It's even possible to use Microweb as an MVC library without a Templating Library, but the HTML text of views would need to be completely generated on fly, and programmatically.

A critique about the MVC architecture is presented below, and the separation between the __domain__ and __entity__ layers, in Microweb projects is rationalized.

### MVC, MVP, MVVM, the _Controller_ and the _Model_ problem

<span style="color: red;">__TODO__</span>

# Project Sample

<span style="color: red;">__TODO__</span>
