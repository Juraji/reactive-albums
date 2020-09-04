import React, { FC, useEffect, useMemo, useState } from 'react';
import Navbar from 'react-bootstrap/Navbar';
import { Conditional } from '@components';
import Form from 'react-bootstrap/Form';
import { PAGINATION_SIZE_OPTIONS } from '../../config.json';
import { useTranslation } from 'react-i18next';
import Pagination from 'react-bootstrap/Pagination';
import { useDebouncedValue } from '@hooks';
import { PicturesPageResult } from '@reducers';

interface PictureSearchFieldProps {
  onChange: (value: string) => void;
  value: string;
}

const PictureSearchField: FC<PictureSearchFieldProps> = ({ value, onChange }) => {
  const { t } = useTranslation();
  const [input, setInput] = useState(value);
  const debouncedInput = useDebouncedValue(input, 500);

  useEffect(() => onChange(debouncedInput), [debouncedInput, onChange]);

  return (
    <Form.Control
      placeholder={t('home.pagination.filter.placeholder')}
      value={input}
      onChange={(e) => setInput(e.target.value)}
      size="sm"
    />
  );
};

interface PictureControlsProps {
  pageResult: PicturesPageResult;
}

export const PictureControls: FC<PictureControlsProps> = ({ pageResult }) => {
  const { t } = useTranslation();
  const { page, setFilter, setSize, setPage } = pageResult;
  const { totalItems, size, currentPage, first, last, totalPages, filter } = page;

  const sizeOpts = useMemo(
    () =>
      PAGINATION_SIZE_OPTIONS.map((option, i) => (
        <option key={i} value={option}>
          {t('home.pagination.page_size_label', { option, totalItems })}
        </option>
      )),
    [totalItems, t]
  );

  return (
    <Navbar variant="light" fixed="bottom" bg="light">
      <Form inline className="mr-2">
        <PictureSearchField onChange={setFilter} value={filter} />
      </Form>
      <Conditional condition={totalItems > 0} orElse={<Navbar.Text>{t('home.pagination.no_items_found')}</Navbar.Text>}>
        <Form inline>
          <Form.Control as="select" className="mr-2" size="sm" value={size} onChange={(e) => setSize(+e.target.value)}>
            {sizeOpts}
          </Form.Control>
          <Pagination className="mb-0 mr-2">
            <Pagination.First disabled={first} onClick={() => setPage(0)} />
            <Pagination.Prev disabled={first} onClick={() => setPage(currentPage - 1)} />
            <Pagination.Item disabled>
              {t('home.pagination.current_page_label', { currentPage: currentPage + 1, totalPages })}
            </Pagination.Item>
            <Pagination.Next disabled={last} onClick={() => setPage(currentPage + 1)} />
            <Pagination.Last disabled={last} onClick={() => setPage(totalPages - 1)} />
          </Pagination>
        </Form>
      </Conditional>
    </Navbar>
  );
};
