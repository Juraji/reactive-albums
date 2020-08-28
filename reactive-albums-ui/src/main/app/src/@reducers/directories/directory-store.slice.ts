import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { Directory } from '../../@types/Directory.domain';

export const directoryEntityAdapter = createEntityAdapter<Directory>({
  selectId: (p) => p.id,
  sortComparer: (a, b) => a.location.localeCompare(b.location),
});

export const directoryStoreReducer = createReducer(directoryEntityAdapter.getInitialState(), (builder) => {});

export const {
  selectAll: selectAllDirectories,
  selectTotal: selectTotalDirectories,
  selectById: selectDirectoryById,
} = directoryEntityAdapter.getSelectors();
