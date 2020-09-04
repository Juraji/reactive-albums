import React, { FC } from 'react';
import { Picture } from '@types';
import { useApiUrl, useFileSize, useHumanDate } from '@hooks';
import Card from 'react-bootstrap/Card';

import './picture-tile.scss';
import { Link } from 'react-router-dom';

interface PictureTileProps {
  picture: Picture;
}

export const PictureTile: FC<PictureTileProps> = ({ picture }) => {
  const imageUrl = useApiUrl('pictures', picture.id, 'thumbnail');
  const lastModified = useHumanDate(picture.lastModifiedTime);
  const fileSizeHuman = useFileSize(picture.fileSize);

  return (
    <Card className="picture-tile mb-4" as={Link} to={`/picture/${picture.id}`}>
      <Card.Img variant="top" src={imageUrl} />
      <Card.Body>
        <ul className="list-unstyled">
          <li className="font-weight-bold">{picture.displayName}</li>
          <li className="small">
            {picture.imageWidth} x {picture.imageHeight} ({fileSizeHuman})
          </li>
          <li className="small">{lastModified}&nbsp;</li>
        </ul>
      </Card.Body>
    </Card>
  );
};
