import { createAction } from '@reduxjs/toolkit';
import { DuplicateMatch } from '@types';

export const addActivePictureDuplicateMatch = createAction<DuplicateMatch>('pictures/addActivePictureDuplicateMatch');
export const removeActivePictureDuplicateMatch = createAction<{ id: string }>(
  'pictures/removeActivePictureDuplicateMatch'
);
export const deactivatePicture = createAction('pictures/deactivatePicture');
