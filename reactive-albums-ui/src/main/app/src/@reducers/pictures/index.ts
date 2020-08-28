import { combineReducers } from '@reduxjs/toolkit';
import { pictureStoreReducer } from './picture-store.slice';

export const picturesSliceName = 'pictures';
export const picturesSliceReducer = combineReducers({
  pictureStore: pictureStoreReducer,
});

export type PicturesSliceState = ReturnType<typeof picturesSliceReducer>;

export * from './picture.actions'
export * from './picture.thunks'
export * from './picture.selector-hooks'
export * from './picture-store.slice'
