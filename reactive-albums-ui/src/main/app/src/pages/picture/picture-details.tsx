import React, { FC } from 'react';
import { Picture } from '@types';
import Card from 'react-bootstrap/Card';
import { useTranslation } from 'react-i18next';

interface PictureDetailsProps {
  picture: Picture;
}

export const PictureDetails: FC<PictureDetailsProps> = ({ picture }) => {
  const { t } = useTranslation();

  return (
    <Card className="mb-2">
      <Card.Header>
        <Card.Title>{picture.displayName}</Card.Title>
      </Card.Header>
      <Card.Body>
        <ul className="list-unstyled mb-0">
          <li>{picture.location}</li>
          <li>
            {picture.imageWidth}x{picture.imageHeight}
          </li>
          <li>
            {picture.pictureType} {t('common.file_size', { fileSize: picture.fileSize })}
          </li>
          <li>{t('common.full_date', { isoDate: picture.lastModifiedTime })}</li>
        </ul>
      </Card.Body>
    </Card>
  );
};
