import React, { FC } from 'react';
import { Picture } from '@types';
import Card from 'react-bootstrap/Card';
import { useFileSize, useHumanDate } from '@hooks';

interface PictureDetailsProps {
  picture: Picture;
}

export const PictureDetails: FC<PictureDetailsProps> = ({ picture }) => {
  const lastModified = useHumanDate(picture.lastModifiedTime);
  const fileSizeHuman = useFileSize(picture.fileSize);

  return (
    <Card className="mb-2">
      <Card.Header>
        <Card.Title>{picture.displayName}</Card.Title>
      </Card.Header>
      <Card.Body>
        <ul className="list-unstyled">
          <li>{picture.location}</li>
          <li>
            {picture.imageWidth} x {picture.imageHeight}
          </li>
          <li>
            {picture.pictureType} {fileSizeHuman}
          </li>
          <li>{lastModified}</li>
        </ul>
      </Card.Body>
    </Card>
  );
};
