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
