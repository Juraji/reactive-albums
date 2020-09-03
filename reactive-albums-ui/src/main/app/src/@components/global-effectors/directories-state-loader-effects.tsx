import React, { FC, ReactElement } from 'react';
import { useDispatch, useEventSource } from '@hooks';
import { Directory } from '@types';
import { deleteDirectories, fetchAllDirectories, upsertDirectories } from '@reducers';
import { ReactiveEvent } from '@types';

export const DirectoriesStateLoaderEffects: FC = (): ReactElement => {
  const dispatch = useDispatch();

  // Dispatched once
  dispatch(fetchAllDirectories());

  const onDirectoriesMessage = (data: string) => {
    const events: ReactiveEvent<Directory>[] = JSON.parse(data);
    const deleted = events.filter((e) => e.type === 'DELETE').map((e) => e.entity.id);
    dispatch(deleteDirectories(deleted));

    const upserted = events.filter((e) => e.type === 'UPDATE').map((e) => e.entity);
    dispatch(upsertDirectories(upserted));
  };

  useEventSource('/directories/updates', {}, [dispatch], onDirectoriesMessage);

  return <></>;
};
