import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { Picture } from '@types';
import { upsertPictures } from './picture.actions';
import { fetchAllPictures } from './picture.thunks';

export const pictureEntityAdapter = createEntityAdapter<Picture>({
  selectId: (p) => p.id,
  sortComparer: (a, b) => a.displayName.localeCompare(b.displayName),
});

export const pictureStoreReducer = createReducer(pictureEntityAdapter.getInitialState(), (builder) => {
  builder.addCase(fetchAllPictures.fulfilled, (state, action) => {
    return pictureEntityAdapter.addMany(state, action.payload.data);
  });
  builder.addCase(upsertPictures, (state, action) => pictureEntityAdapter.upsertMany(state, action));
});

export const {
  selectAll: selectAllPictures,
  selectTotal: selectTotalPictures,
  selectById: selectPictureById,
} = pictureEntityAdapter.getSelectors();
