import React, { FC } from 'react';
import Card from 'react-bootstrap/Card';
import { DuplicateMatch } from '@types';
import { Conditional } from '@components';
import ListGroup from 'react-bootstrap/ListGroup';
import ListGroupItem from 'react-bootstrap/ListGroupItem';
import { useTranslation } from 'react-i18next';
import Badge from 'react-bootstrap/Badge';
import { usePicture, usePictureDuplicates } from '@reducers';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import Button from 'react-bootstrap/Button';
import { RefreshCw, Scissors, Trash } from 'react-feather';
import { useApiUrl } from '@hooks';

import './picture-duplicate-list.scss';
import { Link } from 'react-router-dom';

interface DuplicateMatchRowProps {
  match: DuplicateMatch;
}

export const DuplicateMatchRow: FC<DuplicateMatchRowProps> = ({ match }) => {
  const targetPicture = usePicture(match.targetId);
  const thumbnailUrl = useApiUrl('pictures', match.targetId, 'thumbnail');
  const { t } = useTranslation();

  return (
    <ListGroupItem className="duplicate-match-row">
      <div className="duplicate-match-details d-flex flex-row mb-1">
        <Link to={`/picture/${match.targetId}`}>
          <img src={thumbnailUrl} className="duplicate-match-thumbnail mr-1" alt={targetPicture?.displayName} />
        </Link>
        <h6 className="duplicate-match-display-name text-ellipsis flex-grow-1">
          <span>{targetPicture?.displayName}</span>
          <br />
          <small>{match.similarity}%</small>
        </h6>
        <ButtonGroup size="sm" vertical>
          <Button title={t('picture.duplicates_list.unlink_single_button.label')}>
            <Scissors />
          </Button>
          <Button title={t('picture.duplicates_list.delete_single_button.label')} variant="danger">
            <Trash />
          </Button>
        </ButtonGroup>
      </div>
    </ListGroupItem>
  );
};

interface PictureDuplicateListProps {
  pictureId: string;
}

export const PictureDuplicateList: FC<PictureDuplicateListProps> = ({ pictureId }) => {
  const matches = usePictureDuplicates(pictureId);
  const { t } = useTranslation();

  return (
    <Conditional condition={!matches.isEmpty()}>
      <Card>
        <Card.Header>
          <Card.Title>
            {t('picture.duplicates_list.title')}&nbsp;<Badge variant="danger">{matches.length}</Badge>
          </Card.Title>
        </Card.Header>
        <ListGroup className="list-group-flush">
          {matches.map((match, index) => (
            <DuplicateMatchRow match={match} key={index} />
          ))}
        </ListGroup>
        <Card.Footer>
          <ButtonGroup size="sm">
            <Button title={t('picture.duplicates_list.rescan_duplicates_button.label')}>
              <RefreshCw />
            </Button>
            <Button title={t('picture.duplicates_list.unlink_all_button.label')}>
              <Scissors />
            </Button>
            <Button variant="danger" title={t('picture.duplicates_list.delete_all_button.label')}>
              <Trash />
            </Button>
          </ButtonGroup>
        </Card.Footer>
      </Card>
    </Conditional>
  );
};
