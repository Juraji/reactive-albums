import { Audited } from './api';

export enum PictureType {
  JPEG = 'JPEG',
  BMP = 'BMP',
  GIF = 'GIF',
  PNG = 'PNG',
  TIFF = 'TIFF',
}

export enum TagLinkType {
  AUTO = 'AUTO',
  USER = 'USER',
}

export interface Picture extends Audited {
  id: string;
  displayName: string;
  location: string;
  parentLocation: string;
  pictureType: PictureType;
  duplicateCount: number;
  tags: TagLink[];
  colors: Color[];
  fileSize?: number;
  lastModifiedTime?: string;
  imageWidth?: number;
  imageHeight?: number;
}

export interface TagLink {
  linkType: TagLinkType;
  tag: Tag;
}

export interface Tag extends Audited {
  id: string;
  label: string;
  tagColor: string;
  textColor: string;
}

export interface Color {
  hexadecimal: string;
}

export interface DuplicateMatch extends Audited {
  id: string;
  pictureId: string;
  targetId: string;
  similarity: number;
  picture?: Picture;
  target?: Picture;
}
