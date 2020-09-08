export type ReactiveEventType = 'UPDATE' | 'DELETE';

export interface ReactiveEvent<T> {
  type: ReactiveEventType;
  entityType: string;
  entity: T;
}
