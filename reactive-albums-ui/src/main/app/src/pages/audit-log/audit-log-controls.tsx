import React, { ChangeEvent, FC, useEffect, useMemo, useState } from 'react';
import Navbar from 'react-bootstrap/Navbar';
import { Conditional } from '@components';
import Form from 'react-bootstrap/Form';
import { PAGINATION_SIZE_OPTIONS } from '../../config.json';
import { useTranslation } from 'react-i18next';
import Pagination from 'react-bootstrap/Pagination';
import { useDispatch, useQueryParams } from '@hooks';
import { fetchAuditLogPage, useAuditLogEntries } from '@reducers';
import InputGroup from 'react-bootstrap/InputGroup';
import Button from 'react-bootstrap/Button';
import { ChevronDown, ChevronUp, RotateCcw } from 'react-feather';
import {
  clearAuditLogAggregateIdFilter,
  setAuditLogAggregateIdFilter,
} from '../../@reducers/audit-log/audit-log.actions';

interface AuditLogControlsProps {}

// noinspection DuplicatedCode
export const AuditLogControls: FC<AuditLogControlsProps> = () => {
  const queryParams = useQueryParams();
  const dispatch = useDispatch();
  const { t } = useTranslation();
  const {
    isEmpty,
    isFirst,
    isLast,
    pageNumber,
    itemsOnPage,
    requestedPageSize,
    totalItemsAvailable,
    totalPagesAvailable,
    sort,
    aggregateId,
  } = useAuditLogEntries();

  const [sortInput, setSortInput] = useState(sort);
  const [pageSizeInput, setPageSizeInput] = useState(requestedPageSize);
  const [pageNoInput, setPageNoInput] = useState(pageNumber);

  const sizeOpts = useMemo(
    () =>
      PAGINATION_SIZE_OPTIONS.map((option, i) => (
        <option key={i} value={option}>
          {t('audit_log.pagination.page_size_label', { option, totalItemsAvailable })}
        </option>
      )),
    [totalItemsAvailable, t]
  );

  const fetchOpts = useMemo(
    () => ({
      page: pageNoInput,
      size: pageSizeInput,
      sort: sortInput,
    }),
    [pageNoInput, pageSizeInput, sortInput]
  );

  useEffect(() => {
    const aggregateId = queryParams.get('aggregateId');
    if (!!aggregateId) {
      dispatch(setAuditLogAggregateIdFilter(aggregateId));
    }
    dispatch(fetchAuditLogPage(fetchOpts));
  }, [dispatch, fetchOpts, queryParams]);

  function onSortPropertySelect(e: ChangeEvent<HTMLSelectElement>) {
    const opt = e.target.value;
    setSortInput((s) => s.copy({ properties: [opt] }));
  }

  function onSortDirectionToggle() {
    setSortInput((s) => s.copy({ direction: s.direction === 'asc' ? 'desc' : 'asc' }));
  }

  function onPageSizeSelect(e: ChangeEvent<HTMLSelectElement>) {
    setPageSizeInput(+e.target.value);
  }

  function onResetFilterAndSort() {
    dispatch(clearAuditLogAggregateIdFilter());
    dispatch(fetchAuditLogPage(fetchOpts));
    queryParams.unset('aggregateId');
  }

  return (
    <Navbar variant="light" fixed="bottom" bg="light" className="border-top">
      <Conditional condition={!isEmpty} orElse={<Navbar.Text>{t('audit_log.pagination.no_items_found')}</Navbar.Text>}>
        <Form inline>
          <InputGroup size="sm" className="mr-2">
            <Form.Control as="select" value={sortInput.properties[0]} onChange={onSortPropertySelect}>
              <option value="timestamp">{t('audit_log.pagination.sort_by_label', { key: 'prop_timestamp' })}</option>
              <option value="aggregateType">
                {t('audit_log.pagination.sort_by_label', { key: 'prop_aggregate_type' })}
              </option>
              <option value="aggregateId">
                {t('audit_log.pagination.sort_by_label', { key: 'prop_aggregate_id' })}
              </option>
              <option value="message">{t('audit_log.pagination.sort_by_label', { key: 'prop_message' })}</option>
            </Form.Control>
            <InputGroup.Append>
              <Button
                variant="secondary"
                onClick={onSortDirectionToggle}
                title={t('audit_log.pagination.sort_direction_toggle_button')}
              >
                {sortInput.direction === 'asc' ? <ChevronUp /> : <ChevronDown />}
              </Button>
            </InputGroup.Append>
          </InputGroup>
          <Form.Control as="select" className="mr-2" size="sm" value={pageSizeInput} onChange={onPageSizeSelect}>
            {sizeOpts}
          </Form.Control>
          <Pagination className="mb-0 mr-2">
            <Pagination.First disabled={isFirst} onClick={() => setPageNoInput(0)} />
            <Pagination.Prev disabled={isFirst} onClick={() => setPageNoInput(pageNumber - 1)} />
            <Pagination.Item disabled>
              {t('audit_log.pagination.current_page_label', {
                currentPage: pageNumber + 1,
                totalPagesAvailable,
                itemsOnPage,
              })}
            </Pagination.Item>
            <Pagination.Next disabled={isLast} onClick={() => setPageNoInput(pageNumber + 1)} />
            <Pagination.Last disabled={isLast} onClick={() => setPageNoInput(totalPagesAvailable - 1)} />
          </Pagination>
          <Conditional condition={!!aggregateId}>
            <Button onClick={onResetFilterAndSort} title={t('audit_log.pagination.reset_aggregate_filter')}>
              <RotateCcw />
            </Button>
          </Conditional>
        </Form>
      </Conditional>
    </Navbar>
  );
};
