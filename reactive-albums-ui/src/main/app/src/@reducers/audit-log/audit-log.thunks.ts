import { createApiGetThunk } from '@utils';
import { AuditLogEntry, Page, Sort } from '@types';

interface FetchAuditLogPageThunk {
  page: number;
  size: number;
  sort: Sort;
}

export const fetchAuditLogPage = createApiGetThunk<Page<AuditLogEntry>, FetchAuditLogPageThunk>(
  'auditLog/fetchAuditLogPage',
  ({ page, size, sort }, api) => ({
    url: '/api/audit-log',
    params: {
      page,
      size,
      aggregateId: api.getState().auditLog.aggregateId,
      sort: `${sort.properties.join(',')},${sort.direction}`,
    },
  })
);

interface FetchAuditLogPageWithAggregateIdThunk extends FetchAuditLogPageThunk {
  aggregateId: string;
}

export const fetchAuditLogPageWithAggregateId = createApiGetThunk<
  Page<AuditLogEntry>,
  FetchAuditLogPageWithAggregateIdThunk
>('auditLog/fetchAuditLogPageWithAggregateId', ({ page, size, sort, aggregateId }) => ({
  url: '/api/audit-log',
  params: {
    page,
    size,
    aggregateId,
    sort: `${sort.properties.join(',')},${sort.direction}`,
  },
}));
