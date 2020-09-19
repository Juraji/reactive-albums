import React, { FC, useMemo } from 'react';
import { Tag } from '@types';

interface PictureTagProps {
  tag: Tag;
  fontSize?: string;
}

export const PictureTag: FC<PictureTagProps> = ({ tag, fontSize }) => {
  const style = useMemo(
    () => ({
      backgroundColor: `${tag.tagColor}`,
      color: `${tag.textColor}`,
      fontSize,
    }),
    [tag, fontSize]
  );

  return (
    <span className="badge mr-1" style={style}>
      {tag.label}
    </span>
  );
};