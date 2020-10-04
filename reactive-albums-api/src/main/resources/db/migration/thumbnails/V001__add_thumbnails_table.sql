create table Thumbnail
(
    id               varchar(255) not null,
    thumbnail        blob         not null,
    lastModifiedTime datetime(3)  not null,
    contentType      varchar(255) not null,
    primary key (id)
)
