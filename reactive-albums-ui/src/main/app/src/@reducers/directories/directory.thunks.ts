import { createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { unwrapApiResponse } from '@utils';
import { Directory } from '@types';

export const fetchAllDirectories = createAsyncThunk<Directory[]>('directories/fetchAllDirectories', () =>
  axios.get('/api/directories').then(unwrapApiResponse)
);

interface RegisterDirectoryThunk {
  location: string;
  recursive: boolean;
}

export const registerDirectory = createAsyncThunk<Directory[], RegisterDirectoryThunk>(
  'directories/registerDirectory',
  (body) => axios.post('/api/directories', body).then(unwrapApiResponse)
);

interface UpdateDirectoryThunk {
  directory: Directory;
}

export const updateDirectory = createAsyncThunk<Directory, UpdateDirectoryThunk>(
  'directories/updateDirectory',
  ({ directory }) => axios.put(`/api/directories/${directory.id}`, directory).then(unwrapApiResponse)
);

interface UnregisterDirectoryThunk {
  directoryId: string;
}

export const unregisterDirectory = createAsyncThunk<Directory, UnregisterDirectoryThunk>(
  'directories/unregisterDirectory',
  ({ directoryId }) => axios.delete(`/api/directories/${directoryId}`).then(unwrapApiResponse)
);
