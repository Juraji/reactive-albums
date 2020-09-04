import React, { FC } from 'react';

import './index.scss';
import { usePicturesPage } from '@reducers';
import { PictureControls } from './picture-controls';
import { PictureTile } from './picture-tile';
import Container from 'react-bootstrap/Container';

const HomePage: FC = () => {
  const page = usePicturesPage();

  return (
    <Container fluid>
      <div className="home-page d-flex flex-wrap justify-content-between">
        {page.page.content.map((picture, pictureIdx) => (
          <PictureTile key={pictureIdx} picture={picture} />
        ))}
      </div>

      <PictureControls pageResult={page} />
    </Container>
  );
};

export default HomePage;
