export type ReactiveEventType = 'UPSERT' | 'DELETE';

export interface ReactiveEvent<T> {
  type: ReactiveEventType;
  entityType: string;
  entity: T;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  numberOfElements: number;
  number: number;
  size: number;
  last: boolean;
  first: boolean;
  empty: boolean;
}

export interface Audited {
  createdAt: string;
  lastModifiedAt: string;
}
