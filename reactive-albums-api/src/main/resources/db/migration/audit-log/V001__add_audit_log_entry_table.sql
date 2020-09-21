create sequence hibernate_sequence start with 1 increment by 1;

create table AuditLogEntry
(
    id            bigint not null,
    aggregateId   varchar(64),
    aggregateName varchar(255),
    message       text,
    timestamp     timestamp,
    primary key (id)
)
