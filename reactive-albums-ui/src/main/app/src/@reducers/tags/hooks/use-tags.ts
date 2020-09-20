import { Tag } from '@types';
import { useDispatch, useSelector } from '@hooks';
import { useEffect } from 'react';
import { fetchAllTags, selectAllTags } from '@reducers';

export function useTags(doFetch: boolean = true): Tag[] {
  const dispatch = useDispatch();

  useEffect(() => {
    if (doFetch) {
      dispatch(fetchAllTags());
    }
  }, [doFetch, dispatch]);

  return useSelector((state) => selectAllTags(state));
}
