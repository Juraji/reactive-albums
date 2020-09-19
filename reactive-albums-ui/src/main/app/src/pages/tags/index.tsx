import React, { FC } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import NavLink from 'react-bootstrap/NavLink';
import Container from 'react-bootstrap/Container';
import { TagsActionBar } from './tags-action-bar';
import { TagsTable } from './tags-table';

export const TagsNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();

  return (
    <NavLink as={Link} to="/tags" active={loc.pathname === '/tags'}>
      {t('tags.nav-item.label')}
    </NavLink>
  );
};

const TagManagementPage: FC = () => {
  return (
    <Container>
      <TagsTable />
      <TagsActionBar />
    </Container>
  );
};

export default TagManagementPage;
