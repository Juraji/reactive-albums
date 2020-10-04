create table TagProjection
(
    id             varchar(64)  not null,
    createdAt      timestamp    not null,
    lastModifiedAt timestamp    not null,
    label          varchar(255) not null,
    tagColor       varchar(8)   not null,
    textColor      varchar(8)   not null,
    tagType        varchar(255) not null,
    primary key (id)
);

create table PictureProjection_TagProjection
(
    PictureProjection_id varchar(255) not null,
    tags_id              varchar(255) not null,
    primary key (PictureProjection_id, tags_id)
);

create fulltext index idx_ft_tag_label on TagProjection (label);
create index idx_tag_label on TagProjection (label);
