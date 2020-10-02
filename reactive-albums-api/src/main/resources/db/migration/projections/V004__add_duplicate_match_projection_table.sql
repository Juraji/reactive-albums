create table DuplicateMatchProjection
(
    id             varchar(255) not null,
    createdAt      timestamp    not null,
    lastModifiedAt timestamp    not null,
    pictureId      varchar(255) not null,
    similarity     integer      not null,
    targetId       varchar(255) not null,
    primary key (id)
);

create index idx_duplicate_match_picture_id on DuplicateMatchProjection(pictureId);
create index idx_duplicate_match_target_id on DuplicateMatchProjection(targetId);
