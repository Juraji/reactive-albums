import { Directory } from '@types';
import { useEffect } from 'react';
import { fetchAllDirectories, selectAllDirectories } from '@reducers';
import { useDispatch, useSelector } from '@hooks';

export function useDirectories(doFetch: boolean = true): Directory[] {
  const dispatch = useDispatch();

  useEffect(() => {
    if (doFetch) {
      dispatch(fetchAllDirectories());
    }
  }, [dispatch, doFetch]);

  return useSelector((state) => selectAllDirectories(state));
}
