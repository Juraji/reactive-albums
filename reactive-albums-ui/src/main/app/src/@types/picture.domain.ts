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

export interface Tag {
  label: string;
  labelColor: string;
  textColor: string;
  linkType: TagLinkType;
}

export interface Picture {
  id: string;
  displayName: string;
  location: string;
  pictureType: PictureType;
  duplicateCount: number;
  tags: Tag[];
  colors: Color[];
  fileSize?: number;
  lastModifiedTime?: string;
  imageWidth?: number;
  imageHeight?: number;
}

export interface Color {
  hexadecimal: string;
}

export interface DuplicateMatch {
  id: string;
  pictureId: string;
  targetId: string;
  similarity: number;
  target?: Picture;
}
