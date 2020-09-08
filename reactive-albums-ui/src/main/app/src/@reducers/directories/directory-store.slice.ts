import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { Directory } from '@types';
import { deleteEventDirectories, upsertEventDirectories } from './directory.actions';
import { fetchAllDirectories } from './directory.thunks';

export const directoryEntityAdapter = createEntityAdapter<Directory>({
  selectId: (p) => p.id,
  sortComparer: (a, b) => a.location.localeCompare(b.location),
});

export const directoryStoreReducer = createReducer(directoryEntityAdapter.getInitialState(), (builder) => {
  builder.addCase(fetchAllDirectories.fulfilled, directoryEntityAdapter.addMany);
  builder.addCase(upsertEventDirectories, directoryEntityAdapter.upsertMany);
  builder.addCase(deleteEventDirectories, directoryEntityAdapter.removeMany);
});

export const {
  selectAll: selectAllDirectories,
  selectTotal: selectTotalDirectories,
  selectById: selectDirectoryById,
} = directoryEntityAdapter.getSelectors();
