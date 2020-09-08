import React, { FC } from 'react';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import Button from 'react-bootstrap/Button';
import { useTranslation } from 'react-i18next';
import { Copy, Move, RefreshCw, Trash } from 'react-feather';
import { Picture } from '@types';

interface PictureActionBarProps {
  picture: Picture;
}

export const PictureActionBar: FC<PictureActionBarProps> = ({ picture }) => {
  const { t } = useTranslation();

  const onRerunAttributeAnalysis = () => {
    console.log('onRerunAttributeAnalysis');
  };

  const onRerunDuplicateAnalysis = () => {
    console.log('onRerunDuplicateAnalysis');
  };

  const onMovePicture = () => {
    console.log('onMovePicture');
  };

  const onDeletePicture = () => {
    console.log('onDeletePicture');
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
      <Button variant="danger" onClick={onDeletePicture} title={t('picture.actions.delete_picture')}>
        <Trash />
      </Button>
    </ButtonGroup>
  );
};
