create table TagProjection
(
    id        varchar(64)  not null,
    label     varchar(255) not null,
    tagColor  varchar(7) not null,
    textColor varchar(7) not null,
    primary key (id),
    constraint uk_tag_projection_label unique (label)
);

create table PictureProjection_tags
(
    PictureProjection_id varchar(64)  not null,
    linkType             integer      not null,
    tag_id               varchar(64) not null,
    constraint fk_tag_id_to_tag_projection foreign key (tag_id) references TagProjection (id),
    constraint fk_id_to_picture_projection foreign key (PictureProjection_id) references PictureProjection (id)
);
