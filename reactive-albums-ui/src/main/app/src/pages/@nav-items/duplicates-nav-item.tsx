import React, { FC } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import { useTotalDuplicateCount } from '@reducers';
import NavLink from 'react-bootstrap/NavLink';
import { Copy } from 'react-feather';

export const DuplicatesNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();
  const totalDuplicates = useTotalDuplicateCount();

  return (
    <NavLink as={Link} to="/duplicates" active={loc.pathname === '/duplicates'} disabled={totalDuplicates === 0}>
      <Copy />
      &nbsp;{t('duplicates.nav-item.label', { totalDuplicates })}
    </NavLink>
  );
};
