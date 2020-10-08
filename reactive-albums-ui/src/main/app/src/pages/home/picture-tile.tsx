import React, { FC, useMemo } from 'react';
import { Picture, Tag } from '@types';
import { useApiUrl } from '@hooks';
import Card from 'react-bootstrap/Card';
import { Link } from 'react-router-dom';
import Badge from 'react-bootstrap/Badge';
import { Conditional } from '@components';

import './picture-tile.scss';
import { useTranslation } from 'react-i18next';
import { RefreshCw } from 'react-feather';

interface TagOrbProps {
  tag: Tag;
}

export const TagOrb: FC<TagOrbProps> = ({ tag }) => {
  const style = useMemo(() => ({ backgroundColor: tag.tagColor }), [tag]);
  return <div className="tag-orb" title={tag.label} style={style} />;
};

interface PictureTileProps {
  picture: Picture;
}

export const PictureTile: FC<PictureTileProps> = ({ picture }) => {
  const { t } = useTranslation();
  const imageUrl = useApiUrl('pictures', picture.id, 'thumbnail');

  return (
    <Card className="picture-tile mb-2 mr-2">
      <Link to={`/picture/${picture.id}`}>
        <Card.Img variant="top" src={imageUrl} className="picture-thumbnail" />
      </Link>
      <Card.Body>
        <ul className="list-unstyled mb-0">
          <li className="font-weight-bold text-ellipsis">{picture.displayName}</li>
          <li className="small">
            {picture.analysisCompleted
              ? t('home.picture_tile.file_meta', picture)
              : t('home.picture_tile.analysis_in_progress')}
          </li>
          <li className="small">{t('home.picture_tile.file_modification_date', picture)}&nbsp;</li>
        </ul>
        <div className="tag-orbs">
          {picture.tags.map((tagLink, index) => (
            <TagOrb tag={tagLink} key={index} />
          ))}
        </div>
        <Conditional condition={picture.duplicateCount > 0}>
          <Badge variant="danger" className="duplicate-count">
            {picture.duplicateCount}
          </Badge>
        </Conditional>
        <Conditional condition={!picture.analysisCompleted}>
          <div className="d-flex flex-column picture-tile-cover">
            <RefreshCw size={48} className="my-auto mx-auto animate-rotate" />
          </div>
        </Conditional>
      </Card.Body>
    </Card>
  );
};
