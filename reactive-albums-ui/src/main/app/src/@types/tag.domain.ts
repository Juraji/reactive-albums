import { Audited } from './api';

export enum TagType {
  USER = 'USER',
  COLOR = 'COLOR',
  DIRECTORY = 'DIRECTORY',
}

export interface Tag extends Audited {
  id: string;
  label: string;
  tagColor: string;
  textColor: string;
  tagType: TagType;
}
