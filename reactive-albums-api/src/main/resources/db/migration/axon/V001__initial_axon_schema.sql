create sequence hibernate_sequence start with 1 increment by 1;
create sequence domain_event_entry_sequence start with 1 increment by 1;

create table AssociationValueEntry
(
    id               bigint       not null,
    associationKey   varchar(255) not null,
    associationValue varchar(255),
    sagaId           varchar(255) not null,
    sagaType         varchar(255),
    primary key (id)
);

create table DomainEventEntry
(
    globalIndex         bigint       not null,
    eventIdentifier     varchar(255) not null,
    metaData            blob,
    payload             blob         not null,
    payloadRevision     varchar(255),
    payloadType         varchar(255) not null,
    timeStamp           varchar(255) not null,
    aggregateIdentifier varchar(255) not null,
    sequenceNumber      bigint       not null,
    type                varchar(255),
    primary key (globalIndex)
);

create table SagaEntry
(
    sagaId         varchar(255) not null,
    revision       varchar(255),
    sagaType       varchar(255),
    serializedSaga blob,
    primary key (sagaId)
);

create table SnapshotEventEntry
(
    aggregateIdentifier varchar(255) not null,
    sequenceNumber      bigint       not null,
    type                varchar(255) not null,
    eventIdentifier     varchar(255) not null,
    metaData            blob,
    payload             blob         not null,
    payloadRevision     varchar(255),
    payloadType         varchar(255) not null,
    timeStamp           varchar(255) not null,
    primary key (aggregateIdentifier, sequenceNumber, type)
);

create table TokenEntry
(
    processorName varchar(255) not null,
    segment       integer      not null,
    owner         varchar(255),
    timestamp     varchar(255) not null,
    token         blob,
    tokenType     varchar(255),
    primary key (processorName, segment)
);

create index IDX2uqqpmht3w2i368ld2ham2out on AssociationValueEntry (sagaType, associationKey, associationValue);

create index IDXpo4uvnt1l3922m6y62fk73p3f on AssociationValueEntry (sagaId, sagaType);

alter table DomainEventEntry
    add constraint UKdg43ia27ypo1jovw2x64vbwv8 unique (aggregateIdentifier, sequenceNumber);

alter table DomainEventEntry
    add constraint UK_k5lt6d2792amnloo7q91njp0v unique (eventIdentifier);

alter table SnapshotEventEntry
    add constraint UK_sg7xx45yh4ajlsjd8t0uygnjn unique (eventIdentifier);
