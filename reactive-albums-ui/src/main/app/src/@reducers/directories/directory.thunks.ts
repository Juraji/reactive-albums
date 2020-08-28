import { createAsyncThunk } from '@reduxjs/toolkit';
import axios, { AxiosResponse } from 'axios';

interface RegisterDirectoryThunk {
  location: string;
  recursive: boolean;
}

export const registerDirectory = createAsyncThunk<AxiosResponse<void>, RegisterDirectoryThunk>(
  'directories/registerDirectory',
  (body) => axios.post('/api/directories', body)
);
