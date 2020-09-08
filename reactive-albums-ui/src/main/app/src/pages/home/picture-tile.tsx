import React, { FC, useMemo } from 'react';
import { Picture, Tag } from '@types';
import { useApiUrl, useFileSize, useHumanDate } from '@hooks';
import Card from 'react-bootstrap/Card';
import { Link } from 'react-router-dom';
import Badge from 'react-bootstrap/Badge';
import { Conditional } from '@components';

import './picture-tile.scss';

interface TagOrbProps {
  tag: Tag;
}

export const TagOrb: FC<TagOrbProps> = ({ tag }) => {
  const style = useMemo(() => ({ backgroundColor: `#${tag.labelColor}` }), [tag]);
  return <div className="tag-orb" title={tag.label} style={style} />;
};

interface PictureTileProps {
  picture: Picture;
}

export const PictureTile: FC<PictureTileProps> = ({ picture }) => {
  const imageUrl = useApiUrl('pictures', picture.id, 'thumbnail');
  const lastModified = useHumanDate(picture.lastModifiedTime);
  const fileSizeHuman = useFileSize(picture.fileSize);

  return (
    <Card className="picture-tile mb-4">
      <Link to={`/picture/${picture.id}`}>
        <Card.Img variant="top" src={imageUrl} className="picture-thumbnail" />
      </Link>
      <Card.Body>
        <ul className="list-unstyled">
          <li className="font-weight-bold text-ellipsis">{picture.displayName}</li>
          <li className="small">
            {picture.imageWidth} x {picture.imageHeight} ({fileSizeHuman})
          </li>
          <li className="small">{lastModified}&nbsp;</li>
        </ul>
        <div className="tag-orbs">
          {picture.tags.map((tag, index) => (
            <TagOrb tag={tag} key={index} />
          ))}
        </div>
        <Conditional condition={picture.duplicateCount > 0}>
          <Badge variant="danger" className="duplicate-count">
            {picture.duplicateCount}
          </Badge>
        </Conditional>
      </Card.Body>
    </Card>
  );
};
