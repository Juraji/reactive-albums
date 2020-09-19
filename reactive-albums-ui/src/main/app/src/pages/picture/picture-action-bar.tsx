import React, { FC } from 'react';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import Button from 'react-bootstrap/Button';
import { useTranslation } from 'react-i18next';
import { Copy, Move, RefreshCw } from 'react-feather';
import { Picture } from '@types';
import { useToasts } from 'react-toast-notifications';
import { useDispatch } from '@hooks';
import { rescanDuplicates } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';
import { DeletePictureButton } from '@components';

interface PictureActionBarProps {
  picture: Picture;
}

export const PictureActionBar: FC<PictureActionBarProps> = ({ picture }) => {
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();

  const onRerunAttributeAnalysis = () => {
    console.log('onRerunAttributeAnalysis');
  };

  const onRerunDuplicateAnalysis = () => {
    dispatch(rescanDuplicates({ pictureId: picture.id }))
      .then(unwrapResult)
      .then(() => addToast(t('picture.actions.rerun_duplicate_analysis_success')))
      .catch(() => addToast(t('picture.actions.rerun_duplicate_analysis_failed'), { appearance: 'error' }));
  };

  const onMovePicture = () => {
    console.log('onMovePicture');
  };

  return (
    <ButtonGroup size="sm" className="mb-2">
      <Button onClick={onRerunAttributeAnalysis} title={t('picture.actions.rerun_attribute_analysis')}>
        <RefreshCw />
      </Button>
      <Button onClick={onRerunDuplicateAnalysis} title={t('picture.actions.rerun_duplicate_analysis')}>
        <Copy />
      </Button>
      <Button onClick={onMovePicture} title={t('picture.actions.move_picture')}>
        <Move />
      </Button>
      <DeletePictureButton picture={picture} />
    </ButtonGroup>
  );
};
