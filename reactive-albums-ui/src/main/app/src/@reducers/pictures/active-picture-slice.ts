import { createReducer } from '@reduxjs/toolkit';
import { DuplicateMatchView, Picture } from '@types';
import { activatePictureById } from './picture.thunks';
import {
  addActivePictureDuplicateMatch,
  deactivatePicture,
  removeActivePictureDuplicateMatch,
} from './picture.actions';

export interface ActivePictureState {
  picture?: Picture;
  duplicates: DuplicateMatchView[];
}

const initialState: ActivePictureState = {
  duplicates: [],
};

export const activePictureSliceName = 'activePicture';
export const activePictureSliceReducer = createReducer(initialState, (builder) => {
  builder.addCase(activatePictureById.fulfilled, (state, { payload }) => state.copy({ picture: payload }));

  builder.addCase(addActivePictureDuplicateMatch, (state, action) => {
    return state.copy({ duplicates: [...state.duplicates, action.payload] });
  });

  builder.addCase(removeActivePictureDuplicateMatch, (state, action) => {
    return state.copy({ duplicates: state.duplicates?.filter((dm) => dm.id !== action.payload.id) });
  });

  builder.addCase(deactivatePicture, (state) =>
    state.copy({
      picture: undefined,
      duplicates: [],
    })
  );
});
