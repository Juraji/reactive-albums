import React, { FC } from 'react';
import { Picture } from '@types';
import Card from 'react-bootstrap/Card';
import { PictureTag } from '@components';

interface PictureTagsProps {
  picture: Picture;
}

export const PictureTags: FC<PictureTagsProps> = ({ picture }) => (
  <Card className="mb-2">
    <Card.Body>
      {picture.tags.map((tagLink, index) => (
        <PictureTag tag={tagLink.tag} key={index} />
      ))}
    </Card.Body>
  </Card>
);
