import { combineReducers } from 'redux';
import { configureStore } from '@reduxjs/toolkit';
import { picturesSliceName, picturesSliceReducer } from './pictures';
import { tagsSliceName, tagsSliceReducer } from './tags';

const rootReducers = combineReducers({
  [picturesSliceName]: picturesSliceReducer,
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
