import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { Directory } from '@types';
import { upsertDirectories } from './directory.actions';
import { fetchAllDirectories } from './directory.thunks';

export const directoryEntityAdapter = createEntityAdapter<Directory>({
  selectId: (p) => p.id,
  sortComparer: (a, b) => a.location.localeCompare(b.location),
});

export const directoryStoreReducer = createReducer(directoryEntityAdapter.getInitialState(), (builder) => {
  builder.addCase(fetchAllDirectories.fulfilled, (state, action) => {
    return directoryEntityAdapter.addMany(state, action.payload);
  });
  builder.addCase(upsertDirectories, (state, action) => directoryEntityAdapter.upsertMany(state, action));
});

export const {
  selectAll: selectAllDirectories,
  selectTotal: selectTotalDirectories,
  selectById: selectDirectoryById,
} = directoryEntityAdapter.getSelectors();
