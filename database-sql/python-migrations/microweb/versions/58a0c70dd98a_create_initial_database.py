"""Create initial database.

Revision ID: 58a0c70dd98a
Revises: 
Create Date: 2019-11-25 13:11:03.299220

"""
from alembic import op
from alembic import context
from sqlalchemy.dialects.oracle import RAW
from sqlalchemy.dialects.postgresql import UUID
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = '58a0c70dd98a'
down_revision = None
branch_labels = None
depends_on = None

def upgrade():
    entity_id_type = context.uuid('type')
    uuid_generation_function = context.uuid('function')
    realm = context.config.get_section_option('openid', 'realm')
    master_admin_name = context.config.get_section_option('openid', 'master_admin.name')
    master_admin_password = context.config.get_section_option('openid', 'master_admin.password')
    server_path = context.config.get_section_option('openid', 'server_path')
    openid_client = context.config.get_section_option('openid', 'client')
    available_permissions = context.config.get_section_option('openid', 'available_permissions')
    
    if "'" in available_permissions:
        raise Exception("Configuration [openid]available_permissions couldn't " 
                "contain single quotes.")
    
    # This table stores the entity history in the entire database. It's a JSON
    # database, indexed on the id field, to enable fast search
    op.create_table(
        'entity_history',
        # The primary key is a sequential big number, to ensure order.
        # This is the only table in the entire system not using a GUID id.
        sa.Column('id', sa.Numeric(38), nullable=False),
        sa.Column('entity_name', sa.String(1024), nullable=False),
        sa.Column('entity_id', entity_id_type, nullable=False),
        sa.Column('entity_data', sa.Text, nullable=False),
        sa.Column('create_date', sa.types.TIMESTAMP(timezone=True), nullable=False, 
            server_default=sa.func.now()))

    # Create the general configurations table:
    op.create_table(
        'configuration',
        sa.Column('name', sa.String(191), unique=True, nullable=False),
        sa.Column('value', sa.String(2048), nullable=False))
    op.set_timeable('configuration') 
     
    # Create the table to store temporary runtime data on the system:
    op.create_table(
        'runtime',
        sa.Column('name', sa.String(191), nullable=False, unique=True),
        sa.Column('value', sa.String(2048), nullable=False))
    op.set_timeable('runtime') 

    # Table used to control critical sections:
    op.create_table(
        'lock_control',
        # The lock control name:
        sa.Column('name', sa.String(256), nullable=False, unique=True),
        # The expiration date of current locking acquiring:
        sa.Column('expiration', sa.types.TIMESTAMP(timezone=True), nullable=False,
            server_default=sa.func.now()),
        # The lock status, which can be 'L', for locked, or 'F' for free:
        sa.Column('status', sa.types.CHAR(1), nullable=False),
        # The lock Machine Process/Thread owner:
        sa.Column('owner', sa.String(256), nullable=False)
    )
    op.set_identifiable('lock_control')
    # The possible statuses for lock, where 'L' = locked and 'F' = free:
    op.create_check_constraint('lc_kZqjYy8X_ck', 'lock_control', "status in ('L', 'F')")

    # Create the Phone-number entity table:
    op.create_table(
        'phone_number',
        # This is the phone number, per se, in international format:
        # +<Country code> <Space> [<Long distance code (if exists)> <Space>] <Number, only digits>
        sa.Column('number', sa.String(32), unique=True, nullable=False),
        # The phone number status:
        sa.Column('status', sa.String(64), nullable=False)
    )
    op.set_timeable('phone_number')

    # Create the E-mail entity table:
    op.create_table(
        'email_address',
        # This is the email address
        sa.Column('address', sa.String(256), unique=True, nullable=False),
        # This the e-mail status:
        sa.Column('status', sa.String(64), nullable=False)
    )
    op.set_timeable('email_address')

    # Create the Person entity table - this is just a reference for an external unstructured
    # data repository, since Person information and identification is unstructured. As an
    # example, the document identification for a person, in Brazil, consists of two different
    # numbers, the CPF, and the RG, and a person can have multiple RGs, one for each Brazilian
    # state. This same person can be a European Union citizen, identified by the Passport, and
    # an American citizen, identified by the social security number.
    op.create_table(
        'person',
        # The person name:
        sa.Column('name', sa.String(512), nullable=False),
        # The person birthday, which can be unknown
        # at the person registration:
        sa.Column('birthday', sa.types.TIMESTAMP(timezone=True), nullable=False),
        # The person registration status:
        sa.Column('status', sa.String(64), nullable=False)
    )
    op.set_timeable('person')
   
    # This is the User table, having BCrypt2 password hashes, but with
    # no token management on it. BCrypt2 passwords are ignored if OpenID on
    # KeyCloak is used:
    op.create_table(
        'user_',
        # This is the field to store the password BCrypt2 or the PBKDF2 hash.
		# If Keycloak OpenID is used, then the value of this field will be '-':
        sa.Column('password', sa.String(256), nullable=False),
        # This is the user name, which isn't necessarily equal to the
        # person name, but must be unique, to help user identification:
        sa.Column('name', sa.String(256), unique=True, nullable=False),
        # This is a user alias: a social name to be shown to other users.
        sa.Column('alias', sa.String(256), nullable=False),
        #The state of each user register, which is an live entity in the
        # system:
        sa.Column('status', sa.String(64), nullable=False),
        # The user id provided by KeyCloak. If you're not using KeyCloak, this field
        # is ignored, so you can update this field with any value. If you are not
        # using KeyCloak and you're migrating to KeyCloak, then you need to update
        # this value with the IDs generated by KeyCloak on ALL user registers:
        sa.Column('central_control_id', entity_id_type, nullable=True),
        # The user given name (first name) - this can be provided by KeyCloak, if
        # you are using this user management system:
        sa.Column('given_name', sa.String(256), nullable=False),
        # The user family name (last name) - this can be provided by KeyCloak, if
        # you are using this user management system:
        sa.Column('family_name', sa.String(256), nullable=False)
    )
    op.set_timeable('user_')
    # User is always a person, but we don't know which
    # person is a user in all cases. A person can have multiple users:
    op.set_reference('user_', 'person_id', 'person', 'id', True)

    # User->email address relationships:
    op.create_table(
        'user__email_address',
        # The preference order of the e-mail: the lower number, the most preferred
        # e-mail address by the user:
        sa.Column('preference_order', sa.Integer, nullable=False),
        # Status control:
        sa.Column('status', sa.String(64), nullable=False)
    )
    op.set_timeable('user__email_address')
    op.set_reference('user__email_address', 'user_id', 'user_')
    op.set_reference('user__email_address', 'email_address_id', 'email_address')
    op.create_unique_constraint('uea_T5hSEP0k_uidx', 'user__email_address',
            ['user_id', 'email_address_id', 'preference_order'])

    op.create_table(
        'user__phone_number',
        # The preference order of the telephone: the lower number, the most 
        # preferred e-mail address by the user:
        sa.Column('preference_order', sa.Integer, nullable=False),
        # Status control:
        sa.Column('status', sa.String(64), nullable=False)
    )
    op.set_timeable('user__phone_number')
    op.set_reference('user__phone_number', 'user_id', 'user_')
    op.set_reference('user__phone_number', 'phone_number_id', 'phone_number')
    op.create_unique_constraint('upn_Occ5eQEz_uidx', 'user__phone_number',
            ['user_id', 'phone_number_id', 'preference_order'])
    
    # The access token is an ENTITY, with complete life cycle, which
    # can be used for user AUTHORIZATION. Used only if KeyCloak OpenId is not
    # used.
    op.create_table(
        'access_token',
        sa.Column('token', sa.String(256), nullable=False, unique=True),
        sa.Column('expiration', sa.types.TIMESTAMP(timezone=True), nullable=True),
        sa.Column('status', sa.String(64), nullable=False)
    )
    op.set_timeable('access_token')
    op.set_reference('access_token', 'user_id', 'user_')

    op.create_table(
        'role',
        # This is the role name
        sa.Column('name', sa.String(256), unique=True, nullable=False),
        # This status:
        sa.Column('status', sa.String(64), nullable=False)
    )
    op.set_timeable('role')

    op.create_table(
        'user__role',
        sa.Column('status', sa.String(64), nullable=False),
        sa.Column('description', sa.Unicode(200))
    )
    op.set_timeable('user__role')
    op.set_reference('user__role', 'user_id', 'user_')
    op.set_reference('user__role', 'role_id', 'role')
    op.create_unique_constraint('ur_ts9Gr5L1_uidx', 'user__role', 
            ['user_id', 'role_id'])

    # Next follow the seeds of the schema built above:
    op.execute("""
        insert into configuration(name, value)
        values('Java backend port', '48080')
        """)
    op.execute("""
        insert into entity_history(id, entity_name, entity_id, entity_data) 
        values (0, '<no-entity>', %s, '{}')
        """ % uuid_generation_function)
    op.execute(f"""
        insert into configuration(name, value)
        values ('backend oauth wellknown', '{server_path}/auth/realms/{realm}/.well-known/uma2-configuration')
        """)
    op.execute(f"""
        insert into configuration(name, value)
        values('keycloak master oauth wellknown', '{server_path}/auth/realms/master/.well-known/uma2-configuration')
        """)
    op.execute(f"""
        insert into configuration(name, value)
        values('keycloak admin resource', '{server_path}/auth/admin/realms/{realm}')
        """)
    op.execute(f"""
        insert into configuration(name, value)
        values ('keycloak master admin name','{master_admin_name}')
        """)
    op.execute(f"""
        insert into configuration(name, value)
        values ('keycloak master admin password','{master_admin_password}')
        """)
    op.execute(f"""
        insert into configuration(name, value)
        values ('keycloak admin realm', '{realm}')
        """)
    op.execute(f"""
        insert into configuration(name, value)
        values ('keycloak client application', '{openid_client}')
        """)
    op.execute(f"""
        insert into configuration(name, value)
        values ('keycloak client application available permissions', '{available_permissions}')
        """)

def downgrade():
    op.drop_table('user__role')
    op.drop_table('role')
    op.drop_table('access_token')
    op.drop_table('user__phone_number')
    op.drop_table('user__email_address')
    op.drop_table('user_')
    op.drop_table('person')
    op.drop_table('email_address')
    op.drop_table('phone_number')
    op.drop_table('lock_control')
    op.drop_table('runtime')
    op.drop_table('configuration')
    op.drop_table('entity_history')

