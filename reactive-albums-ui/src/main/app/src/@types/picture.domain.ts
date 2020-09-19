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

export interface Picture {
  id: string;
  displayName: string;
  location: string;
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

export interface Tag {
  id: string;
  label: string;
  tagColor: string;
  textColor: string;
}

export interface Color {
  hexadecimal: string;
}

export interface DuplicateMatch {
  id: string;
  pictureId: string;
  targetId: string;
  similarity: number;
  picture?: Picture;
  target?: Picture;
}
