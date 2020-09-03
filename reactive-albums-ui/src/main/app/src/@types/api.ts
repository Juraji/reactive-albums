type ReactiveEventType = 'UPDATE' | 'DELETE';

export interface ReactiveEvent<T> {
  type: ReactiveEventType;
  entity: T;
}
