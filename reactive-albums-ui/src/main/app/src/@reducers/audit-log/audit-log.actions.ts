import { createAction } from '@reduxjs/toolkit';

export const setAuditLogAggregateIdFilter = createAction<string>('auditLog/setAuditLogAggregateIdFilter');
export const clearAuditLogAggregateIdFilter = createAction('auditLog/clearAuditLogAggregateIdFilter');
