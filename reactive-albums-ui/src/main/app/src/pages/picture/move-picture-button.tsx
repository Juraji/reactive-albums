import { Directory, Picture } from '@types';
import React, { ChangeEvent, FC, useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useDispatch, useToggleState } from '@hooks';
import { useToasts } from 'react-toast-notifications';
import { movePicture, useDirectories } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';
import Button from 'react-bootstrap/Button';
import { Move } from 'react-feather';
import { ConfirmModal } from '@components';
import Form from 'react-bootstrap/Form';

interface MovePictureButtonProps {
  picture: Picture;
}

export const MovePictureButton: FC<MovePictureButtonProps> = ({ picture }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const { addToast } = useToasts();

  const [isShowMoveConfirm, showMoveConfirm, hideMoveConfirm] = useToggleState(false);
  const [targetDirectory, setTargetDirectory] = useState<Directory | undefined>();
  const targetDirectoryId = useMemo(() => targetDirectory?.id || '', [targetDirectory]);

  const directories = useDirectories(isShowMoveConfirm);

  useEffect(() => setTargetDirectory(directories[0]), [directories]);

  const onSelectedDirectoryIdChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setTargetDirectory(directories.find((it) => it.id === e.target.value));
  };

  const onMoveConfirmed = () => {
    if (!!targetDirectory) {
      dispatch(
        movePicture({
          pictureId: picture.id,
          targetDirectoryId: targetDirectory.id,
        })
      )
        .then(unwrapResult)
        .then(() => {
          hideMoveConfirm();
          return addToast(t('picture.actions.move_picture_button.move_success'), { appearance: 'success' });
        })
        .catch((e) => addToast(t('picture.actions.move_picture_button.move_failed', e), { appearance: 'error' }));
    }
  };

  return (
    <>
      <Button onClick={showMoveConfirm} title={t('picture.actions.move_picture_button.title')}>
        <Move />
      </Button>

      <ConfirmModal
        show={isShowMoveConfirm}
        onConfirm={onMoveConfirmed}
        onCancel={hideMoveConfirm}
        headerTitle={t('picture.actions.move_picture_button.title')}
        isValid={!!targetDirectory}
      >
        <Form.Group controlId="exampleForm.SelectCustom">
          <Form.Label>{t('picture.actions.move_picture_button.select_directory_label')}</Form.Label>
          <Form.Control as="select" custom value={targetDirectoryId} onChange={onSelectedDirectoryIdChange}>
            {directories.map((d) => (
              <option key={d.id} value={d.id}>
                {d.location}
              </option>
            ))}
          </Form.Control>
        </Form.Group>
      </ConfirmModal>
    </>
  );
};
