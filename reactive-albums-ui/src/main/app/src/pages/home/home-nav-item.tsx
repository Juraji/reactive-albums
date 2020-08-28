import React, { FC } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import NavLink from 'react-bootstrap/NavLink';

export const HomeNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();

  return (
    <NavLink as={Link} to="/home" active={loc.pathname === '/home'}>
      {t('home.nav-item.label')}
    </NavLink>
  );
};
