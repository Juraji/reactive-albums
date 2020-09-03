import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { Directory } from '@types';
import { deleteDirectories, upsertDirectories } from './directory.actions';
import { fetchAllDirectories } from './directory.thunks';

export const directoryEntityAdapter = createEntityAdapter<Directory>({
  selectId: (p) => p.id,
  sortComparer: (a, b) => a.location.localeCompare(b.location),
});

export const directoryStoreReducer = createReducer(directoryEntityAdapter.getInitialState(), (builder) => {
  builder.addCase(fetchAllDirectories.fulfilled, directoryEntityAdapter.addMany);
  builder.addCase(upsertDirectories, directoryEntityAdapter.upsertMany);
  builder.addCase(deleteDirectories, directoryEntityAdapter.removeMany);
});

export const {
  selectAll: selectAllDirectories,
  selectTotal: selectTotalDirectories,
  selectById: selectDirectoryById,
} = directoryEntityAdapter.getSelectors();
