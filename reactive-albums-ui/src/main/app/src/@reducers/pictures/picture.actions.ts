import { createAction } from '@reduxjs/toolkit';
import { Picture } from '@types';

export const upsertPictures = createAction<Picture[]>('pictures/upsertPictures');
export const deletePictures = createAction<string[]>('pictures/deletePictures');

export const activatePicture = createAction<Picture>('pictures/activatePicture');
export const deactivatePicture = createAction('pictures/deactivatePicture');
