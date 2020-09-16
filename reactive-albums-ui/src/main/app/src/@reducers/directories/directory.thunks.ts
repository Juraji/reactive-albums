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
  directoryId: string;
  automaticScanEnabled?: boolean;
}

export const updateDirectory = createAsyncThunk<Directory, UpdateDirectoryThunk>(
  'directories/updateDirectory',
  ({ directoryId, automaticScanEnabled }) =>
    axios.put(`/api/directories/${directoryId}`, { automaticScanEnabled }).then(unwrapApiResponse)
);

interface UnregisterDirectoryThunk {
  directoryId: string;
  recursive: boolean;
}

export const unregisterDirectory = createAsyncThunk<Directory, UnregisterDirectoryThunk>(
  'directories/unregisterDirectory',
  ({ directoryId, recursive }) =>
    axios.delete(`/api/directories/${directoryId}`, { params: { recursive } }).then(unwrapApiResponse)
);
