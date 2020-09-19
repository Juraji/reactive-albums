import { Picture } from '@types';
import { createApiDeleteThunk, createApiGetThunk, createApiPostThunk } from '@utils';

interface PictureThunk {
  pictureId: string;
}

export const activatePictureById = createApiGetThunk<Picture, PictureThunk>(
  'pictures/activatePicture',
  ({ pictureId }) => `/api/pictures/${pictureId}`
);

export const fetchPictureById = createApiGetThunk<Picture, PictureThunk>(
  'pictures/fetchPictureById',
  ({ pictureId }) => `/api/pictures/${pictureId}`
);

export const rescanDuplicates = createApiPostThunk<unknown, PictureThunk>(
  'pictures/rescanDuplicates',
  ({ pictureId }) => `/api/pictures/${pictureId}/rescan-duplicates`
);

interface UnlinkDuplicateMatchThunk extends PictureThunk {
  matchId: string;
}

export const unlinkDuplicateMatch = createApiDeleteThunk<unknown, UnlinkDuplicateMatchThunk>(
  'pictures/unlinkDuplicateMatch',
  ({ pictureId, matchId }) => `/api/pictures/${pictureId}/duplicate-matches/${matchId}`
);

interface DeletePictureThunk extends PictureThunk {
  deletePhysicalFile?: boolean;
}

export const deletePicture = createApiDeleteThunk<unknown, DeletePictureThunk>(
  'pictures/deletePicture',
  ({ pictureId, deletePhysicalFile }) => ({
    url: `/api/pictures/${pictureId}`,
    params: { deletePhysicalFile },
  })
);
