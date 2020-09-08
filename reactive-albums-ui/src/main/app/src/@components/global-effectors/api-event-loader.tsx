import React, { FC } from 'react';
import { useDispatch, useEventSource } from '@hooks';
import { Directory, DuplicateMatch, Picture, ReactiveEvent, ReactiveEventType } from '@types';
import {
  deleteEventDirectories,
  deleteEventDuplicateMatches,
  deleteEventPictures,
  upsertEventDirectories,
  upsertEventDuplicateMatches,
  upsertEventPictures,
} from '@reducers';

type EventGrouping = Record<string, ReactiveEvent<any>[]>;

export const ApiEventLoader: FC = () => {
  const dispatch = useDispatch();

  function filterAndMap<T, R>(e: ReactiveEvent<T>[], type: ReactiveEventType, mapper: (e: ReactiveEvent<T>) => R): R[] {
    return e.filter((e) => e.type === type).map(mapper);
  }

  const onDirectoryEvent = (events: ReactiveEvent<Directory>[]) => {
    const deleted = filterAndMap(events, 'DELETE', (e) => e.entity.id);
    dispatch(deleteEventDirectories(deleted));

    const upserted = filterAndMap(events, 'UPDATE', (e) => e.entity);
    dispatch(upsertEventDirectories(upserted));
  };

  const onPictureEvent = (events: ReactiveEvent<Picture>[]) => {
    const deleted = filterAndMap(events, 'DELETE', (e) => e.entity.id);
    dispatch(deleteEventPictures(deleted));

    const upserted = filterAndMap(events, 'UPDATE', (e) => e.entity);
    dispatch(upsertEventPictures(upserted));
  };

  const onDuplicateMatchEvent = (events: ReactiveEvent<DuplicateMatch>[]) => {
    const deleted = filterAndMap(events, 'DELETE', (e) => e.entity.id);
    dispatch(deleteEventDuplicateMatches(deleted));

    const upserted = filterAndMap(events, 'UPDATE', (e) => e.entity);
    dispatch(upsertEventDuplicateMatches(upserted));
  };

  const onEvent = (data: string) => {
    const events: ReactiveEvent<any>[] = JSON.parse(data);
    const groups: EventGrouping = events.reduce((acc, next) => {
      acc[next.entityType] = !!acc[next.entityType] ? [...acc[next.entityType], next] : [next];
      return acc;
    }, {} as EventGrouping);

    Object.entries(groups).forEach(([entityType, events]) => {
      switch (entityType) {
        case 'DirectoryProjection':
          onDirectoryEvent(events);
          break;
        case 'PictureProjection':
          onPictureEvent(events);
          break;
        case 'DuplicateMatchProjection':
          onDuplicateMatchEvent(events);
          break;
        default:
          console.error(`No event handler for api event of type ${entityType}`, events);
          break;
      }
    });
  };

  useEventSource('/events', {}, [dispatch], onEvent);

  return <></>;
};
