import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { Tag } from '@types';
import { createTag, deleteTag, fetchAllTags, updateTag } from './tag.thunks';
import { AppState } from '../index';

export const tagStoreAdapter = createEntityAdapter<Tag>({
  selectId: (e) => e.id,
  sortComparer: (a, b) => a.label.localeCompare(b.label),
});

export const tagStoreSliceName = 'tagStore';
export const tagStoreSlice = createReducer(tagStoreAdapter.getInitialState(), (builder) => {
  builder.addCase(fetchAllTags.fulfilled, tagStoreAdapter.upsertMany);
  builder.addCase(createTag.fulfilled, tagStoreAdapter.upsertOne);
  builder.addCase(updateTag.fulfilled, tagStoreAdapter.upsertOne);
  builder.addCase(deleteTag.fulfilled, tagStoreAdapter.removeOne);
});

const tagStoreSelectors = tagStoreAdapter.getSelectors<AppState>((s) => s.tags.tagStore);
export const { selectAll: selectAllTags } = tagStoreSelectors;
