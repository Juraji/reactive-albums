import { combineReducers } from '@reduxjs/toolkit';
import { pictureStoreReducer } from './picture-store.slice';
import { duplicateMatchStoreReducer } from './duplicates-store.slice';

export const picturesSliceName = 'pictures';
export const picturesSliceReducer = combineReducers({
  pictureStore: pictureStoreReducer,
  duplicateMatches: duplicateMatchStoreReducer,
});

export type PicturesSliceState = ReturnType<typeof picturesSliceReducer>;

export * from './picture.actions';
export * from './picture.thunks';
export * from './hooks';
export * from './picture-store.slice';
export * from './duplicates-store.slice';
