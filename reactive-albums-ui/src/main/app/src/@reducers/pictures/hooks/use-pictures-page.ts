import { Page, Picture } from '@types';
import { useEffect, useMemo, useState } from 'react';
import { useApiUrl } from '@hooks';
import axios from 'axios';
import { unwrapApiResponse } from '@utils';
import { useToasts } from 'react-toast-notifications';
import { useTranslation } from 'react-i18next';

export function usePicturesPage(page: number, size: number, filterValue: string | undefined): Page<Picture> {
  const { addToast } = useToasts();
  const { t } = useTranslation();
  const baseUrl = useApiUrl('pictures');
  const requestUrl = useMemo(() => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });

    if (!!filterValue) {
      params.append('filter', filterValue);
    }

    return `${baseUrl}?${params.toString()}`;
  }, [baseUrl, page, size, filterValue]);

  const [currentPage, setCurrentPage] = useState<Page<Picture>>({
    content: [],
    empty: true,
    first: false,
    last: false,
    number: 0,
    numberOfElements: 0,
    size: 0,
    totalElements: 0,
    totalPages: 0,
  });

  useEffect(() => {
    if (!!requestUrl) {
      axios
        .get(requestUrl)
        .then(unwrapApiResponse)
        .then(setCurrentPage)
        .catch((e) => addToast(t('home.use-pictures-page.fetch-failed', e)));
    }
  }, [requestUrl, setCurrentPage, addToast, t]);

  return currentPage;
}
