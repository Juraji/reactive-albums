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
import { Tag } from '@types';
import Badge from 'react-bootstrap/Badge';

interface TabTitleProps {
  i18nKey: string;
  tags: Tag[];
}

const TabTitle: FC<TabTitleProps> = ({ i18nKey, tags }) => {
  const { t } = useTranslation();

  return (
    <>
      {t(i18nKey)}&nbsp;<Badge variant="primary">{tags.length}</Badge>
    </>
  );
};

const TagManagementPage: FC = () => {
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
        <Tab title={<TabTitle i18nKey="tags.tab_user.tab_header" tags={userTags} />} eventKey="user-tags">
          <ButtonGroup className="my-2">
            <EditTagButton icon={<Plus />} />
          </ButtonGroup>
          <TagsTable tags={userTags} />
        </Tab>
        <Tab
          title={<TabTitle i18nKey="tags.tab_directory.tab_header" tags={directoryTags} />}
          eventKey="directory-tags"
        >
          <TagsTable tags={directoryTags} />
        </Tab>
        <Tab title={<TabTitle i18nKey="tags.tab_color.tab_header" tags={colorTags} />} eventKey="color-tags">
          <TagsTable tags={colorTags} />
        </Tab>
      </Tabs>
    </Container>
  );
};

export default TagManagementPage;
