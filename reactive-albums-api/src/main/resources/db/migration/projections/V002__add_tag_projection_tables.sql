create table TagProjection
(
    id             varchar(64)  not null,
    createdAt      timestamp    not null,
    lastModifiedAt timestamp    not null,
    label          varchar(255) not null,
    tagColor       varchar(8)   not null,
    textColor      varchar(8)   not null,
    tagType        varchar(255) not null,
    tagColorRed    int          not null,
    tagColorGreen  int          not null,
    tagColorBlue   int          not null,
    primary key (id)
);

create table PictureProjection_tags
(
    PictureProjection_id varchar(64) not null,
    linkType             integer     not null,
    tag_id               varchar(64) not null
);

create fulltext index idx_ft_tag_label on TagProjection(label);
create index idx_tag_label on TagProjection(label);
create index idx_tags_picture_id on PictureProjection_tags(PictureProjection_id)
