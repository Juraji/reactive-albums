import { createApiGetThunk } from '@utils';
import { AuditLogEntry, Page, Sort } from '@types';

interface FetchAuditLogPageThunk {
  page: number;
  size: number;
  sort: Sort;
  filter?: string;
}

export const fetchAuditLogPage = createApiGetThunk<Page<AuditLogEntry>, FetchAuditLogPageThunk>(
  'auditLog/fetchAuditLogPage',
  ({ filter, page, size, sort }) => ({
    url: '/api/audit-log',
    params: {
      page,
      size,
      filter,
      sort: `${sort.properties.join(',')},${sort.direction}`,
    },
  })
);
