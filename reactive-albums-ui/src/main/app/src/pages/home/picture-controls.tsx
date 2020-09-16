import React, { FC, useEffect, useMemo, useState } from 'react';
import Navbar from 'react-bootstrap/Navbar';
import { Conditional } from '@components';
import Form from 'react-bootstrap/Form';
import { PAGINATION_SIZE_OPTIONS } from '../../config.json';
import { useTranslation } from 'react-i18next';
import Pagination from 'react-bootstrap/Pagination';
import { useDebouncedValue } from '@hooks';
import { Page, Picture } from '@types';

interface PictureSearchFieldProps {
  onChange: (value: string | undefined) => void;
}

const PictureSearchField: FC<PictureSearchFieldProps> = ({ onChange }) => {
  const { t } = useTranslation();
  const [input, setInput] = useState<string>('');
  const debouncedInput = useDebouncedValue(input, 500);

  useEffect(() => {
    if (!debouncedInput || debouncedInput.length === 0) {
      onChange(undefined);
    } else {
      onChange(debouncedInput);
    }
  }, [debouncedInput, onChange]);

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
  pageResult: Page<Picture>;
  onPageNumberChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
  onFilterChange: (value: string | undefined) => void;
}

export const PictureControls: FC<PictureControlsProps> = ({
  pageResult,
  onPageNumberChange,
  onPageSizeChange,
  onFilterChange,
}) => {
  const { t } = useTranslation();
  const { empty, first, last, number, size, totalElements, totalPages } = pageResult;

  const sizeOpts = useMemo(
    () =>
      PAGINATION_SIZE_OPTIONS.map((option, i) => (
        <option key={i} value={option}>
          {t('home.pagination.page_size_label', { option, totalElements })}
        </option>
      )),
    [totalElements, t]
  );

  return (
    <Navbar variant="light" fixed="bottom" bg="light">
      <Form inline className="mr-2">
        <PictureSearchField onChange={onFilterChange} />
      </Form>
      <Conditional condition={!empty} orElse={<Navbar.Text>{t('home.pagination.no_items_found')}</Navbar.Text>}>
        <Form inline>
          <Form.Control
            as="select"
            className="mr-2"
            size="sm"
            value={size}
            onChange={(e) => onPageSizeChange(+e.target.value)}
          >
            {sizeOpts}
          </Form.Control>
          <Pagination className="mb-0 mr-2">
            <Pagination.First disabled={first} onClick={() => onPageNumberChange(0)} />
            <Pagination.Prev disabled={first} onClick={() => onPageNumberChange(number - 1)} />
            <Pagination.Item disabled>
              {t('home.pagination.current_page_label', { currentPage: number + 1, totalPages })}
            </Pagination.Item>
            <Pagination.Next disabled={last} onClick={() => onPageNumberChange(number + 1)} />
            <Pagination.Last disabled={last} onClick={() => onPageNumberChange(totalPages - 1)} />
          </Pagination>
        </Form>
      </Conditional>
    </Navbar>
  );
};
