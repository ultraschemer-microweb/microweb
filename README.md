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

The main current limitation of microweb is that it's exclusive to PostgreSQL database. It has been adapted to Oracle, but not to other databases, but it can be, if demanded.

# Using Microweb

Microweb is a project template, not a Library. It's made to be altered and improved by the uers. So, to use it, download it from Git, and personalized it:

```bash
git clone git@github.com:ultraschemer/microweb.git
rm -Rf .git # to unlink the project from its previous repository
sed -i 's/microweb/<your-project-name-here>/g' settings.gradle
grep -i 's/microweb/<your-database-here>/g' database-sql/database-finalization.sh
grep -i 's/microweb/<your-database-here>/g' database-sql/database-initialization.sh
grep -i 's/microweb/<your-database-here>/g' database-sql/generate-database.sh
grep -i 's/microweb/<your-database-here>/g' src/main/resources/hibernate.cfg.xml
```

These personalizations will assume the creation and configuration of a database in PostgreSQL which name and password is equals to the _&lt;your-database-here&gt;_ input, given above.
