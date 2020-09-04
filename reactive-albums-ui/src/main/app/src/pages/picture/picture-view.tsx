import React, { FC } from 'react';
import { useApiUrl } from '@hooks';
import Card from 'react-bootstrap/Card';

import './picture-view.scss';

interface PictureViewProps {
  pictureId: string;
}

export const PictureView: FC<PictureViewProps> = ({ pictureId }) => {
  const imageUrl = useApiUrl('pictures', pictureId, 'image');
  return (
    <Card className="picture-view-card">
      <Card.Img src={imageUrl} />
    </Card>
  );
};
