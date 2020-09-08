import { createAction } from '@reduxjs/toolkit';
import { DuplicateMatch, Picture } from '@types';

export const upsertPictures = createAction<Picture[]>('pictures/upsertPictures');
export const deletePictures = createAction<string[]>('pictures/deletePictures');

export const upsertDuplicateMatches = createAction<DuplicateMatch[]>('pictures/upsertDuplicateMatches');
export const deleteDuplicateMatches = createAction<string[]>('pictures/deleteDuplicateMatches');

export const activatePicture = createAction<Picture>('pictures/activatePicture');
export const deactivatePicture = createAction('pictures/deactivatePicture');
