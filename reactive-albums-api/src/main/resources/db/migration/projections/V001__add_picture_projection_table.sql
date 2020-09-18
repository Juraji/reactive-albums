create table PictureProjection
(
    id               varchar(64)   not null,
    directoryId      varchar(255)  not null,
    displayName      varchar(4096) not null,
    duplicateCount   integer       not null,
    fileSize         bigint,
    imageHeight      integer,
    imageWidth       integer,
    lastModifiedTime timestamp,
    location         varchar(4096) not null,
    parentLocation   varchar(4096) not null,
    pictureType      varchar(255)  not null,
    primary key (id)
);
