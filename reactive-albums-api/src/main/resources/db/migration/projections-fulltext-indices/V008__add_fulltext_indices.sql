create fulltext index idx_ft_picture_location on PictureProjection (location);
create fulltext index idx_ft_tag_label on TagProjection (label);
create fulltext index idx_ft_directory_location on DirectoryProjection (location)
