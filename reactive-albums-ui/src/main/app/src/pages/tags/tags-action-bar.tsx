import React, { FC } from 'react';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import { Plus } from 'react-feather';
import { EditTagButton } from './edit-tag-button';

interface TagActionBarProps {}

export const TagsActionBar: FC<TagActionBarProps> = () => {
  return (
    <ButtonGroup>
      <EditTagButton icon={<Plus />} />
    </ButtonGroup>
  );
};
