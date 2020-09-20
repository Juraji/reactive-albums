import React, { FC, useEffect, useMemo, useState } from 'react';
import Navbar from 'react-bootstrap/Navbar';
import { Conditional } from '@components';
import Form from 'react-bootstrap/Form';
import { PAGINATION_SIZE_OPTIONS } from '../../config.json';
import { useTranslation } from 'react-i18next';
import Pagination from 'react-bootstrap/Pagination';
import { useDebouncedValue, useDispatch } from '@hooks';
import { fetchPicturesPage, usePicturesOverview } from '@reducers';

interface PictureControlsProps {}

export const PictureControls: FC<PictureControlsProps> = () => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const {
    isEmpty,
    isFirst,
    isLast,
    pageNumber,
    itemsOnPage,
    requestedPageSize,
    totalItemsAvailable,
    totalPagesAvailable,
    filter,
  } = usePicturesOverview();

  const [pageSizeInput, setPageSizeInput] = useState(requestedPageSize);
  const [pageNoInput, setPageNoInput] = useState(pageNumber);
  const [filterInput, setFilterInput] = useState<string>(filter || '');
  const debouncedFilterInput = useDebouncedValue(filterInput, 500);

  const sizeOpts = useMemo(
    () =>
      PAGINATION_SIZE_OPTIONS.map((option, i) => (
        <option key={i} value={option}>
          {t('home.pagination.page_size_label', { option, totalItemsAvailable })}
        </option>
      )),
    [totalItemsAvailable, t]
  );

  useEffect(() => {
    dispatch(fetchPicturesPage({ filter: debouncedFilterInput, page: pageNoInput, size: pageSizeInput }));
  }, [dispatch, debouncedFilterInput, pageNoInput, pageSizeInput]);

  return (
    <Navbar variant="light" fixed="bottom" bg="light" className="border-top">
      <Form inline className="mr-2">
        <Form.Control
          placeholder={t('home.pagination.filter.placeholder')}
          value={filterInput}
          onChange={(e) => setFilterInput(e.target.value)}
          size="sm"
        />
      </Form>
      <Conditional condition={!isEmpty} orElse={<Navbar.Text>{t('home.pagination.no_items_found')}</Navbar.Text>}>
        <Form inline>
          <Form.Control
            as="select"
            className="mr-2"
            size="sm"
            value={pageSizeInput}
            onChange={(e) => setPageSizeInput(+e.target.value)}
          >
            {sizeOpts}
          </Form.Control>
          <Pagination className="mb-0 mr-2">
            <Pagination.First disabled={isFirst} onClick={() => setPageNoInput(0)} />
            <Pagination.Prev disabled={isFirst} onClick={() => setPageNoInput(pageNumber - 1)} />
            <Pagination.Item disabled>
              {t('home.pagination.current_page_label', {
                currentPage: pageNumber + 1,
                totalPagesAvailable,
                itemsOnPage,
              })}
            </Pagination.Item>
            <Pagination.Next disabled={isLast} onClick={() => setPageNoInput(pageNumber + 1)} />
            <Pagination.Last disabled={isLast} onClick={() => setPageNoInput(totalPagesAvailable - 1)} />
          </Pagination>
        </Form>
      </Conditional>
    </Navbar>
  );
};
