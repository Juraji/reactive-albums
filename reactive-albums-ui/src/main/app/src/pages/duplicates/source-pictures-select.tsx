import React, { FC } from 'react';
import ListGroup from 'react-bootstrap/ListGroup';
import { useApiUrl } from '@hooks';
import { usePicture } from '@reducers';

import './source-pictures-select.scss';

interface RootPictureProps {
  pictureId: string;
}

const SourcePicturePreview: FC<RootPictureProps> = ({ pictureId }) => {
  const thumbnailUrl = useApiUrl('pictures', pictureId, 'thumbnail');
  const picture = usePicture(pictureId);

  return (
    <div className="duplicate-match-details d-flex flex-row mb-1">
      <img src={thumbnailUrl} className="img-thumbnail p-0 duplicate-match-thumbnail" alt={picture?.displayName} />
      <h6 className="root-picture-display-name text-ellipsis flex-grow-1 mx-1">
        <span>{picture?.displayName}</span>
        <br />
        <small>{picture?.duplicateCount}</small>
      </h6>
    </div>
  );
};

interface SourcePicturesSelectProps {
  sourcePictureIds: string[];
  activeId: string;
  onActivateId: (id: string) => void;
}

export const SourcePicturesSelect: FC<SourcePicturesSelectProps> = ({ sourcePictureIds, activeId, onActivateId }) => {
  return (
    <ListGroup>
      {sourcePictureIds.map((pictureId, index) => (
        <ListGroup.Item
          key={index}
          action
          className="root-picture-row"
          onClick={() => onActivateId(pictureId)}
          active={pictureId === activeId}
        >
          <SourcePicturePreview pictureId={pictureId} />
        </ListGroup.Item>
      ))}
    </ListGroup>
  );
};
