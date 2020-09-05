create table DirectoryProjection
(
    id                   varchar(64)   not null,
    displayName          varchar(255)  not null,
    location             varchar(4096) not null,
    automaticScanEnabled boolean       not null,
    primary key (id)
)
