import { Picture } from '@types';
import React, { ChangeEvent, FC, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useDispatch, useToggleState } from '@hooks';
import { useToasts } from 'react-toast-notifications';
import { deletePicture } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';
import Button from 'react-bootstrap/Button';
import { Trash } from 'react-feather';
import { ConfirmModal } from './confirm-modal';
import FormCheck from 'react-bootstrap/FormCheck';

interface DeletePictureButtonProps {
  picture: Picture;
  onDeleteConfirmed?: () => void;
}

export const DeletePictureButton: FC<DeletePictureButtonProps> = ({ picture, onDeleteConfirmed }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const { addToast } = useToasts();

  const [isShowDeleteConfirm, showDeleteConfirm, hideDeleteConfirm] = useToggleState(false);
  const [deletePhysicalFile, setDeletePhysicalFile] = useState(true);

  const onDeletePictureConfirmed = () => {
    dispatch(deletePicture({ pictureId: picture.id }))
      .then(unwrapResult)
      .then(() => addToast(t('delete_picture_button.delete_picture_success'), { appearance: 'success' }))
      .catch(() => addToast(t('delete_picture_button.delete_picture_failed'), { appearance: 'error' }))
      .finally(() => {
        hideDeleteConfirm();
        if (!!onDeleteConfirmed) {
          onDeleteConfirmed();
        }
      });
  };
  return (
    <>
      <Button variant="danger" onClick={showDeleteConfirm} title={t('delete_picture_button.button_title')}>
        <Trash />
      </Button>

      <ConfirmModal show={isShowDeleteConfirm} onConfirm={onDeletePictureConfirmed} onCancel={hideDeleteConfirm}>
        <p>{t('delete_picture_button.delete_picture_confirm', picture)}</p>
        <FormCheck
          label={t('delete_picture_button.delete_picture_confirm_delete_physical')}
          checked={deletePhysicalFile}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setDeletePhysicalFile(e.target.checked)}
        />
        {deletePhysicalFile ? <span className="text-danger">{t('common.action_can_not_be_undone')}</span> : null}
      </ConfirmModal>
    </>
  );
};
