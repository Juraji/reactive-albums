import { createAsyncThunk } from '@reduxjs/toolkit';
import axios, { AxiosResponse } from 'axios';
import { Picture } from '@types';

export const fetchAllPictures = createAsyncThunk<AxiosResponse<Picture[]>>('pictures/fetchAllPictures', () =>
  axios.get('/api/pictures')
);

interface AddPictureThunk {
  location: string;
}

export const addPicture = createAsyncThunk<AxiosResponse<Picture>, AddPictureThunk>('pictures/addPicture', (body) =>
    axios.post('/api/pictures', body)
);
