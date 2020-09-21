import { Audited } from './api';

export interface Tag extends Audited {
  id: string;
  label: string;
  tagColor: string;
  textColor: string;
}
