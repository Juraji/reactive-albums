create table ContentHashProjection
(
    pictureId   varchar(64) not null,
    contentHash blob        not null,
    primary key (pictureId)
)
