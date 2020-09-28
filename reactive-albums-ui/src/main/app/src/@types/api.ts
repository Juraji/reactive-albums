export type ReactiveEventType = 'UPSERT' | 'DELETE';

export interface ReactiveEvent<T> {
  type: ReactiveEventType;
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

export interface Sort {
  direction: 'asc' | 'desc';
  properties: string[];
}

export interface Audited {
  createdAt: string;
  lastModifiedAt: string;
}
