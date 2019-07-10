---------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------
-- USER MANAGEMENT TABLES
---------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------

--
-- This is the version table, used to control schema creation and system updates:
--
create table version (
  id uuid primary key default uuid_generate_v4(),
  file_name varchar(256) unique not null,
  version varchar(64) unique not null,
  create_date timestamp with time zone not null default now()
);

-- This table stores the entity history in the entire database. It's a JSON
-- database, indexed on the id field, to enable fast search
create table entity_history (
  -- The primary key is a sequential big number, to ensure order.
  -- This is the only table in the entire system not using a GUID id.
  id numeric(1000) not null primary key,
  entity_name varchar(1024) not null,
  entity_id uuid not null,
  entity_data jsonb not null,
  create_date timestamp with time zone not null default now()
);

--
-- This is the seed control table, used to control seeding:
--
create table seed (
  id uuid primary key default uuid_generate_v4(),
  file_name varchar(256) unique not null,
  create_date timestamp with time zone not null default now()
);

-- Create the general configurations table:
create table configuration (
  -- The primary key is a binary number, with 16 Bytes (128 bits),
  -- storing a Guid:
  id uuid primary key default uuid_generate_v4(),

  name varchar(191) unique not null,
  value varchar(2048) not null,

  -- The creation, and update timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now()
);

-- Create the table to store temporary runtime data on the system:
create table runtime (
  id uuid primary key default uuid_generate_v4(),

  -- The runtime variable name:
  name varchar(256) unique not null,

  -- And the runtime variable generic value:
  value varchar(1024) null,
  
  -- The creation, and update timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now()
);

-- Table used to control critical sections:
create table lock_control (
  id uuid primary key default uuid_generate_v4(),

  -- The lock control name:
  name varchar(256) unique not null,

  -- The expiration date of current locking acquiring:
  expiration timestamp with time zone not null,

  -- The lock status, which can be 'L', for locked, or 'F' for free:
  status char(1) not null,

  -- The lock Machine Process/Thread owner:
  owner varchar(256) not null,

  -- The possible statuses for lock, where 'L' = locked and 'F' = free:
  constraint lock_control_status_ck check(status in ('L', 'F'))
);

-- Create the Phone-number entity table:
create table phone_number (
  -- The primary key is a binary number, with 16 Bytes (128 bits),
  -- storing a Guid:
  id uuid primary key default uuid_generate_v4(),

  -- This is the phone number, per se, in international format:
  -- +<Country code> <Space> [<Long distance code (if exists)> <Space>] <Number, only digits>
  number varchar(32) unique not null,

  -- The phone number status:
  status varchar(64) not null,

  -- The creation, and update timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now()
);

-- Create the E-mail entity table:
create table email_address (
  id uuid primary key default uuid_generate_v4(),

  -- This is the email address
  address varchar(256) unique not null,

  -- This the e-mail status:
  status varchar(64) not null,

  -- The creation, and update timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now()
);

-- Create the Person entity table - this is just a reference for an external unstructured
-- data repository, since Person information and identification is unstructured. As an
-- example, the document identification for a person, in Brazil, consists of two different
-- numbers, the CPF, and the RG, and a person can have multiple RGs, one for each Brazilian
-- state. This same person can be a European Union citizen, identified by the Passport, and
-- an American citizen, identified by the social security number.
create table person (
  id uuid primary key default uuid_generate_v4(),

  -- The person name:
  name varchar(512) not null,

  -- The person birthday, which can be unknown
  -- at the person registration:
  birthday date null,

  -- The person registration status:
  status varchar(64) not null,

  -- The creation, and update timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now()
);

-- This is the User table, having BCrypt2 password hashes, but with
-- no token management on it:
create table "user" (
  id uuid primary key default uuid_generate_v4(),

  -- Always user is a person, but we don't know which
  -- person is a user in all cases. A person can have multiple users:
  person_id uuid null references person(id),

  -- This is the field to store the password BCrypt2 or the PBKDF2 hash:
  password varchar(256) not null,

  -- This is the user name, which isn't necessarily equal to the
  -- person name, but must be unique, to help user identification:
  name varchar(256) unique not null,

  -- This is a user alias: a social name to be shown to other users.
  alias varchar(256) not null,

  -- The state of each user register, which is an live entity in the
  -- system:
  status varchar(64) not null,

  -- The creation, and update timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now()
);

-- User->email address and User->phone number relationships:
create table user__email_address (
  id uuid primary key default uuid_generate_v4(),

  -- The user:
  user_id uuid not null references "user"(id),

  -- The e-mail address:
  email_address_id uuid not null references email_address(id),

  -- The preference order of the e-mail: the lower number, the most preferred
  -- e-mail address by the user:
  preference_order integer not null check(preference_order >= 0),

  -- Status control:
  status varchar(64) not null,

  -- The creation timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now(),

  -- Add constraints:
  constraint user_id_email_address_id_uidx
      unique(user_id, email_address_id, preference_order)
);

create table user__phone_number (
  id uuid primary key default uuid_generate_v4(),

  -- the user:
  user_id uuid not null references "user"(id),

  -- the phone number:
  phone_number_id uuid not null references phone_number(id),
  
  -- The preference order of the e-mail: the lower number, the most preferred
  -- e-mail address by the user:
  preference_order integer not null check(preference_order >= 0),

  -- The creation timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now(),

  -- Add constraints:
  constraint user_id_phone_number_id_uidx
      unique(user_id, phone_number_id, preference_order)
);

-- The access token is an ENTITY, with complete life cycle, which
-- can be used for user AUTHORIZATION.
create table access_token (
  id uuid primary key default uuid_generate_v4(),

  -- The token, per se:
  token varchar(256) unique not null,

  -- The expiration date (if the token is expirable, null otherwise):
  expiration timestamp with time zone,

  -- The owner of such token:
  user_id uuid not null references "user"(id),

  -- Status:
  status varchar(64) not null,

  -- Creation and update timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now()
);

create table role (
  id uuid primary key default uuid_generate_v4(),

  -- The role name:
  name varchar(245) unique not null,

  -- Creation and update timestamps:
  created_at timestamp with time zone not null default now(),
  updated_at timestamp with time zone not null default now()
);

create table user__role (
  id uuid primary key default uuid_generate_v4(),

  -- The user:
  user_id uuid not null references "user"(id),

  -- The role assumed by the user in Altec Backoffice:
  role_id uuid not null references role(id),

  -- Relationship create time:
  created_at timestamp with time zone not null default now(),

  -- Add constraints:
  constraint user_id_role_id_uidx unique(user_id, role_id)
);

-- Increments the database version:
insert into version (file_name, version, create_date)
values ('2018-06-09-22-07-create_configurations_and_user_management_tables.sql', '0.0.0', now());
