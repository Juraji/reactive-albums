import { createEntityAdapter, createReducer } from '@reduxjs/toolkit';
import { Picture } from '@types';
import { deleteEventPictures, upsertEventPictures } from './picture.actions';
import { fetchAllPictures } from './picture.thunks';

export const pictureEntityAdapter = createEntityAdapter<Picture>({
  selectId: (p) => p.id,
  sortComparer: (a, b) => a.displayName.localeCompare(b.displayName),
});

export const pictureStoreReducer = createReducer(pictureEntityAdapter.getInitialState(), (builder) => {
  builder.addCase(fetchAllPictures.fulfilled, pictureEntityAdapter.addMany);
  builder.addCase(upsertEventPictures, pictureEntityAdapter.upsertMany);
  builder.addCase(deleteEventPictures, pictureEntityAdapter.removeMany);
});

export const {
  selectAll: selectAllPictures,
  selectTotal: selectTotalPictures,
  selectById: selectPictureById,
} = pictureEntityAdapter.getSelectors();
