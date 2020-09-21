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
          <li>{t('picture.details.picture_info', picture)}</li>
          <li className="small text-muted">{picture.parentLocation}</li>
          <li className="small text-muted">{t('picture.details.added_on', picture)}</li>
          <li className="small text-muted">{t('picture.details.file_last_modified', picture)}</li>
        </ul>
      </Card.Body>
    </Card>
  );
};
