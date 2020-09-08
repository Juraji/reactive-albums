import { createAction } from '@reduxjs/toolkit';
import { DuplicateMatch, Picture } from '@types';

export const upsertEventPictures = createAction<Picture[]>('pictures/events/upsertPictures');
export const deleteEventPictures = createAction<string[]>('pictures/events/deletePictures');

export const upsertEventDuplicateMatches = createAction<DuplicateMatch[]>('pictures/events/upsertDuplicateMatches');
export const deleteEventDuplicateMatches = createAction<string[]>('pictures/events/deleteDuplicateMatches');
