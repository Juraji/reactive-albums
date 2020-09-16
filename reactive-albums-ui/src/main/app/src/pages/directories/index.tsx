import React, { FC, useEffect, useState } from 'react';
import Container from 'react-bootstrap/Container';
import { useTranslation } from 'react-i18next';
import { RegisterDirectory } from './register-directory';
import { DirectoryItem } from './directory-item';
import { Conditional } from '@components';
import { useDispatch } from '@hooks';
import { fetchAllDirectories } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';
import { Directory } from '@types';

const DirectoriesPage: FC = () => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const [allDirectories, setAllDirectories] = useState<Directory[]>([]);

  useEffect(() => {
    dispatch(fetchAllDirectories()).then(unwrapResult).then(setAllDirectories);
  }, [dispatch, setAllDirectories]);

  return (
    <Container>
      <h3>{t('directories.page_title')}</h3>

      <div className="directories-list">
        <Conditional
          condition={!allDirectories.isEmpty()}
          orElse={<div className="text-muted mb-2">{t('directories.no_directories_registered')}</div>}
        >
          {allDirectories.map((dir, idx) => (
            <DirectoryItem key={idx} directory={dir} />
          ))}
        </Conditional>
      </div>
      <RegisterDirectory />
    </Container>
  );
};

export default DirectoriesPage;
