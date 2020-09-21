import { Page, Picture, TagLink } from '@types';
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

interface FetchPicturesPageThunk {
  page: number;
  size: number;
  filter?: string;
}

export const fetchPicturesPage = createApiGetThunk<Page<Picture>, FetchPicturesPageThunk>(
  'pictures/fetchPicturesPage',
  (params) => ({
    url: '/api/pictures',
    params,
  })
);

export const rescanDuplicates = createApiPostThunk<string, PictureThunk>(
  'pictures/rescanDuplicates',
  ({ pictureId }) => `/api/pictures/${pictureId}/rescan-duplicates`
);

interface UnlinkDuplicateMatchThunk extends PictureThunk {
  matchId: string;
}

export const unlinkDuplicateMatch = createApiDeleteThunk<string, UnlinkDuplicateMatchThunk>(
  'pictures/unlinkDuplicateMatch',
  ({ pictureId, matchId }) => `/api/pictures/${pictureId}/duplicate-matches/${matchId}`
);

interface PictureTagThunk extends PictureThunk {
  tagId: string;
}

export const linkPictureTag = createApiPostThunk<TagLink[], PictureTagThunk>(
  'pictures/linkPictureTag',
  ({ pictureId, tagId }) => `/api/pictures/${pictureId}/tags/${tagId}`
);

export const unlinkPictureTag = createApiDeleteThunk<TagLink[], PictureTagThunk>(
  'pictures/unlinkPictureTag',
  ({ pictureId, tagId }) => `/api/pictures/${pictureId}/tags/${tagId}`
);

interface MovePictureThunk extends PictureThunk {
  targetLocation: string;
}

export const movePicture = createApiPostThunk<Picture, MovePictureThunk>(
  'pictures/movePicture',
  ({ pictureId, targetLocation }) => ({
    url: `/api/pictures/${pictureId}/move`,
    params: { targetLocation },
  })
);

interface DeletePictureThunk extends PictureThunk {
  deletePhysicalFile?: boolean;
}

export const deletePicture = createApiDeleteThunk<string, DeletePictureThunk>(
  'pictures/deletePicture',
  ({ pictureId, deletePhysicalFile }) => ({
    url: `/api/pictures/${pictureId}`,
    params: { deletePhysicalFile },
  })
);
