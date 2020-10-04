import { combineReducers, createEntityAdapter, createReducer, EntityAdapter } from '@reduxjs/toolkit';
import { Tag, TagType } from '@types';
import { createTag, deleteTag, fetchAllTags, updateTag } from './tag.thunks';
import { AppState } from '../index';

const createTagReducer = (adapter: EntityAdapter<Tag>, tagType: TagType) =>
  createReducer(adapter.getInitialState(), (builder) => {
    builder.addCase(fetchAllTags.fulfilled, (state, action) => {
      const tags = action.payload.filter((t) => t.tagType === tagType);
      return tags.isNotEmpty() ? adapter.upsertMany(state, tags) : state;
    });
    builder.addCase(createTag.fulfilled, (state, action) => {
      return action.payload.tagType === tagType ? adapter.upsertOne(state, action) : state;
    });
    builder.addCase(updateTag.fulfilled, (state, action) => {
      return action.payload.tagType === tagType ? adapter.upsertOne(state, action) : state;
    });
    builder.addCase(deleteTag.fulfilled, adapter.removeOne);
  });

/**** Color tags ****/
const colorTagStoreAdapter = createEntityAdapter<Tag>({
  selectId: (e) => e.id,
  sortComparer: (a, b) => a.label.localeCompare(b.label),
});

const colorTagStoreReducer = createTagReducer(colorTagStoreAdapter, TagType.COLOR);

/**** Directory tags ****/
const directoryTagStoreAdapter = createEntityAdapter<Tag>({
  selectId: (e) => e.id,
  sortComparer: (a, b) => a.label.localeCompare(b.label),
});

const directoryTagStoreReducer = createTagReducer(colorTagStoreAdapter, TagType.DIRECTORY);

/**** User tags ****/
const userTagStoreAdapter = createEntityAdapter<Tag>({
  selectId: (e) => e.id,
  sortComparer: (a, b) => a.label.localeCompare(b.label),
});

const userTagStoreReducer = createTagReducer(colorTagStoreAdapter, TagType.USER);

export const tagStoreSliceName = 'tagStore';
export const tagStoreSlice = combineReducers({
  colors: colorTagStoreReducer,
  directories: directoryTagStoreReducer,
  userDefined: userTagStoreReducer,
});

const colorStoreSelectors = colorTagStoreAdapter.getSelectors<AppState>((s) => s.tags.tagStore.colors);
const directoryStoreSelectors = directoryTagStoreAdapter.getSelectors<AppState>((s) => s.tags.tagStore.directories);
const userStoreSelectors = userTagStoreAdapter.getSelectors<AppState>((s) => s.tags.tagStore.userDefined);
export const { selectAll: selectAllColorTags } = colorStoreSelectors;
export const { selectAll: selectAllDirectoryTags } = directoryStoreSelectors;
export const { selectAll: selectAllUserTags } = userStoreSelectors;
