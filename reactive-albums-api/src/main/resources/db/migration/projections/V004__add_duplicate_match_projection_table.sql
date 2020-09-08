create table DuplicateMatchProjection
(
    id         varchar(64)  not null,
    pictureId  varchar(64) not null,
    targetId   varchar(64) not null,
    similarity float        not null,
    primary key (id)
)
