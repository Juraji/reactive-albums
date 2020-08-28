import React, { FC, ReactElement } from 'react';
import { useEventSource } from '../../@hooks/use-event-source';
import { useDispatch } from '@hooks';
import { Picture } from '@types';
import { upsertPicture } from '@reducers';

export const PicturesStateLoaderEffects: FC = (): ReactElement => {
  const dispatch = useDispatch();

  const onPictureMessage = (data: string) => {
    const picture: Picture = JSON.parse(data);
    dispatch(upsertPicture(picture));
  };

  useEventSource('/pictures', {}, [dispatch], onPictureMessage);

  return <></>;
};
