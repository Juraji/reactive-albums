import React, { FC } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import NavLink from 'react-bootstrap/NavLink';
import { List } from 'react-feather';

export const AuditLogNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();

  return (
    <NavLink as={Link} to="/audit-log" active={loc.pathname === '/audit-log'}>
      <List />
      &nbsp;{t('audit_log.nav-item.label')}
    </NavLink>
  );
};
