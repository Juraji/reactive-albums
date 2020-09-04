import React, { FC, useMemo } from 'react';
import { Picture } from '@types';
import { useApiUrl } from '@hooks';
import { format, parseISO } from 'date-fns';
import Card from 'react-bootstrap/Card';
import fileSize from 'filesize';

import './picture-tile.scss';
import { Link } from 'react-router-dom';

interface PictureTileProps {
  picture: Picture;
}

export const PictureTile: FC<PictureTileProps> = ({ picture }) => {
  const imageUrl = useApiUrl('pictures', picture.id, 'thumbnail');
  const lastModified = useMemo(() => {
    if (!!picture.lastModifiedTime) {
      const d = parseISO(picture.lastModifiedTime);
      return format(d, 'do MMMM yyyy HH:mm:ss');
    } else {
      return null;
    }
  }, [picture]);
  const fileSizeHuman = useMemo(() => (!!picture.fileSize ? fileSize(picture.fileSize) : null), [picture]);

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
