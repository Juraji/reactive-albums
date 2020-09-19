import { Directory } from '@types';
import { createApiDeleteThunk, createApiGetThunk, createApiPostThunk, createApiPutThunk } from '@utils';

export const fetchAllDirectories = createApiGetThunk<Directory[]>(
  'directories/fetchAllDirectories',
  () => '/api/directories'
);

interface RegisterDirectoryThunk {
  location: string;
  recursive: boolean;
}

export const registerDirectory = createApiPostThunk<Directory[], RegisterDirectoryThunk>(
  'directories/registerDirectory',
  (p) => ({ url: '/api/directories', data: p })
);

interface UpdateDirectoryThunk {
  directoryId: string;
  automaticScanEnabled?: boolean;
}

export const updateDirectory = createApiPutThunk<Directory, UpdateDirectoryThunk>(
  'directories/updateDirectory',
  ({ directoryId, automaticScanEnabled }) => ({
    url: `/api/directories/${directoryId}`,
    data: { automaticScanEnabled },
  })
);

interface UnregisterDirectoryThunk {
  directoryId: string;
  recursive: boolean;
}

export const unregisterDirectory = createApiDeleteThunk<Directory, UnregisterDirectoryThunk>(
  'directories/unregisterDirectory',
  ({ directoryId, recursive }) => ({
    url: `/api/directories/${directoryId}`,
    params: { recursive },
  })
);
