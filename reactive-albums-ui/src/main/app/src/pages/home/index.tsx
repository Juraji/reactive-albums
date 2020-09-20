import React, { FC } from 'react';

import './index.scss';
import { PictureControls } from './picture-controls';
import { PictureTile } from './picture-tile';
import Container from 'react-bootstrap/Container';
import { usePicturesOverview } from '@reducers';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
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

const HomePage: FC = () => {
  const page = usePicturesOverview();

  return (
    <Container fluid>
      <div className="home-page d-flex flex-wrap">
        {page.content.map((picture, pictureIdx) => (
          <PictureTile key={pictureIdx} picture={picture} />
        ))}
      </div>

      <PictureControls />
    </Container>
  );
};

export default HomePage;
