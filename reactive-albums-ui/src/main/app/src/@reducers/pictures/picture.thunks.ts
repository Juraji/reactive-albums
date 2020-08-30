import { createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { Picture } from '@types';
import { unwrapApiResponse } from '@utils';

export const fetchAllPictures = createAsyncThunk<Picture[]>('pictures/fetchAllPictures', () =>
  axios.get('/api/pictures').then(unwrapApiResponse)
);

interface AddPictureThunk {
  location: string;
}

export const addPicture = createAsyncThunk<Picture, AddPictureThunk>('pictures/addPicture', (body) =>
  axios.post('/api/pictures', body).then(unwrapApiResponse)
);
