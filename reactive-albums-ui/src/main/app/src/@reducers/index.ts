import { combineReducers } from 'redux';
import { configureStore } from '@reduxjs/toolkit';
import { picturesSliceName, picturesSliceReducer } from './pictures';
import { directoriesSliceName, directoriesSliceReducer } from './directories';

const rootReducers = combineReducers({
  [picturesSliceName]: picturesSliceReducer,
  [directoriesSliceName]: directoriesSliceReducer,
});

export const appStore = configureStore({
  reducer: rootReducers,
  devTools: true,
});

export type AppState = ReturnType<typeof appStore.getState>;

export * from './pictures';
export * from './directories';
