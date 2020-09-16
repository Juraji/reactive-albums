import { combineReducers } from '@reduxjs/toolkit';
import { activePictureSliceName, activePictureSliceReducer } from './active-picture-slice';

export const picturesSliceName = 'pictures';
export const picturesSliceReducer = combineReducers({
  [activePictureSliceName]: activePictureSliceReducer,
});

export type PicturesSliceState = ReturnType<typeof picturesSliceReducer>;

export * from './picture.actions';
export * from './picture.thunks';
export * from './hooks';

export * from './active-picture-slice';
