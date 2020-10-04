import React, { FC, useMemo } from 'react';
import { Tag } from '@types';

interface PictureTagProps {
  tag: Partial<Tag>;
  fontSize?: string;
  onClick?: () => void;
}

export const PictureTag: FC<PictureTagProps> = ({ tag, fontSize, onClick }) => {
  const style = useMemo(
    () => ({
      backgroundColor: `${tag.tagColor}`,
      color: `${tag.textColor}`,
      fontSize,
    }),
    [tag, fontSize]
  );

  return (
    <span className="picture-tag badge mr-1 border cursor-pointer" style={style} onClick={onClick}>
      <span>{tag.label}</span>
    </span>
  );
};
