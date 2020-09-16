import { createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { Picture } from '@types';
import { unwrapApiResponse } from '@utils';

interface PictureThunk {
  pictureId: string;
}

export const activatePictureById = createAsyncThunk<Picture, PictureThunk>(
  'pictures/activatePicture',
  ({ pictureId }) => axios.get(`/api/pictures/${pictureId}`).then(unwrapApiResponse)
);

export const fetchPictureById = createAsyncThunk<Picture, PictureThunk>('pictures/fetchPictureById', ({ pictureId }) =>
  axios.get(`/api/pictures/${pictureId}`).then(unwrapApiResponse)
);

export const rescanDuplicates = createAsyncThunk<unknown, PictureThunk>('pictures/rescanDuplicates', ({ pictureId }) =>
  axios.post(`/api/pictures/${pictureId}/rescan-duplicates`).then(unwrapApiResponse)
);

interface UnlinkDuplicateMatchThunk extends PictureThunk {
  matchId: string;
}

export const unlinkDuplicateMatch = createAsyncThunk<unknown, UnlinkDuplicateMatchThunk>(
  'pictures/unlinkDuplicateMatch',
  ({ pictureId, matchId }) =>
    axios.post(`/api/pictures/${pictureId}/unlink-duplicate-match/${matchId}`).then(unwrapApiResponse)
);

export const deletePicture = createAsyncThunk<unknown, PictureThunk>('pictures/deletePicture', ({ pictureId }) =>
  axios.delete(`/api/pictures/${pictureId}`).then(unwrapApiResponse)
);
