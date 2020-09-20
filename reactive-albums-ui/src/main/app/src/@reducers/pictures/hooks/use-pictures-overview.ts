import { useSelector } from '@hooks';
import { PictureOverviewSliceState } from '../picture-overview.slice';

export function usePicturesOverview(): PictureOverviewSliceState {
  return useSelector((state) => state.pictures.pictureOverview);
}
