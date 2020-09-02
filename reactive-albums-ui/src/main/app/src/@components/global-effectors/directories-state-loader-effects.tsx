import React, { FC, ReactElement } from 'react';
import { useDispatch, useEventSource } from '@hooks';
import { Directory } from '@types';
import { fetchAllDirectories, upsertDirectories } from '@reducers';

export const DirectoriesStateLoaderEffects: FC = (): ReactElement => {
  const dispatch = useDispatch();

  // Dispatched once
  dispatch(fetchAllDirectories());

  const onDirectoriesMessage = (data: string) => {
    const directories: Directory[] = JSON.parse(data);
    dispatch(upsertDirectories(directories));
  };

  useEventSource('/directories/updates', {}, [dispatch], onDirectoriesMessage);

  return <></>;
};
