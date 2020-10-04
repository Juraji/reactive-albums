import { createAction } from '@reduxjs/toolkit';
import { DuplicateMatch } from '@types';

export const addActivePictureDuplicateMatch = createAction<DuplicateMatch>('pictures/addActivePictureDuplicateMatch');
export const removeActivePictureDuplicateMatch = createAction<string>('pictures/removeActivePictureDuplicateMatch');
export const deactivatePicture = createAction('pictures/deactivatePicture');

export const setPictureOverviewFilter = createAction<string>('pictures/setPictureOverviewFilter');
