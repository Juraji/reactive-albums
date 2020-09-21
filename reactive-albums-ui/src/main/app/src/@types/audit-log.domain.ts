export interface AuditLogEntry {
  id: number;
  timestamp: string;
  aggregateType: AggregateType;
  aggregateId: string;
  message: string;
}

export enum AggregateType {
  DIRECTORY = 'DIRECTORY',
  PICTURE = 'PICTURE',
  TAG = 'TAG',
}
