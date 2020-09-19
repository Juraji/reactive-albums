import React, { FC } from 'react';
import Container from 'react-bootstrap/Container';
import { useTranslation } from 'react-i18next';
import { RegisterDirectory } from './register-directory';
import { DirectoryItem } from './directory-item';
import { Conditional } from '@components';
import { useDirectories } from '@reducers';
import { Link, useLocation } from 'react-router-dom';
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

const DirectoriesPage: FC = () => {
  const { t } = useTranslation();
  const directories = useDirectories();

  return (
    <Container>
      <h3>{t('directories.page_title')}</h3>

      <div className="directories-list">
        <Conditional
          condition={!directories.isEmpty()}
          orElse={<div className="text-muted mb-2">{t('directories.no_directories_registered')}</div>}
        >
          {directories.map((dir, idx) => (
            <DirectoryItem key={idx} directory={dir} />
          ))}
        </Conditional>
      </div>
      <RegisterDirectory />
    </Container>
  );
};

export default DirectoriesPage;
