create table PictureProjection_tags
(
    PictureProjection_id varchar(64)  not null,
    color                varchar(6)   not null,
    label                varchar(255) not null,
    linkType             varchar(255) not null,
    constraint fk_picture_projection_id foreign key (PictureProjection_id) references PictureProjection (id)
)
