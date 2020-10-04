create table DirectoryProjection
(
    id                   varchar(64)   not null,
    createdAt            datetime(3)   not null,
    lastModifiedAt       datetime(3)   not null,
    automaticScanEnabled boolean       not null,
    displayName          varchar(4096) not null,
    location             varchar(4096) not null,
    primary key (id)
);

create fulltext index idx_ft_directory_location on DirectoryProjection (location)
