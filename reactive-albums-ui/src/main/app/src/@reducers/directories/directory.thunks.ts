import { createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { unwrapApiResponse } from '@utils';
import { Directory } from '../../@types/Directory.domain';

interface RegisterDirectoryThunk {
  location: string;
  recursive: boolean;
}

export const registerDirectory = createAsyncThunk<Directory[], RegisterDirectoryThunk>(
  'directories/registerDirectory',
  (body) => axios.post('/api/directories', body).then(unwrapApiResponse)
);
