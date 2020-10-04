import { createReducer } from '@reduxjs/toolkit';
import { fetchPicturesPage } from './picture.thunks';
import { Picture } from '@types';
import { setPictureOverviewFilter } from './picture.actions';

export interface PictureOverviewSliceState {
  content: Picture[];
  isEmpty: boolean;
  isFirst: boolean;
  isLast: boolean;
  pageNumber: number;
  itemsOnPage: number;
  requestedPageSize: number;
  totalItemsAvailable: number;
  totalPagesAvailable: number;
  filter?: string;
}

const initialState: PictureOverviewSliceState = {
  content: [],
  isEmpty: true,
  isFirst: true,
  isLast: true,
  pageNumber: 0,
  itemsOnPage: 0,
  requestedPageSize: 50,
  totalItemsAvailable: 0,
  totalPagesAvailable: 0,
};

export const pictureOverviewSliceName = 'pictureOverview';
export const pictureOverviewSliceReducer = createReducer(initialState, (builder) => {
  builder.addCase(fetchPicturesPage.fulfilled, (state, action) => {
    return state.copy({
      content: action.payload.content,
      isEmpty: action.payload.empty,
      isFirst: action.payload.first,
      isLast: action.payload.last,
      pageNumber: action.payload.number,
      itemsOnPage: action.payload.numberOfElements,
      requestedPageSize: action.payload.size,
      totalItemsAvailable: action.payload.totalElements,
      totalPagesAvailable: action.payload.totalPages,
      filter: action.meta.arg.filter,
    });
  });

  builder.addCase(setPictureOverviewFilter, (state, action) => state.copy({ filter: action.payload }));
});
