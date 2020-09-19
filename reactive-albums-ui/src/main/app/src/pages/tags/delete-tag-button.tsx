import React, { FC } from 'react';
import Button from 'react-bootstrap/Button';
import { useDispatch, useToggleState } from '@hooks';
import { Tag } from '@types';
import { Trash } from 'react-feather';
import { ConfirmModal } from '@components';
import { useTranslation } from 'react-i18next';
import { deleteTag } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';
import { useToasts } from 'react-toast-notifications';

interface DeleteTagButtonProps {
  tag: Tag;
}

export const DeleteTagButton: FC<DeleteTagButtonProps> = ({ tag }) => {
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();
  const [isShowDeleteConfirm, showDeleteConfirm, hideDeleteConfirm] = useToggleState(false);

  const onDeleteConfirmed = () => {
    dispatch(deleteTag({ id: tag.id }))
      .then(unwrapResult)
      .then(() => {
        hideDeleteConfirm();
        return addToast(t('tags.delete_tag_button.delete_success', { appearance: 'success' }));
      })
      .catch(() => addToast(t('tags.delete_tag_button.delete_failed'), { appearance: 'error' }));
  };

  return (
    <>
      <Button variant="danger" onClick={showDeleteConfirm}>
        <Trash />
      </Button>

      {isShowDeleteConfirm ? (
        <ConfirmModal show onConfirm={onDeleteConfirmed} onCancel={hideDeleteConfirm}>
          <p>{t('tags.delete_tag_button.confirm', tag)}</p>
          <span className="text-danger">{t('common.action_can_not_be_undone')}</span>
        </ConfirmModal>
      ) : null}
    </>
  );
};
