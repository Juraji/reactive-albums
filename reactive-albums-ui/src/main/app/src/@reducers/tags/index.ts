import { combineReducers } from 'redux';
import { tagStoreSlice, tagStoreSliceName } from './tag-store.slice';

export const tagsSliceName = 'tags';
export const tagsSliceReducer = combineReducers({
  [tagStoreSliceName]: tagStoreSlice,
});

export * from './hooks';
export * from './tag.thunks';
export * from './tag-store.slice';
