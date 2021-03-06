import React, { FC, useEffect } from 'react';
import Container from 'react-bootstrap/Container';
import { useTranslation } from 'react-i18next';
import { RegisterDirectory } from './register-directory';
import { DirectoryItem } from './directory-item';
import { Conditional } from '@components';
import { fetchAllDirectories, useDirectories } from '@reducers';
import Table from 'react-bootstrap/Table';
import { useDispatch } from '@hooks';

const DirectoriesPage: FC = () => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const directories = useDirectories();

  useEffect(() => {
    dispatch(fetchAllDirectories());
  }, [dispatch]);

  return (
    <Container>
      <h3>{t('directories.page_title')}</h3>

      <div className="directories-list">
        <Conditional
          condition={!directories.isEmpty()}
          orElse={<div className="text-muted mb-2">{t('directories.no_directories_registered')}</div>}
        >
          <Table>
            <tbody>
              {directories.map((dir, idx) => (
                <DirectoryItem key={idx} directory={dir} />
              ))}
            </tbody>
          </Table>
        </Conditional>
      </div>
      <RegisterDirectory />
    </Container>
  );
};

export default DirectoriesPage;
