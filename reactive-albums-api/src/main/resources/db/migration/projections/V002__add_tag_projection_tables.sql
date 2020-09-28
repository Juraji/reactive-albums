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

create table PictureProjection_tags
(
    PictureProjection_id varchar(64) not null,
    linkType             integer     not null,
    tag_id               varchar(64) not null,
    constraint fk_tag_id_to_tag_projection foreign key (tag_id) references TagProjection (id),
    constraint fk_id_to_picture_projection foreign key (PictureProjection_id) references PictureProjection (id)
);
