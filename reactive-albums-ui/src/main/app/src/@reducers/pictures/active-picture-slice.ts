import { combineReducers, createEntityAdapter, createReducer, EntityState } from '@reduxjs/toolkit';
import { DuplicateMatch, Picture } from '@types';
import { activatePictureById, deletePicture, linkPictureTag, movePicture, unlinkPictureTag } from './picture.thunks';
import {
  addActivePictureDuplicateMatch,
  deactivatePicture,
  removeActivePictureDuplicateMatch,
} from './picture.actions';
import { AppState } from '../index';

export interface ActivePictureState {
  picture?: Picture;
  duplicates: EntityState<DuplicateMatch>;
}

const duplicatesEntityAdapter = createEntityAdapter<DuplicateMatch>({
  selectId: (e) => e.id,
  sortComparer: (a, b) => a.similarity - b.similarity,
});

const activeDuplicateMatchesReducer = createReducer(duplicatesEntityAdapter.getInitialState(), (builder) => {
  builder.addCase(addActivePictureDuplicateMatch, duplicatesEntityAdapter.upsertOne);
  builder.addCase(removeActivePictureDuplicateMatch, duplicatesEntityAdapter.removeOne);
  builder.addCase(deactivatePicture, duplicatesEntityAdapter.removeAll);
});

const activePictureReducer = createReducer<Picture | null>(null, (builder) => {
  builder.addCase(activatePictureById.fulfilled, (_, { payload }) => payload);
  builder.addCase(deactivatePicture, () => null);
  builder.addCase(movePicture.fulfilled, (state, { payload }) => payload);
  builder.addCase(deletePicture.fulfilled, () => null);
  builder.addCase(linkPictureTag.fulfilled, (state, action) => {
    return state?.copy({ tags: action.payload });
  });
  builder.addCase(unlinkPictureTag.fulfilled, (state, action) => {
    return state?.copy({ tags: action.payload });
  });
});

export const activePictureSliceName = 'activePicture';
export const activePictureSliceReducer = combineReducers({
  picture: activePictureReducer,
  duplicates: activeDuplicateMatchesReducer,
});

const duplicateMatchSelectors = duplicatesEntityAdapter.getSelectors<AppState>(
  (state) => state.pictures.activePicture.duplicates
);
export const { selectAll: selectActiveDuplicateMatches } = duplicateMatchSelectors;
