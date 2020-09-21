import React, { FC } from 'react';
import Container from 'react-bootstrap/Container';
import { TagsActionBar } from './tags-action-bar';
import { TagsTable } from './tags-table';

const TagManagementPage: FC = () => {
  return (
    <Container>
      <TagsTable />
      <TagsActionBar />
    </Container>
  );
};

export default TagManagementPage;
