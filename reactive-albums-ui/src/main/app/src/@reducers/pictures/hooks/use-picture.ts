import { Picture } from '@types';
import { useEffect, useState } from 'react';
import { useDispatch } from '@hooks';
import { fetchPictureById } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';

export function usePicture(pictureId: string): Picture | undefined {
  const dispatch = useDispatch();
  const [picture, setPicture] = useState<Picture | undefined>();

  useEffect(() => {
    dispatch(fetchPictureById({ pictureId })).then(unwrapResult).then(setPicture);
  }, [pictureId, dispatch, setPicture]);

  return picture;
}
