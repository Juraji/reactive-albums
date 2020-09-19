import { combineReducers } from 'redux';
import { directoryStoreSliceName, directoryStoreSliceReducer } from './directory-store.slice';

export const directoriesSliceName = 'directories';
export const directoriesSliceReducer = combineReducers({
  [directoryStoreSliceName]: directoryStoreSliceReducer,
});

export * from './hooks';
export * from './directory.thunks';
export * from './directory-store.slice';
