import { Page, Picture } from '@types';
import { useMemo } from 'react';
import { selectAllPictures, selectTotalPictures } from './picture-store.slice';
import { EntityState } from '@reduxjs/toolkit';
import { useSelector } from '@hooks';

export function usePicturesStoreState(): EntityState<Picture> {
  return useSelector((state) => state.pictures.pictureStore);
}

export function usePicturesPage(pageNumber: number, size: number): Page<Picture> {
  const picturesStore = usePicturesStoreState();
  const content = useMemo(() => {
    const all = selectAllPictures(picturesStore);
    const start = pageNumber * size;
    const end = start + size;
    return all.slice(start, end);
  }, [picturesStore, pageNumber, size]);
  const total = useMemo(() => selectTotalPictures(picturesStore), [picturesStore]);

  return new Page<Picture>(content, pageNumber, total, size);
}
