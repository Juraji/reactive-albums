import { combineReducers } from 'redux';
import { configureStore } from '@reduxjs/toolkit';
import { picturesSliceName, picturesSliceReducer } from './pictures';
import { directoriesSliceName, directoriesSliceReducer } from './directories';
import { tagsSliceName, tagsSliceReducer } from './tags';

const rootReducers = combineReducers({
  [picturesSliceName]: picturesSliceReducer,
  [directoriesSliceName]: directoriesSliceReducer,
  [tagsSliceName]: tagsSliceReducer,
});

export const appStore = configureStore({
  reducer: rootReducers,
  devTools: true,
});

export type AppState = ReturnType<typeof appStore.getState>;

export * from './pictures';
export * from './directories';
export * from './tags';
