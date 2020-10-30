import { Picture } from '@types';
import { useEffect, useState } from 'react';
import { fetchPictureById } from '@reducers';
import { useDispatch } from '@hooks';
import { unwrapResult } from '@reduxjs/toolkit';

export function usePictureById(pictureId: string): Picture | undefined {
  const dispatch = useDispatch();
  const [picture, setPicture] = useState<Picture>();

  useEffect(() => {
    dispatch(fetchPictureById({ pictureId })).then(unwrapResult).then(setPicture);
  }, [dispatch, setPicture, pictureId]);

  return picture;
}
