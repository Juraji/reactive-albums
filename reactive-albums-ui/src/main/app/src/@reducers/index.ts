import { combineReducers } from 'redux';
import { configureStore } from '@reduxjs/toolkit';
import { picturesSliceName, picturesSliceReducer } from './pictures';

const rootReducers = combineReducers({
  [picturesSliceName]: picturesSliceReducer,
});

export const appStore = configureStore({
  reducer: rootReducers,
  devTools: true,
});

export type AppState = ReturnType<typeof appStore.getState>;

export * from './pictures';
export * from './directories';
