import { combineReducers } from '@reduxjs/toolkit';
import { pictureStoreReducer } from './picture-store.slice';
import { activePictureReducer } from './active-picture.slice';

export const picturesSliceName = 'pictures';
export const picturesSliceReducer = combineReducers({
  pictureStore: pictureStoreReducer,
  activePicture: activePictureReducer,
});

export type PicturesSliceState = ReturnType<typeof picturesSliceReducer>;

export * from './picture.actions';
export * from './picture.thunks';
export * from './hooks';
export * from './picture-store.slice';
export * from './active-picture.slice';
export { usePicturesStore } from './hooks/use-pictures-store';
