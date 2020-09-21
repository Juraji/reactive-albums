import { combineReducers } from 'redux';
import { configureStore } from '@reduxjs/toolkit';
import { picturesSliceName, picturesSliceReducer } from './pictures';
import { directoriesSliceName, directoriesSliceReducer } from './directories';
import { tagsSliceName, tagsSliceReducer } from './tags';
import { auditLogSliceName, auditLogSliceReducer } from './audit-log';

const rootReducers = combineReducers({
  [picturesSliceName]: picturesSliceReducer,
  [directoriesSliceName]: directoriesSliceReducer,
  [tagsSliceName]: tagsSliceReducer,
  [auditLogSliceName]: auditLogSliceReducer,
});

export const appStore = configureStore({
  reducer: rootReducers,
  devTools: true,
});

export type AppState = ReturnType<typeof appStore.getState>;

export * from './pictures';
export * from './directories';
export * from './tags';
export * from './audit-log';
