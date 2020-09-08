import React, { FC } from 'react';
import Card from 'react-bootstrap/Card';
import { DuplicateMatch } from '@types';
import { Conditional } from '@components';
import ListGroup from 'react-bootstrap/ListGroup';
import ListGroupItem from 'react-bootstrap/ListGroupItem';
import { useTranslation } from 'react-i18next';
import Badge from 'react-bootstrap/Badge';
import { deletePicture, unlinkDuplicateMatch, usePicture, usePictureDuplicates } from '@reducers';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import Button from 'react-bootstrap/Button';
import { Scissors, Trash } from 'react-feather';
import { useApiUrl, useDispatch, useToggleState } from '@hooks';

import './picture-duplicate-list.scss';
import { Link } from 'react-router-dom';
import { ConfirmModal } from '../../@components/confirm-modal';
import { unwrapResult } from '@reduxjs/toolkit';
import { useToasts } from 'react-toast-notifications';

interface DuplicateMatchRowProps {
  match: DuplicateMatch;
}

export const DuplicateMatchRow: FC<DuplicateMatchRowProps> = ({ match }) => {
  const targetPicture = usePicture(match.targetId);
  const thumbnailUrl = useApiUrl('pictures', match.targetId, 'thumbnail');
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();

  const [isShowUnlinkConfirm, showUnlinkConfirm, hideUnlinkConfirm] = useToggleState(false);
  const [isShowDeleteConfirm, showDeleteConfirm, hideDeleteConfirm] = useToggleState(false);

  function onUnlinkDuplicateConfirmed() {
    dispatch(unlinkDuplicateMatch({ pictureId: match.pictureId, matchId: match.id }))
      .then(unwrapResult)
      .then(() => addToast(t('picture.duplicates_list.unlink_single_button.success')))
      .catch(() => addToast(t('picture.duplicates_list.unlink_single_button.failed', { appearance: 'error' })));
    hideUnlinkConfirm();
  }

  function onDeleteDuplicateConfirmed() {
    dispatch(deletePicture({ pictureId: match.targetId }))
      .then(unwrapResult)
      .then(() => addToast(t('picture.duplicates_list.delete_single_button.success')))
      .catch(() => addToast(t('picture.duplicates_list.delete_single_button.failed', { appearance: 'error' })));
    hideDeleteConfirm();
  }

  return (
    <>
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
            <Button onClick={showUnlinkConfirm} title={t('picture.duplicates_list.unlink_single_button.label')}>
              <Scissors />
            </Button>
            <Button
              onClick={showDeleteConfirm}
              title={t('picture.duplicates_list.delete_single_button.label')}
              variant="danger"
            >
              <Trash />
            </Button>
          </ButtonGroup>
        </div>
      </ListGroupItem>

      <ConfirmModal show={isShowUnlinkConfirm} onConfirm={onUnlinkDuplicateConfirmed} onCancel={hideUnlinkConfirm}>
        {t('picture.duplicates_list.unlink_single_button.confirm', targetPicture)}
      </ConfirmModal>

      <ConfirmModal show={isShowDeleteConfirm} onConfirm={onDeleteDuplicateConfirmed} onCancel={hideDeleteConfirm}>
        <p>{t('picture.duplicates_list.delete_single_button.confirm', targetPicture)}</p>
        <span className="text-danger">{t('common.action_can_not_be_undone')}</span>
      </ConfirmModal>
    </>
  );
};

interface PictureDuplicateListProps {
  pictureId: string;
}

export const PictureDuplicateList: FC<PictureDuplicateListProps> = ({ pictureId }) => {
  const matches = usePictureDuplicates(pictureId);
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();

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
