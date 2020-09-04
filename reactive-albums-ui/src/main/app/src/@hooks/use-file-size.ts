import { useMemo } from 'react';
import fileSize from 'filesize';

export function useFileSize(bytes: number | undefined): string {
  return useMemo(() => {
    if (bytes !== undefined) {
      return fileSize(bytes);
    } else {
      return '';
    }
  }, [bytes]);
}
