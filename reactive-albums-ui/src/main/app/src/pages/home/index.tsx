import React, { FC, useState } from 'react';

import './index.scss';
import { PictureControls } from './picture-controls';
import { PictureTile } from './picture-tile';
import Container from 'react-bootstrap/Container';
import { usePicturesPage } from './@hooks/use-pictures-page';

const HomePage: FC = () => {
  const [pageNumber, setPageNumber] = useState(0);
  const [pageSize, setPageSize] = useState(50);
  const [filterValue, setFilterValue] = useState<string | undefined>(undefined);
  const page = usePicturesPage(pageNumber, pageSize, filterValue);

  return (
    <Container fluid>
      <div className="home-page d-flex flex-wrap justify-content-between">
        {page.content.map((picture, pictureIdx) => (
          <PictureTile key={pictureIdx} picture={picture} />
        ))}
      </div>

      <PictureControls
        pageResult={page}
        onPageNumberChange={setPageNumber}
        onPageSizeChange={setPageSize}
        onFilterChange={setFilterValue}
      />
    </Container>
  );
};

export default HomePage;
