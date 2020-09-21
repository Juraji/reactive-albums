import React, { FC, useState } from 'react';
import { useApiUrl } from '@hooks';

import './picture-view.scss';

interface PictureViewProps {
  pictureId: string;
}

export const PictureView: FC<PictureViewProps> = ({ pictureId }) => {
  const imageUrl = useApiUrl('pictures', pictureId, 'image');
  const [fill, setFill] = useState(true);

  const onToggleFill = () => setFill((s) => !s);

  return (
    <img
      className={'border rounded picture-view-image' + (fill ? ' fill' : '')}
      src={imageUrl}
      alt="Preview"
      onClick={onToggleFill}
    />
  );
};
