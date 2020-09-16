import { createAction } from '@reduxjs/toolkit';
import { DuplicateMatchView } from '@types';

export const addActivePictureDuplicateMatch = createAction<DuplicateMatchView>(
  'pictures/addActivePictureDuplicateMatch'
);
export const removeActivePictureDuplicateMatch = createAction<{ id: string }>(
  'pictures/removeActivePictureDuplicateMatch'
);
export const deactivatePicture = createAction('pictures/deactivatePicture');
