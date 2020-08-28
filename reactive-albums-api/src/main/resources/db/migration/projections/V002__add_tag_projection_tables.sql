create table PictureProjection_tags
(
    PictureProjection_id varchar(64) not null,
    color                varchar(6),
    label                varchar(255),
    linkType             varchar(255),
    constraint fk_picture_projection_id foreign key (PictureProjection_id) references PictureProjection(id)
)
