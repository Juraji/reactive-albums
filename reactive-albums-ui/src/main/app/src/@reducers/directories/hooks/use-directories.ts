import { Directory } from '@types';
import { useEffect } from 'react';
import { fetchAllDirectories, selectAllDirectories } from '@reducers';
import { useDispatch, useSelector } from '@hooks';

export function useDirectories(): Directory[] {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchAllDirectories());
  }, [dispatch]);

  return useSelector((state) => selectAllDirectories(state));
}
