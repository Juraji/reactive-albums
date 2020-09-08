import React, { FC } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import NavLink from 'react-bootstrap/NavLink';
import { useTotalDuplicateCount } from '@reducers';

export const DuplicatesNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();
  const totalDuplicates = useTotalDuplicateCount();

  return (
    <NavLink as={Link} to="/duplicates" active={loc.pathname === '/duplicates'} disabled={totalDuplicates === 0}>
      {t('duplicates.nav-item.label', { totalDuplicates })}
    </NavLink>
  );
};
