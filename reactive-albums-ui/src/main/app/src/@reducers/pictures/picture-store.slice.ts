import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { Picture } from '@types';
import { upsertPicture } from './picture.actions';

export const pictureEntityAdapter = createEntityAdapter<Picture>({
  selectId: (p) => p.id,
  sortComparer: (a, b) => a.displayName.localeCompare(b.displayName),
});

export const pictureStoreReducer = createReducer(pictureEntityAdapter.getInitialState(), (builder) => {
  builder.addCase(upsertPicture, (state, action) => pictureEntityAdapter.upsertOne(state, action));
});

export const {
  selectAll: selectAllPictures,
  selectTotal: selectTotalPictures,
  selectById: selectPictureById,
} = pictureEntityAdapter.getSelectors();
