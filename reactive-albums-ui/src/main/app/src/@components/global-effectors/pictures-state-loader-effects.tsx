import React, {FC, ReactElement, useEffect} from 'react';
import {useDispatch, useEventSource} from '@hooks';
import {Picture, ReactiveEvent} from '@types';
import {deletePictures, fetchAllPictures, upsertPictures} from '@reducers';

export const PicturesStateLoaderEffects: FC = (): ReactElement => {
  const dispatch = useDispatch();

  const onPictureMessage = (data: string) => {
    const events: ReactiveEvent<Picture>[] = JSON.parse(data);
    const deleted = events.filter((e) => e.type === 'DELETE').map((e) => e.entity.id);
    dispatch(deletePictures(deleted));

    const upserted = events.filter((e) => e.type === 'UPDATE').map((e) => e.entity);
    dispatch(upsertPictures(upserted));
  };

  useEventSource('/pictures/updates', {}, [dispatch], onPictureMessage);

  useEffect(() => {
    dispatch(fetchAllPictures());
  }, [dispatch]);

  return <></>;
};
