import { createReducer } from '@reduxjs/toolkit';
import { AuditLogEntry, Sort } from '@types';
import { fetchAuditLogPage } from './audit-log.thunks';

export interface AuditLogSliceState {
  content: AuditLogEntry[];
  isEmpty: boolean;
  isFirst: boolean;
  isLast: boolean;
  pageNumber: number;
  itemsOnPage: number;
  requestedPageSize: number;
  totalItemsAvailable: number;
  totalPagesAvailable: number;
  sort: Sort;
  filter?: string;
}

const initialState: AuditLogSliceState = {
  content: [],
  isEmpty: true,
  isFirst: true,
  isLast: true,
  pageNumber: 0,
  itemsOnPage: 0,
  requestedPageSize: 50,
  totalItemsAvailable: 0,
  totalPagesAvailable: 0,
  sort: { direction: 'desc', properties: ['timestamp'] },
};

export const auditLogSliceName = 'auditLog';
export const auditLogSliceReducer = createReducer(initialState, (builder) => {
  builder.addCase(fetchAuditLogPage.fulfilled, (state, action) => {
    return state.copy({
      content: action.payload.content,
      isEmpty: action.payload.empty,
      isFirst: action.payload.first,
      isLast: action.payload.last,
      pageNumber: action.payload.number,
      itemsOnPage: action.payload.numberOfElements,
      requestedPageSize: action.payload.size,
      totalItemsAvailable: action.payload.totalElements,
      totalPagesAvailable: action.payload.totalPages,
      sort: action.meta.arg.sort,
      filter: action.meta.arg.filter,
    });
  });
});

export * from './audit-log.thunks';
export * from './hooks';