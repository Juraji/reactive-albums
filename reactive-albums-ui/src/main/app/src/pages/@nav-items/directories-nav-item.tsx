import React, { FC } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import NavLink from 'react-bootstrap/NavLink';
import { Folder } from 'react-feather';

export const DirectoriesNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();

  return (
    <NavLink as={Link} to="/directories" active={loc.pathname === '/directories'}>
      <Folder />
      &nbsp;{t('directories.nav-item.label')}
    </NavLink>
  );
};
