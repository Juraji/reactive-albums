create table Thumbnail
(
    id               varchar(255) not null,
    thumbnail        blob,
    lastModifiedTime timestamp    not null,
    primary key (id)
)
