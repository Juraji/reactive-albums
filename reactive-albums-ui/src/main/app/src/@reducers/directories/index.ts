import { combineReducers } from '@reduxjs/toolkit';
import { directoryStoreReducer } from './directory-store.slice';

export const directoriesSliceName = 'directories';
export const directoriesSliceReducer = combineReducers({
  directoryStore: directoryStoreReducer,
});

export type DirectoriesSliceState = ReturnType<typeof directoriesSliceReducer>;

export * from './directory.thunks';
