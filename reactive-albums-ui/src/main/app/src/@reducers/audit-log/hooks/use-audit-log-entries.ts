import { useSelector } from '@hooks';
import { AuditLogSliceState } from '@reducers';

export function useAuditLogEntries(): AuditLogSliceState {
  return useSelector((state) => state.auditLog);
}
