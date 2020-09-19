import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { Directory } from '@types';
import { fetchAllDirectories, registerDirectory, unregisterDirectory, updateDirectory } from './directory.thunks';
import { AppState } from '../index';

export const directoryStoreAdapter = createEntityAdapter<Directory>({
  selectId: (e) => e.id,
  sortComparer: (a, b) => a.location.localeCompare(b.location),
});

export const directoryStoreSliceName = 'directoryStore';
export const directoryStoreSliceReducer = createReducer(directoryStoreAdapter.getInitialState(), (builder) => {
  builder.addCase(fetchAllDirectories.fulfilled, directoryStoreAdapter.upsertMany);
  builder.addCase(registerDirectory.fulfilled, directoryStoreAdapter.upsertMany);
  builder.addCase(updateDirectory.fulfilled, directoryStoreAdapter.upsertOne);
  builder.addCase(unregisterDirectory.fulfilled, directoryStoreAdapter.removeMany);
});

const directoryStoreSelectors = directoryStoreAdapter.getSelectors<AppState>((s) => s.directories.directoryStore);
export const { selectAll: selectAllDirectories } = directoryStoreSelectors;
