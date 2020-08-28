import { createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { DuplicateMatch, Picture } from '@types';

interface AddPictureThunk {
  location: string;
}

export const addPicture = createAsyncThunk<Picture, AddPictureThunk>('pictures/addPicture', (body) =>
  axios.post('/api/pictures', body).then((r) => r.data)
);

interface FetchPictureDuplicateMatchesThunk {
  pictureId: string;
}

export const fetchPictureDuplicateMatches = createAsyncThunk<DuplicateMatch[], FetchPictureDuplicateMatchesThunk>(
  'pictures/fetchPictureDuplicateMatches',
  ({ pictureId }) => axios.get(`/api/pictures/${pictureId}/duplicates`).then((r) => r.data)
);
