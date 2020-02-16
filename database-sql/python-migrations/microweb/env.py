from logging.config import fileConfig

from sqlalchemy import engine_from_config
from sqlalchemy import pool

from alembic import context

# To set register primary key operation:
from alembic.operations import Operations, MigrateOperation

import sqlalchemy as sa

# Custom database types to support UUID:
from sqlalchemy.dialects.oracle import RAW
from sqlalchemy.dialects.postgresql import UUID

# this is the Alembic Config object, which provides
# access to the values within the .ini file in use.
config = context.config

# Interpret the config file for Python logging.
# This line sets up loggers basically.
fileConfig(config.config_file_name)

# add your model's MetaData object here
# for 'autogenerate' support
# from myapp import mymodel
# target_metadata = mymodel.Base.metadata
target_metadata = None

# other values from the config, defined by the needs of env.py,
# can be acquired:
# my_important_option = config.get_main_option("my_important_option")
# ... etc.

def uuid_generate_function(condition):
    if condition not in ['function', 'type']:
        raise Exception("UUID generator only support two options: "
                "'function' or 'type'")
    conn_uri = context.config.get_section_option('alembic', 'sqlalchemy.url')
    if conn_uri.startswith("postgresql"):
        if condition == 'type':
            return UUID
        else:
            return 'uuid_generate_v4()'
    elif conn_uri.startswith("oracle"):
        if condition == 'type':
            return RAW(16)
        else:
            return 'sys_guid()'
    else:
        raise Exception("Unsupported database. Currently only Oracle and "
                "PostgreSQL databases are supported.")

context.uuid = uuid_generate_function

@Operations.register_operation("set_identifiable")
class SetIdentifiable(MigrateOperation):
    """Set UUID primary key in a given table."""

    def __init__(self, table_name, **kw):
        self.table_name = table_name

    @classmethod
    def set_identifiable(cls, operations, table_name, **kw):
        """Set the primary key for a given table."""
        op = SetIdentifiable(table_name, **kw)
        return operations.invoke(op)
    
    def reverse(self):
        return UnsetIdentifiable(self.table_name)

@Operations.register_operation("unset_identifiable")
class UnsetIdentifiable(MigrateOperation):
    """Unset UUID primary key in a given table."""

    def __init__(self, table_name, **kw):
        self.table_name = table_name
    
    @classmethod
    def unset_identifiable(cls, operations, table_name, **kw):
        """Unset the primary key for a given table."""
        op = UnsetIdentifiable(table_name, **kw)
        return operations.invoke(op)
    
    def reverse(self):
        return SetIdentifiable(self.table_name)

@Operations.implementation_for(SetIdentifiable)
def set_identifiable(operations, operation):
    conn_uri = context.config.get_section_option('alembic', 'sqlalchemy.url')
    
    # Add the custom primary key:
    if(conn_uri.startswith('postgresql')):
        operations.execute(
            "alter table %s "
            "add id uuid primary key "
            "default uuid_generate_v4()" % operation.table_name
            )
    elif(conn_uri.startswith('oracle')):
        operations.execute(
            "alter table %s "
            "add id raw(16) default sys_guid() "
            "primary key" % operation.table_name
            )
    else:
        raise Exception('Unsupported database - only Oracle and PostgreSQL are supported currently')

@Operations.implementation_for(UnsetIdentifiable)
def unset_identifiable(operations, operation):
    operations.drop_column(operation.table_name, 'id')

@Operations.register_operation("set_createable")
class SetCreateable(MigrateOperation):
    """Set creation date in a given table."""

    def __init__(self, table_name, **kw):
        self.table_name = table_name

    @classmethod
    def set_createable(cls, operations, table_name, **kw):
        """Set the creation time for a given table."""
        op = SetCreateable(table_name, **kw)
        return operations.invoke(op)
    
    def reverse(self):
        return UnsetCreateable(self.table_name)

@Operations.register_operation("unset_createable")
class UnsetCreateable(MigrateOperation):
    """Unset creation date in a given table."""

    def __init__(self, table_name, **kw):
        self.table_name = table_name

    @classmethod
    def unset_createable(cls, operations, table_name, **kw):
        """Unset the creation time for a given table."""
        op = UnsetCreateable(table_name, **kw)
        return operations.invoke(op)
    
    def reverse(self):
        return SetCreateable(self.table_name)

@Operations.implementation_for(SetCreateable)
def set_createable(operations, operation):
    operations.set_identifiable(operation.table_name)
    operations.add_column(operation.table_name, 
            sa.Column('created_at', sa.types.TIMESTAMP(timezone=True),
                server_default=sa.func.now(), nullable=False))

@Operations.implementation_for(UnsetCreateable)
def unset_createable(operations, operation):
    operations.drop_column(operation.table_name, 'created_at')
    operations.unset_identifiable(operation.table_name)

@Operations.register_operation("set_timeable")
class SetTimeable(MigrateOperation):
    """Set update date in a given table."""

    def __init__(self, table_name, **kw):
        self.table_name = table_name

    @classmethod
    def set_timeable(cls, operations, table_name, **kw):
        """Set the update time for a given table."""
        op = SetTimeable(table_name, **kw)
        return operations.invoke(op)
    
    def reverse(self):
        return UnsetTimeable(self.table_name)

@Operations.register_operation("unset_timeable")
class UnsetTimeable(MigrateOperation):
    """Unset update date in a given table."""

    def __init__(self, table_name, **kw):
        self.table_name = table_name

    @classmethod
    def unset_timeable(cls, operations, table_name, **kw):
        """Unset the update time time for a given table."""
        op = UnsetTimeable(table_name, **kw)
        return operations.invoke(op)
    
    def reverse(self):
        return UnsetCreateable(self.table_name)

@Operations.implementation_for(SetTimeable)
def set_timeable(operations, operation):
    operations.set_createable(operation.table_name)
    operations.add_column(operation.table_name, 
            sa.Column('updated_at', sa.types.TIMESTAMP(timezone=True),
                server_default=sa.func.now(), nullable=False))

@Operations.implementation_for(UnsetTimeable)
def unset_timeable(operations, operation):
    operations.drop_column(operation.table_name, 'updated_at')
    operations.unset_createable(operation.table_name)

@Operations.register_operation("set_reference")
class SetReference(MigrateOperation):
    """Set a foreign key based on the custom UUID fields"""

    def __init__(self, tn, f, rtn, rf='id', n=False, **kw):
        self.table_name = tn
        self.field = f
        self.referenced_table_name = rtn
        self.referenced_field = rf
        self.nullable = n

    @classmethod
    def set_reference(cls, operations, tn, f, rtn, rf='id', n=False, **kw):
        """Set the foreign key based in the custom UUID field"""
        op = SetReference(tn, f, rtn, rf, n, **kw)
        return operations.invoke(op)

    def reverse(self):
        return UnsetReference(self.table_name, self.field, 
                self.referenced_table_name, self.referenced_field, self.nullable)

@Operations.register_operation("unset_reference")
class UnsetReference(MigrateOperation):
    """Reverse operation to set_reference. A simple column drop can be used too."""
    
    def __init__(self, tn, f, rtn, rf='id', n=False, **kw):
        self.table_name = tn
        self.field = f
        self.referenced_table_name = rtn
        self.referenced_field = rf
        self.nullable = n

    @classmethod
    def unset_reference(cls, operations, tn, f, rtn, rf='id', n=False, **kw):
        """Drop the referenced column."""
        op = UnsetReference(tn, f, rtn, rf, n, **kw)
        return operations.invoke(op)

    def reverse(self):
        return SetReference(self.table_name, self.field, 
                self.referenced_table_name, self.referenced_field, 
                self.nullable)

@Operations.implementation_for(SetReference)
def set_reference(operations, operation):
    conn_uri = context.config.get_section_option('alembic', 'sqlalchemy.url')
    n = "null" if operation.nullable else "not null"

    # Add the custom primary key:
    if(conn_uri.startswith('postgresql')):
        operations.execute(
            "alter table %s "
            "add %s uuid %s "
            "references %s(%s)" % (operation.table_name, operation.field, n,
                operation.referenced_table_name, operation.referenced_field)
            )
    elif(conn_uri.startswith('oracle')):
        operations.execute(
            "alter table %s "
            "add %s raw(16) %s "
            "references %s(%s) " % (operation.table_name, operation.field,
                n, operation.referenced_table_name, operation.referenced_field)
            )
    else:
        raise Exception("Unsupported database - only Oracle and "
                "PostgreSQL are supported currently")

@Operations.implementation_for(UnsetReference)
def unset_reference(operations, operation):
    operations.drop_column(operation.table_name, operation.field)

def run_migrations_offline():
    """Run migrations in 'offline' mode.

    This configures the context with just a URL
    and not an Engine, though an Engine is acceptable
    here as well.  By skipping the Engine creation
    we don't even need a DBAPI to be available.

    Calls to context.execute() here emit the given string to the
    script output.

    """
    url = config.get_main_option("sqlalchemy.url")
    context.configure(
        url=url,
        target_metadata=target_metadata,
        literal_binds=True,
        dialect_opts={"paramstyle": "named"},
    )

    with context.begin_transaction():
        context.run_migrations()

def run_migrations_online():
    """Run migrations in 'online' mode.

    In this scenario we need to set an Engine
    and associate a connection with the context.

    """
    connectable = engine_from_config(
        config.get_section(config.config_ini_section),
        prefix="sqlalchemy.",
        poolclass=pool.NullPool,
    )

    with connectable.connect() as connection:
        context.configure(
            connection=connection, target_metadata=target_metadata
        )

        with context.begin_transaction():
            context.run_migrations()


if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()
