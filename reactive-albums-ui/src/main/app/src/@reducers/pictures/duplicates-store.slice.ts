import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { DuplicateMatch } from '@types';
import { fetchAllDuplicateMatches } from './picture.thunks';
import { deleteDuplicateMatches, upsertDuplicateMatches } from './picture.actions';

export const duplicateMatchEntityAdapter = createEntityAdapter<DuplicateMatch>({
  selectId: (p) => p.id,
  sortComparer: (a, b) => a.similarity - b.similarity,
});

export const duplicateMatchStoreReducer = createReducer(duplicateMatchEntityAdapter.getInitialState(), (builder) => {
  builder.addCase(fetchAllDuplicateMatches.fulfilled, duplicateMatchEntityAdapter.addMany);
  builder.addCase(upsertDuplicateMatches, duplicateMatchEntityAdapter.upsertMany);
  builder.addCase(deleteDuplicateMatches, duplicateMatchEntityAdapter.removeMany);
});

export const {
  selectAll: selectAllDuplicateMatches,
  selectTotal: selectTotalDuplicateMatches,
  selectById: selectDuplicateMatchById,
} = duplicateMatchEntityAdapter.getSelectors();
