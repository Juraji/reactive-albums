create table PictureProjection
(
    id               varchar(64)   not null,
    createdAt        timestamp     not null,
    lastModifiedAt   timestamp     not null,
    displayName      varchar(4096) not null,
    duplicateCount   integer       not null,
    fileSize         bigint,
    imageHeight      integer,
    imageWidth       integer,
    lastModifiedTime timestamp,
    location         varchar(4096),
    parentLocation   varchar(4096),
    pictureType      varchar(255),
    primary key (id)
);

create fulltext index idx_ft_picture_location on PictureProjection(location);
create index idx_picture_parent_location on PictureProjection(parentLocation);
