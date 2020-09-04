import { createReducer } from '@reduxjs/toolkit';
import { Picture } from '@types';
import { activatePicture, deactivatePicture } from './picture.actions';

export interface ActivePictureSliceState {
  picture?: Picture;
}

const initial: ActivePictureSliceState = {
  picture: undefined,
};

export const activePictureReducer = createReducer(initial, (builder) => {
  builder.addCase(activatePicture, (state, action) => state.copy({ picture: action.payload }));
  builder.addCase(deactivatePicture, (state) => state.copy({ picture: undefined }));
});
