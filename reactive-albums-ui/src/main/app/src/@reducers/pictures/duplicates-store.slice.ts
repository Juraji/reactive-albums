import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { DuplicateMatch } from '@types';
import { fetchAllDuplicateMatches } from './picture.thunks';
import { deleteEventDuplicateMatches, upsertEventDuplicateMatches } from './picture.actions';

export const duplicateMatchEntityAdapter = createEntityAdapter<DuplicateMatch>({
  selectId: (p) => p.id,
  sortComparer: (a, b) => a.similarity - b.similarity,
});

export const duplicateMatchStoreReducer = createReducer(duplicateMatchEntityAdapter.getInitialState(), (builder) => {
  builder.addCase(fetchAllDuplicateMatches.fulfilled, duplicateMatchEntityAdapter.addMany);
  builder.addCase(upsertEventDuplicateMatches, duplicateMatchEntityAdapter.upsertMany);
  builder.addCase(deleteEventDuplicateMatches, duplicateMatchEntityAdapter.removeMany);
});

export const {
  selectAll: selectAllDuplicateMatches,
  selectTotal: selectTotalDuplicateMatches,
  selectById: selectDuplicateMatchById,
} = duplicateMatchEntityAdapter.getSelectors();
