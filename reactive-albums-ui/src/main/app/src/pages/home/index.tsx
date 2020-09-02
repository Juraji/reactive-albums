import React, { FC } from 'react';

import './index.scss';
import { usePicturesPage } from '@reducers';
import { PictureControls } from './picture-controls';

const HomePage: FC = () => {
  const { page, setPage, setSize, setFilter } = usePicturesPage();

  return (
    <>
      <div className="home-page">
        <pre>{JSON.stringify(page, null, 2)}</pre>
      </div>

      <PictureControls page={page} onSelectPage={setPage} onSelectPageSize={setSize} onUpdateFilter={setFilter} />
    </>
  );
};

export default HomePage;
