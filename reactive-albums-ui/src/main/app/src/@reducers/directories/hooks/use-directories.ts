import { Directory } from '@types';
import { useEffect, useState } from 'react';
import { fetchAllDirectories } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';
import { useDispatch } from '@hooks';

export function useDirectories(): Directory[] {
  const dispatch = useDispatch();
  const [directories, setDirectories] = useState<Directory[]>([]);

  useEffect(() => {
    dispatch(fetchAllDirectories()).then(unwrapResult).then(setDirectories);
  }, [dispatch, setDirectories]);

  return directories;
}
