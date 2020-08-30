import React, { FC, ReactElement, useEffect } from 'react';
import { useDispatch, useEventSource } from '@hooks';
import { Picture } from '@types';
import { fetchAllPictures, upsertPictures } from '@reducers';

export const PicturesStateLoaderEffects: FC = (): ReactElement => {
  const dispatch = useDispatch();

  const onPictureMessage = (data: string) => {
    const pictures: Picture[] = JSON.parse(data);
    dispatch(upsertPictures(pictures));
  };

  useEventSource('/pictures/updates', {}, [dispatch], onPictureMessage);

  useEffect(() => {
    dispatch(fetchAllPictures());
  }, [dispatch]);

  return <></>;
};
