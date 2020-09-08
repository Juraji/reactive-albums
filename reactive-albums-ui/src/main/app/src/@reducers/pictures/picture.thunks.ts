import { createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { DuplicateMatch, Picture } from '@types';
import { unwrapApiResponse } from '@utils';

export const fetchAllPictures = createAsyncThunk<Picture[]>('pictures/fetchAllPictures', () =>
  axios.get('/api/pictures').then(unwrapApiResponse)
);

export const fetchAllDuplicateMatches = createAsyncThunk<DuplicateMatch[]>('pictures/fetchAllDuplicateMatches', () =>
  axios.get('/api/duplicate-matches').then(unwrapApiResponse)
);

interface AddPictureThunk {
  location: string;
}

export const addPicture = createAsyncThunk<Picture, AddPictureThunk>('pictures/addPicture', (body) =>
  axios.post('/api/pictures', body).then(unwrapApiResponse)
);

interface RescanDuplicatesThunk {
  pictureId: string;
}

export const rescanDuplicates = createAsyncThunk<unknown, RescanDuplicatesThunk>(
  'pictures/rescanDuplicates',
  ({ pictureId }) => axios.post(`/api/pictures/${pictureId}/rescan-duplicates`).then(unwrapApiResponse)
);

interface UnlinkDuplicateMatchThunk {
  pictureId: string;
  matchId: string;
}

export const unlinkDuplicateMatch = createAsyncThunk<unknown, UnlinkDuplicateMatchThunk>(
  'pictures/unlinkDuplicateMatch',
  ({ pictureId, matchId }) =>
    axios.post(`/api/pictures/${pictureId}/unlink-duplicate-match/${matchId}`).then(unwrapApiResponse)
);

interface DeletePictureThunk {
  pictureId: string;
}

export const deletePicture = createAsyncThunk<unknown, DeletePictureThunk>('pictures/deletePicture', ({ pictureId }) =>
  axios.delete(`/api/pictures/${pictureId}`).then(unwrapApiResponse)
);
