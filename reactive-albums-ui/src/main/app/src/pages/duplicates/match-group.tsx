import React, { FC, useMemo } from 'react';
import { DuplicateMatch } from '@types';
import { deletePicture, unlinkDuplicateMatch, usePicture } from '@reducers';
import Card from 'react-bootstrap/Card';
import { useApiUrl, useDispatch, useToggleState } from '@hooks';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { useTranslation } from 'react-i18next';
import Button from 'react-bootstrap/Button';
import { Scissors, Search, Trash } from 'react-feather';
import { Link } from 'react-router-dom';

import './match-group.scss';
import { useToasts } from 'react-toast-notifications';
import { unwrapResult } from '@reduxjs/toolkit';
import { ConfirmModal } from '../../@components/confirm-modal';

interface SourcePictureProps {
  pictureId: string;
  averageSimilarity: number;
}

const SourcePicture: FC<SourcePictureProps> = ({ pictureId, averageSimilarity }) => {
  const { t } = useTranslation();
  const picture = usePicture(pictureId);
  const imageUrl = useApiUrl('pictures', pictureId, 'image');

  return (
    <Card>
      <Card.Header>
        <Card.Title>{t('duplicates.match_group.source_title', picture)}</Card.Title>
        <Card.Subtitle>
          {t('duplicates.match_group.source_sub_title', {
            ...picture,
            averageSimilarity,
          })}
        </Card.Subtitle>
      </Card.Header>
      <Card.Img src={imageUrl} className="match-group-card-img" />
      <Card.Body>
        <ul className="list-unstyled mb-0">
          <li>{picture?.location}</li>
          <li>
            {picture?.imageWidth}x{picture?.imageHeight}&nbsp;{picture?.pictureType}&nbsp;
            {t('common.file_size', { fileSize: picture?.fileSize })}
          </li>
          <li>{t('common.full_date', { isoDate: picture?.lastModifiedTime })}</li>
        </ul>
      </Card.Body>
    </Card>
  );
};

export const MatchPicture: FC<DuplicateMatch> = ({ id: matchId, pictureId, targetId, similarity }) => {
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();
  const picture = usePicture(targetId);
  const imageUrl = useApiUrl('pictures', targetId, 'image');

  const [isShowUnlinkConfirm, showUnlinkConfirm, hideUnlinkConfirm] = useToggleState(false);
  const [isShowDeleteConfirm, showDeleteConfirm, hideDeleteConfirm] = useToggleState(false);

  function onUnlinkDuplicateConfirmed() {
    dispatch(unlinkDuplicateMatch({ pictureId, matchId }))
      .then(unwrapResult)
      .then(() => addToast(t('duplicates.match_group.match_unlink_button.success')))
      .catch(() => addToast(t('duplicates.match_group.match_unlink_button.failed', { appearance: 'error' })));
    hideUnlinkConfirm();
  }

  function onDeleteDuplicateConfirmed() {
    dispatch(deletePicture({ pictureId: targetId }))
      .then(unwrapResult)
      .then(() => addToast(t('duplicates.match_group.match_delete_button.success')))
      .catch(() => addToast(t('duplicates.match_group.match_delete_button.failed', { appearance: 'error' })));
    hideDeleteConfirm();
  }

  return (
    <>
      <Card>
        <Card.Header>
          <Card.Title className="mb-0">
            {t('duplicates.match_group.match_title', picture)}&nbsp;
            <small>{similarity}%</small>
          </Card.Title>
        </Card.Header>
        <Card.Img src={imageUrl} className="match-group-card-img" />
        <Card.Body>
          <ul className="list-unstyled mb-0">
            <li>{picture?.location}</li>
            <li>
              {picture?.imageWidth}x{picture?.imageHeight}&nbsp;{picture?.pictureType}&nbsp;
              {t('common.file_size', { fileSize: picture?.fileSize })}
            </li>
            <li>{t('common.full_date', { isoDate: picture?.lastModifiedTime })}</li>
          </ul>
        </Card.Body>
        <Card.Footer className="d-flex flex-row">
          <span className="flex-grow-1">&nbsp;</span>
          <Button className="mr-1" variant="outline-primary" as={Link} to={`/picture/${targetId}`}>
            <Search />
            {t('duplicates.match_group.match_view_button.label')}
          </Button>
          <Button className="mr-1" onClick={showUnlinkConfirm}>
            <Scissors />
            {t('duplicates.match_group.match_unlink_button.label')}
          </Button>
          <Button variant="danger" onClick={showDeleteConfirm}>
            <Trash />
            {t('duplicates.match_group.match_delete_button.label')}
          </Button>
        </Card.Footer>
      </Card>

      <ConfirmModal show={isShowUnlinkConfirm} onConfirm={onUnlinkDuplicateConfirmed} onCancel={hideUnlinkConfirm}>
        {t('duplicates.match_group.match_unlink_button.confirm', picture)}
      </ConfirmModal>

      <ConfirmModal show={isShowDeleteConfirm} onConfirm={onDeleteDuplicateConfirmed} onCancel={hideDeleteConfirm}>
        <p>{t('duplicates.match_group.match_delete_button.confirm', picture)}</p>
        <span className="text-danger">{t('common.action_can_not_be_undone')}</span>
      </ConfirmModal>
    </>
  );
};

interface MatchGroupProps {
  sourcePictureId: string;
  matches: DuplicateMatch[];
}

export const MatchGroup: FC<MatchGroupProps> = ({ sourcePictureId, matches }) => {
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();

  const averageSimilarity = useMemo(() => matches.reduce((acc, next) => acc + next.similarity, 0) / matches.length, [
    matches,
  ]);

  const [isShowUnlinkAllConfirm, showUnlinkAllConfirm, hideUnlinkAllConfirm] = useToggleState(false);
  const [isShowDeleteAllConfirm, showDeleteAllConfirm, hideDeleteAllConfirm] = useToggleState(false);

  function onUnlinkAllConfirmed() {
    const unlinkAction = ({ id: matchId }: DuplicateMatch) =>
      unlinkDuplicateMatch({ pictureId: sourcePictureId, matchId });

    Promise.all(matches.map((match) => dispatch(unlinkAction(match)).then(unwrapResult)))
      .then(() => addToast(t('duplicates.match_group.unlink_all_button.success')))
      .catch(() => addToast(t('duplicates.match_group.unlink_all_button.failed', { appearance: 'error' })));
    hideUnlinkAllConfirm();
  }

  function onDeleteAllConfirmed() {
    const deleteAction = ({ pictureId }: DuplicateMatch) => deletePicture({ pictureId });
    Promise.all(matches.map((match) => dispatch(deleteAction(match)).then(unwrapResult)))
      .then(() => addToast(t('duplicates.match_group.delete_all_button.success')))
      .catch(() => addToast(t('duplicates.match_group.delete_all_button.failed', { appearance: 'error' })));
    hideDeleteAllConfirm();
  }

  return (
    <>
      <SourcePicture pictureId={sourcePictureId} averageSimilarity={averageSimilarity} />
      <hr />
      <div className="d-flex flex-row mb-3">
        <span className="flex-grow-1">&nbsp;</span>
        <Button onClick={showUnlinkAllConfirm} className="mr-1">
          <Scissors />
          {t('duplicates.match_group.unlink_all_button.label')}
        </Button>
        <Button onClick={showDeleteAllConfirm} variant="danger">
          <Trash />
          {t('duplicates.match_group.delete_all_button.label')}
        </Button>
      </div>
      <Row>
        {matches.map(({ id, pictureId, targetId, similarity }, index) => (
          <Col sm={12} md={6} key={index} className="mb-2">
            <MatchPicture id={id} pictureId={pictureId} targetId={targetId} similarity={similarity} />
          </Col>
        ))}
      </Row>

      <ConfirmModal show={isShowUnlinkAllConfirm} onConfirm={onUnlinkAllConfirmed} onCancel={hideUnlinkAllConfirm}>
        {t('duplicates.match_group.unlink_all_button.confirm', { count: matches.length })}
      </ConfirmModal>

      <ConfirmModal show={isShowDeleteAllConfirm} onConfirm={onDeleteAllConfirmed} onCancel={hideDeleteAllConfirm}>
        <p>{t('duplicates.match_group.delete_all_button.confirm', { count: matches.length })}</p>
        <span className="text-danger">{t('common.action_can_not_be_undone')}</span>
      </ConfirmModal>
    </>
  );
};
