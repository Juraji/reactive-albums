import React, { FC } from 'react';

import './index.scss';
import { usePicturesPage } from '@reducers';
import { RegisterDirectoryButton } from './register-directory-button';

const HomePage: FC = () => {
  const currentPage = usePicturesPage(0, 50);

  return (
    <>
      <RegisterDirectoryButton />

      <div className="home-page">
        <pre>{JSON.stringify(currentPage, null, 2)}</pre>
      </div>
    </>
  );
};

export default HomePage;
