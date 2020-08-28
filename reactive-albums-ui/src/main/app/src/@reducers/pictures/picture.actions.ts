import {createAction} from '@reduxjs/toolkit';
import {Picture} from '@types';

export const upsertPicture = createAction<Picture>('pictures/upsertPicture');

export const activatePicture = createAction<Picture>('pictures/activatePicture');
export const deactivatePicture = createAction('pictures/deactivatePicture');
