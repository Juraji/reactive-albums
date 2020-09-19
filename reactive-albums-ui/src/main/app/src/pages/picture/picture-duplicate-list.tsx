import React, { FC, useCallback } from 'react';
import Card from 'react-bootstrap/Card';
import { DuplicateMatch, ReactiveEvent } from '@types';
import { Conditional, ConfirmModal, DeletePictureButton } from '@components';
import ListGroup from 'react-bootstrap/ListGroup';
import ListGroupItem from 'react-bootstrap/ListGroupItem';
import { useTranslation } from 'react-i18next';
import Badge from 'react-bootstrap/Badge';
import {
  addActivePictureDuplicateMatch,
  deletePicture,
  removeActivePictureDuplicateMatch,
  unlinkDuplicateMatch,
  useActivePictureDuplicates,
} from '@reducers';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import Button from 'react-bootstrap/Button';
import { Scissors, Trash } from 'react-feather';
import { useApiUrl, useDispatch, useEventSource, useToggleState } from '@hooks';
import { Link } from 'react-router-dom';
import { unwrapResult } from '@reduxjs/toolkit';
import { useToasts } from 'react-toast-notifications';

import './picture-duplicate-list.scss';

interface DuplicateMatchRowProps {
  match: DuplicateMatch;
}

export const DuplicateMatchRow: FC<DuplicateMatchRowProps> = ({ match }) => {
  const targetPicture = match.target;
  const thumbnailUrl = useApiUrl('pictures', match.targetId, 'thumbnail');
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();

  const [isShowUnlinkConfirm, showUnlinkConfirm, hideUnlinkConfirm] = useToggleState(false);

  function onUnlinkDuplicateConfirmed() {
    dispatch(unlinkDuplicateMatch({ pictureId: match.pictureId, matchId: match.id }))
      .then(unwrapResult)
      .then(() => addToast(t('picture.duplicates_list.unlink_single_button.success')))
      .catch(() => addToast(t('picture.duplicates_list.unlink_single_button.failed', { appearance: 'error' })));
    hideUnlinkConfirm();
  }

  return (
    <Conditional condition={!!targetPicture}>
      <ListGroupItem className="duplicate-match-row">
        <div className="duplicate-match-details d-flex flex-row mb-1">
          <Link to={`/picture/${match.targetId}`}>
            <img
              src={thumbnailUrl}
              className="img-thumbnail p-0 duplicate-match-thumbnail"
              alt={targetPicture?.displayName}
            />
          </Link>
          <h6 className="duplicate-match-display-name text-ellipsis flex-grow-1 mx-1">
            <span>{targetPicture?.displayName}</span>
            <br />
            <small>{match.similarity}%</small>
          </h6>
          <ButtonGroup size="sm" vertical>
            <Button onClick={showUnlinkConfirm} title={t('picture.duplicates_list.unlink_single_button.label')}>
              <Scissors />
            </Button>
            <DeletePictureButton picture={targetPicture!} />
          </ButtonGroup>
        </div>
      </ListGroupItem>

      <ConfirmModal show={isShowUnlinkConfirm} onConfirm={onUnlinkDuplicateConfirmed} onCancel={hideUnlinkConfirm}>
        {t('picture.duplicates_list.unlink_single_button.confirm', targetPicture)}
      </ConfirmModal>
    </Conditional>
  );
};

interface PictureDuplicateListProps {
  pictureId: string;
}

export const PictureDuplicateList: FC<PictureDuplicateListProps> = ({ pictureId }) => {
  const matches = useActivePictureDuplicates();
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();

  const onDuplicateMatchesEvent = useCallback(
    (msg: string) => {
      const evt: ReactiveEvent<DuplicateMatch> = JSON.parse(msg);

      switch (evt.type) {
        case 'UPSERT':
          dispatch(addActivePictureDuplicateMatch(evt.entity));
          break;
        case 'DELETE':
          dispatch(removeActivePictureDuplicateMatch({ id: evt.entity.id }));
          break;
      }
    },
    [dispatch]
  );

  const matchesEndpoint = useApiUrl('events', 'duplicate-matches', pictureId);
  useEventSource(onDuplicateMatchesEvent, matchesEndpoint, [pictureId]);

  const [isShowUnlinkAllConfirm, showUnlinkAllConfirm, hideUnlinkAllConfirm] = useToggleState(false);
  const [isShowDeleteAllConfirm, showDeleteAllConfirm, hideDeleteAllConfirm] = useToggleState(false);

  function onUnlinkAllConfirmed() {
    const unlinkAction = ({ id: matchId }: DuplicateMatch) => unlinkDuplicateMatch({ pictureId, matchId });

    Promise.all(matches.map((match) => dispatch(unlinkAction(match)).then(unwrapResult)))
      .then(() => addToast(t('picture.duplicates_list.unlink_all_button.success')))
      .catch(() => addToast(t('picture.duplicates_list.unlink_all_button.failed', { appearance: 'error' })));
    hideUnlinkAllConfirm();
  }

  function onDeleteAllConfirmed() {
    const deleteAction = ({ pictureId }: DuplicateMatch) => deletePicture({ pictureId });
    Promise.all(matches.map((match) => dispatch(deleteAction(match)).then(unwrapResult)))
      .then(() => addToast(t('picture.duplicates_list.delete_all_button.success')))
      .catch(() => addToast(t('picture.duplicates_list.delete_all_button.failed', { appearance: 'error' })));
    hideDeleteAllConfirm();
  }

  return (
    <Conditional condition={matches.isNotEmpty()}>
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
            <Button onClick={showUnlinkAllConfirm} title={t('picture.duplicates_list.unlink_all_button.label')}>
              <Scissors />
            </Button>
            <Button
              onClick={showDeleteAllConfirm}
              variant="danger"
              title={t('picture.duplicates_list.delete_all_button.label')}
            >
              <Trash />
            </Button>
          </ButtonGroup>
        </Card.Footer>
      </Card>

      <ConfirmModal show={isShowUnlinkAllConfirm} onConfirm={onUnlinkAllConfirmed} onCancel={hideUnlinkAllConfirm}>
        {t('picture.duplicates_list.unlink_all_button.confirm', { count: matches.length })}
      </ConfirmModal>

      <ConfirmModal show={isShowDeleteAllConfirm} onConfirm={onDeleteAllConfirmed} onCancel={hideDeleteAllConfirm}>
        <p>{t('picture.duplicates_list.delete_all_button.confirm', { count: matches.length })}</p>
        <span className="text-danger">{t('common.action_can_not_be_undone')}</span>
      </ConfirmModal>
    </Conditional>
  );
};
