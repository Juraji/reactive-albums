import React, { FC } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import NavLink from 'react-bootstrap/NavLink';
import { Tag } from 'react-feather';

export const TagsNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();

  return (
    <NavLink as={Link} to="/tags" active={loc.pathname === '/tags'}>
      <Tag />
      &nbsp;{t('tags.nav-item.label')}
    </NavLink>
  );
};
