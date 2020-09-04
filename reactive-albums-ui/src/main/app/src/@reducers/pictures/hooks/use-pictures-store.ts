import { EntityState } from '@reduxjs/toolkit';
import { Picture } from '@types';
import { useSelector } from '@hooks';

export function usePicturesStore(): EntityState<Picture> {
  return useSelector((state) => state.pictures.pictureStore);
}
