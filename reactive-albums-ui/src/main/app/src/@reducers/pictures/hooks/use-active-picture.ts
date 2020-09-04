import { useDispatch, useSelector } from '@hooks';
import { ActivePictureSliceState } from '../active-picture.slice';
import { useEffect } from 'react';
import { usePicturesStore } from './use-pictures-store';
import { selectPictureById } from '../picture-store.slice';
import { activatePicture, deactivatePicture } from '../picture.actions';

export function useActivePicture(pictureId?: string, deactivateOnUnmount = false): ActivePictureSliceState {
  const picturesStore = usePicturesStore();
  const activePicture = useSelector((state) => state.pictures.activePicture);
  const dispatch = useDispatch();

  useEffect(() => {
    if (!!pictureId && (!activePicture || activePicture.picture?.id !== pictureId)) {
      const picture = selectPictureById(picturesStore, pictureId);

      if (!!picture) {
        dispatch(activatePicture(picture));
      }
    }
  }, [pictureId, activePicture, picturesStore, dispatch]);

  useEffect(() => {
    if (deactivateOnUnmount) {
      return () => {
        dispatch(deactivatePicture());
      };
    }
  }, [dispatch, deactivateOnUnmount]);

  return activePicture;
}
