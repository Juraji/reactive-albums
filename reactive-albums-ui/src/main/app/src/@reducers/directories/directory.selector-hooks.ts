import { Directory } from '@types';
import { EntityState } from '@reduxjs/toolkit';
import { useSelector } from '@hooks';
import { selectAllDirectories } from './directory-store.slice';

export function useDirectoriesStoreState(): EntityState<Directory> {
  return useSelector((state) => state.directories.directoryStore);
}

export function useAllDirectories(): Directory[] {
  const directoryStore = useDirectoriesStoreState();

  return selectAllDirectories(directoryStore);
}
