import React, { FC, useMemo } from 'react';
import { Picture, Tag } from '@types';
import Card from 'react-bootstrap/Card';

interface TagProps {
  tag: Tag;
}

const PictureTag: FC<TagProps> = ({ tag }) => {
  const style = useMemo(
    () => ({
      backgroundColor: `#${tag.labelColor}`,
      color: `#${tag.textColor}`,
    }),
    [tag]
  );

  return (
    <span className="badge mr-1" style={style}>
      {tag.label}
    </span>
  );
};

interface PictureTagsProps {
  picture: Picture;
}

export const PictureTags: FC<PictureTagsProps> = ({ picture }) => (
  <Card>
    <Card.Body>
      {picture.tags.map((tag, index) => (
        <PictureTag tag={tag} key={index} />
      ))}
    </Card.Body>
  </Card>
);
