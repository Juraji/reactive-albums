import React, { FC } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import NavLink from 'react-bootstrap/NavLink';

export const AuditLogNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();

  return (
    <NavLink as={Link} to="/audit-log" active={loc.pathname === '/audit-log'}>
      {t('audit_log.nav-item.label')}
    </NavLink>
  );
};
