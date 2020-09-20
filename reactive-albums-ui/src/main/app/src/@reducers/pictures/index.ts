import { combineReducers } from '@reduxjs/toolkit';
import { activePictureSliceName, activePictureSliceReducer } from './active-picture-slice';
import { pictureOverviewSliceName, pictureOverviewSliceReducer } from './picture-overview.slice';

export const picturesSliceName = 'pictures';
export const picturesSliceReducer = combineReducers({
  [activePictureSliceName]: activePictureSliceReducer,
  [pictureOverviewSliceName]: pictureOverviewSliceReducer,
});

export type PicturesSliceState = ReturnType<typeof picturesSliceReducer>;

export * from './picture.actions';
export * from './picture.thunks';
export * from './hooks';

export * from './active-picture-slice';
export * from './picture-overview.slice';
