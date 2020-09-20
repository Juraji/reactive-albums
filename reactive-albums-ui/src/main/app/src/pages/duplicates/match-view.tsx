import React, { FC } from 'react';
import { DuplicateMatch, Picture } from '@types';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { useApiUrl, useDispatch, useToggleState } from '@hooks';
import Card from 'react-bootstrap/Card';
import { useTranslation } from 'react-i18next';

import './match-view.scss';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import Button from 'react-bootstrap/Button';
import { Scissors, Search } from 'react-feather';
import { ConfirmModal, DeletePictureButton } from '@components';
import { unlinkDuplicateMatch } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';
import { useToasts } from 'react-toast-notifications';
import { Link } from 'react-router-dom';

interface MatchViewPictureProps {
  picture: Picture;
}

export const MatchViewPicture: FC<MatchViewPictureProps> = ({ picture }) => {
  const { t } = useTranslation();
  const imageUrl = useApiUrl('pictures', picture.id, 'image');

  return (
    <Card className="match-view-picture">
      <Card.Img variant="top" src={imageUrl} className="picture-image" />
      <Card.Body>
        <ul className="list-unstyled">
          <li className="font-weight-bold text-ellipsis">{picture.displayName}</li>
          <li className="small">{picture.location}</li>
          <li className="small">
            {picture.imageWidth} x {picture.imageHeight} ({t('common.file_size', { fileSize: picture.fileSize })})
          </li>
          <li className="small">{t('common.full_date', { isoDate: picture.lastModifiedTime })}&nbsp;</li>
        </ul>
      </Card.Body>
      <Card.Footer>
        <ButtonGroup>
          <Button as={Link} to={`/picture/${picture.id}`}>
            <Search />
          </Button>
          <DeletePictureButton picture={picture} />
        </ButtonGroup>
      </Card.Footer>
    </Card>
  );
};

interface MatchesActionBarProps {
  match: DuplicateMatch;
}

export const MatchesActionBar: FC<MatchesActionBarProps> = ({ match }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const { addToast } = useToasts();

  const [isShowUnlinkConfirm, showUnlinkConfirm, hideUnlinkConfirm] = useToggleState(false);

  const onUnlinkDuplicateConfirmed = () => {
    dispatch(unlinkDuplicateMatch({ matchId: match.id, pictureId: match.pictureId }))
      .then(unwrapResult)
      .then(() => addToast(t('duplicates.matches_action_bar.unlink_duplicates_success'), { appearance: 'success' }))
      .catch(() => addToast(t('duplicates.matches_action_bar.unlink_duplicates_failed'), { appearance: 'error' }))
      .finally(hideUnlinkConfirm);
  };

  return (
    <>
      <ButtonGroup className="mb-3">
        <Button
          variant="info"
          onClick={showUnlinkConfirm}
          title={t('duplicates.matches_action_bar.unlink_duplicates_button')}
        >
          <Scissors />
        </Button>
      </ButtonGroup>

      <ConfirmModal show={isShowUnlinkConfirm} onConfirm={onUnlinkDuplicateConfirmed} onCancel={hideUnlinkConfirm}>
        {t('duplicates.matches_action_bar.unlink_duplicates_confirm', match)}
      </ConfirmModal>
    </>
  );
};

interface MatchViewProps {
  match: DuplicateMatch;
}

export const MatchView: FC<MatchViewProps> = ({ match }) => {
  return (
    <Row>
      <Col sm={12}>
        <MatchesActionBar match={match} />
      </Col>
      <Col sm={12} lg={6} className="pb-4">
        <MatchViewPicture picture={match.picture!} />
      </Col>
      <Col sm={12} lg={6}>
        <MatchViewPicture picture={match.target!} />
      </Col>
    </Row>
  );
};
