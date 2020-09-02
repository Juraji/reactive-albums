import React, { FC } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import NavLink from 'react-bootstrap/NavLink';

export const DirectoriesNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();

  return (
    <NavLink as={Link} to="/directories" active={loc.pathname === '/directories'}>
      {t('directories.nav-item.label')}
    </NavLink>
  );
};
