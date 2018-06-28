CREATE DATABASE blynk;

\connect blynk

CREATE TABLE users (
  email text NOT NULL,
  appName text NOT NULL,
  region text,
  ip text,
  name text,
  pass text,
  last_modified timestamp,
  last_logged timestamp,
  last_logged_ip text,
  is_facebook_user bool,
  role int2,
  energy int,
  json text,
  PRIMARY KEY(email, appName)
);

CREATE TABLE redeem (
  token character(32) PRIMARY KEY,
  company text,
  isRedeemed boolean DEFAULT FALSE,
  reward integer NOT NULL DEFAULT 0,
  email text,
  version integer NOT NULL DEFAULT 1,
  ts timestamp
);

CREATE TABLE flashed_tokens (
  token character(32),
  app_name text,
  email text,
  project_id int4 NOT NULL,
  device_id int4 NOT NULL,
  is_activated boolean DEFAULT FALSE,
  ts timestamp,
  PRIMARY KEY(token, app_name)
);

CREATE TABLE cloned_projects (
  token character(32),
  ts timestamp,
  json text,
  PRIMARY KEY(token)
);

CREATE TABLE purchase (
  email text,
  reward integer NOT NULL,
  transactionId text,
  price float8,
  ts timestamp NOT NULL DEFAULT NOW(),
  PRIMARY KEY (email, transactionId)
);

CREATE TABLE forwarding_tokens (
  token character(32),
  host text,
  email text,
  project_id int4,
  device_id int4,
  ts timestamp DEFAULT NOW(),
  PRIMARY KEY(token, host)
);

CREATE TABLE invitation_tokens (
  token character(32),
  email text,
  name text,
  role text,
  is_activated boolean DEFAULT FALSE,
  created_ts timestamp DEFAULT NOW(),
  activated_ts timestamp,
  PRIMARY KEY(token, email)
);

CREATE TABLE reporting_events (
  id bigserial,
  device_id int4,
  type smallint,
  ts timestamp without time zone default (now() at time zone 'utc'),
  event_hashcode int4 DEFAULT 0,
  description text,
  is_resolved boolean DEFAULT FALSE,
  resolved_by text,
  resolved_at timestamp,
  resolved_comment text
);
CREATE INDEX reporting_events_main_idx ON reporting_events (device_id, type, ts);

CREATE TABLE reporting_events_last_seen (
  device_id int4,
  email text,
  ts timestamp without time zone default (now() at time zone 'utc'),
  PRIMARY KEY(device_id, email)
);

CREATE TABLE knight_laundry (
   device_id int4,
   pin int2,
   pin_type int2,
   created timestamp,
   type_of_record int4,
   washer_id int4,
   start_date date,
   start_time time,
   finish_time time,
   cycle_time time,
   formula_number int4,
   load_weight int4,
   pump_id int4,
   volume int4,
   run_time int4,
   pulse_count int4
);
create index on knight_laundry (device_id, pin, pin_type, created);

CREATE TABLE knight_scopetech (
   device_id int4,
   pin int2,
   pin_type int2,
   created timestamp,
   time timestamp,
   scope_user text,
   serial int4,
   dose_volume int4,
   flush_volume int4,
   rinse_volume int4,
   leak_test int4,
   pressure int4,
   temperature int4,
   error int4
);
create index on knight_scopetech (device_id, pin, pin_type, created);

CREATE TABLE blynk_default (
   device_id int4,
   pin int2,
   pin_type int2,
   created timestamp,
   value float8
);
create index on blynk_default (device_id, pin, pin_type, created);

create user test with password 'test';
GRANT CONNECT ON DATABASE blynk TO test;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO test;
GRANT ALL ON sequence reporting_events_id_seq to test;