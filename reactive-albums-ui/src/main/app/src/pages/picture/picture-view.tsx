import React, { FC } from 'react';
import { useApiUrl } from '@hooks';

import './picture-view.scss';

interface PictureViewProps {
  pictureId: string;
}

export const PictureView: FC<PictureViewProps> = ({ pictureId }) => {
  const imageUrl = useApiUrl('pictures', pictureId, 'image');
  return <img className="border rounded picture-view-image" src={imageUrl} alt="Preview" />;
};
