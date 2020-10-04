import React, { FC } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import NavLink from 'react-bootstrap/NavLink';
import { Image } from 'react-feather';

export const HomeNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();

  return (
    <NavLink as={Link} to="/home" active={loc.pathname === '/home'}>
      <Image />
      &nbsp;{t('home.nav-item.label')}
    </NavLink>
  );
};
