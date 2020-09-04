import { Page, Picture } from '@types';
import { useCallback, useMemo, useState } from 'react';
import { selectAllPictures } from './picture-store.slice';
import { EntityState } from '@reduxjs/toolkit';
import { useSelector } from '@hooks';

export function usePicturesStoreState(): EntityState<Picture> {
  return useSelector((state) => state.pictures.pictureStore);
}

export interface PicturesPageResult {
  page: Page<Picture>;
  setSize: (size: number) => void;
  setPage: (size: number) => void;
  setFilter: (value: string) => void;
}

export function usePicturesPage(): PicturesPageResult {
  const [size, setSize] = useState(50);
  const [pageNo, setPage] = useState(0);
  const [filter, setFilter] = useState('');

  const applyFilter = useCallback(
    (items: Picture[]): Picture[] => {
      if (!!filter) {
        const term = filter.toLowerCase();
        return items.filter((p) => p.displayName.toLowerCase().includes(term));
      } else {
        return items;
      }
    },
    [filter]
  );

  const applyPaging = useCallback(
    (items: Picture[]): Picture[] => {
      const start = pageNo * size;
      const end = start + size;
      return items.slice(start, end);
    },
    [pageNo, size]
  );

  const picturesStore = usePicturesStoreState();
  const { content, total } = useMemo(() => {
    const all = selectAllPictures(picturesStore);
    const filtered = applyFilter(all);
    const paged = applyPaging(filtered);

    return { content: paged, total: filtered.length };
  }, [picturesStore, applyPaging, applyFilter]);

  const page = useMemo(() => new Page<Picture>(content, pageNo, total, size, filter), [
    content,
    total,
    pageNo,
    size,
    filter,
  ]);

  return useMemo(() => ({ page, setSize, setPage, setFilter }), [page, setSize, setPage, setFilter]);
}
