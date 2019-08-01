# MicroWeb
Small REST __project template__ based on Vert.x Web.

Microweb brings to Vert.x some MVC concepts, to organize the development of HTTP Rest micro-services.

Microweb has these main characteristics:

* Strong separation and Controllers and Business Domain code layers.
* Default abstractions for Filter and Request Controllers.
* Distributed register lockers, to define distributed critical sections, without locking the database, to group various transactions and queries together.
* Default entity search routines.
* Logic Big Integer sequences.
* Default entity logging on register creation and updating.
* Default simple user management.
* Default Exception mapping and handling.
* Default entity logging.

And, currently, adding migrations, using Liquibase (not implemented, yet).

The main advantages of microweb, currently, are:

* Highly performant on limited environments: in Windows, 32Bit JVM, the entire system will use around 30MB of Memory.
* Completely asynchronous, but supporting limited syncronicity.
* Full exposure of Vert.x features.
* Not very opinionated, apart the rigid separation of Controller and Domain layers.
