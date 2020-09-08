import React, { FC, useEffect, useState } from 'react';
import { useDispatch } from '@hooks';
import { fetchAllDirectories, fetchAllDuplicateMatches, fetchAllPictures } from '@reducers';

export const StateRehydrator: FC = () => {
  const dispatch = useDispatch();
  const [rehydrated, setRehydrated] = useState(false);

  useEffect(() => {
    if (!rehydrated) {
      dispatch(fetchAllPictures());
      dispatch(fetchAllDirectories());
      dispatch(fetchAllDuplicateMatches());
      setRehydrated(true);
    }
  }, [dispatch, rehydrated, setRehydrated]);

  return <></>;
};
