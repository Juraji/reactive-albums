create table PictureProjection
(
    id               varchar(64)  not null,
    displayName      varchar(4096) not null,
    location         varchar(4096) not null,
    pictureType      varchar(255)  not null,
    fileSize         bigint,
    lastModifiedTime timestamp,
    imageHeight      integer,
    imageWidth       integer,
    duplicateCount   integer,
    contentHash      blob,
    primary key (id),
    constraint uk_location unique (location)
);
