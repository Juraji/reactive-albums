import { Tag } from '@types';
import { useDispatch, useSelector } from '@hooks';
import { useEffect } from 'react';
import { fetchAllTags, selectAllTags } from '@reducers';

export function useTags(): Tag[] {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchAllTags());
  }, [dispatch]);

  return useSelector((state) => selectAllTags(state));
}
