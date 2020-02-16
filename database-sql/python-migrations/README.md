# Standard Migrations for Microweb Database

The basic Microweb database is a specific script for PostgreSQL. Unfortunately,
providing script to only one database is not enough, as projects require different
types of database.

This project provides a skeleton structure for Standard Migrations for Microweb,
using Python as the language to customize database.

Other migration tools in Python, exists (as South, from Django), but they're
deeply linked to another frameworks, so their usage has been declined.

Java database migration tools, like Liquibase and Flyway have been evaluated too,
but they seemed less flexible than Alembic, and customizing them are much more
difficult, since they need to be extended using plugins, to customize database
creation and seeding.

# How to load the custom Alembic environment

This project advises the usage of VirtualEnv to manage and use of Migrations.
The project is developed in __Python 3__, and __Python 2__ has no support.

Create the default environment in the directory __<project root>/.venv__:

 $ virtualenv .venv

Then, load the environment and install the requirements:

On windows:
```
 > .venv\Scripts\activate
 > pip install -r requirements.txt
```
On MacOS/Linux/Unix:
```
 $ source .venv/bin/activate
 $ pip install -r requirements.txt
```

That's all, you loaded the Alembic environment to use the database migration
project.

# Customizing the migrations

There are, already, some migrations in the project. You just need to extend them
from this point and customize the __alembic.ini__ file, available in the 
__<project root>__ directory.

The most important customizable initialization variable is the __sqlalchemy.url__
present in the __[alembic]__ block, just in the beginning of the file. Change this
url by that belonging to your database, considering the 
[standard url](https://docs.sqlalchemy.org/en/13/core/engines.html) 
schemas required by SQLAlchemy, the database ORM engine behind Alembic. The 
example given in the file assumes a PostgreSQL database acessible from 
__localhost__, on default port, which name and password are equal to __microweb__.
Customize this value in according to your needs. Don't forget to install the
suitable DBAPI driver you'll need to connect to your database.

After this customization, you can use the migrations in according to the default
instructions found in [Alembic](https://alembic.sqlalchemy.org) web page.
