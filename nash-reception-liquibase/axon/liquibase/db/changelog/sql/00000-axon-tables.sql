--liquibase formatted sql
--changeset ad13021:00001
--preconditions onFail:WARN
CREATE SCHEMA IF NOT EXISTS public;

SET SCHEMA 'public';

CREATE TABLE IF NOT EXISTS public.association_value_entry (
    id int8 NOT NULL,
    association_key varchar(255) NOT NULL,
    association_value varchar(255) NULL,
    saga_id varchar(255) NOT NULL,
    saga_type varchar(255) NULL,
    CONSTRAINT association_value_entry_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.saga_entry (
    saga_id varchar(255) NOT NULL,
    revision varchar(255) NULL,
    saga_type varchar(255) NULL,
    serialized_saga oid NULL,
    CONSTRAINT saga_entry_pkey PRIMARY KEY (saga_id)
);

CREATE TABLE IF NOT EXISTS public.dead_letter_entry (
    dead_letter_id varchar(255) NOT NULL,
    cause_message varchar(1023) NULL,
    cause_type varchar(255) NULL,
    "diagnostics" oid NULL,
    enqueued_at timestamptz(6) NOT NULL,
    last_touched timestamptz(6) NULL,
    aggregate_identifier varchar(255) NULL,
    event_identifier varchar(255) NOT NULL,
    message_type varchar(255) NOT NULL,
    meta_data oid NULL,
    payload oid NOT NULL,
    payload_revision varchar(255) NULL,
    payload_type varchar(255) NOT NULL,
    sequence_number int8 NULL,
    "time_stamp" varchar(255) NOT NULL,
    "token" oid NULL,
    token_type varchar(255) NULL,
    "type" varchar(255) NULL,
    processing_group varchar(255) NOT NULL,
    processing_started timestamptz(6) NULL,
    sequence_identifier varchar(255) NOT NULL,
    sequence_index int8 NOT NULL,
    CONSTRAINT dead_letter_entry_pkey PRIMARY KEY (dead_letter_id),
    CONSTRAINT dead_letter_unique UNIQUE (processing_group, sequence_identifier, sequence_index)
);


CREATE TABLE IF NOT EXISTS public.domainevententry (
    globalindex bigserial NOT NULL,
    aggregateidentifier varchar(255) NOT NULL,
    sequencenumber int8 NOT NULL,
    "type" varchar(255) NULL,
    eventidentifier varchar(255) NOT NULL,
    metadata jsonb NULL,
    payload jsonb NOT NULL,
    payloadrevision varchar(255) NULL,
    payloadtype varchar(255) NOT NULL,
    "timestamp" varchar(255) NOT NULL,
    CONSTRAINT domainevententry_aggregateidentifier_sequencenumber_key UNIQUE (aggregateidentifier, sequencenumber),
    CONSTRAINT domainevententry_eventidentifier_key UNIQUE (eventidentifier),
    CONSTRAINT domainevententry_pkey PRIMARY KEY (globalindex)
);

CREATE TABLE IF NOT EXISTS public.snapshotevententry (
    aggregateidentifier varchar(255) NOT NULL,
    sequencenumber int8 NOT NULL,
    "type" varchar(255) NOT NULL,
    eventidentifier varchar(255) NOT NULL,
    metadata jsonb NULL,
    payload jsonb NOT NULL,
    payloadrevision varchar(255) NULL,
    payloadtype varchar(255) NOT NULL,
    "timestamp" varchar(255) NOT NULL,
    CONSTRAINT snapshotevententry_eventidentifier_key UNIQUE (eventidentifier),
    CONSTRAINT snapshotevententry_pkey PRIMARY KEY (aggregateidentifier, sequencenumber)
);

CREATE TABLE IF NOT EXISTS public.tokenentry (
    processorname varchar(255) NOT NULL,
    segment int4 NOT NULL,
    "owner" varchar(255) NULL,
    "timestamp" varchar(255) NOT NULL,
    "token" jsonb NULL,
    tokentype varchar(255) NULL,
    CONSTRAINT token_entry_pkey PRIMARY KEY (processorname, segment)
);

CREATE TABLE IF NOT EXISTS public.shedlock(
    name VARCHAR(64) NOT NULL,
    lock_until TIMESTAMP NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    locked_by VARCHAR(255) NOT NULL,
    CONSTRAINT shedlock_provider_pkey PRIMARY KEY (name)
);

CREATE INDEX IF NOT EXISTS idx_saga_id_saga_type ON public.association_value_entry (saga_id, saga_type);
CREATE INDEX IF NOT EXISTS idx_saga_type_assoc_cols ON public.association_value_entry (saga_type, association_key, association_value);

CREATE INDEX IF NOT EXISTS idx_p_group ON public.dead_letter_entry (processing_group);
CREATE INDEX IF NOT EXISTS idx_p_group_seq_id ON public.dead_letter_entry (processing_group, sequence_identifier);

CREATE SEQUENCE IF NOT EXISTS public.association_value_entry_seq
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;

CREATE SEQUENCE IF NOT EXISTS public.domainevententry_globalindex_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;