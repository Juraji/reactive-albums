import React, { FC } from 'react';

import './index.scss';
import { PictureControls } from './picture-controls';
import { PictureTile } from './picture-tile';
import Container from 'react-bootstrap/Container';
import { usePicturesOverview } from '@reducers';

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
