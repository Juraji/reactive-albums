import React, { FC, useMemo } from 'react';
import { Picture, TagLink } from '@types';
import { useApiUrl } from '@hooks';
import Card from 'react-bootstrap/Card';
import { Link } from 'react-router-dom';
import Badge from 'react-bootstrap/Badge';
import { Conditional } from '@components';

import './picture-tile.scss';
import { useTranslation } from 'react-i18next';

interface TagOrbProps {
  tagLink: TagLink;
}

export const TagOrb: FC<TagOrbProps> = ({ tagLink }) => {
  const style = useMemo(() => ({ backgroundColor: `#${tagLink.tag.tagColor}` }), [tagLink]);
  return <div className="tag-orb" title={tagLink.tag.label} style={style} />;
};

interface PictureTileProps {
  picture: Picture;
}

export const PictureTile: FC<PictureTileProps> = ({ picture }) => {
  const { t } = useTranslation();
  const imageUrl = useApiUrl('pictures', picture.id, 'thumbnail');

  return (
    <Card className="picture-tile mb-4 mr-2">
      <Link to={`/picture/${picture.id}`}>
        <Card.Img variant="top" src={imageUrl} className="picture-thumbnail" />
      </Link>
      <Card.Body>
        <ul className="list-unstyled">
          <li className="font-weight-bold text-ellipsis">{picture.displayName}</li>
          <li className="small">
            {picture.imageWidth} x {picture.imageHeight} ({t('common.file_size', { fileSize: picture.fileSize })})
          </li>
          <li className="small">{t('common.full_date', { isoDate: picture.lastModifiedTime })}&nbsp;</li>
        </ul>
        <div className="tag-orbs">
          {picture.tags.map((tagLink, index) => (
            <TagOrb tagLink={tagLink} key={index} />
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
