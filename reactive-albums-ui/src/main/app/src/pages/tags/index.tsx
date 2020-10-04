import React, { FC, useEffect } from 'react';
import Container from 'react-bootstrap/Container';
import { TagsTable } from './tags-table';
import { fetchAllTags, useColorTags, useDirectoryTags, useUserTags } from '@reducers';
import Tabs from 'react-bootstrap/Tabs';
import Tab from 'react-bootstrap/Tab';
import { useTranslation } from 'react-i18next';
import { EditTagButton } from './edit-tag-button';
import { Plus } from 'react-feather';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import { useDispatch } from '@hooks';

const TagManagementPage: FC = () => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const userTags = useUserTags();
  const colorTags = useColorTags();
  const directoryTags = useDirectoryTags();

  useEffect(() => {
    dispatch(fetchAllTags());
  }, [dispatch]);

  return (
    <Container>
      <Tabs id="tag-management-tabs" defaultActiveKey="user-tags">
        <Tab title={t('tags.tab_user.tab_header')} eventKey="user-tags">
          <ButtonGroup className="my-2">
            <EditTagButton icon={<Plus />} />
          </ButtonGroup>
          <TagsTable tags={userTags} />
        </Tab>
        <Tab title={t('tags.tab_directory.tab_header')} eventKey="directory-tags">
          <TagsTable tags={directoryTags} />
        </Tab>
        <Tab title={t('tags.tab_color.tab_header')} eventKey="color-tags">
          <TagsTable tags={colorTags} />
        </Tab>
      </Tabs>
    </Container>
  );
};

export default TagManagementPage;
